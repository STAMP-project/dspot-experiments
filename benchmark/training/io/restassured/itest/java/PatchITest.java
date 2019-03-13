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
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class PatchITest extends WithJetty {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void requestSpecificationAllowsSpecifyingCookie() throws Exception {
        RestAssured.given().cookies("username", "John", "token", "1234").then().expect().body(Matchers.equalTo("username, token")).when().patch("/cookie");
    }

    @Test
    public void bodyHamcrestMatcherWithoutKey() throws Exception {
        RestAssured.given().parameters("firstName", "John", "lastName", "Doe").expect().body(Matchers.equalTo("{\"greeting\":\"Greetings John Doe\"}")).when().patch("/greetPatch");
    }

    @Test
    public void patchSupportsBinaryBody() throws Exception {
        final byte[] body = "a body".getBytes("UTF-8");
        RestAssured.given().body(body).expect().body(Matchers.equalTo("97, 32, 98, 111, 100, 121")).when().patch("/binaryBody");
    }

    @Test
    public void patchSupportsStringBody() throws Exception {
        RestAssured.given().body("a body").expect().body(Matchers.equalTo("a body")).when().patch("/body");
    }

    @Test
    public void patchWithFormParams() throws Exception {
        RestAssured.given().formParameters("firstName", "John", "lastName", "Doe").expect().body("greeting", Matchers.equalTo("Greetings John Doe")).when().patch("/greetPatch");
    }

    @Test
    public void patchWithFormParam() throws Exception {
        RestAssured.given().formParam("firstName", "John").formParam("lastName", "Doe").expect().body("greeting", Matchers.equalTo("Greetings John Doe")).when().patch("/greetPatch");
    }

    @Test
    public void patchSupportsMultiValueFormParameters() throws Exception {
        RestAssured.given().formParam("list", "a", "b", "c").expect().body("list", Matchers.equalTo("a,b,c")).when().patch("/multiValueParam");
    }

    @Test
    public void canUseMapAsBodyToPatch() throws Exception {
        Map<String, String> greeting = new HashMap<>();
        greeting.put("firstName", "John");
        greeting.put("lastName", "Doe");
        RestAssured.given().contentType(JSON).body(greeting).when().patch("/jsonGreet").then().statusCode(200).body("fullName", Matchers.equalTo("John Doe"));
    }
}

