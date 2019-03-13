package cn.hutool.core.date;


import DateField.DAY_OF_MONTH;
import DateField.DAY_OF_YEAR;
import DatePattern.NORM_DATETIME_PATTERN;
import DatePattern.NORM_DATE_PATTERN;
import DateUnit.DAY;
import DateUnit.HOUR;
import DateUnit.MINUTE;
import DateUnit.MS;
import DateUnit.SECOND;
import Week.WEDNESDAY;
import cn.hutool.core.collection.CollUtil;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;

import static DateField.DAY_OF_YEAR;


/**
 * ????????
 *
 * @author Looly
 */
public class DateUtilTest {
    @Test
    public void nowTest() {
        // ????
        Date date = DateUtil.date();
        Assert.assertNotNull(date);
        // ????
        Date date2 = DateUtil.date(Calendar.getInstance());
        Assert.assertNotNull(date2);
        // ????
        Date date3 = DateUtil.date(System.currentTimeMillis());
        Assert.assertNotNull(date3);
        // ???????????yyyy-MM-dd HH:mm:ss
        String now = DateUtil.now();
        Assert.assertNotNull(now);
        // ???????????yyyy-MM-dd
        String today = DateUtil.today();
        Assert.assertNotNull(today);
    }

    @Test
    public void formatAndParseTest() {
        String dateStr = "2017-03-01";
        Date date = DateUtil.parse(dateStr);
        String format = DateUtil.format(date, "yyyy/MM/dd");
        Assert.assertEquals("2017/03/01", format);
        // ????????
        String formatDate = DateUtil.formatDate(date);
        Assert.assertEquals("2017-03-01", formatDate);
        String formatDateTime = DateUtil.formatDateTime(date);
        Assert.assertEquals("2017-03-01 00:00:00", formatDateTime);
        String formatTime = DateUtil.formatTime(date);
        Assert.assertEquals("00:00:00", formatTime);
    }

    @Test
    public void beginAndEndTest() {
        String dateStr = "2017-03-01 22:33:23";
        Date date = DateUtil.parse(dateStr);
        // ?????
        Date beginOfDay = DateUtil.beginOfDay(date);
        Assert.assertEquals("2017-03-01 00:00:00", beginOfDay.toString());
        // ?????
        Date endOfDay = DateUtil.endOfDay(date);
        Assert.assertEquals("2017-03-01 23:59:59", endOfDay.toString());
    }

    @Test
    public void beginAndWeedTest() {
        String dateStr = "2017-03-01 22:33:23";
        Date date = DateUtil.parse(dateStr);
        // ?????
        Date beginOfWeek = DateUtil.beginOfWeek(date);
        Assert.assertEquals("2017-02-27 00:00:00", beginOfWeek.toString());
        // ?????
        Date endOfWeek = DateUtil.endOfWeek(date);
        Assert.assertEquals("2017-03-05 23:59:59", endOfWeek.toString());
        Calendar calendar = DateUtil.calendar(date);
        // ?????
        Calendar begin = DateUtil.beginOfWeek(calendar);
        Assert.assertEquals("2017-02-27 00:00:00", DateUtil.date(begin).toString());
        // ?????
        Calendar end = DateUtil.endOfWeek(calendar);
        Assert.assertEquals("2017-03-05 23:59:59", DateUtil.date(end).toString());
    }

    @Test
    public void offsetDateTest() {
        String dateStr = "2017-03-01 22:33:23";
        Date date = DateUtil.parse(dateStr);
        Date newDate = DateUtil.offset(date, DAY_OF_MONTH, 2);
        Assert.assertEquals("2017-03-03 22:33:23", newDate.toString());
        // ???
        DateTime newDate2 = DateUtil.offsetDay(date, 3);
        Assert.assertEquals("2017-03-04 22:33:23", newDate2.toString());
        // ????
        DateTime newDate3 = DateUtil.offsetHour(date, (-3));
        Assert.assertEquals("2017-03-01 19:33:23", newDate3.toString());
        // ???
        DateTime offsetMonth = DateUtil.offsetMonth(date, (-1));
        Assert.assertEquals("2017-02-01 22:33:23", offsetMonth.toString());
    }

