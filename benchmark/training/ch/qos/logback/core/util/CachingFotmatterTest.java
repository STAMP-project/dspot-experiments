package ch.qos.logback.core.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


public class CachingFotmatterTest {
    static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm";

    SimpleDateFormat sdf = new SimpleDateFormat(CachingFotmatterTest.DATE_PATTERN);

    TimeZone perthTZ = TimeZone.getTimeZone("Australia/Perth");

    TimeZone utcTZ = TimeZone.getTimeZone("UTC");

    @Test
    public void timeZoneIsTakenIntoAccount() throws ParseException {
        CachingDateFormatter cdf = new CachingDateFormatter(CachingFotmatterTest.DATE_PATTERN);
        TimeZone perthTZ = TimeZone.getTimeZone("Australia/Perth");
        cdf.setTimeZone(perthTZ);
        Date march26_2015_0949_UTC = sdf.parse("2015-03-26T09:49");
        System.out.print(march26_2015_0949_UTC);
        String result = cdf.format(march26_2015_0949_UTC.getTime());
        // AWST (Perth) is 8 hours ahead of UTC
        Assert.assertEquals("2015-03-26T17:49", result);
    }
}

