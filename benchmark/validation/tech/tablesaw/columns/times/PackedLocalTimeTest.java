/**
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
package tech.tablesaw.columns.times;


import com.google.common.collect.ImmutableList;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;


/**
 * Tests for PackedLocalTime
 */
public class PackedLocalTimeTest {
    @Test
    public void testTruncatedTo() {
        List<LocalTime> times = ImmutableList.of(LocalTime.of(5, 11, 24), LocalTime.of(21, 11, 24), LocalTime.MIDNIGHT, LocalTime.NOON, LocalTime.MIN, LocalTime.MAX);
        for (LocalTime time : times) {
            Assertions.assertEquals(time.truncatedTo(ChronoUnit.SECONDS), PackedLocalTime.asLocalTime(PackedLocalTime.truncatedTo(ChronoUnit.SECONDS, PackedLocalTime.pack(time))));
            Assertions.assertEquals(time.truncatedTo(ChronoUnit.MINUTES), PackedLocalTime.asLocalTime(PackedLocalTime.truncatedTo(ChronoUnit.MINUTES, PackedLocalTime.pack(time))));
            Assertions.assertEquals(time.truncatedTo(ChronoUnit.HOURS), PackedLocalTime.asLocalTime(PackedLocalTime.truncatedTo(ChronoUnit.HOURS, PackedLocalTime.pack(time))));
            Assertions.assertEquals(time.truncatedTo(ChronoUnit.HALF_DAYS), PackedLocalTime.asLocalTime(PackedLocalTime.truncatedTo(ChronoUnit.HALF_DAYS, PackedLocalTime.pack(time))));
            Assertions.assertEquals(time.truncatedTo(ChronoUnit.DAYS), PackedLocalTime.asLocalTime(PackedLocalTime.truncatedTo(ChronoUnit.DAYS, PackedLocalTime.pack(time))));
        }
    }

    @Test
    public void testGetHour() {
        LocalTime now = LocalTime.now();
        Assertions.assertEquals(now.getHour(), PackedLocalTime.getHour(PackedLocalTime.pack(now)));
    }

    @Test
    public void testGetMinute() {
        LocalTime now = LocalTime.now();
        Assertions.assertEquals(now.getMinute(), PackedLocalTime.getMinute(PackedLocalTime.pack(now)));
    }

    @Test
    public void testGetSecond() {
        LocalTime now = LocalTime.now();
        Assertions.assertEquals(now.getSecond(), PackedLocalTime.getSecond(PackedLocalTime.pack(now)));
    }

    @Test
    public void testGetSecondOfDay() {
        LocalTime now = LocalTime.now();
        Assertions.assertEquals(now.get(ChronoField.SECOND_OF_DAY), PackedLocalTime.getSecondOfDay(PackedLocalTime.pack(now)), 1.0E-4);
    }

    @Test
    public void testGetMinuteOfDay() {
        LocalTime now = LocalTime.now();
        Assertions.assertEquals(now.get(ChronoField.MINUTE_OF_DAY), PackedLocalTime.getMinuteOfDay(PackedLocalTime.pack(now)), 1.0E-4);
    }

    @Test
    public void testToNanoOfDay() {
        int pTime = PackedLocalTime.of(7, 18, 32, 232);
        LocalTime time = PackedLocalTime.asLocalTime(pTime);
        Assertions.assertEquals(time.getLong(ChronoField.NANO_OF_DAY), PackedLocalTime.toNanoOfDay(pTime));
    }

    @Test
    public void testGetMillisecondOfDay() {
        LocalTime now = LocalTime.now();
        Assertions.assertEquals(now.get(ChronoField.MILLI_OF_DAY), PackedLocalTime.getMillisecondOfDay(PackedLocalTime.pack(now)));
    }

    @Test
    public void testConstructors1() {
        LocalTime localTime = LocalTime.of(5, 11, 36);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.of(5, 11, 36);
        Assertions.assertEquals(PackedLocalTime.getMillisecondOfDay(packedTime), PackedLocalTime.getMillisecondOfDay(packedTime2));
        Assertions.assertEquals(localTime.getHour(), PackedLocalTime.getHour(packedTime2));
        Assertions.assertEquals(localTime.getMinute(), PackedLocalTime.getMinute(packedTime2));
        Assertions.assertEquals(localTime.getSecond(), PackedLocalTime.getSecond(packedTime2));
    }

    @Test
    public void testConstructors2() {
        LocalTime localTime = LocalTime.of(5, 11);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.of(5, 11);
        Assertions.assertEquals(PackedLocalTime.getMillisecondOfDay(packedTime), PackedLocalTime.getMillisecondOfDay(packedTime2));
        Assertions.assertEquals(localTime.getHour(), PackedLocalTime.getHour(packedTime2));
        Assertions.assertEquals(localTime.getMinute(), PackedLocalTime.getMinute(packedTime2));
        Assertions.assertEquals(localTime.getSecond(), PackedLocalTime.getSecond(packedTime2));
    }

