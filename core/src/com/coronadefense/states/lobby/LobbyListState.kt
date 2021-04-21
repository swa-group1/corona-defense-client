package com.coronadefense.states.lobby

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.LobbyData
import com.coronadefense.states.State
import kotlinx.coroutines.*
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.types.Lobby
import com.coronadefense.receiver.Receiver
import com.coronadefense.utils.BackButton
import com.coronadefense.states.MenuState
import com.coronadefense.utils.Font
import com.coronadefense.utils.TextInputListener

class LobbyListState(stateManager: GameStateManager): State(stateManager)  {
  var lobbyList: List<LobbyData>? = null
  init {
    camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    runBlocking {
      lobbyList = ApiClient.lobbyListRequest()
    }
  }
  private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
  private val stage: Stage = Stage(viewport, Game.batch)
  private val background = Texture("initiate_game_state.jpg")
  private val createLobbyButton = Image(Texture("greenBorder.png"))
  val font: BitmapFont = Font.generateFont(20)
  val nameListener = TextInputListener()
  val passwordListener = TextInputListener()
  var mode = 1 //TODO: remove this when join lobby (api) is implemented (lobbyID-check is enough)
  var lobbyID: Long? = null
  var lobbyPlayerCount: Int? = null

  val joinLobbyTextures: MutableList<Texture> = mutableListOf()
  val joinLobbyButtons: MutableList<Image> = mutableListOf()

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }
    createLobbyButton.setSize(180f, 60f)
    createLobbyButton.setPosition(Game.WIDTH/2-90, Game.HEIGHT/2-210)
    createLobbyButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        mode = 2
        Gdx.input.getTextInput(passwordListener, "Lobby password", "", "password")
        Gdx.input.getTextInput(nameListener, "Lobby name", "", "lobby name")
      }
    })
    stage.addActor(createLobbyButton)
    lobbyList?.let {
      val xPosition: Float = Game.WIDTH / 2 - 160f
      for (lobbyIndex in lobbyList!!.indices) {
        val yPosition: Float = (Game.HEIGHT / 2) + 17f - (30f * lobbyIndex)
        val joinLobbyTexture = Texture("greenBorder.png")
        joinLobbyTextures += joinLobbyTexture
        val joinLobbyButton = Image(joinLobbyTexture)
        joinLobbyButtons += joinLobbyButton
        joinLobbyButton.setSize(310f, 30f)
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

  val backButton = BackButton("MainMenu", stateManager, stage)

  fun createLobby() {
    println("create lobby:")
    println(passwordListener.value)
    println(nameListener.value)
    GlobalScope.launch {
      lobbyID = (ApiClient.createLobbyRequest(nameListener.value, passwordListener.value))
    }
    println("lobby ID:")
    println(lobbyID)
    mode = 1
  }

  fun joinLobby() {
    var accessToken: Long?
    runBlocking {
      val connectionNumber = Game.receiver.connectAsync()
      val response = ApiClient.joinLobbyRequest(lobbyID!!, passwordListener.value, connectionNumber)
      println(response)
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
    println("reset lobby info")
  }


  override fun handleInput() {
  }
  override fun update(deltaTime: Float) {
    backButton.update()
    if(passwordListener.value.isNotEmpty() && nameListener.value.isNotEmpty() && mode == 2 && lobbyID == null){
      createLobby()
    }
    if(passwordListener.value.isNotEmpty() && nameListener.value.isNotEmpty() && mode == 1 && lobbyID != null){
      joinLobby()
    }
  }
  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()
    sprites.draw(background, 0F, 0F, Game.WIDTH, Game.HEIGHT)
    lobbyList?.let {
      val xPosition: Float = Game.WIDTH / 2 - 140f
      font.draw(sprites, "LOBBIES", xPosition, Game.HEIGHT/2+70)
      font.draw(sprites, "# players", xPosition + 210f, Game.HEIGHT/2+70)
      for (lobbyIndex in lobbyList!!.indices) {
        val yPosition: Float = (Game.HEIGHT / 2) + 40f - (30f * lobbyIndex)
        font.draw(sprites, lobbyList!![lobbyIndex].name, xPosition, yPosition)
        font.draw(sprites, lobbyList!![lobbyIndex].playerCount.toString(), xPosition + 270f, yPosition)
      }
    }
    font.draw(sprites, "CREATE LOBBY", Game.WIDTH/2-70, Game.HEIGHT/2-175)
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