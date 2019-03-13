/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.container.ozoneimpl;


import SupportedRpcType.GRPC;
import SupportedRpcType.NETTY;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests ozone containers with Apache Ratis.
 */
@Ignore("Disabling Ratis tests for pipeline work.")
public class TestRatisManager {
    private static final Logger LOG = LoggerFactory.getLogger(TestRatisManager.class);

    /**
     * Set the timeout for every test.
     */
    @Rule
    public Timeout testTimeout = new Timeout(200000);

    @Test
    public void testTestRatisManagerGrpc() throws Exception {
        TestRatisManager.runTestRatisManager(GRPC);
    }

    @Test
    public void testTestRatisManagerNetty() throws Exception {
        TestRatisManager.runTestRatisManager(NETTY);
    }
}

