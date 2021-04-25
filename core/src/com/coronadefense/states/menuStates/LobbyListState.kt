package com.coronadefense.states.menuStates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.states.StateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.LobbyData
import kotlinx.coroutines.*
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.receiver.Receiver
import com.coronadefense.states.GameObserver
import com.coronadefense.states.InputState
import com.coronadefense.utils.*
import com.coronadefense.utils.Constants.BOTTOM_BUTTON_OFFSET
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.LIST_ITEM_HEIGHT
import com.coronadefense.utils.Constants.LIST_TEXT_INLINE_OFFSET
import com.coronadefense.utils.Constants.LIST_ITEM_WIDTH
import com.coronadefense.utils.Constants.MENU_BUTTON_HEIGHT
import com.coronadefense.utils.Constants.MENU_BUTTON_WIDTH
import com.coronadefense.utils.Constants.MENU_TITLE_OFFSET

/**
 * State to show the list of joinable lobbies.
 * Extends StageState for user input on the Stage.
 * @param stateManager Manager of all game states.
 */
class LobbyListState(
  stateManager: StateManager
): InputState(stateManager)  {
  private val background = Textures.background("menu")
  private val buttonTexture = Textures.button("standard")

  private val backButton = BackButton("MainMenu", stateManager, stage)

  private val font = Font(20)

  private val lobbiesTitle = "LOBBIES"
  private val playerCountTitle = "Players"
  private val titlePositionY = GAME_HEIGHT * 0.5f + MENU_TITLE_OFFSET - LIST_ITEM_HEIGHT

  private val createLobbyButtonText = "CREATE LOBBY"
  private val createLobbyPositionX = GAME_WIDTH * 0.5f
  private val createLobbyPositionY = GAME_HEIGHT * 0.5f + BOTTOM_BUTTON_OFFSET

  private val nameListener = TextInputListener()
  private val passwordListener = TextInputListener()

  var lobbyList: List<LobbyData>? = null
  private var selectedLobbyID: Long? = null
  private var selectedLobbyPlayerCount: Int? = null
  private val listPositionX: Float = (GAME_WIDTH - LIST_ITEM_WIDTH) * 0.5f
  private val listPositionsY: MutableList<Float> = mutableListOf()

  init {
    val createLobbyButton = Image(buttonTexture)
    buttons += createLobbyButton

    createLobbyButton.setSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT)
    createLobbyButton.setPosition(
      createLobbyPositionX - MENU_BUTTON_WIDTH * 0.5f,
      createLobbyPositionY
    )

    createLobbyButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        Gdx.input.getTextInput(passwordListener, "Lobby password", "", "password")
        Gdx.input.getTextInput(nameListener, "Lobby name", "", "lobby name")
      }
    })

    stage.addActor(createLobbyButton)

    GlobalScope.launch {
      lobbyList = ApiClient.lobbyListRequest()
      for ((index, lobby) in lobbyList!!.withIndex()) {
        listPositionsY += GAME_HEIGHT * 0.5f + MENU_TITLE_OFFSET - LIST_ITEM_HEIGHT * (index + 2)

        val joinLobbyButton = Image(buttonTexture)
        buttons += joinLobbyButton

        joinLobbyButton.setSize(LIST_ITEM_WIDTH, LIST_ITEM_HEIGHT)
        joinLobbyButton.setPosition(listPositionX, listPositionsY[index])

        joinLobbyButton.addListener(object : ClickListener() {
          override fun clicked(event: InputEvent?, x: Float, y: Float) {
            Gdx.input.getTextInput(passwordListener, "Lobby password", "", "password")
            nameListener.input(lobby.name)
            selectedLobbyID = lobby.id
            selectedLobbyPlayerCount = lobby.playerCount
          }
        })

        stage.addActor(joinLobbyButton)
      }
    }
  }

  private fun createLobby() {
    println("Create lobby: name ${nameListener.value}")
    runBlocking {
      selectedLobbyID = (ApiClient.createLobbyRequest(nameListener.value, passwordListener.value))
    }
    println("Lobby ID: $selectedLobbyID")
  }

  @ExperimentalUnsignedTypes
  private fun joinLobby() {
    runBlocking {
      val connectionNumber = Receiver.connectAsync()
      val response = ApiClient.joinLobbyRequest(selectedLobbyID!!, passwordListener.value, connectionNumber)
      val gameObserver = GameObserver(selectedLobbyID!!, nameListener.value, response.accessToken, selectedLobbyPlayerCount ?: 1)
      Receiver.observer = gameObserver
      stateManager.set(LobbyState(stateManager, gameObserver))
      resetLobbyInfo()
    }
  }

  private fun resetLobbyInfo(){
    nameListener.input("")
    passwordListener.input("")
    selectedLobbyID = null
    selectedLobbyPlayerCount = null
    println("Lobby info reset")
  }

  @ExperimentalUnsignedTypes
  override fun update(deltaTime: Float) {
    backButton.update()

    if(passwordListener.value.isNotEmpty() && nameListener.value.isNotEmpty()) {
      if (selectedLobbyID == null) {
        createLobby()
      }
      if (selectedLobbyID != null) {
        joinLobby()
      }
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(background, 0F, 0F, GAME_WIDTH, GAME_HEIGHT)

    lobbyList?.let {
      font.draw(
        sprites,
        lobbiesTitle,
        listPositionX,
        titlePositionY - font.height(lobbiesTitle) * 0.5f
      )
      font.draw(
        sprites,
        playerCountTitle,
        listPositionX + LIST_ITEM_WIDTH - font.width(playerCountTitle),
        titlePositionY - font.height(playerCountTitle) * 0.5f
      )
      for ((index, lobby) in lobbyList!!.withIndex()) {
        font.draw(
          sprites,
          lobby.name,
          listPositionX + LIST_TEXT_INLINE_OFFSET,
          listPositionsY[index] - font.height(lobby.name) * 0.5f
        )
        val playerCountText = lobby.playerCount.toString()
        font.draw(
          sprites,
          playerCountText,
          listPositionX + LIST_ITEM_WIDTH - LIST_TEXT_INLINE_OFFSET - font.width(playerCountText) * 0.5f,
          listPositionsY[index] - font.height(playerCountText) * 0.5f
        )
      }
    }

    font.draw(
      sprites,
      createLobbyButtonText,
      createLobbyPositionX - font.width(createLobbyButtonText) * 0.5f,
      createLobbyPositionY + (MENU_BUTTON_HEIGHT + font.height(createLobbyButtonText)) * 0.5f
    )

    sprites.end()
    super.draw()
  }

  override fun dispose() {
    super.dispose()
    Textures.disposeAll()
    nameListener.dispose()
    passwordListener.dispose()
    background.dispose()
    font.dispose()
    backButton.dispose()

    println("LobbyListState disposed")
  }
}