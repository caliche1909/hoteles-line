package util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LoggingConfigurator {
    public static void configure() {
        // Obtener el logger global que afecta a todas las entradas de log
        Logger rootLogger = Logger.getLogger("");

        // Configurar el nivel de log deseado aquí
        rootLogger.setLevel(Level.WARNING);

        // Crear un manejador de consola para mostrar los mensajes de log
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.WARNING); // Establecer el nivel de log del manejador
        consoleHandler.setFormatter(new SimpleFormatter()); // Definir el formato de los logs

        // Remover todos los manejadores existentes y agregar el nuevo
        Logger.getLogger("").getHandlers()[0].setLevel(Level.WARNING);
        rootLogger.addHandler(consoleHandler);
    }
    
}
