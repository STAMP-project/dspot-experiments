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
package org.apache.beam.runners.direct;


import GlobalWindow.INSTANCE;
import TimeDomain.EVENT_TIME;
import org.apache.beam.runners.core.StateNamespaces;
import org.apache.beam.runners.core.TimerInternals.TimerData;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link DoFnLifecycleManagerRemovingTransformEvaluator}.
 */
@RunWith(JUnit4.class)
public class DoFnLifecycleManagerRemovingTransformEvaluatorTest {
    private DoFnLifecycleManager lifecycleManager;

    @Test
    public void delegatesToUnderlying() throws Exception {
        ParDoEvaluator<Object> underlying = Mockito.mock(ParDoEvaluator.class);
        DoFn<?, ?> original = lifecycleManager.get();
        TransformEvaluator<Object> evaluator = DoFnLifecycleManagerRemovingTransformEvaluator.wrapping(underlying, lifecycleManager);
        WindowedValue<Object> first = WindowedValue.valueInGlobalWindow(new Object());
        WindowedValue<Object> second = WindowedValue.valueInGlobalWindow(new Object());
        evaluator.processElement(first);
        Mockito.verify(underlying).processElement(first);
        evaluator.processElement(second);
        Mockito.verify(underlying).processElement(second);
        evaluator.finishBundle();
        Mockito.verify(underlying).finishBundle();
    }

    @Test
    public void removesOnExceptionInProcessElement() throws Exception {
        ParDoEvaluator<Object> underlying = Mockito.mock(ParDoEvaluator.class);
        Mockito.doThrow(Exception.class).when(underlying).processElement(ArgumentMatchers.any(WindowedValue.class));
        DoFn<?, ?> original = lifecycleManager.get();
        Assert.assertThat(original, Matchers.not(Matchers.nullValue()));
        TransformEvaluator<Object> evaluator = DoFnLifecycleManagerRemovingTransformEvaluator.wrapping(underlying, lifecycleManager);
        try {
            evaluator.processElement(WindowedValue.valueInGlobalWindow(new Object()));
        } catch (Exception e) {
            Assert.assertThat(lifecycleManager.get(), Matchers.not(Matchers.theInstance(original)));
            return;
        }
        Assert.fail("Expected underlying evaluator to throw on method call");
    }

    @Test
    public void removesOnExceptionInOnTimer() throws Exception {
        ParDoEvaluator<Object> underlying = Mockito.mock(ParDoEvaluator.class);
        Mockito.doThrow(Exception.class).when(underlying).onTimer(ArgumentMatchers.any(TimerData.class), ArgumentMatchers.any(BoundedWindow.class));
        DoFn<?, ?> original = lifecycleManager.get();
        Assert.assertThat(original, Matchers.not(Matchers.nullValue()));
        DoFnLifecycleManagerRemovingTransformEvaluator<Object> evaluator = DoFnLifecycleManagerRemovingTransformEvaluator.wrapping(underlying, lifecycleManager);
        try {
            evaluator.onTimer(TimerData.of("foo", StateNamespaces.global(), new Instant(0), EVENT_TIME), INSTANCE);
        } catch (Exception e) {
            Assert.assertThat(lifecycleManager.get(), Matchers.not(Matchers.theInstance(original)));
            return;
        }
        Assert.fail("Expected underlying evaluator to throw on method call");
    }

    @Test
    public void removesOnExceptionInFinishBundle() throws Exception {
        ParDoEvaluator<Object> underlying = Mockito.mock(ParDoEvaluator.class);
        Mockito.doThrow(Exception.class).when(underlying).finishBundle();
        DoFn<?, ?> original = lifecycleManager.get();
        // the LifecycleManager is set when the evaluator starts
        Assert.assertThat(original, Matchers.not(Matchers.nullValue()));
        TransformEvaluator<Object> evaluator = DoFnLifecycleManagerRemovingTransformEvaluator.wrapping(underlying, lifecycleManager);
        try {
            evaluator.finishBundle();
        } catch (Exception e) {
            Assert.assertThat(lifecycleManager.get(), Matchers.not(Matchers.theInstance(original)));
            return;
        }
        Assert.fail("Expected underlying evaluator to throw on method call");
    }

    private static class TestFn extends DoFn<Object, Object> {
        @ProcessElement
        public void processElement(ProcessContext c) throws Exception {
        }
    }
}

