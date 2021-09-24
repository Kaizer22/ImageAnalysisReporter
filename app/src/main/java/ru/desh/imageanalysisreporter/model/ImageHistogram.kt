package ru.desh.imageanalysisreporter.model

import com.google.gson.annotations.SerializedName

class ImageHistogram {
    @SerializedName("red_hist")
    var red: Array<Float>? = null
    @SerializedName("green_hist")
    var green: Array<Float>? = null
    @SerializedName("blue_hist")
    var blue: Array<Float>? = null
}