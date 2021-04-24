package com.coronadefense.api

import kotlinx.serialization.Serializable

@Serializable
data class GenericResponse(
  val details: String,
  val success: Boolean,
)

@Serializable
data class TowerData(
  val Description: String,
  val MediumCost: Int,
  val ProjectileSpeed: Float,
  val ProjectileSpriteNumber: Int,
  val Range: Int,
  val ReloadTime: Float,
  val TowerSpriteNumber: Int,
  val TypeNumber: Int,
  val CanSpotCamo: Boolean,
)

@Serializable
data class TowerListResponse(
  val Towers: List<TowerData>
)

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
data class LobbyData(
  val id: Long,
  val name: String,
  val playerCount: Int,
)

@Serializable
data class LobbyResponse(
  val lobby: LobbyData,
  val details: String,
  val success: Boolean,
)

@Serializable
data class LobbyListResponse(
  val lobbies: List<LobbyData>,
  val details: String,
  val success: Boolean,
)

@Serializable
data class VerifyVersionResponse(
  val validVersion: Boolean,
  val details: String,
  val success: Boolean,
)