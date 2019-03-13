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


import MediaType.TEXT_PLAIN;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;


/**
 * Unit tests for {@link DefaultResponseErrorHandler} handling of specific
 * HTTP status codes.
 */
@RunWith(Parameterized.class)
public class DefaultResponseErrorHandlerHttpStatusTests {
    @Parameterized.Parameter
    public HttpStatus httpStatus;

    @Parameterized.Parameter(1)
    public Class<? extends Throwable> expectedExceptionClass;

    private final DefaultResponseErrorHandler handler = new DefaultResponseErrorHandler();

    private final ClientHttpResponse response = Mockito.mock(ClientHttpResponse.class);

    @Test
    public void hasErrorTrue() throws Exception {
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(this.httpStatus.value());
        Assert.assertTrue(this.handler.hasError(this.response));
    }

    @Test
    public void handleErrorException() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        BDDMockito.given(this.response.getRawStatusCode()).willReturn(this.httpStatus.value());
        BDDMockito.given(this.response.getHeaders()).willReturn(headers);
        try {
            this.handler.handleError(this.response);
            Assert.fail(("expected " + (this.expectedExceptionClass.getSimpleName())));
        } catch (HttpStatusCodeException ex) {
            Assert.assertEquals(("Expected " + (this.expectedExceptionClass.getSimpleName())), this.expectedExceptionClass, ex.getClass());
        }
    }
}

