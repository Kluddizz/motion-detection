package de.hsfl.hansen.motiondetection

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.view.PreviewView
import de.hsfl.hansen.motiondetection.camera.CameraManager
import de.hsfl.hansen.motiondetection.detection.SquadDetector

class MainActivity : AppCompatActivity() {
  private lateinit var previewView: PreviewView
  private lateinit var imageView: ImageView
  private lateinit var cameraManager: CameraManager
  private lateinit var detector: SquadDetector

  companion object {
    const val REQUEST_CODE_CAMERA_PERMISSION = 10
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    previewView = findViewById(R.id.previewView)
    imageView = findViewById(R.id.imageView)

    cameraManager = CameraManager(this, previewView)
    detector = SquadDetector()

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

  private fun startCameraPreview() {
    cameraManager.addCameraListener(detector)
    cameraManager.startCamera()
  }

}