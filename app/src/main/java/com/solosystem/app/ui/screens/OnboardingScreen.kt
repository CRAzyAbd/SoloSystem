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
fun OnboardingScreen(viewModel: AppViewModel) {
    var step       by remember { mutableIntStateOf(0) }
    var username   by remember { mutableStateOf("") }
    var error      by remember { mutableStateOf("") }
    val selStats   = remember { mutableStateListOf<StatType>() }
    val selClasses = remember { mutableStateListOf<String>() }

    val classes = listOf("Coder","Cybersec","Student","Athlete","Designer","Writer","Entrepreneur","AI/ML","Gamer")
    val statOptions = StatType.values().filter { it != StatType.DISCIPLINE }

    Box(Modifier.fillMaxSize().background(Black), contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("⚡", fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text("SOLO SYSTEM", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
            Text("BETA v1.0", color = TextMuted, fontSize = 11.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(32.dp))

            when (step) {
                0 -> {
                    StepCard(title = "STEP 1 · IDENTITY", accent = RankB) {
                        Text("Choose your Hunter name", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("This appears on the leaderboard.", color = TextMuted, fontSize = 13.sp)
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = username, onValueChange = { username = it; error = "" },
                            placeholder = { Text("e.g. ShadowHunter", color = TextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                focusedBorderColor = RankB, unfocusedBorderColor = Border
                            )
                        )
                        if (error.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(error, color = RankS, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        GlowButton("CONTINUE →", RankB) {
                            if (username.trim().length < 3) error = "At least 3 characters"
                            else step = 1
                        }
                    }
                }

                1 -> {
                    StepCard(title = "STEP 2 · CLASSES (up to 3)", accent = RankC) {
                        Text("Who are you IRL?", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        FlowRow(classes, selClasses, max = 3, accent = RankC)
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlineBtn("← BACK", Modifier.weight(1f)) { step = 0 }
                            GlowButton("CONTINUE →", RankC, Modifier.weight(2f)) { step = 2 }
                        }
                    }
                }

                2 -> {
                    StepCard(title = "STEP 3 · STATS (pick 3–5)", accent = RankD) {
                        Text("Build your stat sheet", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Discipline is always included.", color = TextMuted, fontSize = 12.sp)
                        Text("${selStats.size}/4 selected (+Discipline)", color = RankB, fontSize = 11.sp)
                        Spacer(Modifier.height(12.dp))
                        statOptions.forEach { stat ->
                            val sel = stat in selStats
                            val col = Color(stat.color)
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    .background(if (sel) col.copy(alpha=.1f) else Color.Transparent, RoundedCornerShape(10.dp))
                                    .border(1.dp, if (sel) col.copy(alpha=.5f) else Border, RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (sel) selStats.remove(stat)
                                        else if (selStats.size < 4) selStats.add(stat)
                                    }.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stat.emoji, fontSize = 20.sp)
                                Spacer(Modifier.width(10.dp))
                                Text(stat.displayName, color = if(sel) col else TextSecondary,
                                    fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (sel) {
                                    Spacer(Modifier.weight(1f))
                                    Text("✓", color = col, fontSize = 16.sp)
                                }
                            }
                        }
                        if (error.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(error, color = RankS, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlineBtn("← BACK", Modifier.weight(1f)) { step = 1 }
                            GlowButton("ENTER THE SYSTEM →", RankD, Modifier.weight(2f)) {
                                if (selStats.size < 3) { error = "Pick at least 3 stats"; return@GlowButton }
                                val stats = listOf(StatType.DISCIPLINE) + selStats
                                viewModel.setupNewUser(username.trim(), stats, selClasses.toList())
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepCard(title: String, accent: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .background(DeepNavy, RoundedCornerShape(20.dp))
            .border(1.dp, accent.copy(alpha=.3f), RoundedCornerShape(20.dp))
            .padding(24.dp)
    ) {
        Text(title, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(items: List<String>, selected: MutableList<String>, max: Int, accent: Color) {
    androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            val sel = item in selected
            Box(
                Modifier.padding(bottom = 8.dp)
                    .background(if(sel) accent.copy(alpha=.15f) else Color.Transparent, RoundedCornerShape(10.dp))
                    .border(1.dp, if(sel) accent.copy(alpha=.6f) else Border, RoundedCornerShape(10.dp))
                    .clickable { if(sel) selected.remove(item) else if(selected.size < max) selected.add(item) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(item, color = if(sel) accent else TextMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GlowButton(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha=.8f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
    }
}

@Composable
fun OutlineBtn(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
        border = BorderStroke(1.dp, Border)
    ) { Text(text, fontWeight = FontWeight.Bold) }
}