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
package org.apache.flink.runtime.jobmanager;


import ConfigConstants.DEFAULT_HA_MODE;
import ConfigConstants.DEFAULT_RECOVERY_MODE;
import HighAvailabilityMode.FACTORY_CLASS;
import HighAvailabilityMode.NONE;
import HighAvailabilityMode.ZOOKEEPER;
import HighAvailabilityOptions.HA_MODE;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link HighAvailabilityMode}.
 */
public class HighAvailabilityModeTest extends TestLogger {
    // Default HA mode
    private static final HighAvailabilityMode DEFAULT_HA_MODE = HighAvailabilityMode.valueOf(ConfigConstants.DEFAULT_HA_MODE.toUpperCase());

    /**
     * Tests HA mode configuration.
     */
    @Test
    public void testFromConfig() throws Exception {
        Configuration config = new Configuration();
        // Check default
        Assert.assertEquals(HighAvailabilityModeTest.DEFAULT_HA_MODE, HighAvailabilityMode.fromConfig(config));
        // Check not equals default
        config.setString(HA_MODE, ZOOKEEPER.name().toLowerCase());
        Assert.assertEquals(ZOOKEEPER, HighAvailabilityMode.fromConfig(config));
        // Check factory class
        config.setString(HA_MODE, "factory.class.FQN");
        Assert.assertEquals(FACTORY_CLASS, HighAvailabilityMode.fromConfig(config));
    }

    /**
     * Tests HA mode configuration with deprecated config values.
     */
    @Test
    public void testDeprecatedFromConfig() throws Exception {
        Configuration config = new Configuration();
        // Check mapping of old default to new default
        config.setString("recovery.mode", DEFAULT_RECOVERY_MODE);
        Assert.assertEquals(HighAvailabilityModeTest.DEFAULT_HA_MODE, HighAvailabilityMode.fromConfig(config));
        // Check deprecated config
        config.setString("recovery.mode", ZOOKEEPER.name().toLowerCase());
        Assert.assertEquals(ZOOKEEPER, HighAvailabilityMode.fromConfig(config));
        // Check precedence over deprecated config
        config.setString("high-availability", NONE.name().toLowerCase());
        config.setString("recovery.mode", ZOOKEEPER.name().toLowerCase());
        Assert.assertEquals(NONE, HighAvailabilityMode.fromConfig(config));
    }

    @Test
    public void testCheckHighAvailabilityModeActivated() throws Exception {
        Configuration config = new Configuration();
        // check defaults
        Assert.assertTrue((!(HighAvailabilityMode.isHighAvailabilityModeActivated(config))));
        // check NONE
        config.setString("high-availability", NONE.name().toLowerCase());
        Assert.assertTrue((!(HighAvailabilityMode.isHighAvailabilityModeActivated(config))));
        // check ZOOKEEPER
        config.setString("high-availability", ZOOKEEPER.name().toLowerCase());
        Assert.assertTrue(HighAvailabilityMode.isHighAvailabilityModeActivated(config));
        // check FACTORY_CLASS
        config.setString("high-availability", FACTORY_CLASS.name().toLowerCase());
        Assert.assertTrue(HighAvailabilityMode.isHighAvailabilityModeActivated(config));
    }
}

