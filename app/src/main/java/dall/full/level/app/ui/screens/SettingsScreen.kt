package dall.full.level.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dall.full.level.app.R
import dall.full.level.app.data.AppLanguage
import dall.full.level.app.repository.findActivity
import dall.full.level.app.ui.components.BannerAdView
import dall.full.level.app.ui.theme.AppThemePalette
import dall.full.level.app.viewmodel.ClinometerViewModel
import dall.full.level.app.viewmodel.SettingsViewModel
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    clinometerViewModel: ClinometerViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val darkThemeEnabled by viewModel.darkTheme.collectAsState()
    val oledModeEnabled by viewModel.oledMode.collectAsState()
    val followSystemTheme by viewModel.followSystemTheme.collectAsState()
    val currentPalette by viewModel.themePalette.collectAsState()
    val currentLanguage by viewModel.appLanguage.collectAsState()
    val pitchOffset by clinometerViewModel.pitchOffset.collectAsState()
    val rollOffset by clinometerViewModel.rollOffset.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val beepEnabled by viewModel.beepEnabled.collectAsState()
    val ttsEnabled by viewModel.ttsEnabled.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    val isCalibrated = abs(pitchOffset) > 0.01f || abs(rollOffset) > 0.01f
    val selectedMode = when {
        followSystemTheme -> "auto"
        darkThemeEnabled -> "dark"
        else -> "light"
    }
    val isDarkAppearance = when (selectedMode) {
        "dark" -> true
        "auto" -> isSystemInDarkTheme()
        else -> false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BannerAdView(modifier = Modifier.padding(bottom = 8.dp))
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            SettingsSection(
                title = stringResource(R.string.app_language_section),
                icon = Icons.Default.Language
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 72.dp)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(currentLanguage.flag, fontSize = 25.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.app_language_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                if (currentLanguage == AppLanguage.AUTO) {
                                    stringResource(R.string.lang_auto)
                                } else {
                                    currentLanguage.nativeName
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            SettingsSection(
                title = stringResource(R.string.theme_mode_section),
                icon = Icons.Default.Palette
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeModeOption(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.theme_auto),
                            icon = Icons.Default.Settings,
                            selected = selectedMode == "auto",
                            onClick = { viewModel.setThemeMode(SettingsViewModel.ThemeMode.AUTO) }
                        )
                        ThemeModeOption(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.theme_light),
                            icon = Icons.Default.WbSunny,
                            selected = selectedMode == "light",
                            onClick = { viewModel.setThemeMode(SettingsViewModel.ThemeMode.LIGHT) }
                        )
                        ThemeModeOption(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.theme_dark),
                            icon = Icons.Default.Brightness4,
                            selected = selectedMode == "dark",
                            onClick = { viewModel.setThemeMode(SettingsViewModel.ThemeMode.DARK) }
                        )
                    }
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        SettingsToggleRow(
                            title = stringResource(R.string.oled_mode_title),
                            description = stringResource(R.string.oled_mode_desc),
                            icon = Icons.Default.Contrast,
                            checked = oledModeEnabled,
                            enabled = isDarkAppearance,
                            onCheckedChange = viewModel::setOledMode
                        )
                    }
                }
            }

            SettingsSection(
                title = stringResource(R.string.theme_palette_section),
                icon = Icons.Default.ColorLens
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppThemePalette.entries.chunked(2).forEach { rowPalettes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowPalettes.forEach { palette ->
                                val isSelected = currentPalette == palette
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setThemePalette(palette) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.surfaceContainerHigh
                                        }
                                    ),
                                    border = if (isSelected) {
                                        BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                    } else {
                                        null
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 56.dp)
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(palette.primaryColor)
                                        )
                                        Text(
                                            text = stringResource(palette.displayNameRes),
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (isSelected) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = stringResource(R.string.theme_selected),
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            if (rowPalettes.size == 1) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            SettingsSection(
                title = stringResource(R.string.sensor_calibration_section),
                icon = Icons.Default.Tune
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (isCalibrated) Icons.Default.Check else Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(21.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (isCalibrated) stringResource(R.string.custom_calibration_active)
                                    else stringResource(R.string.factory_calibration),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    String.format(
                                        Locale.getDefault(),
                                        "Offset: Pitch %.2f° | Roll %.2f°",
                                        pitchOffset,
                                        rollOffset
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            stringResource(R.string.sensor_calibration_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    clinometerViewModel.calibrateCurrentZero()
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.calibration_success_toast),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.calibrate_zero), maxLines = 1)
                            }
                            if (isCalibrated) {
                                OutlinedButton(
                                    onClick = {
                                        clinometerViewModel.resetCalibration()
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.calibration_reset_toast),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.reset), maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }

            SettingsSection(
                title = stringResource(R.string.haptic_and_audio_section),
                icon = Icons.Default.GraphicEq
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column {
                        SettingsToggleRow(
                            title = stringResource(R.string.haptic_vibration_title),
                            description = stringResource(R.string.haptic_vibration_desc),
                            icon = Icons.Default.Vibration,
                            checked = hapticEnabled,
                            onCheckedChange = viewModel::setHapticEnabled
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        SettingsToggleRow(
                            title = stringResource(R.string.proximity_beeps_title),
                            description = stringResource(R.string.proximity_beeps_desc),
                            icon = if (beepEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            checked = beepEnabled,
                            onCheckedChange = viewModel::setBeepEnabled
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        SettingsToggleRow(
                            title = stringResource(R.string.voice_assistant_title),
                            description = stringResource(R.string.voice_assistant_desc),
                            icon = Icons.Default.RecordVoiceOver,
                            checked = ttsEnabled,
                            onCheckedChange = viewModel::setTtsEnabled
                        )
                    }
                }
            }

            SettingsSection(
                title = stringResource(R.string.pro_support),
                icon = Icons.Default.Favorite
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.support_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                stringResource(R.string.support_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Button(
                            onClick = {
                                clinometerViewModel.showRewardedAd(context.findActivity()) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.support_thanks_toast),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        ) {
                            Text(stringResource(R.string.support_button), maxLines = 1)
                        }
                    }
                }
            }
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(stringResource(R.string.app_language_section), fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppLanguage.entries.forEach { language ->
                        val isSelected = currentLanguage == language
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setAppLanguage(language)
                                    showLanguageDialog = false
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            },
                            border = if (isSelected) {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            } else {
                                null
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(language.flag, fontSize = 21.sp)
                                Text(
                                    if (language == AppLanguage.AUTO) stringResource(R.string.lang_auto)
                                    else language.nativeName,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                )
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = stringResource(R.string.theme_selected),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(19.dp)
            )
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        content()
    }
}

@Composable
private fun ThemeModeOption(
    modifier: Modifier,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            }
        ),
        border = if (selected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 68.dp)
                .padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(21.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(21.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}
