/**
 * Copyright 2015 Google LLC
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
import JobInfo.SchemaUpdateOption.ALLOW_FIELD_ADDITION;
import LegacySQLTypeName.INTEGER;
import Type.DAY;
import WriteChannelConfiguration.Builder;
import com.google.cloud.bigquery.JobInfo.CreateDisposition;
import com.google.cloud.bigquery.JobInfo.WriteDisposition;
import com.google.common.collect.ImmutableList;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class WriteChannelConfigurationTest {
    private static final CsvOptions CSV_OPTIONS = CsvOptions.newBuilder().setAllowJaggedRows(true).setAllowQuotedNewLines(false).setEncoding(StandardCharsets.UTF_8).build();

    private static final TableId TABLE_ID = TableId.of("dataset", "table");

    private static final CreateDisposition CREATE_DISPOSITION = CreateDisposition.CREATE_IF_NEEDED;

    private static final WriteDisposition WRITE_DISPOSITION = WriteDisposition.WRITE_APPEND;

    private static final String NULL_MARKER = "\\N";

    private static final Integer MAX_BAD_RECORDS = 42;

    private static final String FORMAT = "CSV";

    private static final Boolean IGNORE_UNKNOWN_VALUES = true;

    private static final Field FIELD_SCHEMA = Field.newBuilder("IntegerField", INTEGER).setMode(REQUIRED).setDescription("FieldDescription").build();

    private static final Schema TABLE_SCHEMA = Schema.of(WriteChannelConfigurationTest.FIELD_SCHEMA);

    private static final Boolean AUTODETECT = true;

    private static final Boolean USERAVROLOGICALTYPES = true;

    private static final List<JobInfo.SchemaUpdateOption> SCHEMA_UPDATE_OPTIONS = ImmutableList.of(ALLOW_FIELD_ADDITION);

    private static final TimePartitioning TIME_PARTITIONING = TimePartitioning.of(DAY);

    private static final Clustering CLUSTERING = Clustering.newBuilder().setFields(ImmutableList.of("Foo", "Bar")).build();

    private static final WriteChannelConfiguration LOAD_CONFIGURATION_CSV = WriteChannelConfiguration.newBuilder(WriteChannelConfigurationTest.TABLE_ID).setCreateDisposition(WriteChannelConfigurationTest.CREATE_DISPOSITION).setWriteDisposition(WriteChannelConfigurationTest.WRITE_DISPOSITION).setNullMarker(WriteChannelConfigurationTest.NULL_MARKER).setFormatOptions(WriteChannelConfigurationTest.CSV_OPTIONS).setIgnoreUnknownValues(WriteChannelConfigurationTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(WriteChannelConfigurationTest.MAX_BAD_RECORDS).setSchema(WriteChannelConfigurationTest.TABLE_SCHEMA).setSchemaUpdateOptions(WriteChannelConfigurationTest.SCHEMA_UPDATE_OPTIONS).setAutodetect(WriteChannelConfigurationTest.AUTODETECT).setTimePartitioning(WriteChannelConfigurationTest.TIME_PARTITIONING).setClustering(WriteChannelConfigurationTest.CLUSTERING).build();

    private static final DatastoreBackupOptions BACKUP_OPTIONS = DatastoreBackupOptions.newBuilder().setProjectionFields(ImmutableList.of("field_1", "field_2")).build();

    private static final WriteChannelConfiguration LOAD_CONFIGURATION_BACKUP = WriteChannelConfiguration.newBuilder(WriteChannelConfigurationTest.TABLE_ID).setCreateDisposition(WriteChannelConfigurationTest.CREATE_DISPOSITION).setWriteDisposition(WriteChannelConfigurationTest.WRITE_DISPOSITION).setFormatOptions(WriteChannelConfigurationTest.BACKUP_OPTIONS).setIgnoreUnknownValues(WriteChannelConfigurationTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(WriteChannelConfigurationTest.MAX_BAD_RECORDS).setSchema(WriteChannelConfigurationTest.TABLE_SCHEMA).setSchemaUpdateOptions(WriteChannelConfigurationTest.SCHEMA_UPDATE_OPTIONS).setAutodetect(WriteChannelConfigurationTest.AUTODETECT).build();

    private static final WriteChannelConfiguration LOAD_CONFIGURATION_AVRO = WriteChannelConfiguration.newBuilder(WriteChannelConfigurationTest.TABLE_ID).setCreateDisposition(WriteChannelConfigurationTest.CREATE_DISPOSITION).setWriteDisposition(WriteChannelConfigurationTest.WRITE_DISPOSITION).setNullMarker(WriteChannelConfigurationTest.NULL_MARKER).setFormatOptions(FormatOptions.avro()).setIgnoreUnknownValues(WriteChannelConfigurationTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(WriteChannelConfigurationTest.MAX_BAD_RECORDS).setSchema(WriteChannelConfigurationTest.TABLE_SCHEMA).setSchemaUpdateOptions(WriteChannelConfigurationTest.SCHEMA_UPDATE_OPTIONS).setAutodetect(WriteChannelConfigurationTest.AUTODETECT).setTimePartitioning(WriteChannelConfigurationTest.TIME_PARTITIONING).setClustering(WriteChannelConfigurationTest.CLUSTERING).setUseAvroLogicalTypes(WriteChannelConfigurationTest.USERAVROLOGICALTYPES).build();

    @Test
    public void testToBuilder() {
        compareLoadConfiguration(WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV, WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.toBuilder().build());
        WriteChannelConfiguration configuration = WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.toBuilder().setDestinationTable(TableId.of("dataset", "newTable")).build();
        Assert.assertEquals("newTable", configuration.getDestinationTable().getTable());
        configuration = configuration.toBuilder().setDestinationTable(WriteChannelConfigurationTest.TABLE_ID).build();
        compareLoadConfiguration(WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV, configuration);
        compareLoadConfiguration(WriteChannelConfigurationTest.LOAD_CONFIGURATION_AVRO, WriteChannelConfigurationTest.LOAD_CONFIGURATION_AVRO.toBuilder().build());
        WriteChannelConfiguration configurationAvro = WriteChannelConfigurationTest.LOAD_CONFIGURATION_AVRO.toBuilder().setDestinationTable(TableId.of("dataset", "newTable")).build();
        Assert.assertEquals("newTable", configurationAvro.getDestinationTable().getTable());
        configurationAvro = configurationAvro.toBuilder().setDestinationTable(WriteChannelConfigurationTest.TABLE_ID).build();
        compareLoadConfiguration(WriteChannelConfigurationTest.LOAD_CONFIGURATION_AVRO, configurationAvro);
    }

    @Test
    public void testOf() {
        WriteChannelConfiguration configuration = WriteChannelConfiguration.of(WriteChannelConfigurationTest.TABLE_ID);
        Assert.assertEquals(WriteChannelConfigurationTest.TABLE_ID, configuration.getDestinationTable());
        configuration = WriteChannelConfiguration.of(WriteChannelConfigurationTest.TABLE_ID, WriteChannelConfigurationTest.CSV_OPTIONS);
        Assert.assertEquals(WriteChannelConfigurationTest.TABLE_ID, configuration.getDestinationTable());
        Assert.assertEquals(WriteChannelConfigurationTest.FORMAT, configuration.getFormat());
        Assert.assertEquals(WriteChannelConfigurationTest.CSV_OPTIONS, configuration.getCsvOptions());
    }

    @Test
    public void testToBuilderIncomplete() {
        WriteChannelConfiguration configuration = WriteChannelConfiguration.of(WriteChannelConfigurationTest.TABLE_ID);
        compareLoadConfiguration(configuration, configuration.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(WriteChannelConfigurationTest.TABLE_ID, WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.getDestinationTable());
        Assert.assertEquals(WriteChannelConfigurationTest.CREATE_DISPOSITION, WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.getCreateDisposition());
        Assert.assertEquals(WriteChannelConfigurationTest.WRITE_DISPOSITION, WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.getWriteDisposition());
        Assert.assertEquals(WriteChannelConfigurationTest.NULL_MARKER, WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.getNullMarker());
        Assert.assertEquals(WriteChannelConfigurationTest.CSV_OPTIONS, WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.getCsvOptions());
        Assert.assertEquals(WriteChannelConfigurationTest.FORMAT, WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.getFormat());
        Assert.assertEquals(WriteChannelConfigurationTest.IGNORE_UNKNOWN_VALUES, WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.ignoreUnknownValues());
        Assert.assertEquals(WriteChannelConfigurationTest.MAX_BAD_RECORDS, WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.getMaxBadRecords());
        Assert.assertEquals(WriteChannelConfigurationTest.TABLE_SCHEMA, WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.getSchema());
        Assert.assertEquals(WriteChannelConfigurationTest.BACKUP_OPTIONS, WriteChannelConfigurationTest.LOAD_CONFIGURATION_BACKUP.getDatastoreBackupOptions());
        Assert.assertEquals(WriteChannelConfigurationTest.SCHEMA_UPDATE_OPTIONS, WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.getSchemaUpdateOptions());
        Assert.assertEquals(WriteChannelConfigurationTest.SCHEMA_UPDATE_OPTIONS, WriteChannelConfigurationTest.LOAD_CONFIGURATION_BACKUP.getSchemaUpdateOptions());
        WriteChannelConfiguration.Builder builder = WriteChannelConfiguration.newBuilder(WriteChannelConfigurationTest.TABLE_ID, WriteChannelConfigurationTest.CSV_OPTIONS).setCreateDisposition(WriteChannelConfigurationTest.CREATE_DISPOSITION).setWriteDisposition(WriteChannelConfigurationTest.WRITE_DISPOSITION).setNullMarker(WriteChannelConfigurationTest.NULL_MARKER).setIgnoreUnknownValues(WriteChannelConfigurationTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(WriteChannelConfigurationTest.MAX_BAD_RECORDS).setSchemaUpdateOptions(WriteChannelConfigurationTest.SCHEMA_UPDATE_OPTIONS).setSchema(WriteChannelConfigurationTest.TABLE_SCHEMA).setAutodetect(WriteChannelConfigurationTest.AUTODETECT);
        WriteChannelConfiguration loadConfigurationCSV = builder.build();
        Assert.assertEquals(WriteChannelConfigurationTest.TABLE_ID, loadConfigurationCSV.getDestinationTable());
        Assert.assertEquals(WriteChannelConfigurationTest.CREATE_DISPOSITION, loadConfigurationCSV.getCreateDisposition());
        Assert.assertEquals(WriteChannelConfigurationTest.WRITE_DISPOSITION, loadConfigurationCSV.getWriteDisposition());
        Assert.assertEquals(WriteChannelConfigurationTest.NULL_MARKER, loadConfigurationCSV.getNullMarker());
        Assert.assertEquals(WriteChannelConfigurationTest.CSV_OPTIONS, loadConfigurationCSV.getCsvOptions());
        Assert.assertEquals(WriteChannelConfigurationTest.FORMAT, loadConfigurationCSV.getFormat());
        Assert.assertEquals(WriteChannelConfigurationTest.IGNORE_UNKNOWN_VALUES, loadConfigurationCSV.ignoreUnknownValues());
        Assert.assertEquals(WriteChannelConfigurationTest.MAX_BAD_RECORDS, loadConfigurationCSV.getMaxBadRecords());
        Assert.assertEquals(WriteChannelConfigurationTest.TABLE_SCHEMA, loadConfigurationCSV.getSchema());
        Assert.assertEquals(WriteChannelConfigurationTest.SCHEMA_UPDATE_OPTIONS, loadConfigurationCSV.getSchemaUpdateOptions());
        Assert.assertEquals(WriteChannelConfigurationTest.AUTODETECT, loadConfigurationCSV.getAutodetect());
        builder.setFormatOptions(WriteChannelConfigurationTest.BACKUP_OPTIONS);
        WriteChannelConfiguration loadConfigurationBackup = builder.build();
        Assert.assertEquals(WriteChannelConfigurationTest.BACKUP_OPTIONS, loadConfigurationBackup.getDatastoreBackupOptions());
    }

    @Test
    public void testToPbAndFromPb() {
        Assert.assertNull(WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.toPb().getLoad().getSourceUris());
        compareLoadConfiguration(WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV, WriteChannelConfiguration.fromPb(WriteChannelConfigurationTest.LOAD_CONFIGURATION_CSV.toPb()));
        WriteChannelConfiguration configuration = WriteChannelConfiguration.of(WriteChannelConfigurationTest.TABLE_ID);
        compareLoadConfiguration(configuration, WriteChannelConfiguration.fromPb(configuration.toPb()));
    }

    @Test
    public void testSetProjectIdDoNotOverride() {
        WriteChannelConfiguration configuration = WriteChannelConfiguration.of(WriteChannelConfigurationTest.TABLE_ID).setProjectId("project");
        configuration.setProjectId("different-project").toBuilder();
        Assert.assertEquals("project", configuration.getDestinationTable().getProject());
    }
}

