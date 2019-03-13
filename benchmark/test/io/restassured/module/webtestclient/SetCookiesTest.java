/**
 * Copyright 2018 the original author or authors.
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


import org.junit.Test;


public class SetCookiesTest {
    @Test
    public void can_receive_cookies() {
        RestAssuredWebTestClient.given().queryParam("name", "John Doe").queryParam("project", "rest assured").when().get("/setCookies").then().statusCode(200).cookie("name", "John Doe").cookie("project", "rest assured");
    }
}

