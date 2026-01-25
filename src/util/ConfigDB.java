package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigDB {
    
    private static Properties properties = new Properties();
    private static String environment = "development"; // Por defecto desarrollo
    
    static {
        cargarConfiguracion();
    }
    
    private static void cargarConfiguracion() {
        InputStream input = null;
        try {
            // Intentar cargar desde el classpath primero
            input = ConfigDB.class.getClassLoader().getResourceAsStream("config.properties");
            
            // Si no está en classpath, intentar desde el directorio raíz del proyecto
            if (input == null) {
                input = new FileInputStream("config.properties");
            }
            
            properties.load(input);
            environment = properties.getProperty("environment", "development");
            System.out.println("✓ Configuración cargada - Entorno: " + environment.toUpperCase());
        } catch (IOException e) {
            System.err.println("⚠ No se pudo cargar config.properties, usando valores por defecto");
            System.err.println("Error: " + e.getMessage());
            cargarConfiguracionPorDefecto();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static void cargarConfiguracionPorDefecto() {
        // Configuración por defecto para desarrollo
        properties.setProperty("environment", "development");
        properties.setProperty("db.dev.url", "jdbc:mysql://localhost:3306/doralplazapruebas");
        properties.setProperty("db.dev.username", "root");
        properties.setProperty("db.dev.password", "Carlos.2020#");
        properties.setProperty("db.dev.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("db.prod.url", "jdbc:mysql://localhost:3306/doralplaza");
        properties.setProperty("db.prod.username", "root");
        properties.setProperty("db.prod.password", "Carlos.2020#");
        properties.setProperty("db.prod.driver", "com.mysql.cj.jdbc.Driver");
        System.out.println("📝 Usando configuración por defecto - Entorno: DEVELOPMENT (doralplazapruebas)");
    }
    
    public static String getUrl() {
        String prefix = isDevelopment() ? "db.dev." : "db.prod.";
        return properties.getProperty(prefix + "url");
    }
    
    public static String getUsername() {
        String prefix = isDevelopment() ? "db.dev." : "db.prod.";
        return properties.getProperty(prefix + "username");
    }
    
    public static String getPassword() {
        String prefix = isDevelopment() ? "db.dev." : "db.prod.";
        return properties.getProperty(prefix + "password");
    }
    
    public static String getDriver() {
        String prefix = isDevelopment() ? "db.dev." : "db.prod.";
        return properties.getProperty(prefix + "driver");
    }
    
    public static boolean isDevelopment() {
        return "development".equalsIgnoreCase(environment);
    }
    
    public static boolean isProduction() {
        return "production".equalsIgnoreCase(environment);
    }
    
    public static String getEnvironment() {
        return environment;
    }
    
    public static void setEnvironment(String env) {
        environment = env;
        System.out.println("✓ Entorno cambiado a: " + environment);
    }
}
