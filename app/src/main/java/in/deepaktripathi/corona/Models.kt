package `in`.deepaktripathi.corona

import java.text.SimpleDateFormat
import java.util.*


data class LatestCovidData(
    val success: Boolean,
    val data: LatestData? = null,
    val lastOriginUpdate: Date? = null
) {
    inline val lastUpdate
            get() = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US).format( lastOriginUpdate?: Date())
}

data class LatestData (
    val summary: LatestSummery
)

data class LatestSummery (
    val total: Int,
    val confirmedCasesIndian: Int,
    val confirmedCasesForeign: Int,
    val discharged: Int,
    val deaths: Int
)
