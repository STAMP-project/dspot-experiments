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
package org.apache.hadoop.metrics2.impl;


import org.apache.commons.configuration2.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test metrics configuration
 */
public class TestMetricsConfig {
    static final Logger LOG = LoggerFactory.getLogger(TestMetricsConfig.class);

    /**
     * Common use cases
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCommon() throws Exception {
        String filename = TestMetricsConfig.getTestFilename("test-metrics2");
        new ConfigBuilder().add("*.foo", "default foo").add("p1.*.bar", "p1 default bar").add("p1.t1.*.bar", "p1.t1 default bar").add("p1.t1.i1.name", "p1.t1.i1.name").add("p1.t1.42.bar", "p1.t1.42.bar").add("p1.t2.i1.foo", "p1.t2.i1.foo").add("p2.*.foo", "p2 default foo").save(filename);
        MetricsConfig mc = MetricsConfig.create("p1", filename);
        TestMetricsConfig.LOG.debug(("mc:" + mc));
        Configuration expected = new ConfigBuilder().add("*.bar", "p1 default bar").add("t1.*.bar", "p1.t1 default bar").add("t1.i1.name", "p1.t1.i1.name").add("t1.42.bar", "p1.t1.42.bar").add("t2.i1.foo", "p1.t2.i1.foo").config;
        ConfigUtil.assertEq(expected, mc);
        testInstances(mc);
    }

    /**
     * Should not throw if missing config files
     */
    @Test
    public void testMissingFiles() {
        MetricsConfig config = MetricsConfig.create("JobTracker", "non-existent.properties");
        Assert.assertTrue(config.isEmpty());
    }

    /**
     * Test the config file load order
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testLoadFirst() throws Exception {
        String filename = TestMetricsConfig.getTestFilename("hadoop-metrics2-p1");
        new ConfigBuilder().add("p1.foo", "p1foo").save(filename);
        MetricsConfig mc = MetricsConfig.create("p1");
        MetricsConfig mc2 = MetricsConfig.create("p1", "na1", "na2", filename);
        Configuration expected = new ConfigBuilder().add("foo", "p1foo").config;
        ConfigUtil.assertEq(expected, mc);
        ConfigUtil.assertEq(expected, mc2);
    }
}

