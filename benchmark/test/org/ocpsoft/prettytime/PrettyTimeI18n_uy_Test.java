package org.ocpsoft.prettytime;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created with IntelliJ IDEA. User: xirwajim Date: 2016-04-17 Time: 23:00
 */
public class PrettyTimeI18n_uy_Test {
    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    private Locale locale;

    @Test
    public void testPrettyTime() {
        PrettyTime p = new PrettyTime(locale);
        Assert.assertEquals("??????", p.format(new Date()));
    }

    @Test
    public void testPrettyTimeCenturies() {
        PrettyTime p = new PrettyTime(new Date((3155692597470L * 3L)), locale);
        Assert.assertEquals("3 ????? ???????", p.format(new Date(0)));
        p = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 ????? ?????", p.format(new Date((3155692597470L * 3L))));
    }

    @Test
    public void testCeilingInterval() throws Exception {
        Date then = format.parse("20/5/2009");
        Date ref = format.parse("17/6/2009");
        PrettyTime t = new PrettyTime(ref, locale);
        Assert.assertEquals("1 ??? ???????", t.format(then));
    }

    @Test
    public void testNullDate() throws Exception {
        PrettyTime t = new PrettyTime(locale);
        Date date = null;
        Assert.assertEquals("??????", t.format(date));
    }

    @Test
    public void testRightNow() throws Exception {
        PrettyTime t = new PrettyTime(locale);
        Assert.assertEquals("??????", t.format(new Date()));
    }

    @Test
    public void testRightNowVariance() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("??????", t.format(new Date(600)));
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("12 ????? ?????", t.format(new Date(((1000 * 60) * 12))));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 ????? ?????", t.format(new Date((((1000 * 60) * 60) * 3))));
    }

    @Test
    public void testDaysFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 ??? ?????", t.format(new Date(((((1000 * 60) * 60) * 24) * 3))));
    }

    @Test
    public void testWeeksFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 ????? ?????", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 3))));
    }

    @Test
    public void testMonthsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 ??? ?????", t.format(new Date((2629743830L * 3L))));
    }

    @Test
    public void testYearsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 ??? ?????", t.format(new Date(((2629743830L * 12L) * 3L))));
    }

    @Test
    public void testDecadesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 0 ??? ?????", t.format(new Date((315569259747L * 3L))));
    }

    @Test
    public void testCenturiesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 ????? ?????", t.format(new Date((3155692597470L * 3L))));
    }

    /* Past */
    @Test
    public void testMomentsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(6000), locale);
        Assert.assertEquals("??????", t.format(new Date(0)));
    }

    @Test
    public void testMinutesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((1000 * 60) * 12)), locale);
        Assert.assertEquals("12 ????? ???????", t.format(new Date(0)));
    }

    @Test
    public void test1HourAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 1)), locale);
        Assert.assertEquals("1 ????? ???????", t.format(new Date(0)));
    }

    @Test
    public void test3HoursAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 3)), locale);
        Assert.assertEquals("3 ????? ???????", t.format(new Date(0)));
    }

    @Test
    public void test6HoursAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 6)), locale);
        Assert.assertEquals("6 ????? ???????", t.format(new Date(0)));
    }

    @Test
    public void testDaysAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((1000 * 60) * 60) * 24) * 3)), locale);
        Assert.assertEquals("3 ??? ???????", t.format(new Date(0)));
    }

    @Test
    public void testWeeksAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000 * 60) * 60) * 24) * 7) * 3)), locale);
        Assert.assertEquals("3 ????? ???????", t.format(new Date(0)));
    }

    @Test
    public void testMonthsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((2629743830L * 3L)), locale);
        Assert.assertEquals("3 ??? ???????", t.format(new Date(0)));
    }

    @Test
    public void testYearsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((2629743830L * 12L) * 3L)), locale);
        Assert.assertEquals("3 ??? ???????", t.format(new Date(0)));
    }

    @Test
    public void test8YearsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((2629743830L * 12L) * 8L)), locale);
        Assert.assertEquals("8 ??? ???????", t.format(new Date(0)));
    }

    @Test
    public void testDecadesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 3L)), locale);
        Assert.assertEquals("3 0 ??? ???????", t.format(new Date(0)));
    }

    @Test
    public void test8DecadesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 8L)), locale);
        Assert.assertEquals("8 0 ??? ???????", t.format(new Date(0)));
    }

    @Test
    public void testCenturiesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((3155692597470L * 3L)), locale);
        Assert.assertEquals("3 ????? ???????", t.format(new Date(0)));
    }
}

