package de.hsfl.hansen.motiondetection.detection

import android.graphics.Bitmap
import de.hsfl.hansen.motiondetection.camera.CameraListener
import java.nio.MappedByteBuffer

class SquadDetector(model: MappedByteBuffer) : SquadNet(model), CameraListener {
  var detectionCallback: DetectionCallback? = null

  interface DetectionCallback {
    fun invoke(bitmap: Bitmap, person: Person)
  }

  override fun onCameraFrame(bitmap: Bitmap) {
    val person = analyze(bitmap)
    detectionCallback?.invoke(bitmap, person)
  }

}