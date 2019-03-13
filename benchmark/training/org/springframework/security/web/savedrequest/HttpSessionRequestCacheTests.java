/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.web.savedrequest;


import HttpSessionRequestCache.SAVED_REQUEST;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 *
 *
 * @author Luke Taylor
 * @author Edd? Mel?ndez
 * @since 3.0
 */
public class HttpSessionRequestCacheTests {
    @Test
    public void originalGetRequestDoesntMatchIncomingPost() {
        HttpSessionRequestCache cache = new HttpSessionRequestCache();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/destination");
        MockHttpServletResponse response = new MockHttpServletResponse();
        cache.saveRequest(request, response);
        assertThat(request.getSession().getAttribute(SAVED_REQUEST)).isNotNull();
        assertThat(cache.getRequest(request, response)).isNotNull();
        MockHttpServletRequest newRequest = new MockHttpServletRequest("POST", "/destination");
        newRequest.setSession(request.getSession());
        assertThat(cache.getMatchingRequest(newRequest, response)).isNull();
    }

    @Test
    public void requestMatcherDefinesCorrectSubsetOfCachedRequests() throws Exception {
        HttpSessionRequestCache cache = new HttpSessionRequestCache();
        cache.setRequestMatcher(new RequestMatcher() {
            public boolean matches(HttpServletRequest request) {
                return request.getMethod().equals("GET");
            }
        });
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/destination");
        MockHttpServletResponse response = new MockHttpServletResponse();
        cache.saveRequest(request, response);
        assertThat(cache.getRequest(request, response)).isNull();
        assertThat(cache.getRequest(new MockHttpServletRequest(), new MockHttpServletResponse())).isNull();
        assertThat(cache.getMatchingRequest(request, response)).isNull();
    }

    // SEC-2246
    @Test
    public void getRequestCustomNoClassCastException() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/destination");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpSessionRequestCache cache = new HttpSessionRequestCache() {
            @Override
            public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
                request.getSession().setAttribute(SAVED_REQUEST, new HttpSessionRequestCacheTests.CustomSavedRequest(new DefaultSavedRequest(request, new PortResolverImpl())));
            }
        };
        cache.saveRequest(request, response);
        cache.saveRequest(request, response);
        assertThat(cache.getRequest(request, response)).isInstanceOf(HttpSessionRequestCacheTests.CustomSavedRequest.class);
    }

    @Test
    public void testCustomSessionAttrName() {
        HttpSessionRequestCache cache = new HttpSessionRequestCache();
        cache.setSessionAttrName("CUSTOM_SAVED_REQUEST");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/destination");
        MockHttpServletResponse response = new MockHttpServletResponse();
        cache.saveRequest(request, response);
        assertThat(request.getSession().getAttribute(SAVED_REQUEST)).isNull();
        assertThat(request.getSession().getAttribute("CUSTOM_SAVED_REQUEST")).isNotNull();
    }

    private static final class CustomSavedRequest implements SavedRequest {
        private final SavedRequest delegate;

        private CustomSavedRequest(SavedRequest delegate) {
            this.delegate = delegate;
        }

        public String getRedirectUrl() {
            return delegate.getRedirectUrl();
        }

        public List<Cookie> getCookies() {
            return delegate.getCookies();
        }

        public String getMethod() {
            return delegate.getMethod();
        }

        public List<String> getHeaderValues(String name) {
            return delegate.getHeaderValues(name);
        }

        public Collection<String> getHeaderNames() {
            return delegate.getHeaderNames();
        }

        public List<Locale> getLocales() {
            return delegate.getLocales();
        }

        public String[] getParameterValues(String name) {
            return delegate.getParameterValues(name);
        }

        public Map<String, String[]> getParameterMap() {
            return delegate.getParameterMap();
        }

        private static final long serialVersionUID = 2426831999233621470L;
    }
}

