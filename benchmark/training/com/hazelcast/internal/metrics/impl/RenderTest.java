/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.metrics.impl;


import ProbeLevel.MANDATORY;
import com.hazelcast.internal.metrics.renderers.ProbeRenderer;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RenderTest {
    private MetricsRegistryImpl metricsRegistry;

    @Test(expected = NullPointerException.class)
    public void whenCalledWithNullRenderer() {
        metricsRegistry.render(null);
    }

    @Test
    public void whenLongProbeFunctions() {
        ProbeRenderer renderer = Mockito.mock(ProbeRenderer.class);
        registerLongMetric("foo", 10);
        registerLongMetric("bar", 20);
        metricsRegistry.render(renderer);
        Mockito.verify(renderer).renderLong("foo", 10);
        Mockito.verify(renderer).renderLong("bar", 20);
        Mockito.verifyNoMoreInteractions(renderer);
    }

    @Test
    public void whenDoubleProbeFunctions() {
        ProbeRenderer renderer = Mockito.mock(ProbeRenderer.class);
        registerDoubleMetric("foo", 10);
        registerDoubleMetric("bar", 20);
        metricsRegistry.render(renderer);
        Mockito.verify(renderer).renderDouble("foo", 10);
        Mockito.verify(renderer).renderDouble("bar", 20);
        Mockito.verifyNoMoreInteractions(renderer);
    }

    @Test
    public void whenException() {
        ProbeRenderer renderer = Mockito.mock(ProbeRenderer.class);
        final ExpectedRuntimeException ex = new ExpectedRuntimeException();
        metricsRegistry.register(this, "foo", MANDATORY, new com.hazelcast.internal.metrics.LongProbeFunction<RenderTest>() {
            @Override
            public long get(RenderTest source) throws Exception {
                throw ex;
            }
        });
        metricsRegistry.render(renderer);
        Mockito.verify(renderer).renderException("foo", ex);
        Mockito.verifyNoMoreInteractions(renderer);
    }

    @Test
    public void getSortedProbes_whenProbeAdded() {
        List<ProbeInstance> instances1 = metricsRegistry.getSortedProbeInstances();
        registerLongMetric("foo", 10);
        List<ProbeInstance> instances2 = metricsRegistry.getSortedProbeInstances();
        Assert.assertNotSame(instances1, instances2);
    }

    @Test
    public void getSortedProbes_whenNoChange() {
        List<ProbeInstance> instances1 = metricsRegistry.getSortedProbeInstances();
        List<ProbeInstance> instances2 = metricsRegistry.getSortedProbeInstances();
        Assert.assertSame(instances1, instances2);
    }
}

