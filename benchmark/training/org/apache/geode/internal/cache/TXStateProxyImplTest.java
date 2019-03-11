/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.LocalRegion.NonTXEntry;
import org.apache.geode.internal.cache.region.entry.RegionEntryFactoryBuilder;
import org.junit.Test;
import org.mockito.Mockito;


public class TXStateProxyImplTest {
    private InternalCache cache;

    private LocalRegion region;

    String key = "testkey";

    TXStateProxyImpl tx;

    LocalRegionDataView view;

    private TXId txId;

    private TXManagerImpl txManager;

    private DistributedSystem system;

    private DistributedMember member;

    @Test
    public void getKeyForIteratorReturnsKey() {
        RegionEntryFactory regionEntryFactory = new RegionEntryFactoryBuilder().create(false, false, false, false, false);
        RegionEntry regionEntry = regionEntryFactory.createEntry(region, key, null);
        KeyInfo stringKeyInfo = new KeyInfo(key, null, null);
        KeyInfo regionEntryKeyInfo = new KeyInfo(regionEntry, null, null);
        boolean allowTombstones = false;
        boolean rememberReads = true;
        Mockito.when(region.getSharedDataView()).thenReturn(view);
        Mockito.when(view.getEntry(stringKeyInfo, region, allowTombstones)).thenReturn(Mockito.mock(NonTXEntry.class));
        Mockito.when(view.getKeyForIterator(stringKeyInfo, region, rememberReads, allowTombstones)).thenCallRealMethod();
        Mockito.when(view.getKeyForIterator(regionEntryKeyInfo, region, rememberReads, allowTombstones)).thenCallRealMethod();
        TXStateProxyImpl tx = new TXStateProxyImpl(cache, txManager, txId, false);
        Object key1 = tx.getKeyForIterator(regionEntryKeyInfo, region, rememberReads, allowTombstones);
        assertThat(key1.equals(key)).isTrue();
        Object key2 = tx.getKeyForIterator(stringKeyInfo, region, rememberReads, allowTombstones);
        assertThat(key2.equals(key)).isTrue();
    }

    @Test
    public void getCacheReturnsInjectedCache() {
        TXStateProxyImpl tx = new TXStateProxyImpl(cache, txManager, txId, false);
        assertThat(tx.getCache()).isSameAs(cache);
    }

    @Test
    public void isOverTransactionTimeoutLimitReturnsTrueIfHavingRecentOperation() {
        TXStateProxyImpl tx = Mockito.spy(new TXStateProxyImpl(cache, txManager, txId, false));
        Mockito.doReturn(0L).when(tx).getLastOperationTimeFromClient();
        Mockito.doReturn(1001L).when(tx).getCurrentTime();
        Mockito.when(txManager.getTransactionTimeToLive()).thenReturn(1);
        assertThat(tx.isOverTransactionTimeoutLimit()).isEqualTo(true);
    }

    @Test
    public void isOverTransactionTimeoutLimitReturnsFalseIfNotHavingRecentOperation() {
        TXStateProxyImpl tx = Mockito.spy(new TXStateProxyImpl(cache, txManager, txId, false));
        Mockito.doReturn(0L).when(tx).getLastOperationTimeFromClient();
        Mockito.doReturn(1000L).when(tx).getCurrentTime();
        Mockito.when(txManager.getTransactionTimeToLive()).thenReturn(1);
        assertThat(tx.isOverTransactionTimeoutLimit()).isEqualTo(false);
    }

    @Test
    public void setTargetWillSetTargetToItselfAndSetTXStateIfRealDealIsNull() {
        TXStateProxyImpl tx = Mockito.spy(new TXStateProxyImpl(cache, txManager, txId, false));
        assertThat(tx.hasRealDeal()).isFalse();
        assertThat(tx.getTarget()).isNull();
        tx.setTarget(member);
        assertThat(tx.getTarget()).isEqualTo(member);
        assertThat(tx.isRealDealLocal()).isTrue();
    }

    @Test
    public void setTargetWillSetTXStateStubIfTargetIsDifferentFromLocalMember() {
        TXStateProxyImpl tx = Mockito.spy(new TXStateProxyImpl(cache, txManager, txId, false));
        assertThat(tx.hasRealDeal()).isFalse();
        assertThat(tx.getTarget()).isNull();
        DistributedMember remoteMember = Mockito.mock(InternalDistributedMember.class);
        tx.setTarget(remoteMember);
        assertThat(tx.getTarget()).isEqualTo(remoteMember);
        assertThat(tx.isRealDealLocal()).isFalse();
        assertThat(tx.hasRealDeal()).isTrue();
    }

    @Test
    public void setTargetToItSelfIfRealDealIsTXStateAndTargetIsSameAsLocalMember() {
        TXStateProxyImpl tx = Mockito.spy(new TXStateProxyImpl(cache, txManager, txId, false));
        tx.setLocalTXState(new TXState(tx, true));
        assertThat(tx.isRealDealLocal()).isTrue();
        assertThat(tx.getTarget()).isNull();
        tx.setTarget(member);
        assertThat(tx.getTarget()).isEqualTo(member);
        assertThat(tx.isRealDealLocal()).isTrue();
    }

    @Test(expected = AssertionError.class)
    public void setTargetThrowsIfIfRealDealIsTXStateAndTargetIsDifferentFromLocalMember() {
        TXStateProxyImpl tx = Mockito.spy(new TXStateProxyImpl(cache, txManager, txId, false));
        tx.setLocalTXState(new TXState(tx, true));
        assertThat(tx.getTarget()).isNull();
        DistributedMember remoteMember = Mockito.mock(InternalDistributedMember.class);
        tx.setTarget(remoteMember);
    }
}

