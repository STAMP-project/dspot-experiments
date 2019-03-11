/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server;


import ZooKeeperServerConf.KEY_CLIENT_PORT;
import ZooKeeperServerConf.KEY_CLIENT_PORT_LISTEN_BACKLOG;
import ZooKeeperServerConf.KEY_DATA_DIR;
import ZooKeeperServerConf.KEY_DATA_LOG_DIR;
import ZooKeeperServerConf.KEY_MAX_CLIENT_CNXNS;
import ZooKeeperServerConf.KEY_MAX_SESSION_TIMEOUT;
import ZooKeeperServerConf.KEY_MIN_SESSION_TIMEOUT;
import ZooKeeperServerConf.KEY_SERVER_ID;
import ZooKeeperServerConf.KEY_TICK_TIME;
import java.util.Map;
import org.apache.zookeeper.ZKTestCase;
import org.junit.Assert;
import org.junit.Test;


public class ZooKeeperServerConfTest extends ZKTestCase {
    private ZooKeeperServerConf c;

    @Test
    public void testGetters() {
        Assert.assertEquals(1, c.getClientPort());
        Assert.assertEquals("a", c.getDataDir());
        Assert.assertEquals("b", c.getDataLogDir());
        Assert.assertEquals(2, c.getTickTime());
        Assert.assertEquals(3, c.getMaxClientCnxnsPerHost());
        Assert.assertEquals(4, c.getMinSessionTimeout());
        Assert.assertEquals(5, c.getMaxSessionTimeout());
        Assert.assertEquals(6L, c.getServerId());
        Assert.assertEquals(7, c.getClientPortListenBacklog());
    }

    @Test
    public void testToMap() {
        Map<String, Object> m = c.toMap();
        Assert.assertEquals(9, m.size());
        Assert.assertEquals(Integer.valueOf(1), m.get(KEY_CLIENT_PORT));
        Assert.assertEquals("a", m.get(KEY_DATA_DIR));
        Assert.assertEquals("b", m.get(KEY_DATA_LOG_DIR));
        Assert.assertEquals(Integer.valueOf(2), m.get(KEY_TICK_TIME));
        Assert.assertEquals(Integer.valueOf(3), m.get(KEY_MAX_CLIENT_CNXNS));
        Assert.assertEquals(Integer.valueOf(4), m.get(KEY_MIN_SESSION_TIMEOUT));
        Assert.assertEquals(Integer.valueOf(5), m.get(KEY_MAX_SESSION_TIMEOUT));
        Assert.assertEquals(Long.valueOf(6L), m.get(KEY_SERVER_ID));
        Assert.assertEquals(Integer.valueOf(7), m.get(KEY_CLIENT_PORT_LISTEN_BACKLOG));
    }
}

