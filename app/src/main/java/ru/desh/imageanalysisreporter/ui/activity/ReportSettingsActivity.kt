package ru.desh.imageanalysisreporter.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.button.MaterialButton
import pub.devrel.easypermissions.EasyPermissions
import ru.desh.imageanalysisreporter.R
import ru.desh.imageanalysisreporter.ui.service.SettingsProvider
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
    enum class SettingsType {
        IMAGE_INFO_SETTINGS, COLOR_PALETTE_SETTINGS,
        FOURIER_TRANSFORM_SETTINGS, EDGE_DETECTION_SETTINGS,
        IMAGE_HISTOGRAM_SETTINGS, IMAGE_SEGMENTATION_SETTINGS
    }
    private lateinit var currentReport: Report

    private val galleryPermissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val settingsProvider = SettingsProvider()

    private lateinit var generateReport: MaterialButton
    private lateinit var uploadImage: MaterialButton
    private lateinit var addSection: ImageButton
    private lateinit var reportSettingsHint: TextView
    private lateinit var reportSettingsContainer: LinearLayout

    private lateinit var uploadedImage: File

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
                // MultipartBody.Part is used to send also the actual file name
                val body = MultipartBody.Part.createFormData("file", sourceImage.name, requestFile)
                // finally, execute the request
                NetworkUtils.getRetrofitService().UploadImage(body).enqueue(object : Callback<UploadImageResponse>{
                    override fun onResponse(
                        call: Call<UploadImageResponse>,
                        response: Response<UploadImageResponse>
                    ) {
                        Log.i("CODE", response.code().toString())
                        val b = response.body()
                        if (b != null) {
                            Log.i("IMAGE_UPLOAD_RESULT", "Got external file name: " + b.externalFileName)
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
                generateReport.visibility = View.VISIBLE
                reportSettingsHint.text = resources.getString(R.string.report_settings_hint_2)
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
                    else -> false
                }
            }
            popupMenu.show()
        }
        generateReport.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            val jsonString = Json.encodeToString(currentReport)
            intent.putExtra("REPORT_SETTINGS", jsonString)

            startActivity(intent)
        }
    }


    private fun addSection(sectionType: SettingsType, sectionViewId: Int): Boolean {
        if (currentReport.settings.contains(sectionType)) {
            Toast.makeText(this, "Report has already have this type of section",
                Toast.LENGTH_LONG).show()
        } else {
            val settingsSection = layoutInflater.inflate(sectionViewId, null)

            reportSettingsContainer.addView(settingsSection,0,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ))
            currentReport.settings.putIfAbsent(sectionType, mutableMapOf())
            Log.i("ADD_VIEW", "View added")
            Log.i("CHILD_COUNT",
                (reportSettingsContainer as ViewGroup).childCount.toString()
            )
        }
        return true
    }
}