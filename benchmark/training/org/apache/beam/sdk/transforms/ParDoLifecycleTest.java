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
package org.apache.beam.sdk.transforms;


import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.UsesParDoLifecycle;
import org.apache.beam.sdk.testing.UsesStatefulParDo;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollectionList;
import org.apache.beam.sdk.values.TupleTagList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests that {@link ParDo} exercises {@link DoFn} methods in the appropriate sequence.
 */
@RunWith(JUnit4.class)
public class ParDoLifecycleTest implements Serializable {
    @Rule
    public final transient TestPipeline p = TestPipeline.create();

    private static class CallSequenceEnforcingDoFn<T> extends DoFn<T, T> {
        private boolean setupCalled = false;

        private int startBundleCalls = 0;

        private int finishBundleCalls = 0;

        private boolean teardownCalled = false;

        @Setup
        public void setup() {
            Assert.assertThat("setup should not be called twice", setupCalled, ParDoLifecycleTest.CallSequenceEnforcingDoFn.is(false));
            Assert.assertThat("setup should be called before startBundle", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingDoFn.equalTo(0));
            Assert.assertThat("setup should be called before finishBundle", finishBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingDoFn.equalTo(0));
            Assert.assertThat("setup should be called before teardown", teardownCalled, ParDoLifecycleTest.CallSequenceEnforcingDoFn.is(false));
            setupCalled = true;
        }

        @StartBundle
        public void startBundle() {
            Assert.assertThat("setup should have been called", setupCalled, ParDoLifecycleTest.CallSequenceEnforcingDoFn.is(true));
            Assert.assertThat("Even number of startBundle and finishBundle calls in startBundle", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingDoFn.equalTo(finishBundleCalls));
            Assert.assertThat("teardown should not have been called", teardownCalled, ParDoLifecycleTest.CallSequenceEnforcingDoFn.is(false));
            (startBundleCalls)++;
        }

        @ProcessElement
        public void processElement(ProcessContext c) throws Exception {
            Assert.assertThat("startBundle should have been called", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingDoFn.greaterThan(0));
            Assert.assertThat("there should be one startBundle call with no call to finishBundle", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingDoFn.equalTo(((finishBundleCalls) + 1)));
            Assert.assertThat("teardown should not have been called", teardownCalled, ParDoLifecycleTest.CallSequenceEnforcingDoFn.is(false));
        }

        @FinishBundle
        public void finishBundle() {
            Assert.assertThat("startBundle should have been called", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingDoFn.greaterThan(0));
            Assert.assertThat("there should be one bundle that has been started but not finished", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingDoFn.equalTo(((finishBundleCalls) + 1)));
            Assert.assertThat("teardown should not have been called", teardownCalled, ParDoLifecycleTest.CallSequenceEnforcingDoFn.is(false));
            (finishBundleCalls)++;
        }

        @Teardown
        public void teardown() {
            Assert.assertThat(setupCalled, ParDoLifecycleTest.CallSequenceEnforcingDoFn.is(true));
            Assert.assertThat(startBundleCalls, Matchers.anyOf(ParDoLifecycleTest.CallSequenceEnforcingDoFn.equalTo(finishBundleCalls)));
            Assert.assertThat(teardownCalled, ParDoLifecycleTest.CallSequenceEnforcingDoFn.is(false));
            teardownCalled = true;
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesParDoLifecycle.class })
    public void testFnCallSequence() {
        PCollectionList.of(p.apply("Impolite", Create.of(1, 2, 4))).and(p.apply("Polite", Create.of(3, 5, 6, 7))).apply(Flatten.pCollections()).apply(ParDo.of(new ParDoLifecycleTest.CallSequenceEnforcingFn()));
        p.run();
    }

    @Test
    @Category({ ValidatesRunner.class, UsesParDoLifecycle.class })
    public void testFnCallSequenceMulti() {
        PCollectionList.of(p.apply("Impolite", Create.of(1, 2, 4))).and(p.apply("Polite", Create.of(3, 5, 6, 7))).apply(Flatten.pCollections()).apply(ParDo.of(new ParDoLifecycleTest.CallSequenceEnforcingFn<Integer>()).withOutputTags(new org.apache.beam.sdk.values.TupleTag<Integer>() {}, TupleTagList.empty()));
        p.run();
    }

