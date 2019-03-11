/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mongodb.transforms;


import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import io.debezium.connector.mongodb.transforms.UnwrapFromMongoDbEnvelope.ArrayEncoding;
import java.util.Map;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.junit.Test;


/**
 * Unit test for {@code MongoDataConverter} that verifies array types.
 *
 * @author Jiri Pechanec
 */
public class MongoArrayConverterTest {
    private static final String HETEROGENOUS_ARRAY = "{\n" + ((((("    \"_id\": 1,\n" + "    \"a2\": [\n") + "        11,\n") + "        \"abc\"\n") + "    ]\n") + "}");

    private static final String EMPTY_ARRAY = "{\n" + (("    \"_id\": 1,\n" + "    \"f\": []\n") + "}");

    private static final String HETEROGENOUS_DOCUMENT_IN_ARRAY = "{\n" + ((((((((("    \"_id\": 1,\n" + "    \"a1\": [\n") + "        {\n") + "            \"a\": 1\n") + "        },\n") + "        {\n") + "            \"a\": \"c\"\n") + "        }\n") + "    ],\n") + "}");

    private static final String HOMOGENOUS_ARRAYS = "{\n" + (((((((((((((("    \"_id\": 1,\n" + "    \"a1\": [\n") + "        {\n") + "            \"a\": 1\n") + "        },\n") + "        {\n") + "            \"b\": \"c\"\n") + "        }\n") + "    ],\n") + "    \"a2\": [\n") + "        \"11\",\n") + "        \"abc\"\n") + "    ],\n") + "    \"empty\": []\n") + "}");

    private SchemaBuilder builder;

