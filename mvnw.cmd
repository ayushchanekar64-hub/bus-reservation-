@REM Maven Wrapper Script for Windows
@echo off
setlocal

set "DIR=%~dp0"

REM 1. Detect JAVA_HOME
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" goto findMaven

if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\jbr\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\jbr"
    goto findMaven
)

if exist "%USERPROFILE%\.jdks\jbr-21.0.8\bin\java.exe" (
    set "JAVA_HOME=%USERPROFILE%\.jdks\jbr-21.0.8"
    goto findMaven
)

:findMaven
REM 2. Detect Maven command
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set "MAVEN_CMD=mvn"
    goto runMaven
)

if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\plugins\maven\lib\maven3\bin\mvn.cmd" (
    set "MAVEN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\plugins\maven\lib\maven3\bin\mvn.cmd"
    goto runMaven
)

if exist "%USERPROFILE%\maven\apache-maven-3.9.9\bin\mvn.cmd" (
    set "MAVEN_CMD=%USERPROFILE%\maven\apache-maven-3.9.9\bin\mvn.cmd"
    goto runMaven
)

set "MAVEN_CMD=mvn"

:runMaven
"%MAVEN_CMD%" %*
endlocal
