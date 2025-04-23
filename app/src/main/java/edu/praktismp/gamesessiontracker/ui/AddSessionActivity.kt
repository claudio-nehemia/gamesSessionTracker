package edu.praktismp.gamesessiontracker.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.praktismp.gamesessiontracker.R
import edu.praktismp.gamesessiontracker.data.SessionDatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class AddSessionActivity : AppCompatActivity() {

    private lateinit var dbHelper: SessionDatabaseHelper
    private lateinit var etGameName: EditText
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var btnSave: Button

    private var editingId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_session)

        dbHelper = SessionDatabaseHelper(this)

        etGameName = findViewById(R.id.etGameName)
        etDate = findViewById(R.id.etDate)
        etStartTime = findViewById(R.id.etStartTime)
        etEndTime = findViewById(R.id.etEndTime)
        btnSave = findViewById(R.id.btnSave)

        val calendar = Calendar.getInstance()

        etDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                etDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
            },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val showTimePicker = { target: EditText ->
            TimePickerDialog(this, { _, hour, minute ->
                target.setText(String.format("%02d:%02d", hour, minute))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        etStartTime.setOnClickListener { showTimePicker(etStartTime) }
        etEndTime.setOnClickListener { showTimePicker(etEndTime) }

        editingId = intent.getIntExtra("id", -1)
        if (editingId != -1) {
            etGameName.setText(intent.getStringExtra("gameName"))
            etDate.setText(intent.getStringExtra("date"))
            etStartTime.setText(intent.getStringExtra("startTime"))
            etEndTime.setText(intent.getStringExtra("endTime"))
            btnSave.text = "Update Sesi"
        }

        btnSave.setOnClickListener {
            saveSession()
        }
    }

    private fun saveSession() {
        val name = etGameName.text.toString()
        val date = etDate.text.toString()
        val start = etStartTime.text.toString()
        val end = etEndTime.text.toString()

        if (name.isBlank() || date.isBlank() || start.isBlank() || end.isBlank()) {
            Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startTime = sdf.parse(start)
        val endTime = sdf.parse(end)

        if (startTime == null || endTime == null) {
            Toast.makeText(this, "Format waktu salah", Toast.LENGTH_SHORT).show()
            return
        }

        val duration = (endTime.time - startTime.time) / 1000 / 60

        val db = dbHelper.writableDatabase
        if (editingId != -1) {
            val sql = """
                UPDATE sessions 
                SET gameName = ?, date = ?, startTime = ?, endTime = ?, duration = ?
                WHERE id = ?
            """.trimIndent()
            val stmt = db.compileStatement(sql)
            stmt.bindString(1, name)
            stmt.bindString(2, date)
            stmt.bindString(3, start)
            stmt.bindString(4, end)
            stmt.bindLong(5, duration)
            stmt.bindLong(6, editingId.toLong())
            stmt.executeUpdateDelete()
            Toast.makeText(this, "Sesi diperbarui", Toast.LENGTH_SHORT).show()
        } else {
            val sql = """
                INSERT INTO sessions (gameName, date, startTime, endTime, duration)
                VALUES (?, ?, ?, ?, ?)
            """.trimIndent()
            val stmt = db.compileStatement(sql)
            stmt.bindString(1, name)
            stmt.bindString(2, date)
            stmt.bindString(3, start)
            stmt.bindString(4, end)
            stmt.bindLong(5, duration)
            stmt.executeInsert()
            Toast.makeText(this, "Sesi ditambahkan", Toast.LENGTH_SHORT).show()
        }

        db.close()
        finish()
    }
}
