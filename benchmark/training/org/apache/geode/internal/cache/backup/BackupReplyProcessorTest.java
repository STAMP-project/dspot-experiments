/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.backup;


import java.util.Set;
import org.apache.geode.cache.persistence.PersistentID;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.DistributionMessage;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BackupReplyProcessorTest {
    private BackupReplyProcessor backupReplyProcessor;

    private BackupResultCollector resultCollector;

    private DistributionManager dm;

    private InternalDistributedSystem system;

    private InternalDistributedMember sender;

    private Set<InternalDistributedMember> recipients;

    private Set<PersistentID> persistentIds;

    private BackupResponse backupResponse;

    private DistributionMessage nonBackupResponse;

    @Test
    public void stopBecauseOfExceptionsReturnsFalse() throws Exception {
        assertThat(backupReplyProcessor.stopBecauseOfExceptions()).isFalse();
    }

    @Test
    public void processBackupResponseAddsSenderToResults() throws Exception {
        backupReplyProcessor.process(backupResponse, false);
        Mockito.verify(resultCollector, Mockito.times(1)).addToResults(ArgumentMatchers.eq(sender), ArgumentMatchers.eq(persistentIds));
    }

    @Test
    public void processNonBackupResponseDoesNotAddSenderToResults() throws Exception {
        backupReplyProcessor.process(nonBackupResponse, false);
        Mockito.verify(resultCollector, Mockito.times(0)).addToResults(ArgumentMatchers.eq(sender), ArgumentMatchers.eq(persistentIds));
    }
}

