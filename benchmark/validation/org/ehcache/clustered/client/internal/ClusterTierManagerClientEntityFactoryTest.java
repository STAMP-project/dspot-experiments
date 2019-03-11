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
package org.ehcache.clustered.client.internal;


import org.ehcache.clustered.common.internal.ClusterTierManagerConfiguration;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.terracotta.connection.Connection;
import org.terracotta.connection.entity.EntityRef;
import org.terracotta.exception.EntityAlreadyExistsException;
import org.terracotta.exception.EntityConfigurationException;
import org.terracotta.exception.EntityNotFoundException;


public class ClusterTierManagerClientEntityFactoryTest {
    @Mock
    private EntityRef<ClusterTierManagerClientEntity, Object, Void> entityRef;

    @Mock
    private ClusterTierManagerClientEntity entity;

    @Mock
    private Connection connection;

    @Test
    public void testCreate() throws Exception {
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(entityRef);
        ClusterTierManagerClientEntityFactoryTest.addMockUnlockedLock(connection, "VoltronReadWriteLock-ClusterTierManagerClientEntityFactory-AccessLock-test");
        ClusterTierManagerClientEntityFactory factory = new ClusterTierManagerClientEntityFactory(connection);
        factory.create("test", null);
        Mockito.verify(entityRef).create(ArgumentMatchers.isA(ClusterTierManagerConfiguration.class));
        Mockito.verifyNoMoreInteractions(entityRef);
    }

    @Test
    public void testCreateBadConfig() throws Exception {
        Mockito.doThrow(EntityConfigurationException.class).when(entityRef).create(ArgumentMatchers.any(ClusterTierManagerConfiguration.class));
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(entityRef);
        ClusterTierManagerClientEntityFactoryTest.addMockUnlockedLock(connection, "VoltronReadWriteLock-ClusterTierManagerClientEntityFactory-AccessLock-test");
        ClusterTierManagerClientEntityFactory factory = new ClusterTierManagerClientEntityFactory(connection);
        try {
            factory.create("test", null);
            Assert.fail("Expecting ClusterTierManagerCreationException");
        } catch (ClusterTierManagerCreationException e) {
            // expected
        }
    }

    @Test
    public void testCreateWhenExisting() throws Exception {
        Mockito.doThrow(EntityAlreadyExistsException.class).when(entityRef).create(ArgumentMatchers.any());
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(entityRef);
        ClusterTierManagerClientEntityFactoryTest.addMockUnlockedLock(connection, "VoltronReadWriteLock-ClusterTierManagerClientEntityFactory-AccessLock-test");
        ClusterTierManagerClientEntityFactory factory = new ClusterTierManagerClientEntityFactory(connection);
        try {
            factory.create("test", null);
            Assert.fail("Expected EntityAlreadyExistsException");
        } catch (EntityAlreadyExistsException e) {
            // expected
        }
    }

    @Test
    public void testRetrieve() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(entity);
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(entityRef);
        ClusterTierManagerClientEntityFactoryTest.addMockUnlockedLock(connection, "VoltronReadWriteLock-ClusterTierManagerClientEntityFactory-AccessLock-test");
        ClusterTierManagerClientEntityFactory factory = new ClusterTierManagerClientEntityFactory(connection);
        Assert.assertThat(factory.retrieve("test", null), Is.is(entity));
        Mockito.verify(entity).validate(ArgumentMatchers.isNull());
        Mockito.verify(entity, Mockito.never()).close();
    }

    @Test
    public void testRetrieveFailedValidate() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(entity);
        Mockito.doThrow(IllegalArgumentException.class).when(entity).validate(ArgumentMatchers.isNull());
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(entityRef);
        ClusterTierManagerClientEntityFactoryTest.addMockUnlockedLock(connection, "VoltronReadWriteLock-ClusterTierManagerClientEntityFactory-AccessLock-test");
        ClusterTierManagerClientEntityFactory factory = new ClusterTierManagerClientEntityFactory(connection);
        try {
            factory.retrieve("test", null);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        Mockito.verify(entity).validate(ArgumentMatchers.isNull());
        Mockito.verify(entity).close();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRetrieveWhenNotExisting() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenThrow(EntityNotFoundException.class);
        Mockito.doThrow(EntityAlreadyExistsException.class).when(entityRef).create(ArgumentMatchers.any());
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(entityRef);
        ClusterTierManagerClientEntityFactoryTest.addMockUnlockedLock(connection, "VoltronReadWriteLock-ClusterTierManagerClientEntityFactory-AccessLock-test");
        ClusterTierManagerClientEntityFactory factory = new ClusterTierManagerClientEntityFactory(connection);
        try {
            factory.retrieve("test", null);
            Assert.fail("Expected EntityNotFoundException");
        } catch (EntityNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testDestroy() throws Exception {
        ClusterTierManagerClientEntity mockEntity = Mockito.mock(ClusterTierManagerClientEntity.class);
        Mockito.when(entityRef.fetchEntity(null)).thenReturn(mockEntity);
        Mockito.doReturn(Boolean.TRUE).when(entityRef).destroy();
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(entityRef);
        ClusterTierManagerClientEntityFactoryTest.addMockUnlockedLock(connection, "VoltronReadWriteLock-ClusterTierManagerClientEntityFactory-AccessLock-test");
        ClusterTierManagerClientEntityFactory factory = new ClusterTierManagerClientEntityFactory(connection);
        factory.destroy("test");
        Mockito.verify(entityRef).destroy();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroyWhenNotExisting() throws Exception {
        Mockito.when(entityRef.fetchEntity(null)).thenThrow(EntityNotFoundException.class);
        Mockito.doThrow(EntityNotFoundException.class).when(entityRef).destroy();
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(entityRef);
        ClusterTierManagerClientEntityFactoryTest.addMockUnlockedLock(connection, "VoltronReadWriteLock-ClusterTierManagerClientEntityFactory-AccessLock-test");
        ClusterTierManagerClientEntityFactory factory = new ClusterTierManagerClientEntityFactory(connection);
        factory.destroy("test");
        Mockito.verify(entityRef).destroy();
    }
}

