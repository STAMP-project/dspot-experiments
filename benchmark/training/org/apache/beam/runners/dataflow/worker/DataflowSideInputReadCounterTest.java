/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.worker;


import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.dataflow.worker.DataflowOperationContext.DataflowExecutionState;
import org.apache.beam.runners.dataflow.worker.counters.Counter;
import org.apache.beam.runners.dataflow.worker.counters.CounterFactory;
import org.apache.beam.runners.dataflow.worker.counters.NameContext;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link DataflowSideInputReadCounter}.
 */
@RunWith(JUnit4.class)
public class DataflowSideInputReadCounterTest {
    @Test
    public void testToStringReturnsWellFormedDescriptionString() {
        DataflowExecutionContext mockedExecutionContext = Mockito.mock(DataflowExecutionContext.class);
        DataflowOperationContext mockedOperationContext = Mockito.mock(DataflowOperationContext.class);
        final int siIndexId = 3;
        ExecutionStateTracker mockedExecutionStateTracker = Mockito.mock(ExecutionStateTracker.class);
        Mockito.when(mockedExecutionContext.getExecutionStateTracker()).thenReturn(mockedExecutionStateTracker);
        Thread mockedThreadObject = Mockito.mock(Thread.class);
        Mockito.when(mockedExecutionStateTracker.getTrackedThread()).thenReturn(mockedThreadObject);
        DataflowSideInputReadCounter testObject = new DataflowSideInputReadCounter(mockedExecutionContext, mockedOperationContext, siIndexId);
        Assert.assertThat(testObject.toString(), Matchers.equalTo("DataflowSideInputReadCounter{sideInputIndex=3, declaringStep=null}"));
    }

    @Test
    public void testAddBytesReadSkipsAddingCountersIfCurrentCounterIsNull() {
        DataflowExecutionContext mockedExecutionContext = Mockito.mock(DataflowExecutionContext.class);
        DataflowOperationContext mockedOperationContext = Mockito.mock(DataflowOperationContext.class);
        final int siIndexId = 3;
        ExecutionStateTracker mockedExecutionStateTracker = Mockito.mock(ExecutionStateTracker.class);
        Mockito.when(mockedExecutionContext.getExecutionStateTracker()).thenReturn(mockedExecutionStateTracker);
        Thread mockedThreadObject = Mockito.mock(Thread.class);
        Mockito.when(mockedExecutionStateTracker.getTrackedThread()).thenReturn(mockedThreadObject);
        DataflowSideInputReadCounter testObject = new DataflowSideInputReadCounter(mockedExecutionContext, mockedOperationContext, siIndexId);
        try {
            testObject.addBytesRead(10);
        } catch (Exception e) {
            Assert.fail(("Supposedly, we tried to add bytes to counter and that shouldn't happen. Ex.: " + (e.toString())));
        }
    }

    @Test
    public void testAddBytesReadUpdatesCounter() {
        DataflowExecutionContext mockedExecutionContext = Mockito.mock(DataflowExecutionContext.class);
        DataflowOperationContext mockedOperationContext = Mockito.mock(DataflowOperationContext.class);
        final int siIndexId = 3;
        ExecutionStateTracker mockedExecutionStateTracker = Mockito.mock(ExecutionStateTracker.class);
        Mockito.when(mockedExecutionContext.getExecutionStateTracker()).thenReturn(mockedExecutionStateTracker);
        Thread mockedThreadObject = Mockito.mock(Thread.class);
        Mockito.when(mockedExecutionStateTracker.getTrackedThread()).thenReturn(mockedThreadObject);
        DataflowExecutionState mockedExecutionState = Mockito.mock(DataflowExecutionState.class);
        Mockito.when(mockedExecutionStateTracker.getCurrentState()).thenReturn(mockedExecutionState);
        NameContext mockedNameContext = Mockito.mock(NameContext.class);
        Mockito.when(mockedExecutionState.getStepName()).thenReturn(mockedNameContext);
        Mockito.when(mockedNameContext.originalName()).thenReturn("DummyName");
        NameContext mockedDeclaringNameContext = Mockito.mock(NameContext.class);
        Mockito.when(mockedOperationContext.nameContext()).thenReturn(mockedDeclaringNameContext);
        Mockito.when(mockedDeclaringNameContext.originalName()).thenReturn("DummyDeclaringName");
        CounterFactory mockedCounterFactory = Mockito.mock(CounterFactory.class);
        Mockito.when(mockedExecutionContext.getCounterFactory()).thenReturn(mockedCounterFactory);
        Counter<Long, Long> mockedCounter = Mockito.mock(Counter.class);
        Mockito.when(mockedCounterFactory.longSum(ArgumentMatchers.any())).thenReturn(mockedCounter);
        Mockito.when(mockedExecutionContext.getExecutionStateRegistry()).thenReturn(Mockito.mock(DataflowExecutionStateRegistry.class));
        DataflowSideInputReadCounter testObject = new DataflowSideInputReadCounter(mockedExecutionContext, mockedOperationContext, siIndexId);
        testObject.addBytesRead(10L);
        Mockito.verify(mockedCounter).addValue(10L);
    }

