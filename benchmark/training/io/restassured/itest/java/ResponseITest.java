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


import io.restassured.builder.ResponseBuilder;
import io.restassured.http.Headers;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import io.restassured.path.xml.exception.XmlPathException;
import io.restassured.response.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ResponseITest extends WithJetty {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenNoExpectationsDefinedThenGetCanReturnBodyAsString() throws Exception {
        final String body = get("/hello").asString();
        Assert.assertEquals("{\"hello\":\"Hello Scalatra\"}", body);
    }

    @Test
    public void whenNoExpectationsDefinedThenGetCanReturnAStringAsByteArray() throws Exception {
        final byte[] expected = "{\"hello\":\"Hello Scalatra\"}".getBytes();
        final byte[] actual = get("/hello").asByteArray();
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void whenExpectationsDefinedThenAsStringReturnsCanReturnTheResponseBody() throws Exception {
        final String body = get("/hello").asString();
        Assert.assertThat(body, containsString("Hello"));
    }

    @Test
    public void whenNoExpectationsDefinedThenPostCanReturnBodyAsString() throws Exception {
        final String body = with().parameters("firstName", "John", "lastName", "Doe").post("/greetXML").andReturn().body().asString();
        Assert.assertEquals(("<greeting><firstName>John</firstName>\n" + ("      <lastName>Doe</lastName>\n" + "    </greeting>")), body);
    }

    @Test
    public void whenNoExpectationsDefinedThenPostWithBodyCanReturnBodyAsString() throws Exception {
        byte[] body = new byte[]{ 23, 42, 127, 123 };
        final String actual = given().body(body).when().post("/binaryBody").andReturn().asString();
        Assert.assertEquals("23, 42, 127, 123", actual);
    }

    @Test
    public void whenNoExpectationsDefinedThenPutCanReturnBodyAsString() throws Exception {
        final String actual = given().cookies("username", "John", "token", "1234").when().put("/cookie").asString();
        Assert.assertEquals("username, token", actual);
    }

    @Test
    public void whenNoExpectationsDefinedThenPutWithBodyCanReturnBodyAsString() throws Exception {
        final String body = given().body("a body").when().put("/body").andReturn().body().asString();
        Assert.assertEquals("a body", body);
    }

    @Test
    public void whenNoExpectationsDefinedThenDeleteWithBodyCanReturnBodyAsString() throws Exception {
        final String actual = given().parameters("firstName", "John", "lastName", "Doe").when().delete("/greet").thenReturn().asString();
        Assert.assertEquals("{\"greeting\":\"Greetings John Doe\"}", actual);
    }

    @Test
    public void responseSupportsGettingCookies() throws Exception {
        final Response response = get("/setCookies");
        Assert.assertEquals(3, response.getCookies().size());
        Assert.assertEquals(3, response.cookies().size());
        Assert.assertEquals("value1", response.getCookie("key1"));
        Assert.assertEquals("value2", response.cookie("key2"));
    }

    @Test
    public void responseSupportsGettingHeaders() throws Exception {
        final Response response = get("/setCookies");
        Assert.assertEquals(7, response.getHeaders().size());
        Assert.assertEquals(7, response.headers().size());
        Assert.assertEquals("text/plain;charset=utf-8", response.getHeader("Content-Type"));
        final String server = response.header("Server");
        Assert.assertThat(server, containsString("Jetty"));
    }

    @Test
    public void responseSupportsGettingStatusLine() throws Exception {
        final Response response = get("/hello");
        Assert.assertThat(response.statusLine(), equalTo("HTTP/1.1 200 OK"));
        Assert.assertThat(response.getStatusLine(), equalTo("HTTP/1.1 200 OK"));
    }

    @Test
    public void responseSupportsGettingStatusCode() throws Exception {
        final Response response = get("/hello");
        Assert.assertThat(response.statusCode(), equalTo(200));
        Assert.assertThat(response.getStatusCode(), equalTo(200));
    }

    @Test
    public void whenNoExpectationsDefinedThenGetCanReturnBodyAsInputStream() throws Exception {
        final InputStream inputStream = get("/hello").asInputStream();
        final String string = IOUtils.toString(inputStream);
        Assert.assertThat(string, equalTo("{\"hello\":\"Hello Scalatra\"}"));
    }

    @Test
    public void whenExpectationsDefinedThenGetCanReturnBodyAsInputStream() throws Exception {
        final InputStream inputStream = get("/hello").asInputStream();
        final String string = IOUtils.toString(inputStream);
        Assert.assertThat(string, equalTo("{\"hello\":\"Hello Scalatra\"}"));
    }

    @Test
    public void whenExpectationsDefinedAndLoggingThenGetCanReturnBodyAsInputStream() throws Exception {
        final InputStream inputStream = get("/hello").asInputStream();
        final String string = IOUtils.toString(inputStream);
        Assert.assertThat(string, equalTo("{\"hello\":\"Hello Scalatra\"}"));
    }

    @Test
    public void whenNoExpectationsDefinedButLoggingThenGetCanReturnBodyAsInputStream() throws Exception {
        final InputStream inputStream = get("/hello").asInputStream();
        final String string = IOUtils.toString(inputStream);
        Assert.assertThat(string, equalTo("{\"hello\":\"Hello Scalatra\"}"));
    }

    @Test
    public void usingJsonPathViewFromTheResponse() throws Exception {
        final String hello = get("/hello").andReturn().jsonPath().getString("hello");
        Assert.assertThat(hello, equalTo("Hello Scalatra"));
    }

    @Test
    public void usingXmlPathViewFromTheResponse() throws Exception {
        final String firstName = with().parameters("firstName", "John", "lastName", "Doe").post("/greetXML").andReturn().xmlPath().getString("greeting.firstName");
        Assert.assertThat(firstName, equalTo("John"));
    }

    @Test
    public void usingXmlPathWithHtmlCompatibilityModeFromTheResponse() throws Exception {
        // When
        final String title = get("/textHTML").xmlPath(HTML).getString("html.head.title");
        // Then
        Assert.assertThat(title, equalTo("my title"));
    }

    @Test
    public void usingHtmlPathToParseHtmlFromTheResponse() throws Exception {
        // When
        final String title = get("/textHTML").htmlPath().getString("html.head.title");
        // Then
        Assert.assertThat(title, equalTo("my title"));
    }

    @Test
    public void usingPathWithContentTypeJsonFromTheResponse() throws Exception {
        final String hello = get("/hello").andReturn().path("hello");
        Assert.assertThat(hello, equalTo("Hello Scalatra"));
    }

    @Test
    public void usingPathWithParameters() throws Exception {
        final String hello = get("/hello").andReturn().path("hel%s", "lo");
        Assert.assertThat(hello, equalTo("Hello Scalatra"));
    }

    @Test
    public void usingPathWithContentTypeXmlFromTheResponse() throws Exception {
        final String firstName = with().parameters("firstName", "John", "lastName", "Doe").post("/greetXML").andReturn().path("greeting.firstName");
        Assert.assertThat(firstName, equalTo("John"));
    }

    @Test
    public void usingACustomRegisteredParserAllowsUsingPath() throws Exception {
        final String message = get("/customMimeTypeJsonCompatible2").path("message");
        Assert.assertThat(message, equalTo("It works"));
    }

    @Test
    public void usingADefaultParserAllowsUsingPath() throws Exception {
        final String message = get("/customMimeTypeJsonCompatible2").path("message");
        Assert.assertThat(message, equalTo("It works"));
    }

    @Test
    public void responseTakeCharsetIntoAccount() throws Exception {
        ResponseBuilder b = new ResponseBuilder();
        b.setHeaders(new Headers());
        b.setBody(new ByteArrayInputStream("???".getBytes("UTF-8")));
        b.setStatusCode(200);
        b.setContentType("application/json;charset=UTF-8");
        final Response response = b.build();
        Assert.assertThat("???", equalTo(response.asString()));
    }

    @Test
    public void jsonPathReturnedByResponseUsesConfigurationFromRestAssured() throws Exception {
        // When
        final JsonPath jsonPath = get("/jsonStore").jsonPath();
        // Then
        Assert.assertThat(jsonPath.<BigDecimal>get("store.book.price.min()"), is(new BigDecimal("8.95")));
        Assert.assertThat(jsonPath.<BigDecimal>get("store.book.price.max()"), is(new BigDecimal("22.99")));
    }

    @Test
    public void jsonPathWithConfigReturnedByResponseOverridesConfigurationFromRestAssured() throws Exception {
        // When
        final JsonPath jsonPath = get("/jsonStore").jsonPath(with().numberReturnType(JsonPathConfig.NumberReturnType.FLOAT_AND_DOUBLE));
        // Then
        Assert.assertThat(jsonPath.<Float>get("store.book.price.min()"), is(8.95F));
        Assert.assertThat(jsonPath.<Float>get("store.book.price.max()"), is(22.99F));
    }

    @Test
    public void pathWorksForMultipleInvocationsWithJson() throws Exception {
        Response response = get("/jsonStore");
        float minPrice = response.path("store.book.price.min()");
        float maxPrice = response.path("store.book.price.max()");
        Assert.assertThat(minPrice, is(8.95F));
        Assert.assertThat(maxPrice, is(22.99F));
    }

    @Test
    public void pathThrowsExceptionWhenTryingToUseXmlPathAfterHavingUsedJsonPath() throws Exception {
        exception.expect(XmlPathException.class);
        exception.expectMessage("Failed to parse the XML document");
        Response response = get("/jsonStore");
        response.path("store.book.price.min()");
        get("store.book.price.min()");
    }

    @Test
    public void pathWorksForMultipleInvocationsWithXml() throws Exception {
        Response response = get("/videos");
        String title = response.path("videos.music[0].title.toString().trim()");
        String artist = response.path("videos.music[0].artist.toString().trim()");
        Assert.assertThat(title, equalTo("Video Title 1"));
        Assert.assertThat(artist, equalTo("Artist 1"));
    }

    @Test
    public void pathThrowsExceptionWhenTryingToUseJsonPathAfterHavingUsedXmlPath() throws Exception {
        exception.expect(JsonPathException.class);
        exception.expectMessage("Failed to parse the JSON document");
        Response response = get("/videos");
        response.path("videos.music[0].title.toString().trim()");
        get("videos");
    }

    @Test
    public void canParsePathAfterPrettyPrint() throws Exception {
        Response response = get("/videos");
        response.prettyPrint();
        String title = response.path("videos.music[0].title.toString().trim()");
        String artist = response.path("videos.music[0].artist.toString().trim()");
        Assert.assertThat(title, equalTo("Video Title 1"));
        Assert.assertThat(artist, equalTo("Artist 1"));
    }

    @Test
    public void canParsePathAfterPrint() throws Exception {
        Response response = get("/videos");
        response.print();
        String title = response.path("videos.music[0].title.toString().trim()");
        String artist = response.path("videos.music[0].artist.toString().trim()");
        Assert.assertThat(title, equalTo("Video Title 1"));
        Assert.assertThat(artist, equalTo("Artist 1"));
    }

    @Test
    public void canGetAsStringMultipleTimes() throws Exception {
        // When
        Response response = get("/videos");
        response.asString();
        String string = response.asString();
        // Then
        Assert.assertThat(string, not(nullValue()));
    }

    @Test
    public void canGetAsByteArrayMultipleTimes() throws Exception {
        // When
        Response response = get("/videos");
        response.asByteArray();
        final byte[] bytes = response.asByteArray();
        // Then
        Assert.assertThat(bytes, not(nullValue()));
    }

    @Test
    public void canCombineAsByteArrayWithPrettyPrintAndAsString() throws Exception {
        // When
        Response response = get("/videos");
        response.asByteArray();
        response.prettyPrint();
        String string = response.asString();
        // Then
        Assert.assertThat(string, not(nullValue()));
    }

    @Test
    public void canCombineAsStringWithPrettyPrintAndAsByteArray() throws Exception {
        // When
        Response response = get("/videos");
        response.asString();
        response.prettyPrint();
        byte[] bytes = response.asByteArray();
        // Then
        Assert.assertThat(bytes, not(nullValue()));
    }

    @Test
    public void canParsePathAfterPrettyPeek() throws Exception {
        Response response = get("/videos").prettyPeek();
        String title = response.path("videos.music[0].title.toString().trim()");
        String artist = response.path("videos.music[0].artist.toString().trim()");
        Assert.assertThat(title, equalTo("Video Title 1"));
        Assert.assertThat(artist, equalTo("Artist 1"));
    }

    @Test
    public void canParsePathAfterPeek() throws Exception {
        Response response = get("/videos").peek();
        String title = response.path("videos.music[0].title.toString().trim()");
        String artist = response.path("videos.music[0].artist.toString().trim()");
        Assert.assertThat(title, equalTo("Video Title 1"));
        Assert.assertThat(artist, equalTo("Artist 1"));
    }
}

