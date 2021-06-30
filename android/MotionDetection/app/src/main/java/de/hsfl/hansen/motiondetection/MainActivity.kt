package de.hsfl.hansen.motiondetection

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.camera.view.PreviewView
import de.hsfl.hansen.motiondetection.camera.CameraManager
import de.hsfl.hansen.motiondetection.detection.Person
import de.hsfl.hansen.motiondetection.detection.PersonOverlay
import de.hsfl.hansen.motiondetection.detection.SquadDetector
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {
  private lateinit var previewView: PreviewView
  private lateinit var imageView: PersonOverlay
  private lateinit var scoreTextView: TextView
  private lateinit var cameraManager: CameraManager
  private lateinit var detector: SquadDetector

  companion object {
    const val REQUEST_CODE_CAMERA_PERMISSION = 10
  }

  private fun loadModel(modelFile: String) : MappedByteBuffer {
    val fileDescriptor = assets.openFd(modelFile)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    previewView = findViewById(R.id.previewView)
    imageView = findViewById(R.id.imageView)
    scoreTextView = findViewById(R.id.scoreTextView)

    cameraManager = CameraManager(this, previewView)

    val model = loadModel("movenet.tflite")
    detector = SquadDetector(model)

    when {
      checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
        startCameraPreview()
      }
      shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
        requestCameraPermission()
      }
      else -> {
        requestCameraPermission()
      }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      REQUEST_CODE_CAMERA_PERMISSION -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          startCameraPreview()
        } else {
          Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
      }
      else -> {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
      }
    }
  }

  private fun requestCameraPermission() {
    requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION)
  }

  private fun updateMotionInfo() {

  }

  private fun showPermissionInfo() {

  }

  private val detectionCallback = object : SquadDetector.DetectionCallback {
    override fun invoke(bitmap: Bitmap, person: Person) {
      runOnUiThread {
        imageView.drawPerson(person)
        val scoreAvg = person.keypoints.map { it.score }.sum() / person.keypoints.size
        scoreTextView.text = scoreAvg.toString()
      }
    }
  }

  private fun startCameraPreview() {
    detector.detectionCallback = detectionCallback

    cameraManager.listener = detector
    cameraManager.startCamera()
  }

}