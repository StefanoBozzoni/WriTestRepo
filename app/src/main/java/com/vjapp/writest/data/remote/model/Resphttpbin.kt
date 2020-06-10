package com.vjapp.writest.data.remote.model

import com.google.gson.annotations.SerializedName

data class Resphttpbin(
    @SerializedName("args")
    val args: Args,
    @SerializedName("headers")
    val headers: Headers,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("url")
    val url: String
)

data class Headers(
    @SerializedName("Accept")
    val accept: String,
    @SerializedName("Accept-Encoding")
    val acceptEncoding: String,
    @SerializedName("Cache-Control")
    val cacheControl: String,
    @SerializedName("Content-Length")
    val contentLength: String,
    @SerializedName("Content-Type")
    val contentType: String,
    @SerializedName("Host")
    val host: String,
    @SerializedName("Postman-Token")
    val postmanToken: String,
    @SerializedName("User-Agent")
    val userAgent: String,
    @SerializedName("X-Amzn-Trace-Id")
    val xAmznTraceId: String
)

class Args(
)