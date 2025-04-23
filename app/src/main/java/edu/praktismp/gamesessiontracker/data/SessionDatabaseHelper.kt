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

    companion object {
        private const val DATABASE_NAME = "sessions.db"
        private const val DATABASE_VERSION = 1
    }
}
