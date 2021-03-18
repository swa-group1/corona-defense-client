package com.coronadefense.states

import com.coronadefense.GameStateManager

class MenuState(stateManager: GameStateManager): State(stateManager) {
    init {
        camera.setToOrtho(false, HelicopterGame.WIDTH, HelicopterGame.HEIGHT)
    }
    private val viewport: Viewport = StretchViewport(HelicopterGame.WIDTH, HelicopterGame.HEIGHT, camera)
    private val stage: Stage = Stage(viewport, HelicopterGame.batch)
    private val table: Table = Table()
    private val task1Img = Image(Texture("task1.png"))
    private val task2Img = Image(Texture("task2.png"))
    private val task3Img = Image(Texture("task3.png"))
    private val task4Img = Image(Texture("task4.png"))
    init {
        val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
        if (!inputMultiplexer.processors.contains(stage)) {
            inputMultiplexer.addProcessor(stage);
        }
        table.left().bottom()
        task1Img.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                stateManager.set(PlayState1(stateManager))
            }
        })
        task2Img.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                stateManager.set(PlayState2(stateManager))
            }
        })
        task3Img.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                stateManager.set(PlayState3(stateManager))
            }
        })
        task4Img.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                stateManager.set(PlayState4(stateManager))
            }
        })
        table.add(task1Img).size((HelicopterGame.WIDTH / 2), HelicopterGame.HEIGHT / 2)
        table.add(task2Img).size((HelicopterGame.WIDTH / 2), HelicopterGame.HEIGHT / 2)
        table.row()
        table.add(task3Img).size((HelicopterGame.WIDTH / 2), HelicopterGame.HEIGHT / 2)
        table.add(task4Img).size((HelicopterGame.WIDTH / 2), HelicopterGame.HEIGHT / 2)
        stage.addActor(table)
    }
    override fun handleInput() {}
    override fun update(deltaTime: Float) {
        handleInput()
    }
    override fun render(sprites: SpriteBatch) {
        sprites.projectionMatrix = camera.combined
        stage.draw()
    }
    override fun dispose(){
        stage.dispose()
    }
}