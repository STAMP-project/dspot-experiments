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
package com.hazelcast.multimap;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MultiMapBackupTest extends HazelcastTestSupport {
    private static final String MULTI_MAP_NAME = "MultiMapBackupTest";

    private static final int KEY_COUNT = 1000;

    private static final int VALUE_COUNT = 5;

    private static final int BACKUP_COUNT = 4;

    private static final ILogger LOGGER = Logger.getLogger(MultiMapBackupTest.class);

    private TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();

    @Test
    public void testBackups() {
        Config config = new Config();
        config.getMultiMapConfig(MultiMapBackupTest.MULTI_MAP_NAME).setBackupCount(MultiMapBackupTest.BACKUP_COUNT).setAsyncBackupCount(0);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        MultiMap<Integer, Integer> multiMap = hz.getMultiMap(MultiMapBackupTest.MULTI_MAP_NAME);
        for (int i = 0; i < (MultiMapBackupTest.KEY_COUNT); i++) {
            for (int j = 0; j < (MultiMapBackupTest.VALUE_COUNT); j++) {
                multiMap.put(i, (i + j));
            }
        }
        // scale up
        MultiMapBackupTest.LOGGER.info((("Scaling up to " + ((MultiMapBackupTest.BACKUP_COUNT) + 1)) + " members..."));
        for (int backupCount = 1; backupCount <= (MultiMapBackupTest.BACKUP_COUNT); backupCount++) {
            factory.newHazelcastInstance(config);
            HazelcastTestSupport.waitAllForSafeState(factory.getAllHazelcastInstances());
            Assert.assertEquals((backupCount + 1), factory.getAllHazelcastInstances().iterator().next().getCluster().getMembers().size());
            assertMultiMapBackups(backupCount);
        }
        // scale down
        MultiMapBackupTest.LOGGER.info("Scaling down to 1 member...");
        for (int backupCount = (MultiMapBackupTest.BACKUP_COUNT) - 1; backupCount > 0; backupCount--) {
            factory.getAllHazelcastInstances().iterator().next().shutdown();
            HazelcastTestSupport.waitAllForSafeState(factory.getAllHazelcastInstances());
            Assert.assertEquals((backupCount + 1), factory.getAllHazelcastInstances().iterator().next().getCluster().getMembers().size());
            assertMultiMapBackups(backupCount);
        }
    }
}

