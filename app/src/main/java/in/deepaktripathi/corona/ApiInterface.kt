package `in`.deepaktripathi.corona

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url


interface ApiInterface {

    @GET("covid19-in/stats/latest")
    fun getLatestDetails(): Call<LatestCovidData>

    @GET
    fun downloadApk(@Url fileUrl: String): Call<ResponseBody>

    companion object {
        var api = Retrofit.Builder()
            .baseUrl("https://api.rootnet.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)
    }
}