package com.solosystem.app.data.dao

import androidx.room.*
import com.solosystem.app.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // ── UserProfile ──────────────────────────────────────────────────────────
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    // ── Quests ───────────────────────────────────────────────────────────────
    @Query("SELECT * FROM quests WHERE isActive = 1 ORDER BY tier DESC")
    fun getAllActiveQuests(): Flow<List<Quest>>

    @Query("SELECT * FROM quests WHERE type = 'DAILY' AND isActive = 1")
    fun getDailyQuests(): Flow<List<Quest>>

    @Query("SELECT * FROM quests WHERE type = 'WEEKLY' AND isActive = 1")
    fun getWeeklyQuests(): Flow<List<Quest>>

    @Query("SELECT * FROM quests WHERE isCompleted = 0 AND isActive = 1")
    fun getIncompleteQuests(): Flow<List<Quest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: Quest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<Quest>)

    @Update
    suspend fun updateQuest(quest: Quest)

    @Query("DELETE FROM quests WHERE id = :questId")
    suspend fun deleteQuest(questId: Int)

    @Query("UPDATE quests SET isCompleted = 0, completedDate = '' WHERE type = 'DAILY'")
    suspend fun resetDailyQuests()

    @Query("UPDATE quests SET isCompleted = 0, completedDate = '' WHERE type = 'WEEKLY'")
    suspend fun resetWeeklyQuests()

    // ── Stats ─────────────────────────────────────────────────────────────────
    @Query("SELECT * FROM stat_points")
    fun getAllStats(): Flow<List<StatPoints>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStat(stat: StatPoints)

    @Query("SELECT * FROM stat_points WHERE statType = :type")
    suspend fun getStatByType(type: StatType): StatPoints?

    // ── Titles ────────────────────────────────────────────────────────────────
    @Query("SELECT * FROM titles")
    fun getAllTitles(): Flow<List<Title>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTitle(title: Title)

    @Query("UPDATE titles SET isEquipped = 0")
    suspend fun unequipAllTitles()

    @Query("UPDATE titles SET isEquipped = 1 WHERE id = :titleId")
    suspend fun equipTitle(titleId: String)

    // ── Quest Log ─────────────────────────────────────────────────────────────
    @Query("SELECT * FROM quest_log ORDER BY completedDate DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<QuestLog>>

    @Insert
    suspend fun insertLog(log: QuestLog)

    @Query("SELECT COUNT(*) FROM quest_log WHERE completedDate = :date")
    suspend fun getQuestsCompletedOnDate(date: String): Int

    // ── Penalty ───────────────────────────────────────────────────────────────
    @Insert
    suspend fun insertPenalty(penalty: PenaltyLog)

    @Query("SELECT * FROM penalty_log ORDER BY triggeredDate DESC LIMIT 10")
    fun getPenalties(): Flow<List<PenaltyLog>>

    @Query("UPDATE penalty_log SET isCompleted = 1 WHERE id = :id")
    suspend fun completePenalty(id: Int)
}