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
        val averagesMap = dbHelper.getAverageDurationPerDay()
        val dates = averagesMap.keys.toList()
        val averages = averagesMap.values.toList()

        val entries = ArrayList<Entry>()
        dates.forEachIndexed { index, _ ->
            entries.add(Entry(index.toFloat(), averages[index].toFloat()))
        }

        val dataSet = LineDataSet(entries, "Rata-rata Durasi Harian (menit)").apply {
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
                valueFormatter = IndexAxisValueFormatter(dates)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                labelCount = dates.size
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
