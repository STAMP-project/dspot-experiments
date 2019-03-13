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


import java.time.Duration;
import org.ehcache.clustered.client.TestTimeSource;
import org.ehcache.clustered.client.internal.store.ResolvedChain;
import org.ehcache.clustered.client.internal.store.operations.ChainResolver;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.clustered.common.internal.store.operations.codecs.OperationsCodec;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.core.spi.time.TimeSource;
import org.ehcache.expiry.ExpiryPolicy;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ExpiryChainResolverTest extends AbstractChainResolverTest {
    @Test
    @Override
    public void testCompactDecodesOperationValueOnlyOnDemand() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 1), new PutOperation(1L, "Suresh", 2), new PutOperation(1L, "Matthew", 3));
        AbstractChainResolverTest.CountingLongSerializer keySerializer = new AbstractChainResolverTest.CountingLongSerializer();
        AbstractChainResolverTest.CountingStringSerializer valueSerializer = new AbstractChainResolverTest.CountingStringSerializer();
        OperationsCodec<Long, String> customCodec = new OperationsCodec(keySerializer, valueSerializer);
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration(), customCodec);
        resolver.compactChain(chain, 0L);
        Assert.assertThat(keySerializer.decodeCount, Matchers.is(3));
        Assert.assertThat(valueSerializer.decodeCount, Matchers.is(3));
        Assert.assertThat(valueSerializer.encodeCount, Matchers.is(0));
        Assert.assertThat(keySerializer.encodeCount, Matchers.is(1));// One encode from encoding the resolved operation's key

    }

    @Test
    @Override
    public void testResolveDecodesOperationValueOnlyOnDemand() {
        Chain chain = getChainFromOperations(new PutOperation(1L, "Albin", 1), new PutOperation(1L, "Suresh", 2), new PutOperation(1L, "Matthew", 3));
        AbstractChainResolverTest.CountingLongSerializer keySerializer = new AbstractChainResolverTest.CountingLongSerializer();
        AbstractChainResolverTest.CountingStringSerializer valueSerializer = new AbstractChainResolverTest.CountingStringSerializer();
        OperationsCodec<Long, String> customCodec = new OperationsCodec(keySerializer, valueSerializer);
        ChainResolver<Long, String> resolver = createChainResolver(ExpiryPolicyBuilder.noExpiration(), customCodec);
        resolver.resolve(chain, 1L, 0L);
        Assert.assertThat(keySerializer.decodeCount, Matchers.is(3));
        Assert.assertThat(valueSerializer.decodeCount, Matchers.is(3));
        Assert.assertThat(valueSerializer.encodeCount, Matchers.is(0));
        Assert.assertThat(keySerializer.encodeCount, Matchers.is(1));// One encode from encoding the resolved operation's key

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetExpiryForAccessIsIgnored() {
        TimeSource timeSource = new TestTimeSource();
        ExpiryPolicy<Long, String> expiry = Mockito.mock(ExpiryPolicy.class);
        ChainResolver<Long, String> chainResolver = createChainResolver(expiry);
        Mockito.when(expiry.getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(ExpiryPolicy.INFINITE);
        Chain chain = getChainFromOperations(new PutOperation(1L, "One", timeSource.getTimeMillis()), new PutOperation(1L, "Second", timeSource.getTimeMillis()));
        ResolvedChain<Long, String> resolvedChain = chainResolver.resolve(chain, 1L, timeSource.getTimeMillis());
        Mockito.verify(expiry, Mockito.times(0)).getExpiryForAccess(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        Mockito.verify(expiry, Mockito.times(1)).getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        Mockito.verify(expiry, Mockito.times(1)).getExpiryForUpdate(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyString());
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetExpiryForCreationIsInvokedOnlyOnce() {
        TimeSource timeSource = new TestTimeSource();
        ExpiryPolicy<Long, String> expiry = Mockito.mock(ExpiryPolicy.class);
        ChainResolver<Long, String> chainResolver = createChainResolver(expiry);
        Mockito.when(expiry.getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(ExpiryPolicy.INFINITE);
        Chain chain = getChainFromOperations(new PutOperation(1L, "One", timeSource.getTimeMillis()), new PutOperation(1L, "Second", timeSource.getTimeMillis()), new PutOperation(1L, "Three", timeSource.getTimeMillis()), new PutOperation(1L, "Four", timeSource.getTimeMillis()));
        ResolvedChain<Long, String> resolvedChain = chainResolver.resolve(chain, 1L, timeSource.getTimeMillis());
        InOrder inOrder = Mockito.inOrder(expiry);
        inOrder.verify(expiry, Mockito.times(1)).getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        inOrder.verify(expiry, Mockito.times(3)).getExpiryForUpdate(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyString());
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetExpiryForCreationIsNotInvokedForReplacedChains() {
        TimeSource timeSource = new TestTimeSource();
        ExpiryPolicy<Long, String> expiry = Mockito.mock(ExpiryPolicy.class);
        ChainResolver<Long, String> chainResolver = createChainResolver(expiry);
        Mockito.when(expiry.getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(ExpiryPolicy.INFINITE);
        Chain chain = getChainFromOperations(new PutOperation(1L, "Replaced", (-10L)), new PutOperation(1L, "SecondAfterReplace", timeSource.getTimeMillis()), new PutOperation(1L, "ThirdAfterReplace", timeSource.getTimeMillis()), new PutOperation(1L, "FourthAfterReplace", timeSource.getTimeMillis()));
        ResolvedChain<Long, String> resolvedChain = chainResolver.resolve(chain, 1L, timeSource.getTimeMillis());
        Mockito.verify(expiry, Mockito.times(0)).getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        Mockito.verify(expiry, Mockito.times(3)).getExpiryForUpdate(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyString());
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetExpiryForCreationIsInvokedAfterRemoveOperations() {
        TimeSource timeSource = new TestTimeSource();
        ExpiryPolicy<Long, String> expiry = Mockito.mock(ExpiryPolicy.class);
        ChainResolver<Long, String> chainResolver = createChainResolver(expiry);
        Mockito.when(expiry.getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(ExpiryPolicy.INFINITE);
        Chain replacedChain = getChainFromOperations(new PutOperation(1L, "Replaced", 10L), new PutOperation(1L, "SecondAfterReplace", 3L), new RemoveOperation(1L, 4L), new PutOperation(1L, "FourthAfterReplace", 5L));
        ResolvedChain<Long, String> resolvedChain = chainResolver.resolve(replacedChain, 1L, timeSource.getTimeMillis());
        InOrder inOrder = Mockito.inOrder(expiry);
        Mockito.verify(expiry, Mockito.times(0)).getExpiryForAccess(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        inOrder.verify(expiry, Mockito.times(1)).getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        inOrder.verify(expiry, Mockito.times(1)).getExpiryForUpdate(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyString());
        inOrder.verify(expiry, Mockito.times(1)).getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
        Mockito.reset(expiry);
        Mockito.when(expiry.getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(ExpiryPolicy.INFINITE);
        Chain chain = getChainFromOperations(new PutOperation(1L, "One", timeSource.getTimeMillis()), new PutOperation(1L, "Second", timeSource.getTimeMillis()), new RemoveOperation(1L, timeSource.getTimeMillis()), new PutOperation(1L, "Four", timeSource.getTimeMillis()));
        chainResolver.resolve(chain, 1L, timeSource.getTimeMillis());
        inOrder = Mockito.inOrder(expiry);
        Mockito.verify(expiry, Mockito.times(0)).getExpiryForAccess(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        inOrder.verify(expiry, Mockito.times(1)).getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        inOrder.verify(expiry, Mockito.times(1)).getExpiryForUpdate(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyString());
        inOrder.verify(expiry, Mockito.times(1)).getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNullGetExpiryForCreation() {
        TimeSource timeSource = new TestTimeSource();
        ExpiryPolicy<Long, String> expiry = Mockito.mock(ExpiryPolicy.class);
        ChainResolver<Long, String> chainResolver = createChainResolver(expiry);
        Mockito.when(expiry.getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(null);
        Chain chain = getChainFromOperations(new PutOperation(1L, "Replaced", 10L));
        ResolvedChain<?, ?> resolvedChain = chainResolver.resolve(chain, 1L, timeSource.getTimeMillis());
        Assert.assertTrue(resolvedChain.getCompactedChain().isEmpty());
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNullGetExpiryForUpdate() {
        TimeSource timeSource = new TestTimeSource();
        ExpiryPolicy<Long, String> expiry = Mockito.mock(ExpiryPolicy.class);
        ChainResolver<Long, String> chainResolver = createChainResolver(expiry);
        Mockito.when(expiry.getExpiryForUpdate(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(null);
        Chain chain = getChainFromOperations(new PutOperation(1L, "Replaced", (-10L)), new PutOperation(1L, "New", timeSource.getTimeMillis()));
        ResolvedChain<Long, String> resolvedChain = chainResolver.resolve(chain, 1L, timeSource.getTimeMillis());
        Assert.assertThat(resolvedChain.getResolvedResult(1L).getValue(), Matchers.is("New"));
        Assert.assertTrue(getOperationsListFromChain(resolvedChain.getCompactedChain()).get(0).isExpiryAvailable());
        Assert.assertThat(getOperationsListFromChain(resolvedChain.getCompactedChain()).get(0).expirationTime(), Matchers.is(10L));
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetExpiryForUpdateUpdatesExpirationTimeStamp() {
        TimeSource timeSource = new TestTimeSource();
        ExpiryPolicy<Long, String> expiry = Mockito.mock(ExpiryPolicy.class);
        ChainResolver<Long, String> chainResolver = createChainResolver(expiry);
        Mockito.when(expiry.getExpiryForUpdate(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(Duration.ofMillis(2L));
        Chain chain = getChainFromOperations(new PutOperation(1L, "Replaced", (-10L)), new PutOperation(1L, "New", timeSource.getTimeMillis()));
        ResolvedChain<Long, String> resolvedChain = chainResolver.resolve(chain, 1L, timeSource.getTimeMillis());
        Assert.assertThat(resolvedChain.getResolvedResult(1L).getValue(), Matchers.is("New"));
        Assert.assertTrue(getOperationsListFromChain(resolvedChain.getCompactedChain()).get(0).isExpiryAvailable());
        Assert.assertThat(getOperationsListFromChain(resolvedChain.getCompactedChain()).get(0).expirationTime(), Matchers.is(2L));
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExpiryThrowsException() {
        TimeSource timeSource = new TestTimeSource();
        ExpiryPolicy<Long, String> expiry = Mockito.mock(ExpiryPolicy.class);
        ChainResolver<Long, String> chainResolver = createChainResolver(expiry);
        Mockito.when(expiry.getExpiryForUpdate(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenThrow(new RuntimeException("Test Update Expiry"));
        Mockito.when(expiry.getExpiryForCreation(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenThrow(new RuntimeException("Test Create Expiry"));
        Chain chain = getChainFromOperations(new PutOperation(1L, "One", (-10L)), new PutOperation(1L, "Two", timeSource.getTimeMillis()));
        ResolvedChain<Long, String> resolvedChain = chainResolver.resolve(chain, 1L, timeSource.getTimeMillis());
        Assert.assertThat(resolvedChain.getResolvedResult(1L), IsNull.nullValue());
        chain = getChainFromOperations(new PutOperation(1L, "One", timeSource.getTimeMillis()), new PutOperation(1L, "Two", timeSource.getTimeMillis()));
        resolvedChain = chainResolver.resolve(chain, 1L, timeSource.getTimeMillis());
        Assert.assertThat(resolvedChain.getResolvedResult(1L), IsNull.nullValue());
        Assert.assertThat(resolvedChain.isCompacted(), Matchers.is(true));
    }
}

