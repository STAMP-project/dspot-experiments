package org.javacore.time;


import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TimeUtilTest {
    @Test
    public void testParseSpecificTimeStringByUsingDefaultPattern() throws Exception {
        LocalDateTime expectedDateTime = LocalDateTime.of(2014, 11, 11, 10, 11, 11);
        LocalDateTime parsedTime = TimeUtil.parseTime("2014-11-11 10:11:11");
        Assert.assertEquals(expectedDateTime, parsedTime);
    }

    @Test
    public void testParseSpecificTimeStringUsingTimeFormat() throws Exception {
        LocalDateTime expectedDateTime = LocalDateTime.of(2014, 11, 11, 10, 11, 11);
        LocalDateTime parsedTime = TimeUtil.parseTime("2014/11/11 10:11:11", TimeFormat.LONG_DATE_PATTERN_SLASH);
        Assert.assertEquals(expectedDateTime, parsedTime);
    }

    @Test
    public void testParseLocalDateTimeByUsingDefaultFormatter() throws Exception {
        LocalDateTime time = LocalDateTime.of(2014, 11, 11, 10, 11, 11);
        Assert.assertEquals(TimeUtil.parseTime(time), "2014-11-11 10:11:11");
    }

    @Test
    public void testParseLocalDateTimeByUsingTimeFormat() throws Exception {
        LocalDateTime time = LocalDateTime.of(2014, 11, 11, 10, 11, 11);
        Assert.assertEquals(TimeUtil.parseTime(time, TimeFormat.LONG_DATE_PATTERN_DOUBLE_SLASH), "2014\\11\\11 10:11:11");
    }
}

