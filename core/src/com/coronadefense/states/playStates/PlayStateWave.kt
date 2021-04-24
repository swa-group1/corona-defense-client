package com.coronadefense.states.playStates

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coronadefense.states.StateManager
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.GameObserver
import com.coronadefense.states.InputState
import com.coronadefense.states.gameEndStates.EndGameState
import com.coronadefense.types.gameObjects.Intruder
import com.coronadefense.types.gameObjects.MovingGameObject
import com.coronadefense.types.gameObjects.Projectile
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.LARGE_ICON_SIZE
import com.coronadefense.utils.Constants.SIDEBAR_WIDTH
import com.coronadefense.utils.Font
import com.coronadefense.utils.Textures

class PlayStateWave(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : InputState(stateManager) {
  private val sidebarTexture: Texture = Texture(Textures.background("sidebar"))
  private val heartTexture: Texture = Texture(Textures.icon("heart"))
  private val moneyTexture: Texture = Texture(Textures.icon("money"))
  private val font = Font(32)

  private val stageMapTexture: Texture = Texture(Textures.stage(gameObserver.gameStage!!.Number))
  private val stageMap = Image(stageMapTexture)

  init {
    stageMap.setSize(GAME_WIDTH - SIDEBAR_WIDTH, GAME_HEIGHT)
    stageMap.setPosition(0f, 0f)
    stage.addActor(stageMap)
  }

  private var time: Float = 0f //time passed
  private var nextRound: Int = 0 // number of next round, set when all animations are received

  private var movingGameObjects = mutableListOf<MovingGameObject>()

  override fun update(deltaTime: Float) {
    val removePathAnimations = mutableListOf<PathToPathAnimationMessage>()
    val removeBoardAnimations = mutableListOf<BoardToPathAnimationMessage>()
    if (time + deltaTime <= gameObserver.timeConfirmed) {
      val newTime = time + deltaTime
      time += deltaTime
      for (message in gameObserver.pathToPathAnimations) {
        if (message.startTime < newTime) {
          movingGameObjects.add(Intruder(
            message.spriteNumber,
            message.startPosition,
            message.endPosition,
            message.endTime - message.startTime,
            gameObserver.gameStage!!
          ))
          removePathAnimations += message
        }
      }
      for (message in gameObserver.boardToPathAnimations) {
        if (message.startTime < newTime) {
          movingGameObjects.add(Projectile(
            message.spriteNumber,
            message.startX,
            message.startY,
            message.endPosition,
            message.endTime - message.startTime,
            gameObserver.gameStage!!
          ))
          removeBoardAnimations += message
        }
      }
      for (message in gameObserver.moneyAnimations) {
        if (message.time < newTime) {
          gameObserver.money = message.newValue
        }
      }
      for (message in gameObserver.healthAnimations) {
        if (message.time < newTime) {
          gameObserver.health = message.newValue
        }
      }
      for (movingGameObject in movingGameObjects) {
        movingGameObject.update(deltaTime)
      }
    } else if (gameObserver.endGame && gameObserver.timeConfirmed > 0) {
      stateManager.set(EndGameState(stateManager, gameObserver.endGameMessage!!))
    } else if (gameObserver.timeConfirmed > 0) {
      gameObserver.healthAnimations.clear()
      gameObserver.moneyAnimations.clear()
      stateManager.set(PlayStatePlacement(stateManager, gameObserver))
    }
    for (message in removePathAnimations) {
      gameObserver.pathToPathAnimations -= message
    }
    for (message in removeBoardAnimations) {
      gameObserver.boardToPathAnimations -= message
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined

    sprites.begin()
    sprites.draw(sidebarTexture, GAME_WIDTH - SIDEBAR_WIDTH, 0f, SIDEBAR_WIDTH, GAME_HEIGHT)
    sprites.end()

    super.draw()

    sprites.begin()

    sprites.draw(
      heartTexture,
      GAME_WIDTH - (SIDEBAR_WIDTH + LARGE_ICON_SIZE) / 2,
      GAME_HEIGHT * 9/12 - LARGE_ICON_SIZE / 2,
      LARGE_ICON_SIZE,
      LARGE_ICON_SIZE
    )
    val healthText = gameObserver.health.toString()
    font.draw(
      sprites,
      healthText,
      GAME_WIDTH - (SIDEBAR_WIDTH + font.width(healthText)) / 2,
      GAME_HEIGHT * 7/12 + font.height(healthText) / 2
    )

    sprites.draw(
      moneyTexture,
      GAME_WIDTH - (SIDEBAR_WIDTH + LARGE_ICON_SIZE) / 2,
      GAME_HEIGHT * 4/12 - LARGE_ICON_SIZE / 2,
      LARGE_ICON_SIZE,
      LARGE_ICON_SIZE
    )
    val moneyText = gameObserver.money.toString()
    font.draw(
      sprites,
      moneyText,
      GAME_WIDTH - (SIDEBAR_WIDTH + font.width(moneyText)) / 2,
      GAME_HEIGHT * 2/12 + font.height(moneyText) / 2
    )

    for (movingGameObject in movingGameObjects) {
      movingGameObject.draw(sprites)
    }

    for (tower in gameObserver.placedTowers) {
      sprites.draw(
              Texture(Textures.tower(tower.type)),
              tower.position.x * gameObserver.gameStage!!.tileWidth,
              tower.position.y * gameObserver.gameStage!!.tileHeight,
              gameObserver.gameStage!!.tileWidth,
              gameObserver.gameStage!!.tileHeight
      )
    }
    for (movingGameObject in movingGameObjects) {
      movingGameObject.draw(sprites)
    }
    sprites.end()
  }

  override fun dispose() {
    super.dispose()

    for (movingGameObject in movingGameObjects) {
      movingGameObject.dispose()
    }
  }
}