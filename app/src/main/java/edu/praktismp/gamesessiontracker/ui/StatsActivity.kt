package edu.praktismp.gamesessiontracker.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.praktismp.gamesessiontracker.R
import edu.praktismp.gamesessiontracker.data.SessionDatabaseHelper

class StatsActivity : AppCompatActivity() {
    private lateinit var totalDurationText: TextView
    private lateinit var averageDurationText: TextView
    private lateinit var sessionCountText: TextView
    private lateinit var dbHelper: SessionDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        totalDurationText = findViewById(R.id.totalDurationText)
        averageDurationText = findViewById(R.id.averageDurationText)
        sessionCountText = findViewById(R.id.sessionCountText)
        dbHelper = SessionDatabaseHelper(this)

        showStats()
    }

    private fun showStats() {
        val total = dbHelper.getTotalDuration()
        val average = dbHelper.getAverageDuration()
        val count = dbHelper.getSessionCount()

        totalDurationText.text = "Total Durasi: $total menit"
        averageDurationText.text = "Rata-rata Durasi: $average menit"
        sessionCountText.text = "Jumlah Sesi: $count"
    }
}
