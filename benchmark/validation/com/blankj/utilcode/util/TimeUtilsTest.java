package com.blankj.utilcode.util;


import TimeConstants.DAY;
import TimeConstants.MIN;
import TimeConstants.MSEC;
import TimeConstants.SEC;
import com.blankj.utilcode.constant.TimeConstants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/12
 *     desc  : test TimeUtils
 * </pre>
 */
public class TimeUtilsTest extends BaseTest {
    private final DateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private final DateFormat mFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss", Locale.getDefault());

    private final long timeMillis = 1493887049000L;// 2017-05-04 16:37:29


    private final Date timeDate = new Date(timeMillis);

    private final String timeString = defaultFormat.format(timeDate);

    private final String timeStringFormat = mFormat.format(timeDate);

    private final long tomorrowTimeMillis = 1493973449000L;

    private final Date tomorrowTimeDate = new Date(tomorrowTimeMillis);

    private final String tomorrowTimeString = defaultFormat.format(tomorrowTimeDate);

    private final String tomorrowTimeStringFormat = mFormat.format(tomorrowTimeDate);

    private final long delta = 10;// ???? 10ms


    @Test
    public void millis2String() {
        Assert.assertEquals(timeString, TimeUtils.millis2String(timeMillis));
        Assert.assertEquals(timeStringFormat, TimeUtils.millis2String(timeMillis, mFormat));
    }

    @Test
    public void string2Millis() {
        Assert.assertEquals(timeMillis, TimeUtils.string2Millis(timeString));
        Assert.assertEquals(timeMillis, TimeUtils.string2Millis(timeStringFormat, mFormat));
    }

    @Test
    public void string2Date() {
        Assert.assertEquals(timeDate, TimeUtils.string2Date(timeString));
        Assert.assertEquals(timeDate, TimeUtils.string2Date(timeStringFormat, mFormat));
    }

    @Test
    public void date2String() {
        Assert.assertEquals(timeString, TimeUtils.date2String(timeDate));
        Assert.assertEquals(timeStringFormat, TimeUtils.date2String(timeDate, mFormat));
    }

    @Test
    public void date2Millis() {
        Assert.assertEquals(timeMillis, TimeUtils.date2Millis(timeDate));
    }

    @Test
    public void millis2Date() {
        Assert.assertEquals(timeDate, TimeUtils.millis2Date(timeMillis));
    }

    @Test
    public void getTimeSpan() {
        long testTimeMillis = (timeMillis) + (120 * (TimeConstants.SEC));
        String testTimeString = TimeUtils.millis2String(testTimeMillis);
        String testTimeStringFormat = TimeUtils.millis2String(testTimeMillis, mFormat);
        Date testTimeDate = TimeUtils.millis2Date(testTimeMillis);
        Assert.assertEquals((-120), TimeUtils.getTimeSpan(timeString, testTimeString, SEC));
        Assert.assertEquals(2, TimeUtils.getTimeSpan(testTimeStringFormat, timeStringFormat, mFormat, MIN));
        Assert.assertEquals((-2), TimeUtils.getTimeSpan(timeDate, testTimeDate, MIN));
        Assert.assertEquals(120, TimeUtils.getTimeSpan(testTimeMillis, timeMillis, SEC));
    }

    @Test
    public void getFitTimeSpan() {
        long testTimeMillis = (((timeMillis) + (10 * (TimeConstants.DAY))) + (10 * (TimeConstants.MIN))) + (10 * (TimeConstants.SEC));
        String testTimeString = TimeUtils.millis2String(testTimeMillis);
        String testTimeStringFormat = TimeUtils.millis2String(testTimeMillis, mFormat);
        Date testTimeDate = TimeUtils.millis2Date(testTimeMillis);
        Assert.assertEquals("-10?10??10?", TimeUtils.getFitTimeSpan(timeString, testTimeString, 5));
        Assert.assertEquals("10?10??10?", TimeUtils.getFitTimeSpan(testTimeStringFormat, timeStringFormat, mFormat, 5));
        Assert.assertEquals("-10?10??10?", TimeUtils.getFitTimeSpan(timeDate, testTimeDate, 5));
        Assert.assertEquals("10?10??10?", TimeUtils.getFitTimeSpan(testTimeMillis, timeMillis, 5));
    }

