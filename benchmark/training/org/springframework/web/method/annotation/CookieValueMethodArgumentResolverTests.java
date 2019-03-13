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
package org.springframework.web.method.annotation;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;


/**
 * Test fixture with {@link org.springframework.web.method.annotation.AbstractCookieValueMethodArgumentResolver}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class CookieValueMethodArgumentResolverTests {
    private AbstractCookieValueMethodArgumentResolver resolver;

    private MethodParameter paramNamedCookie;

    private MethodParameter paramNamedDefaultValueString;

    private MethodParameter paramString;

    private ServletWebRequest webRequest;

    private MockHttpServletRequest request;

    @Test
    public void supportsParameter() {
        Assert.assertTrue("Cookie parameter not supported", resolver.supportsParameter(paramNamedCookie));
        Assert.assertTrue("Cookie string parameter not supported", resolver.supportsParameter(paramNamedDefaultValueString));
        Assert.assertFalse("non-@CookieValue parameter supported", resolver.supportsParameter(paramString));
    }

    @Test
    public void resolveCookieDefaultValue() throws Exception {
        Object result = resolver.resolveArgument(paramNamedDefaultValueString, null, webRequest, null);
        Assert.assertTrue((result instanceof String));
        Assert.assertEquals("Invalid result", "bar", result);
    }

    @Test(expected = ServletRequestBindingException.class)
    public void notFound() throws Exception {
        resolver.resolveArgument(paramNamedCookie, null, webRequest, null);
        Assert.fail("Expected exception");
    }

    private static class TestCookieValueMethodArgumentResolver extends AbstractCookieValueMethodArgumentResolver {
        public TestCookieValueMethodArgumentResolver() {
            super(null);
        }

        @Override
        protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
            return null;
        }
    }
}

