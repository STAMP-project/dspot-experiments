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
import org.mockito.Mockito;


public class SlidingTimeWindowReservoirTest {
    private final Clock clock = Mockito.mock(Clock.class);

    private final SlidingTimeWindowReservoir reservoir = new SlidingTimeWindowReservoir(10, TimeUnit.NANOSECONDS, clock);

    @Test
    public void storesMeasurementsWithDuplicateTicks() throws Exception {
        Mockito.when(clock.getTick()).thenReturn(20L);
        reservoir.update(1);
        reservoir.update(2);
        assertThat(reservoir.getSnapshot().getValues()).containsOnly(1, 2);
    }

    @Test
    public void boundsMeasurementsToATimeWindow() throws Exception {
        Mockito.when(clock.getTick()).thenReturn(0L);
        reservoir.update(1);
        Mockito.when(clock.getTick()).thenReturn(5L);
        reservoir.update(2);
        Mockito.when(clock.getTick()).thenReturn(10L);
        reservoir.update(3);
        Mockito.when(clock.getTick()).thenReturn(15L);
        reservoir.update(4);
        Mockito.when(clock.getTick()).thenReturn(20L);
        reservoir.update(5);
        assertThat(reservoir.getSnapshot().getValues()).containsOnly(4, 5);
    }
}

