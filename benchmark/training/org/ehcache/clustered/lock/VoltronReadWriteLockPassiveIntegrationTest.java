/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.lock;


import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.ehcache.clustered.ClusteredTests;
import org.ehcache.clustered.client.internal.lock.VoltronReadWriteLock;
import org.ehcache.clustered.client.internal.lock.VoltronReadWriteLock.Hold;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.connection.Connection;
import org.terracotta.testing.rules.Cluster;


public class VoltronReadWriteLockPassiveIntegrationTest extends ClusteredTests {
    @ClassRule
    public static Cluster CLUSTER = newCluster(2).in(new File("build/cluster")).build();

    @Test
    public void testSingleThreadSingleClientInteraction() throws Throwable {
        try (Connection client = VoltronReadWriteLockPassiveIntegrationTest.CLUSTER.newConnection()) {
            VoltronReadWriteLock lock = new VoltronReadWriteLock(client, "test");
            Hold hold = lock.writeLock();
            VoltronReadWriteLockPassiveIntegrationTest.CLUSTER.getClusterControl().terminateActive();
            VoltronReadWriteLockPassiveIntegrationTest.CLUSTER.getClusterControl().startOneServer();
            hold.unlock();
        }
    }

    @Test
    public void testMultipleThreadsSingleConnection() throws Throwable {
        try (Connection client = VoltronReadWriteLockPassiveIntegrationTest.CLUSTER.newConnection()) {
            final VoltronReadWriteLock lock = new VoltronReadWriteLock(client, "test");
            Hold hold = lock.writeLock();
            Future<Void> waiter = VoltronReadWriteLockIntegrationTest.async(() -> {
                lock.writeLock().unlock();
                return null;
            });
            try {
                waiter.get(100, TimeUnit.MILLISECONDS);
                Assert.fail("TimeoutException expected");
            } catch (TimeoutException e) {
                // expected
            }
            VoltronReadWriteLockPassiveIntegrationTest.CLUSTER.getClusterControl().terminateActive();
            VoltronReadWriteLockPassiveIntegrationTest.CLUSTER.getClusterControl().startOneServer();
            try {
                waiter.get(100, TimeUnit.MILLISECONDS);
                Assert.fail("TimeoutException expected");
            } catch (TimeoutException e) {
                // expected
            }
            hold.unlock();
            waiter.get();
        }
    }

    @Test
    public void testMultipleClients() throws Throwable {
        try (Connection clientA = VoltronReadWriteLockPassiveIntegrationTest.CLUSTER.newConnection();Connection clientB = VoltronReadWriteLockPassiveIntegrationTest.CLUSTER.newConnection()) {
            VoltronReadWriteLock lockA = new VoltronReadWriteLock(clientA, "test");
            Hold hold = lockA.writeLock();
            Future<Void> waiter = VoltronReadWriteLockIntegrationTest.async(() -> {
                writeLock().unlock();
                return null;
            });
            try {
                waiter.get(100, TimeUnit.MILLISECONDS);
                Assert.fail("TimeoutException expected");
            } catch (TimeoutException e) {
                // expected
            }
            VoltronReadWriteLockPassiveIntegrationTest.CLUSTER.getClusterControl().terminateActive();
            VoltronReadWriteLockPassiveIntegrationTest.CLUSTER.getClusterControl().startOneServer();
            try {
                waiter.get(100, TimeUnit.MILLISECONDS);
                Assert.fail("TimeoutException expected");
            } catch (TimeoutException e) {
                // expected
            }
            hold.unlock();
            waiter.get();
        }
    }
}

