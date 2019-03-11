package org.ocpsoft.prettytime;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;

import static java.util.concurrent.TimeUnit.MINUTES;


/**
 * Most languages (using the standard %n %u pattern) will render something like: {prefix} {number} {unitName} {suffix}.
 */
public class PrettyTimeI18n_JA_Test {
    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    private Locale locale;

    @Test
    public void testNullDate() throws Exception {
        PrettyTime t = new PrettyTime(locale);
        Date date = null;
        // moments from now
        Assert.assertEquals("?????", t.format(date));
    }

    @Test
    public void testRightNow() throws Exception {
        PrettyTime t = new PrettyTime(locale);
        // moments from now
        Assert.assertEquals("?????", t.format(new Date()));
    }

    @Test
    public void testRightNowVariance() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        // moments from now
        Assert.assertEquals("?????", t.format(new Date(600)));
    }

    @Test
    public void testMillisecondsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        t.removeUnit(JustNow.class);
        // 450 milliseconds from now
        Assert.assertEquals("450 milliseconds from now", "???450????", t.format(new Date(450L)));
    }

    @Test
    public void testSecondsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        t.removeUnit(JustNow.class);
        // 36 seconds from now
        Assert.assertEquals("36 seconds from now", "???36??", t.format(new Date((1000L * 36L))));
        t.removeUnit(Millisecond.class);
        // 1 second from now
        Assert.assertEquals("1 second from now", "???1??", t.format(new Date(10)));
        // 1 second from now
        Assert.assertEquals("1 second from now", "???1??", t.formatUnrounded(new Date(10)));
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        // 12 minutes from now
        Assert.assertEquals("12 minutes from now", "???12??", t.format(new Date(((1000 * 60) * 12))));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        // 3 hours from now
        Assert.assertEquals("3 hours from now", "???3???", t.format(new Date((((1000 * 60) * 60) * 3))));
    }

    @Test
    public void testDaysFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        // 3 days from now
        Assert.assertEquals("3 days from now", "???3??", t.format(new Date(((((1000 * 60) * 60) * 24) * 3))));
    }

    @Test
    public void testWeeksFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        // 3 weeks from now
        Assert.assertEquals("3 weeks from now", "???3???", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 3))));
    }

    @Test
    public void testMonthsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        // 3 months from now
        Assert.assertEquals("3 months from now", "???3???", t.format(new Date((2629743830L * 3L))));
    }

    @Test
    public void testYearsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        // 3 years from now
        Assert.assertEquals("3 years from now", "???3??", t.format(new Date(((2629743830L * 12L) * 3L))));
    }

    @Test
    public void testOneDecadeFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        // 1 decade from now
        Assert.assertEquals("???10??", t.format(new Date((315569259747L * 1L))));
    }

    @Test
    public void testCenturiesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        // 3 centuries from now
        Assert.assertEquals("3 centuries from now", "???3???", t.format(new Date((3155692597470L * 3L))));
    }

    @Test
    public void testMillenniumFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        // 3 millennia from now
        Assert.assertEquals("3 millennia from now", "???3000??", t.format(new Date(((3155692597470L * 10L) * 3L))));
    }

    /* Past */
    @Test
    public void testMomentsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(6000), locale);
        // moments ago
        Assert.assertEquals("????", t.format(new Date(0)));
    }

    @Test
    public void testMillisecondsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(450L), locale);
        t.removeUnit(JustNow.class);
        // 450 milliseconds ago
        Assert.assertEquals("450 milliseconds ago", "450????", t.format(new Date(0)));
    }

    @Test
    public void testSecondsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((1000 * 36)), locale);
        t.removeUnit(JustNow.class);
        // 36 seconds ago
        Assert.assertEquals("36 seconds ago", "36??", t.format(new Date(0)));
        t.setReference(new Date(10)).removeUnit(Millisecond.class);
        // 1 second ago
        Assert.assertEquals("1 second ago", "1??", t.format(new Date(0)));
    }

    @Test
    public void testMinutesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((1000 * 60) * 12)), locale);
        // 12 minutes ago
        Assert.assertEquals("12??", t.format(new Date(0)));
    }

    @Test
    public void testHoursAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 3)), locale);
        // 3 hours ago
        Assert.assertEquals("3???", t.format(new Date(0)));
    }

    @Test
    public void testDaysAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((1000 * 60) * 60) * 24) * 3)), locale);
        // 3 days ago
        Assert.assertEquals("3??", t.format(new Date(0)));
    }

    @Test
    public void testWeeksAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000 * 60) * 60) * 24) * 7) * 3)), locale);
        // 3 weeks ago
        Assert.assertEquals("3???", t.format(new Date(0)));
    }

    @Test
    public void testOneMonthAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((2629743830L * 1L)), locale);
        // 3 months ago
        Assert.assertEquals("1 months ago", "1???", t.format(new Date(0)));
    }

    @Test
    public void testMonthsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((2629743830L * 3L)), locale);
        // 3 months ago
        Assert.assertEquals("3 months ago", "3???", t.format(new Date(0)));
    }

    @Test
    public void testCustomFormat() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        TimeUnit unit = new TimeUnit() {
            @Override
            public long getMaxQuantity() {
                return 0;
            }

            @Override
            public long getMillisPerUnit() {
                return 5000;
            }

            @Override
            public boolean isPrecise() {
                return false;
            }
        };
        t.clearUnits();
        t.registerUnit(unit, new SimpleTimeFormat().setSingularName("tick").setPluralName("ticks").setPattern("%n %u").setRoundingTolerance(20).setFutureSuffix("... RUN!").setFuturePrefix("self destruct in: ").setPastPrefix("self destruct was: ").setPastSuffix(" ago..."));
        Assert.assertEquals("self destruct in: 5 ticks ... RUN!", t.format(new Date(25000)));
        t.setReference(new Date(25000));
        Assert.assertEquals("self destruct was: 5 ticks ago...", t.format(new Date(0)));
    }

    @Test
    public void testYearsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((2629743830L * 12L) * 3L)), locale);
        // 3 years ago
        Assert.assertEquals("3??", t.format(new Date(0)));
    }

    @Test
    public void testDecadeAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 1L)), locale);
        // 1 decade ago
        Assert.assertEquals("10??", t.format(new Date(0)));
    }

    @Test
    public void testCenturiesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((3155692597470L * 3L)), locale);
        // 3 centuries ago
        Assert.assertEquals("3 centuries ago", "3???", t.format(new Date(0)));
    }

    @Test
    public void testMilleniumAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((3155692597470L * 10) * 3L)), locale);
        // 3 millennia ago
        Assert.assertEquals("3 millennia ago", "3000??", t.format(new Date(0)));
    }

    @Test
    public void testWithinTwoHoursRounding() throws Exception {
        PrettyTime t = new PrettyTime(locale);
        // 2 hours ago
        Assert.assertEquals("2???", t.format(new Date(((new Date().getTime()) - 6543990))));
    }

    @Test
    public void testPreciseInTheFuture() throws Exception {
        PrettyTime t = new PrettyTime(locale);
        List<Duration> durations = t.calculatePreciseDuration(new Date(((new Date().getTime()) + (1000 * ((10 * 60) + ((5 * 60) * 60))))));
        Assert.assertTrue(((durations.size()) >= 2));
        Assert.assertEquals(5, durations.get(0).getQuantity());
        Assert.assertEquals(10, durations.get(1).getQuantity());
    }

    @Test
    public void testPreciseInThePast() throws Exception {
        PrettyTime t = new PrettyTime(locale);
        List<Duration> durations = t.calculatePreciseDuration(new Date(((new Date().getTime()) - (1000 * ((10 * 60) + ((5 * 60) * 60))))));
        Assert.assertTrue(((durations.size()) >= 2));
        Assert.assertEquals((-5), durations.get(0).getQuantity());
        Assert.assertEquals((-10), durations.get(1).getQuantity());
    }

    @Test
    public void testSetLocale() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 1L)), locale);
        // 1 decade ago
        Assert.assertEquals("10??", t.format(new Date(0)));
        t.setLocale(Locale.GERMAN);
        Assert.assertEquals("vor 1 Jahrzehnt", t.format(new Date(0)));
    }

    /**
     * Tests formatApproximateDuration and by proxy, formatDuration.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFormatApproximateDuration() throws Exception {
        long tenMinMillis = MINUTES.toMillis(10);
        Date tenMinAgo = new Date(((System.currentTimeMillis()) - tenMinMillis));
        PrettyTime t = new PrettyTime();
        String result = t.formatDuration(tenMinAgo);
        // 10 minutes
        assert result.equals("10?");
    }
}

