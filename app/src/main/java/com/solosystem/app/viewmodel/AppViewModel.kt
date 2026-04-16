package com.solosystem.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.solosystem.app.data.database.AppDatabase
import com.solosystem.app.data.model.*
import com.solosystem.app.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).appDao()

    val userProfile: StateFlow<UserProfile?> =
        dao.getUserProfile().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allQuests: StateFlow<List<Quest>> =
        dao.getAllActiveQuests().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStats: StateFlow<List<StatPoints>> =
        dao.getAllStats().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTitles: StateFlow<List<Title>> =
        dao.getAllTitles().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLogs: StateFlow<List<QuestLog>> =
        dao.getRecentLogs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val penalties: StateFlow<List<PenaltyLog>> =
        dao.getPenalties().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Setup ─────────────────────────────────────────────────────────────────
    fun setupNewUser(username: String, selectedStats: List<StatType>, classes: List<String>) {
        viewModelScope.launch {
            val profile = UserProfile(
                username = username,
                selectedStats = selectedStats.joinToString(",") { it.name },
                selectedClasses = classes.joinToString(","),
                joinedDate = DateUtils.today(),
                lastActiveDate = DateUtils.today()
            )
            dao.saveUserProfile(profile)

            // Init stat points for each selected stat
            selectedStats.forEach { stat ->
                dao.upsertStat(StatPoints(statType = stat, lastUpdated = DateUtils.today()))
            }

            // Seed default quests based on selected stats
            val defaultQuests = QuestSuggestions.all
                .filter { it.stat in selectedStats || it.stat == StatType.DISCIPLINE }
                .take(6)
                .map { s ->
                    Quest(
                        name = s.name,
                        description = s.description,
                        tier = s.tier,
                        type = s.type,
                        statType = s.stat,
                        estimatedMinutes = s.estimatedMinutes,
                        createdDate = DateUtils.today()
                    )
                }
            dao.insertQuests(defaultQuests)

            // Seed all titles as locked
            TitleChecker.allTitles.forEach { t ->
                dao.upsertTitle(Title(id = t.id, name = t.name, description = t.description))
            }
        }
    }

    // ── Complete quest ─────────────────────────────────────────────────────────
    fun completeQuest(quest: Quest, focusMinutes: Int = 0) {
        viewModelScope.launch {
            val profile = dao.getUserProfileOnce() ?: return@launch
            val xpEarned = DateUtils.xpWithStreak(quest.tier.xp, profile.streak)

            dao.updateQuest(quest.copy(isCompleted = true, completedDate = DateUtils.today()))
            dao.insertLog(
                QuestLog(
                    questId = quest.id, questName = quest.name,
                    tier = quest.tier, xpEarned = xpEarned,
                    focusMinutes = focusMinutes, completedDate = DateUtils.today(),
                    statType = quest.statType
                )
            )

            // Update stat points
            val current = dao.getStatByType(quest.statType)
            dao.upsertStat(
                StatPoints(
                    statType = quest.statType,
                    points = (current?.points ?: 0) + quest.tier.xp,
                    weeklyGain = (current?.weeklyGain ?: 0) + quest.tier.xp,
                    lastUpdated = DateUtils.today()
                )
            )

            // Update profile XP + leveling
            val ranks = Rank.values()
            var newXp     = profile.currentXp + xpEarned
            var newLevel  = profile.level
            var newRank   = profile.rank
            val xpNeeded  = newRank.xpPerLevel

            if (newXp >= xpNeeded) {
                newXp -= xpNeeded
                newLevel++
                if (newLevel > 10 && newRank.ordinal < ranks.size - 1) {
                    newRank   = ranks[newRank.ordinal + 1]
                    newLevel  = 1
                } else if (newLevel > 10) {
                    newLevel = 10
                }
            }

            val updatedProfile = profile.copy(
                currentXp        = newXp,
                totalXp          = profile.totalXp + xpEarned,
                level            = newLevel,
                rank             = newRank,
                totalQuestsDone  = profile.totalQuestsDone + 1,
                totalFocusMinutes= profile.totalFocusMinutes + focusMinutes,
                lastActiveDate   = DateUtils.today()
            )
            dao.saveUserProfile(updatedProfile)
            checkAndUnlockTitles(updatedProfile)
            checkStreak(updatedProfile)
        }
    }

    // ── Streak ────────────────────────────────────────────────────────────────
    private suspend fun checkStreak(profile: UserProfile) {
        val days = DateUtils.daysBetween(profile.lastActiveDate, DateUtils.today())
        val newStreak = when {
            days <= 1L -> profile.streak + 1
            else       -> 0
        }
        if (newStreak != profile.streak) {
            dao.saveUserProfile(profile.copy(streak = newStreak))
        }
    }

    // ── Penalty ───────────────────────────────────────────────────────────────
    fun checkPenalty() {
        viewModelScope.launch {
            val profile = dao.getUserProfileOnce() ?: return@launch
            val daysMissed = DateUtils.daysBetween(profile.lastActiveDate, DateUtils.today())
            if (daysMissed >= 3 && !profile.penaltyActive) {
                dao.saveUserProfile(profile.copy(
                    penaltyActive = true,
                    missedDays = daysMissed.toInt()
                ))
                dao.insertPenalty(
                    PenaltyLog(triggeredDate = DateUtils.today(), deadline = DateUtils.today())
                )
                // XP decay 10%
                val decayedXp = (profile.currentXp * 0.9).toInt()
                dao.saveUserProfile(profile.copy(currentXp = decayedXp, penaltyActive = true))
            }
        }
    }

    // ── Titles ────────────────────────────────────────────────────────────────
    private suspend fun checkAndUnlockTitles(profile: UserProfile) {
        TitleChecker.checkUnlocks(profile).forEach { titleDef ->
            dao.upsertTitle(
                Title(
                    id = titleDef.id, name = titleDef.name,
                    description = titleDef.description,
                    isUnlocked = true, unlockedDate = DateUtils.today()
                )
            )
        }
    }

    fun equipTitle(titleId: String) {
        viewModelScope.launch {
            dao.unequipAllTitles()
            dao.equipTitle(titleId)
            dao.getUserProfileOnce()?.let {
                dao.saveUserProfile(it.copy(activeTitle = titleId))
            }
        }
    }

    // ── Quest management ──────────────────────────────────────────────────────
    fun addQuest(quest: Quest) {
        viewModelScope.launch { dao.insertQuest(quest) }
    }

    fun deleteQuest(questId: Int) {
        viewModelScope.launch { dao.deleteQuest(questId) }
    }

    fun resetDailyQuests() {
        viewModelScope.launch { dao.resetDailyQuests() }
    }

    fun useGraceCard() {
        viewModelScope.launch {
            val p = dao.getUserProfileOnce() ?: return@launch
            if (p.graceCards > 0)
                dao.saveUserProfile(p.copy(graceCards = p.graceCards - 1, penaltyActive = false))
        }
    }

    fun useLifeCard() {
        viewModelScope.launch {
            val p = dao.getUserProfileOnce() ?: return@launch
            if (p.lifeCards > 0)
                dao.saveUserProfile(p.copy(lifeCards = p.lifeCards - 1, penaltyActive = false))
        }
    }
}