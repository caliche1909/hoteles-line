# Script PowerShell para ejecutar el proyecto

Write-Host "🔨 Recopilando librerías..." -ForegroundColor Cyan
$libs = (Get-ChildItem -Path "src/lib" -Include "*.jar" -Recurse | ForEach-Object { $_.FullName }) -join ';'

Write-Host "▶️  Ejecutando ProyectoDoral02..." -ForegroundColor Green
java -cp "build/classes;$libs" proyectodoral02.ProyectoDoral02

Read-Host -Prompt "Presiona Enter para salir"
