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


import io.restassured.module.webtestclient.setup.SecuredProcessor;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


public class SecuredRequestTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void basic_authentication_filter_is_applied() {
        RouterFunction<ServerResponse> routerFunction = RouterFunctions.route(path("/securedGreeting").and(method(HttpMethod.GET)), new SecuredProcessor()::processSecuredRequest);
        RestAssuredWebTestClient.standaloneSetup(routerFunction, basicAuthentication("test", "pass"));
        RestAssuredWebTestClient.given().when().get("/securedGreeting").then().statusCode(200).body("auth", Matchers.equalTo("Basic dGVzdDpwYXNz"));
    }
}

