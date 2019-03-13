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
package com.hazelcast.client.map.impl.nearcache;


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(NightlyTest.class)
public class ClientMapNearCachePreloaderStressTest extends HazelcastTestSupport {
    private final TestHazelcastFactory factory = new TestHazelcastFactory();

    @Test
    public void testDestroyAndCreateProxyWithNearCache() {
        Config config = getBaseConfig();
        ClientConfig clientConfig = new ClientConfig().addNearCacheConfig(getNearCacheConfig("test"));
        factory.newHazelcastInstance(config);
        final HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        int createPutGetThreadCount = 2;
        int destroyThreadCount = 2;
        ExecutorService pool = Executors.newFixedThreadPool((createPutGetThreadCount + destroyThreadCount));
        final AtomicBoolean isRunning = new AtomicBoolean(true);
        final AtomicReference<Exception> exception = new AtomicReference<Exception>();
        for (int i = 0; i < destroyThreadCount; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    while (isRunning.get()) {
                        for (DistributedObject distributedObject : client.getDistributedObjects()) {
                            distributedObject.destroy();
                        }
                    } 
                }
            });
        }
        for (int i = 0; i < createPutGetThreadCount; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (isRunning.get()) {
                            IMap<Object, Object> map = client.getMap("test");
                            map.put(1, 1);
                            map.get(1);
                        } 
                    } catch (Exception e) {
                        isRunning.set(false);
                        e.printStackTrace(System.out);
                        exception.set(e);
                    }
                }
            });
        }
        sleepSeconds(5);
        isRunning.set(false);
        pool.shutdown();
        TestCase.assertNull(exception.get());
    }
}

