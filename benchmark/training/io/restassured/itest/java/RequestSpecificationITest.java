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
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class RequestSpecificationITest {
    @Test
    public void filterable_request_specification_returns_correct_port() {
        try {
            RestAssured.baseURI = "http://localhost:8089";
            RequestSpecification spec = new RequestSpecBuilder().addHeader("authorization", "abracadabra").build();
            RestAssured.given().spec(spec).filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    assertThat(requestSpec.getPort(), is(8089));
                    return new io.restassured.builder.ResponseBuilder().setStatusCode(200).build();
                }
            }).when().get("/test");
        } finally {
            RestAssured.reset();
        }
    }
}

