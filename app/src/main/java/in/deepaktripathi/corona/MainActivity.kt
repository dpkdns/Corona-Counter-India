package `in`.deepaktripathi.corona

import `in`.deepaktripathi.corona.databinding.ActivityMainBinding
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), ForceUpdateChecker.OnUpdateNeededListener {

    var data: LatestData? = null
    lateinit var mFirebaseAnalytics: FirebaseAnalytics


    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check()
    }

    @Override
    override fun onUpdateNeeded(updateUrl: String) {
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("New version available")
            .setMessage("Please, update app to new version to continue reposting.")
            .setPositiveButton(
                "Update"
            ) { _, _ -> downloadApk(this, updateUrl) }
            .setNegativeButton(
                "No, thanks"
            ) { _, _ -> finish() }.create()
        dialog.show()
    }

    private fun downloadApk(context: Context, apkUrl: String) {

        val fileName = "corona_counter_${System.currentTimeMillis()}"

        ApiInterface.api.downloadApk(apkUrl).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                toast("Sorry, Can not download update")
            }

            override fun onResponse(
                call: Call<ResponseBody>?,
                response: Response<ResponseBody>?
            ) {
                isStoragePermissionGranted()
                response!!.body()?.let {
                    val downloaded = Utils.writeResponseBodyToDisk(
                        it,
                        fileName,
                        context.getExternalFilesDir(null)!!
                    )
                    mFirebaseAnalytics.logEvent(
                        "UPDATE_DOWNLOADED", Bundle().apply { putBoolean("DOWNLOADED", downloaded) }
                    )
                }
                context.openFile(fileName)
                toast("Download complete")
            }

        })
    }

    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else {
            true
        }
    }
}
