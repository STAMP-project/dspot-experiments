/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.micrometer.spi;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.internal.TimedExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import org.apache.camel.spi.ThreadPoolFactory;
import org.apache.camel.spi.ThreadPoolProfile;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class InstrumentedThreadPoolFactoryTest {
    private static final String METRICS_NAME = "metrics.name.";

    @Mock
    private MeterRegistry registry;

    @Mock
    private ThreadPoolFactory threadPoolFactory;

    @Mock
    private ThreadFactory threadFactory;

    private ThreadPoolProfile profile;

    private InstrumentedThreadPoolFactory instrumentedThreadPoolFactory;

    private InOrder inOrder;

    @Test
    public void testNewCachedThreadPool() {
        final ExecutorService executorService = instrumentedThreadPoolFactory.newCachedThreadPool(threadFactory);
        Assert.assertThat(executorService, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(executorService, Matchers.is(Matchers.instanceOf(TimedExecutorService.class)));
        Tags tags = Tags.of("name", "instrumented-delegate-1");
        inOrder.verify(registry, Mockito.times(1)).timer("executor", tags);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testNewThreadPool() {
        final ExecutorService executorService = instrumentedThreadPoolFactory.newThreadPool(profile, threadFactory);
        Assert.assertThat(executorService, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(executorService, Matchers.is(Matchers.instanceOf(TimedExecutorService.class)));
        Tags tags = Tags.of("name", ((InstrumentedThreadPoolFactoryTest.METRICS_NAME) + "1"));
        inOrder.verify(registry, Mockito.times(1)).timer("executor", tags);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testNewScheduledThreadPool() {
        final ScheduledExecutorService scheduledExecutorService = instrumentedThreadPoolFactory.newScheduledThreadPool(profile, threadFactory);
        Assert.assertThat(scheduledExecutorService, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(scheduledExecutorService, Matchers.is(Matchers.instanceOf(TimedExecutorService.class)));
        Tags tags = Tags.of("name", ((InstrumentedThreadPoolFactoryTest.METRICS_NAME) + "1"));
        inOrder.verify(registry, Mockito.times(1)).timer("executor", tags);
        inOrder.verifyNoMoreInteractions();
    }
}

