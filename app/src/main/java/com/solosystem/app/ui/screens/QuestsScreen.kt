package com.solosystem.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.solosystem.app.data.model.*
import com.solosystem.app.ui.theme.*
import com.solosystem.app.util.QuestSuggestions
import com.solosystem.app.viewmodel.AppViewModel
import com.solosystem.app.util.DateUtils

@Composable
fun QuestsScreen(viewModel: AppViewModel) {
    val quests  by viewModel.allQuests.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var focusQuest by remember { mutableStateOf<Quest?>(null) }
    var tab     by remember { mutableIntStateOf(0) }

    val filtered = when(tab) {
        0 -> quests.filter { it.type == QuestType.DAILY }
        1 -> quests.filter { it.type == QuestType.WEEKLY }
        else -> quests.filter { it.type == QuestType.CUSTOM }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().background(Black)) {
            // Tabs
            Row(Modifier.fillMaxWidth().background(DeepNavy).padding(horizontal=16.dp, vertical=8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Daily","Weekly","Custom").forEachIndexed { i, label ->
                    val sel = tab == i
                    Box(
                        Modifier.background(if(sel) RankB.copy(alpha=.2f) else Color.Transparent, RoundedCornerShape(10.dp))
                            .border(1.dp, if(sel) RankB.copy(alpha=.6f) else Border, RoundedCornerShape(10.dp))
                            .clickable { tab = i }.padding(horizontal=16.dp, vertical=8.dp)
                    ) { Text(label, color = if(sel) RankB else TextMuted, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                }
            }

            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
                val done = filtered.count { it.isCompleted }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("${done}/${filtered.size} complete", color = TextMuted, fontSize = 11.sp)
                    LinearProgressIndicator(
                        progress = { if(filtered.isEmpty()) 0f else done.toFloat()/filtered.size },
                        modifier = Modifier.width(100.dp).height(5.dp),
                        color = GreenXP, trackColor = Surface
                    )
                }
                Spacer(Modifier.height(12.dp))

                if (filtered.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📋", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("No quests here yet.", color = TextMuted, fontSize = 14.sp)
                            Text("Tap + to add one.", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }

                filtered.forEach { q ->
                    QuestRowWithFocus(q,
                        onComplete = { viewModel.completeQuest(q) },
                        onFocus    = { focusQuest = q },
                        onDelete   = { viewModel.deleteQuest(q.id) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAdd = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = RankB, contentColor = Color.White
        ) { Icon(Icons.Default.Add, "Add quest") }
    }

    if (showAdd) {
        val stats = profile?.selectedStats?.split(",")?.mapNotNull {
            runCatching { StatType.valueOf(it) }.getOrNull()
        } ?: listOf(StatType.DISCIPLINE)
        AddQuestDialog(stats, viewModel) { showAdd = false }
    }

    focusQuest?.let { q ->
        FocusModeDialog(q,
            onDismiss = { focusQuest = null },
            onComplete = { mins -> viewModel.completeQuest(q, mins); focusQuest = null }
        )
    }
}

@Composable
fun QuestRowWithFocus(quest: Quest, onComplete: () -> Unit, onFocus: () -> Unit, onDelete: () -> Unit) {
    val tc = when(quest.tier) {
        QuestTier.E->TierE; QuestTier.D->TierD; QuestTier.C->TierC
        QuestTier.B->TierB; QuestTier.A->TierA; QuestTier.S->TierS
    }
    Row(
        Modifier.fillMaxWidth()
            .background(if(quest.isCompleted) Black else DeepNavy, RoundedCornerShape(14.dp))
            .border(1.dp, if(quest.isCompleted) Border else tc.copy(alpha=.3f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(28.dp).background(if(quest.isCompleted) tc else Color.Transparent, RoundedCornerShape(8.dp))
                .border(2.dp, tc, RoundedCornerShape(8.dp))
                .clickable(enabled = !quest.isCompleted) { onComplete() },
            contentAlignment = Alignment.Center
        ) { if(quest.isCompleted) Text("✓", color = Color.White, fontSize = 14.sp) }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(quest.name, color = if(quest.isCompleted) TextMuted else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Box(Modifier.background(tc.copy(alpha=.15f), RoundedCornerShape(99.dp))
                    .border(1.dp, tc.copy(alpha=.4f), RoundedCornerShape(99.dp)).padding(horizontal=6.dp, vertical=2.dp)) {
                    Text(quest.tier.label, color = tc, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Text("${quest.statType.displayName} · +${quest.tier.xp} XP", color = TextMuted, fontSize = 10.sp)
        }
        if (!quest.isCompleted) {
            TextButton(onClick = onFocus) { Text("▶", color = tc, fontSize = 16.sp) }
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, null, tint = TextMuted, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun AddQuestDialog(userStats: List<StatType>, viewModel: AppViewModel, onDismiss: () -> Unit) {
    var tab     by remember { mutableIntStateOf(0) }  // 0=custom, 1=suggested
    var name    by remember { mutableStateOf("") }
    var tier    by remember { mutableStateOf(QuestTier.C) }
    var stat    by remember { mutableStateOf(userStats.firstOrNull() ?: StatType.DISCIPLINE) }
    var type    by remember { mutableStateOf(QuestType.DAILY) }
    var mins    by remember { mutableStateOf("30") }

    val suggestions = QuestSuggestions.getByStats(userStats)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepNavy,
        title = {
            Column {
                Text("NEW QUEST", color = RankB, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth().padding(top=8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Custom","Suggestions").forEachIndexed { i, l ->
                        val sel = tab == i
                        Box(
                            Modifier.background(if(sel) RankB.copy(alpha=.2f) else Color.Transparent, RoundedCornerShape(8.dp))
                                .border(1.dp, if(sel) RankB.copy(alpha=.5f) else Border, RoundedCornerShape(8.dp))
                                .clickable { tab = i }.padding(horizontal=12.dp, vertical=6.dp)
                        ) { Text(l, color = if(sel) RankB else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                if (tab == 0) {
                    // Custom quest form
                    OutlinedTextField(value = name, onValueChange = { name = it },
                        label = { Text("Quest name", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = RankB, unfocusedBorderColor = Border)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("TIER", color = TextMuted, fontSize = 10.sp, letterSpacing = 1.sp)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        QuestTier.values().forEach { t ->
                            val tc = when(t){QuestTier.E->TierE;QuestTier.D->TierD;QuestTier.C->TierC;QuestTier.B->TierB;QuestTier.A->TierA;QuestTier.S->TierS}
                            Box(Modifier.weight(1f).background(if(tier==t) tc.copy(alpha=.2f) else Color.Transparent, RoundedCornerShape(8.dp))
                                .border(1.dp, if(tier==t) tc else Border, RoundedCornerShape(8.dp))
                                .clickable { tier = t }.padding(vertical=8.dp),
                                contentAlignment = Alignment.Center) {
                                Text(t.label, color = if(tier==t) tc else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("TYPE", color = TextMuted, fontSize = 10.sp, letterSpacing = 1.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(QuestType.DAILY, QuestType.WEEKLY, QuestType.CUSTOM).forEach { t ->
                            Box(Modifier.background(if(type==t) RankB.copy(alpha=.2f) else Color.Transparent, RoundedCornerShape(8.dp))
                                .border(1.dp, if(type==t) RankB.copy(alpha=.5f) else Border, RoundedCornerShape(8.dp))
                                .clickable { type = t }.padding(horizontal=10.dp, vertical=6.dp)) {
                                Text(t.name, color = if(type==t) RankB else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Suggestions list
                    suggestions.forEach { s ->
                        val tc = when(s.tier){QuestTier.E->TierE;QuestTier.D->TierD;QuestTier.C->TierC;QuestTier.B->TierB;QuestTier.A->TierA;QuestTier.S->TierS}
                        Row(
                            Modifier.fillMaxWidth().padding(vertical=4.dp)
                                .background(DeepNavy, RoundedCornerShape(10.dp))
                                .border(1.dp, Border, RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.addQuest(Quest(
                                        name = s.name, description = s.description,
                                        tier = s.tier, type = s.type, statType = s.stat,
                                        estimatedMinutes = s.estimatedMinutes,
                                        createdDate = DateUtils.today()
                                    ))
                                    onDismiss()
                                }.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(s.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("${s.stat.displayName} · ${s.type.name}", color = TextMuted, fontSize = 10.sp)
                            }
                            Box(Modifier.background(tc.copy(alpha=.15f), RoundedCornerShape(99.dp))
                                .border(1.dp, tc.copy(alpha=.4f), RoundedCornerShape(99.dp)).padding(horizontal=6.dp, vertical=2.dp)) {
                                Text(s.tier.label, color = tc, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (tab == 0) GlowButton("ADD QUEST", RankB) {
                if (name.isNotBlank()) {
                    viewModel.addQuest(Quest(
                        name = name, tier = tier, type = type, statType = stat,
                        estimatedMinutes = mins.toIntOrNull() ?: 30, createdDate = DateUtils.today()
                    ))
                    onDismiss()
                }
            }
        },
        dismissButton = { OutlineBtn("Cancel") { onDismiss() } }
    )
}

@Composable
fun FocusModeDialog(quest: Quest, onDismiss: () -> Unit, onComplete: (Int) -> Unit) {
    var seconds by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var breaks  by remember { mutableIntStateOf(0) }
    val tc = when(quest.tier){QuestTier.E->TierE;QuestTier.D->TierD;QuestTier.C->TierC;QuestTier.B->TierB;QuestTier.A->TierA;QuestTier.S->TierS}

    LaunchedEffect(running) {
        if (running) {
            while (running) {
                kotlinx.coroutines.delay(1000)
                seconds++
            }
        }
    }

    val mins = seconds / 60
    val secs = seconds % 60
    val progress = (seconds.toFloat() / (quest.estimatedMinutes * 60)).coerceIn(0f, 1f)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepNavy,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("FOCUS MODE", color = TextMuted, fontSize = 10.sp, letterSpacing = 3.sp)
                Spacer(Modifier.height(4.dp))
                Text(quest.name, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                    CircularProgressIndicator(progress = { progress }, modifier = Modifier.size(160.dp),
                        color = tc, trackColor = Surface, strokeWidth = 8.dp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("%02d:%02d".format(mins, secs), color = tc, fontSize = 36.sp, fontWeight = FontWeight.Black)
                        Text("${(progress*100).toInt()}%", color = TextMuted, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                if (breaks > 0) Text("⚠ $breaks break${if(breaks>1)"s" else ""}", color = RankS, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GlowButton(if(running) "⏸ PAUSE" else if(seconds==0) "▶ START" else "▶ RESUME", tc, Modifier.weight(1f)) {
                        running = !running
                    }
                    OutlineBtn("BREAK", Modifier.weight(1f)) { breaks++; running = false }
                }
            }
        },
        confirmButton = { GlowButton("✓ COMPLETE", GreenXP) { onComplete(seconds / 60) } },
        dismissButton = { OutlineBtn("Abandon") { onDismiss() } }
    )
}