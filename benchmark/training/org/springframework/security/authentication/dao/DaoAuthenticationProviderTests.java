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
package org.springframework.security.authentication.dao;


import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.PasswordEncodedUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.EhCacheBasedUserCache;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Tests {@link DaoAuthenticationProvider}.
 *
 * @author Ben Alex
 * @author Rob Winch
 */
public class DaoAuthenticationProviderTests {
    private static final List<GrantedAuthority> ROLES_12 = AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO");

    // ~ Methods
    // ========================================================================================================
    @Test
    public void testAuthenticateFailsForIncorrectPasswordCase() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("rod", "KOala");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown BadCredentialsException");
        } catch (BadCredentialsException expected) {
        }
    }

    @Test
    public void testReceivedBadCredentialsWhenCredentialsNotProvided() {
        // Test related to SEC-434
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.setUserCache(new MockUserCache());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("rod", null);
        try {
            provider.authenticate(authenticationToken);
            fail("Expected BadCredenialsException");
        } catch (BadCredentialsException expected) {
        }
    }

    @Test
    public void testAuthenticateFailsIfAccountExpired() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("peter", "opal");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserPeterAccountExpired());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown AccountExpiredException");
        } catch (AccountExpiredException expected) {
        }
    }

    @Test
    public void testAuthenticateFailsIfAccountLocked() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("peter", "opal");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserPeterAccountLocked());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown LockedException");
        } catch (LockedException expected) {
        }
    }

    @Test
    public void testAuthenticateFailsIfCredentialsExpired() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("peter", "opal");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserPeterCredentialsExpired());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown CredentialsExpiredException");
        } catch (CredentialsExpiredException expected) {
        }
        // Check that wrong password causes BadCredentialsException, rather than
        // CredentialsExpiredException
        token = new UsernamePasswordAuthenticationToken("peter", "wrong_password");
        try {
            provider.authenticate(token);
            fail("Should have thrown BadCredentialsException");
        } catch (BadCredentialsException expected) {
        }
    }

    @Test
    public void testAuthenticateFailsIfUserDisabled() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("peter", "opal");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserPeter());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown DisabledException");
        } catch (DisabledException expected) {
        }
    }

    @Test
    public void testAuthenticateFailsWhenAuthenticationDaoHasBackendFailure() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("rod", "koala");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceSimulateBackendError());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown InternalAuthenticationServiceException");
        } catch (InternalAuthenticationServiceException expected) {
        }
    }

    @Test
    public void testAuthenticateFailsWithEmptyUsername() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(null, "koala");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown BadCredentialsException");
        } catch (BadCredentialsException expected) {
        }
    }

    @Test
    public void testAuthenticateFailsWithInvalidPassword() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("rod", "INVALID_PASSWORD");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown BadCredentialsException");
        } catch (BadCredentialsException expected) {
        }
    }

    @Test
    public void testAuthenticateFailsWithInvalidUsernameAndHideUserNotFoundExceptionFalse() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("INVALID_USER", "koala");
        DaoAuthenticationProvider provider = createProvider();
        provider.setHideUserNotFoundExceptions(false);// we want

        // UsernameNotFoundExceptions
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown UsernameNotFoundException");
        } catch (UsernameNotFoundException expected) {
        }
    }

    @Test
    public void testAuthenticateFailsWithInvalidUsernameAndHideUserNotFoundExceptionsWithDefaultOfTrue() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("INVALID_USER", "koala");
        DaoAuthenticationProvider provider = createProvider();
        assertThat(provider.isHideUserNotFoundExceptions()).isTrue();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown BadCredentialsException");
        } catch (BadCredentialsException expected) {
        }
    }

    @Test
    public void testAuthenticateFailsWithInvalidUsernameAndChangePasswordEncoder() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("INVALID_USER", "koala");
        DaoAuthenticationProvider provider = createProvider();
        assertThat(provider.isHideUserNotFoundExceptions()).isTrue();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown BadCredentialsException");
        } catch (BadCredentialsException expected) {
        }
        provider.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
        try {
            provider.authenticate(token);
            fail("Should have thrown BadCredentialsException");
        } catch (BadCredentialsException expected) {
        }
    }

    @Test
    public void testAuthenticateFailsWithMixedCaseUsernameIfDefaultChanged() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("RoD", "koala");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.setUserCache(new MockUserCache());
        try {
            provider.authenticate(token);
            fail("Should have thrown BadCredentialsException");
        } catch (BadCredentialsException expected) {
        }
    }

    @Test
    public void testAuthenticates() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("rod", "koala");
        token.setDetails("192.168.0.1");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.setUserCache(new MockUserCache());
        Authentication result = provider.authenticate(token);
        if (!(result instanceof UsernamePasswordAuthenticationToken)) {
            fail("Should have returned instance of UsernamePasswordAuthenticationToken");
        }
        UsernamePasswordAuthenticationToken castResult = ((UsernamePasswordAuthenticationToken) (result));
        assertThat(castResult.getPrincipal().getClass()).isEqualTo(User.class);
        assertThat(castResult.getCredentials()).isEqualTo("koala");
        assertThat(AuthorityUtils.authorityListToSet(castResult.getAuthorities())).contains("ROLE_ONE", "ROLE_TWO");
        assertThat(castResult.getDetails()).isEqualTo("192.168.0.1");
    }

    @Test
    public void testAuthenticatesASecondTime() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("rod", "koala");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.setUserCache(new MockUserCache());
        Authentication result = provider.authenticate(token);
        if (!(result instanceof UsernamePasswordAuthenticationToken)) {
            fail("Should have returned instance of UsernamePasswordAuthenticationToken");
        }
        // Now try to authenticate with the previous result (with its UserDetails)
        Authentication result2 = provider.authenticate(result);
        if (!(result2 instanceof UsernamePasswordAuthenticationToken)) {
            fail("Should have returned instance of UsernamePasswordAuthenticationToken");
        }
        assertThat(result2.getCredentials()).isEqualTo(result.getCredentials());
    }

    @Test
    public void testAuthenticatesWithForcePrincipalAsString() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("rod", "koala");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.setUserCache(new MockUserCache());
        provider.setForcePrincipalAsString(true);
        Authentication result = provider.authenticate(token);
        if (!(result instanceof UsernamePasswordAuthenticationToken)) {
            fail("Should have returned instance of UsernamePasswordAuthenticationToken");
        }
        UsernamePasswordAuthenticationToken castResult = ((UsernamePasswordAuthenticationToken) (result));
        assertThat(castResult.getPrincipal().getClass()).isEqualTo(String.class);
        assertThat(castResult.getPrincipal()).isEqualTo("rod");
    }

    @Test
    public void authenticateWhenSuccessAndPasswordManagerThenUpdates() {
        String password = "password";
        String encodedPassword = "encoded";
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", password);
        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
        UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
        UserDetailsPasswordService passwordManager = Mockito.mock(UserDetailsPasswordService.class);
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(encoder);
        provider.setUserDetailsService(userDetailsService);
        provider.setUserDetailsPasswordService(passwordManager);
        UserDetails user = PasswordEncodedUser.user();
        Mockito.when(encoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(encoder.upgradeEncoding(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(encoder.encode(ArgumentMatchers.any())).thenReturn(encodedPassword);
        Mockito.when(userDetailsService.loadUserByUsername(ArgumentMatchers.any())).thenReturn(user);
        Mockito.when(passwordManager.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(user);
        Authentication result = provider.authenticate(token);
        Mockito.verify(encoder).encode(password);
        Mockito.verify(passwordManager).updatePassword(ArgumentMatchers.eq(user), ArgumentMatchers.eq(encodedPassword));
    }

    @Test
    public void authenticateWhenBadCredentialsAndPasswordManagerThenNoUpdate() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", "password");
        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
        UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
        UserDetailsPasswordService passwordManager = Mockito.mock(UserDetailsPasswordService.class);
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(encoder);
        provider.setUserDetailsService(userDetailsService);
        provider.setUserDetailsPasswordService(passwordManager);
        UserDetails user = PasswordEncodedUser.user();
        Mockito.when(encoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(false);
        Mockito.when(userDetailsService.loadUserByUsername(ArgumentMatchers.any())).thenReturn(user);
        assertThatThrownBy(() -> provider.authenticate(token)).isInstanceOf(BadCredentialsException.class);
        Mockito.verifyZeroInteractions(passwordManager);
    }

    @Test
    public void authenticateWhenNotUpgradeAndPasswordManagerThenNoUpdate() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", "password");
        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
        UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
        UserDetailsPasswordService passwordManager = Mockito.mock(UserDetailsPasswordService.class);
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(encoder);
        provider.setUserDetailsService(userDetailsService);
        provider.setUserDetailsPasswordService(passwordManager);
        UserDetails user = PasswordEncodedUser.user();
        Mockito.when(encoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(encoder.upgradeEncoding(ArgumentMatchers.any())).thenReturn(false);
        Mockito.when(userDetailsService.loadUserByUsername(ArgumentMatchers.any())).thenReturn(user);
        Authentication result = provider.authenticate(token);
        Mockito.verifyZeroInteractions(passwordManager);
    }

    @Test
    public void testDetectsNullBeingReturnedFromAuthenticationDao() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("rod", "koala");
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceReturnsNull());
        try {
            provider.authenticate(token);
            fail("Should have thrown AuthenticationServiceException");
        } catch (AuthenticationServiceException expected) {
            assertThat("UserDetailsService returned null, which is an interface contract violation").isEqualTo(expected.getMessage());
        }
    }

    @Test
    public void testGettersSetters() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        assertThat(provider.getPasswordEncoder().getClass()).isEqualTo(BCryptPasswordEncoder.class);
        provider.setUserCache(new EhCacheBasedUserCache());
        assertThat(provider.getUserCache().getClass()).isEqualTo(EhCacheBasedUserCache.class);
        assertThat(provider.isForcePrincipalAsString()).isFalse();
        provider.setForcePrincipalAsString(true);
        assertThat(provider.isForcePrincipalAsString()).isTrue();
    }

    @Test
    public void testGoesBackToAuthenticationDaoToObtainLatestPasswordIfCachedPasswordSeemsIncorrect() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("rod", "koala");
        DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod authenticationDao = new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod();
        MockUserCache cache = new MockUserCache();
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(authenticationDao);
        provider.setUserCache(cache);
        // This will work, as password still "koala"
        provider.authenticate(token);
        // Check "rod = koala" ended up in the cache
        assertThat(cache.getUserFromCache("rod").getPassword()).isEqualTo("koala");
        // Now change the password the AuthenticationDao will return
        authenticationDao.setPassword("easternLongNeckTurtle");
        // Now try authentication again, with the new password
        token = new UsernamePasswordAuthenticationToken("rod", "easternLongNeckTurtle");
        provider.authenticate(token);
        // To get this far, the new password was accepted
        // Check the cache was updated
        assertThat(cache.getUserFromCache("rod").getPassword()).isEqualTo("easternLongNeckTurtle");
    }

    @Test
    public void testStartupFailsIfNoAuthenticationDao() throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        try {
            provider.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testStartupFailsIfNoUserCacheSet() throws Exception {
        DaoAuthenticationProvider provider = createProvider();
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        assertThat(provider.getUserCache().getClass()).isEqualTo(NullUserCache.class);
        provider.setUserCache(null);
        try {
            provider.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testStartupSuccess() throws Exception {
        DaoAuthenticationProvider provider = createProvider();
        UserDetailsService userDetailsService = new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod();
        provider.setUserDetailsService(userDetailsService);
        provider.setUserCache(new MockUserCache());
        assertThat(provider.getUserDetailsService()).isEqualTo(userDetailsService);
        provider.afterPropertiesSet();
    }

    @Test
    public void testSupports() {
        DaoAuthenticationProvider provider = createProvider();
        assertThat(provider.supports(UsernamePasswordAuthenticationToken.class)).isTrue();
        assertThat((!(provider.supports(TestingAuthenticationToken.class)))).isTrue();
    }

    // SEC-2056
    @Test
    public void testUserNotFoundEncodesPassword() throws Exception {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("missing", "koala");
        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(encoder.encode(ArgumentMatchers.anyString())).thenReturn("koala");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(encoder);
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        provider.afterPropertiesSet();
        try {
            provider.authenticate(token);
            fail("Expected Exception");
        } catch (UsernameNotFoundException success) {
        }
        // ensure encoder invoked w/ non-null strings since PasswordEncoder impls may fail
        // if encoded password is null
        Mockito.verify(encoder).matches(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(String.class));
    }

    @Test
    public void testUserNotFoundBCryptPasswordEncoder() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("missing", "koala");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(encoder);
        DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod userDetailsService = new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod();
        userDetailsService.password = encoder.encode(((CharSequence) (token.getCredentials())));
        provider.setUserDetailsService(userDetailsService);
        try {
            provider.authenticate(token);
            fail("Expected Exception");
        } catch (UsernameNotFoundException success) {
        }
    }

    @Test
    public void testUserNotFoundDefaultEncoder() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("missing", null);
        DaoAuthenticationProvider provider = createProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        try {
            provider.authenticate(token);
            fail("Expected Exception");
        } catch (UsernameNotFoundException success) {
        }
    }

    @Test
    public void testUserNotFoundNullCredentials() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("missing", null);
        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(encoder);
        provider.setUserDetailsService(new DaoAuthenticationProviderTests.MockUserDetailsServiceUserRod());
        try {
            provider.authenticate(token);
            fail("Expected Exception");
        } catch (UsernameNotFoundException success) {
        }
        Mockito.verify(encoder, Mockito.times(0)).matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    // ~ Inner Classes
    // ==================================================================================================
    private class MockUserDetailsServiceReturnsNull implements UserDetailsService {
        public UserDetails loadUserByUsername(String username) {
            return null;
        }
    }

    private class MockUserDetailsServiceSimulateBackendError implements UserDetailsService {
        public UserDetails loadUserByUsername(String username) {
            throw new DataRetrievalFailureException("This mock simulator is designed to fail");
        }
    }

    private class MockUserDetailsServiceUserRod implements UserDetailsService {
        private String password = "koala";

        public UserDetails loadUserByUsername(String username) {
            if ("rod".equals(username)) {
                return new User("rod", password, true, true, true, true, DaoAuthenticationProviderTests.ROLES_12);
            }
            throw new UsernameNotFoundException(("Could not find: " + username));
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    private class MockUserDetailsServiceUserPeter implements UserDetailsService {
        public UserDetails loadUserByUsername(String username) {
            if ("peter".equals(username)) {
                return new User("peter", "opal", false, true, true, true, DaoAuthenticationProviderTests.ROLES_12);
            }
            throw new UsernameNotFoundException(("Could not find: " + username));
        }
    }

    private class MockUserDetailsServiceUserPeterAccountExpired implements UserDetailsService {
        public UserDetails loadUserByUsername(String username) {
            if ("peter".equals(username)) {
                return new User("peter", "opal", true, false, true, true, DaoAuthenticationProviderTests.ROLES_12);
            }
            throw new UsernameNotFoundException(("Could not find: " + username));
        }
    }

    private class MockUserDetailsServiceUserPeterAccountLocked implements UserDetailsService {
        public UserDetails loadUserByUsername(String username) {
            if ("peter".equals(username)) {
                return new User("peter", "opal", true, true, true, false, DaoAuthenticationProviderTests.ROLES_12);
            }
            throw new UsernameNotFoundException(("Could not find: " + username));
        }
    }

    private class MockUserDetailsServiceUserPeterCredentialsExpired implements UserDetailsService {
        public UserDetails loadUserByUsername(String username) {
            if ("peter".equals(username)) {
                return new User("peter", "opal", true, true, false, true, DaoAuthenticationProviderTests.ROLES_12);
            }
            throw new UsernameNotFoundException(("Could not find: " + username));
        }
    }
}

