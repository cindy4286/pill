package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.HealthCheckRepository
import com.example.data.MealType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class HealthCheckUiState(
  val dateKey: String = currentDateKey(),
  val formattedDate: String = formatKoreanDate(),
  val dayOfWeek: String = formatKoreanDayOfWeek(),
  val morningTaken: Boolean = false,
  val lunchTaken: Boolean = false,
  val dinnerTaken: Boolean = false,
  val waterCups: Int = 0,
  val waterGoalCups: Int = 8,
) {
  val completedMedCount: Int
    get() = listOf(morningTaken, lunchTaken, dinnerTaken).count { it }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HealthCheckViewModel(
  private val repository: HealthCheckRepository,
  private val timeProvider: () -> Long = { System.currentTimeMillis() },
  private val enableMidnightTicker: Boolean = true,
) : ViewModel() {

  private val activeDateKey = MutableStateFlow(currentDateKey(Date(timeProvider())))

  val uiState: StateFlow<HealthCheckUiState> =
    activeDateKey
      .flatMapLatest { key ->
        repository.observeRecord(key).map { record ->
          val now = Date(timeProvider())
          HealthCheckUiState(
            dateKey = record.dateKey,
            formattedDate = formatKoreanDate(now),
            dayOfWeek = formatKoreanDayOfWeek(now),
            morningTaken = record.morningTaken,
            lunchTaken = record.lunchTaken,
            dinnerTaken = record.dinnerTaken,
            waterCups = record.waterCups,
          )
        }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue =
          HealthCheckUiState(
            dateKey = activeDateKey.value,
            formattedDate = formatKoreanDate(Date(timeProvider())),
            dayOfWeek = formatKoreanDayOfWeek(Date(timeProvider())),
          ),
      )

  init {
    viewModelScope.launch { repository.clearRecordsBefore(activeDateKey.value) }
    if (enableMidnightTicker) {
      startMidnightWatcher()
    }
  }

  private fun startMidnightWatcher() {
    viewModelScope.launch {
      while (isActive) {
        val waitMillis = millisUntilNextMidnight(timeProvider()) + 150L
        delay(waitMillis)
        refreshDateIfNeeded()
      }
    }
  }

  fun refreshDateIfNeeded() {
    val todayKey = currentDateKey(Date(timeProvider()))
    if (activeDateKey.value != todayKey) {
      activeDateKey.value = todayKey
      viewModelScope.launch { repository.clearRecordsBefore(todayKey) }
    }
  }

  fun onToggleMeal(mealType: MealType) {
    refreshDateIfNeeded()
    val key = activeDateKey.value
    viewModelScope.launch { repository.toggleMeal(key, mealType) }
  }

  fun onAddWaterCup() {
    refreshDateIfNeeded()
    val key = activeDateKey.value
    viewModelScope.launch { repository.incrementWater(key) }
  }

  fun onRemoveWaterCup() {
    refreshDateIfNeeded()
    val key = activeDateKey.value
    viewModelScope.launch { repository.decrementWater(key) }
  }

  class Factory(private val repository: HealthCheckRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(HealthCheckViewModel::class.java)) {
        return HealthCheckViewModel(repository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
  }
}

fun millisUntilNextMidnight(nowMillis: Long = System.currentTimeMillis()): Long {
  val nextMidnight =
    Calendar.getInstance().apply {
      timeInMillis = nowMillis
      add(Calendar.DAY_OF_YEAR, 1)
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
  return (nextMidnight.timeInMillis - nowMillis).coerceAtLeast(1_000L)
}

fun currentDateKey(date: Date = Date()): String =
  SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)

fun formatKoreanDate(date: Date = Date()): String =
  SimpleDateFormat("yyyy년 M월 d일", Locale.KOREAN).format(date)

fun formatKoreanDayOfWeek(date: Date = Date()): String =
  SimpleDateFormat("EEEE", Locale.KOREAN).format(date)
