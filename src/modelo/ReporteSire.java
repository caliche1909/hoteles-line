package modelo;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import vistas.ThermalPrinter;

public final class ReporteSire {

    ImageIcon icon = new ImageIcon("src/img/pequeño.png");

    boolean primerProceso = true;
    private WebDriver driver;
    private String localPath;
    private String tipDocLogin = "Cédula de Ciudadanía";
    private String numDocLogin = "59822999";
    private String contraseñaSireLogin = "migracion";
    private String tipoMovimiento;
    private String fechaMovimiento;
    private String tipoDocumento;
    private String fechaNacimiento;
    private String numeroDocumento;
    private String primerApellido;
    private String segundoApellido;
    private String nombresR;
    private String paisNacionalidad;
    private String paisProce;
    private String deparProce;
    private String ciudadProce;
    private String paisDest;
    private String deparDest;
    private String ciudadDest;

    public ReporteSire() {
        inicializarDriver();
    }

    public ReporteSire(String tipoMovimiento, String fechaMovimiento, String tipoDocumento, String fechaNacimiento, String numeroDocumento,
            String primerApellido, String segundoApellido, String nombresR, String paisNacionalidad, String paisProce, String deparProce,
            String ciudadProce, String paisDest, String deparDest, String ciudadDest) {
        this.tipoMovimiento = tipoMovimiento;
        this.fechaMovimiento = fechaMovimiento;
        this.tipoDocumento = tipoDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.numeroDocumento = numeroDocumento;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.nombresR = nombresR;
        this.paisNacionalidad = paisNacionalidad;
        this.paisProce = paisProce;
        this.deparProce = deparProce;
        this.ciudadProce = ciudadProce;
        this.paisDest = paisDest;
        this.deparDest = deparDest;
        this.ciudadDest = ciudadDest;

        inicializarDriver();

    }

    public void inicializarDriver() {
        if (driver == null) {
            try {

                System.setProperty("webdriver.chrome.driver", "C:\\WebDriver\\chromedriver.exe");
                System.setProperty("webdriver.chrome.silentOutput", "true");
                localPath = new java.io.File(".").getCanonicalPath();

                ChromeOptions optionsGoo = new ChromeOptions();
                optionsGoo.addArguments("--no-sandbox", "--disable-notifications", "--user-data-dir=" + localPath + "\\sireReport");
                driver = new ChromeDriver(optionsGoo);
                // enviamos laventana emergente ala parte de atras
                try {
                    Thread.sleep(500);
                    Robot robot = new Robot();
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_TAB);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    robot.keyRelease(KeyEvent.VK_TAB);
                } catch (AWTException e) {
                    Logger.getLogger(ReporteSire.class.getName()).log(Level.SEVERE, null, e);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ReporteSire.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error! no se pudo inicializar el driver de Chrome\npara el reporte SIRE. \nTIPO: " + e);
            }

        }
    }

