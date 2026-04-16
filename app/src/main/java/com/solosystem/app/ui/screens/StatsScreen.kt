package com.solosystem.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.solosystem.app.data.model.StatType
import com.solosystem.app.ui.theme.*
import com.solosystem.app.viewmodel.AppViewModel

@Composable
fun StatsScreen(viewModel: AppViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val stats   by viewModel.allStats.collectAsState()
    val p = profile ?: return

    val userStatTypes = p.selectedStats.split(",").mapNotNull {
        runCatching { StatType.valueOf(it) }.getOrNull()
    }

    Column(Modifier.fillMaxSize().background(Black).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("STAT SHEET", color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp)
        Text(p.username, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(16.dp))

        // Focus time card
        Row(
            Modifier.fillMaxWidth().background(RankB.copy(alpha=.08f), RoundedCornerShape(14.dp))
                .border(1.dp, RankB.copy(alpha=.3f), RoundedCornerShape(14.dp)).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⏱", fontSize = 24.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Focus Time", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Total hours logged", color = TextMuted, fontSize = 10.sp)
                }
            }
            Text("${p.totalFocusMinutes / 60}h ${p.totalFocusMinutes % 60}m",
                color = RankB, fontSize = 22.sp, fontWeight = FontWeight.Black)
        }

        Spacer(Modifier.height(12.dp))

        // Stat cards
        userStatTypes.forEach { statType ->
            val statData = stats.find { it.statType == statType }
            val pts      = statData?.points ?: 0
            val col      = Color(statType.color)
            val pct      = (pts.toFloat() / 1000f).coerceIn(0f, 1f)

            Box(
                Modifier.fillMaxWidth().padding(bottom = 10.dp)
                    .background(DeepNavy, RoundedCornerShape(14.dp))
                    .border(1.dp, col.copy(alpha=.3f), RoundedCornerShape(14.dp)).padding(16.dp)
            ) {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(statType.emoji, fontSize = 22.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(statType.displayName, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("STAT POINTS", color = TextMuted, fontSize = 9.sp, letterSpacing = 1.sp)
                            }
                        }
                        Text("$pts", color = col, fontSize = 26.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(progress = { pct }, modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = col, trackColor = Surface)
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("+${statData?.weeklyGain ?: 0} this week", color = TextMuted, fontSize = 10.sp)
                        Text("${(pct*100).toInt()}%", color = col, fontSize = 10.sp)
                    }
                }
            }
        }

        // Undetermined
        val undetermined = StatType.values().filter { it !in userStatTypes }
        if (undetermined.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text("UNDETERMINED", color = TextMuted.copy(alpha=.4f), fontSize = 10.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                undetermined.take(4).forEach { stat ->
                    Box(Modifier.weight(1f).background(DeepNavy.copy(alpha=.5f), RoundedCornerShape(12.dp))
                        .border(1.dp, Border, RoundedCornerShape(12.dp)).padding(14.dp),
                        contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stat.emoji, fontSize = 20.sp, modifier = Modifier.then(Modifier))
                            Text(stat.displayName, color = TextMuted.copy(alpha=.4f), fontSize = 10.sp)
                            Text("???", color = TextMuted.copy(alpha=.3f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}