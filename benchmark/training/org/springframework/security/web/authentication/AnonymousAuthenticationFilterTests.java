/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
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


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Tests {@link AnonymousAuthenticationFilter}.
 *
 * @author Ben Alex
 * @author Edd? Mel?ndez
 */
public class AnonymousAuthenticationFilterTests {
    @Test(expected = IllegalArgumentException.class)
    public void testDetectsMissingKey() throws Exception {
        new AnonymousAuthenticationFilter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDetectsUserAttribute() throws Exception {
        new AnonymousAuthenticationFilter("qwerty", null, null);
    }

    @Test
    public void testOperationWhenAuthenticationExistsInContextHolder() throws Exception {
        // Put an Authentication object into the SecurityContextHolder
        Authentication originalAuth = new TestingAuthenticationToken("user", "password", "ROLE_A");
        SecurityContextHolder.getContext().setAuthentication(originalAuth);
        AnonymousAuthenticationFilter filter = new AnonymousAuthenticationFilter("qwerty", "anonymousUsername", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        // Test
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("x");
        executeFilterInContainerSimulator(Mockito.mock(FilterConfig.class), filter, request, new MockHttpServletResponse(), new AnonymousAuthenticationFilterTests.MockFilterChain(true));
        // Ensure filter didn't change our original object
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(originalAuth);
    }

    @Test
    public void testOperationWhenNoAuthenticationInSecurityContextHolder() throws Exception {
        AnonymousAuthenticationFilter filter = new AnonymousAuthenticationFilter("qwerty", "anonymousUsername", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        filter.afterPropertiesSet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("x");
        executeFilterInContainerSimulator(Mockito.mock(FilterConfig.class), filter, request, new MockHttpServletResponse(), new AnonymousAuthenticationFilterTests.MockFilterChain(true));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth.getPrincipal()).isEqualTo("anonymousUsername");
        assertThat(AuthorityUtils.authorityListToSet(auth.getAuthorities())).contains("ROLE_ANONYMOUS");
        SecurityContextHolder.getContext().setAuthentication(null);// so anonymous fires

        // again
    }

    // ~ Inner Classes
    // ==================================================================================================
    private class MockFilterChain implements FilterChain {
        private boolean expectToProceed;

        public MockFilterChain(boolean expectToProceed) {
            this.expectToProceed = expectToProceed;
        }

        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (!(expectToProceed)) {
                fail("Did not expect filter chain to proceed");
            }
        }
    }
}

