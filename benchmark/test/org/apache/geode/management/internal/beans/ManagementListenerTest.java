/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geode.management.internal.beans;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.distributed.internal.ResourceEvent;
import org.apache.geode.test.junit.categories.ManagementTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ManagementListener}.
 */
@Category(ManagementTest.class)
public class ManagementListenerTest {
    private InternalDistributedSystem system;

    private Lock readLock;

    private Lock writeLock;

    private ReadWriteLock readWriteLock;

    private InOrder readLockInOrder;

    private InOrder writeLockInOrder;

    private ManagementListener managementListener;

    @Test
    public void shouldProceedReturnsTrueIfSystemNotConnectedForCacheRemoveEvent() {
        Mockito.when(system.isConnected()).thenReturn(false);
        assertThat(managementListener.shouldProceed(ResourceEvent.CACHE_REMOVE)).isTrue();
    }

    @Test
    public void shouldProceedReturnsFalseIfSystemNotConnectedForOtherEvents() {
        Mockito.when(system.isConnected()).thenReturn(false);
        for (ResourceEvent resourceEvent : ResourceEvent.values()) {
            if (resourceEvent != (ResourceEvent.CACHE_REMOVE)) {
                assertThat(managementListener.shouldProceed(ResourceEvent.CACHE_REMOVE)).isTrue();
            }
        }
    }

    @Test
    public void shouldProceedReturnsTrueIfSystemIsConnected() {
        for (ResourceEvent resourceEvent : ResourceEvent.values()) {
            assertThat(managementListener.shouldProceed(resourceEvent)).isTrue();
        }
    }

    @Test
    public void shouldProceedReturnsFalseIfNoCache() {
        Mockito.when(system.getCache()).thenReturn(null);
        for (ResourceEvent resourceEvent : ResourceEvent.values()) {
            assertThat(managementListener.shouldProceed(resourceEvent)).isFalse();
        }
    }

    @Test
    public void handleEventUsesWriteLockForCacheCreateEvent() throws InterruptedException {
        managementListener.handleEvent(ResourceEvent.CACHE_CREATE, null);
        writeLockInOrder.verify(writeLock).lockInterruptibly();
        writeLockInOrder.verify(writeLock).unlock();
    }

    @Test
    public void handleEventUsesWriteLockForCacheRemoveEvent() throws InterruptedException {
        managementListener.handleEvent(ResourceEvent.CACHE_REMOVE, null);
        writeLockInOrder.verify(writeLock).lockInterruptibly();
        writeLockInOrder.verify(writeLock).unlock();
    }

    @Test
    public void handleEventDoesNotUseLocksForSystemAlertEvent() {
        managementListener.handleEvent(ResourceEvent.SYSTEM_ALERT, null);
        Mockito.verifyZeroInteractions(readWriteLock);
        Mockito.verifyZeroInteractions(readLock);
        Mockito.verifyZeroInteractions(writeLock);
    }

    @Test
    public void handleEventUsesReadLockForOtherEvents() throws InterruptedException {
        for (ResourceEvent resourceEvent : ResourceEvent.values()) {
            if (((resourceEvent != (ResourceEvent.CACHE_CREATE)) && (resourceEvent != (ResourceEvent.CACHE_REMOVE))) && (resourceEvent != (ResourceEvent.SYSTEM_ALERT))) {
                managementListener.handleEvent(resourceEvent, null);
                readLockInOrder.verify(readLock).lockInterruptibly();
                readLockInOrder.verify(readLock).unlock();
            }
        }
    }
}

