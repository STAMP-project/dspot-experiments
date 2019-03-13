/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.security.config.http;


import java.lang.reflect.Method;
import javax.servlet.Filter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.ReflectionUtils;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReflectionUtils.class, Method.class })
@PowerMockIgnore({ "org.w3c.dom.*", "org.xml.sax.*", "org.apache.xerces.*", "javax.xml.parsers.*" })
public class SessionManagementConfigServlet31Tests {
    private static final String XML_AUTHENTICATION_MANAGER = "<authentication-manager>" + ((((("  <authentication-provider>" + "    <user-service>") + "      <user name='user' password='{noop}password' authorities='ROLE_USER' />") + "    </user-service>") + "  </authentication-provider>") + "</authentication-manager>");

    @Mock
    Method method;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    MockFilterChain chain;

    ConfigurableApplicationContext context;

    Filter springSecurityFilterChain;

    @Test
    public void changeSessionIdThenPreserveParameters() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "");
        request.getSession();
        request.setServletPath("/login");
        request.setMethod("POST");
        request.setParameter("username", "user");
        request.setParameter("password", "password");
        request.getSession().setAttribute("attribute1", "value1");
        String id = request.getSession().getId();
        loadContext((("<http>\n" + ((("        <form-login/>\n" + "        <session-management/>\n") + "        <csrf disabled=\'true\'/>\n") + "    </http>")) + (SessionManagementConfigServlet31Tests.XML_AUTHENTICATION_MANAGER)));
        springSecurityFilterChain.doFilter(request, response, chain);
        assertThat(request.getSession().getId()).isNotEqualTo(id);
        assertThat(request.getSession().getAttribute("attribute1")).isEqualTo("value1");
    }

    @Test
    public void changeSessionId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "");
        request.getSession();
        request.setServletPath("/login");
        request.setMethod("POST");
        request.setParameter("username", "user");
        request.setParameter("password", "password");
        String id = request.getSession().getId();
        loadContext((("<http>\n" + ((("        <form-login/>\n" + "        <session-management session-fixation-protection=\'changeSessionId\'/>\n") + "        <csrf disabled=\'true\'/>\n") + "    </http>")) + (SessionManagementConfigServlet31Tests.XML_AUTHENTICATION_MANAGER)));
        springSecurityFilterChain.doFilter(request, response, chain);
        assertThat(request.getSession().getId()).isNotEqualTo(id);
    }
}

