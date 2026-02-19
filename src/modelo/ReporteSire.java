package modelo;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
//import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
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
import io.github.bonigarcia.wdm.WebDriverManager;

public final class ReporteSire {

    ImageIcon icon = new ImageIcon("src/img/pequeño.png");

    // Variable estática para rastrear instancia activa (singleton pattern)
    private static ReporteSire instanciaActiva = null;
    
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

    /**
     * Verifica si hay una ventana de reporte SIRE abierta
     * @return true si hay una instancia activa con driver no nulo
     */
    public static boolean hayVentanaAbierta() {
        boolean instanciaNoNula = instanciaActiva != null;
        boolean driverNoNulo = instanciaNoNula && instanciaActiva.driver != null;
        System.out.println("[SIRE-CHECK] hayVentanaAbierta() llamado:");
        System.out.println("[SIRE-CHECK]   - instanciaActiva != null: " + instanciaNoNula);
        System.out.println("[SIRE-CHECK]   - driver != null: " + driverNoNulo);        
        // Si hay instancia y driver, verificar que el navegador realmente esté abierto
        if (instanciaNoNula && driverNoNulo) {
            try {
                // getWindowHandles() lanzará excepción si el navegador está cerrado
                instanciaActiva.driver.getWindowHandles();
                System.out.println("[SIRE-CHECK]   - Navegador verificado: ABIERTO");
                System.out.println("[SIRE-CHECK]   - Resultado: true");
                return true;
            } catch (Exception e) {
                // El navegador fue cerrado manualmente por el usuario
                System.out.println("[SIRE-CHECK]   - Navegador verificado: CERRADO (limpiando instancia)");
                instanciaActiva.driver = null;
                instanciaActiva = null;
                System.out.println("[SIRE-CHECK]   - Resultado: false");
                return false;
            }
        }
                System.out.println("[SIRE-CHECK]   - Resultado: " + (instanciaNoNula && driverNoNulo));
        return instanciaNoNula && driverNoNulo;
    }
    
    /**
     * Maximiza y trae al frente la ventana SIRE pendiente
     */
    public static void maximizarVentanaPendiente() {
        if (hayVentanaAbierta()) {
            try {
                System.out.println("[SIRE-STATIC] Maximizando ventana SIRE pendiente...");
                instanciaActiva.driver.manage().window().maximize();
                
                // Usar Robot para traer al frente
                Thread.sleep(300);
                Robot robot = new Robot();
                robot.keyPress(KeyEvent.VK_ALT);
                robot.keyPress(KeyEvent.VK_TAB);
                Thread.sleep(100);
                robot.keyRelease(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_ALT);
                
                System.out.println("[SIRE-STATIC] ✓ Ventana SIRE traída al frente");
            } catch (Exception e) {
                System.err.println("[SIRE-STATIC] ⚠ Error al maximizar: " + e.getMessage());
            }
        }
    }

    public ReporteSire() {
        instanciaActiva = this; // Registrar como instancia activa
        System.out.println("[SIRE-INIT] Constructor simple: instanciaActiva establecida (ID: " + System.identityHashCode(this) + ")");
        inicializarDriver();
    }

