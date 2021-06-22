package de.hsfl.hansen.motiondetection.detection

class SquadMotionObject(
  var angle: Float,
  x: Int,
  y: Int,
  score: Float
) : MotionObject(x, y, score) {
}