package com.videoplayerbanuba

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager


class VideoEditorReactPackage : ReactPackage {



    override fun createNativeModules(reactContext: ReactApplicationContext): MutableList<NativeModule> {
        val modules = mutableListOf<NativeModule>()
        modules.add(VideoEditorModule(reactContext))
        return modules
    }



    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return mutableListOf(VideoEditorViewManager())
    }


}