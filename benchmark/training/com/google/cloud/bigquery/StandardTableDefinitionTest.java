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
import TableDefinition.Type.TABLE;
import TimePartitioning.Type.DAY;
import com.google.api.services.bigquery.model.Streamingbuffer;
import com.google.cloud.bigquery.StandardTableDefinition.StreamingBuffer;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;


public class StandardTableDefinitionTest {
    private static final Field FIELD_SCHEMA1 = Field.newBuilder("StringField", STRING).setMode(NULLABLE).setDescription("FieldDescription1").build();

    private static final Field FIELD_SCHEMA2 = Field.newBuilder("IntegerField", INTEGER).setMode(REPEATED).setDescription("FieldDescription2").build();

    private static final Field FIELD_SCHEMA3 = Field.newBuilder("RecordField", RECORD, StandardTableDefinitionTest.FIELD_SCHEMA1, StandardTableDefinitionTest.FIELD_SCHEMA2).setMode(REQUIRED).setDescription("FieldDescription3").build();

    private static final Schema TABLE_SCHEMA = Schema.of(StandardTableDefinitionTest.FIELD_SCHEMA1, StandardTableDefinitionTest.FIELD_SCHEMA2, StandardTableDefinitionTest.FIELD_SCHEMA3);

    private static final Long NUM_BYTES = 42L;

    private static final Long NUM_LONG_TERM_BYTES = 18L;

    private static final Long NUM_ROWS = 43L;

    private static final String LOCATION = "US";

    private static final StreamingBuffer STREAMING_BUFFER = new StreamingBuffer(1L, 2L, 3L);

    private static final TimePartitioning TIME_PARTITIONING = TimePartitioning.of(DAY, 42);

    private static final Clustering CLUSTERING = Clustering.newBuilder().setFields(ImmutableList.of("Foo", "Bar")).build();

    private static final StandardTableDefinition TABLE_DEFINITION = StandardTableDefinition.newBuilder().setLocation(StandardTableDefinitionTest.LOCATION).setNumBytes(StandardTableDefinitionTest.NUM_BYTES).setNumRows(StandardTableDefinitionTest.NUM_ROWS).setNumLongTermBytes(StandardTableDefinitionTest.NUM_LONG_TERM_BYTES).setStreamingBuffer(StandardTableDefinitionTest.STREAMING_BUFFER).setSchema(StandardTableDefinitionTest.TABLE_SCHEMA).setTimePartitioning(StandardTableDefinitionTest.TIME_PARTITIONING).setClustering(StandardTableDefinitionTest.CLUSTERING).build();

    @Test
    public void testToBuilder() {
        compareStandardTableDefinition(StandardTableDefinitionTest.TABLE_DEFINITION, StandardTableDefinitionTest.TABLE_DEFINITION.toBuilder().build());
        StandardTableDefinition tableDefinition = StandardTableDefinitionTest.TABLE_DEFINITION.toBuilder().setLocation("EU").build();
        Assert.assertEquals("EU", tableDefinition.getLocation());
        tableDefinition = tableDefinition.toBuilder().setLocation(StandardTableDefinitionTest.LOCATION).build();
        compareStandardTableDefinition(StandardTableDefinitionTest.TABLE_DEFINITION, tableDefinition);
    }

    @Test
    public void testToBuilderIncomplete() {
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(StandardTableDefinitionTest.TABLE_SCHEMA);
        Assert.assertEquals(tableDefinition, tableDefinition.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(TABLE, StandardTableDefinitionTest.TABLE_DEFINITION.getType());
        Assert.assertEquals(StandardTableDefinitionTest.TABLE_SCHEMA, StandardTableDefinitionTest.TABLE_DEFINITION.getSchema());
        Assert.assertEquals(StandardTableDefinitionTest.LOCATION, StandardTableDefinitionTest.TABLE_DEFINITION.getLocation());
        Assert.assertEquals(StandardTableDefinitionTest.NUM_BYTES, StandardTableDefinitionTest.TABLE_DEFINITION.getNumBytes());
        Assert.assertEquals(StandardTableDefinitionTest.NUM_LONG_TERM_BYTES, StandardTableDefinitionTest.TABLE_DEFINITION.getNumLongTermBytes());
        Assert.assertEquals(StandardTableDefinitionTest.NUM_ROWS, StandardTableDefinitionTest.TABLE_DEFINITION.getNumRows());
        Assert.assertEquals(StandardTableDefinitionTest.STREAMING_BUFFER, StandardTableDefinitionTest.TABLE_DEFINITION.getStreamingBuffer());
        Assert.assertEquals(StandardTableDefinitionTest.TIME_PARTITIONING, StandardTableDefinitionTest.TABLE_DEFINITION.getTimePartitioning());
        Assert.assertEquals(StandardTableDefinitionTest.CLUSTERING, StandardTableDefinitionTest.TABLE_DEFINITION.getClustering());
    }

    @Test
    public void testOf() {
        StandardTableDefinition definition = StandardTableDefinition.of(StandardTableDefinitionTest.TABLE_SCHEMA);
        Assert.assertEquals(TABLE, StandardTableDefinitionTest.TABLE_DEFINITION.getType());
        Assert.assertEquals(StandardTableDefinitionTest.TABLE_SCHEMA, StandardTableDefinitionTest.TABLE_DEFINITION.getSchema());
        Assert.assertNull(definition.getLocation());
        Assert.assertNull(definition.getNumBytes());
        Assert.assertNull(definition.getNumLongTermBytes());
        Assert.assertNull(definition.getNumRows());
        Assert.assertNull(definition.getStreamingBuffer());
        Assert.assertNull(definition.getTimePartitioning());
        Assert.assertNull(definition.getClustering());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertTrue(((TableDefinition.fromPb(StandardTableDefinitionTest.TABLE_DEFINITION.toPb())) instanceof StandardTableDefinition));
        compareStandardTableDefinition(StandardTableDefinitionTest.TABLE_DEFINITION, TableDefinition.<StandardTableDefinition>fromPb(StandardTableDefinitionTest.TABLE_DEFINITION.toPb()));
        StandardTableDefinition definition = StandardTableDefinition.of(StandardTableDefinitionTest.TABLE_SCHEMA);
        Assert.assertTrue(((TableDefinition.fromPb(definition.toPb())) instanceof StandardTableDefinition));
        compareStandardTableDefinition(definition, TableDefinition.<StandardTableDefinition>fromPb(definition.toPb()));
    }

    @Test
    public void testFromPbWithNullEstimatedRowsAndBytes() {
        StandardTableDefinition.fromPb(StandardTableDefinitionTest.TABLE_DEFINITION.toPb().setStreamingBuffer(new Streamingbuffer()));
    }

    @Test
    public void testStreamingBufferWithNullFieldsToPb() {
        new StreamingBuffer(null, null, null).toPb();
    }
}

