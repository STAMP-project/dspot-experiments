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
package org.springframework.security.web.authentication.switchuser;


import AnyRequestMatcher.INSTANCE;
import SwitchUserFilter.SPRING_SECURITY_SWITCH_USERNAME_KEY;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.FilterChain;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.util.FieldUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;


/**
 * Tests
 * {@link org.springframework.security.web.authentication.switchuser.SwitchUserFilter}.
 *
 * @author Mark St.Godard
 * @author Luke Taylor
 */
public class SwitchUserFilterTests {
    private static final List<GrantedAuthority> ROLES_12 = AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void requiresExitUserMatchesCorrectly() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setExitUserUrl("/j_spring_security_my_exit_user");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/j_spring_security_my_exit_user");
        assertThat(filter.requiresExitUser(request)).isTrue();
    }

    // gh-4249
    @Test
    public void requiresExitUserWhenEndsWithThenDoesNotMatch() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setExitUserUrl("/j_spring_security_my_exit_user");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/foo/bar/j_spring_security_my_exit_user");
        assertThat(filter.requiresExitUser(request)).isFalse();
    }

    @Test
    public void requiresExitUserWhenMatcherThenWorks() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setExitUserMatcher(INSTANCE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/foo/bar/j_spring_security_my_exit_user");
        assertThat(filter.requiresExitUser(request)).isTrue();
    }

    @Test
    public void requiresSwitchMatchesCorrectly() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setSwitchUserUrl("/j_spring_security_my_switch_user");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/j_spring_security_my_switch_user");
        assertThat(filter.requiresSwitchUser(request)).isTrue();
    }

    // gh-4249
    @Test
    public void requiresSwitchUserWhenEndsWithThenDoesNotMatch() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setSwitchUserUrl("/j_spring_security_my_exit_user");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/foo/bar/j_spring_security_my_exit_user");
        assertThat(filter.requiresSwitchUser(request)).isFalse();
    }

    @Test
    public void requiresSwitchUserWhenMatcherThenWorks() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setSwitchUserMatcher(INSTANCE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/foo/bar/j_spring_security_my_exit_user");
        assertThat(filter.requiresSwitchUser(request)).isTrue();
    }

    @Test(expected = UsernameNotFoundException.class)
    public void attemptSwitchToUnknownUserFails() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY, "user-that-doesnt-exist");
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(new SwitchUserFilterTests.MockUserDetailsService());
        filter.attemptSwitchUser(request);
    }

    @Test(expected = DisabledException.class)
    public void attemptSwitchToUserThatIsDisabledFails() throws Exception {
        switchToUser("mcgarrett");
    }

    @Test(expected = AccountExpiredException.class)
    public void attemptSwitchToUserWithAccountExpiredFails() throws Exception {
        switchToUser("wofat");
    }

    @Test(expected = CredentialsExpiredException.class)
    public void attemptSwitchToUserWithExpiredCredentialsFails() throws Exception {
        switchToUser("steve");
    }

    @Test(expected = UsernameNotFoundException.class)
    public void switchUserWithNullUsernameThrowsException() throws Exception {
        switchToUser(null);
    }

    @Test
    public void attemptSwitchUserIsSuccessfulWithValidUser() throws Exception {
        assertThat(switchToUser("jacklord")).isNotNull();
    }

    @Test
    public void switchToLockedAccountCausesRedirectToSwitchFailureUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/login/impersonate");
        request.addParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY, "mcgarrett");
        MockHttpServletResponse response = new MockHttpServletResponse();
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setTargetUrl("/target");
        filter.setUserDetailsService(new SwitchUserFilterTests.MockUserDetailsService());
        filter.afterPropertiesSet();
        // Check it with no url set (should get a text response)
        FilterChain chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.never()).doFilter(request, response);
        assertThat(response.getErrorMessage()).isNotNull();
        // Now check for the redirect
        request.setContextPath("/mywebapp");
        request.setRequestURI("/mywebapp/login/impersonate");
        filter = new SwitchUserFilter();
        filter.setTargetUrl("/target");
        filter.setUserDetailsService(new SwitchUserFilterTests.MockUserDetailsService());
        filter.setSwitchFailureUrl("/switchfailed");
        filter.afterPropertiesSet();
        response = new MockHttpServletResponse();
        chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.never()).doFilter(request, response);
        assertThat(response.getRedirectedUrl()).isEqualTo("/mywebapp/switchfailed");
        assertThat(FieldUtils.getFieldValue(filter, "switchFailureUrl")).isEqualTo("/switchfailed");
    }

    @Test(expected = IllegalArgumentException.class)
    public void configMissingUserDetailsServiceFails() throws Exception {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setSwitchUserUrl("/login/impersonate");
        filter.setExitUserUrl("/logout/impersonate");
        filter.setTargetUrl("/main.jsp");
        filter.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadConfigMissingTargetUrl() throws Exception {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(new SwitchUserFilterTests.MockUserDetailsService());
        filter.setSwitchUserUrl("/login/impersonate");
        filter.setExitUserUrl("/logout/impersonate");
        filter.afterPropertiesSet();
    }

    @Test
    public void defaultProcessesFilterUrlMatchesUrlWithPathParameter() {
        MockHttpServletRequest request = createMockSwitchRequest();
        request.setContextPath("/webapp");
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setSwitchUserUrl("/login/impersonate");
        request.setRequestURI("/webapp/login/impersonate;jsessionid=8JHDUD723J8");
        assertThat(filter.requiresSwitchUser(request)).isTrue();
    }

    @Test
    public void exitUserJackLordToDanoSucceeds() throws Exception {
        // original user
        UsernamePasswordAuthenticationToken source = new UsernamePasswordAuthenticationToken("dano", "hawaii50", SwitchUserFilterTests.ROLES_12);
        // set current user (Admin)
        List<GrantedAuthority> adminAuths = new ArrayList<>();
        adminAuths.addAll(SwitchUserFilterTests.ROLES_12);
        adminAuths.add(new SwitchUserGrantedAuthority("PREVIOUS_ADMINISTRATOR", source));
        UsernamePasswordAuthenticationToken admin = new UsernamePasswordAuthenticationToken("jacklord", "hawaii50", adminAuths);
        SecurityContextHolder.getContext().setAuthentication(admin);
        MockHttpServletRequest request = createMockSwitchRequest();
        request.setRequestURI("/logout/impersonate");
        // setup filter
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(new SwitchUserFilterTests.MockUserDetailsService());
        filter.setExitUserUrl("/logout/impersonate");
        filter.setSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/webapp/someOtherUrl"));
        // run 'exit'
        FilterChain chain = Mockito.mock(FilterChain.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.never()).doFilter(request, response);
        // check current user, should be back to original user (dano)
        Authentication targetAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(targetAuth).isNotNull();
        assertThat(targetAuth.getPrincipal()).isEqualTo("dano");
    }

    @Test(expected = AuthenticationException.class)
    public void exitUserWithNoCurrentUserFails() throws Exception {
        // no current user in secure context
        SecurityContextHolder.clearContext();
        MockHttpServletRequest request = createMockSwitchRequest();
        request.setRequestURI("/logout/impersonate");
        // setup filter
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(new SwitchUserFilterTests.MockUserDetailsService());
        filter.setExitUserUrl("/logout/impersonate");
        // run 'exit', expect fail due to no current user
        FilterChain chain = Mockito.mock(FilterChain.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.never()).doFilter(request, response);
    }

    @Test
    public void redirectToTargetUrlIsCorrect() throws Exception {
        MockHttpServletRequest request = createMockSwitchRequest();
        request.setContextPath("/webapp");
        request.addParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY, "jacklord");
        request.setRequestURI("/webapp/login/impersonate");
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setSwitchUserUrl("/login/impersonate");
        filter.setSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/someOtherUrl"));
        filter.setUserDetailsService(new SwitchUserFilterTests.MockUserDetailsService());
        FilterChain chain = Mockito.mock(FilterChain.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.never()).doFilter(request, response);
        assertThat(response.getRedirectedUrl()).isEqualTo("/webapp/someOtherUrl");
    }

    @Test
    public void redirectOmitsContextPathIfUseRelativeContextSet() throws Exception {
        // set current user
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("dano", "hawaii50");
        SecurityContextHolder.getContext().setAuthentication(auth);
        MockHttpServletRequest request = createMockSwitchRequest();
        request.setContextPath("/webapp");
        request.addParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY, "jacklord");
        request.setRequestURI("/webapp/login/impersonate");
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setSwitchUserUrl("/login/impersonate");
        SimpleUrlAuthenticationSuccessHandler switchSuccessHandler = new SimpleUrlAuthenticationSuccessHandler("/someOtherUrl");
        DefaultRedirectStrategy contextRelativeRedirector = new DefaultRedirectStrategy();
        contextRelativeRedirector.setContextRelative(true);
        switchSuccessHandler.setRedirectStrategy(contextRelativeRedirector);
        filter.setSuccessHandler(switchSuccessHandler);
        filter.setUserDetailsService(new SwitchUserFilterTests.MockUserDetailsService());
        FilterChain chain = Mockito.mock(FilterChain.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.never()).doFilter(request, response);
        assertThat(response.getRedirectedUrl()).isEqualTo("/someOtherUrl");
    }

    @Test
    public void testSwitchRequestFromDanoToJackLord() throws Exception {
        // set current user
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("dano", "hawaii50");
        SecurityContextHolder.getContext().setAuthentication(auth);
        // http request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/webapp/login/impersonate");
        request.setContextPath("/webapp");
        request.addParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY, "jacklord");
        // http response
        MockHttpServletResponse response = new MockHttpServletResponse();
        // setup filter
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(new SwitchUserFilterTests.MockUserDetailsService());
        filter.setSwitchUserUrl("/login/impersonate");
        filter.setSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/webapp/someOtherUrl"));
        FilterChain chain = Mockito.mock(FilterChain.class);
        // test updates user token and context
        filter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.never()).doFilter(request, response);
        // check current user
        Authentication targetAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(targetAuth).isNotNull();
        assertThat(((targetAuth.getPrincipal()) instanceof UserDetails)).isTrue();
        assertThat(getUsername()).isEqualTo("jacklord");
    }

    @Test
    public void modificationOfAuthoritiesWorks() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("dano", "hawaii50");
        SecurityContextHolder.getContext().setAuthentication(auth);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY, "jacklord");
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(new SwitchUserFilterTests.MockUserDetailsService());
        filter.setSwitchUserAuthorityChanger(new SwitchUserAuthorityChanger() {
            public Collection<GrantedAuthority> modifyGrantedAuthorities(UserDetails targetUser, Authentication currentAuthentication, Collection<? extends GrantedAuthority> authoritiesToBeGranted) {
                List<GrantedAuthority> auths = new ArrayList<>();
                auths.add(new SimpleGrantedAuthority("ROLE_NEW"));
                return auths;
            }
        });
        Authentication result = filter.attemptSwitchUser(request);
        assertThat((result != null)).isTrue();
        assertThat(result.getAuthorities()).hasSize(2);
        assertThat(AuthorityUtils.authorityListToSet(result.getAuthorities())).contains("ROLE_NEW");
    }

    // SEC-1763
    @Test
    public void nestedSwitchesAreNotAllowed() throws Exception {
        // original user
        UsernamePasswordAuthenticationToken source = new UsernamePasswordAuthenticationToken("orig", "hawaii50", SwitchUserFilterTests.ROLES_12);
        SecurityContextHolder.getContext().setAuthentication(source);
        SecurityContextHolder.getContext().setAuthentication(switchToUser("jacklord"));
        Authentication switched = switchToUser("dano");
        SwitchUserGrantedAuthority switchedFrom = null;
        for (GrantedAuthority ga : switched.getAuthorities()) {
            if (ga instanceof SwitchUserGrantedAuthority) {
                switchedFrom = ((SwitchUserGrantedAuthority) (ga));
                break;
            }
        }
        assertThat(switchedFrom).isNotNull();
        assertThat(source).isSameAs(switchedFrom.getSource());
    }

    // gh-3697
    @Test
    public void switchAuthorityRoleCannotBeNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("switchAuthorityRole cannot be null");
        switchToUserWithAuthorityRole("dano", null);
    }

    // gh-3697
    @Test
    public void switchAuthorityRoleCanBeChanged() throws Exception {
        String switchAuthorityRole = "PREVIOUS_ADMINISTRATOR";
        // original user
        UsernamePasswordAuthenticationToken source = new UsernamePasswordAuthenticationToken("orig", "hawaii50", SwitchUserFilterTests.ROLES_12);
        SecurityContextHolder.getContext().setAuthentication(source);
        SecurityContextHolder.getContext().setAuthentication(switchToUser("jacklord"));
        Authentication switched = switchToUserWithAuthorityRole("dano", switchAuthorityRole);
        SwitchUserGrantedAuthority switchedFrom = null;
        for (GrantedAuthority ga : switched.getAuthorities()) {
            if (ga instanceof SwitchUserGrantedAuthority) {
                switchedFrom = ((SwitchUserGrantedAuthority) (ga));
                break;
            }
        }
        assertThat(switchedFrom).isNotNull();
        assertThat(switchedFrom.getSource()).isSameAs(source);
        assertThat(switchAuthorityRole).isEqualTo(switchedFrom.getAuthority());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSwitchFailureUrlWhenNullThenThrowException() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setSwitchFailureUrl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSwitchFailureUrlWhenEmptyThenThrowException() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setSwitchFailureUrl("");
    }

    @Test
    public void setSwitchFailureUrlWhenValidThenNoException() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setSwitchFailureUrl("/foo");
    }

    // ~ Inner Classes
    // ==================================================================================================
    private class MockUserDetailsService implements UserDetailsService {
        private String password = "hawaii50";

        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            // jacklord, dano (active)
            // mcgarrett (disabled)
            // wofat (account expired)
            // steve (credentials expired)
            if (("jacklord".equals(username)) || ("dano".equals(username))) {
                return new org.springframework.security.core.userdetails.User(username, password, true, true, true, true, SwitchUserFilterTests.ROLES_12);
            } else
                if ("mcgarrett".equals(username)) {
                    return new org.springframework.security.core.userdetails.User(username, password, false, true, true, true, SwitchUserFilterTests.ROLES_12);
                } else
                    if ("wofat".equals(username)) {
                        return new org.springframework.security.core.userdetails.User(username, password, true, false, true, true, SwitchUserFilterTests.ROLES_12);
                    } else
                        if ("steve".equals(username)) {
                            return new org.springframework.security.core.userdetails.User(username, password, true, true, false, true, SwitchUserFilterTests.ROLES_12);
                        } else {
                            throw new UsernameNotFoundException(("Could not find: " + username));
                        }



        }
    }
}

