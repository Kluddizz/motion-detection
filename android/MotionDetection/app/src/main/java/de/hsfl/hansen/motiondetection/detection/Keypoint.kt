package de.hsfl.hansen.motiondetection.detection

open class Keypoint(
  var x: Float,
  var y: Float,
  var score: Float
) {
  override fun toString(): String {
    return "{${x}, ${y}, ${score}}"
  }
}