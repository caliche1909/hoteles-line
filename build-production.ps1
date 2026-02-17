# ========================================
# Script para crear BUILD de PRODUCCIÓN
# ========================================
# Este script compila el proyecto y lo configura para usar la BD de producción (doralplaza)

Write-Host "🔨 Iniciando BUILD de PRODUCCIÓN..." -ForegroundColor Yellow
Write-Host ""

# 1. Crear archivo de marcador de producción
Write-Host "1️⃣  Configurando para PRODUCCIÓN..." -ForegroundColor Cyan
New-Item -ItemType File -Path ".production" -Force | Out-Null
Write-Host "   ✓ Archivo .production creado" -ForegroundColor Green

# 2. Copiar configuración de producción al build
Write-Host "2️⃣  Copiando configuración de PRODUCCIÓN..." -ForegroundColor Cyan
if (!(Test-Path "build/classes")) { New-Item -ItemType Directory -Path "build/classes" -Force | Out-Null }
Copy-Item "config-prod.properties" -Destination "build/classes/config-prod.properties" -Force
Write-Host "   ✓ config-prod.properties copiado" -ForegroundColor Green

# 3. Recopilar librerías
Write-Host "3️⃣  Recopilando librerías..." -ForegroundColor Cyan
$libs = (Get-ChildItem -Path "src/lib" -Include "*.jar" -Recurse | ForEach-Object { $_.FullName }) -join ';'
Write-Host "   ✓ $((Get-ChildItem -Path 'src/lib' -Include '*.jar' -Recurse).Count) librerías encontradas" -ForegroundColor Green

# 4. Compilar
Write-Host "4️⃣  Compilando proyecto..." -ForegroundColor Cyan
$javaFiles = Get-ChildItem -Path src -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
javac -d build/classes -cp $libs -encoding UTF-8 $javaFiles 2>&1 | Out-Null

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✅ Compilación exitosa" -ForegroundColor Green
} else {
    Write-Host "   ❌ Error en compilación" -ForegroundColor Red
    Remove-Item ".production" -ErrorAction SilentlyContinue
    exit 1
}

# 5. Crear JAR ejecutable
Write-Host "5️⃣  Creando JAR de PRODUCCIÓN..." -ForegroundColor Cyan
if (!(Test-Path "dist")) { New-Item -ItemType Directory -Path "dist" | Out-Null }

# Copiar archivo de marcador al dist
Copy-Item ".production" -Destination "dist/.production" -Force

# Copiar configuración de producción al dist
Copy-Item "config-prod.properties" -Destination "dist/config-prod.properties" -Force

# Copiar librerías al dist
if (!(Test-Path "dist/lib")) { New-Item -ItemType Directory -Path "dist/lib" | Out-Null }
Copy-Item -Path "src/lib/*" -Destination "dist/lib/" -Recurse -Force
Write-Host "   ✓ Librerías copiadas a dist/lib" -ForegroundColor Green

# Crear manifest
$libFiles = Get-ChildItem -Path "src/lib" -Include "*.jar" -Recurse | ForEach-Object { "lib/" + $_.Name }
$classpath = $libFiles -join " "
$manifest = @"
Manifest-Version: 1.0
Main-Class: proyectodoral02.ProyectoDoral02
Class-Path: $classpath
"@
Set-Content "build/classes/MANIFEST.MF" $manifest

# Crear JAR
jar cfm dist/ProyectoDoral02.jar build/classes/MANIFEST.MF -C build/classes .
Write-Host "   ✓ JAR creado exitosamente" -ForegroundColor Green

# Limpiar
Remove-Item ".production" -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "✅ ============================================" -ForegroundColor Green
Write-Host "✅  BUILD DE PRODUCCIÓN COMPLETADO" -ForegroundColor Green
Write-Host "✅ ============================================" -ForegroundColor Green
Write-Host ""
Write-Host "📦 JAR: dist/ProyectoDoral02.jar" -ForegroundColor Cyan
Write-Host "🔴 Base de Datos: doralplaza (PRODUCCIÓN)" -ForegroundColor Red
Write-Host "📋 Configuración: dist/config-prod.properties" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para ejecutar en producción:" -ForegroundColor Yellow
Write-Host "  1. Copiar todo el contenido de 'dist/' al servidor" -ForegroundColor White
Write-Host "  2. En el servidor, crear archivo .production:" -ForegroundColor White
Write-Host "     New-Item -ItemType File -Path '.production'" -ForegroundColor Gray
Write-Host "  3. Ejecutar:" -ForegroundColor White
Write-Host "     java -jar ProyectoDoral02.jar" -ForegroundColor Gray
Write-Host ""
