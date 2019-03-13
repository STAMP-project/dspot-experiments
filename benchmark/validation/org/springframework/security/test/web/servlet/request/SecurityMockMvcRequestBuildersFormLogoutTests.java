/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.security.test.web.servlet.request;


import CsrfRequestPostProcessor.TestCsrfTokenRepository.TOKEN_ATTR_NAME;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.web.csrf.CsrfToken;


public class SecurityMockMvcRequestBuildersFormLogoutTests {
    private MockServletContext servletContext;

    @Test
    public void defaults() throws Exception {
        MockHttpServletRequest request = SecurityMockMvcRequestBuilders.logout().buildRequest(servletContext);
        CsrfToken token = ((CsrfToken) (request.getAttribute(TOKEN_ATTR_NAME)));
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getParameter(token.getParameterName())).isEqualTo(token.getToken());
        assertThat(request.getRequestURI()).isEqualTo("/logout");
    }

    @Test
    public void custom() throws Exception {
        MockHttpServletRequest request = SecurityMockMvcRequestBuilders.logout("/admin/logout").buildRequest(servletContext);
        CsrfToken token = ((CsrfToken) (request.getAttribute(TOKEN_ATTR_NAME)));
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getParameter(token.getParameterName())).isEqualTo(token.getToken());
        assertThat(request.getRequestURI()).isEqualTo("/admin/logout");
    }
}

