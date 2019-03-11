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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class EntityQueryIDTest {
    final ObjectMapper objectMapper = INSTANCE.mapper;

    @Test
    public void shouldSerializeCorrectly() throws IOException {
        final String id = "query-id";
        final String serialized = String.format("\"%s\"", id);
        final EntityQueryId deserialized = objectMapper.readValue(serialized, EntityQueryId.class);
        Assert.assertThat(deserialized.getId(), CoreMatchers.equalTo(id));
        Assert.assertThat(objectMapper.writeValueAsString(id), CoreMatchers.equalTo(serialized));
    }
}

