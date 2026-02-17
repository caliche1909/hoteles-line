# 🔐 Sistema de Configuración de Base de Datos

## ✅ Ahora GARANTIZADO: Producción usa `doralplaza`

Este proyecto usa un sistema automático de detección de entorno que **garantiza** que tu aplicación se conecte a la base de datos correcta.

---

## 📋 Cómo Funciona

### Desarrollo (Por Defecto)
- **Base de Datos:** `doralplazapruebas`
- **Archivo:** `config-dev.properties`
- ✅ Se activa automáticamente cuando NO existe el archivo `.production`

### Producción
- **Base de Datos:** `doralplaza`
- **Archivo:** `config-prod.properties`
- ✅ Se activa automáticamente cuando existe el archivo `.production`

---

## 🚀 Para Desarrollar (doralplazapruebas)

```powershell
# Simplemente ejecuta desde VS Code con F5
# O desde terminal:
java -cp "build/classes;src/lib/*" proyectodoral02.ProyectoDoral02
```

✅ Usará automáticamente `doralplazapruebas`

---

## 📦 Para Crear Build de Producción

```powershell
# Ejecutar el script de build
.\build-production.ps1
```

Esto creará:
- `dist/ProyectoDoral02.jar` - El ejecutable
- `dist/lib/` - Todas las librerías
- `dist/.production` - Marcador de entorno
- `dist/config-prod.properties` - Configuración de producción

---

## 🖥️ Para Ejecutar en Producción

### Opción 1: Usando el Archivo .production (Recomendado)

```powershell
# 1. Copiar todo el contenido de 'dist/' al servidor de producción
Copy-Item -Path "dist\*" -Destination "C:\Servidor\ProyectoDoral" -Recurse

# 2. Ir al directorio
cd C:\Servidor\ProyectoDoral

# 3. Crear el marcador (si no existe)
New-Item -ItemType File -Path ".production"

# 4. Ejecutar
java -jar ProyectoDoral02.jar
```

✅ Se conectará automáticamente a `doralplaza`

### Opción 2: Variable de Entorno

```powershell
# Establecer variable de entorno del sistema
$env:APP_ENVIRONMENT = "production"

# Ejecutar
java -jar ProyectoDoral02.jar
```

### Opción 3: Parámetro al Ejecutar

```powershell
java -DAPP_ENVIRONMENT=production -jar ProyectoDoral02.jar
```

---

## 🔍 Verificar Conexión

Al iniciar la aplicación, verás en la consola:

### Desarrollo:
```
🔍 Usando entorno por defecto: development
✓ Configuración cargada desde: config-dev.properties
✓ Entorno: DEVELOPMENT
✓ Base de Datos: jdbc:mysql://localhost:3306/doralplazapruebas
```

### Producción:
```
🔍 Entorno detectado desde archivo .production: production
✓ Configuración cargada desde: config-prod.properties
✓ Entorno: PRODUCTION
✓ Base de Datos: jdbc:mysql://localhost:3306/doralplaza
```

---

## ⚠️ Importante

- ✅ **NUNCA** subas `config-dev.properties` o `config-prod.properties` a Git (ya están en `.gitignore`)
- ✅ El archivo `.production` indica que es producción
- ✅ Si no existe `.production`, siempre usa desarrollo
- ✅ No hay manera de confundir los entornos

---

## 🛠️ Archivos del Sistema

| Archivo | Descripción |
|---------|-------------|
| `config-dev.properties` | Configuración de desarrollo (doralplazapruebas) |
| `config-prod.properties` | Configuración de producción (doralplaza) |
| `.production` | Marcador que indica entorno de producción |
| `build-production.ps1` | Script para crear build de producción |
| `src/util/ConfigDB.java` | Clase que detecta el entorno |

---

## 🎯 Prioridad de Detección

El sistema detecta el entorno en este orden:

1. **Variable de entorno:** `APP_ENVIRONMENT`
2. **Propiedad del sistema:** `-DAPP_ENVIRONMENT=production`
3. **Archivo:** `.production`
4. **Por defecto:** `development`

---

## ✅ Garantía

✔️ **En desarrollo:** Siempre usa `doralplazapruebas`  
✔️ **En producción:** Siempre usa `doralplaza` (si existe `.production` o variable de entorno)  
✔️ **No hay posibilidad de error:** El sistema es automático  

---

## 📞 Soporte

Si necesitas cambiar las credenciales, edita:
- `config-dev.properties` para desarrollo
- `config-prod.properties` para producción

**¡Nunca modifiques `ConfigDB.java` para cambiar credenciales!**
