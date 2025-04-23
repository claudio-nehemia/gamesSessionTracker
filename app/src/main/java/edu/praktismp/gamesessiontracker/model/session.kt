package edu.praktismp.gamesessiontracker.model

data class Session(
    var id: Int = 0,
    var gameName: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    var duration: Long
)
