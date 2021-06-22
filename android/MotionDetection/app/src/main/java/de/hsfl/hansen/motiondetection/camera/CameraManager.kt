package de.hsfl.hansen.motiondetection.camera

import android.content.Context
import android.graphics.*
import android.util.Size
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import de.hsfl.hansen.motiondetection.camera.CameraListener
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor

class CameraManager(
  private val context: Context,
  private val previewView: PreviewView
) :
  CameraXConfig.Provider,
  ImageAnalysis.Analyzer {

  private var listeners: MutableList<CameraListener> = mutableListOf()

  override fun getCameraXConfig(): CameraXConfig {
    return Camera2Config.defaultConfig()
  }

  private fun convertImageToBitmap(image: ImageProxy) : Bitmap {
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    uBuffer.get(nv21, ySize, vSize)
    vBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
    val imageBytes = out.toByteArray()

    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
  }

  override fun analyze(image: ImageProxy) {
    for (listener in listeners) {
      val bitmap = convertImageToBitmap(image)
      image.close()

      Thread {
        listener.onCameraFrame(bitmap)
      }.start()
    }
  }

  private fun bindPreview(cameraProvider: ProcessCameraProvider) {
    val preview = Preview.Builder()
      .build()

    val cameraSelector = CameraSelector.Builder()
      .requireLensFacing(CameraSelector.LENS_FACING_BACK)
      .build()

    val imageAnalysis = ImageAnalysis.Builder()
      .setTargetResolution(Size(1280, 720))
      .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
      .build()

    val executor = ContextCompat.getMainExecutor(context)
    imageAnalysis.setAnalyzer(executor, this)

    preview.setSurfaceProvider(previewView.surfaceProvider)
    cameraProvider.bindToLifecycle(context as LifecycleOwner, cameraSelector, imageAnalysis, preview)
  }

  fun addCameraListener(cameraListener: CameraListener) {
    listeners.add(cameraListener)
  }

  fun removeCameraListener(cameraListener: CameraListener) {
    listeners.remove(cameraListener)
  }

  fun startCamera() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener(Runnable {
      val cameraProvider = cameraProviderFuture.get()
      bindPreview(cameraProvider)
    }, ContextCompat.getMainExecutor(context))
  }
}