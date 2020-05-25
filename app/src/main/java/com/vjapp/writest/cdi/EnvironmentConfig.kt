package com.vjapp.writest.cdi

import com.vjapp.writest.BuildConfig

object EnvironmentConfig {
    //const val APP_CONFIG_URL = "https://ssl.engds.it/build/lispa/fse/config/android/config_app_${BuildConfig.VERSION_CONFIG}.json"
    const val BASE_DOMAIN = "https://localhost:8080"
    const val BASE_URL = "https://$BASE_DOMAIN/"

    val allowedSSlFingerprints = emptyList<String>()
    /*
    val allowedSSlFingerprints = listOf(
        "sha256/ecc..."
    )
    */
}