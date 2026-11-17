package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyRecordDao {
  @Query("SELECT * FROM daily_records WHERE dateKey = :dateKey LIMIT 1")
  fun observeRecordByDate(dateKey: String): Flow<DailyRecordEntity?>

  @Query("SELECT * FROM daily_records WHERE dateKey = :dateKey LIMIT 1")
  suspend fun getRecordByDate(dateKey: String): DailyRecordEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertRecord(record: DailyRecordEntity)

  @Query("DELETE FROM daily_records WHERE dateKey < :todayKey")
  suspend fun deleteRecordsBefore(todayKey: String)
}
