/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.web.security.knox;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class KnoxAuthenticationFilterTest {
    private static final String COOKIE_NAME = "hadoop-jwt";

    private KnoxAuthenticationFilter knoxAuthenticationFilter;

    @Test
    public void testInsecureHttp() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(false);
        Assert.assertNull(knoxAuthenticationFilter.attemptAuthentication(request));
    }

    @Test
    public void testNullCookies() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getCookies()).thenReturn(null);
        Assert.assertNull(knoxAuthenticationFilter.attemptAuthentication(request));
    }

    @Test
    public void testNoCookies() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{  });
        Assert.assertNull(knoxAuthenticationFilter.attemptAuthentication(request));
    }

    @Test
    public void testWrongCookieName() throws Exception {
        final String jwt = "my-jwt";
        final Cookie knoxCookie = Mockito.mock(Cookie.class);
        Mockito.when(knoxCookie.getName()).thenReturn("not-hadoop-jwt");
        Mockito.when(knoxCookie.getValue()).thenReturn(jwt);
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ knoxCookie });
        final KnoxAuthenticationRequestToken authRequest = ((KnoxAuthenticationRequestToken) (knoxAuthenticationFilter.attemptAuthentication(request)));
        Assert.assertNull(authRequest);
    }

    @Test
    public void testKnoxCookie() throws Exception {
        final String jwt = "my-jwt";
        final Cookie knoxCookie = Mockito.mock(Cookie.class);
        Mockito.when(knoxCookie.getName()).thenReturn(KnoxAuthenticationFilterTest.COOKIE_NAME);
        Mockito.when(knoxCookie.getValue()).thenReturn(jwt);
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ knoxCookie });
        final KnoxAuthenticationRequestToken authRequest = ((KnoxAuthenticationRequestToken) (knoxAuthenticationFilter.attemptAuthentication(request)));
        Assert.assertNotNull(authRequest);
        Assert.assertEquals(jwt, authRequest.getToken());
    }
}

