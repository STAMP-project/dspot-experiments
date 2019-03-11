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
package org.ehcache.clustered.lock.server;


import org.ehcache.clustered.common.internal.lock.LockMessaging;
import org.ehcache.clustered.common.internal.lock.LockMessaging.LockTransition;
import org.hamcrest.beans.HasPropertyWithValue;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.terracotta.entity.ActiveInvokeContext;
import org.terracotta.entity.ClientCommunicator;
import org.terracotta.entity.ClientDescriptor;
import org.terracotta.entity.EntityResponse;
import org.terracotta.entity.MessageCodecException;


public class VoltronReadWriteLockActiveEntityTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ClientCommunicator communicator = Mockito.mock(ClientCommunicator.class);

    @InjectMocks
    VoltronReadWriteLockActiveEntity entity;

    private ActiveInvokeContext<LockTransition> context = VoltronReadWriteLockActiveEntityTest.newContext();

    @Test
    public void testWriteLock() {
        LockTransition transition = entity.invokeActive(context, LockMessaging.lock(WRITE));
        Assert.assertThat(transition.isAcquired(), Is.is(true));
    }

    @Test
    public void testReadLock() {
        LockTransition transition = entity.invokeActive(context, LockMessaging.lock(READ));
        Assert.assertThat(transition.isAcquired(), Is.is(true));
    }

    @Test
    public void testWriteUnlock() {
        entity.invokeActive(context, LockMessaging.lock(WRITE));
        LockTransition transition = entity.invokeActive(context, LockMessaging.unlock(WRITE));
        Assert.assertThat(transition.isReleased(), Is.is(true));
    }

    @Test
    public void testReadUnlock() {
        entity.invokeActive(context, LockMessaging.lock(READ));
        LockTransition transition = entity.invokeActive(context, LockMessaging.unlock(READ));
        Assert.assertThat(transition.isReleased(), Is.is(true));
    }

    @Test
    public void testTryWriteLockWhenWriteLocked() {
        entity.invokeActive(context, LockMessaging.lock(WRITE));
        LockTransition transition = entity.invokeActive(VoltronReadWriteLockActiveEntityTest.newContext(), LockMessaging.tryLock(WRITE));
        Assert.assertThat(transition.isAcquired(), Is.is(false));
    }

    @Test
    public void testTryReadLockWhenWriteLocked() {
        entity.invokeActive(context, LockMessaging.lock(WRITE));
        LockTransition transition = entity.invokeActive(VoltronReadWriteLockActiveEntityTest.newContext(), LockMessaging.tryLock(READ));
        Assert.assertThat(transition.isAcquired(), Is.is(false));
    }

    @Test
    public void testTryWriteLockWhenReadLocked() {
        entity.invokeActive(context, LockMessaging.lock(READ));
        LockTransition transition = entity.invokeActive(VoltronReadWriteLockActiveEntityTest.newContext(), LockMessaging.tryLock(WRITE));
        Assert.assertThat(transition.isAcquired(), Is.is(false));
    }

    @Test
    public void testTryReadLockWhenReadLocked() {
        entity.invokeActive(context, LockMessaging.lock(READ));
        LockTransition transition = entity.invokeActive(VoltronReadWriteLockActiveEntityTest.newContext(), LockMessaging.tryLock(READ));
        Assert.assertThat(transition.isAcquired(), Is.is(true));
    }

    @Test
    public void testWriteUnlockNotifiesListeners() throws MessageCodecException {
        ActiveInvokeContext<LockTransition> locker = VoltronReadWriteLockActiveEntityTest.newContext();
        ActiveInvokeContext<LockTransition> waiter = VoltronReadWriteLockActiveEntityTest.newContext();
        ClientDescriptor waiterDescriptor = () -> null;
        Mockito.when(waiter.getClientDescriptor()).thenReturn(waiterDescriptor);
        entity.invokeActive(locker, LockMessaging.lock(WRITE));
        entity.invokeActive(waiter, LockMessaging.lock(WRITE));
        entity.invokeActive(locker, LockMessaging.unlock(WRITE));
        Mockito.verify(communicator).sendNoResponse(ArgumentMatchers.same(waiterDescriptor), MockitoHamcrest.argThat(HasPropertyWithValue.<EntityResponse>hasProperty("released", Is.is(true))));
    }

    @Test
    public void testReadUnlockNotifiesListeners() throws MessageCodecException {
        ActiveInvokeContext<LockTransition> locker = VoltronReadWriteLockActiveEntityTest.newContext();
        ActiveInvokeContext<LockTransition> waiter = VoltronReadWriteLockActiveEntityTest.newContext();
        ClientDescriptor waiterDescriptor = () -> null;
        Mockito.when(waiter.getClientDescriptor()).thenReturn(waiterDescriptor);
        entity.invokeActive(locker, LockMessaging.lock(READ));
        entity.invokeActive(waiter, LockMessaging.lock(WRITE));
        entity.invokeActive(locker, LockMessaging.unlock(READ));
        Mockito.verify(communicator).sendNoResponse(ArgumentMatchers.same(waiterDescriptor), MockitoHamcrest.argThat(HasPropertyWithValue.<EntityResponse>hasProperty("released", Is.is(true))));
    }
}

