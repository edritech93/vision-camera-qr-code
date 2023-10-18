package com.visioncameraqrcode

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.mrousavy.camera.frameprocessor.FrameProcessorPluginRegistry

class VisionCameraQrCodePluginPackage : ReactPackage {
  init {
    FrameProcessorPluginRegistry.addFrameProcessorPlugin("scanCode") { options ->
      VisionCameraQrCodePlugin()
    }
  }

  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return emptyList()
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return emptyList()
  }
}
