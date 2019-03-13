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
package org.springframework.web.servlet.mvc.method.annotation;


import HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * Test fixture with {@link PathVariableMapMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class PathVariableMapMethodArgumentResolverTests {
    private PathVariableMapMethodArgumentResolver resolver;

    private ModelAndViewContainer mavContainer;

    private ServletWebRequest webRequest;

    private MockHttpServletRequest request;

    private MethodParameter paramMap;

    private MethodParameter paramNamedMap;

    private MethodParameter paramMapNoAnnot;

    @Test
    public void supportsParameter() {
        Assert.assertTrue(resolver.supportsParameter(paramMap));
        Assert.assertFalse(resolver.supportsParameter(paramNamedMap));
        Assert.assertFalse(resolver.supportsParameter(paramMapNoAnnot));
    }

    @Test
    public void resolveArgument() throws Exception {
        Map<String, String> uriTemplateVars = new HashMap<>();
        uriTemplateVars.put("name1", "value1");
        uriTemplateVars.put("name2", "value2");
        request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);
        Object result = resolver.resolveArgument(paramMap, mavContainer, webRequest, null);
        Assert.assertEquals(uriTemplateVars, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void resolveArgumentNoUriVars() throws Exception {
        Map<String, String> map = ((Map<String, String>) (resolver.resolveArgument(paramMap, mavContainer, webRequest, null)));
        Assert.assertEquals(Collections.emptyMap(), map);
    }
}

