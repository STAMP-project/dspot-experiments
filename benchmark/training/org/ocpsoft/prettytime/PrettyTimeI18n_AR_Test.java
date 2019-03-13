/**
 * Copyright 2012 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.prettytime;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;


public class PrettyTimeI18n_AR_Test {
    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    private Locale locale;

    @Test
    public void testCeilingInterval() throws Exception {
        Date then = format.parse("5/20/2009");
        Date ref = format.parse("6/17/2009");
        PrettyTime t = new PrettyTime(ref);
        Assert.assertEquals("??? 1 ???", t.format(then));
    }

    @Test
    public void testFromNow() {
        PrettyTime prettyTime = new PrettyTime(locale);
        Assert.assertEquals("??? ?????", prettyTime.format(new Date()));
    }

    @Test
    public void testNullDate() throws Exception {
        PrettyTime t = new PrettyTime();
        Date date = null;
        Assert.assertEquals("??? ?????", t.format(date));
    }

    @Test
    public void testRightNow() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("??? ?????", t.format(new Date()));
    }

    @Test
    public void testRightNowVariance() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("??? ?????", t.format(new Date(600)));
    }

    @Test
    public void testPrettyTimeDefault() {
        PrettyTime p = new PrettyTime(locale);
        Assert.assertEquals(p.format(new Date()), "??? ?????");
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("??? 12 ?????", t.format(new Date(((1000 * 60) * 12))));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("??? 3 ?????", t.format(new Date((((1000 * 60) * 60) * 3))));
    }

    @Test
    public void testDaysFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("??? 3 ????", t.format(new Date(((((1000 * 60) * 60) * 24) * 3))));
    }

    @Test
    public void testWeeksFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("??? 3 ??????", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 3))));
    }

    @Test
    public void testMonthsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("??? 3 ????", t.format(new Date((2629743830L * 3L))));
    }

    @Test
    public void testYearsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("??? 3 ?????", t.format(new Date(((2629743830L * 12L) * 3L))));
    }

    @Test
    public void testDecadesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("??? 3 ????", t.format(new Date((315569259747L * 3L))));
    }

    @Test
    public void testCenturiesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("??? 3 ????", t.format(new Date((3155692597470L * 3L))));
    }

    /* Past */
    @Test
    public void testMomentsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(6000));
        Assert.assertEquals("??? ?????", t.format(new Date(0)));
    }

    @Test
    public void testMinutesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((1000 * 60) * 12)));
        Assert.assertEquals("??? 12 ?????", t.format(new Date(0)));
    }

    @Test
    public void testHoursAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 3)));
        Assert.assertEquals("??? 3 ?????", t.format(new Date(0)));
    }

    @Test
    public void testDaysAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((1000 * 60) * 60) * 24) * 3)));
        Assert.assertEquals("??? 3 ????", t.format(new Date(0)));
    }

    @Test
    public void testWeeksAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000 * 60) * 60) * 24) * 7) * 3)));
        Assert.assertEquals("??? 3 ??????", t.format(new Date(0)));
    }

    @Test
    public void testMonthsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((2629743830L * 3L)));
        Assert.assertEquals("??? 3 ????", t.format(new Date(0)));
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
        Assert.assertEquals("??? 3 ?????", t.format(new Date(0)));
    }

    @Test
    public void testDecadesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 3L)));
        Assert.assertEquals("??? 3 ????", t.format(new Date(0)));
    }

    @Test
    public void testCenturiesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((3155692597470L * 3L)));
        Assert.assertEquals("??? 3 ????", t.format(new Date(0)));
    }

    @Test
    public void testWithinTwoHoursRounding() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("??? 2 ?????", t.format(new Date(((new Date().getTime()) - 6543990))));
    }

    @Test
    public void testPreciseInTheFuture() throws Exception {
        PrettyTime t = new PrettyTime();
        List<Duration> durations = t.calculatePreciseDuration(new Date(((new Date().getTime()) + (1000 * ((10 * 60) + ((5 * 60) * 60))))));
        Assert.assertTrue(((durations.size()) >= 2));// might be more because of milliseconds between date capturing and result

        // calculation
        Assert.assertEquals(5, durations.get(0).getQuantity());
        Assert.assertEquals(10, durations.get(1).getQuantity());
    }

    @Test
    public void testPreciseInThePast() throws Exception {
        PrettyTime t = new PrettyTime();
        List<Duration> durations = t.calculatePreciseDuration(new Date(((new Date().getTime()) - (1000 * ((10 * 60) + ((5 * 60) * 60))))));
        Assert.assertTrue(((durations.size()) >= 2));// might be more because of milliseconds between date capturing and result

        // calculation
        Assert.assertEquals((-5), durations.get(0).getQuantity());
        Assert.assertEquals((-10), durations.get(1).getQuantity());
    }

    @Test
    public void testFormattingDurationListInThePast() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((((1000 * 60) * 60) * 24) * 3) + (((1000 * 60) * 60) * 15)) + ((1000 * 60) * 38))));
        List<Duration> durations = t.calculatePreciseDuration(new Date(0));
        Assert.assertEquals("??? 3 ???? 15 ????? 38 ?????", t.format(durations));
    }

    @Test
    public void testFormattingDurationListInTheFuture() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        List<Duration> durations = t.calculatePreciseDuration(new Date(((((((1000 * 60) * 60) * 24) * 3) + (((1000 * 60) * 60) * 15)) + ((1000 * 60) * 38))));
        Assert.assertEquals("??? 3 ???? 15 ????? 38 ?????", t.format(durations));
    }

    @Test
    public void testSetLocale() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 3L)));
        Assert.assertEquals("??? 3 ????", t.format(new Date(0)));
    }
}

