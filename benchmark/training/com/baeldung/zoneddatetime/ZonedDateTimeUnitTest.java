package com.baeldung.zoneddatetime;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ZonedDateTimeUnitTest {
    private static final Logger log = Logger.getLogger(ZonedDateTimeUnitTest.class.getName());

    @Test
    public void givenZonedDateTime_whenConvertToString_thenOk() {
        ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime zonedDateTimeOf = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z");
        String formattedString = zonedDateTime.format(formatter);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss z");
        String formattedString2 = zonedDateTime.format(formatter2);
        ZonedDateTimeUnitTest.log.info(formattedString);
        ZonedDateTimeUnitTest.log.info(formattedString2);
    }

    @Test
    public void givenString_whenParseZonedDateTime_thenOk() {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");
        ZonedDateTimeUnitTest.log.info(zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    }

    @Test
    public void givenString_whenParseZonedDateTimeWithoutZone_thenException() {
        Assertions.assertThrows(DateTimeParseException.class, () -> ZonedDateTime.parse("2011-12-03T10:15:30", DateTimeFormatter.ISO_DATE_TIME));
    }

    @Test
    public void givenString_whenParseLocalDateTimeAtZone_thenOk() {
        ZoneId timeZone = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = LocalDateTime.parse("2011-12-03T10:15:30", DateTimeFormatter.ISO_DATE_TIME).atZone(timeZone);
        ZonedDateTimeUnitTest.log.info(zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    }
}

