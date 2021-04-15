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
data class CreateLobbyInput(
  val name: String,
  val password: String
)

@Serializable
data class CreateLobbyResponse(
  val lobbyId: Int,
  val details: String,
  val success: Boolean,
)