package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MealType
import com.example.ui.components.MedicationCardButton
import com.example.ui.components.WaterGlassGraphic
import com.example.ui.theme.DinnerBadgeBg
import com.example.ui.theme.DinnerIconTint
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.IvoryBackground
import com.example.ui.theme.IvoryCardBorder
import com.example.ui.theme.IvoryCardSurface
import com.example.ui.theme.IvoryHeaderTint
import com.example.ui.theme.LunchBadgeBg
import com.example.ui.theme.LunchIconTint
import com.example.ui.theme.MedDoneBadgeBg
import com.example.ui.theme.MedDoneGreen
import com.example.ui.theme.MedDoneGreenDark
import com.example.ui.theme.MorningBadgeBg
import com.example.ui.theme.MorningIconTint
import com.example.ui.theme.SeniorCareTheme
import com.example.ui.theme.WaterCardBorder
import com.example.ui.theme.WaterCardSurface
import com.example.ui.theme.WaterPrimaryBlue

@Composable
fun HealthCheckScreen(
  uiState: HealthCheckUiState,
  onToggleMeal: (MealType) -> Unit,
  onAddWaterCup: () -> Unit,
  onRemoveWaterCup: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = IvoryBackground,
    contentWindowInsets = WindowInsets.safeDrawing,
  ) { innerPadding ->
    BoxWithConstraints(
      modifier =
        Modifier.fillMaxSize()
          .padding(innerPadding)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 20.dp),
      contentAlignment = Alignment.TopCenter,
    ) {
      val isWideLayout = maxWidth >= 720.dp

      Column(
        modifier = Modifier.fillMaxWidth().widthIn(max = if (isWideLayout) 960.dp else 600.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
      ) {
        // 1. Top Today Date Banner
        TodayDateHeader(
          formattedDate = uiState.formattedDate,
          dayOfWeek = uiState.dayOfWeek,
          completedMedCount = uiState.completedMedCount,
          waterCups = uiState.waterCups,
        )

        if (isWideLayout) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.Top,
          ) {
            MedicationSection(
              uiState = uiState,
              onToggleMeal = onToggleMeal,
              modifier = Modifier.weight(1f),
            )
            WaterIntakeSection(
              waterCups = uiState.waterCups,
              goalCups = uiState.waterGoalCups,
              onAddWaterCup = onAddWaterCup,
              onRemoveWaterCup = onRemoveWaterCup,
              modifier = Modifier.weight(1f),
            )
          }
        } else {
          // 2. Vertical Medication Buttons Section
          MedicationSection(uiState = uiState, onToggleMeal = onToggleMeal)

          // 3. Bottom Water Drinking Section
          WaterIntakeSection(
            waterCups = uiState.waterCups,
            goalCups = uiState.waterGoalCups,
            onAddWaterCup = onAddWaterCup,
            onRemoveWaterCup = onRemoveWaterCup,
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
      }
    }
  }
}

@Composable
private fun TodayDateHeader(
  formattedDate: String,
  dayOfWeek: String,
  completedMedCount: Int,
  waterCups: Int,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier.fillMaxWidth().testTag("today_date_header"),
    shape = RoundedCornerShape(26.dp),
    color = IvoryCardSurface,
    border = BorderStroke(2.dp, IvoryCardBorder),
    shadowElevation = 2.dp,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Box(
          modifier = Modifier.size(48.dp).clip(CircleShape).background(IvoryHeaderTint),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.CalendarToday,
            contentDescription = null,
            tint = InkPrimary,
            modifier = Modifier.size(26.dp),
          )
        }
        Text(
          text = formattedDate,
          style =
            MaterialTheme.typography.headlineLarge.copy(
              fontSize = 30.sp,
              fontWeight = FontWeight.Bold,
            ),
          color = InkPrimary,
          modifier = Modifier.testTag("today_date_text"),
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = IvoryHeaderTint,
        ) {
          Text(
            text = dayOfWeek,
            style =
              MaterialTheme.typography.labelMedium.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
              ),
            color = InkPrimary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          )
        }

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (completedMedCount == 3) MedDoneBadgeBg else IvoryHeaderTint,
        ) {
          Text(
            text = "약 $completedMedCount/3 · 물 ${waterCups}잔",
            style =
              MaterialTheme.typography.labelMedium.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
              ),
            color = if (completedMedCount == 3) MedDoneGreenDark else InkSecondary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun MedicationSection(
  uiState: HealthCheckUiState,
  onToggleMeal: (MealType) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(18.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.padding(horizontal = 4.dp),
    ) {
      Icon(
        imageVector = Icons.Rounded.Medication,
        contentDescription = null,
        tint = MedDoneGreen,
        modifier = Modifier.size(30.dp),
      )
      Text(
        text = stringResource(R.string.section_medication_title),
        style =
          MaterialTheme.typography.titleLarge.copy(
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
          ),
        color = InkPrimary,
      )
    }

    MedicationCardButton(
      title = stringResource(R.string.med_morning_title),
      hint = stringResource(R.string.med_morning_hint),
      isTaken = uiState.morningTaken,
      timeIcon = Icons.Rounded.WbTwilight,
      pendingBadgeBg = MorningBadgeBg,
      pendingIconTint = MorningIconTint,
      testTag = "med_button_morning",
      onClick = { onToggleMeal(MealType.MORNING) },
    )

    MedicationCardButton(
      title = stringResource(R.string.med_lunch_title),
      hint = stringResource(R.string.med_lunch_hint),
      isTaken = uiState.lunchTaken,
      timeIcon = Icons.Rounded.WbSunny,
      pendingBadgeBg = LunchBadgeBg,
      pendingIconTint = LunchIconTint,
      testTag = "med_button_lunch",
      onClick = { onToggleMeal(MealType.LUNCH) },
    )

    MedicationCardButton(
      title = stringResource(R.string.med_dinner_title),
      hint = stringResource(R.string.med_dinner_hint),
      isTaken = uiState.dinnerTaken,
      timeIcon = Icons.Rounded.NightsStay,
      pendingBadgeBg = DinnerBadgeBg,
      pendingIconTint = DinnerIconTint,
      testTag = "med_button_dinner",
      onClick = { onToggleMeal(MealType.DINNER) },
    )
  }
}

