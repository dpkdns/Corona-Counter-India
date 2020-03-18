package `in`.deepaktripathi.corona

import `in`.deepaktripathi.corona.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.latestStates = LatestCovidData(success = true)
        val call = ApiInterface.api.getLatestDetails()

        call.enqueue(object : Callback<LatestCovidData> {
            override fun onFailure(call: Call<LatestCovidData>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<LatestCovidData>?, response: Response<LatestCovidData>?) {
                binding.latestStates = response?.body()
                Toast.makeText(applicationContext, "Refreshed @ ${binding.latestStates?.lastUpdate?:""}", Toast.LENGTH_LONG).show()
            }

        })

    }
}
