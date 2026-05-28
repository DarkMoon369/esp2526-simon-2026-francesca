package com.hfad.simonprototype

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GameResultAdapter
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fab = findViewById<FloatingActionButton>(R.id.fabNewGame)
        fab.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        dbHelper = DBHelper(this)
        loadGames()
    }

    override fun onResume() {
        super.onResume()
        loadGames()  // Ricarica la lista quando si torna dalla partita
    }

    private fun loadGames() {
        val games = dbHelper.getAllGames()
        android.util.Log.d("MainActivity", "Caricate ${games.size} partite")
        adapter = GameResultAdapter(games) { game ->
            val intent = Intent(this, MatchDetailActivity::class.java)
            intent.putExtra("game_id", game.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}