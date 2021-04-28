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
 * Extends InputState to allow users to join and create lobbies.
 * @param stateManager Manager of all game states.
 */
class LobbyListState(
  stateManager: StateManager
): InputState(stateManager)  {
  private val backButton = BackButton("MainMenu", stateManager, stage)

  private val font = Font(20)

  private val lobbiesTitle = "LOBBIES"
  private val playerCountTitle = "Players"
  private val titlePositionY = GAME_HEIGHT * 0.5f + MENU_TITLE_OFFSET

  private val centerPositionX = GAME_WIDTH * 0.5f

  private val createLobbyButtonText = "CREATE LOBBY"
  private val createLobbyPositionY = GAME_HEIGHT * 0.5f + BOTTOM_BUTTON_OFFSET

  // Listeners for text input when the user creates/joins a lobby.
  private val nameListener = TextInputListener()
  private val passwordListener = TextInputListener()

  // Error message for feedback to the user on invalid input.
  private var errorMessage: String? = null

  private var lobbyList: List<LobbyData>? = null

  // State for data of the lobby to join, to be passed on to LobbyState.
  private var selectedLobbyID: Long? = null
  private var selectedLobbyPlayerCount: Int? = null

  private val listPositionX: Float = centerPositionX - LIST_ITEM_WIDTH * 0.5f
  private val listPositionsY: MutableList<Float> = mutableListOf()

  init {
    val createLobbyButton = Image()
    buttons += createLobbyButton

    createLobbyButton.setSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT)
    createLobbyButton.setPosition(
      centerPositionX - MENU_BUTTON_WIDTH * 0.5f,
      createLobbyPositionY
    )

    // Once Create Lobby button is clicked, display the input for name and password.
    createLobbyButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        Gdx.input.getTextInput(passwordListener, "Lobby password", "", "password")
        Gdx.input.getTextInput(nameListener, "Lobby name", "", "lobby name")
      }
    })

    stage.addActor(createLobbyButton)

    // Launches a coroutine to fetch the list of lobbies, and adds a button for each.
    // Upon clicking a lobby, opens the password input field.
    GlobalScope.launch {
      lobbyList = ApiClient.lobbyListRequest()
      for ((index, lobby) in lobbyList!!.withIndex()) {
        val listPositionY = GAME_HEIGHT * 0.5f + MENU_TITLE_OFFSET - LIST_ITEM_HEIGHT * (index + 1.5f)
        listPositionsY += listPositionY

        val joinLobbyButton = Image()
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

    // Launches a coroutine to send the Create Lobby request to the API.
    runBlocking {
      selectedLobbyID = ApiClient.createLobbyRequest(nameListener.value, passwordListener.value)
    }

    println("Lobby ID: $selectedLobbyID")
  }

  @ExperimentalUnsignedTypes
  private fun joinLobby() {
    // Launches a coroutine to join the lobby. If unsuccessful, displays a message to the user.
    // On a success, a GameObserver is instantiated to observe the Receiver for game messages, and the state is set to LobbyState.
    runBlocking {
      val connectionNumber = Receiver.connectAsync()
      try {
        val response = ApiClient.joinLobbyRequest(selectedLobbyID!!, passwordListener.value, connectionNumber)
        val gameObserver = GameObserver(selectedLobbyID!!, nameListener.value, response.accessToken, selectedLobbyPlayerCount ?: 1)
        Receiver.observer = gameObserver
        stateManager.set(LobbyState(stateManager, gameObserver))
      } catch (exception: Exception) {
        errorMessage = "Wrong password!"
      }
      resetLobbyInfo()
    }
  }

  // Utility method for canceling of a join.
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

    // Checks for user input in the name/password text fields.
    // If no lobby is selected, then the user clicked Create Lobby, and that is executed first.
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

    sprites.draw(Textures.background("menu"), 0F, 0F, GAME_WIDTH, GAME_HEIGHT)
    backButton.render(sprites)

    // Checks whether to show error message to user; if so, renders it.
    errorMessage?.let {
      font.draw(
        sprites,
        errorMessage!!,
        centerPositionX - font.width(errorMessage!!),
        GAME_HEIGHT - (LIST_ITEM_HEIGHT - font.height(errorMessage!!)) * 0.5f
      )
    }

    font.draw(
      sprites,
      lobbiesTitle,
      listPositionX,
      titlePositionY + font.height(lobbiesTitle) * 0.5f
    )
    font.draw(
      sprites,
      playerCountTitle,
      listPositionX + LIST_ITEM_WIDTH - font.width(playerCountTitle),
      titlePositionY + font.height(playerCountTitle) * 0.5f
    )

    // Checks for fetched lobby list, then loops through it to render the button textures and text.
    lobbyList?.let {
      for ((index, lobby) in lobbyList!!.withIndex()) {
        sprites.draw(
          Textures.button("standard"),
          listPositionX,
          listPositionsY[index],
          LIST_ITEM_WIDTH,
          LIST_ITEM_HEIGHT
        )
        font.draw(
          sprites,
          lobby.name,
          listPositionX + LIST_TEXT_INLINE_OFFSET,
          listPositionsY[index] + (LIST_ITEM_HEIGHT + font.height(lobby.name)) * 0.5f
        )
        val playerCountText = lobby.playerCount.toString()
        font.draw(
          sprites,
          playerCountText,
          listPositionX + LIST_ITEM_WIDTH - LIST_TEXT_INLINE_OFFSET - font.width(playerCountText) * 0.5f,
          listPositionsY[index] + (LIST_ITEM_HEIGHT + font.height(playerCountText)) * 0.5f
        )
      }
    }

    // Renders texture and text for the Create Lobby button.
    sprites.draw(
      Textures.button("standard"),
      centerPositionX - MENU_BUTTON_WIDTH * 0.5f,
      createLobbyPositionY,
      MENU_BUTTON_WIDTH,
      MENU_BUTTON_HEIGHT
    )
    font.draw(
      sprites,
      createLobbyButtonText,
      centerPositionX - font.width(createLobbyButtonText) * 0.5f,
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
    font.dispose()
    backButton.dispose()

    println("LobbyListState disposed")
  }
}