package com.solosystem.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// ─── ENUMS ───────────────────────────────────────────────────────────────────

enum class QuestTier(val xp: Int, val label: String) {
    E(50, "E"), D(100, "D"), C(200, "C"),
    B(350, "B"), A(500, "A"), S(800, "S")
}

enum class QuestType { DAILY, WEEKLY, CUSTOM }

enum class StatType(val displayName: String, val emoji: String, val color: Long) {
    DISCIPLINE("Discipline", "🧠", 0xFFF59E0B),
    ACADEMIC("Academic",    "📚", 0xFF3B82F6),
    PHYSICAL("Physical",    "💪", 0xFFEF4444),
    WELLNESS("Wellness",    "🌿", 0xFF22C55E),
    SKILLS("Skills",        "💻", 0xFFA855F7),
    FINANCIAL("Financial",  "💰", 0xFF10B981),
    CREATIVE("Creative",    "🎨", 0xFFF43F5E),
    SPIRITUAL("Spiritual",  "🛐", 0xFF8B5CF6)
}

enum class Rank(val displayName: String, val color: Long, val xpPerLevel: Int) {
    E("E Rank",  0xFF6B7280, 1500),
    D("D Rank",  0xFF22C55E, 4000),
    C("C Rank",  0xFF3B82F6, 10000),
    B("B Rank",  0xFFA855F7, 22000),
    A("A Rank",  0xFFF59E0B, 50000),
    S("S Rank",  0xFFEF4444, 120000)
}

// ─── ROOM ENTITIES ────────────────────────────────────────────────────────────

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val username: String = "Hunter",
    val rank: Rank = Rank.E,
    val level: Int = 1,
    val currentXp: Int = 0,
    val totalXp: Int = 0,
    val streak: Int = 0,
    val lastActiveDate: String = "",
    val selectedStats: String = "DISCIPLINE,ACADEMIC,PHYSICAL",  // comma-separated
    val selectedClasses: String = "",
    val activeTitle: String = "",
    val graceCards: Int = 0,
    val lifeCards: Int = 0,
    val totalQuestsDone: Int = 0,
    val totalFocusMinutes: Int = 0,
    val missedDays: Int = 0,    // for penalty tracking
    val penaltyActive: Boolean = false,
    val joinedDate: String = ""
)

@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val tier: QuestTier = QuestTier.C,
    val type: QuestType = QuestType.DAILY,
    val statType: StatType = StatType.DISCIPLINE,
    val estimatedMinutes: Int = 30,
    val isCompleted: Boolean = false,
    val isActive: Boolean = true,
    val completedDate: String = "",
    val createdDate: String = "",
    val scheduledTime: String = "",  // "HH:mm" or ""
    val isPrivate: Boolean = false
)

@Entity(tableName = "stat_points")
data class StatPoints(
    @PrimaryKey val statType: StatType,
    val points: Int = 0,
    val weeklyGain: Int = 0,
    val lastUpdated: String = ""
)

@Entity(tableName = "titles")
data class Title(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val isEquipped: Boolean = false,
    val unlockedDate: String = ""
)

@Entity(tableName = "quest_log")
data class QuestLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questId: Int,
    val questName: String,
    val tier: QuestTier,
    val xpEarned: Int,
    val focusMinutes: Int = 0,
    val completedDate: String,
    val statType: StatType
)

@Entity(tableName = "penalty_log")
data class PenaltyLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val triggeredDate: String,
    val questName: String = "Penalty Quest",
    val isCompleted: Boolean = false,
    val deadline: String = ""
)