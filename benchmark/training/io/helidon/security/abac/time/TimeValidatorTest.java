/**
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security.abac.time;


import Errors.Collector;
import TimeValidator.TimeConfig;
import io.helidon.common.Errors;
import io.helidon.security.ProviderRequest;
import io.helidon.security.SecurityEnvironment;
import io.helidon.security.SecurityTime;
import java.lang.annotation.Annotation;
import java.time.DayOfWeek;
import java.time.temporal.ChronoField;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Unit test for {@link TimeValidator}.
 */
public class TimeValidatorTest {
    private static TimeValidator validator;

    private static List<Annotation> annotations = new LinkedList<>();

    private static TimeConfig timeConfig;

    @Test
    public void testBetweenTimesAndDayOfWekPermit() {
        // explicitly set time to 10:00
        SecurityTime time = SecurityTime.builder().value(ChronoField.HOUR_OF_DAY, 10).value(ChronoField.MINUTE_OF_HOUR, 0).value(ChronoField.DAY_OF_WEEK, DayOfWeek.TUESDAY.getValue()).build();
        Errors.Collector collector = Errors.collector();
        SecurityEnvironment env = SecurityEnvironment.builder().time(time).build();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(env);
        TimeValidatorTest.validator.validate(TimeValidatorTest.timeConfig, collector, request);
        collector.collect().checkValid();
    }

    @Test
    public void testBetweenTimesDeny() {
        // explicitly set time to 10:00
        SecurityTime time = SecurityTime.builder().value(ChronoField.HOUR_OF_DAY, 12).value(ChronoField.MINUTE_OF_HOUR, 15).value(ChronoField.DAY_OF_WEEK, DayOfWeek.TUESDAY.getValue()).build();
        Errors.Collector collector = Errors.collector();
        SecurityEnvironment env = SecurityEnvironment.builder().time(time).build();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(env);
        TimeValidatorTest.validator.validate(TimeValidatorTest.timeConfig, collector, request);
        if (collector.collect().isValid()) {
            Assertions.fail("Should have failed, as 12:15 is not in supported times");
        }
    }

    @Test
    public void testDayOfWeekDeny() {
        // explicitly set time to 10:00
        SecurityTime time = SecurityTime.builder().value(ChronoField.HOUR_OF_DAY, 12).value(ChronoField.MINUTE_OF_HOUR, 15).value(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.getValue()).build();
        Errors.Collector collector = Errors.collector();
        SecurityEnvironment env = SecurityEnvironment.builder().time(time).build();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(env);
        TimeValidatorTest.validator.validate(TimeValidatorTest.timeConfig, collector, request);
        if (collector.collect().isValid()) {
            Assertions.fail("Should have failed, as 12:15 is not in supported times");
        }
    }
}

