/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ipc;


import org.apache.hadoop.ipc.metrics.RetryCacheMetrics;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link RetryCacheMetrics}
 */
public class TestRetryCacheMetrics {
    static final String cacheName = "NameNodeRetryCache";

    @Test
    public void testNames() {
        RetryCache cache = Mockito.mock(RetryCache.class);
        Mockito.when(cache.getCacheName()).thenReturn(TestRetryCacheMetrics.cacheName);
        RetryCacheMetrics metrics = RetryCacheMetrics.create(cache);
        metrics.incrCacheHit();
        metrics.incrCacheCleared();
        metrics.incrCacheCleared();
        metrics.incrCacheUpdated();
        metrics.incrCacheUpdated();
        metrics.incrCacheUpdated();
        checkMetrics(1, 2, 3);
    }
}

