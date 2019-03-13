package cn.hutool.core.date;


import DateField.YEAR;
import Month.JANUARY;
import Quarter.Q1;
import Quarter.Q2;
import Quarter.Q3;
import Quarter.Q4;
import org.junit.Assert;
import org.junit.Test;

import static DatePattern.NORM_DATETIME_FORMAT;
import static DatePattern.NORM_DATETIME_MS_FORMAT;


/**
 * DateTime????
 *
 * @author Looly
 */
public class DateTimeTest {
    @Test
    public void datetimeTest() {
        DateTime dateTime = new DateTime("2017-01-05 12:34:23", NORM_DATETIME_FORMAT);
        // ?
        int year = dateTime.year();
        Assert.assertEquals(2017, year);
        // ???????
        Quarter season = dateTime.quarterEnum();
        Assert.assertEquals(Q1, season);
        // ??
        Month month = dateTime.monthEnum();
        Assert.assertEquals(JANUARY, month);
        // ?
        int day = dateTime.dayOfMonth();
        Assert.assertEquals(5, day);
    }

    @Test
    public void quarterTest() {
        DateTime dateTime = new DateTime("2017-01-05 12:34:23", NORM_DATETIME_FORMAT);
        Quarter quarter = dateTime.quarterEnum();
        Assert.assertEquals(Q1, quarter);
        dateTime = new DateTime("2017-04-05 12:34:23", NORM_DATETIME_FORMAT);
        quarter = dateTime.quarterEnum();
        Assert.assertEquals(Q2, quarter);
        dateTime = new DateTime("2017-07-05 12:34:23", NORM_DATETIME_FORMAT);
        quarter = dateTime.quarterEnum();
        Assert.assertEquals(Q3, quarter);
        dateTime = new DateTime("2017-10-05 12:34:23", NORM_DATETIME_FORMAT);
        quarter = dateTime.quarterEnum();
        Assert.assertEquals(Q4, quarter);
        // ?????
        DateTime beginTime = new DateTime("2017-10-01 00:00:00.000", NORM_DATETIME_MS_FORMAT);
        dateTime = DateUtil.beginOfQuarter(dateTime);
        Assert.assertEquals(beginTime, dateTime);
        // ?????
        DateTime endTime = new DateTime("2017-12-31 23:59:59.999", NORM_DATETIME_MS_FORMAT);
        dateTime = DateUtil.endOfQuarter(dateTime);
        Assert.assertEquals(endTime, dateTime);
    }

    @Test
    public void mutableTest() {
        DateTime dateTime = new DateTime("2017-01-05 12:34:23", NORM_DATETIME_FORMAT);
        // ?????DateTime?????
        DateTime offsite = dateTime.offset(YEAR, 0);
        Assert.assertTrue((offsite == dateTime));
        // ?????????????????
        dateTime.setMutable(false);
        offsite = dateTime.offset(YEAR, 0);
        Assert.assertFalse((offsite == dateTime));
    }

    @Test
    public void toStringTest() {
        DateTime dateTime = new DateTime("2017-01-05 12:34:23", NORM_DATETIME_FORMAT);
        Assert.assertEquals("2017-01-05 12:34:23", dateTime.toString());
        String dateStr = dateTime.toString("yyyy/MM/dd");
        Assert.assertEquals("2017/01/05", dateStr);
    }

    @Test
    public void monthTest() {
        int month = DateUtil.parse("2017-07-01").month();
        Assert.assertEquals(6, month);
    }

    @Test
    public void weekOfYearTest() {
        DateTime date = DateUtil.parse("2016-12-27");
        Assert.assertEquals(2016, date.year());
        // ?????????1
        Assert.assertEquals(1, date.weekOfYear());
    }
}

