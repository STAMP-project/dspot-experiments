package org.embulk.spi.time;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.Test;


public class TestTimestampFormatter {
    @Test
    public void testJava() {
        testJavaToFormat(OffsetDateTime.of(2017, 2, 28, 2, 0, 45, 0, ZoneOffset.UTC).toInstant(), "EEE MMM dd HH:mm:ss uuuu XXXXX", "-07:00", "Mon Feb 27 19:00:45 2017 -07:00");
    }

    @Test
    public void testRuby() {
        testRubyToFormat(OffsetDateTime.of(2017, 2, 28, 2, 0, 45, 0, ZoneOffset.UTC).toInstant(), "%Y-%m-%dT%H:%M:%S %Z", "-09:00", "2017-02-27T17:00:45 -09:00");
    }

    @Test
    public void testLegacy() {
        testLegacyToFormat(OffsetDateTime.of(2017, 2, 28, 2, 0, 45, 0, ZoneOffset.UTC).toInstant(), "%Y-%m-%dT%H:%M:%S %Z", "Asia/Tokyo", "2017-02-28T11:00:45 JST");
    }
}

