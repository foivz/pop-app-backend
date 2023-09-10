package hr.foi.pop.backend.utils

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class DateMatcher(
    private val expectedDate: LocalDateTime? = null,
    private val toleranceMillis: Long = 0
) : TypeSafeMatcher<String>() {

    override fun matchesSafely(actualDate: String?): Boolean {
        val parsedActualDate = LocalDateTime.parse(actualDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val actualMillis = parsedActualDate.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
        val expectedMillis = expectedDate!!.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
        return Math.abs(actualMillis - expectedMillis) <= toleranceMillis
    }

    override fun describeTo(description: Description) {
        description.appendText("a date within $toleranceMillis milliseconds of $expectedDate")
    }
}
