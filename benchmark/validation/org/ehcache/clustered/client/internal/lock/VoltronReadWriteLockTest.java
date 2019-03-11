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


import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.terracotta.connection.Connection;
import org.terracotta.connection.entity.EntityRef;
import org.terracotta.exception.EntityAlreadyExistsException;


public class VoltronReadWriteLockTest {
    @Mock
    private VoltronReadWriteLockClient client;

    @Mock
    private EntityRef<VoltronReadWriteLockClient, Void, Void> entityRef;

    @Mock
    private Connection connection;

    @Test
    public void testCreateLockEntityWhenNotExisting() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.readLock();
        Mockito.verify(entityRef).create(ArgumentMatchers.isNull());
    }

    @Test
    public void testFetchExistingLockEntityWhenExists() throws Exception {
        Mockito.doThrow(EntityAlreadyExistsException.class).when(entityRef).create(ArgumentMatchers.any(Void.class));
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.readLock();
    }

    @Test
    public void testWriteLockLocksWrite() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.writeLock();
        Mockito.verify(client).lock(WRITE);
    }

    @Test
    public void testReadLockLocksRead() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.readLock();
        Mockito.verify(client).lock(READ);
    }

    @Test
    public void testWriteUnlockUnlocksWrite() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.writeLock().unlock();
        Mockito.verify(client).unlock(WRITE);
    }

    @Test
    public void testReadUnlockUnlocksRead() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.readLock().unlock();
        Mockito.verify(client).unlock(READ);
    }

    @Test
    public void testWriteUnlockClosesEntity() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.writeLock().unlock();
        Mockito.verify(client).close();
    }

    @Test
    public void testReadUnlockClosesEntity() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.readLock().unlock();
        Mockito.verify(client).close();
    }

    @Test
    public void testWriteUnlockDestroysEntity() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.writeLock().unlock();
        Mockito.verify(entityRef).destroy();
    }

    @Test
    public void testReadUnlockDestroysEntity() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.readLock().unlock();
        Mockito.verify(entityRef).destroy();
    }

    @Test
    public void testTryWriteLockTryLocksWrite() throws Exception {
        Mockito.when(client.tryLock(WRITE)).thenReturn(true);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        Assert.assertThat(lock.tryWriteLock(), IsNull.notNullValue());
        Mockito.verify(client).tryLock(WRITE);
    }

    @Test
    public void testTryReadLockTryLocksRead() throws Exception {
        Mockito.when(client.tryLock(READ)).thenReturn(true);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        Assert.assertThat(lock.tryReadLock(), IsNull.notNullValue());
        Mockito.verify(client).tryLock(READ);
    }

    @Test
    public void testTryWriteUnlockUnlocksWrite() throws Exception {
        Mockito.when(client.tryLock(WRITE)).thenReturn(true);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.tryWriteLock().unlock();
        Mockito.verify(client).unlock(WRITE);
    }

    @Test
    public void testTryReadUnlockUnlocksRead() throws Exception {
        Mockito.when(client.tryLock(READ)).thenReturn(true);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.tryReadLock().unlock();
        Mockito.verify(client).unlock(READ);
    }

    @Test
    public void testTryWriteUnlockClosesEntity() throws Exception {
        Mockito.when(client.tryLock(WRITE)).thenReturn(true);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.tryWriteLock().unlock();
        Mockito.verify(client).close();
    }

    @Test
    public void testTryReadUnlockClosesEntity() throws Exception {
        Mockito.when(client.tryLock(READ)).thenReturn(true);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.tryReadLock().unlock();
        Mockito.verify(client).close();
    }

    @Test
    public void testTryWriteUnlockDestroysEntity() throws Exception {
        Mockito.when(client.tryLock(WRITE)).thenReturn(true);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.tryWriteLock().unlock();
        Mockito.verify(entityRef).destroy();
    }

    @Test
    public void testTryReadUnlockDestroysEntity() throws Exception {
        Mockito.when(client.tryLock(READ)).thenReturn(true);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        lock.tryReadLock().unlock();
        Mockito.verify(entityRef).destroy();
    }

    @Test
    public void testTryWriteLockFailingClosesEntity() throws Exception {
        Mockito.when(client.tryLock(WRITE)).thenReturn(false);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        Assert.assertThat(lock.tryWriteLock(), IsNull.nullValue());
        Mockito.verify(client).close();
    }

    @Test
    public void testTryReadLockFailingClosesEntity() throws Exception {
        Mockito.when(client.tryLock(READ)).thenReturn(false);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        Assert.assertThat(lock.tryReadLock(), IsNull.nullValue());
        Mockito.verify(client).close();
    }

    @Test
    public void testTryWriteLockFailingDestroysEntity() throws Exception {
        Mockito.when(client.tryLock(WRITE)).thenReturn(false);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        Assert.assertThat(lock.tryWriteLock(), IsNull.nullValue());
        Mockito.verify(entityRef).destroy();
    }

    @Test
    public void testTryReadLockFailingDestroysEntity() throws Exception {
        Mockito.when(client.tryLock(READ)).thenReturn(false);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(client);
        Mockito.when(connection.<VoltronReadWriteLockClient, Void, Void>getEntityRef(VoltronReadWriteLockClient.class, 1, "VoltronReadWriteLock-TestLock")).thenReturn(entityRef);
        VoltronReadWriteLock lock = new VoltronReadWriteLock(connection, "TestLock");
        Assert.assertThat(lock.tryReadLock(), IsNull.nullValue());
        Mockito.verify(entityRef).destroy();
    }
}

