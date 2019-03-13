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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;


import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test SchedulingRequest With Asynchronous Scheduling.
 */
@RunWith(Parameterized.class)
public class TestSchedulingRequestContainerAllocationAsync {
    private final int GB = 1024;

    private YarnConfiguration conf;

    private String placementConstraintHandler;

    RMNodeLabelsManager mgr;

    public TestSchedulingRequestContainerAllocationAsync(String placementConstraintHandler) {
        this.placementConstraintHandler = placementConstraintHandler;
    }

    @Test(timeout = 300000)
    public void testSingleThreadAsyncContainerAllocation() throws Exception {
        testIntraAppAntiAffinityAsync(1);
    }

    @Test(timeout = 300000)
    public void testTwoThreadsAsyncContainerAllocation() throws Exception {
        testIntraAppAntiAffinityAsync(2);
    }

    @Test(timeout = 300000)
    public void testThreeThreadsAsyncContainerAllocation() throws Exception {
        testIntraAppAntiAffinityAsync(3);
    }

    @Test(timeout = 300000)
    public void testFourThreadsAsyncContainerAllocation() throws Exception {
        testIntraAppAntiAffinityAsync(4);
    }

    @Test(timeout = 300000)
    public void testFiveThreadsAsyncContainerAllocation() throws Exception {
        testIntraAppAntiAffinityAsync(5);
    }
}

