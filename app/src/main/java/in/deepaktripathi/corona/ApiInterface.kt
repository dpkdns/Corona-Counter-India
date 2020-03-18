package `in`.deepaktripathi.corona

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface ApiInterface {

    @GET("covid19-in/stats/latest")
    fun getLatestDetails(): Call<LatestCovidData>

    companion object {
        var api = Retrofit.Builder()
            .baseUrl("https://api.rootnet.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)
    }
}