/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.lookup.namespace;


import CacheScheduler.VersionedCache;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.druid.java.util.common.lifecycle.Lifecycle;
import org.apache.druid.query.lookup.namespace.StaticMapExtractionNamespace;
import org.apache.druid.server.lookup.namespace.cache.CacheScheduler;
import org.junit.Assert;
import org.junit.Test;


public class StaticMapCacheGeneratorTest {
    private static final Map<String, String> MAP = ImmutableMap.<String, String>builder().put("foo", "bar").build();

    private Lifecycle lifecycle;

    private CacheScheduler scheduler;

    @Test
    public void testSimpleGenerator() {
        final StaticMapCacheGenerator factory = new StaticMapCacheGenerator();
        final StaticMapExtractionNamespace namespace = new StaticMapExtractionNamespace(StaticMapCacheGeneratorTest.MAP);
        CacheScheduler.VersionedCache versionedCache = factory.generateCache(namespace, null, null, scheduler);
        Assert.assertNotNull(versionedCache);
        Assert.assertEquals(factory.getVersion(), versionedCache.getVersion());
        Assert.assertEquals(StaticMapCacheGeneratorTest.MAP, versionedCache.getCache());
    }

    @Test(expected = AssertionError.class)
    public void testNonNullLastVersionCausesAssertionError() {
        final StaticMapCacheGenerator factory = new StaticMapCacheGenerator();
        final StaticMapExtractionNamespace namespace = new StaticMapExtractionNamespace(StaticMapCacheGeneratorTest.MAP);
        factory.generateCache(namespace, null, factory.getVersion(), scheduler);
    }
}

