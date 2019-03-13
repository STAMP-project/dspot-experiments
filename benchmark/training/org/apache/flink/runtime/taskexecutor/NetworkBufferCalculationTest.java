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
package org.apache.flink.runtime.taskexecutor;


import MemoryType.HEAP;
import MemoryType.OFF_HEAP;
import TaskManagerOptions.MANAGED_MEMORY_FRACTION;
import TaskManagerOptions.MANAGED_MEMORY_SIZE;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the network buffer calculation from heap size.
 */
public class NetworkBufferCalculationTest extends TestLogger {
    /**
     * Test for {@link TaskManagerServices#calculateNetworkBufferMemory(TaskManagerServicesConfiguration, long)}
     * using the same (manual) test cases as in {@link TaskManagerServicesTest#calculateHeapSizeMB()}.
     */
    @Test
    public void calculateNetworkBufFromHeapSize() throws Exception {
        TaskManagerServicesConfiguration tmConfig;
        tmConfig = NetworkBufferCalculationTest.getTmConfig(Long.valueOf(MANAGED_MEMORY_SIZE.defaultValue()), MANAGED_MEMORY_FRACTION.defaultValue(), 0.1F, (60L << 20), (1L << 30), HEAP);
        /* one too many due to floating point imprecision */
        Assert.assertEquals(((100L << 20) + 1), TaskManagerServices.calculateNetworkBufferMemory(tmConfig, (900L << 20)));// 900MB

        tmConfig = NetworkBufferCalculationTest.getTmConfig(Long.valueOf(MANAGED_MEMORY_SIZE.defaultValue()), MANAGED_MEMORY_FRACTION.defaultValue(), 0.2F, (60L << 20), (1L << 30), HEAP);
        /* slightly too many due to floating point imprecision */
        Assert.assertEquals(((200L << 20) + 3), TaskManagerServices.calculateNetworkBufferMemory(tmConfig, (800L << 20)));// 800MB

        tmConfig = NetworkBufferCalculationTest.getTmConfig(10, MANAGED_MEMORY_FRACTION.defaultValue(), 0.1F, (60L << 20), (1L << 30), OFF_HEAP);
        /* one too many due to floating point imprecision */
        Assert.assertEquals(((100L << 20) + 1), TaskManagerServices.calculateNetworkBufferMemory(tmConfig, (890L << 20)));// 890MB

        tmConfig = NetworkBufferCalculationTest.getTmConfig((-1), 0.1F, 0.1F, (60L << 20), (1L << 30), OFF_HEAP);
        /* one too many due to floating point imprecision */
        Assert.assertEquals(((100L << 20) + 1), TaskManagerServices.calculateNetworkBufferMemory(tmConfig, (810L << 20)));// 810MB

    }
}

