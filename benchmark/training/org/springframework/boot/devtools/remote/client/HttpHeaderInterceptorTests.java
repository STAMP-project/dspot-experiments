/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.devtools.remote.client;


import java.io.IOException;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * Tests for {@link HttpHeaderInterceptor}.
 *
 * @author Rob Winch
 * @since 1.3.0
 */
public class HttpHeaderInterceptorTests {
    private String name;

    private String value;

    private HttpHeaderInterceptor interceptor;

    private HttpRequest request;

    private byte[] body;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private ClientHttpResponse response;

    private MockHttpServletRequest httpRequest;

    @Test
    public void constructorNullHeaderName() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpHeaderInterceptor(null, this.value)).withMessageContaining("Name must not be empty");
    }

    @Test
    public void constructorEmptyHeaderName() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpHeaderInterceptor("", this.value)).withMessageContaining("Name must not be empty");
    }

    @Test
    public void constructorNullHeaderValue() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpHeaderInterceptor(this.name, null)).withMessageContaining("Value must not be empty");
    }

    @Test
    public void constructorEmptyHeaderValue() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpHeaderInterceptor(this.name, "")).withMessageContaining("Value must not be empty");
    }

    @Test
    public void intercept() throws IOException {
        ClientHttpResponse result = this.interceptor.intercept(this.request, this.body, this.execution);
        assertThat(this.request.getHeaders().getFirst(this.name)).isEqualTo(this.value);
        assertThat(result).isEqualTo(this.response);
    }
}

