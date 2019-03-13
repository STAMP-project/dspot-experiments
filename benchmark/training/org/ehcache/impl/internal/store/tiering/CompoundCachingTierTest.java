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
package org.ehcache.impl.internal.store.tiering;


import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.ehcache.config.ResourceType;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.store.tiering.CachingTier;
import org.ehcache.core.spi.store.tiering.HigherCachingTier;
import org.ehcache.core.spi.store.tiering.LowerCachingTier;
import org.ehcache.impl.internal.util.UnmatchedResourceType;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.ehcache.config.ResourceType.Core.HEAP;
import static org.ehcache.config.ResourceType.Core.OFFHEAP;


/**
 *
 *
 * @author Ludovic Orban
 */
public class CompoundCachingTierTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testGetOrComputeIfAbsentComputesWhenBothTiersEmpty() throws Exception {
        HigherCachingTier<String, String> higherTier = Mockito.mock(HigherCachingTier.class);
        LowerCachingTier<String, String> lowerTier = Mockito.mock(LowerCachingTier.class);
        Store.ValueHolder<String> valueHolder = Mockito.mock(Store.ValueHolder.class);
        ArgumentCaptor<Function<String, Store.ValueHolder<String>>> functionArg = ArgumentCaptor.forClass(Function.class);
        ArgumentCaptor<String> keyArg = ArgumentCaptor.forClass(String.class);
        Mockito.when(higherTier.getOrComputeIfAbsent(keyArg.capture(), functionArg.capture())).then(( invocation) -> functionArg.getValue().apply(keyArg.getValue()));
        Mockito.when(lowerTier.getAndRemove(ArgumentMatchers.anyString())).thenReturn(null);
        CompoundCachingTier<String, String> compoundCachingTier = new CompoundCachingTier<>(higherTier, lowerTier);
        final AtomicBoolean computed = new AtomicBoolean(false);
        Assert.assertThat(compoundCachingTier.getOrComputeIfAbsent("1", ( s) -> {
            computed.set(true);
            return valueHolder;
        }), Is.is(valueHolder));
        Assert.assertThat(computed.get(), Is.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetOrComputeIfAbsentDoesNotComputesWhenHigherTierContainsValue() throws Exception {
        HigherCachingTier<String, String> higherTier = Mockito.mock(HigherCachingTier.class);
        LowerCachingTier<String, String> lowerTier = Mockito.mock(LowerCachingTier.class);
        final Store.ValueHolder<String> valueHolder = Mockito.mock(Store.ValueHolder.class);
        Mockito.when(higherTier.getOrComputeIfAbsent(ArgumentMatchers.anyString(), ArgumentMatchers.any(Function.class))).thenReturn(valueHolder);
        Mockito.when(lowerTier.getAndRemove(ArgumentMatchers.anyString())).thenThrow(AssertionError.class);
        CompoundCachingTier<String, String> compoundCachingTier = new CompoundCachingTier<>(higherTier, lowerTier);
        final AtomicBoolean computed = new AtomicBoolean(false);
        Assert.assertThat(compoundCachingTier.getOrComputeIfAbsent("1", ( s) -> {
            computed.set(true);
            return valueHolder;
        }), Is.is(valueHolder));
        Assert.assertThat(computed.get(), Is.is(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetOrComputeIfAbsentDoesNotComputesWhenLowerTierContainsValue() throws Exception {
        HigherCachingTier<String, String> higherTier = Mockito.mock(HigherCachingTier.class);
        LowerCachingTier<String, String> lowerTier = Mockito.mock(LowerCachingTier.class);
        Store.ValueHolder<String> valueHolder = Mockito.mock(Store.ValueHolder.class);
        ArgumentCaptor<Function<String, Store.ValueHolder<String>>> functionArg = ArgumentCaptor.forClass(Function.class);
        ArgumentCaptor<String> keyArg = ArgumentCaptor.forClass(String.class);
        Mockito.when(higherTier.getOrComputeIfAbsent(keyArg.capture(), functionArg.capture())).then(( invocation) -> functionArg.getValue().apply(keyArg.getValue()));
        Mockito.when(lowerTier.getAndRemove(ArgumentMatchers.anyString())).thenReturn(valueHolder);
        CompoundCachingTier<String, String> compoundCachingTier = new CompoundCachingTier<>(higherTier, lowerTier);
        final AtomicBoolean computed = new AtomicBoolean(false);
        Assert.assertThat(compoundCachingTier.getOrComputeIfAbsent("1", ( s) -> {
            computed.set(true);
            return valueHolder;
        }), Is.is(valueHolder));
        Assert.assertThat(computed.get(), Is.is(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetOrComputeIfAbsentComputesWhenLowerTierExpires() throws Exception {
        HigherCachingTier<String, String> higherTier = Mockito.mock(HigherCachingTier.class);
        LowerCachingTier<String, String> lowerTier = Mockito.mock(LowerCachingTier.class);
        Store.ValueHolder<String> originalValueHolder = Mockito.mock(Store.ValueHolder.class);
        Store.ValueHolder<String> newValueHolder = Mockito.mock(Store.ValueHolder.class);
        ArgumentCaptor<Function<String, Store.ValueHolder<String>>> functionArg = ArgumentCaptor.forClass(Function.class);
        ArgumentCaptor<String> keyArg = ArgumentCaptor.forClass(String.class);
        Mockito.when(higherTier.getOrComputeIfAbsent(keyArg.capture(), functionArg.capture())).then(( invocation) -> functionArg.getValue().apply(keyArg.getValue()));
        ArgumentCaptor<CachingTier.InvalidationListener<String, String>> invalidationListenerArg = ArgumentCaptor.forClass(CachingTier.InvalidationListener.class);
        Mockito.doNothing().when(lowerTier).setInvalidationListener(invalidationListenerArg.capture());
        Mockito.when(lowerTier.getAndRemove(ArgumentMatchers.anyString())).thenAnswer(( invocation) -> {
            String key = ((String) (invocation.getArguments()[0]));
            invalidationListenerArg.getValue().onInvalidation(key, originalValueHolder);
            return null;
        });
        final AtomicReference<Store.ValueHolder<String>> invalidated = new AtomicReference<>();
        CompoundCachingTier<String, String> compoundCachingTier = new CompoundCachingTier<>(higherTier, lowerTier);
        compoundCachingTier.setInvalidationListener(( key, valueHolder) -> invalidated.set(valueHolder));
        final AtomicBoolean computed = new AtomicBoolean(false);
        Assert.assertThat(compoundCachingTier.getOrComputeIfAbsent("1", ( s) -> {
            computed.set(true);
            return newValueHolder;
        }), Is.is(newValueHolder));
        Assert.assertThat(computed.get(), Is.is(true));
        Assert.assertThat(invalidated.get(), Is.is(originalValueHolder));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidateNoArg() throws Exception {
        HigherCachingTier<String, String> higherTier = Mockito.mock(HigherCachingTier.class);
        LowerCachingTier<String, String> lowerTier = Mockito.mock(LowerCachingTier.class);
        CompoundCachingTier<String, String> compoundCachingTier = new CompoundCachingTier<>(higherTier, lowerTier);
        compoundCachingTier.clear();
        Mockito.verify(higherTier, Mockito.times(1)).clear();
        Mockito.verify(lowerTier, Mockito.times(1)).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidateWhenNoValueDoesNotFireListener() throws Exception {
        HigherCachingTier<String, String> higherTier = Mockito.mock(HigherCachingTier.class);
        LowerCachingTier<String, String> lowerTier = Mockito.mock(LowerCachingTier.class);
        final AtomicReference<Store.ValueHolder<String>> invalidated = new AtomicReference<>();
        CompoundCachingTier<String, String> compoundCachingTier = new CompoundCachingTier<>(higherTier, lowerTier);
        compoundCachingTier.setInvalidationListener(( key, valueHolder) -> invalidated.set(valueHolder));
        compoundCachingTier.invalidate("1");
        Assert.assertThat(invalidated.get(), Is.is(Matchers.nullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidateWhenValueInLowerTierFiresListener() throws Exception {
        HigherCachingTier<String, String> higherTier = Mockito.mock(HigherCachingTier.class);
        LowerCachingTier<String, String> lowerTier = Mockito.mock(LowerCachingTier.class);
        Store.ValueHolder<String> valueHolder = Mockito.mock(Store.ValueHolder.class);
        AtomicReference<Store.ValueHolder<String>> higherTierValueHolder = new AtomicReference<>();
        AtomicReference<Store.ValueHolder<String>> lowerTierValueHolder = new AtomicReference<>(valueHolder);
        ArgumentCaptor<CachingTier.InvalidationListener<String, String>> higherTierInvalidationListenerArg = ArgumentCaptor.forClass(CachingTier.InvalidationListener.class);
        Mockito.doNothing().when(higherTier).setInvalidationListener(higherTierInvalidationListenerArg.capture());
        Mockito.doAnswer(( invocation) -> {
            String key = ((String) (invocation.getArguments()[0]));
            higherTierInvalidationListenerArg.getValue().onInvalidation(key, higherTierValueHolder.getAndSet(null));
            ((Function) (invocation.getArguments()[1])).apply(higherTierValueHolder.get());
            return null;
        }).when(higherTier).silentInvalidate(ArgumentMatchers.anyString(), ArgumentMatchers.any(Function.class));
        ArgumentCaptor<Function<String, Store.ValueHolder<String>>> functionArg = ArgumentCaptor.forClass(Function.class);
        ArgumentCaptor<String> keyArg = ArgumentCaptor.forClass(String.class);
        Mockito.when(lowerTier.installMapping(keyArg.capture(), functionArg.capture())).then(( invocation) -> lowerTierValueHolder.get());
        ArgumentCaptor<CachingTier.InvalidationListener<String, String>> lowerTierInvalidationListenerArg = ArgumentCaptor.forClass(CachingTier.InvalidationListener.class);
        Mockito.doNothing().when(lowerTier).setInvalidationListener(lowerTierInvalidationListenerArg.capture());
        Mockito.doAnswer(( invocation) -> {
            String key = ((String) (invocation.getArguments()[0]));
            lowerTierInvalidationListenerArg.getValue().onInvalidation(key, lowerTierValueHolder.getAndSet(null));
            return null;
        }).when(lowerTier).invalidate(ArgumentMatchers.anyString());
        AtomicReference<Store.ValueHolder<String>> invalidated = new AtomicReference<>();
        CompoundCachingTier<String, String> compoundCachingTier = new CompoundCachingTier<>(higherTier, lowerTier);
        compoundCachingTier.setInvalidationListener(( key, valueHolder1) -> invalidated.set(valueHolder1));
        compoundCachingTier.invalidate("1");
        Assert.assertThat(invalidated.get(), Is.is(valueHolder));
        Assert.assertThat(higherTierValueHolder.get(), Is.is(Matchers.nullValue()));
        Assert.assertThat(lowerTierValueHolder.get(), Is.is(Matchers.nullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidateWhenValueInHigherTierFiresListener() throws Exception {
        HigherCachingTier<String, String> higherTier = Mockito.mock(HigherCachingTier.class);
        LowerCachingTier<String, String> lowerTier = Mockito.mock(LowerCachingTier.class);
        Store.ValueHolder<String> valueHolder = Mockito.mock(Store.ValueHolder.class);
        AtomicReference<Store.ValueHolder<String>> higherTierValueHolder = new AtomicReference<>(valueHolder);
        AtomicReference<Store.ValueHolder<String>> lowerTierValueHolder = new AtomicReference<>();
        ArgumentCaptor<CachingTier.InvalidationListener<String, String>> higherTierInvalidationListenerArg = ArgumentCaptor.forClass(CachingTier.InvalidationListener.class);
        Mockito.doNothing().when(higherTier).setInvalidationListener(higherTierInvalidationListenerArg.capture());
        Mockito.doAnswer(( invocation) -> {
            String key = ((String) (invocation.getArguments()[0]));
            higherTierInvalidationListenerArg.getValue().onInvalidation(key, higherTierValueHolder.getAndSet(null));
            ((Function) (invocation.getArguments()[1])).apply(higherTierValueHolder.get());
            return null;
        }).when(higherTier).silentInvalidate(ArgumentMatchers.anyString(), ArgumentMatchers.any(Function.class));
        ArgumentCaptor<Function<String, Store.ValueHolder<String>>> functionArg = ArgumentCaptor.forClass(Function.class);
        ArgumentCaptor<String> keyArg = ArgumentCaptor.forClass(String.class);
        Mockito.when(lowerTier.installMapping(keyArg.capture(), functionArg.capture())).then(( invocation) -> {
            Object apply = functionArg.getValue().apply(keyArg.getValue());
            lowerTierValueHolder.set(((Store.ValueHolder<String>) (apply)));
            return apply;
        });
        final ArgumentCaptor<CachingTier.InvalidationListener<String, String>> lowerTierInvalidationListenerArg = ArgumentCaptor.forClass(CachingTier.InvalidationListener.class);
        Mockito.doNothing().when(lowerTier).setInvalidationListener(lowerTierInvalidationListenerArg.capture());
        Mockito.doAnswer(( invocation) -> {
            String key = ((String) (invocation.getArguments()[0]));
            lowerTierInvalidationListenerArg.getValue().onInvalidation(key, lowerTierValueHolder.getAndSet(null));
            return null;
        }).when(lowerTier).invalidate(ArgumentMatchers.anyString());
        final AtomicReference<Store.ValueHolder<String>> invalidated = new AtomicReference<>();
        CompoundCachingTier<String, String> compoundCachingTier = new CompoundCachingTier<>(higherTier, lowerTier);
        compoundCachingTier.setInvalidationListener(( key, valueHolder1) -> invalidated.set(valueHolder1));
        compoundCachingTier.invalidate("1");
        Assert.assertThat(invalidated.get(), Is.is(valueHolder));
        Assert.assertThat(higherTierValueHolder.get(), Is.is(Matchers.nullValue()));
        Assert.assertThat(lowerTierValueHolder.get(), Is.is(Matchers.nullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidateAllCoversBothTiers() throws Exception {
        HigherCachingTier<String, String> higherTier = Mockito.mock(HigherCachingTier.class);
        LowerCachingTier<String, String> lowerTier = Mockito.mock(LowerCachingTier.class);
        CompoundCachingTier<String, String> compoundCachingTier = new CompoundCachingTier<>(higherTier, lowerTier);
        compoundCachingTier.invalidateAll();
        Mockito.verify(higherTier).silentInvalidateAll(ArgumentMatchers.any(BiFunction.class));
        Mockito.verify(lowerTier).invalidateAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRankCachingTier() throws Exception {
        CompoundCachingTier.Provider provider = new CompoundCachingTier.Provider();
        HashSet<ResourceType<?>> resourceTypes = new HashSet<>(EnumSet.of(HEAP, OFFHEAP));
        Assert.assertThat(provider.rankCachingTier(resourceTypes, Collections.EMPTY_LIST), Is.is(2));
        resourceTypes.clear();
        resourceTypes.add(new UnmatchedResourceType());
        Assert.assertThat(provider.rankCachingTier(resourceTypes, Collections.EMPTY_LIST), Is.is(0));
    }
}

