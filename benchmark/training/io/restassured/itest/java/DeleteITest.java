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
package io.restassured.itest.java;


import io.restassured.RestAssured;
import io.restassured.itest.java.support.WithJetty;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DeleteITest extends WithJetty {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void requestSpecificationAllowsSpecifyingCookie() throws Exception {
        RestAssured.given().cookies("username", "John", "token", "1234").then().expect().body(Matchers.equalTo("username, token")).when().delete("/cookie");
    }

    @Test
    public void bodyHamcrestMatcherWithoutKey() throws Exception {
        RestAssured.given().parameters("firstName", "John", "lastName", "Doe").expect().body(Matchers.equalTo("{\"greeting\":\"Greetings John Doe\"}")).when().delete("/greet");
    }

    @Test
    public void deleteSupportsStringBody() throws Exception {
        RestAssured.given().body("a body").expect().body(Matchers.equalTo("a body")).when().delete("/body");
    }
}

