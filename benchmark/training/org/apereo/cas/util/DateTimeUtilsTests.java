package org.apereo.cas.util;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link DateTimeUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class DateTimeUtilsTests {
    @Test
    public void verifyParsingDateAsLocalDateTime() {
        Assertions.assertNotNull(DateTimeUtils.localDateTimeOf(LocalDateTime.now().toString()));
    }

    @Test
    public void verifyParsingDateAsLocalDate() {
        Assertions.assertNotNull(DateTimeUtils.localDateTimeOf(LocalDate.now().toString()));
    }

    @Test
    public void verifyParsingDateAsLocalDateString1() {
        Assertions.assertNotNull(DateTimeUtils.localDateTimeOf("2017-10-15"));
    }

    @Test
    public void verifyParsingDateAsLocalDateString2() {
        Assertions.assertNotNull(DateTimeUtils.localDateTimeOf("09/19/2017"));
    }

    @Test
    public void verifyParsingDateAsLocalDateString3() {
        Assertions.assertNotNull(DateTimeUtils.localDateTimeOf("09/19/2017 4:30 pm"));
    }

    @Test
    public void verifyParsingDateAsLocalDateString4() {
        Assertions.assertNotNull(DateTimeUtils.localDateTimeOf("2017-10-12T07:00:00.000Z"));
    }

    @Test
    public void verifyParsingCalendar() {
        val calendar = Calendar.getInstance();
        Assertions.assertNotNull(DateTimeUtils.zonedDateTimeOf(calendar));
    }

    @Test
    public void verifyParsingChronoUnit() {
        Assertions.assertEquals(ChronoUnit.DAYS, DateTimeUtils.toChronoUnit(TimeUnit.DAYS));
        Assertions.assertEquals(ChronoUnit.HOURS, DateTimeUtils.toChronoUnit(TimeUnit.HOURS));
        Assertions.assertEquals(ChronoUnit.MINUTES, DateTimeUtils.toChronoUnit(TimeUnit.MINUTES));
        Assertions.assertEquals(ChronoUnit.SECONDS, DateTimeUtils.toChronoUnit(TimeUnit.SECONDS));
        Assertions.assertEquals(ChronoUnit.MICROS, DateTimeUtils.toChronoUnit(TimeUnit.MICROSECONDS));
        Assertions.assertEquals(ChronoUnit.MILLIS, DateTimeUtils.toChronoUnit(TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ChronoUnit.NANOS, DateTimeUtils.toChronoUnit(TimeUnit.NANOSECONDS));
    }
}

