@echo off
echo ============================================================
echo  Online Examination System - Java Swing GUI
echo ============================================================
echo.

mkdir out 2>nul

echo Compiling Java files...
for /r src %%f in (*.java) do set SOURCES=!SOURCES! "%%f"
javac -d out -sourcepath src src\com\examgui\Main.java

if errorlevel 1 (
    echo.
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Demo Accounts:
echo   Admin:   admin@exam.com   / admin123
echo   Student: alice@exam.com   / alice123
echo.

java -cp out com.examgui.Main
pause
