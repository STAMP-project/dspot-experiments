/**
 * Copyright 2015 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.map.mutable.primitive;


import com.gs.collections.api.map.primitive.MutableIntLongMap;
import org.junit.Test;


public class IntLongMapProbeTest {
    private static final int SMALL_COLLIDING_KEY_COUNT = 500;

    private static final int LARGE_COLLIDING_KEY_COUNT = 40000;

    @Test
    public void randomNumbers_get() {
        MutableIntLongMap intlongSmallNonPresized = new IntLongHashMap();
        this.testRandomGet(intlongSmallNonPresized, IntLongMapProbeTest.SMALL_COLLIDING_KEY_COUNT);
        MutableIntLongMap intlongSmallPresized = new IntLongHashMap(1000);
        this.testRandomGet(intlongSmallPresized, IntLongMapProbeTest.SMALL_COLLIDING_KEY_COUNT);
        MutableIntLongMap intlongLargeNonPresized = new IntLongHashMap();
        this.testRandomGet(intlongLargeNonPresized, IntLongMapProbeTest.LARGE_COLLIDING_KEY_COUNT);
        MutableIntLongMap intlongLargePresized = new IntLongHashMap(1000000);
        this.testRandomGet(intlongLargePresized, IntLongMapProbeTest.LARGE_COLLIDING_KEY_COUNT);
    }

    @Test
    public void nonRandomNumbers_get() {
        MutableIntLongMap intlongMapSmall = new IntLongHashMap(1000);
        int[] intKeysForSmallMap = this.getSmallCollidingNumbers().toArray();
        this.testNonRandomGet(intlongMapSmall, IntLongMapProbeTest.SMALL_COLLIDING_KEY_COUNT, intKeysForSmallMap);
        MutableIntLongMap intlongMapLarge = new IntLongHashMap(1000000);
        int[] intKeysForLargeMap = this.getLargeCollidingNumbers().toArray();
        this.testNonRandomGet(intlongMapLarge, IntLongMapProbeTest.LARGE_COLLIDING_KEY_COUNT, intKeysForLargeMap);
    }

    @Test
    public void nonRandomNumbers_remove() {
        MutableIntLongMap intlongMapSmallNonPresized = new IntLongHashMap();
        int[] intKeysForMap = this.getSmallCollidingNumbers().toArray();
        this.testNonRandomRemove(intlongMapSmallNonPresized, IntLongMapProbeTest.SMALL_COLLIDING_KEY_COUNT, intKeysForMap);
        MutableIntLongMap intlongMapSmallPresized = new IntLongHashMap(1000);
        int[] intKeysForMap2 = this.getSmallCollidingNumbers().toArray();
        this.testNonRandomRemove(intlongMapSmallPresized, IntLongMapProbeTest.SMALL_COLLIDING_KEY_COUNT, intKeysForMap2);
        MutableIntLongMap intlongMapLargeNonPresized = new IntLongHashMap();
        int[] intKeysForMap3 = this.getLargeCollidingNumbers().toArray();
        this.testNonRandomRemove(intlongMapLargeNonPresized, IntLongMapProbeTest.LARGE_COLLIDING_KEY_COUNT, intKeysForMap3);
        MutableIntLongMap intlongMapLargePresized = new IntLongHashMap(1000000);
        int[] intKeysForMap4 = this.getLargeCollidingNumbers().toArray();
        this.testNonRandomRemove(intlongMapLargePresized, IntLongMapProbeTest.LARGE_COLLIDING_KEY_COUNT, intKeysForMap4);
    }

    @Test
    public void randomNumbers_remove() {
        MutableIntLongMap intlongMapSmallNonPresized = new IntLongHashMap();
        this.testRandomRemove(intlongMapSmallNonPresized, IntLongMapProbeTest.SMALL_COLLIDING_KEY_COUNT);
        MutableIntLongMap intlongMapSmallPresized = new IntLongHashMap(1000);
        this.testRandomRemove(intlongMapSmallPresized, IntLongMapProbeTest.SMALL_COLLIDING_KEY_COUNT);
        MutableIntLongMap intlongMapLargeNonPresized = new IntLongHashMap();
        this.testRandomRemove(intlongMapLargeNonPresized, IntLongMapProbeTest.LARGE_COLLIDING_KEY_COUNT);
        MutableIntLongMap intlongMapLargePresized = new IntLongHashMap(1000000);
        this.testRandomRemove(intlongMapLargePresized, IntLongMapProbeTest.LARGE_COLLIDING_KEY_COUNT);
    }
}

