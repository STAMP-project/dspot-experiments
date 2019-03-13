/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.web.util;


import com.navercorp.pinpoint.web.vo.Range;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 *
 *
 * @author hyungil.jeong
 */
public class TimeWindowSlotCentricSamplerTest {
    private static final long START_TIME_STAMP = 1234567890123L;

    private static final long ONE_SECOND = TimeUnit.SECONDS.toMillis(1);

    private static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);

    private static final long ONE_HOUR = TimeUnit.HOURS.toMillis(1);

    private static final long ONE_DAY = TimeUnit.DAYS.toMillis(1);

    private static final TimeWindowSampler sampler = new TimeWindowSlotCentricSampler.TimeWindowSlotCentricSampler();

    @Test
    public void getWindowSizeFor_1_second() {
        // Given
        final long from = TimeWindowSlotCentricSamplerTest.START_TIME_STAMP;
        final long to = (TimeWindowSlotCentricSamplerTest.START_TIME_STAMP) + (TimeWindowSlotCentricSamplerTest.ONE_SECOND);
        final Range range = new Range(from, to);
        // When
        final long idealWindowSize = TimeWindowSlotCentricSamplerTest.sampler.getWindowSize(range);
        // Then
        assertWindowSizeIsIdeal(from, to, idealWindowSize);
    }

    @Test
    public void getWindowSizeFor_5_seconds() {
        // Given
        final long from = TimeWindowSlotCentricSamplerTest.START_TIME_STAMP;
        final long to = (TimeWindowSlotCentricSamplerTest.START_TIME_STAMP) + (5 * (TimeWindowSlotCentricSamplerTest.ONE_SECOND));
        final Range range = new Range(from, to);
        // When
        final long idealWindowSize = TimeWindowSlotCentricSamplerTest.sampler.getWindowSize(range);
        // Then
        assertWindowSizeIsIdeal(from, to, idealWindowSize);
    }

    @Test
    public void getWindowSizeFor_5_minutes() {
        // Given
        final long from = TimeWindowSlotCentricSamplerTest.START_TIME_STAMP;
        final long to = (TimeWindowSlotCentricSamplerTest.START_TIME_STAMP) + (5 * (TimeWindowSlotCentricSamplerTest.ONE_MINUTE));
        final Range range = new Range(from, to);
        // When
        final long idealWindowSize = TimeWindowSlotCentricSamplerTest.sampler.getWindowSize(range);
        // Then
        assertWindowSizeIsIdeal(from, to, idealWindowSize);
    }

    @Test
    public void getWindowSizeFor_20_minutes() {
        // Given
        final long from = TimeWindowSlotCentricSamplerTest.START_TIME_STAMP;
        final long to = (TimeWindowSlotCentricSamplerTest.START_TIME_STAMP) + (20 * (TimeWindowSlotCentricSamplerTest.ONE_MINUTE));
        final Range range = new Range(from, to);
        // When
        final long idealWindowSize = TimeWindowSlotCentricSamplerTest.sampler.getWindowSize(range);
        // Then
        assertWindowSizeIsIdeal(from, to, idealWindowSize);
    }

    @Test
    public void getWindowSizeFor_1_hour() {
        // Given
        final long from = TimeWindowSlotCentricSamplerTest.START_TIME_STAMP;
        final long to = (TimeWindowSlotCentricSamplerTest.START_TIME_STAMP) + (TimeWindowSlotCentricSamplerTest.ONE_HOUR);
        final Range range = new Range(from, to);
        // When
        final long idealWindowSize = TimeWindowSlotCentricSamplerTest.sampler.getWindowSize(range);
        // Then
        assertWindowSizeIsIdeal(from, to, idealWindowSize);
    }

    @Test
    public void getWindowSizeFor_3_hours() {
        // Given
        final long from = TimeWindowSlotCentricSamplerTest.START_TIME_STAMP;
        final long to = (TimeWindowSlotCentricSamplerTest.START_TIME_STAMP) + (3 * (TimeWindowSlotCentricSamplerTest.ONE_HOUR));
        final Range range = new Range(from, to);
        // When
        final long idealWindowSize = TimeWindowSlotCentricSamplerTest.sampler.getWindowSize(range);
        // Then
        assertWindowSizeIsIdeal(from, to, idealWindowSize);
    }

    @Test
    public void getWindowSizeFor_6_hours() {
        // Given
        final long from = TimeWindowSlotCentricSamplerTest.START_TIME_STAMP;
        final long to = (TimeWindowSlotCentricSamplerTest.START_TIME_STAMP) + (6 * (TimeWindowSlotCentricSamplerTest.ONE_HOUR));
        final Range range = new Range(from, to);
        // When
        final long idealWindowSize = TimeWindowSlotCentricSamplerTest.sampler.getWindowSize(range);
        // Then
        assertWindowSizeIsIdeal(from, to, idealWindowSize);
    }

    @Test
    public void getWindowSizeFor_12_hours() {
        // Given
        final long from = TimeWindowSlotCentricSamplerTest.START_TIME_STAMP;
        final long to = (TimeWindowSlotCentricSamplerTest.START_TIME_STAMP) + (12 * (TimeWindowSlotCentricSamplerTest.ONE_HOUR));
        final Range range = new Range(from, to);
        // When
        final long idealWindowSize = TimeWindowSlotCentricSamplerTest.sampler.getWindowSize(range);
        // Then
        assertWindowSizeIsIdeal(from, to, idealWindowSize);
    }

    @Test
    public void getWindowSizeFor_1_day() {
        // Given
        final long from = TimeWindowSlotCentricSamplerTest.START_TIME_STAMP;
        final long to = (TimeWindowSlotCentricSamplerTest.START_TIME_STAMP) + (TimeWindowSlotCentricSamplerTest.ONE_DAY);
        final Range range = new Range(from, to);
        // When
        final long idealWindowSize = TimeWindowSlotCentricSamplerTest.sampler.getWindowSize(range);
        // Then
        assertWindowSizeIsIdeal(from, to, idealWindowSize);
    }

    @Test
    public void getWindowSizeFor_2_days() {
        // Given
        final long from = TimeWindowSlotCentricSamplerTest.START_TIME_STAMP;
        final long to = (TimeWindowSlotCentricSamplerTest.START_TIME_STAMP) + (2 * (TimeWindowSlotCentricSamplerTest.ONE_DAY));
        final Range range = new Range(from, to);
        // When
        final long idealWindowSize = TimeWindowSlotCentricSamplerTest.sampler.getWindowSize(range);
        // Then
        assertWindowSizeIsIdeal(from, to, idealWindowSize);
    }

    @Test
    public void getWindowSizeEverySecondsFor_5_years() {
        final long numSecondsPerYear = ((60 * 60) * 24) * 365;
        for (long periodMs = 0; periodMs <= (numSecondsPerYear * 5); periodMs += TimeWindowSlotCentricSamplerTest.ONE_SECOND) {
            final long from = TimeWindowSlotCentricSamplerTest.START_TIME_STAMP;
            final long to = (TimeWindowSlotCentricSamplerTest.START_TIME_STAMP) + periodMs;
            final Range range = new Range(from, to);
            final long idealWindowSize = TimeWindowSlotCentricSamplerTest.sampler.getWindowSize(range);
            assertWindowSizeIsIdeal(from, to, idealWindowSize);
        }
    }
}

