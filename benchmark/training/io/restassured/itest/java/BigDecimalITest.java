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


import JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import io.restassured.RestAssured;
import io.restassured.itest.java.support.WithJetty;
import java.math.BigDecimal;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;


public class BigDecimalITest extends WithJetty {
    @Test
    public void big_decimal_works_when_configured_in_given_clause() {
        RestAssured.given().config(jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).when().get("/amount").then().body("amount", Matchers.equalTo(new BigDecimal("250.00")));
    }

    @Test
    public void big_decimal_works_when_configured_statically() {
        RestAssured.config = jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.when().get("/amount").then().body("amount", Matchers.equalTo(new BigDecimal("250.00")));
    }

    @Test
    public void floats_are_used_as_big_decimal_in_anonymous_list_with_numbers_when_configured_accordingly() {
        RestAssured.config = jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.when().get("/anonymous_list_with_numbers").then().statusCode(HttpStatus.SC_OK).content("$", Matchers.hasItems(100, 50, BigDecimal.valueOf(31.0)));
    }
}

