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
import io.restassured.filter.Filter;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;


public class OAuthITest {
    @Test
    public void oauth1_url_encoded() {
        RestAssured.given().auth().oauth("key", "secret", "accesskey", "accesssecret").formParam("works", "true").when().post("http://term.ie/oauth/example/echo_api.php").then().body("html.body", Matchers.equalTo("works=true"));
    }

    @Test
    public void oauth2_works_with_preemptive_header_signing() {
        final String accessToken = "accessToken";
        RestAssured.given().auth().preemptive().oauth2(accessToken).filter(new Filter() {
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                assertThat(requestSpec.getHeaders().getValue("Authorization"), Matchers.equalTo(("Bearer " + accessToken)));
                return new io.restassured.builder.ResponseBuilder().setBody("ok").setStatusCode(200).build();
            }
        }).when().get("/somewhere").then().statusCode(200);
    }

    @Test
    public void oauth2_works_with_non_preemptive_header_signing() {
        final String accessToken = "accessToken";
        RestAssured.given().auth().oauth2(accessToken).filter(new Filter() {
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                io.restassured.authentication.AuthenticationScheme scheme = requestSpec.getAuthenticationScheme();
                assertThat(scheme, Matchers.instanceOf(io.restassured.authentication.PreemptiveOAuth2HeaderScheme.class));
                assertThat(getAccessToken(), Matchers.equalTo(accessToken));
                return new io.restassured.builder.ResponseBuilder().setBody("ok").setStatusCode(200).build();
            }
        }).when().get("/somewhere").then().statusCode(200);
    }
}

