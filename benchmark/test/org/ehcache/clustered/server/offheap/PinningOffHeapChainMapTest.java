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
package org.ehcache.clustered.server.offheap;


import java.nio.ByteBuffer;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.common.internal.store.Chain;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PinningOffHeapChainMapTest {
    @Test
    public void testAppendWithPinningOperation() {
        PinningOffHeapChainMap<Long> pinningOffHeapChainMap = getPinningOffHeapChainMap();
        pinningOffHeapChainMap.append(1L, buffer(PUT_WITH_WRITER));
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(true));
    }

    @Test
    public void testAppendWithNormalOperation() {
        PinningOffHeapChainMap<Long> pinningOffHeapChainMap = getPinningOffHeapChainMap();
        pinningOffHeapChainMap.append(1L, buffer(PUT));
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(false));
    }

    @Test
    public void testGetAndAppendWithPinningOperation() {
        PinningOffHeapChainMap<Long> pinningOffHeapChainMap = getPinningOffHeapChainMap();
        pinningOffHeapChainMap.getAndAppend(1L, buffer(REMOVE_CONDITIONAL));
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(true));
    }

    @Test
    public void testGetAndAppendWithNormalOperation() {
        PinningOffHeapChainMap<Long> pinningOffHeapChainMap = getPinningOffHeapChainMap();
        pinningOffHeapChainMap.getAndAppend(1L, buffer(PUT));
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(false));
    }

    @Test
    public void testPutWithPinningChain() {
        PinningOffHeapChainMap<Long> pinningOffHeapChainMap = getPinningOffHeapChainMap();
        pinningOffHeapChainMap.put(1L, ChainUtils.chainOf(buffer(PUT), buffer(REMOVE)));
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(true));
    }

    @Test
    public void testPutWithNormalChain() {
        PinningOffHeapChainMap<Long> pinningOffHeapChainMap = getPinningOffHeapChainMap();
        pinningOffHeapChainMap.put(1L, ChainUtils.chainOf(buffer(PUT), buffer(PUT)));
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(false));
    }

    @Test
    public void testReplaceAtHeadWithUnpinningChain() {
        PinningOffHeapChainMap<Long> pinningOffHeapChainMap = getPinningOffHeapChainMap();
        ByteBuffer buffer = buffer(PUT_IF_ABSENT);
        Chain pinningChain = ChainUtils.chainOf(buffer);
        Chain unpinningChain = ChainUtils.chainOf(buffer(PUT));
        pinningOffHeapChainMap.append(1L, buffer);
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(true));
        pinningOffHeapChainMap.replaceAtHead(1L, pinningChain, unpinningChain);
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(false));
    }

    @Test
    public void testReplaceAtHeadWithPinningChain() {
        PinningOffHeapChainMap<Long> pinningOffHeapChainMap = getPinningOffHeapChainMap();
        ByteBuffer buffer = buffer(REPLACE);
        Chain pinningChain = ChainUtils.chainOf(buffer);
        Chain unpinningChain = ChainUtils.chainOf(buffer(REPLACE_CONDITIONAL));
        pinningOffHeapChainMap.append(1L, buffer);
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(true));
        pinningOffHeapChainMap.replaceAtHead(1L, pinningChain, unpinningChain);
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(true));
    }

    @Test
    public void testReplaceAtHeadWithEmptyChain() {
        PinningOffHeapChainMap<Long> pinningOffHeapChainMap = getPinningOffHeapChainMap();
        ByteBuffer buffer = buffer(PUT_WITH_WRITER);
        Chain pinningChain = ChainUtils.chainOf(buffer);
        Chain unpinningChain = ChainUtils.chainOf();
        pinningOffHeapChainMap.append(1L, buffer);
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(true));
        pinningOffHeapChainMap.replaceAtHead(1L, pinningChain, unpinningChain);
        Assert.assertThat(pinningOffHeapChainMap.heads.isPinned(1L), Matchers.is(false));
    }
}

