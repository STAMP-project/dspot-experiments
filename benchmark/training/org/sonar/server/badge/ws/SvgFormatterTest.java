/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.badge.ws;


import org.junit.Test;
import org.sonar.test.TestUtils;


public class SvgFormatterTest {
    private static final int HOURS_IN_DAY = 8;

    private static final long ONE_MINUTE = 1L;

    private static final long ONE_HOUR = (SvgFormatterTest.ONE_MINUTE) * 60;

    private static final long ONE_DAY = (SvgFormatterTest.HOURS_IN_DAY) * (SvgFormatterTest.ONE_HOUR);

    @Test
    public void format_numeric() {
        assertThat(SvgFormatter.formatNumeric(0L)).isEqualTo("0");
        assertThat(SvgFormatter.formatNumeric(5L)).isEqualTo("5");
        assertThat(SvgFormatter.formatNumeric(950L)).isEqualTo("950");
        assertThat(SvgFormatter.formatNumeric(1000L)).isEqualTo("1k");
        assertThat(SvgFormatter.formatNumeric(1010L)).isEqualTo("1k");
        assertThat(SvgFormatter.formatNumeric(1100L)).isEqualTo("1.1k");
        assertThat(SvgFormatter.formatNumeric(1690L)).isEqualTo("1.7k");
        assertThat(SvgFormatter.formatNumeric(950000L)).isEqualTo("950k");
        assertThat(SvgFormatter.formatNumeric(1000000L)).isEqualTo("1m");
        assertThat(SvgFormatter.formatNumeric(1010000L)).isEqualTo("1m");
        assertThat(SvgFormatter.formatNumeric(1000000000L)).isEqualTo("1b");
        assertThat(SvgFormatter.formatNumeric(1000000000000L)).isEqualTo("1t");
    }

    @Test
    public void format_percent() {
        assertThat(SvgFormatter.formatPercent(0.0)).isEqualTo("0%");
        assertThat(SvgFormatter.formatPercent(12.345)).isEqualTo("12.3%");
        assertThat(SvgFormatter.formatPercent(12.56)).isEqualTo("12.6%");
    }

    @Test
    public void format_duration() {
        assertThat(SvgFormatter.formatDuration(0)).isEqualTo("0");
        assertThat(SvgFormatter.formatDuration(SvgFormatterTest.ONE_DAY)).isEqualTo("1d");
        assertThat(SvgFormatter.formatDuration(SvgFormatterTest.ONE_HOUR)).isEqualTo("1h");
        assertThat(SvgFormatter.formatDuration(SvgFormatterTest.ONE_MINUTE)).isEqualTo("1min");
        assertThat(SvgFormatter.formatDuration((5 * (SvgFormatterTest.ONE_DAY)))).isEqualTo("5d");
        assertThat(SvgFormatter.formatDuration((2 * (SvgFormatterTest.ONE_HOUR)))).isEqualTo("2h");
        assertThat(SvgFormatter.formatDuration(SvgFormatterTest.ONE_MINUTE)).isEqualTo("1min");
        assertThat(SvgFormatter.formatDuration(((5 * (SvgFormatterTest.ONE_DAY)) + (3 * (SvgFormatterTest.ONE_HOUR))))).isEqualTo("5d");
        assertThat(SvgFormatter.formatDuration(((3 * (SvgFormatterTest.ONE_HOUR)) + (25 * (SvgFormatterTest.ONE_MINUTE))))).isEqualTo("3h");
        assertThat(SvgFormatter.formatDuration((((5 * (SvgFormatterTest.ONE_DAY)) + (3 * (SvgFormatterTest.ONE_HOUR))) + (40 * (SvgFormatterTest.ONE_MINUTE))))).isEqualTo("5d");
    }

    @Test
    public void format_duration_is_rounding_result() {
        // When starting to add more than 4 hours, the result will be rounded to the next day (as 4 hour is a half day)
        assertThat(SvgFormatter.formatDuration(((5 * (SvgFormatterTest.ONE_DAY)) + (4 * (SvgFormatterTest.ONE_HOUR))))).isEqualTo("6d");
        assertThat(SvgFormatter.formatDuration(((5 * (SvgFormatterTest.ONE_DAY)) + (5 * (SvgFormatterTest.ONE_HOUR))))).isEqualTo("6d");
        // When starting to add more than 30 minutes, the result will be rounded to the next hour
        assertThat(SvgFormatter.formatDuration(((3 * (SvgFormatterTest.ONE_HOUR)) + (30 * (SvgFormatterTest.ONE_MINUTE))))).isEqualTo("4h");
        assertThat(SvgFormatter.formatDuration(((3 * (SvgFormatterTest.ONE_HOUR)) + (40 * (SvgFormatterTest.ONE_MINUTE))))).isEqualTo("4h");
        // When duration is close to next unit (0.9), the result is rounded to next unit
        assertThat(SvgFormatter.formatDuration((((7 * (SvgFormatterTest.ONE_HOUR)) + 20) + (SvgFormatterTest.ONE_MINUTE)))).isEqualTo("1d");
        assertThat(SvgFormatter.formatDuration((55 * (SvgFormatterTest.ONE_MINUTE)))).isEqualTo("1h");
    }

    @Test
    public void only_statics() {
        assertThat(TestUtils.hasOnlyPrivateConstructors(SvgFormatter.class)).isTrue();
    }
}

