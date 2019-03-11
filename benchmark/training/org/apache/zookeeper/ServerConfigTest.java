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
package org.apache.zookeeper;


import org.apache.zookeeper.server.ServerConfig;
import org.junit.Assert;
import org.junit.Test;


public class ServerConfigTest {
    private ServerConfig serverConfig;

    @Test(expected = IllegalArgumentException.class)
    public void testFewArguments() {
        String[] args = new String[]{ "2181" };
        serverConfig.parse(args);
    }

    @Test
    public void testValidArguments() {
        String[] args = new String[]{ "2181", "/data/dir", "60000", "10000" };
        serverConfig.parse(args);
        Assert.assertEquals(2181, serverConfig.getClientPortAddress().getPort());
        Assert.assertTrue(checkEquality("/data/dir", serverConfig.getDataDir()));
        Assert.assertEquals(60000, serverConfig.getTickTime());
        Assert.assertEquals(10000, serverConfig.getMaxClientCnxns());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooManyArguments() {
        String[] args = new String[]{ "2181", "/data/dir", "60000", "10000", "9999" };
        serverConfig.parse(args);
    }
}