    public boolean ingresarLogins() {
        if (driver == null) {
            inicializarDriver();
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

    public void mensaje() {
        JOptionPane.showMessageDialog(null, """
                                                   ERROR!
                                                   No se pudo repetir este proceso, asegurece de hacer
                                                   manualmente el reporte de: \n
                                                    """
                + nombresR + " " + primerApellido + " " + segundoApellido + "\n\n");
        ThermalPrinter thermalPrinter = new ThermalPrinter();
        String clientInfo = String.format("""
                                            Tipo Movimiento   : %s  
                                            Fecha Movimiento  : %s
                                            Tipo Documento    : %s
                                            Fecha Nacimiento  : %s
                                            Numero Documento  : %s 
                                            Primer apellido   : %s
                                            Segundo Apellido  : %s
                                            Nombres           : %s
                                            Nacionalidad      : %s
                                            Pais Procedencia  : %s
                                            Depto procedencia : %s
                                            Ciudad Procedencia: %s
                                            Pais destino      : %s
                                            Depto destino     : %s
                                            Ciudad Destino    : %s
                                            """,
                tipoMovimiento, fechaMovimiento, tipoDocumento, fechaNacimiento, numeroDocumento, primerApellido, segundoApellido,
                nombresR, paisNacionalidad, paisProce, deparProce, ciudadProce, paisDest, deparDest, ciudadDest);
        List<String[]> productList = new ArrayList<>();
        String nuevoPDF = thermalPrinter.createPDF(clientInfo, productList, "SIRE_" + nombresR + "_" + primerApellido);
        thermalPrinter.printPDF(nuevoPDF);
    }

    public void repetir() {
        primerProceso = false;

        boolean repetir = repetirProceso();
        if (repetir) {
            driver.navigate().refresh();
            abrirPagina();
        } else {
            mensaje();
        }

    }

    public boolean repetirProceso() {
        String[] opciones = {"REINTENTAR", "YO LO TERMINO!"};
        int dialogResult = JOptionPane.showOptionDialog(
                null,
                """
                Algo salio mal!
                Desea repetir el proceso o prefiere terminarlo 
                maualmente ?
                """,
                "ERROR!",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                icon,
                opciones,
                opciones[0]
        );

        switch (dialogResult) {
            case 0 -> {
                return true;  // El usuario quiere reintentar
            }
            case 1 -> {
                return false;  // El usuario no quiere reintentar
            }
            default ->
                throw new AssertionError();
        }
    }
    // Método para verificar si un elemento está visible

    private boolean isElementDisplayed(String id) {
        try {
            // Devuelve true si el elemento está visible, false en caso contrario
            return driver.findElement(By.id(id)).isDisplayed();
        } catch (NoSuchElementException e) {
            // En caso de que el elemento no se encuentre, retorna false
            return false;
        }
    }

    public void abrirPagina() {
        boolean estadoProceso = false;
        if (driver == null) {
            inicializarDriver();
        }
        // Espera explícita de 10 segundos
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            if (primerProceso) {
                // Navegamos a la URL de Migración Colombia
                driver.get("https://apps.migracioncolombia.gov.co/sire/public/login.jsf");

            }

            // Espera hasta que se muestren ciertos elementos
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.id("formLogin:tipoDocumento")),
                    ExpectedConditions.visibilityOfElementLocated(By.id("iconitemCargarInformacion"))
            ));

