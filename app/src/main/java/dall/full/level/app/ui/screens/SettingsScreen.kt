package dall.full.level.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tonality
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dall.full.level.app.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val darkThemeEnabled by viewModel.darkTheme.collectAsState()
    val oledModeEnabled by viewModel.oledMode.collectAsState()
    val followSystemTheme by viewModel.followSystemTheme.collectAsState()

    // Estado combinado para selección
    val selectedMode = when {
        followSystemTheme -> "auto"
        darkThemeEnabled -> "dark"
        else -> "light"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Apariencia", style = MaterialTheme.typography.titleLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Automático
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewModel.setThemeMode(SettingsViewModel.ThemeMode.AUTO)
                        },
                    shape = RoundedCornerShape(16.dp),
                    border = if (selectedMode == "auto") BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.primary
                    ) else null
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Text("Automático", fontSize = 16.sp)
                        if (selectedMode == "auto") Text(
                            "Seleccionado",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                }
                // Claro
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewModel.setThemeMode(SettingsViewModel.ThemeMode.LIGHT)
                        },
                    shape = RoundedCornerShape(16.dp),
                    border = if (selectedMode == "light") BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.primary
                    ) else null
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.WbSunny,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Text("Claro", fontSize = 16.sp)
                        if (selectedMode == "light") Text(
                            "Seleccionado",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                }
                // Oscuro
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewModel.setThemeMode(SettingsViewModel.ThemeMode.DARK)
                        },
                    shape = RoundedCornerShape(16.dp),
                    border = if (selectedMode == "dark") BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.primary
                    ) else null
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.Brightness4, contentDescription = null, modifier = Modifier.size(40.dp))
                        Text("Oscuro", fontSize = 16.sp)
                        if (selectedMode == "dark") Text("Seleccionado", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(30.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Tonality, contentDescription = null)
                Spacer(Modifier.width(16.dp))
                Text("Modo OLED (fondo negro absoluto)", modifier = Modifier.weight(1f))
                Switch(
                    checked = oledModeEnabled,
                    onCheckedChange = {
                        if (selectedMode == "dark") viewModel.setOledMode(it)
                    },
                    enabled = selectedMode == "dark"
                )
            }
            if (selectedMode != "dark") {
                Text(
                    "Solo disponible en Oscuro",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 36.dp, top = 2.dp)
                )
            }
        }
    }
}
