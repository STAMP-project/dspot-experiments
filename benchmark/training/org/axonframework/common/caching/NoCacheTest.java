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
import java.util.HashMap;
import java.util.Map;
import javax.cache.CacheException;
import org.axonframework.common.Registration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static NoCache.INSTANCE;


/**
 *
 *
 * @author Allard Buijze
 */
public class NoCacheTest {
    @Test
    public void testCacheDoesNothing() throws CacheException {
        // this is pretty stupid, but we're testing that it does absolutely nothing
        NoCache cache = INSTANCE;
        Registration registration = cache.registerCacheEntryListener(Mockito.mock(EntryListener.class));
        Assert.assertFalse(cache.containsKey(new Object()));
        Assert.assertNull(cache.get(new Object()));
        cache.put(new Object(), new Object());
        Map<Object, Object> map = new HashMap<>();
        map.put(new Object(), new Object());
        Assert.assertFalse(cache.remove(new Object()));
        registration.cancel();
    }
}

