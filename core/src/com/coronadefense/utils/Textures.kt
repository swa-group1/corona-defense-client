package com.coronadefense.utils

import com.badlogic.gdx.graphics.Texture

object Textures {
  fun disposeAll() {
    disposeButtons()
    disposeStages()
    disposeIntruders()
    disposeTowers()
    disposeProjectiles()
    disposeBackgrounds()
    disposeIcons()
  }

  private val buttons: MutableMap<String, Texture> = mutableMapOf()
  fun button(type: String): Texture {
    if (buttons.containsKey(type)) {
      buttons[type] = Texture("buttons/$type.png")
    }
    return buttons[type]!!
  }
  fun disposeButtons() {
    for ((type, texture) in buttons) {
      texture.dispose()
      buttons.remove(type)
    }
  }

  private val stages: MutableMap<Int, Texture> = mutableMapOf()
  fun stage(number: Int): Texture {
    if (stages.containsKey(number)) {
      stages[number] = Texture("stages/stage_${number.toString().padStart(3, '0')}_img.png")
    }
    return stages[number]!!
  }
  fun disposeStages() {
    for ((number, texture) in stages) {
      texture.dispose()
      stages.remove(number)
    }
  }

  private val intruders: MutableMap<Int, Texture> = mutableMapOf()
  fun intruder(number: Int): Texture {
    if (intruders.containsKey(number)) {
      intruders[number] = Texture("intruders/${number.toString().padStart(3, '0')}.png")
    }
    return intruders[number]!!
  }
  fun disposeIntruders() {
    for ((number, texture) in intruders) {
      texture.dispose()
      intruders.remove(number)
    }
  }

  private val towers: MutableMap<Int, Texture> = mutableMapOf()
  fun tower(number: Int): Texture {
    if (towers.containsKey(number)) {
      towers[number] = Texture("towers/1${(number * 2).toString().padStart(2, '0')}.png")
    }
    return towers[number]!!
  }
  fun disposeTowers() {
    for ((number, texture) in towers) {
      texture.dispose()
      towers.remove(number)
    }
  }

  private val projectiles: MutableMap<Int, Texture> = mutableMapOf()
  fun projectile(number: Int): Texture {
    if (projectiles.containsKey(number)) {
      projectiles[number] = Texture("projectiles/1${(number * 2).toString().padStart(2, '0')}.png")
    }
    return projectiles[number]!!
  }
  fun disposeProjectiles() {
    for ((number, texture) in projectiles) {
      texture.dispose()
      projectiles.remove(number)
    }
  }

  private val backgrounds: MutableMap<String, Texture> = mutableMapOf()
  fun background(type: String): Texture {
    if (backgrounds.containsKey(type)) {
      backgrounds[type] = Texture("backgrounds/$type.png")
    }
    return backgrounds[type]!!
  }
  fun disposeBackgrounds() {
    for ((type, texture) in backgrounds) {
      texture.dispose()
      backgrounds.remove(type)
    }
  }

  private val icons: MutableMap<String, Texture> = mutableMapOf()
  fun icon(type: String): Texture {
    if (icons.containsKey(type)) {
      icons[type] = Texture("icons/$type.png")
    }
    return icons[type]!!
  }
  fun disposeIcons() {
    for ((type, texture) in icons) {
      texture.dispose()
      icons.remove(type)
    }
  }
}