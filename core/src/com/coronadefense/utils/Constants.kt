package com.coronadefense.utils

object Constants {
  const val GAME_TITLE = "Corona Defense"
  const val GAME_WIDTH = 800f
  const val GAME_HEIGHT = 450f

  const val SIDEBAR_WIDTH = GAME_WIDTH / 4
  const val SIDEBAR_SPACING = 25f
  const val SHOP_TOWER_SIZE = 75f
  const val SHOP_TOWER_PADDING = 10f

  const val MENU_BUTTON_WIDTH = 180f
  const val MENU_BUTTON_HEIGHT = 60f
  const val MENU_BUTTON_SPACING = 30f
  const val MENU_TITLE_OFFSET = 120f

  const val BOTTOM_BUTTON_OFFSET = -210f

  const val LIST_ITEM_WIDTH = 300f
  const val LIST_ITEM_HEIGHT = 30f
  const val LIST_TEXT_INLINE_OFFSET = 25f

  const val BACK_BUTTON_SIZE = 60f
  const val BACK_BUTTON_X_OFFSET = 100f
  const val BACK_BUTTON_Y_OFFSET = 65f

  const val SMALL_ICON_SIZE = 20f
  const val SMALL_ICON_SPACING = 10f
  const val LARGE_ICON_SIZE = 100f
}

enum class DIFFICULTY(val value: Int) {
  EASY(0),
  MEDIUM(1),
  HARD(2)
}