package com.solosystem.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Palette ──────────────────────────────────────────────────────────────────
val Black        = Color(0xFF060610)
val DeepNavy     = Color(0xFF0D0D1A)
val Navy         = Color(0xFF12122A)
val Surface      = Color(0xFF1A1A2E)
val Border       = Color(0xFF1E1E3A)

val RankE        = Color(0xFF6B7280)
val RankD        = Color(0xFF22C55E)
val RankC        = Color(0xFF3B82F6)
val RankB        = Color(0xFFA855F7)
val RankA        = Color(0xFFF59E0B)
val RankS        = Color(0xFFEF4444)

val TextPrimary  = Color(0xFFE2E8F0)
val TextSecondary= Color(0xFF94A3B8)
val TextMuted    = Color(0xFF4A5568)

val TierE        = Color(0xFF6B7280)
val TierD        = Color(0xFF22C55E)
val TierC        = Color(0xFF3B82F6)
val TierB        = Color(0xFFA855F7)
val TierA        = Color(0xFFF59E0B)
val TierS        = Color(0xFFEF4444)

val GoldXP       = Color(0xFFF59E0B)
val GreenXP      = Color(0xFF22C55E)

private val DarkColorScheme = darkColorScheme(
    primary        = RankB,
    secondary      = RankC,
    tertiary       = GoldXP,
    background     = Black,
    surface        = DeepNavy,
    surfaceVariant = Navy,
    onPrimary      = Color.White,
    onBackground   = TextPrimary,
    onSurface      = TextPrimary,
    outline        = Border
)

@Composable
fun SoloSystemTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}