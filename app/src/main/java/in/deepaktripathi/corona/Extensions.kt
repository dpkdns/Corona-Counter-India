package `in`.deepaktripathi.corona

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun Context.openFile(fileName: String) {
    try {
        val file = File(this.getExternalFilesDir(null), fileName.plus(".apk"))
        val install = Intent(Intent.ACTION_VIEW)
        val mimeType = "application/vnd.android.package-archive"
        install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val apkURI = FileProvider.getUriForFile(
                this,
                this.applicationContext.packageName + ".provider",
                file
            )
            install.setDataAndType(apkURI, mimeType)
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            install.setDataAndType(Uri.fromFile(file), mimeType)
        }
        startActivity(install)
    } catch (e: Exception) {
        toast("Not able to open this file.")
    }
}