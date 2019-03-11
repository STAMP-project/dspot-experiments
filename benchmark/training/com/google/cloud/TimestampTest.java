/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import Timestamp.MAX_VALUE;
import Timestamp.MIN_VALUE;
import com.google.common.testing.EqualsTester;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link com.google.cloud.Timestamp}.
 */
@RunWith(JUnit4.class)
public class TimestampTest {
    private static final String TEST_TIME_ISO = "2015-10-12T15:14:54Z";

    private static final long TEST_TIME_SECONDS = 1444662894L;

    private static final long TEST_TIME_MICROSECONDS = 10000100L;

    private static final long TEST_TIME_MILLISECONDS = (TimeUnit.SECONDS.toMillis(1444662894L)) + (TimeUnit.MICROSECONDS.toMillis(1234));

    private static final long TEST_TIME_MILLISECONDS_NEGATIVE = -1000L;

    private static final Date TEST_DATE = new Date(TimestampTest.TEST_TIME_MILLISECONDS);

    private static final Date TEST_DATE_PRE_EPOCH = new Date(TimestampTest.TEST_TIME_MILLISECONDS_NEGATIVE);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void minValue() {
        // MIN_VALUE is before the start of the Gregorian calendar... use magic value.
        assertThat(MIN_VALUE.getSeconds()).isEqualTo((-62135596800L));
        assertThat(MIN_VALUE.getNanos()).isEqualTo(0);
    }

