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
package com.hazelcast.map;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapInstanceSharingTest extends HazelcastTestSupport {
    private HazelcastInstance local;

    private HazelcastInstance remote;

    @Test
    public void invocationToLocalMember() throws InterruptedException, ExecutionException {
        String localKey = HazelcastTestSupport.generateKeyOwnedBy(local);
        IMap<String, MapInstanceSharingTest.DummyObject> map = local.getMap(UUID.randomUUID().toString());
        MapInstanceSharingTest.DummyObject inserted = new MapInstanceSharingTest.DummyObject();
        map.put(localKey, inserted);
        MapInstanceSharingTest.DummyObject get1 = map.get(localKey);
        MapInstanceSharingTest.DummyObject get2 = map.get(localKey);
        Assert.assertNotNull(get1);
        Assert.assertNotNull(get2);
        Assert.assertNotSame(get1, get2);
        Assert.assertNotSame(get1, inserted);
        Assert.assertNotSame(get2, inserted);
    }

    @Test
    public void invocationToRemoteMember() throws InterruptedException, ExecutionException {
        String remoteKey = HazelcastTestSupport.generateKeyOwnedBy(remote);
        IMap<String, MapInstanceSharingTest.DummyObject> map = local.getMap(UUID.randomUUID().toString());
        MapInstanceSharingTest.DummyObject inserted = new MapInstanceSharingTest.DummyObject();
        map.put(remoteKey, inserted);
        MapInstanceSharingTest.DummyObject get1 = map.get(remoteKey);
        MapInstanceSharingTest.DummyObject get2 = map.get(remoteKey);
        Assert.assertNotNull(get1);
        Assert.assertNotNull(get2);
        Assert.assertNotSame(get1, get2);
        Assert.assertNotSame(get1, inserted);
        Assert.assertNotSame(get2, inserted);
    }

    public static class DummyObject implements Serializable {}
}

