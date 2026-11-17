package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.IvoryCardBorder
import com.example.ui.theme.IvoryCardSurface
import com.example.ui.theme.MedDoneGreen
import com.example.ui.theme.MedDoneGreenDark

@Composable
fun MedicationCardButton(
  title: String,
  hint: String,
  isTaken: Boolean,
  timeIcon: ImageVector,
  pendingBadgeBg: Color,
  pendingIconTint: Color,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current

  val containerColor by
    animateColorAsState(
      targetValue = if (isTaken) MedDoneGreen else IvoryCardSurface,
      animationSpec = tween(durationMillis = 220),
      label = "medCardContainer",
    )

  val borderColor by
    animateColorAsState(
      targetValue = if (isTaken) MedDoneGreenDark else IvoryCardBorder,
      animationSpec = tween(durationMillis = 220),
      label = "medCardBorder",
    )

  val titleColor = if (isTaken) Color.White else InkPrimary
  val subtitleColor = if (isTaken) Color.White.copy(alpha = 0.92f) else InkSecondary
  val statusText =
    if (isTaken) stringResource(R.string.status_taken)
    else stringResource(R.string.status_pending)

  Surface(
    onClick = {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
      onClick()
    },
    shape = RoundedCornerShape(24.dp),
    color = containerColor,
    border = BorderStroke(width = 2.dp, color = borderColor),
    shadowElevation = if (isTaken) 4.dp else 2.dp,
    modifier =
      modifier
        .fillMaxWidth()
        .minimumInteractiveComponentSize()
        .heightIn(min = 98.dp)
        .testTag(testTag)
        .semantics {
          role = Role.Checkbox
          stateDescription = statusText
        },
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f),
      ) {
        Box(
          modifier =
            Modifier.size(56.dp)
              .clip(CircleShape)
              .background(
                if (isTaken) Color.White.copy(alpha = 0.20f) else pendingBadgeBg
              ),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = timeIcon,
            contentDescription = null,
            tint = if (isTaken) Color.White else pendingIconTint,
            modifier = Modifier.size(32.dp),
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = title,
            style =
              MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
              ),
            color = titleColor,
          )
          Text(
            text = hint,
            style =
              MaterialTheme.typography.bodyMedium.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
              ),
            color = subtitleColor,
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Prominent right-side checkmark / circle indicator
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier =
          Modifier.clip(RoundedCornerShape(16.dp))
            .background(
              if (isTaken) Color.White.copy(alpha = 0.18f) else Color.Transparent
            )
            .padding(horizontal = if (isTaken) 12.dp else 4.dp, vertical = 8.dp),
      ) {
        if (isTaken) {
          Text(
            text = statusText,
            style =
              MaterialTheme.typography.labelMedium.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
              ),
            color = Color.White,
          )
        }
        Icon(
          imageVector =
            if (isTaken) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
          contentDescription =
            if (isTaken) stringResource(R.string.cd_med_done)
            else stringResource(R.string.cd_med_pending),
          tint = if (isTaken) Color.White else IvoryCardBorder,
          modifier = Modifier.size(40.dp),
        )
      }
    }
  }
}
