package com.solosystem.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.solosystem.app.data.model.Rank
import com.solosystem.app.ui.theme.*
import com.solosystem.app.viewmodel.AppViewModel
import androidx.compose.material3.Text

data class LeaderEntry(val username: String, val rank: Rank, val level: Int, val xp: Int, val streak: Int, val classes: String)

@Composable
fun LeaderboardScreen(viewModel: AppViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val logs    by viewModel.recentLogs.collectAsState()
    val p = profile ?: return

    // Leaderboard is local — shows your own progress + seeded demo hunters
    val entries = remember {
        listOf(
            LeaderEntry("ShadowKing",  Rank.S, 8,  14200, 44, "Coder, AI/ML"),
            LeaderEntry("IronHunter",  Rank.A, 6,  9800,  31, "Athlete"),
            LeaderEntry("NightCrawler",Rank.B, 9,  7100,  19, "Student, Coder"),
            LeaderEntry("ZenMaster",   Rank.B, 4,  5600,  8,  "Wellness"),
            LeaderEntry("ByteWarrior", Rank.C, 7,  3900,  15, "Coder"),
        )
    }

    // Insert real player in sorted position
    val playerEntry = LeaderEntry(p.username, p.rank, p.level, p.totalXp, p.streak,
        p.selectedClasses.replace(",", ", "))
    val allEntries = (entries + playerEntry).sortedWith(
        compareByDescending<LeaderEntry> { it.rank.ordinal }
            .thenByDescending { it.level }
            .thenByDescending { it.xp }
    )

    Column(Modifier.fillMaxSize().background(Black).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("LEADERBOARD", color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp)
        Text("Global Rankings", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("* Demo hunters shown until more players join", color = TextMuted, fontSize = 11.sp)
        Spacer(Modifier.height(16.dp))

        allEntries.forEachIndexed { i, entry ->
            val isMe = entry.username == p.username
            val rc_  = Color(entry.rank.color)
            val medal = when(i) { 0->"🥇"; 1->"🥈"; 2->"🥉"; else->"#${i+1}" }

            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    .background(if(isMe) rc_.copy(alpha=.1f) else DeepNavy, RoundedCornerShape(14.dp))
                    .border(1.dp, if(isMe) rc_.copy(alpha=.5f) else Border, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                    Text(medal, fontSize = if(i<3) 20.sp else 12.sp,
                        color = when(i){0->GoldXP;1->Color(0xFF94A3B8);2->Color(0xFFCD7F32);else->TextMuted},
                        fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(entry.username, color = if(isMe) rc_ else TextPrimary,
                            fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        if (isMe) Box(Modifier.background(rc_.copy(alpha=.2f), RoundedCornerShape(99.dp))
                            .border(1.dp, rc_.copy(alpha=.4f), RoundedCornerShape(99.dp)).padding(horizontal=6.dp, vertical=1.dp)) {
                            Text("YOU", color = rc_, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Box(Modifier.background(rc_.copy(alpha=.15f), RoundedCornerShape(99.dp))
                            .border(1.dp, rc_.copy(alpha=.3f), RoundedCornerShape(99.dp)).padding(horizontal=6.dp, vertical=1.dp)) {
                            Text(entry.rank.name, color = rc_, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    Text("Lv.${entry.level} · ${entry.xp.toLocaleString()} XP · 🔥${entry.streak}",
                        color = TextMuted, fontSize = 10.sp)
                    if (entry.classes.isNotEmpty())
                        Text(entry.classes, color = TextMuted.copy(alpha=.6f), fontSize = 10.sp)
                }
            }
        }

        // Quest log
        Spacer(Modifier.height(20.dp))
        Text("RECENT ACTIVITY", color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(10.dp))

        if (logs.isEmpty()) {
            Text("Complete quests to see your activity here.", color = TextMuted, fontSize = 12.sp)
        } else {
            logs.take(10).forEach { log ->
                val tc = when(log.tier){com.solosystem.app.data.model.QuestTier.E->TierE;com.solosystem.app.data.model.QuestTier.D->TierD;com.solosystem.app.data.model.QuestTier.C->TierC;com.solosystem.app.data.model.QuestTier.B->TierB;com.solosystem.app.data.model.QuestTier.A->TierA;com.solosystem.app.data.model.QuestTier.S->TierS}
                Row(
                    Modifier.fillMaxWidth().padding(bottom=6.dp)
                        .background(DeepNavy, RoundedCornerShape(10.dp))
                        .border(1.dp, Border, RoundedCornerShape(10.dp)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✓", color = GreenXP, fontSize = 14.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(log.questName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(log.completedDate, color = TextMuted, fontSize = 10.sp)
                    }
                    Text("+${log.xpEarned} XP", color = tc, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun Int.toLocaleString(): String {
    return String.format("%,d", this)
}