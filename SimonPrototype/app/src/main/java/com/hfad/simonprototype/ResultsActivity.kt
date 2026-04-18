package com.hfad.simonprototype

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import kotlin.collections.arrayListOf
import androidx.recyclerview.widget.LinearLayoutManager


class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        //recupero RecyclerView
        val recycler = findViewById<RecyclerView>(R.id.resultsRecycler)

        //recupero la lista passata dalla MainActivity
        @Suppress("DEPRECATION")
        val results = intent.getSerializableExtra("results") as? ArrayList<GameResult> ?: arrayListOf()

        //imposto layout manager
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ResultsAdapter(results)
    }
}