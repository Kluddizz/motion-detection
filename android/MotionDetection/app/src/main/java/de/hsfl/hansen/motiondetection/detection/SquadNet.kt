package de.hsfl.hansen.motiondetection.detection

import android.graphics.Bitmap

open class SquadNet : MotionNet<SquadMotionObject> {

  override fun analyze(bitmap: Bitmap): SquadMotionObject {
    return SquadMotionObject(0.0f, 0, 0, 0.0f)
  }

}