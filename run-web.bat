@echo off
title SwiftBus Web Preview Server
echo Starting SwiftBus Web Server on port 8085...
cd /d "%~dp0"
"C:\Users\Admin\.jdks\jbr-21.0.8\bin\java.exe" -cp "target\classes;C:\Users\Admin\.m2\repository\org\postgresql\postgresql\42.7.2\postgresql-42.7.2.jar;C:\Users\Admin\.m2\repository\org\apache\pdfbox\pdfbox\3.0.1\pdfbox-3.0.1.jar;C:\Users\Admin\.m2\repository\org\apache\pdfbox\pdfbox-io\3.0.1\pdfbox-io-3.0.1.jar" com.busreservation.web.WebPreviewServer
pause
