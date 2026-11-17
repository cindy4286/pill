package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.HealthCheckRepository
import com.example.ui.HealthCheckScreen
import com.example.ui.HealthCheckViewModel
import com.example.ui.theme.SeniorCareTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
    )

    val database = AppDatabase.getInstance(applicationContext)
    val repository = HealthCheckRepository(database.dailyRecordDao())

    setContent {
      SeniorCareTheme {
        val viewModel: HealthCheckViewModel =
          viewModel(factory = HealthCheckViewModel.Factory(repository))
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
          viewModel.refreshDateIfNeeded()
        }

        DisposableEffect(context, viewModel) {
          val dateChangeReceiver =
            object : BroadcastReceiver() {
              override fun onReceive(ctx: Context?, intent: Intent?) {
                viewModel.refreshDateIfNeeded()
              }
            }
          val filter =
            IntentFilter().apply {
              addAction(Intent.ACTION_DATE_CHANGED)
              addAction(Intent.ACTION_TIME_CHANGED)
              addAction(Intent.ACTION_TIMEZONE_CHANGED)
            }
          ContextCompat.registerReceiver(
            context,
            dateChangeReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
          )
          onDispose { context.unregisterReceiver(dateChangeReceiver) }
        }

        HealthCheckScreen(
          uiState = uiState,
          onToggleMeal = viewModel::onToggleMeal,
          onAddWaterCup = viewModel::onAddWaterCup,
          onRemoveWaterCup = viewModel::onRemoveWaterCup,
        )
      }
    }
  }
}
