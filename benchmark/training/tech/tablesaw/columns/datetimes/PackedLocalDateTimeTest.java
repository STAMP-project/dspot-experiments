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
package tech.tablesaw.columns.datetimes;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.columns.times.PackedLocalTime;


public class PackedLocalDateTimeTest {
    @Test
    public void testGetDayOfMonth() {
        LocalDateTime today = LocalDateTime.now();
        Assertions.assertEquals(today.getDayOfMonth(), PackedLocalDateTime.getDayOfMonth(PackedLocalDateTime.pack(today)));
    }

    @Test
    public void testGetYear() {
        LocalDateTime today = LocalDateTime.now();
        Assertions.assertEquals(today.getYear(), PackedLocalDateTime.getYear(PackedLocalDateTime.pack(today)));
    }

    @Test
    public void testAsLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        long packed = PackedLocalDateTime.pack(dateTime.toLocalDate(), dateTime.toLocalTime());
        LocalDateTime upacked = PackedLocalDateTime.asLocalDateTime(packed);
        Assertions.assertEquals(dateTime.getDayOfYear(), upacked.getDayOfYear());
        Assertions.assertEquals(dateTime.getHour(), upacked.getHour());
        Assertions.assertEquals(dateTime.getMinute(), upacked.getMinute());
        Assertions.assertEquals(dateTime.getSecond(), upacked.getSecond());
    }

    @Test
    public void testGetMonthValue() {
        long dateTime = PackedLocalDateTime.pack(LocalDate.of(2015, 12, 25), LocalTime.now());
        Assertions.assertEquals(12, PackedLocalDateTime.getMonthValue(dateTime));
    }

    @Test
    public void testPack() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        long packed = PackedLocalDateTime.pack(date, time);
        LocalDate d1 = PackedLocalDate.asLocalDate(PackedLocalDateTime.date(packed));
        LocalTime t1 = PackedLocalTime.asLocalTime(PackedLocalDateTime.time(packed));
        Assertions.assertNotNull(d1);
        Assertions.assertNotNull(t1);
        Assertions.assertEquals(date.toString(), d1.toString());
    }

    @Test
    public void testGetHour() {
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertEquals(now.getHour(), PackedLocalDateTime.getHour(PackedLocalDateTime.pack(now)));
    }

    @Test
    public void testGetMinute() {
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertEquals(now.getMinute(), PackedLocalDateTime.getMinute(PackedLocalDateTime.pack(now)));
    }

    @Test
    public void testGetSecond() {
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertEquals(now.getSecond(), PackedLocalDateTime.getSecond(PackedLocalDateTime.pack(now)));
    }

    @Test
    public void testGetSecondOfDay() {
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertEquals(now.get(ChronoField.SECOND_OF_DAY), PackedLocalDateTime.getSecondOfDay(PackedLocalDateTime.pack(now)), 1.0E-4);
    }

    @Test
    public void testGetMinuteOfDay() {
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertEquals(now.get(ChronoField.MINUTE_OF_DAY), PackedLocalDateTime.getMinuteOfDay(PackedLocalDateTime.pack(now)));
    }

    @Test
    public void testGetMillisecondOfDay() {
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertEquals(now.get(ChronoField.MILLI_OF_DAY), PackedLocalDateTime.getMillisecondOfDay(PackedLocalDateTime.pack(now)));
    }

    @Test
    public void testGetDayOfWeek() {
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertEquals(now.get(ChronoField.DAY_OF_WEEK), PackedLocalDateTime.getDayOfWeek(PackedLocalDateTime.pack(now)).getValue());
    }

    @Test
    public void testToEpochMillis() {
        long now = PackedLocalDateTime.pack(LocalDateTime.now());
        long millis = PackedLocalDateTime.toEpochMilli(now, ZoneOffset.UTC);
        long now2 = PackedLocalDateTime.ofEpochMilli(millis, ZoneId.of("UTC"));
        Assertions.assertEquals(now, now2);
    }
}

