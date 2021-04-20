package com.coronadefense.states.lobby
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color.BLACK
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.states.State
import com.coronadefense.utils.Font
import com.coronadefense.utils.TextInputListener


class CreateLobbyState(stateManager: GameStateManager): State(stateManager)  {
    init {
        camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    }
    private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
    private val stage: Stage = Stage(viewport, Game.batch)

    private val background: Texture = Texture("backround.jpg")

    init {
        val font = Font.generateFont(24)
        val skin = TextField.TextFieldStyle(font, BLACK, null, null, null)
        val usernameTextField = TextField("", skin)
        usernameTextField.setPosition(24f, 73f)
        usernameTextField.setSize(88f, 14f)
        stage.addActor(usernameTextField)
        val passwordTextField = TextField("", skin)
        passwordTextField.setPosition(24f, 102f)
        passwordTextField.setSize(88f, 14f)
        stage.addActor(passwordTextField)
        val passwordListener = TextInputListener()
        Gdx.input.getTextInput(passwordListener, "Lobby password", "", "password")
        val nameListener = TextInputListener()
        Gdx.input.getTextInput(nameListener, "Lobby name", "", "lobby name")
    }


    override fun handleInput() {

    }

    override fun update(deltaTime: Float) {

    }

    override fun render(sprites: SpriteBatch) {
        sprites.projectionMatrix = camera.combined
        sprites.begin()
        sprites.draw(background, 0F, 0F, Game.WIDTH, Game.HEIGHT)
        sprites.end()
        stage.draw()
    }

    override fun dispose() {
        background.dispose()

        println("CreateLobby State Disposed")
    }

}