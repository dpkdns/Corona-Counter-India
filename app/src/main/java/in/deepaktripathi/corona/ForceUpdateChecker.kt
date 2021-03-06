package `in`.deepaktripathi.corona

import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig


class ForceUpdateChecker(
    private val context: Context,
    onUpdateNeededListener: OnUpdateNeededListener?
) {
    private val onUpdateNeededListener: OnUpdateNeededListener?

    interface OnUpdateNeededListener {
        fun onUpdateNeeded(updateUrl: String)
    }

    fun check() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        sharableUrl = remoteConfig.getString(KEY_SHARABLE_URL)
        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
            val currentVersion =
                remoteConfig.getString(KEY_CURRENT_VERSION)
            val appVersion = getAppVersion(context)
            val updateUrl =
                remoteConfig.getString(KEY_UPDATE_URL)
            if (!TextUtils.equals(currentVersion, appVersion)
                && onUpdateNeededListener != null
            ) {
                onUpdateNeededListener.onUpdateNeeded(updateUrl)
            }
        }
    }

    private fun getAppVersion(context: Context): String {
        var result = ""
        try {
            result = context.packageManager
                .getPackageInfo(context.getPackageName(), 0).versionName
            result = result.replace("[a-zA-Z]|-".toRegex(), "")
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, e.message ?: "")
        }
        return result
    }

    class Builder(private val context: Context) {
        private var onUpdateNeededListener: OnUpdateNeededListener? = null
        fun onUpdateNeeded(onUpdateNeededListener: OnUpdateNeededListener?): Builder {
            this.onUpdateNeededListener = onUpdateNeededListener
            return this
        }

        fun build(): ForceUpdateChecker {
            return ForceUpdateChecker(context, onUpdateNeededListener)
        }

        fun check(): ForceUpdateChecker {
            val forceUpdateChecker = build()
            forceUpdateChecker.check()
            return forceUpdateChecker
        }

    }

    companion object {
        private val TAG = ForceUpdateChecker::class.java.simpleName
        const val KEY_UPDATE_REQUIRED = "force_update_required"
        const val KEY_CURRENT_VERSION = "force_update_current_version"
        const val KEY_UPDATE_URL = "force_update_store_url"
        const val KEY_SHARABLE_URL = "sharable_url"
        var sharableUrl = SHARABLE_APP_URL
        fun with(context: Context): Builder {
            return Builder(context)
        }
    }

    init {
        this.onUpdateNeededListener = onUpdateNeededListener
    }
}