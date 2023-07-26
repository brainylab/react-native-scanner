package com.brainylab.reactnativescanner

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.ReactNativeScannerViewManagerInterface
import com.facebook.react.viewmanagers.ReactNativeScannerViewManagerDelegate
import com.facebook.soloader.SoLoader



@ReactModule(name = ReactNativeScannerViewManager.NAME)
class ReactNativeScannerViewManager(private val mCallerContext: ReactApplicationContext): SimpleViewManager<ReactNativeScannerView>(),
  ReactNativeScannerViewManagerInterface<ReactNativeScannerView> {

  companion object {
    const val NAME = "ReactNativeScannerView"

    init {
      if (BuildConfig.CODEGEN_MODULE_REGISTRATION != null) {
        SoLoader.loadLibrary(BuildConfig.CODEGEN_MODULE_REGISTRATION)
      }
    }
  }

  private val mDelegate: ViewManagerDelegate<ReactNativeScannerView>

  init {
    mDelegate = ReactNativeScannerViewManagerDelegate(this)
  }

  override fun getDelegate(): ViewManagerDelegate<ReactNativeScannerView>? {
    return mDelegate
  }


  override fun getName(): String {
    return NAME
  }

  override fun createViewInstance(reactContext: ThemedReactContext): ReactNativeScannerView {
    val reactnativeScannerView = ReactNativeScannerView(mCallerContext)
    reactnativeScannerView.setUpCamera(mCallerContext)
    return reactnativeScannerView
  }

  override fun getExportedCustomDirectEventTypeConstants(): Map<String?, Any> {
    return MapBuilder.of(
      "topOnScanner",
      MapBuilder.of("registrationName", "onCodeScanned")
    )
  }
}
