package org.ocpsoft.prettytime;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.prettytime.units.Minute;


public class PrettyTimeI18n_EL_Test {
    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    private Locale locale;

    @Test
    public void testCeilingInterval() throws Exception {
        Date then = format.parse("5/20/2009");
        Date ref = format.parse("6/17/2009");
        PrettyTime t = new PrettyTime(ref);
        Assert.assertEquals("1 ????? ???? ???", t.format(then));
    }

    @Test
    public void testNullDate() throws Exception {
        PrettyTime t = new PrettyTime();
        Date date = null;
        Assert.assertEquals("??????? ??? ????", t.format(date));
    }

    @Test
    public void testRightNow() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("??????? ??? ????", t.format(new Date()));
    }

    @Test
    public void testCalculatePreciceDuration() throws Exception {
        PrettyTime t = new PrettyTime();
        List<Duration> preciseDuration = t.calculatePreciseDuration(new Date((((System.currentTimeMillis()) - (((2 * 60) * 60) * 1000)) - ((2 * 60) * 1000))));
        Assert.assertEquals("2 ???? 2 ????? ???? ???", t.format(preciseDuration));
        Assert.assertEquals("2 ???? 2 ?????", t.formatDuration(preciseDuration));
        Assert.assertEquals("??????? ??? ????", t.format(t.calculatePreciseDuration(new Date())));
    }

    @Test
    public void testCalculatePreciseDuration2() {
        PrettyTime prettyTime = new PrettyTime();
        prettyTime.clearUnits();
        Minute minutes = new Minute();
        prettyTime.registerUnit(minutes, new org.ocpsoft.prettytime.impl.ResourcesTimeFormat(minutes));
        Assert.assertEquals("40 ????? ???? ???", prettyTime.formatUnrounded(prettyTime.calculatePreciseDuration(new Date((((new Date().getTime()) - ((40 * 60) * 1000)) - (40 * 1000))))));
    }

    @Test
    public void testRightNowVariance() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("??????? ??? ????", t.format(new Date(600)));
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("12 ????? ??? ????", t.format(new Date(((1000 * 60) * 12))));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ???? ??? ????", t.format(new Date((((1000 * 60) * 60) * 3))));
    }

    @Test
    public void testDaysFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ?????? ??? ????", t.format(new Date(((((1000 * 60) * 60) * 24) * 3))));
    }

    @Test
    public void testWeeksFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ????????? ??? ????", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 3))));
    }

    @Test
    public void testMonthsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ????? ??? ????", t.format(new Date((2629743830L * 3L))));
    }

    @Test
    public void testYearsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ??? ??? ????", t.format(new Date(((2629743830L * 12L) * 3L))));
    }

    @Test
    public void testDecadesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ????????? ??? ????", t.format(new Date((315569259747L * 3L))));
    }

    @Test
    public void testCenturiesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ?????? ??? ????", t.format(new Date((3155692597470L * 3L))));
    }

    /* Past */
    @Test
    public void testMomentsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(6000));
        Assert.assertEquals("???? ??? ???????", t.format(new Date(0)));
    }

    @Test
    public void testMinutesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((1000 * 60) * 12)));
        Assert.assertEquals("12 ????? ???? ???", t.format(new Date(0)));
    }

    @Test
    public void testMinutesFromNowDefaultReference() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("12 ????? ??? ????", t.format(new Date(((System.currentTimeMillis()) + ((1000 * 60) * 12)))));
    }

    @Test
    public void testHoursAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 3)));
        Assert.assertEquals("3 ???? ???? ???", t.format(new Date(0)));
    }

    @Test
    public void testHoursAgoDefaultReference() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("3 ???? ???? ???", t.format(new Date(((System.currentTimeMillis()) - (((1000 * 60) * 60) * 3)))));
    }

    @Test
    public void testDaysAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((1000 * 60) * 60) * 24) * 3)));
        Assert.assertEquals("3 ?????? ???? ???", t.format(new Date(0)));
    }

    @Test
    public void testWeeksAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000 * 60) * 60) * 24) * 7) * 3)));
        Assert.assertEquals("3 ????????? ???? ???", t.format(new Date(0)));
    }

    @Test
    public void testMonthsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((2629743830L * 3L)));
        Assert.assertEquals("3 ????? ???? ???", t.format(new Date(0)));
    }

    @Test
    public void testYearsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((2629743830L * 12L) * 3L)));
        Assert.assertEquals("3 ??? ???? ???", t.format(new Date(0)));
    }

    @Test
    public void testDecadesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 3L)));
        Assert.assertEquals("3 ????????? ???? ???", t.format(new Date(0)));
    }

    @Test
    public void testCenturiesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((3155692597470L * 3L)));
        Assert.assertEquals("3 ?????? ???? ???", t.format(new Date(0)));
    }

    @Test
    public void testWithinTwoHoursRounding() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("2 ???? ???? ???", t.format(new Date(((new Date().getTime()) - 6543990))));
    }
}

