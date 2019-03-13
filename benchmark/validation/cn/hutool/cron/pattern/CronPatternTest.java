package cn.hutool.cron.pattern;


import cn.hutool.core.date.DateUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * ?????????
 *
 * @author Looly
 */
public class CronPatternTest {
    @Test
    public void matchAllTest() {
        CronPattern pattern;
        // ??????
        pattern = new CronPattern("* * * * *");
        Assert.assertTrue(pattern.match(DateUtil.current(false), true));
        Assert.assertTrue(pattern.match(DateUtil.current(false), false));
    }

    @Test
    public void cronPatternTest() {
        CronPattern pattern;
        // 12:11??
        pattern = new CronPattern("39 11 12 * * *");
        assertMatch(pattern, "12:11:39");
        // ?5???????????[0,5,10,15,20,25,30,35,40,45,50,55]
        pattern = new CronPattern("39 */5 * * * *");
        assertMatch(pattern, "12:00:39");
        assertMatch(pattern, "12:05:39");
        assertMatch(pattern, "12:10:39");
        assertMatch(pattern, "12:15:39");
        assertMatch(pattern, "12:20:39");
        assertMatch(pattern, "12:25:39");
        assertMatch(pattern, "12:30:39");
        assertMatch(pattern, "12:35:39");
        assertMatch(pattern, "12:40:39");
        assertMatch(pattern, "12:45:39");
        assertMatch(pattern, "12:50:39");
        assertMatch(pattern, "12:55:39");
        // 2:01,3:01,4:01
        pattern = new CronPattern("39 1 2-4 * * *");
        assertMatch(pattern, "02:01:39");
        assertMatch(pattern, "03:01:39");
        assertMatch(pattern, "04:01:39");
        // 2:01,3:01,4:01
        pattern = new CronPattern("39 1 2,3,4 * * *");
        assertMatch(pattern, "02:01:39");
        assertMatch(pattern, "03:01:39");
        assertMatch(pattern, "04:01:39");
        // 08-07, 08-06
        pattern = new CronPattern("39 0 0 6,7 8 *");
        assertMatch(pattern, "2016-08-07 00:00:39");
        assertMatch(pattern, "2016-08-06 00:00:39");
        // ???????
        pattern = new CronPattern("39 0 0 6,7 Aug *");
        assertMatch(pattern, "2016-08-06 00:00:39");
        assertMatch(pattern, "2016-08-07 00:00:39");
        pattern = new CronPattern("39 0 0 7 aug *");
        assertMatch(pattern, "2016-08-07 00:00:39");
        // ???
        pattern = new CronPattern("39 0 0 * * Thu");
        assertMatch(pattern, "2017-02-09 00:00:39");
        assertMatch(pattern, "2017-02-09 00:00:39");
    }

    @Test
    public void CronPatternTest2() {
        CronPattern pattern = new CronPattern("0/30 * * * *");
        Assert.assertTrue(pattern.match(DateUtil.parse("2018-10-09 12:00:00").getTime(), false));
        Assert.assertTrue(pattern.match(DateUtil.parse("2018-10-09 12:30:00").getTime(), false));
        pattern = new CronPattern("32 * * * *");
        Assert.assertTrue(pattern.match(DateUtil.parse("2018-10-09 12:32:00").getTime(), false));
    }

    @Test
    public void quartzPatternTest() {
        CronPattern pattern = new CronPattern("* 0 4 * * ?");
        assertMatch(pattern, "2017-02-09 04:00:00");
        assertMatch(pattern, "2017-02-19 04:00:33");
        // 6?Quartz?????
        pattern = new CronPattern("* 0 4 * * ?");
        assertMatch(pattern, "2017-02-09 04:00:00");
        assertMatch(pattern, "2017-02-19 04:00:33");
    }

    @Test
    public void quartzRangePatternTest() {
        CronPattern pattern = new CronPattern("* 20/2 * * * ?");
        assertMatch(pattern, "2017-02-09 04:20:00");
        assertMatch(pattern, "2017-02-09 05:20:00");
        assertMatch(pattern, "2017-02-19 04:22:33");
        pattern = new CronPattern("* 2-20/2 * * * ?");
        assertMatch(pattern, "2017-02-09 04:02:00");
        assertMatch(pattern, "2017-02-09 05:04:00");
        assertMatch(pattern, "2017-02-19 04:20:33");
    }
}

