/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client;


import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.Request;
import com.linecorp.armeria.common.metric.NoopMeterRegistry;
import io.netty.channel.EventLoop;
import io.netty.util.AttributeKey;
import org.junit.Test;
import org.mockito.Mockito;

import static ClientOptions.DEFAULT;
import static com.linecorp.armeria.common.SessionProtocol.H2C;


public class DefaultClientRequestContextTest {
    @Test
    public void deriveContext() {
        final DefaultClientRequestContext originalCtx = new DefaultClientRequestContext(Mockito.mock(EventLoop.class), NoopMeterRegistry.get(), H2C, Endpoint.of("example.com", 8080), HttpMethod.POST, "/foo", null, null, DEFAULT, Mockito.mock(Request.class));
        DefaultClientRequestContextTest.setAdditionalHeaders(originalCtx);
        final AttributeKey<String> foo = AttributeKey.valueOf(DefaultClientRequestContextTest.class, "foo");
        originalCtx.attr(foo).set("foo");
        final Request newRequest = Mockito.mock(Request.class);
        final ClientRequestContext derivedCtx = originalCtx.newDerivedContext(newRequest);
        assertThat(derivedCtx.endpoint()).isSameAs(originalCtx.endpoint());
        assertThat(derivedCtx.sessionProtocol()).isSameAs(originalCtx.sessionProtocol());
        assertThat(derivedCtx.method()).isSameAs(originalCtx.method());
        assertThat(derivedCtx.options()).isSameAs(originalCtx.options());
        assertThat(derivedCtx.<Request>request()).isSameAs(newRequest);
        assertThat(derivedCtx.path()).isEqualTo(originalCtx.path());
        assertThat(derivedCtx.maxResponseLength()).isEqualTo(originalCtx.maxResponseLength());
        assertThat(derivedCtx.responseTimeoutMillis()).isEqualTo(originalCtx.responseTimeoutMillis());
        assertThat(derivedCtx.writeTimeoutMillis()).isEqualTo(originalCtx.writeTimeoutMillis());
        assertThat(derivedCtx.additionalRequestHeaders().get(com.linecorp.armeria.common.HttpHeaderNames.of("my-header#1"))).isNull();
        assertThat(derivedCtx.additionalRequestHeaders().get(com.linecorp.armeria.common.HttpHeaderNames.of("my-header#2"))).isEqualTo("value#2");
        assertThat(derivedCtx.additionalRequestHeaders().get(com.linecorp.armeria.common.HttpHeaderNames.of("my-header#3"))).isEqualTo("value#3");
        assertThat(derivedCtx.additionalRequestHeaders().get(com.linecorp.armeria.common.HttpHeaderNames.of("my-header#4"))).isEqualTo("value#4");
        // the attribute is derived as well
        assertThat(derivedCtx.attr(foo).get()).isEqualTo("foo");
        // log is different
        assertThat(derivedCtx.log()).isNotSameAs(originalCtx.log());
        final AttributeKey<String> bar = AttributeKey.valueOf(DefaultClientRequestContextTest.class, "bar");
        originalCtx.attr(bar).set("bar");
        // the Attribute added to the original context after creation is not propagated to the derived context
        assertThat(derivedCtx.attr(bar).get()).isEqualTo(null);
    }
}

