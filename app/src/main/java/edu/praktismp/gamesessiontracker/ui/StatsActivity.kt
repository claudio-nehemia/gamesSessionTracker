package edu.praktismp.gamesessiontracker.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.praktismp.gamesessiontracker.R
import edu.praktismp.gamesessiontracker.data.SessionDatabaseHelper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class StatsActivity : AppCompatActivity() {

    private lateinit var totalDurationText: TextView
    private lateinit var averageDurationText: TextView
    private lateinit var sessionCountText: TextView
    private lateinit var lineChart: LineChart
    private lateinit var dbHelper: SessionDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        totalDurationText = findViewById(R.id.totalDurationText)
        averageDurationText = findViewById(R.id.averageDurationText)
        sessionCountText = findViewById(R.id.sessionCountText)
        lineChart = findViewById(R.id.lineChart)
        dbHelper = SessionDatabaseHelper(this)

        showStats()
        setupLineChart()
    }

    private fun showStats() {
        val total = dbHelper.getTotalDuration()
        val average = dbHelper.getAverageDuration()
        val count = dbHelper.getSessionCount()

        totalDurationText.text = "Total Durasi: $total menit"
        averageDurationText.text = "Rata-rata Durasi: $average menit"
        sessionCountText.text = "Jumlah Sesi: $count"
    }

    private fun setupLineChart() {
        val total = dbHelper.getTotalDuration()
        val average = dbHelper.getAverageDuration()
        val count = dbHelper.getSessionCount()

        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, total.toFloat()))
        entries.add(Entry(1f, average.toFloat()))
        entries.add(Entry(2f, count.toFloat()))

        val dataSet = LineDataSet(entries, "Statistik")
        dataSet.color = resources.getColor(R.color.purple_500, theme)
        dataSet.valueTextColor = resources.getColor(R.color.black, theme)
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 5f
        dataSet.setCircleColor(resources.getColor(R.color.purple_500, theme))

        val lineData = LineData(dataSet)

        lineChart.data = lineData
        lineChart.description.isEnabled = false

        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Total", "Rata2", "Jumlah"))
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisRight.isEnabled = false
        lineChart.invalidate() // Refresh chart
    }
}
