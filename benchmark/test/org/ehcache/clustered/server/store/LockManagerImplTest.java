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
package org.ehcache.clustered.server.store;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.ehcache.clustered.server.TestClientDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.terracotta.entity.ClientDescriptor;


public class LockManagerImplTest {
    @Test
    public void testLock() {
        LockManagerImpl lockManager = new LockManagerImpl();
        ClientDescriptor clientDescriptor = new TestClientDescriptor();
        Assert.assertThat(lockManager.lock(1L, clientDescriptor), Matchers.is(true));
        Assert.assertThat(lockManager.lock(1L, clientDescriptor), Matchers.is(false));
        Assert.assertThat(lockManager.lock(2L, clientDescriptor), Matchers.is(true));
    }

    @Test
    public void testUnlock() {
        LockManagerImpl lockManager = new LockManagerImpl();
        ClientDescriptor clientDescriptor = new TestClientDescriptor();
        Assert.assertThat(lockManager.lock(1L, clientDescriptor), Matchers.is(true));
        lockManager.unlock(1L);
        Assert.assertThat(lockManager.lock(1L, clientDescriptor), Matchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSweepLocksForClient() {
        LockManagerImpl lockManager = new LockManagerImpl();
        ClientDescriptor clientDescriptor1 = new TestClientDescriptor();
        ClientDescriptor clientDescriptor2 = new TestClientDescriptor();
        Assert.assertThat(lockManager.lock(1L, clientDescriptor1), Matchers.is(true));
        Assert.assertThat(lockManager.lock(2L, clientDescriptor1), Matchers.is(true));
        Assert.assertThat(lockManager.lock(3L, clientDescriptor1), Matchers.is(true));
        Assert.assertThat(lockManager.lock(4L, clientDescriptor1), Matchers.is(true));
        Assert.assertThat(lockManager.lock(5L, clientDescriptor2), Matchers.is(true));
        Assert.assertThat(lockManager.lock(6L, clientDescriptor2), Matchers.is(true));
        AtomicInteger counter = new AtomicInteger();
        Consumer<List<Long>> consumer = Mockito.mock(Consumer.class);
        ArgumentCaptor<List<Long>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.doAnswer(( invocation) -> counter.incrementAndGet()).when(consumer).accept(argumentCaptor.capture());
        lockManager.sweepLocksForClient(clientDescriptor2, consumer);
        Assert.assertThat(counter.get(), Matchers.is(1));
        Assert.assertThat(argumentCaptor.getValue().size(), Matchers.is(2));
        Assert.assertThat(argumentCaptor.getValue(), Matchers.containsInAnyOrder(5L, 6L));
        Assert.assertThat(lockManager.lock(5L, clientDescriptor2), Matchers.is(true));
        Assert.assertThat(lockManager.lock(6L, clientDescriptor2), Matchers.is(true));
        Assert.assertThat(lockManager.lock(1L, clientDescriptor1), Matchers.is(false));
        Assert.assertThat(lockManager.lock(2L, clientDescriptor1), Matchers.is(false));
        Assert.assertThat(lockManager.lock(3L, clientDescriptor1), Matchers.is(false));
        Assert.assertThat(lockManager.lock(4L, clientDescriptor1), Matchers.is(false));
    }

    @Test
    public void testCreateLockStateAfterFailover() {
        LockManagerImpl lockManager = new LockManagerImpl();
        ClientDescriptor clientDescriptor = new TestClientDescriptor();
        Set<Long> locks = new HashSet<>();
        locks.add(1L);
        locks.add(100L);
        locks.add(1000L);
        lockManager.createLockStateAfterFailover(clientDescriptor, locks);
        ClientDescriptor clientDescriptor1 = new TestClientDescriptor();
        Assert.assertThat(lockManager.lock(100L, clientDescriptor1), Matchers.is(false));
        Assert.assertThat(lockManager.lock(1000L, clientDescriptor1), Matchers.is(false));
        Assert.assertThat(lockManager.lock(1L, clientDescriptor1), Matchers.is(false));
    }
}

