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


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.map.MapUnboundedReturnValuesTestSupport.KeyType.INTEGER;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapUnboundedReturnValues_QuickTest extends MapUnboundedReturnValuesTestSupport {
    @Test(timeout = MapUnboundedReturnValuesTestSupport.TEN_MINUTES_IN_MILLIS)
    public void testMapKeySet_SmallLimit_NoPreCheck() {
        runMapQuickTest(MapUnboundedReturnValuesTestSupport.PARTITION_COUNT, 1, MapUnboundedReturnValuesTestSupport.SMALL_LIMIT, MapUnboundedReturnValuesTestSupport.PRE_CHECK_TRIGGER_LIMIT_INACTIVE, INTEGER);
    }

    @Test(timeout = MapUnboundedReturnValuesTestSupport.TEN_MINUTES_IN_MILLIS)
    public void testMapKeySet_SmallLimit_PreCheck() {
        runMapQuickTest(MapUnboundedReturnValuesTestSupport.PARTITION_COUNT, 1, MapUnboundedReturnValuesTestSupport.SMALL_LIMIT, MapUnboundedReturnValuesTestSupport.PRE_CHECK_TRIGGER_LIMIT_ACTIVE, INTEGER);
    }
}

