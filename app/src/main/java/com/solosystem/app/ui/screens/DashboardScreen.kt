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
import com.solosystem.app.data.model.*
import com.solosystem.app.ui.theme.*
import com.solosystem.app.viewmodel.AppViewModel

@Composable
fun DashboardScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val profile by viewModel.userProfile.collectAsState()
    val quests  by viewModel.allQuests.collectAsState()
    val p = profile ?: return

    val rc         = Color(p.rank.color)
    val dailyTotal = quests.count { it.type == QuestType.DAILY }
    val dailyDone  = quests.count { it.type == QuestType.DAILY && it.isCompleted }
    val xpPct      = (p.currentXp.toFloat() / p.rank.xpPerLevel).coerceIn(0f, 1f)

    Column(Modifier.fillMaxSize().background(Black).verticalScroll(rememberScrollState()).padding(16.dp)) {

        // ── Rank Hero Card ────────────────────────────────────────────────────
        Box(
            Modifier.fillMaxWidth()
                .background(DeepNavy, RoundedCornerShape(20.dp))
                .border(1.dp, rc.copy(alpha=.3f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("RANK", color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp)
                        Text(p.rank.name, color = rc, fontSize = 48.sp, fontWeight = FontWeight.Black)
                        Text("Level ${p.level} · ${p.username}", color = TextSecondary, fontSize = 13.sp)
                        if (p.activeTitle.isNotEmpty())
                            Text("「${p.activeTitle}」", color = rc.copy(alpha=.7f), fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("STREAK", color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp)
                        Text("🔥 ${p.streak}", color = GoldXP, fontSize = 30.sp, fontWeight = FontWeight.Black)
                        Text("${com.solosystem.app.util.DateUtils.streakMultiplier(p.streak)}× mult", color = TextMuted, fontSize = 10.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("EXP", color = TextMuted, fontSize = 10.sp)
                    Text("${p.currentXp} / ${p.rank.xpPerLevel}", color = rc, fontSize = 10.sp)
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { xpPct },
                    modifier = Modifier.fillMaxWidth().height(7.dp),
                    color = rc, trackColor = Surface
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Quick Stats ───────────────────────────────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(
                Triple("TODAY", "$dailyDone/$dailyTotal", if(dailyDone==dailyTotal && dailyTotal>0) GreenXP else RankB),
                Triple("GRACE",  "${p.graceCards}",    GoldXP),
                Triple("DONE",   "${p.totalQuestsDone}", RankC)
            ).forEach { (label, value, color) ->
                Box(Modifier.weight(1f).background(DeepNavy, RoundedCornerShape(12.dp))
                    .border(1.dp, color.copy(alpha=.2f), RoundedCornerShape(12.dp)).padding(12.dp),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(label, color = TextMuted, fontSize = 9.sp, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Penalty Warning ───────────────────────────────────────────────────
        if (p.penaltyActive) {
            Box(
                Modifier.fillMaxWidth().background(RankS.copy(alpha=.1f), RoundedCornerShape(12.dp))
                    .border(1.dp, RankS.copy(alpha=.5f), RoundedCornerShape(12.dp)).padding(16.dp)
            ) {
                Column {
                    Text("⚠ PENALTY ACTIVE", color = RankS, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Complete your penalty quest or use a Life Card to remove it.", color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (p.lifeCards > 0) GlowButton("USE LIFE CARD", RankS, Modifier.weight(1f)) { viewModel.useLifeCard() }
                        if (p.graceCards > 0) GlowButton("USE GRACE CARD", GoldXP, Modifier.weight(1f)) { viewModel.useGraceCard() }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Today's Quests ────────────────────────────────────────────────────
        Text("TODAY'S QUESTS", color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(10.dp))

        quests.filter { it.type == QuestType.DAILY }.take(5).forEach { quest ->
            QuestRow(quest) { viewModel.completeQuest(quest) }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(8.dp))
        OutlineBtn("View All Quests →", Modifier.fillMaxWidth()) { onNavigate("quests") }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun QuestRow(quest: Quest, onComplete: () -> Unit) {
    val tc = when(quest.tier) {
        QuestTier.E -> TierE; QuestTier.D -> TierD; QuestTier.C -> TierC
        QuestTier.B -> TierB; QuestTier.A -> TierA; QuestTier.S -> TierS
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
        ) {
            if (quest.isCompleted) Text("✓", color = Color.White, fontSize = 14.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(quest.name, color = if(quest.isCompleted) TextMuted else TextPrimary,
                    fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    textDecoration = if(quest.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null)
                Box(Modifier.background(tc.copy(alpha=.15f), RoundedCornerShape(99.dp))
                    .border(1.dp, tc.copy(alpha=.4f), RoundedCornerShape(99.dp)).padding(horizontal=6.dp, vertical=2.dp)) {
                    Text(quest.tier.label, color = tc, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Text("${quest.statType.displayName} · +${quest.tier.xp} XP · ~${quest.estimatedMinutes}min",
                color = TextMuted, fontSize = 10.sp)
        }
    }
}