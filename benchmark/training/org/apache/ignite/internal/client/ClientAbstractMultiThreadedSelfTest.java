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
package org.apache.ignite.internal.client;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.ignite.Ignite;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public abstract class ClientAbstractMultiThreadedSelfTest extends GridCommonAbstractTest {
    /**
     * Partitioned cache name.
     */
    protected static final String PARTITIONED_CACHE_NAME = "partitioned";

    /**
     * Partitioned cache with async commit and backup name.
     */
    protected static final String PARTITIONED_ASYNC_BACKUP_CACHE_NAME = "partitioned-async-backup";

    /**
     * Replicated cache name.
     */
    private static final String REPLICATED_CACHE_NAME = "replicated";

    /**
     * Replicated cache  with async commit name.
     */
    private static final String REPLICATED_ASYNC_CACHE_NAME = "replicated-async";

    /**
     * Nodes count.
     */
    protected static final int NODES_CNT = 5;

    /**
     * Thread count to run tests.
     */
    private static final int THREAD_CNT = 20;

    /**
     * Count of tasks to run.
     */
    private static final int TASK_EXECUTION_CNT = 50000;

    /**
     * Count of cache puts in tests.
     */
    private static final int CACHE_PUT_CNT = 10000;

    /**
     * Topology update frequency.
     */
    private static final int TOP_REFRESH_FREQ = 1000;

    /**
     * Info messages will be printed each 5000 iterations.
     */
    private static final int STATISTICS_PRINT_STEP = 5000;

    /**
     * Host.
     */
    public static final String HOST = "127.0.0.1";

    /**
     * Base for tcp rest ports.
     */
    public static final int REST_TCP_PORT_BASE = 12345;

    static {
        System.setProperty("CLIENTS_MODULE_PATH", U.resolveIgnitePath("modules/clients").getAbsolutePath());
    }

    /**
     * Client instance for each test.
     */
    protected GridClient client;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultithreadedTaskRun() throws Exception {
        final AtomicLong cnt = new AtomicLong();
        final AtomicReference<GridClientException> err = new AtomicReference<>();
        final ConcurrentLinkedQueue<String> execQueue = new ConcurrentLinkedQueue<>();
        IgniteInternalFuture<?> fut = multithreadedAsync(new Runnable() {
            @Override
            public void run() {
                long processed;
                while ((processed = cnt.getAndIncrement()) < (taskExecutionCount())) {
                    try {
                        if ((processed > 0) && ((processed % (ClientAbstractMultiThreadedSelfTest.STATISTICS_PRINT_STEP)) == 0))
                            info(((">>>>>>> " + processed) + " tasks finished."));

                        String res = client.compute().execute(ClientAbstractMultiThreadedSelfTest.TestTask.class.getName(), null);
                        execQueue.add(res);
                    } catch (GridClientException e) {
                        err.compareAndSet(null, e);
                    }
                } 
            }
        }, ClientAbstractMultiThreadedSelfTest.THREAD_CNT, "client-task-request");
        fut.get();
        if ((err.get()) != null)
            throw new Exception(err.get());

        assertEquals(taskExecutionCount(), execQueue.size());
        // With round-robin balancer each node must receive equal count of task requests.
        Collection<String> executionIds = new HashSet<>(execQueue);
        assertTrue(((executionIds.size()) == (ClientAbstractMultiThreadedSelfTest.NODES_CNT)));
        Map<String, AtomicInteger> statisticsMap = new HashMap<>();
        for (String id : executionIds)
            statisticsMap.put(id, new AtomicInteger());

        for (String id : execQueue)
            statisticsMap.get(id).incrementAndGet();

        info(">>>>>>> Execution statistics per node:");
        for (Map.Entry<String, AtomicInteger> e : statisticsMap.entrySet())
            info(((((">>>>>>> " + (e.getKey())) + " run ") + (e.getValue().get())) + " tasks"));

    }

    /**
     * Test task. Returns a tuple in which first component is id of node that has split the task,
     * and second component is count of nodes that executed jobs.
     */
    private static class TestTask extends ComputeTaskSplitAdapter<Object, String> {
        /**
         * Injected grid.
         */
        @IgniteInstanceResource
        private Ignite ignite;

        /**
         * Count of tasks this job was split to.
         */
        private int gridSize;

        /**
         * {@inheritDoc }
         */
        @Override
        protected Collection<? extends ComputeJob> split(int gridSize, Object arg) {
            Collection<ComputeJobAdapter> jobs = new ArrayList<>(gridSize);
            this.gridSize = gridSize;
            final String locNodeId = ignite.cluster().localNode().id().toString();
            for (int i = 0; i < gridSize; i++) {
                jobs.add(new ComputeJobAdapter() {
                    @Override
                    public Object execute() {
                        return new IgniteBiTuple(locNodeId, 1);
                    }
                });
            }
            return jobs;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String reduce(List<ComputeJobResult> results) {
            int sum = 0;
            String locNodeId = null;
            for (ComputeJobResult res : results) {
                IgniteBiTuple<String, Integer> part = res.getData();
                if (locNodeId == null)
                    locNodeId = part.get1();

                Integer i = part.get2();
                if (i != null)
                    sum += i;

            }
            assert (gridSize) == sum;
            return locNodeId;
        }
    }
}

