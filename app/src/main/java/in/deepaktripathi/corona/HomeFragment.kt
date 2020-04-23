package `in`.deepaktripathi.corona

import `in`.deepaktripathi.corona.databinding.FragmentHomeBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    var data: LatestData? = null
    lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.latestStates = LatestCovidData(success = true)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, Bundle())

        fetchLatestCovidData(binding)


        binding.swiperefresh.setOnRefreshListener {
            mFirebaseAnalytics.logEvent("APP_REFRESH", Bundle())
            fetchLatestCovidData(binding)
        }

        fetchLatestCovidData(binding)

        binding.floatingShareButton.setOnClickListener {
            val message = if(data != null) {
                """
                    Total Corona Cases In India -
                    Total - ${data?.summary?.total},
                    Indians - ${data?.summary?.confirmedCasesIndian},
                    Foreigners - ${data?.summary?.confirmedCasesForeign},
                    Deaths - ${data?.summary?.deaths},
                    Recovered - ${data?.summary?.discharged}
                    
                    
                """.trimIndent()
            } else {
                ""
            }.plus("download Corona Counter app and get latest updates - ${ForceUpdateChecker.sharableUrl}")

            ShareCompat.IntentBuilder.from(requireActivity()).setType("text/plain")
                .setChooserTitle("Chooser title")
                .setText(message)
                .startChooser()
            mFirebaseAnalytics.logEvent("DATA_SHARED", Bundle())
        }

        return binding.root
    }

    private fun fetchLatestCovidData(
        binding: FragmentHomeBinding
    ) {
        val call = ApiInterface.api.getLatestDetails()
        binding.swiperefresh.isRefreshing = true
        call.enqueue(object : Callback<LatestCovidData> {
            override fun onFailure(call: Call<LatestCovidData>?, t: Throwable?) {
                context?.toast("Sorry, Can not refresh")
                binding.swiperefresh.isRefreshing = false
            }

            override fun onResponse(
                call: Call<LatestCovidData>?,
                response: Response<LatestCovidData>?
            ) {
                binding.latestStates = response?.body()
                data = response?.body()?.data
                context?.toast("Refreshed @ ${binding.latestStates?.lastUpdate ?: ""}")
                binding.swiperefresh.isRefreshing = false
            }

        })
    }


}
