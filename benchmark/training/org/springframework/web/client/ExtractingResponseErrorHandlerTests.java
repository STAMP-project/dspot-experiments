/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.web.client;


import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.I_AM_A_TEAPOT;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import HttpStatus.Series.CLIENT_ERROR;
import MediaType.APPLICATION_JSON;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.client.ClientHttpResponse;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class ExtractingResponseErrorHandlerTests {
    private ExtractingResponseErrorHandler errorHandler;

    private final ClientHttpResponse response = Mockito.mock(ClientHttpResponse.class);

    @Test
    public void hasError() throws Exception {
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(I_AM_A_TEAPOT.value());
        Assert.assertTrue(this.errorHandler.hasError(this.response));
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(INTERNAL_SERVER_ERROR.value());
        Assert.assertTrue(this.errorHandler.hasError(this.response));
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(OK.value());
        Assert.assertFalse(this.errorHandler.hasError(this.response));
    }

    @Test
    public void hasErrorOverride() throws Exception {
        this.errorHandler.setSeriesMapping(Collections.singletonMap(CLIENT_ERROR, null));
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(I_AM_A_TEAPOT.value());
        Assert.assertTrue(this.errorHandler.hasError(this.response));
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(NOT_FOUND.value());
        Assert.assertFalse(this.errorHandler.hasError(this.response));
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(OK.value());
        Assert.assertFalse(this.errorHandler.hasError(this.response));
    }

    @Test
    public void handleErrorStatusMatch() throws Exception {
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(I_AM_A_TEAPOT.value());
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        responseHeaders.setContentType(APPLICATION_JSON);
        BDDMockito.given(this.response.getHeaders()).willReturn(responseHeaders);
        byte[] body = "{\"foo\":\"bar\"}".getBytes(StandardCharsets.UTF_8);
        responseHeaders.setContentLength(body.length);
        BDDMockito.given(this.response.getBody()).willReturn(new ByteArrayInputStream(body));
        try {
            this.errorHandler.handleError(this.response);
            Assert.fail("MyRestClientException expected");
        } catch (ExtractingResponseErrorHandlerTests.MyRestClientException ex) {
            Assert.assertEquals("bar", ex.getFoo());
        }
    }

    @Test
    public void handleErrorSeriesMatch() throws Exception {
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(INTERNAL_SERVER_ERROR.value());
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        responseHeaders.setContentType(APPLICATION_JSON);
        BDDMockito.given(this.response.getHeaders()).willReturn(responseHeaders);
        byte[] body = "{\"foo\":\"bar\"}".getBytes(StandardCharsets.UTF_8);
        responseHeaders.setContentLength(body.length);
        BDDMockito.given(this.response.getBody()).willReturn(new ByteArrayInputStream(body));
        try {
            this.errorHandler.handleError(this.response);
            Assert.fail("MyRestClientException expected");
        } catch (ExtractingResponseErrorHandlerTests.MyRestClientException ex) {
            Assert.assertEquals("bar", ex.getFoo());
        }
    }

    @Test
    public void handleNoMatch() throws Exception {
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(NOT_FOUND.value());
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        responseHeaders.setContentType(APPLICATION_JSON);
        BDDMockito.given(this.response.getHeaders()).willReturn(responseHeaders);
        byte[] body = "{\"foo\":\"bar\"}".getBytes(StandardCharsets.UTF_8);
        responseHeaders.setContentLength(body.length);
        BDDMockito.given(this.response.getBody()).willReturn(new ByteArrayInputStream(body));
        try {
            this.errorHandler.handleError(this.response);
            Assert.fail("HttpClientErrorException expected");
        } catch (HttpClientErrorException ex) {
            Assert.assertEquals(NOT_FOUND, ex.getStatusCode());
            Assert.assertArrayEquals(body, ex.getResponseBodyAsByteArray());
        }
    }

    @Test
    public void handleNoMatchOverride() throws Exception {
        this.errorHandler.setSeriesMapping(Collections.singletonMap(CLIENT_ERROR, null));
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(NOT_FOUND.value());
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        responseHeaders.setContentType(APPLICATION_JSON);
        BDDMockito.given(this.response.getHeaders()).willReturn(responseHeaders);
        byte[] body = "{\"foo\":\"bar\"}".getBytes(StandardCharsets.UTF_8);
        responseHeaders.setContentLength(body.length);
        BDDMockito.given(this.response.getBody()).willReturn(new ByteArrayInputStream(body));
        this.errorHandler.handleError(this.response);
    }

    @SuppressWarnings("serial")
    private static class MyRestClientException extends RestClientException {
        private String foo;

        public MyRestClientException(String msg) {
            super(msg);
        }

        public MyRestClientException(String msg, Throwable ex) {
            super(msg, ex);
        }

        public String getFoo() {
            return this.foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }
}

