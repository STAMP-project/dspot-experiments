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


import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessagingPredicates;
import org.springframework.messaging.handler.invocation.ResolvableMethod;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;


/**
 * Test fixture for {@link HeaderMethodArgumentResolver} tests.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 4.0
 */
public class HeaderMethodArgumentResolverTests {
    private HeaderMethodArgumentResolver resolver;

    private final ResolvableMethod resolvable = ResolvableMethod.on(getClass()).named("handleMessage").build();

    @Test
    public void supportsParameter() {
        Assert.assertTrue(this.resolver.supportsParameter(this.resolvable.annot(MessagingPredicates.headerPlain()).arg()));
        Assert.assertFalse(this.resolver.supportsParameter(this.resolvable.annotNotPresent(Header.class).arg()));
    }

    @Test
    public void resolveArgument() throws Exception {
        Message<byte[]> message = setHeader("param1", "foo").build();
        Object result = this.resolver.resolveArgument(this.resolvable.annot(MessagingPredicates.headerPlain()).arg(), message);
        Assert.assertEquals("foo", result);
    }

    // SPR-11326
    @Test
    public void resolveArgumentNativeHeader() throws Exception {
        HeaderMethodArgumentResolverTests.TestMessageHeaderAccessor headers = new HeaderMethodArgumentResolverTests.TestMessageHeaderAccessor();
        setNativeHeader("param1", "foo");
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();
        Assert.assertEquals("foo", this.resolver.resolveArgument(this.resolvable.annot(MessagingPredicates.headerPlain()).arg(), message));
    }

    @Test
    public void resolveArgumentNativeHeaderAmbiguity() throws Exception {
        HeaderMethodArgumentResolverTests.TestMessageHeaderAccessor headers = new HeaderMethodArgumentResolverTests.TestMessageHeaderAccessor();
        setHeader("param1", "foo");
        setNativeHeader("param1", "native-foo");
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();
        Assert.assertEquals("foo", this.resolver.resolveArgument(this.resolvable.annot(MessagingPredicates.headerPlain()).arg(), message));
        Assert.assertEquals("native-foo", this.resolver.resolveArgument(this.resolvable.annot(MessagingPredicates.header("nativeHeaders.param1")).arg(), message));
    }

    @Test(expected = MessageHandlingException.class)
    public void resolveArgumentNotFound() throws Exception {
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).build();
        this.resolver.resolveArgument(this.resolvable.annot(MessagingPredicates.headerPlain()).arg(), message);
    }

    @Test
    public void resolveArgumentDefaultValue() throws Exception {
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).build();
        Object result = this.resolver.resolveArgument(this.resolvable.annot(MessagingPredicates.header("name", "bar")).arg(), message);
        Assert.assertEquals("bar", result);
    }

    @Test
    public void resolveDefaultValueSystemProperty() throws Exception {
        System.setProperty("systemProperty", "sysbar");
        try {
            Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).build();
            MethodParameter param = this.resolvable.annot(MessagingPredicates.header("name", "#{systemProperties.systemProperty}")).arg();
            Object result = resolver.resolveArgument(param, message);
            Assert.assertEquals("sysbar", result);
        } finally {
            System.clearProperty("systemProperty");
        }
    }

    @Test
    public void resolveNameFromSystemProperty() throws Exception {
        System.setProperty("systemProperty", "sysbar");
        try {
            Message<byte[]> message = setHeader("sysbar", "foo").build();
            MethodParameter param = this.resolvable.annot(MessagingPredicates.header("#{systemProperties.systemProperty}")).arg();
            Object result = resolver.resolveArgument(param, message);
            Assert.assertEquals("foo", result);
        } finally {
            System.clearProperty("systemProperty");
        }
    }

    @Test
    public void resolveOptionalHeaderWithValue() throws Exception {
        Message<String> message = setHeader("foo", "bar").build();
        MethodParameter param = this.resolvable.annot(MessagingPredicates.header("foo")).arg(Optional.class, String.class);
        Object result = resolver.resolveArgument(param, message);
        Assert.assertEquals(Optional.of("bar"), result);
    }

    @Test
    public void resolveOptionalHeaderAsEmpty() throws Exception {
        Message<String> message = MessageBuilder.withPayload("foo").build();
        MethodParameter param = this.resolvable.annot(MessagingPredicates.header("foo")).arg(Optional.class, String.class);
        Object result = resolver.resolveArgument(param, message);
        Assert.assertEquals(Optional.empty(), result);
    }

    public static class TestMessageHeaderAccessor extends NativeMessageHeaderAccessor {
        TestMessageHeaderAccessor() {
            super(((Map<String, List<String>>) (null)));
        }
    }
}

