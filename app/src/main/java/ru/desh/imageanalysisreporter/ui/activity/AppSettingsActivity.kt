package ru.desh.imageanalysisreporter.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import ru.desh.imageanalysisreporter.R
import ru.desh.imageanalysisreporter.network.NetworkUtils

class AppSettingsActivity: AppCompatActivity() {

    private lateinit var languageField: MaterialButton
    private lateinit var backendURLInput: EditText
    private lateinit var saveAndReturnButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_settings)
        initElements()
    }

    private fun initElements() {
        languageField = findViewById(R.id.languageButton)
        backendURLInput = findViewById(R.id.editTextBackendURL)

        backendURLInput.setText(NetworkUtils.baseUrl)

        saveAndReturnButton = findViewById(R.id.saveAndReturnButton)

        languageField.setOnClickListener {
            //TODO: localization
        }
        saveAndReturnButton.setOnClickListener {
            val backendURL = backendURLInput.text
            if (Patterns.WEB_URL.matcher(backendURL).matches()) {
                NetworkUtils.initBackendConnection(backendURL.toString())
            } else {
                Toast.makeText(this, "Invalid URL cannot be set as backend address.",
                    Toast.LENGTH_LONG).show()
            }
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
    }
}