package com.hfad.simonprototype

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

    private val sequence = mutableListOf<Int>()   // sequenza del computer
    private var playerIndex = 0                   // posizione del giocatore nella sequenza
    private var isPlayerTurn = false              // indica se è il turno del giocatore
    private var isPaused = false
    private var isComputerPlaying = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val buttons = listOf(
            findViewById<Button>(R.id.btn1),
            findViewById<Button>(R.id.btn2),
            findViewById<Button>(R.id.btn3),
            findViewById<Button>(R.id.btn4),
            findViewById<Button>(R.id.btn5),
            findViewById<Button>(R.id.btn6)
        )

        val btnPause = findViewById<Button>(R.id.btnPause)
        val btnResume = findViewById<Button>(R.id.btnResume)
        val btnEnd = findViewById<Button>(R.id.btnEnd)
        val txtLog = findViewById<TextView>(R.id.txtLog)
        val btnStart = findViewById<Button>(R.id.btnStart)

        // container controls
        val controlsGrid = findViewById<GridLayout>(R.id.controlsGrid)

        // click sui 6 pulsanti colorati
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (isPlayerTurn) {
                    handlePlayerInput(index + 1, buttons)
                }
            }
        }

        // inizialmente disabilitati
        buttons.forEach { it.isEnabled = false }

        // start
        btnStart.setOnClickListener {
            startGame(buttons)
        }

        // pausa
        btnPause.setOnClickListener {
            isPaused = true
            disableButtons()
            txtLog.text = "Gioco in pausa"
            btnResume.isEnabled = true
        }

        // riprendi
        btnResume.setOnClickListener {
            isPaused = false
            txtLog.text = "Ripreso"
            btnResume.isEnabled = false

            if (isComputerPlaying) {
                computerTurn(buttons)
            } else {
                enableButtons()
            }
        }

        // fine
        btnEnd.setOnClickListener {
            isPaused = true
            disableButtons()
            txtLog.text = "Partita terminata"
        }

        ViewCompat.setOnApplyWindowInsetsListener(controlsGrid) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraDp = 8
            val extraPx = (extraDp * resources.displayMetrics.density).toInt()
            v.updatePadding(bottom = sysBars.bottom + extraPx)
            insets
        }
    }


    private fun addToSequence() {
        val next = (1..6).random()
        sequence.add(next)
    }

    @SuppressLint("SetTextI18n")
    private fun computerTurn(buttons: List<Button>) {
        isPlayerTurn = false
        isComputerPlaying = true

        val txtLog = findViewById<TextView>(R.id.txtLog)

        lifecycleScope.launch {
            // Log: inizio turno del computer
            txtLog.text = "Turno del computer: ${sequence.size} elementi"

            // Disattivo i pulsanti
            buttons.forEach { it.isEnabled = false }

            // Illumino la sequenza
            for (num in sequence) {
                if (isPaused) return@launch
                val button = buttons[num - 1]
                highlightButton(button)
            }
            isComputerPlaying = false

            // Log: ora tocca al giocatore
            txtLog.text = "Tocca a te! Ripeti ${sequence.size} pulsanti"

            // Reset dell’indice del giocatore
            playerIndex = 0
            isPlayerTurn = true

            // Attivo i pulsanti
            buttons.forEach { it.isEnabled = true }
        }
    }

    private fun startGame(buttons: List<Button>) {
        sequence.clear()
        playerIndex = 0
        isPlayerTurn = false

        addToSequence()
        computerTurn(buttons)
    }

    private suspend fun highlightButton(button: Button) {
        val originalColor = button.background
        button.alpha = 0.3f   // effetto “illuminato”
        delay(500)
        button.alpha = 1f
        delay(200)
    }

    private fun handlePlayerInput(choice: Int, buttons: List<Button>) {
        val txtLog = findViewById<TextView>(R.id.txtLog)

        if (choice == sequence[playerIndex]) {
            playerIndex++

            if (playerIndex == sequence.size) {
                isPlayerTurn = false
                buttons.forEach { it.isEnabled = false }

                // messaggio di feedback
                txtLog.text = "Ben fatto! Nuovo turno..."

                // Pausa di 1 secondo PRIMA del turno del computer
                lifecycleScope.launch {
                    delay(1000)
                    addToSequence()
                    computerTurn(buttons)
                }
            }

        } else {
            // ERRORE → partita finita
            isPlayerTurn = false
            buttons.forEach { it.isEnabled = false }
            txtLog.text = "Errore! Partita terminata."
        }
    }

    private fun disableButtons() {
        val buttons = listOf(
            findViewById<Button>(R.id.btn1),
            findViewById<Button>(R.id.btn2),
            findViewById<Button>(R.id.btn3),
            findViewById<Button>(R.id.btn4),
            findViewById<Button>(R.id.btn5),
            findViewById<Button>(R.id.btn6)
        )
        buttons.forEach { it.isEnabled = false }
    }

    private fun enableButtons() {
        val buttons = listOf(
            findViewById<Button>(R.id.btn1),
            findViewById<Button>(R.id.btn2),
            findViewById<Button>(R.id.btn3),
            findViewById<Button>(R.id.btn4),
            findViewById<Button>(R.id.btn5),
            findViewById<Button>(R.id.btn6)
        )
        buttons.forEach { it.isEnabled = true }
    }
}
