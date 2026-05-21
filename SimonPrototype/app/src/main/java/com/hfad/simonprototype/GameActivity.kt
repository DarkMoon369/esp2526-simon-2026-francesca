package com.hfad.simonprototype

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.TextView


class GameActivity : AppCompatActivity() {

    private val sequence = mutableListOf<Int>()   // sequenza del computer
    private var playerIndex = 0                   // posizione del giocatore nella sequenza
    private var isPlayerTurn = false              // indica se è il turno del giocatore

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

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (isPlayerTurn) {
                    handlePlayerInput(index + 1, buttons)
                }
            }
        }

        buttons.forEach { it.isEnabled = false }
        val btnStart = findViewById<Button>(R.id.btnStart)
        btnStart.setOnClickListener {
            startGame(buttons)
        }
    }

    private fun addToSequence() {
        val next = (1..6).random()
        sequence.add(next)
    }

    private fun computerTurn(buttons: List<Button>) {
        isPlayerTurn = false

        val txtLog = findViewById<TextView>(R.id.txtLog)

        lifecycleScope.launch {

            // Log: inizio turno del computer
            txtLog.text = "Turno del computer: ${sequence.size} elementi"

            // Disattivo i pulsanti
            buttons.forEach { it.isEnabled = false }

            // Illumino la sequenza
            for (num in sequence) {
                val button = buttons[num - 1]
                highlightButton(button)
            }

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
            // corretto
            playerIndex++

            if (playerIndex == sequence.size) {
                // turno completato
                isPlayerTurn = false
                buttons.forEach { it.isEnabled = false }

                // messaggio di feedback
                txtLog.text = "Ben fatto! Nuovo turno..."

                // ⬇️ Pausa di 1 secondo PRIMA del turno del computer
                lifecycleScope.launch {
                    delay(1000)

                    // aggiungo un nuovo elemento alla sequenza
                    addToSequence()

                    // riparte il turno del computer
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



}
