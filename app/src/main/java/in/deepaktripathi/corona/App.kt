package `in`.deepaktripathi.corona

import android.app.Application
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig


class App : Application() {

    private val TAG = App::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        // set in-app defaults

        // set in-app defaults
        val remoteConfigDefaults: MutableMap<String, Any> =
            HashMap()
        remoteConfigDefaults[ForceUpdateChecker.KEY_UPDATE_REQUIRED] = false
        remoteConfigDefaults[ForceUpdateChecker.KEY_CURRENT_VERSION] = CURRENT_APP_VERSION
        remoteConfigDefaults[ForceUpdateChecker.KEY_UPDATE_URL] = UPDATE_APP_URL
        remoteConfigDefaults[ForceUpdateChecker.KEY_SHARABLE_URL] = SHARABLE_APP_URL

        firebaseRemoteConfig.setDefaults(remoteConfigDefaults)
        firebaseRemoteConfig.fetch(60) // fetch every minutes
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "remote config is fetched.")
                    firebaseRemoteConfig.activateFetched()
                }
            }
    }
}