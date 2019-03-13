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


import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.Matchers;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.clustered.common.internal.store.Element;
import org.ehcache.clustered.common.internal.store.ServerStore;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verify Server Store
 */
public abstract class ServerStoreTest {
    private final ChainBuilder chainBuilder = newChainBuilder();

    private final ElementBuilder elementBuilder = newElementBuilder();

    @Test
    public void testGetNoMappingExists() throws Exception {
        ServerStore store = newStore();
        Chain chain = store.get(1);
        Assert.assertThat(chain.isEmpty(), Is.is(true));
        Assert.assertThat(chain.iterator().hasNext(), Is.is(false));
    }

    @Test
    public void testGetMappingExists() throws Exception {
        ServerStore store = newStore();
        ServerStoreTest.populateStore(store);
        Chain chain = store.get(1L);
        Assert.assertThat(chain.isEmpty(), Is.is(false));
        Assert.assertThat(chain, Matchers.hasPayloads(1L));
    }

    @Test
    public void testAppendNoMappingExists() throws Exception {
        ServerStore store = newStore();
        store.append(1L, ChainUtils.createPayload(1L));
        Chain chain = store.get(1L);
        Assert.assertThat(chain.isEmpty(), Is.is(false));
        Assert.assertThat(chain, Matchers.hasPayloads(1L));
    }

    @Test
    public void testAppendMappingExists() throws Exception {
        ServerStore store = newStore();
        ServerStoreTest.populateStore(store);
        store.append(2L, ChainUtils.createPayload(22L));
        Chain chain = store.get(2L);
        Assert.assertThat(chain.isEmpty(), Is.is(false));
        Assert.assertThat(chain, Matchers.hasPayloads(2L, 22L));
    }

    @Test
    public void testGetAndAppendNoMappingExists() throws Exception {
        ServerStore store = newStore();
        Chain chain = store.getAndAppend(1, ChainUtils.createPayload(1));
        Assert.assertThat(chain.isEmpty(), Is.is(true));
        chain = store.get(1);
        Assert.assertThat(chain, Matchers.hasPayloads(1L));
    }

    @Test
    public void testGetAndAppendMappingExists() throws Exception {
        ServerStore store = newStore();
        ServerStoreTest.populateStore(store);
        Chain chain = store.getAndAppend(1, ChainUtils.createPayload(22));
        for (Element element : chain) {
            Assert.assertThat(ChainUtils.readPayload(element.getPayload()), Is.is(Long.valueOf(1)));
        }
        chain = store.get(1);
        Assert.assertThat(chain, Matchers.hasPayloads(1, 22));
    }

    @Test
    public void testReplaceAtHeadSucceedsMappingExistsHeadMatchesStrictly() throws Exception {
        ServerStore store = newStore();
        ServerStoreTest.populateStore(store);
        Chain existingMapping = store.get(1);
        store.replaceAtHead(1, existingMapping, chainBuilder.build(elementBuilder.build(ChainUtils.createPayload(11))));
        Chain chain = store.get(1);
        Assert.assertThat(chain, Matchers.hasPayloads(11));
        store.append(2, ChainUtils.createPayload(22));
        store.append(2, ChainUtils.createPayload(222));
        existingMapping = store.get(2);
        store.replaceAtHead(2, existingMapping, chainBuilder.build(elementBuilder.build(ChainUtils.createPayload(2222))));
        chain = store.get(2);
        Assert.assertThat(chain, Matchers.hasPayloads(2222));
    }

    @Test
    public void testReplaceAtHeadSucceedsMappingExistsHeadMatches() throws Exception {
        ServerStore store = newStore();
        ServerStoreTest.populateStore(store);
        Chain existingMapping = store.get(1);
        store.append(1, ChainUtils.createPayload(11));
        store.replaceAtHead(1, existingMapping, chainBuilder.build(elementBuilder.build(ChainUtils.createPayload(111))));
        Chain chain = store.get(1);
        Assert.assertThat(chain, Matchers.hasPayloads(111, 11));
        store.append(2, ChainUtils.createPayload(22));
        existingMapping = store.get(2);
        store.append(2, ChainUtils.createPayload(222));
        store.replaceAtHead(2, existingMapping, chainBuilder.build(elementBuilder.build(ChainUtils.createPayload(2222))));
        chain = store.get(2);
        Assert.assertThat(chain, Matchers.hasPayloads(2222, 222));
    }

