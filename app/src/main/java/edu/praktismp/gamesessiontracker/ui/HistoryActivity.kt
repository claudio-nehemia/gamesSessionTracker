package edu.praktismp.gamesessiontracker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.praktismp.gamesessiontracker.R
import edu.praktismp.gamesessiontracker.data.SessionDatabaseHelper
import edu.praktismp.gamesessiontracker.model.Session

class HistoryActivity : AppCompatActivity() {

    private lateinit var lvSessions: ListView
    private lateinit var dbHelper: SessionDatabaseHelper
    private lateinit var sessionList: MutableList<Session>
    // It's good practice to define column names as constants
    // to avoid typos, especially if used in multiple places.
    // Example:
    // companion object {
    //     const val COLUMN_ID = "id"
    //     const val COLUMN_GAME_NAME = "gameName"
    //     // ... and so on
    // }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        lvSessions = findViewById(R.id.lvSessions)
        dbHelper = SessionDatabaseHelper(this)

        // loadSessions() will be called in onResume, so it might be redundant here
        // unless you need it loaded before onResume for some specific reason.
    }

    override fun onResume() {
        super.onResume()
        loadSessions() // Good practice to refresh data in onResume
    }

    private fun loadSessions() {
        val db = dbHelper.readableDatabase
        // It's better to specify columns explicitly and use try-with-resources (use) for the cursor
        val cursor = db.rawQuery("SELECT id, gameName, date, startTime, endTime, duration FROM sessions ORDER BY date DESC", null)

        sessionList = mutableListOf()
        val displayList = mutableListOf<String>()

        cursor.use { // Ensures the cursor is closed automatically
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow("id"))
                    val name = it.getString(it.getColumnIndexOrThrow("gameName"))
                    val date = it.getString(it.getColumnIndexOrThrow("date"))
                    val start = it.getString(it.getColumnIndexOrThrow("startTime"))
                    val end = it.getString(it.getColumnIndexOrThrow("endTime"))

                    // **CRITICAL FIX HERE:**
                    // Assuming 'duration' in the database could be TEXT or INTEGER.
                    // We need to robustly get it and convert to Long for the Session model.
                    val durationValue: Long
                    val durationColumnIndex = it.getColumnIndexOrThrow("duration")

                    // Check the type of the column at runtime if unsure, though ideally schema is fixed.
                    // For now, let's assume it *might* be a string that needs conversion,
                    // or an integer that needs conversion to Long.
                    // The error "actual type is 'kotlin.String!', but 'kotlin.Long' was expected"
                    // strongly suggests it's being read as a String at some point when a Long is needed.

                    // If 'duration' is stored as TEXT (String) in the DB representing minutes:
                    val durationString = it.getString(durationColumnIndex)
                    durationValue = durationString.toLongOrNull() ?: 0L // Convert String to Long, default to 0 if null or invalid

                    // If 'duration' is stored as INTEGER in the DB representing minutes:
                    // val durationInt = it.getInt(durationColumnIndex)
                    // durationValue = durationInt.toLong()

                    sessionList.add(Session(id, name, date, start, end, durationValue))
                    displayList.add("$name - $date\n$start s/d $end ($durationValue menit)")
                } while (it.moveToNext())
            }
        }
        // db.close() // Generally, the readableDatabase obtained from the helper doesn't need explicit closing here.
        // The helper manages the database lifecycle. Closing it prematurely can cause issues.

        val adapter = SessionAdapter(this, sessionList, displayList,
            onEdit = { session ->
                val intent = Intent(this, AddSessionActivity::class.java).apply {
                    putExtra("id", session.id)
                    putExtra("gameName", session.gameName)
                    putExtra("date", session.date)
                    putExtra("startTime", session.startTime)
                    putExtra("endTime", session.endTime)
                    // If AddSessionActivity expects duration, pass it correctly:
                    // putExtra("duration", session.duration) // Pass the Long
                }
                startActivity(intent)
            },
            onDelete = { session ->
                deleteSession(session.id)
                // The list will refresh when onResume calls loadSessions().
                // If you need immediate refresh without waiting for onResume, uncomment:
                // loadSessions()
            }
        )
        lvSessions.adapter = adapter
    }

    private fun deleteSession(id: Int) {
        val db = dbHelper.writableDatabase
        try {
            db.delete("sessions", "id = ?", arrayOf(id.toString()))
        } finally {
            // db.close() // Writable databases from a helper are also often managed by the helper.
            // Only close if you are managing the SQLiteDatabase instance directly without a helper.
        }
        Toast.makeText(this, "Sesi dihapus", Toast.LENGTH_SHORT).show()
    }
}