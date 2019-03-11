/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.path.json;


import io.restassured.common.mapper.ObjectDeserializationContext;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.json.mapping.JsonPathObjectDeserializer;
import io.restassured.path.json.support.Greeting;
import io.restassured.path.json.support.Winner;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import static JsonPath.config;


public class JsonPathObjectDeserializationTest {
    private static final String GREETING = "{ \"greeting\" : { \n" + ((("                \"firstName\" : \"John\", \n" + "                \"lastName\" : \"Doe\" \n") + "               }\n") + "}");

    private static final String LOTTO = "{\"lotto\":{\n" + (((((((((("    \"lottoId\":5,\n" + "    \"winning-numbers\":[2,45,34,23,7,5,3],\n") + "    \"winners\":[{\n") + "      \"winnerId\":23,\n") + "      \"numbers\":[2,45,34,23,3,5]\n") + "    },{\n") + "      \"winnerId\":54,\n") + "      \"numbers\":[52,3,12,11,18,22]\n") + "    }]\n") + "  }\n") + "}");

    @Test
    public void json_path_supports_custom_deserializer() {
        // Given
        final AtomicBoolean customDeserializerUsed = new AtomicBoolean(false);
        final JsonPath jsonPath = new JsonPath(JsonPathObjectDeserializationTest.GREETING).using(new JsonPathConfig().defaultObjectDeserializer(new JsonPathObjectDeserializer() {
            public <T> T deserialize(ObjectDeserializationContext ctx) {
                customDeserializerUsed.set(true);
                final String json = ctx.getDataToDeserialize().asString();
                final Greeting greeting = new Greeting();
                greeting.setFirstName(StringUtils.substringBetween(json, "\"firstName\":\"", "\""));
                greeting.setLastName(StringUtils.substringBetween(json, "\"lastName\":\"", "\""));
                return ((T) (greeting));
            }
        }));
        // When
        final Greeting greeting = jsonPath.getObject("", Greeting.class);
        // Then
        Assert.assertThat(greeting.getFirstName(), equalTo("John"));
        Assert.assertThat(greeting.getLastName(), equalTo("Doe"));
        Assert.assertThat(customDeserializerUsed.get(), is(true));
    }

    @Test
    public void json_path_supports_custom_deserializer_with_static_configuration() {
        // Given
        final AtomicBoolean customDeserializerUsed = new AtomicBoolean(false);
        config = new JsonPathConfig().defaultObjectDeserializer(new JsonPathObjectDeserializer() {
            public <T> T deserialize(ObjectDeserializationContext ctx) {
                customDeserializerUsed.set(true);
                final String json = ctx.getDataToDeserialize().asString();
                final Greeting greeting = new Greeting();
                greeting.setFirstName(StringUtils.substringBetween(json, "\"firstName\":\"", "\""));
                greeting.setLastName(StringUtils.substringBetween(json, "\"lastName\":\"", "\""));
                return ((T) (greeting));
            }
        });
        final JsonPath jsonPath = new JsonPath(JsonPathObjectDeserializationTest.GREETING);
        // When
        try {
            final Greeting greeting = jsonPath.getObject("", Greeting.class);
            // Then
            Assert.assertThat(greeting.getFirstName(), equalTo("John"));
            Assert.assertThat(greeting.getLastName(), equalTo("Doe"));
            Assert.assertThat(customDeserializerUsed.get(), is(true));
        } finally {
            JsonPath.reset();
        }
    }

    @Test
    public void extracting_first_lotto_winner_to_java_object() {
        // When
        final Winner winner = JsonPath.from(JsonPathObjectDeserializationTest.LOTTO).getObject("lotto.winners[0]", Winner.class);
        // Then
        Assert.assertThat(winner.getNumbers(), hasItems(2, 45, 34, 23, 3, 5));
        Assert.assertThat(winner.getWinnerId(), is(23));
    }

    @Test
    public void getting_numbers_greater_than_ten_for_lotto_winner_with_id_equal_to_23() {
        // When
        List<Integer> numbers = JsonPath.from(JsonPathObjectDeserializationTest.LOTTO).getList("lotto.winners.find { it.winnerId == 23 }.numbers.findAll { it > 10 }", Integer.class);
        // Then
        Assert.assertThat(numbers, hasItems(45, 34, 23));
        Assert.assertThat(numbers, hasSize(3));
    }
}

