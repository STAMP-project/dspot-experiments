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
package org.springframework.web.client;


import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import MediaType.TEXT_PLAIN;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;


/**
 * Unit tests for {@link DefaultResponseErrorHandler}.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Denys Ivano
 */
public class DefaultResponseErrorHandlerTests {
    private final DefaultResponseErrorHandler handler = new DefaultResponseErrorHandler();

    private final ClientHttpResponse response = Mockito.mock(ClientHttpResponse.class);

    @Test
    public void hasErrorTrue() throws Exception {
        BDDMockito.given(response.getRawStatusCode()).willReturn(NOT_FOUND.value());
        Assert.assertTrue(handler.hasError(response));
    }

    @Test
    public void hasErrorFalse() throws Exception {
        BDDMockito.given(response.getRawStatusCode()).willReturn(OK.value());
        Assert.assertFalse(handler.hasError(response));
    }

    @Test
    public void handleError() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        BDDMockito.given(response.getRawStatusCode()).willReturn(NOT_FOUND.value());
        BDDMockito.given(response.getStatusText()).willReturn("Not Found");
        BDDMockito.given(response.getHeaders()).willReturn(headers);
        BDDMockito.given(response.getBody()).willReturn(new ByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8)));
        try {
            handler.handleError(response);
            Assert.fail("expected HttpClientErrorException");
        } catch (HttpClientErrorException ex) {
            Assert.assertSame(headers, ex.getResponseHeaders());
        }
    }

    @Test(expected = HttpClientErrorException.class)
    public void handleErrorIOException() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        BDDMockito.given(response.getRawStatusCode()).willReturn(NOT_FOUND.value());
        BDDMockito.given(response.getStatusText()).willReturn("Not Found");
        BDDMockito.given(response.getHeaders()).willReturn(headers);
        BDDMockito.given(response.getBody()).willThrow(new IOException());
        handler.handleError(response);
    }

    @Test(expected = HttpClientErrorException.class)
    public void handleErrorNullResponse() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        BDDMockito.given(response.getRawStatusCode()).willReturn(NOT_FOUND.value());
        BDDMockito.given(response.getStatusText()).willReturn("Not Found");
        BDDMockito.given(response.getHeaders()).willReturn(headers);
        handler.handleError(response);
    }

    // SPR-16108
    @Test
    public void hasErrorForUnknownStatusCode() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        BDDMockito.given(response.getRawStatusCode()).willReturn(999);
        BDDMockito.given(response.getStatusText()).willReturn("Custom status code");
        BDDMockito.given(response.getHeaders()).willReturn(headers);
        Assert.assertFalse(handler.hasError(response));
    }

    // SPR-9406
    @Test(expected = UnknownHttpStatusCodeException.class)
    public void handleErrorUnknownStatusCode() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        BDDMockito.given(response.getRawStatusCode()).willReturn(999);
        BDDMockito.given(response.getStatusText()).willReturn("Custom status code");
        BDDMockito.given(response.getHeaders()).willReturn(headers);
        handler.handleError(response);
    }

    // SPR-17461
    @Test
    public void hasErrorForCustomClientError() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        BDDMockito.given(response.getRawStatusCode()).willReturn(499);
        BDDMockito.given(response.getStatusText()).willReturn("Custom status code");
        BDDMockito.given(response.getHeaders()).willReturn(headers);
        Assert.assertTrue(handler.hasError(response));
    }

    @Test(expected = UnknownHttpStatusCodeException.class)
    public void handleErrorForCustomClientError() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        BDDMockito.given(response.getRawStatusCode()).willReturn(499);
        BDDMockito.given(response.getStatusText()).willReturn("Custom status code");
        BDDMockito.given(response.getHeaders()).willReturn(headers);
        handler.handleError(response);
    }

    // SPR-17461
    @Test
    public void hasErrorForCustomServerError() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        BDDMockito.given(response.getRawStatusCode()).willReturn(599);
        BDDMockito.given(response.getStatusText()).willReturn("Custom status code");
        BDDMockito.given(response.getHeaders()).willReturn(headers);
        Assert.assertTrue(handler.hasError(response));
    }

    @Test(expected = UnknownHttpStatusCodeException.class)
    public void handleErrorForCustomServerError() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        BDDMockito.given(response.getRawStatusCode()).willReturn(599);
        BDDMockito.given(response.getStatusText()).willReturn("Custom status code");
        BDDMockito.given(response.getHeaders()).willReturn(headers);
        handler.handleError(response);
    }

    // SPR-16604
    @Test
    public void bodyAvailableAfterHasErrorForUnknownStatusCode() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        DefaultResponseErrorHandlerTests.TestByteArrayInputStream body = new DefaultResponseErrorHandlerTests.TestByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8));
        BDDMockito.given(response.getRawStatusCode()).willReturn(999);
        BDDMockito.given(response.getStatusText()).willReturn("Custom status code");
        BDDMockito.given(response.getHeaders()).willReturn(headers);
        BDDMockito.given(response.getBody()).willReturn(body);
        Assert.assertFalse(handler.hasError(response));
        Assert.assertFalse(body.isClosed());
        Assert.assertEquals("Hello World", StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
    }

    private static class TestByteArrayInputStream extends ByteArrayInputStream {
        private boolean closed;

        public TestByteArrayInputStream(byte[] buf) {
            super(buf);
            this.closed = false;
        }

        public boolean isClosed() {
            return closed;
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public synchronized void mark(int readlimit) {
            throw new UnsupportedOperationException("mark/reset not supported");
        }

        @Override
        public synchronized void reset() {
            throw new UnsupportedOperationException("mark/reset not supported");
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.closed = true;
        }
    }
}

