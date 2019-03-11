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
package org.springframework.security.ldap.authentication.ad;


import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


/**
 *
 *
 * @author Luke Taylor
 * @author Rob Winch
 */
public class ActiveDirectoryLdapAuthenticationProviderTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    ActiveDirectoryLdapAuthenticationProvider provider;

    UsernamePasswordAuthenticationToken joe = new UsernamePasswordAuthenticationToken("joe", "password");

    @Test
    public void bindPrincipalIsCreatedCorrectly() throws Exception {
        assertThat(provider.createBindPrincipal("joe")).isEqualTo("joe@mydomain.eu");
        assertThat(provider.createBindPrincipal("joe@mydomain.eu")).isEqualTo("joe@mydomain.eu");
    }

    @Test
    public void successfulAuthenticationProducesExpectedAuthorities() throws Exception {
        checkAuthentication("dc=mydomain,dc=eu", provider);
    }

    // SEC-1915
    @Test
    public void customSearchFilterIsUsedForSuccessfulAuthentication() throws Exception {
        // given
        String customSearchFilter = "(&(objectClass=user)(sAMAccountName={0}))";
        DirContext ctx = Mockito.mock(DirContext.class);
        Mockito.when(ctx.getNameInNamespace()).thenReturn("");
        DirContextAdapter dca = new DirContextAdapter();
        SearchResult sr = new SearchResult("CN=Joe Jannsen,CN=Users", dca, dca.getAttributes());
        Mockito.when(ctx.search(ArgumentMatchers.any(Name.class), ArgumentMatchers.eq(customSearchFilter), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenReturn(new ActiveDirectoryLdapAuthenticationProviderTests.MockNamingEnumeration(sr));
        ActiveDirectoryLdapAuthenticationProvider customProvider = new ActiveDirectoryLdapAuthenticationProvider("mydomain.eu", "ldap://192.168.1.200/");
        customProvider.contextFactory = createContextFactoryReturning(ctx);
        // when
        customProvider.setSearchFilter(customSearchFilter);
        Authentication result = customProvider.authenticate(joe);
        // then
        assertThat(result.isAuthenticated()).isTrue();
    }

    @Test
    public void defaultSearchFilter() throws Exception {
        // given
        final String defaultSearchFilter = "(&(objectClass=user)(userPrincipalName={0}))";
        DirContext ctx = Mockito.mock(DirContext.class);
        Mockito.when(ctx.getNameInNamespace()).thenReturn("");
        DirContextAdapter dca = new DirContextAdapter();
        SearchResult sr = new SearchResult("CN=Joe Jannsen,CN=Users", dca, dca.getAttributes());
        Mockito.when(ctx.search(ArgumentMatchers.any(Name.class), ArgumentMatchers.eq(defaultSearchFilter), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenReturn(new ActiveDirectoryLdapAuthenticationProviderTests.MockNamingEnumeration(sr));
        ActiveDirectoryLdapAuthenticationProvider customProvider = new ActiveDirectoryLdapAuthenticationProvider("mydomain.eu", "ldap://192.168.1.200/");
        customProvider.contextFactory = createContextFactoryReturning(ctx);
        // when
        Authentication result = customProvider.authenticate(joe);
        // then
        assertThat(result.isAuthenticated()).isTrue();
        Mockito.verify(ctx).search(ArgumentMatchers.any(DistinguishedName.class), ArgumentMatchers.eq(defaultSearchFilter), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class));
    }

    // SEC-2897,SEC-2224
    @Test
    public void bindPrincipalAndUsernameUsed() throws Exception {
        // given
        final String defaultSearchFilter = "(&(objectClass=user)(userPrincipalName={0}))";
        ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);
        DirContext ctx = Mockito.mock(DirContext.class);
        Mockito.when(ctx.getNameInNamespace()).thenReturn("");
        DirContextAdapter dca = new DirContextAdapter();
        SearchResult sr = new SearchResult("CN=Joe Jannsen,CN=Users", dca, dca.getAttributes());
        Mockito.when(ctx.search(ArgumentMatchers.any(Name.class), ArgumentMatchers.eq(defaultSearchFilter), captor.capture(), ArgumentMatchers.any(SearchControls.class))).thenReturn(new ActiveDirectoryLdapAuthenticationProviderTests.MockNamingEnumeration(sr));
        ActiveDirectoryLdapAuthenticationProvider customProvider = new ActiveDirectoryLdapAuthenticationProvider("mydomain.eu", "ldap://192.168.1.200/");
        customProvider.contextFactory = createContextFactoryReturning(ctx);
        // when
        Authentication result = customProvider.authenticate(joe);
        // then
        assertThat(captor.getValue()).containsExactly("joe@mydomain.eu", "joe");
        assertThat(result.isAuthenticated()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSearchFilterNull() {
        provider.setSearchFilter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSearchFilterEmpty() {
        provider.setSearchFilter(" ");
    }

    @Test
    public void nullDomainIsSupportedIfAuthenticatingWithFullUserPrincipal() throws Exception {
        provider = new ActiveDirectoryLdapAuthenticationProvider(null, "ldap://192.168.1.200/");
        DirContext ctx = Mockito.mock(DirContext.class);
        Mockito.when(ctx.getNameInNamespace()).thenReturn("");
        DirContextAdapter dca = new DirContextAdapter();
        SearchResult sr = new SearchResult("CN=Joe Jannsen,CN=Users", dca, dca.getAttributes());
        Mockito.when(ctx.search(ArgumentMatchers.eq(new DistinguishedName("DC=mydomain,DC=eu")), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenReturn(new ActiveDirectoryLdapAuthenticationProviderTests.MockNamingEnumeration(sr));
        provider.contextFactory = createContextFactoryReturning(ctx);
        try {
            provider.authenticate(joe);
            fail("Expected BadCredentialsException for user with no domain information");
        } catch (BadCredentialsException expected) {
        }
        provider.authenticate(new UsernamePasswordAuthenticationToken("joe@mydomain.eu", "password"));
    }

    @Test(expected = BadCredentialsException.class)
    public void failedUserSearchCausesBadCredentials() throws Exception {
        DirContext ctx = Mockito.mock(DirContext.class);
        Mockito.when(ctx.getNameInNamespace()).thenReturn("");
        Mockito.when(ctx.search(ArgumentMatchers.any(Name.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenThrow(new NameNotFoundException());
        provider.contextFactory = createContextFactoryReturning(ctx);
        provider.authenticate(joe);
    }

    // SEC-2017
    @Test(expected = BadCredentialsException.class)
    public void noUserSearchCausesUsernameNotFound() throws Exception {
        DirContext ctx = Mockito.mock(DirContext.class);
        Mockito.when(ctx.getNameInNamespace()).thenReturn("");
        Mockito.when(ctx.search(ArgumentMatchers.any(Name.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenReturn(new org.apache.directory.shared.ldap.util.EmptyEnumeration());
        provider.contextFactory = createContextFactoryReturning(ctx);
        provider.authenticate(joe);
    }

    // SEC-2500
    @Test(expected = BadCredentialsException.class)
    public void sec2500PreventAnonymousBind() {
        provider.authenticate(new UsernamePasswordAuthenticationToken("rwinch", ""));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void duplicateUserSearchCausesError() throws Exception {
        DirContext ctx = Mockito.mock(DirContext.class);
        Mockito.when(ctx.getNameInNamespace()).thenReturn("");
        NamingEnumeration<SearchResult> searchResults = Mockito.mock(NamingEnumeration.class);
        Mockito.when(searchResults.hasMore()).thenReturn(true, true, false);
        SearchResult searchResult = Mockito.mock(SearchResult.class);
        Mockito.when(searchResult.getObject()).thenReturn(new DirContextAdapter("ou=1"), new DirContextAdapter("ou=2"));
        Mockito.when(searchResults.next()).thenReturn(searchResult);
        Mockito.when(ctx.search(ArgumentMatchers.any(Name.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenReturn(searchResults);
        provider.contextFactory = createContextFactoryReturning(ctx);
        provider.authenticate(joe);
    }

    static final String msg = "[LDAP: error code 49 - 80858585: LdapErr: DSID-DECAFF0, comment: AcceptSecurityContext error, data ";

    @Test(expected = BadCredentialsException.class)
    public void userNotFoundIsCorrectlyMapped() {
        provider.contextFactory = createContextFactoryThrowing(new AuthenticationException(((ActiveDirectoryLdapAuthenticationProviderTests.msg) + "525, xxxx]")));
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.authenticate(joe);
    }

    @Test(expected = BadCredentialsException.class)
    public void incorrectPasswordIsCorrectlyMapped() {
        provider.contextFactory = createContextFactoryThrowing(new AuthenticationException(((ActiveDirectoryLdapAuthenticationProviderTests.msg) + "52e, xxxx]")));
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.authenticate(joe);
    }

    @Test(expected = BadCredentialsException.class)
    public void notPermittedIsCorrectlyMapped() {
        provider.contextFactory = createContextFactoryThrowing(new AuthenticationException(((ActiveDirectoryLdapAuthenticationProviderTests.msg) + "530, xxxx]")));
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.authenticate(joe);
    }

    @Test
    public void passwordNeedsResetIsCorrectlyMapped() {
        final String dataCode = "773";
        provider.contextFactory = createContextFactoryThrowing(new AuthenticationException((((ActiveDirectoryLdapAuthenticationProviderTests.msg) + dataCode) + ", xxxx]")));
        provider.setConvertSubErrorCodesToExceptions(true);
        thrown.expect(BadCredentialsException.class);
        thrown.expect(new BaseMatcher<BadCredentialsException>() {
            private Matcher<Object> causeInstance = CoreMatchers.instanceOf(ActiveDirectoryAuthenticationException.class);

            private Matcher<String> causeDataCode = CoreMatchers.equalTo(dataCode);

            public boolean matches(Object that) {
                Throwable t = ((Throwable) (that));
                ActiveDirectoryAuthenticationException cause = ((ActiveDirectoryAuthenticationException) (t.getCause()));
                return (causeInstance.matches(cause)) && (causeDataCode.matches(cause.getDataCode()));
            }

            public void describeTo(Description desc) {
                desc.appendText("getCause() ");
                causeInstance.describeTo(desc);
                desc.appendText("getCause().getDataCode() ");
                causeDataCode.describeTo(desc);
            }
        });
        provider.authenticate(joe);
    }

    @Test(expected = CredentialsExpiredException.class)
    public void expiredPasswordIsCorrectlyMapped() {
        provider.contextFactory = createContextFactoryThrowing(new AuthenticationException(((ActiveDirectoryLdapAuthenticationProviderTests.msg) + "532, xxxx]")));
        try {
            provider.authenticate(joe);
            fail("BadCredentialsException should had been thrown");
        } catch (BadCredentialsException expected) {
        }
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.authenticate(joe);
    }

    @Test(expected = DisabledException.class)
    public void accountDisabledIsCorrectlyMapped() {
        provider.contextFactory = createContextFactoryThrowing(new AuthenticationException(((ActiveDirectoryLdapAuthenticationProviderTests.msg) + "533, xxxx]")));
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.authenticate(joe);
    }

    @Test(expected = AccountExpiredException.class)
    public void accountExpiredIsCorrectlyMapped() {
        provider.contextFactory = createContextFactoryThrowing(new AuthenticationException(((ActiveDirectoryLdapAuthenticationProviderTests.msg) + "701, xxxx]")));
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.authenticate(joe);
    }

    @Test(expected = LockedException.class)
    public void accountLockedIsCorrectlyMapped() {
        provider.contextFactory = createContextFactoryThrowing(new AuthenticationException(((ActiveDirectoryLdapAuthenticationProviderTests.msg) + "775, xxxx]")));
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.authenticate(joe);
    }

    @Test(expected = BadCredentialsException.class)
    public void unknownErrorCodeIsCorrectlyMapped() {
        provider.contextFactory = createContextFactoryThrowing(new AuthenticationException(((ActiveDirectoryLdapAuthenticationProviderTests.msg) + "999, xxxx]")));
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.authenticate(joe);
    }

    @Test(expected = BadCredentialsException.class)
    public void errorWithNoSubcodeIsHandledCleanly() throws Exception {
        provider.contextFactory = createContextFactoryThrowing(new AuthenticationException(ActiveDirectoryLdapAuthenticationProviderTests.msg));
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.authenticate(joe);
    }

    @Test(expected = CommunicationException.class)
    public void nonAuthenticationExceptionIsConvertedToSpringLdapException() throws Exception {
        provider.contextFactory = createContextFactoryThrowing(new CommunicationException(ActiveDirectoryLdapAuthenticationProviderTests.msg));
        provider.authenticate(joe);
    }

    @Test
    public void rootDnProvidedSeparatelyFromDomainAlsoWorks() throws Exception {
        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider("mydomain.eu", "ldap://192.168.1.200/", "dc=ad,dc=eu,dc=mydomain");
        checkAuthentication("dc=ad,dc=eu,dc=mydomain", provider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setContextEnvironmentPropertiesNull() {
        provider.setContextEnvironmentProperties(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setContextEnvironmentPropertiesEmpty() {
        provider.setContextEnvironmentProperties(new Hashtable<String, Object>());
    }

    @Test
    public void contextEnvironmentPropertiesUsed() throws Exception {
        Hashtable<String, Object> env = new Hashtable<>();
        env.put("java.naming.ldap.factory.socket", "unknown.package.NonExistingSocketFactory");
        provider.setContextEnvironmentProperties(env);
        try {
            provider.authenticate(joe);
            fail("CommunicationException was expected with a root cause of ClassNotFoundException");
        } catch (org.springframework.ldap expected) {
            assertThat(expected.getRootCause()).isInstanceOf(ClassNotFoundException.class);
        }
    }

    static class MockNamingEnumeration implements NamingEnumeration<SearchResult> {
        private SearchResult sr;

        public MockNamingEnumeration(SearchResult sr) {
            this.sr = sr;
        }

        public SearchResult next() {
            SearchResult result = sr;
            sr = null;
            return result;
        }

        public boolean hasMore() {
            return (sr) != null;
        }

        public void close() {
        }

        public boolean hasMoreElements() {
            return hasMore();
        }

        public SearchResult nextElement() {
            return next();
        }
    }
}

