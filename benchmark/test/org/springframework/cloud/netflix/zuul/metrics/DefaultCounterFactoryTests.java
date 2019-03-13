/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.zuul.metrics;


import com.netflix.zuul.monitoring.CounterFactory;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultCounterFactoryTests {
    private static final String NAME = "my-super-metric-name";

    @Test
    public void shouldIncrement() throws Exception {
        MeterRegistry meterRegistry = Mockito.mock(MeterRegistry.class);
        CounterFactory factory = new DefaultCounterFactory(meterRegistry);
        Counter counter = Mockito.mock(Counter.class);
        Mockito.when(meterRegistry.counter(DefaultCounterFactoryTests.NAME)).thenReturn(counter);
        factory.increment(DefaultCounterFactoryTests.NAME);
        Mockito.verify(counter).increment();
    }
}

