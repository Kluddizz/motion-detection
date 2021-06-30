package de.hsfl.hansen.motiondetection.detection

import android.graphics.Bitmap
import de.hsfl.hansen.motiondetection.utils.ImageHelper.Companion.rotate
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.MappedByteBuffer

open class SquadNet(model: MappedByteBuffer) : MoveNet<Person> {

  private var interpreter: Interpreter = Interpreter(model)
  private var inputShape: IntArray
  private var outputShape: IntArray

  init {
    inputShape = interpreter.getInputTensor(0).shape()
    outputShape = interpreter.getOutputTensor(0).shape()
  }

  private fun processInputImage(bitmap: Bitmap, inputWidth: Int, inputHeight: Int): TensorImage? {
    val imageProcessor = ImageProcessor.Builder().apply {
      add(ResizeOp(inputWidth, inputHeight, ResizeOp.ResizeMethod.BILINEAR))
    }.build()

    val tensorImage = TensorImage(DataType.FLOAT32)
    tensorImage.load(bitmap)

    return imageProcessor.process(tensorImage)
  }

  override fun analyze(bitmap: Bitmap): Person {
    val rotatedBitmap = bitmap.rotate(90.0f)

    val inputWidth = inputShape[1]
    val inputHeight = inputShape[2]
    val inputTensor = processInputImage(rotatedBitmap, inputWidth, inputHeight)
    val outputTensor = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)
    val numberKeyPoints = outputShape[2]
    val person = Person(numberKeyPoints)

    inputTensor?.let { input ->
      interpreter.run(input.tensorBuffer.buffer, outputTensor.buffer)
      val output = outputTensor.floatArray

      val keyPoints = mutableListOf<Keypoint>()

      for (i in 0 until numberKeyPoints) {
        // Read out the buffer (3 components: x, y, score).
        val x = output[i * 3 + 1]
        val y = output[i * 3 + 0]
        val score = output[i * 3 + 2]

        // Create key point and add it to a temporary list.
        val keyPoint = Keypoint(x, y, score)
        keyPoints.add(keyPoint)
      }

      person.keypoints = keyPoints.toTypedArray()
    }

    return person
  }

}