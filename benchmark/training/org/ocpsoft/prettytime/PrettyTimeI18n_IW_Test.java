/**
 *
 *
 * @author ranmeirman
 */
package org.ocpsoft.prettytime;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;
import org.ocpsoft.prettytime.units.Minute;

import static java.util.concurrent.TimeUnit.MINUTES;


public class PrettyTimeI18n_IW_Test {
    SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");

    private Locale locale;

    @Test
    public void testNullDate() throws Exception {
        PrettyTime t = new PrettyTime();
        Date date = null;
        Assert.assertEquals("??? ???", t.format(date));
    }

    @Test
    public void testRightNow() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("??? ???", t.format(new Date()));
    }

    @Test
    public void testCalculatePreciceDuration() throws Exception {
        PrettyTime t = new PrettyTime();
        List<Duration> preciseDuration = t.calculatePreciseDuration(new Date((((System.currentTimeMillis()) - (((2 * 60) * 60) * 1000)) - ((2 * 60) * 1000))));
        Assert.assertEquals("???? 2 ???? 2 ????", t.format(preciseDuration));
        Assert.assertEquals("2 ???? 2 ????", t.formatDuration(preciseDuration));
        Assert.assertEquals("??? ???", t.format(t.calculatePreciseDuration(new Date())));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testCalculatePreciceDurationMillenia() throws Exception {
        PrettyTime t = new PrettyTime(new Date(2014, 8, 15, 0, 0));
        List<Duration> durations = t.calculatePreciseDuration(new Date(0));
        Assert.assertEquals("???? 1 ??????? 9 ???? 4 ?????? 4 ???? 8 ?????? 1 ???? 6 ???? 20 ???? 5 ????", t.format(durations));
        Assert.assertEquals("1 ??????? 9 ???? 4 ?????? 4 ???? 8 ?????? 1 ???? 6 ???? 20 ???? 5 ????", t.formatDuration(durations));
    }

    @Test
    public void testCalculatePreciseDuration2() {
        PrettyTime prettyTime = new PrettyTime();
        prettyTime.clearUnits();
        Minute minutes = new Minute();
        prettyTime.registerUnit(minutes, new org.ocpsoft.prettytime.impl.ResourcesTimeFormat(minutes));
        Assert.assertEquals("???? 40 ????", prettyTime.formatUnrounded(prettyTime.calculatePreciseDuration(new Date((((new Date().getTime()) - ((40 * 60) * 1000)) - (40 * 1000))))));
    }

    @Test
    public void testRightNowVariance() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("??? ???", t.format(new Date(600)));
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 12 ????", t.format(new Date(((1000 * 60) * 12))));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ????", t.format(new Date((((1000 * 60) * 60) * 3))));
    }

    @Test
    public void testDaysFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ????", t.format(new Date(((((1000 * 60) * 60) * 24) * 3))));
    }

    @Test
    public void testWeeksFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ??????", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 3))));
    }

    @Test
    public void testMonthsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ??????", t.format(new Date((2629743830L * 3L))));
    }

    @Test
    public void testYearsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ????", t.format(new Date(((2629743830L * 12L) * 3L))));
    }

    @Test
    public void testDecadesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ??????", t.format(new Date((315569259747L * 3L))));
    }

    @Test
    public void testCenturiesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("???? 3 ????", t.format(new Date((3155692597470L * 3L))));
    }

    /* Past */
    @Test
    public void testMomentsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(6000));
        Assert.assertEquals("???? ???", t.format(new Date(0)));
    }

    @Test
    public void testMinutesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((1000 * 60) * 12)));
        Assert.assertEquals("???? 12 ????", t.format(new Date(0)));
    }

    @Test
    public void testMinutesFromNowDefaultReference() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("???? 12 ????", t.format(new Date(((System.currentTimeMillis()) + ((1000 * 60) * 12)))));
    }

    @Test
    public void testHoursAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 3)));
        Assert.assertEquals("???? 3 ????", t.format(new Date(0)));
    }

    @Test
    public void testHoursAgoDefaultReference() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("???? 3 ????", t.format(new Date(((System.currentTimeMillis()) - (((1000 * 60) * 60) * 3)))));
    }

    @Test
    public void testDaysAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((1000 * 60) * 60) * 24) * 3)));
        Assert.assertEquals("???? 3 ????", t.format(new Date(0)));
    }

    @Test
    public void testWeeksAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000 * 60) * 60) * 24) * 7) * 3)));
        Assert.assertEquals("???? 3 ??????", t.format(new Date(0)));
    }

    @Test
    public void testMonthsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((2629743830L * 3L)));
        Assert.assertEquals("???? 3 ??????", t.format(new Date(0)));
    }

    @Test
    public void testCustomFormat() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
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
        PrettyTime t = new PrettyTime(new Date(((2629743830L * 12L) * 3L)));
        Assert.assertEquals("???? 3 ????", t.format(new Date(0)));
    }

    @Test
    public void testDecadesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 3L)));
        Assert.assertEquals("???? 3 ??????", t.format(new Date(0)));
    }

    @Test
    public void testCenturiesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((3155692597470L * 3L)));
        Assert.assertEquals("???? 3 ????", t.format(new Date(0)));
    }

    @Test
    public void testWithinTwoHoursRounding() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("???? 2 ????", t.format(new Date(((new Date().getTime()) - 6543990))));
    }

    @Test
    public void testPreciseInTheFuture() throws Exception {
        PrettyTime t = new PrettyTime();
        List<Duration> durations = t.calculatePreciseDuration(new Date(((new Date().getTime()) + (1000 * ((10 * 60) + ((5 * 60) * 60))))));
        Assert.assertTrue(((durations.size()) >= 2));
        Assert.assertEquals(5, durations.get(0).getQuantity());
        Assert.assertEquals(10, durations.get(1).getQuantity());
    }

    @Test
    public void testPreciseInThePast() throws Exception {
        PrettyTime t = new PrettyTime();
        List<Duration> durations = t.calculatePreciseDuration(new Date(((new Date().getTime()) - (1000 * ((10 * 60) + ((5 * 60) * 60))))));
        Assert.assertTrue(((durations.size()) >= 2));
        Assert.assertEquals((-5), durations.get(0).getQuantity());
        Assert.assertEquals((-10), durations.get(1).getQuantity());
    }

    @Test
    public void testFormattingDurationListInThePast() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((((1000 * 60) * 60) * 24) * 3) + (((1000 * 60) * 60) * 15)) + ((1000 * 60) * 38))));
        List<Duration> durations = t.calculatePreciseDuration(new Date(0));
        Assert.assertEquals("???? 3 ???? 15 ???? 38 ????", t.format(durations));
    }

    @Test
    public void testFormattingDurationListInTheFuture() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        List<Duration> durations = t.calculatePreciseDuration(new Date(((((((1000 * 60) * 60) * 24) * 3) + (((1000 * 60) * 60) * 15)) + ((1000 * 60) * 38))));
        Assert.assertEquals("???? 3 ???? 15 ???? 38 ????", t.format(durations));
    }

    @Test
    public void testSetLocale() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 3L)));
        Assert.assertEquals("???? 3 ??????", t.format(new Date(0)));
        t.setLocale(Locale.GERMAN);
        Assert.assertEquals("vor 3 Jahrzehnten", t.format(new Date(0)));
    }

    /**
     * Tests formatApproximateDuration and by proxy, formatDuration.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFormatDuration() throws Exception {
        long tenMinMillis = MINUTES.toMillis(10);
        Date tenMinAgo = new Date(((System.currentTimeMillis()) - tenMinMillis));
        PrettyTime t = new PrettyTime();
        String result = t.formatDuration(tenMinAgo);
        Assert.assertTrue(result.equals("10 ????"));
    }

    @Test
    public void testFormatDurationWithRounding() throws Exception {
        long tenMinMillis = (MINUTES.toMillis(10)) + (1000 * 40);
        Date tenMinAgo = new Date(((System.currentTimeMillis()) - tenMinMillis));
        PrettyTime t = new PrettyTime();
        String result = t.formatDuration(tenMinAgo);
        Assert.assertTrue(result.equals("11 ????"));
    }

    @Test
    public void testFormatDurationUnrounded() throws Exception {
        long tenMinMillis = (MINUTES.toMillis(10)) + (1000 * 40);
        Date tenMinAgo = new Date(((System.currentTimeMillis()) - tenMinMillis));
        PrettyTime t = new PrettyTime();
        String result = t.formatDurationUnrounded(tenMinAgo);
        Assert.assertTrue(result.equals("10 ????"));
    }

    @Test
    public void testFormatList() throws Exception {
        PrettyTime prettyTime = new PrettyTime();
        prettyTime.clearUnits();
        Minute minutes = new Minute();
        prettyTime.registerUnit(minutes, new org.ocpsoft.prettytime.impl.ResourcesTimeFormat(minutes));
        Assert.assertEquals("???? 41 ????", prettyTime.format(prettyTime.calculatePreciseDuration(new Date((((new Date().getTime()) - ((40 * 60) * 1000)) - (40 * 1000))))));
    }

    @Test
    public void testFormatListUnrounded() throws Exception {
        PrettyTime prettyTime = new PrettyTime();
        prettyTime.clearUnits();
        Minute minutes = new Minute();
        prettyTime.registerUnit(minutes, new org.ocpsoft.prettytime.impl.ResourcesTimeFormat(minutes));
        Assert.assertEquals("???? 40 ????", prettyTime.formatUnrounded(prettyTime.calculatePreciseDuration(new Date((((new Date().getTime()) - ((40 * 60) * 1000)) - (40 * 1000))))));
    }

    @Test
    public void testFormatDurationList() throws Exception {
        PrettyTime prettyTime = new PrettyTime();
        prettyTime.clearUnits();
        Minute minutes = new Minute();
        prettyTime.registerUnit(minutes, new org.ocpsoft.prettytime.impl.ResourcesTimeFormat(minutes));
        Assert.assertEquals("41 ????", prettyTime.formatDuration(prettyTime.calculatePreciseDuration(new Date((((new Date().getTime()) - ((40 * 60) * 1000)) - (40 * 1000))))));
    }

    @Test
    public void testFormatDurationListUnrounded() throws Exception {
        PrettyTime prettyTime = new PrettyTime();
        prettyTime.clearUnits();
        Minute minutes = new Minute();
        prettyTime.registerUnit(minutes, new org.ocpsoft.prettytime.impl.ResourcesTimeFormat(minutes));
        Assert.assertEquals("40 ????", prettyTime.formatDurationUnrounded(prettyTime.calculatePreciseDuration(new Date((((new Date().getTime()) - ((40 * 60) * 1000)) - (40 * 1000))))));
    }

    @Test
    public void testCalculatePreciseDuration() {
        PrettyTime prettyTime = new PrettyTime();
        prettyTime.clearUnits();
        Minute minutes = new Minute();
        prettyTime.registerUnit(minutes, new org.ocpsoft.prettytime.impl.ResourcesTimeFormat(minutes));
        Assert.assertEquals("???? 41 ????", prettyTime.format(prettyTime.calculatePreciseDuration(new Date((((new Date().getTime()) - ((40 * 60) * 1000)) - (40 * 1000))))));
    }

    @Test
    public void testCalculatePreciseDurationUnrounded() {
        PrettyTime prettyTime = new PrettyTime();
        prettyTime.clearUnits();
        Minute minutes = new Minute();
        prettyTime.registerUnit(minutes, new org.ocpsoft.prettytime.impl.ResourcesTimeFormat(minutes));
        Assert.assertEquals("???? 40 ????", prettyTime.formatUnrounded(prettyTime.calculatePreciseDuration(new Date((((new Date().getTime()) - ((40 * 60) * 1000)) - (40 * 1000))))));
    }
}

