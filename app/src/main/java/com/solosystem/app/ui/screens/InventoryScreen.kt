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
import com.solosystem.app.ui.theme.*
import com.solosystem.app.viewmodel.AppViewModel

@Composable
fun InventoryScreen(viewModel: AppViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val titles  by viewModel.allTitles.collectAsState()
    val p = profile ?: return

    Column(Modifier.fillMaxSize().background(Black).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("INVENTORY", color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(16.dp))

        // Cards
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CardBox("🛡️", "Grace Cards", "${p.graceCards}", "1-day penalty shield", GoldXP, Modifier.weight(1f))
            CardBox("❤️", "Life Cards",  "${p.lifeCards}",  "Removes penalty quest", RankS,  Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        // Stats summary
        Text("HUNTER RECORD", color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                Triple("Quests Done",     "${p.totalQuestsDone}",       RankC),
                Triple("Focus Hours",     "${p.totalFocusMinutes/60}h", RankB),
                Triple("Best Streak",     "🔥${p.streak}",              GoldXP)
            ).forEach { (label, value, color) ->
                Box(Modifier.weight(1f).background(DeepNavy, RoundedCornerShape(12.dp))
                    .border(1.dp, color.copy(alpha=.2f), RoundedCornerShape(12.dp)).padding(12.dp),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        Text(label, color = TextMuted, fontSize = 9.sp, letterSpacing = .5.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Titles
        Text("TITLES", color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(10.dp))

        val unlocked = titles.filter { it.isUnlocked }
        val locked   = titles.filter { !it.isUnlocked }

        if (unlocked.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(vertical=20.dp), contentAlignment = Alignment.Center) {
                Text("Complete achievements to unlock titles.", color = TextMuted, fontSize = 12.sp)
            }
        }

        unlocked.forEach { title ->
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    .background(if(title.isEquipped) RankB.copy(alpha=.1f) else DeepNavy, RoundedCornerShape(12.dp))
                    .border(1.dp, if(title.isEquipped) RankB.copy(alpha=.5f) else Border, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👑", fontSize = 20.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(title.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(title.description, color = TextMuted, fontSize = 11.sp)
                    if (title.isEquipped) Text("EQUIPPED", color = RankB, fontSize = 9.sp, letterSpacing = 1.sp)
                }
                if (!title.isEquipped) {
                    OutlineBtn("EQUIP") { viewModel.equipTitle(title.id) }
                }
            }
        }

        if (locked.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text("LOCKED", color = TextMuted.copy(alpha=.4f), fontSize = 10.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(10.dp))
            locked.forEach { title ->
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        .background(Black, RoundedCornerShape(12.dp))
                        .border(1.dp, Border, RoundedCornerShape(12.dp))
                        .padding(14.dp).then(Modifier),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔒", fontSize = 20.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(title.name, color = TextMuted.copy(alpha=.5f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(title.description, color = TextMuted.copy(alpha=.4f), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CardBox(icon: String, label: String, value: String, sub: String, color: Color, modifier: Modifier) {
    Box(modifier.background(color.copy(alpha=.07f), RoundedCornerShape(14.dp))
        .border(1.dp, color.copy(alpha=.3f), RoundedCornerShape(14.dp)).padding(16.dp),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 28.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, color = color, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(sub,   color = TextMuted,     fontSize = 9.sp)
        }
    }
}