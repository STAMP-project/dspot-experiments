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
package org.ehcache.clustered.client.internal.lock;


import java.net.URI;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.terracotta.connection.Connection;
import org.terracotta.connection.ConnectionFactory;
import org.terracotta.connection.entity.EntityRef;


public class VoltronReadWriteLockClientTest {
    private static final URI TEST_URI = URI.create("http://example.com:666");

    @Test
    public void testWriteLockExcludesRead() throws Exception {
        try (Connection connection = ConnectionFactory.connect(VoltronReadWriteLockClientTest.TEST_URI, new Properties())) {
            EntityRef<VoltronReadWriteLockClient, Void, Void> ref = getEntityReference(connection);
            ref.create(null);
            VoltronReadWriteLockClient locker = ref.fetchEntity(null);
            locker.lock(WRITE);
            try {
                VoltronReadWriteLockClient tester = ref.fetchEntity(null);
                Assert.assertThat(tester.tryLock(READ), Is.is(false));
            } finally {
                locker.unlock(WRITE);
            }
        }
    }

    @Test
    public void testWriteLockExcludesWrite() throws Exception {
        try (Connection connection = ConnectionFactory.connect(VoltronReadWriteLockClientTest.TEST_URI, new Properties())) {
            EntityRef<VoltronReadWriteLockClient, Void, Void> ref = getEntityReference(connection);
            ref.create(null);
            VoltronReadWriteLockClient locker = ref.fetchEntity(null);
            locker.lock(WRITE);
            try {
                VoltronReadWriteLockClient tester = ref.fetchEntity(null);
                Assert.assertThat(tester.tryLock(WRITE), Is.is(false));
            } finally {
                locker.unlock(WRITE);
            }
        }
    }

    @Test
    public void testReadLockExcludesWrite() throws Exception {
        try (Connection connection = ConnectionFactory.connect(VoltronReadWriteLockClientTest.TEST_URI, new Properties())) {
            EntityRef<VoltronReadWriteLockClient, Void, Void> ref = getEntityReference(connection);
            ref.create(null);
            VoltronReadWriteLockClient locker = ref.fetchEntity(null);
            locker.lock(READ);
            try {
                VoltronReadWriteLockClient tester = ref.fetchEntity(null);
                Assert.assertThat(tester.tryLock(WRITE), Is.is(false));
            } finally {
                locker.unlock(READ);
            }
        }
    }

    @Test
    public void testReadLockAllowsRead() throws Exception {
        try (Connection connection = ConnectionFactory.connect(VoltronReadWriteLockClientTest.TEST_URI, new Properties())) {
            EntityRef<VoltronReadWriteLockClient, Void, Void> ref = getEntityReference(connection);
            ref.create(null);
            VoltronReadWriteLockClient locker = ref.fetchEntity(null);
            locker.lock(READ);
            try {
                VoltronReadWriteLockClient tester = ref.fetchEntity(null);
                Assert.assertThat(tester.tryLock(READ), Is.is(true));
                tester.unlock(READ);
            } finally {
                locker.unlock(READ);
            }
        }
    }

    @Test
    public void testReadUnblocksAfterWriteReleased() throws Exception {
        try (Connection connection = ConnectionFactory.connect(VoltronReadWriteLockClientTest.TEST_URI, new Properties())) {
            final EntityRef<VoltronReadWriteLockClient, Void, Void> ref = getEntityReference(connection);
            ref.create(null);
            Future<Void> success;
            final VoltronReadWriteLockClient tester;
            VoltronReadWriteLockClient locker = ref.fetchEntity(null);
            locker.lock(WRITE);
            try {
                tester = ref.fetchEntity(null);
                success = VoltronReadWriteLockClientTest.async(() -> {
                    tester.lock(READ);
                    return null;
                });
                try {
                    success.get(50, TimeUnit.MILLISECONDS);
                    Assert.fail("Expected TimeoutException");
                } catch (TimeoutException e) {
                    // expected
                }
            } finally {
                locker.unlock(WRITE);
            }
            success.get(2, TimeUnit.MINUTES);
            tester.unlock(READ);
        }
    }

    @Test
    public void testWriteUnblocksAfterWriteReleased() throws Exception {
        try (Connection connection = ConnectionFactory.connect(VoltronReadWriteLockClientTest.TEST_URI, new Properties())) {
            final EntityRef<VoltronReadWriteLockClient, Void, Void> ref = getEntityReference(connection);
            ref.create(null);
            Future<Void> success;
            final VoltronReadWriteLockClient tester;
            VoltronReadWriteLockClient locker = ref.fetchEntity(null);
            locker.lock(WRITE);
            try {
                tester = ref.fetchEntity(null);
                success = VoltronReadWriteLockClientTest.async(() -> {
                    tester.lock(WRITE);
                    return null;
                });
                try {
                    success.get(50, TimeUnit.MILLISECONDS);
                    Assert.fail("Expected TimeoutException");
                } catch (TimeoutException e) {
                    // expected
                }
            } finally {
                locker.unlock(WRITE);
            }
            success.get(2, TimeUnit.MINUTES);
            tester.unlock(WRITE);
        }
    }

    @Test
    public void testWriteUnblocksAfterReadReleased() throws Exception {
        try (Connection connection = ConnectionFactory.connect(VoltronReadWriteLockClientTest.TEST_URI, new Properties())) {
            final EntityRef<VoltronReadWriteLockClient, Void, Void> ref = getEntityReference(connection);
            ref.create(null);
            Future<Void> success;
            final VoltronReadWriteLockClient tester;
            VoltronReadWriteLockClient locker = ref.fetchEntity(null);
            locker.lock(READ);
            try {
                tester = ref.fetchEntity(null);
                success = VoltronReadWriteLockClientTest.async(() -> {
                    tester.lock(WRITE);
                    return null;
                });
                try {
                    success.get(50, TimeUnit.MILLISECONDS);
                    Assert.fail("Expected TimeoutException");
                } catch (TimeoutException e) {
                    // expected
                }
            } finally {
                locker.unlock(READ);
            }
            success.get(2, TimeUnit.MINUTES);
            tester.unlock(WRITE);
        }
    }
}

