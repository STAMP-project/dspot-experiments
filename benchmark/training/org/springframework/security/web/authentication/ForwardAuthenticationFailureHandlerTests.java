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


import WebAttributes.AUTHENTICATION_EXCEPTION;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;


/**
 * <p>
 * Forward Authentication Failure Handler Tests
 * </p>
 *
 * @author Shazin Sadakath
 * @unknown 
 */
public class ForwardAuthenticationFailureHandlerTests {
    @Test(expected = IllegalArgumentException.class)
    public void invalidForwardUrl() {
        new ForwardAuthenticationFailureHandler("aaa");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyForwardUrl() {
        new ForwardAuthenticationFailureHandler("");
    }

    @Test
    public void responseIsForwarded() throws Exception {
        ForwardAuthenticationFailureHandler fafh = new ForwardAuthenticationFailureHandler("/forwardUrl");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException e = Mockito.mock(AuthenticationException.class);
        fafh.onAuthenticationFailure(request, response, e);
        assertThat(response.getForwardedUrl()).isEqualTo("/forwardUrl");
        assertThat(request.getAttribute(AUTHENTICATION_EXCEPTION)).isEqualTo(e);
    }
}

