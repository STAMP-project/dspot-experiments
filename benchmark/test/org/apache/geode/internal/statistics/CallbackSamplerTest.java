/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.statistics;


import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.geode.CancelCriterion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for {@link CallbackSampler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CallbackSamplerTest {
    @Mock
    CancelCriterion cancelCriterion;

    @Mock
    StatSamplerStats statSamplerStats;

    @Mock
    StatisticsManager statisticsManager;

    @Mock
    ScheduledExecutorService executorService;

    private CallbackSampler sampler;

    @Test
    public void taskShouldSampleStatistics() {
        Runnable sampleTask = invokeStartAndGetTask();
        StatisticsImpl stats1 = Mockito.mock(StatisticsImpl.class);
        StatisticsImpl stats2 = Mockito.mock(StatisticsImpl.class);
        Mockito.when(stats1.invokeSuppliers()).thenReturn(3);
        Mockito.when(stats2.invokeSuppliers()).thenReturn(2);
        Mockito.when(stats1.getSupplierCount()).thenReturn(7);
        Mockito.when(stats2.getSupplierCount()).thenReturn(8);
        Mockito.when(statisticsManager.getStatsList()).thenReturn(Arrays.asList(stats1, stats2));
        sampleTask.run();
        Mockito.verify(statSamplerStats).setSampleCallbacks(ArgumentMatchers.eq(15));
        Mockito.verify(statSamplerStats).incSampleCallbackErrors(5);
        Mockito.verify(statSamplerStats).incSampleCallbackDuration(ArgumentMatchers.anyLong());
    }

    @Test
    public void stopShouldStopExecutor() {
        sampler.start(executorService, statisticsManager, 1, TimeUnit.MILLISECONDS);
        sampler.stop();
        Mockito.verify(executorService).shutdown();
    }

    @Test
    public void cancelCriterionShouldStopExecutor() {
        Runnable sampleTask = invokeStartAndGetTask();
        Mockito.when(cancelCriterion.isCancelInProgress()).thenReturn(Boolean.TRUE);
        sampleTask.run();
        Mockito.verify(executorService).shutdown();
    }
}

