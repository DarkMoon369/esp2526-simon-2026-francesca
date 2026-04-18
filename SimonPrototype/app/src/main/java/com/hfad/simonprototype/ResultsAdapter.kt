package com.hfad.simonprototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultsAdapter(private val results: List<GameResult>) :
    RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>() {

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countText: TextView = itemView.findViewById(R.id.itemCount)
        val sequenceText: TextView = itemView.findViewById(R.id.itemSequence)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val item = results[position]

        // numero di elementi premuti
        val count = item.sequence.split(",").size
        holder.countText.text = count.toString()

        // sequenza troncata se troppo lunga
        val seq = if (item.sequence.length > 25)
            item.sequence.take(25) + "..."
        else
            item.sequence

        holder.sequenceText.text = seq
    }

    override fun getItemCount(): Int = results.size
}
