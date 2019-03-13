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
package io.restassured.module.mockmvc;


import MatcherConfig.ErrorDescriptionType.HAMCREST;
import MatcherConfig.ErrorDescriptionType.REST_ASSURED;
import io.restassured.config.MatcherConfig;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import io.restassured.module.mockmvc.http.GreetingController;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static RestAssuredMockMvc.config;


public class MockMvcMatcherConfigTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void response_message_is_formatted_with_rest_assured_description_type() {
        config = RestAssuredMockMvcConfig.config().matcherConfig(MatcherConfig.matcherConfig().errorDescriptionType(REST_ASSURED));
        exception.expect(AssertionError.class);
        exception.expectMessage(("1 expectation failed.\n" + (("JSON path content doesn\'t match.\n" + "Expected: Hello, John!\n") + "  Actual: Hello, World!")));
        try {
            // When
            RestAssuredMockMvc.given().standaloneSetup(new GreetingController()).when().get("/greeting").then().body("content", Matchers.equalTo("Hello, John!"));
        } finally {
            RestAssuredMockMvc.reset();
        }
    }

    @Test
    public void response_message_is_formatted_with_hamcrest_description_type() {
        config = RestAssuredMockMvcConfig.config().matcherConfig(MatcherConfig.matcherConfig().errorDescriptionType(HAMCREST));
        exception.expect(AssertionError.class);
        exception.expectMessage(("1 expectation failed.\n" + ((("JSON path content doesn\'t match.\n" + "\n") + "Expected: \"Hello, John!\"\n") + "  Actual: was \"Hello, World!\"")));
        try {
            // When
            RestAssuredMockMvc.given().standaloneSetup(new GreetingController()).when().get("/greeting").then().body("content", Matchers.equalTo("Hello, John!"));
        } finally {
            RestAssuredMockMvc.reset();
        }
    }
}

