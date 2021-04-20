
package com.coronadefense.api

import kotlinx.coroutines.*

class Lobby(
        val id: Long,
        val name: String,
        val accessToken: Long,
) {
  var playerCount: Int = 1
    set(value) { if (value > 0) field = value }
  constructor(
          id: Long, name: String, accessToken: Long, playerCount: Int
  ) : this(id, name, accessToken) {
    this.playerCount = playerCount
  }
  fun leaveLobby() {
    GlobalScope.launch {
      ApiClient.leaveLobbyRequest(id, accessToken)
    }
  }
}