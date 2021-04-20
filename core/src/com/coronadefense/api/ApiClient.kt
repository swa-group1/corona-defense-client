package com.coronadefense.api

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.serialization.*

//const val baseUrl = "http://10.0.2.2:5000"
const val baseUrl = "http://localhost:5000"

object ApiClient {
  private val client = HttpClient {
    install(JsonFeature) {
      serializer = KotlinxSerializer()
    }
  }
  fun close() {
    client.close()
  }
  suspend fun highScoreListRequest(): List<HighScore> {
    val response: HighScoreListResponse = client.get("$baseUrl/HighScoreList")
    if (response.success) {
      return response.scores
    } else {
      throw Exception(response.details)
    }
  }
  suspend fun createLobbyRequest(name: String, password: String): Long {
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
  suspend fun joinLobbyRequest(lobbyId: Long, password: String, connectionNumber: Long): LobbyJoined {
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
  suspend fun leaveLobbyRequest(lobbyId: Long, accessToken: Long) {
    val response: GenericResponse = client.patch("$baseUrl/LeaveLobby") {
      parameter("lobbyId", lobbyId)
      parameter("accessToken", accessToken)
    }
    if (!response.success) {
      throw Exception(response.details)
    }
  }
  suspend fun lobbyRequest(id: Long): LobbyData {
    val response: LobbyResponse = client.get("$baseUrl/Lobby") {
      parameter("id", id)
    }
    if (response.success) {
      return response.lobby
    } else {
      throw Exception(response.details)
    }
  }
  suspend fun lobbyListRequest(): List<LobbyData> {
    val response: LobbyListResponse = client.get("$baseUrl/LobbyList")
    if (response.success) {
      return response.lobbies
    } else {
      throw Exception(response.details)
    }
  }
  suspend fun placeTowerRequest(lobbyId: Long, accessToken: Long, towerTypeNumber: Int, x: Int, y: Int) {
    val response: GenericResponse = client.patch("$baseUrl/PlaceTower") {
      parameter("lobbyId", lobbyId)
      parameter("accessToken", accessToken)
      parameter("towerTypeNumber", towerTypeNumber)
      parameter("x", x)
      parameter("y", y)
    }
    if (!response.success) {
      throw Exception(response.details)
    }
  }
  suspend fun sellTowerRequest(lobbyId: Long, accessToken: Long, towerId: Int) {
    val response: GenericResponse = client.patch("$baseUrl/SellTower") {
      parameter("lobbyId", lobbyId)
      parameter("accessToken", accessToken)
      parameter("towerId", towerId)
    }
    if (!response.success) {
      throw Exception(response.details)
    }
  }
  suspend fun startGameRequest(lobbyId: Long, accessToken: Long, stageNumber: Int, difficulty: Int) {
    val response: GenericResponse = client.patch("$baseUrl/StartGame") {
      parameter("lobbyId", lobbyId)
      parameter("accessToken", accessToken)
      parameter("stageNumber", stageNumber)
      parameter("difficulty", difficulty)
    }
    if (!response.success) {
      throw Exception(response.details)
    }
  }
  suspend fun startRoundRequest(lobbyId: Long, accessToken: Long) {
    val response: GenericResponse = client.patch("$baseUrl/StartRound") {
      parameter("lobbyId", lobbyId)
      parameter("accessToken", accessToken)
    }
    if (!response.success) {
      throw Exception(response.details)
    }
  }
  suspend fun verifyVersionRequest(version: String): Boolean {
    val response: VerifyVersionResponse = client.get("$baseUrl/VerifyVersion") {
      parameter("version", version)
    }
    if (response.success) {
      return response.validVersion
    } else {
      throw Exception(response.details)
    }
  }
}