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
package io.confluent.ksql.serde.connect;


import Schema.BOOLEAN_SCHEMA;
import Schema.BYTES_SCHEMA;
import Schema.FLOAT64_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import Schema.Type.ARRAY;
import Schema.Type.MAP;
import Schema.Type.STRUCT;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ConnectSchemaTranslatorTest {
    private final ConnectSchemaTranslator schemaTranslator = new ConnectSchemaTranslator();

    @Test
    public void shouldTranslatePrimitives() {
        final Schema connectSchema = SchemaBuilder.struct().field("intField", INT32_SCHEMA).field("longField", INT64_SCHEMA).field("doubleField", FLOAT64_SCHEMA).field("stringField", STRING_SCHEMA).field("booleanField", BOOLEAN_SCHEMA).build();
        final Schema ksqlSchema = schemaTranslator.toKsqlSchema(connectSchema);
        Assert.assertThat(ksqlSchema.schema().type(), CoreMatchers.equalTo(STRUCT));
        Assert.assertThat(ksqlSchema.fields().size(), CoreMatchers.equalTo(connectSchema.fields().size()));
        for (int i = 0; i < (ksqlSchema.fields().size()); i++) {
            Assert.assertThat(ksqlSchema.fields().get(i).name(), CoreMatchers.equalTo(connectSchema.fields().get(i).name().toUpperCase()));
            Assert.assertThat(ksqlSchema.fields().get(i).schema().type(), CoreMatchers.equalTo(connectSchema.fields().get(i).schema().type()));
            Assert.assertThat(ksqlSchema.fields().get(i).schema().isOptional(), CoreMatchers.is(true));
        }
    }

    @Test
    public void shouldTranslateMaps() {
        final Schema connectSchema = SchemaBuilder.struct().field("mapField", SchemaBuilder.map(STRING_SCHEMA, INT32_SCHEMA)).build();
        final Schema ksqlSchema = schemaTranslator.toKsqlSchema(connectSchema);
        Assert.assertThat(ksqlSchema.field("MAPFIELD"), CoreMatchers.notNullValue());
        final Schema mapSchema = ksqlSchema.field("MAPFIELD").schema();
        Assert.assertThat(mapSchema.type(), CoreMatchers.equalTo(MAP));
        Assert.assertThat(mapSchema.keySchema(), CoreMatchers.equalTo(OPTIONAL_STRING_SCHEMA));
        Assert.assertThat(mapSchema.valueSchema(), CoreMatchers.equalTo(OPTIONAL_INT32_SCHEMA));
    }

    @Test
    public void shouldTranslateStructInsideMap() {
        final Schema connectSchema = SchemaBuilder.struct().field("mapField", SchemaBuilder.map(STRING_SCHEMA, SchemaBuilder.struct().field("innerIntField", INT32_SCHEMA).build())).build();
        final Schema ksqlSchema = schemaTranslator.toKsqlSchema(connectSchema);
        Assert.assertThat(ksqlSchema.field("MAPFIELD"), CoreMatchers.notNullValue());
        final Schema mapSchema = ksqlSchema.field("MAPFIELD").schema();
        Assert.assertThat(mapSchema.type(), CoreMatchers.equalTo(MAP));
        Assert.assertThat(mapSchema.isOptional(), CoreMatchers.is(true));
        Assert.assertThat(mapSchema.keySchema(), CoreMatchers.equalTo(OPTIONAL_STRING_SCHEMA));
        Assert.assertThat(mapSchema.valueSchema().type(), CoreMatchers.equalTo(STRUCT));
        Assert.assertThat(mapSchema.valueSchema().fields().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(mapSchema.valueSchema().fields().get(0).name(), CoreMatchers.equalTo("INNERINTFIELD"));
        Assert.assertThat(mapSchema.valueSchema().fields().get(0).schema(), CoreMatchers.equalTo(OPTIONAL_INT32_SCHEMA));
    }

    @Test
    public void shouldTranslateArray() {
        final Schema connectSchema = SchemaBuilder.struct().field("arrayField", SchemaBuilder.array(INT32_SCHEMA)).build();
        final Schema ksqlSchema = schemaTranslator.toKsqlSchema(connectSchema);
        Assert.assertThat(ksqlSchema.field("ARRAYFIELD"), CoreMatchers.notNullValue());
        final Schema arraySchema = ksqlSchema.field("ARRAYFIELD").schema();
        Assert.assertThat(arraySchema.type(), CoreMatchers.equalTo(ARRAY));
        Assert.assertThat(arraySchema.isOptional(), CoreMatchers.is(true));
        Assert.assertThat(arraySchema.valueSchema(), CoreMatchers.equalTo(OPTIONAL_INT32_SCHEMA));
    }

    @Test
    public void shouldTranslateStructInsideArray() {
        final Schema connectSchema = SchemaBuilder.struct().field("arrayField", SchemaBuilder.array(SchemaBuilder.struct().field("innerIntField", OPTIONAL_INT32_SCHEMA).build())).build();
        final Schema ksqlSchema = schemaTranslator.toKsqlSchema(connectSchema);
        Assert.assertThat(ksqlSchema.field("ARRAYFIELD"), CoreMatchers.notNullValue());
        final Schema arraySchema = ksqlSchema.field("ARRAYFIELD").schema();
        Assert.assertThat(arraySchema.type(), CoreMatchers.equalTo(ARRAY));
        Assert.assertThat(arraySchema.valueSchema().type(), CoreMatchers.equalTo(STRUCT));
        Assert.assertThat(arraySchema.valueSchema().fields().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(arraySchema.valueSchema().fields().get(0).name(), CoreMatchers.equalTo("INNERINTFIELD"));
        Assert.assertThat(arraySchema.valueSchema().fields().get(0).schema(), CoreMatchers.equalTo(OPTIONAL_INT32_SCHEMA));
    }

    @Test
    public void shouldTranslateNested() {
        final Schema connectInnerSchema = SchemaBuilder.struct().field("intField", INT32_SCHEMA).build();
        final Schema connectSchema = SchemaBuilder.struct().field("structField", connectInnerSchema).build();
        final Schema ksqlSchema = schemaTranslator.toKsqlSchema(connectSchema);
        Assert.assertThat(ksqlSchema.field("STRUCTFIELD"), CoreMatchers.notNullValue());
        final Schema innerSchema = ksqlSchema.field("STRUCTFIELD").schema();
        Assert.assertThat(innerSchema.fields().size(), CoreMatchers.equalTo(connectInnerSchema.fields().size()));
        for (int i = 0; i < (connectInnerSchema.fields().size()); i++) {
            Assert.assertThat(innerSchema.fields().get(i).name().toUpperCase(), CoreMatchers.equalTo(connectInnerSchema.fields().get(i).name().toUpperCase()));
            Assert.assertThat(innerSchema.fields().get(i).schema().type(), CoreMatchers.equalTo(connectInnerSchema.fields().get(i).schema().type()));
            Assert.assertThat(innerSchema.fields().get(i).schema().isOptional(), CoreMatchers.is(true));
        }
    }

    @Test
    public void shouldTranslateMapWithNonStringKey() {
        final Schema connectSchema = SchemaBuilder.struct().field("mapfield", SchemaBuilder.map(INT32_SCHEMA, INT32_SCHEMA)).build();
        final Schema ksqlSchema = schemaTranslator.toKsqlSchema(connectSchema);
        Assert.assertThat(ksqlSchema.field("MAPFIELD"), CoreMatchers.notNullValue());
        final Schema mapSchema = ksqlSchema.field("MAPFIELD").schema();
        Assert.assertThat(mapSchema.type(), CoreMatchers.equalTo(MAP));
        Assert.assertThat(mapSchema.keySchema(), CoreMatchers.equalTo(OPTIONAL_STRING_SCHEMA));
        Assert.assertThat(mapSchema.valueSchema(), CoreMatchers.equalTo(OPTIONAL_INT32_SCHEMA));
    }

    @Test
    public void shouldIgnoreUnsupportedType() {
        final Schema connectSchema = SchemaBuilder.struct().field("bytesField", BYTES_SCHEMA).build();
        final Schema ksqlSchema = schemaTranslator.toKsqlSchema(connectSchema);
        Assert.assertThat(ksqlSchema.fields().size(), CoreMatchers.equalTo(0));
    }
}

