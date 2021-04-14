package com.coronadefense.api

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.serialization.*

const val baseUrl = "http://localhost:5000"

@Serializable
data class HighScore(val name: String, val value: Int)

@Serializable
data class HighScoreList(
  val scores: List<HighScore>,
  val details: String,
  val success: Boolean
)

class ApiClient {
  val client = HttpClient {
    install(JsonFeature) {
      serializer = KotlinxSerializer()
    }
  }
  suspend fun getHighScoreList(): List<HighScore> {
    val highScoreList: HighScoreList = client.get("${baseUrl}/HighScoreList")
    client.close()
    if (highScoreList.success) {
      return highScoreList.scores
    } else {
      throw Exception("Failed to fetch high score list.")
    }
  }
}