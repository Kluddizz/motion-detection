package de.hsfl.hansen.motiondetection.detection

import android.graphics.Bitmap

interface MoveNet<T> {
  fun analyze(bitmap: Bitmap) : T
}