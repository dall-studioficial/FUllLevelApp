package dall.full.level.app.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dall.full.level.app.R
import dall.full.level.app.repository.findActivity
import dall.full.level.app.viewmodel.ClinometerViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CameraLevelScreen(
    onBack: () -> Unit,
    viewModel: ClinometerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val showPitch by viewModel.showPitch.collectAsState()
    val isHeld by viewModel.isHeld.collectAsState()
    val isRotatedReference by viewModel.isRotatedReference.collectAsStateWithLifecycle()
    val clinometerData by viewModel.clinometerState.collectAsStateWithLifecycle()

    var isFlashOn by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(true) }
    var cameraInstance by remember { mutableStateOf<Camera?>(null) }
    var imageCaptureInstance by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val currentAngle = clinometerData.pitchAngle
    val rollAngle = clinometerData.rollAngle
    val isLeveledHoriz = abs(currentAngle) < 0.5f
    val isLeveledVert = abs(rollAngle) < 0.5f

    // Lógica exacta de estabilización y suavizado copiada de ClinometerCircle (modo aguja)
    fun shortestAngleDistance(from: Float, to: Float): Float {
        val diff = (to - from + 540f) % 360f - 180f
        return from + diff
    }

    val previousAzimuthState = remember { mutableStateOf(clinometerData.azimuthAngle) }
    val previousAzimuth = previousAzimuthState.value
    val targetAzimuth = shortestAngleDistance(previousAzimuth, clinometerData.azimuthAngle)
    val animatedAzimuth by animateFloatAsState(
        targetValue = targetAzimuth,
        animationSpec = tween(150),
        label = "azimuth"
    )
    LaunchedEffect(targetAzimuth) { previousAzimuthState.value = targetAzimuth }

    val normalizedAzimuth = ((animatedAzimuth % 360f) + 360f) % 360f

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.camera_level_title), fontWeight = FontWeight.SemiBold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    // Botón de Referencia (Ref: 0° / Ref: 90°)
                    FilledTonalButton(
                        onClick = { viewModel.toggleReferenceMode() },
                        enabled = !isHeld,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (isRotatedReference) stringResource(R.string.ref_90) else stringResource(R.string.ref_0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Linterna
                    IconButton(onClick = {
                        isFlashOn = !isFlashOn
                        cameraInstance?.cameraControl?.enableTorch(isFlashOn)
                    }) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                            contentDescription = stringResource(R.string.flashlight),
                            tint = if (isFlashOn) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    // Cuadrícula
                    IconButton(onClick = { showGrid = !showGrid }) {
                        Icon(
                            imageVector = if (showGrid) Icons.Filled.GridOn else Icons.Filled.GridOff,
                            contentDescription = stringResource(R.string.grid)
                        )
                    }
                    // Modo Grados / Pitch
                    IconButton(onClick = { viewModel.toggleShowPitch() }) {
                        Icon(
                            imageVector = if (showPitch) Icons.Outlined.Straighten else Icons.Filled.Roofing,
                            contentDescription = stringResource(R.string.change_mode)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.9f),
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            // Panel de control inferior M3 flotante y limpio
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 28.dp, vertical = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Congelar / Reanudar
                    FilledTonalButton(
                        onClick = { viewModel.toggleHold() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isHeld) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = if (isHeld) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = if (isHeld) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isHeld) stringResource(R.string.resume) else stringResource(R.string.freeze), fontWeight = FontWeight.Medium)
                    }

                    // Botón Capturar Foto (FAB M3)
                    LargeFloatingActionButton(
                        onClick = {
                            imageCaptureInstance?.let { capture ->
                                takePhotoWithOverlay(
                                    context = context,
                                    imageCapture = capture,
                                    angle = currentAngle,
                                    normalizedAzimuth = normalizedAzimuth,
                                    showPitch = showPitch,
                                    isLeveled = isLeveledHoriz,
                                    isHeld = isHeld,
                                    isRotatedReference = isRotatedReference,
                                    onSaved = { bitmap ->
                                        capturedBitmap = bitmap
                                        Toast.makeText(context, context.getString(R.string.capture_saved_toast), Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = stringResource(R.string.capture), modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (cameraPermissionState.status.isGranted) {
                CameraPreview(
                    onCameraInitialized = { camera, imageCapture ->
                        cameraInstance = camera
                        imageCaptureInstance = imageCapture
                    }
                )
                ProOverlay(
                    angle = currentAngle,
                    normalizedAzimuth = normalizedAzimuth,
                    showPitch = showPitch,
                    isHeld = isHeld,
                    showGrid = showGrid,
                    isLeveledHoriz = isLeveledHoriz,
                    isLeveledVert = isLeveledVert,
                    isRotatedReference = isRotatedReference
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            stringResource(R.string.camera_permission_required),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                            Text(stringResource(R.string.grant_permission))
                        }
                    }
                }
            }

            // Diálogo / Pantalla de Previsualización Inmediata de la Captura
            if (capturedBitmap != null) {
                CapturePreviewDialog(
                    bitmap = capturedBitmap!!,
                    onDismiss = {
                        val activity = context.findActivity()
                        viewModel.showInterstitialAd(activity) {
                            capturedBitmap = null
                        }
                    },
                    onShare = {
                        val activity = context.findActivity()
                        viewModel.showInterstitialAd(activity) {
                            shareBitmap(context, capturedBitmap!!)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CapturePreviewDialog(
    bitmap: Bitmap,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cabecera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                    Text(stringResource(R.string.capture_saved_dialog_title), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel), tint = Color.White)
                }
            }

            // Imagen Previsualizada
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = stringResource(R.string.capture_saved_dialog_title),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Botones de Acción (Compartir / Continuar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.share))
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(R.string.continue_measuring))
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    onCameraInitialized: (Camera, ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val executor = ContextCompat.getMainExecutor(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageCapture = ImageCapture.Builder().build()

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    onCameraInitialized(camera, imageCapture)
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Fallo al vincular cámara", e)
                }
            }, executor)
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ProOverlay(
    angle: Float,
    normalizedAzimuth: Float,
    showPitch: Boolean,
    isHeld: Boolean,
    showGrid: Boolean,
    isLeveledHoriz: Boolean,
    isLeveledVert: Boolean,
    isRotatedReference: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val lineLengthH = size.width * 0.85f
        val lineLengthV = size.height * 0.5f

        // Cuadrícula PRO opcional (Regla de tercios)
        if (showGrid) {
            val thirdW = size.width / 3f
            val thirdH = size.height / 3f
            val gridColor = Color.White.copy(alpha = 0.2f)

            drawLine(gridColor, Offset(thirdW, 0f), Offset(thirdW, size.height), 1.dp.toPx())
            drawLine(gridColor, Offset(thirdW * 2f, 0f), Offset(thirdW * 2f, size.height), 1.dp.toPx())
            drawLine(gridColor, Offset(0f, thirdH), Offset(size.width, thirdH), 1.dp.toPx())
            drawLine(gridColor, Offset(0f, thirdH * 2f), Offset(size.width, thirdH * 2f), 1.dp.toPx())
        }

        // 1. Línea Guía Horizontal (para alinear techos, suelos, etc.)
        val barColorH = if (isLeveledHoriz) Color(0xFF4CAF50) else Color(0xFFFFEB3B)
        drawLine(
            color = barColorH,
            start = Offset(center.x - lineLengthH / 2f, center.y),
            end = Offset(center.x + lineLengthH / 2f, center.y),
            strokeWidth = 4.dp.toPx()
        )

        // 2. Línea Guía Vertical (para alinear paredes, postes, esquinas, etc.)
        val barColorV = if (isLeveledVert) Color(0xFF4CAF50) else Color(0xFFFFEB3B)
        drawLine(
            color = barColorV,
            start = Offset(center.x, center.y - lineLengthV / 2f),
            end = Offset(center.x, center.y + lineLengthV / 2f),
            strokeWidth = 4.dp.toPx()
        )

        // Marcas de retícula central
        val crossSize = 10.dp.toPx()
        val crossColor = Color.White.copy(alpha = 0.8f)
        drawLine(crossColor, Offset(center.x - crossSize, center.y), Offset(center.x + crossSize, center.y), 2.dp.toPx())
        drawLine(crossColor, Offset(center.x, center.y - crossSize), Offset(center.x, center.y + crossSize), 2.dp.toPx())
    }

    // Panel flotante superior usando la rotación idéntica e invertida 180° a ClinometerCircle para estar siempre al derecho (parado)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .offset(y = (-140).dp)
                .graphicsLayer {
                    rotationZ = normalizedAzimuth + 180f
                },
            color = Color(0xFF141414).copy(alpha = 0.9f),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 8.dp,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, if (isLeveledHoriz) Color(0xFF4CAF50).copy(alpha = 0.6f) else Color(0xFFFFEB3B).copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mostrar estado SOLO si está congelado o nivelado
                if (isHeld || isLeveledHoriz) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val indicatorColor = if (isHeld) Color(0xFFEF5350) else Color(0xFF4CAF50)
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                        ) {
                            Surface(color = indicatorColor, modifier = Modifier.fillMaxSize()) {}
                        }
                        Text(
                            text = if (isHeld) stringResource(R.string.held) else stringResource(R.string.leveled),
                            color = indicatorColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Indicador de Referencia Activa
                Text(
                    text = if (isRotatedReference) stringResource(R.string.ref_90) else stringResource(R.string.ref_0),
                    color = Color(0xFF90CAF9),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                val displayText = if (showPitch) {
                    if (angle >= 999f) "Infinito / 12"
                    else "${round(angle).toInt()} / 12"
                } else {
                    String.format(Locale.getDefault(), "%.1f°", angle)
                }

                Text(
                    text = displayText,
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun takePhotoWithOverlay(
    context: Context,
    imageCapture: ImageCapture,
    angle: Float,
    normalizedAzimuth: Float,
    showPitch: Boolean,
    isLeveled: Boolean,
    isHeld: Boolean,
    isRotatedReference: Boolean,
    onSaved: (Bitmap) -> Unit
) {
    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                try {
                    val bitmap = imageProxy.toBitmap()
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()

                    // Rotar bitmap según orientación de cámara
                    val matrix = Matrix().apply { postRotate(rotationDegrees) }
                    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                    val canvas = AndroidCanvas(rotatedBitmap)
                    val width = rotatedBitmap.width.toFloat()
                    val height = rotatedBitmap.height.toFloat()
                    val centerX = width / 2f
                    val centerY = height / 2f

                    // 1. Barra horizontal central
                    val paintLineH = Paint().apply {
                        color = if (isLeveled) android.graphics.Color.parseColor("#4CAF50") else android.graphics.Color.parseColor("#FFFFEB3B")
                        strokeWidth = 10f
                        style = Paint.Style.STROKE
                    }
                    val lineLengthH = width * 0.85f
                    canvas.drawLine(centerX - lineLengthH / 2f, centerY, centerX + lineLengthH / 2f, centerY, paintLineH)

                    // 1.2 Barra vertical central
                    val paintLineV = Paint().apply {
                        color = android.graphics.Color.parseColor("#FFFFEB3B")
                        strokeWidth = 10f
                        style = Paint.Style.STROKE
                    }
                    val lineLengthV = height * 0.5f
                    canvas.drawLine(centerX, centerY - lineLengthV / 2f, centerX, centerY + lineLengthV / 2f, paintLineV)

                    // 2. Retícula
                    val paintCross = Paint().apply {
                        color = android.graphics.Color.argb(180, 255, 255, 255)
                        strokeWidth = 6f
                        style = Paint.Style.STROKE
                    }
                    canvas.drawLine(centerX - 30f, centerY, centerX + 30f, centerY, paintCross)
                    canvas.drawLine(centerX, centerY - 30f, centerX, centerY + 30f, paintCross)

                    // 3. Tarjeta HUD superior (Estabilizada exactamente con normalizedAzimuth + 180f)
                    canvas.save()
                    canvas.rotate(normalizedAzimuth + 180f, centerX, centerY - 280f)

                    val textValue = if (showPitch) {
                        if (angle >= 999f) "Infinito / 12" else "${round(angle).toInt()} / 12"
                    } else {
                        String.format(Locale.getDefault(), "%.1f°", angle)
                    }

                    val hasStatus = isHeld || isLeveled
                    val statusText = if (isHeld) "CONGELADO" else "¡NIVELADO!"
                    val refText = if (isRotatedReference) "Ref: 90°" else "Ref: 0°"

                    // Fondo oscuro moderno
                    val boxWidth = 560f
                    val boxHeight = if (hasStatus) 240f else 180f
                    val boxLeft = centerX - boxWidth / 2f
                    val boxTop = centerY - 280f - boxHeight / 2f

                    val paintBg = Paint().apply {
                        color = android.graphics.Color.argb(230, 20, 20, 20)
                        style = Paint.Style.FILL
                    }
                    canvas.drawRoundRect(boxLeft, boxTop, boxLeft + boxWidth, boxTop + boxHeight, 40f, 40f, paintBg)

                    // Borde de la tarjeta HUD
                    val paintBorder = Paint().apply {
                        color = if (isLeveled) android.graphics.Color.parseColor("#4CAF50") else android.graphics.Color.parseColor("#FFFFEB3B")
                        strokeWidth = 4f
                        style = Paint.Style.STROKE
                    }
                    canvas.drawRoundRect(boxLeft, boxTop, boxLeft + boxWidth, boxTop + boxHeight, 40f, 40f, paintBorder)

                    var currentTop = boxTop + 45f
                    if (hasStatus) {
                        val paintStatus = Paint().apply {
                            color = if (isHeld) android.graphics.Color.parseColor("#EF5350") else android.graphics.Color.parseColor("#4CAF50")
                            textSize = 32f
                            isAntiAlias = true
                            textAlign = Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        canvas.drawText(statusText, centerX, currentTop, paintStatus)
                        currentTop += 45f
                    }

                    val paintRef = Paint().apply {
                        color = android.graphics.Color.parseColor("#90CAF9")
                        textSize = 28f
                        isAntiAlias = true
                        textAlign = Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    canvas.drawText(refText, centerX, currentTop, paintRef)
                    currentTop += 65f

                    val paintValue = Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 85f
                        isAntiAlias = true
                        textAlign = Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    canvas.drawText(textValue, centerX, currentTop, paintValue)

                    canvas.restore()

                    saveBitmapToMediaStore(context, rotatedBitmap) {
                        onSaved(rotatedBitmap)
                    }
                } catch (e: Exception) {
                    Log.e("CameraOverlay", "Error procesando imagen", e)
                } finally {
                    imageProxy.close()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraCapture", "Error al capturar foto: ${exception.message}", exception)
                Toast.makeText(context, "Error al guardar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

private fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap, onSaved: () -> Unit) {
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "FullLevel_$name.jpg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FullLevel-PRO")
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (uri != null) {
        var fos: OutputStream? = null
        try {
            fos = resolver.openOutputStream(uri)
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos)
                fos.flush()
                ContextCompat.getMainExecutor(context).execute { onSaved() }
            }
        } catch (e: Exception) {
            Log.e("SaveBitmap", "Error saving bitmap", e)
        } finally {
            fos?.close()
        }
    }
}

private fun shareBitmap(context: Context, bitmap: Bitmap) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "shared_measurement.jpg")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        stream.close()

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_title)))
    } catch (e: Exception) {
        Log.e("ShareBitmap", "Error compartiendo imagen", e)
        Toast.makeText(context, "Error al compartir la imagen", Toast.LENGTH_SHORT).show()
    }
}
