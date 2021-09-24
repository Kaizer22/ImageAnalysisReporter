package ru.desh.imageanalysisreporter.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.desh.imageanalysisreporter.R
import ru.desh.imageanalysisreporter.model.ImageInfo
import ru.desh.imageanalysisreporter.network.NetworkUtils

class ImageInfoResultFragment(private val filename: String): Fragment() {

    private val logTag = "Get Image Info"

    private lateinit var imageInfo: ImageInfo

    private lateinit var widthLabel: TextView
    private lateinit var widthValue: TextView

    private lateinit var heightLabel: TextView
    private lateinit var heightValue: TextView

    private lateinit var filetypeLabel: TextView
    private lateinit var filetypeValue: TextView

    private lateinit var colorSchemaLabel: TextView
    private lateinit var colorSchemaValue: TextView

    private lateinit var fileSizeLabel: TextView
    private lateinit var fileSizeValue: TextView

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_info, container, false)
        initElements(view)
        if (!::imageInfo.isInitialized) {
            getImageInfo()
        } else {
            renderInfo()
        }
        return view
    }

    private fun initElements(view: View) {
        widthLabel = view.findViewById(R.id.widthField)
        widthValue = view.findViewById(R.id.widthValue)

        heightLabel = view.findViewById(R.id.heightField)
        heightValue = view.findViewById(R.id.heightValue)

        filetypeLabel = view.findViewById(R.id.filetypeField)
        filetypeValue = view.findViewById(R.id.filetypeValue)

        colorSchemaLabel = view.findViewById(R.id.colorSchemaField)
        colorSchemaValue = view.findViewById(R.id.colorSchemaValue)

        fileSizeLabel = view.findViewById(R.id.fileSizeField)
        fileSizeValue = view.findViewById(R.id.fileSizeValue)

        progressBar = view.findViewById(R.id.imageInfoLoadingBar)
    }

    private fun renderInfo() {
        widthLabel.visibility = View.VISIBLE
        widthValue.visibility = View.VISIBLE
        widthValue.text = imageInfo.width

        heightLabel.visibility = View.VISIBLE
        heightValue.visibility = View.VISIBLE
        heightValue.text = imageInfo.height

        filetypeLabel.visibility = View.VISIBLE
        filetypeValue.visibility = View.VISIBLE
        filetypeValue.text = imageInfo.filetype

        colorSchemaLabel.visibility = View.VISIBLE
        colorSchemaValue.visibility = View.VISIBLE
        colorSchemaValue.text = imageInfo.colorSchema

        fileSizeLabel.visibility = View.VISIBLE
        fileSizeValue.visibility = View.VISIBLE
        fileSizeValue.text = imageInfo.sizeBytes

        progressBar.visibility = View.GONE
    }

    private fun getImageInfo() {
        val retrofitService = NetworkUtils.getRetrofitService()
        retrofitService.GetImageInfo(filename).enqueue(
            object : Callback<ImageInfo> {
                override fun onResponse(call: Call<ImageInfo>, response: Response<ImageInfo>) {
                    if (response.isSuccessful) {
                        imageInfo = response.body()!!
                        Log.i("IMAGE_INFO", imageInfo!!.sizeBytes!!)
                        renderInfo()
                    } else {
                        Log.w(logTag, "Response is not successful: " + response.code() + " code" )
                    }
                }

                override fun onFailure(call: Call<ImageInfo>, t: Throwable) {
                    Log.e(logTag, "Fail", t)
                    progressBar.visibility = View.GONE
                }
            }
        )
    }
}

