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
package org.apache.ignite.internal.client.integration;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.internal.client.balancer.GridClientRandomBalancer;
import org.apache.ignite.internal.client.balancer.GridClientRoundRobinBalancer;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class ClientPreferDirectSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int NODES_CNT = 6;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRandomBalancer() throws Exception {
        GridClientRandomBalancer b = new GridClientRandomBalancer();
        b.setPreferDirectNodes(true);
        executeTest(b);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRoundRobinBalancer() throws Exception {
        GridClientRoundRobinBalancer b = new GridClientRoundRobinBalancer();
        b.setPreferDirectNodes(true);
        executeTest(b);
    }

    /**
     * Test task. Returns Id of the node that has split the task,
     */
    private static class TestTask extends ComputeTaskSplitAdapter<Object, String> {
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
            for (int i = 0; i < gridSize; i++) {
                jobs.add(new ComputeJobAdapter() {
                    @Override
                    public Object execute() {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ignored) {
                            Thread.currentThread().interrupt();
                        }
                        return "OK";
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
            for (ComputeJobResult res : results) {
                assertNotNull(res.getData());
                sum += 1;
            }
            assert (gridSize) == sum;
            return ignite.cluster().localNode().id().toString();
        }
    }
}

