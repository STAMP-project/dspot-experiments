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


import Field.Mode.NULLABLE;
import Field.Mode.REPEATED;
import Field.Mode.REQUIRED;
import LegacySQLTypeName.INTEGER;
import LegacySQLTypeName.RECORD;
import LegacySQLTypeName.STRING;
import TableDefinition.Type.EXTERNAL;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ExternalTableDefinitionTest {
    private static final List<String> SOURCE_URIS = ImmutableList.of("uri1", "uri2");

    private static final Field FIELD_SCHEMA1 = Field.newBuilder("StringField", STRING).setMode(NULLABLE).setDescription("FieldDescription1").build();

    private static final Field FIELD_SCHEMA2 = Field.newBuilder("IntegerField", INTEGER).setMode(REPEATED).setDescription("FieldDescription2").build();

    private static final Field FIELD_SCHEMA3 = Field.newBuilder("RecordField", RECORD, ExternalTableDefinitionTest.FIELD_SCHEMA1, ExternalTableDefinitionTest.FIELD_SCHEMA2).setMode(REQUIRED).setDescription("FieldDescription3").build();

    private static final Schema TABLE_SCHEMA = Schema.of(ExternalTableDefinitionTest.FIELD_SCHEMA1, ExternalTableDefinitionTest.FIELD_SCHEMA2, ExternalTableDefinitionTest.FIELD_SCHEMA3);

    private static final Integer MAX_BAD_RECORDS = 42;

    private static final Boolean IGNORE_UNKNOWN_VALUES = true;

    private static final String COMPRESSION = "GZIP";

    private static final Boolean AUTODETECT = true;

    private static final CsvOptions CSV_OPTIONS = CsvOptions.newBuilder().build();

    private static final ExternalTableDefinition EXTERNAL_TABLE_DEFINITION = ExternalTableDefinition.newBuilder(ExternalTableDefinitionTest.SOURCE_URIS, ExternalTableDefinitionTest.TABLE_SCHEMA, ExternalTableDefinitionTest.CSV_OPTIONS).setCompression(ExternalTableDefinitionTest.COMPRESSION).setIgnoreUnknownValues(ExternalTableDefinitionTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(ExternalTableDefinitionTest.MAX_BAD_RECORDS).setAutodetect(ExternalTableDefinitionTest.AUTODETECT).build();

    @Test
    public void testToBuilder() {
        compareExternalTableDefinition(ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION, ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION.toBuilder().build());
        ExternalTableDefinition externalTableDefinition = ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION.toBuilder().setCompression("NONE").build();
        Assert.assertEquals("NONE", externalTableDefinition.getCompression());
        externalTableDefinition = externalTableDefinition.toBuilder().setCompression(ExternalTableDefinitionTest.COMPRESSION).build();
        compareExternalTableDefinition(ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION, externalTableDefinition);
    }

    @Test
    public void testToBuilderIncomplete() {
        ExternalTableDefinition externalTableDefinition = ExternalTableDefinition.of(ExternalTableDefinitionTest.SOURCE_URIS, ExternalTableDefinitionTest.TABLE_SCHEMA, FormatOptions.json());
        Assert.assertEquals(externalTableDefinition, externalTableDefinition.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(EXTERNAL, ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION.getType());
        Assert.assertEquals(ExternalTableDefinitionTest.COMPRESSION, ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION.getCompression());
        Assert.assertEquals(ExternalTableDefinitionTest.CSV_OPTIONS, ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION.getFormatOptions());
        Assert.assertEquals(ExternalTableDefinitionTest.IGNORE_UNKNOWN_VALUES, ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION.ignoreUnknownValues());
        Assert.assertEquals(ExternalTableDefinitionTest.MAX_BAD_RECORDS, ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION.getMaxBadRecords());
        Assert.assertEquals(ExternalTableDefinitionTest.TABLE_SCHEMA, ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION.getSchema());
        Assert.assertEquals(ExternalTableDefinitionTest.SOURCE_URIS, ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION.getSourceUris());
        Assert.assertEquals(ExternalTableDefinitionTest.AUTODETECT, ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION.getAutodetect());
    }

    @Test
    public void testToAndFromPb() {
        compareExternalTableDefinition(ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION, ExternalTableDefinition.fromPb(ExternalTableDefinitionTest.EXTERNAL_TABLE_DEFINITION.toPb()));
        ExternalTableDefinition externalTableDefinition = ExternalTableDefinition.newBuilder(ExternalTableDefinitionTest.SOURCE_URIS, ExternalTableDefinitionTest.TABLE_SCHEMA, ExternalTableDefinitionTest.CSV_OPTIONS).build();
        compareExternalTableDefinition(externalTableDefinition, ExternalTableDefinition.fromPb(externalTableDefinition.toPb()));
    }
}

