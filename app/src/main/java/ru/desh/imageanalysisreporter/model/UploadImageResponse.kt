package ru.desh.imageanalysisreporter.model

import com.google.gson.annotations.SerializedName

class UploadImageResponse {
    @SerializedName("internal_file_name")
    var externalFileName: String = ""
}