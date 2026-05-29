package com.hfad.simonprototype

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

class GameActivity : AppCompatActivity() {

    // Stato del gioco
    private val computerSequence = mutableListOf<Int>()
    private var playerIndex = 0
    private var isPlayerTurn = false
    private var isPaused = false
    private var isComputerPlaying = false
    private var gameActive = false
    private var currentMaxCorrect = 0
    private val fullSequencePlayed = mutableListOf<Int>()
    private var errorPosition = -1
    private var isFirstSequencePresentation = false
    private var currentPlaybackIndex = 0
    private var audioTrack: AudioTrack? = null

    // UI
    private lateinit var buttons: List<Button>
    private lateinit var txtLog: TextView
    private lateinit var txtPlayerSequence: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        buttons = listOf(
            findViewById(R.id.btn1),
            findViewById(R.id.btn2),
            findViewById(R.id.btn3),
            findViewById(R.id.btn4),
            findViewById(R.id.btn5),
            findViewById(R.id.btn6)
        )
        // Inizializza le proprietà della classe (lateinit)
        btnStart = findViewById(R.id.btnStart)
        btnPause = findViewById(R.id.btnPause)
        val btnResume = findViewById<Button>(R.id.btnResume)
        val btnEnd = findViewById<Button>(R.id.btnEnd)
        txtLog = findViewById(R.id.txtLog)
        txtPlayerSequence = findViewById(R.id.txtPlayerSequence)
        val controlsGrid = findViewById<android.widget.GridLayout>(R.id.controlsGrid)

        // Accessibility
        buttons[0].contentDescription = getString(R.string.desc_red)
        buttons[1].contentDescription = getString(R.string.desc_green)
        buttons[2].contentDescription = getString(R.string.desc_blue)
        buttons[3].contentDescription = getString(R.string.desc_yellow)
        buttons[4].contentDescription = getString(R.string.desc_magenta)
        buttons[5].contentDescription = getString(R.string.desc_cyan)

        // Stato iniziale dei pulsanti di controllo
        btnStart.isEnabled = true
        btnPause.isEnabled = false
        btnResume.isEnabled = false
        buttons.forEach { it.isEnabled = false }
        txtPlayerSequence.text = ""

