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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.Ordered;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static AccessManager.PERMIT_ALL;


/**
 * Tests for {@link Dispatcher}.
 *
 * @author Phillip Webb
 */
public class DispatcherTests {
    @Mock
    private AccessManager accessManager;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private ServerHttpRequest serverRequest;

    private ServerHttpResponse serverResponse;

    @Test
    public void accessManagerMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Dispatcher(null, Collections.emptyList())).withMessageContaining("AccessManager must not be null");
    }

    @Test
    public void mappersMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Dispatcher(this.accessManager, null)).withMessageContaining("Mappers must not be null");
    }

    @Test
    public void accessManagerVetoRequest() throws Exception {
        BDDMockito.given(this.accessManager.isAllowed(ArgumentMatchers.any(ServerHttpRequest.class))).willReturn(false);
        HandlerMapper mapper = Mockito.mock(HandlerMapper.class);
        Handler handler = Mockito.mock(Handler.class);
        BDDMockito.given(mapper.getHandler(ArgumentMatchers.any(ServerHttpRequest.class))).willReturn(handler);
        Dispatcher dispatcher = new Dispatcher(this.accessManager, Collections.singleton(mapper));
        dispatcher.handle(this.serverRequest, this.serverResponse);
        Mockito.verifyZeroInteractions(handler);
        assertThat(this.response.getStatus()).isEqualTo(403);
    }

    @Test
    public void accessManagerAllowRequest() throws Exception {
        BDDMockito.given(this.accessManager.isAllowed(ArgumentMatchers.any(ServerHttpRequest.class))).willReturn(true);
        HandlerMapper mapper = Mockito.mock(HandlerMapper.class);
        Handler handler = Mockito.mock(Handler.class);
        BDDMockito.given(mapper.getHandler(ArgumentMatchers.any(ServerHttpRequest.class))).willReturn(handler);
        Dispatcher dispatcher = new Dispatcher(this.accessManager, Collections.singleton(mapper));
        dispatcher.handle(this.serverRequest, this.serverResponse);
        Mockito.verify(handler).handle(this.serverRequest, this.serverResponse);
    }

    @Test
    public void ordersMappers() throws Exception {
        HandlerMapper mapper1 = Mockito.mock(HandlerMapper.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        HandlerMapper mapper2 = Mockito.mock(HandlerMapper.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        BDDMockito.given(getOrder()).willReturn(1);
        BDDMockito.given(getOrder()).willReturn(2);
        List<HandlerMapper> mappers = Arrays.asList(mapper2, mapper1);
        Dispatcher dispatcher = new Dispatcher(PERMIT_ALL, mappers);
        dispatcher.handle(this.serverRequest, this.serverResponse);
        InOrder inOrder = Mockito.inOrder(mapper1, mapper2);
        inOrder.verify(mapper1).getHandler(this.serverRequest);
        inOrder.verify(mapper2).getHandler(this.serverRequest);
    }
}

