
package com.coronadefense.types

import com.coronadefense.api.ApiClient
import kotlinx.coroutines.*

class Lobby(
        val id: Long,
        val name: String,
        val accessToken: Long,
        var playerCount: Int,
) {
  fun leaveLobby() {
    GlobalScope.launch {
      ApiClient.leaveLobbyRequest(id, accessToken)
      ApiClient.close()
    }
  }
}