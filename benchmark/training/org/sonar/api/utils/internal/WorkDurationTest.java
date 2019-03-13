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
package org.sonar.api.utils.internal;


import WorkDuration.UNIT.DAYS;
import WorkDuration.UNIT.HOURS;
import WorkDuration.UNIT.MINUTES;
import org.junit.Test;


public class WorkDurationTest {
    static final int HOURS_IN_DAY = 8;

    static final Long ONE_MINUTE = 1L;

    static final Long ONE_HOUR_IN_MINUTES = (WorkDurationTest.ONE_MINUTE) * 60;

    static final Long ONE_DAY_IN_MINUTES = (WorkDurationTest.ONE_HOUR_IN_MINUTES) * (WorkDurationTest.HOURS_IN_DAY);

    @Test
    public void create_from_days_hours_minutes() {
        WorkDuration workDuration = WorkDuration.create(1, 1, 1, WorkDurationTest.HOURS_IN_DAY);
        assertThat(workDuration.days()).isEqualTo(1);
        assertThat(workDuration.hours()).isEqualTo(1);
        assertThat(workDuration.minutes()).isEqualTo(1);
        assertThat(workDuration.toMinutes()).isEqualTo((((WorkDurationTest.ONE_DAY_IN_MINUTES) + (WorkDurationTest.ONE_HOUR_IN_MINUTES)) + (WorkDurationTest.ONE_MINUTE)));
        assertThat(workDuration.hoursInDay()).isEqualTo(WorkDurationTest.HOURS_IN_DAY);
    }

    @Test
    public void create_from_value_and_unit() {
        WorkDuration result = WorkDuration.createFromValueAndUnit(1, DAYS, WorkDurationTest.HOURS_IN_DAY);
        assertThat(result.days()).isEqualTo(1);
        assertThat(result.hours()).isEqualTo(0);
        assertThat(result.minutes()).isEqualTo(0);
        assertThat(result.hoursInDay()).isEqualTo(WorkDurationTest.HOURS_IN_DAY);
        assertThat(result.toMinutes()).isEqualTo(WorkDurationTest.ONE_DAY_IN_MINUTES);
        assertThat(WorkDuration.createFromValueAndUnit(1, DAYS, WorkDurationTest.HOURS_IN_DAY).toMinutes()).isEqualTo(WorkDurationTest.ONE_DAY_IN_MINUTES);
        assertThat(WorkDuration.createFromValueAndUnit(1, HOURS, WorkDurationTest.HOURS_IN_DAY).toMinutes()).isEqualTo(WorkDurationTest.ONE_HOUR_IN_MINUTES);
        assertThat(WorkDuration.createFromValueAndUnit(1, MINUTES, WorkDurationTest.HOURS_IN_DAY).toMinutes()).isEqualTo(WorkDurationTest.ONE_MINUTE);
    }

    @Test
    public void create_from_minutes() {
        WorkDuration workDuration = WorkDuration.createFromMinutes(WorkDurationTest.ONE_MINUTE, WorkDurationTest.HOURS_IN_DAY);
        assertThat(workDuration.days()).isEqualTo(0);
        assertThat(workDuration.hours()).isEqualTo(0);
        assertThat(workDuration.minutes()).isEqualTo(1);
        workDuration = WorkDuration.createFromMinutes(WorkDurationTest.ONE_HOUR_IN_MINUTES, WorkDurationTest.HOURS_IN_DAY);
        assertThat(workDuration.days()).isEqualTo(0);
        assertThat(workDuration.hours()).isEqualTo(1);
        assertThat(workDuration.minutes()).isEqualTo(0);
        workDuration = WorkDuration.createFromMinutes(WorkDurationTest.ONE_DAY_IN_MINUTES, WorkDurationTest.HOURS_IN_DAY);
        assertThat(workDuration.days()).isEqualTo(1);
        assertThat(workDuration.hours()).isEqualTo(0);
        assertThat(workDuration.minutes()).isEqualTo(0);
    }

