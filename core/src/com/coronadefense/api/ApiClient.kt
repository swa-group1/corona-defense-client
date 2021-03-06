package com.coronadefense.api

import com.coronadefense.types.GameStage
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*

const val baseUrl = "http://35.228.171.73:5000"
//const val baseUrl = "http://10.0.2.2:5000"
//const val baseUrl = "http://localhost:5000"
const val firebaseUrl = "https://firebasestorage.googleapis.com/v0/b/coronadefense-1.appspot.com/o/"

/**
 * Object to send requests to the API.
 * Used to fetch config files from Firebase, set up lobbies, and communicate user input to the game on the server.
 */
object ApiClient {
  private val client = HttpClient {
    install(JsonFeature) {
      serializer = KotlinxSerializer()
    }
  }

  fun close() {
    client.close()
    println("ApiClient: client closed")
  }

  suspend fun gameStageRequest(stageNumber: Int): GameStage? {
    var gameStage: GameStage? = null
    try {
      gameStage = client.get("${firebaseUrl}stage_${stageNumber.toString().padStart(3, '0')}.json?alt=media")
      println("gameStageRequest succeeded")
    } catch (exception: ClientRequestException) {
      println("gameStageRequest failed")
    }
    return gameStage
  }

  suspend fun towerListRequest(): List<TowerData>? {
    var towerList: List<TowerData>? = null
    try {
      val response: TowerListResponse = client.get("${firebaseUrl}towers.json?alt=media")
      towerList = response.Towers
      println("towerListRequest succeeded")
    } catch (exception: ClientRequestException) {
      println("towerListRequest failed")
      println(exception)
    }
    return towerList
  }

  suspend fun stagesListRequest(): List<SimpleStageData>?{
    var stagesList: List<SimpleStageData>? = null
    try {
      println("tries")
      val response: StagesData = client.get("${firebaseUrl}stages.json?alt=media")
      println(response)
      stagesList = response.Stages
      println("stagesListRequest succeeded")
    } catch (exception: ClientRequestException) {
      println("stagesListRequest failed")
    }
    return stagesList
  }

  suspend fun highScoreListRequest(): List<HighScore> {
    val response: HighScoreListResponse = client.get("$baseUrl/HighScoreList")
    if (response.success) {
      println("highScoreListRequest succeeded")
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
      println("createLobbyRequest succeeded")
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
      println("joinLobbyRequest succeeded")
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
    if (response.success) {
      println("leaveLobbyRequest succeeded")
    } else {
      throw Exception(response.details)
    }
  }

  suspend fun lobbyRequest(id: Long): LobbyData {
    val response: LobbyResponse = client.get("$baseUrl/Lobby") {
      parameter("id", id)
    }
    if (response.success) {
      println("lobbyRequest succeeded")
      return response.lobby
    } else {
      throw Exception(response.details)
    }
  }

  suspend fun lobbyListRequest(): List<LobbyData> {
    val response: LobbyListResponse = client.get("$baseUrl/LobbyList")
    if (response.success) {
      println("lobbyListRequest succeeded")
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
    if (response.success) {
      println("placeTowerRequest succeeded")
    } else {
      throw Exception(response.details)
    }
  }

  suspend fun sellTowerRequest(lobbyId: Long, accessToken: Long, towerId: Int) {
    val response: GenericResponse = client.patch("$baseUrl/SellTower") {
      parameter("lobbyId", lobbyId)
      parameter("accessToken", accessToken)
      parameter("towerId", towerId)
    }
    if (response.success) {
      println("sellTowerRequest succeeded")
    } else {
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
    if (response.success) {
      println("startGameRequest succeeded")
    } else {
      throw Exception(response.details)
    }
  }

  suspend fun startRoundRequest(lobbyId: Long, accessToken: Long) {
    val response: GenericResponse = client.patch("$baseUrl/StartRound") {
      parameter("lobbyId", lobbyId)
      parameter("accessToken", accessToken)
    }
    if (response.success) {
      println("startRoundRequest succeeded")
    } else {
      throw Exception(response.details)
    }
  }

  suspend fun verifyVersionRequest(version: String): Boolean {
    val response: VerifyVersionResponse = client.get("$baseUrl/VerifyVersion") {
      parameter("version", version)
    }
    if (response.success) {
      println("verifyVersionRequest succeeded")
      return response.validVersion
    } else {
      throw Exception(response.details)
    }
  }
}