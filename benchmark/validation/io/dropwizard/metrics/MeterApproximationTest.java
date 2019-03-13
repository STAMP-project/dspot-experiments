/**
 * Copyright 2010-2013 Coda Hale and Yammer, Inc., 2014-2017 Dropwizard Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
package io.dropwizard.metrics;


import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MeterApproximationTest {
    private final long ratePerMinute;

    public MeterApproximationTest(long ratePerMinute) {
        this.ratePerMinute = ratePerMinute;
    }

    @Test
    public void controlMeter1MinuteMeanApproximation() throws Exception {
        final Meter meter = simulateMetronome(62934, TimeUnit.MILLISECONDS, 3, TimeUnit.MINUTES);
        assertThat(((meter.getOneMinuteRate()) * 60.0)).isEqualTo(ratePerMinute, offset((0.1 * (ratePerMinute))));
    }

    @Test
    public void controlMeter5MinuteMeanApproximation() throws Exception {
        final Meter meter = simulateMetronome(62934, TimeUnit.MILLISECONDS, 13, TimeUnit.MINUTES);
        assertThat(((meter.getFiveMinuteRate()) * 60.0)).isEqualTo(ratePerMinute, offset((0.1 * (ratePerMinute))));
    }

    @Test
    public void controlMeter15MinuteMeanApproximation() throws Exception {
        final Meter meter = simulateMetronome(62934, TimeUnit.MILLISECONDS, 38, TimeUnit.MINUTES);
        assertThat(((meter.getFifteenMinuteRate()) * 60.0)).isEqualTo(ratePerMinute, offset((0.1 * (ratePerMinute))));
    }
}

