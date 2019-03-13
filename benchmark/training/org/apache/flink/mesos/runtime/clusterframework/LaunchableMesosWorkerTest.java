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
package org.apache.flink.mesos.runtime.clusterframework;


import LaunchableMesosWorker.TM_PORT_KEYS;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static LaunchableMesosWorker.TM_PORT_KEYS;


/**
 * Test that mesos config are extracted correctly from the configuration.
 */
public class LaunchableMesosWorkerTest extends TestLogger {
    @Test
    public void canGetPortKeys() {
        // Setup
        Set<String> additionalPorts = new HashSet<>(Arrays.asList("someport.here", "anotherport"));
        Configuration config = new Configuration();
        config.setString(PORT_ASSIGNMENTS, String.join(",", additionalPorts));
        // Act
        Set<String> portKeys = LaunchableMesosWorker.extractPortKeys(config);
        // Assert
        Set<String> expectedPorts = new HashSet(TM_PORT_KEYS);
        expectedPorts.addAll(additionalPorts);
        Assert.assertThat(portKeys, CoreMatchers.is(CoreMatchers.equalTo(expectedPorts)));
    }

    @Test
    public void canGetNoPortKeys() {
        // Setup
        Configuration config = new Configuration();
        // Act
        Set<String> portKeys = LaunchableMesosWorker.extractPortKeys(config);
        // Assert
        Assert.assertThat(portKeys, CoreMatchers.is(CoreMatchers.equalTo(TM_PORT_KEYS)));
    }
}

