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


import ContentType.JSON;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import org.apache.commons.lang3.mutable.MutableObject;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AcceptHeaderITest extends WithJetty {
    @Test
    public void accept_method_with_string_parameter_is_just_an_alias_for_header_accept() {
        RestAssured.given().accept("application/json").body("{ \"message\" : \"hello world\"}").when().post("/jsonBodyAcceptHeader").then().body(Matchers.equalTo("hello world"));
    }

    @Test
    public void accept_method_with_content_type_parameter_is_just_an_alias_for_header_accept() {
        RestAssured.given().accept(JSON).body("{ \"message\" : \"hello world\"}").when().post("/jsonBodyAcceptHeader").then().body(Matchers.equalTo("hello world"));
    }

    @Test
    public void accept_method_from_spec_is_set_to_request_when_specified_as_content_type() {
        RequestSpecification spec = new RequestSpecBuilder().setAccept(JSON).build();
        RestAssured.given().spec(spec).body("{ \"message\" : \"hello world\"}").when().post("/jsonBodyAcceptHeader").then().body(Matchers.equalTo("hello world"));
    }

    @Test
    public void accept_method_from_spec_is_set_to_request_when_specified_as_string() {
        RequestSpecification spec = new RequestSpecBuilder().setAccept("application/json").build();
        RestAssured.given().spec(spec).body("{ \"message\" : \"hello world\"}").when().post("/jsonBodyAcceptHeader").then().body(Matchers.equalTo("hello world"));
    }

    @Test
    public void accept_headers_are_overwritten_from_request_spec_by_default() {
        RequestSpecification spec = new RequestSpecBuilder().setAccept(JSON).build();
        final MutableObject<List<String>> headers = new MutableObject<List<String>>();
        RestAssured.given().accept("text/jux").spec(spec).body("{ \"message\" : \"hello world\"}").filter(new Filter() {
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                headers.setValue(requestSpec.getHeaders().getValues("Accept"));
                return ctx.next(requestSpec, responseSpec);
            }
        }).when().post("/jsonBodyAcceptHeader").then().body(Matchers.equalTo("hello world"));
        Assert.assertThat(headers.getValue(), Matchers.contains("application/json, application/javascript, text/javascript, text/json"));
    }

    @Test
    public void accept_headers_are_merged_from_request_spec_and_request_when_configured_to() {
        RequestSpecification spec = new RequestSpecBuilder().setAccept("text/jux").build();
        final MutableObject<List<String>> headers = new MutableObject<List<String>>();
        RestAssured.given().config(io.restassured.config.RestAssuredConfig.config().headerConfig(io.restassured.config.HeaderConfig.headerConfig().mergeHeadersWithName("Accept"))).accept(JSON).spec(spec).body("{ \"message\" : \"hello world\"}").filter(new Filter() {
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                headers.setValue(requestSpec.getHeaders().getValues("Accept"));
                return ctx.next(requestSpec, responseSpec);
            }
        }).when().post("/jsonBodyAcceptHeader").then().body(Matchers.equalTo("hello world"));
        Assert.assertThat(headers.getValue(), Matchers.contains("application/json, application/javascript, text/javascript, text/json", "text/jux"));
    }
}

