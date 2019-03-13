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
import org.apache.http.HttpStatus;
import org.junit.Test;


public class DoubleITest extends WithJetty {
    @Test
    public void double_works() {
        RestAssured.when().get("/amount").then().body("amount", equalTo(250.0));
    }

    @Test
    public void floats_are_used_as_doubles_in_anonymous_list_with_numbers_when_configured_accordingly() {
        RestAssured.when().get("/anonymous_list_with_numbers").then().statusCode(HttpStatus.SC_OK).content("$", hasItems(100, 50, 31.0));
    }

    @Test
    public void can_use_the_close_to_hamcrest_matcher_when_number_return_type_is_double() {
        RestAssured.when().get("/amount").then().body("amount", closeTo(250.0, 0.001));
    }
}