            // Si se encuentra el formulario de Login
            if (isElementDisplayed("formLogin:tipoDocumento")) {

                estadoProceso = ingresarLogins();

                if (estadoProceso) {
                    JOptionPane.showMessageDialog(null, "El reporte a SIRE fue exitoso! 1");
                    cerrarDriver(estadoProceso);
                } else {
                    estadoProceso = ingresarLogins();
                    if (estadoProceso) {
                        JOptionPane.showMessageDialog(null, "El reporte a SIRE fue exitoso! 2");
                        cerrarDriver(estadoProceso);
                    } else {
                        cerrarDriver(estadoProceso);
                        mensaje();

                    }
                }
            } else if (isElementDisplayed("iconitemCargarInformacion")) {

                estadoProceso = cargarInformacion();

                // La sesión ya está iniciada
                if (estadoProceso) {
                    JOptionPane.showMessageDialog(null, "El reporte a SIRE fue exitoso! 3");
                    cerrarDriver(estadoProceso);
                } else {
                    estadoProceso = cargarInformacion();
                    if (estadoProceso) {
                        JOptionPane.showMessageDialog(null, "El reporte a SIRE fue exitoso! 4");
                        cerrarDriver(estadoProceso);
                    } else {
                        cerrarDriver(estadoProceso);
                        mensaje();
                    }
                }
            }
        } catch (HeadlessException e) {
            // Si hay un error, mostramos un mensaje
            repetir();
        } finally {
            cerrarDriver(estadoProceso);
        }
    }

    public boolean cargarInformacion() {
        if (driver == null) {
            // Trata de manejar la situación aquí, ¿necesitas lanzar una excepción? ¿O puedes lograrlo y continuar?
            System.out.println("Driver is null en cargari nformacion----------------------------------------------------------------------------------------------------------------------------------------------------");
            return false;
        }
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
        // Tu fecha de movimiento
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Encuentra el campo de entrada de la fecha de nacimiento
        try {
            WebElement fechaMovimiento = driver.findElement(By.id("cargueFormHospedaje:fechaMovimientoInputDate"));
            // Tu fecha de nacimiento en formato "d/M/yyyy"
            String fechaMov = this.fechaMovimiento;
            js.executeScript("arguments[0].value='" + fechaMov + "';", fechaMovimiento);
        } catch (Exception e) {
            System.out.println("no se pude introducir la fecha de movimiento: " + e + "-------------------------------------------------------------------------------------------------------------------------");
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
            String numDocumento = this.numeroDocumento != null? this.numeroDocumento:"";
            String primApellido = this.primerApellido!= null? this.primerApellido:"";
            String segApellido = this.segundoApellido != null? this.segundoApellido:"";
            String nombre = this.nombresR != null? this.nombresR:"";

            WebElement numeroDocumentoInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:numeroDocumento")));
            numeroDocumentoInput.sendKeys(numDocumento);
            WebElement apellido1 = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:primerApellido")));
            apellido1.sendKeys(primApellido);
            WebElement apellido2 = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:segundoApellido")));
            apellido2.sendKeys(segApellido);
            WebElement nombres = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:nombres")));
            nombres.sendKeys(nombre);
        } catch (Exception e) {
            System.out.println("no se pude introducir nombres y apellidos: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }
        // encuentra la nacionalidad
        try {
            WebElement cbxNacionalidad = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:nacionalidad")));
            Select desplegableNCL = new Select(cbxNacionalidad);
            desplegableNCL.selectByVisibleText(paisNacionalidad);
        } catch (Exception e) {
            System.out.println("No se pude seleccionar la nacionalidad: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }
        // pais de procedencia
        try {
            WebElement procePais = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:procedencia")));
            Select paisProcedencia = new Select(procePais);
            paisProcedencia.selectByVisibleText(paisProce);

        } catch (Exception e) {
            System.out.println("No se pude seleccionar pais de procedencia: " + paisProce + " " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }

        try {
            if ("COLOMBIA".equals(paisProce)) {
                WebElement proceDepar = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:dptoProcedencia")));
                Select deparProcedencia = new Select(proceDepar);
                deparProcedencia.selectByVisibleText(deparProce);

                WebElement proceCiudad = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:ciudadProcedencia")));
                Select ciudadProcedencia = new Select(proceCiudad);
                ciudadProcedencia.selectByVisibleText(ciudadProce);
            }
        } catch (Exception e) {
            System.out.println("No se pude seleccionar departamento y ciudad de procedencia en colombia: " + deparProce + " " + ciudadProce + " " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }

        try {
            WebElement destPais = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:destino")));
            Select paisDestino = new Select(destPais);
            paisDestino.selectByVisibleText(paisDest);

        } catch (Exception e) {
            System.out.println("No se pude seleccionar pais de destino:" + paisDest + " " + e + "-------------------------------------------------------------------------------------------------------------------------");
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
            System.out.println("No se pude seleccionar departamento y destino  en colombia:  " + deparDest + " " + ciudadDest + "" + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }

        // Espera hasta que el botón "Agregar Registro" esté disponible y clickeable
        try {
            System.out.println("vamos a esperar que aparesca boton de registro");
            WebElement agregarRegistroButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:j_id877")));
            JOptionPane.showMessageDialog(null, "ya se encontro el boton de guardar el registro en sire");
            //agregarRegistroButton.click();
        } catch (Exception e) {
            System.out.println("Error al hacer clic en el botón 'Agregar Registro': " + e.getMessage());
            return false;
        }
        return true;
    }

    public void cerrarDriver(boolean cerrarNavegador) {
        if (driver != null) {
            if (cerrarNavegador) {
                // Cierra el navegador y libera el driver
                driver.quit();
            } else {
                // Solo libera el driver, pero deja la página abierta
                driver = null;
            }
        }
    }

    /*
    public static void main(String[] args) {
        ReporteSire accesoWeb = new ReporteSire();
        accesoWeb.abrirPagina();
    }*/
}
