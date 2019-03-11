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
package tech.tablesaw.columns.dates;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class PackedLocalDateTest {
    @Test
    public void testGetDayOfMonth() {
        LocalDate day = LocalDate.of(2011, 3, 9);
        Assertions.assertEquals(9, PackedLocalDate.getDayOfMonth(PackedLocalDate.pack(day)));
    }

    @Test
    public void testWithDayOfMonth() {
        LocalDate day = LocalDate.of(2011, 3, 9);
        int packed = PackedLocalDate.pack(day);
        int day2 = PackedLocalDate.withDayOfMonth(4, packed);
        Assertions.assertEquals(4, PackedLocalDate.getDayOfMonth(day2));
        Assertions.assertEquals(2011, PackedLocalDate.getYear(day2));
    }

    @Test
    public void testWithMonth() {
        LocalDate day = LocalDate.of(2011, 3, 9);
        int packed = PackedLocalDate.pack(day);
        int day2 = PackedLocalDate.withMonth(7, packed);
        Assertions.assertEquals(7, PackedLocalDate.getMonthValue(day2));
        Assertions.assertEquals(2011, PackedLocalDate.getYear(day2));
        Assertions.assertEquals(9, PackedLocalDate.getDayOfMonth(day2));
    }

    @Test
    public void testWithYear() {
        LocalDate day = LocalDate.of(2011, 3, 9);
        int packed = PackedLocalDate.pack(day);
        int day2 = PackedLocalDate.withYear(2020, packed);
        Assertions.assertEquals(3, PackedLocalDate.getMonthValue(day2));
        Assertions.assertEquals(2020, PackedLocalDate.getYear(day2));
        Assertions.assertEquals(9, PackedLocalDate.getDayOfMonth(day2));
    }

    @Test
    public void testPlusYears() {
        LocalDate day = LocalDate.of(2011, 3, 9);
        int packed = PackedLocalDate.pack(day);
        int day2 = PackedLocalDate.plusYears(10, packed);
        Assertions.assertEquals(3, PackedLocalDate.getMonthValue(day2));
        Assertions.assertEquals(2021, PackedLocalDate.getYear(day2));
        Assertions.assertEquals(9, PackedLocalDate.getDayOfMonth(day2));
    }

    @Test
    public void testMinusYears() {
        LocalDate day = LocalDate.of(2011, 3, 9);
        int packed = PackedLocalDate.pack(day);
        int day2 = PackedLocalDate.minusYears(10, packed);
        Assertions.assertEquals(3, PackedLocalDate.getMonthValue(day2));
        Assertions.assertEquals(2001, PackedLocalDate.getYear(day2));
        Assertions.assertEquals(9, PackedLocalDate.getDayOfMonth(day2));
    }

    @Test
    public void testPlusMonths() {
        LocalDate day = LocalDate.of(2011, 3, 9);
        int packed = PackedLocalDate.pack(day);
        int day2 = PackedLocalDate.plusMonths(11, packed);
        Assertions.assertEquals(2, PackedLocalDate.getMonthValue(day2));
        Assertions.assertEquals(2012, PackedLocalDate.getYear(day2));
        Assertions.assertEquals(9, PackedLocalDate.getDayOfMonth(day2));
    }

    @Test
    public void testMinusMonths() {
        LocalDate day = LocalDate.of(2011, 3, 9);
        int packed = PackedLocalDate.pack(day);
        int day2 = PackedLocalDate.minusMonths(4, packed);
        Assertions.assertEquals(11, PackedLocalDate.getMonthValue(day2));
        Assertions.assertEquals(2010, PackedLocalDate.getYear(day2));
        Assertions.assertEquals(9, PackedLocalDate.getDayOfMonth(day2));
    }

    @Test
    public void testPlusDays() {
        LocalDate day = LocalDate.of(2011, 12, 30);
        int packed = PackedLocalDate.pack(day);
        int day2 = PackedLocalDate.plusDays(11, packed);
        Assertions.assertEquals(1, PackedLocalDate.getMonthValue(day2));
        Assertions.assertEquals(2012, PackedLocalDate.getYear(day2));
        Assertions.assertEquals(10, PackedLocalDate.getDayOfMonth(day2));
    }

    @Test
    public void testPlusWeeks() {
        LocalDate day = LocalDate.of(2000, 2, 26);
        int packed = PackedLocalDate.pack(day);
        int day2 = PackedLocalDate.plusWeeks(2, packed);
        Assertions.assertEquals(PackedLocalDate.asLocalDate(day2), day.plusWeeks(2));
    }

    @Test
    public void testMinusWeeks() {
        LocalDate day = LocalDate.of(2001, 1, 3);
        int packed = PackedLocalDate.pack(day);
        int day2 = PackedLocalDate.minusWeeks(5, packed);
        Assertions.assertEquals(PackedLocalDate.asLocalDate(day2), day.minusWeeks(5));
    }

    @Test
    public void testDaysBetween() {
        int packed = PackedLocalDate.pack(2001, 1, 3);
        int day2 = PackedLocalDate.pack(2001, 1, 10);
        Assertions.assertEquals(7, PackedLocalDate.daysUntil(day2, packed));
    }

    @Test
    public void testMinusDays() {
        LocalDate day = LocalDate.of(2011, 1, 3);
        int packed = PackedLocalDate.pack(day);
        int day2 = PackedLocalDate.minusDays(4, packed);
        Assertions.assertEquals(12, PackedLocalDate.getMonthValue(day2));
        Assertions.assertEquals(2010, PackedLocalDate.getYear(day2));
        Assertions.assertEquals(30, PackedLocalDate.getDayOfMonth(day2));
    }

    @Test
    public void testLengthOfYear() {
        LocalDate day = LocalDate.of(2000, 1, 3);
        int packed = PackedLocalDate.pack(day);
        Assertions.assertEquals(366, PackedLocalDate.lengthOfYear(packed));
        day = LocalDate.of(2001, 1, 3);
        packed = PackedLocalDate.pack(day);
        Assertions.assertEquals(365, PackedLocalDate.lengthOfYear(packed));
    }

    @Test
    public void testLengthOfMonth() {
        LocalDate day = LocalDate.of(2011, 1, 3);
        int packed = PackedLocalDate.pack(day);
        Assertions.assertEquals(31, PackedLocalDate.lengthOfMonth(packed));
        day = LocalDate.of(2011, 9, 3);
        packed = PackedLocalDate.pack(day);
        Assertions.assertEquals(30, PackedLocalDate.lengthOfMonth(packed));
    }

    @Test
    public void testDayOfWeek() {
        LocalDate day = LocalDate.of(2018, 3, 29);
        int packed = PackedLocalDate.pack(day);
        Assertions.assertEquals(DayOfWeek.THURSDAY, PackedLocalDate.getDayOfWeek(packed));
        Assertions.assertTrue(PackedLocalDate.isThursday(packed));
        packed = PackedLocalDate.plusDays(1, packed);
        Assertions.assertEquals(DayOfWeek.FRIDAY, PackedLocalDate.getDayOfWeek(packed));
        Assertions.assertTrue(PackedLocalDate.isFriday(packed));
        packed = PackedLocalDate.plusDays(1, packed);
        Assertions.assertEquals(DayOfWeek.SATURDAY, PackedLocalDate.getDayOfWeek(packed));
        Assertions.assertTrue(PackedLocalDate.isSaturday(packed));
        packed = PackedLocalDate.plusDays(1, packed);
        Assertions.assertEquals(DayOfWeek.SUNDAY, PackedLocalDate.getDayOfWeek(packed));
        Assertions.assertTrue(PackedLocalDate.isSunday(packed));
        packed = PackedLocalDate.plusDays(1, packed);
        Assertions.assertEquals(DayOfWeek.MONDAY, PackedLocalDate.getDayOfWeek(packed));
        Assertions.assertTrue(PackedLocalDate.isMonday(packed));
        packed = PackedLocalDate.plusDays(1, packed);
        Assertions.assertEquals(DayOfWeek.TUESDAY, PackedLocalDate.getDayOfWeek(packed));
        Assertions.assertTrue(PackedLocalDate.isTuesday(packed));
        packed = PackedLocalDate.plusDays(1, packed);
        Assertions.assertEquals(DayOfWeek.WEDNESDAY, PackedLocalDate.getDayOfWeek(packed));
        Assertions.assertTrue(PackedLocalDate.isWednesday(packed));
    }

    @Test
    public void testQuarters() {
        LocalDate day = LocalDate.of(2018, 3, 29);
        int packed = PackedLocalDate.pack(day);
        Assertions.assertTrue(PackedLocalDate.isInQ1(packed));
        packed = PackedLocalDate.plusMonths(3, packed);
        Assertions.assertTrue(PackedLocalDate.isInQ2(packed));
        packed = PackedLocalDate.plusMonths(3, packed);
        Assertions.assertTrue(PackedLocalDate.isInQ3(packed));
        packed = PackedLocalDate.plusMonths(3, packed);
        Assertions.assertTrue(PackedLocalDate.isInQ4(packed));
    }

    @Test
    public void testGetYear() {
        LocalDate today = LocalDate.now();
        Assertions.assertEquals(today.getYear(), PackedLocalDate.getYear(PackedLocalDate.pack(today)));
    }

    @Test
    public void testGetMonthValue() {
        int date = PackedLocalDate.pack(LocalDate.of(2015, 1, 25));
        Month[] months = Month.values();
        for (int i = 0; i < (months.length); i++) {
            Assertions.assertEquals(months[i], PackedLocalDate.getMonth(date));
            Assertions.assertEquals((i + 1), PackedLocalDate.getMonthValue(date));
            switch (i) {
                case 0 :
                    Assertions.assertTrue(PackedLocalDate.isInJanuary(date));
                    break;
                case 1 :
                    Assertions.assertTrue(PackedLocalDate.isInFebruary(date));
                    break;
                case 2 :
                    Assertions.assertTrue(PackedLocalDate.isInMarch(date));
                    break;
                case 3 :
                    Assertions.assertTrue(PackedLocalDate.isInApril(date));
                    break;
                case 4 :
                    Assertions.assertTrue(PackedLocalDate.isInMay(date));
                    break;
                case 5 :
                    Assertions.assertTrue(PackedLocalDate.isInJune(date));
                    break;
                case 6 :
                    Assertions.assertTrue(PackedLocalDate.isInJuly(date));
                    break;
                case 7 :
                    Assertions.assertTrue(PackedLocalDate.isInAugust(date));
                    break;
                case 8 :
                    Assertions.assertTrue(PackedLocalDate.isInSeptember(date));
                    break;
                case 9 :
                    Assertions.assertTrue(PackedLocalDate.isInOctober(date));
                    break;
                case 10 :
                    Assertions.assertTrue(PackedLocalDate.isInNovember(date));
                    break;
                case 11 :
                    Assertions.assertTrue(PackedLocalDate.isInDecember(date));
                    break;
                default :
                    throw new IllegalArgumentException("Can't have a month outside this range");
            }
            date = PackedLocalDate.plusMonths(1, date);
        }
    }

    @Test
    public void testEquals() {
        int date = PackedLocalDate.pack(LocalDate.of(2015, 1, 25));
        int date2 = PackedLocalDate.pack(LocalDate.of(2015, 1, 25));
        Assertions.assertTrue(PackedLocalDate.isEqualTo(date, date2));
    }

    @Test
    public void testAfter() {
        int date = PackedLocalDate.pack(LocalDate.of(2015, 1, 25));
        int date2 = PackedLocalDate.minusDays(1, date);
        Assertions.assertTrue(PackedLocalDate.isAfter(date, date2));
        Assertions.assertFalse(PackedLocalDate.isEqualTo(date, date2));
        Assertions.assertFalse(PackedLocalDate.isBefore(date, date2));
    }

    @Test
    public void testBefore() {
        int date = PackedLocalDate.pack(LocalDate.of(2015, 1, 25));
        int date2 = PackedLocalDate.plusDays(1, date);
        Assertions.assertTrue(PackedLocalDate.isBefore(date, date2));
        Assertions.assertFalse(PackedLocalDate.isAfter(date, date2));
        Assertions.assertFalse(PackedLocalDate.isEqualTo(date, date2));
    }

    @Test
    public void testGetDayOfWeek() {
        LocalDate date = LocalDate.of(2015, 12, 25);
        int dateTime = PackedLocalDate.pack(date);
        Assertions.assertEquals(date.getDayOfWeek(), PackedLocalDate.getDayOfWeek(dateTime));
    }
}

