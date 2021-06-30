package de.hsfl.hansen.motiondetection.detection

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class PersonOverlay(context: Context?, attributeSet: AttributeSet?) : View(context, attributeSet) {

  private var person: Person = Person(17)
  private val paint = Paint().apply {
    style = Paint.Style.STROKE
    color = Color.RED
    strokeWidth = 2.0f
  }

  private fun Canvas.drawLineRelative(startX: Float, startY: Float, stopX: Float, stopY: Float, paint: Paint) {
    this.drawLine(startX * width, startY * height, stopX * width, stopY * height, paint)
  }

  private fun drawTorso(canvas: Canvas?, person: Person) {
    val leftShoulder = person.keypoints[BodyPart.LEFT_SHOULDER.position]
    val rightShoulder = person.keypoints[BodyPart.RIGHT_SHOULDER.position]
    val leftHip = person.keypoints[BodyPart.LEFT_HIP.position]
    val rightHip = person.keypoints[BodyPart.RIGHT_HIP.position]
    val leftElbow = person.keypoints[BodyPart.LEFT_ELBOW.position]
    val rightElbow = person.keypoints[BodyPart.RIGHT_ELBOW.position]
    val leftHand = person.keypoints[BodyPart.LEFT_WRIST.position]
    val rightHand = person.keypoints[BodyPart.RIGHT_WRIST.position]
    val leftKnee = person.keypoints[BodyPart.LEFT_KNEE.position]
    val rightKnee = person.keypoints[BodyPart.RIGHT_KNEE.position]
    val leftAnkle = person.keypoints[BodyPart.LEFT_ANKLE.position]
    val rightAnkle = person.keypoints[BodyPart.RIGHT_ANKLE.position]

    // Body
    canvas?.drawLineRelative(leftShoulder.x, leftShoulder.y, rightShoulder.x, rightShoulder.y, paint)
    canvas?.drawLineRelative(leftShoulder.x, leftShoulder.y, leftHip.x, leftHip.y, paint)
    canvas?.drawLineRelative(rightShoulder.x, rightShoulder.y, rightHip.x, rightHip.y, paint)
    canvas?.drawLineRelative(leftHip.x, leftHip.y, rightHip.x, rightHip.y, paint)
    // Upper arms
    canvas?.drawLineRelative(leftShoulder.x, leftShoulder.y, leftElbow.x, leftElbow.y, paint)
    canvas?.drawLineRelative(rightShoulder.x, rightShoulder.y, rightElbow.x, rightElbow.y, paint)
    // Lower arms
    canvas?.drawLineRelative(leftElbow.x, leftElbow.y, leftHand.x, leftHand.y, paint)
    canvas?.drawLineRelative(rightElbow.x, rightElbow.y, rightHand.x, rightHand.y, paint)
    // Upper legs
    canvas?.drawLineRelative(leftHip.x, leftHip.y, leftKnee.x, leftKnee.y, paint)
    canvas?.drawLineRelative(rightHip.x, rightHip.y, rightKnee.x, rightKnee.y, paint)
    // Lower legs
    canvas?.drawLineRelative(leftKnee.x, leftKnee.y, leftAnkle.x, leftAnkle.y, paint)
    canvas?.drawLineRelative(rightKnee.x, rightKnee.y, rightAnkle.x, rightAnkle.y, paint)
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    drawTorso(canvas, person)

    person.keypoints.forEach {
      canvas?.drawCircle(
        it.x * width.toFloat(),
        it.y * height.toFloat(),
        5.0f,
        paint
      )
    }
  }

  fun drawPerson(person: Person) {
    this.person = person
    invalidate()
  }

}