    @Test
    public void create_from_working_long() {
        // 1 minute
        WorkDuration workDuration = WorkDuration.createFromLong(1L, WorkDurationTest.HOURS_IN_DAY);
        assertThat(workDuration.days()).isEqualTo(0);
        assertThat(workDuration.hours()).isEqualTo(0);
        assertThat(workDuration.minutes()).isEqualTo(1);
        // 1 hour
        workDuration = WorkDuration.createFromLong(100L, WorkDurationTest.HOURS_IN_DAY);
        assertThat(workDuration.days()).isEqualTo(0);
        assertThat(workDuration.hours()).isEqualTo(1);
        assertThat(workDuration.minutes()).isEqualTo(0);
        // 1 day
        workDuration = WorkDuration.createFromLong(10000L, WorkDurationTest.HOURS_IN_DAY);
        assertThat(workDuration.days()).isEqualTo(1);
        assertThat(workDuration.hours()).isEqualTo(0);
        assertThat(workDuration.minutes()).isEqualTo(0);
    }

    @Test
    public void convert_to_seconds() {
        assertThat(WorkDuration.createFromValueAndUnit(2, MINUTES, WorkDurationTest.HOURS_IN_DAY).toMinutes()).isEqualTo((2L * (WorkDurationTest.ONE_MINUTE)));
        assertThat(WorkDuration.createFromValueAndUnit(2, HOURS, WorkDurationTest.HOURS_IN_DAY).toMinutes()).isEqualTo((2L * (WorkDurationTest.ONE_HOUR_IN_MINUTES)));
        assertThat(WorkDuration.createFromValueAndUnit(2, DAYS, WorkDurationTest.HOURS_IN_DAY).toMinutes()).isEqualTo((2L * (WorkDurationTest.ONE_DAY_IN_MINUTES)));
    }

    @Test
    public void convert_to_working_days() {
        assertThat(WorkDuration.createFromValueAndUnit(2, MINUTES, WorkDurationTest.HOURS_IN_DAY).toWorkingDays()).isEqualTo(((2.0 / 60.0) / 8.0));
        assertThat(WorkDuration.createFromValueAndUnit(240, MINUTES, WorkDurationTest.HOURS_IN_DAY).toWorkingDays()).isEqualTo(0.5);
        assertThat(WorkDuration.createFromValueAndUnit(4, HOURS, WorkDurationTest.HOURS_IN_DAY).toWorkingDays()).isEqualTo(0.5);
        assertThat(WorkDuration.createFromValueAndUnit(8, HOURS, WorkDurationTest.HOURS_IN_DAY).toWorkingDays()).isEqualTo(1.0);
        assertThat(WorkDuration.createFromValueAndUnit(16, HOURS, WorkDurationTest.HOURS_IN_DAY).toWorkingDays()).isEqualTo(2.0);
        assertThat(WorkDuration.createFromValueAndUnit(2, DAYS, WorkDurationTest.HOURS_IN_DAY).toWorkingDays()).isEqualTo(2.0);
    }

    @Test
    public void convert_to_working_long() {
        assertThat(WorkDuration.createFromValueAndUnit(2, MINUTES, WorkDurationTest.HOURS_IN_DAY).toLong()).isEqualTo(2L);
        assertThat(WorkDuration.createFromValueAndUnit(4, HOURS, WorkDurationTest.HOURS_IN_DAY).toLong()).isEqualTo(400L);
        assertThat(WorkDuration.createFromValueAndUnit(10, HOURS, WorkDurationTest.HOURS_IN_DAY).toLong()).isEqualTo(10200L);
        assertThat(WorkDuration.createFromValueAndUnit(8, HOURS, WorkDurationTest.HOURS_IN_DAY).toLong()).isEqualTo(10000L);
        assertThat(WorkDuration.createFromValueAndUnit(2, DAYS, WorkDurationTest.HOURS_IN_DAY).toLong()).isEqualTo(20000L);
    }

