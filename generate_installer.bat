@echo off
setlocal enabledelayedexpansion
title Building and Packaging VoiceControl

:: === CONFIGURATION ===
set PROJECT_DIR=C:\_Programming_Stuff\_desktopApps\voice-to-text-app
set JAVA_JMODS="C:\Program Files\Java\jdk-21\jmods"
set JAVAFX_LIB="C:\Program Files\Java\javafx-sdk-21.0.6\lib"
set OUTPUT_RUNTIME=target/runtime
set MAIN_JAR=VoiceControl-1.0.0.jar
set MAIN_CLASS=com.voice_to_text.Main
set APP_NAME=VoiceControl
set ICON=icon.ico
set INSTALL_DIR=C:\VoiceControl
set RESOURCE_DIR=src/main/resources
set INSTALLER_OUTPUT=target/installer
set DLL_SOURCE=%PROJECT_DIR%\VC_DLLS

:: === STEP 1: Move to project directory ===
echo.
echo === Navigating to Project Directory ===
cd /d %PROJECT_DIR%

:: === STEP 2: Clean and build ===
echo.
echo === Running Maven Clean Package ===
call mvn clean package

if %errorlevel% neq 0 (
    echo [ERROR] Maven build failed.
    pause
    exit /b %errorlevel%
)

:: === STEP 3: JLink Custom Runtime ===
echo.
echo === Creating Custom Runtime with JLink ===
rmdir /s /q %OUTPUT_RUNTIME%
jlink ^
    --module-path %JAVA_JMODS%;%JAVAFX_LIB% ^
    --add-modules java.base,java.desktop,javafx.controls,javafx.fxml,java.logging,java.scripting,javafx.graphics,javafx.base ^
    --output %OUTPUT_RUNTIME% ^
    --strip-debug ^
    --compress=2 ^
    --no-header-files ^
    --no-man-pages

if %errorlevel% neq 0 (
    echo [ERROR] JLink failed.
    pause
    exit /b %errorlevel%
)

:: === STEP 4: Copy DLLs & vosk-model ===
echo.
echo === Copying Resources to Runtime ===
xcopy /s /y "%DLL_SOURCE%\*.dll" "%OUTPUT_RUNTIME%\bin" >nul

:: === STEP 5: Package with JPackage ===
echo.
echo === Creating Installer with JPackage ===
jpackage ^
    --type exe ^
    --name %APP_NAME% ^
    --input target ^
    --main-jar %MAIN_JAR% ^
    --main-class %MAIN_CLASS% ^
    --runtime-image %OUTPUT_RUNTIME% ^
    --icon %ICON% ^
    --install-dir "%INSTALL_DIR%" ^
    --win-dir-chooser ^
    --app-version 1.0.0 ^
    --win-shortcut ^
    --win-menu ^
    --win-menu-group "Voice Control" ^
    --resource-dir %RESOURCE_DIR% ^
    --dest %INSTALLER_OUTPUT% ^
    --win-console ^
    --java-options "-Dprism.order=sw" ^
    --java-options "-Dprism.verbose=true"

if %errorlevel% neq 0 (
    echo [ERROR] JPackage failed.
    pause
    exit /b %errorlevel%
)

echo.
echo ✅ All done! The installer is in %INSTALLER_OUTPUT%
pause
