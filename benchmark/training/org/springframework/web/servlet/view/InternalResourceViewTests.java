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
package org.springframework.web.servlet.view;


import View.PATH_VARIABLES;
import WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockRequestDispatcher;
import org.springframework.mock.web.test.MockServletContext;


/**
 * Unit tests for {@link InternalResourceView}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public class InternalResourceViewTests {
    @SuppressWarnings("serial")
    private static final Map<String, Object> model = Collections.unmodifiableMap(new HashMap<String, Object>() {
        {
            put("foo", "bar");
            put("I", 1L);
        }
    });

    private static final String url = "forward-to";

    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private final MockHttpServletResponse response = new MockHttpServletResponse();

    private final InternalResourceView view = new InternalResourceView();

    /**
     * If the url property isn't supplied, view initialization should fail.
     */
    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullUrl() throws Exception {
        view.afterPropertiesSet();
    }

    @Test
    public void forward() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myservlet/handler.do");
        request.setContextPath("/mycontext");
        request.setServletPath("/myservlet");
        request.setPathInfo(";mypathinfo");
        request.setQueryString("?param1=value1");
        view.setUrl(InternalResourceViewTests.url);
        view.setServletContext(new MockServletContext() {
            @Override
            public int getMinorVersion() {
                return 4;
            }
        });
        view.render(InternalResourceViewTests.model, request, response);
        Assert.assertEquals(InternalResourceViewTests.url, response.getForwardedUrl());
        InternalResourceViewTests.model.forEach(( key, value) -> Assert.assertEquals((("Values for model key '" + key) + "' must match"), value, request.getAttribute(key)));
    }

    @Test
    public void alwaysInclude() throws Exception {
        BDDMockito.given(request.getAttribute(PATH_VARIABLES)).willReturn(null);
        BDDMockito.given(request.getRequestDispatcher(InternalResourceViewTests.url)).willReturn(new MockRequestDispatcher(InternalResourceViewTests.url));
        view.setUrl(InternalResourceViewTests.url);
        view.setAlwaysInclude(true);
        // Can now try multiple tests
        view.render(InternalResourceViewTests.model, request, response);
        Assert.assertEquals(InternalResourceViewTests.url, response.getIncludedUrl());
        InternalResourceViewTests.model.forEach(( key, value) -> Mockito.verify(request).setAttribute(key, value));
    }

    @Test
    public void includeOnAttribute() throws Exception {
        BDDMockito.given(request.getAttribute(PATH_VARIABLES)).willReturn(null);
        BDDMockito.given(request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE)).willReturn("somepath");
        BDDMockito.given(request.getRequestDispatcher(InternalResourceViewTests.url)).willReturn(new MockRequestDispatcher(InternalResourceViewTests.url));
        view.setUrl(InternalResourceViewTests.url);
        // Can now try multiple tests
        view.render(InternalResourceViewTests.model, request, response);
        Assert.assertEquals(InternalResourceViewTests.url, response.getIncludedUrl());
        InternalResourceViewTests.model.forEach(( key, value) -> Mockito.verify(request).setAttribute(key, value));
    }

    @Test
    public void includeOnCommitted() throws Exception {
        BDDMockito.given(request.getAttribute(PATH_VARIABLES)).willReturn(null);
        BDDMockito.given(request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE)).willReturn(null);
        BDDMockito.given(request.getRequestDispatcher(InternalResourceViewTests.url)).willReturn(new MockRequestDispatcher(InternalResourceViewTests.url));
        response.setCommitted(true);
        view.setUrl(InternalResourceViewTests.url);
        // Can now try multiple tests
        view.render(InternalResourceViewTests.model, request, response);
        Assert.assertEquals(InternalResourceViewTests.url, response.getIncludedUrl());
        InternalResourceViewTests.model.forEach(( k, v) -> Mockito.verify(request).setAttribute(k, v));
    }
}

