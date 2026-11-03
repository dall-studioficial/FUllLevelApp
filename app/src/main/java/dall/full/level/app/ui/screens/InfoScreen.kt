package dall.full.level.app.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dall.full.level.app.BuildConfig
import dall.full.level.app.R
import dall.full.level.app.ui.components.BannerAdView
import dall.full.level.app.viewmodel.InfoViewModel

private const val CONTACT_EMAIL = "dall.studioficial@gmail.com"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    viewModel: InfoViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = colors.primary
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
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(colors.primary, colors.tertiary)
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = colors.onPrimary.copy(alpha = 0.16f),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = null,
                                tint = colors.onPrimary,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.about_app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.onPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.about_tagline),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onPrimary.copy(alpha = 0.92f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = colors.onPrimary.copy(alpha = 0.16f)
                    ) {
                        Text(
                            stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            color = colors.onPrimary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                stringResource(R.string.about_features_heading),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )

            FeatureCard(
                icon = Icons.Filled.Tune,
                title = stringResource(R.string.about_measure_title),
                description = stringResource(R.string.about_measure_body)
            )
            FeatureCard(
                icon = Icons.Filled.CameraAlt,
                title = stringResource(R.string.about_camera_title),
                description = stringResource(R.string.about_camera_body)
            )
            FeatureCard(
                icon = Icons.Filled.Pause,
                title = stringResource(R.string.about_tools_title),
                description = stringResource(R.string.about_tools_body)
            )
            FeatureCard(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                title = stringResource(R.string.about_feedback_title),
                description = stringResource(R.string.about_feedback_body)
            )
            FeatureCard(
                icon = Icons.Filled.Palette,
                title = stringResource(R.string.about_personalization_title),
                description = stringResource(R.string.about_personalization_body)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerHigh),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(R.string.about_contact_heading),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.about_author),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.AlternateEmail,
                            contentDescription = null,
                            tint = colors.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            CONTACT_EMAIL,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurface
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(CONTACT_EMAIL))
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.email_copied_toast),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ) {
                            Text(stringResource(R.string.copy), fontSize = 12.sp)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.contact_subject))
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.contact_body))
                    }
                    context.startActivity(
                        Intent.createChooser(intent, context.getString(R.string.share_contact_title))
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Share, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.share_contact),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    val colors = MaterialTheme.colorScheme
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerHigh),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = colors.onPrimaryContainer)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}
