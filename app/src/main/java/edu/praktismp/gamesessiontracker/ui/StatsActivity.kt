package edu.praktismp.gamesessiontracker.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var gameSpinner: Spinner
    private lateinit var timeRangeSpinner: Spinner
    private lateinit var btnApplyFilter: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        totalDurationText = findViewById(R.id.totalDurationText)
        averageDurationText = findViewById(R.id.averageDurationText)
        sessionCountText = findViewById(R.id.sessionCountText)
        lineChart = findViewById(R.id.lineChart)
        dbHelper = SessionDatabaseHelper(this)
        gameSpinner = findViewById(R.id.gameSpinner)
        timeRangeSpinner = findViewById(R.id.timeRangeSpinner)
        btnApplyFilter = findViewById(R.id.btnApplyFilter)

        setupSpinners()
        setupChartWithFilters()
        showStats()
        //setupLineChart()
    }


    private fun setupSpinners() {
        // Setup game spinner
        val games = dbHelper.getGameNames().toMutableList()
        games.add(0, "All Games")
        val gameAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, games)
        gameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gameSpinner.adapter = gameAdapter

        // Setup time range spinner
        val timeRanges = arrayOf("Per Jam", "Per Hari", "Per Minggu")
        val timeRangeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeRanges)
        timeRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeRangeSpinner.adapter = timeRangeAdapter

        // Set default selection
        timeRangeSpinner.setSelection(1) // Default: Per Hari
    }

    private fun setupChartWithFilters() {
        // Button untuk menerapkan filter
        val btnApply = findViewById<Button>(R.id.btnApplyFilter)
        btnApply.setOnClickListener {
            updateChartWithFilters()
        }
        updateChartWithFilters() // Load initial data
    }

    private fun updateChartWithFilters() {
        val selectedGame = if (gameSpinner.selectedItemPosition == 0) null else gameSpinner.selectedItem.toString()
        val selectedTimeRange = when (timeRangeSpinner.selectedItemPosition) {
            0 -> SessionDatabaseHelper.TIME_RANGE_HOURLY
            1 -> SessionDatabaseHelper.TIME_RANGE_DAILY
            2 -> SessionDatabaseHelper.TIME_RANGE_WEEKLY
            else -> SessionDatabaseHelper.TIME_RANGE_DAILY
        }

        val filteredStats = dbHelper.getFilteredStats(selectedGame, selectedTimeRange)
        setupLineChart(filteredStats, selectedTimeRange)
    }

    private fun showStats() {
        val total = dbHelper.getTotalDuration()
        val average = dbHelper.getAverageDuration()
        val count = dbHelper.getSessionCount()

        totalDurationText.text = "Total Durasi: $total menit"
        averageDurationText.text = "Rata-rata Durasi: $average menit"
        sessionCountText.text = "Jumlah Sesi: $count"
    }

    private fun setupLineChart(stats: Map<String, Double>, timeRange: String) {
        if (stats.isEmpty()) {
            lineChart.clear()
            lineChart.invalidate()
            Toast.makeText(this, "Tidak ada data untuk ditampilkan", Toast.LENGTH_SHORT).show()
            return
        }

        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()

        stats.keys.forEachIndexed { index, key ->
            entries.add(Entry(index.toFloat(), stats[key]!!.toFloat()))
            labels.add(
                when (timeRange) {
                    SessionDatabaseHelper.TIME_RANGE_HOURLY -> "Jam $key"
                    SessionDatabaseHelper.TIME_RANGE_WEEKLY -> "Minggu ${key.split("-")[1]}"
                    else -> key
                }
            )
        }

        val dataSet = LineDataSet(entries, "Rata-rata Durasi (menit)").apply {
            color = resources.getColor(R.color.purple_500, theme)
            valueTextColor = resources.getColor(R.color.black, theme)
            lineWidth = 2f
            circleRadius = 5f
            setCircleColor(resources.getColor(R.color.purple_500, theme))
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        lineChart.apply {
            data = LineData(dataSet)
            xAxis.run {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                labelCount = labels.size
                setAvoidFirstLastClipping(true)
            }

            axisLeft.run {
                axisMinimum = 0f
                granularity = 30f
            }

            axisRight.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            animateY(1000)
            invalidate()
        }
    }
}
