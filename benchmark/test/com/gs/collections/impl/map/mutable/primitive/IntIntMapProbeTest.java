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


import com.gs.collections.api.map.primitive.MutableIntIntMap;
import org.junit.Test;


public class IntIntMapProbeTest {
    private static final int SMALL_COLLIDING_KEY_COUNT = 500;

    private static final int LARGE_COLLIDING_KEY_COUNT = 40000;

    @Test
    public void randomNumbers_get() {
        MutableIntIntMap intIntSmallNonPresized = new IntIntHashMap();
        this.testRandomGet(intIntSmallNonPresized, IntIntMapProbeTest.SMALL_COLLIDING_KEY_COUNT);
        MutableIntIntMap intIntSmallPresized = new IntIntHashMap(1000);
        this.testRandomGet(intIntSmallPresized, IntIntMapProbeTest.SMALL_COLLIDING_KEY_COUNT);
        MutableIntIntMap intIntLargeNonPresized = new IntIntHashMap();
        this.testRandomGet(intIntLargeNonPresized, IntIntMapProbeTest.LARGE_COLLIDING_KEY_COUNT);
        MutableIntIntMap intIntLargePresized = new IntIntHashMap(1000000);
        this.testRandomGet(intIntLargePresized, IntIntMapProbeTest.LARGE_COLLIDING_KEY_COUNT);
    }

    @Test
    public void nonRandomNumbers_get() {
        MutableIntIntMap intIntMapSmall = new IntIntHashMap(1000);
        int[] intKeysForSmallMap = this.getSmallCollidingNumbers().toArray();
        this.testNonRandomGet(intIntMapSmall, IntIntMapProbeTest.SMALL_COLLIDING_KEY_COUNT, intKeysForSmallMap);
        MutableIntIntMap intIntMapLarge = new IntIntHashMap(1000000);
        int[] intKeysForLargeMap = this.getLargeCollidingNumbers().toArray();
        this.testNonRandomGet(intIntMapLarge, IntIntMapProbeTest.LARGE_COLLIDING_KEY_COUNT, intKeysForLargeMap);
    }

    @Test
    public void nonRandomNumbers_remove() {
        MutableIntIntMap intIntMapSmallNonPresized = new IntIntHashMap();
        int[] intKeysForMap = this.getSmallCollidingNumbers().toArray();
        this.testNonRandomRemove(intIntMapSmallNonPresized, IntIntMapProbeTest.SMALL_COLLIDING_KEY_COUNT, intKeysForMap);
        MutableIntIntMap intIntMapSmallPresized = new IntIntHashMap(1000);
        int[] intKeysForMap2 = this.getSmallCollidingNumbers().toArray();
        this.testNonRandomRemove(intIntMapSmallPresized, IntIntMapProbeTest.SMALL_COLLIDING_KEY_COUNT, intKeysForMap2);
        MutableIntIntMap intIntMapLargeNonPresized = new IntIntHashMap();
        int[] intKeysForMap3 = this.getLargeCollidingNumbers().toArray();
        this.testNonRandomRemove(intIntMapLargeNonPresized, IntIntMapProbeTest.LARGE_COLLIDING_KEY_COUNT, intKeysForMap3);
        MutableIntIntMap intIntMapLargePresized = new IntIntHashMap(1000000);
        int[] intKeysForMap4 = this.getLargeCollidingNumbers().toArray();
        this.testNonRandomRemove(intIntMapLargePresized, IntIntMapProbeTest.LARGE_COLLIDING_KEY_COUNT, intKeysForMap4);
    }

    @Test
    public void randomNumbers_remove() {
        MutableIntIntMap intIntMapSmallNonPresized = new IntIntHashMap();
        this.testRandomRemove(intIntMapSmallNonPresized, IntIntMapProbeTest.SMALL_COLLIDING_KEY_COUNT);
        MutableIntIntMap intIntMapSmallPresized = new IntIntHashMap(1000);
        this.testRandomRemove(intIntMapSmallPresized, IntIntMapProbeTest.SMALL_COLLIDING_KEY_COUNT);
        MutableIntIntMap intIntMapLargeNonPresized = new IntIntHashMap();
        this.testRandomRemove(intIntMapLargeNonPresized, IntIntMapProbeTest.LARGE_COLLIDING_KEY_COUNT);
        MutableIntIntMap intIntMapLargePresized = new IntIntHashMap(1000000);
        this.testRandomRemove(intIntMapLargePresized, IntIntMapProbeTest.LARGE_COLLIDING_KEY_COUNT);
    }
}

