package com.hfad.simonprototype

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class GameResultAdapter(
    private val games: List<GameResult>,
    private val onItemClick: (GameResult) -> Unit
) : RecyclerView.Adapter<GameResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = games[position]
        holder.tvMaxCorrect.text = "${game.maxCorrect}"

        // Costruisce la stringa della sequenza (es. "R G C")
        val seqStrings = game.sequence.map { colorToLetter(it) }
        val fullSeq = seqStrings.joinToString(" ")

        // Evidenzia la parte dall'errore in poi
        val errorIndex = game.firstErrorIndex
        val spannable = SpannableString(fullSeq)
        if (errorIndex >= 0 && errorIndex < seqStrings.size) {
            var startPos = 0
            for (i in 0 until errorIndex) {
                startPos += seqStrings[i].length + 1 // +1 per lo spazio
            }
            val endPos = startPos + seqStrings[errorIndex].length
            val color = ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark)
            spannable.setSpan(ForegroundColorSpan(color), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        holder.tvSequence.text = spannable

        holder.itemView.setOnClickListener { onItemClick(game) }
    }

    override fun getItemCount() = games.size

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val tvMaxCorrect: TextView = view.findViewById(R.id.tvMaxCorrect)
        val tvSequence: TextView = view.findViewById(R.id.tvSequence)
    }

    private fun colorToLetter(color: Int): String = when (color) {
        1 -> "R"; 2 -> "G"; 3 -> "B"; 4 -> "Y"; 5 -> "M"; 6 -> "C"
        else -> "?"
    }
}