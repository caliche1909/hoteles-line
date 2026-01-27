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
import io.github.bonigarcia.wdm.WebDriverManager;

public class MensajesWATest2 {

    private WebDriver driver;
    private String localPath;
    private boolean yaCodificado = false;

    public MensajesWATest2() {
        try {
            localPath = new java.io.File(".").getCanonicalPath();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error! no se pudo conectar WhatsApp Web \nTIPO: " + e);
        }
    }

    public void iniciarDriver() {
        if (driver == null) {
            try {
                System.out.println("[WA] Configurando WebDriverManager...");
                WebDriverManager.chromedriver().setup();

                ChromeOptions optionsGoo = new ChromeOptions();
                // Opciones para mejorar rendimiento
                optionsGoo.addArguments(
                    "--no-sandbox",
                    "--disable-notifications",
                    "--disable-gpu",
                    "--disable-dev-shm-usage",
                    "--disable-extensions",
                    "--disable-popup-blocking",
                    "--start-maximized",
                    "--user-data-dir=" + localPath + "\\chromeWA"
                );
                // Silenciar logs innecesarios
                optionsGoo.addArguments("--log-level=3");
                optionsGoo.setExperimentalOption("excludeSwitches", new String[]{"enable-logging"});

                System.out.println("[WA] Iniciando Chrome...");
                driver = new ChromeDriver(optionsGoo);
                
                // Enviar ventana al fondo
                enviarVentanaAlFondo();
                
                System.out.println("[WA] Chrome iniciado correctamente");

            } catch (Exception e) {
                System.out.println("[WA] ERROR al iniciar WebDriver: " + e.getMessage());
            }
        }
    }

