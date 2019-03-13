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
package org.springframework.boot.devtools.remote.server;


import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Tests for {@link HttpStatusHandler}.
 *
 * @author Phillip Webb
 */
public class HttpStatusHandlerTests {
    private MockHttpServletRequest servletRequest;

    private MockHttpServletResponse servletResponse;

    private ServerHttpResponse response;

    private ServerHttpRequest request;

    @Test
    public void statusMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpStatusHandler(null)).withMessageContaining("Status must not be null");
    }

    @Test
    public void respondsOk() throws Exception {
        HttpStatusHandler handler = new HttpStatusHandler();
        handler.handle(this.request, this.response);
        assertThat(this.servletResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void respondsWithStatus() throws Exception {
        HttpStatusHandler handler = new HttpStatusHandler(HttpStatus.I_AM_A_TEAPOT);
        handler.handle(this.request, this.response);
        assertThat(this.servletResponse.getStatus()).isEqualTo(418);
    }
}

