package `in`.deepaktripathi.corona

import okhttp3.ResponseBody
import java.io.*

object Utils {
    fun writeResponseBodyToDisk(
        body: ResponseBody,
        fileName: String,
        file: File
    ) =
        try {
            if (!file.exists()) {
                file.mkdirs()
            }
            val destinationFile = File(file, fileName.plus(".apk"))
            destinationFile.createNewFile()
            var inputStream: InputStream? = null
            var os: OutputStream? = null
            try {
                inputStream = body.byteStream()
                os = FileOutputStream(destinationFile)

                val data = ByteArray(4096)
                var progress = 0
                var count: Int
                var total = 0
                while (true) {
                    count = inputStream?.read(data) ?: 0
                    total += count
                    if (count == -1)
                        break
                    os.write(data, 0, count)
                    progress += count
                    val percentage = ((progress.toFloat() * 100F) / body.contentLength()).toInt()
                }
                os.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                os?.close()
            }
        } catch (e: IOException) {
            false
        }
}