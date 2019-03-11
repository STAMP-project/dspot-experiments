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
package org.apache.flink.client.program;


import JobManagerOptions.ADDRESS;
import JobManagerOptions.PORT;
import JobMaster.JOB_MANAGER_NAME;
import java.net.UnknownHostException;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.util.StandaloneUtils;
import org.apache.flink.util.ConfigurationException;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that verify that the LeaderRetrievalService correctly handles non-resolvable host names
 * and does not fail with another exception.
 */
public class LeaderRetrievalServiceHostnameResolutionTest extends TestLogger {
    private static final String nonExistingHostname = "foo.bar.com.invalid";

    /* Tests that the StandaloneLeaderRetrievalService resolves host names if specified. */
    @Test
    public void testUnresolvableHostname1() throws UnknownHostException, ConfigurationException {
        Configuration config = new Configuration();
        config.setString(ADDRESS, LeaderRetrievalServiceHostnameResolutionTest.nonExistingHostname);
        config.setInteger(PORT, 17234);
        StandaloneUtils.createLeaderRetrievalService(config, false, JOB_MANAGER_NAME);
    }

    /* Tests that the StandaloneLeaderRetrievalService does not resolve host names by default. */
    @Test
    public void testUnresolvableHostname2() throws Exception {
        try {
            Configuration config = new Configuration();
            config.setString(ADDRESS, LeaderRetrievalServiceHostnameResolutionTest.nonExistingHostname);
            config.setInteger(PORT, 17234);
            StandaloneUtils.createLeaderRetrievalService(config, true, JOB_MANAGER_NAME);
            Assert.fail("This should fail with an UnknownHostException");
        } catch (UnknownHostException e) {
            // that is what we want!
        }
    }
}

