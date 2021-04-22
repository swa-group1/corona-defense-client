package com.coronadefense.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator

object Font {
  fun generateFont(size: Int): BitmapFont {
    val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("mainfont.ttf"))
    val fontParameter: FreeTypeFontGenerator.FreeTypeFontParameter = FreeTypeFontGenerator.FreeTypeFontParameter()
    fontParameter.size = size
    fontParameter.genMipMaps = true
    fontGenerator.scaleForPixelHeight(size)
    fontParameter.minFilter = Texture.TextureFilter.MipMapLinearNearest
    fontParameter.magFilter = Texture.TextureFilter.MipMapLinearNearest
    return fontGenerator.generateFont(fontParameter)
  }

  fun textWidth(font: BitmapFont, text: String): Float {
    val glyphLayout = GlyphLayout()
    glyphLayout.setText(font, text)
    return glyphLayout.width
  }
}