    private void enviarVentanaAlFondo() {
        try {
            Thread.sleep(100);
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_TAB);
        } catch (AWTException | InterruptedException e) {
            // Ignorar - no es crítico
        }
    }

    public boolean enviarMensaje(String telefono, String mensaje) {
        System.out.println("[WA] === INICIANDO ENVIO DE MENSAJE ===");
        
        // Codificar mensaje si no está codificado
        if (!yaCodificado) {
            try {
                mensaje = URLEncoder.encode(mensaje, "UTF-8");
                yaCodificado = true;
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(MensajesWATest2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
            // Iniciar driver si es necesario
            if (driver == null) {
                iniciarDriver();
            }
            
            // Navegar a WhatsApp Web con el mensaje
            String url = "https://web.whatsapp.com/send?phone=" + telefono + "&text=" + mensaje;
            System.out.println("[WA] Navegando a WhatsApp Web...");
            driver.get(url);
            
            // Esperar a que cargue la página (máximo 15 segundos)
            WebDriverWait waitCorto = new WebDriverWait(driver, Duration.ofSeconds(15));
            
            // Verificar si aparece QR (sesión no iniciada)
            System.out.println("[WA] Verificando estado de sesión...");
            if (necesitaInicioSesion()) {
                System.out.println("[WA] Se requiere escanear código QR");
                JOptionPane.showMessageDialog(null, 
                    "***FALTA INICIO DE SESION EN WHATSAPP***\n\n" +
                    "1-> Escanea el código QR en WhatsApp Web\n" +
                    "2-> Haz clic en OK cuando hayas iniciado sesión",
                    "WhatsApp", JOptionPane.INFORMATION_MESSAGE);
                
                // Esperar a que el usuario inicie sesión (máximo 60 segundos)
                if (!esperarSesionIniciada(60)) {
                    System.out.println("[WA] Timeout esperando inicio de sesión");
                    cerrarDriver();
                    return false;
                }
            }
            
            // Esperar y hacer clic en el botón de enviar
            System.out.println("[WA] Buscando botón de enviar...");
            WebDriverWait waitEnviar = new WebDriverWait(driver, Duration.ofSeconds(30));
            
            // Intentar encontrar el botón de enviar con diferentes selectores
            WebElement sendButton = esperarBotonEnviar(waitEnviar);
            
            if (sendButton != null) {
                System.out.println("[WA] Botón encontrado, enviando mensaje...");
                sendButton.click();
                
                // Pequeña espera para que se envíe
                Thread.sleep(2000);
                
                System.out.println("[WA] ✅ MENSAJE ENVIADO EXITOSAMENTE");
                cerrarDriver();
                return true;
            } else {
                System.out.println("[WA] No se encontró el botón de enviar");
                cerrarDriver();
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("[WA] ERROR: " + e.getMessage());
            cerrarDriver();
            return false;
        }
    }

    /**
     * Verifica si se necesita iniciar sesión (aparece QR)
     */
    private boolean necesitaInicioSesion() {
        try {
            // Esperar un poco a que cargue
            Thread.sleep(3000);
            
            // Buscar canvas del QR o texto de vincular
            boolean hayQR = !driver.findElements(By.cssSelector("canvas[aria-label='Scan me!']")).isEmpty();
            boolean hayTextoVincular = !driver.findElements(By.xpath("//*[contains(text(),'Vincular con el número')]")).isEmpty();
            boolean hayPantallaInicio = !driver.findElements(By.cssSelector("div[data-testid='qrcode']")).isEmpty();
            
            return hayQR || hayTextoVincular || hayPantallaInicio;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Espera a que se inicie sesión verificando múltiples elementos en paralelo
     */
    private boolean esperarSesionIniciada(int maxSegundos) {
        System.out.println("[WA] Esperando inicio de sesión (máx " + maxSegundos + " seg)...");
        
        long inicio = System.currentTimeMillis();
        long timeout = maxSegundos * 1000L;
        
        while ((System.currentTimeMillis() - inicio) < timeout) {
            try {
                // Verificar múltiples indicadores de sesión iniciada
                boolean sesionActiva = 
                    !driver.findElements(By.cssSelector("button[aria-label='Enviar']")).isEmpty() ||
                    !driver.findElements(By.cssSelector("div[aria-label='Adjuntar']")).isEmpty() ||
                    !driver.findElements(By.cssSelector("span[data-icon='send']")).isEmpty() ||
                    !driver.findElements(By.cssSelector("footer")).isEmpty() ||
                    !driver.findElements(By.cssSelector("div[contenteditable='true']")).isEmpty();
                
                if (sesionActiva) {
                    System.out.println("[WA] Sesión detectada como activa");
                    return true;
                }
                
                // Verificar si ya no hay QR (usuario lo escaneó)
                boolean qrDesaparecio = driver.findElements(By.cssSelector("canvas[aria-label='Scan me!']")).isEmpty() &&
                                        driver.findElements(By.cssSelector("div[data-testid='qrcode']")).isEmpty();
                
                if (qrDesaparecio) {
                    System.out.println("[WA] QR desapareció, esperando carga...");
                    Thread.sleep(3000);
                    return true;
                }
                
                Thread.sleep(1000);
            } catch (Exception e) {
                // Continuar intentando
            }
        }
        
        return false;
    }

    /**
     * Espera el botón de enviar con múltiples selectores
     */
    private WebElement esperarBotonEnviar(WebDriverWait wait) {
        // Lista de selectores posibles para el botón de enviar
        String[] selectores = {
            "button[aria-label='Enviar']",
            "span[data-icon='send']",
            "button[data-testid='send']",
            "button[aria-label='Send']"
        };
        
        for (int intento = 0; intento < 3; intento++) {
            for (String selector : selectores) {
                try {
                    WebElement elemento = wait.until(
                        ExpectedConditions.elementToBeClickable(By.cssSelector(selector))
                    );
                    if (elemento != null) {
                        return elemento;
                    }
                } catch (Exception e) {
                    // Probar siguiente selector
                }
            }
            
            // Si no encontró, esperar un poco y reintentar
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                break;
            }
        }
        
        return null;
    }

    public void cerrarDriver() {
        if (driver != null) {
            try {
                System.out.println("[WA] Cerrando navegador...");
                driver.quit();
            } catch (Exception e) {
                System.out.println("[WA] Error al cerrar: " + e.getMessage());
            } finally {
                driver = null;
                yaCodificado = false;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE ENVIO WHATSAPP ===");
        MensajesWATest2 mensajesWA = new MensajesWATest2();
        
        String telefono = "+573232951780";
        String mensaje = "Hola, esto es una prueba desde MensajesWATest2!";
        
        boolean resultado = mensajesWA.enviarMensaje(telefono, mensaje);
        
        if (resultado) {
            System.out.println("✅ Mensaje enviado exitosamente!");
        } else {
            System.out.println("❌ No se pudo enviar el mensaje.");
        }
    }
}
