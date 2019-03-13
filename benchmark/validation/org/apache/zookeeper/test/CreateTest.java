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
package org.apache.zookeeper.test;


import CreateMode.PERSISTENT;
import Ids.OPEN_ACL_UNSAFE;
import java.io.IOException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test suite for validating the Create API.
 */
public class CreateTest extends ClientBase {
    private ZooKeeper zk;

    @Test
    public void testCreate() throws IOException, InterruptedException, KeeperException {
        createNoStatVerifyResult("/foo");
        createNoStatVerifyResult("/foo/child");
    }

    @Test
    public void testCreateWithStat() throws IOException, InterruptedException, KeeperException {
        String name = "/foo";
        Stat stat = createWithStatVerifyResult("/foo");
        Stat childStat = createWithStatVerifyResult("/foo/child");
        // Don't expect to get the same stats for different creates.
        Assert.assertFalse(stat.equals(childStat));
    }

    @Test
    public void testCreateWithNullStat() throws IOException, InterruptedException, KeeperException {
        String name = "/foo";
        Assert.assertNull(zk.exists(name, false));
        Stat stat = null;
        // If a null Stat object is passed the create should still
        // succeed, but no Stat info will be returned.
        String path = zk.create(name, name.getBytes(), OPEN_ACL_UNSAFE, PERSISTENT, stat);
        Assert.assertNull(stat);
        Assert.assertNotNull(zk.exists(name, false));
    }
}

