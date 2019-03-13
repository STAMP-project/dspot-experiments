package org.ocpsoft.prettytime;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class PrettyTimeI18n_TH_Test {
    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    private Locale locale;

    @Test
    public void testLocaleISOCorrectness() {
        Assert.assertEquals("th", this.locale.getLanguage());
        Assert.assertEquals("???", this.locale.getDisplayLanguage());
    }

    @Test
    public void testFromNow() {
        PrettyTime prettyTime = new PrettyTime(locale);
        Assert.assertEquals("????????????????????", prettyTime.format(new Date()));
    }

    @Test
    public void testNullDate() throws Exception {
        PrettyTime t = new PrettyTime();
        Date date = null;
        Assert.assertEquals("????????????????????", t.format(date));
    }

    @Test
    public void testPrettyTimeDefault() {
        PrettyTime p = new PrettyTime(locale);
        Assert.assertEquals(p.format(new Date()), "????????????????????");
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("12 ???? ???????????", t.format(new Date(((1000 * 60) * 12))));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ??????? ?????????", t.format(new Date((((1000 * 60) * 60) * 3))));
    }

    @Test
    public void testDaysFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ??? ???????????", t.format(new Date(((((1000 * 60) * 60) * 24) * 3))));
    }

    @Test
    public void testWeeksFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ??????? ???????????", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 3))));
    }

    @Test
    public void testMonthsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ????? ?????????", t.format(new Date((2629743830L * 3L))));
    }

    @Test
    public void testCenturiesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("3 ?????? ?????????", t.format(new Date((3155692597470L * 3L))));
    }

    /* Past */
    @Test
    public void testMomentsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(6000));
        Assert.assertEquals("???????????", t.format(new Date(0)));
    }

    @Test
    public void testMinutesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((1000 * 60) * 12)));
        Assert.assertEquals("12 ???? ????", t.format(new Date(0)));
    }

    @Test
    public void testHoursAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 3)));
        Assert.assertEquals("3 ??????? ????", t.format(new Date(0)));
    }

    @Test
    public void testDaysAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((1000 * 60) * 60) * 24) * 3)));
        Assert.assertEquals("3 ??? ????", t.format(new Date(0)));
    }

    @Test
    public void testWeeksAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000 * 60) * 60) * 24) * 7) * 3)));
        Assert.assertEquals("3 ??????? ????", t.format(new Date(0)));
    }

    @Test
    public void testMonthsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((2629743830L * 3L)));
        Assert.assertEquals("3 ????? ????", t.format(new Date(0)));
    }

    @Test
    public void testDecadesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 3L)));
        Assert.assertEquals("3 ?????? ????", t.format(new Date(0)));
    }

    @Test
    public void testCenturiesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((3155692597470L * 3L)));
        Assert.assertEquals("3 ?????? ????", t.format(new Date(0)));
    }
}

