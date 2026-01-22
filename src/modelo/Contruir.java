/*package modelo;


import conectar.Canectar;
import java.awt.HeadlessException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Contruir {

    private WebDriver driver;
    private String localPath;
    private String tipDocLogin = "Cédula de Ciudadanía";
    private String numDocLogin = "59822999";
    private String contraseñaSireLogin = "migracion";
    private String tipoMovimiento = "Entrada";
    private String fechaMovimiento = "2023-06-22";
    private String tipoDocumento = "PASAPORTE";
    private String fechaNacimiento = "22/6/2023";
    private String numeroDocumento = "1085275";
    private String primerApellido = "moran";
    private String segundoApellido = "caicedo";
    private String nombresR = "carlos andres";
    private String paisNacionalidad = "COMORAS";
    private String paisProce = "COLOMBIA";
    private String deparProce = "NARIÑO";
    private String ciudadProce = "PASTO";
    private String paisDest = "COLOMBIA";
    private String deparDest = "CAUCA";
    private String ciudadDest = "POPAYÁN";

    public Contruir() {
        try {
            localPath = new java.io.File(".").getCanonicalPath();
            System.setProperty("webdriver.chrome.driver", localPath + "\\crome driver\\chromedriver.exe");

            ChromeOptions optionsGoo = new ChromeOptions();
            optionsGoo.addArguments("--no-sandbox", "--disable-notifications", "--user-data-dir=" + localPath + "\\chromeWA");
            driver = new ChromeDriver(optionsGoo);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error! no se pudo inicializar el driver de Chrome. \nTIPO: " + e);
        }

    }

    public boolean ingresarLogins() {
        if (driver == null) {
            System.out.println("Driver is null en ingresarLogins----------------------------------------------------------------------------------------------------------------------------------------------------");
            return false; // Devuelve false si el driver es nulo
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            WebElement cbxTipDoc = wait.until(ExpectedConditions.elementToBeClickable(By.id("formLogin:tipoDocumento")));
            Select tipDocCBX = new Select(cbxTipDoc);
            tipDocCBX.selectByVisibleText(tipDocLogin);

            WebElement numeroDocumentoField = wait.until(ExpectedConditions.elementToBeClickable(By.id("formLogin:numeroDocumento")));
            String valorActual = numeroDocumentoField.getAttribute("value");
            if (valorActual.isEmpty() || !valorActual.equals(numDocLogin)) {
                numeroDocumentoField.clear();
                numeroDocumentoField.sendKeys(numDocLogin);
                numeroDocumentoField.sendKeys(Keys.TAB);
            }

            WebElement empresaElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("formLogin:listaEmpresa")));
            Select empresaCBX = new Select(empresaElement);
            empresaCBX.selectByVisibleText("HOTEL DORAL PLAZA");

            WebElement contrasenia = wait.until(ExpectedConditions.elementToBeClickable(By.id("formLogin:password")));
            contrasenia.sendKeys(contraseñaSireLogin);

            if (!driver.findElement(By.id("formLogin:password")).getAttribute("value").isEmpty()
                    && !driver.findElement(By.id("formLogin:numeroDocumento")).getAttribute("value").isEmpty()
                    && !driver.findElement(By.id("formLogin:listaEmpresa")).getAttribute("value").isEmpty()
                    && !driver.findElement(By.id("formLogin:tipoDocumento")).getAttribute("value").isEmpty()) {
                driver.findElement(By.id("formLogin:button1")).click();
                boolean info = cargarInformacion();
                return info; // Devuelve true si todo el proceso de login se completa correctamente
            } else {

                System.out.println("Vamos a cargar de nuevo los valores (intentos restantes: ------------------------------------------------------------------------------------------");
                return false; // Devuelve false si no se completó el proceso de login
            }

        } catch (Exception e) {
            // Imprime los mensajes de error dependiendo de dónde ocurrió la excepción
            System.out.println("Error durante el proceso de login: " + e.getMessage());
            return false; // Devuelve false si se encontró una excepción
        }
    }

    public boolean cargarInformacion() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Intenta hacer clic en el botón Cargar Información
        try {
            WebElement cargarInformacionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("iconitemCargarInformacion")));
            cargarInformacionButton.click();
        } catch (Exception e) {
            System.out.println("-5- Error al hacer clic en el botón Cargar Información: " + e.getMessage() + "--------------------------------------------------------------------------------------");
            return false;
        }

        // Click en el objeto: alojamiento y hospedaje
        try {
            WebElement hospedajeLabel = wait.until(ExpectedConditions.elementToBeClickable(By.id("HOTEL_lbl")));
            hospedajeLabel.click();
        } catch (Exception e) {
            System.out.println("No se pude ciclear el boton de alojamiento y hospedaje: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }

        // Selecciona el tipo de movimiento
        try {
            WebElement dropdownMovimiento = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:tipoMovimiento")));
            Select desplegableTM = new Select(dropdownMovimiento);
            desplegableTM.selectByVisibleText(tipoMovimiento);
        } catch (Exception e) {
            System.out.println("no se pude seleccionar tipo de movimiento: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }
        // Tu fecha en formato "yyyy-MM-dd"
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            String fecha = this.fechaMovimiento; // puedes cambiar esto a la fecha que necesites

            // Crear un objeto DateTimeFormatter para el formato de la fecha de entrada
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Crear un objeto DateTimeFormatter para el formato de la fecha de salida
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("d/M/yyyy");

            // Parsear la fecha de entrada al formato de la fecha de salida
            LocalDate parsedDate = LocalDate.parse(fecha, inputFormat);
            String formattedDate = parsedDate.format(outputFormat);

            // Enviar la fecha formateada al campo de entrada, usar JavascriptExecutor para establecer el valor
            js.executeScript("document.getElementById('cargueFormHospedaje:fechaMovimientoInputDate').value='" + formattedDate + "';");
            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("no se pude seleccionar la fecha del movimiento: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }

        // encuentra el elemento <select>
        try {
            WebElement dropdownTipoDoc = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:tipoDocumento")));
            Select desplegableTD = new Select(dropdownTipoDoc);
            desplegableTD.selectByVisibleText(tipoDocumento);
        } catch (Exception e) {
            System.out.println("no se pude seleccionar tipo de documento del reportado: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }

        // Encuentra el campo de entrada de la fecha de nacimiento
        try {
            WebElement fechaNacimientoInput = driver.findElement(By.id("cargueFormHospedaje:fechaNacimientoInputDate"));
            // Tu fecha de nacimiento en formato "d/M/yyyy"
            String fechaNac = this.fechaNacimiento;
            js.executeScript("arguments[0].value='" + fechaNac + "';", fechaNacimientoInput);
        } catch (Exception e) {
            System.out.println("no se pude introducir la fecha de nacimiento: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }

        // Encuentra el campo de entrada del número de documento
        try {
            String numDocumento = this.numeroDocumento;
            String primApellido = this.primerApellido;
            String segApellido = this.segundoApellido;
            String nombre = this.nombresR;

            WebElement numeroDocumentoInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:numeroDocumento")));
            numeroDocumentoInput.sendKeys(numDocumento);
            WebElement apellido1 = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:primerApellido")));
            apellido1.sendKeys(primApellido);
            WebElement apellido2 = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:segundoApellido")));
            apellido2.sendKeys(segApellido);
            WebElement nombres = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:nombres")));
            nombres.sendKeys(nombre);
        } catch (Exception e) {
            System.out.println("no se pude introducir la fecha de nacimiento: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }

        try {
            WebElement cbxNacionalidad = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:nacionalidad")));
            Select desplegableNCL = new Select(cbxNacionalidad);
            desplegableNCL.selectByVisibleText(paisNacionalidad);
        } catch (Exception e) {
            System.out.println("No se pude seleccionar la nacionalidad: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }
        // Crear una instancia de la clase Canectar
        Canectar canectar = new Canectar();
        java.sql.Connection con = canectar.conexion();
        PreparedStatement ps = null;

        try {
            paisProce = "COLOMBIA";
            if ("COLOMBIA".equals(paisProce)) {
                WebElement procePais = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:procedencia")));
                Select paisProcedencia = new Select(procePais);
                paisProcedencia.selectByVisibleText(paisProce);

                WebElement proceDepar = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:dptoProcedencia")));
                Select deparProcedencia = new Select(proceDepar);

                List<WebElement> departamentos = deparProcedencia.getOptions();
                for (int i = 1; i < departamentos.size(); i++) { // Comenzar en 1 para ignorar 'Seleccionar'
                    String nombreDepartamento = departamentos.get(i).getText();
                    deparProcedencia.selectByVisibleText(nombreDepartamento);

                    String queryDepartamento = "INSERT INTO depar_sire (Nombre_Departamento, Fk_Id_Pais) VALUES (?, ?)";
                    PreparedStatement pstmtDepartamento = con.prepareStatement(queryDepartamento, Statement.RETURN_GENERATED_KEYS);
                    pstmtDepartamento.setString(1, nombreDepartamento);
                    pstmtDepartamento.setInt(2, 49); // Asegúrate de que este es el ID del país correcto
                    pstmtDepartamento.executeUpdate();
                    ResultSet rs = pstmtDepartamento.getGeneratedKeys();
                    int idDepartamento = 0;
                    if (rs.next()) {
                        idDepartamento = rs.getInt(1);
                    }
                    Thread.sleep(2000);

                    WebElement proceCiudad = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:ciudadProcedencia")));
                    Select ciudadProcedencia = new Select(proceCiudad);
                    List<WebElement> ciudades = ciudadProcedencia.getOptions();

                    for (WebElement ciudad : ciudades) {
                        String nombreCiudad = ciudad.getText();

                        if (!nombreCiudad.equals("Seleccionar")) {
                            String queryCiudad = "INSERT INTO ciudades_sire (Nombre_Ciudad, Fk_Id_Departamento) VALUES (?, ?)";
                            PreparedStatement pstmtCiudad = con.prepareStatement(queryCiudad);
                            pstmtCiudad.setString(1, nombreCiudad);
                            pstmtCiudad.setInt(2, idDepartamento);
                            pstmtCiudad.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo seleccionar lugares de procedencia en colombia: " + e);
            return false;
        }

        try {
            WebElement destPais = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:destino")));
            Select paisDestino = new Select(destPais);
            paisDestino.selectByVisibleText(paisDest);

        } catch (Exception e) {
            System.out.println("No se pude seleccionar pais de destino: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }

        try {
            if ("COLOMBIA".equals(paisDest)) {
                WebElement destDepar = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:dptoDestino")));
                Select deparDestino = new Select(destDepar);
                deparDestino.selectByVisibleText(deparDest);

                WebElement destCiudad = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:ciudadDestino")));
                Select ciudadDestino = new Select(destCiudad);
                ciudadDestino.selectByVisibleText(ciudadDest);
            }
        } catch (Exception e) {
            System.out.println("No se pude seleccionar lugares de procedencia en colombia: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }

        return true;

    }

    public void abrirPagina() {
        // Espera explícita de 10 segundos
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        if (driver == null) {
            // Trata de manejar la situación aquí, ¿necesitas lanzar una excepción? ¿O puedes lograrlo y continuar?
            System.out.println("Driver is null en abrir pagina----------------------------------------------------------------------------------------------------------------------------------------------------");
            return;
        }
        try {

            // Navegamos a la URL de Migración Colombia
            driver.get("https://apps.migracioncolombia.gov.co/sire/public/login.jsf");

            // Espera hasta que se muestren ciertos elementos
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.id("formLogin:tipoDocumento")),
                    ExpectedConditions.visibilityOfElementLocated(By.id("iconitemCargarInformacion"))
            ));

            // Si se encuentra el formulario de Login
            if (isElementDisplayed("formLogin:tipoDocumento")) {
                if (ingresarLogins()) {
                    JOptionPane.showMessageDialog(null, "El reporte a SIRE fue exitoso!");
                } else {
                    boolean log = ingresarLogins();
                    if (log) {
                        JOptionPane.showMessageDialog(null, "El reporte a SIRE fue exitoso!");
                    } else {

                    }
                }
            } else if (isElementDisplayed("iconitemCargarInformacion")) {
                // La sesión ya está iniciada
                if (cargarInformacion()) {
                    JOptionPane.showMessageDialog(null, "El reporte a SIRE fue exitoso!");
                } else {
                    boolean log2 = cargarInformacion();
                    if (log2) {
                        JOptionPane.showMessageDialog(null, "El reporte a SIRE fue exitoso!");
                    } else {

                    }
                }
            }
        } catch (HeadlessException e) {
            // Si hay un error, mostramos un mensaje

        } finally {

        }
    }

    private boolean isElementDisplayed(String id) {
        try {
            // Devuelve true si el elemento está visible, false en caso contrario
            return driver.findElement(By.id(id)).isDisplayed();
        } catch (NoSuchElementException e) {
            // En caso de que el elemento no se encuentre, retorna false
            return false;
        }
    }

    public static void main(String[] args) {
        Contruir accesoWeb = new Contruir();
        accesoWeb.abrirPagina();
    }

}*/
