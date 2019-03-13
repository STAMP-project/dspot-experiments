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
package com.hazelcast.client.lock;


import ClientProperty.INVOCATION_TIMEOUT_SECONDS;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.ClientTestSupport;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.function.BiConsumer;
import com.hazelcast.util.function.Consumer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientLockRetryWhenOwnerDiesTest extends ClientTestSupport {
    private static long leaseTime = 120;

    private static long waitTime = 120;

    static class Shutdown implements Consumer<HazelcastInstance> {
        @Override
        public void accept(HazelcastInstance hazelcastInstance) {
            hazelcastInstance.shutdown();
        }

        @Override
        public String toString() {
            return "Shutdown{}";
        }
    }

    static class Terminate implements Consumer<HazelcastInstance> {
        @Override
        public void accept(HazelcastInstance hazelcastInstance) {
            hazelcastInstance.getLifecycleService().terminate();
        }

        @Override
        public String toString() {
            return "Terminate{}";
        }
    }

    static class LockLock implements BiConsumer<HazelcastInstance, String> {
        @Override
        public void accept(HazelcastInstance client, String key) {
            ILock lock = client.getLock(key);
            lock.lock();
            lock.unlock();
        }

        @Override
        public String toString() {
            return "LockLock{}";
        }
    }

    static class LockLockLease implements BiConsumer<HazelcastInstance, String> {
        @Override
        public void accept(HazelcastInstance client, String key) {
            ILock lock = client.getLock(key);
            lock.lock(ClientLockRetryWhenOwnerDiesTest.leaseTime, TimeUnit.SECONDS);
            lock.unlock();
        }

        @Override
        public String toString() {
            return "LockLockLease{}";
        }
    }

    static class LockTryLockTimeout implements BiConsumer<HazelcastInstance, String> {
        @Override
        public void accept(HazelcastInstance client, String key) {
            ILock lock = client.getLock(key);
            try {
                lock.tryLock(ClientLockRetryWhenOwnerDiesTest.waitTime, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }

        @Override
        public String toString() {
            return "LockTryLockTimeout{}";
        }
    }

    static class LockTryLockTimeoutLease implements BiConsumer<HazelcastInstance, String> {
        @Override
        public void accept(HazelcastInstance client, String key) {
            ILock lock = client.getLock(key);
            try {
                lock.tryLock(ClientLockRetryWhenOwnerDiesTest.waitTime, TimeUnit.SECONDS, ClientLockRetryWhenOwnerDiesTest.leaseTime, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }

        @Override
        public String toString() {
            return "LockTryLockTimeoutLease{}";
        }
    }

    static class MapLock implements BiConsumer<HazelcastInstance, String> {
        @Override
        public void accept(HazelcastInstance client, String key) {
            IMap map = client.getMap(key);
            map.lock(key);
            map.unlock(key);
        }

        @Override
        public String toString() {
            return "MapLock{}";
        }
    }

    static class MapLockLease implements BiConsumer<HazelcastInstance, String> {
        @Override
        public void accept(HazelcastInstance client, String key) {
            IMap map = client.getMap(key);
            map.lock(key, ClientLockRetryWhenOwnerDiesTest.leaseTime, TimeUnit.SECONDS);
            map.unlock(key);
        }
    }

    static class MapTryLockTimeout implements BiConsumer<HazelcastInstance, String> {
        @Override
        public void accept(HazelcastInstance client, String key) {
            IMap map = client.getMap(key);
            try {
                map.tryLock(key, ClientLockRetryWhenOwnerDiesTest.waitTime, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            map.unlock(key);
        }

        @Override
        public String toString() {
            return "MapTryLockTimeout{}";
        }
    }

    static class MapTryLockTimeoutLease implements BiConsumer<HazelcastInstance, String> {
        @Override
        public void accept(HazelcastInstance client, String key) {
            IMap map = client.getMap(key);
            try {
                map.tryLock(key, ClientLockRetryWhenOwnerDiesTest.waitTime, TimeUnit.SECONDS, ClientLockRetryWhenOwnerDiesTest.leaseTime, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            map.unlock(key);
        }

        @Override
        public String toString() {
            return "MapTryLockTimeoutLease{}";
        }
    }

    @Parameterized.Parameter
    public Consumer<HazelcastInstance> closePolicy;

    @Parameterized.Parameter(1)
    public BiConsumer<HazelcastInstance, String> lockPolicy;

    private TestHazelcastFactory factory = new TestHazelcastFactory();

    @Test
    public void testKeyOwnerCloses_afterInvocationTimeout() throws Exception {
        HazelcastInstance keyOwner = newHazelcastInstance();
        HazelcastInstance instance = newHazelcastInstance();
        warmUpPartitions(keyOwner, instance);
        ClientConfig clientConfig = new ClientConfig();
        long invocationTimeoutMillis = 1000;
        clientConfig.setProperty(INVOCATION_TIMEOUT_SECONDS.getName(), String.valueOf(TimeUnit.MILLISECONDS.toSeconds(invocationTimeoutMillis)));
        final HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        // this is needed in the test because we set the timeout too short for faster test.
        makeSureConnectedToServers(client, 2);
        final String key = generateKeyOwnedBy(keyOwner);
        ILock lock = client.getLock(key);
        lock.lock();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            public void run() {
                lockPolicy.accept(client, key);
                latch.countDown();
            }
        }).start();
        Thread.sleep((invocationTimeoutMillis * 2));
        closePolicy.accept(keyOwner);
        // wait for the key owned by second member after close to avoid operation timeout during transition
        // this is needed in the test because we set the timeout too short for faster test.
        Member secondMember = instance.getCluster().getLocalMember();
        while (!(secondMember.equals(client.getPartitionService().getPartition(key).getOwner()))) {
            Thread.sleep(100);
        } 
        Assert.assertTrue(lock.isLocked());
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertTrue(lock.tryLock());
        lock.unlock();
        lock.unlock();
        assertOpenEventually(latch);
    }
}

