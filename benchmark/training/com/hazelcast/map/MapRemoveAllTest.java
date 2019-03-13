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


import TruePredicate.INSTANCE;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapRemoveAllTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final int MAP_SIZE = 1000;

    private static final int NODE_COUNT = 3;

    private HazelcastInstance member;

    private HazelcastInstance[] instances;

    @Test
    public void throws_exception_whenPredicateNull() throws Exception {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("predicate cannot be null");
        IMap<Integer, Integer> map = member.getMap("test");
        map.removeAll(null);
    }

    @Test
    public void removes_all_entries_whenPredicateTrue() throws Exception {
        IMap<Integer, Integer> map = member.getMap("test");
        for (int i = 0; i < (MapRemoveAllTest.MAP_SIZE); i++) {
            map.put(i, i);
        }
        map.removeAll(INSTANCE);
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void removes_no_entries_whenPredicateFalse() throws Exception {
        IMap<Integer, Integer> map = member.getMap("test");
        for (int i = 0; i < (MapRemoveAllTest.MAP_SIZE); i++) {
            map.put(i, i);
        }
        map.removeAll(FalsePredicate.INSTANCE);
        Assert.assertEquals(MapRemoveAllTest.MAP_SIZE, map.size());
    }

    @Test
    public void removes_odd_keys_whenPredicateOdd() throws Exception {
        IMap<Integer, Integer> map = member.getMap("test");
        for (int i = 0; i < (MapRemoveAllTest.MAP_SIZE); i++) {
            map.put(i, i);
        }
        map.removeAll(new MapRemoveAllTest.OddFinderPredicate());
        Assert.assertEquals(500, map.size());
    }

    @Test
    public void removes_same_number_of_entries_from_owner_and_backup() {
        String mapName = "test";
        IMap<Integer, Integer> map = member.getMap(mapName);
        for (int i = 0; i < 1000; i++) {
            map.put(i, i);
        }
        map.removeAll(new SqlPredicate("__key >= 100"));
        HazelcastTestSupport.waitAllForSafeState(instances);
        long totalOwnedEntryCount = 0;
        for (HazelcastInstance instance : instances) {
            LocalMapStats localMapStats = instance.getMap(mapName).getLocalMapStats();
            totalOwnedEntryCount += localMapStats.getOwnedEntryCount();
        }
        long totalBackupEntryCount = 0;
        for (HazelcastInstance instance : instances) {
            LocalMapStats localMapStats = instance.getMap(mapName).getLocalMapStats();
            totalBackupEntryCount += localMapStats.getBackupEntryCount();
        }
        Assert.assertEquals(100, totalOwnedEntryCount);
        Assert.assertEquals(100, totalBackupEntryCount);
    }

    private static final class OddFinderPredicate implements Predicate<Integer, Integer> {
        @Override
        public boolean apply(Map.Entry<Integer, Integer> mapEntry) {
            return ((mapEntry.getKey()) % 2) != 0;
        }
    }
}