    @Test
    @Category({ ValidatesRunner.class, UsesStatefulParDo.class, UsesParDoLifecycle.class })
    public void testFnCallSequenceStateful() {
        PCollectionList.of(p.apply("Impolite", Create.of(KV.of("a", 1), KV.of("b", 2), KV.of("a", 4)))).and(p.apply("Polite", Create.of(KV.of("b", 3), KV.of("a", 5), KV.of("c", 6), KV.of("c", 7)))).apply(Flatten.pCollections()).apply(ParDo.of(new ParDoLifecycleTest.CallSequenceEnforcingStatefulFn<String, Integer>()).withOutputTags(new org.apache.beam.sdk.values.TupleTag<KV<String, Integer>>() {}, TupleTagList.empty()));
        p.run();
    }

    private static class CallSequenceEnforcingFn<T> extends DoFn<T, T> {
        private boolean setupCalled = false;

        private int startBundleCalls = 0;

        private int finishBundleCalls = 0;

        private boolean teardownCalled = false;

        @Setup
        public void before() {
            Assert.assertThat("setup should not be called twice", setupCalled, ParDoLifecycleTest.CallSequenceEnforcingFn.is(false));
            Assert.assertThat("setup should be called before startBundle", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingFn.equalTo(0));
            Assert.assertThat("setup should be called before finishBundle", finishBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingFn.equalTo(0));
            Assert.assertThat("setup should be called before teardown", teardownCalled, ParDoLifecycleTest.CallSequenceEnforcingFn.is(false));
            setupCalled = true;
        }

        @StartBundle
        public void begin() {
            Assert.assertThat("setup should have been called", setupCalled, ParDoLifecycleTest.CallSequenceEnforcingFn.is(true));
            Assert.assertThat("Even number of startBundle and finishBundle calls in startBundle", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingFn.equalTo(finishBundleCalls));
            Assert.assertThat("teardown should not have been called", teardownCalled, ParDoLifecycleTest.CallSequenceEnforcingFn.is(false));
            (startBundleCalls)++;
        }

        @ProcessElement
        public void process(ProcessContext c) throws Exception {
            Assert.assertThat("startBundle should have been called", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingFn.greaterThan(0));
            Assert.assertThat("there should be one startBundle call with no call to finishBundle", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingFn.equalTo(((finishBundleCalls) + 1)));
            Assert.assertThat("teardown should not have been called", teardownCalled, ParDoLifecycleTest.CallSequenceEnforcingFn.is(false));
        }

        @FinishBundle
        public void end() {
            Assert.assertThat("startBundle should have been called", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingFn.greaterThan(0));
            Assert.assertThat("there should be one bundle that has been started but not finished", startBundleCalls, ParDoLifecycleTest.CallSequenceEnforcingFn.equalTo(((finishBundleCalls) + 1)));
            Assert.assertThat("teardown should not have been called", teardownCalled, ParDoLifecycleTest.CallSequenceEnforcingFn.is(false));
            (finishBundleCalls)++;
        }

        @Teardown
        public void after() {
            Assert.assertThat(setupCalled, ParDoLifecycleTest.CallSequenceEnforcingFn.is(true));
            Assert.assertThat(startBundleCalls, Matchers.anyOf(ParDoLifecycleTest.CallSequenceEnforcingFn.equalTo(finishBundleCalls)));
            Assert.assertThat(teardownCalled, ParDoLifecycleTest.CallSequenceEnforcingFn.is(false));
            teardownCalled = true;
        }
    }

    private static class CallSequenceEnforcingStatefulFn<K, V> extends ParDoLifecycleTest.CallSequenceEnforcingDoFn<KV<K, V>> {
        private static final String STATE_ID = "foo";

        @StateId(ParDoLifecycleTest.CallSequenceEnforcingStatefulFn.STATE_ID)
        private final StateSpec<ValueState<String>> valueSpec = StateSpecs.value();
    }

