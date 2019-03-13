package com.secupwn.aimsicd.utils;


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class MiscUtilsTest {
    @Test
    public void testParseLogcatTimeStamp() throws Exception {
        Date date = MiscUtils.parseLogcatTimeStamp("03-31 01:04:39.348 14308-14308/com.SecUpwN.AIMSICD I/SignalStrengthTracker: Ignored signal sample for");
        Assert.assertEquals(2, date.getMonth());// Month is 0 based

        Assert.assertEquals(31, date.getDate());
        Assert.assertEquals(1, date.getHours());
        Assert.assertEquals(4, date.getMinutes());
        Assert.assertEquals(39, date.getSeconds());
    }
}

