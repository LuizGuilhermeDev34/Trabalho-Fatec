@echo off
chcp 65001 > nul
title CastraPet

echo ================================================
echo           CASTRAPET - Iniciando Sistema
echo ================================================
echo.

set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot
set PATH=%JAVA_HOME%\bin;%USERPROFILE%\apache-maven\bin;%PATH%

echo [1/2] Compilando...
cd /d "%~dp0backend"
call "%USERPROFILE%\apache-maven\bin\mvn.cmd" package -DskipTests -q 2>nul
if errorlevel 1 (
    echo [ERRO] Falha na compilacao!
    pause
    exit /b 1
)
echo Compilado com sucesso!
echo.

echo [2/2] Iniciando servidor...
start "CastraPet Backend" cmd /k "chcp 65001 > nul && title CastraPet Backend && "%JAVA_HOME%\bin\java.exe" -jar "%~dp0backend\target\castrapet.jar""

timeout /t 3 /nobreak > nul
start http://localhost:8080

echo.
echo ================================================
echo   Acesse:  http://localhost:8080
echo   Admin:   admin@castrapet.com / admin123
echo ================================================
pause
