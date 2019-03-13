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
package com.hazelcast.internal.partition.impl;


import MigrationManager.MigrateTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MigrationQueueTest {
    private final MigrationQueue migrationQueue = new MigrationQueue();

    @Test
    public void test_migrationTaskCount_incremented() {
        migrationQueue.add(Mockito.mock(MigrationRunnable.class));
        Assert.assertEquals(1, migrationQueue.migrationTaskCount());
    }

    @Test
    public void test_migrationTaskCount_notDecremented_afterMigrateTaskPolled() throws InterruptedException {
        migrationQueue.add(Mockito.mock(MigrateTask.class));
        migrationQueue.poll(1, TimeUnit.SECONDS);
        Assert.assertTrue(migrationQueue.hasMigrationTasks());
    }

    @Test
    public void test_migrateTaskCount_decremented_afterTaskCompleted() throws InterruptedException {
        final MigrationRunnable task = Mockito.mock(MigrationRunnable.class);
        migrationQueue.add(task);
        migrationQueue.afterTaskCompletion(task);
        Assert.assertFalse(migrationQueue.hasMigrationTasks());
    }

    @Test
    public void test_migrateTaskCount_decremented_onClear() {
        migrationQueue.add(Mockito.mock(MigrateTask.class));
        migrationQueue.clear();
        Assert.assertFalse(migrationQueue.hasMigrationTasks());
    }

    @Test(expected = IllegalStateException.class)
    public void test_migrateTaskCount_notDecremented_belowZero() {
        migrationQueue.afterTaskCompletion(Mockito.mock(MigrateTask.class));
    }
}

