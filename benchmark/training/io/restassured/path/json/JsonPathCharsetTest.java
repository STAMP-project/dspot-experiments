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


import io.restassured.path.json.config.JsonPathConfig;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JsonPathCharsetTest {
    private static final String GREETING_WITH_STRANGE_CHARS = "{ \"greeting\" : { \n" + ((("                \"firstName\" : \"\u20ac%#\u00e5\u00f6\", \n" + "                \"lastName\" : \"`\u00fc\" \n") + "               }\n") + "}");

    @Test
    public void json_path_supports_deserializing_input_stream_using_with_given_charset() throws UnsupportedEncodingException {
        // Given
        InputStream is = new ByteArrayInputStream(JsonPathCharsetTest.GREETING_WITH_STRANGE_CHARS.getBytes("UTF-8"));
        JsonPath jsonPath = new JsonPath(is).using(new JsonPathConfig("UTF-8"));
        // When
        final String firstName = jsonPath.getString("greeting.firstName");
        final String lastName = jsonPath.getString("greeting.lastName");
        // Then
        Assert.assertThat(firstName, Matchers.equalTo("?%#??"));
        Assert.assertThat(lastName, Matchers.equalTo("`?"));
    }

    @Test
    public void json_path_cannot_correctly_deserialize_input_stream_using_wrong_charset() throws IOException {
        // Given
        InputStream is = new ByteArrayInputStream(JsonPathCharsetTest.GREETING_WITH_STRANGE_CHARS.getBytes("US-ASCII"));
        JsonPath jsonPath = new JsonPath(is).using(new JsonPathConfig("ISO-8859-1"));
        // When
        final String firstName = jsonPath.getString("greeting.firstName");
        final String lastName = jsonPath.getString("greeting.lastName");
        // Then
        Assert.assertThat(firstName, Matchers.containsString("?%#??"));
        Assert.assertThat(lastName, Matchers.containsString("`?"));
    }
}

