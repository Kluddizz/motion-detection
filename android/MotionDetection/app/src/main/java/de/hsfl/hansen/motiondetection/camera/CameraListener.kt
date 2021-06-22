package de.hsfl.hansen.motiondetection.camera

import android.graphics.Bitmap

interface CameraListener {
  fun onCameraFrame(bitmap: Bitmap)
}