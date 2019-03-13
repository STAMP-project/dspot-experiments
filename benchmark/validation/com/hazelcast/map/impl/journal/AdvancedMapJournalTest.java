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
package com.hazelcast.map.impl.journal;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AdvancedMapJournalTest extends HazelcastTestSupport {
    private static final int PARTITION_COUNT = 100;

    private HazelcastInstance[] instances;

    @Test
    public void testBackupSafety() throws Exception {
        String name = HazelcastTestSupport.randomMapName();
        IMap<Integer, Object> m = instances[0].getMap(name);
        int keyCount = 1000;
        int updateCount = 3;
        for (int n = 0; n < updateCount; n++) {
            for (int i = 0; i < keyCount; i++) {
                m.set(i, HazelcastTestSupport.randomString());
            }
        }
        LinkedList<HazelcastInstance> instanceList = new LinkedList<HazelcastInstance>(Arrays.asList(instances));
        HazelcastTestSupport.waitAllForSafeState(instanceList);
        int expectedSize = keyCount * updateCount;
        while ((instanceList.size()) > 1) {
            HazelcastInstance instance = instanceList.removeFirst();
            instance.getLifecycleService().terminate();
            HazelcastTestSupport.waitAllForSafeState(instanceList);
            m = instanceList.getFirst().getMap(name);
            int journalSize = AdvancedMapJournalTest.getJournalSize(m);
            Assert.assertEquals(expectedSize, journalSize);
        } 
    }
}

