package de.hsfl.hansen.motiondetection.detection

open class Person(numberKeyPoints: Int) {
  var keypoints: Array<Keypoint> = Array(numberKeyPoints) { Keypoint(0.0f, 0.0f, 0.0f) }
}