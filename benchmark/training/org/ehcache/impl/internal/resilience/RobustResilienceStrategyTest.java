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
package org.ehcache.impl.internal.resilience;


import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.ehcache.spi.resilience.RecoveryStore;
import org.ehcache.spi.resilience.StoreAccessException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class RobustResilienceStrategyTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private RecoveryStore<Integer> store;

    @InjectMocks
    private RobustResilienceStrategy<Integer, Long> strategy;

    private final StoreAccessException accessException = new StoreAccessException("The exception");

    @Test
    public void getFailure() throws StoreAccessException {
        assertThat(strategy.getFailure(1, accessException)).isNull();
        Mockito.verify(store).obliterate(1);
    }

    @Test
    public void containsKeyFailure() throws StoreAccessException {
        assertThat(strategy.containsKeyFailure(1, accessException)).isFalse();
        Mockito.verify(store).obliterate(1);
    }

    @Test
    public void putFailure() throws StoreAccessException {
        strategy.putFailure(1, 1L, accessException);
        Mockito.verify(store).obliterate(1);
    }

    @Test
    public void removeFailure() throws StoreAccessException {
        assertThat(strategy.removeFailure(1, 1L, accessException)).isFalse();
        Mockito.verify(store).obliterate(1);
    }

    @Test
    public void clearFailure() throws StoreAccessException {
        strategy.clearFailure(accessException);
        Mockito.verify(store).obliterate();
    }

    @Test
    public void putIfAbsentFailure() throws StoreAccessException {
        assertThat(strategy.putIfAbsentFailure(1, 1L, accessException)).isNull();
        Mockito.verify(store).obliterate(1);
    }

    @Test
    public void removeFailure1() throws StoreAccessException {
        assertThat(strategy.removeFailure(1, 1L, accessException)).isFalse();
        Mockito.verify(store).obliterate(1);
    }

    @Test
    public void replaceFailure() throws StoreAccessException {
        assertThat(strategy.replaceFailure(1, 1L, accessException)).isNull();
        Mockito.verify(store).obliterate(1);
    }

    @Test
    public void replaceFailure1() throws StoreAccessException {
        assertThat(strategy.replaceFailure(1, 1L, 2L, accessException)).isFalse();
        Mockito.verify(store).obliterate(1);
    }

    @Test
    public void getAllFailure() throws StoreAccessException {
        assertThat(strategy.getAllFailure(Arrays.asList(1, 2), accessException)).containsExactly(entry(1, null), entry(2, null));
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<Integer>> captor = ArgumentCaptor.forClass(Iterable.class);
        Mockito.verify(store).obliterate(captor.capture());
        assertThat(captor.getValue()).contains(1, 2);
    }

    @Test
    public void putAllFailure() throws StoreAccessException {
        strategy.putAllFailure(Stream.of(1, 2).collect(Collectors.toMap(Function.identity(), ( k) -> ((long) (k)))), accessException);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<Integer>> captor = ArgumentCaptor.forClass(Iterable.class);
        Mockito.verify(store).obliterate(captor.capture());
        assertThat(captor.getValue()).contains(1, 2);
    }

    @Test
    public void removeAllFailure() throws StoreAccessException {
        strategy.removeAllFailure(Arrays.asList(1, 2), accessException);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<Integer>> captor = ArgumentCaptor.forClass(Iterable.class);
        Mockito.verify(store).obliterate(captor.capture());
        assertThat(captor.getValue()).contains(1, 2);
    }
}

