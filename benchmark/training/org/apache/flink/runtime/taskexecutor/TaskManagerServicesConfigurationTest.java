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


import TaskManagerOptions.NETWORK_BUFFERS_MEMORY_FRACTION;
import TaskManagerOptions.NETWORK_BUFFERS_MEMORY_MAX;
import TaskManagerOptions.NETWORK_BUFFERS_MEMORY_MIN;
import TaskManagerOptions.NETWORK_NUM_BUFFERS;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link TaskManagerServicesConfiguration}.
 */
public class TaskManagerServicesConfigurationTest extends TestLogger {
    /**
     * Verifies that {@link TaskManagerServicesConfiguration#hasNewNetworkBufConf(Configuration)}
     * returns the correct result for old configurations via
     * {@link TaskManagerOptions#NETWORK_NUM_BUFFERS}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void hasNewNetworkBufConfOld() throws Exception {
        Configuration config = new Configuration();
        config.setInteger(NETWORK_NUM_BUFFERS, 1);
        Assert.assertFalse(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config));
    }

    /**
     * Verifies that {@link TaskManagerServicesConfiguration#hasNewNetworkBufConf(Configuration)}
     * returns the correct result for new configurations via
     * {@link TaskManagerOptions#NETWORK_BUFFERS_MEMORY_FRACTION},
     * {@link TaskManagerOptions#NETWORK_BUFFERS_MEMORY_MIN} and {@link TaskManagerOptions#NETWORK_BUFFERS_MEMORY_MAX}.
     */
    @Test
    public void hasNewNetworkBufConfNew() throws Exception {
        Configuration config = new Configuration();
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config));
        // fully defined:
        config.setFloat(NETWORK_BUFFERS_MEMORY_FRACTION, 0.1F);
        config.setString(NETWORK_BUFFERS_MEMORY_MIN, "1024");
        config.setString(NETWORK_BUFFERS_MEMORY_MAX, "2048");
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config));
        // partly defined:
        config = new Configuration();
        config.setFloat(NETWORK_BUFFERS_MEMORY_FRACTION, 0.1F);
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config));
        config.setString(NETWORK_BUFFERS_MEMORY_MAX, "1024");
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config));
        config = new Configuration();
        config.setString(NETWORK_BUFFERS_MEMORY_MIN, "1024");
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config));
        config.setFloat(NETWORK_BUFFERS_MEMORY_FRACTION, 0.1F);
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config));
        config = new Configuration();
        config.setString(NETWORK_BUFFERS_MEMORY_MAX, "1024");
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config));
        config.setString(NETWORK_BUFFERS_MEMORY_MIN, "1024");
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config));
    }

    /**
     * Verifies that {@link TaskManagerServicesConfiguration#hasNewNetworkBufConf(Configuration)}
     * returns the correct result for mixed old/new configurations.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void hasNewNetworkBufConfMixed() throws Exception {
        Configuration config = new Configuration();
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config));
        config.setInteger(NETWORK_NUM_BUFFERS, 1);
        Assert.assertFalse(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config));
        // old + 1 new parameter = new:
        Configuration config1 = config.clone();
        config1.setFloat(NETWORK_BUFFERS_MEMORY_FRACTION, 0.1F);
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config1));
        config1 = config.clone();
        config1.setString(NETWORK_BUFFERS_MEMORY_MIN, "1024");
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config1));
        config1 = config.clone();
        config1.setString(NETWORK_BUFFERS_MEMORY_MAX, "1024");
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config1));
    }
}

