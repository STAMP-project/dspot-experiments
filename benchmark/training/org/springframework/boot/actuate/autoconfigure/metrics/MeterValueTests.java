/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.autoconfigure.metrics;


import Type.COUNTER;
import Type.DISTRIBUTION_SUMMARY;
import Type.GAUGE;
import Type.LONG_TASK_TIMER;
import Type.OTHER;
import Type.TIMER;
import org.junit.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link MeterValue}.
 *
 * @author Phillip Webb
 */
public class MeterValueTests {
    @Test
    public void getValueForDistributionSummaryWhenFromLongShouldReturnLongValue() {
        MeterValue meterValue = MeterValue.valueOf(123L);
        assertThat(meterValue.getValue(DISTRIBUTION_SUMMARY)).isEqualTo(123);
    }

    @Test
    public void getValueForDistributionSummaryWhenFromNumberStringShouldReturnLongValue() {
        MeterValue meterValue = MeterValue.valueOf("123");
        assertThat(meterValue.getValue(DISTRIBUTION_SUMMARY)).isEqualTo(123);
    }

    @Test
    public void getValueForDistributionSummaryWhenFromDurationStringShouldReturnNull() {
        MeterValue meterValue = MeterValue.valueOf("123ms");
        assertThat(meterValue.getValue(DISTRIBUTION_SUMMARY)).isNull();
    }

    @Test
    public void getValueForTimerWhenFromLongShouldReturnMsToNanosValue() {
        MeterValue meterValue = MeterValue.valueOf(123L);
        assertThat(meterValue.getValue(TIMER)).isEqualTo(123000000);
    }

    @Test
    public void getValueForTimerWhenFromNumberStringShouldMsToNanosValue() {
        MeterValue meterValue = MeterValue.valueOf("123");
        assertThat(meterValue.getValue(TIMER)).isEqualTo(123000000);
    }

    @Test
    public void getValueForTimerWhenFromDurationStringShouldReturnDurationNanos() {
        MeterValue meterValue = MeterValue.valueOf("123ms");
        assertThat(meterValue.getValue(TIMER)).isEqualTo(123000000);
    }

    @Test
    public void getValueForOthersShouldReturnNull() {
        MeterValue meterValue = MeterValue.valueOf("123");
        assertThat(meterValue.getValue(COUNTER)).isNull();
        assertThat(meterValue.getValue(GAUGE)).isNull();
        assertThat(meterValue.getValue(LONG_TASK_TIMER)).isNull();
        assertThat(meterValue.getValue(OTHER)).isNull();
    }

    @Test
    public void valueOfShouldWorkInBinder() {
        MockEnvironment environment = new MockEnvironment();
        TestPropertyValues.of("duration=10ms", "long=20").applyTo(environment);
        assertThat(Binder.get(environment).bind("duration", Bindable.of(MeterValue.class)).get().getValue(TIMER)).isEqualTo(10000000);
        assertThat(Binder.get(environment).bind("long", Bindable.of(MeterValue.class)).get().getValue(TIMER)).isEqualTo(20000000);
    }
}

