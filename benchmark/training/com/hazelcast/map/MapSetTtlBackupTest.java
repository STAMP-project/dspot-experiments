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


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.environment.RuntimeAvailableProcessorsRule;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapSetTtlBackupTest extends HazelcastTestSupport {
    private static final int CLUSTER_SIZE = 3;

    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    @Rule
    public RuntimeAvailableProcessorsRule runtimeAvailableProcessorsRule = new RuntimeAvailableProcessorsRule(4);

    protected HazelcastInstance[] instances;

    private TestHazelcastInstanceFactory factory;

    @Test
    public void testBackups() {
        String mapName = HazelcastTestSupport.randomMapName();
        HazelcastInstance instance = instances[0];
        MapSetTtlBackupTest.putKeys(instance, mapName, null, 0, 1000);
        MapSetTtlBackupTest.setTtl(instance, mapName, 0, 1000, 1, TimeUnit.SECONDS);
        HazelcastTestSupport.sleepAtLeastMillis(1001);
        for (int i = 0; i < (MapSetTtlBackupTest.CLUSTER_SIZE); i++) {
            MapSetTtlBackupTest.assertKeysNotPresent(instances, mapName, 0, 1000);
        }
    }

    @Test
    public void testMakesTempBackupEntriesUnlimited() {
        String mapName = HazelcastTestSupport.randomMapName();
        HazelcastInstance instance = instances[0];
        MapSetTtlBackupTest.putKeys(instance, mapName, 10, 0, 20);
        MapSetTtlBackupTest.setTtl(instance, mapName, 0, 20, 0, TimeUnit.SECONDS);
        HazelcastTestSupport.sleepAtLeastMillis(10100);
        for (int i = 0; i < (MapSetTtlBackupTest.CLUSTER_SIZE); i++) {
            MapSetTtlBackupTest.assertKeys(instances, mapName, 0, 20);
        }
    }
}

