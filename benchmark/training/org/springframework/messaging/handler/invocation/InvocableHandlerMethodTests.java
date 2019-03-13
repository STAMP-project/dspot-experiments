/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.messaging.handler.invocation;


import java.lang.reflect.Method;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;


/**
 * Unit tests for {@link InvocableHandlerMethod}.
 *
 * @author Rossen Stoyanchev
 */
public class InvocableHandlerMethodTests {
    private final Message<?> message = Mockito.mock(Message.class);

    private final HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();

    @Test
    public void resolveArg() throws Exception {
        this.resolvers.addResolver(new StubArgumentResolver(99));
        this.resolvers.addResolver(new StubArgumentResolver("value"));
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
        Object value = invoke(new InvocableHandlerMethodTests.Handler(), method);
        Assert.assertEquals(1, getStubResolver(0).getResolvedParameters().size());
        Assert.assertEquals(1, getStubResolver(1).getResolvedParameters().size());
        Assert.assertEquals("99-value", value);
        Assert.assertEquals("intArg", getStubResolver(0).getResolvedParameters().get(0).getParameterName());
        Assert.assertEquals("stringArg", getStubResolver(1).getResolvedParameters().get(0).getParameterName());
    }

    @Test
    public void resolveNoArgValue() throws Exception {
        this.resolvers.addResolver(new StubArgumentResolver(Integer.class));
        this.resolvers.addResolver(new StubArgumentResolver(String.class));
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
        Object value = invoke(new InvocableHandlerMethodTests.Handler(), method);
        Assert.assertEquals(1, getStubResolver(0).getResolvedParameters().size());
        Assert.assertEquals(1, getStubResolver(1).getResolvedParameters().size());
        Assert.assertEquals("null-null", value);
    }

    @Test
    public void cannotResolveArg() throws Exception {
        try {
            Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
            invoke(new InvocableHandlerMethodTests.Handler(), method);
            Assert.fail("Expected exception");
        } catch (MethodArgumentResolutionException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertTrue(ex.getMessage().contains("Could not resolve parameter [0]"));
        }
    }

    @Test
    public void resolveProvidedArg() throws Exception {
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
        Object value = invoke(new InvocableHandlerMethodTests.Handler(), method, 99, "value");
        Assert.assertNotNull(value);
        Assert.assertEquals(String.class, value.getClass());
        Assert.assertEquals("99-value", value);
    }

    @Test
    public void resolveProvidedArgFirst() throws Exception {
        this.resolvers.addResolver(new StubArgumentResolver(1));
        this.resolvers.addResolver(new StubArgumentResolver("value1"));
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
        Object value = invoke(new InvocableHandlerMethodTests.Handler(), method, 2, "value2");
        Assert.assertEquals("2-value2", value);
    }

    @Test
    public void exceptionInResolvingArg() throws Exception {
        this.resolvers.addResolver(new InvocableHandlerMethodTests.ExceptionRaisingArgumentResolver());
        try {
            Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
            invoke(new InvocableHandlerMethodTests.Handler(), method);
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException ex) {
            // expected -  allow HandlerMethodArgumentResolver exceptions to propagate
        }
    }

    @Test
    public void illegalArgumentException() throws Exception {
        this.resolvers.addResolver(new StubArgumentResolver(Integer.class, "__not_an_int__"));
        this.resolvers.addResolver(new StubArgumentResolver("value"));
        try {
            Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0, "")).method();
            invoke(new InvocableHandlerMethodTests.Handler(), method);
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
    public void invocationTargetException() throws Exception {
        InvocableHandlerMethodTests.Handler handler = new InvocableHandlerMethodTests.Handler();
        Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).argTypes(Throwable.class).resolveMethod();
        Throwable expected = null;
        try {
            expected = new RuntimeException("error");
            invoke(handler, method, expected);
            Assert.fail("Expected exception");
        } catch (RuntimeException actual) {
            Assert.assertSame(expected, actual);
        }
        try {
            expected = new Error("error");
            invoke(handler, method, expected);
            Assert.fail("Expected exception");
        } catch (Error actual) {
            Assert.assertSame(expected, actual);
        }
        try {
            expected = new Exception("error");
            invoke(handler, method, expected);
            Assert.fail("Expected exception");
        } catch (Exception actual) {
            Assert.assertSame(expected, actual);
        }
        try {
            expected = new Throwable("error", expected);
            invoke(handler, method, expected);
            Assert.fail("Expected exception");
        } catch (IllegalStateException actual) {
            Assert.assertNotNull(actual.getCause());
            Assert.assertSame(expected, actual.getCause());
            Assert.assertTrue(actual.getMessage().contains("Invocation failure"));
        }
    }

    // Based on SPR-13917 (spring-web)
    @Test
    public void invocationErrorMessage() throws Exception {
        this.resolvers.addResolver(new StubArgumentResolver(double.class));
        try {
            Method method = ResolvableMethod.on(InvocableHandlerMethodTests.Handler.class).mockCall(( c) -> c.handle(0.0)).method();
            invoke(new InvocableHandlerMethodTests.Handler(), method);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertThat(ex.getMessage(), containsString("Illegal argument"));
        }
    }

    @SuppressWarnings("unused")
    private static class Handler {
        public String handle(Integer intArg, String stringArg) {
            return (intArg + "-") + stringArg;
        }

        public void handle(double amount) {
        }

        public void handleWithException(Throwable ex) throws Throwable {
            throw ex;
        }
    }

    private static class ExceptionRaisingArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return true;
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, Message<?> message) {
            throw new IllegalArgumentException("oops, can't read");
        }
    }
}

