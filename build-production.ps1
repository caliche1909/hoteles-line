# Script para crear BUILD de PRODUCCIÓN
# Este script compila el proyecto y lo configura para usar la BD de producción

Write-Host "🔨 Iniciando BUILD de PRODUCCIÓN..." -ForegroundColor Yellow
Write-Host ""

# 1. Cambiar configuración a producción
Write-Host "1️⃣  Configurando para PRODUCCIÓN..." -ForegroundColor Cyan
$configContent = Get-Content "config.properties"
$configContent = $configContent -replace "environment=development", "environment=production"
Set-Content "config.properties.temp" $configContent

# 2. Copiar config temporal al build
Write-Host "2️⃣  Copiando configuración..." -ForegroundColor Cyan
Copy-Item "config.properties.temp" -Destination "build/classes/config.properties" -Force

# 3. Recopilar librerías
Write-Host "3️⃣  Recopilando librerías..." -ForegroundColor Cyan
$libs = (Get-ChildItem -Path "src/lib" -Include "*.jar" -Recurse | ForEach-Object { $_.FullName }) -join ';'

# 4. Compilar
Write-Host "4️⃣  Compilando proyecto..." -ForegroundColor Cyan
javac -d build/classes -cp $libs -encoding UTF-8 -Xlint:deprecation `
    src/proyectodoral02/*.java src/vistas/*.java src/modelo/*.java src/conectar/*.java src/util/*.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilación exitosa" -ForegroundColor Green
} else {
    Write-Host "❌ Error en compilación" -ForegroundColor Red
    Remove-Item "config.properties.temp" -ErrorAction SilentlyContinue
    exit 1
}

# 5. Crear JAR ejecutable (opcional)
Write-Host "5️⃣  Creando JAR..." -ForegroundColor Cyan
if (!(Test-Path "dist")) { New-Item -ItemType Directory -Path "dist" | Out-Null }

# Copiar librerías al dist
if (!(Test-Path "dist/lib")) { New-Item -ItemType Directory -Path "dist/lib" | Out-Null }
Copy-Item -Path "src/lib/*" -Destination "dist/lib/" -Recurse -Force

# Crear manifest
$manifest = @"
Manifest-Version: 1.0
Main-Class: proyectodoral02.ProyectoDoral02
Class-Path: $(Get-ChildItem -Path "src/lib" -Include "*.jar" -Recurse | ForEach-Object { "lib/" + $_.Name } | Join-String -Separator " ")
"@
Set-Content "build/classes/MANIFEST.MF" $manifest

# Crear JAR
jar cfm dist/ProyectoDoral02.jar build/classes/MANIFEST.MF -C build/classes .

# Limpiar temporal
Remove-Item "config.properties.temp" -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "✅ BUILD COMPLETADO" -ForegroundColor Green
Write-Host "📦 JAR creado en: dist/ProyectoDoral02.jar" -ForegroundColor Cyan
Write-Host "🔴 Configurado para: PRODUCCIÓN (doralplaza)" -ForegroundColor Red
Write-Host ""
Write-Host "Para ejecutar: java -jar dist/ProyectoDoral02.jar" -ForegroundColor Yellow
