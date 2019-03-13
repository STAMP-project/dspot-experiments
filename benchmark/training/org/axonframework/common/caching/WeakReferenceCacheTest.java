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
package org.axonframework.common.caching;


import Cache.EntryListener;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.axonframework.common.Registration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class WeakReferenceCacheTest {
    private WeakReferenceCache testSubject;

    private EntryListener mockListener;

    private Registration registration;

    @Test
    public void testItemPurgedWhenNoLongerReferenced() throws Exception {
        // Mockito holds a reference to all parameters, preventing GC
        registration.cancel();
        final Set<String> expiredEntries = new CopyOnWriteArraySet<>();
        testSubject.registerCacheEntryListener(new Cache.EntryListenerAdapter() {
            @Override
            public void onEntryExpired(Object key) {
                expiredEntries.add(key.toString());
            }
        });
        Object value = new Object();
        testSubject.put("test1", value);
        Assert.assertSame(value, testSubject.get("test1"));
        // dereference
        value = null;
        // hope for a true GC
        System.gc();
        for (int i = 0; (i < 5) && (testSubject.containsKey("test1")); i++) {
            // try again
            System.gc();
            Thread.sleep(100);
        }
        Assert.assertNull(testSubject.get("test1"));
        // the reference is gone, but it may take a 'while' for the reference to be queued
        for (int i = 0; (i < 5) && (!(expiredEntries.contains("test1"))); i++) {
            testSubject.get("test1");
            Thread.sleep(100);
        }
        Assert.assertEquals(Collections.singleton("test1"), expiredEntries);
    }

    @Test
    public void testEntryListenerNotifiedOfCreationUpdateAndDeletion() {
        Object value = new Object();
        Object value2 = new Object();
        testSubject.put("test1", value);
        Mockito.verify(mockListener).onEntryCreated("test1", value);
        testSubject.put("test1", value2);
        Mockito.verify(mockListener).onEntryUpdated("test1", value2);
        testSubject.get("test1");
        Mockito.verify(mockListener).onEntryRead("test1", value2);
        testSubject.remove("test1");
        Mockito.verify(mockListener).onEntryRemoved("test1");
        Assert.assertNull(testSubject.get("test1"));
        Mockito.verifyNoMoreInteractions(mockListener);
    }
}

