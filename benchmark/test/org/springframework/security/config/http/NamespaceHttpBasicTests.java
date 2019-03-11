/**
 * Copyright 2002-2017 the original author or authors.
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


import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_UNAUTHORIZED;
import java.lang.reflect.Method;
import java.util.Base64;
import javax.servlet.Filter;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author Rob Winch
 */
public class NamespaceHttpBasicTests {
    @Mock
    Method method;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    MockFilterChain chain;

    ConfigurableApplicationContext context;

    Filter springSecurityFilterChain;

    // gh-3296
    @Test
    public void httpBasicWithPasswordEncoder() throws Exception {
        // @formatter:off
        loadContext(("<http>\n" + ((((((((((((("\t\t<intercept-url pattern=\"/**\" access=\"hasRole(\'USER\')\" />\n" + "\t\t<http-basic />\n") + "\t</http>\n") + "\n") + "\t<authentication-manager id=\"authenticationManager\">\n") + "\t\t<authentication-provider>\n") + "\t\t\t<password-encoder ref=\"passwordEncoder\" />\n") + "\t\t\t<user-service>\n") + "\t\t\t\t<user name=\"user\" password=\"$2a$10$Zk1MxFEt7YYji4Ccy9xlfuewWzUMsmHZfy4UcCmNKVV6z5i/JNGJW\" authorities=\"ROLE_USER\"/>\n") + "\t\t\t</user-service>\n") + "\t\t</authentication-provider>\n") + "\t</authentication-manager>\n") + "\t<b:bean id=\"passwordEncoder\"\n") + "\t\tclass=\"org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder\" />")));
        // @formatter:on
        this.request.addHeader("Authorization", ("Basic " + (Base64.getEncoder().encodeToString("user:test".getBytes("UTF-8")))));
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_OK);
    }

    // gh-4220
    @Test
    public void httpBasicUnauthorizedOnDefault() throws Exception {
        // @formatter:off
        loadContext(("<http>\n" + (((("\t\t<intercept-url pattern=\"/**\" access=\"hasRole(\'USER\')\" />\n" + "\t\t<http-basic />\n") + "\t</http>\n") + "\n") + "	<authentication-manager />")));
        // @formatter:on
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
        assertThat(this.response.getHeader("WWW-Authenticate")).isEqualTo("Basic realm=\"Realm\"");
    }
}