    @Test
    public void maxValue() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        GregorianCalendar calendar = new GregorianCalendar(tz);
        calendar.set(9999, Calendar.DECEMBER, 31, 23, 59, 59);
        Timestamp expectedMin = new Timestamp(calendar.getTimeInMillis());
        expectedMin.setNanos(999999999);
        assertThat(MAX_VALUE.getSeconds()).isEqualTo(((calendar.getTimeInMillis()) / 1000L));
        assertThat(MAX_VALUE.getNanos()).isEqualTo(999999999);
    }

    @Test
    public void ofMicroseconds() {
        Timestamp timestamp = Timestamp.ofTimeMicroseconds(TimestampTest.TEST_TIME_MICROSECONDS);
        assertThat(timestamp.getSeconds()).isEqualTo(((TimestampTest.TEST_TIME_MICROSECONDS) / 1000000L));
        assertThat(timestamp.getNanos()).isEqualTo((((TimestampTest.TEST_TIME_MICROSECONDS) % 1000000L) * 1000));
    }

    @Test
    public void ofDate() {
        Timestamp timestamp = Timestamp.of(TimestampTest.TEST_DATE);
        Long expectedSeconds = TimeUnit.MILLISECONDS.toSeconds(TimestampTest.TEST_TIME_MILLISECONDS);
        Long expectedNanos = (TimeUnit.MILLISECONDS.toNanos(TimestampTest.TEST_TIME_MILLISECONDS)) - (TimeUnit.SECONDS.toNanos(expectedSeconds));
        assertThat(timestamp.getSeconds()).isEqualTo(expectedSeconds);
        assertThat(timestamp.getNanos()).isEqualTo(expectedNanos);
    }

    @Test
    public void ofDatePreEpoch() {
        Timestamp timestamp = Timestamp.of(TimestampTest.TEST_DATE_PRE_EPOCH);
        long expectedSeconds = (TimestampTest.TEST_TIME_MILLISECONDS_NEGATIVE) / 1000;
        int expectedNanos = ((int) (((TimestampTest.TEST_TIME_MILLISECONDS_NEGATIVE) % 1000) * 1000000));
        if (expectedNanos < 0) {
            expectedSeconds--;
            expectedNanos += 1000000000;
        }
        assertThat(timestamp.getSeconds()).isEqualTo(expectedSeconds);
        assertThat(timestamp.getNanos()).isEqualTo(expectedNanos);
    }

    @Test
    public void toDate() {
        Timestamp timestamp = Timestamp.ofTimeSecondsAndNanos(TimestampTest.TEST_TIME_SECONDS, (1234 * 1000));
        Date date = timestamp.toDate();
        assertThat(TimestampTest.TEST_TIME_MILLISECONDS).isEqualTo(date.getTime());
    }

    @Test
    public void toFromSqlTimestamp() {
        long seconds = TimestampTest.TEST_TIME_SECONDS;
        int nanos = 500000000;
        Timestamp sqlTs = new Timestamp((seconds * 1000));
        sqlTs.setNanos(nanos);
        Timestamp ts = Timestamp.of(sqlTs);
        assertThat(ts.getSeconds()).isEqualTo(seconds);
        assertThat(ts.getNanos()).isEqualTo(nanos);
        assertThat(ts.toSqlTimestamp()).isEqualTo(sqlTs);
    }

    @Test
    public void boundsSecondsMin() {
        expectedException.expect(IllegalArgumentException.class);
        Timestamp.ofTimeSecondsAndNanos(((MIN_VALUE.getSeconds()) - 1), 999999999);
    }

    @Test
    public void boundsSecondsMax() {
        expectedException.expect(IllegalArgumentException.class);
        Timestamp.ofTimeSecondsAndNanos(((MAX_VALUE.getSeconds()) + 1), 0);
    }

    @Test
    public void boundsNanosMin() {
        expectedException.expect(IllegalArgumentException.class);
        Timestamp.ofTimeSecondsAndNanos(TimestampTest.TEST_TIME_SECONDS, (-1));
    }

    @Test
    public void boundsNanosMax() {
        expectedException.expect(IllegalArgumentException.class);
        Timestamp.ofTimeSecondsAndNanos(TimestampTest.TEST_TIME_SECONDS, 1000000000);
    }

    @Test
    public void boundsSqlTimestampMin() {
        expectedException.expect(IllegalArgumentException.class);
        Timestamp.of(new Timestamp((((MIN_VALUE.getSeconds()) - 1) * 1000)));
    }

    @Test
    public void boundsSqlTimestampMax() {
        expectedException.expect(IllegalArgumentException.class);
        Timestamp.of(new Timestamp((((MAX_VALUE.getSeconds()) + 1) * 1000)));
    }

    @Test
    public void equalsAndHashCode() {
        EqualsTester tester = new EqualsTester();
        tester.addEqualityGroup(Timestamp.ofTimeSecondsAndNanos(TimestampTest.TEST_TIME_SECONDS, 0), Timestamp.ofTimeSecondsAndNanos(TimestampTest.TEST_TIME_SECONDS, 0), Timestamp.of(new Timestamp(((TimestampTest.TEST_TIME_SECONDS) * 1000))));
        tester.addEqualityGroup(Timestamp.ofTimeSecondsAndNanos(((TimestampTest.TEST_TIME_SECONDS) + 1), 0));
        tester.addEqualityGroup(Timestamp.ofTimeSecondsAndNanos(TimestampTest.TEST_TIME_SECONDS, 1));
        tester.testEquals();
    }

    @Test
    public void testToString() {
        assertThat(MIN_VALUE.toString()).isEqualTo("0001-01-01T00:00:00Z");
        assertThat(MAX_VALUE.toString()).isEqualTo("9999-12-31T23:59:59.999999999Z");
        assertThat(Timestamp.ofTimeSecondsAndNanos(0, 0).toString()).isEqualTo("1970-01-01T00:00:00Z");
        assertThat(Timestamp.ofTimeSecondsAndNanos(0, 100).toString()).isEqualTo("1970-01-01T00:00:00.000000100Z");
        assertThat(Timestamp.ofTimeSecondsAndNanos(TimestampTest.TEST_TIME_SECONDS, 0).toString()).isEqualTo(TimestampTest.TEST_TIME_ISO);
    }

    @Test
    public void parseTimestamp() {
        assertThat(Timestamp.parseTimestamp("0001-01-01T00:00:00Z")).isEqualTo(MIN_VALUE);
        assertThat(Timestamp.parseTimestamp("9999-12-31T23:59:59.999999999Z")).isEqualTo(MAX_VALUE);
        assertThat(Timestamp.parseTimestamp(TimestampTest.TEST_TIME_ISO)).isEqualTo(Timestamp.ofTimeSecondsAndNanos(TimestampTest.TEST_TIME_SECONDS, 0));
    }

    @Test
    public void fromProto() {
        com.google.protobuf.Timestamp proto = com.google.protobuf.Timestamp.newBuilder().setSeconds(1234).setNanos(567).build();
        Timestamp timestamp = Timestamp.fromProto(proto);
        assertThat(timestamp.getSeconds()).isEqualTo(1234);
        assertThat(timestamp.getNanos()).isEqualTo(567);
    }

    @Test
    public void comparable() {
        assertThat(MIN_VALUE).isLessThan(MAX_VALUE);
        assertThat(MAX_VALUE).isGreaterThan(MIN_VALUE);
        assertThat(Timestamp.ofTimeSecondsAndNanos(100, 0)).isAtLeast(Timestamp.ofTimeSecondsAndNanos(100, 0));
        assertThat(Timestamp.ofTimeSecondsAndNanos(100, 0)).isAtMost(Timestamp.ofTimeSecondsAndNanos(100, 0));
        assertThat(Timestamp.ofTimeSecondsAndNanos(100, 1000)).isLessThan(Timestamp.ofTimeSecondsAndNanos(101, 0));
        assertThat(Timestamp.ofTimeSecondsAndNanos(100, 1000)).isAtMost(Timestamp.ofTimeSecondsAndNanos(101, 0));
        assertThat(Timestamp.ofTimeSecondsAndNanos(101, 0)).isGreaterThan(Timestamp.ofTimeSecondsAndNanos(100, 1000));
        assertThat(Timestamp.ofTimeSecondsAndNanos(101, 0)).isAtLeast(Timestamp.ofTimeSecondsAndNanos(100, 1000));
    }

    @Test
    public void serialization() throws Exception {
        reserializeAndAssert(Timestamp.parseTimestamp("9999-12-31T23:59:59.999999999Z"));
    }
}

