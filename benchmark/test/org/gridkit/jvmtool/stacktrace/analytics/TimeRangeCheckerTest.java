package org.gridkit.jvmtool.stacktrace.analytics;


import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


public class TimeRangeCheckerTest {
    private static String DATE_FORMAT = "yyyy.MM.dd_HH:mm:ss";

    private static TimeZone UTC = TimeZone.getTimeZone("UTC");

    @Test
    public void verify_hour_wrap_range() {
        TimeRangeChecker checker = new TimeRangeChecker("10:10", "12:20", TimeRangeCheckerTest.UTC);
        Assert.assertFalse(checker.evaluate(date("2016.02.21_08:08:09")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_09:10:10")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_10:10:11")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_11:11:18")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_12:11:40")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_13:12:19")));
        Assert.assertFalse(checker.evaluate(date("2016.02.21_14:12:20")));
        Assert.assertFalse(checker.evaluate(date("2016.02.21_15:12:21")));
        // Second pass
        Assert.assertFalse(checker.evaluate(date("2016.02.21_08:08:09")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_09:10:10")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_10:10:11")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_11:11:18")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_12:11:40")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_13:12:19")));
        Assert.assertFalse(checker.evaluate(date("2016.02.21_14:12:20")));
        Assert.assertFalse(checker.evaluate(date("2016.02.21_15:12:21")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_08:08:09")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_09:10:10")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_10:10:11")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_11:11:18")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_12:11:40")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_13:12:19")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_14:12:20")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_15:12:21")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_08:06:09")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_09:11:10")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_10:11:11")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_11:11:18")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_12:12:10")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_14:12:20")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_15:12:21")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_15:13:21")));
    }

    @Test
    public void verify_hour_wrap_range_inverted() {
        TimeRangeChecker checker = new TimeRangeChecker("18:10", "02:20", TimeRangeCheckerTest.UTC);
        Assert.assertFalse(checker.evaluate(date("2016.02.21_08:17:00")));
        Assert.assertFalse(checker.evaluate(date("2016.02.21_09:18:09")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_10:18:10")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_11:00:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_12:00:15")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_13:01:40")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_14:02:19")));
        Assert.assertFalse(checker.evaluate(date("2016.02.21_15:02:20")));
        Assert.assertFalse(checker.evaluate(date("2016.02.21_16:02:21")));
        // Second pass
        Assert.assertFalse(checker.evaluate(date("2016.02.21_08:17:00")));
        Assert.assertFalse(checker.evaluate(date("2016.02.21_09:18:09")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_10:18:10")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_11:00:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_12:00:15")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_13:01:40")));
        Assert.assertTrue(checker.evaluate(date("2016.02.21_14:02:19")));
        Assert.assertFalse(checker.evaluate(date("2016.02.21_15:02:20")));
        Assert.assertFalse(checker.evaluate(date("2016.02.21_16:02:21")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_08:17:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_09:18:09")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_10:18:10")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_11:00:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_12:00:15")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_13:01:40")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_14:02:19")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_15:02:20")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_16:02:21")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_08:17:05")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_09:18:7")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_10:18:12")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_11:01:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_12:01:15")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_13:01:40")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_14:02:10")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_15:02:22")));
        Assert.assertTrue(checker.isCached(date("2016.02.21_16:14:00")));
    }

    @Test
    public void verify_day_wrap_range() {
        TimeRangeChecker checker = new TimeRangeChecker("10:10:00", "12:20:00", TimeRangeCheckerTest.UTC);
        Assert.assertFalse(checker.evaluate(date("2016.02.10_08:09:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.11_10:10:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.12_10:11:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.13_11:18:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.14_11:40:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.15_12:19:59")));
        Assert.assertFalse(checker.evaluate(date("2016.02.16_12:20:00")));
        Assert.assertFalse(checker.evaluate(date("2016.02.17_12:21:00")));
        // Second pass
        Assert.assertFalse(checker.evaluate(date("2016.02.10_08:09:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.11_10:10:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.12_10:11:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.13_11:18:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.14_11:40:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.15_12:19:59")));
        Assert.assertFalse(checker.evaluate(date("2016.02.16_12:20:00")));
        Assert.assertFalse(checker.evaluate(date("2016.02.17_12:21:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.10_08:09:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.11_10:10:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.12_10:11:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.13_11:18:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.14_11:40:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.15_12:19:59")));
        Assert.assertTrue(checker.isCached(date("2016.02.16_12:20:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.17_12:21:00")));
    }

    @Test
    public void verify_day_wrap_range_inverted() {
        TimeRangeChecker checker = new TimeRangeChecker("18:10:00", "02:20:00", TimeRangeCheckerTest.UTC);
        Assert.assertFalse(checker.evaluate(date("2016.02.10_17:00:00")));
        Assert.assertFalse(checker.evaluate(date("2016.02.11_18:09:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.12_18:10:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.13_00:00:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.14_00:15:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.15_01:40:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.16_02:19:59")));
        Assert.assertFalse(checker.evaluate(date("2016.02.17_02:20:00")));
        Assert.assertFalse(checker.evaluate(date("2016.02.18_02:21:00")));
        // Second pass
        Assert.assertFalse(checker.evaluate(date("2016.02.10_17:00:00")));
        Assert.assertFalse(checker.evaluate(date("2016.02.11_18:09:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.12_18:10:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.13_00:00:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.14_00:15:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.15_01:40:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.16_02:19:59")));
        Assert.assertFalse(checker.evaluate(date("2016.02.17_02:20:00")));
        Assert.assertFalse(checker.evaluate(date("2016.02.18_02:21:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.10_17:00:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.11_18:09:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.12_18:10:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.13_00:00:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.14_00:15:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.15_01:40:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.16_02:19:59")));
        Assert.assertTrue(checker.isCached(date("2016.02.17_02:20:00")));
        Assert.assertTrue(checker.isCached(date("2016.02.18_02:21:00")));
    }

    @Test
    public void verify_month_wrap_range() {
        TimeRangeChecker checker = new TimeRangeChecker("10_18:10:00", "20_02:20:00", TimeRangeCheckerTest.UTC);
        Assert.assertFalse(checker.evaluate(date("2016.02.10_18:09:00")));
        Assert.assertTrue(checker.evaluate(date("2016.03.10_18:10:00")));
        Assert.assertTrue(checker.evaluate(date("2016.04.12_10:11:00")));
        Assert.assertTrue(checker.evaluate(date("2016.05.13_11:18:00")));
        Assert.assertTrue(checker.evaluate(date("2016.06.14_11:40:00")));
        Assert.assertTrue(checker.evaluate(date("2016.07.20_02:19:59")));
        Assert.assertFalse(checker.evaluate(date("2016.08.20_02:20:00")));
        Assert.assertFalse(checker.evaluate(date("2016.09.20_02:21:00")));
        // Second pass
        Assert.assertFalse(checker.evaluate(date("2016.02.10_18:09:00")));
        Assert.assertTrue(checker.evaluate(date("2016.03.10_18:10:00")));
        Assert.assertTrue(checker.evaluate(date("2016.04.12_10:11:00")));
        Assert.assertTrue(checker.evaluate(date("2016.05.13_11:18:00")));
        Assert.assertTrue(checker.evaluate(date("2016.06.14_11:40:00")));
        Assert.assertTrue(checker.evaluate(date("2016.07.20_02:19:59")));
        Assert.assertFalse(checker.evaluate(date("2016.08.20_02:20:00")));
        Assert.assertFalse(checker.evaluate(date("2016.09.20_02:21:00")));
    }

    @Test
    public void verify_year_wrap_range() {
        TimeRangeChecker checker = new TimeRangeChecker("02.20_18:10:00", "04.10_02:20:00", TimeRangeCheckerTest.UTC);
        Assert.assertFalse(checker.evaluate(date("2016.02.20_18:09:00")));
        Assert.assertTrue(checker.evaluate(date("2017.02.20_18:10:00")));
        Assert.assertTrue(checker.evaluate(date("2018.02.25_10:11:00")));
        Assert.assertTrue(checker.evaluate(date("2019.03.13_11:18:00")));
        Assert.assertTrue(checker.evaluate(date("2020.03.14_11:40:00")));
        Assert.assertTrue(checker.evaluate(date("2021.04.10_02:19:59")));
        Assert.assertFalse(checker.evaluate(date("2022.04.10_02:20:00")));
        Assert.assertFalse(checker.evaluate(date("2023.09.20_02:21:00")));
        // Second pass
        Assert.assertFalse(checker.evaluate(date("2016.02.20_18:09:00")));
        Assert.assertTrue(checker.evaluate(date("2017.02.20_18:10:00")));
        Assert.assertTrue(checker.evaluate(date("2018.02.25_10:11:00")));
        Assert.assertTrue(checker.evaluate(date("2019.03.13_11:18:00")));
        Assert.assertTrue(checker.evaluate(date("2020.03.14_11:40:00")));
        Assert.assertTrue(checker.evaluate(date("2021.04.10_02:19:59")));
        Assert.assertFalse(checker.evaluate(date("2022.04.10_02:20:00")));
        Assert.assertFalse(checker.evaluate(date("2023.09.20_02:21:00")));
    }

    @Test
    public void verify_year_accurate_range() {
        TimeRangeChecker checker = new TimeRangeChecker("16.02.20_18:10:00", "16.04.10_02:20:00", TimeRangeCheckerTest.UTC);
        Assert.assertFalse(checker.evaluate(date("2015.02.20_18:10:00")));
        Assert.assertFalse(checker.evaluate(date("2016.02.20_18:09:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.20_18:10:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.25_10:11:00")));
        Assert.assertTrue(checker.evaluate(date("2016.03.13_11:18:00")));
        Assert.assertTrue(checker.evaluate(date("2016.03.14_11:40:00")));
        Assert.assertTrue(checker.evaluate(date("2016.04.10_02:19:59")));
        Assert.assertFalse(checker.evaluate(date("2016.04.10_02:20:00")));
        Assert.assertFalse(checker.evaluate(date("2016.09.20_02:21:00")));
    }

    @Test
    public void verify_millenium_accurate_range() {
        TimeRangeChecker checker = new TimeRangeChecker("2016.02.20_18:10:00", "2016.04.10_02:20:00", TimeRangeCheckerTest.UTC);
        Assert.assertFalse(checker.evaluate(date("2015.02.20_18:10:00")));
        Assert.assertFalse(checker.evaluate(date("2016.02.20_18:09:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.20_18:10:00")));
        Assert.assertTrue(checker.evaluate(date("2016.02.25_10:11:00")));
        Assert.assertTrue(checker.evaluate(date("2016.03.13_11:18:00")));
        Assert.assertTrue(checker.evaluate(date("2016.03.14_11:40:00")));
        Assert.assertTrue(checker.evaluate(date("2016.04.10_02:19:59")));
        Assert.assertFalse(checker.evaluate(date("2016.04.10_02:20:00")));
        Assert.assertFalse(checker.evaluate(date("2016.09.20_02:21:00")));
    }
}

