package edu.praktismp.gamesessiontracker.model

data class Session(
    val id: Int,
    val gameName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val duration: Long // Expects a Long for duration
)