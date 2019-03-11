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


import io.restassured.itest.java.support.WithJetty;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.get;


public class GivenWhenThenExtractITest extends WithJetty {
    @Test
    public void extract_response_as_string_works() {
        String body = get("/hello").then().assertThat().contentType(JSON).and().extract().body().asString();
        Assert.assertThat(body, equalTo("{\"hello\":\"Hello Scalatra\"}"));
    }

    @Test
    public void extract_single_path_works() {
        String hello = get("/hello").then().assertThat().contentType(JSON).and().extract().path("hello");
        Assert.assertThat(hello, equalTo("Hello Scalatra"));
    }

    @Test
    public void extract_entire_response_works() {
        Response response = get("/hello").then().assertThat().contentType(JSON).and().extract().response();
        Assert.assertThat(response.getHeaders().hasHeaderWithName("content-type"), is(true));
    }

    @Test
    public void extract_single_path_works_after_body_validation() {
        String hello = get("/hello").then().assertThat().contentType(JSON).and().body("hello", equalTo("Hello Scalatra")).extract().path("hello");
        Assert.assertThat(hello, equalTo("Hello Scalatra"));
    }

    @Test
    public void extract_single_path_works_after_status_code_and_body_validation() {
        String hello = get("/hello").then().assertThat().statusCode(200).and().body("hello", equalTo("Hello Scalatra")).extract().path("hello");
        Assert.assertThat(hello, equalTo("Hello Scalatra"));
    }

    @Test
    public void extract_single_path_works_after_multiple_body_validations() {
        int lottoId = get("/lotto").then().assertThat().statusCode(200).and().body("lotto.lottoId", equalTo(5)).and().body("lotto.winning-numbers", hasItems(2, 45, 34, 23, 7, 5, 3)).extract().path("lotto.lottoId");
        Assert.assertThat(lottoId, is(5));
    }

    @Test
    public void extract_using_type_ref() {
        List<Map<String, Object>> products = get("/products").as(new io.restassured.common.mapper.TypeRef<List<Map<String, Object>>>() {});
        Assert.assertThat(products, hasSize(2));
        Assert.assertThat(products.get(0).get("id"), equalTo(2));
        Assert.assertThat(products.get(0).get("name"), equalTo("An ice sculpture"));
        Assert.assertThat(products.get(0).get("price"), equalTo(12.5));
        Assert.assertThat(products.get(1).get("id"), equalTo(3));
        Assert.assertThat(products.get(1).get("name"), equalTo("A blue mouse"));
        Assert.assertThat(products.get(1).get("price"), equalTo(25.5));
    }

    @Test
    public void extract_using_type_ref2() {
        List<Map<String, Float>> products = get("/products").then().extract().path("dimensions");
        Assert.assertThat(products, hasSize(2));
        Assert.assertThat(products.get(0).get("length"), equalTo(7.0F));
        Assert.assertThat(products.get(0).get("width"), equalTo(12.0F));
        Assert.assertThat(products.get(0).get("height"), equalTo(9.5F));
        Assert.assertThat(products.get(1).get("length"), equalTo(3.1F));
        Assert.assertThat(products.get(1).get("width"), equalTo(1.0F));
        Assert.assertThat(products.get(1).get("height"), equalTo(1.0F));
    }
}

