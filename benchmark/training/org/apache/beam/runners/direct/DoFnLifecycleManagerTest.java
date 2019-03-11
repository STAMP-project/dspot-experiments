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


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.util.UserCodeException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DoFnLifecycleManager}.
 */
@RunWith(JUnit4.class)
public class DoFnLifecycleManagerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DoFnLifecycleManagerTest.TestFn fn = new DoFnLifecycleManagerTest.TestFn();

    private DoFnLifecycleManager mgr = DoFnLifecycleManager.of(fn);

    @Test
    public void setupOnGet() throws Exception {
        DoFnLifecycleManagerTest.TestFn obtained = ((DoFnLifecycleManagerTest.TestFn) (mgr.get()));
        Assert.assertThat(obtained, Matchers.not(Matchers.theInstance(fn)));
        Assert.assertThat(obtained.setupCalled, Matchers.is(true));
        Assert.assertThat(obtained.teardownCalled, Matchers.is(false));
    }

    @Test
    public void getMultipleCallsSingleSetupCall() throws Exception {
        DoFnLifecycleManagerTest.TestFn obtained = ((DoFnLifecycleManagerTest.TestFn) (mgr.get()));
        DoFnLifecycleManagerTest.TestFn secondObtained = ((DoFnLifecycleManagerTest.TestFn) (mgr.get()));
        Assert.assertThat(obtained, Matchers.theInstance(secondObtained));
        Assert.assertThat(obtained.setupCalled, Matchers.is(true));
        Assert.assertThat(obtained.teardownCalled, Matchers.is(false));
    }

    @Test
    public void getMultipleThreadsDifferentInstances() throws Exception {
        CountDownLatch startSignal = new CountDownLatch(1);
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<DoFnLifecycleManagerTest.TestFn>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(executor.submit(new DoFnLifecycleManagerTest.GetFnCallable(mgr, startSignal)));
        }
        startSignal.countDown();
        List<DoFnLifecycleManagerTest.TestFn> fns = new ArrayList<>();
        for (Future<DoFnLifecycleManagerTest.TestFn> future : futures) {
            fns.add(future.get(1L, TimeUnit.SECONDS));
        }
        for (DoFnLifecycleManagerTest.TestFn fn : fns) {
            Assert.assertThat(fn.setupCalled, Matchers.is(true));
            int sameInstances = 0;
            for (DoFnLifecycleManagerTest.TestFn otherFn : fns) {
                if (otherFn == fn) {
                    sameInstances++;
                }
            }
            Assert.assertThat(sameInstances, Matchers.equalTo(1));
        }
    }

    @Test
    public void teardownOnRemove() throws Exception {
        DoFnLifecycleManagerTest.TestFn obtained = ((DoFnLifecycleManagerTest.TestFn) (mgr.get()));
        mgr.remove();
        Assert.assertThat(obtained, Matchers.not(Matchers.theInstance(fn)));
        Assert.assertThat(obtained.setupCalled, Matchers.is(true));
        Assert.assertThat(obtained.teardownCalled, Matchers.is(true));
        Assert.assertThat(mgr.get(), Matchers.not(Matchers.<DoFn<?, ?>>theInstance(obtained)));
    }

    @Test
    public void teardownThrowsRemoveThrows() throws Exception {
        DoFnLifecycleManagerTest.TestFn obtained = ((DoFnLifecycleManagerTest.TestFn) (mgr.get()));
        obtained.teardown();
        thrown.expect(UserCodeException.class);
        thrown.expectCause(Matchers.isA(IllegalStateException.class));
        thrown.expectMessage("Cannot call teardown: already torn down");
        mgr.remove();
    }

    @Test
    public void teardownAllOnRemoveAll() throws Exception {
        CountDownLatch startSignal = new CountDownLatch(1);
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<DoFnLifecycleManagerTest.TestFn>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(executor.submit(new DoFnLifecycleManagerTest.GetFnCallable(mgr, startSignal)));
        }
        startSignal.countDown();
        List<DoFnLifecycleManagerTest.TestFn> fns = new ArrayList<>();
        for (Future<DoFnLifecycleManagerTest.TestFn> future : futures) {
            fns.add(future.get(1L, TimeUnit.SECONDS));
        }
        mgr.removeAll();
        for (DoFnLifecycleManagerTest.TestFn fn : fns) {
            Assert.assertThat(fn.setupCalled, Matchers.is(true));
            Assert.assertThat(fn.teardownCalled, Matchers.is(true));
        }
    }

    @Test
    public void removeAndRemoveAllConcurrent() throws Exception {
        CountDownLatch startSignal = new CountDownLatch(1);
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<DoFnLifecycleManagerTest.TestFn>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(executor.submit(new DoFnLifecycleManagerTest.GetFnCallable(mgr, startSignal)));
        }
        startSignal.countDown();
        List<DoFnLifecycleManagerTest.TestFn> fns = new ArrayList<>();
        for (Future<DoFnLifecycleManagerTest.TestFn> future : futures) {
            fns.add(future.get(1L, TimeUnit.SECONDS));
        }
        CountDownLatch removeSignal = new CountDownLatch(1);
        List<Future<Void>> removeFutures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            // These will reuse the threads used in the GetFns
            removeFutures.add(executor.submit(new DoFnLifecycleManagerTest.TeardownFnCallable(mgr, removeSignal)));
        }
        removeSignal.countDown();
        Assert.assertThat(mgr.removeAll(), Matchers.emptyIterable());
        for (Future<Void> removed : removeFutures) {
            // Should not have thrown an exception.
            removed.get();
        }
        for (DoFnLifecycleManagerTest.TestFn fn : fns) {
            Assert.assertThat(fn.setupCalled, Matchers.is(true));
            Assert.assertThat(fn.teardownCalled, Matchers.is(true));
        }
    }

    private static class GetFnCallable implements Callable<DoFnLifecycleManagerTest.TestFn> {
        private final DoFnLifecycleManager mgr;

        private final CountDownLatch startSignal;

        private GetFnCallable(DoFnLifecycleManager mgr, CountDownLatch startSignal) {
            this.mgr = mgr;
            this.startSignal = startSignal;
        }

        @Override
        public DoFnLifecycleManagerTest.TestFn call() throws Exception {
            startSignal.await();
            return ((DoFnLifecycleManagerTest.TestFn) (mgr.get()));
        }
    }

    private static class TeardownFnCallable implements Callable<Void> {
        private final DoFnLifecycleManager mgr;

        private final CountDownLatch startSignal;

        private TeardownFnCallable(DoFnLifecycleManager mgr, CountDownLatch startSignal) {
            this.mgr = mgr;
            this.startSignal = startSignal;
        }

        @Override
        public Void call() throws Exception {
            startSignal.await();
            // Will throw an exception if the TestFn has already been removed from this thread
            mgr.remove();
            return null;
        }
    }

    private static class TestFn extends DoFn<Object, Object> {
        boolean setupCalled = false;

        boolean teardownCalled = false;

        @Setup
        public void setup() {
            checkState((!(setupCalled)), "Cannot call setup: already set up");
            checkState((!(teardownCalled)), "Cannot call setup: already torn down");
            setupCalled = true;
        }

        @ProcessElement
        public void processElement(ProcessContext c) throws Exception {
        }

        @Teardown
        public void teardown() {
            checkState(setupCalled, "Cannot call teardown: not set up");
            checkState((!(teardownCalled)), "Cannot call teardown: already torn down");
            teardownCalled = true;
        }
    }
}

