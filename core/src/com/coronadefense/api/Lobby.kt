
package com.coronadefense.api

import kotlinx.coroutines.*

class Lobby(
        val id: Long,
        val name: String,
        val accessToken: Long,
        val playerCount: Int,
) {
  fun leaveLobby() {
    GlobalScope.launch {
      ApiClient.leaveLobbyRequest(id, accessToken)
    }
  }
}