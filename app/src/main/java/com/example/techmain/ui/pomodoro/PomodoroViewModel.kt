package com.example.techmain.ui.pomodoro

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.TechMainApp
import com.example.techmain.data.db.entity.PomodoroSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PomodoroState { IDLE, FOCUS, BREAK }

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as TechMainApp).pomodoroRepository
    private val shopRepo = (application as TechMainApp).shopRepository

    private val _state = MutableStateFlow(PomodoroState.IDLE)
    val state: StateFlow<PomodoroState> = _state.asStateFlow()

    private val _timerDisplay = MutableStateFlow("25:00")
    val timerDisplay: StateFlow<String> = _timerDisplay.asStateFlow()

    private val _progress = MutableStateFlow(1f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _sessionCount = MutableStateFlow(0)
    val sessionCount: StateFlow<Int> = _sessionCount.asStateFlow()

    private val _monstersDefeated = MutableStateFlow(0)
    val monstersDefeated: StateFlow<Int> = _monstersDefeated.asStateFlow()

    val totalFocusSessions = repo.getTotalFocusSessions()
    val totalFocusMinutes = repo.getTotalFocusMinutes()
    val totalMonstersDefeated = repo.getTotalMonstersDefeated()

    private var timer: CountDownTimer? = null
    private var focusDuration = 25 * 60 * 1000L
    private var breakDuration = 5 * 60 * 1000L

    fun setFocusDuration(minutes: Int) {
        focusDuration = minutes * 60 * 1000L
        if (_state.value == PomodoroState.IDLE) {
            _timerDisplay.value = formatTime(focusDuration)
        }
    }

    fun startFocus() {
        _state.value = PomodoroState.FOCUS
        startTimer(focusDuration)
    }

    fun skipToBreak() {
        timer?.cancel()
        onTimerFinish()
    }

    private fun startTimer(duration: Long) {
        _progress.value = 1f
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timerDisplay.value = formatTime(millisUntilFinished)
                _progress.value = millisUntilFinished.toFloat() / duration.toFloat()
            }

            override fun onFinish() {
                onTimerFinish()
            }
        }.start()
    }

    private fun onTimerFinish() {
        when (_state.value) {
            PomodoroState.FOCUS -> {
                val newCount = _sessionCount.value + 1
                _sessionCount.value = newCount
                val monsterDefeated = if (newCount % 2 == 0) {
                    _monstersDefeated.value += 1
                    true
                } else false

                _state.value = PomodoroState.BREAK
                _progress.value = 1f
                _timerDisplay.value = formatTime(breakDuration)
                startTimer(breakDuration)

                viewModelScope.launch {
                    repo.addSession(
                        PomodoroSession(
                            focusSessionsCompleted = 1,
                            totalFocusMinutes = focusDuration.toInt() / 60000,
                            monstersDefeated = if (monsterDefeated) 1 else 0
                        )
                    )
                    shopRepo.addRewards(xp = 10, gold = 5)
                }
            }
            PomodoroState.BREAK -> {
                _state.value = PomodoroState.IDLE
                _timerDisplay.value = formatTime(focusDuration)
                _progress.value = 1f
            }
            PomodoroState.IDLE -> {}
        }
    }

    fun reset() {
        timer?.cancel()
        _state.value = PomodoroState.IDLE
        _timerDisplay.value = formatTime(focusDuration)
        _progress.value = 1f
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    private fun formatTime(ms: Long): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return "%02d:%02d".format(min, sec)
    }
}
