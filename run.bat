@echo off
echo ============================================================
echo  Online Examination System - Java Swing GUI
echo ============================================================
echo.

mkdir out 2>nul

echo Compiling Java files...
javac -d out *.java

if errorlevel 1 (
    echo.
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.

echo Demo Accounts:
echo   Admin:  admin@exam.com   / admin123
echo   Student: pulkit@gmail.com   / pulkit123
echo.

java -cp out com.examgui.Main
pause
