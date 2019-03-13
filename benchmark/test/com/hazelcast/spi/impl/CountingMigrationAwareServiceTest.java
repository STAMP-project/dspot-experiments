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
package com.hazelcast.spi.impl;


import com.hazelcast.spi.FragmentedMigrationAwareService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test count-tracking functionality of CountingMigrationAwareService
 */
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CountingMigrationAwareServiceTest {
    @Parameterized.Parameter
    public FragmentedMigrationAwareService wrappedMigrationAwareService;

    @Parameterized.Parameter(1)
    public PartitionMigrationEvent event;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CountingMigrationAwareService countingMigrationAwareService;

    private int initialMigrationStamp;

    @Test
    public void beforeMigration() throws Exception {
        // when: countingMigrationAwareService.beforeMigration was invoked (in setUp method)
        // then: if event involves primary replica, stamp should change.
        if (CountingMigrationAwareService.isPrimaryReplicaMigrationEvent(event)) {
            Assert.assertEquals(CountingMigrationAwareService.IN_FLIGHT_MIGRATION_STAMP, countingMigrationAwareService.getMigrationStamp());
            Assert.assertFalse(countingMigrationAwareService.validateMigrationStamp(CountingMigrationAwareService.IN_FLIGHT_MIGRATION_STAMP));
        } else {
            Assert.assertEquals(initialMigrationStamp, countingMigrationAwareService.getMigrationStamp());
            Assert.assertTrue(countingMigrationAwareService.validateMigrationStamp(initialMigrationStamp));
        }
    }

    @Test
    public void commitMigration() throws Exception {
        // when: before - commit migration methods have been executed
        try {
            countingMigrationAwareService.commitMigration(event);
        } catch (RuntimeException e) {
            // we do not care whether the wrapped service throws an exception
        }
        int currentMigrationStamp = countingMigrationAwareService.getMigrationStamp();
        // then: if event involves primary replica, stamp should change.
        if (CountingMigrationAwareService.isPrimaryReplicaMigrationEvent(event)) {
            Assert.assertNotEquals(initialMigrationStamp, currentMigrationStamp);
        } else {
            Assert.assertEquals(initialMigrationStamp, currentMigrationStamp);
        }
        Assert.assertTrue(countingMigrationAwareService.validateMigrationStamp(currentMigrationStamp));
    }

    @Test
    public void rollbackMigration() throws Exception {
        // when: before - rollback migration methods have been executed
        try {
            countingMigrationAwareService.rollbackMigration(event);
        } catch (RuntimeException e) {
            // we do not care whether the wrapped service throws an exception
        }
        int currentMigrationStamp = countingMigrationAwareService.getMigrationStamp();
        // then: if event involves primary replica, stamp should change.
        if (CountingMigrationAwareService.isPrimaryReplicaMigrationEvent(event)) {
            Assert.assertNotEquals(initialMigrationStamp, currentMigrationStamp);
        } else {
            Assert.assertEquals(initialMigrationStamp, currentMigrationStamp);
        }
        Assert.assertTrue(countingMigrationAwareService.validateMigrationStamp(currentMigrationStamp));
    }

    @Test
    public void commitMigration_invalidCount_throwsAssertionError() {
        // when: invalid sequence of beforeMigration, commitMigration, commitMigration is executed
        // and
        try {
            countingMigrationAwareService.commitMigration(event);
        } catch (RuntimeException e) {
            // we do not care whether the wrapped service throws an exception
        }
        // on second commitMigration, if event involves partition owner assertion error is thrown
        if (CountingMigrationAwareService.isPrimaryReplicaMigrationEvent(event)) {
            expectedException.expect(AssertionError.class);
        }
        try {
            countingMigrationAwareService.commitMigration(event);
        } catch (RuntimeException e) {
            // we do not care whether the wrapped service throws an exception
        }
    }

    @Test
    public void rollbackMigration_invalidCount_throwsAssertionError() {
        // when: invalid sequence of beforeMigration, rollbackMigration, rollbackMigration is executed
        try {
            countingMigrationAwareService.rollbackMigration(event);
        } catch (RuntimeException e) {
            // we do not care whether the wrapped service throws an exception
        }
        // on second rollbackMigration, if event involves partition owner assertion error is thrown
        if (CountingMigrationAwareService.isPrimaryReplicaMigrationEvent(event)) {
            expectedException.expect(AssertionError.class);
        }
        try {
            countingMigrationAwareService.rollbackMigration(event);
        } catch (RuntimeException e) {
            // we do not care whether the wrapped service throws an exception
        }
    }

    static class ExceptionThrowingMigrationAwareService implements FragmentedMigrationAwareService {
        @Override
        public Collection<ServiceNamespace> getAllServiceNamespaces(PartitionReplicationEvent event) {
            return null;
        }

        @Override
        public boolean isKnownServiceNamespace(ServiceNamespace namespace) {
            return false;
        }

        @Override
        public Operation prepareReplicationOperation(PartitionReplicationEvent event, Collection<ServiceNamespace> namespaces) {
            return null;
        }

        @Override
        public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
            return null;
        }

        @Override
        public void beforeMigration(PartitionMigrationEvent event) {
            throw new RuntimeException("");
        }

        @Override
        public void commitMigration(PartitionMigrationEvent event) {
            throw new RuntimeException("");
        }

        @Override
        public void rollbackMigration(PartitionMigrationEvent event) {
            throw new RuntimeException("");
        }

        @Override
        public String toString() {
            return "ExceptionThrowingMigrationAwareService";
        }
    }

    static class NoOpMigrationAwareService implements FragmentedMigrationAwareService {
        @Override
        public Collection<ServiceNamespace> getAllServiceNamespaces(PartitionReplicationEvent event) {
            return null;
        }

        @Override
        public boolean isKnownServiceNamespace(ServiceNamespace namespace) {
            return false;
        }

        @Override
        public Operation prepareReplicationOperation(PartitionReplicationEvent event, Collection<ServiceNamespace> namespaces) {
            return null;
        }

        @Override
        public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
            return null;
        }

        @Override
        public void beforeMigration(PartitionMigrationEvent event) {
        }

        @Override
        public void commitMigration(PartitionMigrationEvent event) {
        }

        @Override
        public void rollbackMigration(PartitionMigrationEvent event) {
        }

        @Override
        public String toString() {
            return "NoOpMigrationAwareService";
        }
    }
}

