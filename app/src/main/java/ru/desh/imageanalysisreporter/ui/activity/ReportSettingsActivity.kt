package ru.desh.imageanalysisreporter.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.button.MaterialButton
import pub.devrel.easypermissions.EasyPermissions
import ru.desh.imageanalysisreporter.R
import ru.desh.imageanalysisreporter.ui.service.SettingsProvider
import android.view.ViewGroup
import android.widget.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.desh.imageanalysisreporter.model.Report
import ru.desh.imageanalysisreporter.model.UploadImageResponse
import ru.desh.imageanalysisreporter.network.NetworkUtils
import java.io.File

import ru.desh.imageanalysisreporter.utils.file.FileUtils


class ReportSettingsActivity : AppCompatActivity() {
    companion object {
        val COLORS_NUMBER_PARAM_KEY = "COLORS_NUMBER"
    }
    enum class SettingsType {
        IMAGE_INFO_SETTINGS, COLOR_PALETTE_SETTINGS,
        FOURIER_TRANSFORM_SETTINGS,
        IMAGE_HISTOGRAM_SETTINGS, BIT_PLANES,

        //TODO
        EDGE_DETECTION_SETTINGS,
        IMAGE_SEGMENTATION_SETTINGS,
    }
    private var currentSectionPosition = 0
    private lateinit var currentReport: Report
    private var settingsViews = mutableMapOf<SettingsType, View>()

    private val galleryPermissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var generateReport: MaterialButton
    private lateinit var uploadImage: MaterialButton
    private lateinit var addSection: ImageButton
    private lateinit var reportSettingsHint: TextView
    private lateinit var reportSettingsContainer: LinearLayout


    private val onUploadImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
        uri.let {
            val absolutePath: String = FileUtils.getPath(this, uri)
            val sourceImage: File
            if (absolutePath.isNotEmpty()) {
                sourceImage = File(absolutePath)

                val requestFile: RequestBody = RequestBody.create(
                    MediaType.parse(contentResolver.getType(uri)),
                    sourceImage
                )
                val body = MultipartBody.Part.createFormData("file", sourceImage.name, requestFile)
                NetworkUtils.getRetrofitService().UploadImage(body).enqueue(object : Callback<UploadImageResponse>{
                    override fun onResponse(
                        call: Call<UploadImageResponse>,
                        response: Response<UploadImageResponse>
                    ) {
                        Log.i("CODE", response.code().toString())
                        val b = response.body()
                        if (b != null) {
                            Log.i("IMAGE_UPLOAD_RESULT", "Got external file name: " + b.externalFileName)
                            generateReport.visibility = View.VISIBLE
                            reportSettingsHint.text = resources.getString(R.string.report_settings_hint_2)
                            currentReport.externalFileName = b.externalFileName
                        }
                    }

                    override fun onFailure(call: Call<UploadImageResponse>, t: Throwable) {
                        Log.e("UPLOAD FAIL", "failed", t)
                    }
                })
            } else {
                Log.d("ABSOLUTE_PATH", "is null")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_settings)

        initElements()
    }

    private fun initElements() {
        currentReport = Report()
        currentReport.settings = mutableMapOf()

        addSection = findViewById(R.id.addReportSection)
        generateReport = findViewById(R.id.generateReportButton)
        uploadImage = findViewById(R.id.saveAsPDFButton)
        reportSettingsContainer = findViewById(R.id.reportContainer)
        reportSettingsHint = findViewById(R.id.reportSettingsHint)

        uploadImage.setOnClickListener {
            Log.i("TEST", "click")
            if (EasyPermissions.hasPermissions(this, *galleryPermissions)) {
                onUploadImage.launch("image/*")
                uploadImage.visibility = View.INVISIBLE
            } else {
                EasyPermissions.requestPermissions(this, "Access for storage", 101, *galleryPermissions)
            }
        }
        addSection.setOnClickListener {
            val popupMenu = androidx.appcompat.widget.PopupMenu(this, addSection)
            popupMenu.inflate(R.menu.section_type_popup)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.image_info_section_type -> addSection(SettingsType.IMAGE_INFO_SETTINGS, R.layout.image_info_settings_view)
                    R.id.fourier_transform_section_type -> addSection(SettingsType.FOURIER_TRANSFORM_SETTINGS, R.layout.fourier_transform_settings_view)
                    R.id.rgb_hist_section_type -> addSection(SettingsType.IMAGE_HISTOGRAM_SETTINGS, R.layout.rgb_histogram_settings_view)
                    R.id.color_palette_section_type -> addSection(SettingsType.COLOR_PALETTE_SETTINGS, R.layout.colors_palette_settings_view)
                    R.id.bit_planes_section_type -> addSection(SettingsType.BIT_PLANES, R.layout.bit_planes_settings_view)
                    else -> false
                }
            }
            popupMenu.show()
        }
        generateReport.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)

            putSettingsParams()
            val jsonString = Json.encodeToString(currentReport)
            intent.putExtra("REPORT_SETTINGS", jsonString)
            startActivity(intent)
        }
    }

    private fun putSettingsParams() {
        for (section in settingsViews.keys) {
            when (section) {
                SettingsType.COLOR_PALETTE_SETTINGS -> {
                    val colorsNumber = settingsViews[section]!!.findViewById<EditText>(R.id.editTextColorsNumber)
                    val num = colorsNumber.text.toString()
                    Log.i("PUT_SETTING_PARAMS", num)
                    currentReport.settings[section]!![COLORS_NUMBER_PARAM_KEY] = num
                    Log.i("AFTER_PUT_SETTINGS",
                        currentReport.settings[section]!![COLORS_NUMBER_PARAM_KEY].toString()
                    )
                }
            }
        }
    }


    private fun addSection(sectionType: SettingsType, sectionViewId: Int): Boolean {
        if (currentReport.settings.contains(sectionType)) {
            Toast.makeText(this, "Report has already have this type of section",
                Toast.LENGTH_LONG).show()
        } else {
            val settingsSection = layoutInflater.inflate(sectionViewId, null)

            reportSettingsContainer.addView(settingsSection,currentSectionPosition,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ))
            currentSectionPosition++
            currentReport.settings.putIfAbsent(sectionType, mutableMapOf())
            settingsViews[sectionType] = settingsSection
            Log.i("ADD_VIEW", "View added")
            Log.i("CHILD_COUNT",
                (reportSettingsContainer as ViewGroup).childCount.toString()
            )
        }
        return true
    }
}