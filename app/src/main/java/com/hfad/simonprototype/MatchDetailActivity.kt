package com.hfad.simonprototype

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MatchDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_detail)

        val gameId = intent.getLongExtra("game_id", -1)
        val dbHelper = DBHelper(this)
        val games = dbHelper.getAllGames()
        val game = games.find { it.id == gameId }

        val textDetails = findViewById<TextView>(R.id.textDetails)
        if (game != null) {
            val seqStr = game.sequence.joinToString(" → ") { colorToLetter(it) }
            val errorIndex = game.firstErrorIndex
            val sb = StringBuilder()
            sb.append("Max correct: ${game.maxCorrect}\n\n")
            sb.append("Full sequence:\n")
            if (errorIndex >= 0) {
                // Colora la parte dopo l'errore (semplice indicazione testuale)
                val before = game.sequence.take(errorIndex).joinToString(" ")
                val error = colorToLetter(game.sequence[errorIndex])
                val after = game.sequence.drop(errorIndex + 1).joinToString(" ")
                sb.append("$before [$error] $after")
                sb.append("\n\nError at position ${errorIndex + 1}")
            } else {
                sb.append(seqStr)
            }
            textDetails.text = sb.toString()
        } else {
            textDetails.text = getString(R.string.game_not_found)
        }
        dbHelper.close()
    }

    private fun colorToLetter(color: Int): String = when (color) {
        1 -> "R"; 2 -> "G"; 3 -> "B"; 4 -> "Y"; 5 -> "M"; 6 -> "C"
        else -> "?"
    }
}