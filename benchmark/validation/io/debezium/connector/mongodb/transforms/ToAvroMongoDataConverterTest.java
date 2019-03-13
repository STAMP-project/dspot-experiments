/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mongodb.transforms;


import GenericData.Record;
import io.confluent.connect.avro.AvroData;
import io.debezium.doc.FixFor;
import java.util.Map;
import org.apache.avro.generic.GenericData;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.junit.Test;


/**
 * Unit test for {@code MongoDataConverter} that tests Avro serialization.
 *
 * @author Jiri Pechanec
 */
public class ToAvroMongoDataConverterTest {
    private String record;

    private BsonDocument val;

    private SchemaBuilder builder;

    private AvroData avroData;

    private MongoDataConverter converter;

    @Test
    public void shouldCreateStructWithNestedObject() {
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
        Schema finalSchema = builder.build();
        Struct struct = new Struct(finalSchema);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.convertRecord(entry, finalSchema, struct);
        }
        final GenericData.Record avro = ((GenericData.Record) (avroData.fromConnectData(finalSchema, struct)));
        assertThat(avro.toString()).isEqualTo(("{\"_id\": 1, " + ("\"s1\": {\"s1f1\": \"field1s1\", \"s1f2\": \"field2s1\"}, " + "\"s2\": {\"s2f1\": \"field1s2\", \"s2f2\": {\"in1\": 1}}}")));
    }

    @Test
    @FixFor("DBZ-650")
    public void shouldCreateSchemaWithNestedObject() {
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
        Schema finalSchema = builder.build();
        final org.apache.avro.Schema avroSchema = avroData.fromConnectSchema(finalSchema);
        assertThat(avroSchema.toString()).isEqualTo(("{\"type\":\"record\",\"name\":\"complex\",\"fields\":[" + ((((((((((("{\"name\":\"_id\",\"type\":[\"null\",\"int\"],\"default\":null}," + "{\"name\":\"s1\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"s1\",\"namespace\":\"complex\",\"fields\":[") + "{\"name\":\"s1f1\",\"type\":[\"null\",\"string\"],\"default\":null},") + "{\"name\":\"s1f2\",\"type\":[\"null\",\"string\"],\"default\":null}],") + "\"connect.name\":\"complex.s1\"}],\"default\":null},") + "{\"name\":\"s2\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"s2\",\"namespace\":\"complex\",\"fields\":[") + "{\"name\":\"s2f1\",\"type\":[\"null\",\"string\"],\"default\":null},") + "{\"name\":\"s2f2\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"s2f2\",\"namespace\":\"complex.s2\",\"fields\":[") + "{\"name\":\"in1\",\"type\":[\"null\",\"int\"],\"default\":null}],") + "\"connect.name\":\"complex.s2.s2f2\"}],\"default\":null}],") + "\"connect.name\":\"complex.s2\"}],\"default\":null}],") + "\"connect.name\":\"complex\"}")));
    }
}

