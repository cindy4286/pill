package com.example

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.HealthCheckRepository
import com.example.data.MealType
import com.example.ui.HealthCheckScreen
import com.example.ui.HealthCheckUiState
import com.example.ui.HealthCheckViewModel
import com.example.ui.millisUntilNextMidnight
import com.example.ui.theme.SeniorCareTheme
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `app name matches senior med and water check title`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("오늘 약·물 체크", appName)
  }

  @Test
  fun `today medication and water records persist across database reopen`() {
    runBlocking {
      val context = ApplicationProvider.getApplicationContext<Context>()
      val dbName = "test_senior_health_${System.currentTimeMillis()}.db"
      val dateKey = "2026-11-16"

      val firstDb =
        Room.databaseBuilder(context, AppDatabase::class.java, dbName)
          .allowMainThreadQueries()
          .build()
      val firstRepo = HealthCheckRepository(firstDb.dailyRecordDao())

      firstRepo.toggleMeal(dateKey, MealType.MORNING)
      firstRepo.toggleMeal(dateKey, MealType.DINNER)
      firstRepo.incrementWater(dateKey)
      firstRepo.incrementWater(dateKey)
      firstRepo.incrementWater(dateKey)
      firstRepo.decrementWater(dateKey)
      firstDb.close()

      val reopenedDb =
        Room.databaseBuilder(context, AppDatabase::class.java, dbName)
          .allowMainThreadQueries()
          .build()
      val reopenedRepo = HealthCheckRepository(reopenedDb.dailyRecordDao())
      val restored = reopenedRepo.observeRecord(dateKey).first()

      assertTrue(restored.morningTaken)
      assertFalse(restored.lunchTaken)
      assertTrue(restored.dinnerTaken)
      assertEquals(2, restored.waterCups)
      reopenedDb.close()
      context.deleteDatabase(dbName)
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `records automatically reset and clear yesterday data after midnight`() {
    runBlocking {
      val testDispatcher = kotlinx.coroutines.test.UnconfinedTestDispatcher()
      Dispatchers.setMain(testDispatcher)
      try {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db =
          Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.dailyRecordDao()
        val repo = HealthCheckRepository(dao)

        var fakeTimeMillis =
          Calendar.getInstance()
            .apply {
              set(2026, Calendar.NOVEMBER, 16, 23, 59, 50)
              set(Calendar.MILLISECOND, 0)
            }
            .timeInMillis

        assertEquals(10_000L, millisUntilNextMidnight(fakeTimeMillis))

        val viewModel =
          HealthCheckViewModel(
            repository = repo,
            timeProvider = { fakeTimeMillis },
            enableMidnightTicker = false,
          )
        val collectJob = launch { viewModel.uiState.collect {} }

        viewModel.onToggleMeal(MealType.MORNING)
        viewModel.onToggleMeal(MealType.LUNCH)
        viewModel.onAddWaterCup()
        viewModel.onAddWaterCup()

        val beforeMidnight =
          kotlinx.coroutines.withTimeout(5_000) {
            viewModel.uiState.first {
              it.dateKey == "2026-11-16" && it.morningTaken && it.lunchTaken && it.waterCups == 2
            }
          }
        assertTrue(beforeMidnight.morningTaken)
        assertTrue(beforeMidnight.lunchTaken)
        assertEquals(2, beforeMidnight.waterCups)

        // Advance clock past midnight to 2026-11-17 00:00:01
        fakeTimeMillis =
          Calendar.getInstance()
            .apply {
              set(2026, Calendar.NOVEMBER, 17, 0, 0, 1)
              set(Calendar.MILLISECOND, 0)
            }
            .timeInMillis

        viewModel.refreshDateIfNeeded()

        val nextDayState =
          kotlinx.coroutines.withTimeout(5_000) {
            viewModel.uiState.first {
              it.dateKey == "2026-11-17" &&
                !it.morningTaken &&
                !it.lunchTaken &&
                !it.dinnerTaken &&
                it.waterCups == 0
            }
          }
        assertEquals("2026년 11월 17일", nextDayState.formattedDate)

        // Yesterday's DB row should also be cleared
        assertNull(dao.getRecordByDate("2026-11-16"))

        collectJob.cancel()
        db.close()
      } finally {
        Dispatchers.resetMain()
      }
    }
  }

  @Test
  fun `health check screen toggles medication and updates water cup count`() {
    var uiState by
      mutableStateOf(
        HealthCheckUiState(
          dateKey = "2026-11-16",
          formattedDate = "2026년 11월 16일",
          dayOfWeek = "월요일",
        )
      )

    composeTestRule.setContent {
      SeniorCareTheme {
        HealthCheckScreen(
          uiState = uiState,
          onToggleMeal = { meal ->
            uiState =
              when (meal) {
                MealType.MORNING -> uiState.copy(morningTaken = !uiState.morningTaken)
                MealType.LUNCH -> uiState.copy(lunchTaken = !uiState.lunchTaken)
                MealType.DINNER -> uiState.copy(dinnerTaken = !uiState.dinnerTaken)
              }
          },
          onAddWaterCup = { uiState = uiState.copy(waterCups = uiState.waterCups + 1) },
          onRemoveWaterCup = {
            uiState = uiState.copy(waterCups = (uiState.waterCups - 1).coerceAtLeast(0))
          },
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("today_date_text").assertTextEquals("2026년 11월 16일")
    composeTestRule.onNodeWithTag("water_cup_count_text").assertTextEquals("0")

    composeTestRule.onNodeWithTag("med_button_morning").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("복용 완료").assertIsDisplayed()

    composeTestRule.onNodeWithTag("water_add_button").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("water_add_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("water_cup_count_text").performScrollTo().assertTextEquals("2")

    composeTestRule.onNodeWithTag("water_undo_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("water_cup_count_text").performScrollTo().assertTextEquals("1")
  }
}
