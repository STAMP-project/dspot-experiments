/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.cache.impl;


import Cache.Entry;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.LinkedList;
import javax.cache.integration.CacheWriter;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.verify;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LatencyTrackingCacheWriterTest extends HazelcastTestSupport {
    private static final String NAME = "someCache";

    private StoreLatencyPlugin plugin;

    private CacheWriter<Integer, String> delegate;

    private LatencyTrackingCacheWriter<Integer, String> cacheWriter;

    @Test
    public void write() {
        Entry<Integer, String> entry = new CacheEntry<Integer, String>(1, "peter");
        cacheWriter.write(entry);
        verify(delegate).write(entry);
        assertProbeCalledOnce("write");
    }

    @Test
    public void writeAll() {
        Collection c = new LinkedList();
        cacheWriter.writeAll(c);
        verify(delegate).writeAll(c);
        assertProbeCalledOnce("writeAll");
    }

    @Test
    public void delete() {
        Entry<Integer, String> entry = new CacheEntry<Integer, String>(1, "peter");
        cacheWriter.delete(entry);
        verify(delegate).delete(entry);
        assertProbeCalledOnce("delete");
    }

    @Test
    public void deleteAll() {
        Collection c = new LinkedList();
        cacheWriter.deleteAll(c);
        verify(delegate).deleteAll(c);
        assertProbeCalledOnce("deleteAll");
    }
}

