# 📐 Clinómetro Pro - Nivel Digital Android

Una aplicación moderna de clinómetro/nivel digital para Android construida con las mejores prácticas
y tecnologías actuales. **Optimizado para medir rotación del teléfono**.

## ✨ Características

- **Interfaz Circular con Aguja**: Diseño visual con aguja que apunta al ángulo actual en tiempo
  real
- **Rotación del Teléfono**: Configurado para medir cuando rotas el teléfono (como ver videos
  horizontales)
- **Medición Precisa**: Utiliza sensores de acelerómetro y magnetómetro para mediciones precisas
- **Indicadores Visuales**:
  - **Aguja dinámica** que apunta al ángulo exacto en tiempo real
    - Ángulo principal en el centro con valor numérico grande
    - Estado "NIVELADO" o "INCLINADO" con colores intuitivos
    - Marcas de grados cada 15° con números principales cada 90°
- **Información Completa**:
  - Ángulo de rotación del teléfono (Roll)
    - Orientación magnética (Azimut)
    - Estado de calibración de sensores
- **Animaciones Suaves**: Aguja y valores con transiciones fluidas

## 🔧 Uso Como Nivel de Rotación

### Posicionamiento Correcto

1. **Sostener el teléfono normalmente** (vertical, pantalla hacia ti) = **0°**
2. **Rotar el teléfono hacia la derecha** (como ver video horizontal) = **90°**
3. **La aguja apuntará** al ángulo exacto de rotación
4. **0°** significa posición vertical normal
5. **Verde** indica nivelado (±2°), **Rojo** indica rotado

### Casos de Uso

- ✅ Verificar si fotos/videos están nivelados
- ✅ Calibrar orientación de pantallas
- ✅ Nivelar marcos y cuadros en la pared
- ✅ Medición de ángulos de rotación
- ✅ Verificar horizontalidad de objetos

## 🏗️ Arquitectura y Tecnologías

### Patrón MVVM (Model-View-ViewModel)

- **Model**: `ClinometerData` - Modelo de datos limpio
- **View**: Composables de Jetpack Compose
- **ViewModel**: `ClinometerViewModel` - Lógica de presentación separada

### Stack Tecnológico

- **Kotlin**: Lenguaje de programación moderno
- **Jetpack Compose**: UI moderna y declarativa
- **Android Sensors**: Acelerómetro y magnetómetro
- **StateFlow**: Manejo reactivo de estado
- **Coroutines**: Programación asíncrona
- **Material Design 3**: Diseño moderno y consistente

### Separación de Responsabilidades

```
📱 MainActivity (Punto de entrada)
    ↓
🖼️ ClinometerScreen (Vista principal)
    ↓
🧠 ClinometerViewModel (Lógica de presentación)
    ↓
📂 SensorRepository (Acceso a sensores)
    ↓
📊 ClinometerData (Modelo de datos)
```

## 🎨 Componentes UI

### ClinometerCircle

- Componente principal circular
- Canvas personalizado con dibujo de marcas y escalas
- Burbuja indicadora animada
- Líneas de referencia cruz

### ClinometerScreen

- Pantalla principal con diseño responsivo
- Barra superior con estado de calibración
- Panel de información adicional
- Manejo de errores integrado

## 🔧 Configuración

### Dependencias Principales

```kotlin
// ViewModel y LiveData para MVVM
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
implementation("androidx.compose.runtime:runtime-livedata:1.7.4")

// Animaciones suaves en Compose
implementation("androidx.compose.animation:animation:1.7.4")
```

### Permisos

No requiere permisos especiales - usa sensores estándar del dispositivo.

## 🚀 Instalación y Uso

1. **Clonar el repositorio**
2. **Abrir en Android Studio**
3. **Sincronizar Gradle**
4. **Ejecutar en dispositivo físico** (recomendado para sensores reales)

### Uso de la Aplicación

1. Abrir la aplicación
2. Esperar a que se calibren los sensores
3. Rotar el dispositivo para ver mediciones en tiempo real
4. La aguja apuntará al ángulo exacto de rotación

## 🎯 Características Técnicas

- **Precisión**: Medición con tolerancia de ±2°
- **Respuesta**: Actualización en tiempo real con suavizado
- **Compatibilidad**: Android API 24+ (Android 7.0)
- **Rendimiento**: Optimizado con animaciones de 60fps

## 🔄 Estados de la Aplicación

- **Calibrando**: Sensor inicializándose
- **Calibrado**: Listo para mediciones precisas
- **Error**: Sensores no disponibles con opción de reintentar

## 🎨 Diseño Visual

- **Colores**:
    - Verde: Dispositivo nivelado
    - Naranja: Dispositivo inclinado
    - Azul: Información de Pitch
    - Rojo: Información de Roll
- **Fondo**: Gradiente oscuro moderno
- **Tipografía**: Material Design con jerarquía clara

## 📱 Capturas de Pantalla

La aplicación presenta:

- Círculo clinómetro principal con aguja indicadora
- Valor numérico grande del ángulo principal
- Panel inferior con mediciones detalladas
- Indicador de estado de calibración

---

*Desarrollado siguiendo las mejores prácticas de Android con arquitectura MVVM y tecnologías
modernas.*