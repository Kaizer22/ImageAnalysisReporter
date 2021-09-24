package ru.desh.imageanalysisreporter.network

import okhttp3.MultipartBody
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.*
import ru.desh.imageanalysisreporter.model.ImageHistogram
import ru.desh.imageanalysisreporter.model.ImageInfo
import ru.desh.imageanalysisreporter.model.ImagePalette
import ru.desh.imageanalysisreporter.model.UploadImageResponse

interface RetrofitService {

    @Multipart
    @POST("/upload-image")
    fun UploadImage(@Part file: MultipartBody.Part): Call<UploadImageResponse>

    @GET("/get-image-info")
    fun GetImageInfo(
        @Query("filename") filename: String
    ): Call<ImageInfo>
    @GET("/get-bit-planes")
    fun GetBitPlanes(
        @Query("filename") filename: String
    ): Call<MultipartBody>
    @GET("/get-color-palette")
    fun GetColorPalette(
        @Query("filename") filename: String,
        @Query("colors_count") colorsNumber: Int
    ): Call<ImagePalette>
    @GET("/get-edge-detection-result")
    fun GetEdgeDetectionResult(
        @Query("filename") filename: String
    ): Call<ResponseBody>
    @GET("/get-image-segmentation-result")
    fun GetImageSegmentationResult(
        @Query("filename") filename: String
    ): Call<ResponseBody>
    @GET("/get-rgb-histogram")
    fun GetRGBHistogram(
        @Query("filename") filename: String
    ): Call<ImageHistogram>
    @GET("/get-fft")
    fun GetFourierTransform(
        @Query("filename") filename: String
    ): Call<ResponseBody>

}