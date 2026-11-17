package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

enum class MealType {
  MORNING,
  LUNCH,
  DINNER,
}

data class DailyRecord(
  val dateKey: String,
  val morningTaken: Boolean = false,
  val lunchTaken: Boolean = false,
  val dinnerTaken: Boolean = false,
  val waterCups: Int = 0,
) {
  val completedMedCount: Int
    get() = listOf(morningTaken, lunchTaken, dinnerTaken).count { it }
}

class HealthCheckRepository(private val dao: DailyRecordDao) {

  private val writeMutex = Mutex()

  fun observeRecord(dateKey: String): Flow<DailyRecord> =
    dao.observeRecordByDate(dateKey).map { entity ->
      entity?.toDomain() ?: DailyRecord(dateKey = dateKey)
    }

  suspend fun toggleMeal(dateKey: String, mealType: MealType) {
    writeMutex.withLock {
      val current = dao.getRecordByDate(dateKey) ?: DailyRecordEntity(dateKey = dateKey)
      val updated =
        when (mealType) {
          MealType.MORNING -> current.copy(morningTaken = !current.morningTaken)
          MealType.LUNCH -> current.copy(lunchTaken = !current.lunchTaken)
          MealType.DINNER -> current.copy(dinnerTaken = !current.dinnerTaken)
        }.copy(updatedAtMillis = System.currentTimeMillis())
      dao.upsertRecord(updated)
    }
  }

  suspend fun incrementWater(dateKey: String) {
    writeMutex.withLock {
      val current = dao.getRecordByDate(dateKey) ?: DailyRecordEntity(dateKey = dateKey)
      val updated =
        current.copy(
          waterCups = (current.waterCups + 1).coerceAtMost(99),
          updatedAtMillis = System.currentTimeMillis(),
        )
      dao.upsertRecord(updated)
    }
  }

  suspend fun decrementWater(dateKey: String) {
    writeMutex.withLock {
      val current = dao.getRecordByDate(dateKey) ?: return
      if (current.waterCups <= 0) return
      val updated =
        current.copy(
          waterCups = (current.waterCups - 1).coerceAtLeast(0),
          updatedAtMillis = System.currentTimeMillis(),
        )
      dao.upsertRecord(updated)
    }
  }

  suspend fun clearRecordsBefore(todayKey: String) {
    writeMutex.withLock {
      dao.deleteRecordsBefore(todayKey)
    }
  }

  private fun DailyRecordEntity.toDomain(): DailyRecord =
    DailyRecord(
      dateKey = dateKey,
      morningTaken = morningTaken,
      lunchTaken = lunchTaken,
      dinnerTaken = dinnerTaken,
      waterCups = waterCups,
    )
}
