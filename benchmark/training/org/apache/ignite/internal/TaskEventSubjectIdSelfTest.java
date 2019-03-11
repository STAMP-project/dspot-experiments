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
package org.apache.ignite.internal;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.ignite.IgniteException;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.compute.ComputeTaskTimeoutException;
import org.apache.ignite.events.TaskEvent;
import org.apache.ignite.internal.client.GridClient;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 * Tests for security subject ID in task events.
 */
public class TaskEventSubjectIdSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final Collection<TaskEvent> evts = new ArrayList<>();

    /**
     *
     */
    private static CountDownLatch latch;

    /**
     *
     */
    private static UUID nodeId;

    /**
     *
     */
    private static GridClient client;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleTask() throws Exception {
        TaskEventSubjectIdSelfTest.latch = new CountDownLatch(3);
        grid().compute().execute(new TaskEventSubjectIdSelfTest.SimpleTask(), null);
        assert TaskEventSubjectIdSelfTest.latch.await(1000, TimeUnit.MILLISECONDS);
        assertEquals(3, TaskEventSubjectIdSelfTest.evts.size());
        Iterator<TaskEvent> it = TaskEventSubjectIdSelfTest.evts.iterator();
        assert it.hasNext();
        TaskEvent evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_STARTED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.nodeId, evt.subjectId());
        assert it.hasNext();
        evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_REDUCED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.nodeId, evt.subjectId());
        assert it.hasNext();
        evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_FINISHED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.nodeId, evt.subjectId());
        assert !(it.hasNext());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFailedTask() throws Exception {
        TaskEventSubjectIdSelfTest.latch = new CountDownLatch(2);
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                grid().compute().execute(new TaskEventSubjectIdSelfTest.FailedTask(), null);
                return null;
            }
        }, IgniteException.class, null);
        assert TaskEventSubjectIdSelfTest.latch.await(1000, TimeUnit.MILLISECONDS);
        assertEquals(2, TaskEventSubjectIdSelfTest.evts.size());
        Iterator<TaskEvent> it = TaskEventSubjectIdSelfTest.evts.iterator();
        assert it.hasNext();
        TaskEvent evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_STARTED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.nodeId, evt.subjectId());
        assert it.hasNext();
        evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_FAILED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.nodeId, evt.subjectId());
        assert !(it.hasNext());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTimedOutTask() throws Exception {
        TaskEventSubjectIdSelfTest.latch = new CountDownLatch(2);
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                grid().compute().withTimeout(100).execute(new TaskEventSubjectIdSelfTest.TimedOutTask(), null);
                return null;
            }
        }, ComputeTaskTimeoutException.class, null);
        assert TaskEventSubjectIdSelfTest.latch.await(1000, TimeUnit.MILLISECONDS);
        assertEquals(3, TaskEventSubjectIdSelfTest.evts.size());
        Iterator<TaskEvent> it = TaskEventSubjectIdSelfTest.evts.iterator();
        assert it.hasNext();
        TaskEvent evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_STARTED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.nodeId, evt.subjectId());
        assert it.hasNext();
        evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_TIMEDOUT, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.nodeId, evt.subjectId());
        assert it.hasNext();
        evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_FAILED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.nodeId, evt.subjectId());
        assert !(it.hasNext());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosure() throws Exception {
        TaskEventSubjectIdSelfTest.latch = new CountDownLatch(3);
        grid().compute().run(new IgniteRunnable() {
            @Override
            public void run() {
                // No-op.
            }
        });
        assert TaskEventSubjectIdSelfTest.latch.await(1000, TimeUnit.MILLISECONDS);
        assertEquals(3, TaskEventSubjectIdSelfTest.evts.size());
        Iterator<TaskEvent> it = TaskEventSubjectIdSelfTest.evts.iterator();
        assert it.hasNext();
        TaskEvent evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_STARTED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.nodeId, evt.subjectId());
        assert it.hasNext();
        evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_REDUCED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.nodeId, evt.subjectId());
        assert it.hasNext();
        evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_FINISHED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.nodeId, evt.subjectId());
        assert !(it.hasNext());
    }

    /**
     * Events for class tasks that was started from external clients should contain
     * client subject id instead of the node where it was started. This test checks it.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClient() throws Exception {
        TaskEventSubjectIdSelfTest.latch = new CountDownLatch(3);
        TaskEventSubjectIdSelfTest.client.compute().execute(TaskEventSubjectIdSelfTest.SimpleTask.class.getName(), null);
        assert TaskEventSubjectIdSelfTest.latch.await(1000, TimeUnit.MILLISECONDS);
        assertEquals(3, TaskEventSubjectIdSelfTest.evts.size());
        Iterator<TaskEvent> it = TaskEventSubjectIdSelfTest.evts.iterator();
        assert it.hasNext();
        TaskEvent evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_STARTED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.client.id(), evt.subjectId());
        assert it.hasNext();
        evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_REDUCED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.client.id(), evt.subjectId());
        assert it.hasNext();
        evt = it.next();
        assert evt != null;
        assertEquals(EVT_TASK_FINISHED, evt.type());
        assertEquals(TaskEventSubjectIdSelfTest.client.id(), evt.subjectId());
        assert !(it.hasNext());
    }

    /**
     *
     */
    private static class SimpleTask extends ComputeTaskSplitAdapter<Object, Object> {
        /**
         * {@inheritDoc }
         */
        @Override
        protected Collection<? extends ComputeJob> split(int gridSize, Object arg) {
            return Collections.singleton(new ComputeJobAdapter() {
                @Nullable
                @Override
                public Object execute() {
                    return null;
                }
            });
        }

        /**
         * {@inheritDoc }
         */
        @Nullable
        @Override
        public Object reduce(List<ComputeJobResult> results) {
            return null;
        }
    }

    /**
     *
     */
    private static class FailedTask extends ComputeTaskSplitAdapter<Object, Object> {
        /**
         * {@inheritDoc }
         */
        @Override
        protected Collection<? extends ComputeJob> split(int gridSize, Object arg) {
            return Collections.singleton(new ComputeJobAdapter() {
                @Nullable
                @Override
                public Object execute() {
                    return null;
                }
            });
        }

        /**
         * {@inheritDoc }
         */
        @Nullable
        @Override
        public Object reduce(List<ComputeJobResult> results) {
            throw new IgniteException("Task failed.");
        }
    }

    /**
     *
     */
    private static class TimedOutTask extends ComputeTaskSplitAdapter<Object, Object> {
        /**
         * {@inheritDoc }
         */
        @Override
        protected Collection<? extends ComputeJob> split(int gridSize, Object arg) {
            return Collections.singleton(new ComputeJobAdapter() {
                @Nullable
                @Override
                public Object execute() {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ignored) {
                        // No-op.
                    }
                    return null;
                }
            });
        }

        /**
         * {@inheritDoc }
         */
        @Nullable
        @Override
        public Object reduce(List<ComputeJobResult> results) {
            return null;
        }
    }
}

