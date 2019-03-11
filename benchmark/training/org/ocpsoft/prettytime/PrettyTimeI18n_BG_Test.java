package org.ocpsoft.prettytime;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class PrettyTimeI18n_BG_Test {
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    // Stores current locale so that it can be restored
    private Locale locale;

    @Test
    public void testCenturiesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ????", t.format(new Date((3155692597470L * 3L))));
    }

    @Test
    public void testCenturiesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((3155692597470L * 3L)));
        Assert.assertEquals("????? 3 ????", t.format(new Date(0)));
    }

    @Test
    public void testCenturySingular() throws Exception {
        PrettyTime t = new PrettyTime(new Date(3155692597470L));
        Assert.assertEquals("????? 1 ???", t.format(new Date(0)));
    }

    @Test
    public void testDaysFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ???", t.format(new Date(((((1000 * 60) * 60) * 24) * 3))));
    }

    @Test
    public void testDaysAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((1000 * 60) * 60) * 24) * 3)));
        Assert.assertEquals("????? 3 ???", t.format(new Date(0)));
    }

    @Test
    public void testDaySingular() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 24)));
        Assert.assertEquals("????? 1 ???", t.format(new Date(0)));
    }

    @Test
    public void testDecadesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 3L)));
        Assert.assertEquals("????? 3 ???????????", t.format(new Date(0)));
    }

    @Test
    public void testDecadesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ???????????", t.format(new Date((315569259747L * 3L))));
    }

    @Test
    public void testDecadeSingular() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 1 ???????????", t.format(new Date(315569259747L)));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ????", t.format(new Date((((1000 * 60) * 60) * 3))));
    }

    @Test
    public void testHoursAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 3)));
        Assert.assertEquals("????? 3 ????", t.format(new Date(0)));
    }

    @Test
    public void testHourSingular() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((1000 * 60) * 60)));
        Assert.assertEquals("????? 1 ???", t.format(new Date(0)));
    }

    @Test
    public void testRightNow() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("? ???????", t.format(new Date()));
    }

    @Test
    public void testMomentsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(6000));
        Assert.assertEquals("???? ??", t.format(new Date(0)));
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 12 ??????", t.format(new Date(((1000 * 60) * 12))));
    }

    @Test
    public void testMinutesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((1000 * 60) * 12)));
        Assert.assertEquals("????? 12 ??????", t.format(new Date(0)));
    }

    @Test
    public void testMonthsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ??????", t.format(new Date((2629743830L * 3L))));
    }

    @Test
    public void testMonthsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((2629743830L * 3L)));
        Assert.assertEquals("????? 3 ??????", t.format(new Date(0)));
    }

    @Test
    public void testMonthSingular() throws Exception {
        PrettyTime t = new PrettyTime(new Date(2629743830L));
        Assert.assertEquals("????? 1 ?????", t.format(new Date(0)));
    }

    @Test
    public void testWeeksFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ???????", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 3))));
    }

    @Test
    public void testWeeksAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000 * 60) * 60) * 24) * 7) * 3)));
        Assert.assertEquals("????? 3 ???????", t.format(new Date(0)));
    }

    @Test
    public void testWeekSingular() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((1000 * 60) * 60) * 24) * 7)));
        Assert.assertEquals("????? 1 ???????", t.format(new Date(0)));
    }

    @Test
    public void testYearsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ??????", t.format(new Date(((2629743830L * 12L) * 3L))));
    }

    @Test
    public void testYearsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((2629743830L * 12L) * 3L)));
        Assert.assertEquals("????? 3 ??????", t.format(new Date(0)));
    }

    @Test
    public void testYearSingular() throws Exception {
        PrettyTime t = new PrettyTime(new Date((2629743830L * 12L)));
        Assert.assertEquals("????? 1 ??????", t.format(new Date(0)));
    }

    @Test
    public void testFormattingDurationListInThePast() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((((1000 * 60) * 60) * 24) * 3) + (((1000 * 60) * 60) * 15)) + ((1000 * 60) * 38))));
        List<Duration> durations = t.calculatePreciseDuration(new Date(0));
        Assert.assertEquals("????? 3 ??? 15 ???? 38 ??????", t.format(durations));
    }

    @Test
    public void testFormattingDurationListInTheFuture() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        List<Duration> durations = t.calculatePreciseDuration(new Date(((((((1000 * 60) * 60) * 24) * 3) + (((1000 * 60) * 60) * 15)) + ((1000 * 60) * 38))));
        Assert.assertEquals("???? 3 ??? 15 ???? 38 ??????", t.format(durations));
    }
}

