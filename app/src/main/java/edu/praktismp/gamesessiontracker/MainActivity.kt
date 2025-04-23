package edu.praktismp.gamesessiontracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import edu.praktismp.gamesessiontracker.ui.AddSessionActivity
import edu.praktismp.gamesessiontracker.ui.HistoryActivity
import edu.praktismp.gamesessiontracker.ui.StatsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAdd = findViewById<Button>(R.id.btnAddSession)
        val btnHistory = findViewById<Button>(R.id.btnHistory)
        val btnStats = findViewById<Button>(R.id.btnStats)

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddSessionActivity::class.java))
        }

        btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        btnStats.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }
    }
}
