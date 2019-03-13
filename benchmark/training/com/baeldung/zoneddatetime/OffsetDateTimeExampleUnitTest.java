package com.baeldung.zoneddatetime;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.Assert;
import org.junit.Test;


public class OffsetDateTimeExampleUnitTest {
    OffsetDateTimeExample offsetDateTimeExample = new OffsetDateTimeExample();

    @Test
    public void givenZoneOffset_whenGetCurrentTime_thenResultHasZone() {
        String offset = "+02:00";
        OffsetDateTime time = offsetDateTimeExample.getCurrentTimeByZoneOffset(offset);
        Assert.assertTrue(time.getOffset().equals(ZoneOffset.of(offset)));
    }
}

