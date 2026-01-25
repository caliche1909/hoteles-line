@echo off
REM Script para ejecutar ProyectoDoral02

echo Compilando el proyecto...
for /r src\lib %%i in (*.jar) do set CLASSPATH=!CLASSPATH!;%%i

javac -d build/classes -cp "%CLASSPATH%" -encoding UTF-8 src/proyectodoral02/*.java src/vistas/*.java src/modelo/*.java src/conectar/*.java src/util/*.java 2>nul

echo Ejecutando el proyecto...
java -cp "build/classes;%CLASSPATH%" proyectodoral02.ProyectoDoral02

pause
