/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.modelling.saga.repository;


import java.util.Collections;
import java.util.Set;
import net.sf.ehcache.CacheManager;
import org.axonframework.common.caching.Cache;
import org.axonframework.modelling.saga.AssociationValue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class CachingSagaStoreTest {
    private Cache associationsCache;

    private Cache sagaCache;

    private CachingSagaStore<StubSaga> testSubject;

    private CacheManager cacheManager;

    private Cache ehCache;

    private SagaStore<StubSaga> mockSagaStore;

    @Test
    public void testSagaAddedToCacheOnAdd() {
        testSubject.insertSaga(StubSaga.class, "123", new StubSaga(), Collections.singleton(new AssociationValue("key", "value")));
        Mockito.verify(sagaCache).put(ArgumentMatchers.eq("123"), ArgumentMatchers.any());
        Mockito.verify(associationsCache, Mockito.never()).put(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testAssociationsAddedToCacheOnLoad() {
        testSubject.insertSaga(StubSaga.class, "id", new StubSaga(), Collections.singleton(new AssociationValue("key", "value")));
        Mockito.verify(associationsCache, Mockito.never()).put(ArgumentMatchers.any(), ArgumentMatchers.any());
        ehCache.removeAll();
        Mockito.reset(sagaCache, associationsCache);
        final AssociationValue associationValue = new AssociationValue("key", "value");
        Set<String> actual = testSubject.findSagas(StubSaga.class, associationValue);
        Assert.assertEquals(actual, Collections.singleton("id"));
        Mockito.verify(associationsCache, Mockito.atLeast(1)).get("org.axonframework.modelling.saga.repository.StubSaga/key=value");
        Mockito.verify(associationsCache).put("org.axonframework.modelling.saga.repository.StubSaga/key=value", Collections.singleton("id"));
    }

    @Test
    public void testSagaAddedToCacheOnLoad() {
        StubSaga saga = new StubSaga();
        testSubject.insertSaga(StubSaga.class, "id", saga, Collections.singleton(new AssociationValue("key", "value")));
        ehCache.removeAll();
        Mockito.reset(sagaCache, associationsCache);
        SagaStore.Entry<StubSaga> actual = testSubject.loadSaga(StubSaga.class, "id");
        Assert.assertSame(saga, actual.saga());
        Mockito.verify(sagaCache).get("id");
        Mockito.verify(sagaCache).put(ArgumentMatchers.eq("id"), ArgumentMatchers.any());
        Mockito.verify(associationsCache, Mockito.never()).put(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testSagaNotAddedToCacheWhenLoadReturnsNull() {
        ehCache.removeAll();
        Mockito.reset(sagaCache, associationsCache);
        SagaStore.Entry<StubSaga> actual = testSubject.loadSaga(StubSaga.class, "id");
        Assert.assertNull(actual);
        Mockito.verify(sagaCache).get("id");
        Mockito.verify(sagaCache, Mockito.never()).put(ArgumentMatchers.eq("id"), ArgumentMatchers.any());
        Mockito.verify(associationsCache, Mockito.never()).put(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testCommitDelegatedAfterAddingToCache() {
        StubSaga saga = new StubSaga();
        AssociationValue associationValue = new AssociationValue("key", "value");
        testSubject.insertSaga(StubSaga.class, "123", saga, Collections.singleton(associationValue));
        Mockito.verify(associationsCache, Mockito.never()).put(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(mockSagaStore).insertSaga(StubSaga.class, "123", saga, Collections.singleton(associationValue));
    }
}

