package com.coronadefense.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.*

const val baseUrl = "localhost:5000"

@Serializable
data class HighScore(val name: String, val value: Int)

@Serializable
data class HighScoreList(
  val scores: List<HighScore>,
  val details: String,
  val success: Boolean
)

class ApiClient {
  val client = HttpClient(CIO)
  suspend fun getHighScoreList(): List<HighScore> {
    val highScoreList = client.get<HighScoreList>("${baseUrl}/HighScoreList")
    if (highScoreList.success) {
      return highScoreList.scores
    } else {
      throw Exception("Failed to fetch high score list.")
    }
  }
}