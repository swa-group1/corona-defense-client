package com.coronadefense.states.playStates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.states.StateManager
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.GameObserver
import com.coronadefense.states.gameEndStates.EndGameState
import com.coronadefense.types.gameObjects.Intruder
import com.coronadefense.types.gameObjects.MovingGameObject
import com.coronadefense.types.gameObjects.Projectile
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.LARGE_ICON_SIZE
import com.coronadefense.utils.Font
import com.coronadefense.utils.Textures

class PlayStateWave(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : PlayState(stateManager, gameObserver) {
  private val bigFont = Font(32)

  private var time: Float = 0f //time passed

  private var movingGameObjects = mutableListOf<MovingGameObject>()

  override fun update(deltaTime: Float) {
    if (super.update()) {
      return
    }

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
    sprites.draw(
      Textures.stage(gameObserver.gameStage!!.Number),
      0f,
      0f,
      Constants.GAME_WIDTH - Constants.SIDEBAR_WIDTH,
      GAME_HEIGHT
    )
    sprites.end()

    super.draw()

    sprites.begin()

    for (movingGameObject in movingGameObjects) {
      movingGameObject.draw(sprites)
    }

    for (tower in gameObserver.placedTowers) {
      sprites.draw(
        Textures.tower(tower.type),
        tower.positionX * gameObserver.gameStage!!.tileWidth,
        tower.positionY * gameObserver.gameStage!!.tileHeight,
        gameObserver.gameStage!!.tileWidth,
        gameObserver.gameStage!!.tileHeight
      )
    }

    for (movingGameObject in movingGameObjects) {
      movingGameObject.draw(sprites)
    }

    super.renderSidebar(sprites)

    // Health display
    sprites.draw(
      Textures.icon("heart"),
      centerPositionX - LARGE_ICON_SIZE * 0.5f,
      GAME_HEIGHT * 0.75f - LARGE_ICON_SIZE * 0.5f,
      LARGE_ICON_SIZE,
      LARGE_ICON_SIZE
    )
    val healthText = gameObserver.health.toString()
    bigFont.draw(
      sprites,
      healthText,
      centerPositionX - bigFont.width(healthText) * 0.5f,
      GAME_HEIGHT * 0.6f + bigFont.height(healthText) * 0.5f
    )

    // Money display
    sprites.draw(
      Textures.icon("money"),
      centerPositionX - LARGE_ICON_SIZE * 0.5f,
      GAME_HEIGHT * 0.35f - LARGE_ICON_SIZE * 0.5f,
      LARGE_ICON_SIZE,
      LARGE_ICON_SIZE
    )
    val moneyText = gameObserver.money.toString()
    bigFont.draw(
      sprites,
      moneyText,
      centerPositionX - bigFont.width(moneyText) * 0.5f,
      GAME_HEIGHT * 0.2f + bigFont.height(moneyText) * 0.5f
    )

    sprites.end()
  }

  override fun dispose() {
    super.dispose()
    bigFont.dispose()
  }
}