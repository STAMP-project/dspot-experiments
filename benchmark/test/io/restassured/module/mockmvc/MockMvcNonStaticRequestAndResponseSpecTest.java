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
package io.restassured.module.mockmvc;


import ContentType.JSON;
import io.restassured.module.mockmvc.http.GreetingController;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecBuilder;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MockMvcNonStaticRequestAndResponseSpecTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void request_and_response_spec_can_be_defined_statically() {
        RestAssuredMockMvc.given().spec(new MockMvcRequestSpecBuilder().setContentType(JSON).addHeader("accept", JSON.toString()).build()).standaloneSetup(new GreetingController()).formParam("name", "Johan").when().post("/greeting").then().statusCode(200).body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan!"));
    }
}

