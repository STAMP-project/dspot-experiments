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
package org.springframework.security.web.authentication;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;


public class SavedRequestAwareAuthenticationSuccessHandlerTests {
    @Test
    public void defaultUrlMuststartWithSlashOrHttpScheme() {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/acceptableRelativeUrl");
        handler.setDefaultTargetUrl("http://some.site.org/index.html");
        handler.setDefaultTargetUrl("https://some.site.org/index.html");
        try {
            handler.setDefaultTargetUrl("missingSlash");
            fail("Shouldn't accept default target without leading slash");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void onAuthenticationSuccessHasSavedRequest() throws Exception {
        String redirectUrl = "http://localhost/appcontext/page";
        RedirectStrategy redirectStrategy = Mockito.mock(RedirectStrategy.class);
        RequestCache requestCache = Mockito.mock(RequestCache.class);
        SavedRequest savedRequest = Mockito.mock(SavedRequest.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Mockito.when(savedRequest.getRedirectUrl()).thenReturn(redirectUrl);
        Mockito.when(requestCache.getRequest(request, response)).thenReturn(savedRequest);
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setRequestCache(requestCache);
        handler.setRedirectStrategy(redirectStrategy);
        handler.onAuthenticationSuccess(request, response, Mockito.mock(Authentication.class));
        Mockito.verify(redirectStrategy).sendRedirect(request, response, redirectUrl);
    }
}

