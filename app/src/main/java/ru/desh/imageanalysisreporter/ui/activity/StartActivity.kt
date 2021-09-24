package ru.desh.imageanalysisreporter.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import ru.desh.imageanalysisreporter.R

class StartActivity : AppCompatActivity() {

    private lateinit var buttonNewReport: MaterialButton
    private lateinit var buttonSettings: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        initElements()
    }

    private fun initElements() {
        buttonNewReport = findViewById(R.id.newReportButton)
        buttonSettings = findViewById(R.id.settingsButton)

        buttonSettings.setOnClickListener {
            val intent = Intent(this, AppSettingsActivity::class.java)
            startActivity(intent)
        }
        buttonNewReport.setOnClickListener {
            val intent = Intent(this, ReportSettingsActivity::class.java)
            startActivity(intent)
        }
    }
}