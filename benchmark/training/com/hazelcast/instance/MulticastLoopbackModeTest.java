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
package com.hazelcast.instance;


import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Test the multicast loopback mode when there is no other
 * network interface than 127.0.0.1.
 *
 * @author St&amp;eacute;phane Galland <galland@arakhne.org>
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class MulticastLoopbackModeTest extends HazelcastTestSupport {
    private String multicastGroup;

    private HazelcastInstance hz1;

    private HazelcastInstance hz2;

    @Test
    public void testEnabledMode() throws Exception {
        createTestEnvironment(true);
        HazelcastTestSupport.assertClusterSize(2, hz1, hz2);
        Cluster cluster1 = hz1.getCluster();
        Cluster cluster2 = hz2.getCluster();
        Assert.assertTrue(((("Members list " + (cluster1.getMembers())) + " should contain ") + (cluster2.getLocalMember())), cluster1.getMembers().contains(cluster2.getLocalMember()));
        Assert.assertTrue(((("Members list " + (cluster2.getMembers())) + " should contain ") + (cluster1.getLocalMember())), cluster2.getMembers().contains(cluster1.getLocalMember()));
    }

    @Test
    public void testDisabledMode() throws Exception {
        createTestEnvironment(false);
        HazelcastTestSupport.assertClusterSize(1, hz1);
        HazelcastTestSupport.assertClusterSize(1, hz2);
    }
}

