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
import io.restassured.matcher.ResponseAwareMatcherComposer;
import io.restassured.matcher.RestAssuredMatchers;
import org.junit.Test;


public class ResponseAwareMatcherITest extends WithJetty {
    @Test
    public void can_use_response_aware_matcher_to_construct_hamcrest_matcher_with_data_from_response() {
        RestAssured.when().get("/game").then().statusCode(200).body("_links.self.href", ( response) -> equalTo(("http://localhost:8080/" + (response.path("id"))))).body("status", equalTo("ongoing"));
    }

    @Test
    public void can_use_response_aware_matcher_to_construct_hamcrest_matcher_with_data_from_response_with_predefined_matcher() {
        RestAssured.when().get("/game").then().statusCode(200).body("_links.self.href", RestAssuredMatchers.endsWithPath("id")).body("status", equalTo("ongoing"));
    }

    @Test
    public void can_use_response_aware_matcher_to_construct_hamcrest_matcher_with_data_from_response_with_root_path() {
        RestAssured.when().get("/game").then().statusCode(200).root("_links.%s.href").body(RestAssured.withArgs("self"), RestAssuredMatchers.endsWithPath("id"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void response_aware_matchers_are_composable_with_hamcrest_matchers() {
        RestAssured.when().get("/game").then().statusCode(200).body("_links.self.href", ResponseAwareMatcherComposer.and(RestAssuredMatchers.endsWithPath("id"), anyOf(startsWith("http://localhost:8081"), containsString("localhost")), ( response) -> containsString(response.path("id")))).body("status", equalTo("ongoing"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void and_using_hamcrest_matchers_are_composable_with_response_aware_matchers() {
        RestAssured.when().get("/game").then().statusCode(200).body("_links.self.href", ResponseAwareMatcherComposer.and(startsWith("http://localhost:8080"), RestAssuredMatchers.endsWithPath("id"))).body("status", equalTo("ongoing"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void or_using_hamcrest_matchers_are_composable_with_response_aware_matchers() {
        RestAssured.when().get("/game").then().statusCode(200).body("_links.self.href", ResponseAwareMatcherComposer.or(startsWith("http://localhost:8081"), RestAssuredMatchers.endsWithPath("_links.self.href"))).body("status", equalTo("ongoing"));
    }

    @Test
    public void response_aware_matchers_are_composable_with_other_response_aware_matchers() {
        RestAssured.when().get("/game").then().statusCode(200).body("_links.self.href", ResponseAwareMatcherComposer.and(RestAssuredMatchers.endsWithPath("id"), ( response) -> containsString("localhost"))).body("status", equalTo("ongoing"));
    }

    @Test
    public void response_aware_matchers_are_composable_with_other_response_aware_matchers_and_hamcrest_matchers() {
        RestAssured.when().get("/game").then().statusCode(200).body("_links.self.href", ResponseAwareMatcherComposer.or(ResponseAwareMatcherComposer.and(RestAssuredMatchers.endsWithPath("id"), ( response) -> containsString("localhost2")), ( response) -> startsWith("http://"))).body("status", equalTo("ongoing"));
    }

    @Test
    public void using_restAssuredJsonRootObject_for_nested_queries() {
        RestAssured.when().get("/response").then().log().all().statusCode(200).body("response.data.tasks.findAll{ task -> task.triggered_by.contains(restAssuredJsonRootObject.response.data.tasks.find{ t2 -> t2.name.equals('InvestigateSuggestions')}.id) }.name", containsInAnyOrder("Mistral", "Ansible", "Camunda"));
    }
}

