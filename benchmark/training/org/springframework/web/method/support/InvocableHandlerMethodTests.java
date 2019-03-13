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
package org.springframework.web.method.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;


/**
 * Unit tests for {@link InvocableHandlerMethod}.
 *
 * @author Rossen Stoyanchev
 */
public class InvocableHandlerMethodTests {
    private NativeWebRequest request;

    private final HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();

    @Test
    public void resolveArg() throws Exception {
        this.composite.addResolver(new StubArgumentResolver(99));
        this.composite.addResolver(new StubArgumentResolver("value"));
        Object value = getInvocable(Integer.class, String.class).invokeForRequest(request, null);
        Assert.assertEquals(1, getStubResolver(0).getResolvedParameters().size());
        Assert.assertEquals(1, getStubResolver(1).getResolvedParameters().size());
        Assert.assertEquals("99-value", value);
        Assert.assertEquals("intArg", getStubResolver(0).getResolvedParameters().get(0).getParameterName());
        Assert.assertEquals("stringArg", getStubResolver(1).getResolvedParameters().get(0).getParameterName());
    }

    @Test
    public void resolveNoArgValue() throws Exception {
        this.composite.addResolver(new StubArgumentResolver(Integer.class));
        this.composite.addResolver(new StubArgumentResolver(String.class));
        Object returnValue = getInvocable(Integer.class, String.class).invokeForRequest(request, null);
        Assert.assertEquals(1, getStubResolver(0).getResolvedParameters().size());
        Assert.assertEquals(1, getStubResolver(1).getResolvedParameters().size());
        Assert.assertEquals("null-null", returnValue);
    }

    @Test
    public void cannotResolveArg() throws Exception {
        try {
            getInvocable(Integer.class, String.class).invokeForRequest(request, null);
            Assert.fail("Expected exception");
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("Could not resolve parameter [0]"));
        }
    }

    @Test
    public void resolveProvidedArg() throws Exception {
        Object value = getInvocable(Integer.class, String.class).invokeForRequest(request, null, 99, "value");
        Assert.assertNotNull(value);
        Assert.assertEquals(String.class, value.getClass());
        Assert.assertEquals("99-value", value);
    }

    @Test
    public void resolveProvidedArgFirst() throws Exception {
        this.composite.addResolver(new StubArgumentResolver(1));
        this.composite.addResolver(new StubArgumentResolver("value1"));
        Object value = getInvocable(Integer.class, String.class).invokeForRequest(request, null, 2, "value2");
        Assert.assertEquals("2-value2", value);
    }

    @Test
    public void exceptionInResolvingArg() throws Exception {
        this.composite.addResolver(new InvocableHandlerMethodTests.ExceptionRaisingArgumentResolver());
        try {
            getInvocable(Integer.class, String.class).invokeForRequest(request, null);
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException ex) {
            // expected -  allow HandlerMethodArgumentResolver exceptions to propagate
        }
    }

    @Test
    public void illegalArgumentException() throws Exception {
        this.composite.addResolver(new StubArgumentResolver(Integer.class, "__not_an_int__"));
        this.composite.addResolver(new StubArgumentResolver("value"));
        try {
            getInvocable(Integer.class, String.class).invokeForRequest(request, null);
            Assert.fail("Expected exception");
        } catch (IllegalStateException ex) {
            Assert.assertNotNull("Exception not wrapped", ex.getCause());
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
            Assert.assertTrue(ex.getMessage().contains("Controller ["));
            Assert.assertTrue(ex.getMessage().contains("Method ["));
            Assert.assertTrue(ex.getMessage().contains("with argument values:"));
            Assert.assertTrue(ex.getMessage().contains("[0] [type=java.lang.String] [value=__not_an_int__]"));
            Assert.assertTrue(ex.getMessage().contains("[1] [type=java.lang.String] [value=value"));
        }
    }

    @Test
    public void invocationTargetException() throws Exception {
        Throwable expected = new RuntimeException("error");
        try {
            getInvocable(Throwable.class).invokeForRequest(this.request, null, expected);
            Assert.fail("Expected exception");
        } catch (RuntimeException actual) {
            Assert.assertSame(expected, actual);
        }
        expected = new Error("error");
        try {
            getInvocable(Throwable.class).invokeForRequest(this.request, null, expected);
            Assert.fail("Expected exception");
        } catch (Error actual) {
            Assert.assertSame(expected, actual);
        }
        expected = new Exception("error");
        try {
            getInvocable(Throwable.class).invokeForRequest(this.request, null, expected);
            Assert.fail("Expected exception");
        } catch (Exception actual) {
            Assert.assertSame(expected, actual);
        }
        expected = new Throwable("error");
        try {
            getInvocable(Throwable.class).invokeForRequest(this.request, null, expected);
            Assert.fail("Expected exception");
        } catch (IllegalStateException actual) {
            Assert.assertNotNull(actual.getCause());
            Assert.assertSame(expected, actual.getCause());
            Assert.assertTrue(actual.getMessage().contains("Invocation failure"));
        }
    }

    // SPR-13917
    @Test
    public void invocationErrorMessage() throws Exception {
        this.composite.addResolver(new StubArgumentResolver(double.class));
        try {
            getInvocable(double.class).invokeForRequest(this.request, null);
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
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            throw new IllegalArgumentException("oops, can't read");
        }
    }
}

