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
package org.apache.hadoop.yarn.sls.scheduler;


import TaskRunner.Task;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class TestTaskRunner {
    private TaskRunner runner;

    public static class SingleTask extends TaskRunner.Task {
        public static CountDownLatch latch = new CountDownLatch(1);

        public static boolean first;

        public SingleTask(long startTime) {
            init(startTime);
        }

        @Override
        public void firstStep() {
            if (TestTaskRunner.SingleTask.first) {
                Assert.fail();
            }
            TestTaskRunner.SingleTask.first = true;
            TestTaskRunner.SingleTask.latch.countDown();
        }

        @Override
        public void middleStep() {
            Assert.fail();
        }

        @Override
        public void lastStep() {
            Assert.fail();
        }
    }

    @Test
    public void testSingleTask() throws Exception {
        runner.start();
        runner.schedule(new TestTaskRunner.SingleTask(0));
        TestTaskRunner.SingleTask.latch.await(5000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(TestTaskRunner.SingleTask.first);
    }

    public static class DualTask extends TaskRunner.Task {
        public static CountDownLatch latch = new CountDownLatch(1);

        public static boolean first;

        public static boolean last;

        public DualTask(long startTime, long endTime, long interval) {
            super.init(startTime, endTime, interval);
        }

        @Override
        public void firstStep() {
            if (TestTaskRunner.DualTask.first) {
                Assert.fail();
            }
            TestTaskRunner.DualTask.first = true;
        }

        @Override
        public void middleStep() {
            Assert.fail();
        }

        @Override
        public void lastStep() {
            if (TestTaskRunner.DualTask.last) {
                Assert.fail();
            }
            TestTaskRunner.DualTask.last = true;
            TestTaskRunner.DualTask.latch.countDown();
        }
    }

    @Test
    public void testDualTask() throws Exception {
        runner.start();
        runner.schedule(new TestTaskRunner.DualTask(0, 10, 10));
        TestTaskRunner.DualTask.latch.await(5000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(TestTaskRunner.DualTask.first);
        Assert.assertTrue(TestTaskRunner.DualTask.last);
    }

    public static class TriTask extends TaskRunner.Task {
        public static CountDownLatch latch = new CountDownLatch(1);

        public static boolean first;

        public static boolean middle;

        public static boolean last;

        public TriTask(long startTime, long endTime, long interval) {
            super.init(startTime, endTime, interval);
        }

        @Override
        public void firstStep() {
            if (TestTaskRunner.TriTask.first) {
                Assert.fail();
            }
            TestTaskRunner.TriTask.first = true;
        }

        @Override
        public void middleStep() {
            if (TestTaskRunner.TriTask.middle) {
                Assert.fail();
            }
            TestTaskRunner.TriTask.middle = true;
        }

        @Override
        public void lastStep() {
            if (TestTaskRunner.TriTask.last) {
                Assert.fail();
            }
            TestTaskRunner.TriTask.last = true;
            TestTaskRunner.TriTask.latch.countDown();
        }
    }

    @Test
    public void testTriTask() throws Exception {
        runner.start();
        runner.schedule(new TestTaskRunner.TriTask(0, 10, 5));
        TestTaskRunner.TriTask.latch.await(5000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(TestTaskRunner.TriTask.first);
        Assert.assertTrue(TestTaskRunner.TriTask.middle);
        Assert.assertTrue(TestTaskRunner.TriTask.last);
    }

    public static class MultiTask extends TaskRunner.Task {
        public static CountDownLatch latch = new CountDownLatch(1);

        public static boolean first;

        public static int middle;

        public static boolean last;

        public MultiTask(long startTime, long endTime, long interval) {
            super.init(startTime, endTime, interval);
        }

        @Override
        public void firstStep() {
            if (TestTaskRunner.MultiTask.first) {
                Assert.fail();
            }
            TestTaskRunner.MultiTask.first = true;
        }

        @Override
        public void middleStep() {
            (TestTaskRunner.MultiTask.middle)++;
        }

        @Override
        public void lastStep() {
            if (TestTaskRunner.MultiTask.last) {
                Assert.fail();
            }
            TestTaskRunner.MultiTask.last = true;
            TestTaskRunner.MultiTask.latch.countDown();
        }
    }

    @Test
    public void testMultiTask() throws Exception {
        runner.start();
        runner.schedule(new TestTaskRunner.MultiTask(0, 20, 5));
        TestTaskRunner.MultiTask.latch.await(5000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(TestTaskRunner.MultiTask.first);
        Assert.assertEquals(((((20 - 0) / 5) - 2) + 1), TestTaskRunner.MultiTask.middle);
        Assert.assertTrue(TestTaskRunner.MultiTask.last);
    }

    public static class PreStartTask extends TaskRunner.Task {
        public static CountDownLatch latch = new CountDownLatch(1);

        public static boolean first;

        public PreStartTask(long startTime) {
            init(startTime);
        }

        @Override
        public void firstStep() {
            if (TestTaskRunner.PreStartTask.first) {
                Assert.fail();
            }
            TestTaskRunner.PreStartTask.first = true;
            TestTaskRunner.PreStartTask.latch.countDown();
        }

        @Override
        public void middleStep() {
        }

        @Override
        public void lastStep() {
        }
    }

    @Test
    public void testPreStartQueueing() throws Exception {
        runner.schedule(new TestTaskRunner.PreStartTask(210));
        Thread.sleep(210);
        runner.start();
        long startedAt = System.currentTimeMillis();
        TestTaskRunner.PreStartTask.latch.await(5000, TimeUnit.MILLISECONDS);
        long runAt = System.currentTimeMillis();
        Assert.assertTrue(TestTaskRunner.PreStartTask.first);
        Assert.assertTrue(((runAt - startedAt) >= 200));
    }
}

