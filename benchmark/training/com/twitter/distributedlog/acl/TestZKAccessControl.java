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
package com.twitter.distributedlog.acl;


import CreateMode.PERSISTENT;
import ZKAccessControl.DEFAULT_ACCESS_CONTROL_ENTRY;
import com.google.common.base.Charsets;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.ZooKeeperClusterTestCase;
import com.twitter.distributedlog.thrift.AccessControlEntry;
import com.twitter.util.Await;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.Test;

import static ZKAccessControl.DEFAULT_ACCESS_CONTROL_ENTRY;


public class TestZKAccessControl extends ZooKeeperClusterTestCase {
    private ZooKeeperClient zkc;

    @Test(timeout = 60000)
    public void testCreateZKAccessControl() throws Exception {
        AccessControlEntry ace = new AccessControlEntry();
        ace.setDenyWrite(true);
        String zkPath = "/create-zk-access-control";
        ZKAccessControl zkac = new ZKAccessControl(ace, zkPath);
        Await.result(zkac.create(zkc));
        ZKAccessControl readZKAC = Await.result(ZKAccessControl.read(zkc, zkPath, null));
        Assert.assertEquals(zkac, readZKAC);
        ZKAccessControl another = new ZKAccessControl(ace, zkPath);
        try {
            Await.result(another.create(zkc));
        } catch (KeeperException ke) {
            // expected
        }
    }

    @Test(timeout = 60000)
    public void testDeleteZKAccessControl() throws Exception {
        String zkPath = "/delete-zk-access-control";
        AccessControlEntry ace = new AccessControlEntry();
        ace.setDenyDelete(true);
        ZKAccessControl zkac = new ZKAccessControl(ace, zkPath);
        Await.result(zkac.create(zkc));
        ZKAccessControl readZKAC = Await.result(ZKAccessControl.read(zkc, zkPath, null));
        Assert.assertEquals(zkac, readZKAC);
        Await.result(ZKAccessControl.delete(zkc, zkPath));
        try {
            Await.result(ZKAccessControl.read(zkc, zkPath, null));
        } catch (KeeperException nne) {
            // expected.
        }
        Await.result(ZKAccessControl.delete(zkc, zkPath));
    }

    @Test(timeout = 60000)
    public void testEmptyZKAccessControl() throws Exception {
        String zkPath = "/empty-access-control";
        zkc.get().create(zkPath, new byte[0], zkc.getDefaultACL(), PERSISTENT);
        ZKAccessControl readZKAC = Await.result(ZKAccessControl.read(zkc, zkPath, null));
        Assert.assertEquals(zkPath, readZKAC.zkPath);
        Assert.assertEquals(DEFAULT_ACCESS_CONTROL_ENTRY, readZKAC.getAccessControlEntry());
        Assert.assertTrue(((DEFAULT_ACCESS_CONTROL_ENTRY) == (readZKAC.getAccessControlEntry())));
    }

    @Test(timeout = 60000)
    public void testCorruptedZKAccessControl() throws Exception {
        String zkPath = "/corrupted-zk-access-control";
        zkc.get().create(zkPath, "corrupted-data".getBytes(Charsets.UTF_8), zkc.getDefaultACL(), PERSISTENT);
        try {
            Await.result(ZKAccessControl.read(zkc, zkPath, null));
        } catch (ZKAccessControl cace) {
            // expected
        }
    }

    @Test(timeout = 60000)
    public void testUpdateZKAccessControl() throws Exception {
        String zkPath = "/update-zk-access-control";
        AccessControlEntry ace = new AccessControlEntry();
        ace.setDenyDelete(true);
        ZKAccessControl zkac = new ZKAccessControl(ace, zkPath);
        Await.result(zkac.create(zkc));
        ZKAccessControl readZKAC = Await.result(ZKAccessControl.read(zkc, zkPath, null));
        Assert.assertEquals(zkac, readZKAC);
        ace.setDenyRelease(true);
        ZKAccessControl newZKAC = new ZKAccessControl(ace, zkPath);
        Await.result(newZKAC.update(zkc));
        ZKAccessControl readZKAC2 = Await.result(ZKAccessControl.read(zkc, zkPath, null));
        Assert.assertEquals(newZKAC, readZKAC2);
        try {
            Await.result(readZKAC.update(zkc));
        } catch (KeeperException bve) {
            // expected
        }
        readZKAC2.accessControlEntry.setDenyTruncate(true);
        Await.result(readZKAC2.update(zkc));
        ZKAccessControl readZKAC3 = Await.result(ZKAccessControl.read(zkc, zkPath, null));
        Assert.assertEquals(readZKAC2, readZKAC3);
    }
}

