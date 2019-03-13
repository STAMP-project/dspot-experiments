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


import io.restassured.path.json.config.JsonParserType;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.json.support.Greeting;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class JsonPathBuiltinObjectDeserializationTest {
    private static final String GREETING = "{ \"greeting\" : { \n" + ((("                \"firstName\" : \"John\", \n" + "                \"lastName\" : \"Doe\" \n") + "               }\n") + "}");

    private static final String GREETINGS = "{ \"greeting\" : [{ \n" + (((((("                \"firstName\" : \"John\", \n" + "                \"lastName\" : \"Doe\" \n") + "                }, { \n") + "                \"firstName\" : \"Tom\", \n") + "                \"lastName\" : \"Smith\" \n") + "               }]\n") + "}");

    private final JsonParserType parserType;

    public JsonPathBuiltinObjectDeserializationTest(JsonParserType parserType) {
        this.parserType = parserType;
    }

    @Test
    public void json_path_supports_buitin_deserializers() {
        final JsonPath jsonPath = new JsonPath(JsonPathBuiltinObjectDeserializationTest.GREETING).using(new JsonPathConfig().defaultParserType(parserType));
        // When
        final Greeting greeting = jsonPath.getObject("greeting", Greeting.class);
        // Then
        Assert.assertThat(greeting.getFirstName(), Matchers.equalTo("John"));
        Assert.assertThat(greeting.getLastName(), Matchers.equalTo("Doe"));
    }

    @Test
    public void json_path_supports_buitin_deserializers_with_arrays() {
        final JsonPath jsonPath = new JsonPath(JsonPathBuiltinObjectDeserializationTest.GREETINGS).using(new JsonPathConfig().defaultParserType(parserType));
        // When
        final Greeting[] greeting = jsonPath.getObject("greeting", Greeting[].class);
        // Then
        Assert.assertThat(greeting.length, Matchers.equalTo(2));
        Assert.assertThat(greeting[0].getFirstName(), Matchers.equalTo("John"));
        Assert.assertThat(greeting[0].getLastName(), Matchers.equalTo("Doe"));
        Assert.assertThat(greeting[1].getFirstName(), Matchers.equalTo("Tom"));
        Assert.assertThat(greeting[1].getLastName(), Matchers.equalTo("Smith"));
    }
}

