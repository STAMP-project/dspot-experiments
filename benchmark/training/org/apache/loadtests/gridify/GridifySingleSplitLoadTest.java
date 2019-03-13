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
package org.apache.loadtests.gridify;


import org.apache.ignite.Ignite;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.loadtests.GridLoadTestStatistics;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.ignite.testframework.junits.common.GridCommonTest;
import org.junit.Test;


/**
 * Gridify single split load test.
 */
@GridCommonTest(group = "Load Test")
public class GridifySingleSplitLoadTest extends GridCommonAbstractTest {
    /**
     *
     */
    public GridifySingleSplitLoadTest() {
        super(true);
    }

    /**
     * Load test grid.
     *
     * @throws Exception
     * 		If task execution failed.
     */
    @Test
    public void testGridifyLoad() throws Exception {
        Ignite ignite = G.ignite(getTestIgniteInstanceName());
        ignite.compute().localDeployTask(GridifyLoadTestTask.class, GridifyLoadTestTask.class.getClassLoader());
        final long end = (((getTestDurationInMinutes()) * 60) * 1000) + (System.currentTimeMillis());
        // Warm up.
        new GridifyLoadTestJobTarget().executeLoadTestJob(3);
        info((("Load test will be executed for '" + (getTestDurationInMinutes())) + "' mins."));
        info(("Thread count: " + (getThreadCount())));
        final GridLoadTestStatistics stats = new GridLoadTestStatistics();
        GridTestUtils.runMultiThreaded(new Runnable() {
            @Override
            public void run() {
                while ((end - (System.currentTimeMillis())) > 0) {
                    int levels = 3;
                    int exp = factorial(levels);
                    long start = System.currentTimeMillis();
                    int res = new GridifyLoadTestJobTarget().executeLoadTestJob(exp);
                    if (res != exp)
                        fail((((("Received wrong result [expected=" + exp) + ", actual=") + res) + ']'));

                    long taskCnt = stats.onTaskCompleted(null, exp, ((System.currentTimeMillis()) - start));
                    if ((taskCnt % 500) == 0)
                        info(stats.toString());

                } 
            }
        }, getThreadCount(), "grid-load-test-thread");
        info(("Final test statistics: " + stats));
    }
}

