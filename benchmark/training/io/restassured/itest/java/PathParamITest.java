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
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.itest.java.support.RequestPathFromLogExtractor;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.path.json.JsonPath;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.output.WriterOutputStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class PathParamITest extends WithJetty {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void supportsPassingPathParamsToRequestSpec() throws Exception {
        when().get("/{firstName}/{lastName}", "John", "Doe");
    }

    @Test
    public void possibleToGetOriginalRequestPathForUnnamedPathParamsFromRequestSpec() throws Exception {
        when().get("/{firstName}/{lastName}", "John", "Doe").then().body("fullName", Matchers.equalTo("John Doe"));
    }

    @Test
    public void possibleToGetOriginalRequestPathForNamedPathParamsUsingRequestSpec() throws Exception {
        when().get("/{firstName}/{lastName}").then().body("fullName", Matchers.equalTo("John Doe"));
    }

    @Test
    public void supportsPassingPathParamsAsMapToRequestSpec() throws Exception {
        final Map<String, Object> params = new HashMap<>();
        params.put("firstName", "John");
        params.put("lastName", 42);
        when().get("/{firstName}/{lastName}", params);
    }

    @Test
    public void supportsPassingIntPathParamsToRequestSpec() throws Exception {
        when().get("/{firstName}/{lastName}", "John", 42);
    }

    @Test
    public void urlEncodesPathParams() throws Exception {
        when().get("/{firstName}/{lastName}", "John:()", "Doe");
    }

    @Test
    public void doesntUrlEncodesPathParamsWhenUrlEncodingIsDisabled() throws Exception {
        RestAssured.urlEncodingEnabled = false;
        final String encoded = URLEncoder.encode("John:()", "UTF-8");
        try {
            when().get("/{firstName}/{lastName}", encoded, "Doe");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void urlEncodesPathParamsInMap() throws Exception {
        final Map<String, String> params = new HashMap<>();
        params.put("firstName", "John: ?");
        params.put("lastName", "Doe");
        when().get("/{firstName}/{lastName}", params);
    }

    @Test
    public void doesntUrlEncodePathParamsInMapWhenUrlEncodingIsDisabled() throws Exception {
        RestAssured.urlEncodingEnabled = false;
        try {
            final Map<String, String> params = new HashMap<>();
            params.put("firstName", "John%20?");
            params.put("lastName", "Doe");
            when().get("/{firstName}/{lastName}", params);
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void supportsPassingPathParamsToGet() throws Exception {
        final String response = get("/{firstName}/{lastName}", "John", "Doe").asString();
        final String fullName = JsonPath.from(response).getString("fullName");
        Assert.assertThat(fullName, Matchers.equalTo("John Doe"));
    }

    @Test
    public void supportsPassingPathParamsAsMapToGet() throws Exception {
        final Map<String, String> params = new HashMap<>();
        params.put("firstName", "John=me");
        params.put("lastName", "Doe");
        final String response = get("/{firstName}/{lastName}", params).asString();
        final String fullName = JsonPath.from(response).getString("fullName");
        Assert.assertThat(fullName, Matchers.equalTo("John=me Doe"));
    }

    @Test
    public void throwsIAEWhenNumberOfSuppliedUnnamedPathParamsAreGreaterThanDefinedPathParams() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid number of path parameters. Expected 2, was 3. Redundant path parameters are: Real Doe");
        get("/{firstName}/{lastName}", "John", "Doe", "Real Doe");
    }

    @Test
    public void throwsIAEWhenNumberOfSuppliedNamedPathParamsAreEqualButDifferentToPlaceholders() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Path parameters were not correctly defined. Redundant path parameters are: x=first, y=second. Undefined path parameters are: firstName, lastName.");
        given().pathParam("x", "first").pathParam("y", "second").get("/{firstName}/{lastName}");
    }

    @Test
    public void throwsIAEWhenNumberOfSuppliedNamedPathParamsAreDefinedButNoPlaceholdersAreDefined() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid number of path parameters. Expected 0, was 2. Redundant path parameters are: x=first, y=second.");
        given().pathParam("x", "first").pathParam("y", "second").get("/x");
    }

    @Test
    public void throwsIAEWhenNumberOfSuppliedNamedPathParamsIsGreaterThanDefinedPlaceholders() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid number of path parameters. Expected 2, was 3. Redundant path parameters are: x=first, y=second. Undefined path parameters are: firstName.");
        given().pathParam("x", "first").pathParam("lastName", "Doe").pathParam("y", "second").get("/{firstName}/{lastName}");
    }

    @Test
    public void throwsIAEWhenNumberOfSuppliedNamedAndUnnamedPathParamsIsGreaterThanDefinedPlaceholders() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid number of path parameters. Expected 2, was 6. Redundant path parameters are: x=first, y=second and Doe, Last.");
        given().pathParam("x", "first").pathParam("y", "second").get("/{firstName}/{lastName}", "John", "Middle", "Doe", "Last");
    }

    @Test
    public void throwsIAEWhenNumberOfSuppliedPathParamsAreLowerThanDefinedPathParams() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid number of path parameters. Expected 2, was 1. Undefined path parameters are: lastName");
        get("/{firstName}/{lastName}", "John");
    }

    @Test
    public void supportsPassingPathParamWithGiven() throws Exception {
        when().get("/{firstName}/{lastName}");
    }

    @Test
    public void supportsPassingPathParamWithIntWithGiven() throws Exception {
        when().get("/{firstName}/{lastName}");
    }

    @Test
    public void supportsPassingPathParamsWithGiven() throws Exception {
        when().get("/{firstName}/{lastName}");
    }

    @Test
    public void supportsPassingPathParamsWithIntWithGiven() throws Exception {
        when().get("/{firstName}/{lastName}");
    }

    @Test
    public void supportsPassingPathParamsWithMapWithGiven() throws Exception {
        final Map<String, String> params = new HashMap<>();
        params.put("firstName", "John");
        params.put("lastName", "Doe");
        when().get("/{firstName}/{lastName}");
    }

    @Test
    public void supportsPassingPathParamsWithIntWithMapWhenGiven() throws Exception {
        final Map<String, Object> params = new HashMap<>();
        params.put("firstName", "John");
        params.put("lastName", 42);
        when().get("/{firstName}/{lastName}");
    }

    @Test
    public void mergesPathParamsMapWithNonMapWhenGiven() throws Exception {
        final Map<String, Object> params = new HashMap<>();
        params.put("firstName", "John");
        when().get("/{firstName}/{lastName}");
    }

    @Test
    public void passingInTwoManyPathParamsWithGivenThrowsIAE() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid number of path parameters. Expected 2, was 3.");
        when().get("/{firstName}/{lastName}");
    }

    @Test
    public void passingInTooFewNamedPathParamsWithGivenThrowsIAE() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid number of path parameters. Expected 2, was 1. Undefined path parameters are: lastName");
        when().get("/{firstName}/{lastName}");
    }

    @Test
    public void canUsePathParamsWithNonStandardChars() throws Exception {
        final String nonStandardChars = "\\$\u00a3@\"){\u00a4$";
        when().get("/{firstName}/{lastName}", nonStandardChars, "Last");
    }

    @Test
    public void passingInSinglePathParamsThatHaveBeenDefinedMultipleTimesWorks() throws Exception {
        when().get("/{firstName}/{firstName}");
    }

    @Test
    public void unnamedQueryParametersWorks() throws Exception {
        when().get("http://www.google.se/search?q={query}&hl=en", "query");
    }

    @Test
    public void throwsIllegalArgumentExceptionWhenTooManyPathParametersAreUsed() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid number of path parameters. Expected 1, was 2. Redundant path parameters are: ikk.");
        when().get("http://www.google.se/search?q={query}&hl=en", "query", "ikk");
    }

    @Test
    public void throwsIllegalArgumentExceptionWhenTooFewPathParametersAreUsed() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid number of path parameters. Expected 1, was 0.");
        when().get("http://www.google.se/search?q={query}&hl=en");
    }

    @Test
    public void mixingUnnamedPathParametersAndQueryParametersWorks() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        given().config(RestAssuredConfig.config().logConfig(new LogConfig(captor, true))).log().all().filter(new Filter() {
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setBody("changed").build();
            }
        }).get("/{channelName}/item-import/rss/import?source={url}", "games", "http://myurl.com");
        // Then
        Assert.assertThat(RequestPathFromLogExtractor.loggedRequestPathIn(writer), Matchers.equalTo("http://localhost:8080/games/item-import/rss/import?source=http%3A%2F%2Fmyurl.com"));
    }

    @Test
    public void urlEncodesUnnamedPathParametersThatContainsCurlyBracesAndEquals() throws Exception {
        // When
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        given().config(RestAssuredConfig.config().logConfig(new LogConfig(captor, true))).log().all().filter(new Filter() {
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setBody("changed").build();
            }
        }).get("/feed?canonicalName={trackingName}&platform=ed4", "{trackingName='trackingname1'}");
        // Then
        Assert.assertThat(RequestPathFromLogExtractor.loggedRequestPathIn(writer), Matchers.equalTo("http://localhost:8080/feed?canonicalName=%7BtrackingName%3D%27trackingname1%27%7D&platform=ed4"));
    }

    @Test
    public void urlEncodesNamedPathParametersThatContainsCurlyBracesAndEquals() throws Exception {
        // When
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        given().config(RestAssuredConfig.config().logConfig(new LogConfig(captor, true))).pathParam("trackingName", "{trackingName='trackingname1'}").pathParam("platform", "platform").log().all().filter(new Filter() {
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setBody("changed").build();
            }
        }).get("/feed?canonicalName={trackingName}&{platform}=ed4");
        // Then
        Assert.assertThat(RequestPathFromLogExtractor.loggedRequestPathIn(writer), Matchers.equalTo("http://localhost:8080/feed?canonicalName=%7BtrackingName%3D%27trackingname1%27%7D&platform=ed4"));
    }

    @Test
    public void unnamedPathParametersCanBeAppendedBeforeSubPath() throws Exception {
        get("/{path}.json", "something").then().assertThat().statusCode(Matchers.is(200)).and().body("value", Matchers.equalTo("something"));
    }

    @Test
    public void namedPathParametersCanBeAppendedBeforeSubPath() throws Exception {
        when().get("/{path}.json").then().assertThat().statusCode(Matchers.is(200)).and().body("value", Matchers.equalTo("something"));
    }

    @Test
    public void unnamedPathParametersCanBeAppendedAfterSubPath() throws Exception {
        get("/something.{format}", "json").then().assertThat().statusCode(Matchers.is(200)).and().body("value", Matchers.equalTo("something"));
    }

    @Test
    public void namedPathParametersCanBeAppendedAfterSubPath() throws Exception {
        when().get("/something.{format}").then().assertThat().statusCode(Matchers.is(200)).and().body("value", Matchers.equalTo("something"));
    }

    @Test
    public void namedPathParametersWorksWithUnicodeParameterValues() throws Exception {
        when().get("/reflect?param1={param1Value}&param2={param2Value}").then().statusCode(Matchers.is(200)).body(Matchers.equalTo("http://localhost:8080/reflect?param1=Hello&param2=Hello%C2%85"));
    }

    @Test
    public void unnamedPathParametersWorksWithUnicodeParameterValues() throws Exception {
        when().get("/reflect?param1={param1Value}&param2={param2Value}", "Hello", "Hello\u0085").then().statusCode(Matchers.is(200)).body(Matchers.equalTo("http://localhost:8080/reflect?param1=Hello&param2=Hello%C2%85"));
    }

    @Test
    public void unnamedPathParametersWorksWhenThereAreMultipleTemplatesBetweenEachSlash() throws Exception {
        String param1Value = "Hello";
        String param2Value = "Hello2";
        when().get("param1={param1Value}&param2={param2Value}", param1Value, param2Value).then().body(Matchers.equalTo("http://localhost:8080/param1%3DHello%26param2%3DHello2"));
    }

    @Test
    public void namedPathParametersWorksWhenThereAreMultipleTemplatesBetweenEachSlash() throws Exception {
        String param1Value = "Hello";
        String param2Value = "Hello2";
        when().get("param1={param1Value}&param2={param2Value}").then().body(Matchers.equalTo("http://localhost:8080/param1%3DHello%26param2%3DHello2"));
    }

    @Test
    public void canSetNamedPathParameterDefinedAsFirstPathParamInPathAndConjWithAnUnnamedPathParam() {
        when().get("/{firstName}/{lastName}", "Doe").then().statusCode(200).body("firstName", Matchers.equalTo("John")).body("lastName", Matchers.equalTo("Doe")).body("fullName", Matchers.equalTo("John Doe"));
    }

    @Test
    public void canSetNamedPathParameterDefinedAsLastPathParamInPathAndConjWithAnUnnamedPathParam() {
        when().get("/{firstName}/{lastName}", "John").then().statusCode(200).body("firstName", Matchers.equalTo("John")).body("lastName", Matchers.equalTo("Doe")).body("fullName", Matchers.equalTo("John Doe"));
    }

    @Test
    public void named_path_parameters_have_precedence_over_unnamed_path_parameters() {
        when().get("/{firstName}/{middleName}/{lastName}", "John", "Doe").then().statusCode(200).body("firstName", Matchers.equalTo("John")).body("middleName", Matchers.equalTo("The Beast")).body("lastName", Matchers.equalTo("Doe"));
    }

    @Test
    public void can_specify_space_only_named_path_parameters() {
        when().get("/{firstName}/{lastName}").then().statusCode(200).body("firstName", Matchers.equalTo("John")).body("lastName", Matchers.equalTo(" "));
    }

    @Test
    public void can_specify_space_only_unnamed_path_parameters() {
        when().get("/{firstName}/{lastName}", "John", " ").then().statusCode(200).body("firstName", Matchers.equalTo("John")).body("lastName", Matchers.equalTo(" "));
    }

    @Test
    public void can_specify_empty_named_path_parameters() {
        when().get("/{firstName}/{lastName}").then().statusCode(404);// A resource matching only {firstName} is not defined

    }

    @Test
    public void can_specify_empty_unnamed_path_parameters() {
        when().get("/{firstName}/{lastName}", "John", "").then().statusCode(404);// A resource matching only {firstName} is not defined

    }

    @Test
    public void returns_nice_error_message_when_several_unnamed_path_parameter_are_be_null() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Unnamed path parameter cannot be null (path parameters at indices 0,2 are null)");
        get("/{firstName}/{middleName}", null, "something", null);
    }

    @Test
    public void returns_nice_error_message_when_single_unnamed_path_parameter_is_null() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Unnamed path parameter cannot be null (path parameter at index 0 is null");
        get("/{firstName}/{middleName}", ((Object) (null)));
    }

    @Test
    public void can_use_path_parameters_value_shorter_than_the_template_name_when_using_multiple_templates_in_a_subresource() {
        when().get("/matrix;{abcde}={value}", "John", "Doe").then().statusCode(200).body("John", Matchers.equalTo("Doe"));
    }

    @Test
    public void can_use_path_parameters_value_longer_than_the_template_name_when_using_multiple_templates_in_a_subresource() {
        when().get("/matrix;{abcde}={value}", "JohnJohn", "Doe").then().statusCode(200).body("JohnJohn", Matchers.equalTo("Doe"));
    }
}

