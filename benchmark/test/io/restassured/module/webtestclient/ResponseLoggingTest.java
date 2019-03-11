/**
 * Copyright 2016-2018 the original author or authors.
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
package io.restassured.module.webtestclient;


import io.restassured.module.webtestclient.setup.PostController;
import java.io.StringWriter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ResponseLoggingTest {
    private StringWriter writer;

    @Test
    public void logging_if_response_validation_fails_works() {
        try {
            RestAssuredWebTestClient.given().standaloneSetup(new PostController()).param("name", "Johan").when().post("/greetingPost").then().log().ifValidationFails().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan2!"));
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            Assert.assertThat(writer.toString(), Matchers.equalTo(String.format(("200%n" + ((((((("Content-Type: application/json;charset=UTF-8%n" + "Content-Length: 34%n") + "%n") + "{") + "\n    \"id\": 1,\n") + "    \"content\": \"Hello, Johan!\"") + "\n") + "}%n")))));
        }
    }

    @Test
    public void logging_if_response_validation_fails_doesnt_log_anything_if_validation_succeeds() {
        RestAssuredWebTestClient.given().standaloneSetup(new PostController()).param("name", "Johan").when().post("/greetingPost").then().log().ifValidationFails().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan!"));
        Assert.assertThat(writer.toString(), Matchers.isEmptyString());
    }
}

