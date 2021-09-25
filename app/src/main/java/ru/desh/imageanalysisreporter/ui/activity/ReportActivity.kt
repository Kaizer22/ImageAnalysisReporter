package ru.desh.imageanalysisreporter.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ru.desh.imageanalysisreporter.R
import android.view.ViewGroup

import android.widget.FrameLayout

import android.view.View

import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.desh.imageanalysisreporter.model.ImagePalette
import ru.desh.imageanalysisreporter.model.Report
import ru.desh.imageanalysisreporter.ui.fragment.*


class ReportActivity : AppCompatActivity() {
    private lateinit var linearLayout: LinearLayout
    private lateinit var reportSettings: Report


    private val REPORT_SETTINGS_KEY = "REPORT_SETTINGS"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        val jsonString = intent.getStringExtra(REPORT_SETTINGS_KEY)
        Log.i("REPORT_SETTINGS", jsonString.toString())
        reportSettings = Json.decodeFromString(jsonString.toString())
        initReport()
    }

    private fun initReport() {
        linearLayout = findViewById(R.id.reportContainer)
        var position = 0
        for (settingKey in reportSettings.settings.keys) {
            val fm = FrameLayout(this)
            fm.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            linearLayout.addView(fm)
            fm.id = ++position
            Log.i("REPORT_SETTINGS", settingKey.toString())
            //TODO replace with Recycler View
            when(settingKey) {
                ReportSettingsActivity.SettingsType.IMAGE_INFO_SETTINGS -> {
                    val fragment = ImageInfoResultFragment(reportSettings.externalFileName)
                    supportFragmentManager.
                    beginTransaction().
                    replace(position, (fragment as Fragment), settingKey.toString()).
                    commit()
                }
                ReportSettingsActivity.SettingsType.FOURIER_TRANSFORM_SETTINGS -> {
                    val fragment = FourierTransformResultFragment(reportSettings.externalFileName)
                    supportFragmentManager.
                    beginTransaction().
                    replace(position, (fragment as Fragment), settingKey.toString()).
                    commit()
                }
                ReportSettingsActivity.SettingsType.IMAGE_HISTOGRAM_SETTINGS -> {
                    val fragment = ImageHistogramResultFragment(reportSettings.externalFileName)
                    supportFragmentManager.
                    beginTransaction().
                    replace(position, (fragment as Fragment), settingKey.toString()).
                    commit()
                }
                ReportSettingsActivity.SettingsType.COLOR_PALETTE_SETTINGS -> {
                    val colorsNumber = reportSettings
                        .settings[ReportSettingsActivity.SettingsType.COLOR_PALETTE_SETTINGS]!![ReportSettingsActivity.COLORS_NUMBER_PARAM_KEY]
                        ?.toInt()
                    val fragment = ColorPaletteResultFragment(reportSettings.externalFileName, colorsNumber)
                    supportFragmentManager.
                    beginTransaction().
                    replace(position, (fragment as Fragment), settingKey.toString()).
                    commit()
                }
                ReportSettingsActivity.SettingsType.BIT_PLANES -> {
                    val fragment = BitPlanesResultFragment(reportSettings.externalFileName)
                    supportFragmentManager.
                    beginTransaction().
                    replace(position, (fragment as Fragment), settingKey.toString()).
                    commit()
                }
                ReportSettingsActivity.SettingsType.EDGE_DETECTION_SETTINGS -> TODO()
                ReportSettingsActivity.SettingsType.IMAGE_SEGMENTATION_SETTINGS -> TODO()
            }
        }
    }
}