package ru.desh.imageanalysisreporter.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.desh.imageanalysisreporter.R
import ru.desh.imageanalysisreporter.network.NetworkUtils

class FourierTransformResultFragment(private val filename: String): Fragment() {
    private val logTag = "Get Image Info"

    private lateinit var fourierImagePreview: SubsamplingScaleImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var fourierImage: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fourier_transform, container, false)
        initElements(view)
        if (!::fourierImage.isInitialized) {
            getGetFourierImage()
        } else {
            renderImage()
        }
        return view
    }

    private fun initElements(view: View) {
        fourierImagePreview = view.findViewById(R.id.subsamplingScaleImageView)
        progressBar = view.findViewById(R.id.imageInfoLoadingBar)
    }

    private fun renderImage() {
        fourierImagePreview.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun getGetFourierImage() {
        val retrofitService = NetworkUtils.getRetrofitService()
        retrofitService.GetFourierTransform(filename).enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            val imStream = response.body()!!.byteStream()
                            fourierImage = BitmapFactory.decodeStream(imStream)
                            fourierImagePreview.setImage(ImageSource.bitmap(fourierImage))
                            renderImage()
                        }
                    } else {
                        Log.w(logTag, "Response is not successful: " + response.code() + " code" )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(logTag, "Fail", t)
                    progressBar.visibility = View.GONE
                }
            }
        )
    }
}