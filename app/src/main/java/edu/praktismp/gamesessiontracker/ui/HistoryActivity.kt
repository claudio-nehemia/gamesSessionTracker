package edu.praktismp.gamesessiontracker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import edu.praktismp.gamesessiontracker.R
import edu.praktismp.gamesessiontracker.data.SessionDatabaseHelper
import edu.praktismp.gamesessiontracker.model.Session

class HistoryActivity : AppCompatActivity() {

    private lateinit var lvSessions: ListView
    private lateinit var dbHelper: SessionDatabaseHelper
    private lateinit var sessionList: MutableList<Session>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        lvSessions = findViewById(R.id.lvSessions)
        dbHelper = SessionDatabaseHelper(this)

        loadSessions()
    }

    override fun onResume() {
        super.onResume()
        loadSessions()
    }

    private fun loadSessions() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM sessions ORDER BY date DESC", null)

        sessionList = mutableListOf()
        val displayList = mutableListOf<String>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("gameName"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val start = cursor.getString(cursor.getColumnIndexOrThrow("startTime"))
                val end = cursor.getString(cursor.getColumnIndexOrThrow("endTime"))
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))

                sessionList.add(Session(id, name, date, start, end, duration.toLong()));
                displayList.add("$name - $date\n$start s/d $end (${duration} menit)");
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        val adapter = SessionAdapter(this, sessionList, displayList,
            onEdit = { session ->
                val intent = Intent(this, AddSessionActivity::class.java)
                intent.putExtra("id", session.id)
                intent.putExtra("gameName", session.gameName)
                intent.putExtra("date", session.date)
                intent.putExtra("startTime", session.startTime)
                intent.putExtra("endTime", session.endTime)
                startActivity(intent)
            },
            onDelete = { session ->
                deleteSession(session.id)
                loadSessions()
            }
        )

        lvSessions.adapter = adapter
    }

    private fun deleteSession(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete("sessions", "id = ?", arrayOf(id.toString()))
        db.close()
        Toast.makeText(this, "Sesi dihapus", Toast.LENGTH_SHORT).show()
    }
}
