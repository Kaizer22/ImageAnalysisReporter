package ru.desh.imageanalysisreporter.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.desh.imageanalysisreporter.R
import ru.desh.imageanalysisreporter.model.ImagePalette
import ru.desh.imageanalysisreporter.network.NetworkUtils

class ColorPaletteResultFragment(
    private val filename: String,
    private val colorsNumber: Int?): Fragment() {

    private val logTag = "Get colors palette"

    private lateinit var colorsPalette: ImagePalette

    private lateinit var colorsContainer: LinearLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_color_palette, container, false)
        initElements(view)
        if (!::colorsPalette.isInitialized) {
            getColorsPalette()
        } else {
            renderColors()
        }
        return view
    }

    private fun initElements(view: View) {
        colorsContainer = view.findViewById(R.id.colorsContainer)
        progressBar = view.findViewById(R.id.imageInfoLoadingBar)
    }

    private fun renderColors() {
        progressBar.visibility = View.GONE
        for (color in colorsPalette.colors!!) {
            val colorView = layoutInflater.inflate(R.layout.single_color_view, null)
            val colorPreview = colorView.findViewById<ImageView>(R.id.colorPreview)
            val colorHEX = colorView.findViewById<TextView>(R.id.colorHEX)
            val colorRGB = colorView.findViewById<TextView>(R.id.colorRGB)

            val rgbColor = "(${color[0]}, ${color[1]}, ${color[2]})"
            val hexColor = "#${color[0].toString(16)}${color[1].toString(16)}${color[2].toString(16)}"
            colorRGB.text = rgbColor
            colorHEX.text = hexColor
            colorPreview.setBackgroundColor(Color.rgb(color[0], color[1], color[2]))


            colorsContainer.addView(colorView ,0,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ))
        }
    }

    private fun getColorsPalette() {
        val retrofitService = NetworkUtils.getRetrofitService()
        if (colorsNumber != null) {
            retrofitService.GetColorPalette(filename, colorsNumber).enqueue(
                object : Callback<ImagePalette> {
                    override fun onResponse(call: Call<ImagePalette>, response: Response<ImagePalette>) {
                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                Log.i(logTag, response.body()!!.colors.toString())
                                colorsPalette = response.body()!!
                                renderColors()
                            }
                        } else {
                            Log.w(logTag, "Response is not successful: " + response.code() + " code" )
                        }
                    }
                    override fun onFailure(call: Call<ImagePalette>, t: Throwable) {
                        Log.e(logTag, "Fail", t)
                        progressBar.visibility = View.GONE
                    }
                }
            )
        }
    }
}