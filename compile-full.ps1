# ==================================================
# Script de Compilación COMPLETA - ProyectoDoral02
# ==================================================
# Este script limpia y recompila TODO el proyecto

Write-Host "🧹 LIMPIANDO archivos compilados antiguos..." -ForegroundColor Yellow
Write-Host ""

# 1. ELIMINAR archivos .class antiguos
Write-Host "1️⃣  Eliminando archivos .class de vistas/ y modelo/..." -ForegroundColor Cyan
Remove-Item "build\classes\vistas\*.class" -ErrorAction SilentlyContinue
Remove-Item "build\classes\modelo\*.class" -ErrorAction SilentlyContinue
Write-Host "   ✓ Archivos antiguos eliminados" -ForegroundColor Green
Write-Host ""

# 2. RECOPILAR todas las librerías JAR
Write-Host "2️⃣  Recopilando librerías..." -ForegroundColor Cyan
$libs = (Get-ChildItem -Path "src\lib" -Include "*.jar" -Recurse | ForEach-Object { $_.FullName }) -join ';'
$libraryCount = (Get-ChildItem -Path "src\lib" -Include "*.jar" -Recurse).Count
Write-Host "   ✓ $libraryCount librerías encontradas" -ForegroundColor Green
Write-Host ""

# 3. COMPILAR archivos específicos primero (los que modificamos)
Write-Host "3️⃣  Compilando archivos modificados..." -ForegroundColor Cyan
$filesToCompile = @(
    "src\modelo\ReporteSire.java",
    "src\vistas\Registros.java"
)

javac -d build\classes -cp "$libs;build\classes" -encoding UTF-8 $filesToCompile 2>&1 | Out-Null

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ ReporteSire.java compilado" -ForegroundColor Green
    Write-Host "   ✓ Registros.java compilado" -ForegroundColor Green
} else {
    Write-Host "   ❌ Error en compilación" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 4. VERIFICAR timestamps
Write-Host "4️⃣  Verificando archivos compilados..." -ForegroundColor Cyan
$registrosJava = Get-Item "src\vistas\Registros.java"
$registrosClass = Get-Item "build\classes\vistas\Registros.class"
$reporteJava = Get-Item "src\modelo\ReporteSire.java"
$reporteClass = Get-Item "build\classes\modelo\ReporteSire.class"

Write-Host "   📄 Registros.java:   $($registrosJava.LastWriteTime)" -ForegroundColor White
Write-Host "   📦 Registros.class:  $($registrosClass.LastWriteTime)" -ForegroundColor White
Write-Host ""
Write-Host "   📄 ReporteSire.java: $($reporteJava.LastWriteTime)" -ForegroundColor White
Write-Host "   📦 ReporteSire.class: $($reporteClass.LastWriteTime)" -ForegroundColor White
Write-Host ""

# 5. VALIDAR que los .class son más recientes
if ($registrosClass.LastWriteTime -ge $registrosJava.LastWriteTime -and 
    $reporteClass.LastWriteTime -ge $reporteJava.LastWriteTime) {
    Write-Host "✅ COMPILACIÓN EXITOSA" -ForegroundColor Green
    Write-Host "   Los archivos .class están actualizados" -ForegroundColor Green
    Write-Host ""
    Write-Host "🚀 Ahora puedes ejecutar el programa con los cambios aplicados" -ForegroundColor Cyan
} else {
    Write-Host "⚠️  ADVERTENCIA: Los timestamps no son correctos" -ForegroundColor Yellow
    Write-Host "   Puede que la compilación no se haya completado correctamente" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor White
