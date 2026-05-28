package com.hfad.simonprototype

data class GameResult(
    val id: Long = -1L,
    val maxCorrect: Int,
    val sequence: ArrayList<Int>,
    val firstErrorIndex: Int = -1,
    val timestamp: Long = System.currentTimeMillis()
)