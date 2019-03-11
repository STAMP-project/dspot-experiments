/**
 * Copyright 2019 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.schema.inference;


import Schema.BOOLEAN_SCHEMA;
import Schema.BYTES_SCHEMA;
import Schema.INT16_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.INT8_SCHEMA;
import Schema.OPTIONAL_BOOLEAN_SCHEMA;
import Schema.OPTIONAL_FLOAT64_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import io.confluent.connect.avro.AvroData;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.ksql.metastore.MetaStore;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.org.apache.avro.SchemaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultSchemaInjectorFunctionalTest {
    @Mock
    private SchemaRegistryClient srClient;

    @Mock
    private MetaStore metaStore;

    private DefaultSchemaInjector schemaInjector;

    @Test
    public void shouldInferIntAsInteger() {
        shouldInferType(org.apache.avro.SchemaBuilder.builder().intType(), OPTIONAL_INT32_SCHEMA);
    }

    @Test
    public void shouldInferLongAsLong() {
        shouldInferType(org.apache.avro.SchemaBuilder.builder().longType(), OPTIONAL_INT64_SCHEMA);
    }

    @Test
    public void shouldInferBooleanAsBoolean() {
        shouldInferType(org.apache.avro.SchemaBuilder.builder().booleanType(), OPTIONAL_BOOLEAN_SCHEMA);
    }

    @Test
    public void shouldInferStringAsString() {
        shouldInferType(org.apache.avro.SchemaBuilder.builder().stringType(), OPTIONAL_STRING_SCHEMA);
    }

    @Test
    public void shouldInferFloatAsDouble() {
        shouldInferType(org.apache.avro.SchemaBuilder.builder().floatType(), OPTIONAL_FLOAT64_SCHEMA);
    }

    @Test
    public void shouldInferDoubleAsDouble() {
        shouldInferType(org.apache.avro.SchemaBuilder.builder().doubleType(), OPTIONAL_FLOAT64_SCHEMA);
    }

    @Test
    public void shouldInferArrayAsArray() {
        shouldInferType(org.apache.avro.SchemaBuilder.builder().array().items(org.apache.avro.SchemaBuilder.builder().longType()), SchemaBuilder.array(OPTIONAL_INT64_SCHEMA).optional().build());
    }

    @Test
    public void shouldInferMapAsMap() {
        shouldInferType(org.apache.avro.SchemaBuilder.builder().map().values(org.apache.avro.SchemaBuilder.builder().intType()), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build());
    }

    @Test
    public void shouldInferRecordAsAStruct() {
        shouldInferType(org.apache.avro.SchemaBuilder.record("inner_record").fields().name("inner1").type().intType().noDefault().name("inner2").type().stringType().noDefault().endRecord(), SchemaBuilder.struct().field("INNER1", OPTIONAL_INT32_SCHEMA).field("INNER2", OPTIONAL_STRING_SCHEMA).optional().build());
    }

    @Test
    public void shouldInferRecordWithOptionalField() {
        shouldInferType(org.apache.avro.SchemaBuilder.record("inner_record").fields().name("inner1").type().optional().intType().name("inner2").type().optional().stringType().endRecord(), SchemaBuilder.struct().field("INNER1", OPTIONAL_INT32_SCHEMA).field("INNER2", OPTIONAL_STRING_SCHEMA).optional().build());
    }

    @Test
    public void shouldInferOptionalField() {
        shouldInferType(org.apache.avro.SchemaBuilder.unionOf().nullType().and().intType().endUnion(), OPTIONAL_INT32_SCHEMA);
    }

    @Test
    public void shouldInferEnumAsString() {
        shouldInferType(org.apache.avro.SchemaBuilder.enumeration("foo").symbols("A", "B", "C"), OPTIONAL_STRING_SCHEMA);
    }

    @Test
    public void shouldInferDateAsIntegeer() {
        shouldInferType(org.apache.avro.LogicalTypes.date().addToSchema(org.apache.avro.SchemaBuilder.builder().intType()), OPTIONAL_INT32_SCHEMA);
    }

    @Test
    public void shouldInferTimeMillisAsInteger() {
        shouldInferType(org.apache.avro.LogicalTypes.timeMillis().addToSchema(org.apache.avro.SchemaBuilder.builder().intType()), OPTIONAL_INT32_SCHEMA);
    }

    @Test
    public void shouldInferTimeMicrosAsBigint() {
        shouldInferType(org.apache.avro.LogicalTypes.timeMicros().addToSchema(org.apache.avro.SchemaBuilder.builder().longType()), OPTIONAL_INT64_SCHEMA);
    }

    @Test
    public void shouldInferTimestampMillisAsBigint() {
        shouldInferType(org.apache.avro.LogicalTypes.timestampMillis().addToSchema(org.apache.avro.SchemaBuilder.builder().longType()), OPTIONAL_INT64_SCHEMA);
    }

    @Test
    public void shouldInferTimestampMicrosAsBigint() {
        shouldInferType(org.apache.avro.LogicalTypes.timestampMicros().addToSchema(org.apache.avro.SchemaBuilder.builder().longType()), OPTIONAL_INT64_SCHEMA);
    }

    @Test
    public void shouldInferUnionAsStruct() {
        shouldInferType(org.apache.avro.SchemaBuilder.unionOf().intType().and().stringType().endUnion(), SchemaBuilder.struct().field("INT", OPTIONAL_INT32_SCHEMA).field("STRING", OPTIONAL_STRING_SCHEMA).optional().build());
    }

    @Test
    public void shouldIgnoreFixed() {
        shouldInferType(org.apache.avro.SchemaBuilder.fixed("fixed_field").size(32), null);
    }

    @Test
    public void shouldIgnoreBytes() {
        shouldInferType(org.apache.avro.SchemaBuilder.builder().bytesType(), null);
    }

    @Test
    public void shouldInferInt8AsInteger() {
        shouldInferConnectType(INT8_SCHEMA, OPTIONAL_INT32_SCHEMA);
    }

    @Test
    public void shouldInferInt16AsInteger() {
        shouldInferConnectType(INT16_SCHEMA, OPTIONAL_INT32_SCHEMA);
    }

    @Test
    public void shouldInferOptional() {
        shouldInferConnectType(OPTIONAL_INT32_SCHEMA, OPTIONAL_INT32_SCHEMA);
    }

    @Test
    public void shouldInferConnectMapWithOptionalStringKey() {
        shouldInferConnectType(SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build(), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build());
    }

    @Test
    public void shouldInferConnectMapWithInt8Key() {
        shouldInferConnectType(SchemaBuilder.map(INT8_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build(), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build());
    }

    @Test
    public void shouldInferConnectMapWithInt16Key() {
        shouldInferConnectType(SchemaBuilder.map(INT16_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build(), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build());
    }

    @Test
    public void shouldInferConnectMapWithInt32Key() {
        shouldInferConnectType(SchemaBuilder.map(INT32_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build(), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build());
    }

    @Test
    public void shouldInferConnectMapWithInt64Key() {
        shouldInferConnectType(SchemaBuilder.map(INT64_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build(), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build());
    }

    @Test
    public void shouldInferConnectMapWithBooleanKey() {
        shouldInferConnectType(SchemaBuilder.map(BOOLEAN_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build(), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build());
    }

    @Test
    public void shouldIgnoreConnectMapWithUnsupportedKey() {
        shouldInferConnectType(SchemaBuilder.map(BYTES_SCHEMA, OPTIONAL_INT64_SCHEMA), null);
    }

    @Test
    public void shouldInferComplexConnectSchema() {
        final Schema arrayInner = SchemaBuilder.struct().field("arrayInner1", OPTIONAL_STRING_SCHEMA).field("arrayInner2", INT64_SCHEMA).name("arrayInner").build();
        final Schema mapInner = SchemaBuilder.struct().field("mapInner1", OPTIONAL_FLOAT64_SCHEMA).field("mapInner2", STRING_SCHEMA).name("mapInner").build();
        final Schema structInner2 = SchemaBuilder.struct().field("structInner2_1", BOOLEAN_SCHEMA).field("structInner2_2", OPTIONAL_INT32_SCHEMA).name("structInner2").optional().build();
        final Schema structInner1 = SchemaBuilder.struct().field("structInner1_1", STRING_SCHEMA).field("structInner1_2", structInner2).name("structInner1").build();
        final Schema connectSchema = SchemaBuilder.struct().field("primitive", INT32_SCHEMA).field("array", arrayInner).field("map", mapInner).field("struct", structInner1).build();
        final Schema ksqlArrayInner = SchemaBuilder.struct().field("ARRAYINNER1", OPTIONAL_STRING_SCHEMA).field("ARRAYINNER2", OPTIONAL_INT64_SCHEMA).optional().build();
        final Schema ksqlMapInner = SchemaBuilder.struct().field("MAPINNER1", OPTIONAL_FLOAT64_SCHEMA).field("MAPINNER2", OPTIONAL_STRING_SCHEMA).optional().build();
        final Schema ksqlStructInner2 = SchemaBuilder.struct().field("STRUCTINNER2_1", OPTIONAL_BOOLEAN_SCHEMA).field("STRUCTINNER2_2", OPTIONAL_INT32_SCHEMA).optional().build();
        final Schema ksqlStructInner1 = SchemaBuilder.struct().field("STRUCTINNER1_1", OPTIONAL_STRING_SCHEMA).field("STRUCTINNER1_2", ksqlStructInner2).optional().build();
        final Schema ksqlSchema = SchemaBuilder.struct().field("PRIMITIVE", OPTIONAL_INT32_SCHEMA).field("ARRAY", ksqlArrayInner).field("MAP", ksqlMapInner).field("STRUCT", ksqlStructInner1).build();
        shouldInferSchema(new AvroData(1).fromConnectSchema(connectSchema), ksqlSchema);
    }
}