    public ReporteSire(String tipoMovimiento, String fechaMovimiento, String tipoDocumento, String fechaNacimiento, String numeroDocumento,
            String primerApellido, String segundoApellido, String nombresR, String paisNacionalidad, String paisProce, String deparProce,
            String ciudadProce, String paisDest, String deparDest, String ciudadDest) {
        instanciaActiva = this; // Registrar como instancia activa
        System.out.println("[SIRE-INIT] Constructor con params: instanciaActiva establecida (ID: " + System.identityHashCode(this) + ")");
        
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
                System.out.println("[SIRE-INIT] Inicializando driver de Chrome...");
                
                // WebDriverManager descarga automáticamente el ChromeDriver correcto
                // Se actualiza automáticamente con cada nueva versión de Chrome
                WebDriverManager.chromedriver().setup();
                System.setProperty("webdriver.chrome.silentOutput", "true");
                
                localPath = new java.io.File(".").getCanonicalPath();
                String userDataDir = localPath + "\\sireReport";
                
                System.out.println("[SIRE-INIT] user-data-dir: " + userDataDir);

                ChromeOptions optionsGoo = new ChromeOptions();
                optionsGoo.addArguments(
                    "--no-sandbox",
                    "--disable-notifications",
                    "--disable-gpu",
                    "--disable-dev-shm-usage",
                    "--remote-debugging-port=0",  // Puerto dinámico para evitar conflictos
                    "--user-data-dir=" + userDataDir
                );
                optionsGoo.addArguments("--log-level=3");
                optionsGoo.setExperimentalOption("excludeSwitches", new String[]{"enable-logging"});
                
                driver = new ChromeDriver(optionsGoo);
                System.out.println("[SIRE-INIT] ✓ Driver iniciado correctamente");
                
                // Enviar ventana al fondo
                enviarVentanaAlFondo();

            } catch (org.openqa.selenium.SessionNotCreatedException e) {
                System.err.println("[SIRE-INIT] ❌ Error SessionNotCreatedException: " + e.getMessage());
                System.err.println("[SIRE-INIT] Posible causa: Chrome ya está en uso o archivos bloqueados");
                limpiarRecursos();
                JOptionPane.showMessageDialog(null, 
                    "⚠ ERROR AL INICIAR REPORTE SIRE\n\n" +
                    "No se pudo abrir Chrome para el reporte SIRE.\n" +
                    "Posibles causas:\n" +
                    "• Chrome ya está abierto con el mismo perfil\n" +
                    "• Archivos bloqueados\n\n" +
                    "Solución:\n" +
                    "1. Cierra todas las ventanas de Chrome\n" +
                    "2. Intenta nuevamente",
                    "Error SIRE",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                System.err.println("[SIRE-INIT] ❌ Error inesperado: " + e.getClass().getSimpleName());
                System.err.println("[SIRE-INIT] Mensaje: " + e.getMessage());
                limpiarRecursos();
                JOptionPane.showMessageDialog(null, 
                    "Error al inicializar Chrome para SIRE:\n" + e.getMessage(),
                    "Error SIRE",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void enviarVentanaAlFondo() {
        try {
            Thread.sleep(500);
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_TAB);
        } catch (AWTException | InterruptedException e) {
            System.out.println("[SIRE-INIT] ⚠ No se pudo enviar ventana al fondo (no crítico)");
        }
    }
    
    private void limpiarRecursos() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                // Ignorar errores al cerrar
            } finally {
                driver = null;
            }
        }
        
