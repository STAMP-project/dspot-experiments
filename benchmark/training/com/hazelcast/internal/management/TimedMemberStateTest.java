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
package com.hazelcast.internal.management;


import ReplicatedMapService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.spi.impl.NodeEngineImpl;
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
public class TimedMemberStateTest extends HazelcastTestSupport {
    private TimedMemberState timedMemberState;

    private HazelcastInstance hz;

    @Test
    public void testClone() throws CloneNotSupportedException {
        TimedMemberState cloned = timedMemberState.clone();
        Assert.assertNotNull(cloned);
        Assert.assertEquals("ClusterName", cloned.getClusterName());
        Assert.assertEquals(1827731, cloned.getTime());
        Assert.assertNotNull(cloned.getMemberState());
        Assert.assertTrue(cloned.isSslEnabled());
        Assert.assertTrue(cloned.isLite());
        Assert.assertFalse(cloned.isScriptingEnabled());
        Assert.assertNotNull(cloned.toString());
    }

    @Test
    public void testSerialization() {
        JsonObject serialized = timedMemberState.toJson();
        TimedMemberState deserialized = new TimedMemberState();
        deserialized.fromJson(serialized);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals("ClusterName", deserialized.getClusterName());
        Assert.assertEquals(1827731, deserialized.getTime());
        Assert.assertNotNull(deserialized.getMemberState());
        Assert.assertTrue(deserialized.isSslEnabled());
        Assert.assertTrue(deserialized.isLite());
        Assert.assertFalse(deserialized.isScriptingEnabled());
        Assert.assertNotNull(deserialized.toString());
    }

    @Test
    public void testReplicatedMapGetStats() {
        NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(hz);
        hz.getReplicatedMap("replicatedMap");
        ReplicatedMapService replicatedMapService = nodeEngine.getService(SERVICE_NAME);
        Assert.assertNotNull(replicatedMapService.getStats().get("replicatedMap"));
    }
}

