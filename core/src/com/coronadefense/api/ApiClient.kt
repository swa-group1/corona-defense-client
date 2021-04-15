package com.coronadefense.api

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.serialization.*

const val baseUrl = "http://localhost:5000"

class ApiClient {
  val client = HttpClient {
    install(JsonFeature) {
      serializer = KotlinxSerializer()
    }
  }
  suspend fun getHighScoreList(): List<HighScore> {
    val response: HighScoreListResponse = client.get("${baseUrl}/HighScoreList")
    client.close()
    if (response.success) {
      return response.scores
    } else {
      throw Exception("Failed to fetch high score list.")
    }
  }
  suspend fun createLobby(name: String, password: String): Int {
    val response: CreateLobbyResponse = client.post("${baseUrl}/CreateLobby") {
      body = CreateLobbyInput(name, password)
    }
    if (response.success) {
      return response.lobbyId
    } else {
      throw Exception("Failed to fetch high score list.")
    }
  }
}