    @Test
    public void getNowMills() {
        Assert.assertEquals(System.currentTimeMillis(), TimeUtils.getNowMills(), delta);
    }

    @Test
    public void getNowString() {
        Assert.assertEquals(System.currentTimeMillis(), TimeUtils.string2Millis(TimeUtils.getNowString()), delta);
        Assert.assertEquals(System.currentTimeMillis(), TimeUtils.string2Millis(TimeUtils.getNowString(mFormat), mFormat), delta);
    }

    @Test
    public void getNowDate() {
        Assert.assertEquals(System.currentTimeMillis(), TimeUtils.date2Millis(TimeUtils.getNowDate()), delta);
    }

    @Test
    public void getTimeSpanByNow() {
        Assert.assertEquals(0, TimeUtils.getTimeSpanByNow(TimeUtils.getNowString(), MSEC), delta);
        Assert.assertEquals(0, TimeUtils.getTimeSpanByNow(TimeUtils.getNowString(mFormat), mFormat, MSEC), delta);
        Assert.assertEquals(0, TimeUtils.getTimeSpanByNow(TimeUtils.getNowDate(), MSEC), delta);
        Assert.assertEquals(0, TimeUtils.getTimeSpanByNow(TimeUtils.getNowMills(), MSEC), delta);
    }

    @Test
    public void getFitTimeSpanByNow() {
        // long spanMillis = 6 * TimeConstants.DAY + 6 * TimeConstants.HOUR + 6 * TimeConstants.MIN + 6 * TimeConstants.SEC;
        // assertEquals("6?6??6??6?", TimeUtils.getFitTimeSpanByNow(TimeUtils.millis2String(System.currentTimeMillis() + spanMillis), 4));
        // assertEquals("6?6??6??6?", TimeUtils.getFitTimeSpanByNow(TimeUtils.millis2String(System.currentTimeMillis() + spanMillis, mFormat), mFormat, 4));
        // assertEquals("6?6??6??6?", TimeUtils.getFitTimeSpanByNow(TimeUtils.millis2Date(System.currentTimeMillis() + spanMillis), 4));
        // assertEquals("6?6??6??6?", TimeUtils.getFitTimeSpanByNow(System.currentTimeMillis() + spanMillis, 4));
    }

    @Test
    public void getFriendlyTimeSpanByNow() {
        Assert.assertEquals("??", TimeUtils.getFriendlyTimeSpanByNow(TimeUtils.getNowString()));
        Assert.assertEquals("??", TimeUtils.getFriendlyTimeSpanByNow(TimeUtils.getNowString(mFormat), mFormat));
        Assert.assertEquals("??", TimeUtils.getFriendlyTimeSpanByNow(TimeUtils.getNowDate()));
        Assert.assertEquals("??", TimeUtils.getFriendlyTimeSpanByNow(TimeUtils.getNowMills()));
        Assert.assertEquals("1??", TimeUtils.getFriendlyTimeSpanByNow(((TimeUtils.getNowMills()) - (TimeConstants.SEC))));
        Assert.assertEquals("1???", TimeUtils.getFriendlyTimeSpanByNow(((TimeUtils.getNowMills()) - (TimeConstants.MIN))));
    }

    @Test
    public void getMillis() {
        Assert.assertEquals(tomorrowTimeMillis, TimeUtils.getMillis(timeMillis, 1, DAY));
        Assert.assertEquals(tomorrowTimeMillis, TimeUtils.getMillis(timeString, 1, DAY));
        Assert.assertEquals(tomorrowTimeMillis, TimeUtils.getMillis(timeStringFormat, mFormat, 1, DAY));
        Assert.assertEquals(tomorrowTimeMillis, TimeUtils.getMillis(timeDate, 1, DAY));
    }

