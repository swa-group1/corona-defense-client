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
    if (!buttons.containsKey(type)) {
      buttons[type] = Texture("buttons/$type.png")
    }
    return buttons[type]!!
  }
  fun disposeButtons() {
    val iterator = buttons.iterator()
    while (iterator.hasNext()) {
      iterator.next().value.dispose()
      iterator.remove()
    }
  }

  private val stages: MutableMap<Int, Texture> = mutableMapOf()
  fun stage(number: Int): Texture {
    if (!stages.containsKey(number)) {
      stages[number] = Texture("stages/stage_${number.toString().padStart(3, '0')}_img.png")
    }
    return stages[number]!!
  }
  fun disposeStages() {
    val iterator = stages.iterator()
    while (iterator.hasNext()) {
      iterator.next().value.dispose()
      iterator.remove()
    }
  }

  private val intruders: MutableMap<Int, Texture> = mutableMapOf()
  fun intruder(number: Int): Texture {
    if (!intruders.containsKey(number)) {
      intruders[number] = Texture("intruders/${number.toString().padStart(3, '0')}.png")
    }
    return intruders[number]!!
  }
  fun disposeIntruders() {
    val iterator = intruders.iterator()
    while (iterator.hasNext()) {
      iterator.next().value.dispose()
      iterator.remove()
    }
  }

  private val towers: MutableMap<Int, Texture> = mutableMapOf()
  fun tower(number: Int): Texture {
    if (!towers.containsKey(number)) {
      towers[number] = Texture("towers/1${(number * 2).toString().padStart(2, '0')}.png")
    }
    return towers[number]!!
  }
  fun disposeTowers() {
    val iterator = towers.iterator()
    while (iterator.hasNext()) {
      iterator.next().value.dispose()
      iterator.remove()
    }
  }

  private val projectiles: MutableMap<Int, Texture> = mutableMapOf()
  fun projectile(number: Int): Texture {
    if (!projectiles.containsKey(number)) {
      projectiles[number] = Texture("projectiles/${number.toString().padStart(3, '0')}.png")
    }
    return projectiles[number]!!
  }
  fun disposeProjectiles() {
    val iterator = projectiles.iterator()
    while (iterator.hasNext()) {
      iterator.next().value.dispose()
      iterator.remove()
    }
  }

  private val backgrounds: MutableMap<String, Texture> = mutableMapOf()
  fun background(type: String): Texture {
    if (!backgrounds.containsKey(type)) {
      backgrounds[type] = Texture("backgrounds/$type.png")
    }
    return backgrounds[type]!!
  }
  fun disposeBackgrounds() {
    val iterator = backgrounds.iterator()
    while (iterator.hasNext()) {
      iterator.next().value.dispose()
      iterator.remove()
    }
  }

  private val icons: MutableMap<String, Texture> = mutableMapOf()
  fun icon(type: String): Texture {
    if (!icons.containsKey(type)) {
      icons[type] = Texture("icons/$type.png")
    }
    return icons[type]!!
  }
  fun disposeIcons() {
    val iterator = icons.iterator()
    while (iterator.hasNext()) {
      iterator.next().value.dispose()
      iterator.remove()
    }
  }
}