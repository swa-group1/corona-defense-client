package com.coronadefense.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator

class Font(size: Int) {
  private val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/mainfont.ttf"))
  private val fontParameter = FreeTypeFontGenerator.FreeTypeFontParameter()
  init {
    fontParameter.size = size
    fontParameter.genMipMaps = true
    fontGenerator.scaleForPixelHeight(size)
    fontParameter.minFilter = Texture.TextureFilter.MipMapLinearNearest
    fontParameter.magFilter = Texture.TextureFilter.MipMapLinearNearest
  }
  private val font: BitmapFont = fontGenerator.generateFont(fontParameter)

  fun width(text: String): Float {
    val glyphLayout = GlyphLayout()
    glyphLayout.setText(font, text)
    return glyphLayout.width
  }

  fun height(text: String): Float {
    val glyphLayout = GlyphLayout()
    glyphLayout.setText(font, text)
    return glyphLayout.height
  }

  fun draw(sprites: SpriteBatch, text: String, xPosition: Float, yPosition: Float) {
    font.draw(sprites, text, xPosition, yPosition)
  }

  fun dispose() {
    font.dispose()
  }
}