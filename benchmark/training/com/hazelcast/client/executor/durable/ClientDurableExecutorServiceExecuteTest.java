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
package com.hazelcast.client.executor.durable;


import com.hazelcast.client.executor.tasks.MapPutRunnable;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.durableexecutor.DurableExecutorService;
import com.hazelcast.test.AssertTask;
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
public class ClientDurableExecutorServiceExecuteTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance server;

    private HazelcastInstance client;

    @Test
    public void testExecute() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        service.execute(new MapPutRunnable(mapName));
        IMap map = client.getMap(mapName);
        HazelcastTestSupport.assertSizeEventually(1, map);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("ConstantConditions")
    public void testExecute_whenTaskNull() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        service.execute(null);
    }

    @Test
    public void testExecuteOnKeyOwner() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        Member member = server.getCluster().getLocalMember();
        final String targetUuid = member.getUuid();
        String key = HazelcastTestSupport.generateKeyOwnedBy(server);
        service.executeOnKeyOwner(new MapPutRunnable(mapName), key);
        final IMap map = client.getMap(mapName);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            public void run() throws Exception {
                Assert.assertTrue(map.containsKey(targetUuid));
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteOnKeyOwner_whenKeyNull() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        service.executeOnKeyOwner(new MapPutRunnable("map"), null);
    }
}