    @Test
    public void testReplaceAtHeadIgnoredMappingExistsHeadMisMatch() throws Exception {
        ServerStore store = newStore();
        ServerStoreTest.populateStore(store);
        store.append(1, ChainUtils.createPayload(11));
        store.append(1, ChainUtils.createPayload(111));
        Chain mappingReadFirst = store.get(1);
        store.replaceAtHead(1, mappingReadFirst, chainBuilder.build(elementBuilder.build(ChainUtils.createPayload(111))));
        Chain current = store.get(1);
        Assert.assertThat(current, Matchers.hasPayloads(111));
        store.append(1, ChainUtils.createPayload(1111));
        store.replaceAtHead(1, mappingReadFirst, chainBuilder.build(elementBuilder.build(ChainUtils.createPayload(11111))));
        Chain toVerify = store.get(1);
        Assert.assertThat(toVerify, Matchers.hasPayloads(111, 1111));
    }

    @Test
    public void test_append_doesNotConsumeBuffer() throws Exception {
        ServerStore store = newStore();
        ByteBuffer payload = ChainUtils.createPayload(1L);
        store.append(1L, payload);
        Assert.assertThat(payload.remaining(), Is.is(8));
    }

    @Test
    public void test_getAndAppend_doesNotConsumeBuffer() throws Exception {
        ServerStore store = newStore();
        ByteBuffer payload = ChainUtils.createPayload(1L);
        store.getAndAppend(1L, payload);
        Assert.assertThat(payload.remaining(), Is.is(8));
    }

    @Test
    public void test_replaceAtHead_doesNotConsumeBuffer() {
        ServerStore store = newStore();
        ByteBuffer payload = ChainUtils.createPayload(1L);
        Chain expected = newChainBuilder().build(newElementBuilder().build(payload), newElementBuilder().build(payload));
        Chain update = newChainBuilder().build(newElementBuilder().build(payload));
        store.replaceAtHead(1L, expected, update);
        Assert.assertThat(payload.remaining(), Is.is(8));
    }

    @Test
    public void testEmptyIterator() throws TimeoutException {
        ServerStore store = newStore();
        Iterator<Chain> chainIterator = store.iterator();
        Assert.assertThat(chainIterator.hasNext(), Is.is(false));
        try {
            chainIterator.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testSingleElementIterator() throws TimeoutException {
        ServerStore store = newStore();
        store.append(1L, ChainUtils.createPayload(42L));
        Iterator<Chain> chainIterator = store.iterator();
        Assert.assertThat(chainIterator.hasNext(), Is.is(true));
        Assert.assertThat(chainIterator.next(), Matchers.hasPayloads(42L));
        Assert.assertThat(chainIterator.hasNext(), Is.is(false));
        try {
            chainIterator.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testHeavilyPopulatedIterator() throws TimeoutException {
        ServerStore store = newStore();
        LongStream.range(0, 100).forEach(( k) -> {
            try {
                store.append(k, ChainUtils.createPayload(k));
            } catch (TimeoutException e) {
                throw new AssertionError();
            }
        });
        Iterator<Chain> chainIterator = store.iterator();
        Set<Long> longs = new HashSet<>();
        while (chainIterator.hasNext()) {
            Chain chain = chainIterator.next();
            for (Element e : chain) {
                long l = ChainUtils.readPayload(e.getPayload());
                Assert.assertThat(longs, IsNot.not(IsCollectionContaining.hasItem(l)));
                longs.add(l);
            }
        } 
        Assert.assertThat(longs, IsCollectionContaining.hasItems(LongStream.range(0, 100).boxed().toArray(Long[]::new)));
    }
}