@Composable
private fun WaterIntakeSection(
  waterCups: Int,
  goalCups: Int,
  onAddWaterCup: () -> Unit,
  onRemoveWaterCup: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.padding(horizontal = 4.dp),
    ) {
      Icon(
        imageVector = Icons.Rounded.WaterDrop,
        contentDescription = null,
        tint = WaterPrimaryBlue,
        modifier = Modifier.size(30.dp),
      )
      Text(
        text = stringResource(R.string.section_water_title),
        style =
          MaterialTheme.typography.titleLarge.copy(
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
          ),
        color = InkPrimary,
      )
    }

    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(26.dp),
      color = WaterCardSurface,
      border = BorderStroke(2.dp, WaterCardBorder),
      shadowElevation = 2.dp,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        // Water cup visual + large numeric display
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
          WaterGlassGraphic(
            waterCups = waterCups,
            goalCups = goalCups,
            contentDescriptionText = stringResource(R.string.cd_water_glass),
            modifier = Modifier.testTag("water_glass_graphic"),
          )

          Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f),
          ) {
            Row(
              verticalAlignment = Alignment.Bottom,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Text(
                text = "$waterCups",
                style =
                  MaterialTheme.typography.displayLarge.copy(
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                  ),
                color = WaterPrimaryBlue,
                modifier = Modifier.testTag("water_cup_count_text"),
              )
              Text(
                text = stringResource(R.string.water_cup_unit),
                style =
                  MaterialTheme.typography.titleLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                  ),
                color = InkPrimary,
                modifier = Modifier.padding(bottom = 6.dp),
              )
            }

            Text(
              text =
                if (waterCups >= goalCups) stringResource(R.string.water_goal_achieved)
                else stringResource(R.string.water_goal_label),
              style =
                MaterialTheme.typography.bodyMedium.copy(
                  fontSize = 22.sp,
                  fontWeight = FontWeight.SemiBold,
                ),
              color = if (waterCups >= goalCups) MedDoneGreenDark else InkSecondary,
            )
          }
        }

        // Visual cup progress dots (8 large cup indicators)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          for (cupIndex in 1..goalCups) {
            val isFilled = cupIndex <= waterCups
            Box(
              modifier =
                Modifier.size(34.dp)
                  .clip(CircleShape)
                  .background(
                    if (isFilled) WaterPrimaryBlue else Color.White.copy(alpha = 0.9f)
                  ),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Rounded.LocalDrink,
                contentDescription = null,
                tint = if (isFilled) Color.White else WaterCardBorder,
                modifier = Modifier.size(20.dp),
              )
            }
          }
        }

        // Large primary "+1 cup" button
        Button(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onAddWaterCup()
          },
          shape = RoundedCornerShape(20.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = WaterPrimaryBlue,
              contentColor = Color.White,
            ),
          contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
          modifier =
            Modifier.fillMaxWidth()
              .minimumInteractiveComponentSize()
              .heightIn(min = 78.dp)
              .testTag("water_add_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.WaterDrop,
            contentDescription = null,
            modifier = Modifier.size(30.dp),
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = stringResource(R.string.water_add_button),
            style =
              MaterialTheme.typography.labelLarge.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
              ),
          )
        }

        // Gentle undo button when at least 1 cup has been recorded
        if (waterCups > 0) {
          OutlinedButton(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onRemoveWaterCup()
            },
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(2.dp, WaterCardBorder),
            colors =
              ButtonDefaults.outlinedButtonColors(
                containerColor = IvoryCardSurface,
                contentColor = InkSecondary,
              ),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            modifier =
              Modifier.fillMaxWidth()
                .minimumInteractiveComponentSize()
                .heightIn(min = 58.dp)
                .testTag("water_undo_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Remove,
              contentDescription = null,
              modifier = Modifier.size(26.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = stringResource(R.string.water_undo_button),
              style =
                MaterialTheme.typography.labelMedium.copy(
                  fontSize = 22.sp,
                  fontWeight = FontWeight.SemiBold,
                ),
            )
          }
        }
      }
    }
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun HealthCheckScreenPreview() {
  SeniorCareTheme {
    HealthCheckScreen(
      uiState =
        HealthCheckUiState(
          dateKey = "2026-11-16",
          formattedDate = "2026년 11월 16일",
          dayOfWeek = "월요일",
          morningTaken = true,
          lunchTaken = false,
          dinnerTaken = false,
          waterCups = 3,
        ),
      onToggleMeal = {},
      onAddWaterCup = {},
      onRemoveWaterCup = {},
    )
  }
}