    @Test
    public void offsetMonthTest() {
        DateTime st = DateUtil.parseDate("2018-05-31");
        List<DateTime> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(DateUtil.offsetMonth(st, i));
        }
        Assert.assertEquals("2018-05-31 00:00:00", list.get(0).toString());
        Assert.assertEquals("2018-06-30 00:00:00", list.get(1).toString());
        Assert.assertEquals("2018-07-31 00:00:00", list.get(2).toString());
        Assert.assertEquals("2018-08-31 00:00:00", list.get(3).toString());
    }

    @Test
    public void betweenTest() {
        String dateStr1 = "2017-03-01 22:34:23";
        Date date1 = DateUtil.parse(dateStr1);
        String dateStr2 = "2017-04-01 23:56:14";
        Date date2 = DateUtil.parse(dateStr2);
        // ???
        long betweenMonth = DateUtil.betweenMonth(date1, date2, false);
        Assert.assertEquals(1, betweenMonth);// ?????

        // ??
        betweenMonth = DateUtil.betweenMonth(date2, date1, false);
        Assert.assertEquals(1, betweenMonth);// ?????

        // ???
        long betweenDay = DateUtil.between(date1, date2, DAY);
        Assert.assertEquals(31, betweenDay);// ??????31?

        // ??
        betweenDay = DateUtil.between(date2, date1, DAY);
        Assert.assertEquals(31, betweenDay);// ??????31?

        // ????
        long betweenHour = DateUtil.between(date1, date2, HOUR);
        Assert.assertEquals(745, betweenHour);
        // ??
        betweenHour = DateUtil.between(date2, date1, HOUR);
        Assert.assertEquals(745, betweenHour);
        // ???
        long betweenMinute = DateUtil.between(date1, date2, MINUTE);
        Assert.assertEquals(44721, betweenMinute);
        // ??
        betweenMinute = DateUtil.between(date2, date1, MINUTE);
        Assert.assertEquals(44721, betweenMinute);
        // ???
        long betweenSecond = DateUtil.between(date1, date2, SECOND);
        Assert.assertEquals(2683311, betweenSecond);
        // ??
        betweenSecond = DateUtil.between(date2, date1, SECOND);
        Assert.assertEquals(2683311, betweenSecond);
        // ???
        long betweenMS = DateUtil.between(date1, date2, MS);
        Assert.assertEquals(2683311000L, betweenMS);
        // ??
        betweenMS = DateUtil.between(date2, date1, MS);
        Assert.assertEquals(2683311000L, betweenMS);
    }

    @Test
    public void formatChineseDateTest() {
        String formatChineseDate = DateUtil.formatChineseDate(DateUtil.parse("2018-02-24"), true);
        Assert.assertEquals("???????????", formatChineseDate);
    }

    @Test
    public void formatBetweenTest() {
        String dateStr1 = "2017-03-01 22:34:23";
        Date date1 = DateUtil.parse(dateStr1);
        String dateStr2 = "2017-04-01 23:56:14";
        Date date2 = DateUtil.parse(dateStr2);
        long between = DateUtil.between(date1, date2, MS);
        String formatBetween = DateUtil.formatBetween(between, Level.MINUTE);
        Assert.assertEquals("31?1??21?", formatBetween);
    }

    @Test
    public void timerTest() {
        TimeInterval timer = DateUtil.timer();
        // ---------------------------------
        // -------??????
        // ---------------------------------
        timer.interval();// ?????

        timer.intervalRestart();// ??????????????

        timer.intervalMinute();// ?????

    }

    @Test
    public void currentTest() {
        long current = DateUtil.current(false);
        String currentStr = String.valueOf(current);
        Assert.assertEquals(13, currentStr.length());
        long currentNano = DateUtil.current(true);
        String currentNanoStr = String.valueOf(currentNano);
        Assert.assertNotNull(currentNanoStr);
    }

    @Test
    public void weekOfYearTest() {
        // ?????
        int weekOfYear1 = DateUtil.weekOfYear(DateUtil.parse("2016-01-03"));
        Assert.assertEquals(1, weekOfYear1);
        // ?????
        int weekOfYear2 = DateUtil.weekOfYear(DateUtil.parse("2016-01-07"));
        Assert.assertEquals(2, weekOfYear2);
    }

    @Test
    public void timeToSecondTest() {
        int second = DateUtil.timeToSecond("00:01:40");
        Assert.assertEquals(100, second);
        second = DateUtil.timeToSecond("00:00:40");
        Assert.assertEquals(40, second);
        second = DateUtil.timeToSecond("01:00:00");
        Assert.assertEquals(3600, second);
        second = DateUtil.timeToSecond("00:00:00");
        Assert.assertEquals(0, second);
    }

    @Test
    public void secondToTime() {
        String time = DateUtil.secondToTime(3600);
        Assert.assertEquals("01:00:00", time);
        time = DateUtil.secondToTime(3800);
        Assert.assertEquals("01:03:20", time);
        time = DateUtil.secondToTime(0);
        Assert.assertEquals("00:00:00", time);
        time = DateUtil.secondToTime(30);
        Assert.assertEquals("00:00:30", time);
    }

    @Test
    public void parseTest() throws ParseException {
        String time = "12:11:39";
        DateTime parse = DateUtil.parse("12:11:39");
        Assert.assertEquals(DateUtil.parseTimeToday(time).getTime(), parse.getTime());
    }

    @Test
    public void parseTest2() throws ParseException {
        // ?????SimpleDateFormat????????
        String birthday = "700403";
        Date birthDate = DateUtil.parse(birthday, "yyMMdd");
        // ?????(??????,??2010)
        int sYear = DateUtil.year(birthDate);
        Assert.assertEquals(1970, sYear);
    }

    @Test
    public void parseTest3() throws ParseException {
        String dateStr = "2018-10-10 12:11:11";
        Date date = DateUtil.parse(dateStr);
        String format = DateUtil.format(date, NORM_DATETIME_PATTERN);
        Assert.assertEquals(dateStr, format);
    }

    @Test
    public void parseDateTest() throws ParseException {
        String dateStr = "2018-4-10";
        Date date = DateUtil.parseDate(dateStr);
        String format = DateUtil.format(date, NORM_DATE_PATTERN);
        Assert.assertEquals("2018-04-10", format);
    }

    @Test
    public void parseToDateTimeTest1() {
        String dateStr1 = "2017-02-01";
        String dateStr2 = "2017/02/01";
        String dateStr3 = "2017.02.01";
        String dateStr4 = "2017?02?01?";
        DateTime dt1 = DateUtil.parse(dateStr1);
        DateTime dt2 = DateUtil.parse(dateStr2);
        DateTime dt3 = DateUtil.parse(dateStr3);
        DateTime dt4 = DateUtil.parse(dateStr4);
        Assert.assertEquals(dt1, dt2);
        Assert.assertEquals(dt2, dt3);
        Assert.assertEquals(dt3, dt4);
    }

    @Test
    public void parseToDateTimeTest2() {
        String dateStr1 = "2017-02-01 12:23";
        String dateStr2 = "2017/02/01 12:23";
        String dateStr3 = "2017.02.01 12:23";
        String dateStr4 = "2017?02?01? 12:23";
        DateTime dt1 = DateUtil.parse(dateStr1);
        DateTime dt2 = DateUtil.parse(dateStr2);
        DateTime dt3 = DateUtil.parse(dateStr3);
        DateTime dt4 = DateUtil.parse(dateStr4);
        Assert.assertEquals(dt1, dt2);
        Assert.assertEquals(dt2, dt3);
        Assert.assertEquals(dt3, dt4);
    }

    @Test
    public void parseToDateTimeTest3() {
        String dateStr1 = "2017-02-01 12:23:45";
        String dateStr2 = "2017/02/01 12:23:45";
        String dateStr3 = "2017.02.01 12:23:45";
        String dateStr4 = "2017?02?01? 12?23?45?";
        DateTime dt1 = DateUtil.parse(dateStr1);
        DateTime dt2 = DateUtil.parse(dateStr2);
        DateTime dt3 = DateUtil.parse(dateStr3);
        DateTime dt4 = DateUtil.parse(dateStr4);
        Assert.assertEquals(dt1, dt2);
        Assert.assertEquals(dt2, dt3);
        Assert.assertEquals(dt3, dt4);
    }

    @Test
    public void parseToDateTimeTest4() {
        String dateStr1 = "2017-02-01 12:23:45";
        String dateStr2 = "20170201122345";
        DateTime dt1 = DateUtil.parse(dateStr1);
        DateTime dt2 = DateUtil.parse(dateStr2);
        Assert.assertEquals(dt1, dt2);
    }

    @Test
    public void parseToDateTimeTest5() {
        String dateStr1 = "2017-02-01";
        String dateStr2 = "20170201";
        DateTime dt1 = DateUtil.parse(dateStr1);
        DateTime dt2 = DateUtil.parse(dateStr2);
        Assert.assertEquals(dt1, dt2);
    }

    @Test
    public void parseUTCTest() throws ParseException {
        String dateStr1 = "2018-09-13T05:34:31Z";
        DateTime dt = DateUtil.parseUTC(dateStr1);
        // parse????UTC????
        DateTime dt2 = DateUtil.parse(dateStr1);
        Assert.assertEquals(dt, dt2);
        // ????Pattern???????UTC??
        String dateStr = dt.toString();
        Assert.assertEquals("2018-09-13 05:34:31", dateStr);
        // ??????????
        dateStr = dt.toString(TimeZone.getTimeZone("GMT+8:00"));
        Assert.assertEquals("2018-09-13 13:34:31", dateStr);
    }

    @Test
    public void endOfWeekTest() {
        DateTime now = DateUtil.date();
        DateTime startOfWeek = DateUtil.beginOfWeek(now);
        DateTime endOfWeek = DateUtil.endOfWeek(now);
        long between = DateUtil.between(endOfWeek, startOfWeek, DAY);
        // ???????6?
        Assert.assertEquals(6, between);
    }

    @Test
    public void dayOfWeekTest() {
        int dayOfWeek = DateUtil.dayOfWeek(DateUtil.parse("2018-03-07"));
        Assert.assertEquals(Calendar.WEDNESDAY, dayOfWeek);
        Week week = DateUtil.dayOfWeekEnum(DateUtil.parse("2018-03-07"));
        Assert.assertEquals(WEDNESDAY, week);
    }

    @Test
    public void rangeTest() {
        DateTime start = DateUtil.parse("2017-01-01");
        DateTime end = DateUtil.parse("2017-01-03");
        // ???????????????1???
        DateRange range = DateUtil.range(start, end, DAY_OF_YEAR);
        Assert.assertEquals(range.next(), DateUtil.parse("2017-01-01"));
        Assert.assertEquals(range.next(), DateUtil.parse("2017-01-02"));
        Assert.assertEquals(range.next(), DateUtil.parse("2017-01-03"));
        try {
            range.next();
            Assert.fail("?????????????????");
        } catch (NoSuchElementException e) {
        }
        // ????????
        range = new DateRange(start, end, DAY_OF_YEAR, 2);
        Assert.assertEquals(range.next(), DateUtil.parse("2017-01-01"));
        Assert.assertEquals(range.next(), DateUtil.parse("2017-01-03"));
        // ??????????????
        range = new DateRange(start, end, DAY_OF_YEAR, 1, false, false);
        Assert.assertEquals(range.next(), DateUtil.parse("2017-01-02"));
        try {
            range.next();
            Assert.fail("??????????????????????");
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void rangeToListTest() {
        DateTime start = DateUtil.parse("2017-01-01");
        DateTime end = DateUtil.parse("2017-01-31");
        List<DateTime> rangeToList = DateUtil.rangeToList(start, end, DAY_OF_YEAR);
        Assert.assertEquals(rangeToList.get(0), DateUtil.parse("2017-01-01"));
        Assert.assertEquals(rangeToList.get(1), DateUtil.parse("2017-01-02"));
    }

    @Test
    public void yearAndQTest() {
        String yearAndQuarter = DateUtil.yearAndQuarter(DateUtil.parse("2018-12-01"));
        Assert.assertEquals("20184", yearAndQuarter);
        LinkedHashSet<String> yearAndQuarters = DateUtil.yearAndQuarter(DateUtil.parse("2018-09-10"), DateUtil.parse("2018-12-20"));
        List<String> list = CollUtil.list(false, yearAndQuarters);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("20183", list.get(0));
        Assert.assertEquals("20184", list.get(1));
        LinkedHashSet<String> yearAndQuarters2 = DateUtil.yearAndQuarter(DateUtil.parse("2018-10-10"), DateUtil.parse("2018-12-10"));
        List<String> list2 = CollUtil.list(false, yearAndQuarters2);
        Assert.assertEquals(1, list2.size());
        Assert.assertEquals("20184", list2.get(0));
    }

    @Test
    public void formatHttpDateTest() {
        String formatHttpDate = DateUtil.formatHttpDate(DateUtil.parse("2019-01-02 22:32:01"));
        Assert.assertEquals("Wed, 02 Jan 2019 14:32:01 GMT", formatHttpDate);
    }
}

