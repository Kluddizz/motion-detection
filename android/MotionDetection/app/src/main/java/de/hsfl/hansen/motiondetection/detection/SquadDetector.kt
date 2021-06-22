package de.hsfl.hansen.motiondetection.detection

import android.graphics.Bitmap
import de.hsfl.hansen.motiondetection.camera.CameraListener

class SquadDetector : SquadNet(), CameraListener {

  override fun onCameraFrame(bitmap: Bitmap) {
    val motionInfo = analyze(bitmap)
  }

}