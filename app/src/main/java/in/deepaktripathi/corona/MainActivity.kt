package `in`.deepaktripathi.corona

import `in`.deepaktripathi.corona.databinding.ActivityMainBinding
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var mFirebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.latestStates = LatestCovidData(success = true)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        swiperefresh.setOnRefreshListener {
            mFirebaseAnalytics.logEvent("APP_REFRESH", Bundle())
            fetchLatestCovidData(binding)
        }

        fetchLatestCovidData(binding)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, Bundle())
    }

    private fun fetchLatestCovidData(
        binding: ActivityMainBinding
    ) {
        val call = ApiInterface.api.getLatestDetails()
        binding.swiperefresh.isRefreshing = true
        call.enqueue(object : Callback<LatestCovidData> {
            override fun onFailure(call: Call<LatestCovidData>?, t: Throwable?) {
                toast("Sorry, Can not refresh")
                binding.swiperefresh.isRefreshing = false
            }

            override fun onResponse(
                call: Call<LatestCovidData>?,
                response: Response<LatestCovidData>?
            ) {
                binding.latestStates = response?.body()
                toast("Refreshed @ ${binding.latestStates?.lastUpdate ?: ""}")
                binding.swiperefresh.isRefreshing = false
            }

        })
    }


}
