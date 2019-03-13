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
import org.springframework.web.context.request.NativeWebRequest;


/**
 * Test fixture with {@link ExpressionValueMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class ExpressionValueMethodArgumentResolverTests {
    private ExpressionValueMethodArgumentResolver resolver;

    private MethodParameter paramSystemProperty;

    private MethodParameter paramContextPath;

    private MethodParameter paramNotSupported;

    private NativeWebRequest webRequest;

    @Test
    public void supportsParameter() throws Exception {
        Assert.assertTrue(resolver.supportsParameter(paramSystemProperty));
        Assert.assertTrue(resolver.supportsParameter(paramContextPath));
        Assert.assertFalse(resolver.supportsParameter(paramNotSupported));
    }

    @Test
    public void resolveSystemProperty() throws Exception {
        System.setProperty("systemProperty", "22");
        Object value = resolver.resolveArgument(paramSystemProperty, null, webRequest, null);
        System.clearProperty("systemProperty");
        Assert.assertEquals("22", value);
    }

    @Test
    public void resolveContextPath() throws Exception {
        webRequest.getNativeRequest(MockHttpServletRequest.class).setContextPath("/contextPath");
        Object value = resolver.resolveArgument(paramContextPath, null, webRequest, null);
        Assert.assertEquals("/contextPath", value);
    }
}

