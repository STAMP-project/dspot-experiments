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


import Parser.HTML;
import io.restassured.internal.ResponseSpecificationImpl;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class ResponseSpecBuilderTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void response_spec_doesnt_throw_NPE_when_logging_all_after_creation() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Cannot configure logging since request specification is not defined. You may be misusing the API.");
        new ResponseSpecBuilder().build().log().all(true);
    }

    @Test
    public void responseSpecShouldContainMergedExpectations() {
        ResponseSpecification originalSpec = new ResponseSpecBuilder().expectBody(CoreMatchers.equalTo("goodTestBody")).build();
        ResponseSpecification mergedSpec = new ResponseSpecBuilder().addResponseSpecification(originalSpec).build();
        Response goodResponse = Mockito.mock(Response.class);
        Mockito.when(goodResponse.asString()).thenReturn("goodTestBody");
        Response badResponse = Mockito.mock(Response.class);
        Mockito.when(badResponse.asString()).thenReturn("badTestBody");
        mergedSpec.validate(goodResponse);
        exception.expect(AssertionError.class);
        mergedSpec.validate(badResponse);
    }

    @Test
    public void responseParserShouldHandleConfiguredContentType() {
        ResponseSpecificationImpl responseSpec = ((ResponseSpecificationImpl) (new ResponseSpecBuilder().registerParser("dummyContentType", HTML).build()));
        Assert.assertEquals(HTML, responseSpec.getRpr().getParser("dummyContentType"));
    }

    @Test
    public void defaultResponseParserShouldBeConfiguredToHandleUnrecognizedContentTypes() {
        ResponseSpecificationImpl responseSpec = ((ResponseSpecificationImpl) (new ResponseSpecBuilder().setDefaultParser(HTML).build()));
        Assert.assertEquals(HTML, responseSpec.getRpr().getParser("nonExistentContentType"));
    }
}

