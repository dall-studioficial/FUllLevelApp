package dall.full.level.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import dall.full.level.app.R

private data class GuidePage(
    val titleRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector
)

@Composable
fun OnboardingGuide(
    currentStep: Int,
    liveReading: String,
    needleAngle: Float,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    val pages = listOf(
        GuidePage(R.string.guide_welcome_title, R.string.guide_welcome_body, Icons.Default.AutoAwesome),
        GuidePage(R.string.guide_level_title, R.string.guide_level_body, Icons.Default.Explore),
        GuidePage(R.string.guide_modes_title, R.string.guide_modes_body, Icons.Default.Straighten),
        GuidePage(R.string.guide_reference_title, R.string.guide_reference_body, Icons.Default.Tune),
        GuidePage(R.string.guide_tools_title, R.string.guide_tools_body, Icons.Default.PauseCircle),
        GuidePage(R.string.guide_ready_title, R.string.guide_ready_body, Icons.Default.CheckCircle)
    )
    val page = pages[currentStep.coerceIn(pages.indices)]
    val progress by animateFloatAsState(
        targetValue = (currentStep + 1f) / pages.size,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "guideProgress"
    )
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.56f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 18.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (currentStep == 1) 76.dp else 178.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    primary,
                                    MaterialTheme.colorScheme.tertiary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(if (currentStep == 1) 4.dp else 12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(if (currentStep == 1) 42.dp else 74.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = page.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(if (currentStep == 1) 25.dp else 38.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.guide_brand),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = if (currentStep == 1) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(
                        horizontal = if (currentStep == 1) 18.dp else 24.dp,
                        vertical = if (currentStep == 1) 12.dp else 22.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.guide_step_counter, currentStep + 1, pages.size),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onFinish) {
                            Text(
                                text = stringResource(R.string.guide_skip),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Spacer(Modifier.height(9.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .height(5.dp)
                                .clip(CircleShape)
                                .background(primary)
                        )
                    }

                    Spacer(Modifier.height(if (currentStep == 1) 12.dp else 22.dp))
                    Text(
                        text = stringResource(page.titleRes),
                        style = if (currentStep == 1) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(if (currentStep == 1) 5.dp else 9.dp))
                    Text(
                        text = stringResource(page.descriptionRes),
                        style = if (currentStep == 1) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = if (currentStep == 1) 20.sp else 24.sp
                    )

                    if (currentStep == 1) {
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                MiniClinometerDial(
                                    needleAngle = needleAngle,
                                    modifier = Modifier.size(82.dp)
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        stringResource(R.string.guide_live_reading),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        liveReading,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        stringResource(R.string.guide_mini_dial),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(if (currentStep == 1) 12.dp else 24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStep == 0) {
                            OutlinedButton(
                                onClick = onFinish,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(stringResource(R.string.guide_skip))
                            }
                        }

                        Button(
                            onClick = if (currentStep == pages.lastIndex) onFinish else onNext,
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                stringResource(
                                    if (currentStep == pages.lastIndex) R.string.guide_start
                                    else R.string.guide_next
                                ),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun MiniClinometerDial(
    needleAngle: Float,
    modifier: Modifier = Modifier
) {
    ClinometerDial(needleAngle = needleAngle, modifier = modifier, showLabels = false)
}
