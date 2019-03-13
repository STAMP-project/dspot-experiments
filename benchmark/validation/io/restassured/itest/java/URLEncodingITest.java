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
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.itest.java.support.RequestPathFromLogExtractor;
import io.restassured.itest.java.support.WithJetty;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Assert;
import org.junit.Test;


public class URLEncodingITest extends WithJetty {
    @Test
    public void urlEncodingDisabledStatically() {
        try {
            RestAssured.baseURI = "https://jira.atlassian.com";
            RestAssured.port = 443;
            RestAssured.urlEncodingEnabled = false;
            final String query = "project%20=%20BAM%20AND%20issuetype%20=%20Bug";
            RestAssured.expect().body("issues", not(nullValue())).when().get("/rest/api/2.0.alpha1/search?jql={q}", query);
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void doesntDoubleEncodeParamsWhenDefiningUrlEncodingToFalseStatically() throws Exception {
        RestAssured.urlEncodingEnabled = false;
        try {
            RestAssured.given().formParam("formParam", "form%20param").then().body(equalTo("query param path param form param")).when().post("/{pathParam}/manyParams?queryParam=query%20param", "path%20param");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void doesntDoubleEncodeParamsWhenDefiningUrlEncodingToFalseNonStatically() throws Exception {
        RestAssured.given().urlEncodingEnabled(false).formParam("formParam", "form%20param").then().body(equalTo("query param path param form param")).when().post("/{pathParam}/manyParams?queryParam=query%20param", "path%20param");
    }

    @Test
    public void doesntDoubleEncodeParamsWhenDefiningUrlEncodingToTrue() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        RestAssured.given().urlEncodingEnabled(true).pathParam("pathParam", "path/param").formParam("formParam", "form/param").filter(new RequestLoggingFilter(captor)).filter(new Filter() {
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                /* Note that Scalatra cannot handle request with path parameters containing "/" (like path/param) even though it's URL encoded.
                Scalatra decodes the path prior to finding the method to invoke and thus we'll get an error back (since no resource mapping to /path/param/manyParams exist).
                 */
                return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setBody("changed").build();
            }
        }).then().body(equalTo("changed")).when().post("/{pathParam}/manyParams?queryParam=query/param");
        Assert.assertThat(RequestPathFromLogExtractor.loggedRequestPathIn(writer), equalTo("http://localhost:8080/path%2Fparam/manyParams?queryParam=query%2Fparam"));
    }

    @Test
    public void urlsWithSchemeIsOkToSendInUrlWithoutBeingUrlEncoded() throws Exception {
        // Given
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        String agentUrl = "https://localhost:9888";
        RestAssured.urlEncodingEnabled = false;
        // When
        try {
            RestAssured.given().contentType(JSON).filter(new RequestLoggingFilter(captor)).filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    /* Note that Scalatra cannot handle request with path parameters containing "/" (like path/param) even though it's URL encoded.
                    Scalatra decodes the path prior to finding the method to invoke and thus we'll get an error back (since no resource mapping to /path/param/manyParams exist).
                     */
                    return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setBody("changed").build();
                }
            }).log().all().expect().statusCode(200).body(equalTo("changed")).when().get(("/agents/probeUrl/" + agentUrl));
        } finally {
            RestAssured.reset();
        }
        Assert.assertThat(RequestPathFromLogExtractor.loggedRequestPathIn(writer), equalTo("http://localhost:8080/agents/probeUrl/https://localhost:9888"));
    }

    @Test
    public void urlEncodesNamedPathParameters() throws Exception {
        // Given
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        String agentUrl = "https://localhost:9888";
        RestAssured.urlEncodingEnabled = true;
        // When
        try {
            RestAssured.given().contentType(JSON).pathParam("x", agentUrl).filter(new RequestLoggingFilter(captor)).filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    /* Note that Scalatra cannot handle request with path parameters containing "/" (like path/param) even though it's URL encoded.
                    Scalatra decodes the path prior to finding the method to invoke and thus we'll get an error back (since no resource mapping to /path/param/manyParams exist).
                     */
                    return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setBody("changed").build();
                }
            }).expect().statusCode(200).when().get("/agents/probeUrl/{x}");
        } finally {
            RestAssured.reset();
        }
        Assert.assertThat(RequestPathFromLogExtractor.loggedRequestPathIn(writer), equalTo("http://localhost:8080/agents/probeUrl/https%3A%2F%2Flocalhost%3A9888"));
    }

    @Test
    public void urlEncodesUnnamedPathParameters() throws Exception {
        // Given
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        String agentUrl = "https://localhost:9888";
        RestAssured.urlEncodingEnabled = true;
        // When
        try {
            RestAssured.given().contentType(JSON).filter(new RequestLoggingFilter(captor)).filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    /* Note that Scalatra cannot handle request with path parameters containing "/" (like path/param) even though it's URL encoded.
                    Scalatra decodes the path prior to finding the method to invoke and thus we'll get an error back (since no resource mapping to /path/param/manyParams exist).
                     */
                    return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setBody("changed").build();
                }
            }).expect().statusCode(200).when().get("/agents/probeUrl/{x}", agentUrl);
        } finally {
            RestAssured.reset();
        }
        Assert.assertThat(RequestPathFromLogExtractor.loggedRequestPathIn(writer), equalTo("http://localhost:8080/agents/probeUrl/https%3A%2F%2Flocalhost%3A9888"));
    }

    @Test
    public void doesntUrlEncodePathFragmentsWhenUrlEncodingIsDisabled() throws Exception {
        // Given
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        String agentUrl = URLEncoder.encode("https://localhost:9888", "UTF-8");
        RestAssured.urlEncodingEnabled = false;
        RestAssured.basePath = "/tmc/api";
        // When
        try {
            RestAssured.given().contentType(JSON).filter(new RequestLoggingFilter(captor)).filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    /* Note that Scalatra cannot handle request with path parameters containing "/" (like path/param) even though it's URL encoded.
                    Scalatra decodes the path prior to finding the method to invoke and thus we'll get an error back (since no resource mapping to /path/param/manyParams exist).
                     */
                    return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setBody("changed").build();
                }
            }).expect().statusCode(200).body(equalTo("changed")).when().get(("/agents/probeUrl/" + agentUrl));
        } finally {
            RestAssured.reset();
        }
        Assert.assertThat(RequestPathFromLogExtractor.loggedRequestPathIn(writer), equalTo("http://localhost:8080/tmc/api/agents/probeUrl/https%3A%2F%2Flocalhost%3A9888"));
    }

    @Test
    public void urlEncodesPathFragmentsRegardlessIfTheyHaveBeenManuallyEncodedWhenUrlEncodingIsEnabled() throws Exception {
        // Given
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        String agentUrl = URLEncoder.encode("https://localhost:9888", "UTF-8");
        RestAssured.urlEncodingEnabled = true;
        // When
        try {
            RestAssured.given().contentType(JSON).filter(new RequestLoggingFilter(captor)).filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    /* Note that Scalatra cannot handle request with path parameters containing "/" (like path/param) even though it's URL encoded.
                    Scalatra decodes the path prior to finding the method to invoke and thus we'll get an error back (since no resource mapping to /path/param/manyParams exist).
                     */
                    return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setBody("changed").build();
                }
            }).expect().statusCode(200).body(equalTo("changed")).when().get(("/agents/probeUrl/" + agentUrl));
        } finally {
            RestAssured.reset();
        }
        Assert.assertThat(RequestPathFromLogExtractor.loggedRequestPathIn(writer), equalTo("http://localhost:8080/agents/probeUrl/https%253A%252F%252Flocalhost%253A9888"));
    }
}

