package com.hfad.simonprototype

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // view references
        val root = findViewById<View>(R.id.rootResults)
        val recycler = findViewById<RecyclerView>(R.id.resultsRecycler)
        val btnStart = findViewById<Button>(R.id.btnStartGame)

        // setup RecyclerView
        @Suppress("DEPRECATION")
        val results = intent.getSerializableExtra("results") as? ArrayList<GameResult> ?: arrayListOf()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ResultsAdapter(results)

        // click sul pulsante
        btnStart.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraDp = 8
            val extraPx = (extraDp * resources.displayMetrics.density).toInt()
            val baseMarginDp = 16
            val baseMarginPx = (baseMarginDp * resources.displayMetrics.density).toInt()

            // aggiorna bottomMargin del pulsante
            val lp = btnStart.layoutParams
            if (lp is androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) {
                lp.bottomMargin = sysBars.bottom + extraPx + baseMarginPx
                btnStart.layoutParams = lp
            }

            // aggiusta padding inferiore del RecyclerView per non nascondere contenuti
            recycler.updatePadding(bottom = sysBars.bottom + extraPx + baseMarginPx)

            v.requestLayout()
            insets
        }
        ViewCompat.requestApplyInsets(root)
    }
}