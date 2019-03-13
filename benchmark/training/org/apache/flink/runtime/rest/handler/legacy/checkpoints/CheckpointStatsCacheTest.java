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
package org.apache.flink.runtime.rest.handler.legacy.checkpoints;


import CheckpointStatsStatus.COMPLETED;
import CheckpointStatsStatus.IN_PROGRESS;
import org.apache.flink.runtime.checkpoint.AbstractCheckpointStats;
import org.apache.flink.runtime.rest.handler.job.checkpoints.CheckpointStatsCache;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the CheckpointStatsCache.
 */
public class CheckpointStatsCacheTest {
    @Test
    public void testZeroSizeCache() throws Exception {
        AbstractCheckpointStats checkpoint = createCheckpoint(0, COMPLETED);
        CheckpointStatsCache cache = new CheckpointStatsCache(0);
        cache.tryAdd(checkpoint);
        Assert.assertNull(cache.tryGet(0L));
    }

    @Test
    public void testCacheAddAndGet() throws Exception {
        AbstractCheckpointStats chk0 = createCheckpoint(0, COMPLETED);
        AbstractCheckpointStats chk1 = createCheckpoint(1, COMPLETED);
        AbstractCheckpointStats chk2 = createCheckpoint(2, IN_PROGRESS);
        CheckpointStatsCache cache = new CheckpointStatsCache(1);
        cache.tryAdd(chk0);
        Assert.assertEquals(chk0, cache.tryGet(0));
        cache.tryAdd(chk1);
        Assert.assertNull(cache.tryGet(0));
        Assert.assertEquals(chk1, cache.tryGet(1));
        cache.tryAdd(chk2);
        Assert.assertNull(cache.tryGet(2));
        Assert.assertNull(cache.tryGet(0));
        Assert.assertEquals(chk1, cache.tryGet(1));
    }
}

