package ru.desh.imageanalysisreporter.model

import com.google.gson.annotations.SerializedName

class ImageInfo {
    var width: String? = null
    var height: String? = null
    var filetype: String? = null
    @SerializedName("color_schema")
    var colorSchema: String? = null
    @SerializedName("size")
    var sizeBytes: String? = null
}