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
package com.hazelcast.client.map.impl.nearcache.invalidation;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationMetaDataFetcher;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataContainer;
import com.hazelcast.internal.nearcache.impl.invalidation.RepairingHandler;
import com.hazelcast.internal.nearcache.impl.invalidation.RepairingTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMapInvalidationMetaDataFetcherTest extends HazelcastTestSupport {
    private TestHazelcastFactory factory = new TestHazelcastFactory();

    @Test
    public void fetches_sequence_and_uuid() {
        String mapName = "test";
        int partition = 1;
        long givenSequence = getInt(1, Integer.MAX_VALUE);
        UUID givenUuid = UuidUtil.newUnsecureUUID();
        RepairingTask repairingTask = getRepairingTask(mapName, partition, givenSequence, givenUuid);
        InvalidationMetaDataFetcher invalidationMetaDataFetcher = repairingTask.getInvalidationMetaDataFetcher();
        ConcurrentMap<String, RepairingHandler> handlers = repairingTask.getHandlers();
        invalidationMetaDataFetcher.fetchMetadata(handlers);
        RepairingHandler repairingHandler = handlers.get(mapName);
        MetaDataContainer metaDataContainer = repairingHandler.getMetaDataContainer(partition);
        UUID foundUuid = metaDataContainer.getUuid();
        long foundSequence = metaDataContainer.getSequence();
        Assert.assertEquals(givenSequence, foundSequence);
        Assert.assertEquals(givenUuid, foundUuid);
    }
}

