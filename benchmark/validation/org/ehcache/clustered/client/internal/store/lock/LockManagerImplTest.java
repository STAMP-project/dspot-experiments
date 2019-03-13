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
package org.ehcache.clustered.client.internal.store.lock;


import java.util.Set;
import java.util.concurrent.TimeoutException;
import org.ehcache.clustered.client.internal.store.ClusterTierClientEntity;
import org.ehcache.clustered.client.internal.store.ServerStoreProxyException;
import org.ehcache.clustered.common.internal.exceptions.UnknownClusterException;
import org.ehcache.clustered.common.internal.messages.ClusterTierReconnectMessage;
import org.ehcache.clustered.common.internal.messages.EhcacheEntityResponse.LockSuccess;
import org.ehcache.clustered.common.internal.messages.ServerStoreOpMessage.LockMessage;
import org.ehcache.clustered.common.internal.store.Chain;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class LockManagerImplTest {
    @Test
    public void testLock() throws Exception {
        ClusterTierClientEntity clusterTierClientEntity = Mockito.mock(ClusterTierClientEntity.class);
        LockManagerImpl lockManager = new LockManagerImpl(clusterTierClientEntity);
        LockSuccess lockSuccess = getLockSuccessResponse();
        Mockito.when(clusterTierClientEntity.invokeAndWaitForComplete(ArgumentMatchers.any(LockMessage.class), ArgumentMatchers.anyBoolean())).thenReturn(lockSuccess);
        Chain lock = lockManager.lock(2L);
        Assert.assertThat(lock, Matchers.notNullValue());
        Assert.assertThat(lock.length(), Matchers.is(3));
    }

    @Test
    public void testLockWhenException() throws Exception {
        ClusterTierClientEntity clusterTierClientEntity = Mockito.mock(ClusterTierClientEntity.class);
        LockManagerImpl lockManager = new LockManagerImpl(clusterTierClientEntity);
        Mockito.when(clusterTierClientEntity.invokeAndWaitForComplete(ArgumentMatchers.any(LockMessage.class), ArgumentMatchers.anyBoolean())).thenThrow(new UnknownClusterException(""), new TimeoutException("timed out test"));
        try {
            lockManager.lock(2L);
            Assert.fail();
        } catch (ServerStoreProxyException sspe) {
            Assert.assertThat(sspe.getCause(), Matchers.instanceOf(UnknownClusterException.class));
        }
        try {
            lockManager.lock(2L);
            Assert.fail();
        } catch (TimeoutException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("timed out test"));
        }
    }

    @Test
    public void testLockWhenFailure() throws Exception {
        ClusterTierClientEntity clusterTierClientEntity = Mockito.mock(ClusterTierClientEntity.class);
        LockManagerImpl lockManager = new LockManagerImpl(clusterTierClientEntity);
        LockSuccess lockSuccess = getLockSuccessResponse();
        Mockito.when(clusterTierClientEntity.invokeAndWaitForComplete(ArgumentMatchers.any(LockMessage.class), ArgumentMatchers.anyBoolean())).thenReturn(lockFailure(), lockFailure(), lockFailure(), lockSuccess);
        Chain lock = lockManager.lock(2L);
        Assert.assertThat(lock, Matchers.notNullValue());
        Assert.assertThat(lock.length(), Matchers.is(3));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnlockClearsLocksHeldState() throws Exception {
        ClusterTierClientEntity clusterTierClientEntity = Mockito.mock(ClusterTierClientEntity.class);
        LockManagerImpl lockManager = new LockManagerImpl(clusterTierClientEntity);
        LockSuccess lockSuccess = getLockSuccessResponse();
        Mockito.when(clusterTierClientEntity.invokeAndWaitForComplete(ArgumentMatchers.any(LockMessage.class), ArgumentMatchers.anyBoolean())).thenReturn(lockSuccess);
        Chain lock = lockManager.lock(2L);
        lockManager.unlock(2L, false);
        ClusterTierReconnectMessage reconnectMessage = Mockito.mock(ClusterTierReconnectMessage.class);
        ArgumentCaptor<Set<Long>> locks = ArgumentCaptor.forClass(Set.class);
        Mockito.doNothing().when(reconnectMessage).addLocksHeld(locks.capture());
        lockManager.reconnectListener(reconnectMessage);
        Assert.assertThat(locks.getValue().size(), Matchers.is(0));
    }
}

