package de.hsfl.hansen.motiondetection.detection

import android.graphics.Bitmap

interface MotionNet<T : MotionObject> {
  fun analyze(bitmap: Bitmap) : T
}