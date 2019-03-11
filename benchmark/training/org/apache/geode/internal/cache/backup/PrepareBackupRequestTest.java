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


import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.geode.cache.persistence.PersistentID;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.admin.remote.AdminFailureResponse;
import org.apache.geode.internal.cache.InternalCache;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PrepareBackupRequestTest {
    private PrepareBackupRequest prepareBackupRequest;

    private DistributionManager dm;

    private Set<InternalDistributedMember> recipients;

    private int msgId;

    private PrepareBackupFactory prepareBackupFactory;

    private InternalDistributedMember sender;

    private InternalCache cache;

    private HashSet<PersistentID> persistentIds;

    private PrepareBackup prepareBackup;

    private File targetDir;

    private File baselineDir;

    private Properties backupProperties;

    @Test
    public void usesFactoryToCreatePrepareBackup() throws Exception {
        prepareBackupRequest.createResponse(dm);
        Mockito.verify(prepareBackupFactory, Mockito.times(1)).createPrepareBackup(ArgumentMatchers.eq(sender), ArgumentMatchers.eq(cache), ArgumentMatchers.eq(backupProperties));
    }

    @Test
    public void usesFactoryToCreateBackupResponse() throws Exception {
        prepareBackupRequest.createResponse(dm);
        Mockito.verify(prepareBackupFactory, Mockito.times(1)).createBackupResponse(ArgumentMatchers.eq(sender), ArgumentMatchers.eq(persistentIds));
    }

    @Test
    public void returnsBackupResponse() throws Exception {
        assertThat(prepareBackupRequest.createResponse(dm)).isInstanceOf(BackupResponse.class);
    }

    @Test
    public void returnsAdminFailureResponseWhenPrepareBackupThrowsIOException() throws Exception {
        Mockito.when(prepareBackup.run()).thenThrow(new IOException());
        assertThat(prepareBackupRequest.createResponse(dm)).isInstanceOf(AdminFailureResponse.class);
    }
}

