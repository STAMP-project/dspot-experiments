/**
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
/**
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package test.java.time;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test LocalDateTime.
 */
@RunWith(DataProviderRunner.class)
public class TestLocalDateTime extends AbstractTest {
    private LocalDateTime TEST_2007_07_15_12_30_40_987654321 = LocalDateTime.of(2007, 7, 15, 12, 30, 40, 987654321);

    @Test
    public void test_withYear_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withYear(2007);
        Assert.assertSame(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate());
        Assert.assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test
    public void test_withMonth_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMonth(7);
        Assert.assertSame(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate());
        Assert.assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test
    public void test_withDayOfMonth_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDayOfMonth(15);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_withDayOfYear_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDayOfYear(((((((31 + 28) + 31) + 30) + 31) + 30) + 15));
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_withHour_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withHour(12);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_withHour_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(1, 0)).withHour(0);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_withHour_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(1, 0)).withHour(12);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }

    @Test
    public void test_withMinute_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMinute(30);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_withMinute_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 1)).withMinute(0);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_withMinute_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 1)).withMinute(0);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }

    @Test
    public void test_withSecond_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withSecond(40);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_withSecond_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 0, 1)).withSecond(0);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_withSecond_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 0, 1)).withSecond(0);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }

    @Test
    public void test_withNanoOfSecond_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withNano(987654321);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_withNanoOfSecond_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 0, 0, 1)).withNano(0);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_withNanoOfSecond_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 0, 0, 1)).withNano(0);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }

    @Test
    public void test_plus_adjuster_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(Period.ZERO);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_plus_Period_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(MockSimplePeriod.ZERO_DAYS);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_plus_longPeriodUnit_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(0, ChronoUnit.DAYS);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_plusYears_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(0);
        Assert.assertSame(TEST_2007_07_15_12_30_40_987654321, t);
    }

    @Test
    public void test_plusMonths_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_plusWeeks_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_plusDays_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_plusHours_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusHours(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_plusHours_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(23, 0)).plusHours(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_plusHours_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(11, 0)).plusHours(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }

    @Test
    public void test_plusMinutes_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMinutes(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_plusMinutes_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMinutes((24 * 60));
        Assert.assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test
    public void test_plusMinutes_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(23, 59)).plusMinutes(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_plusMinutes_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(11, 59)).plusMinutes(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }

    @Test
    public void test_plusSeconds_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusSeconds(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_plusSeconds_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusSeconds(((24 * 60) * 60));
        Assert.assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test
    public void test_plusSeconds_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(23, 59, 59)).plusSeconds(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_plusSeconds_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(11, 59, 59)).plusSeconds(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }

    @Test
    public void test_plusNanos_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusNanos(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_plusNanos_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusNanos((((24 * 60) * 60) * 1000000000L));
        Assert.assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test
    public void test_plusNanos_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(23, 59, 59, 999999999)).plusNanos(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_plusNanos_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(11, 59, 59, 999999999)).plusNanos(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }

    @Test
    public void test_minus_adjuster_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(Period.ZERO);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_minus_Period_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(MockSimplePeriod.ZERO_DAYS);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_minus_longPeriodUnit_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(0, ChronoUnit.DAYS);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_minusYears_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_minusMonths_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_minusWeeks_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_minusDays_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_minusHours_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusHours(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_minusHours_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(1, 0)).minusHours(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_minusHours_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(13, 0)).minusHours(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }

    @Test
    public void test_minusMinutes_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMinutes(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_minusMinutes_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMinutes((24 * 60));
        Assert.assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test
    public void test_minusMinutes_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 1)).minusMinutes(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_minusMinutes_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 1)).minusMinutes(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }

    @Test
    public void test_minusSeconds_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusSeconds(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_minusSeconds_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusSeconds(((24 * 60) * 60));
        Assert.assertEquals(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate().minusDays(1));
        Assert.assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test
    public void test_minusSeconds_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 0, 1)).minusSeconds(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_minusSeconds_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 0, 1)).minusSeconds(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }

    @Test
    public void test_minusNanos_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusNanos(0);
        Assert.assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test
    public void test_minusNanos_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusNanos((((24 * 60) * 60) * 1000000000L));
        Assert.assertEquals(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate().minusDays(1));
        Assert.assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test
    public void test_minusNanos_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 0, 0, 1)).minusNanos(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test
    public void test_minusNanos_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 0, 0, 1)).minusNanos(1);
        Assert.assertSame(t.toLocalTime(), LocalTime.NOON);
    }
}