    @Test(expected = ConnectException.class)
    public void shouldDetectHeterogenousArray() throws Exception {
        final MongoDataConverter converter = new MongoDataConverter(ArrayEncoding.ARRAY);
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.HETEROGENOUS_ARRAY);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
    }

    @Test(expected = ConnectException.class)
    public void shouldDetectHeterogenousDocumentInArray() throws Exception {
        final MongoDataConverter converter = new MongoDataConverter(ArrayEncoding.ARRAY);
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.HETEROGENOUS_DOCUMENT_IN_ARRAY);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
    }

    @Test
    public void shouldCreateSchemaForHomogenousArray() throws Exception {
        final MongoDataConverter converter = new MongoDataConverter(ArrayEncoding.ARRAY);
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.HOMOGENOUS_ARRAYS);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
        final Schema finalSchema = builder.build();
        assertThat(finalSchema).isEqualTo(SchemaBuilder.struct().name("array").field("_id", OPTIONAL_INT32_SCHEMA).field("a1", SchemaBuilder.array(SchemaBuilder.struct().name("array.a1").optional().field("a", OPTIONAL_INT32_SCHEMA).field("b", OPTIONAL_STRING_SCHEMA).build()).optional().build()).field("a2", SchemaBuilder.array(OPTIONAL_STRING_SCHEMA).optional().build()).field("empty", SchemaBuilder.array(OPTIONAL_STRING_SCHEMA).optional().build()).build());
    }

    @Test
    public void shouldCreateStructForHomogenousArray() throws Exception {
        final MongoDataConverter converter = new MongoDataConverter(ArrayEncoding.ARRAY);
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.HOMOGENOUS_ARRAYS);
        final SchemaBuilder builder = SchemaBuilder.struct().name("array");
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
        final Schema finalSchema = builder.build();
        final Struct struct = new Struct(finalSchema);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.convertRecord(entry, finalSchema, struct);
        }
        assertThat(struct.toString()).isEqualTo(("Struct{" + (((((("_id=1," + "a1=[") + "Struct{a=1}, ") + "Struct{b=c}") + "],") + "a2=[11, abc],") + "empty=[]}")));
    }

    @Test
    public void shouldCreateSchemaForEmptyArrayEncodingArray() throws Exception {
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.EMPTY_ARRAY);
        final MongoDataConverter arrayConverter = new MongoDataConverter(ArrayEncoding.ARRAY);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            arrayConverter.addFieldSchema(entry, builder);
        }
        final Schema arraySchema = builder.build();
        assertThat(arraySchema).isEqualTo(SchemaBuilder.struct().name("array").field("_id", OPTIONAL_INT32_SCHEMA).field("f", SchemaBuilder.array(OPTIONAL_STRING_SCHEMA).optional().build()).build());
    }

    @Test
    public void shouldCreateStructForEmptyArrayEncodingArray() throws Exception {
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.EMPTY_ARRAY);
        final MongoDataConverter arrayConverter = new MongoDataConverter(ArrayEncoding.ARRAY);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            arrayConverter.addFieldSchema(entry, builder);
        }
        final Schema arraySchema = builder.build();
        final Struct struct = new Struct(arraySchema);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            arrayConverter.convertRecord(entry, arraySchema, struct);
        }
        assertThat(struct.toString()).isEqualTo(("Struct{" + (("_id=1," + "f=[]") + "}")));
    }

    @Test
    public void shouldCreateSchemaForEmptyArrayEncodingDocument() throws Exception {
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.EMPTY_ARRAY);
        final MongoDataConverter documentConverter = new MongoDataConverter(ArrayEncoding.DOCUMENT);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            documentConverter.addFieldSchema(entry, builder);
        }
        final Schema documentSchema = builder.build();
        assertThat(documentSchema).isEqualTo(SchemaBuilder.struct().name("array").field("_id", OPTIONAL_INT32_SCHEMA).field("f", SchemaBuilder.struct().name("array.f").optional().build()).build());
    }

    @Test
    public void shouldCreateStructForEmptyArrayEncodingDocument() throws Exception {
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.EMPTY_ARRAY);
        final MongoDataConverter documentConverter = new MongoDataConverter(ArrayEncoding.DOCUMENT);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            documentConverter.addFieldSchema(entry, builder);
        }
        final Schema documentSchema = builder.build();
        final Struct struct = new Struct(documentSchema);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            documentConverter.convertRecord(entry, documentSchema, struct);
        }
        assertThat(struct.toString()).isEqualTo(("Struct{" + ("_id=1," + "f=Struct{}}")));
    }

    @Test
    public void shouldCreateSchemaForHeterogenousArray() throws Exception {
        final MongoDataConverter converter = new MongoDataConverter(ArrayEncoding.DOCUMENT);
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.HETEROGENOUS_ARRAY);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
        final Schema finalSchema = builder.build();
        assertThat(finalSchema).isEqualTo(SchemaBuilder.struct().name("array").field("_id", OPTIONAL_INT32_SCHEMA).field("a2", SchemaBuilder.struct().name("array.a2").optional().field("_0", OPTIONAL_INT32_SCHEMA).field("_1", OPTIONAL_STRING_SCHEMA).build()).build());
    }

    @Test
    public void shouldCreateStructForHeterogenousArray() throws Exception {
        final MongoDataConverter converter = new MongoDataConverter(ArrayEncoding.DOCUMENT);
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.HETEROGENOUS_ARRAY);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
        final Schema finalSchema = builder.build();
        final Struct struct = new Struct(finalSchema);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.convertRecord(entry, finalSchema, struct);
        }
        assertThat(struct.toString()).isEqualTo(("Struct{" + ("_id=1," + "a2=Struct{_0=11,_1=abc}}")));
    }

    @Test
    public void shouldCreateSchemaForHeterogenousDocumentInArray() throws Exception {
        final MongoDataConverter converter = new MongoDataConverter(ArrayEncoding.DOCUMENT);
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.HETEROGENOUS_DOCUMENT_IN_ARRAY);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
        final Schema finalSchema = builder.build();
        assertThat(finalSchema).isEqualTo(SchemaBuilder.struct().name("array").field("_id", OPTIONAL_INT32_SCHEMA).field("a1", SchemaBuilder.struct().name("array.a1").optional().field("_0", SchemaBuilder.struct().name("array.a1._0").optional().field("a", OPTIONAL_INT32_SCHEMA).build()).field("_1", SchemaBuilder.struct().name("array.a1._1").optional().field("a", OPTIONAL_STRING_SCHEMA).build()).build()).build());
    }

    @Test
    public void shouldCreateStructForHeterogenousDocumentInArray() throws Exception {
        final MongoDataConverter converter = new MongoDataConverter(ArrayEncoding.DOCUMENT);
        final BsonDocument val = BsonDocument.parse(MongoArrayConverterTest.HETEROGENOUS_DOCUMENT_IN_ARRAY);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
        final Schema finalSchema = builder.build();
        final Struct struct = new Struct(finalSchema);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.convertRecord(entry, finalSchema, struct);
        }
        assertThat(struct.toString()).isEqualTo(("Struct{" + ((((("_id=1," + "a1=Struct{") + "_0=Struct{a=1},") + "_1=Struct{a=c}") + "}") + "}")));
    }
}

