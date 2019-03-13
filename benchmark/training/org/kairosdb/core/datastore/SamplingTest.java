/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kairosdb.core.datastore;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.kairosdb.core.aggregator.Sampling;
import org.kairosdb.util.Util;

import static TimeUnit.DAYS;
import static TimeUnit.HOURS;
import static TimeUnit.MILLISECONDS;
import static TimeUnit.MINUTES;
import static TimeUnit.MONTHS;
import static TimeUnit.SECONDS;
import static TimeUnit.WEEKS;
import static TimeUnit.YEARS;


public class SamplingTest {
    @Test
    public void test_getUnitDuration_year_no_leap() throws Exception {
        Sampling sampling = new Sampling(1, YEARS);
        DateTimeZone timezone = DateTimeZone.forID("Europe/Brussels");
        DateTime dt = new DateTime(2014, 12, 18, 1, 2, 3, 4, timezone);
        MatcherAssert.assertThat(Util.getSamplingDuration(dt.getMillis(), sampling, timezone), Is.is(((((365 * 24) * 60) * 60) * 1000L)));
    }

    @Test
    public void test_getUnitDuration_year_over_leap() throws Exception {
        Sampling sampling = new Sampling(1, YEARS);
        DateTimeZone timezone = DateTimeZone.forID("Europe/Brussels");
        DateTime dt = new DateTime(2012, 2, 18, 1, 2, 3, 4, timezone);
        MatcherAssert.assertThat(Util.getSamplingDuration(dt.getMillis(), sampling, timezone), Is.is(((((366 * 24) * 60) * 60) * 1000L)));
    }

    @Test
    public void test_getUnitDuration_month_january() throws Exception {
        Sampling sampling = new Sampling(1, MONTHS);
        DateTimeZone timezone = DateTimeZone.forID("Europe/Brussels");
        DateTime dt = new DateTime(2014, 1, 18, 1, 2, 3, 4, timezone);
        MatcherAssert.assertThat(Util.getSamplingDuration(dt.getMillis(), sampling, timezone), Is.is(((((31 * 24) * 60) * 60) * 1000L)));
    }

    @Test
    public void test_getUnitDuration_month_february() throws Exception {
        Sampling sampling = new Sampling(1, MONTHS);
        DateTimeZone timezone = DateTimeZone.forID("Europe/Brussels");
        DateTime dt = new DateTime(2014, 2, 18, 1, 2, 3, 4, timezone);
        MatcherAssert.assertThat(Util.getSamplingDuration(dt.getMillis(), sampling, timezone), Is.is(((((28 * 24) * 60) * 60) * 1000L)));
    }

    @Test
    public void test_getUnitDuration_week() throws Exception {
        Sampling sampling = new Sampling(1, WEEKS);
        DateTimeZone timezone = DateTimeZone.forID("Europe/Brussels");
        DateTime dt = new DateTime(2014, 12, 18, 1, 2, 3, 4, timezone);
        MatcherAssert.assertThat(Util.getSamplingDuration(dt.getMillis(), sampling, timezone), Is.is(((((7 * 24) * 60) * 60) * 1000L)));
    }

    @Test
    public void test_getUnitDuration_day() throws Exception {
        Sampling sampling = new Sampling(1, DAYS);
        DateTimeZone timezone = DateTimeZone.forID("Europe/Brussels");
        DateTime dt = new DateTime(2014, 12, 18, 1, 2, 3, 4, timezone);
        MatcherAssert.assertThat(Util.getSamplingDuration(dt.getMillis(), sampling, timezone), Is.is((((24 * 60) * 60) * 1000L)));
    }

    @Test
    public void test_getUnitDuration_hour() throws Exception {
        Sampling sampling = new Sampling(1, HOURS);
        DateTimeZone timezone = DateTimeZone.forID("Europe/Brussels");
        DateTime dt = new DateTime(2014, 12, 18, 1, 2, 3, 4, timezone);
        MatcherAssert.assertThat(Util.getSamplingDuration(dt.getMillis(), sampling, timezone), Is.is(((60 * 60) * 1000L)));
    }

    @Test
    public void test_getUnitDuration_minute() throws Exception {
        Sampling sampling = new Sampling(1, MINUTES);
        DateTimeZone timezone = DateTimeZone.forID("Europe/Brussels");
        DateTime dt = new DateTime(2014, 12, 18, 1, 2, 3, 4, timezone);
        MatcherAssert.assertThat(Util.getSamplingDuration(dt.getMillis(), sampling, timezone), Is.is((1000 * 60L)));
    }

    @Test
    public void test_getUnitDuration_seconds() throws Exception {
        Sampling sampling = new Sampling(1, SECONDS);
        DateTimeZone timezone = DateTimeZone.forID("Europe/Brussels");
        DateTime dt = new DateTime(2014, 12, 18, 1, 2, 3, 4, timezone);
        MatcherAssert.assertThat(Util.getSamplingDuration(dt.getMillis(), sampling, timezone), Is.is(1000L));
    }

    @Test
    public void test_getUnitDuration_milliseconds() throws Exception {
        Sampling sampling = new Sampling(1, MILLISECONDS);
        DateTimeZone timezone = DateTimeZone.forID("Europe/Brussels");
        DateTime dt = new DateTime(2014, 12, 18, 1, 2, 3, 4, timezone);
        MatcherAssert.assertThat(Util.getSamplingDuration(dt.getMillis(), sampling, timezone), Is.is(1L));
    }
}

