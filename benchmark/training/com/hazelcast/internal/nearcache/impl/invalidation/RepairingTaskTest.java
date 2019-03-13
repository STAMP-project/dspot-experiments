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
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RandomPicker;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RepairingTaskTest extends HazelcastTestSupport {
    @Test
    public void whenToleratedMissCountIsConfigured_thenItShouldBeUsed() {
        int maxToleratedMissCount = 123;
        Config config = RepairingTaskTest.getConfigWithMaxToleratedMissCount(maxToleratedMissCount);
        RepairingTask repairingTask = RepairingTaskTest.newRepairingTask(config);
        Assert.assertEquals(maxToleratedMissCount, repairingTask.maxToleratedMissCount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenToleratedMissCountIsNegative_thenThrowException() {
        Config config = RepairingTaskTest.getConfigWithMaxToleratedMissCount((-1));
        RepairingTaskTest.newRepairingTask(config);
    }

    @Test
    public void whenReconciliationIntervalSecondsIsConfigured_thenItShouldBeUsed() {
        int reconciliationIntervalSeconds = 91;
        Config config = RepairingTaskTest.getConfigWithReconciliationInterval(reconciliationIntervalSeconds);
        RepairingTask repairingTask = RepairingTaskTest.newRepairingTask(config);
        Assert.assertEquals(reconciliationIntervalSeconds, TimeUnit.NANOSECONDS.toSeconds(repairingTask.reconciliationIntervalNanos));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenReconciliationIntervalSecondsIsNegative_thenThrowException() {
        Config config = RepairingTaskTest.getConfigWithReconciliationInterval((-1));
        RepairingTaskTest.newRepairingTask(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenReconciliationIntervalSecondsIsNotZeroButSmallerThanThresholdValue_thenThrowException() {
        int thresholdValue = Integer.parseInt(RepairingTask.MIN_RECONCILIATION_INTERVAL_SECONDS.getDefaultValue());
        Config config = RepairingTaskTest.getConfigWithReconciliationInterval(RandomPicker.getInt(1, thresholdValue));
        RepairingTaskTest.newRepairingTask(config);
    }
}

