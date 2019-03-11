/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mongodb.transforms;


import Schema.OPTIONAL_FLOAT64_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import io.debezium.connector.mongodb.transforms.UnwrapFromMongoDbEnvelope.ArrayEncoding;
import io.debezium.doc.FixFor;
import java.util.Map;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.junit.Test;


/**
 * Unit test for {@code MongoDataConverter}.
 *
 * @author Sairam Polavarapu
 */
public class MongoDataConverterTest {
    private String record;

    private BsonDocument val;

    private SchemaBuilder builder;

    private MongoDataConverter converter;

    @Test
    public void shouldCreateCorrectStructFromInsertJson() {
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
        Schema finalSchema = builder.build();
        Struct struct = new Struct(finalSchema);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.convertRecord(entry, finalSchema, struct);
        }
        assertThat(struct.toString()).isEqualTo(("Struct{" + ((((((((((((((((((((("address=Struct{" + "building=1007,") + "floor=Struct{") + "level=17,") + "description=level 17") + "},") + "coord=[-73.856077, 40.848447],") + "street=Morris Park Ave,") + "zipcode=10462") + "},") + "borough=Bronx,") + "cuisine=Bakery,") + "grades=[") + "Struct{date=1393804800000,grade=A,score=2}, ") + "Struct{date=1378857600000,grade=A,score=6}, ") + "Struct{date=1358985600000,grade=A,score=10}, ") + "Struct{date=1322006400000,grade=A,score=9}, ") + "Struct{date=1299715200000,grade=B,score=14}") + "],") + "name=Morris Park Bake Shop,") + "restaurant_id=30075445") + "}")));
    }

    @Test
    public void shouldCreateCorrectSchemaFromInsertJson() {
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
        Schema finalSchema = builder.build();
        assertThat(finalSchema).isEqualTo(SchemaBuilder.struct().name("pub").field("address", SchemaBuilder.struct().name("pub.address").optional().field("building", OPTIONAL_STRING_SCHEMA).field("floor", SchemaBuilder.struct().name("pub.address.floor").optional().field("level", OPTIONAL_INT32_SCHEMA).field("description", OPTIONAL_STRING_SCHEMA).build()).field("coord", SchemaBuilder.array(OPTIONAL_FLOAT64_SCHEMA).optional().build()).field("street", OPTIONAL_STRING_SCHEMA).field("zipcode", OPTIONAL_STRING_SCHEMA).build()).field("borough", OPTIONAL_STRING_SCHEMA).field("cuisine", OPTIONAL_STRING_SCHEMA).field("grades", SchemaBuilder.array(SchemaBuilder.struct().name("pub.grades").optional().field("date", OPTIONAL_INT64_SCHEMA).field("grade", OPTIONAL_STRING_SCHEMA).field("score", OPTIONAL_INT32_SCHEMA).build()).optional().build()).field("name", OPTIONAL_STRING_SCHEMA).field("restaurant_id", OPTIONAL_STRING_SCHEMA).build());
    }

    @Test
    @FixFor("DBZ-928")
    public void shouldProcessNullValue() {
        val = BsonDocument.parse(("{\n" + ((((("    \"_id\" : ObjectId(\"51e5619ee4b01f9fbdfba9fc\"),\n" + "    \"delivery\" : {\n") + "        \"hour\" : null,\n") + "        \"hourId\" : 10\n") + "    }\n") + "}")));
        builder = SchemaBuilder.struct().name("withnull");
        converter = new MongoDataConverter(ArrayEncoding.ARRAY);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.addFieldSchema(entry, builder);
        }
        Schema finalSchema = builder.build();
        Struct struct = new Struct(finalSchema);
        for (Map.Entry<String, BsonValue> entry : val.entrySet()) {
            converter.convertRecord(entry, finalSchema, struct);
        }
        assertThat(finalSchema).isEqualTo(SchemaBuilder.struct().name("withnull").field("_id", OPTIONAL_STRING_SCHEMA).field("delivery", SchemaBuilder.struct().name("withnull.delivery").optional().field("hour", OPTIONAL_STRING_SCHEMA).field("hourId", OPTIONAL_INT32_SCHEMA).build()).build());
        assertThat(struct.toString()).isEqualTo(("Struct{" + (((("_id=51e5619ee4b01f9fbdfba9fc," + "delivery=Struct{") + "hourId=10") + "}") + "}")));
    }
}

