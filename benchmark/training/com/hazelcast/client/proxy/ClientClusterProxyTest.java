/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.proxy;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientClusterProxyTest extends HazelcastTestSupport {
    private TestHazelcastFactory factory;

    @Test
    public void addMembershipListener() throws Exception {
        String regId = client().getCluster().addMembershipListener(new MembershipAdapter());
        Assert.assertNotNull(regId);
    }

    @Test
    public void removeMembershipListener() throws Exception {
        Cluster cluster = client().getCluster();
        String regId = cluster.addMembershipListener(new MembershipAdapter());
        Assert.assertTrue(cluster.removeMembershipListener(regId));
    }

    @Test
    public void getMembers() throws Exception {
        Assert.assertEquals(1, client().getCluster().getMembers().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getLocalMember() throws Exception {
        client().getCluster().getLocalMember();
    }

    @Test
    public void getClusterTime() throws Exception {
        Assert.assertTrue(((client().getCluster().getClusterTime()) > 0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getClusterState() throws Exception {
        client().getCluster().getClusterState();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void changeClusterState() throws Exception {
        client().getCluster().changeClusterState(ClusterState.FROZEN);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getClusterVersion() throws Exception {
        client().getCluster().getClusterVersion();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void changeClusterStateWithOptions() throws Exception {
        client().getCluster().changeClusterState(ClusterState.FROZEN, new com.hazelcast.transaction.TransactionOptions());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shutdown() throws Exception {
        client().getCluster().shutdown();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shutdownWithOptions() throws Exception {
        client().getCluster().shutdown(new com.hazelcast.transaction.TransactionOptions());
    }
}

