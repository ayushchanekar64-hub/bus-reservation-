@echo off
title SwiftBus - Bus Reservation System
echo Starting Bus Reservation System (JavaFX)...
cd /d "%~dp0"
call mvnw.cmd javafx:run
pause
