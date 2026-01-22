package modelo;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MensajesWATest2 {

    private WebDriver driver;
    private String localPath;
    private boolean APARECIO_QR = false;
    private boolean SESION_INICIADA = false;
    private boolean yaCodificado = false;

    public MensajesWATest2() {
        try {
            localPath = new java.io.File(".").getCanonicalPath();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error! no se pudo conectar WhatsApp Web \nTIPO: " + e);
        }
    }

    public boolean esperaExplicita(int seconds) {
        try {
            Thread.sleep(seconds);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void iniciarDriver() {
        if (driver == null) {
            try {
                System.setProperty("webdriver.chrome.driver", "C:\\WebDriver\\chromedriver.exe");

                ChromeOptions optionsGoo = new ChromeOptions();
                optionsGoo.addArguments("--no-sandbox", "--disable-notifications", "--user-data-dir=" + localPath + "\\chromeWA");

                driver = new ChromeDriver(optionsGoo);
                // enviamos laventana emergente ala parte de atras
                try {
                    Thread.sleep(10);
                    Robot robot = new Robot();
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_TAB);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    robot.keyRelease(KeyEvent.VK_TAB);
                } catch (AWTException e) {
                    e.printStackTrace();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MensajesWATest.class.getName()).log(Level.SEVERE, null, ex);
                }
                driver.manage().window().maximize();

            } catch (Exception e) {
                System.out.println("Fallo al iniciar el WebDriver: " + e.getMessage());

            }

        } else {
            // enviamos laventana emergente ala parte de atras
                try {
                    Thread.sleep(10);
                    Robot robot = new Robot();
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_TAB);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    robot.keyRelease(KeyEvent.VK_TAB);
                } catch (AWTException e) {
                    e.printStackTrace();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MensajesWATest.class.getName()).log(Level.SEVERE, null, ex);
                }
            driver.manage().window().maximize();
        }
    }

    public boolean navegarAWhatsApp(String telefono, String mensaje) {
        try {
            String url = "https://web.whatsapp.com/send?phone=" + telefono + "&text=" + mensaje;
            driver.get(url);
            return true;
        } catch (Exception e) {
            System.out.println("Fallo al navegar a WhatsApp: " + e.getMessage());
            return false;
        }
    }

    public boolean enviarMensaje(String telefono, String mensaje) {
        if (!yaCodificado) {
            try {
                mensaje = URLEncoder.encode(mensaje, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(MensajesWATest2.class.getName()).log(Level.SEVERE, null, ex);
            }
            yaCodificado = true;
        }
        if (driver == null) {
            iniciarDriver();
            if (navegarAWhatsApp(telefono, mensaje)) {
                esperaExplicita(1000);
                if (verificarPantallaQR()) {
                    esperaExplicita(1000);
                    JOptionPane.showMessageDialog(null, """
                                                ***FALTA INICIO DE SESION EN WHATSAPP***
                                                
                                                1-> Debe escanear el codigo QR para inciar
                                                sesion en WhatsApp.
                                                
                                                2-> Vuelva y de click en el boton OK, de este cuadro
                                                de dialogo para seguier con el proceso
                                                """);
                    boolean sesionIniciada = esperarInicioDeSesion();
                    if (sesionIniciada || SESION_INICIADA) {
                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                        WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[aria-label='Enviar']")));

                        sendButton.click();
                        esperaExplicita(1000);
                        cerrarDriver();
                        return true;
                    }
                } else if (esperarInicioDeSesion()) {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                    WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[aria-label='Enviar']")));

                    sendButton.click();
                    esperaExplicita(1000);
                    cerrarDriver();
                    return true;

                }

            } else {
                System.out.println("no se pudo navegar a watsaap. error de la linea 113");
            }
        } else {
            if (navegarAWhatsApp(telefono, mensaje)) {
                esperaExplicita(1000);
                if (verificarPantallaQR()) {
                    esperaExplicita(1000);
                    JOptionPane.showMessageDialog(null, """
                                                ***FALTA INICIO DE SESION EN WHATSAPP***
                                                
                                                1-> Debe escanear el codigo QR para inciar
                                                sesion en WhatsApp.
                                                
                                                2-> Vuelva y de click en el boton OK, de este cuadro
                                                de dialogo para seguier con el proceso
                                                """);
                    boolean sesionIniciada = esperarInicioDeSesion();
                    if (sesionIniciada || SESION_INICIADA) {
                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                        WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[aria-label='Enviar']")));

                        sendButton.click();
                        esperaExplicita(1000);
                        cerrarDriver();
                        return true;
                    }
                } else if (esperarInicioDeSesion()) {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                    WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[aria-label='Enviar']")));

                    sendButton.click();
                    esperaExplicita(1000);
                    cerrarDriver();
                    return true;

                }

            } else {
                System.out.println("no se pudo navegar a la url de whatsaap:  error de la linea 107");
            }

        }
        return false;
    }

    private boolean esperarInicioDeSesion() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[aria-label='foto del perfil']")));
            SESION_INICIADA = true;
            return SESION_INICIADA;
        } catch (Exception e) {
            System.out.println("La foto de perfil no aparecio: " + e.getMessage());
        }

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button._ai0b._ai08")));
            SESION_INICIADA = true;
            return SESION_INICIADA;
        } catch (Exception e) {
            System.out.println("No se encontro el boton de buscar: " + e.getMessage());
        }

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span[data-icon='search']")));
            SESION_INICIADA = true;
            return SESION_INICIADA;
        } catch (Exception e) {
            System.out.println("No se encontro el icono de buscar:  " + e.getMessage());
        }

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[title='Detalles del perfil']")));
            SESION_INICIADA = true;
            return SESION_INICIADA;
        } catch (Exception e) {
            System.out.println("Boton de detalles del prfil no encontrado  " + e.getMessage());
        }

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("_ak1i")));
            SESION_INICIADA = true;
            return SESION_INICIADA;
        } catch (Exception e) {
            System.out.println("Footer de whatsaap no encontrado:  " + e.getMessage());
        }

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[aria-label='Adjuntar']")));
            SESION_INICIADA = true;
            return SESION_INICIADA;
        } catch (Exception e) {
            System.out.println("Boton de footer de adjuntar no encontrado: " + e.getMessage());
        }

        return SESION_INICIADA;
    }

    public boolean verificarPantallaQR() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("canvas[aria-label='Scan me!']")));
            APARECIO_QR = true;
        } catch (Exception e) {
            System.out.println("El QR no apareció: " + e.getMessage());
        }

        try {
            if (!APARECIO_QR && !driver.findElements(By.cssSelector("button[text*='Descargar de Microsoft Store']")).isEmpty()) {
                APARECIO_QR = true;
            }
        } catch (Exception e) {
            System.out.println("El botón de descarga no fue encontrado: " + e.getMessage());
        }

        try {
            if (!APARECIO_QR && !driver.findElements(By.xpath("//*[contains(text(),'Vincular con el número de teléfono')]")).isEmpty()) {
                APARECIO_QR = true;
            }
        } catch (Exception e) {
            System.out.println("El texto de vinculación no fue encontrado: " + e.getMessage());
        }

        return APARECIO_QR;
    }

    public void cerrarDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public static void main(String[] args) {
        System.out.println("Iniciando el programa...");
        // Crear una instancia de la clase con el WebDriver
        MensajesWATest2 mensajesWA = new MensajesWATest2();

        // Iniciar el WebDriver y navegar a WhatsApp
        mensajesWA.iniciarDriver();

        // Definir el número de teléfono y el mensaje a enviar
        String telefono = "+573232951780";  // Reemplazar con un número de teléfono real
        String mensaje = "Hola, esto es una prueba desde MensajesWATest2!";

        // Enviar el mensaje
        boolean resultado = mensajesWA.enviarMensaje(telefono, mensaje);
        if (resultado) {
            System.out.println("Mensaje enviado exitosamente!");
        } else {
            System.out.println("No se pudo enviar el mensaje.");
        }
    }

}
