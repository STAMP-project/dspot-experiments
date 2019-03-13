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
package org.springframework.messaging.handler.invocation.reactive;


import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.messaging.handler.invocation.ResolvableMethod;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link InvocableHandlerMethod}.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class InvocableHandlerMethodTests {
    private final Message<?> message = Mockito.mock(Message.class);

    private final List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

    @Test
    public void resolveArg() {
        this.resolvers.add(new StubArgumentResolver(99));
        this.resolvers.add(new StubArgumentResolver("value"));
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
        Object value = invokeAndBlock(new InvocableHandlerMethodTests.Handler(), method);
        Assert.assertEquals(1, getStubResolver(0).getResolvedParameters().size());
        Assert.assertEquals(1, getStubResolver(1).getResolvedParameters().size());
        Assert.assertEquals("99-value", value);
        Assert.assertEquals("intArg", getStubResolver(0).getResolvedParameters().get(0).getParameterName());
        Assert.assertEquals("stringArg", getStubResolver(1).getResolvedParameters().get(0).getParameterName());
    }

    @Test
    public void resolveNoArgValue() {
        this.resolvers.add(new StubArgumentResolver(Integer.class));
        this.resolvers.add(new StubArgumentResolver(String.class));
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
        Object value = invokeAndBlock(new InvocableHandlerMethodTests.Handler(), method);
        Assert.assertEquals(1, getStubResolver(0).getResolvedParameters().size());
        Assert.assertEquals(1, getStubResolver(1).getResolvedParameters().size());
        Assert.assertEquals("null-null", value);
    }

    @Test
    public void cannotResolveArg() {
        try {
            Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
            invokeAndBlock(new InvocableHandlerMethodTests.Handler(), method);
            Assert.fail("Expected exception");
        } catch (MethodArgumentResolutionException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertTrue(ex.getMessage().contains("Could not resolve parameter [0]"));
        }
    }

    @Test
    public void resolveProvidedArg() {
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
        Object value = invokeAndBlock(new InvocableHandlerMethodTests.Handler(), method, 99, "value");
        Assert.assertNotNull(value);
        Assert.assertEquals(String.class, value.getClass());
        Assert.assertEquals("99-value", value);
    }

    @Test
    public void resolveProvidedArgFirst() {
        this.resolvers.add(new StubArgumentResolver(1));
        this.resolvers.add(new StubArgumentResolver("value1"));
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
        Object value = invokeAndBlock(new InvocableHandlerMethodTests.Handler(), method, 2, "value2");
        Assert.assertEquals("2-value2", value);
    }

    @Test
    public void exceptionInResolvingArg() {
        this.resolvers.add(new InvocableHandlerMethodTests.ExceptionRaisingArgumentResolver());
        try {
            Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
            invokeAndBlock(new InvocableHandlerMethodTests.Handler(), method);
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException ex) {
            // expected -  allow HandlerMethodArgumentResolver exceptions to propagate
        }
    }

    @Test
    public void illegalArgumentException() {
        this.resolvers.add(new StubArgumentResolver(Integer.class, "__not_an_int__"));
        this.resolvers.add(new StubArgumentResolver("value"));
        try {
            Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
            invokeAndBlock(new InvocableHandlerMethodTests.Handler(), method);
            Assert.fail("Expected exception");
        } catch (IllegalStateException ex) {
            Assert.assertNotNull("Exception not wrapped", ex.getCause());
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
            Assert.assertTrue(ex.getMessage().contains("Endpoint ["));
            Assert.assertTrue(ex.getMessage().contains("Method ["));
            Assert.assertTrue(ex.getMessage().contains("with argument values:"));
            Assert.assertTrue(ex.getMessage().contains("[0] [type=java.lang.String] [value=__not_an_int__]"));
            Assert.assertTrue(ex.getMessage().contains("[1] [type=java.lang.String] [value=value"));
        }
    }

    @Test
    public void invocationTargetException() {
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).argTypes(Throwable.class).resolveMethod();
        Throwable expected = new Throwable("error");
        Mono<Object> result = invoke(new InvocableHandlerMethodTests.Handler(), method, expected);
        StepVerifier.create(result).expectErrorSatisfies(( actual) -> assertSame(expected, actual)).verify();
    }

    @Test
    public void voidMethod() {
        this.resolvers.add(new StubArgumentResolver(double.class, 5.25));
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0.0)).method();
        InvocableHandlerMethodTests.Handler handler = new InvocableHandlerMethodTests.Handler();
        Object value = invokeAndBlock(handler, method);
        Assert.assertNull(value);
        Assert.assertEquals(1, getStubResolver(0).getResolvedParameters().size());
        Assert.assertEquals("5.25", handler.getResult());
        Assert.assertEquals("amount", getStubResolver(0).getResolvedParameters().get(0).getParameterName());
    }

    @Test
    public void voidMonoMethod() {
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(InvocableHandlerMethodTests.Handler::handleAsync).method();
        InvocableHandlerMethodTests.Handler handler = new InvocableHandlerMethodTests.Handler();
        Object value = invokeAndBlock(handler, method);
        Assert.assertNull(value);
        Assert.assertEquals("success", handler.getResult());
    }

    @SuppressWarnings({ "unused", "UnusedReturnValue", "SameParameterValue" })
    private static class Handler {
        private AtomicReference<String> result = new AtomicReference<>();

        public String getResult() {
            return this.result.get();
        }

        String handle(Integer intArg, String stringArg) {
            return (intArg + "-") + stringArg;
        }

        void handle(double amount) {
            this.result.set(String.valueOf(amount));
        }

        void handleWithException(Throwable ex) throws Throwable {
            throw ex;
        }

        Mono<Void> handleAsync() {
            return Mono.delay(Duration.ofMillis(100)).thenEmpty(Mono.defer(() -> {
                this.result.set("success");
                return Mono.empty();
            }));
        }
    }

    private static class ExceptionRaisingArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return true;
        }

        @Override
        public Mono<Object> resolveArgument(MethodParameter parameter, Message<?> message) {
            return Mono.error(new IllegalArgumentException("oops, can't read"));
        }
    }
}

