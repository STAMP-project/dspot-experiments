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


import Field.Mode.REQUIRED;
import JobConfiguration.Type.LOAD;
import LegacySQLTypeName.INTEGER;
import SchemaUpdateOption.ALLOW_FIELD_ADDITION;
import Type.DAY;
import com.google.cloud.bigquery.JobInfo.CreateDisposition;
import com.google.cloud.bigquery.JobInfo.SchemaUpdateOption;
import com.google.cloud.bigquery.JobInfo.WriteDisposition;
import com.google.common.collect.ImmutableList;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class LoadJobConfigurationTest {
    private static final String TEST_PROJECT_ID = "test-project-id";

    private static final CsvOptions CSV_OPTIONS = CsvOptions.newBuilder().setAllowJaggedRows(true).setAllowQuotedNewLines(false).setEncoding(StandardCharsets.UTF_8).build();

    private static final TableId TABLE_ID = TableId.of("dataset", "table");

    private static final CreateDisposition CREATE_DISPOSITION = CreateDisposition.CREATE_IF_NEEDED;

    private static final WriteDisposition WRITE_DISPOSITION = WriteDisposition.WRITE_APPEND;

    private static final Integer MAX_BAD_RECORDS = 42;

    private static final String FORMAT = "CSV";

    private static final Boolean IGNORE_UNKNOWN_VALUES = true;

    private static final Field FIELD_SCHEMA = Field.newBuilder("IntegerField", INTEGER).setMode(REQUIRED).setDescription("FieldDescription").build();

    private static final List<String> SOURCE_URIS = ImmutableList.of("uri1", "uri2");

    private static final List<SchemaUpdateOption> SCHEMA_UPDATE_OPTIONS = ImmutableList.of(ALLOW_FIELD_ADDITION);

    private static final Schema TABLE_SCHEMA = Schema.of(LoadJobConfigurationTest.FIELD_SCHEMA);

    private static final Boolean AUTODETECT = true;

    private static final Boolean USERAVROLOGICALTYPES = true;

    private static final EncryptionConfiguration JOB_ENCRYPTION_CONFIGURATION = EncryptionConfiguration.newBuilder().setKmsKeyName("KMS_KEY_1").build();

    private static final TimePartitioning TIME_PARTITIONING = TimePartitioning.of(DAY);

    private static final Clustering CLUSTERING = Clustering.newBuilder().setFields(ImmutableList.of("Foo", "Bar")).build();

    private static final LoadJobConfiguration LOAD_CONFIGURATION_CSV = LoadJobConfiguration.newBuilder(LoadJobConfigurationTest.TABLE_ID, LoadJobConfigurationTest.SOURCE_URIS).setCreateDisposition(LoadJobConfigurationTest.CREATE_DISPOSITION).setWriteDisposition(LoadJobConfigurationTest.WRITE_DISPOSITION).setFormatOptions(LoadJobConfigurationTest.CSV_OPTIONS).setIgnoreUnknownValues(LoadJobConfigurationTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(LoadJobConfigurationTest.MAX_BAD_RECORDS).setSchema(LoadJobConfigurationTest.TABLE_SCHEMA).setSchemaUpdateOptions(LoadJobConfigurationTest.SCHEMA_UPDATE_OPTIONS).setAutodetect(LoadJobConfigurationTest.AUTODETECT).setDestinationEncryptionConfiguration(LoadJobConfigurationTest.JOB_ENCRYPTION_CONFIGURATION).setTimePartitioning(LoadJobConfigurationTest.TIME_PARTITIONING).setClustering(LoadJobConfigurationTest.CLUSTERING).build();

    private static final DatastoreBackupOptions BACKUP_OPTIONS = DatastoreBackupOptions.newBuilder().setProjectionFields(ImmutableList.of("field_1", "field_2")).build();

    private static final LoadJobConfiguration LOAD_CONFIGURATION_BACKUP = LoadJobConfiguration.newBuilder(LoadJobConfigurationTest.TABLE_ID, LoadJobConfigurationTest.SOURCE_URIS).setCreateDisposition(LoadJobConfigurationTest.CREATE_DISPOSITION).setWriteDisposition(LoadJobConfigurationTest.WRITE_DISPOSITION).setFormatOptions(LoadJobConfigurationTest.BACKUP_OPTIONS).setIgnoreUnknownValues(LoadJobConfigurationTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(LoadJobConfigurationTest.MAX_BAD_RECORDS).setSchema(LoadJobConfigurationTest.TABLE_SCHEMA).setSchemaUpdateOptions(LoadJobConfigurationTest.SCHEMA_UPDATE_OPTIONS).setAutodetect(LoadJobConfigurationTest.AUTODETECT).build();

    private static final LoadJobConfiguration LOAD_CONFIGURATION_AVRO = LoadJobConfiguration.newBuilder(LoadJobConfigurationTest.TABLE_ID, LoadJobConfigurationTest.SOURCE_URIS).setCreateDisposition(LoadJobConfigurationTest.CREATE_DISPOSITION).setWriteDisposition(LoadJobConfigurationTest.WRITE_DISPOSITION).setFormatOptions(FormatOptions.avro()).setIgnoreUnknownValues(LoadJobConfigurationTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(LoadJobConfigurationTest.MAX_BAD_RECORDS).setSchema(LoadJobConfigurationTest.TABLE_SCHEMA).setSchemaUpdateOptions(LoadJobConfigurationTest.SCHEMA_UPDATE_OPTIONS).setAutodetect(LoadJobConfigurationTest.AUTODETECT).setDestinationEncryptionConfiguration(LoadJobConfigurationTest.JOB_ENCRYPTION_CONFIGURATION).setTimePartitioning(LoadJobConfigurationTest.TIME_PARTITIONING).setClustering(LoadJobConfigurationTest.CLUSTERING).setUseAvroLogicalTypes(LoadJobConfigurationTest.USERAVROLOGICALTYPES).build();

    @Test
    public void testToBuilder() {
        compareLoadJobConfiguration(LoadJobConfigurationTest.LOAD_CONFIGURATION_CSV, LoadJobConfigurationTest.LOAD_CONFIGURATION_CSV.toBuilder().build());
        LoadJobConfiguration configurationCSV = LoadJobConfigurationTest.LOAD_CONFIGURATION_CSV.toBuilder().setDestinationTable(TableId.of("dataset", "newTable")).build();
        Assert.assertEquals("newTable", configurationCSV.getDestinationTable().getTable());
        configurationCSV = configurationCSV.toBuilder().setDestinationTable(LoadJobConfigurationTest.TABLE_ID).build();
        compareLoadJobConfiguration(LoadJobConfigurationTest.LOAD_CONFIGURATION_CSV, configurationCSV);
        compareLoadJobConfiguration(LoadJobConfigurationTest.LOAD_CONFIGURATION_BACKUP, LoadJobConfigurationTest.LOAD_CONFIGURATION_BACKUP.toBuilder().build());
        LoadJobConfiguration configurationBackup = LoadJobConfigurationTest.LOAD_CONFIGURATION_BACKUP.toBuilder().setDestinationTable(TableId.of("dataset", "newTable")).build();
        Assert.assertEquals("newTable", configurationBackup.getDestinationTable().getTable());
        configurationBackup = configurationBackup.toBuilder().setDestinationTable(LoadJobConfigurationTest.TABLE_ID).build();
        compareLoadJobConfiguration(LoadJobConfigurationTest.LOAD_CONFIGURATION_BACKUP, configurationBackup);
        compareLoadJobConfiguration(LoadJobConfigurationTest.LOAD_CONFIGURATION_AVRO, LoadJobConfigurationTest.LOAD_CONFIGURATION_AVRO.toBuilder().build());
        LoadJobConfiguration configurationAvro = LoadJobConfigurationTest.LOAD_CONFIGURATION_AVRO.toBuilder().setDestinationTable(TableId.of("dataset", "newTable")).build();
        Assert.assertEquals("newTable", configurationAvro.getDestinationTable().getTable());
        configurationAvro = configurationAvro.toBuilder().setDestinationTable(LoadJobConfigurationTest.TABLE_ID).build();
        compareLoadJobConfiguration(LoadJobConfigurationTest.LOAD_CONFIGURATION_AVRO, configurationAvro);
    }

    @Test
    public void testOf() {
        LoadJobConfiguration configuration = LoadJobConfiguration.of(LoadJobConfigurationTest.TABLE_ID, LoadJobConfigurationTest.SOURCE_URIS);
        Assert.assertEquals(LoadJobConfigurationTest.TABLE_ID, configuration.getDestinationTable());
        Assert.assertEquals(LoadJobConfigurationTest.SOURCE_URIS, configuration.getSourceUris());
        configuration = LoadJobConfiguration.of(LoadJobConfigurationTest.TABLE_ID, LoadJobConfigurationTest.SOURCE_URIS, LoadJobConfigurationTest.CSV_OPTIONS);
        Assert.assertEquals(LoadJobConfigurationTest.TABLE_ID, configuration.getDestinationTable());
        Assert.assertEquals(LoadJobConfigurationTest.FORMAT, configuration.getFormat());
        Assert.assertEquals(LoadJobConfigurationTest.CSV_OPTIONS, configuration.getCsvOptions());
        Assert.assertEquals(LoadJobConfigurationTest.SOURCE_URIS, configuration.getSourceUris());
        configuration = LoadJobConfiguration.of(LoadJobConfigurationTest.TABLE_ID, "uri1");
        Assert.assertEquals(LoadJobConfigurationTest.TABLE_ID, configuration.getDestinationTable());
        Assert.assertEquals(ImmutableList.of("uri1"), configuration.getSourceUris());
        configuration = LoadJobConfiguration.of(LoadJobConfigurationTest.TABLE_ID, "uri1", LoadJobConfigurationTest.CSV_OPTIONS);
        Assert.assertEquals(LoadJobConfigurationTest.TABLE_ID, configuration.getDestinationTable());
        Assert.assertEquals(LoadJobConfigurationTest.FORMAT, configuration.getFormat());
        Assert.assertEquals(LoadJobConfigurationTest.CSV_OPTIONS, configuration.getCsvOptions());
        Assert.assertEquals(ImmutableList.of("uri1"), configuration.getSourceUris());
    }

    @Test
    public void testToBuilderIncomplete() {
        LoadJobConfiguration configuration = LoadJobConfiguration.of(LoadJobConfigurationTest.TABLE_ID, LoadJobConfigurationTest.SOURCE_URIS);
        compareLoadJobConfiguration(configuration, configuration.toBuilder().build());
    }

    @Test
    public void testToPbAndFromPb() {
        compareLoadJobConfiguration(LoadJobConfigurationTest.LOAD_CONFIGURATION_CSV, LoadJobConfiguration.fromPb(LoadJobConfigurationTest.LOAD_CONFIGURATION_CSV.toPb()));
        LoadJobConfiguration configuration = LoadJobConfiguration.of(LoadJobConfigurationTest.TABLE_ID, LoadJobConfigurationTest.SOURCE_URIS);
        compareLoadJobConfiguration(configuration, LoadJobConfiguration.fromPb(configuration.toPb()));
    }

    @Test
    public void testSetProjectId() {
        LoadConfiguration configuration = LoadJobConfigurationTest.LOAD_CONFIGURATION_CSV.setProjectId(LoadJobConfigurationTest.TEST_PROJECT_ID);
        Assert.assertEquals(LoadJobConfigurationTest.TEST_PROJECT_ID, configuration.getDestinationTable().getProject());
    }

    @Test
    public void testSetProjectIdDoNotOverride() {
        LoadConfiguration configuration = LoadJobConfigurationTest.LOAD_CONFIGURATION_CSV.toBuilder().setDestinationTable(LoadJobConfigurationTest.TABLE_ID.setProjectId(LoadJobConfigurationTest.TEST_PROJECT_ID)).build().setProjectId("do-not-update");
        Assert.assertEquals(LoadJobConfigurationTest.TEST_PROJECT_ID, configuration.getDestinationTable().getProject());
    }

    @Test
    public void testGetType() {
        Assert.assertEquals(LOAD, LoadJobConfigurationTest.LOAD_CONFIGURATION_CSV.getType());
    }
}

