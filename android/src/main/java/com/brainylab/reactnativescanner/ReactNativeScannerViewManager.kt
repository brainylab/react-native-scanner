package com.brainylab.reactnativescanner

import android.graphics.Color
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.ReactNativeScannerViewManagerInterface
import com.facebook.react.viewmanagers.ReactNativeScannerViewManagerDelegate
import com.facebook.soloader.SoLoader

@ReactModule(name = ReactNativeScannerViewManager.NAME)
class ReactNativeScannerViewManager : SimpleViewManager<ReactNativeScannerView>(),
  ReactNativeScannerViewManagerInterface<ReactNativeScannerView> {
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

  public override fun createViewInstance(context: ThemedReactContext): ReactNativeScannerView {
    return ReactNativeScannerView(context)
  }

  @ReactProp(name = "color")
  override fun setColor(view: ReactNativeScannerView?, color: String?) {
    view?.setBackgroundColor(Color.parseColor(color))
  }

  companion object {
    const val NAME = "ReactNativeScannerView"

    init {
      if (BuildConfig.CODEGEN_MODULE_REGISTRATION != null) {
        SoLoader.loadLibrary(BuildConfig.CODEGEN_MODULE_REGISTRATION)
      }
    }
  }
}