    @Test
    @Category({ ValidatesRunner.class, UsesParDoLifecycle.class })
    public void testTeardownCalledAfterExceptionInStartBundle() {
        ParDoLifecycleTest.ExceptionThrowingOldFn fn = new ParDoLifecycleTest.ExceptionThrowingOldFn(ParDoLifecycleTest.MethodForException.START_BUNDLE);
        p.apply(Create.of(1, 2, 3)).apply(ParDo.of(fn));
        try {
            p.run();
            Assert.fail("Pipeline should have failed with an exception");
        } catch (Exception e) {
            Assert.assertThat("Function should have been torn down after exception", ParDoLifecycleTest.ExceptionThrowingOldFn.teardownCalled.get(), Matchers.is(true));
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesParDoLifecycle.class })
    public void testTeardownCalledAfterExceptionInProcessElement() {
        ParDoLifecycleTest.ExceptionThrowingOldFn fn = new ParDoLifecycleTest.ExceptionThrowingOldFn(ParDoLifecycleTest.MethodForException.PROCESS_ELEMENT);
        p.apply(Create.of(1, 2, 3)).apply(ParDo.of(fn));
        try {
            p.run();
            Assert.fail("Pipeline should have failed with an exception");
        } catch (Exception e) {
            Assert.assertThat("Function should have been torn down after exception", ParDoLifecycleTest.ExceptionThrowingOldFn.teardownCalled.get(), Matchers.is(true));
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesParDoLifecycle.class })
    public void testTeardownCalledAfterExceptionInFinishBundle() {
        ParDoLifecycleTest.ExceptionThrowingOldFn fn = new ParDoLifecycleTest.ExceptionThrowingOldFn(ParDoLifecycleTest.MethodForException.FINISH_BUNDLE);
        p.apply(Create.of(1, 2, 3)).apply(ParDo.of(fn));
        try {
            p.run();
            Assert.fail("Pipeline should have failed with an exception");
        } catch (Exception e) {
            Assert.assertThat("Function should have been torn down after exception", ParDoLifecycleTest.ExceptionThrowingOldFn.teardownCalled.get(), Matchers.is(true));
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesParDoLifecycle.class })
    public void testWithContextTeardownCalledAfterExceptionInSetup() {
        ParDoLifecycleTest.ExceptionThrowingOldFn fn = new ParDoLifecycleTest.ExceptionThrowingOldFn(ParDoLifecycleTest.MethodForException.SETUP);
        p.apply(Create.of(1, 2, 3)).apply(ParDo.of(fn));
        try {
            p.run();
            Assert.fail("Pipeline should have failed with an exception");
        } catch (Exception e) {
            Assert.assertThat("Function should have been torn down after exception", ParDoLifecycleTest.ExceptionThrowingOldFn.teardownCalled.get(), Matchers.is(true));
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesParDoLifecycle.class })
    public void testWithContextTeardownCalledAfterExceptionInStartBundle() {
        ParDoLifecycleTest.ExceptionThrowingOldFn fn = new ParDoLifecycleTest.ExceptionThrowingOldFn(ParDoLifecycleTest.MethodForException.START_BUNDLE);
        p.apply(Create.of(1, 2, 3)).apply(ParDo.of(fn));
        try {
            p.run();
            Assert.fail("Pipeline should have failed with an exception");
        } catch (Exception e) {
            Assert.assertThat("Function should have been torn down after exception", ParDoLifecycleTest.ExceptionThrowingOldFn.teardownCalled.get(), Matchers.is(true));
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesParDoLifecycle.class })
    public void testWithContextTeardownCalledAfterExceptionInProcessElement() {
        ParDoLifecycleTest.ExceptionThrowingOldFn fn = new ParDoLifecycleTest.ExceptionThrowingOldFn(ParDoLifecycleTest.MethodForException.PROCESS_ELEMENT);
        p.apply(Create.of(1, 2, 3)).apply(ParDo.of(fn));
        try {
            p.run();
            Assert.fail("Pipeline should have failed with an exception");
        } catch (Exception e) {
            Assert.assertThat("Function should have been torn down after exception", ParDoLifecycleTest.ExceptionThrowingOldFn.teardownCalled.get(), Matchers.is(true));
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesParDoLifecycle.class })
    public void testWithContextTeardownCalledAfterExceptionInFinishBundle() {
        ParDoLifecycleTest.ExceptionThrowingOldFn fn = new ParDoLifecycleTest.ExceptionThrowingOldFn(ParDoLifecycleTest.MethodForException.FINISH_BUNDLE);
        p.apply(Create.of(1, 2, 3)).apply(ParDo.of(fn));
        try {
            p.run();
            Assert.fail("Pipeline should have failed with an exception");
        } catch (Exception e) {
            Assert.assertThat("Function should have been torn down after exception", ParDoLifecycleTest.ExceptionThrowingOldFn.teardownCalled.get(), Matchers.is(true));
        }
    }

