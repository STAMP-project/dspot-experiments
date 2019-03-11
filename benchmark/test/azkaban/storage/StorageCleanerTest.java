/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.storage;


import azkaban.db.DatabaseOperator;
import azkaban.spi.Storage;
import azkaban.utils.Props;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class StorageCleanerTest {
    public static final int TEST_PROJECT_ID = 14;

    private Storage storage;

    private DatabaseOperator databaseOperator;

    /**
     * test default behavior. By default no artifacts should be cleaned up.
     */
    @Test
    public void testNoCleanupCase1() throws Exception {
        final StorageCleaner storageCleaner = new StorageCleaner(new Props(), this.storage, this.databaseOperator);
        Assert.assertFalse(storageCleaner.isCleanupPermitted());
    }

    @Test
    public void testNoCleanupCase2() throws Exception {
        final Props props = new Props();
        props.put(ConfigurationKeys.AZKABAN_STORAGE_ARTIFACT_MAX_RETENTION, 10);
        final StorageCleaner storageCleaner = new StorageCleaner(props, this.storage, this.databaseOperator);
        Assert.assertTrue(storageCleaner.isCleanupPermitted());
        storageCleaner.cleanupProjectArtifacts(StorageCleanerTest.TEST_PROJECT_ID);
        Mockito.verify(this.storage, Mockito.never()).delete(ArgumentMatchers.anyString());
    }

    @Test
    public void testCleanup() throws Exception {
        final Props props = new Props();
        props.put(ConfigurationKeys.AZKABAN_STORAGE_ARTIFACT_MAX_RETENTION, 1);
        final StorageCleaner storageCleaner = new StorageCleaner(props, this.storage, this.databaseOperator);
        Assert.assertTrue(storageCleaner.isCleanupPermitted());
        storageCleaner.cleanupProjectArtifacts(StorageCleanerTest.TEST_PROJECT_ID);
        Mockito.verify(this.storage, Mockito.never()).delete("14/14-9.zip");
        Mockito.verify(this.storage, Mockito.times(1)).delete("14/14-8.zip");
        Mockito.verify(this.storage, Mockito.times(1)).delete("14/14-7.zip");
        Mockito.verify(this.databaseOperator, Mockito.never()).update(StorageCleaner.SQL_DELETE_RESOURCE_ID, "14/14-9.zip");
        Mockito.verify(this.databaseOperator, Mockito.times(1)).update(StorageCleaner.SQL_DELETE_RESOURCE_ID, "14/14-8.zip");
        Mockito.verify(this.databaseOperator, Mockito.never()).update(StorageCleaner.SQL_DELETE_RESOURCE_ID, "14/14-7.zip");
    }
}

