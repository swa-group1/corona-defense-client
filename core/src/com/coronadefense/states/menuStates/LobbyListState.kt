package com.coronadefense.states.menuStates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.Game
import com.coronadefense.states.StateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.LobbyData
import kotlinx.coroutines.*
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.states.GameObserver
import com.coronadefense.states.InputState
import com.coronadefense.utils.*
import com.coronadefense.utils.Constants.BOTTOM_BUTTON_OFFSET
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.LIST_ITEM_HEIGHT
import com.coronadefense.utils.Constants.LIST_TEXT_INLINE_OFFSET
import com.coronadefense.utils.Constants.LIST_ITEM_SPACING
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
  var lobbyList: List<LobbyData>? = null
  init {
    runBlocking {
      lobbyList = ApiClient.lobbyListRequest()
    }
  }

  private val background = Texture(Textures.background("menu"))
  private val font = Font(20)

  private val nameListener = TextInputListener()
  private val passwordListener = TextInputListener()
  private var lobbyToJoinID: Long? = null
  private var lobbyToJoinPlayerCount: Int? = null

  init {
    val createLobbyTexture = Texture(Textures.button("standard"))
    textures += createLobbyTexture

    val createLobbyButton = Image(createLobbyTexture)
    buttons += createLobbyButton

    createLobbyButton.setSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT)
    createLobbyButton.setPosition(
      (GAME_WIDTH - MENU_BUTTON_WIDTH) / 2,
      GAME_HEIGHT / 2 + BOTTOM_BUTTON_OFFSET
    )

    createLobbyButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        Gdx.input.getTextInput(passwordListener, "Lobby password", "", "password")
        Gdx.input.getTextInput(nameListener, "Lobby name", "", "lobby name")
      }
    })

    stage.addActor(createLobbyButton)

    lobbyList?.let {
      val xPosition: Float = GAME_WIDTH / 2 - LIST_ITEM_WIDTH / 2
      for (lobbyIndex in lobbyList!!.indices) {
        val yPosition: Float =
          (GAME_HEIGHT / 2) + MENU_TITLE_OFFSET - ((LIST_ITEM_HEIGHT + LIST_ITEM_SPACING) * (lobbyIndex + 2))

        val joinLobbyTexture = Texture(Textures.button("standard"))
        textures += joinLobbyTexture

        val joinLobbyButton = Image(joinLobbyTexture)
        buttons += joinLobbyButton
        joinLobbyButton.setSize(LIST_ITEM_WIDTH, LIST_ITEM_HEIGHT)
        joinLobbyButton.setPosition(xPosition, yPosition)

        joinLobbyButton.addListener(object : ClickListener() {
          override fun clicked(event: InputEvent?, x: Float, y: Float) {
            Gdx.input.getTextInput(passwordListener, "Lobby password", "", "password")
            nameListener.input(lobbyList!![lobbyIndex].name)
            lobbyToJoinID = lobbyList!![lobbyIndex].id
            lobbyToJoinPlayerCount = lobbyList!![lobbyIndex].playerCount
          }
        })

        stage.addActor(joinLobbyButton)
      }
    }
  }

  private val backButton = BackButton("MainMenu", stateManager, stage)

  private fun createLobby() {
    println("Create lobby: name ${nameListener.value}")
    runBlocking {
      lobbyToJoinID = (ApiClient.createLobbyRequest(nameListener.value, passwordListener.value))
    }
    println("Lobby ID: $lobbyToJoinID")
  }

  private fun joinLobby() {
    var accessToken: Long?

    runBlocking {
      val connectionNumber = Game.receiver.connectAsync()
      val response = ApiClient.joinLobbyRequest(lobbyToJoinID!!, passwordListener.value, connectionNumber)
      accessToken = response.accessToken
    }

    accessToken?.let {
      val lobby = Lobby(lobbyToJoinID!!, nameListener.value, accessToken!!, lobbyToJoinPlayerCount ?: 1)
      val gameObserver = GameObserver(lobbyToJoinID!!, nameListener.value, accessToken!!, lobbyToJoinPlayerCount ?: 1)
      Game.receiver.addObserver(gameObserver)
      stateManager.set(LobbyState(stateManager, gameObserver))
    }

    resetLobbyInfo()
  }

  private fun resetLobbyInfo(){
    nameListener.input("")
    passwordListener.input("")
    lobbyToJoinID = null
    lobbyToJoinPlayerCount = null
    println("Lobby info reset")
  }

  override fun update(deltaTime: Float) {
    backButton.update()

    if(passwordListener.value.isNotEmpty() && nameListener.value.isNotEmpty()) {
      if (lobbyToJoinID == null) {
        createLobby()
      }
      if (lobbyToJoinID != null) {
        joinLobby()
      }
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(background, 0F, 0F, GAME_WIDTH, GAME_HEIGHT)

    lobbyList?.let {
      val xPosition: Float = (GAME_WIDTH - LIST_ITEM_WIDTH) / 2

      val lobbiesTitle = "LOBBIES"
      font.draw(
        sprites,
        lobbiesTitle,
        xPosition,
        (GAME_HEIGHT - font.height(lobbiesTitle)) / 2 + MENU_TITLE_OFFSET
      )
      val playerCountTitle = "Players"
      font.draw(
        sprites,
        playerCountTitle,
        xPosition + LIST_ITEM_WIDTH  - font.width(playerCountTitle),
        (GAME_HEIGHT - font.height(playerCountTitle)) / 2 + MENU_TITLE_OFFSET
      )

      for (lobbyIndex in lobbyList!!.indices) {
        val yPosition: Float =
          GAME_HEIGHT / 2 + MENU_TITLE_OFFSET - (LIST_ITEM_HEIGHT + LIST_ITEM_SPACING) * (lobbyIndex + 1)

        val lobbyNameText = lobbyList!![lobbyIndex].name
        font.draw(
          sprites,
          lobbyNameText,
          xPosition + LIST_TEXT_INLINE_OFFSET,
          yPosition - font.height(lobbyNameText) / 2
        )
        val playerCountText = lobbyList!![lobbyIndex].playerCount.toString()
        font.draw(
          sprites,
          playerCountText,
          xPosition + LIST_ITEM_WIDTH - LIST_TEXT_INLINE_OFFSET - font.width(playerCountText) / 2,
          yPosition - font.height(playerCountText) / 2
        )
      }
    }

    val createLobbyButtonText = "CREATE LOBBY"
    font.draw(
      sprites,
      createLobbyButtonText,
      (GAME_WIDTH - font.width(createLobbyButtonText)) / 2,
      (GAME_HEIGHT + MENU_BUTTON_HEIGHT + font.height(createLobbyButtonText)) / 2 + BOTTOM_BUTTON_OFFSET
    )

    sprites.end()
    super.draw()
  }

  override fun dispose() {
    super.dispose()

    nameListener.dispose()
    passwordListener.dispose()
    background.dispose()
    font.dispose()
    backButton.dispose()


    println("LobbyListState disposed")
  }
}