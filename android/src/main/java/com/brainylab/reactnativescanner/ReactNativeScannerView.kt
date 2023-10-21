package com.brainylab.reactnativescanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import android.view.Choreographer
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.events.EventDispatcher
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ReactNativeScannerView(context: Context) : LinearLayout(context) {
  private var preview: PreviewView
  private var mCameraProvider: ProcessCameraProvider? = null
  private lateinit var cameraExecutor: ExecutorService
  private lateinit var detector: ObjectDetector
  private lateinit var scanner: BarcodeScanner
  private var analysisUseCase: ImageAnalysis =
    ImageAnalysis.Builder()
      .build()
  private var isWatcherEnabled = false
  private var isObjectDetected = false
  private var isCodeScanned = false
  private var isOnlyCenter = false
  private var isDelayScanner: Long = 2000

  companion object {
    const val TAG = "CameraView"
    private val REQUIRED_PERMISSIONS =
      mutableListOf(
        Manifest.permission.CAMERA,
      ).toTypedArray()
  }

  init {
    val linearLayoutParams =
      ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
      )
    layoutParams = linearLayoutParams
    orientation = VERTICAL

    preview = PreviewView(context)
    preview.layoutParams =
      ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT,
      )
    addView(preview)

    setupLayoutHack()
    manuallyLayoutChildren()
  }

  fun setWatcherMode(value: Boolean) {
    isWatcherEnabled = value
  }

  fun setOnlyCenterMode(value: Boolean) {
    isOnlyCenter = value
  }

  private fun setupLayoutHack() {
    Choreographer.getInstance().postFrameCallback(
      object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
          manuallyLayoutChildren()
          viewTreeObserver.dispatchOnGlobalLayout()
          Choreographer.getInstance().postFrameCallback(this)
        }
      },
    )
  }

  private fun manuallyLayoutChildren() {
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      child.measure(
        MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY),
      )
      child.layout(0, 0, child.measuredWidth, child.measuredHeight)
    }
  }

  fun setBarcodeFormats(formats: Array<String>) {
    val codeTypes = formats.toList()
    val barcodeFormats = codeTypes.map { BarcodeFormatUtils.getBarcodeFormat(it) }

    setUpBarcodeScanning(barcodeFormats)
  }

  fun setUpImageAnalyser() {
    analysisUseCase.setAnalyzer(
      // newSingleThreadExecutor() will let us perform analysis on a single worker thread
      Executors.newSingleThreadExecutor(),
    ) { imageProxy ->
      if (!isWatcherEnabled) {
        processImageObjectDetectorProxy(detector, imageProxy)
      }
      processImageBarCodeProxy(scanner, imageProxy)
    }
  }

  fun setUpBarcodeScanning(barcodeFormats: List<Int>) {
    val format = barcodeFormats.first()

    scanner =
      BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
          .setBarcodeFormats(format, *barcodeFormats.toIntArray())
          .build(),
      )

    detector =
      ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
          .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
          .build(),
      )

    setUpImageAnalyser()
  }

  fun setUpCamera(reactApplicationContext: ReactApplicationContext) {
    if (allPermissionsGranted()) {
      startCamera(reactApplicationContext)
    }

    cameraExecutor = Executors.newSingleThreadExecutor()
  }

  @SuppressLint("UnsafeOptInUsageError")
  private fun processImageObjectDetectorProxy(
    objectDetector: ObjectDetector,
    imageProxy: ImageProxy,
  ) {
    imageProxy.image?.let { image ->
      val inputImage =
        InputImage.fromMediaImage(
          image,
          imageProxy.imageInfo.rotationDegrees,
        )

      objectDetector.process(inputImage)
        .addOnSuccessListener { detectedObjects ->
          if (detectedObjects.isEmpty()) {
            if (isObjectDetected && isCodeScanned) {
              isObjectDetected = false

              if (isCodeScanned) {
                Handler().postDelayed({
                  isCodeScanned = false
                }, isDelayScanner)
              }
            }
          } else {
            if (!isCodeScanned) {
              isObjectDetected = true
            }
          }
        }
        .addOnFailureListener { exception ->
          // Handle exceptions
        }
    }
  }

  @SuppressLint("UnsafeOptInUsageError")
  private fun processImageBarCodeProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
  ) {
    imageProxy.image?.let { image ->
      val inputImage =
        InputImage.fromMediaImage(
          image,
          imageProxy.imageInfo.rotationDegrees,
        )

      barcodeScanner.process(inputImage)
        .addOnSuccessListener { barcodeList ->
          val barcode =
            barcodeList.getOrNull(0) // `rawValue` is the decoded value of the barcode

          barcode?.boundingBox?.let { boundingBox ->
            val centerX = boundingBox.centerX()
            val centerY = boundingBox.centerY()
            val imageWidth = image.width
            val imageHeight = image.height

            barcode?.rawValue?.let { value ->
              val reactContext = context as ReactContext
              val eventDispatcher: EventDispatcher? =
                UIManagerHelper.getEventDispatcherForReactTag(
                  reactContext,
                  id,
                )
              if (isOnlyCenter) {
                if (
                  centerX > imageWidth / 3 && centerX < imageWidth * 2 / 3 &&
                  centerY > imageHeight / 3 && centerY < imageHeight * 2 / 3

                ) {
                  if (isWatcherEnabled) {
                    eventDispatcher?.dispatchEvent(ReactNativeScannerViewEvent(id, value))
                  } else {
                    if (isObjectDetected && !isCodeScanned) {
                      isCodeScanned = true

                      eventDispatcher?.dispatchEvent(ReactNativeScannerViewEvent(id, value))
                    }
                  }
                }
              } else {
                if (isWatcherEnabled) {
                  eventDispatcher?.dispatchEvent(ReactNativeScannerViewEvent(id, value))
                } else {
                  if (isObjectDetected && !isCodeScanned) {
                    isCodeScanned = true

                    eventDispatcher?.dispatchEvent(ReactNativeScannerViewEvent(id, value))
                  }
                }
              }
            }
          }
        }
        .addOnFailureListener {
          // This failure will happen if the barcode scanning model
          // fails to download from Google Play Services
        }
        .addOnCompleteListener {
          // When the image is from CameraX analysis use case, must
          // call image.close() on received images when finished
          // using them. Otherwise, new images may not be received
          // or the camera may stall.
          imageProxy.image?.close()
          imageProxy.close()
        }
    }
  }

  private fun allPermissionsGranted() =
    REQUIRED_PERMISSIONS.all {
      ContextCompat.checkSelfPermission(
        context, it,
      ) == PackageManager.PERMISSION_GRANTED
    }

  private fun startCamera(reactApplicationContext: ReactApplicationContext) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
      // Used to bind the lifecycle of cameras to the lifecycle owner
      val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
      mCameraProvider = cameraProvider
      // Preview
      val surfacePreview =
        Preview.Builder()
          .build()
          .also {
            it.setSurfaceProvider(preview.surfaceProvider)
          }

      // Select back camera as a default
      val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

      try {
        // Unbind use cases before rebinding
        cameraProvider.unbindAll()

        // Bind use cases to camera
        cameraProvider.bindToLifecycle(
          (reactApplicationContext.currentActivity as AppCompatActivity),
          cameraSelector,
          surfacePreview,
          analysisUseCase,
        )
      } catch (exc: Exception) {
        Log.e(TAG, "Use case binding failed", exc)
      }
    }, ContextCompat.getMainExecutor(context))
  }

  fun stopCamera() {
    mCameraProvider!!.unbindAll()
    analysisUseCase.clearAnalyzer()
  }

  // override fun onDetachedFromWindow() {
  //     super.onDetachedFromWindow()

  //     // Stop the camera
  //     stopCamera()
  // }
}
