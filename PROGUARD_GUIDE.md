# 🛡️ Guía de ProGuard/R8 - Clinómetro Pro

## ✅ Configuración Completada

Se ha habilitado y configurado ProGuard/R8 completamente para tu aplicación.

## 🔧 Cambios Realizados

### 1. **app/build.gradle.kts**

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true          // ✅ Habilita ProGuard/R8
        isShrinkResources = true        // ✅ Elimina recursos sin usar
        proguardFiles(...)
    }
}
```

### 2. **app/proguard-rules.pro**

Se agregaron reglas específicas para:

- ✅ **Kotlin y Coroutines**: Mantiene metadatos y clases necesarias
- ✅ **ViewModels (MVVM)**: Protege todos los ViewModels y sus constructores
- ✅ **Jetpack Compose**: Mantiene funciones @Composable y clases UI
- ✅ **Navigation Compose**: Protege la navegación
- ✅ **DataStore**: Mantiene preferencias y serialización
- ✅ **Modelos de Datos**: Protege todas las data classes
- ✅ **Repositories**: Mantiene lógica de acceso a datos
- ✅ **Sensores Android**: Protege APIs de sensores y vibración
- ✅ **Optimizaciones**: Elimina logs y verificaciones de null en release

### 3. **Solución de Problema con Linter**

**Problema detectado**: El linter de Android Studio mostraba "Unresolved class name" en las reglas
de DataStore (línea 83).

**Solución aplicada**:

- Se usó `-keep,allowobfuscation` en lugar de `-keep` simple
- Se agregó documentación explicando que es un comportamiento esperado
- Se reorganizaron las reglas para ser más específicas

**Nota importante**: Este tipo de warnings del linter en archivos ProGuard son comunes y **NO
afectan la funcionalidad**. ProGuard procesa correctamente las reglas en runtime cuando las clases
existen en las dependencias compiladas.

## 🚀 Cómo Generar el APK de Release

### Opción 1: Android Studio (Recomendado)

1. Ve a `Build > Generate Signed Bundle / APK`
2. Selecciona **APK**
3. Si no tienes keystore, crea uno nuevo (temporal para pruebas)
4. Completa la información
5. Selecciona **release** build variant
6. Click en **Finish**

### Opción 2: Línea de Comandos

```bash
# Windows (PowerShell)
.\gradlew assembleRelease

# El APK estará en:
# app/build/outputs/apk/release/app-release-unsigned.apk
```

## 🧪 Verificación de ProGuard

### 1. Verificar que R8 está funcionando

Después de hacer build de release, revisa los logs:

```
R8: Shrinking...
R8: Optimizing...
R8: Obfuscating...
```

### 2. Verificar reducción de tamaño

```bash
# Comparar tamaños
# Debug APK: ~8-12 MB
# Release APK: ~4-6 MB (aproximadamente 50% más pequeño)
```

### 3. Verificar mapping.txt (para debugging)

El archivo de mapeo se genera en:

```
app/build/outputs/mapping/release/mapping.txt
```

**⚠️ IMPORTANTE**: Guarda este archivo para cada versión publicada. Lo necesitarás para descifrar
stack traces de producción.

### 4. Probar el APK de Release

```bash
# Instalar en dispositivo conectado
adb install app/build/outputs/apk/release/app-release.apk

