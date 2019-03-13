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
package com.hazelcast.map;


import GroupProperty.PARTITION_COUNT;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Map configuration can be updated dynamically at runtime by using management center ui.
 * This test verifies that the changes will be reflected to corresponding IMap at runtime.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DynamicMapConfigTest extends HazelcastTestSupport {
    @Test
    public void testMapConfigUpdate_reflectedToRecordStore() throws InterruptedException, ExecutionException {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.setProperty(PARTITION_COUNT.getName(), "1");
        HazelcastInstance node = createHazelcastInstance(config);
        IMap<Integer, Integer> map = node.getMap(mapName);
        // trigger recordStore creation
        map.put(1, 1);
        boolean beforeUpdate = (isRecordStoreExpirable(map)) && (isEvictionEnabled(map));
        updateMapConfig(mapName, node);
        boolean afterUpdate = (isRecordStoreExpirable(map)) && (isEvictionEnabled(map));
        Assert.assertFalse("Before MapConfig update, RecordStore should not be expirable and evictable", beforeUpdate);
        Assert.assertTrue("RecordStore should be expirable and evictable after MapConfig update", afterUpdate);
    }
}

