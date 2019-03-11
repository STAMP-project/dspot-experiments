/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
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
package org.apache.geode.cache.lucene.internal;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.geode.InternalGemFireError;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.lucene.internal.repository.IndexRepository;
import org.apache.geode.cache.lucene.internal.repository.RepositoryManager;
import org.apache.geode.internal.Assert;
import org.apache.geode.internal.cache.BucketNotFoundException;
import org.apache.geode.internal.cache.EntrySnapshot;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.apache.lucene.store.AlreadyClosedException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;


/**
 * Unit test that async event listener dispatched the events to the appropriate repository.
 */
@Category({ LuceneTest.class })
public class LuceneEventListenerJUnitTest {
    private RepositoryManager manager;

    private LuceneEventListener listener;

    private InternalCache cache;

    @Test
    public void pdxReadSerializedFlagShouldBeResetBackToOriginalValueAfterProcessingEvents() {
        ArgumentCaptor valueCapture = ArgumentCaptor.forClass(Boolean.class);
        Mockito.doNothing().when(cache).setPdxReadSerializedOverride(((Boolean) (valueCapture.capture())));
        boolean originalPdxReadSerialized = cache.getPdxReadSerializedOverride();
        try {
            cache.setPdxReadSerializedOverride(true);
            Assert.assertTrue(((Boolean) (valueCapture.getValue())));
            listener.process(new LinkedList());
            Assert.assertTrue((!((Boolean) (valueCapture.getValue()))));
        } finally {
            cache.setPdxReadSerializedOverride(originalPdxReadSerialized);
        }
    }

    @Test
    public void testProcessBatch() throws Exception {
        IndexRepository repo1 = Mockito.mock(IndexRepository.class);
        IndexRepository repo2 = Mockito.mock(IndexRepository.class);
        Region region1 = Mockito.mock(Region.class);
        Region region2 = Mockito.mock(Region.class);
        Object callback1 = new Object();
        Mockito.when(manager.getRepository(ArgumentMatchers.eq(region1), ArgumentMatchers.any(), ArgumentMatchers.eq(callback1))).thenReturn(repo1);
        Mockito.when(manager.getRepository(ArgumentMatchers.eq(region2), ArgumentMatchers.any(), ArgumentMatchers.eq(null))).thenReturn(repo2);
        List<AsyncEvent> events = new ArrayList<AsyncEvent>();
        int numEntries = 100;
        for (int i = 0; i < numEntries; i++) {
            AsyncEvent event = Mockito.mock(AsyncEvent.class);
            Region region = ((i % 2) == 0) ? region1 : region2;
            Object callback = ((i % 2) == 0) ? callback1 : null;
            Mockito.when(event.getRegion()).thenReturn(region);
            Mockito.when(event.getKey()).thenReturn(i);
            Mockito.when(event.getCallbackArgument()).thenReturn(callback);
            switch (i % 4) {
                case 0 :
                case 1 :
                    final EntrySnapshot entry = Mockito.mock(EntrySnapshot.class);
                    Mockito.when(entry.getRawValue(true)).thenReturn(i);
                    Mockito.when(region.getEntry(ArgumentMatchers.eq(i))).thenReturn(entry);
                    break;
                case 2 :
                case 3 :
                    // Do nothing, get value will return a destroy
                    break;
            }
            events.add(event);
        }
        listener.processEvents(events);
        Mockito.verify(repo1, Mockito.atLeast((numEntries / 4))).delete(ArgumentMatchers.any());
        Mockito.verify(repo1, Mockito.atLeast((numEntries / 4))).update(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(repo2, Mockito.atLeast((numEntries / 4))).delete(ArgumentMatchers.any());
        Mockito.verify(repo2, Mockito.atLeast((numEntries / 4))).update(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(repo1, Mockito.times(1)).commit();
        Mockito.verify(repo2, Mockito.times(1)).commit();
    }

    @Test
    public void shouldHandleBucketNotFoundException() throws BucketNotFoundException {
        Mockito.when(manager.getRepository(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(BucketNotFoundException.class);
        AsyncEvent event = Mockito.mock(AsyncEvent.class);
        boolean result = listener.processEvents(Arrays.asList(event));
        assertFalse(result);
        Mockito.verify(listener, Mockito.times(1)).logDebugMessage(ArgumentMatchers.startsWith("Bucket not found"), ArgumentMatchers.any(BucketNotFoundException.class));
    }

    @Test
    public void shouldHandleCacheClosedException() throws BucketNotFoundException {
        Mockito.when(manager.getRepository(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(CacheClosedException.class);
        AsyncEvent event = Mockito.mock(AsyncEvent.class);
        boolean result = listener.processEvents(Arrays.asList(event));
        assertFalse(result);
        Mockito.verify(listener, Mockito.times(1)).logDebugMessage(ArgumentMatchers.contains("cache has been closed"), ArgumentMatchers.any(CacheClosedException.class));
    }

    @Test
    public void shouldHandleAlreadyClosedException() throws BucketNotFoundException {
        Mockito.when(manager.getRepository(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(AlreadyClosedException.class);
        AsyncEvent event = Mockito.mock(AsyncEvent.class);
        boolean result = listener.processEvents(Arrays.asList(event));
        assertFalse(result);
        Mockito.verify(listener, Mockito.times(1)).logDebugMessage(ArgumentMatchers.contains("the lucene index is already closed"), ArgumentMatchers.any(AlreadyClosedException.class));
    }

    @Test
    public void shouldThrowAndCaptureIOException() throws BucketNotFoundException {
        Mockito.doAnswer(( m) -> {
            throw new IOException();
        }).when(manager).getRepository(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        AtomicReference<Throwable> lastException = new AtomicReference<>();
        LuceneEventListener.setExceptionObserver(lastException::set);
        AsyncEvent event = Mockito.mock(AsyncEvent.class);
        try {
            listener.processEvents(Arrays.asList(event));
            fail("should have thrown an exception");
        } catch (InternalGemFireError expected) {
            assertEquals(expected, lastException.get());
        }
    }
}

