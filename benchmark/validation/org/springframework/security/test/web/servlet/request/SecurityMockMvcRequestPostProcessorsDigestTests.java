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


import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;


public class SecurityMockMvcRequestPostProcessorsDigestTests {
    private DigestAuthenticationFilter filter;

    private MockHttpServletRequest request;

    private String username;

    private String password;

    private DigestAuthenticationEntryPoint entryPoint;

    @Test
    public void digestWithFilter() throws Exception {
        MockHttpServletRequest postProcessedRequest = SecurityMockMvcRequestPostProcessors.digest().postProcessRequest(request);
        assertThat(extractUser()).isEqualTo("user");
    }

    @Test
    public void digestWithFilterCustomUsername() throws Exception {
        String username = "admin";
        MockHttpServletRequest postProcessedRequest = SecurityMockMvcRequestPostProcessors.digest(username).postProcessRequest(request);
        assertThat(extractUser()).isEqualTo(username);
    }

    @Test
    public void digestWithFilterCustomPassword() throws Exception {
        String username = "custom";
        password = "secret";
        MockHttpServletRequest postProcessedRequest = SecurityMockMvcRequestPostProcessors.digest(username).password(password).postProcessRequest(request);
        assertThat(extractUser()).isEqualTo(username);
    }

    @Test
    public void digestWithFilterCustomRealm() throws Exception {
        String username = "admin";
        entryPoint.setRealmName("Custom");
        MockHttpServletRequest postProcessedRequest = SecurityMockMvcRequestPostProcessors.digest(username).realm(entryPoint.getRealmName()).postProcessRequest(request);
        assertThat(extractUser()).isEqualTo(username);
    }

    @Test
    public void digestWithFilterFails() throws Exception {
        String username = "admin";
        MockHttpServletRequest postProcessedRequest = SecurityMockMvcRequestPostProcessors.digest(username).realm("Invalid").postProcessRequest(request);
        assertThat(extractUser()).isNull();
    }
}

