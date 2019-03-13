/**
 * Copyright 2018 Confluent Inc.
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
package io.confluent.ksql.serde.avro;


import GenericData.Array;
import KsqlConfig.KSQL_USE_NAMED_AVRO_MAPS;
import KsqlConstants.AVRO_SCHEMA_NAMESPACE;
import Schema.OPTIONAL_BOOLEAN_SCHEMA;
import Schema.OPTIONAL_FLOAT64_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.logging.processing.ProcessingLogContext;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlConstants;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.org.apache.avro.SchemaBuilder;
import org.apache.kafka.connect.errors.DataException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class KsqlGenericRowAvroSerializerTest {
    private final SchemaRegistryClient schemaRegistryClient = new MockSchemaRegistryClient();

    private Schema schema = SchemaBuilder.struct().field("ordertime".toUpperCase(), OPTIONAL_INT64_SCHEMA).field("orderid".toUpperCase(), OPTIONAL_INT64_SCHEMA).field("itemid".toUpperCase(), OPTIONAL_STRING_SCHEMA).field("orderunits".toUpperCase(), OPTIONAL_FLOAT64_SCHEMA).field("arraycol".toUpperCase(), SchemaBuilder.array(OPTIONAL_FLOAT64_SCHEMA).optional().build()).field("mapcol".toUpperCase(), SchemaBuilder.map(STRING_SCHEMA, OPTIONAL_FLOAT64_SCHEMA).optional().build()).optional().build();

    private KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());

    private Serializer<GenericRow> serializer;

    private Deserializer<GenericRow> deserializer;

    @Test
    public void shouldSerializeRowCorrectly() {
        final List columns = Arrays.asList(1511897796092L, 1L, "item_1", 10.0, Arrays.asList(100.0), Collections.singletonMap("key1", 100.0));
        final GenericRow genericRow = new GenericRow(columns);
        final byte[] serializedRow = serializer.serialize("t1", genericRow);
        final KafkaAvroDeserializer kafkaAvroDeserializer = new KafkaAvroDeserializer(schemaRegistryClient);
        final GenericRecord genericRecord = ((GenericRecord) (kafkaAvroDeserializer.deserialize("t1", serializedRow)));
        Assert.assertNotNull(genericRecord);
        Assert.assertThat("Incorrect serialization.", genericRecord.get("ordertime".toUpperCase()), CoreMatchers.equalTo(1511897796092L));
        Assert.assertThat("Incorrect serialization.", genericRecord.get("orderid".toUpperCase()), CoreMatchers.equalTo(1L));
        Assert.assertThat("Incorrect serialization.", genericRecord.get("itemid".toUpperCase()).toString(), CoreMatchers.equalTo("item_1"));
        Assert.assertThat("Incorrect serialization.", genericRecord.get("orderunits".toUpperCase()), CoreMatchers.equalTo(10.0));
        final GenericData.Array array = ((GenericData.Array) (genericRecord.get("arraycol".toUpperCase())));
        final Map map = ((Map) (genericRecord.get("mapcol".toUpperCase())));
        Assert.assertThat("Incorrect serialization.", array.size(), CoreMatchers.equalTo(1));
        Assert.assertThat("Incorrect serialization.", array.get(0), CoreMatchers.equalTo(100.0));
        Assert.assertThat("Incorrect serialization.", map.size(), CoreMatchers.equalTo(1));
        Assert.assertThat("Incorrect serialization.", map.get(new Utf8("key1")), CoreMatchers.equalTo(100.0));
    }

    @Test
    public void shouldSerializeRowWithNullCorrectly() {
        final List columns = Arrays.asList(1511897796092L, 1L, null, 10.0, Arrays.asList(100.0), Collections.singletonMap("key1", 100.0));
        final GenericRow genericRow = new GenericRow(columns);
        final byte[] serializedRow = serializer.serialize("t1", genericRow);
        final KafkaAvroDeserializer kafkaAvroDeserializer = new KafkaAvroDeserializer(schemaRegistryClient);
        final GenericRecord genericRecord = ((GenericRecord) (kafkaAvroDeserializer.deserialize("t1", serializedRow)));
        Assert.assertNotNull(genericRecord);
        Assert.assertThat("Incorrect serialization.", genericRecord.get("ordertime".toUpperCase()), CoreMatchers.equalTo(1511897796092L));
        Assert.assertThat("Incorrect serialization.", genericRecord.get("orderid".toUpperCase()), CoreMatchers.equalTo(1L));
        Assert.assertThat("Incorrect serialization.", genericRecord.get("itemid".toUpperCase()), CoreMatchers.equalTo(null));
        Assert.assertThat("Incorrect serialization.", genericRecord.get("orderunits".toUpperCase()), CoreMatchers.equalTo(10.0));
        final GenericData.Array array = ((GenericData.Array) (genericRecord.get("arraycol".toUpperCase())));
        final Map map = ((Map) (genericRecord.get("mapcol".toUpperCase())));
        Assert.assertThat("Incorrect serialization.", array.size(), CoreMatchers.equalTo(1));
        Assert.assertThat("Incorrect serialization.", array.get(0), CoreMatchers.equalTo(100.0));
        Assert.assertThat("Incorrect serialization.", map, CoreMatchers.equalTo(Collections.singletonMap(new Utf8("key1"), 100.0)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSerializeRowWithNullValues() {
        final List columns = Arrays.asList(1511897796092L, 1L, "item_1", 10.0, null, null);
        final GenericRow genericRow = new GenericRow(columns);
        serializer.serialize("t1", genericRow);
    }

    @Test
    public void shouldFailForIncompatibleType() {
        final List columns = Arrays.asList(1511897796092L, 1L, "item_1", "10.0", Arrays.asList(((Double) (100.0))), Collections.singletonMap("key1", 100.0));
        final GenericRow genericRow = new GenericRow(columns);
        try {
            serializer.serialize("t1", genericRow);
            Assert.fail("Did not fail for incompatible types.");
        } catch (final DataException e) {
        }
    }

    @Test
    public void shouldSerializeInteger() {
        shouldSerializeTypeCorrectly(OPTIONAL_INT32_SCHEMA, 123, org.apache.avro.SchemaBuilder.builder().intType());
    }

    @Test
    public void shouldSerializeBigint() {
        shouldSerializeTypeCorrectly(OPTIONAL_INT64_SCHEMA, 123L, org.apache.avro.SchemaBuilder.builder().longType());
    }

    @Test
    public void shouldSerializeBoolean() {
        shouldSerializeTypeCorrectly(OPTIONAL_BOOLEAN_SCHEMA, false, org.apache.avro.SchemaBuilder.builder().booleanType());
        shouldSerializeTypeCorrectly(OPTIONAL_BOOLEAN_SCHEMA, true, org.apache.avro.SchemaBuilder.builder().booleanType());
    }

    @Test
    public void shouldSerializeString() {
        shouldSerializeTypeCorrectly(OPTIONAL_STRING_SCHEMA, "foobar", org.apache.avro.SchemaBuilder.builder().stringType(), new Utf8("foobar"));
    }

    @Test
    public void shouldSerializeDouble() {
        shouldSerializeTypeCorrectly(OPTIONAL_FLOAT64_SCHEMA, 1.23456789012345, org.apache.avro.SchemaBuilder.builder().doubleType());
    }

    @Test
    public void shouldSerializeArray() {
        shouldSerializeTypeCorrectly(SchemaBuilder.array(OPTIONAL_INT32_SCHEMA).optional().build(), ImmutableList.of(1, 2, 3), org.apache.avro.SchemaBuilder.array().items(org.apache.avro.SchemaBuilder.builder().unionOf().nullType().and().intType().endUnion()));
    }

    @Test
    public void shouldSerializeMapWithName() {
        final org.apache.avro.Schema avroSchema = mapSchema(mapEntrySchema(((KsqlConstants.AVRO_SCHEMA_NAMESPACE) + ".KsqlDataSourceSchema_field0")));
        shouldSerializeMap(avroSchema);
    }

    @Test
    public void shouldSerializeMapWithoutNameIfDisabled() {
        ksqlConfig = new KsqlConfig(Collections.singletonMap(KSQL_USE_NAMED_AVRO_MAPS, false));
        final org.apache.avro.Schema avroSchema = mapSchema(legacyMapEntrySchema());
        shouldSerializeMap(avroSchema);
    }

    @Test
    public void shouldSerializeMultipleMaps() {
        final org.apache.avro.Schema avroInnerSchema0 = mapEntrySchema(((KsqlConstants.AVRO_SCHEMA_NAMESPACE) + ".KsqlDataSourceSchema_field0_inner0"));
        final org.apache.avro.Schema avroInnerSchema1 = mapEntrySchema(((KsqlConstants.AVRO_SCHEMA_NAMESPACE) + ".KsqlDataSourceSchema_field0_inner1"), org.apache.avro.SchemaBuilder.unionOf().nullType().and().stringType().endUnion());
        final org.apache.avro.Schema avroSchema = org.apache.avro.SchemaBuilder.record("KsqlDataSourceSchema_field0").namespace("io.confluent.ksql.avro_schemas").fields().name("inner0").type().unionOf().nullType().and().array().items(avroInnerSchema0).endUnion().nullDefault().name("inner1").type().unionOf().nullType().and().array().items(avroInnerSchema1).endUnion().nullDefault().endRecord();
        final Schema ksqlSchema = SchemaBuilder.struct().field("inner0", SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT64_SCHEMA).optional().build()).field("inner1", SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_STRING_SCHEMA).optional().build()).optional().build();
        final Struct value = put("inner0", ImmutableMap.of("foo", 123L)).put("inner1", ImmutableMap.of("bar", "baz"));
        final List<GenericRecord> avroInner0 = Collections.singletonList(set("key", "foo").set("value", 123L).build());
        final List<GenericRecord> avroInner1 = Collections.singletonList(set("value", "baz").build());
        final GenericRecord avroValue = new org.apache.avro.generic.GenericRecordBuilder(avroSchema).set("inner0", avroInner0).set("inner1", avroInner1).build();
        shouldSerializeTypeCorrectly(ksqlSchema, value, avroSchema, avroValue);
    }

    @Test
    public void shouldSerializeStruct() {
        final org.apache.avro.Schema avroSchema = org.apache.avro.SchemaBuilder.record(((KsqlConstants.AVRO_SCHEMA_NAME) + "_field0")).namespace(AVRO_SCHEMA_NAMESPACE).fields().name("field1").type().unionOf().nullType().and().intType().endUnion().nullDefault().name("field2").type().unionOf().nullType().and().stringType().endUnion().nullDefault().endRecord();
        final GenericRecord avroValue = new GenericData.Record(avroSchema);
        avroValue.put("field1", 123);
        avroValue.put("field2", "foobar");
        final Schema ksqlSchema = SchemaBuilder.struct().field("field1", OPTIONAL_INT32_SCHEMA).field("field2", OPTIONAL_STRING_SCHEMA).optional().build();
        final Struct value = new Struct(ksqlSchema);
        value.put("field1", 123);
        value.put("field2", "foobar");
        shouldSerializeTypeCorrectly(ksqlSchema, value, avroSchema, avroValue);
    }

    @Test
    public void shouldRemoveSourceName() {
        final Schema ksqlRecordSchema = SchemaBuilder.struct().field("source.field0", OPTIONAL_INT32_SCHEMA).build();
        final GenericRow ksqlRecord = new GenericRow(ImmutableList.of(123));
        final Serde<GenericRow> serde = new KsqlAvroTopicSerDe(KsqlConstants.DEFAULT_AVRO_SCHEMA_FULL_NAME).getGenericRowSerde(ksqlRecordSchema, new KsqlConfig(Collections.emptyMap()), false, () -> schemaRegistryClient, "loggerName", ProcessingLogContext.create());
        final byte[] bytes = serde.serializer().serialize("topic", ksqlRecord);
        final KafkaAvroDeserializer deserializer = new KafkaAvroDeserializer(schemaRegistryClient);
        final GenericRecord avroRecord = ((GenericRecord) (deserializer.deserialize("topic", bytes)));
        Assert.assertThat(avroRecord.getSchema().getFields().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(avroRecord.get("field0"), CoreMatchers.equalTo(123));
        final GenericRow deserializedKsqlRecord = serde.deserializer().deserialize("topic", bytes);
        Assert.assertThat(deserializedKsqlRecord, CoreMatchers.equalTo(ksqlRecord));
    }

    @Test
    public void shouldTransformSourceNameDelimiterForInternal() {
        final Schema ksqlRecordSchema = SchemaBuilder.struct().field("source.field0", OPTIONAL_INT32_SCHEMA).build();
        final GenericRow ksqlRecord = new GenericRow(ImmutableList.of(123));
        final Serde<GenericRow> serde = new KsqlAvroTopicSerDe(KsqlConstants.DEFAULT_AVRO_SCHEMA_FULL_NAME).getGenericRowSerde(ksqlRecordSchema, new KsqlConfig(Collections.emptyMap()), true, () -> schemaRegistryClient, "loggerName", ProcessingLogContext.create());
        final byte[] bytes = serde.serializer().serialize("topic", ksqlRecord);
        final KafkaAvroDeserializer deserializer = new KafkaAvroDeserializer(schemaRegistryClient);
        final GenericRecord avroRecord = ((GenericRecord) (deserializer.deserialize("topic", bytes)));
        Assert.assertThat(avroRecord.getSchema().getFields().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(avroRecord.getSchema().getFields().get(0).name(), CoreMatchers.equalTo("source_field0"));
        Assert.assertThat(avroRecord.get("source_field0"), CoreMatchers.equalTo(123));
        final GenericRow deserializedKsqlRecord = serde.deserializer().deserialize("topic", bytes);
        Assert.assertThat(deserializedKsqlRecord, CoreMatchers.equalTo(ksqlRecord));
    }

    @Test
    public void shouldUseSchemaNameFromPropertyIfExists() {
        final String schemaName = "TestSchemaName1";
        final String schemaNamespace = "com.test.namespace";
        final Schema ksqlSchema = Schema.OPTIONAL_STRING_SCHEMA;
        final Object ksqlValue = "foobar";
        final Schema ksqlRecordSchema = SchemaBuilder.struct().field("field0", ksqlSchema).build();
        final GenericRow ksqlRecord = new GenericRow(ImmutableList.of(ksqlValue));
        final Serde<GenericRow> serde = new KsqlAvroTopicSerDe(((schemaNamespace + ".") + schemaName)).getGenericRowSerde(ksqlRecordSchema, new KsqlConfig(Collections.emptyMap()), false, () -> schemaRegistryClient, "logger.name.prefix", ProcessingLogContext.create());
        final byte[] bytes = serde.serializer().serialize("topic", ksqlRecord);
        final KafkaAvroDeserializer deserializer = new KafkaAvroDeserializer(schemaRegistryClient);
        final GenericRecord avroRecord = ((GenericRecord) (deserializer.deserialize("topic", bytes)));
        Assert.assertThat(avroRecord.getSchema().getNamespace(), CoreMatchers.equalTo(schemaNamespace));
        Assert.assertThat(avroRecord.getSchema().getName(), CoreMatchers.equalTo(schemaName));
    }
}

