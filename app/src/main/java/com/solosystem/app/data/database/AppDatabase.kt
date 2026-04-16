package com.solosystem.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.solosystem.app.data.dao.AppDao
import com.solosystem.app.data.model.*

class Converters {
    @TypeConverter fun fromRank(r: Rank): String = r.name
    @TypeConverter fun toRank(s: String): Rank = Rank.valueOf(s)
    @TypeConverter fun fromTier(t: QuestTier): String = t.name
    @TypeConverter fun toTier(s: String): QuestTier = QuestTier.valueOf(s)
    @TypeConverter fun fromType(t: QuestType): String = t.name
    @TypeConverter fun toType(s: String): QuestType = QuestType.valueOf(s)
    @TypeConverter fun fromStat(s: StatType): String = s.name
    @TypeConverter fun toStat(s: String): StatType = StatType.valueOf(s)
}

@Database(
    entities = [UserProfile::class, Quest::class, StatPoints::class,
        Title::class, QuestLog::class, PenaltyLog::class],
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "solosystem.db")
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}