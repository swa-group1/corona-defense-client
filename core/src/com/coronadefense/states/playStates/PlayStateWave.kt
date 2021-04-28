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

/**
 * Game state for the "Wave of intruders" phase of the gameplay.
 * Extends PlayState for common playstate functionality.
 * @param stateManager Manager of all game states.
 * @param gameObserver Observes the Receiver for game updates.
 */
class PlayStateWave(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : PlayState(stateManager, gameObserver) {
  private val bigFont = Font(32)

  private var time: Float = 0f //time passed

  // List of all moving game objects to be rendered.
  private var movingGameObjects = mutableListOf<MovingGameObject>()

  override fun update(deltaTime: Float) {
    // If Leave Game button has been pressed, do nothing more.
    if (super.update()) {
      return
    }

    // Lists for sprite changes that have finished rendering and so should be disposed.
    val removePathAnimations = mutableListOf<PathToPathAnimationMessage>()
    val removeBoardAnimations = mutableListOf<BoardToPathAnimationMessage>()

    // Increments the in-game time to determine what to render.
    if (time + deltaTime <= gameObserver.timeConfirmed) {
      val newTime = time + deltaTime
      time += deltaTime

      // Each PathToPathAnimation registered by the GameObserver translates to one Intruder on the map.
      for (message in gameObserver.pathToPathAnimations) {
        // Adds the Intruder once the time has reached their start time.
        if (message.startTime < newTime) {
          movingGameObjects.add(Intruder(
            message.spriteNumber,
            message.startPosition,
            message.endPosition,
            message.endTime - message.startTime,
            gameObserver.gameStage!!
          ))
          // Check off the animation as processed
          removePathAnimations += message
        }
      }

      // Each BoardToPathAnimation registered by the GameObserver translates to one Projectile on the map.
      for (message in gameObserver.boardToPathAnimations) {
        // Adds the Projectile once the time has reached their start time.
        if (message.startTime < newTime) {
          movingGameObjects.add(Projectile(
            message.spriteNumber,
            message.startX,
            message.startY,
            message.endPosition,
            message.endTime - message.startTime,
            gameObserver.gameStage!!
          ))
          // Marks the animation as processed
          removeBoardAnimations += message
        }
      }

      // Each "money animation" translates to a change in the players' currency at the given time
      for (message in gameObserver.moneyAnimations) {
        if (message.time < newTime) {
          gameObserver.money = message.newValue
        }
      }

      // Each "health animation" translates to a change in the players' health points at the given time
      for (message in gameObserver.healthAnimations) {
        if (message.time < newTime) {
          gameObserver.health = message.newValue
        }
      }

      // Update each MovingGameObject to ensure correct positioning.
      for (movingGameObject in movingGameObjects) {
        movingGameObject.update(deltaTime)
      }

    } else if (gameObserver.endGame && gameObserver.timeConfirmed > 0) {
      // If game is ended, change to the appropriate state.
      stateManager.set(EndGameState(stateManager, gameObserver.endGameMessage!!))
    } else if (gameObserver.timeConfirmed > 0) {
      // Clear redundant health and money animations and return to placement phase once the time is up
      gameObserver.healthAnimations.clear()
      gameObserver.moneyAnimations.clear()
      stateManager.set(PlayStatePlacement(stateManager, gameObserver))
    }

    // Remove each message marked as processed
    for (message in removePathAnimations) {
      gameObserver.pathToPathAnimations -= message
    }
    for (message in removeBoardAnimations) {
      gameObserver.boardToPathAnimations -= message
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined

    // Draw the game map first, so intruders do not render over the sidebar
    sprites.begin()
    sprites.draw(
      Textures.stage(gameObserver.gameStage!!.Number),
      0f,
      0f,
      Constants.GAME_WIDTH - Constants.SIDEBAR_WIDTH,
      GAME_HEIGHT
    )
    sprites.end()

    // Renders the Leave Game button
    super.draw()

    sprites.begin()

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

    // Renders the players' remaining health along with an icon
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

    // Renders the players' current money along with an icon
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