package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM question_progress")
    fun getAllProgress(): Flow<List<QuestionProgressEntity>>

    @Query("SELECT * FROM question_progress WHERE questionId = :questionId LIMIT 1")
    suspend fun getProgressById(questionId: Int): QuestionProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(entity: QuestionProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllProgress(entities: List<QuestionProgressEntity>)

    @Query("DELETE FROM question_progress")
    suspend fun clearAllProgress()

    @Query("UPDATE question_progress SET status = 'correct' WHERE status = 'wrong'")
    suspend fun markAllWrongAsCorrect()

    @Query("SELECT * FROM app_pref WHERE id = 1 LIMIT 1")
    fun getAppPref(): Flow<AppPrefEntity?>

    @Query("SELECT * FROM app_pref WHERE id = 1 LIMIT 1")
    suspend fun getAppPrefOnce(): AppPrefEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAppPref(pref: AppPrefEntity)
}

@Database(
    entities = [QuestionProgressEntity::class, AppPrefEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chinhtri300_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
