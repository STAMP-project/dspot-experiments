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


import java.util.HashSet;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.InternalCache;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AbortBackupRequestTest {
    private AbortBackupRequest abortBackupRequest;

    private DistributionManager dm;

    private InternalCache cache;

    private AbortBackupFactory abortBackupFactory;

    private InternalDistributedMember sender;

    @Test
    public void usesFactoryToCreateAbortBackupAndResponse() {
        assertThat(abortBackupRequest.createResponse(dm)).isInstanceOf(BackupResponse.class);
        Mockito.verify(abortBackupFactory, Mockito.times(1)).createAbortBackup(ArgumentMatchers.eq(cache));
        Mockito.verify(abortBackupFactory, Mockito.times(1)).createBackupResponse(ArgumentMatchers.eq(sender), ArgumentMatchers.eq(new HashSet()));
    }
}

