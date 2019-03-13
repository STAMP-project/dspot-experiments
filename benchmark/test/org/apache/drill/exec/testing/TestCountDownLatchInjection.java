/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.testing;


import UserSession.Builder;
import java.util.concurrent.CountDownLatch;
import org.apache.drill.common.concurrent.ExtendedLatch;
import org.apache.drill.exec.ops.QueryContext;
import org.apache.drill.exec.proto.UserBitShared.QueryId;
import org.apache.drill.exec.proto.UserBitShared.UserCredentials;
import org.apache.drill.exec.proto.UserProtos.UserProperties;
import org.apache.drill.exec.rpc.user.UserSession;
import org.apache.drill.exec.util.Pointer;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;


public class TestCountDownLatchInjection extends BaseTestQuery {
    private static final UserSession session = Builder.newBuilder().withCredentials(UserCredentials.newBuilder().setUserName("foo").build()).withUserProperties(UserProperties.getDefaultInstance()).withOptionManager(BaseTestQuery.bits[0].getContext().getOptionManager()).build();

    /**
     * Class whose methods we want to simulate count down latches at run-time for testing
     * purposes. The class must have access to {@link org.apache.drill.exec.ops.QueryContext} or
     * {@link FragmentContextImpl}.
     */
    private static class DummyClass {
        private static final ControlsInjector injector = ControlsInjectorFactory.getInjector(TestCountDownLatchInjection.DummyClass.class);

        private final QueryContext context;

        private final CountDownLatch latch;

        private final int count;

        public DummyClass(final QueryContext context, final CountDownLatch latch, final int count) {
            this.context = context;
            this.latch = latch;
            this.count = count;
        }

        public static final String LATCH_NAME = "<<latch>>";

        /**
         * Method that initializes and waits for "count" number of count down (from those many threads)
         */
        public long initAndWait() throws InterruptedException {
            // ... code ...
            TestCountDownLatchInjection.DummyClass.injector.getLatch(context.getExecutionControls(), TestCountDownLatchInjection.DummyClass.LATCH_NAME).initialize(count);
            // ... code ...
            latch.countDown();// trigger threads spawn

            final long startTime = System.currentTimeMillis();
            // simulated wait for "count" threads to count down on the same latch
            TestCountDownLatchInjection.DummyClass.injector.getLatch(context.getExecutionControls(), TestCountDownLatchInjection.DummyClass.LATCH_NAME).await();
            final long endTime = System.currentTimeMillis();
            // ... code ...
            return endTime - startTime;
        }

        public void countDown() {
            // ... code ...
            TestCountDownLatchInjection.DummyClass.injector.getLatch(context.getExecutionControls(), TestCountDownLatchInjection.DummyClass.LATCH_NAME).countDown();
            // ... code ...
        }
    }

    private static class ThreadCreator extends Thread {
        private final TestCountDownLatchInjection.DummyClass dummyClass;

        private final ExtendedLatch latch;

        private final int count;

        private final Pointer<Long> countingDownTime;

        public ThreadCreator(final TestCountDownLatchInjection.DummyClass dummyClass, final ExtendedLatch latch, final int count, final Pointer<Long> countingDownTime) {
            this.dummyClass = dummyClass;
            this.latch = latch;
            this.count = count;
            this.countingDownTime = countingDownTime;
        }

        @Override
        public void run() {
            latch.awaitUninterruptibly();
            final long startTime = System.currentTimeMillis();
            for (int i = 0; i < (count); i++) {
                new Thread() {
                    @Override
                    public void run() {
                        dummyClass.countDown();
                    }
                }.start();
            }
            final long endTime = System.currentTimeMillis();
            countingDownTime.value = endTime - startTime;
        }
    }

    // test would hang if the correct init, wait and countdowns did not happen, and the test timeout mechanism will
    // catch that case
    @Test
    public void latchInjected() throws InterruptedException {
        final int threads = 10;
        final ExtendedLatch trigger = new ExtendedLatch(1);
        final Pointer<Long> countingDownTime = new Pointer();
        final String controls = Controls.newBuilder().addLatch(TestCountDownLatchInjection.DummyClass.class, TestCountDownLatchInjection.DummyClass.LATCH_NAME).build();
        ControlsInjectionUtil.setControls(TestCountDownLatchInjection.session, controls);
        final QueryContext queryContext = new QueryContext(TestCountDownLatchInjection.session, BaseTestQuery.bits[0].getContext(), QueryId.getDefaultInstance());
        final TestCountDownLatchInjection.DummyClass dummyClass = new TestCountDownLatchInjection.DummyClass(queryContext, trigger, threads);
        new TestCountDownLatchInjection.ThreadCreator(dummyClass, trigger, threads, countingDownTime).start();
        final long timeSpentWaiting;
        try {
            timeSpentWaiting = dummyClass.initAndWait();
        } catch (final InterruptedException e) {
            Assert.fail("Thread should not be interrupted; there is no deliberate attempt.");
            return;
        }
        while ((countingDownTime.value) == null) {
            Thread.sleep(100L);
        } 
        Assert.assertTrue((timeSpentWaiting >= (countingDownTime.value)));
        try {
            queryContext.close();
        } catch (final Exception e) {
            Assert.fail(("Failed to close query context: " + e));
        }
    }
}

