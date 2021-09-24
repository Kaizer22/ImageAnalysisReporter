package ru.desh.imageanalysisreporter.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import ru.desh.imageanalysisreporter.ui.activity.ReportSettingsActivity

@Serializable
class Report {
    lateinit var settings: MutableMap<ReportSettingsActivity.SettingsType, MutableMap<String, String>>
    lateinit var externalFileName: String
}