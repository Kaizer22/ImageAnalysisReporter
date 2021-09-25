package ru.desh.imageanalysisreporter.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ImageWriter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.desh.imageanalysisreporter.R
import ru.desh.imageanalysisreporter.network.NetworkUtils
import kotlin.math.log

class BitPlanesResultFragment (private val filename: String): Fragment() {
    private val logTag = "Get bit planes"

    private lateinit var bitPlane1Label: TextView
    private lateinit var bitPlane1Preview: SubsamplingScaleImageView
    private lateinit var bitPlane2Label: TextView
    private lateinit var bitPlane2Preview: SubsamplingScaleImageView
    private lateinit var bitPlane3Label: TextView
    private lateinit var bitPlane3Preview: SubsamplingScaleImageView
    private lateinit var bitPlane4Label: TextView
    private lateinit var bitPlane4Preview: SubsamplingScaleImageView
    private lateinit var bitPlane5Label: TextView
    private lateinit var bitPlane5Preview: SubsamplingScaleImageView
    private lateinit var bitPlane6Label: TextView
    private lateinit var bitPlane6Preview: SubsamplingScaleImageView
    private lateinit var bitPlane7Label: TextView
    private lateinit var bitPlane7Preview: SubsamplingScaleImageView
    private lateinit var bitPlane8Label: TextView
    private lateinit var bitPlane8Preview: SubsamplingScaleImageView

    private lateinit var progressBar: ProgressBar
    private var gotBitPlanes = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bit_planes, container, false)
        initElements(view)
        if (!gotBitPlanes) {
            getBitPlanes()
        } else {
            renderBitPlanes()
        }
        return view
    }

    private fun initElements(view: View) {
        bitPlane1Label = view.findViewById(R.id.bitPlane1Label)
        bitPlane1Preview = view.findViewById(R.id.bitPlane1)
        bitPlane2Label = view.findViewById(R.id.bitPlane2Label)
        bitPlane2Preview = view.findViewById(R.id.bitPlane2)
        bitPlane3Label = view.findViewById(R.id.bitPlane3Label)
        bitPlane3Preview = view.findViewById(R.id.bitPlane3)
        bitPlane4Label = view.findViewById(R.id.bitPlane4Label)
        bitPlane4Preview = view.findViewById(R.id.bitPlane4)
        bitPlane5Label = view.findViewById(R.id.bitPlane5Label)
        bitPlane5Preview = view.findViewById(R.id.bitPlane5)
        bitPlane6Label = view.findViewById(R.id.bitPlane6Label)
        bitPlane6Preview = view.findViewById(R.id.bitPlane6)
        bitPlane7Label = view.findViewById(R.id.bitPlane7Label)
        bitPlane7Preview = view.findViewById(R.id.bitPlane7)
        bitPlane8Label = view.findViewById(R.id.bitPlane8Label)
        bitPlane8Preview = view.findViewById(R.id.bitPlane8)

        progressBar = view.findViewById(R.id.imageInfoLoadingBar)
    }

    private fun renderBitPlanes() {
        progressBar.visibility = View.GONE
    }

    private fun getBitPlanes() {
        val retrofitService = NetworkUtils.getRetrofitService()
        retrofitService.GetBitPlanes(filename).enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            val contentDescriptor = response.headers().get("My-Content-Descriptor")!!
                                .split("_")
                            Log.i(logTag,"HEADER: ${response.headers().get("My-Content-Descriptor")!!}")
                            val imStream = response.body()!!.byteStream()
                            for ((j, descriptor) in contentDescriptor.withIndex()) {
                                if (descriptor.isNotEmpty()) {
                                    val amountOfBytes = descriptor.toInt()
                                    val bytes = ByteArray(amountOfBytes)
                                    for (i in bytes.indices) {
                                        bytes[i] = imStream.read().toByte()
                                    }
                                    val img = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                    when (j) {
                                        0 -> {
                                            bitPlane1Label.visibility = View.VISIBLE
                                            bitPlane1Preview.visibility = View.VISIBLE
                                            bitPlane1Preview.setImage(ImageSource.bitmap(img))
                                        }
                                        1 -> {
                                            bitPlane2Label.visibility = View.VISIBLE
                                            bitPlane2Preview.visibility = View.VISIBLE
                                            bitPlane2Preview.setImage(ImageSource.bitmap(img))
                                        }
                                        2 -> {
                                            bitPlane3Label.visibility = View.VISIBLE
                                            bitPlane3Preview.visibility = View.VISIBLE
                                            bitPlane3Preview.setImage(ImageSource.bitmap(img))
                                        }
                                        3 -> {
                                            bitPlane4Label.visibility = View.VISIBLE
                                            bitPlane4Preview.visibility = View.VISIBLE
                                            bitPlane4Preview.setImage(ImageSource.bitmap(img))
                                        }
                                        4 -> {
                                            bitPlane5Label.visibility = View.VISIBLE
                                            bitPlane5Preview.visibility = View.VISIBLE
                                            bitPlane5Preview.setImage(ImageSource.bitmap(img))
                                        }
                                        5 -> {
                                            bitPlane6Label.visibility = View.VISIBLE
                                            bitPlane6Preview.visibility = View.VISIBLE
                                            bitPlane6Preview.setImage(ImageSource.bitmap(img))
                                        }
                                        6 -> {
                                            bitPlane7Label.visibility = View.VISIBLE
                                            bitPlane7Preview.visibility = View.VISIBLE
                                            bitPlane7Preview.setImage(ImageSource.bitmap(img))
                                        }
                                        7 -> {
                                            bitPlane8Label.visibility = View.VISIBLE
                                            bitPlane8Preview.visibility = View.VISIBLE
                                            bitPlane8Preview.setImage(ImageSource.bitmap(img))
                                        }
                                    }
                                }
                            }
                            progressBar.visibility = View.GONE
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