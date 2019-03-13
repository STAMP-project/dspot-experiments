/**
 * Copyright 2016 Google LLC
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


import JobConfiguration.Type.COPY;
import com.google.cloud.bigquery.JobInfo.CreateDisposition;
import com.google.cloud.bigquery.JobInfo.WriteDisposition;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class CopyJobConfigurationTest {
    private static final String TEST_PROJECT_ID = "test-project-id";

    private static final TableId SOURCE_TABLE = TableId.of("dataset", "sourceTable");

    private static final List<TableId> SOURCE_TABLES = ImmutableList.of(TableId.of("dataset", "sourceTable1"), TableId.of("dataset", "sourceTable2"));

    private static final TableId DESTINATION_TABLE = TableId.of("dataset", "destinationTable");

    private static final CreateDisposition CREATE_DISPOSITION = CreateDisposition.CREATE_IF_NEEDED;

    private static final WriteDisposition WRITE_DISPOSITION = WriteDisposition.WRITE_APPEND;

    private static final EncryptionConfiguration COPY_JOB_ENCRYPTION_CONFIGURATION = EncryptionConfiguration.newBuilder().setKmsKeyName("KMS_KEY_1").build();

    private static final CopyJobConfiguration COPY_JOB_CONFIGURATION = CopyJobConfiguration.newBuilder(CopyJobConfigurationTest.DESTINATION_TABLE, CopyJobConfigurationTest.SOURCE_TABLE).setCreateDisposition(CopyJobConfigurationTest.CREATE_DISPOSITION).setWriteDisposition(CopyJobConfigurationTest.WRITE_DISPOSITION).setDestinationEncryptionConfiguration(CopyJobConfigurationTest.COPY_JOB_ENCRYPTION_CONFIGURATION).build();

    private static final CopyJobConfiguration COPY_JOB_CONFIGURATION_MULTIPLE_TABLES = CopyJobConfiguration.newBuilder(CopyJobConfigurationTest.DESTINATION_TABLE, CopyJobConfigurationTest.SOURCE_TABLES).setCreateDisposition(CopyJobConfigurationTest.CREATE_DISPOSITION).setWriteDisposition(CopyJobConfigurationTest.WRITE_DISPOSITION).build();

    @Test
    public void testToBuilder() {
        compareCopyJobConfiguration(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION, CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.toBuilder().build());
        compareCopyJobConfiguration(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES, CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES.toBuilder().build());
        CopyJobConfiguration jobConfiguration = CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.toBuilder().setDestinationTable(TableId.of("dataset", "newTable")).build();
        Assert.assertEquals("newTable", jobConfiguration.getDestinationTable().getTable());
        jobConfiguration = jobConfiguration.toBuilder().setDestinationTable(CopyJobConfigurationTest.DESTINATION_TABLE).build();
        compareCopyJobConfiguration(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION, jobConfiguration);
    }

    @Test
    public void testOf() {
        CopyJobConfiguration job = CopyJobConfiguration.of(CopyJobConfigurationTest.DESTINATION_TABLE, CopyJobConfigurationTest.SOURCE_TABLES);
        Assert.assertEquals(CopyJobConfigurationTest.DESTINATION_TABLE, job.getDestinationTable());
        Assert.assertEquals(CopyJobConfigurationTest.SOURCE_TABLES, job.getSourceTables());
        job = CopyJobConfiguration.of(CopyJobConfigurationTest.DESTINATION_TABLE, CopyJobConfigurationTest.SOURCE_TABLE);
        Assert.assertEquals(CopyJobConfigurationTest.DESTINATION_TABLE, job.getDestinationTable());
        Assert.assertEquals(ImmutableList.of(CopyJobConfigurationTest.SOURCE_TABLE), job.getSourceTables());
    }

    @Test
    public void testToBuilderIncomplete() {
        CopyJobConfiguration jobConfiguration = CopyJobConfiguration.of(CopyJobConfigurationTest.DESTINATION_TABLE, CopyJobConfigurationTest.SOURCE_TABLES);
        compareCopyJobConfiguration(jobConfiguration, jobConfiguration.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(CopyJobConfigurationTest.DESTINATION_TABLE, CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES.getDestinationTable());
        Assert.assertEquals(CopyJobConfigurationTest.SOURCE_TABLES, CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES.getSourceTables());
        Assert.assertEquals(CopyJobConfigurationTest.CREATE_DISPOSITION, CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES.getCreateDisposition());
        Assert.assertEquals(CopyJobConfigurationTest.WRITE_DISPOSITION, CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES.getWriteDisposition());
        Assert.assertEquals(CopyJobConfigurationTest.DESTINATION_TABLE, CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.getDestinationTable());
        Assert.assertEquals(ImmutableList.of(CopyJobConfigurationTest.SOURCE_TABLE), CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.getSourceTables());
        Assert.assertEquals(CopyJobConfigurationTest.CREATE_DISPOSITION, CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.getCreateDisposition());
        Assert.assertEquals(CopyJobConfigurationTest.WRITE_DISPOSITION, CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.getWriteDisposition());
    }

    @Test
    public void testToPbAndFromPb() {
        Assert.assertNotNull(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.toPb().getCopy());
        Assert.assertNull(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.toPb().getExtract());
        Assert.assertNull(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.toPb().getLoad());
        Assert.assertNull(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.toPb().getQuery());
        Assert.assertNull(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.toPb().getCopy().getSourceTables());
        Assert.assertNull(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES.toPb().getCopy().getSourceTable());
        compareCopyJobConfiguration(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION, CopyJobConfiguration.fromPb(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.toPb()));
        compareCopyJobConfiguration(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES, CopyJobConfiguration.fromPb(CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES.toPb()));
        CopyJobConfiguration jobConfiguration = CopyJobConfiguration.of(CopyJobConfigurationTest.DESTINATION_TABLE, CopyJobConfigurationTest.SOURCE_TABLES);
        compareCopyJobConfiguration(jobConfiguration, CopyJobConfiguration.fromPb(jobConfiguration.toPb()));
    }

    @Test
    public void testSetProjectId() {
        CopyJobConfiguration configuration = CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES.setProjectId(CopyJobConfigurationTest.TEST_PROJECT_ID);
        Assert.assertEquals(CopyJobConfigurationTest.TEST_PROJECT_ID, configuration.getDestinationTable().getProject());
        for (TableId sourceTable : configuration.getSourceTables()) {
            Assert.assertEquals(CopyJobConfigurationTest.TEST_PROJECT_ID, sourceTable.getProject());
        }
    }

    @Test
    public void testSetProjectIdDoNotOverride() {
        CopyJobConfiguration configuration = CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES.toBuilder().setSourceTables(Lists.transform(CopyJobConfigurationTest.SOURCE_TABLES, new Function<TableId, TableId>() {
            @Override
            public TableId apply(TableId tableId) {
                return tableId.setProjectId(CopyJobConfigurationTest.TEST_PROJECT_ID);
            }
        })).setDestinationTable(CopyJobConfigurationTest.DESTINATION_TABLE.setProjectId(CopyJobConfigurationTest.TEST_PROJECT_ID)).build().setProjectId("do-not-update");
        Assert.assertEquals(CopyJobConfigurationTest.TEST_PROJECT_ID, configuration.getDestinationTable().getProject());
        for (TableId sourceTable : configuration.getSourceTables()) {
            Assert.assertEquals(CopyJobConfigurationTest.TEST_PROJECT_ID, sourceTable.getProject());
        }
    }

    @Test
    public void testGetType() {
        Assert.assertEquals(COPY, CopyJobConfigurationTest.COPY_JOB_CONFIGURATION.getType());
        Assert.assertEquals(COPY, CopyJobConfigurationTest.COPY_JOB_CONFIGURATION_MULTIPLE_TABLES.getType());
    }
}

