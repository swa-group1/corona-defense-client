package com.coronadefense.api

import kotlinx.serialization.Serializable

@Serializable
data class HighScoreListResponse(
  val scores: List<HighScore>,
  val details: String,
  val success: Boolean
)

@Serializable
data class HighScore(val name: String, val value: Int)

@Serializable
data class CreateLobbyResponse(
  val lobbyId: Long,
  val details: String,
  val success: Boolean,
)

@Serializable
data class JoinLobbyResponse(
  val accessToken: Long,
  val lobbyId: Long,
  val details: String,
  val success: Boolean,
)

data class LobbyJoined(
  val accessToken: Long,
  val lobbyId: Long,
)

@Serializable
data class LeaveLobbyResponse(
  val details: String,
  val success: Boolean,
)