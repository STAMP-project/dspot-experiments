/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.scheduler;


import Config.TOPOLOGY_WORKER_CHILDOPTS;
import Config.TOPOLOGY_WORKER_GC_CHILDOPTS;
import Config.TOPOLOGY_WORKER_LOGWRITER_CHILDOPTS;
import Config.WORKER_CHILDOPTS;
import Config.WORKER_GC_CHILDOPTS;
import Config.WORKER_HEAP_MEMORY_MB;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link Cluster}.
 */
public class ClusterTest {
    /**
     * This should match the value in Cluster.getAssignedMemoryForSlot.
     */
    final double TOPOLOGY_WORKER_DEFAULT_MEMORY_ALLOCATION = 768.0;

    @Test
    public void getAssignedMemoryForSlot_allNull() {
        Map<String, Object> topConf = getEmptyConfig();
        Assert.assertEquals(TOPOLOGY_WORKER_DEFAULT_MEMORY_ALLOCATION, Cluster.getAssignedMemoryForSlot(topConf), 0);
    }

    @Test
    public void getAssignedMemoryForSlot_topologyWorkerGcChildopts() {
        singleValueTest(TOPOLOGY_WORKER_GC_CHILDOPTS, "-Xmx128m", 128.0);
    }

    @Test
    public void getAssignedMemoryForSlot_workerGcChildopts() {
        singleValueTest(WORKER_GC_CHILDOPTS, "-Xmx256m", 256.0);
    }

    @Test
    public void getAssignedMemoryForSlot_topologyWorkerChildopts() {
        singleValueTest(TOPOLOGY_WORKER_CHILDOPTS, "-Xmx512m", 512.0);
    }

    @Test
    public void getAssignedMemoryForSlot_workerChildopts() {
        singleValueTest(WORKER_CHILDOPTS, "-Xmx768m", 768.0);
    }

    @Test
    public void getAssignedMemoryForSlot_workerHeapMemoryMb() {
        Map<String, Object> topConf = getConfig(WORKER_HEAP_MEMORY_MB, 1024);
        Assert.assertEquals(1024.0, Cluster.getAssignedMemoryForSlot(topConf), 0);
    }

    @Test
    public void getAssignedMemoryForSlot_topologyWorkerLwChildopts() {
        singleValueTest(TOPOLOGY_WORKER_LOGWRITER_CHILDOPTS, "-Xmx64m", ((TOPOLOGY_WORKER_DEFAULT_MEMORY_ALLOCATION) + 64.0));
    }

    @Test
    public void getAssignedMemoryForSlot_all() {
        Map<String, Object> topConf = getPopulatedConfig();
        Assert.assertEquals((128.0 + 64.0), Cluster.getAssignedMemoryForSlot(topConf), 0);
    }
}

