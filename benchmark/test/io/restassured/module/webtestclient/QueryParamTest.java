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


import io.restassured.module.webtestclient.setup.GreetingController;
import io.restassured.module.webtestclient.setup.QueryParamsProcessor;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


public class QueryParamTest {
    @Test
    public void param_with_int() {
        RestAssuredWebTestClient.given().standaloneSetup(new GreetingController()).queryParam("name", "John").when().get("/greeting").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, John!"));
    }

    @Test
    public void query_param() {
        RouterFunction<ServerResponse> queryParamsRoute = RouterFunctions.route(path("/queryParam").and(method(HttpMethod.GET)), new QueryParamsProcessor()::processQueryParams);
        RestAssuredWebTestClient.given().standaloneSetup(queryParamsRoute).queryParam("name", "John").queryParam("message", "Good!").when().get("/queryParam").then().log().all().body("name", Matchers.equalTo("Hello, John!")).body("message", Matchers.equalTo("Good!")).body("_link", Matchers.equalTo("/queryParam?name=John&message=Good!"));
    }
}

