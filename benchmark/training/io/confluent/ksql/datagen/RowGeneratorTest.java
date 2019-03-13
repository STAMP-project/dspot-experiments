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
package io.confluent.ksql.datagen;


import Schema.OPTIONAL_FLOAT64_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Type.INT64;
import Type.STRING;
import io.confluent.avro.random.generator.Generator;
import io.confluent.connect.avro.AvroData;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.util.Pair;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class RowGeneratorTest {
    @Test
    public void shouldGenerateCorrectRow() throws IOException {
        final Generator generator = new Generator(new File("./src/main/resources/orders_schema.avro"), new Random());
        final Schema addressSchema = SchemaBuilder.struct().field("city", OPTIONAL_STRING_SCHEMA).field("state", OPTIONAL_STRING_SCHEMA).field("zipcode", OPTIONAL_INT64_SCHEMA).optional().build();
        final Schema ordersSchema = SchemaBuilder.struct().field("ordertime", OPTIONAL_INT64_SCHEMA).field("orderid", OPTIONAL_INT32_SCHEMA).field("itemid", OPTIONAL_STRING_SCHEMA).field("orderunits", OPTIONAL_FLOAT64_SCHEMA).field("address", addressSchema).optional().build();
        final RowGenerator rowGenerator = new RowGenerator(generator, new AvroData(1), generator.schema(), ordersSchema, new SessionManager(), "orderid");
        final Pair<String, GenericRow> rowPair = rowGenerator.generateRow();
        MatcherAssert.assertThat(rowPair.getLeft(), CoreMatchers.instanceOf(String.class));
        MatcherAssert.assertThat(rowPair.getRight().getColumns().size(), CoreMatchers.equalTo(5));
        MatcherAssert.assertThat(rowPair.getRight().getColumns().get(4), CoreMatchers.instanceOf(Struct.class));
        final Struct struct = ((Struct) (rowPair.getRight().getColumns().get(4)));
        MatcherAssert.assertThat(struct.schema().fields().size(), CoreMatchers.equalTo(3));
        MatcherAssert.assertThat(struct.schema().field("city").schema().type(), CoreMatchers.equalTo(STRING));
        MatcherAssert.assertThat(struct.schema().field("state").schema().type(), CoreMatchers.equalTo(STRING));
        MatcherAssert.assertThat(struct.schema().field("zipcode").schema().type(), CoreMatchers.equalTo(INT64));
    }
}

