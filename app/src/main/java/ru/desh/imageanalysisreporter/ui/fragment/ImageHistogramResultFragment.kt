package ru.desh.imageanalysisreporter.ui.fragment

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.db.williamchart.view.BarChartView
import okhttp3.ResponseBody
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.desh.imageanalysisreporter.R
import ru.desh.imageanalysisreporter.model.ImageHistogram
import ru.desh.imageanalysisreporter.network.NetworkUtils
import kotlin.math.log

class ImageHistogramResultFragment(private val filename: String): Fragment(){
    private val logTag = "Get RGB histogram"

    private enum class RGBChannels {
        RED, GREEN, BLUE
    }

    private lateinit var redChannelLabel: TextView
    private lateinit var greenChannelLabel: TextView
    private lateinit var blueChannelLabel: TextView

    private lateinit var redChannelMaxValue: TextView
    private lateinit var greenChannelMaxValue: TextView
    private lateinit var blueChannelMaxValue: TextView

    private lateinit var redChannelHist: BarChartView
    private lateinit var greenChannelHist: BarChartView
    private lateinit var blueChannelHist: BarChartView

    private lateinit var histData: ImageHistogram
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rgb_histogram, container, false)
        initElements(view)
        if (!::histData.isInitialized) {
            getHistData()
        } else {
            renderHists()
        }
        return view
    }

    private fun initElements(view: View) {
        redChannelLabel = view.findViewById(R.id.redChannelLabel)
        greenChannelLabel = view.findViewById(R.id.greenChannelLabel)
        blueChannelLabel = view.findViewById(R.id.blueChannelLabel)

        redChannelMaxValue = view.findViewById(R.id.redChannelMaxValue)
        greenChannelMaxValue = view.findViewById(R.id.greenChannelMaxValue)
        blueChannelMaxValue = view.findViewById(R.id.blueChannelMaxValue)

        redChannelHist = view.findViewById(R.id.redChannelBarChart)
        greenChannelHist = view.findViewById(R.id.greenChannelBarChart)
        blueChannelHist = view.findViewById(R.id.blueChannelBarChart)

        progressBar = view.findViewById(R.id.imageInfoLoadingBar)
    }

    private fun renderHists() {
        redChannelHist.barsColor = Color.RED
        greenChannelHist.barsColor = Color.GREEN
        blueChannelHist.barsColor = Color.BLUE

        redChannelHist.show(getDataPoints(RGBChannels.RED))
        greenChannelHist.show(getDataPoints(RGBChannels.GREEN))
        blueChannelHist.show(getDataPoints(RGBChannels.BLUE))

        redChannelLabel.visibility = View.VISIBLE
        greenChannelLabel.visibility = View.VISIBLE
        blueChannelHist.visibility = View.VISIBLE

        redChannelHist.visibility = View.VISIBLE
        greenChannelHist.visibility = View.VISIBLE
        blueChannelHist.visibility = View.VISIBLE

        progressBar.visibility = View.GONE
    }

    private fun getDataPoints(channel: RGBChannels): LinkedHashMap<String, Float> {
        val result = linkedMapOf<String, Float>()
        var maxProb = Float.MIN_VALUE
        var maxIntensity = 0
        val arr = when(channel) {
            RGBChannels.RED -> histData.red
            RGBChannels.GREEN -> histData.green
            RGBChannels.BLUE -> histData.blue
        }
        if (arr != null) {
            for ((ind, a) in arr.withIndex()) {
                if (a > maxProb) {
                    maxProb = a
                    maxIntensity = ind
                }
                result[ind.toString()] = a
                Log.i(logTag, "Data point: $ind:$a")
            }
        }
        val maxText = "Max value: ($maxIntensity : $maxProb)"
        when(channel) {
            RGBChannels.RED -> redChannelMaxValue.text = maxText
            RGBChannels.GREEN -> greenChannelMaxValue.text = maxText
            RGBChannels.BLUE -> blueChannelMaxValue.text = maxText
        }
        return result
    }

    private fun getHistData() {
        val retrofitService = NetworkUtils.getRetrofitService()
        retrofitService.GetRGBHistogram(filename).enqueue(
            object : Callback<ImageHistogram> {
                override fun onResponse(call: Call<ImageHistogram>, response: Response<ImageHistogram>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            histData = response.body()!!
                            Log.i(logTag, "Got data:" + histData.red.toString())
                            renderHists()
                        }
                    } else {
                        Log.w(logTag, "Response is not successful: " + response.code() + " code" )
                    }
                }
                override fun onFailure(call: Call<ImageHistogram>, t: Throwable) {
                    Log.e(logTag, "Fail", t)
                    progressBar.visibility = View.GONE
                }
            }
        )
    }
}