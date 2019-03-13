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
package com.hazelcast.map.impl.mapstore.writebehind;


import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class WriteBehindMapStoreWithLoadAllTest extends HazelcastTestSupport {
    @Test
    public void testWriteBehind_loadAll() throws Exception {
        final MapStoreWithCounter<Integer, Integer> mapStore = new MapStoreWithCounter<Integer, Integer>();
        final IMap<Integer, Integer> map = TestMapUsingMapStoreBuilder.<Integer, Integer>create().withMapStore(mapStore).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).withBackupCount(0).withWriteDelaySeconds(100).withPartitionCount(1).build();
        final int numberOfItems = 3;
        final Map<Integer, Integer> fill = new HashMap<Integer, Integer>();
        for (int i = 0; i < numberOfItems; i++) {
            fill.put(i, (-1));
        }
        mapStore.storeAll(fill);
        populateMap(map, numberOfItems);
        map.loadAll(true);
        assertFinalValueEqualsForEachEntry(map, numberOfItems);
    }
}