    @Test
    public void getString() {
        Assert.assertEquals(tomorrowTimeString, TimeUtils.getString(timeMillis, 1, DAY));
        Assert.assertEquals(tomorrowTimeStringFormat, TimeUtils.getString(timeMillis, mFormat, 1, DAY));
        Assert.assertEquals(tomorrowTimeString, TimeUtils.getString(timeString, 1, DAY));
        Assert.assertEquals(tomorrowTimeStringFormat, TimeUtils.getString(timeStringFormat, mFormat, 1, DAY));
        Assert.assertEquals(tomorrowTimeString, TimeUtils.getString(timeDate, 1, DAY));
        Assert.assertEquals(tomorrowTimeStringFormat, TimeUtils.getString(timeDate, mFormat, 1, DAY));
    }

    @Test
    public void getDate() {
        Assert.assertEquals(tomorrowTimeDate, TimeUtils.getDate(timeMillis, 1, DAY));
        Assert.assertEquals(tomorrowTimeDate, TimeUtils.getDate(timeString, 1, DAY));
        Assert.assertEquals(tomorrowTimeDate, TimeUtils.getDate(timeStringFormat, mFormat, 1, DAY));
        Assert.assertEquals(tomorrowTimeDate, TimeUtils.getDate(timeDate, 1, DAY));
    }

    @Test
    public void getMillisByNow() {
        Assert.assertEquals(((System.currentTimeMillis()) + (TimeConstants.DAY)), TimeUtils.getMillisByNow(1, DAY), delta);
    }

    @Test
    public void getStringByNow() {
        long tomorrowMillis = TimeUtils.string2Millis(TimeUtils.getStringByNow(1, DAY));
        Assert.assertEquals(((System.currentTimeMillis()) + (TimeConstants.DAY)), tomorrowMillis, delta);
        tomorrowMillis = TimeUtils.string2Millis(TimeUtils.getStringByNow(1, mFormat, DAY), mFormat);
        Assert.assertEquals(((System.currentTimeMillis()) + (TimeConstants.DAY)), tomorrowMillis, delta);
    }

    @Test
    public void getDateByNow() {
        long tomorrowMillis = TimeUtils.date2Millis(TimeUtils.getDateByNow(1, DAY));
        Assert.assertEquals(((System.currentTimeMillis()) + (TimeConstants.DAY)), TimeUtils.getMillisByNow(1, DAY), delta);
    }

    @Test
    public void isToday() {
        long todayTimeMillis = System.currentTimeMillis();
        String todayTimeString = TimeUtils.millis2String(todayTimeMillis);
        String todayTimeStringFormat = TimeUtils.millis2String(todayTimeMillis, mFormat);
        Date todayTimeDate = TimeUtils.millis2Date(todayTimeMillis);
        long tomorrowTimeMillis = todayTimeMillis + (TimeConstants.DAY);
        String tomorrowTimeString = TimeUtils.millis2String(tomorrowTimeMillis);
        Date tomorrowTimeDate = TimeUtils.millis2Date(tomorrowTimeMillis);
        Assert.assertTrue(TimeUtils.isToday(todayTimeString));
        Assert.assertTrue(TimeUtils.isToday(todayTimeStringFormat, mFormat));
        Assert.assertTrue(TimeUtils.isToday(todayTimeDate));
        Assert.assertTrue(TimeUtils.isToday(todayTimeMillis));
        Assert.assertFalse(TimeUtils.isToday(tomorrowTimeString));
        Assert.assertFalse(TimeUtils.isToday(tomorrowTimeStringFormat, mFormat));
        Assert.assertFalse(TimeUtils.isToday(tomorrowTimeDate));
        Assert.assertFalse(TimeUtils.isToday(tomorrowTimeMillis));
    }

    @Test
    public void isLeapYear() {
        Assert.assertFalse(TimeUtils.isLeapYear(timeString));
        Assert.assertFalse(TimeUtils.isLeapYear(timeStringFormat, mFormat));
        Assert.assertFalse(TimeUtils.isLeapYear(timeDate));
        Assert.assertFalse(TimeUtils.isLeapYear(timeMillis));
        Assert.assertTrue(TimeUtils.isLeapYear(2016));
        Assert.assertFalse(TimeUtils.isLeapYear(2017));
    }

