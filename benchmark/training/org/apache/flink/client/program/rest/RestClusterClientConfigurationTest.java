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
package org.apache.flink.client.program.rest;


import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link RestClusterClientConfiguration}.
 */
public class RestClusterClientConfigurationTest extends TestLogger {
    private RestClusterClientConfiguration restClusterClientConfiguration;

    @Test
    public void testConfiguration() {
        Assert.assertEquals(1, restClusterClientConfiguration.getAwaitLeaderTimeout());
        Assert.assertEquals(2, restClusterClientConfiguration.getRetryMaxAttempts());
        Assert.assertEquals(3, restClusterClientConfiguration.getRetryDelay());
    }
}

