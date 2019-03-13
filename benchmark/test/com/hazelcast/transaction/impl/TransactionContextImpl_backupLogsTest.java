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
package com.hazelcast.transaction.impl;


import ListService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * A test that makes sure that the backup logs are triggered to be created. Some data-structures like
 * the list/set/queue require it, but others do not.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class TransactionContextImpl_backupLogsTest extends HazelcastTestSupport {
    private TransactionManagerServiceImpl localTxManager;

    private TransactionManagerServiceImpl remoteTxManager;

    private String ownerUuid;

    private HazelcastInstance localHz;

    private NodeEngineImpl localNodeEngine;

    @Test
    public void list_backupLogCreationForced() {
        assertBackupLogCreationForced(SERVICE_NAME);
    }

    @Test
    public void set_backupLogCreationForced() {
        assertBackupLogCreationForced(SetService.SERVICE_NAME);
    }

    @Test
    public void queue_backupLogCreationForced() {
        assertBackupLogCreationForced(QueueService.SERVICE_NAME);
    }

    @Test
    public void map_thenNotForcesBackupLogCreation() {
        assertBackupLogCreationNotForced(MapService.SERVICE_NAME);
    }

    @Test
    public void multimap_thenNotForceBackupLogCreation() {
        assertBackupLogCreationNotForced(MultiMapService.SERVICE_NAME);
    }
}

