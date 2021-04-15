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
    val response: HighScoreListResponse = client.get("$baseUrl/HighScoreList")
    client.close()
    if (response.success) {
      return response.scores
    } else {
      throw Exception(response.details)
    }
  }
  suspend fun createLobby(name: String, password: String): Long {
    val response: CreateLobbyResponse = client.post("$baseUrl/CreateLobby") {
      parameter("name", name)
      parameter("password", password)
    }
    if (response.success) {
      return response.lobbyId
    } else {
      throw Exception(response.details)
    }
  }
  suspend fun joinLobby(lobbyId: Long, password: String, connectionNumber: Long): LobbyJoined {
    val response: JoinLobbyResponse = client.patch("$baseUrl/JoinLobby") {
      parameter("lobbyId", lobbyId)
      parameter("password", password)
      parameter("connectionNumber", connectionNumber)
    }
    if (response.success) {
      return LobbyJoined(response.accessToken, response.lobbyId)
    } else {
      throw Exception(response.details)
    }
  }
  suspend fun leaveLobby(lobbyId: Long, accessToken: Long) {
    val response: LeaveLobbyResponse = client.patch("$baseUrl/LeaveLobby") {
      parameter("lobbyId", lobbyId)
      parameter("accessToken", accessToken)
    }
    if (!response.success) {
      throw Exception(response.details)
    }
  }
}