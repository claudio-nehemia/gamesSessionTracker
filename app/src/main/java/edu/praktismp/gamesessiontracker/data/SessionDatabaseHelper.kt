package edu.praktismp.gamesessiontracker.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SessionDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                gameName TEXT,
                date TEXT,
                startTime TEXT,
                endTime TEXT,
                duration INTEGER
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS sessions")
        onCreate(db)
    }

    fun getTotalDuration(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM(duration) FROM sessions", null)
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    fun getAverageDuration(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT AVG(duration) FROM sessions", null)
        var average = 0
        if (cursor.moveToFirst()) {
            average = cursor.getInt(0)
        }
        cursor.close()
        return average
    }

    fun getSessionCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM sessions", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    // SessionDatabaseHelper.kt
    companion object {
        private const val DATABASE_NAME = "sessions.db"
        private const val DATABASE_VERSION = 1
        const val TIME_RANGE_DAILY = "daily"
        const val TIME_RANGE_WEEKLY = "weekly"
        const val TIME_RANGE_HOURLY = "hourly"
    }

    // Fungsi baru untuk mendapatkan data statistik dengan filter
    fun getFilteredStats(gameName: String?, timeRange: String): Map<String, Double> {
        val db = readableDatabase
        val stats = LinkedHashMap<String, Double>()

        // Build SQL query based on filters
        val whereClause = buildWhereClause(gameName, timeRange)
        val groupByClause = buildGroupByClause(timeRange)

        val query = """
        SELECT 
            ${if (timeRange == TIME_RANGE_HOURLY) "strftime('%H', startTime) as time_group" else "date"}
            , AVG(duration) as average
        FROM sessions
        $whereClause
        GROUP BY ${if (timeRange == TIME_RANGE_HOURLY) "time_group" else "date"}
        $groupByClause
        ORDER BY date ASC
    """.trimIndent()

        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val key = cursor.getString(0)
            val value = cursor.getDouble(1)
            stats[key] = value
        }
        cursor.close()
        return stats
    }

    private fun buildWhereClause(gameName: String?, timeRange: String): String {
        val clauses = mutableListOf<String>()

        if (!gameName.isNullOrBlank()) {
            clauses.add("gameName = '$gameName'")
        }

        if (timeRange == TIME_RANGE_WEEKLY) {
            clauses.add("strftime('%W', date) = strftime('%W', 'now')")
        }

        return if (clauses.isNotEmpty()) "WHERE ${clauses.joinToString(" AND ")}" else ""
    }

    private fun buildGroupByClause(timeRange: String): String {
        return if (timeRange == TIME_RANGE_WEEKLY) {
            "HAVING COUNT(*) > 0" // Memastikan ada data untuk minggu tersebut
        } else {
            ""
        }
    }

    // Fungsi untuk mendapatkan daftar game
    fun getGameNames(): List<String> {
        val db = readableDatabase
        val games = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT DISTINCT gameName FROM sessions", null)
        while (cursor.moveToNext()) {
            games.add(cursor.getString(0))
        }
        cursor.close()
        return games
    }




}
