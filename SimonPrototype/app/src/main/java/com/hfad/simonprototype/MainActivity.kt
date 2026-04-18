package com.hfad.simonprototype

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var  sequenceText: TextView
    private val sequence = StringBuilder() // contiene la seuqenza corrente
    companion object {
        val resultsList = ArrayList<GameResult>()
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sequenceText = findViewById(R.id.sequenceText)

        //ripristino stato se presente
        if (savedInstanceState != null) {
            sequence.append(savedInstanceState.getString("currentSequence", ""))
            sequenceText.text = sequence.toString()
        }
        setupColorButtons()
        setupControlButtons()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentSequence", sequence.toString())
    }

    private fun setupColorButtons(){
        findViewById<Button>(R.id.btnRed).setOnClickListener {
            addToSequence("R")
        }
        findViewById<Button>(R.id.btnGreen).setOnClickListener {
            addToSequence("G")
        }
        findViewById<Button>(R.id.btnBlue).setOnClickListener {
            addToSequence("B")
        }
        findViewById<Button>(R.id.btnMagenta).setOnClickListener {
            addToSequence("M")
        }
        findViewById<Button>(R.id.btnYellow).setOnClickListener {
            addToSequence("Y")
        }
        findViewById<Button>(R.id.btnCyan).setOnClickListener {
            addToSequence("C")
        }
    }
    private fun addToSequence(letter:String){
        if (sequence.isNotEmpty()) sequence.append(",")
        sequence.append(letter)
        sequenceText.text = sequence.toString()
    }

    private fun setupControlButtons(){
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            sequence.clear()
            sequenceText.text = ""
        }
        findViewById<Button>(R.id.btnEnd).setOnClickListener {
            //salvo la sequenza corrente
            val result = GameResult(sequence.toString())
            resultsList.add(result)

            //pulisco la sequenza per la prossima partita
            sequence.clear()
            sequenceText.text = ""

            //apro la schermata 2
            val intent = Intent(this, ResultsActivity::class.java)
            intent.putExtra("results", MainActivity.resultsList)
            startActivity(intent)
        }
    }

}