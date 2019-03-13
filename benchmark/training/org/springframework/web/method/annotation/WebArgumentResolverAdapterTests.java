/**
 * Copyright 2002-2013 the original author or authors.
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


import WebArgumentResolver.UNRESOLVED;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;


/**
 * Test fixture with {@link WebArgumentResolverAdapterTests}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class WebArgumentResolverAdapterTests {
    private WebArgumentResolverAdapterTests.TestWebArgumentResolverAdapter adapter;

    private WebArgumentResolver adaptee;

    private MethodParameter parameter;

    private NativeWebRequest webRequest;

    @Test
    public void supportsParameter() throws Exception {
        BDDMockito.given(adaptee.resolveArgument(parameter, webRequest)).willReturn(42);
        Assert.assertTrue("Parameter not supported", adapter.supportsParameter(parameter));
        Mockito.verify(adaptee).resolveArgument(parameter, webRequest);
    }

    @Test
    public void supportsParameterUnresolved() throws Exception {
        BDDMockito.given(adaptee.resolveArgument(parameter, webRequest)).willReturn(UNRESOLVED);
        Assert.assertFalse("Parameter supported", adapter.supportsParameter(parameter));
        Mockito.verify(adaptee).resolveArgument(parameter, webRequest);
    }

    @Test
    public void supportsParameterWrongType() throws Exception {
        BDDMockito.given(adaptee.resolveArgument(parameter, webRequest)).willReturn("Foo");
        Assert.assertFalse("Parameter supported", adapter.supportsParameter(parameter));
        Mockito.verify(adaptee).resolveArgument(parameter, webRequest);
    }

    @Test
    public void supportsParameterThrowsException() throws Exception {
        BDDMockito.given(adaptee.resolveArgument(parameter, webRequest)).willThrow(new Exception());
        Assert.assertFalse("Parameter supported", adapter.supportsParameter(parameter));
        Mockito.verify(adaptee).resolveArgument(parameter, webRequest);
    }

    @Test
    public void resolveArgument() throws Exception {
        int expected = 42;
        BDDMockito.given(adaptee.resolveArgument(parameter, webRequest)).willReturn(expected);
        Object result = adapter.resolveArgument(parameter, null, webRequest, null);
        Assert.assertEquals("Invalid result", expected, result);
    }

    @Test(expected = IllegalStateException.class)
    public void resolveArgumentUnresolved() throws Exception {
        BDDMockito.given(adaptee.resolveArgument(parameter, webRequest)).willReturn(UNRESOLVED);
        adapter.resolveArgument(parameter, null, webRequest, null);
    }

    @Test(expected = IllegalStateException.class)
    public void resolveArgumentWrongType() throws Exception {
        BDDMockito.given(adaptee.resolveArgument(parameter, webRequest)).willReturn("Foo");
        adapter.resolveArgument(parameter, null, webRequest, null);
    }

    @Test(expected = Exception.class)
    public void resolveArgumentThrowsException() throws Exception {
        BDDMockito.given(adaptee.resolveArgument(parameter, webRequest)).willThrow(new Exception());
        adapter.resolveArgument(parameter, null, webRequest, null);
    }

    private class TestWebArgumentResolverAdapter extends AbstractWebArgumentResolverAdapter {
        public TestWebArgumentResolverAdapter(WebArgumentResolver adaptee) {
            super(adaptee);
        }

        @Override
        protected NativeWebRequest getWebRequest() {
            return WebArgumentResolverAdapterTests.this.webRequest;
        }
    }
}