    @Test
    public void add() {
        // 4h + 5h = 1d 1h
        WorkDuration result = WorkDuration.createFromValueAndUnit(4, HOURS, WorkDurationTest.HOURS_IN_DAY).add(WorkDuration.createFromValueAndUnit(5, HOURS, WorkDurationTest.HOURS_IN_DAY));
        assertThat(result.days()).isEqualTo(1);
        assertThat(result.hours()).isEqualTo(1);
        assertThat(result.minutes()).isEqualTo(0);
        assertThat(result.hoursInDay()).isEqualTo(WorkDurationTest.HOURS_IN_DAY);
        // 40 m + 30m = 1h 10m
        result = WorkDuration.createFromValueAndUnit(40, MINUTES, WorkDurationTest.HOURS_IN_DAY).add(WorkDuration.createFromValueAndUnit(30, MINUTES, WorkDurationTest.HOURS_IN_DAY));
        assertThat(result.days()).isEqualTo(0);
        assertThat(result.hours()).isEqualTo(1);
        assertThat(result.minutes()).isEqualTo(10);
        assertThat(result.hoursInDay()).isEqualTo(WorkDurationTest.HOURS_IN_DAY);
        // 10 m + 20m = 30m
        assertThat(WorkDuration.createFromValueAndUnit(10, MINUTES, WorkDurationTest.HOURS_IN_DAY).add(WorkDuration.createFromValueAndUnit(20, MINUTES, WorkDurationTest.HOURS_IN_DAY)).minutes()).isEqualTo(30);
        assertThat(WorkDuration.createFromValueAndUnit(10, MINUTES, WorkDurationTest.HOURS_IN_DAY).add(null).minutes()).isEqualTo(10);
    }

    @Test
    public void subtract() {
        // 1d 1h - 5h = 4h
        WorkDuration result = WorkDuration.create(1, 1, 0, WorkDurationTest.HOURS_IN_DAY).subtract(WorkDuration.createFromValueAndUnit(5, HOURS, WorkDurationTest.HOURS_IN_DAY));
        assertThat(result.days()).isEqualTo(0);
        assertThat(result.hours()).isEqualTo(4);
        assertThat(result.minutes()).isEqualTo(0);
        assertThat(result.hoursInDay()).isEqualTo(WorkDurationTest.HOURS_IN_DAY);
        // 1h 10m - 30m = 40m
        result = WorkDuration.create(0, 1, 10, WorkDurationTest.HOURS_IN_DAY).subtract(WorkDuration.createFromValueAndUnit(30, MINUTES, WorkDurationTest.HOURS_IN_DAY));
        assertThat(result.days()).isEqualTo(0);
        assertThat(result.hours()).isEqualTo(0);
        assertThat(result.minutes()).isEqualTo(40);
        assertThat(result.hoursInDay()).isEqualTo(WorkDurationTest.HOURS_IN_DAY);
        // 30m - 20m = 10m
        assertThat(WorkDuration.createFromValueAndUnit(30, MINUTES, WorkDurationTest.HOURS_IN_DAY).subtract(WorkDuration.createFromValueAndUnit(20, MINUTES, WorkDurationTest.HOURS_IN_DAY)).minutes()).isEqualTo(10);
        assertThat(WorkDuration.createFromValueAndUnit(10, MINUTES, WorkDurationTest.HOURS_IN_DAY).subtract(null).minutes()).isEqualTo(10);
    }

    @Test
    public void multiply() {
        // 5h * 2 = 1d 2h
        WorkDuration result = WorkDuration.createFromValueAndUnit(5, HOURS, WorkDurationTest.HOURS_IN_DAY).multiply(2);
        assertThat(result.days()).isEqualTo(1);
        assertThat(result.hours()).isEqualTo(2);
        assertThat(result.minutes()).isEqualTo(0);
        assertThat(result.hoursInDay()).isEqualTo(WorkDurationTest.HOURS_IN_DAY);
    }

    @Test
    public void test_equals_and_hashcode() throws Exception {
        WorkDuration duration = WorkDuration.createFromLong(28800, WorkDurationTest.HOURS_IN_DAY);
        WorkDuration durationWithSameValue = WorkDuration.createFromLong(28800, WorkDurationTest.HOURS_IN_DAY);
        WorkDuration durationWithDifferentValue = WorkDuration.createFromLong(14400, WorkDurationTest.HOURS_IN_DAY);
        assertThat(duration).isEqualTo(duration);
        assertThat(durationWithSameValue).isEqualTo(duration);
        assertThat(durationWithDifferentValue).isNotEqualTo(duration);
        assertThat(duration).isNotEqualTo(null);
        assertThat(duration.hashCode()).isEqualTo(duration.hashCode());
        assertThat(durationWithSameValue.hashCode()).isEqualTo(duration.hashCode());
        assertThat(durationWithDifferentValue.hashCode()).isNotEqualTo(duration.hashCode());
    }

    @Test
    public void test_toString() throws Exception {
        assertThat(WorkDuration.createFromLong(28800, WorkDurationTest.HOURS_IN_DAY).toString()).isNotNull();
    }
}

