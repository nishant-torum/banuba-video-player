package com.videoplayerbanuba


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import com.banuba.sdk.core.VideoResolution
import com.banuba.sdk.core.ext.toPx
import com.banuba.sdk.core.media.MediaFileNameHelper
import com.banuba.sdk.effectplayer.adapter.BanubaEffectPlayerKoinModule
import com.banuba.sdk.export.data.*
import com.banuba.sdk.export.di.VeExportKoinModule
import com.banuba.sdk.playback.VideoPlayer
import com.banuba.sdk.playback.di.VePlaybackSdkKoinModule
import com.banuba.sdk.token.storage.di.TokenStorageKoinModule
import com.banuba.sdk.ve.di.VeSdkKoinModule
import com.banuba.sdk.ve.domain.VideoRangeList
import com.banuba.sdk.ve.effects.Effects
import com.banuba.sdk.ve.effects.music.MusicEffect
import com.banuba.sdk.ve.effects.watermark.WatermarkAlignment
import com.banuba.sdk.ve.effects.watermark.WatermarkBuilder
import com.banuba.sdk.ve.effects.watermark.WatermarkProvider
import com.banuba.sdk.ve.ext.withWatermark
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class VideoEditorIntegrationModule {

    companion object {
        /**
         * true - enables custom audio browser implementation in this sample
         * false - default implementation
         */
        const val CONFIG_ENABLE_CUSTOM_AUDIO_BROWSER = false
    }

    fun initialize(applicationContext: Context) {

        startKoin {
            androidContext(applicationContext)
            allowOverride(true)
            Log.e("second here", applicationContext.toString())
            // IMPORTANT! order of modules is required
            modules(

                VeSdkKoinModule().module,
                VeExportKoinModule().module,
                VePlaybackSdkKoinModule().module,
                TokenStorageKoinModule().module,
                // Module is required for applying Face AR masks
                BanubaEffectPlayerKoinModule().module,
                SampleModule().module

            )
        }
    }
}

private class SampleModule {

    val module = module {

//
//        viewModel {
//            PlaybackViewModel(
//                context = androidContext(),
//                videoValidator = VideoGalleryResourceValidator(
//                    context = androidContext()
//                ),
//                videoPlayer = get(named("myVideoPlayer")))
//
//        }


        factory<ExportParamsProvider> {
            CustomExportParamsProvider(
                exportDir = get(named("exportDir")),
                mediaFileNameHelper = get(),
                watermarkBuilder = get()
            )
        }

        single<WatermarkProvider> {
            object : WatermarkProvider {
                override fun getWatermarkBitmap(): Bitmap? = BitmapFactory.decodeResource(
                    androidContext().resources,
                    com.banuba.sdk.ve.R.drawable.df_fsfw
                )
            }
        }

    }


}





private class CustomExportParamsProvider(
    private val exportDir: Uri,
    private val mediaFileNameHelper: MediaFileNameHelper,
    private val watermarkBuilder: WatermarkBuilder
) : ExportParamsProvider {

    override fun provideExportParams(
        effects: Effects,
        videoRangeList: VideoRangeList,
        musicEffects: List<MusicEffect>,
        videoVolume: Float
    ): List<ExportParams> {
        val exportSessionDir = exportDir.toFile().apply {
            // Export dir must be created
            mkdirs()
        }

        // Specify name for your exported video. Do not use ext i.e. .mp4
        val exportVideoFileName = mediaFileNameHelper.generateExportName() + "_watermark"

        val paramsHdWithWatermark =
            ExportParams.Builder(VideoResolution.Exact.HD) // Video Quality resolution
                .effects(effects.withWatermark(watermarkBuilder, WatermarkAlignment.BottomRight(marginRightPx = 16.toPx)))
                .fileName(exportVideoFileName)
                .debugEnabled(true)
                .videoRangeList(videoRangeList)
                .destDir(exportSessionDir)
                .musicEffects(musicEffects)
                .volumeVideo(videoVolume)
                .build()

        return listOf(paramsHdWithWatermark)
    }
}