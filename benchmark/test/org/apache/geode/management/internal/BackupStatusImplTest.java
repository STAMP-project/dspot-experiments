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
package org.apache.geode.management.internal;


import java.util.Map;
import java.util.Set;
import org.apache.geode.cache.persistence.PersistentID;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.management.BackupStatus;
import org.junit.Test;


public class BackupStatusImplTest {
    private Map<DistributedMember, Set<PersistentID>> backedUpDiskStores;

    private Set<PersistentID> offlineDiskStores;

    @Test
    public void getBackedUpDiskStores() {
        BackupStatus backupStatus = new BackupStatusImpl(backedUpDiskStores, offlineDiskStores);
        assertThat(backupStatus.getBackedUpDiskStores()).isEqualTo(backedUpDiskStores);
    }

    @Test
    public void backedUpDiskStoresIsRequired() {
        assertThatThrownBy(() -> new BackupStatusImpl(null, offlineDiskStores)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getOfflineDiskStores() {
        BackupStatus backupStatus = new BackupStatusImpl(backedUpDiskStores, offlineDiskStores);
        assertThat(backupStatus.getOfflineDiskStores()).isEqualTo(offlineDiskStores);
    }

    @Test
    public void offlineDiskStoresIsRequired() {
        assertThatThrownBy(() -> new BackupStatusImpl(backedUpDiskStores, null)).isInstanceOf(IllegalArgumentException.class);
    }
}

