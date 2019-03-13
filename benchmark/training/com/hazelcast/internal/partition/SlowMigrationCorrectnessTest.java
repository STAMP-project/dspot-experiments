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
package com.hazelcast.internal.partition;


import GroupProperty.PARTITION_MIGRATION_INTERVAL;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * https://github.com/hazelcast/hazelcast/issues/5444
 */
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ SlowTest.class })
@Ignore("https://github.com/hazelcast/hazelcast/issues/9828")
public class SlowMigrationCorrectnessTest extends AbstractMigrationCorrectnessTest {
    @Test(timeout = (6000 * 10) * 10)
    public void testPartitionData_whenSameNodesRestarted_afterPartitionsSafe() throws InterruptedException {
        partitionCount = 14;
        Config config = getConfig(true, false);
        config.setProperty(PARTITION_MIGRATION_INTERVAL.getName(), "1");
        HazelcastInstance[] instances = factory.newInstances(config, nodeCount);
        HazelcastTestSupport.warmUpPartitions(instances);
        fillData(instances[((instances.length) - 1)]);
        assertSizeAndDataEventually();
        Address[] restartingAddresses = new Address[backupCount];
        for (int i = 0; i < (backupCount); i++) {
            restartingAddresses[i] = HazelcastTestSupport.getAddress(instances[i]);
        }
        for (int i = 0; i < 5; i++) {
            restartNodes(config, restartingAddresses);
        }
        assertSizeAndDataEventually();
    }
}

