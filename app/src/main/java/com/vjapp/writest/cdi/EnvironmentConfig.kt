package com.vjapp.writest.cdi

object EnvironmentConfig {
    const val BASE_DOMAIN = "https://localhost:8080"
    const val BASE_URL = "https://$BASE_DOMAIN/"

    val allowedSSlFingerprints = emptyList<String>()
    /*
    val allowedSSlFingerprints = listOf(
        "sha256/ecc..."
    )
    */
}