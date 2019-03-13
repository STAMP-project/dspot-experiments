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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Month;

import static java.util.concurrent.TimeUnit.MINUTES;


public class PrettyTimeI18n_CS_Test {
    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    private static Locale locale;

    @Test
    public void testCeilingInterval() throws Exception {
        Date then = format.parse("5/20/2009");
        Date ref = format.parse("6/17/2009");
        PrettyTime t = new PrettyTime(ref);
        Assert.assertEquals("p?ed 1 m?s?cem", t.format(then));
    }

    @Test
    public void testRightNow() throws Exception {
        PrettyTime t = new PrettyTime();
        Assert.assertEquals("za chv?li", t.format(new Date()));
    }

    @Test
    public void testRightNowVariance() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("za chv?li", t.format(new Date(600)));
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        for (TimeUnit u : t.getUnits()) {
            if (u instanceof JustNow) {
                setMaxQuantity(1000L);
            }
        }
        Assert.assertEquals("za 1 minutu", t.format(new Date(((1000 * 60) * 1))));
        Assert.assertEquals("za 3 minuty", t.format(new Date(((1000 * 60) * 3))));
        Assert.assertEquals("za 12 minut", t.format(new Date(((1000 * 60) * 12))));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("za 1 hodinu", t.format(new Date((((1000 * 60) * 60) * 1))));
        Assert.assertEquals("za 3 hodiny", t.format(new Date((((1000 * 60) * 60) * 3))));
        Assert.assertEquals("za 10 hodin", t.format(new Date((((1000 * 60) * 60) * 10))));
    }

    @Test
    public void testDaysFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("za 1 den", t.format(new Date(((((1000 * 60) * 60) * 24) * 1))));
        Assert.assertEquals("za 3 dny", t.format(new Date(((((1000 * 60) * 60) * 24) * 3))));
        Assert.assertEquals("za 5 dn?", t.format(new Date(((((1000 * 60) * 60) * 24) * 5))));
    }

    @Test
    public void testWeeksFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        for (TimeUnit u : t.getUnits()) {
            if (u instanceof Month) {
                t.removeUnit(u);
            }
        }
        Assert.assertEquals("za 1 t?den", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 1L))));
        Assert.assertEquals("za 3 t?dny", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 3L))));
        Assert.assertEquals("za 5 t?dn?", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 5L))));
    }

    @Test
    public void testMonthsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("za 1 m?s?c", t.format(new Date((2629743830L * 1L))));
        Assert.assertEquals("za 3 m?s?ce", t.format(new Date((2629743830L * 3L))));
        Assert.assertEquals("za 6 m?s?c?", t.format(new Date((2629743830L * 6L))));
    }

    @Test
    public void testYearsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("za 1 rok", t.format(new Date(((2629743830L * 12L) * 1L))));
        Assert.assertEquals("za 3 roky", t.format(new Date(((2629743830L * 12L) * 3L))));
        Assert.assertEquals("za 9 let", t.format(new Date(((2629743830L * 12L) * 9L))));
    }

    @Test
    public void testDecadesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("za 3 desetilet?", t.format(new Date((315569259747L * 3L))));
    }

    @Test
    public void testCenturiesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        Assert.assertEquals("za 3 stolet?", t.format(new Date((3155692597470L * 3L))));
    }

    /* Past */
    @Test
    public void testMomentsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(6000));
        Assert.assertEquals("p?ed chv?l?", t.format(new Date(0)));
    }

    @Test
    public void testMinutesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((1000 * 60) * 12)));
        Assert.assertEquals("p?ed 12 minutami", t.format(new Date(0)));
    }

    @Test
    public void testHoursAgo() throws Exception {
        Date base = new Date();
        PrettyTime t = new PrettyTime(base);
        Assert.assertEquals("p?ed 1 hodinou", t.format(addTime(base, (-1), Calendar.HOUR_OF_DAY)));
        Assert.assertEquals("p?ed 3 hodinami", t.format(addTime(base, (-3), Calendar.HOUR_OF_DAY)));
    }

    @Test
    public void testDaysAgo() throws Exception {
        Date base = new Date();
        PrettyTime t = new PrettyTime(base);
        Assert.assertEquals("p?ed 1 dnem", t.format(addTime(base, (-1), Calendar.DAY_OF_MONTH)));
        Assert.assertEquals("p?ed 3 dny", t.format(addTime(base, (-3), Calendar.DAY_OF_MONTH)));
    }

    @Test
    public void testWeeksAgo() throws Exception {
        Date base = new Date();
        PrettyTime t = new PrettyTime(base);
        Assert.assertEquals("p?ed 1 t?dnem", t.format(addTime(base, (-1), Calendar.WEEK_OF_MONTH)));
        Assert.assertEquals("p?ed 3 t?dny", t.format(addTime(base, (-3), Calendar.WEEK_OF_MONTH)));
    }

    @Test
    public void testMonthsAgo() throws Exception {
        Date base = new Date();
        PrettyTime t = new PrettyTime(base);
        Assert.assertEquals("p?ed 1 m?s?cem", t.format(addTime(base, (-1), Calendar.MONTH)));
        Assert.assertEquals("p?ed 3 m?s?ci", t.format(addTime(base, (-3), Calendar.MONTH)));
    }

    @Test
    public void testYearsAgo() throws Exception {
        Date base = new Date();
        PrettyTime t = new PrettyTime(base);
        for (TimeUnit u : t.getUnits()) {
            if (u instanceof Month) {
                t.removeUnit(u);
            }
        }
        Assert.assertEquals("p?ed 1 rokem", t.format(addTime(base, (-1), Calendar.YEAR)));
        Assert.assertEquals("p?ed 3 roky", t.format(addTime(base, (-3), Calendar.YEAR)));
    }

    @Test
    public void testDecadesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 3L)));
        Assert.assertEquals("p?ed 3 desetilet?mi", t.format(new Date(0)));
    }

    @Test
    public void testCenturiesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((3155692597470L * 3L)));
        Assert.assertEquals("p?ed 3 stolet?mi", t.format(new Date(0)));
    }

    @Test
    public void testFormattingDurationListInThePast() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((((1000 * 60) * 60) * 24) * 3) + (((1000 * 60) * 60) * 15)) + ((1000 * 60) * 38))));
        List<Duration> durations = t.calculatePreciseDuration(new Date(0));
        Assert.assertEquals("p?ed 3 dny 15 hodinami 38 minutami", t.format(durations));
    }

    @Test
    public void testFormattingDurationListInTheFuture() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        List<Duration> durations = t.calculatePreciseDuration(new Date(((((((1000 * 60) * 60) * 24) * 3) + (((1000 * 60) * 60) * 15)) + ((1000 * 60) * 38))));
        Assert.assertEquals("za 3 dny 15 hodin 38 minut", t.format(durations));
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
        Assert.assertEquals("10 minutami", result);
    }
}

