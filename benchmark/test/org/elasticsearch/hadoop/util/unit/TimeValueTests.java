/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.util.unit;


import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class TimeValueTests {
    @Test
    public void testSimple() {
        MatcherAssert.assertThat(TimeUnit.MILLISECONDS.toMillis(10), equalTo(new TimeValue(10, TimeUnit.MILLISECONDS).millis()));
        MatcherAssert.assertThat(TimeUnit.MICROSECONDS.toMicros(10), equalTo(new TimeValue(10, TimeUnit.MICROSECONDS).micros()));
        MatcherAssert.assertThat(TimeUnit.SECONDS.toSeconds(10), equalTo(new TimeValue(10, TimeUnit.SECONDS).seconds()));
        MatcherAssert.assertThat(TimeUnit.MINUTES.toMinutes(10), equalTo(new TimeValue(10, TimeUnit.MINUTES).minutes()));
        MatcherAssert.assertThat(TimeUnit.HOURS.toHours(10), equalTo(new TimeValue(10, TimeUnit.HOURS).hours()));
        MatcherAssert.assertThat(TimeUnit.DAYS.toDays(10), equalTo(new TimeValue(10, TimeUnit.DAYS).days()));
    }

    @Test
    public void testToString() {
        MatcherAssert.assertThat("10ms", equalTo(new TimeValue(10, TimeUnit.MILLISECONDS).toString()));
        MatcherAssert.assertThat("1.5s", equalTo(new TimeValue(1533, TimeUnit.MILLISECONDS).toString()));
        MatcherAssert.assertThat("1.5m", equalTo(new TimeValue(90, TimeUnit.SECONDS).toString()));
        MatcherAssert.assertThat("1.5h", equalTo(new TimeValue(90, TimeUnit.MINUTES).toString()));
        MatcherAssert.assertThat("1.5d", equalTo(new TimeValue(36, TimeUnit.HOURS).toString()));
        MatcherAssert.assertThat("1000d", equalTo(new TimeValue(1000, TimeUnit.DAYS).toString()));
    }

    @Test
    public void testMinusOne() {
        MatcherAssert.assertThat(new TimeValue((-1)).nanos(), lessThan(0L));
    }
}

