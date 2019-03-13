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
package io.confluent.ksql.json;


import Schema.INT64_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class StructSerializationModuleTest {
    private final Schema addressSchema = SchemaBuilder.struct().field("NUMBER", OPTIONAL_INT64_SCHEMA).field("STREET", OPTIONAL_STRING_SCHEMA).field("CITY", OPTIONAL_STRING_SCHEMA).field("STATE", OPTIONAL_STRING_SCHEMA).field("ZIPCODE", OPTIONAL_INT64_SCHEMA).optional().build();

    private final Schema categorySchema = SchemaBuilder.struct().field("ID", OPTIONAL_INT64_SCHEMA).field("NAME", OPTIONAL_STRING_SCHEMA).optional().build();

    private final Schema itemInfoSchema = SchemaBuilder.struct().field("ITEMID", INT64_SCHEMA).field("NAME", STRING_SCHEMA).field("CATEGORY", categorySchema).optional().build();

    private ObjectMapper objectMapper;

    @Test
    public void shouldSerializeStructCorrectly() throws JsonProcessingException {
        final Struct address = new Struct(addressSchema);
        address.put("NUMBER", 101L);
        address.put("STREET", "University Ave.");
        address.put("CITY", "Palo Alto");
        address.put("STATE", "CA");
        address.put("ZIPCODE", 94301L);
        final byte[] serializedBytes = objectMapper.writeValueAsBytes(address);
        final String jsonString = new String(serializedBytes, StandardCharsets.UTF_8);
        MatcherAssert.assertThat(jsonString, CoreMatchers.equalTo("{\"NUMBER\":101,\"STREET\":\"University Ave.\",\"CITY\":\"Palo Alto\",\"STATE\":\"CA\",\"ZIPCODE\":94301}"));
    }

    @Test
    public void shouldSerializeStructWithNestedStructCorrectly() throws JsonProcessingException {
        final Struct category = new Struct(categorySchema);
        category.put("ID", 1L);
        category.put("NAME", "Food");
        final Struct item = new Struct(itemInfoSchema);
        item.put("ITEMID", 1L);
        item.put("NAME", "ICE CREAM");
        item.put("CATEGORY", category);
        final byte[] serializedBytes = objectMapper.writeValueAsBytes(item);
        final String jsonString = new String(serializedBytes, StandardCharsets.UTF_8);
        MatcherAssert.assertThat(jsonString, CoreMatchers.equalTo("{\"ITEMID\":1,\"NAME\":\"ICE CREAM\",\"CATEGORY\":{\"ID\":1,\"NAME\":\"Food\"}}"));
    }

    @Test
    public void shouldSerializeStructWithNestedStructAndNullFieldsCorrectly() throws JsonProcessingException {
        final Struct category = new Struct(categorySchema);
        category.put("ID", 1L);
        category.put("NAME", "Food");
        final Struct item = new Struct(itemInfoSchema);
        item.put("ITEMID", 1L);
        item.put("NAME", "ICE CREAM");
        item.put("CATEGORY", null);
        final byte[] serializedBytes = objectMapper.writeValueAsBytes(item);
        final String jsonString = new String(serializedBytes, StandardCharsets.UTF_8);
        MatcherAssert.assertThat(jsonString, CoreMatchers.equalTo("{\"ITEMID\":1,\"NAME\":\"ICE CREAM\",\"CATEGORY\":null}"));
    }

    @Test
    public void shouldSerializeStructInsideListCorrectly() throws JsonProcessingException {
        final Struct category = new Struct(categorySchema);
        category.put("ID", 1L);
        category.put("NAME", "Food");
        final Struct item = new Struct(itemInfoSchema);
        item.put("ITEMID", 1L);
        item.put("NAME", "ICE CREAM");
        item.put("CATEGORY", null);
        final List<Object> list = new ArrayList<>();
        list.add("Hello");
        list.add(1);
        list.add(1L);
        list.add(1.0);
        list.add(item);
        final byte[] serializedBytes = objectMapper.writeValueAsBytes(list);
        final String jsonString = new String(serializedBytes, StandardCharsets.UTF_8);
        MatcherAssert.assertThat(jsonString, CoreMatchers.equalTo("[\"Hello\",1,1,1.0,{\"ITEMID\":1,\"NAME\":\"ICE CREAM\",\"CATEGORY\":null}]"));
    }
}

