/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.cors;


import HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import HttpHeaders.ACCESS_CONTROL_MAX_AGE;
import HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.ORIGIN;
import HttpHeaders.VARY;
import HttpMethod.GET;
import HttpMethod.OPTIONS;
import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_OK;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;


/**
 * Test {@link DefaultCorsProcessor} with simple or preflight CORS request.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class DefaultCorsProcessorTests {
    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private DefaultCorsProcessor processor;

    private CorsConfiguration conf;

    @Test
    public void actualRequestWithOriginHeader() throws Exception {
        this.request.setMethod(GET.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertFalse(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_FORBIDDEN, this.response.getStatus());
    }

    @Test
    public void actualRequestWithOriginHeaderAndNullConfig() throws Exception {
        this.request.setMethod(GET.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.processor.processRequest(null, this.request, this.response);
        Assert.assertFalse(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void actualRequestWithOriginHeaderAndAllowedOrigin() throws Exception {
        this.request.setMethod(GET.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.conf.addAllowedOrigin("*");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals("*", this.response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertFalse(this.response.containsHeader(ACCESS_CONTROL_MAX_AGE));
        Assert.assertFalse(this.response.containsHeader(ACCESS_CONTROL_EXPOSE_HEADERS));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void actualRequestCredentials() throws Exception {
        this.request.setMethod(GET.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.conf.addAllowedOrigin("http://domain1.com");
        this.conf.addAllowedOrigin("http://domain2.com");
        this.conf.addAllowedOrigin("http://domain3.com");
        this.conf.setAllowCredentials(true);
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals("http://domain2.com", this.response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS));
        Assert.assertEquals("true", this.response.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void actualRequestCredentialsWithOriginWildcard() throws Exception {
        this.request.setMethod(GET.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.conf.addAllowedOrigin("*");
        this.conf.setAllowCredentials(true);
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals("http://domain2.com", this.response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS));
        Assert.assertEquals("true", this.response.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void actualRequestCaseInsensitiveOriginMatch() throws Exception {
        this.request.setMethod(GET.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.conf.addAllowedOrigin("http://DOMAIN2.com");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void actualRequestExposedHeaders() throws Exception {
        this.request.setMethod(GET.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.conf.addExposedHeader("header1");
        this.conf.addExposedHeader("header2");
        this.conf.addAllowedOrigin("http://domain2.com");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals("http://domain2.com", this.response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_EXPOSE_HEADERS));
        Assert.assertTrue(this.response.getHeader(ACCESS_CONTROL_EXPOSE_HEADERS).contains("header1"));
        Assert.assertTrue(this.response.getHeader(ACCESS_CONTROL_EXPOSE_HEADERS).contains("header2"));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void preflightRequestAllOriginsAllowed() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.conf.addAllowedOrigin("*");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void preflightRequestWrongAllowedMethod() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "DELETE");
        this.conf.addAllowedOrigin("*");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_FORBIDDEN, this.response.getStatus());
    }

    @Test
    public void preflightRequestMatchedAllowedMethod() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.conf.addAllowedOrigin("*");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertEquals(SC_OK, this.response.getStatus());
        Assert.assertEquals("GET,HEAD", this.response.getHeader(ACCESS_CONTROL_ALLOW_METHODS));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
    }

    @Test
    public void preflightRequestTestWithOriginButWithoutOtherHeaders() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertFalse(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_FORBIDDEN, this.response.getStatus());
    }

    @Test
    public void preflightRequestWithoutRequestMethod() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "Header1");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertFalse(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_FORBIDDEN, this.response.getStatus());
    }

    @Test
    public void preflightRequestWithRequestAndMethodHeaderButNoConfig() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "Header1");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertFalse(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_FORBIDDEN, this.response.getStatus());
    }

    @Test
    public void preflightRequestValidRequestAndConfig() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "Header1");
        this.conf.addAllowedOrigin("*");
        this.conf.addAllowedMethod("GET");
        this.conf.addAllowedMethod("PUT");
        this.conf.addAllowedHeader("header1");
        this.conf.addAllowedHeader("header2");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals("*", this.response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_METHODS));
        Assert.assertEquals("GET,PUT", this.response.getHeader(ACCESS_CONTROL_ALLOW_METHODS));
        Assert.assertFalse(this.response.containsHeader(ACCESS_CONTROL_MAX_AGE));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void preflightRequestCredentials() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "Header1");
        this.conf.addAllowedOrigin("http://domain1.com");
        this.conf.addAllowedOrigin("http://domain2.com");
        this.conf.addAllowedOrigin("http://domain3.com");
        this.conf.addAllowedHeader("Header1");
        this.conf.setAllowCredentials(true);
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals("http://domain2.com", this.response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS));
        Assert.assertEquals("true", this.response.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void preflightRequestCredentialsWithOriginWildcard() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "Header1");
        this.conf.addAllowedOrigin("http://domain1.com");
        this.conf.addAllowedOrigin("*");
        this.conf.addAllowedOrigin("http://domain3.com");
        this.conf.addAllowedHeader("Header1");
        this.conf.setAllowCredentials(true);
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals("http://domain2.com", this.response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void preflightRequestAllowedHeaders() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "Header1, Header2");
        this.conf.addAllowedHeader("Header1");
        this.conf.addAllowedHeader("Header2");
        this.conf.addAllowedHeader("Header3");
        this.conf.addAllowedOrigin("http://domain2.com");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_HEADERS));
        Assert.assertTrue(this.response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS).contains("Header1"));
        Assert.assertTrue(this.response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS).contains("Header2"));
        Assert.assertFalse(this.response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS).contains("Header3"));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void preflightRequestAllowsAllHeaders() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "Header1, Header2");
        this.conf.addAllowedHeader("*");
        this.conf.addAllowedOrigin("http://domain2.com");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_HEADERS));
        Assert.assertTrue(this.response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS).contains("Header1"));
        Assert.assertTrue(this.response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS).contains("Header2"));
        Assert.assertFalse(this.response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS).contains("*"));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void preflightRequestWithEmptyHeaders() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "");
        this.conf.addAllowedHeader("*");
        this.conf.addAllowedOrigin("http://domain2.com");
        this.processor.processRequest(this.conf, this.request, this.response);
        Assert.assertTrue(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertFalse(this.response.containsHeader(ACCESS_CONTROL_ALLOW_HEADERS));
        Assert.assertThat(this.response.getHeaders(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
        Assert.assertEquals(SC_OK, this.response.getStatus());
    }

    @Test
    public void preflightRequestWithNullConfig() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.conf.addAllowedOrigin("*");
        this.processor.processRequest(null, this.request, this.response);
        Assert.assertFalse(this.response.containsHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals(SC_FORBIDDEN, this.response.getStatus());
    }
}

