package modelo;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.openqa.selenium.WebElement;

public class MensajesWATest {

    private WebDriver driver;
    private String localPath;
    private static final int MAX_RETRIES = 2;
    private boolean resultado = false;
    private boolean yaCodificado = false;
    private boolean primerIntento = true;

    public MensajesWATest() {
        try {
            localPath = new java.io.File(".").getCanonicalPath();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error! no se pudo conectar WhatsApp Web \nTIPO: " + e);
        }
    }

    private void iniciarDriver() {

        System.setProperty("webdriver.chrome.driver", "C:\\WebDriver\\chromedriver-win64\\chromedriver.exe");
        //System.setProperty("webdriver.chrome.silentOutput", "true"); para silenciar las impresines en consola

        ChromeOptions optionsGoo = new ChromeOptions();
        optionsGoo.addArguments("--no-sandbox", "--disable-notifications", "--user-data-dir=" + localPath + "\\chromeWA");
        driver = new ChromeDriver(optionsGoo);

        System.out.println("Sesión de Chrome iniciada: " + driver);
        System.out.println("URL actual: " + driver.getCurrentUrl());

        // enviamos laventana emergente ala parte de atras
        try {
            Thread.sleep(500);
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
    }

    public boolean navegarURLtelymen(String telefono, String mensaje) {
        try {
            driver.get("https://web.whatsapp.com/send?phone=" + telefono + "&text=" + mensaje);
            System.out.println("url del metodo uno conquistada metodo 1 paso 2------------------------------------------------------------------------------------------------------------------------------------------------------");
            resultado = true;
        } catch (Exception e) {
            System.out.println("Error al navegar a la URL de WhatsApp Web. TIPO: metodo 1-----------------------------------------------------------------------------------------------------------" + e);
            JOptionPane.showMessageDialog(null, "Error al navegar a la URL de WhatsApp Web. TIPO: " + e);
            resultado = false;
        }
        return resultado;
    }

    public void queAparece() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@aria-label='Enviar']")),
                    ExpectedConditions.visibilityOfElementLocated(By.className("_2I5ox")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@data-testid='popup-controls-cancel']"))
            ));
            System.out.println("aparecio un elemento-  paso 3---------------------------------------------------------------------------");

        } catch (Exception e) {
            System.out.println("no aparecio ninguno de los elementos----------------------------------------------------------------------------");
        }

    }

    public boolean enviarMensajes1(String telefono, String mensaje) throws UnsupportedEncodingException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
        primerIntento = false;
        System.out.println("metodo 1  paso 1---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        try {

            // Intentamos navegar a la URL de WhatsApp Web con el número de teléfono y el mensaje codificado
            resultado = navegarURLtelymen(telefono, mensaje);

            queAparece();

            // caso en el que se encuentra el codigo QR
            if (!driver.findElements(By.className("_2I5ox")).isEmpty()) {
                System.out.println("aprecio el qr metodo 1 paso 4-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                JOptionPane.showMessageDialog(null, """
                                                        Por favor, dirijete a WhatsApp Web,
                                                        escanea el dodigo QR y vuelve aqui a darle click al boton
                                                        'Ok' de este cuadro de dialogo para poder seguir 
                                                        con el proceso""");
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("_2I5ox")));
                System.out.println("Elemento con clase '_2I5ox' ha desaparecido. metodo 1 paso 5------------------------------------------------------------------------------------------------------------------------------------");

                yaCodificado = true;
                resultado = sendMessages(telefono, mensaje);
                return resultado;
                //wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div._3OtEr[data-testid='menu-bar-menu']")));
                //System.out.println("ya se encontro el menu bar----------------------------------------");

                //System.out.println("El elemento menu bar ya está presente en la página bse volvio a llamar a sendMesssages-- se repiten los pasos----------------------------------------------------------------------------------------------");
                // caso en el que se encuentra el boton de dar click    
            } else if (!driver.findElements(By.xpath("//button[@aria-label='Enviar']")).isEmpty()) {
                System.out.println("aprecio el boton de dar click 1 paso 4-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                darClick();
                resultado = true;
            } else if (!driver.findElements(By.xpath("//button[@data-testid='popup-controls-cancel']")).isEmpty()) {
                System.out.println("aprecio el qr metodo 1 paso 4-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                WebDriverWait espera = new WebDriverWait(driver, Duration.ofSeconds(120));
                espera.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//button[@data-testid='popup-controls-cancel']")));
                System.out.println("aprecio el qr metodo 1 paso 5-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MensajesWATest.class.getName()).log(Level.SEVERE, null, ex);
                }
                darClick();

                resultado = true;
            } else {
                JOptionPane.showMessageDialog(null, "No se envió Mensaje de WhatsApp");
                resultado = false;

            }

            // Si todo va bien, devolvemos true
            return resultado;

        } catch (HeadlessException e) {
            resultado = false;
            System.out.println("hubo un error en el try catch mas grande----------------------------------------------------------------------------------------------------------------------");
        }
        return resultado;
    }

    public boolean darClick() {
        System.out.println("Intentando hacer clic en el botón de enviar mensaje...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean clicked = false;
        int retries = 3; // Número de intentos

        while (!clicked && retries > 0) {
            try {
                WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='Enviar']")));
                sendButton.click();
                System.out.println("Botón encontrado y clickeado exitosamente.");
                clicked = true;
                resultado = true;
            } catch (Exception e) {
                retries--;
                System.out.println("Error al hacer clic en el botón de envío de mensaje. Intentos restantes: " + retries + ". Detalles: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (!clicked) {
            System.out.println("No se pudo hacer clic en el botón. Intentando presionar Enter en el siguiente elemento...");

            try {
                WebElement nextElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@class='selectable-text copyable-text x15bjb6t x1n2onr6']")));
                nextElement.click(); // Hacer foco en el elemento

                // Usar Robot para simular la pulsación de Enter
                Robot robot = new Robot();
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);

                System.out.println("Se presionó Enter en el siguiente elemento exitosamente.");
                resultado = true;
            } catch (Exception e) {
                System.out.println("Error al presionar Enter en el siguiente elemento. Detalles: " + e.getMessage());
                e.printStackTrace();
                resultado = false;
            }
        }

        return resultado;
    }

    public boolean sendMessages(String telefono, String mensaje) {

        boolean resultado = false;
        for (int i = 0; i < MAX_RETRIES && !resultado; i++) {
            if (primerIntento) {
                iniciarDriver();
            }

            try {
                // Codificamos el mensaje para que pueda ser enviado en una URL
                if (!yaCodificado) {
                    mensaje = URLEncoder.encode(mensaje, "UTF-8");
                }

                if (enviarMensajes1(telefono, mensaje)) {
                    resultado = true;
                }
            } catch (UnsupportedEncodingException e) {
                System.out.println("Error al codificar el mensaje: del metodo dos-------------------------------------------------------------------------------------------------------------------------" + e);
                e.printStackTrace();
            } finally {
                finalizarDriver();
            }
        }
        return resultado;
    }

    public void finalizarDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("no se pudo finalizar el driver: ---------------------------------------------------------------------------------------------------------" + e);
            }
        }
    }

    public static void main(String[] args) {

        MensajesWATest2 tester = new MensajesWATest2();
        String telefono = "+573232951780";
        String mensaje = "¡Hola! Este es un mensaje de prueba.";

        if (tester.enviarMensaje(telefono, mensaje)) {
            System.out.println("Mensaje enviado correctamente.");
        } else {
            System.out.println("Fallo al enviar el mensaje.");
        }
    }
}

/*public void finalizarDriver() {
        if (driver != null) {
            // En lugar de cerrar el navegador, solo minimízalo u ocúltalo
            driver.manage().window().setPosition(new Point(0, 0)); // Esto mueve la ventana fuera de la pantalla
            // Nota: no llamar a driver.quit() ni a driver.close()
        }
    }*/
 /*public static void main(String[] args) {
        // Crear una instancia de la clase MensajesWATest
        MensajesWATest mensajesWATest = new MensajesWATest();

        // Definir el número de teléfono y el mensaje a enviar
        String telefono = "+573232951780";  // Deberías reemplazar esto con el número de teléfono real
        String mensaje = "Hola, este es un mensaje de prueba.";  // Este es el mensaje que se enviará

        // Llamar al método sendMessages con el número de teléfono y el mensaje
        boolean resultado = mensajesWATest.sendMessages(telefono, mensaje);

        // Imprimir el resultado
        if (resultado) {
            System.out.println("El mensaje se ha enviado con éxito.  Resultado = " + resultado + "");
        } else {
            System.out.println("Ha ocurrido un error al intentar enviar el mensaje.  Resultado =" + resultado + "");
        }
    }*/

 /*<button data-testid="popup-controls-cancel" class="emrlamx0 aiput80m h1a80dm5 sta02ykp g0rxnol2 l7jjieqr hnx8ox4h 
    f8jlpxt4 l1l4so3b bbv8nyr4 m2gb0jvt rfxpxord gwd8mfxi mnh9o63b qmy7ya1v dcuuyf4k swfxs4et bgr8sfoe a6r886iw fx1ldmn8 
    orxa12fk bkifpc9x hjo1mxmu oixtjehm rpz5dbxo bn27j4ou snayiamo szmswy5k"><div class="tvf2evcx m0h2a7mj lb5m6g5c j7l1k36l 
    ktfrpxia nu7pwgvd p357zi0d dnb887gk gjuq5ydh i2cterl7 ac2vgrno sap93d0t gndfcl4n"><div class="tvf2evcx m0h2a7mj lb5m6g5c
    j7l1k36l ktfrpxia nu7pwgvd p357zi0d dnb887gk gjuq5ydh i2cterl7 i6vnu1w3 qjslfuze ac2vgrno sap93d0t gndfcl4n" data-testid="content">Cancelar</div></div></button>*/

 /*<div class="tvf2evcx m0h2a7mj lb5m6g5c j7l1k36l ktfrpxia nu7pwgvd p357zi0d dnb887gk gjuq5ydh i2cterl7 i6vnu1w3 qjslfuze 
    ac2vgrno sap93d0t gndfcl4n" data-testid="content">Cancelar</div>*/
 /* mensajes de entrada con la palabra watsaap <div class="progress"><progress value="0" max="100" dir="ltr"></progress></div>*/
 /*menu bar interno <div class="_3OtEr" data-testid="menu-bar-menu"><div aria-disabled="false" role="button" tabindex="0" class="_3ndVb fbgy3m38 
    ft2m32mm oq31bsqd nu34rnf1" data-tab="2" title="Menú" aria-label="Menú"><span data-testid="menu" data-icon="menu" class="">
    <svg viewBox="0 0 24 24" height="24" width="24" preserveAspectRatio="xMidYMid meet" class="" version="1.1" x="0px" y="0px" 
    enable-background="new 0 0 24 24" xml:space="preserve"><path fill="currentColor" 
    d="M12,7c1.104,0,2-0.896,2-2c0-1.105-0.895-2-2-2c-1.104,0-2,0.894-2,2 C10,6.105,10.895,7,12,7z M12,9c-1.104,0-2,0.894-2,2c0,1.104,
    0.895,2,2,2c1.104,0,2-0.896,2-2C13.999,9.895,13.104,9,12,9z M12,15 c-1.104,0-2,0.894-2,2c0,1.104,0.895,2,2,2c1.104,0,2-0.896,
    2-2C13.999,15.894,13.104,15,12,15z"></path></svg></span></div><span></span></div>*/
 /*cargando tus chats <div class="g0rxnol2 lk9bdx0e d9lyu8cj qlylaf53 d4g41f7d"><progress value="100" max="100" class="ZJWuG">
    </progress></div>*/
 /*cargando tus chats <div class="_3HbCE">Cargando tus chats…</div>*/
