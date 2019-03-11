/**
 * #%L
 * NanoHttpd-Core
 * %%
 * Copyright (C) 2012 - 2015 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.nanohttpd.junit.protocols.http;


import Method.GET;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.nanohttpd.protocols.http.response.Response;


public class HttpGetRequestTest extends HttpServerTest {
    @Test
    public void testDecodingFieldWithEmptyValueAndFieldWithMissingValueGiveDifferentResults() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo&bar= HTTP/1.1"));
        Assert.assertTrue(((this.testServer.decodedParamters.get("foo")) instanceof List));
        Assert.assertEquals(0, this.testServer.decodedParamters.get("foo").size());
        Assert.assertTrue(((this.testServer.decodedParamters.get("bar")) instanceof List));
        Assert.assertEquals(1, this.testServer.decodedParamters.get("bar").size());
        Assert.assertEquals("", this.testServer.decodedParamters.get("bar").get(0));
    }

    @Test
    public void testDecodingMixtureOfParameters() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=bar&foo=baz&zot&zim= HTTP/1.1"));
        Assert.assertTrue(((this.testServer.decodedParamters.get("foo")) instanceof List));
        Assert.assertEquals(2, this.testServer.decodedParamters.get("foo").size());
        Assert.assertEquals("bar", this.testServer.decodedParamters.get("foo").get(0));
        Assert.assertEquals("baz", this.testServer.decodedParamters.get("foo").get(1));
        Assert.assertTrue(((this.testServer.decodedParamters.get("zot")) instanceof List));
        Assert.assertEquals(0, this.testServer.decodedParamters.get("zot").size());
        Assert.assertTrue(((this.testServer.decodedParamters.get("zim")) instanceof List));
        Assert.assertEquals(1, this.testServer.decodedParamters.get("zim").size());
        Assert.assertEquals("", this.testServer.decodedParamters.get("zim").get(0));
    }

    @Test
    public void testDecodingParametersFromParameterMap() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=bar&foo=baz&zot&zim= HTTP/1.1"));
        Assert.assertEquals(this.testServer.decodedParamters, this.testServer.decodedParamtersFromParameter);
    }

    // --------------------------------------------------------------------------------------------------------
    // //
    @Test
    public void testDecodingParametersWithSingleValue() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=bar&baz=zot HTTP/1.1"));
        Assert.assertEquals("foo=bar&baz=zot", this.testServer.queryParameterString);
        Assert.assertTrue(((this.testServer.decodedParamters.get("foo")) instanceof List));
        Assert.assertEquals(1, this.testServer.decodedParamters.get("foo").size());
        Assert.assertEquals("bar", this.testServer.decodedParamters.get("foo").get(0));
        Assert.assertTrue(((this.testServer.decodedParamters.get("baz")) instanceof List));
        Assert.assertEquals(1, this.testServer.decodedParamters.get("baz").size());
        Assert.assertEquals("zot", this.testServer.decodedParamters.get("baz").get(0));
    }

    @Test
    public void testDecodingParametersWithSingleValueAndMissingValue() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo&baz=zot HTTP/1.1"));
        Assert.assertEquals("foo&baz=zot", this.testServer.queryParameterString);
        Assert.assertTrue(((this.testServer.decodedParamters.get("foo")) instanceof List));
        Assert.assertEquals(0, this.testServer.decodedParamters.get("foo").size());
        Assert.assertTrue(((this.testServer.decodedParamters.get("baz")) instanceof List));
        Assert.assertEquals(1, this.testServer.decodedParamters.get("baz").size());
        Assert.assertEquals("zot", this.testServer.decodedParamters.get("baz").get(0));
    }

    @Test
    public void testDecodingSingleFieldRepeated() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=bar&foo=baz HTTP/1.1"));
        Assert.assertTrue(((this.testServer.decodedParamters.get("foo")) instanceof List));
        Assert.assertEquals(2, this.testServer.decodedParamters.get("foo").size());
        Assert.assertEquals("bar", this.testServer.decodedParamters.get("foo").get(0));
        Assert.assertEquals("baz", this.testServer.decodedParamters.get("foo").get(1));
    }

    @Test
    public void testEmptyHeadersSuppliedToServeMethodFromSimpleWorkingGetRequest() {
        invokeServer((("GET " + (HttpServerTest.URI)) + " HTTP/1.1"));
        Assert.assertNotNull(this.testServer.parms);
        Assert.assertNotNull(this.testServer.parameters);
        Assert.assertNotNull(this.testServer.header);
        Assert.assertNotNull(this.testServer.files);
        Assert.assertNotNull(this.testServer.uri);
    }

    @Test
    public void testFullyQualifiedWorkingGetRequest() throws Exception {
        ByteArrayOutputStream outputStream = invokeServer((("GET " + (HttpServerTest.URI)) + " HTTP/1.1"));
        String[] expected = new String[]{ "HTTP/1.1 200 OK", "Content-Type: text/html", "Date: .*", "Connection: keep-alive", "Content-Length: 0", "" };
        assertResponse(outputStream, expected);
    }

    @Test
    public void testMultipleGetParameters() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=bar&baz=zot HTTP/1.1"));
        Assert.assertEquals("bar", this.testServer.parms.get("foo"));
        Assert.assertEquals("zot", this.testServer.parms.get("baz"));
        Assert.assertEquals("bar", this.testServer.parameters.get("foo").get(0));
        Assert.assertEquals("zot", this.testServer.parameters.get("baz").get(0));
    }

    @Test
    public void testMultipleGetParametersWithMissingValue() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=&baz=zot HTTP/1.1"));
        Assert.assertEquals("", this.testServer.parms.get("foo"));
        Assert.assertEquals("zot", this.testServer.parms.get("baz"));
        Assert.assertEquals("", this.testServer.parameters.get("foo").get(0));
        Assert.assertEquals("zot", this.testServer.parameters.get("baz").get(0));
    }

    @Test
    public void testMultipleGetParametersWithMissingValueAndRequestHeaders() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=&baz=zot HTTP/1.1\nAccept: text/html"));
        Assert.assertEquals("", this.testServer.parms.get("foo"));
        Assert.assertEquals("zot", this.testServer.parms.get("baz"));
        Assert.assertEquals("", this.testServer.parameters.get("foo").get(0));
        Assert.assertEquals("zot", this.testServer.parameters.get("baz").get(0));
        Assert.assertEquals("text/html", this.testServer.header.get("accept"));
    }

    @Test
    public void testMultipleHeaderSuppliedToServeMethodFromSimpleWorkingGetRequest() {
        String userAgent = "jUnit 4.8.2 Unit Test";
        String accept = "text/html";
        invokeServer(((((("GET " + (HttpServerTest.URI)) + " HTTP/1.1\nUser-Agent: ") + userAgent) + "\nAccept: ") + accept));
        Assert.assertEquals(userAgent, this.testServer.header.get("user-agent"));
        Assert.assertEquals(accept, this.testServer.header.get("accept"));
    }

    @Test
    public void testOutputOfServeSentBackToClient() throws Exception {
        String responseBody = "Success!";
        this.testServer.response = Response.newFixedLengthResponse(responseBody);
        ByteArrayOutputStream outputStream = invokeServer((("GET " + (HttpServerTest.URI)) + " HTTP/1.1"));
        String[] expected = new String[]{ "HTTP/1.1 200 OK", "Content-Type: text/html", "Date: .*", "Connection: keep-alive", "Content-Length: 8", "", responseBody };
        assertResponse(outputStream, expected);
    }

    @Test
    public void testSingleGetParameter() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=bar HTTP/1.1"));
        Assert.assertEquals("bar", this.testServer.parms.get("foo"));
        Assert.assertEquals("bar", this.testServer.parameters.get("foo").get(0));
    }

    @Test
    public void testMultipleValueGetParameter() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=bar&foo=baz HTTP/1.1"));
        Assert.assertEquals("bar", this.testServer.parms.get("foo"));
        Assert.assertEquals(2, this.testServer.parameters.get("foo").size());
        Assert.assertEquals("bar", this.testServer.parameters.get("foo").get(0));
        Assert.assertEquals("baz", this.testServer.parameters.get("foo").get(1));
    }

    @Test
    public void testSingleGetParameterWithNoValue() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo HTTP/1.1"));
        Assert.assertEquals("", this.testServer.parms.get("foo"));
        Assert.assertEquals("", this.testServer.parameters.get("foo").get(0));
    }

    @Test
    public void testSingleUserAgentHeaderSuppliedToServeMethodFromSimpleWorkingGetRequest() {
        String userAgent = "jUnit 4.8.2 Unit Test";
        invokeServer((((("GET " + (HttpServerTest.URI)) + " HTTP/1.1\nUser-Agent: ") + userAgent) + "\n"));
        Assert.assertEquals(userAgent, this.testServer.header.get("user-agent"));
        Assert.assertEquals(GET, this.testServer.method);
        Assert.assertEquals(HttpServerTest.URI, this.testServer.uri);
    }

    @Test
    public void testGetQueryParameterContainsSpace() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=bar%20baz HTTP/1.1"));
        Assert.assertEquals("Parameter count in URL and decodedParameters should match.", 1, this.testServer.decodedParamters.size());
        Assert.assertEquals("The query parameter value with space decoding incorrect", "bar baz", this.testServer.decodedParamters.get("foo").get(0));
    }

    @Test
    public void testGetQueryParameterContainsQuestionMark() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=bar%3F HTTP/1.1"));
        Assert.assertEquals("Parameter count in URL and decodedParameters should match.", 1, this.testServer.decodedParamters.size());
        Assert.assertEquals("The query parameter value with question mark decoding incorrect", "bar?", this.testServer.decodedParamters.get("foo").get(0));
    }

    @Test
    public void testGetQueryParameterContainsAmpersand() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=bar%26 HTTP/1.1"));
        Assert.assertEquals("Parameter count in URL and decodedParameters should match.", 1, this.testServer.decodedParamters.size());
        Assert.assertEquals("The query parameter value with ampersand decoding incorrect", "bar&", this.testServer.decodedParamters.get("foo").get(0));
    }

    @Test
    public void testGetQueryParameterContainsSpecialCharactersSingleFieldRepeated() {
        invokeServer((("GET " + (HttpServerTest.URI)) + "?foo=bar%20baz&foo=bar%3F&foo=bar%26 HTTP/1.1"));
        Assert.assertEquals("Parameter count in URL and decodedParameters should match.", 1, this.testServer.decodedParamters.size());
        String[] parametersAsArray = this.testServer.decodedParamters.get("foo").toArray(new String[0]);
        String[] expected = new String[]{ "bar baz", "bar?", "bar&" };
        Assert.assertArrayEquals("Repeated parameter not decoded correctly", expected, parametersAsArray);
    }
}

