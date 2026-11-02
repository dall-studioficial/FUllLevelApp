@echo off
echo ========================================
echo   BUILD DE RELEASE - Clinometro Pro
echo ========================================
echo.

echo [1/4] Limpiando builds anteriores...
call gradlew clean
if %errorlevel% neq 0 (
    echo ERROR: Fallo al limpiar
    pause
    exit /b 1
)

echo.
echo [2/4] Generando APK de Release...
call gradlew assembleRelease
if %errorlevel% neq 0 (
    echo ERROR: Fallo al compilar Release
    pause
    exit /b 1
)

echo.
echo [3/4] Verificando archivos generados...
if exist "app\build\outputs\apk\release\app-release-unsigned.apk" (
    echo ✅ APK Release generado exitosamente
    
    echo.
    echo [4/4] Informacion del APK:
    dir "app\build\outputs\apk\release\app-release-unsigned.apk" | findstr "app-release"
    
    echo.
    echo ========================================
    echo   VERIFICACION IMPORTANTE
    echo ========================================
    echo.
    echo 1. Mapping file guardado en:
    echo    app\build\outputs\mapping\release\mapping.txt
    echo.
    echo 2. APK ubicado en:
    echo    app\build\outputs\apk\release\app-release-unsigned.apk
    echo.
    echo 3. Para instalar en dispositivo:
    echo    adb install app\build\outputs\apk\release\app-release-unsigned.apk
    echo.
    echo ✅ BUILD EXITOSO - Verifica que ProGuard funciono revisando el tamano
    echo    (Deberia ser aproximadamente 50%% mas pequeno que debug)
    echo.
) else (
    echo ❌ ERROR: No se encontro el APK generado
)

echo.
pause