    @Test
    public void testEnterEntersStateIfCalledFromTrackedThread() {
        DataflowExecutionContext mockedExecutionContext = Mockito.mock(DataflowExecutionContext.class);
        DataflowOperationContext mockedOperationContext = Mockito.mock(DataflowOperationContext.class);
        final int siIndexId = 3;
        ExecutionStateTracker mockedExecutionStateTracker = Mockito.mock(ExecutionStateTracker.class);
        Mockito.when(mockedExecutionContext.getExecutionStateTracker()).thenReturn(mockedExecutionStateTracker);
        Thread mockedThreadObject = Thread.currentThread();
        Mockito.when(mockedExecutionStateTracker.getTrackedThread()).thenReturn(mockedThreadObject);
        DataflowExecutionState mockedExecutionState = Mockito.mock(DataflowExecutionState.class);
        Mockito.when(mockedExecutionStateTracker.getCurrentState()).thenReturn(mockedExecutionState);
        NameContext mockedNameContext = Mockito.mock(NameContext.class);
        Mockito.when(mockedExecutionState.getStepName()).thenReturn(mockedNameContext);
        Mockito.when(mockedNameContext.originalName()).thenReturn("DummyName");
        NameContext mockedDeclaringNameContext = Mockito.mock(NameContext.class);
        Mockito.when(mockedOperationContext.nameContext()).thenReturn(mockedDeclaringNameContext);
        Mockito.when(mockedDeclaringNameContext.originalName()).thenReturn("DummyDeclaringName");
        CounterFactory mockedCounterFactory = Mockito.mock(CounterFactory.class);
        Mockito.when(mockedExecutionContext.getCounterFactory()).thenReturn(mockedCounterFactory);
        Counter<Long, Long> mockedCounter = Mockito.mock(Counter.class);
        Mockito.when(mockedCounterFactory.longSum(ArgumentMatchers.any())).thenReturn(mockedCounter);
        DataflowExecutionStateRegistry mockedExecutionStateRegistry = Mockito.mock(DataflowExecutionStateRegistry.class);
        Mockito.when(mockedExecutionContext.getExecutionStateRegistry()).thenReturn(mockedExecutionStateRegistry);
        DataflowExecutionState mockedCounterExecutionState = Mockito.mock(DataflowExecutionState.class);
        Mockito.when(mockedExecutionStateRegistry.getIOState(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockedCounterExecutionState);
        DataflowSideInputReadCounter testObject = new DataflowSideInputReadCounter(mockedExecutionContext, mockedOperationContext, siIndexId);
        testObject.enter();
        Mockito.verify(mockedExecutionStateTracker).enterState(mockedCounterExecutionState);
    }

    @Test
    public void testEnterDoesntEnterStateIfCalledFromDifferentThread() {
        DataflowExecutionContext mockedExecutionContext = Mockito.mock(DataflowExecutionContext.class);
        DataflowOperationContext mockedOperationContext = Mockito.mock(DataflowOperationContext.class);
        final int siIndexId = 3;
        ExecutionStateTracker mockedExecutionStateTracker = Mockito.mock(ExecutionStateTracker.class);
        Mockito.when(mockedExecutionContext.getExecutionStateTracker()).thenReturn(mockedExecutionStateTracker);
        Thread mockedThreadObject = Mockito.mock(Thread.class);
        Mockito.when(mockedExecutionStateTracker.getTrackedThread()).thenReturn(mockedThreadObject);
        DataflowExecutionState mockedExecutionState = Mockito.mock(DataflowExecutionState.class);
        Mockito.when(mockedExecutionStateTracker.getCurrentState()).thenReturn(mockedExecutionState);
        NameContext mockedNameContext = Mockito.mock(NameContext.class);
        Mockito.when(mockedExecutionState.getStepName()).thenReturn(mockedNameContext);
        Mockito.when(mockedNameContext.originalName()).thenReturn("DummyName");
        NameContext mockedDeclaringNameContext = Mockito.mock(NameContext.class);
        Mockito.when(mockedOperationContext.nameContext()).thenReturn(mockedDeclaringNameContext);
        Mockito.when(mockedDeclaringNameContext.originalName()).thenReturn("DummyDeclaringName");
        CounterFactory mockedCounterFactory = Mockito.mock(CounterFactory.class);
        Mockito.when(mockedExecutionContext.getCounterFactory()).thenReturn(mockedCounterFactory);
        Counter<Long, Long> mockedCounter = Mockito.mock(Counter.class);
        Mockito.when(mockedCounterFactory.longSum(ArgumentMatchers.any())).thenReturn(mockedCounter);
        DataflowExecutionStateRegistry mockedExecutionStateRegistry = Mockito.mock(DataflowExecutionStateRegistry.class);
        Mockito.when(mockedExecutionContext.getExecutionStateRegistry()).thenReturn(mockedExecutionStateRegistry);
        DataflowExecutionState mockedCounterExecutionState = Mockito.mock(DataflowExecutionState.class);
        Mockito.when(mockedExecutionStateRegistry.getIOState(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockedCounterExecutionState);
        DataflowSideInputReadCounter testObject = new DataflowSideInputReadCounter(mockedExecutionContext, mockedOperationContext, siIndexId);
        testObject.enter();
        Mockito.verify(mockedExecutionStateTracker, Mockito.never()).enterState(ArgumentMatchers.any());
    }
}

