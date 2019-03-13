/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.messaging.handler.annotation.support;


import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.invocation.ResolvableMethod;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;


/**
 * Test fixture for {@link HeadersMethodArgumentResolver} tests.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class HeadersMethodArgumentResolverTests {
    private final HeadersMethodArgumentResolver resolver = new HeadersMethodArgumentResolver();

    private Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).copyHeaders(Collections.singletonMap("foo", "bar")).build();

    private final ResolvableMethod resolvable = ResolvableMethod.on(getClass()).named("handleMessage").build();

    @Test
    public void supportsParameter() {
        Assert.assertTrue(this.resolver.supportsParameter(this.resolvable.annotPresent(Headers.class).arg(Map.class, String.class, Object.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.resolvable.arg(MessageHeaders.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.resolvable.arg(MessageHeaderAccessor.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.resolvable.arg(HeadersMethodArgumentResolverTests.TestMessageHeaderAccessor.class)));
        Assert.assertFalse(this.resolver.supportsParameter(this.resolvable.annotPresent(Headers.class).arg(String.class)));
    }

    @Test
    public void resolveArgumentAnnotated() throws Exception {
        MethodParameter param = this.resolvable.annotPresent(Headers.class).arg(Map.class, String.class, Object.class);
        Object resolved = this.resolver.resolveArgument(param, this.message);
        Assert.assertTrue((resolved instanceof Map));
        @SuppressWarnings("unchecked")
        Map<String, Object> headers = ((Map<String, Object>) (resolved));
        Assert.assertEquals("bar", headers.get("foo"));
    }

    @Test(expected = IllegalStateException.class)
    public void resolveArgumentAnnotatedNotMap() throws Exception {
        this.resolver.resolveArgument(this.resolvable.annotPresent(Headers.class).arg(String.class), this.message);
    }

    @Test
    public void resolveArgumentMessageHeaders() throws Exception {
        Object resolved = this.resolver.resolveArgument(this.resolvable.arg(MessageHeaders.class), this.message);
        Assert.assertTrue((resolved instanceof MessageHeaders));
        MessageHeaders headers = ((MessageHeaders) (resolved));
        Assert.assertEquals("bar", headers.get("foo"));
    }

    @Test
    public void resolveArgumentMessageHeaderAccessor() throws Exception {
        MethodParameter param = this.resolvable.arg(MessageHeaderAccessor.class);
        Object resolved = this.resolver.resolveArgument(param, this.message);
        Assert.assertTrue((resolved instanceof MessageHeaderAccessor));
        MessageHeaderAccessor headers = ((MessageHeaderAccessor) (resolved));
        Assert.assertEquals("bar", headers.getHeader("foo"));
    }

    @Test
    public void resolveArgumentMessageHeaderAccessorSubclass() throws Exception {
        MethodParameter param = this.resolvable.arg(HeadersMethodArgumentResolverTests.TestMessageHeaderAccessor.class);
        Object resolved = this.resolver.resolveArgument(param, this.message);
        Assert.assertTrue((resolved instanceof HeadersMethodArgumentResolverTests.TestMessageHeaderAccessor));
        HeadersMethodArgumentResolverTests.TestMessageHeaderAccessor headers = ((HeadersMethodArgumentResolverTests.TestMessageHeaderAccessor) (resolved));
        Assert.assertEquals("bar", getHeader("foo"));
    }

    public static class TestMessageHeaderAccessor extends NativeMessageHeaderAccessor {
        TestMessageHeaderAccessor(Message<?> message) {
            super(message);
        }

        public static HeadersMethodArgumentResolverTests.TestMessageHeaderAccessor wrap(Message<?> message) {
            return new HeadersMethodArgumentResolverTests.TestMessageHeaderAccessor(message);
        }
    }
}

