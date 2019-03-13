/**
 * Copyright 2018 the original author or authors.
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
package io.restassured.builder;


import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests assertions for ResponseSpec. For each method (type of assertion), there is following test data provided:
 * 1) assertion description
 * 2) response spec builder with tested assertion
 * 3) response mock that should pass assertion
 * 4) response mock that should fail assertion
 */
@RunWith(Parameterized.class)
public class ResponseSpecBuilderExpectationsTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ResponseSpecification responseSpecification;

    private Response matchingResponse;

    private Response unmatchedResponse;

    @SuppressWarnings("unused")
    public ResponseSpecBuilderExpectationsTest(String description, Response matchingResponse, Response unmatchedResponse, ResponseSpecBuilder builder) {
        this.matchingResponse = matchingResponse;
        this.unmatchedResponse = unmatchedResponse;
        this.responseSpecification = builder.build();
    }

    @Test
    public void validResponseShouldMatch() {
        responseSpecification.validate(matchingResponse);
    }

    @Test
    public void invalidResponseShouldNotMatch() {
        exception.expect(AssertionError.class);
        responseSpecification.validate(unmatchedResponse);
    }
}

