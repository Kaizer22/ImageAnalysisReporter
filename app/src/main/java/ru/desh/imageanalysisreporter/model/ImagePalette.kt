package ru.desh.imageanalysisreporter.model

import com.google.gson.annotations.SerializedName

class ImagePalette {
    @SerializedName("rgb_colors")
    var colors: Array<Array<Int>>? = null
}