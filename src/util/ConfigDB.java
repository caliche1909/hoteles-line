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
        
        // Detectar entorno automáticamente
        detectarEntorno();
        
        // Cargar archivo de configuración según el entorno
        String configFile = isDevelopment() ? "config-dev.properties" : "config-prod.properties";
        
        try {
            // Intentar cargar desde el classpath primero
            input = ConfigDB.class.getClassLoader().getResourceAsStream(configFile);
            
            // Si no está en classpath, intentar desde el directorio raíz del proyecto
            if (input == null) {
                input = new FileInputStream(configFile);
            }
            
            properties.load(input);
            System.out.println("✓ Configuración cargada desde: " + configFile);
            System.out.println("✓ Entorno: " + environment.toUpperCase());
            System.out.println("✓ Base de Datos: " + getUrl());
        } catch (IOException e) {
            System.err.println("⚠ No se pudo cargar " + configFile + ", usando valores por defecto");
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
    
    private static void detectarEntorno() {
        // Prioridad 1: Variable de entorno del sistema
        String sysEnv = System.getenv("APP_ENVIRONMENT");
        if (sysEnv != null && !sysEnv.isEmpty()) {
            environment = sysEnv.toLowerCase();
            System.out.println("🔍 Entorno detectado desde variable de sistema: " + environment);
            return;
        }
        
        // Prioridad 2: Propiedad del sistema (java -DAPP_ENVIRONMENT=production)
        String sysProp = System.getProperty("APP_ENVIRONMENT");
        if (sysProp != null && !sysProp.isEmpty()) {
            environment = sysProp.toLowerCase();
            System.out.println("🔍 Entorno detectado desde propiedad de sistema: " + environment);
            return;
        }
        
        // Prioridad 3: Archivo .env (para indicar producción)
        if (new java.io.File(".production").exists()) {
            environment = "production";
            System.out.println("🔍 Entorno detectado desde archivo .production: " + environment);
            return;
        }
        
        // Por defecto: desarrollo
        environment = "development";
        System.out.println("🔍 Usando entorno por defecto: " + environment);
    }
    
    private static void cargarConfiguracionPorDefecto() {
        // Configuración por defecto según el entorno detectado
        if (isDevelopment()) {
            properties.setProperty("db.url", "jdbc:mysql://localhost:3306/doralplazapruebas");
            properties.setProperty("db.username", "root");
            properties.setProperty("db.password", "Carlos.2020#");
            properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            System.out.println("📝 Usando configuración por defecto - Entorno: DEVELOPMENT (doralplazapruebas)");
        } else {
            properties.setProperty("db.url", "jdbc:mysql://localhost:3306/doralplaza");
            properties.setProperty("db.username", "root");
            properties.setProperty("db.password", "Carlos.2020#");
            properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            System.out.println("📝 Usando configuración por defecto - Entorno: PRODUCTION (doralplaza)");
        }
    }
    
    public static String getUrl() {
        return properties.getProperty("db.url");
    }
    
    public static String getUsername() {
        return properties.getProperty("db.username");
    }
    
    public static String getPassword() {
        return properties.getProperty("db.password");
    }
    
    public static String getDriver() {
        return properties.getProperty("db.driver");
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
