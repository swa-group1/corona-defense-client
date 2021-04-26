package com.coronadefense.states.playStates

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coronadefense.states.GameObserver
import com.coronadefense.states.InputState
import com.coronadefense.states.StateManager
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Textures

abstract class PlayState(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : InputState(stateManager) {
  private val sidebarTexture = Textures.background("sidebar")
  protected val inactiveButtonTexture = Textures.button("gray")
  protected val heartTexture = Textures.icon("heart")
  protected val moneyTexture = Textures.icon("money")
  private val stageMapTexture = Textures.stage(gameObserver.gameStage!!.Number)

  private val stageMap = Image(stageMapTexture)

  init {
    stageMap.setSize(Constants.GAME_WIDTH - Constants.SIDEBAR_WIDTH, Constants.GAME_HEIGHT)
    stageMap.setPosition(0f, 0f)
    stage.addActor(stageMap)
  }
}