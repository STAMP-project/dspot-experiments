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
package org.apache.ignite.spi.communication.tcp;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.ignite.IgniteException;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskFuture;
import org.apache.ignite.compute.ComputeTaskSessionFullSupport;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.internal.GridJobExecuteResponse;
import org.apache.ignite.internal.managers.communication.GridIoMessage;
import org.apache.ignite.internal.processors.cache.query.GridCacheQueryResponse;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.plugin.extensions.communication.Message;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 *
 */
public class GridOrderedMessageCancelSelfTest extends GridCommonAbstractTest {
    /**
     * Cancel latch.
     */
    private static CountDownLatch cancelLatch;

    /**
     * Process response latch.
     */
    private static CountDownLatch resLatch;

    /**
     * Finish latch.
     */
    private static CountDownLatch finishLatch;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTask() throws Exception {
        Map map = U.field(context().io(), "msgSetMap");
        int initSize = map.size();
        ComputeTaskFuture<?> fut = executeAsync(compute(grid(0).cluster().forRemotes()), GridOrderedMessageCancelSelfTest.Task.class, null);
        testMessageSet(fut, initSize, map);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTaskException() throws Exception {
        Map map = U.field(context().io(), "msgSetMap");
        int initSize = map.size();
        ComputeTaskFuture<?> fut = executeAsync(compute(grid(0).cluster().forRemotes()), GridOrderedMessageCancelSelfTest.FailTask.class, null);
        testMessageSet(fut, initSize, map);
    }

    /**
     * Communication SPI.
     */
    private static class CommunicationSpi extends TcpCommunicationSpi {
        /**
         * {@inheritDoc }
         */
        @Override
        protected void notifyListener(UUID sndId, Message msg, IgniteRunnable msgC) {
            try {
                GridIoMessage ioMsg = ((GridIoMessage) (msg));
                boolean wait = ((ioMsg.message()) instanceof GridCacheQueryResponse) || ((ioMsg.message()) instanceof GridJobExecuteResponse);
                if (wait) {
                    GridOrderedMessageCancelSelfTest.cancelLatch.countDown();
                    assertTrue(U.await(GridOrderedMessageCancelSelfTest.resLatch, 5000, TimeUnit.MILLISECONDS));
                }
                super.notifyListener(sndId, msg, msgC);
                if (wait)
                    GridOrderedMessageCancelSelfTest.finishLatch.countDown();

            } catch (Exception e) {
                fail(("Unexpected error: " + e));
            }
        }
    }

    /**
     * Test task.
     */
    @ComputeTaskSessionFullSupport
    private static class Task extends ComputeTaskSplitAdapter<Void, Void> {
        /**
         * {@inheritDoc }
         */
        @Override
        protected Collection<? extends ComputeJob> split(int gridSize, Void arg) {
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
        public Void reduce(List<ComputeJobResult> results) {
            return null;
        }
    }

    /**
     * Test task.
     */
    @ComputeTaskSessionFullSupport
    private static class FailTask extends ComputeTaskSplitAdapter<Void, Void> {
        /**
         * {@inheritDoc }
         */
        @Override
        protected Collection<? extends ComputeJob> split(int gridSize, Void arg) {
            return Collections.singleton(new ComputeJobAdapter() {
                @Nullable
                @Override
                public Object execute() {
                    throw new IgniteException("Task failed.");
                }
            });
        }

        /**
         * {@inheritDoc }
         */
        @Nullable
        @Override
        public Void reduce(List<ComputeJobResult> results) {
            return null;
        }
    }
}

