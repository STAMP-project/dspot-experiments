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
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Bram
 */
public class PrettyTimeI18n_DA_Test {
    private Locale locale;

    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    @Test
    public void testPrettyTime() {
        PrettyTime p = new PrettyTime(locale);
        Assert.assertEquals("straks", p.format(new Date()));
    }

    @Test
    public void testPrettyTimeCenturies() {
        PrettyTime p = new PrettyTime(new Date((3155692597470L * 3L)), locale);
        Assert.assertEquals("3 ?rhundreder siden", p.format(new Date(0)));
        p = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 ?rhundreder fra nu", p.format(new Date((3155692597470L * 3L))));
    }

    @Test
    public void testCeilingInterval() throws Exception {
        Date then = format.parse("20/5/2009");
        Date ref = format.parse("17/6/2009");
        PrettyTime t = new PrettyTime(ref, locale);
        Assert.assertEquals("1 m?ned siden", t.format(then));
    }

    @Test
    public void testNullDate() throws Exception {
        PrettyTime t = new PrettyTime(locale);
        Date date = null;
        Assert.assertEquals("straks", t.format(date));
    }

    @Test
    public void testRightNow() throws Exception {
        PrettyTime t = new PrettyTime(locale);
        Assert.assertEquals("straks", t.format(new Date()));
    }

    @Test
    public void testRightNowVariance() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("straks", t.format(new Date(600)));
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("om 12 minutter", t.format(new Date(((1000 * 60) * 12))));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("om 3 timer", t.format(new Date((((1000 * 60) * 60) * 3))));
    }

    @Test
    public void testDaysFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("om 3 dage", t.format(new Date(((((1000 * 60) * 60) * 24) * 3))));
    }

    @Test
    public void testWeeksFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("om 3 uger", t.format(new Date((((((1000 * 60) * 60) * 24) * 7) * 3))));
    }

    @Test
    public void testMonthsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("om 3 m?neder", t.format(new Date((2629743830L * 3L))));
    }

    @Test
    public void testYearsFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("om 3 ?r", t.format(new Date(((2629743830L * 12L) * 3L))));
    }

    @Test
    public void testDecadesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 ?rtier fra nu", t.format(new Date((315569259747L * 3L))));
    }

    @Test
    public void testCenturiesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0), locale);
        Assert.assertEquals("3 ?rhundreder fra nu", t.format(new Date((3155692597470L * 3L))));
    }

    /* Past */
    @Test
    public void testMomentsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(6000), locale);
        Assert.assertEquals("et ?jeblik siden", t.format(new Date(0)));
    }

    @Test
    public void testMinutesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((1000 * 60) * 12)), locale);
        Assert.assertEquals("12 minutter siden", t.format(new Date(0)));
    }

    @Test
    public void testHoursAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((1000 * 60) * 60) * 3)), locale);
        Assert.assertEquals("3 timer siden", t.format(new Date(0)));
    }

    @Test
    public void testDaysAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((((1000 * 60) * 60) * 24) * 3)), locale);
        Assert.assertEquals("3 dage siden", t.format(new Date(0)));
    }

    @Test
    public void testWeeksAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((((((1000 * 60) * 60) * 24) * 7) * 3)), locale);
        Assert.assertEquals("3 uger siden", t.format(new Date(0)));
    }

    @Test
    public void testMonthsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((2629743830L * 3L)), locale);
        Assert.assertEquals("3 m?neder siden", t.format(new Date(0)));
    }

    @Test
    public void testYearsAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date(((2629743830L * 12L) * 3L)), locale);
        Assert.assertEquals("3 ?r siden", t.format(new Date(0)));
    }

    @Test
    public void testDecadesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((315569259747L * 3L)), locale);
        Assert.assertEquals("3 ?rtier siden", t.format(new Date(0)));
    }

    @Test
    public void testCenturiesAgo() throws Exception {
        PrettyTime t = new PrettyTime(new Date((3155692597470L * 3L)), locale);
        Assert.assertEquals("3 ?rhundreder siden", t.format(new Date(0)));
    }
}

