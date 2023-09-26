package com.videoplayerbanuba

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.banuba.sdk.export.data.ExportResult
import com.banuba.sdk.export.utils.EXTRA_EXPORTED_SUCCESS
import com.banuba.sdk.token.storage.license.BanubaVideoEditor
import com.facebook.react.bridge.*
import java.io.*
import java.util.*


class VideoEditorModule(reactContext: ReactApplicationContext ) :
    ReactContextBaseJavaModule(reactContext) {



    companion object {
        const val TAG = "BanubaVideoEditor"

        private const val EXPORT_REQUEST_CODE = 1111
        private const val ERR_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST"
        private const val ERR_VIDEO_EDITOR_CANCELLED = "E_VIDEO_EDITOR_CANCELLED"
        private const val ERR_EXPORTED_VIDEO_NOT_FOUND = "E_EXPORTED_VIDEO_NOT_FOUND"

        private const val ERR_SDK_NOT_INITIALIZED_CODE = "ERR_VIDEO_EDITOR_NOT_INITIALIZED"
        private const val ERR_LICENSE_REVOKED_CODE = "ERR_VIDEO_EDITOR_LICENSE_REVOKED"
        private const val ERR_SDK_NOT_INITIALIZED_MESSAGE =
            "Banuba Video Editor SDK is not initialized: license token is unknown or incorrect.\nPlease check your license token or contact Banuba"
        private const val ERR_LICENSE_REVOKED_MESSAGE =
            "License is revoked or expired. Please contact Banuba https://www.banuba.com/faq/kb-tickets/new";
    }

    private var videoEditorSDK: BanubaVideoEditor? = null



    private var exportResultPromise: Promise? = null
    private var integrationModule: VideoEditorIntegrationModule? = null


    private val videoEditorResultListener = object : ActivityEventListener {
        override fun onActivityResult(
            activity: Activity?,
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        ) {
            if (requestCode == EXPORT_REQUEST_CODE) {
                when {
                    resultCode == Activity.RESULT_OK -> {
                        val exportResult = data?.getParcelableExtra<ExportResult.Success>(
                            EXTRA_EXPORTED_SUCCESS
                        )
                        val exportedVideos = exportResult?.videoList ?: emptyList()
                        val resultUri = exportedVideos.firstOrNull()?.sourceUri

                        if (resultUri == null) {
                            exportResultPromise?.reject(
                                ERR_EXPORTED_VIDEO_NOT_FOUND,
                                "Exported video is null"
                            )
                        } else {
                            exportResultPromise?.resolve(resultUri.toString())
                            /*
                                NOT REQUIRED FOR INTEGRATION
                                Added for playing exported video file.
                            */
//                            activity?.let { demoPlayExportedVideo(it, resultUri) }
                        }
                    }
                    requestCode == Activity.RESULT_CANCELED -> {
                        exportResultPromise?.reject(
                            ERR_VIDEO_EDITOR_CANCELLED,
                            "Video editor export was cancelled"
                        )
                    }
                }
                exportResultPromise = null
            }
        }

        override fun onNewIntent(intent: Intent?) {}
    }


    init {
        reactApplicationContext.addActivityEventListener(videoEditorResultListener)
    }

    override fun getName(): String = "VideoEditorModule"


    @ReactMethod
    fun initVideoEditor(licenseToken: String, inputPromise: Promise) {
        videoEditorSDK = BanubaVideoEditor.initialize(licenseToken)
        Log.e("first here", videoEditorSDK.toString())
        if (videoEditorSDK == null) {
            // Token you provided is not correct - empty or truncated
            Log.e(TAG, ERR_SDK_NOT_INITIALIZED_MESSAGE)
            inputPromise.reject(ERR_SDK_NOT_INITIALIZED_CODE, ERR_SDK_NOT_INITIALIZED_MESSAGE)
        } else {
//            if (integrationModule == null) {
//                Initialize video editor sdk dependencies
            Log.e("first here", integrationModule.toString())

            if (integrationModule == null) {
                // Initialize video editor sdk dependencies
                integrationModule = VideoEditorIntegrationModule().apply {
                    initialize(reactApplicationContext.applicationContext)
                }
            }





            Log.e("second here", integrationModule.toString())

            inputPromise.resolve(null)

        }
    }}

