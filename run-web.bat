@echo off
title SwiftBus Web Preview Server
echo Starting SwiftBus Web Server on port 8085...
cd /d "%~dp0"

set "JAVA_EXE=java"
if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\jbr\bin\java.exe" (
    set "JAVA_EXE=C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\jbr\bin\java.exe"
) else if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
)

"%JAVA_EXE%" -cp "target\classes;%USERPROFILE%\.m2\repository\org\postgresql\postgresql\42.7.2\postgresql-42.7.2.jar;%USERPROFILE%\.m2\repository\org\apache\pdfbox\pdfbox\3.0.1\pdfbox-3.0.1.jar;%USERPROFILE%\.m2\repository\org\apache\pdfbox\pdfbox-io\3.0.1\pdfbox-io-3.0.1.jar" com.busreservation.web.WebPreviewServer
pause
