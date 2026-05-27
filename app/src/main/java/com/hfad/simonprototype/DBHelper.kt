package com.hfad.simonprototype

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "games.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "games"
        private const val COL_ID = "id"
        private const val COL_MAX_CORRECT = "max_correct"
        private const val COL_SEQUENCE = "sequence"       // salveremo JSON string
        private const val COL_FIRST_ERROR = "first_error_index"
        private const val COL_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_MAX_CORRECT INTEGER NOT NULL,
                $COL_SEQUENCE TEXT NOT NULL,
                $COL_FIRST_ERROR INTEGER NOT NULL,
                $COL_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertGame(game: GameResult): Long {
        val db = writableDatabase
        val gson = Gson()
        val sequenceJson = gson.toJson(game.sequence)

        val values = ContentValues().apply {
            put(COL_MAX_CORRECT, game.maxCorrect)
            put(COL_SEQUENCE, sequenceJson)
            put(COL_FIRST_ERROR, game.firstErrorIndex)
            put(COL_TIMESTAMP, game.timestamp)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllGames(): List<GameResult> {
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COL_TIMESTAMP DESC")
        val games = mutableListOf<GameResult>()
        val gson = Gson()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID))
            val maxCorrect = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MAX_CORRECT))
            val sequenceJson = cursor.getString(cursor.getColumnIndexOrThrow(COL_SEQUENCE))
            val firstErrorIndex = cursor.getInt(cursor.getColumnIndexOrThrow(COL_FIRST_ERROR))
            val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP))

            val sequence = gson.fromJson(sequenceJson, Array<Int>::class.java).toMutableList() as ArrayList<Int>
            games.add(GameResult(id, maxCorrect, sequence, firstErrorIndex, timestamp))
        }
        cursor.close()
        db.close()
        return games
    }
}