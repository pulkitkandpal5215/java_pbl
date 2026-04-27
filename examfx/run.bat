@echo off
:: ============================================================
::  Online Examination System — JavaFX + MongoDB
::  Build & Run Script (Windows)
:: ============================================================

echo ============================================================
echo  ExamPortal — JavaFX Online Examination System
echo ============================================================
echo.

:: ── Step 1: Check Prerequisites ──────────────────────────────────────────────
echo Checking prerequisites...

:: Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found. Please install Java 17+ from https://adoptium.net/
    pause
    exit /b 1
)

:: Check Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven not found. Please install Maven 3.6+ from https://maven.apache.org/
    pause
    exit /b 1
)

:: Check MongoDB (basic connectivity test)
echo| set /p="Testing MongoDB connection..."
java -cp ".;out" -Djava.library.path="" com.mongodb.client.MongoClients "mongodb://localhost:27017" >nul 2>&1
if errorlevel 1 (
    echo FAILED
    echo ERROR: MongoDB not running. Please start MongoDB on localhost:27017
    echo Install from: https://docs.mongodb.com/manual/installation/
    pause
    exit /b 1
)
echo OK
echo.

:: ── Step 2: Build & Run with Maven ───────────────────────────────────────────
echo Building and running with Maven...
echo.

mvn clean compile javafx:run

if errorlevel 1 (
    echo.
    echo Build/Run FAILED. Check errors above.
    pause
    exit /b 1
)

pause
