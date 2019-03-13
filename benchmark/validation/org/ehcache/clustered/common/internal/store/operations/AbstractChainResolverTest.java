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
package org.ehcache.clustered.common.internal.store.operations;


import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.ehcache.clustered.client.internal.store.ResolvedChain;
import org.ehcache.clustered.client.internal.store.operations.ChainResolver;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.clustered.common.internal.store.operations.codecs.OperationsCodec;
import org.ehcache.clustered.common.internal.util.ChainBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.impl.serialization.LongSerializer;
import org.ehcache.impl.serialization.StringSerializer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractChainResolverTest {
    private static OperationsCodec<Long, String> codec = new OperationsCodec(new LongSerializer(), new StringSerializer());

    @Test
    @SuppressWarnings("unchecked")
    public void testResolveMaintainsOtherKeysInOrder() {
        Operation<Long, String> expected = new PutOperation(1L, "Suresh", 0L);
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new PutOperation(2L, "Albin", 0L), expected, new PutOperation(2L, "Suresh", 0L), new PutOperation(2L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Result<Long, String> result = resolvedChain.getResolvedResult(1L);
        Assert.assertEquals(expected, result);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(1));
        Chain compactedChain = resolvedChain.getCompactedChain();
        Assert.assertThat(compactedChain, // @SuppressWarnings("unchecked")
        contains(operation(new PutOperation(2L, "Albin", 0L)), operation(new PutOperation(2L, "Suresh", 0L)), operation(new PutOperation(2L, "Matthew", 0L)), operation(new PutOperation(1L, "Suresh", 0L))));
    }

    @Test
    public void testResolveEmptyChain() {
        Chain chain = getChainFromOperations();
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Result<Long, String> result = resolvedChain.getResolvedResult(1L);
        Assert.assertNull(result);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(false));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(0));
    }

    @Test
    public void testResolveChainWithNonExistentKey() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new PutOperation(2L, "Suresh", 0L), new PutOperation(2L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 3L, 0L);
        Result<Long, String> result = resolvedChain.getResolvedResult(3L);
        Assert.assertNull(result);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(false));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(0));
    }

    @Test
    public void testResolveSinglePut() {
        Operation<Long, String> expected = new PutOperation(1L, "Albin", 0L);
        Chain chain = getChainFromOperations(expected);
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Result<Long, String> result = resolvedChain.getResolvedResult(1L);
        Assert.assertEquals(expected, result);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(false));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(0));
    }

    @Test
    public void testResolvePutsOnly() {
        Operation<Long, String> expected = new PutOperation(1L, "Matthew", 0L);
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new PutOperation(1L, "Suresh", 0L), expected);
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Result<Long, String> result = resolvedChain.getResolvedResult(1L);
        Assert.assertEquals(expected, result);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(2));
    }

    @Test
    public void testResolveSingleRemove() {
        Chain chain = getChainFromOperations(new RemoveOperation(1L, 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Result<Long, String> result = resolvedChain.getResolvedResult(1L);
        Assert.assertNull(result);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(1));
    }

    @Test
    public void testResolveRemovesOnly() {
        Chain chain = getChainFromOperations(new RemoveOperation(1L, 0L), new RemoveOperation(1L, 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Result<Long, String> result = resolvedChain.getResolvedResult(1L);
        Assert.assertNull(result);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(2));
    }

    @Test
    public void testPutAndRemove() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new RemoveOperation(1L, 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Result<Long, String> result = resolvedChain.getResolvedResult(1L);
        Assert.assertNull(result);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(2));
    }

    @Test
    public void testResolvePutIfAbsentOnly() {
        Operation<Long, String> expected = new PutOperation(1L, "Matthew", 0L);
        Chain chain = getChainFromOperations(new PutIfAbsentOperation(1L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Result<Long, String> result = resolvedChain.getResolvedResult(1L);
        Assert.assertEquals(expected, result);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(false));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(0));
    }

    @Test
    public void testResolvePutIfAbsentsOnly() {
        Operation<Long, String> expected = new PutOperation(1L, "Albin", 0L);
        Chain chain = getChainFromOperations(new PutIfAbsentOperation(1L, "Albin", 0L), new PutIfAbsentOperation(1L, "Suresh", 0L), new PutIfAbsentOperation(1L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Result<Long, String> result = resolvedChain.getResolvedResult(1L);
        Assert.assertEquals(expected, result);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(2));
    }

    @Test
    public void testResolvePutIfAbsentSucceeds() {
        Operation<Long, String> expected = new PutOperation(1L, "Matthew", 0L);
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new RemoveOperation(1L, 0L), new PutIfAbsentOperation(1L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Result<Long, String> result = resolvedChain.getResolvedResult(1L);
        Assert.assertEquals(expected, result);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(2));
    }

    @Test
    public void testResolveForSingleOperationDoesNotCompact() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(false));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(0));
    }

    @Test
    public void testResolveForMultiplesOperationsAlwaysCompact() {
        // create a random mix of operations
        Chain chain = getChainFromOperations(new PutIfAbsentOperation(1L, "Albin", 0L), new PutOperation(1L, "Suresh", 0L), new PutOperation(1L, "Matthew", 0L), new PutOperation(2L, "Melvin", 0L), new ReplaceOperation(1L, "Joseph", 0L), new RemoveOperation(2L, 0L), new ConditionalRemoveOperation(1L, "Albin", 0L), new PutOperation(1L, "Gregory", 0L), new ConditionalReplaceOperation(1L, "Albin", "Abraham", 0L), new RemoveOperation(1L, 0L), new PutIfAbsentOperation(2L, "Albin", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 0L);
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(8));
    }

    @Test
    public void testResolveDoesNotDecodeOtherKeyOperationValues() {
        Chain chain = getChainFromOperations(new PutOperation(2L, "Albin", 0L), new PutOperation(2L, "Suresh", 0L), new PutOperation(2L, "Matthew", 0L));
        AbstractChainResolverTest.CountingLongSerializer keySerializer = new AbstractChainResolverTest.CountingLongSerializer();
        AbstractChainResolverTest.CountingStringSerializer valueSerializer = new AbstractChainResolverTest.CountingStringSerializer();
        OperationsCodec<Long, String> customCodec = new OperationsCodec(keySerializer, valueSerializer);
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration(), customCodec);
        resolver.resolve(chain, 1L, 0L);
        Assert.assertThat(keySerializer.decodeCount, Matchers.is(3));
        Assert.assertThat(valueSerializer.decodeCount, Matchers.is(0));
        Assert.assertThat(keySerializer.encodeCount, Matchers.is(0));
        Assert.assertThat(valueSerializer.encodeCount, Matchers.is(0));// No operation to resolve

    }

    @Test
    public void testResolveDecodesOperationValueOnlyOnDemand() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 1), new PutOperation(1L, "Suresh", 2), new PutOperation(1L, "Matthew", 3));
        AbstractChainResolverTest.CountingLongSerializer keySerializer = new AbstractChainResolverTest.CountingLongSerializer();
        AbstractChainResolverTest.CountingStringSerializer valueSerializer = new AbstractChainResolverTest.CountingStringSerializer();
        OperationsCodec<Long, String> customCodec = new OperationsCodec(keySerializer, valueSerializer);
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration(), customCodec);
        resolver.resolve(chain, 1L, 0L);
        Assert.assertThat(keySerializer.decodeCount, Matchers.is(3));
        Assert.assertThat(valueSerializer.decodeCount, Matchers.is(0));
        Assert.assertThat(valueSerializer.encodeCount, Matchers.is(0));
        Assert.assertThat(keySerializer.encodeCount, Matchers.is(1));// One encode from encoding the resolved operation's key

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompactingTwoKeys() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new PutOperation(2L, "Albin", 0L), new PutOperation(1L, "Suresh", 0L), new PutOperation(2L, "Suresh", 0L), new PutOperation(2L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compactedChain = resolver.compactChain(chain, 0L);
        Assert.assertThat(compactedChain, // @SuppressWarnings("unchecked")
        containsInAnyOrder(operation(new PutOperation(2L, "Matthew", 0L)), operation(new PutOperation(1L, "Suresh", 0L))));
    }

    @Test
    public void testCompactEmptyChain() {
        Chain chain = new ChainBuilder().build();
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compacted = resolver.compactChain(chain, 0L);
        Assert.assertThat(compacted, Matchers.emptyIterable());
    }

    @Test
    public void testCompactSinglePut() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compacted = resolver.compactChain(chain, 0L);
        Assert.assertThat(compacted, contains(operation(new PutOperation(1L, "Albin", 0L))));
    }

    @Test
    public void testCompactMultiplePuts() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new PutOperation(1L, "Suresh", 0L), new PutOperation(1L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compactedChain = resolver.compactChain(chain, 0L);
        Assert.assertThat(compactedChain, contains(operation(new PutOperation(1L, "Matthew", 0L))));
    }

    @Test
    public void testCompactSingleRemove() {
        Chain chain = getChainFromOperations(new RemoveOperation(1L, 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compactedChain = resolver.compactChain(chain, 0L);
        Assert.assertThat(compactedChain, Matchers.emptyIterable());
    }

    @Test
    public void testCompactMultipleRemoves() {
        Chain chain = getChainFromOperations(new RemoveOperation(1L, 0L), new RemoveOperation(1L, 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compactedChain = resolver.compactChain(chain, 0L);
        Assert.assertThat(compactedChain, Matchers.emptyIterable());
    }

    @Test
    public void testCompactPutAndRemove() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new RemoveOperation(1L, 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compactedChain = resolver.compactChain(chain, 0L);
        Assert.assertThat(compactedChain, Matchers.emptyIterable());
    }

    @Test
    public void testCompactSinglePutIfAbsent() {
        Chain chain = getChainFromOperations(new PutIfAbsentOperation(1L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compactedChain = resolver.compactChain(chain, 0L);
        Assert.assertThat(compactedChain, contains(operation(new PutOperation(1L, "Matthew", 0L))));
    }

    @Test
    public void testCompactMultiplePutIfAbsents() {
        Chain chain = getChainFromOperations(new PutIfAbsentOperation(1L, "Albin", 0L), new PutIfAbsentOperation(1L, "Suresh", 0L), new PutIfAbsentOperation(1L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compactedChain = resolver.compactChain(chain, 0L);
        Assert.assertThat(compactedChain, contains(operation(new PutOperation(1L, "Albin", 0L))));
    }

    @Test
    public void testCompactPutIfAbsentAfterRemove() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new RemoveOperation(1L, 0L), new PutIfAbsentOperation(1L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compactedChain = resolver.compactChain(chain, 0L);
        Assert.assertThat(compactedChain, contains(operation(new PutOperation(1L, "Matthew", 0L))));
    }

    @Test
    public void testCompactForMultipleKeysAndOperations() {
        // create a random mix of operations
        Chain chain = getChainFromOperations(new PutIfAbsentOperation(1L, "Albin", 0L), new PutOperation(1L, "Suresh", 0L), new PutOperation(1L, "Matthew", 0L), new PutOperation(2L, "Melvin", 0L), new ReplaceOperation(1L, "Joseph", 0L), new RemoveOperation(2L, 0L), new ConditionalRemoveOperation(1L, "Albin", 0L), new PutOperation(1L, "Gregory", 0L), new ConditionalReplaceOperation(1L, "Albin", "Abraham", 0L), new RemoveOperation(1L, 0L), new PutIfAbsentOperation(2L, "Albin", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compactedChain = resolver.compactChain(chain, 0L);
        Assert.assertThat(compactedChain, contains(operation(new PutOperation(2L, "Albin", 0L))));
    }

    @Test
    public void testCompactHasCorrectTimeStamp() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0), new PutOperation(1L, "Albin", 1), new RemoveOperation(1L, 2), new PutOperation(1L, "Albin", 3));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Chain compactedChain = resolver.compactChain(chain, 3);
        Assert.assertThat(compactedChain, contains(operation(new PutOperation(1L, "Albin", 3))));
    }

    @Test
    public void testCompactDecodesOperationValueOnlyOnDemand() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 1), new PutOperation(1L, "Suresh", 2), new PutOperation(1L, "Matthew", 3));
        AbstractChainResolverTest.CountingLongSerializer keySerializer = new AbstractChainResolverTest.CountingLongSerializer();
        AbstractChainResolverTest.CountingStringSerializer valueSerializer = new AbstractChainResolverTest.CountingStringSerializer();
        OperationsCodec<Long, String> customCodec = new OperationsCodec(keySerializer, valueSerializer);
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration(), customCodec);
        resolver.compactChain(chain, 0L);
        Assert.assertThat(keySerializer.decodeCount, Matchers.is(3));// Three decodes: one for each operation

        Assert.assertThat(keySerializer.encodeCount, Matchers.is(1));// One encode from encoding the resolved operation's key

        Assert.assertThat(valueSerializer.decodeCount, Matchers.is(0));
        Assert.assertThat(valueSerializer.encodeCount, Matchers.is(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResolvingTwoKeys() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new PutOperation(2L, "Albin", 0L), new PutOperation(1L, "Suresh", 0L), new PutOperation(2L, "Suresh", 0L), new PutOperation(2L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 0L);
        Assert.assertThat(resolved, hasEntry(2L, new PutOperation(2L, "Matthew", 0L)));
        Assert.assertThat(resolved, hasEntry(1L, new PutOperation(1L, "Suresh", 0L)));
    }

    @Test
    public void testFullResolveEmptyChain() {
        Chain chain = new ChainBuilder().build();
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 0L);
        Assert.assertThat(resolved, Matchers.is(Collections.emptyMap()));
    }

    @Test
    public void testFullResolveSinglePut() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 0L);
        Assert.assertThat(resolved, hasEntry(1L, new PutOperation(1L, "Albin", 0L)));
    }

    @Test
    public void testFullResolveMultiplePuts() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new PutOperation(1L, "Suresh", 0L), new PutOperation(1L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 0L);
        Assert.assertThat(resolved, hasEntry(1L, new PutOperation(1L, "Matthew", 0L)));
    }

    @Test
    public void testFullResolveSingleRemove() {
        Chain chain = getChainFromOperations(new RemoveOperation(1L, 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 0L);
        Assert.assertThat(resolved, Matchers.is(Collections.emptyMap()));
    }

    @Test
    public void testFullResolveMultipleRemoves() {
        Chain chain = getChainFromOperations(new RemoveOperation(1L, 0L), new RemoveOperation(1L, 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 0L);
        Assert.assertThat(resolved, Matchers.is(Collections.emptyMap()));
    }

    @Test
    public void testFullResolvePutAndRemove() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new RemoveOperation(1L, 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 0L);
        Assert.assertThat(resolved, Matchers.is(Collections.emptyMap()));
    }

    @Test
    public void testFullResolveSinglePutIfAbsent() {
        Chain chain = getChainFromOperations(new PutIfAbsentOperation(1L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 0L);
        Assert.assertThat(resolved, hasEntry(1L, new PutOperation(1L, "Matthew", 0L)));
    }

    @Test
    public void testFullResolveMultiplePutIfAbsents() {
        Chain chain = getChainFromOperations(new PutIfAbsentOperation(1L, "Albin", 0L), new PutIfAbsentOperation(1L, "Suresh", 0L), new PutIfAbsentOperation(1L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 0L);
        Assert.assertThat(resolved, hasEntry(1L, new PutOperation(1L, "Albin", 0L)));
    }

    @Test
    public void testFullResolvePutIfAbsentAfterRemove() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0L), new RemoveOperation(1L, 0L), new PutIfAbsentOperation(1L, "Matthew", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 0L);
        Assert.assertThat(resolved, hasEntry(1L, new PutOperation(1L, "Matthew", 0L)));
    }

    @Test
    public void testFullResolveForMultipleKeysAndOperations() {
        // create a random mix of operations
        Chain chain = getChainFromOperations(new PutIfAbsentOperation(1L, "Albin", 0L), new PutOperation(1L, "Suresh", 0L), new PutOperation(1L, "Matthew", 0L), new PutOperation(2L, "Melvin", 0L), new ReplaceOperation(1L, "Joseph", 0L), new RemoveOperation(2L, 0L), new ConditionalRemoveOperation(1L, "Albin", 0L), new PutOperation(1L, "Gregory", 0L), new ConditionalReplaceOperation(1L, "Albin", "Abraham", 0L), new RemoveOperation(1L, 0L), new PutIfAbsentOperation(2L, "Albin", 0L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 0L);
        Assert.assertThat(resolved, hasEntry(2L, new PutOperation(2L, "Albin", 0L)));
    }

    @Test
    public void testFullResolveHasCorrectTimeStamp() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 0), new PutOperation(1L, "Albin", 1), new RemoveOperation(1L, 2), new PutOperation(1L, "Albin", 3));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration());
        Map<Long, PutOperation<Long, String>> resolved = resolver.resolveChain(chain, 3);
        Assert.assertThat(resolved, hasEntry(1L, new PutOperation(1L, "Albin", 3)));
    }

    @Test
    public void testResolveForMultipleOperationHasCorrectIsFirstAndTimeStamp() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin1", 0), new PutOperation(1L, "Albin2", 1), new RemoveOperation(1L, 2), new PutOperation(1L, "AlbinAfterRemove", 3));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofHours(1)));
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 3);
        Operation<Long, String> operation = AbstractChainResolverTest.codec.decode(resolvedChain.getCompactedChain().iterator().next().getPayload());
        Assert.assertThat(operation.isExpiryAvailable(), Matchers.is(true));
        Assert.assertThat(operation.expirationTime(), Matchers.is(((TimeUnit.HOURS.toMillis(1)) + 3)));
        try {
            operation.timeStamp();
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.is("Timestamp not available"));
        }
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(3));
    }

    @Test
    public void testResolveForMultipleOperationHasCorrectIsFirstAndTimeStampWithExpiry() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin1", 0L), new PutOperation(1L, "Albin2", 1L), new PutOperation(1L, "Albin3", 2L), new PutOperation(1L, "Albin4", 3L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1L)));
        ResolvedChain<Long, String> resolvedChain = resolver.resolve(chain, 1L, 3L);
        Operation<Long, String> operation = AbstractChainResolverTest.codec.decode(resolvedChain.getCompactedChain().iterator().next().getPayload());
        Assert.assertThat(operation.isExpiryAvailable(), Matchers.is(true));
        Assert.assertThat(operation.expirationTime(), Matchers.is(4L));
        try {
            operation.timeStamp();
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.is("Timestamp not available"));
        }
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
        Assert.assertThat(resolvedChain.getCompactionCount(), Matchers.is(3));
    }

    @Test
    public void testCompactHasCorrectWithExpiry() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin1", 0L), new PutOperation(1L, "Albin2", 1L), new PutOperation(1L, "Albin3", 2L), new PutOperation(1L, "Albin4", 3L));
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1L)));
        Chain compactedChain = resolver.compactChain(chain, 3L);
        Assert.assertThat(compactedChain, contains(operation(new PutOperation(1L, "Albin4", 3L))));
    }

    protected static class CountingLongSerializer extends LongSerializer {
        protected int encodeCount = 0;

        protected int decodeCount = 0;

        @Override
        public ByteBuffer serialize(final Long object) {
            (encodeCount)++;
            return super.serialize(object);
        }

        @Override
        public Long read(final ByteBuffer binary) throws ClassNotFoundException {
            (decodeCount)++;
            return super.read(binary);
        }

        @Override
        public boolean equals(final Long object, final ByteBuffer binary) throws ClassNotFoundException {
            return super.equals(object, binary);
        }
    }

    protected static class CountingStringSerializer extends StringSerializer {
        protected int encodeCount = 0;

        protected int decodeCount = 0;

        @Override
        public ByteBuffer serialize(final String object) {
            (encodeCount)++;
            return super.serialize(object);
        }

        @Override
        public String read(final ByteBuffer binary) throws ClassNotFoundException {
            (decodeCount)++;
            return super.read(binary);
        }

        @Override
        public boolean equals(final String object, final ByteBuffer binary) throws ClassNotFoundException {
            return super.equals(object, binary);
        }
    }
}

