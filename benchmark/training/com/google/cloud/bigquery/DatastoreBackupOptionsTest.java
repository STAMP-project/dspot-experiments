/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import FormatOptions.DATASTORE_BACKUP;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DatastoreBackupOptionsTest {
    private static final List<String> PROJECTION_FIELDS = ImmutableList.of("field1", "field2");

    private static final DatastoreBackupOptions BACKUP_OPTIONS = DatastoreBackupOptions.newBuilder().setProjectionFields(DatastoreBackupOptionsTest.PROJECTION_FIELDS).build();

    @Test
    public void testToBuilder() {
        compareDatastoreBackupOptions(DatastoreBackupOptionsTest.BACKUP_OPTIONS, DatastoreBackupOptionsTest.BACKUP_OPTIONS.toBuilder().build());
        List<String> fields = ImmutableList.of("field1", "field2");
        DatastoreBackupOptions backupOptions = DatastoreBackupOptionsTest.BACKUP_OPTIONS.toBuilder().setProjectionFields(fields).build();
        Assert.assertEquals(fields, backupOptions.getProjectionFields());
        backupOptions = backupOptions.toBuilder().setProjectionFields(DatastoreBackupOptionsTest.PROJECTION_FIELDS).build();
        compareDatastoreBackupOptions(DatastoreBackupOptionsTest.BACKUP_OPTIONS, backupOptions);
    }

    @Test
    public void testToBuilderIncomplete() {
        DatastoreBackupOptions backupOptions = DatastoreBackupOptions.newBuilder().setProjectionFields(DatastoreBackupOptionsTest.PROJECTION_FIELDS).build();
        Assert.assertEquals(backupOptions, backupOptions.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(DATASTORE_BACKUP, DatastoreBackupOptionsTest.BACKUP_OPTIONS.getType());
        Assert.assertEquals(DatastoreBackupOptionsTest.PROJECTION_FIELDS, DatastoreBackupOptionsTest.BACKUP_OPTIONS.getProjectionFields());
    }
}

