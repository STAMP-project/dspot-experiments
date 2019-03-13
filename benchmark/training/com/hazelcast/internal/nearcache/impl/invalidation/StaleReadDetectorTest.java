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
package com.hazelcast.internal.nearcache.impl.invalidation;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class StaleReadDetectorTest extends HazelcastTestSupport {
    private static final String MAP_NAME = "test";

    @Test
    public void no_repairing_handler_created_when_invalidations_disabled() throws Exception {
        Config config = createConfigWithNearCache(false);
        HazelcastInstance node = createHazelcastInstance(config);
        IMap map = node.getMap(StaleReadDetectorTest.MAP_NAME);
        map.put(1, 1);
        map.get(1);
        Assert.assertFalse(StaleReadDetectorTest.isRepairingHandlerCreatedForMap(node, StaleReadDetectorTest.MAP_NAME));
    }

    @Test
    public void repairing_handler_created_when_invalidations_enabled() throws Exception {
        Config config = createConfigWithNearCache(true);
        HazelcastInstance node = createHazelcastInstance(config);
        IMap map = node.getMap(StaleReadDetectorTest.MAP_NAME);
        map.put(1, 1);
        map.get(1);
        Assert.assertTrue(StaleReadDetectorTest.isRepairingHandlerCreatedForMap(node, StaleReadDetectorTest.MAP_NAME));
    }
}

