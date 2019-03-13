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


import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.itest.java.support.WithJetty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class HeaderITest extends WithJetty {
    @Test
    public void requestSpecificationAllowsSpecifyingHeader() throws Exception {
        when().get("/header");
    }

    @Test
    public void allowsSupplyingMappingFunction() throws Exception {
        when().get("/hello").then().header("Content-Length", Integer::parseInt, lessThanOrEqualTo(200));
    }

    @Test
    public void headerExceptionCanFailWhenUsingMappingFunction() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage(containsString("Expected header \"Content-Length\" was not a value greater than <200>, was \"26\". Headers are:"));
        when().get("/hello").then().header("Content-Length", Integer::parseInt, greaterThan(200));
    }

    @Test
    public void allowsParsingMultiValueHeaders() throws Exception {
        final List<String> headers = when().get("/multiValueHeader").headers().getValues("MultiHeader");
        Assert.assertThat(headers, hasItems("Value 1", "Value 2"));
    }

    @Test
    public void orderIsMaintainedForMultiValueHeaders() throws Exception {
        Headers headers = when().get("/multiValueHeader").headers();
        final List<String> headerListString = headers.getValues("MultiHeader");
        final String firstValue = headers.getValue("MultiHeader");
        final List<Header> headerListHeader = headers.getList("MultiHeader");
        Assert.assertThat(headerListString, contains("Value 1", "Value 2"));
        Assert.assertThat(headerListHeader, contains(new Header("MultiHeader", "Value 1"), new Header("MultiHeader", "Value 2")));
        Assert.assertThat(firstValue, equalTo("Value 2"));
    }

    @Test
    public void requestSpecificationAllowsSpecifyingMultiValueHeaders() throws Exception {
        final List<String> myHeaderValues = when().get("/multiHeaderReflect").headers().getValues("MyHeader");
        Assert.assertThat(myHeaderValues.size(), is(2));
        Assert.assertThat(myHeaderValues, hasItems("Something", "Something else"));
    }

    @Test
    public void responseSpecificationAllowsParsingMultiValueHeadersWithValuesIncludingEqualCharacter() throws Exception {
        final List<String> myHeaderValues = when().get("/multiHeaderReflect").headers().getValues("MyHeader");
        Assert.assertThat(myHeaderValues.size(), is(2));
        Assert.assertThat(myHeaderValues, hasItems("Some=thing", "Something=else="));
    }

    @Test
    public void requestSpecificationAllowsSpecifyingHeadersObject() throws Exception {
        final Header header1 = new Header("MyHeader", "Something");
        final Header header2 = new Header("MyHeader", "Something else");
        final Headers headers = new Headers(header1, header2);
        final List<String> myHeaderValues = when().get("/multiHeaderReflect").headers().getValues("MyHeader");
        Assert.assertThat(myHeaderValues.size(), is(2));
        Assert.assertThat(myHeaderValues, hasItems("Something", "Something else"));
    }

    @Test
    public void requestSpecificationAllowsSpecifyingMultipleHeadersInSequenceWhichGetsTreatedAsMultiHeaders() throws Exception {
        final List<String> myHeaderValues = when().get("/multiHeaderReflect").then().extract().headers().getValues("MyHeader");
        Assert.assertThat(myHeaderValues.size(), is(2));
        Assert.assertThat(myHeaderValues, hasItems("Something", "Something else"));
    }

    @Test
    public void requestSpecificationAllowsSpecifyingHeaders() throws Exception {
        when().get("/header");
    }

    @Test
    public void requestSpecificationAllowsSpecifyingMultipleHeaders() throws Exception {
        when().get("/header");
    }

    @Test
    public void supportsHeaderStringMatching() throws Exception {
        when().get("/lotto");
    }

    @Test
    public void multipleHeaderStatementsAreConcatenated() throws Exception {
        when().get("/lotto");
    }

    @Test
    public void multipleHeadersShortVersionUsingPlainStrings() throws Exception {
        when().get("/lotto");
    }

    @Test
    public void multipleHeadersShortVersionUsingHamcrestMatching() throws Exception {
        when().get("/lotto");
    }

    @Test
    public void multipleHeadersShortVersionUsingMixOfHamcrestMatchingAndStringMatching() throws Exception {
        when().get("/lotto");
    }

    @Test
    public void multipleHeadersUsingMap() throws Exception {
        Map expectedHeaders = new HashMap();
        expectedHeaders.put("Content-Type", "application/json;charset=utf-8");
        expectedHeaders.put("Content-Length", "160");
        when().get("/lotto");
    }

    @Test
    public void multipleHeadersUsingMapWithHamcrestMatcher() throws Exception {
        Map expectedHeaders = new HashMap();
        expectedHeaders.put("Content-Type", containsString("application/json;charset=utf-8"));
        expectedHeaders.put("Content-Length", equalTo("160"));
        when().get("/lotto");
    }

    @Test
    public void multipleHeadersUsingMapWithMixOfStringAndHamcrestMatcher() throws Exception {
        Map expectedHeaders = new HashMap();
        expectedHeaders.put("Content-Type", containsString("application/json;charset=utf-8"));
        expectedHeaders.put("Content-Length", "160");
        when().get("/lotto");
    }

    @Test
    public void whenExpectedHeaderDoesntMatchAnAssertionThenAssertionErrorIsThrown() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage(containsString("Expected header \"Content-Length\" was not \"161\", was \"160\". Headers are:"));
        when().get("/lotto");
    }

    @Test
    public void whenExpectedHeaderIsNotFoundThenAnAssertionErrorIsThrown() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage(equalTo(("1 expectation failed.\n" + ((("Expected header \"Not-Defined\" was not \"160\", was \"null\". Headers are:\n" + "Content-Type=application/json;charset=utf-8\n") + "Content-Length=160\n") + "Server=Jetty(9.3.2.v20150730)\n"))));
        when().get("/lotto");
    }

    @Test
    public void whenMultiValueHeadersArePresentedInTheResponseThenTheLastValueHasPrecedence() throws Exception {
        when().get("/multiValueHeader").then().header("MultiHeader", equalTo("Value 2"));
    }

    @Test
    public void canUseResponseAwareMatchersForHeaderValidation() throws Exception {
        when().post("/redirect").then().statusCode(301).header("Location", ( response) -> endsWith(("/redirect/" + (response.path("id")))));
    }
}

