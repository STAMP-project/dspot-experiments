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
package org.ehcache.clustered.client.internal.store;


import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.client.internal.store.ServerStoreProxy.ServerCallback;
import org.ehcache.clustered.common.internal.store.Chain;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.CombinableMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.ehcache.clustered.Matchers.hasPayloads;


public class CommonServerStoreProxyTest extends AbstractServerStoreProxyTest {
    @Test
    public void testGetKeyNotPresent() throws Exception {
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testGetKeyNotPresent");
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testGetKeyNotPresent", clientEntity, Mockito.mock(ServerCallback.class));
        Chain chain = serverStoreProxy.get(1);
        Assert.assertThat(chain.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testAppendKeyNotPresent() throws Exception {
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testAppendKeyNotPresent");
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testAppendKeyNotPresent", clientEntity, Mockito.mock(ServerCallback.class));
        serverStoreProxy.append(2, ChainUtils.createPayload(2));
        Chain chain = serverStoreProxy.get(2);
        Assert.assertThat(chain.isEmpty(), Matchers.is(false));
        Assert.assertThat(chain, org.ehcache.clustered.Matchers.hasPayloads(2L));
    }

    @Test
    public void testGetAfterMultipleAppendsOnSameKey() throws Exception {
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testGetAfterMultipleAppendsOnSameKey");
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testGetAfterMultipleAppendsOnSameKey", clientEntity, Mockito.mock(ServerCallback.class));
        serverStoreProxy.append(3L, ChainUtils.createPayload(3L));
        serverStoreProxy.append(3L, ChainUtils.createPayload(33L));
        serverStoreProxy.append(3L, ChainUtils.createPayload(333L));
        Chain chain = serverStoreProxy.get(3L);
        Assert.assertThat(chain.isEmpty(), Matchers.is(false));
        Assert.assertThat(chain, org.ehcache.clustered.Matchers.hasPayloads(3L, 33L, 333L));
    }

    @Test
    public void testGetAndAppendKeyNotPresent() throws Exception {
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testGetAndAppendKeyNotPresent");
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testGetAndAppendKeyNotPresent", clientEntity, Mockito.mock(ServerCallback.class));
        Chain chain = serverStoreProxy.getAndAppend(4L, ChainUtils.createPayload(4L));
        Assert.assertThat(chain.isEmpty(), Matchers.is(true));
        chain = serverStoreProxy.get(4L);
        Assert.assertThat(chain.isEmpty(), Matchers.is(false));
        Assert.assertThat(chain, org.ehcache.clustered.Matchers.hasPayloads(4L));
    }

    @Test
    public void testGetAndAppendMultipleTimesOnSameKey() throws Exception {
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testGetAndAppendMultipleTimesOnSameKey");
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testGetAndAppendMultipleTimesOnSameKey", clientEntity, Mockito.mock(ServerCallback.class));
        serverStoreProxy.getAndAppend(5L, ChainUtils.createPayload(5L));
        serverStoreProxy.getAndAppend(5L, ChainUtils.createPayload(55L));
        serverStoreProxy.getAndAppend(5L, ChainUtils.createPayload(555L));
        Chain chain = serverStoreProxy.getAndAppend(5L, ChainUtils.createPayload(5555L));
        Assert.assertThat(chain.isEmpty(), Matchers.is(false));
        Assert.assertThat(chain, org.ehcache.clustered.Matchers.hasPayloads(5L, 55L, 555L));
    }

    @Test
    public void testReplaceAtHeadSuccessFull() throws Exception {
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testReplaceAtHeadSuccessFull");
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testReplaceAtHeadSuccessFull", clientEntity, Mockito.mock(ServerCallback.class));
        serverStoreProxy.append(20L, ChainUtils.createPayload(200L));
        serverStoreProxy.append(20L, ChainUtils.createPayload(2000L));
        serverStoreProxy.append(20L, ChainUtils.createPayload(20000L));
        Chain expect = serverStoreProxy.get(20L);
        Chain update = ChainUtils.chainOf(ChainUtils.createPayload(400L));
        serverStoreProxy.replaceAtHead(20L, expect, update);
        Chain afterReplace = serverStoreProxy.get(20L);
        Assert.assertThat(afterReplace, org.ehcache.clustered.Matchers.hasPayloads(400L));
        serverStoreProxy.append(20L, ChainUtils.createPayload(4000L));
        serverStoreProxy.append(20L, ChainUtils.createPayload(40000L));
        serverStoreProxy.replaceAtHead(20L, afterReplace, ChainUtils.chainOf(ChainUtils.createPayload(800L)));
        Chain anotherReplace = serverStoreProxy.get(20L);
        Assert.assertThat(anotherReplace, org.ehcache.clustered.Matchers.hasPayloads(800L, 4000L, 40000L));
    }

    @Test
    public void testClear() throws Exception {
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testClear");
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testClear", clientEntity, Mockito.mock(ServerCallback.class));
        serverStoreProxy.append(1L, ChainUtils.createPayload(100L));
        serverStoreProxy.clear();
        Chain chain = serverStoreProxy.get(1);
        Assert.assertThat(chain.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testResolveRequestIsProcessedAtThreshold() throws Exception {
        ByteBuffer buffer = ChainUtils.createPayload(42L);
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testResolveRequestIsProcessed");
        ServerCallback serverCallback = Mockito.mock(ServerCallback.class);
        Mockito.when(serverCallback.compact(ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(long.class))).thenReturn(ChainUtils.chainOf(buffer.duplicate()));
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testResolveRequestIsProcessed", clientEntity, serverCallback);
        for (int i = 0; i < 8; i++) {
            serverStoreProxy.append(1L, buffer.duplicate());
        }
        Mockito.verify(serverCallback, Mockito.never()).compact(ArgumentMatchers.any(Chain.class));
        Assert.assertThat(serverStoreProxy.get(1L), org.ehcache.clustered.Matchers.hasPayloads(42L, 42L, 42L, 42L, 42L, 42L, 42L, 42L));
        // trigger compaction at > 8 entries
        serverStoreProxy.append(1L, buffer.duplicate());
        Mockito.verify(serverCallback).compact(ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(long.class));
        Assert.assertThat(serverStoreProxy.get(1L), org.ehcache.clustered.Matchers.hasPayloads(42L));
    }

    @Test
    public void testEmptyStoreIteratorIsEmpty() throws Exception {
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testEmptyStoreIteratorIsEmpty");
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testEmptyStoreIteratorIsEmpty", clientEntity, Mockito.mock(ServerCallback.class));
        Iterator<Chain> iterator = serverStoreProxy.iterator();
        Assert.assertThat(iterator.hasNext(), Matchers.is(false));
        try {
            iterator.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testSingleChainIterator() throws Exception {
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testSingleChainIterator");
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testSingleChainIterator", clientEntity, Mockito.mock(ServerCallback.class));
        serverStoreProxy.append(1L, ChainUtils.createPayload(42L));
        Iterator<Chain> iterator = serverStoreProxy.iterator();
        Assert.assertThat(iterator.hasNext(), Matchers.is(true));
        Assert.assertThat(iterator.next(), org.ehcache.clustered.Matchers.hasPayloads(42L));
        Assert.assertThat(iterator.hasNext(), Matchers.is(false));
        try {
            iterator.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testSingleChainMultipleElements() throws Exception {
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testSingleChainMultipleElements");
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testSingleChainMultipleElements", clientEntity, Mockito.mock(ServerCallback.class));
        serverStoreProxy.append(1L, ChainUtils.createPayload(42L));
        serverStoreProxy.append(1L, ChainUtils.createPayload(43L));
        Iterator<Chain> iterator = serverStoreProxy.iterator();
        Assert.assertThat(iterator.hasNext(), Matchers.is(true));
        Assert.assertThat(iterator.next(), org.ehcache.clustered.Matchers.hasPayloads(42L, 43L));
        Assert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
        try {
            iterator.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testMultipleChains() throws Exception {
        ClusterTierClientEntity clientEntity = CommonServerStoreProxyTest.createClientEntity("testMultipleChains");
        CommonServerStoreProxy serverStoreProxy = new CommonServerStoreProxy("testMultipleChains", clientEntity, Mockito.mock(ServerCallback.class));
        serverStoreProxy.append(1L, ChainUtils.createPayload(42L));
        serverStoreProxy.append(2L, ChainUtils.createPayload(43L));
        Iterator<Chain> iterator = serverStoreProxy.iterator();
        Matcher<Chain> chainOne = hasPayloads(42L);
        Matcher<Chain> chainTwo = hasPayloads(43L);
        Assert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        Chain next = iterator.next();
        Assert.assertThat(next, CombinableMatcher.either(chainOne).or(chainTwo));
        if (chainOne.matches(next)) {
            Assert.assertThat(iterator.hasNext(), Matchers.is(true));
            Assert.assertThat(iterator.next(), Matchers.is(chainTwo));
            Assert.assertThat(iterator.hasNext(), Matchers.is(false));
        } else {
            Assert.assertThat(iterator.hasNext(), Matchers.is(true));
            Assert.assertThat(iterator.next(), Matchers.is(chainOne));
            Assert.assertThat(iterator.hasNext(), Matchers.is(false));
        }
        try {
            iterator.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }
}

