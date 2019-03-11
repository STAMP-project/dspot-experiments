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
package org.apache.rocketmq.common.protocol;


import MixAll.MASTER_ID;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.remoting.protocol.RemotingSerializable;
import org.junit.Assert;
import org.junit.Test;


public class ClusterInfoTest {
    @Test
    public void testFormJson() throws Exception {
        ClusterInfo clusterInfo = buildClusterInfo();
        byte[] data = clusterInfo.encode();
        ClusterInfo json = RemotingSerializable.decode(data, ClusterInfo.class);
        Assert.assertNotNull(json);
        Assert.assertNotNull(json.getClusterAddrTable());
        Assert.assertTrue(json.getClusterAddrTable().containsKey("DEFAULT_CLUSTER"));
        Assert.assertTrue(json.getClusterAddrTable().get("DEFAULT_CLUSTER").contains("master"));
        Assert.assertNotNull(json.getBrokerAddrTable());
        Assert.assertTrue(json.getBrokerAddrTable().containsKey("master"));
        Assert.assertEquals(json.getBrokerAddrTable().get("master").getBrokerName(), "master");
        Assert.assertEquals(json.getBrokerAddrTable().get("master").getCluster(), "DEFAULT_CLUSTER");
        Assert.assertEquals(json.getBrokerAddrTable().get("master").getBrokerAddrs().get(MASTER_ID), MixAll.getLocalhostByNetworkInterface());
    }

    @Test
    public void testRetrieveAllClusterNames() throws Exception {
        ClusterInfo clusterInfo = buildClusterInfo();
        byte[] data = clusterInfo.encode();
        ClusterInfo json = RemotingSerializable.decode(data, ClusterInfo.class);
        Assert.assertArrayEquals(new String[]{ "DEFAULT_CLUSTER" }, json.retrieveAllClusterNames());
    }

    @Test
    public void testRetrieveAllAddrByCluster() throws Exception {
        ClusterInfo clusterInfo = buildClusterInfo();
        byte[] data = clusterInfo.encode();
        ClusterInfo json = RemotingSerializable.decode(data, ClusterInfo.class);
        Assert.assertArrayEquals(new String[]{ MixAll.getLocalhostByNetworkInterface() }, json.retrieveAllAddrByCluster("DEFAULT_CLUSTER"));
    }
}

