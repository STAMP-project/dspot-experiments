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


import io.restassured.module.webtestclient.response.WebTestClientResponse;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;


public class ExtractTest {
    @Test
    public void can_extract_rest_assured_web_test_client_response() {
        WebTestClientResponse response = RestAssuredWebTestClient.given().standaloneSetup(new io.restassured.module.webtestclient.setup.GreetingController()).param("name", "Johan").when().get("/greeting").then().statusCode(200).body("id", equalTo(1)).extract().response();
        assertThat(((String) (response.path("content")))).isEqualTo("Hello, Johan!");
    }
}

