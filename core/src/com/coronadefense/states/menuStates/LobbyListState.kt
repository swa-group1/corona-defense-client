package com.coronadefense.states.menuStates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.states.GameStateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.LobbyData
import kotlinx.coroutines.*
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.states.StageState
import com.coronadefense.states.State
import com.coronadefense.types.Lobby
import com.coronadefense.utils.*

/**
 * State to show the list of joinable lobbies.
 * Extends BackgroundState to show the menu background.
 * @param stateManager Manager of all game states.
 */
class LobbyListState(
  stateManager: GameStateManager
): StageState(stateManager)  {
  var lobbyList: List<LobbyData>? = null
  init {
    runBlocking {
      lobbyList = ApiClient.lobbyListRequest()
    }
  }

  private val background = Texture("initiate_game_state.jpg")
  private val font: BitmapFont = Font.generateFont(20)

  private val createLobbyTexture = Texture(Textures.button("standard"))
  private val createLobbyButton = Image(createLobbyTexture)
  private val joinLobbyTextures: MutableList<Texture> = mutableListOf()
  private val joinLobbyButtons: MutableList<Image> = mutableListOf()

  private val nameListener = TextInputListener()
  private val passwordListener = TextInputListener()

  private var mode = 1 //TODO: remove this when join lobby (api) is implemented (lobbyID-check is enough)
  private var lobbyID: Long? = null
  private var lobbyPlayerCount: Int? = null

  init {
    createLobbyButton.setSize(Constants.MENU_BUTTON_WIDTH, Constants.MENU_BUTTON_HEIGHT)
    createLobbyButton.setPosition(
      (Constants.GAME_WIDTH - Constants.MENU_BUTTON_WIDTH) / 2,
      Constants.GAME_HEIGHT / 8
    )
    createLobbyButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        Gdx.input.getTextInput(passwordListener, "Lobby password", "", "password")
        Gdx.input.getTextInput(nameListener, "Lobby name", "", "lobby name")
      }
    })
    stage.addActor(createLobbyButton)

    lobbyList?.let {
      val xPosition: Float = Constants.GAME_WIDTH / 2 - Constants.LIST_BUTTON_WIDTH / 2
      for (lobbyIndex in lobbyList!!.indices) {
        val yPosition: Float = (Constants.GAME_HEIGHT / 2) + 17f - (Constants.LIST_BUTTON_HEIGHT * lobbyIndex)

        val joinLobbyTexture = Texture(Textures.button("standard"))
        joinLobbyTextures += joinLobbyTexture

        val joinLobbyButton = Image(joinLobbyTexture)
        joinLobbyButtons += joinLobbyButton
        joinLobbyButton.setSize(Constants.LIST_BUTTON_WIDTH, Constants.LIST_BUTTON_HEIGHT)
        joinLobbyButton.setPosition(xPosition, yPosition)

        joinLobbyButton.addListener(object : ClickListener() {
          override fun clicked(event: InputEvent?, x: Float, y: Float) {
            Gdx.input.getTextInput(passwordListener, "Lobby password", "", "password")
            nameListener.input(lobbyList!![lobbyIndex].name)
            lobbyID = lobbyList!![lobbyIndex].id
            lobbyPlayerCount = lobbyList!![lobbyIndex].playerCount
          }
        })

        stage.addActor(joinLobbyButton)
      }
    }
  }

  private val backButton = BackButton("MainMenu", stateManager, stage)

  private fun createLobby() {
    println("create lobby: name ${nameListener.value}, password ${passwordListener.value}")
    runBlocking {
      lobbyID = (ApiClient.createLobbyRequest(nameListener.value, passwordListener.value))
    }
    println("lobby ID: $lobbyID")
  }

  private fun joinLobby() {
    var accessToken: Long?
    runBlocking {
      val connectionNumber = Game.receiver.connectAsync()
      val response = ApiClient.joinLobbyRequest(lobbyID!!, passwordListener.value, connectionNumber)
      accessToken = response.accessToken
    }
    accessToken?.let {
      if (lobbyPlayerCount == 0) {
        lobbyPlayerCount = 1
      }
      val lobby = Lobby(lobbyID!!, nameListener.value, accessToken!!, lobbyPlayerCount ?: 1)
      val lobbyState = LobbyState(stateManager, lobby)
      Game.receiver.addObserver(lobbyState)
      stateManager.set(lobbyState)
    }
    resetLobbyInfo()
  }

  fun resetLobbyInfo(){
    nameListener.input("")
    passwordListener.input("")
    mode = 1 //TODO: remove this when join lobby (api) is implemented (lobbyID-check is enough)
    lobbyID = null
    lobbyPlayerCount = null
    println("Lobby info reset")
  }

  override fun update(deltaTime: Float) {
    backButton.update()
    if(passwordListener.value.isNotEmpty() && nameListener.value.isNotEmpty() && lobbyID == null){
      createLobby()
    }
    if(passwordListener.value.isNotEmpty() && nameListener.value.isNotEmpty() && mode == 1 && lobbyID != null){
      joinLobby()
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()
    sprites.draw(background, 0F, 0F, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)
    lobbyList?.let {
      val xPosition: Float = Constants.GAME_WIDTH / 2 - 140f
      font.draw(sprites, "LOBBIES", xPosition, Constants.GAME_HEIGHT/2+70)
      font.draw(sprites, "# players", xPosition + 210f, Constants.GAME_HEIGHT/2+70)
      for (lobbyIndex in lobbyList!!.indices) {
        val yPosition: Float = (Constants.GAME_HEIGHT / 2) + 40f - (30f * lobbyIndex)
        font.draw(sprites, lobbyList!![lobbyIndex].name, xPosition, yPosition)
        font.draw(sprites, lobbyList!![lobbyIndex].playerCount.toString(), xPosition + 270f, yPosition)
      }
    }
    font.draw(sprites, "CREATE LOBBY", Constants.GAME_WIDTH/2-70, Constants.GAME_HEIGHT/2-175)
    sprites.end()
    stage.draw()
  }
  override fun dispose() {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.removeProcessor(stage)
    }
    stage.clear()
    stage.dispose()
    nameListener.dispose()
    passwordListener.dispose()
    background.dispose()
    font.dispose()

    backButton.dispose()

    for (texture in joinLobbyTextures) {
      texture.dispose()
    }
    for (button in joinLobbyButtons) {
      button.clearListeners()
    }

    println("LobbyListState disposed")
  }
}