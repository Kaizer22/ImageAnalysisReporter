package ru.desh.imageanalysisreporter.network

import retrofit2.Retrofit

object NetworkUtils {
    var baseUrl: String = "Empty"
        private set

    //TODO: store url
    fun initBackendConnection(backendURL: String) {
        baseUrl = backendURL
    }

    fun getRetrofitService(): RetrofitService {
        return RetrofitClient.getClient(baseUrl).create(RetrofitService::class.java)
    }
}