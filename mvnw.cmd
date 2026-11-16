@REM Maven Wrapper Script for Windows
@echo off
setlocal

set "DIR=%~dp0"
if defined JAVA_HOME goto findMaven
set "JAVA_HOME=C:\Users\Admin\.jdks\jbr-21.0.8"

:findMaven
if exist "C:\Users\Admin\maven\apache-maven-3.9.9\bin\mvn.cmd" (
    set "MAVEN_CMD=C:\Users\Admin\maven\apache-maven-3.9.9\bin\mvn.cmd"
) else (
    set "MAVEN_CMD=mvn"
)

"%MAVEN_CMD%" %*
endlocal
