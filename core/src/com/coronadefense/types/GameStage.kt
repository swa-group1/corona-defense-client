package com.coronadefense.types

import com.badlogic.gdx.math.Vector2
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.SIDEBAR_WIDTH
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.math.sqrt

private const val HALF_OF_STEP_SIZE: Int = 10
private const val STEPS_IN_TILE: Int = 2 * HALF_OF_STEP_SIZE

/**
 * Data class for stages.
 * Note the getPointAlongPathMethod used to transform a distance along the path to an X,Y-point.
 * @param Number Unique number of this stage.
 * @param Name The display name of this stage.
 * @param XSize The number of tile columns in x direction.
 * @param YSize The number of tile columns in y direction.
 * @param BlockedTiles Tiles that towers can not occupy.
 * @param PathPoints Points that the path passes through.
 */
@Serializable
class GameStage(
  /**
   * Gets a unique number of this stage.
   */
  val Number: Int,

  /**
   * Gets the display name of this stage.
   */
  val Name: String,

  /**
   * Gets the number of tile columns in x direction.
   */
  val XSize: Int,

  /**
   * Gets the number of tile columns in y direction.
   */
  val YSize: Int,

  /**
   * Gets tiles that towers can not occupy.
   */
  val BlockedTiles: List<Tile>,

  /**
   * Gets points that the path passes through.
   */
  val PathPoints: List<Point>,
) {
  companion object {
    /**
     * create a new GameStage object.
     * @param jsonContent The string with JSON content.
     * @return new GameStage object.
     */
    fun parse(jsonContent: String): GameStage {
      return Json.decodeFromString(jsonContent)
    }
  }

  private val cumulativePathLengths: MutableList<Double> = mutableListOf()
  private val lineSegmentsX: MutableList<AffineLine> = mutableListOf()
  private val lineSegmentsY: MutableList<AffineLine> = mutableListOf()

  val tileWidth = (GAME_WIDTH - SIDEBAR_WIDTH) / XSize
  val tileHeight = GAME_HEIGHT / YSize

  init {
    this.calculatePath()
  }

  /**
   * Helper function to save path functions for different line segments.
   * Used for performance reasons.
   */
  private fun calculatePath() {

    this.cumulativePathLengths.clear()
    this.lineSegmentsX.clear()
    this.lineSegmentsY.clear()

    // Adding edge-case logic for requesting a point before the path.
    this.cumulativePathLengths.add(0.0)
    this.lineSegmentsX.add(AffineLine.constant(this.PathPoints[0].X))
    this.lineSegmentsY.add(AffineLine.constant(this.PathPoints[0].Y))

    var cumulativePathLength = 0.0

    for (i: Int in 1 until this.PathPoints.size) {
      val first: Point = this.PathPoints[i - 1]
      val second: Point = this.PathPoints[i]
      val deltaX: Double = second.X - first.X
      val deltaY: Double = second.Y - first.Y
      val newCumulativePathLength: Double = cumulativePathLength + sqrt(deltaX * deltaX + deltaY * deltaY)

      this.lineSegmentsX.add(
        AffineLine(
          cumulativePathLength,
          first.X,
          newCumulativePathLength,
          second.X
        )
      )
      this.lineSegmentsY.add(
        AffineLine(
          cumulativePathLength,
          first.Y,
          newCumulativePathLength,
          second.Y
        )
      )

      cumulativePathLength = newCumulativePathLength
      this.cumulativePathLengths.add(cumulativePathLength)
    }

    // Adding edge-case logic for requesting a point after path.
    this.cumulativePathLengths.add(Double.MAX_VALUE)
    this.lineSegmentsX.add(AffineLine.constant(this.PathPoints[this.PathPoints.size - 1].X))
    this.lineSegmentsY.add(AffineLine.constant(this.PathPoints[this.PathPoints.size - 1].Y))
  }

  /**
   * Get a point with X and Y coordinates from a point defined along the path.
   * The function has been enhanced for performance so it is safe to use it often.
   * @return Point with X and Y.
   * @param length distance along the path.
   */
  fun getPointAlongPath(length: Double): Point {
    var insertIndex: Int = this.cumulativePathLengths.binarySearch(length)
    if (insertIndex < 0) insertIndex = insertIndex.inv()
    return Point(
      this.lineSegmentsX[insertIndex].evaluate(length),
      this.lineSegmentsY[insertIndex].evaluate(length),
    )
  }

  /**
   * Line segment for linear movement.
   */
  @Serializable
  private class AffineLine {
    companion object {
      fun constant(constant: Double): AffineLine {
        return AffineLine(0.0, constant)
      }
    }

    private val a: Double
    private val b: Double

    constructor(pathLength0: Double, value0: Double, pathLength1: Double, value1: Double) {
      this.a = (value1 - value0) / (pathLength1 - pathLength0)
      this.b = value0 - this.a * pathLength0
    }

    private constructor(a: Double, b: Double) {
      this.a = a
      this.b = b
    }

    fun evaluate(length: Double): Double {
      return this.a * length + this.b
    }

    override fun toString(): String {
      return "AffineLine { a: $a b: $b }"
    }
  }

  /**
   * Reference to a specific point on the game board.
   * @param X X coordinate.
   * @param Y Y coordinate.
   */
  @Serializable
  class Point(var X: Double, var Y: Double) {
    override fun toString(): String {
      return "Point { X: $X, Y: $Y }"
    }

    fun toVector2(): Vector2 {
      return Vector2(X.toFloat(), Y.toFloat())
    }
  }

  /**
   * Reference to a specific tile on the game board.
   * @param X X coordinate.
   * @param Y Y coordinate.
   */
  @Serializable
  class Tile(val X: Int, val Y: Int)
}