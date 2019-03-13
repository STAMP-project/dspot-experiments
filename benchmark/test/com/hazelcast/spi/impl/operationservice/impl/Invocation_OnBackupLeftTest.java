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
package com.hazelcast.spi.impl.operationservice.impl;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Invocation_OnBackupLeftTest extends HazelcastTestSupport {
    private static final Set<String> backupRunning = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    // we use 20 seconds so we don't get spurious build failures
    private static final int COMPLETION_TIMEOUT_SECONDS = 20;

    private OperationServiceImpl localOperationService;

    private HazelcastInstance remote;

    private HazelcastInstance local;

    @Test
    public void whenPrimaryResponseNotYetReceived() {
        String backupId = UuidUtil.newUnsecureUuidString();
        int responseDelaySeconds = 5;
        Operation op = new Invocation_OnBackupLeftTest.PrimaryOperation(backupId).setPrimaryResponseDelaySeconds(responseDelaySeconds).setPartitionId(HazelcastTestSupport.getPartitionId(local));
        InvocationFuture f = ((InvocationFuture) (localOperationService.invokeOnPartition(op)));
        waitForBackupRunning(backupId);
        remote.getLifecycleService().terminate();
        HazelcastTestSupport.assertCompletesEventually(f, (responseDelaySeconds + (Invocation_OnBackupLeftTest.COMPLETION_TIMEOUT_SECONDS)));
    }

    @Test
    public void whenPrimaryResponseAlreadyReceived() {
        String backupId = UuidUtil.newUnsecureUuidString();
        Operation op = new Invocation_OnBackupLeftTest.PrimaryOperation(backupId).setPartitionId(HazelcastTestSupport.getPartitionId(local));
        InvocationFuture f = ((InvocationFuture) (localOperationService.invokeOnPartition(op)));
        waitForPrimaryResponse(f);
        waitForBackupRunning(backupId);
        remote.getLifecycleService().terminate();
        HazelcastTestSupport.assertCompletesEventually(f, Invocation_OnBackupLeftTest.COMPLETION_TIMEOUT_SECONDS);
    }

    static class PrimaryOperation extends Operation implements BackupAwareOperation {
        private String backupId;

        private int primaryResponseDelaySeconds;

        public PrimaryOperation() {
        }

        public PrimaryOperation(String backupId) {
            this.backupId = backupId;
        }

        public Invocation_OnBackupLeftTest.PrimaryOperation setPrimaryResponseDelaySeconds(int delaySeconds) {
            this.primaryResponseDelaySeconds = delaySeconds;
            return this;
        }

        @Override
        public boolean shouldBackup() {
            return true;
        }

        @Override
        public int getSyncBackupCount() {
            return 1;
        }

        @Override
        public int getAsyncBackupCount() {
            return 0;
        }

        @Override
        public Operation getBackupOperation() {
            return new Invocation_OnBackupLeftTest.SlowBackupOperation(backupId);
        }

        @Override
        public Object getResponse() {
            // backups are send before the response is send, and we stall sending a response so we can trigger
            // a slow primary response while the backup is running.
            HazelcastTestSupport.sleepSeconds(primaryResponseDelaySeconds);
            return super.getResponse();
        }

        @Override
        public void run() throws Exception {
        }

        @Override
        protected void writeInternal(ObjectDataOutput out) throws IOException {
            out.writeUTF(backupId);
            out.writeInt(primaryResponseDelaySeconds);
        }

        @Override
        protected void readInternal(ObjectDataInput in) throws IOException {
            backupId = in.readUTF();
            primaryResponseDelaySeconds = in.readInt();
        }
    }

    // the backup operation is going to be terribly slow with sending a backup
    static class SlowBackupOperation extends Operation {
        private String backupId;

        public SlowBackupOperation() {
        }

        public SlowBackupOperation(String backupId) {
            this.backupId = backupId;
        }

        @Override
        public void run() throws Exception {
            Invocation_OnBackupLeftTest.backupRunning.add(backupId);
            HazelcastTestSupport.sleepSeconds(120);
        }

        @Override
        protected void writeInternal(ObjectDataOutput out) throws IOException {
            out.writeUTF(backupId);
        }

        @Override
        protected void readInternal(ObjectDataInput in) throws IOException {
            backupId = in.readUTF();
        }
    }
}

