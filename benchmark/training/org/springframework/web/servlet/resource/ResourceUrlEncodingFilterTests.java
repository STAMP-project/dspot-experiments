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
package org.springframework.web.servlet.resource;


import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;


/**
 * Unit tests for {@code ResourceUrlEncodingFilter}.
 *
 * @author Brian Clozel
 */
public class ResourceUrlEncodingFilterTests {
    private ResourceUrlEncodingFilter filter;

    private ResourceUrlProvider urlProvider;

    @Test
    public void encodeURL() throws Exception {
        testEncodeUrl(new MockHttpServletRequest("GET", "/"), "/resources/bar.css", "/resources/bar-11e16cf79faee7ac698c805cf28248d2.css");
    }

    @Test
    public void encodeUrlWithContext() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/context/foo");
        request.setContextPath("/context");
        testEncodeUrl(request, "/context/resources/bar.css", "/context/resources/bar-11e16cf79faee7ac698c805cf28248d2.css");
    }

    @Test
    public void encodeUrlWithContextAndForwardedRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/context/foo");
        request.setContextPath("/context");
        this.filter.doFilter(request, new MockHttpServletResponse(), ( req, res) -> {
            req.setAttribute(ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR, this.urlProvider);
            request.setRequestURI("/forwarded");
            request.setContextPath("/");
            String result = ((javax.servlet.http.HttpServletResponse) (res)).encodeURL("/context/resources/bar.css");
            assertEquals("/context/resources/bar-11e16cf79faee7ac698c805cf28248d2.css", result);
        });
    }

    // SPR-13757
    @Test
    public void encodeContextPathUrlWithoutSuffix() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/context");
        request.setContextPath("/context");
        testEncodeUrl(request, "/context/resources/bar.css", "/context/resources/bar-11e16cf79faee7ac698c805cf28248d2.css");
    }

    @Test
    public void encodeContextPathUrlWithSuffix() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/context/");
        request.setContextPath("/context");
        testEncodeUrl(request, "/context/resources/bar.css", "/context/resources/bar-11e16cf79faee7ac698c805cf28248d2.css");
    }

    // SPR-13018
    @Test
    public void encodeEmptyUrlWithContext() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/context/foo");
        request.setContextPath("/context");
        testEncodeUrl(request, "?foo=1", "?foo=1");
    }

    // SPR-13374
    @Test
    public void encodeUrlWithRequestParams() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        request.setContextPath("/");
        testEncodeUrl(request, "/resources/bar.css?foo=bar&url=http://example.org", "/resources/bar-11e16cf79faee7ac698c805cf28248d2.css?foo=bar&url=http://example.org");
    }

    // SPR-13847
    @Test
    public void encodeUrlPreventStringOutOfBounds() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/context-path/index");
        request.setContextPath("/context-path");
        request.setServletPath("");
        testEncodeUrl(request, "index?key=value", "index?key=value");
    }

    // SPR-17535
    @Test
    public void encodeUrlWithFragment() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        request.setContextPath("/");
        testEncodeUrl(request, "/resources/bar.css#something", "/resources/bar-11e16cf79faee7ac698c805cf28248d2.css#something");
        testEncodeUrl(request, "/resources/bar.css?foo=bar&url=http://example.org#something", "/resources/bar-11e16cf79faee7ac698c805cf28248d2.css?foo=bar&url=http://example.org#something");
    }
}

