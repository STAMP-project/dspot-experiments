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


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:org/springframework/security/web/authentication/DelegatingAuthenticationEntryPointTest-context.xml")
public class DelegatingAuthenticationEntryPointContextTests {
    @Autowired
    private DelegatingAuthenticationEntryPoint daep;

    @Autowired
    @Qualifier("firstAEP")
    private AuthenticationEntryPoint firstAEP;

    @Autowired
    @Qualifier("defaultAEP")
    private AuthenticationEntryPoint defaultAEP;

    @Test
    @DirtiesContext
    public void testFirstAEP() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.10");
        request.addHeader("User-Agent", "Mozilla/5.0");
        daep.commence(request, null, null);
        Mockito.verify(firstAEP).commence(request, null, null);
        Mockito.verify(defaultAEP, Mockito.never()).commence(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(AuthenticationException.class));
    }

    @Test
    @DirtiesContext
    public void testDefaultAEP() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.10");
        daep.commence(request, null, null);
        Mockito.verify(defaultAEP).commence(request, null, null);
        Mockito.verify(firstAEP, Mockito.never()).commence(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(AuthenticationException.class));
    }
}