    @Test
    public void getChineseWeek() {
        Assert.assertEquals("???", TimeUtils.getChineseWeek(timeString));
        Assert.assertEquals("???", TimeUtils.getChineseWeek(timeStringFormat, mFormat));
        Assert.assertEquals("???", TimeUtils.getChineseWeek(timeDate));
        Assert.assertEquals("???", TimeUtils.getChineseWeek(timeMillis));
    }

    @Test
    public void getUSWeek() {
        Assert.assertEquals("Thursday", TimeUtils.getUSWeek(timeString));
        Assert.assertEquals("Thursday", TimeUtils.getUSWeek(timeStringFormat, mFormat));
        Assert.assertEquals("Thursday", TimeUtils.getUSWeek(timeDate));
        Assert.assertEquals("Thursday", TimeUtils.getUSWeek(timeMillis));
    }

    @Test
    public void getWeekIndex() {
        Assert.assertEquals(5, TimeUtils.getValueByCalendarField(timeString, Calendar.DAY_OF_WEEK));
        Assert.assertEquals(5, TimeUtils.getValueByCalendarField(timeString, Calendar.DAY_OF_WEEK));
        Assert.assertEquals(5, TimeUtils.getValueByCalendarField(timeStringFormat, mFormat, Calendar.DAY_OF_WEEK));
        Assert.assertEquals(5, TimeUtils.getValueByCalendarField(timeDate, Calendar.DAY_OF_WEEK));
        Assert.assertEquals(5, TimeUtils.getValueByCalendarField(timeMillis, Calendar.DAY_OF_WEEK));
    }

    @Test
    public void getWeekOfMonth() {
        Assert.assertEquals(1, TimeUtils.getValueByCalendarField(timeString, Calendar.WEEK_OF_MONTH));
        Assert.assertEquals(1, TimeUtils.getValueByCalendarField(timeStringFormat, mFormat, Calendar.WEEK_OF_MONTH));
        Assert.assertEquals(1, TimeUtils.getValueByCalendarField(timeDate, Calendar.WEEK_OF_MONTH));
        Assert.assertEquals(1, TimeUtils.getValueByCalendarField(timeMillis, Calendar.WEEK_OF_MONTH));
    }

    @Test
    public void getWeekOfYear() {
        Assert.assertEquals(18, TimeUtils.getValueByCalendarField(timeString, Calendar.WEEK_OF_YEAR));
        Assert.assertEquals(18, TimeUtils.getValueByCalendarField(timeStringFormat, mFormat, Calendar.WEEK_OF_YEAR));
        Assert.assertEquals(18, TimeUtils.getValueByCalendarField(timeDate, Calendar.WEEK_OF_YEAR));
        Assert.assertEquals(18, TimeUtils.getValueByCalendarField(timeMillis, Calendar.WEEK_OF_YEAR));
    }

    @Test
    public void getChineseZodiac() {
        Assert.assertEquals("?", TimeUtils.getChineseZodiac(timeString));
        Assert.assertEquals("?", TimeUtils.getChineseZodiac(timeStringFormat, mFormat));
        Assert.assertEquals("?", TimeUtils.getChineseZodiac(timeDate));
        Assert.assertEquals("?", TimeUtils.getChineseZodiac(timeMillis));
        Assert.assertEquals("?", TimeUtils.getChineseZodiac(2017));
    }

    @Test
    public void getZodiac() {
        Assert.assertEquals("???", TimeUtils.getZodiac(timeString));
        Assert.assertEquals("???", TimeUtils.getZodiac(timeStringFormat, mFormat));
        Assert.assertEquals("???", TimeUtils.getZodiac(timeDate));
        Assert.assertEquals("???", TimeUtils.getZodiac(timeMillis));
        Assert.assertEquals("???", TimeUtils.getZodiac(8, 16));
    }
}

