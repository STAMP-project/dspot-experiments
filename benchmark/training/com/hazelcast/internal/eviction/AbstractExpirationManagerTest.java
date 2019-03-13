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
package com.hazelcast.internal.eviction;


import com.hazelcast.cluster.ClusterState;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastTestSupport;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public abstract class AbstractExpirationManagerTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testTaskPeriodSeconds_set_viaSystemProperty() {
        String previous = System.getProperty(taskPeriodSecondsPropName());
        try {
            int expectedPeriodSeconds = 12;
            System.setProperty(taskPeriodSecondsPropName(), String.valueOf(expectedPeriodSeconds));
            int actualTaskPeriodSeconds = newExpirationManager(createHazelcastInstance()).getTaskPeriodSeconds();
            Assert.assertEquals(expectedPeriodSeconds, actualTaskPeriodSeconds);
        } finally {
            restoreProperty(taskPeriodSecondsPropName(), previous);
        }
    }

    @Test
    public void testTaskPeriodSeconds_throwsIllegalArgumentException_whenNotPositive() throws Exception {
        String previous = System.getProperty(taskPeriodSecondsPropName());
        try {
            System.setProperty(taskPeriodSecondsPropName(), String.valueOf(0));
            thrown.expectMessage("taskPeriodSeconds should be a positive number");
            thrown.expect(IllegalArgumentException.class);
            newExpirationManager(createHazelcastInstance());
        } finally {
            restoreProperty(taskPeriodSecondsPropName(), previous);
        }
    }

    @Test
    public void testCleanupPercentage_set_viaSystemProperty() {
        String previous = System.getProperty(cleanupPercentagePropName());
        try {
            int expectedCleanupPercentage = 77;
            System.setProperty(cleanupPercentagePropName(), String.valueOf(expectedCleanupPercentage));
            int actualCleanupPercentage = getCleanupPercentage(newExpirationManager(createHazelcastInstance()));
            Assert.assertEquals(expectedCleanupPercentage, actualCleanupPercentage);
        } finally {
            restoreProperty(cleanupPercentagePropName(), previous);
        }
    }

    @Test
    public void testCleanupPercentage_throwsIllegalArgumentException_whenUnderRange() {
        String previous = System.getProperty(cleanupPercentagePropName());
        try {
            System.setProperty(cleanupPercentagePropName(), String.valueOf(0));
            thrown.expectMessage("cleanupPercentage should be in range (0,100]");
            thrown.expect(IllegalArgumentException.class);
            newExpirationManager(createHazelcastInstance());
        } finally {
            restoreProperty(cleanupPercentagePropName(), previous);
        }
    }

    @Test
    public void testCleanupPercentage_throwsIllegalArgumentException_whenAboveRange() {
        String previous = System.getProperty(cleanupPercentagePropName());
        try {
            System.setProperty(cleanupPercentagePropName(), String.valueOf(101));
            thrown.expectMessage("cleanupPercentage should be in range (0,100]");
            thrown.expect(IllegalArgumentException.class);
            newExpirationManager(createHazelcastInstance());
        } finally {
            restoreProperty(cleanupPercentagePropName(), previous);
        }
    }

    @Test
    public void testCleanupOperationCount_set_viaSystemProperty() {
        String previous = System.getProperty(cleanupOperationCountPropName());
        try {
            int expectedCleanupOperationCount = 19;
            System.setProperty(cleanupOperationCountPropName(), String.valueOf(expectedCleanupOperationCount));
            int actualCleanupOperationCount = getCleanupOperationCount(newExpirationManager(createHazelcastInstance()));
            Assert.assertEquals(expectedCleanupOperationCount, actualCleanupOperationCount);
        } finally {
            restoreProperty(cleanupOperationCountPropName(), previous);
        }
    }

    @Test
    public void testCleanupOperationCount_throwsIllegalArgumentException_whenNotPositive() {
        String previous = System.getProperty(cleanupOperationCountPropName());
        try {
            System.setProperty(cleanupOperationCountPropName(), String.valueOf(0));
            thrown.expectMessage("cleanupOperationCount should be a positive number");
            thrown.expect(IllegalArgumentException.class);
            newExpirationManager(createHazelcastInstance());
        } finally {
            restoreProperty(cleanupOperationCountPropName(), previous);
        }
    }

    @Test
    public void gets_taskPeriodSeconds_from_config() {
        Config config = getConfig();
        String taskPeriodSeconds = "77";
        config.setProperty(taskPeriodSecondsPropName(), taskPeriodSeconds);
        HazelcastInstance node = createHazelcastInstance(config);
        ExpirationManager expirationManager = newExpirationManager(node);
        Assert.assertEquals(Integer.parseInt(taskPeriodSeconds), expirationManager.getTaskPeriodSeconds());
    }

    @Test
    public void gets_cleanupPercentage_from_config() {
        Config config = getConfig();
        String cleanupPercentage = "99";
        config.setProperty(cleanupPercentagePropName(), cleanupPercentage);
        HazelcastInstance node = createHazelcastInstance(config);
        ExpirationManager expirationManager = newExpirationManager(node);
        Assert.assertEquals(Integer.parseInt(cleanupPercentage), getCleanupPercentage(expirationManager));
    }

    @Test
    public void gets_cleanupOperationCount_from_config() {
        Config config = getConfig();
        String cleanupOperationCount = "777";
        config.setProperty(cleanupOperationCountPropName(), cleanupOperationCount);
        HazelcastInstance node = createHazelcastInstance(config);
        ExpirationManager expirationManager = newExpirationManager(node);
        Assert.assertEquals(Integer.parseInt(cleanupOperationCount), getCleanupOperationCount(expirationManager));
    }

    @Test
    public void stops_running_when_clusterState_turns_passive() {
        Config config = getConfig();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        HazelcastInstance node = createHazelcastInstance(config);
        final AtomicInteger expirationCounter = configureForTurnsActivePassiveTest(node);
        node.getCluster().changeClusterState(ClusterState.PASSIVE);
        // wait a little to see if any expiration is occurring
        HazelcastTestSupport.sleepSeconds(3);
        int expirationCount = expirationCounter.get();
        Assert.assertEquals(String.format("Expecting no expiration but found:%d", expirationCount), 0, expirationCount);
    }

    @Test
    public void starts_running_when_clusterState_turns_active() {
        Config config = getConfig();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        HazelcastInstance node = createHazelcastInstance(config);
        final AtomicInteger expirationCounter = configureForTurnsActivePassiveTest(node);
        node.getCluster().changeClusterState(ClusterState.PASSIVE);
        node.getCluster().changeClusterState(ClusterState.ACTIVE);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                int expirationCount = expirationCounter.get();
                Assert.assertEquals(String.format("Expecting 1 expiration but found:%d", expirationCount), 1, expirationCount);
            }
        });
    }
}

