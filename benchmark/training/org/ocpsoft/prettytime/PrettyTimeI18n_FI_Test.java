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


import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class PrettyTimeI18n_FI_Test {
    private Locale locale;

    @Test
    public void testRightNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("hetken p??st?", t.format(new Date(6000)));
    }

    @Test
    public void testMomentsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(6000), locale);
        Assert.assertEquals("hetki sitten", t.format(new Date(0)));
    }

    @Test
    public void testMilliSecondsFromNow() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow(new Date(0), locale);
        Assert.assertEquals("13 millisekunnin p??st?", t.format(new Date(13)));
    }

    @Test
    public void testMilliSecondsAgo() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow(new Date(13), locale);
        Assert.assertEquals("13 millisekuntia sitten", t.format(new Date(0)));
    }

    @Test
    public void testMilliSecondFromNow() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow(new Date(0), locale);
        Assert.assertEquals("millisekunnin p??st?", t.format(new Date(1)));
    }

    @Test
    public void testMilliSecondAgo() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow(new Date(1), locale);
        Assert.assertEquals("millisekunti sitten", t.format(new Date(0)));
    }

    @Test
    public void testSecondsFromNow() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow(new Date(0), locale);
        Assert.assertEquals("13 sekunnin p??st?", t.format(new Date((1000 * 13))));
    }

    @Test
    public void testSecondsAgo() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow(new Date((1000 * 13)), locale);
        Assert.assertEquals("13 sekuntia sitten", t.format(new Date(0)));
    }

    @Test
    public void testSecondFromNow() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow(new Date(0), locale);
        Assert.assertEquals("sekunnin p??st?", t.format(new Date((1000 * 1))));
    }

    @Test
    public void testSecondAgo() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow(new Date((1000 * 1)), locale);
        Assert.assertEquals("sekunti sitten", t.format(new Date(0)));
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("13 minuutin p??st?", t.format(new Date(((1000 * 60) * 13))));
    }

    @Test
    public void testMinutesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((1000 * 60) * 13)), locale);
        Assert.assertEquals("13 minuuttia sitten", t.format(new Date(0)));
    }

    @Test
    public void testMinuteFromNow() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow(new Date(0), locale);
        Assert.assertEquals("minuutin p??st?", t.format(new Date(((1000 * 60) * 1))));
    }

    @Test
    public void testMinuteAgo() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow(new Date(((1000 * 60) * 1)), locale);
        Assert.assertEquals("minuutti sitten", t.format(new Date(0)));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 tunnin p??st?", t.format(new Date((((1000 * 60) * 60) * 3))));
    }

    @Test
    public void testHoursAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 3)), locale);
        Assert.assertEquals("3 tuntia sitten", t.format(new Date(0)));
    }

    @Test
    public void testHoursFromNowSingle() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("tunnin p??st?", t.format(new Date((((1000 * 60) * 60) * 1))));
    }

    @Test
    public void testHoursAgoSingle() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 1)), locale);
        Assert.assertEquals("tunti sitten", t.format(new Date(0)));
    }

    @Test
    public void testDaysFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 p?iv?n p??st?", t.format(new Date(((((1000 * 60) * 60) * 24) * 3))));
    }

    @Test
    public void testNear2DaysAgo() throws Exception {
        int hour = (1000 * 60) * 60;
        PrettyTime t = new PrettyTime(new Date((((hour * 24) * 2) - (11 * hour))), locale);
        Assert.assertEquals("2 p?iv?? sitten", t.format(new Date(0)));
        t = new PrettyTime(new Date((((hour * 24) * 2) - (13 * hour))), locale);
        Assert.assertEquals("eilen", t.format(new Date(0)));
    }

    @Test
    public void test3DaysAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((1000 * 60) * 60) * 24) * 3)), locale);
        Assert.assertEquals("3 p?iv?? sitten", t.format(new Date(0)));
    }

    @Test
    public void testDaysFromNowSingle() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("huomenna", t.format(new Date(((((1000 * 60) * 60) * 24) * 1))));
    }

    @Test
    public void testDaysAgoSingle() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((1000 * 60) * 60) * 24) * 1)), locale);
        Assert.assertEquals("eilen", t.format(new Date(0)));
    }

    @Test
    public void testWeeksFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 viikon p??st?", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 3))));
    }

    @Test
    public void testWeeksAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000 * 60) * 60) * 24) * 7) * 3)), locale);
        Assert.assertEquals("3 viikkoa sitten", t.format(new Date(0)));
    }

    @Test
    public void testWeeksFromNowSingle() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("viikon p??st?", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 1))));
    }

    @Test
    public void testWeeksAgoSingle() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000 * 60) * 60) * 24) * 7) * 1)), locale);
        Assert.assertEquals("viikko sitten", t.format(new Date(0)));
    }

    @Test
    public void testMonthsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 kuukauden p??st?", t.format(new Date((((((1000L * 60) * 60) * 24) * 30) * 3))));
    }

    @Test
    public void testMonthsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000L * 60) * 60) * 24) * 30) * 3)), locale);
        Assert.assertEquals("3 kuukautta sitten", t.format(new Date(0)));
    }

    @Test
    public void testMonthFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("kuukauden p??st?", t.format(new Date((((((1000L * 60) * 60) * 24) * 30) * 1))));
    }

    @Test
    public void testMonthAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000L * 60) * 60) * 24) * 30) * 1)), locale);
        Assert.assertEquals("kuukausi sitten", t.format(new Date(0)));
    }

    @Test
    public void testYearsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 vuoden p??st?", t.format(new Date((((((1000L * 60) * 60) * 24) * 365) * 3))));
    }

    @Test
    public void testYearsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000L * 60) * 60) * 24) * 365) * 3)), locale);
        Assert.assertEquals("3 vuotta sitten", t.format(new Date(0)));
    }

    @Test
    public void testYearFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("vuoden p??st?", t.format(new Date((((((1000L * 60) * 60) * 24) * 366) * 1))));
    }

    @Test
    public void testYearAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000L * 60) * 60) * 24) * 366) * 1)), locale);
        Assert.assertEquals("vuosi sitten", t.format(new Date(0)));
    }

    @Test
    public void testDecadesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 vuosikymmenen p??st?", t.format(new Date(((((((1000L * 60) * 60) * 24) * 365) * 10) * 3))));
    }

    @Test
    public void testDecadesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((((1000L * 60) * 60) * 24) * 365) * 10) * 3)), locale);
        Assert.assertEquals("3 vuosikymment? sitten", t.format(new Date(0)));
    }

    @Test
    public void testDecadeFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("vuosikymmenen p??st?", t.format(new Date(((((((1000L * 60) * 60) * 24) * 365) * 11) * 1))));
    }

    @Test
    public void testDecadeAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000L * 60) * 60) * 24) * 365) * 11)), locale);
        Assert.assertEquals("vuosikymmen sitten", t.format(new Date(0)));
    }

    @Test
    public void testCenturiesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 vuosisadan p??st?", t.format(new Date(((((((1000L * 60) * 60) * 24) * 365) * 100) * 3))));
    }

    @Test
    public void testCenturiesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((((1000L * 60) * 60) * 24) * 365) * 100) * 3)), locale);
        Assert.assertEquals("3 vuosisataa sitten", t.format(new Date(0)));
    }

    @Test
    public void testCenturyFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("vuosisadan p??st?", t.format(new Date((((((1000L * 60) * 60) * 24) * 365) * 101))));
    }

    @Test
    public void testCenturyAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000L * 60) * 60) * 24) * 365) * 101)), locale);
        Assert.assertEquals("vuosisata sitten", t.format(new Date(0)));
    }

    @Test
    public void testMillenniaFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 vuosituhannen p??st?", t.format(new Date(((((((1000L * 60) * 60) * 24) * 365) * 1000) * 3))));
    }

    @Test
    public void testMillenniaAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((((1000L * 60) * 60) * 24) * 365) * 1000) * 3)), locale);
        Assert.assertEquals("3 vuosituhatta sitten", t.format(new Date(0)));
    }

    @Test
    public void testMillenniumFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("vuosituhannen p??st?", t.format(new Date((((((1000L * 60) * 60) * 24) * 365) * 1001))));
    }

    @Test
    public void testMillenniumAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000L * 60) * 60) * 24) * 365) * 1001)), locale);
        Assert.assertEquals("vuosituhat sitten", t.format(new Date(0)));
    }

    @Test
    public void testFormattingDurationListInThePast() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((((1000 * 60) * 60) * 24) * 3) + (((1000 * 60) * 60) * 15)) + ((1000 * 60) * 38))), locale);
        List<Duration> durations = t.calculatePreciseDuration(new Date(0));
        Assert.assertEquals("3 p?iv?? 15 tuntia 38 minuuttia sitten", t.format(durations));
    }

    @Test
    public void testFormattingDurationListInTheFuture() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        List<Duration> durations = t.calculatePreciseDuration(new Date(((((((1000 * 60) * 60) * 24) * 3) + (((1000 * 60) * 60) * 15)) + ((1000 * 60) * 38))));
        Assert.assertEquals("3 p?iv?n 15 tunnin 38 minuutin p??st?", t.format(durations));
    }
}

