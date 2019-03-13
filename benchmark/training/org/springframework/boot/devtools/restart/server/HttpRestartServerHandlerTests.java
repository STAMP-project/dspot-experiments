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
package org.springframework.boot.devtools.restart.server;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;


/**
 * Tests for {@link HttpRestartServerHandler}.
 *
 * @author Phillip Webb
 */
public class HttpRestartServerHandlerTests {
    @Test
    public void serverMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpRestartServerHandler(null)).withMessageContaining("Server must not be null");
    }

    @Test
    public void handleDelegatesToServer() throws Exception {
        HttpRestartServer server = Mockito.mock(HttpRestartServer.class);
        HttpRestartServerHandler handler = new HttpRestartServerHandler(server);
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        ServerHttpResponse response = Mockito.mock(ServerHttpResponse.class);
        handler.handle(request, response);
        Mockito.verify(server).handle(request, response);
    }
}

