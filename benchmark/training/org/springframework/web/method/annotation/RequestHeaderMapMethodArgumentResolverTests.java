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


import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.NativeWebRequest;


/**
 * Text fixture with {@link RequestHeaderMapMethodArgumentResolver}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class RequestHeaderMapMethodArgumentResolverTests {
    private RequestHeaderMapMethodArgumentResolver resolver;

    private MethodParameter paramMap;

    private MethodParameter paramMultiValueMap;

    private MethodParameter paramHttpHeaders;

    private MethodParameter paramUnsupported;

    private NativeWebRequest webRequest;

    private MockHttpServletRequest request;

    @Test
    public void supportsParameter() {
        Assert.assertTrue("Map parameter not supported", resolver.supportsParameter(paramMap));
        Assert.assertTrue("MultiValueMap parameter not supported", resolver.supportsParameter(paramMultiValueMap));
        Assert.assertTrue("HttpHeaders parameter not supported", resolver.supportsParameter(paramHttpHeaders));
        Assert.assertFalse("non-@RequestParam map supported", resolver.supportsParameter(paramUnsupported));
    }

    @Test
    public void resolveMapArgument() throws Exception {
        String name = "foo";
        String value = "bar";
        Map<String, String> expected = Collections.singletonMap(name, value);
        request.addHeader(name, value);
        Object result = resolver.resolveArgument(paramMap, null, webRequest, null);
        Assert.assertTrue((result instanceof Map));
        Assert.assertEquals("Invalid result", expected, result);
    }

    @Test
    public void resolveMultiValueMapArgument() throws Exception {
        String name = "foo";
        String value1 = "bar";
        String value2 = "baz";
        request.addHeader(name, value1);
        request.addHeader(name, value2);
        MultiValueMap<String, String> expected = new org.springframework.util.LinkedMultiValueMap(1);
        expected.add(name, value1);
        expected.add(name, value2);
        Object result = resolver.resolveArgument(paramMultiValueMap, null, webRequest, null);
        Assert.assertTrue((result instanceof MultiValueMap));
        Assert.assertEquals("Invalid result", expected, result);
    }

    @Test
    public void resolveHttpHeadersArgument() throws Exception {
        String name = "foo";
        String value1 = "bar";
        String value2 = "baz";
        request.addHeader(name, value1);
        request.addHeader(name, value2);
        org.springframework.http.HttpHeaders expected = new org.springframework.http.HttpHeaders();
        expected.add(name, value1);
        expected.add(name, value2);
        Object result = resolver.resolveArgument(paramHttpHeaders, null, webRequest, null);
        Assert.assertTrue((result instanceof org.springframework.http.HttpHeaders));
        Assert.assertEquals("Invalid result", expected, result);
    }
}

