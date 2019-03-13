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
package com.hazelcast.json.internal;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ ParallelTest.class, QuickTest.class })
public class JsonMetadataCreationMigrationTest extends HazelcastTestSupport {
    protected static final int ENTRY_COUNT = 1000;

    protected TestHazelcastInstanceFactory factory;

    protected final int NODE_COUNT = 5;

    @Test
    public void testMetadataIsCreatedWhenRecordsAreMigrated() throws InterruptedException {
        HazelcastInstance instance = factory.newHazelcastInstance(getConfig());
        final IMap<HazelcastJsonValue, HazelcastJsonValue> map = instance.getMap(HazelcastTestSupport.randomMapName());
        for (int i = 0; i < (JsonMetadataCreationMigrationTest.ENTRY_COUNT); i++) {
            map.put(JsonMetadataCreationMigrationTest.createJsonValue("key", i), JsonMetadataCreationMigrationTest.createJsonValue("value", i));
        }
        for (int i = 1; i < (NODE_COUNT); i++) {
            factory.newHazelcastInstance(getConfig());
        }
        HazelcastTestSupport.waitAllForSafeState(factory.getAllHazelcastInstances());
        HazelcastTestSupport.warmUpPartitions(factory.getAllHazelcastInstances());
        assertMetadataCreatedEventually(map.getName());
    }
}

