package io.ribot.app;


import io.ribot.app.util.DateUtil;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class DateUtilTest {
    @Test
    public void isTodayTrue() {
        Assert.assertTrue(DateUtil.isToday(new Date().getTime()));
    }

    @Test
    public void isTodayFalse() {
        Assert.assertTrue(DateUtil.isToday(((new Date().getTime()) - (((24 * 60) * 60) * 100))));
    }
}

