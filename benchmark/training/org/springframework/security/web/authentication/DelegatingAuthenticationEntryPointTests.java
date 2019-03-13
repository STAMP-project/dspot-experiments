/**
 * Copyright 2010-2016 the original author or authors.
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


import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 * Test class for {@link DelegatingAuthenticationEntryPoint}
 *
 * @author Mike Wiesner
 * @since 3.0.2
 * @version $Id:$
 */
public class DelegatingAuthenticationEntryPointTests {
    private DelegatingAuthenticationEntryPoint daep;

    private LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints;

    private AuthenticationEntryPoint defaultEntryPoint;

    private HttpServletRequest request = new MockHttpServletRequest();

    @Test
    public void testDefaultEntryPoint() throws Exception {
        AuthenticationEntryPoint firstAEP = Mockito.mock(AuthenticationEntryPoint.class);
        RequestMatcher firstRM = Mockito.mock(RequestMatcher.class);
        Mockito.when(firstRM.matches(request)).thenReturn(false);
        entryPoints.put(firstRM, firstAEP);
        daep.commence(request, null, null);
        Mockito.verify(defaultEntryPoint).commence(request, null, null);
        Mockito.verify(firstAEP, Mockito.never()).commence(request, null, null);
    }

    @Test
    public void testFirstEntryPoint() throws Exception {
        AuthenticationEntryPoint firstAEP = Mockito.mock(AuthenticationEntryPoint.class);
        RequestMatcher firstRM = Mockito.mock(RequestMatcher.class);
        AuthenticationEntryPoint secondAEP = Mockito.mock(AuthenticationEntryPoint.class);
        RequestMatcher secondRM = Mockito.mock(RequestMatcher.class);
        Mockito.when(firstRM.matches(request)).thenReturn(true);
        entryPoints.put(firstRM, firstAEP);
        entryPoints.put(secondRM, secondAEP);
        daep.commence(request, null, null);
        Mockito.verify(firstAEP).commence(request, null, null);
        Mockito.verify(secondAEP, Mockito.never()).commence(request, null, null);
        Mockito.verify(defaultEntryPoint, Mockito.never()).commence(request, null, null);
        Mockito.verify(secondRM, Mockito.never()).matches(request);
    }

    @Test
    public void testSecondEntryPoint() throws Exception {
        AuthenticationEntryPoint firstAEP = Mockito.mock(AuthenticationEntryPoint.class);
        RequestMatcher firstRM = Mockito.mock(RequestMatcher.class);
        AuthenticationEntryPoint secondAEP = Mockito.mock(AuthenticationEntryPoint.class);
        RequestMatcher secondRM = Mockito.mock(RequestMatcher.class);
        Mockito.when(firstRM.matches(request)).thenReturn(false);
        Mockito.when(secondRM.matches(request)).thenReturn(true);
        entryPoints.put(firstRM, firstAEP);
        entryPoints.put(secondRM, secondAEP);
        daep.commence(request, null, null);
        Mockito.verify(secondAEP).commence(request, null, null);
        Mockito.verify(firstAEP, Mockito.never()).commence(request, null, null);
        Mockito.verify(defaultEntryPoint, Mockito.never()).commence(request, null, null);
    }
}

