# Proyecto Doral - Sistema de Gestión Hotelera

## 📋 Estado del Proyecto

Este es un sistema completo de gestión hotelera (PMS) desarrollado en Java con Swing.

## ✅ Prerequisitos Verificados

- ✅ **Java JDK 17.0.10** instalado
- ✅ **javac** (compilador) disponible
- ⚠️ **MySQL** - Requiere instalación/configuración

## 🗄️ Configuración de Base de Datos

### Credenciales actuales en el código:
- **Base de datos:** `doralplaza`
- **Host:** `localhost:3306`
- **Usuario:** `root`
- **Contraseña:** `Carlos.2020#`

### Para ejecutar el proyecto necesitas:

1. **Instalar MySQL** (si no lo tienes):
   - Descarga MySQL Community Server desde: https://dev.mysql.com/downloads/mysql/
   - O instala XAMPP que incluye MySQL

2. **Crear la base de datos:**
   - Necesitas el archivo SQL de la base de datos
   - Ejecutar: `CREATE DATABASE doralplaza;`
   - Importar el esquema y datos

3. **Verificar que MySQL esté ejecutándose:**
   - Si usas XAMPP: Inicia el panel de control y arranca MySQL
   - Si usas MySQL directo: El servicio debe estar activo

## 🚀 Cómo Ejecutar en VS Code

1. **Asegúrate de tener instalado:**
   - Extension Pack for Java (Microsoft)
   - Language Support for Java (Red Hat)

2. **Abrir el proyecto:**
   - Ya está configurado con `.vscode/settings.json` y `.vscode/launch.json`

3. **Ejecutar:**
   - Presiona `F5` o ve a "Run and Debug" 
   - Selecciona "Launch ProyectoDoral02"

## 📦 Librerías Incluidas

El proyecto ya incluye todas las librerías necesarias en `src/lib/`:
- MySQL Connector 8.0.31
- iText 7 (para PDFs)
- Selenium WebDriver (para WhatsApp)
- JCalendar 1.4
- bcrypt 0.4
- Apache Commons

## 🔍 Siguiente Paso

**¿Tienes MySQL instalado o prefieres que te ayude a instalarlo/configurarlo?**

También puedo:
- Ayudarte a encontrar o recrear el archivo SQL de la base de datos
- Guiarte en la instalación de XAMPP (forma más fácil)
- Revisar el código para generar el esquema de base de datos

## 📝 Notas Adicionales

- El proyecto está configurado para usar codificación UTF-8
- Genera facturas en PDF en la carpeta `facturas/`
- Tiene integración con WhatsApp Web usando Selenium
- Soporta impresoras térmicas para recibos