    @Test
    public void testConstructors3() {
        LocalTime localTime = LocalTime.of(5, 11, 33, (811 * 1000000));
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.of(5, 11, 33, 811);
        Assertions.assertEquals(PackedLocalTime.getMillisecondOfDay(packedTime), PackedLocalTime.getMillisecondOfDay(packedTime2));
        assertTimeEquals(localTime, packedTime2);
    }

    @Test
    public void testWithHour() {
        LocalTime localTime = LocalTime.of(5, 11, 33, (811 * 1000000));
        LocalTime localTime2 = localTime.withHour(7);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.withHour(7, packedTime);
        assertTimeEquals(localTime2, packedTime2);
    }

    @Test
    public void testWithMinute() {
        LocalTime localTime = LocalTime.of(5, 11, 33, (811 * 1000000));
        LocalTime localTime2 = localTime.withMinute(7);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.withMinute(7, packedTime);
        assertTimeEquals(localTime2, packedTime2);
    }

    @Test
    public void testWithSecond() {
        LocalTime localTime = LocalTime.of(5, 11, 33, (811 * 1000000));
        LocalTime localTime2 = localTime.withSecond(42);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.withSecond(42, packedTime);
        assertTimeEquals(localTime2, packedTime2);
    }

    @Test
    public void testPlusSeconds() {
        LocalTime localTime = LocalTime.of(5, 11, 33, (811 * 1000000));
        LocalTime localTime2 = localTime.plusSeconds(4340);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.plusSeconds(4340, packedTime);
        assertTimeEquals(localTime2, packedTime2);
        int packedTime3 = PackedLocalTime.minusSeconds(4340, packedTime2);
        assertTimeEquals(localTime, packedTime3);
    }

    @Test
    public void testPlusMinutes() {
        LocalTime localTime = LocalTime.of(5, 11, 33, (811 * 1000000));
        LocalTime localTime2 = localTime.plusMinutes(77);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.plusMinutes(77, packedTime);
        assertTimeEquals(localTime2, packedTime2);
        int packedTime3 = PackedLocalTime.minusMinutes(77, packedTime2);
        assertTimeEquals(localTime, packedTime3);
    }

    @Test
    public void testPlusHours() {
        LocalTime localTime = LocalTime.of(5, 11, 33, (811 * 1000000));
        LocalTime localTime2 = localTime.plusHours(3);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.plusHours(3, packedTime);
        assertTimeEquals(localTime2, packedTime2);
        int packedTime3 = PackedLocalTime.minusHours(3, packedTime2);
        assertTimeEquals(localTime, packedTime3);
    }

    @Test
    public void testPlusHours2() {
        LocalTime localTime = LocalTime.of(5, 11, 33, (811 * 1000000));
        LocalTime localTime2 = localTime.plusHours(20);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.plusHours(20, packedTime);
        assertTimeEquals(localTime2, packedTime2);
        int packedTime3 = PackedLocalTime.minusHours(20, packedTime2);
        assertTimeEquals(localTime, packedTime3);
    }

    @Test
    public void testSecondsUntil() {
        LocalTime localTime = LocalTime.of(5, 11, 33, (811 * 1000000));
        LocalTime localTime2 = localTime.plusHours(20);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.pack(localTime2);
        Assertions.assertEquals(localTime.until(localTime2, ChronoUnit.SECONDS), PackedLocalTime.secondsUntil(packedTime2, packedTime));
    }

    @Test
    public void testMinutesUntil() {
        LocalTime localTime = LocalTime.of(5, 11, 33, (811 * 1000000));
        LocalTime localTime2 = localTime.plusHours(20);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.pack(localTime2);
        Assertions.assertEquals(localTime.until(localTime2, ChronoUnit.MINUTES), PackedLocalTime.minutesUntil(packedTime2, packedTime));
    }

    @Test
    public void testHoursUntil() {
        LocalTime localTime = LocalTime.of(5, 11, 33, (811 * 1000000));
        LocalTime localTime2 = localTime.plusHours(20);
        int packedTime = PackedLocalTime.pack(localTime);
        int packedTime2 = PackedLocalTime.pack(localTime2);
        Assertions.assertEquals(localTime.until(localTime2, ChronoUnit.HOURS), PackedLocalTime.hoursUntil(packedTime2, packedTime));
    }

    @Test
    public void testPack() {
        LocalTime time = LocalTime.now();
        int packed = PackedLocalTime.pack(time);
        LocalTime t1 = PackedLocalTime.asLocalTime(PackedLocalDateTime.time(packed));
        Assertions.assertNotNull(t1);
        Assertions.assertEquals(time.getHour(), t1.getHour());
        Assertions.assertEquals(time.getMinute(), t1.getMinute());
        Assertions.assertEquals(time.getSecond(), t1.getSecond());
        Assertions.assertEquals(time.get(ChronoField.MILLI_OF_SECOND), t1.get(ChronoField.MILLI_OF_SECOND));
    }
}

