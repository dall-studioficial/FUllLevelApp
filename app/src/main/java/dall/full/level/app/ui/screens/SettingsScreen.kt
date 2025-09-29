package dall.full.level.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
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
            Text("Personaliza tu experiencia visual", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Activar modo oscuro", modifier = Modifier.weight(1f))
                Switch(
                    checked = darkThemeEnabled,
                    onCheckedChange = viewModel::setDarkTheme
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Modo OLED (fondo negro absoluto)", modifier = Modifier.weight(1f))
                Switch(
                    checked = oledModeEnabled,
                    onCheckedChange = { if (darkThemeEnabled) viewModel.setOledMode(it) },
                    enabled = darkThemeEnabled
                )
            }
            if (!darkThemeEnabled) {
                Text(
                    "Solo disponible en Tema Oscuro",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 12.dp, top = 0.dp)
                )
            }
            // Aquí puedes añadir más funciones visuales escalables
        }
    }
}