        // Click dei tasti colore
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (isPlayerTurn && !isPaused) {
                    handlePlayerInput(index + 1)
                }
            }
        }

        btnStart.setOnClickListener { startGame() }
        btnPause.setOnClickListener { pauseGame() }
        btnResume.setOnClickListener { resumeGame() }
        btnEnd.setOnClickListener { endGame() }



        // Tasto Back
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveAndExit()
            }
        })

        // Window insets per il padding (evita che i pulsanti finiscano sotto la barra di sistema)
        ViewCompat.setOnApplyWindowInsetsListener(controlsGrid) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraDp = 8
            val extraPx = (extraDp * resources.displayMetrics.density).toInt()
            v.updatePadding(bottom = sysBars.bottom + extraPx)
            insets
        }
        ViewCompat.requestApplyInsets(controlsGrid)

        savedInstanceState?.let {
            restoreGameState(it)
        }
    }

    private fun startGame() {
        computerSequence.clear()
        fullSequencePlayed.clear()
        playerIndex = 0
        isPlayerTurn = false
        gameActive = true
        currentMaxCorrect = 0
        errorPosition = -1
        isFirstSequencePresentation = false
        txtPlayerSequence.text = ""
        btnStart.isEnabled = false
        addToSequence()
        computerTurn()
    }

    private fun addToSequence() {
        val next = (1..6).random()
        computerSequence.add(next)
        fullSequencePlayed.add(next)
    }

    private fun computerTurn(startIndex: Int = 0) {
        isPlayerTurn = false
        isComputerPlaying = true
        isFirstSequencePresentation = (computerSequence.size == 1)

        lifecycleScope.launch {
            txtLog.text = getString(R.string.computer_turn, computerSequence.size)
            disableColorButtons()
            btnPause.isEnabled = true

            for (i in startIndex until computerSequence.size) {
                if (isPaused) return@launch
                currentPlaybackIndex = i   // salva indice corrente
                val button = buttons[computerSequence[i] - 1]
                highlightButton(button)
                playSound(computerSequence[i])
            }
            isComputerPlaying = false
            isFirstSequencePresentation = false
            btnPause.isEnabled = false
            currentPlaybackIndex = 0        // reset indice

            txtLog.text = getString(R.string.player_turn, computerSequence.size)
            playerIndex = 0
            txtPlayerSequence.text = ""
            isPlayerTurn = true
            enableColorButtons()
        }
    }

    private suspend fun highlightButton(button: Button) {
        val originalAlpha = button.alpha
        button.alpha = 0.3f
        delay(500)
        button.alpha = originalAlpha
        delay(200)
    }

    private fun handlePlayerInput(choice: Int) {
        playSound(choice)
        val current = txtPlayerSequence.text.toString()
        val newSeq = if (current.isEmpty()) colorToLetter(choice) else "$current, ${colorToLetter(choice)}"
        txtPlayerSequence.text = newSeq

        if (choice == computerSequence[playerIndex]) {
            playerIndex++
            if (playerIndex == computerSequence.size) {
                isPlayerTurn = false
                disableColorButtons()
                txtLog.text = getString(R.string.well_done)
                currentMaxCorrect = computerSequence.size
                lifecycleScope.launch {
                    delay(1000)
                    addToSequence()
                    computerTurn()
                }
            }
        } else {
            isPlayerTurn = false
            gameActive = false
            fullSequencePlayed.add(choice)
            errorPosition = fullSequencePlayed.lastIndex   // indice dell'errore (ultimo elemento)
            disableColorButtons()
            txtLog.text = getString(R.string.error_end)
        }
    }

    private fun endGame() {
        saveAndExit()
    }

    private fun pauseGame() {
        if (isComputerPlaying && !isPaused) {
            isPaused = true
            disableColorButtons()
            txtLog.text = getString(R.string.paused)
            findViewById<Button>(R.id.btnResume).isEnabled = true
            btnPause.isEnabled = false
        }
    }

    private fun resumeGame() {
        if (isPaused) {
            isPaused = false
            txtLog.text = getString(R.string.resumed)
            findViewById<Button>(R.id.btnResume).isEnabled = false
            if (isComputerPlaying) {
                btnPause.isEnabled = true
                computerTurn(currentPlaybackIndex)
            } else {
                enableColorButtons()
            }
        }
    }

    private fun saveAndExit() {
        if (!isFirstSequencePresentation && computerSequence.isNotEmpty()) {
            if (errorPosition == -1) {
                val nextExpected = computerSequence.getOrNull(playerIndex) ?: computerSequence.last()
                fullSequencePlayed.add(nextExpected)
                errorPosition = playerIndex
            }
            saveGameToDatabase()
        }
        btnStart.isEnabled = true
        finish()
    }

    private fun saveGameToDatabase() {
        val dbHelper = DBHelper(this)
        val result = GameResult(
            maxCorrect = currentMaxCorrect,
            sequence = ArrayList(fullSequencePlayed),
            firstErrorIndex = errorPosition,
            timestamp = System.currentTimeMillis()
        )
        dbHelper.insertGame(result)
        dbHelper.close()
    }

    private fun disableColorButtons() {
        buttons.forEach { it.isEnabled = false }
    }

    private fun enableColorButtons() {
        if (isPlayerTurn && !isPaused) {
            buttons.forEach { it.isEnabled = true }
        }
    }

    private fun colorToLetter(color: Int): String = when (color) {
        1 -> "R"
        2 -> "G"
        3 -> "B"
        4 -> "Y"
        5 -> "M"
        6 -> "C"
        else -> ""
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("computerSequence", ArrayList(computerSequence))
        outState.putInt("playerIndex", playerIndex)
        outState.putBoolean("isPlayerTurn", isPlayerTurn)
        outState.putBoolean("isPaused", isPaused)
        outState.putBoolean("isComputerPlaying", isComputerPlaying)
        outState.putBoolean("gameActive", gameActive)
        outState.putInt("currentMaxCorrect", currentMaxCorrect)
        outState.putIntegerArrayList("fullSequencePlayed", ArrayList(fullSequencePlayed))
        outState.putInt("errorPosition", errorPosition)
        outState.putBoolean("isFirstSequencePresentation", isFirstSequencePresentation)
        outState.putString("txtPlayerSequence", txtPlayerSequence.text.toString())
        outState.putString("txtLog", txtLog.text.toString())
        outState.putInt("currentPlaybackIndex", currentPlaybackIndex)
    }

    private fun restoreGameState(savedInstanceState: Bundle) {
        savedInstanceState.getIntegerArrayList("computerSequence")?.let {
            computerSequence.clear()
            computerSequence.addAll(it)
        }
        playerIndex = savedInstanceState.getInt("playerIndex")
        isPlayerTurn = savedInstanceState.getBoolean("isPlayerTurn")
        isPaused = savedInstanceState.getBoolean("isPaused")
        isComputerPlaying = savedInstanceState.getBoolean("isComputerPlaying")
        gameActive = savedInstanceState.getBoolean("gameActive")
        currentMaxCorrect = savedInstanceState.getInt("currentMaxCorrect")
        savedInstanceState.getIntegerArrayList("fullSequencePlayed")?.let {
            fullSequencePlayed.clear()
            fullSequencePlayed.addAll(it)
        }
        errorPosition = savedInstanceState.getInt("errorPosition")
        isFirstSequencePresentation = savedInstanceState.getBoolean("isFirstSequencePresentation")
        txtPlayerSequence.text = savedInstanceState.getString("txtPlayerSequence", "")
        txtLog.text = savedInstanceState.getString("txtLog", "")

        currentPlaybackIndex = savedInstanceState.getInt("currentPlaybackIndex", 0)

        if (gameActive) {
            if (isComputerPlaying && !isPaused) {
                if (computerSequence.isNotEmpty()) {
                    computerTurn(currentPlaybackIndex)
                }
            } else if (isPlayerTurn && !isPaused) {
                enableColorButtons()
            }
        }
    }
    private fun playTone(freq: Int, durationMs: Int = 150) {
        val sampleRate = 44100
        val durationSamples = (sampleRate * durationMs / 1000.0).toInt()
        val buffer = ShortArray(durationSamples)
        for (i in 0 until durationSamples) {
            val angle = 2.0 * Math.PI * i * freq / sampleRate
            buffer[i] = (Math.sin(angle) * 32767).toInt().toShort()
        }
        // Rilascia il precedente se esiste
        audioTrack?.release()
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .build()
        audioTrack?.play()
        audioTrack?.write(buffer, 0, buffer.size)
    }

    private fun playSound(color: Int) {
        val frequencies = listOf(261, 330, 392, 523, 659, 784)
        val freq = frequencies.getOrNull(color - 1) ?: 261
        playTone(freq, 150)
    }

    override fun onDestroy() {
        audioTrack?.release()
        super.onDestroy()
    }
}