    private static class ExceptionThrowingOldFn extends DoFn<Object, Object> {
        static AtomicBoolean teardownCalled = new AtomicBoolean(false);

        private final ParDoLifecycleTest.MethodForException toThrow;

        private boolean thrown;

        private ExceptionThrowingOldFn(ParDoLifecycleTest.MethodForException toThrow) {
            this.toThrow = toThrow;
        }

        @Setup
        public void setup() throws Exception {
            throwIfNecessary(ParDoLifecycleTest.MethodForException.SETUP);
        }

        @StartBundle
        public void startBundle() throws Exception {
            throwIfNecessary(ParDoLifecycleTest.MethodForException.START_BUNDLE);
        }

        @ProcessElement
        public void processElement(ProcessContext c) throws Exception {
            throwIfNecessary(ParDoLifecycleTest.MethodForException.PROCESS_ELEMENT);
        }

        @FinishBundle
        public void finishBundle() throws Exception {
            throwIfNecessary(ParDoLifecycleTest.MethodForException.FINISH_BUNDLE);
        }

        private void throwIfNecessary(ParDoLifecycleTest.MethodForException method) throws Exception {
            if (((toThrow) == method) && (!(thrown))) {
                thrown = true;
                throw new Exception("Hasn't yet thrown");
            }
        }

        @Teardown
        public void teardown() {
            if (!(thrown)) {
                Assert.fail("Excepted to have a processing method throw an exception");
            }
            ParDoLifecycleTest.ExceptionThrowingOldFn.teardownCalled.set(true);
        }
    }

    private static class ExceptionThrowingFn extends DoFn<Object, Object> {
        static AtomicBoolean teardownCalled = new AtomicBoolean(false);

        private final ParDoLifecycleTest.MethodForException toThrow;

        private boolean thrown;

        private ExceptionThrowingFn(ParDoLifecycleTest.MethodForException toThrow) {
            this.toThrow = toThrow;
        }

        @Setup
        public void before() throws Exception {
            throwIfNecessary(ParDoLifecycleTest.MethodForException.SETUP);
        }

        @StartBundle
        public void preBundle() throws Exception {
            throwIfNecessary(ParDoLifecycleTest.MethodForException.START_BUNDLE);
        }

        @ProcessElement
        public void perElement(ProcessContext c) throws Exception {
            throwIfNecessary(ParDoLifecycleTest.MethodForException.PROCESS_ELEMENT);
        }

        @FinishBundle
        public void postBundle() throws Exception {
            throwIfNecessary(ParDoLifecycleTest.MethodForException.FINISH_BUNDLE);
        }

        private void throwIfNecessary(ParDoLifecycleTest.MethodForException method) throws Exception {
            if (((toThrow) == method) && (!(thrown))) {
                thrown = true;
                throw new Exception("Hasn't yet thrown");
            }
        }

        @Teardown
        public void after() {
            if (!(thrown)) {
                Assert.fail("Excepted to have a processing method throw an exception");
            }
            ParDoLifecycleTest.ExceptionThrowingFn.teardownCalled.set(true);
        }
    }

    private enum MethodForException {

        SETUP,
        START_BUNDLE,
        PROCESS_ELEMENT,
        FINISH_BUNDLE;}
}