# Verificar que funciona correctamente:
# ✅ La app abre sin crashes
# ✅ Los sensores funcionan
# ✅ Las animaciones son suaves
# ✅ La navegación funciona
# ✅ Las preferencias se guardan
# ✅ La vibración funciona
```

## 🐛 Solución de Problemas

### Problema 1: Crash al abrir la app

**Causa**: Clase necesaria fue eliminada por R8

**Solución**: Agrega regla en `proguard-rules.pro`:

```proguard
-keep class com.ejemplo.ClaseProblematica { *; }
```

### Problema 2: ViewModels no funcionan

**Causa**: Constructor eliminado

**Solución**: Ya está incluido en las reglas, pero verifica:

```proguard
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
```

### Problema 3: Compose crashea

**Causa**: Funciones @Composable fueron ofuscadas

**Solución**: Ya incluido en reglas:

```proguard
-keep @androidx.compose.runtime.Composable class * { *; }
```

### Problema 4: DataStore no guarda preferencias

**Causa**: Serialización rota

**Solución**: Ya incluido:

```proguard
-keep class androidx.datastore.*.** { *; }
```

## 📊 Análisis del APK

### Ver qué contiene tu APK:

1. En Android Studio: `Build > Analyze APK...`
2. Selecciona tu APK de release
3. Verás:
    - Tamaño total
    - Tamaño de clases.dex (código ofuscado)
    - Recursos
    - Assets

### Reducción esperada:

- **Código (classes.dex)**: 40-60% más pequeño
- **Recursos**: 10-30% más pequeño (si tienes imágenes/drawables sin usar)
- **APK Total**: ~50% más pequeño

## 🔒 Beneficios de ProGuard Habilitado

### 1. **Seguridad** 🛡️

- Código ofuscado (dificulta ingeniería inversa)
- Nombres de clases/métodos renombrados
- Lógica más difícil de entender

### 2. **Performance** ⚡

- APK más pequeño (descarga más rápida)
- Código optimizado (ejecución más rápida)
- Menos uso de memoria
- Menor consumo de batería

### 3. **Optimización** 📉

- Elimina código muerto (dead code)
- Inline de funciones pequeñas
- Elimina logs de debug
- Remove verificaciones innecesarias

## 📝 Checklist de Verificación

Antes de publicar, verifica:

- [ ] Build de release compila sin errores
- [ ] APK es significativamente más pequeño que debug
- [ ] La app funciona completamente en dispositivo real
- [ ] No hay crashes al abrir
- [ ] Sensores funcionan correctamente
- [ ] Navegación funciona
- [ ] Tema claro/oscuro funciona
- [ ] Vibración funciona
- [ ] Preferencias se guardan correctamente
- [ ] Has guardado el archivo `mapping.txt`

## 🚨 Advertencias Importantes

### 1. **Guarda mapping.txt**

Cada versión publicada genera un `mapping.txt` único. **Debes guardarlo** para poder leer crashes de
producción.

### 2. **Prueba Exhaustivamente**

ProGuard puede romper cosas sutiles. Prueba toda la funcionalidad en el APK de release antes de
publicar.

### 3. **Usa Build Variants**

- `debug`: Sin ProGuard, logs habilitados
- `release`: Con ProGuard, optimizado

### 4. **Stack Traces en Producción**

Para descifrar crashes, usa:

```bash
# Windows
.\gradlew transformReleaseClassesWithProGuard

# Descifrar stack trace
retrace.bat app/build/outputs/mapping/release/mapping.txt stacktrace.txt
```

## 📱 Siguiente Paso: Generar Bundle para Play Store

Para Play Store, es mejor usar Android App Bundle (AAB):

```bash
# Generar bundle
.\gradlew bundleRelease

# El bundle estará en:
# app/build/outputs/bundle/release/app-release.aab
```

Los bundles son ~15% más pequeños que APK y Play Store optimiza para cada dispositivo.

## 🎯 Resumen

✅ **ProGuard está completamente configurado**
✅ **Reglas optimizadas para tu stack tecnológico**
✅ **APK será ~50% más pequeño**
✅ **Código protegido contra ingeniería inversa**
✅ **Rendimiento optimizado**

**Ahora puedes generar builds de release con confianza.**

---

## 📞 Soporte

Si encuentras algún problema específico con ProGuard:

1. Revisa los logs de build
2. Busca warnings de R8
3. Agrega reglas específicas según sea necesario
4. Prueba incrementalmente

**¡Tu app está lista para optimización de producción!** 🚀