        // CRÍTICO: Limpiar instancia activa si falla la inicialización
        // Esto asegura que hayVentanaAbierta() retorne false correctamente
        if (instanciaActiva == this) {
            instanciaActiva = null;
            System.out.println("[SIRE-INIT] ✓ instanciaActiva limpiada por fallo de inicialización");
        }
    }

    public boolean ingresarLogins() {
        if (driver == null) {
            inicializarDriver();
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            System.out.println("[SIRE-LOGIN] Seleccionando tipo de documento...");
            WebElement cbxTipDoc = wait.until(ExpectedConditions.elementToBeClickable(By.id("formLogin:tipoDocumento")));
            Select tipDocCBX = new Select(cbxTipDoc);
            tipDocCBX.selectByVisibleText(tipDocLogin);
            System.out.println("[SIRE-LOGIN] ✓ Tipo de documento seleccionado");

            System.out.println("[SIRE-LOGIN] Ingresando número de documento...");
            WebElement numeroDocumentoField = wait.until(ExpectedConditions.elementToBeClickable(By.id("formLogin:numeroDocumento")));
            String valorActual = numeroDocumentoField.getAttribute("value");
            if (valorActual.isEmpty() || !valorActual.equals(numDocLogin)) {
                numeroDocumentoField.clear();
                numeroDocumentoField.sendKeys(numDocLogin);
                numeroDocumentoField.sendKeys(Keys.TAB);
            }
            System.out.println("[SIRE-LOGIN] ✓ Número de documento ingresado");

            // Esperar 1 segundo para que la página se estabilice después de ingresar el documento
            Thread.sleep(1000);
            System.out.println("[SIRE-LOGIN] Esperando carga del dropdown de empresas...");
            
            // Re-localizar el elemento de empresa para evitar stale element
            WebElement empresaElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("formLogin:listaEmpresa")));
            Select empresaCBX = new Select(empresaElement);
            System.out.println("[SIRE-LOGIN] Seleccionando empresa...");
            empresaCBX.selectByVisibleText("HOTEL DORAL PLAZA");
            System.out.println("[SIRE-LOGIN] ✓ Empresa seleccionada: HOTEL DORAL PLAZA");

            System.out.println("[SIRE-LOGIN] Ingresando contraseña...");
            WebElement contrasenia = wait.until(ExpectedConditions.elementToBeClickable(By.id("formLogin:password")));
            contrasenia.sendKeys(contraseñaSireLogin);
            System.out.println("[SIRE-LOGIN] ✓ Contraseña ingresada");

            if (!driver.findElement(By.id("formLogin:password")).getAttribute("value").isEmpty()
                    && !driver.findElement(By.id("formLogin:numeroDocumento")).getAttribute("value").isEmpty()
                    && !driver.findElement(By.id("formLogin:listaEmpresa")).getAttribute("value").isEmpty()
                    && !driver.findElement(By.id("formLogin:tipoDocumento")).getAttribute("value").isEmpty()) {
                System.out.println("[SIRE-LOGIN] ✓ Todos los campos verificados, iniciando sesión...");
                driver.findElement(By.id("formLogin:button1")).click();
                System.out.println("[SIRE-LOGIN] ✓ Click en botón de login realizado");
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
        System.out.println("[SIRE-ERROR] ============ DATOS DEL REPORTE FALLIDO ============");
        System.out.println("[SIRE-ERROR] Tipo Movimiento: " + tipoMovimiento);
        System.out.println("[SIRE-ERROR] Fecha Movimiento: " + fechaMovimiento);
        System.out.println("[SIRE-ERROR] Tipo Documento: " + tipoDocumento);
        System.out.println("[SIRE-ERROR] Fecha Nacimiento: " + fechaNacimiento);
        System.out.println("[SIRE-ERROR] Numero Documento: " + numeroDocumento);
        System.out.println("[SIRE-ERROR] Primer Apellido: '" + primerApellido + "'");
        System.out.println("[SIRE-ERROR] Segundo Apellido: '" + segundoApellido + "'");
        System.out.println("[SIRE-ERROR] Nombres: '" + nombresR + "'");
        System.out.println("[SIRE-ERROR] Nacionalidad: " + paisNacionalidad);
        System.out.println("[SIRE-ERROR] País Procedencia: " + paisProce);
        System.out.println("[SIRE-ERROR] Depto Procedencia: " + deparProce);
        System.out.println("[SIRE-ERROR] Ciudad Procedencia: " + ciudadProce);
        System.out.println("[SIRE-ERROR] País Destino: " + paisDest);
        System.out.println("[SIRE-ERROR] Depto Destino: " + deparDest);
        System.out.println("[SIRE-ERROR] Ciudad Destino: " + ciudadDest);
        System.out.println("[SIRE-ERROR] =================================================");
        
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

    public boolean abrirPagina() {
        boolean estadoProceso = false;
        if (driver == null) {
            inicializarDriver();
        }
        
        // CRÍTICO: Si después de intentar inicializar el driver sigue siendo null, abortar
        if (driver == null) {
            return false;
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
                    System.out.println("[SIRE] Formulario cargado exitosamente - Navegador permanece abierto");
                    // NO cerrar el navegador - dejarlo abierto para que el usuario complete el reporte
                    cerrarDriver(false);
                } else {
                    estadoProceso = ingresarLogins();
                    if (estadoProceso) {
                        System.out.println("[SIRE] Formulario cargado exitosamente (segundo intento) - Navegador permanece abierto");
                        // NO cerrar el navegador - dejarlo abierto para que el usuario complete el reporte
                        cerrarDriver(false);
                    } else {
                        cerrarDriver(estadoProceso);
                        mensaje();

                    }
                }
            } else if (isElementDisplayed("iconitemCargarInformacion")) {

                estadoProceso = cargarInformacion();

                // La sesión ya está iniciada
                if (estadoProceso) {
                    System.out.println("[SIRE] Formulario cargado exitosamente (sesión activa) - Navegador permanece abierto");
                    // NO cerrar el navegador - dejarlo abierto para que el usuario complete el reporte
                    cerrarDriver(false);
                } else {
                    estadoProceso = cargarInformacion();
                    if (estadoProceso) {
                        System.out.println("[SIRE] Formulario cargado exitosamente (segundo intento con sesión activa) - Navegador permanece abierto");
                        // NO cerrar el navegador - dejarlo abierto para que el usuario complete el reporte
                        cerrarDriver(false);
                    } else {
                        cerrarDriver(estadoProceso);
                        mensaje();
                    }
                }
            }
        } catch (HeadlessException e) {
            // Si hay un error, mostramos un mensaje
            repetir();
        }
        // NO cerrar en finally - el navegador debe permanecer abierto para que el usuario complete el reporte
        // El navegador solo se cierra si hubo un error (cerrarDriver(false) ya fue llamado en los casos exitosos)
        System.out.println("[SIRE] abrirPagina() finalizado con estadoProceso=" + estadoProceso);
        return estadoProceso;
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
            // Validar que los valores no sean null ni la palabra "null"
            String numDocumento = (this.numeroDocumento != null && !this.numeroDocumento.equals("null")) ? this.numeroDocumento : "";
            String primApellido = (this.primerApellido != null && !this.primerApellido.equals("null")) ? this.primerApellido : "";
            String segApellido = (this.segundoApellido != null && !this.segundoApellido.equals("null")) ? this.segundoApellido : "";
            String nombre = (this.nombresR != null && !this.nombresR.equals("null")) ? this.nombresR : "";

            System.out.println("[SIRE-DATOS] Número Doc: " + numDocumento);
            System.out.println("[SIRE-DATOS] Primer Apellido: " + primApellido);
            System.out.println("[SIRE-DATOS] Segundo Apellido: " + (segApellido.isEmpty() ? "(vacío)" : segApellido));
            System.out.println("[SIRE-DATOS] Nombres: " + nombre);

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
            // SIRE usa nombres en MAYÚSCULAS
            String nacionalidadMayusculas = paisNacionalidad.toUpperCase();
            System.out.println("[SIRE-NACIONALIDAD] Buscando: " + nacionalidadMayusculas);
            
            WebElement cbxNacionalidad = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:nacionalidad")));
            Select desplegableNCL = new Select(cbxNacionalidad);
            desplegableNCL.selectByVisibleText(nacionalidadMayusculas);
            System.out.println("[SIRE-NACIONALIDAD] ✓ Seleccionada: " + nacionalidadMayusculas);
        } catch (Exception e) {
            System.out.println("No se pude seleccionar la nacionalidad: " + e + "-------------------------------------------------------------------------------------------------------------------------");
            return false;
        }
        // pais de procedencia
        try {
            // SIRE usa nombres en MAYÚSCULAS
            String paisProceMayusculas = paisProce.toUpperCase();
            System.out.println("[SIRE-PROCEDENCIA] País: " + paisProceMayusculas);
            
            WebElement procePais = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:procedencia")));
            Select paisProcedencia = new Select(procePais);
            paisProcedencia.selectByVisibleText(paisProceMayusculas);
            System.out.println("[SIRE-PROCEDENCIA] ✓ País seleccionado");

        } catch (Exception e) {
            System.out.println("[SIRE-PROCEDENCIA] ❌ Error seleccionando país: " + paisProce + " - " + e.getMessage());
            return false;
        }

        try {
            if ("COLOMBIA".equals(paisProce.toUpperCase())) {
                System.out.println("[SIRE-PROCEDENCIA] País es COLOMBIA, esperando carga de departamentos...");
                // Esperar 2 segundos para que se carguen los departamentos
                Thread.sleep(2000);
                
                String deparProceMayusculas = deparProce.toUpperCase();
                System.out.println("[SIRE-PROCEDENCIA] Departamento: " + deparProceMayusculas);
                
                WebElement proceDepar = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:dptoProcedencia")));
                Select deparProcedencia = new Select(proceDepar);
                deparProcedencia.selectByVisibleText(deparProceMayusculas);
                System.out.println("[SIRE-PROCEDENCIA] ✓ Departamento seleccionado");
                
                System.out.println("[SIRE-PROCEDENCIA] Esperando carga de ciudades...");
                // Esperar 2 segundos para que se carguen las ciudades
                Thread.sleep(2000);
                
                String ciudadProceMayusculas = ciudadProce.toUpperCase();
                System.out.println("[SIRE-PROCEDENCIA] Ciudad: " + ciudadProceMayusculas);
                
                WebElement proceCiudad = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:ciudadProcedencia")));
                Select ciudadProcedencia = new Select(proceCiudad);
                ciudadProcedencia.selectByVisibleText(ciudadProceMayusculas);
                System.out.println("[SIRE-PROCEDENCIA] ✓ Ciudad seleccionada");
            }
        } catch (Exception e) {
            System.out.println("[SIRE-PROCEDENCIA] ❌ Error seleccionando departamento/ciudad: " + deparProce + "/" + ciudadProce);
            System.out.println("[SIRE-PROCEDENCIA] Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        try {
            // SIRE usa nombres en MAYÚSCULAS
            String paisDestMayusculas = paisDest.toUpperCase();
            System.out.println("[SIRE-DESTINO] País: " + paisDestMayusculas);
            
            WebElement destPais = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:destino")));
            Select paisDestino = new Select(destPais);
            paisDestino.selectByVisibleText(paisDestMayusculas);
            System.out.println("[SIRE-DESTINO] ✓ País seleccionado");

        } catch (Exception e) {
            System.out.println("[SIRE-DESTINO] ❌ Error seleccionando país: " + paisDest + " - " + e.getMessage());
            return false;
        }

        try {
            if ("COLOMBIA".equals(paisDest.toUpperCase())) {
                System.out.println("[SIRE-DESTINO] País es COLOMBIA, esperando carga de departamentos...");
                // Esperar 2 segundos para que se carguen los departamentos
                Thread.sleep(2000);
                
                String deparDestMayusculas = deparDest.toUpperCase();
                System.out.println("[SIRE-DESTINO] Departamento: " + deparDestMayusculas);
                
                WebElement destDepar = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:dptoDestino")));
                Select deparDestino = new Select(destDepar);
                deparDestino.selectByVisibleText(deparDestMayusculas);
                System.out.println("[SIRE-DESTINO] ✓ Departamento seleccionado");
                
                System.out.println("[SIRE-DESTINO] Esperando carga de ciudades...");
                // Esperar 2 segundos para que se carguen las ciudades
                Thread.sleep(2000);
                
                String ciudadDestMayusculas = ciudadDest.toUpperCase();
                System.out.println("[SIRE-DESTINO] Ciudad: " + ciudadDestMayusculas);
                
                WebElement destCiudad = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:ciudadDestino")));
                Select ciudadDestino = new Select(destCiudad);
                ciudadDestino.selectByVisibleText(ciudadDestMayusculas);
                System.out.println("[SIRE-DESTINO] ✓ Ciudad seleccionada");
            }
        } catch (Exception e) {
            System.out.println("[SIRE-DESTINO] ❌ Error seleccionando departamento/ciudad: " + deparDest + "/" + ciudadDest);
            System.out.println("[SIRE-DESTINO] Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        // Quitar el foco de cualquier elemento del formulario para evitar envíos accidentales
        try {
            // Reutilizar la variable js ya declarada anteriormente
            js.executeScript("document.activeElement.blur();");
            System.out.println("[SIRE] ✓ Foco removido de elementos del formulario");
        } catch (Exception e) {
            System.out.println("[SIRE] ⚠ No se pudo remover el foco: " + e.getMessage());
        }

        // PASO 1: Esperar y hacer clic automáticamente en "Agregar Registro"
        try {
            System.out.println("[SIRE] Esperando que aparezca el botón 'Agregar Registro'...");
            WebElement agregarRegistroButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("cargueFormHospedaje:j_id877")));
            System.out.println("[SIRE] ✓ Botón 'Agregar Registro' encontrado");
            
            // Preparar la ventana de Chrome antes de hacer clic
            try {
                System.out.println("[SIRE] Preparando ventana del navegador...");
                driver.manage().window().maximize();
                Thread.sleep(500); // Dar tiempo para que se maximice
                System.out.println("[SIRE] ✓ Ventana maximizada");
            } catch (Exception e) {
                System.out.println("[SIRE] ⚠ Error al maximizar: " + e.getMessage());
            }
            
            // Hacer clic en el botón "Agregar Registro"
            System.out.println("[SIRE] Haciendo clic en 'Agregar Registro'...");
            agregarRegistroButton.click();
            System.out.println("[SIRE] ✓ Clic realizado en 'Agregar Registro'");
            
            // Esperar un momento para que SIRE procese el clic y muestre el botón "Guardar"
            Thread.sleep(1500);
            
        } catch (Exception e) {
            System.out.println("[SIRE] Error al hacer clic en 'Agregar Registro': " + e.getMessage());
            return false;
        }
        
        // PASO 2: Esperar a que aparezca el botón "Guardar" y mostrar mensaje al usuario
        try {
            System.out.println("[SIRE] Esperando que aparezca el botón 'Guardar'...");
            // El botón "Guardar" suele aparecer después de "Agregar Registro"
            // Intentar encontrarlo para confirmar que el formulario está listo
            try {
                // Esperar hasta 5 segundos a que aparezca el botón "Guardar"
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement guardarButton = shortWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@value='Guardar' or @value='GUARDAR' or contains(@id, 'guardar') or contains(@id, 'Guardar')]")
                ));
                
                System.out.println("[SIRE] ✓ Botón 'Guardar' detectado");
            } catch (Exception e) {
                System.out.println("[SIRE] ⚠ No se detectó el botón 'Guardar' específicamente, pero continuando...");
            }
            
            // Mostrar mensaje al usuario para que revise y haga clic en "Guardar"
            JOptionPane.showMessageDialog(null, 
                "✅ REGISTRO AGREGADO AL FORMULARIO SIRE\n\n" +
                "El registro ha sido agregado al formulario exitosamente.\n\n" +
                "Por favor:\n" +
                "1. Verifique que todos los datos sean correctos\n" +
                "2. Haga clic en el botón 'GUARDAR'\n" +
                "3. Espere la confirmación de SIRE\n\n" +
                "El navegador permanecerá abierto para que pueda completar el proceso.",
                "Reporte SIRE - Acción Requerida",
                JOptionPane.INFORMATION_MESSAGE);
            
            System.out.println("[SIRE] Usuario notificado - Trayendo Chrome al frente...");
            
            // Usar Robot para traer Chrome al frente después de cerrar el diálogo
            try {
                // Esperar 1 segundo para que el JOptionPane se cierre completamente
                Thread.sleep(1000);
                
                // Usar Robot para simular ALT+TAB y traer Chrome al frente
                Robot robot = new Robot();
                robot.keyPress(KeyEvent.VK_ALT);
                robot.keyPress(KeyEvent.VK_TAB);
                Thread.sleep(100); // Pequeña pausa entre press y release
                robot.keyRelease(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_ALT);
                
                System.out.println("[SIRE] ✓ Ventana del navegador traída al frente con Robot");
            } catch (AWTException e) {
                System.out.println("[SIRE] ⚠ Error con Robot: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("[SIRE] ⚠ Error en Thread.sleep: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[SIRE] ⚠ No se pudo traer completamente al frente: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("[SIRE] Error al mostrar mensaje de confirmación: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void cerrarDriver(boolean cerrarNavegador) {
        if (driver != null) {
            try {
                if (cerrarNavegador) {
                    System.out.println("[SIRE] Cerrando navegador y liberando recursos...");
                    driver.quit();
                    System.out.println("[SIRE] ✓ Navegador cerrado correctamente");
                } else {
                    System.out.println("[SIRE] Manteniendo navegador abierto (solo liberando referencia)");
                }
            } catch (Exception e) {
                System.err.println("[SIRE] ⚠ Error al cerrar driver: " + e.getMessage());
            } finally {
                if (cerrarNavegador) {
                    System.out.println("[SIRE-CLOSE] Limpiando referencias (ID: " + System.identityHashCode(this) + ")");
                    driver = null;
                    // Limpiar instancia activa si es esta instancia
                    if (instanciaActiva == this) {
                        instanciaActiva = null;
                        System.out.println("[SIRE-CLOSE] ✓ instanciaActiva limpiada");
                    } else {
                        System.out.println("[SIRE-CLOSE] ⚠ instanciaActiva NO es esta instancia, no se limpia");
                    }
                } else {
                    System.out.println("[SIRE-CLOSE] Navegador NO cerrado - Referencias preservadas (ID: " + System.identityHashCode(this) + ")");
                    System.out.println("[SIRE-CLOSE]   - driver != null: " + (driver != null));
                    System.out.println("[SIRE-CLOSE]   - instanciaActiva == this: " + (instanciaActiva == this));
                }
            }
        }
    }

    /*
    public static void main(String[] args) {
        ReporteSire accesoWeb = new ReporteSire();
        accesoWeb.abrirPagina();
    }*/
}
