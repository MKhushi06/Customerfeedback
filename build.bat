@echo off
if not exist bin mkdir bin
echo ==========================================
echo Compiling Java Swing Feedback System...
echo ==========================================
"C:\Program Files\jdk-26.0.1\bin\javac.exe" -d bin -cp "lib/*" --source-path src src/com/feedback/app/Main.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %errorlevel%
)

echo.
echo [INFO] Copying resources to build folder...
if not exist bin\resources mkdir bin\resources
copy src\resources\db.properties bin\resources\db.properties /Y > nul

echo.
echo ==========================================
echo Running Customer Feedback Management System...
echo ==========================================
"C:\Program Files\jdk-26.0.1\bin\java.exe" -cp "bin;lib/*" com.feedback.app.Main
