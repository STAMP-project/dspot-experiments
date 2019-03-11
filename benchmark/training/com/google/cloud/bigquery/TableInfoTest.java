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


import Field.Mode.NULLABLE;
import Field.Mode.REPEATED;
import Field.Mode.REQUIRED;
import LegacySQLTypeName.INTEGER;
import LegacySQLTypeName.RECORD;
import LegacySQLTypeName.STRING;
import StandardTableDefinition.StreamingBuffer;
import com.google.common.collect.ImmutableList;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TableInfoTest {
    private static final String ETAG = "etag";

    private static final String GENERATED_ID = "project:dataset:table";

    private static final String SELF_LINK = "selfLink";

    private static final TableId TABLE_ID = TableId.of("dataset", "table");

    private static final String FRIENDLY_NAME = "friendlyName";

    private static final String DESCRIPTION = "description";

    private static final Long CREATION_TIME = 10L;

    private static final Long EXPIRATION_TIME = 100L;

    private static final Long LAST_MODIFIED_TIME = 20L;

    private static final Field FIELD_SCHEMA1 = Field.newBuilder("StringField", STRING).setMode(NULLABLE).setDescription("FieldDescription1").build();

    private static final Field FIELD_SCHEMA2 = Field.newBuilder("IntegerField", INTEGER).setMode(REPEATED).setDescription("FieldDescription2").build();

    private static final Field FIELD_SCHEMA3 = Field.newBuilder("RecordField", RECORD, TableInfoTest.FIELD_SCHEMA1, TableInfoTest.FIELD_SCHEMA2).setMode(REQUIRED).setDescription("FieldDescription3").build();

    private static final Schema TABLE_SCHEMA = Schema.of(TableInfoTest.FIELD_SCHEMA1, TableInfoTest.FIELD_SCHEMA2, TableInfoTest.FIELD_SCHEMA3);

    private static final Long NUM_BYTES = 42L;

    private static final Long NUM_LONG_TERM_BYTES = 21L;

    private static final Long NUM_ROWS = 43L;

    private static final String LOCATION = "US";

    private static final StreamingBuffer STREAMING_BUFFER = new StandardTableDefinition.StreamingBuffer(1L, 2L, 3L);

    private static final StandardTableDefinition TABLE_DEFINITION = StandardTableDefinition.newBuilder().setLocation(TableInfoTest.LOCATION).setNumBytes(TableInfoTest.NUM_BYTES).setNumLongTermBytes(TableInfoTest.NUM_LONG_TERM_BYTES).setNumRows(TableInfoTest.NUM_ROWS).setStreamingBuffer(TableInfoTest.STREAMING_BUFFER).setSchema(TableInfoTest.TABLE_SCHEMA).build();

    private static final List<String> SOURCE_URIS = ImmutableList.of("uri1", "uri2");

    private static final Integer MAX_BAD_RECORDS = 42;

    private static final Boolean IGNORE_UNKNOWN_VALUES = true;

    private static final String COMPRESSION = "GZIP";

    private static final CsvOptions CSV_OPTIONS = CsvOptions.newBuilder().build();

    private static final ExternalTableDefinition EXTERNAL_TABLE_DEFINITION = ExternalTableDefinition.newBuilder(TableInfoTest.SOURCE_URIS, TableInfoTest.TABLE_SCHEMA, TableInfoTest.CSV_OPTIONS).setCompression(TableInfoTest.COMPRESSION).setIgnoreUnknownValues(TableInfoTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(TableInfoTest.MAX_BAD_RECORDS).build();

    private static final String VIEW_QUERY = "VIEW QUERY";

    private static final List<UserDefinedFunction> USER_DEFINED_FUNCTIONS = ImmutableList.of(UserDefinedFunction.inline("Function"), UserDefinedFunction.fromUri("URI"));

    private static final ViewDefinition VIEW_DEFINITION = ViewDefinition.newBuilder(TableInfoTest.VIEW_QUERY, TableInfoTest.USER_DEFINED_FUNCTIONS).build();

    private static final TableInfo TABLE_INFO = TableInfo.newBuilder(TableInfoTest.TABLE_ID, TableInfoTest.TABLE_DEFINITION).setCreationTime(TableInfoTest.CREATION_TIME).setDescription(TableInfoTest.DESCRIPTION).setEtag(TableInfoTest.ETAG).setExpirationTime(TableInfoTest.EXPIRATION_TIME).setFriendlyName(TableInfoTest.FRIENDLY_NAME).setGeneratedId(TableInfoTest.GENERATED_ID).setLastModifiedTime(TableInfoTest.LAST_MODIFIED_TIME).setNumBytes(TableInfoTest.NUM_BYTES).setNumLongTermBytes(TableInfoTest.NUM_LONG_TERM_BYTES).setNumRows(BigInteger.valueOf(TableInfoTest.NUM_ROWS)).setSelfLink(TableInfoTest.SELF_LINK).setLabels(Collections.singletonMap("a", "b")).build();

    private static final TableInfo VIEW_INFO = TableInfo.newBuilder(TableInfoTest.TABLE_ID, TableInfoTest.VIEW_DEFINITION).setCreationTime(TableInfoTest.CREATION_TIME).setDescription(TableInfoTest.DESCRIPTION).setEtag(TableInfoTest.ETAG).setExpirationTime(TableInfoTest.EXPIRATION_TIME).setFriendlyName(TableInfoTest.FRIENDLY_NAME).setGeneratedId(TableInfoTest.GENERATED_ID).setLastModifiedTime(TableInfoTest.LAST_MODIFIED_TIME).setSelfLink(TableInfoTest.SELF_LINK).build();

    private static final TableInfo EXTERNAL_TABLE_INFO = TableInfo.newBuilder(TableInfoTest.TABLE_ID, TableInfoTest.EXTERNAL_TABLE_DEFINITION).setCreationTime(TableInfoTest.CREATION_TIME).setDescription(TableInfoTest.DESCRIPTION).setEtag(TableInfoTest.ETAG).setExpirationTime(TableInfoTest.EXPIRATION_TIME).setFriendlyName(TableInfoTest.FRIENDLY_NAME).setGeneratedId(TableInfoTest.GENERATED_ID).setLastModifiedTime(TableInfoTest.LAST_MODIFIED_TIME).setSelfLink(TableInfoTest.SELF_LINK).build();

    @Test
    public void testToBuilder() {
        compareTableInfo(TableInfoTest.TABLE_INFO, TableInfoTest.TABLE_INFO.toBuilder().build());
        compareTableInfo(TableInfoTest.VIEW_INFO, TableInfoTest.VIEW_INFO.toBuilder().build());
        compareTableInfo(TableInfoTest.EXTERNAL_TABLE_INFO, TableInfoTest.EXTERNAL_TABLE_INFO.toBuilder().build());
        TableInfo tableInfo = TableInfoTest.TABLE_INFO.toBuilder().setDescription("newDescription").build();
        Assert.assertEquals("newDescription", tableInfo.getDescription());
        tableInfo = tableInfo.toBuilder().setDescription("description").build();
        compareTableInfo(TableInfoTest.TABLE_INFO, tableInfo);
    }

    @Test
    public void testToBuilderIncomplete() {
        TableInfo tableInfo = TableInfo.of(TableInfoTest.TABLE_ID, TableInfoTest.TABLE_DEFINITION);
        Assert.assertEquals(tableInfo, tableInfo.toBuilder().build());
        tableInfo = TableInfo.of(TableInfoTest.TABLE_ID, TableInfoTest.VIEW_DEFINITION);
        Assert.assertEquals(tableInfo, tableInfo.toBuilder().build());
        tableInfo = TableInfo.of(TableInfoTest.TABLE_ID, TableInfoTest.EXTERNAL_TABLE_DEFINITION);
        Assert.assertEquals(tableInfo, tableInfo.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(TableInfoTest.TABLE_ID, TableInfoTest.TABLE_INFO.getTableId());
        Assert.assertEquals(TableInfoTest.CREATION_TIME, TableInfoTest.TABLE_INFO.getCreationTime());
        Assert.assertEquals(TableInfoTest.DESCRIPTION, TableInfoTest.TABLE_INFO.getDescription());
        Assert.assertEquals(TableInfoTest.ETAG, TableInfoTest.TABLE_INFO.getEtag());
        Assert.assertEquals(TableInfoTest.EXPIRATION_TIME, TableInfoTest.TABLE_INFO.getExpirationTime());
        Assert.assertEquals(TableInfoTest.FRIENDLY_NAME, TableInfoTest.TABLE_INFO.getFriendlyName());
        Assert.assertEquals(TableInfoTest.GENERATED_ID, TableInfoTest.TABLE_INFO.getGeneratedId());
        Assert.assertEquals(TableInfoTest.LAST_MODIFIED_TIME, TableInfoTest.TABLE_INFO.getLastModifiedTime());
        Assert.assertEquals(TableInfoTest.TABLE_DEFINITION, TableInfoTest.TABLE_INFO.getDefinition());
        Assert.assertEquals(TableInfoTest.SELF_LINK, TableInfoTest.TABLE_INFO.getSelfLink());
        Assert.assertEquals(TableInfoTest.NUM_BYTES, TableInfoTest.TABLE_INFO.getNumBytes());
        Assert.assertEquals(TableInfoTest.NUM_LONG_TERM_BYTES, TableInfoTest.TABLE_INFO.getNumLongTermBytes());
        Assert.assertEquals(BigInteger.valueOf(TableInfoTest.NUM_ROWS), TableInfoTest.TABLE_INFO.getNumRows());
        Assert.assertEquals(TableInfoTest.TABLE_ID, TableInfoTest.VIEW_INFO.getTableId());
        Assert.assertEquals(TableInfoTest.VIEW_DEFINITION, TableInfoTest.VIEW_INFO.getDefinition());
        Assert.assertEquals(TableInfoTest.CREATION_TIME, TableInfoTest.VIEW_INFO.getCreationTime());
        Assert.assertEquals(TableInfoTest.DESCRIPTION, TableInfoTest.VIEW_INFO.getDescription());
        Assert.assertEquals(TableInfoTest.ETAG, TableInfoTest.VIEW_INFO.getEtag());
        Assert.assertEquals(TableInfoTest.EXPIRATION_TIME, TableInfoTest.VIEW_INFO.getExpirationTime());
        Assert.assertEquals(TableInfoTest.FRIENDLY_NAME, TableInfoTest.VIEW_INFO.getFriendlyName());
        Assert.assertEquals(TableInfoTest.GENERATED_ID, TableInfoTest.VIEW_INFO.getGeneratedId());
        Assert.assertEquals(TableInfoTest.LAST_MODIFIED_TIME, TableInfoTest.VIEW_INFO.getLastModifiedTime());
        Assert.assertEquals(TableInfoTest.VIEW_DEFINITION, TableInfoTest.VIEW_INFO.getDefinition());
        Assert.assertEquals(TableInfoTest.SELF_LINK, TableInfoTest.VIEW_INFO.getSelfLink());
        Assert.assertEquals(TableInfoTest.TABLE_ID, TableInfoTest.EXTERNAL_TABLE_INFO.getTableId());
        Assert.assertEquals(TableInfoTest.CREATION_TIME, TableInfoTest.EXTERNAL_TABLE_INFO.getCreationTime());
        Assert.assertEquals(TableInfoTest.DESCRIPTION, TableInfoTest.EXTERNAL_TABLE_INFO.getDescription());
        Assert.assertEquals(TableInfoTest.ETAG, TableInfoTest.EXTERNAL_TABLE_INFO.getEtag());
        Assert.assertEquals(TableInfoTest.EXPIRATION_TIME, TableInfoTest.EXTERNAL_TABLE_INFO.getExpirationTime());
        Assert.assertEquals(TableInfoTest.FRIENDLY_NAME, TableInfoTest.EXTERNAL_TABLE_INFO.getFriendlyName());
        Assert.assertEquals(TableInfoTest.GENERATED_ID, TableInfoTest.EXTERNAL_TABLE_INFO.getGeneratedId());
        Assert.assertEquals(TableInfoTest.LAST_MODIFIED_TIME, TableInfoTest.EXTERNAL_TABLE_INFO.getLastModifiedTime());
        Assert.assertEquals(TableInfoTest.EXTERNAL_TABLE_DEFINITION, TableInfoTest.EXTERNAL_TABLE_INFO.getDefinition());
        Assert.assertEquals(TableInfoTest.SELF_LINK, TableInfoTest.EXTERNAL_TABLE_INFO.getSelfLink());
    }

    @Test
    public void testOf() {
        TableInfo tableInfo = TableInfo.of(TableInfoTest.TABLE_ID, TableInfoTest.TABLE_DEFINITION);
        Assert.assertEquals(TableInfoTest.TABLE_ID, tableInfo.getTableId());
        Assert.assertNull(tableInfo.getCreationTime());
        Assert.assertNull(tableInfo.getDescription());
        Assert.assertNull(tableInfo.getEtag());
        Assert.assertNull(tableInfo.getExpirationTime());
        Assert.assertNull(tableInfo.getFriendlyName());
        Assert.assertNull(tableInfo.getGeneratedId());
        Assert.assertNull(tableInfo.getLastModifiedTime());
        Assert.assertEquals(TableInfoTest.TABLE_DEFINITION, tableInfo.getDefinition());
        Assert.assertNull(tableInfo.getSelfLink());
        tableInfo = TableInfo.of(TableInfoTest.TABLE_ID, TableInfoTest.VIEW_DEFINITION);
        Assert.assertEquals(TableInfoTest.TABLE_ID, tableInfo.getTableId());
        Assert.assertNull(tableInfo.getCreationTime());
        Assert.assertNull(tableInfo.getDescription());
        Assert.assertNull(tableInfo.getEtag());
        Assert.assertNull(tableInfo.getExpirationTime());
        Assert.assertNull(tableInfo.getFriendlyName());
        Assert.assertNull(tableInfo.getGeneratedId());
        Assert.assertNull(tableInfo.getLastModifiedTime());
        Assert.assertEquals(TableInfoTest.VIEW_DEFINITION, tableInfo.getDefinition());
        Assert.assertNull(tableInfo.getSelfLink());
        tableInfo = TableInfo.of(TableInfoTest.TABLE_ID, TableInfoTest.EXTERNAL_TABLE_DEFINITION);
        Assert.assertEquals(TableInfoTest.TABLE_ID, tableInfo.getTableId());
        Assert.assertNull(tableInfo.getCreationTime());
        Assert.assertNull(tableInfo.getDescription());
        Assert.assertNull(tableInfo.getEtag());
        Assert.assertNull(tableInfo.getExpirationTime());
        Assert.assertNull(tableInfo.getFriendlyName());
        Assert.assertNull(tableInfo.getGeneratedId());
        Assert.assertNull(tableInfo.getLastModifiedTime());
        Assert.assertEquals(TableInfoTest.EXTERNAL_TABLE_DEFINITION, tableInfo.getDefinition());
        Assert.assertNull(tableInfo.getSelfLink());
    }

    @Test
    public void testToAndFromPb() {
        compareTableInfo(TableInfoTest.TABLE_INFO, TableInfo.fromPb(TableInfoTest.TABLE_INFO.toPb()));
        compareTableInfo(TableInfoTest.VIEW_INFO, TableInfo.fromPb(TableInfoTest.VIEW_INFO.toPb()));
        compareTableInfo(TableInfoTest.EXTERNAL_TABLE_INFO, TableInfo.fromPb(TableInfoTest.EXTERNAL_TABLE_INFO.toPb()));
    }

    @Test
    public void testSetProjectId() {
        Assert.assertEquals("project", TableInfoTest.TABLE_INFO.setProjectId("project").getTableId().getProject());
        Assert.assertEquals("project", TableInfoTest.EXTERNAL_TABLE_INFO.setProjectId("project").getTableId().getProject());
        Assert.assertEquals("project", TableInfoTest.VIEW_INFO.setProjectId("project").getTableId().getProject());
    }

    @Test
    public void testSetProjectIdDoNotOverride() {
        TableInfo tableInfo = TableInfo.of(TableInfoTest.TABLE_ID, TableInfoTest.TABLE_DEFINITION).setProjectId("project");
        tableInfo.setProjectId("not-override-project").toBuilder();
        Assert.assertEquals("project", tableInfo.getTableId().getProject());
    }
}

