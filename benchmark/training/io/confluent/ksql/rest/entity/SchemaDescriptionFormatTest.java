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
package io.confluent.ksql.rest.entity;


import JsonMapper.INSTANCE;
import SchemaInfo.Type;
import SchemaInfo.Type.STRUCT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SchemaDescriptionFormatTest {
    @Test
    public void shouldFormatSchemaEntityCorrectlyForStruct() throws IOException {
        final String descriptionString = "[\n" + ((((((((((((((((((((((((((((((((("  {\n" + "    \"name\": \"l1integer\",\n") + "    \"schema\": {\n") + "      \"type\": \"INTEGER\",\n") + "      \"fields\": null,\n") + "      \"memberSchema\": null\n") + "    }\n") + "  },\n") + "  {\n") + "    \"name\": \"l1struct\",\n") + "    \"schema\": {\n") + "      \"type\": \"STRUCT\",\n") + "      \"fields\": [\n") + "        {\n") + "          \"name\": \"l2string\",\n") + "          \"schema\": {\n") + "            \"type\": \"STRING\",\n") + "            \"fields\": null,\n") + "            \"memberSchema\": null\n") + "          }\n") + "        },\n") + "        {\n") + "          \"name\": \"l2integer\",\n") + "          \"schema\": {\n") + "            \"type\": \"INTEGER\",\n") + "            \"fields\": null,\n") + "            \"memberSchema\": null\n") + "          }\n") + "        }\n") + "      ],\n") + "      \"memberSchema\": null\n") + "    }\n") + "  }\n") + "]");
        final ObjectMapper objectMapper = INSTANCE.mapper;
        final List<FieldInfo> deserialized = objectMapper.readValue(descriptionString, new TypeReference<List<FieldInfo>>() {});
        // Test deserialization
        Assert.assertThat(deserialized.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(deserialized.get(0).getName(), CoreMatchers.equalTo("l1integer"));
        Assert.assertThat(deserialized.get(0).getSchema(), CoreMatchers.equalTo(new SchemaInfo(Type.INTEGER, null, null)));
        Assert.assertThat(deserialized.get(1).getName(), CoreMatchers.equalTo("l1struct"));
        final SchemaInfo structSchema = deserialized.get(1).getSchema();
        Assert.assertThat(structSchema.getType(), CoreMatchers.equalTo(STRUCT));
        Assert.assertThat(structSchema.getMemberSchema(), CoreMatchers.equalTo(Optional.empty()));
        Assert.assertThat(structSchema.getFields().get().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(structSchema.getFields().get().get(0).getName(), CoreMatchers.equalTo("l2string"));
        Assert.assertThat(structSchema.getFields().get().get(0).getSchema(), CoreMatchers.equalTo(new SchemaInfo(Type.STRING, null, null)));
        Assert.assertThat(structSchema.getFields().get().get(1).getName(), CoreMatchers.equalTo("l2integer"));
        Assert.assertThat(structSchema.getFields().get().get(1).getSchema(), CoreMatchers.equalTo(new SchemaInfo(Type.INTEGER, null, null)));
        shouldSerializeCorrectly(descriptionString, deserialized);
    }

    @Test
    public void shouldFormatSchemaEntityCorrectlyForMap() throws IOException {
        final String descriptionString = "[\n" + (((((((((((("  {\n" + "    \"name\": \"mapfield\",\n") + "    \"schema\": {\n") + "      \"type\": \"MAP\",\n") + "      \"memberSchema\": {\n") + "        \"type\": \"STRING\",\n") + "        \"memberSchema\": null,\n") + "        \"fields\": null\n") + "      },\n") + "      \"fields\": null\n") + "    }\n") + "  }\n") + "]");
        final ObjectMapper objectMapper = INSTANCE.mapper;
        final List<FieldInfo> deserialized = objectMapper.readValue(descriptionString, new TypeReference<List<FieldInfo>>() {});
        // Test deserialization
        Assert.assertThat(deserialized.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(deserialized.get(0).getName(), CoreMatchers.equalTo("mapfield"));
        Assert.assertThat(deserialized.get(0).getSchema(), CoreMatchers.equalTo(new SchemaInfo(Type.MAP, null, new SchemaInfo(Type.STRING, null, null))));
        shouldSerializeCorrectly(descriptionString, deserialized);
    }

    @Test
    public void shouldFormatSchemaEntityCorrectlyForArray() throws IOException {
        final String descriptionString = "[\n" + (((((((((((("  {\n" + "    \"name\": \"arrayfield\",\n") + "    \"schema\": {\n") + "      \"type\": \"ARRAY\",\n") + "      \"memberSchema\": {\n") + "        \"type\": \"STRING\",\n") + "        \"memberSchema\": null,\n") + "        \"fields\": null\n") + "      },\n") + "      \"fields\": null\n") + "    }\n") + "  }\n") + "]");
        final ObjectMapper objectMapper = INSTANCE.mapper;
        final List<FieldInfo> deserialized = objectMapper.readValue(descriptionString, new TypeReference<List<FieldInfo>>() {});
        // Test deserialization
        Assert.assertThat(deserialized.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(deserialized.get(0).getName(), CoreMatchers.equalTo("arrayfield"));
        Assert.assertThat(deserialized.get(0).getSchema(), CoreMatchers.equalTo(new SchemaInfo(Type.ARRAY, null, new SchemaInfo(Type.STRING, null, null))));
        shouldSerializeCorrectly(descriptionString, deserialized);
    }
}

