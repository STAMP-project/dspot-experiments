/**
 * Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.security.saml;


import com.netflix.genie.test.categories.UnitTest;
import io.micrometer.core.instrument.Timer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;


/**
 * Tests for the SAMLUserDetailsImpl class. Makes sure we're pulling the right attributes and authorities from the
 * SAML assertion.
 *
 * @author tgianos
 * @since 3.0.0
 */
@Category(UnitTest.class)
public class SAMLUserDetailsServiceImplUnitTests {
    private static final String USER_ATTRIBUTE_NAME = UUID.randomUUID().toString();

    private static final String GROUP_ATTRIBUTE_NAME = UUID.randomUUID().toString();

    private static final String ADMIN_GROUP = UUID.randomUUID().toString();

    private static final String USER_ID = UUID.randomUUID().toString();

    private static final String[] GROUPS = new String[]{ UUID.randomUUID().toString(), SAMLUserDetailsServiceImplUnitTests.ADMIN_GROUP, UUID.randomUUID().toString() };

    private SAMLUserDetailsServiceImpl service;

    private Timer loadAuthenticationTimer;

    /**
     * Test to make sure a null credential throws exception.
     */
    @Test(expected = UsernameNotFoundException.class)
    public void doesThrowErrorOnNullCredential() {
        this.service.loadUserBySAML(null);
        Mockito.verify(this.loadAuthenticationTimer, Mockito.times(1)).record(Mockito.anyLong(), Mockito.eq(TimeUnit.NANOSECONDS));
    }

    /**
     * Make sure if no username is found an exception is thrown.
     */
    @Test(expected = UsernameNotFoundException.class)
    public void doesThrowErrorOnUserIdNotFound() {
        final SAMLCredential credential = Mockito.mock(SAMLCredential.class);
        Mockito.when(credential.getAttributeAsString(Mockito.eq(SAMLUserDetailsServiceImplUnitTests.USER_ATTRIBUTE_NAME))).thenReturn(null);
        this.service.loadUserBySAML(credential);
        Mockito.verify(this.loadAuthenticationTimer, Mockito.times(1)).record(Mockito.anyLong(), Mockito.eq(TimeUnit.NANOSECONDS));
    }

    /**
     * Make sure if no groups are found but a user id is that the user logs in but only gets role user.
     */
    @Test
    public void canLoadUserWithoutGroups() {
        final SAMLCredential credential = Mockito.mock(SAMLCredential.class);
        Mockito.when(credential.getAttributeAsString(Mockito.eq(SAMLUserDetailsServiceImplUnitTests.USER_ATTRIBUTE_NAME))).thenReturn(SAMLUserDetailsServiceImplUnitTests.USER_ID);
        Mockito.when(credential.getAttributeAsStringArray(Mockito.eq(SAMLUserDetailsServiceImplUnitTests.GROUP_ATTRIBUTE_NAME))).thenReturn(null);
        final Object result = this.service.loadUserBySAML(credential);
        Assert.assertThat(result, Matchers.notNullValue());
        Assert.assertTrue((result instanceof User));
        final User user = ((User) (result));
        Assert.assertThat(user.getUsername(), Matchers.is(SAMLUserDetailsServiceImplUnitTests.USER_ID));
        Assert.assertThat(user.getAuthorities(), Matchers.contains(new SimpleGrantedAuthority("ROLE_USER")));
        Mockito.verify(this.loadAuthenticationTimer, Mockito.times(1)).record(Mockito.anyLong(), Mockito.eq(TimeUnit.NANOSECONDS));
    }

    /**
     * Make sure if user logs in and has admin group they get admin rights.
     */
    @Test
    public void canLoadUserWithAdminGroup() {
        final SAMLCredential credential = Mockito.mock(SAMLCredential.class);
        Mockito.when(credential.getAttributeAsString(Mockito.eq(SAMLUserDetailsServiceImplUnitTests.USER_ATTRIBUTE_NAME))).thenReturn(SAMLUserDetailsServiceImplUnitTests.USER_ID);
        Mockito.when(credential.getAttributeAsStringArray(Mockito.eq(SAMLUserDetailsServiceImplUnitTests.GROUP_ATTRIBUTE_NAME))).thenReturn(SAMLUserDetailsServiceImplUnitTests.GROUPS);
        final Object result = this.service.loadUserBySAML(credential);
        Assert.assertThat(result, Matchers.notNullValue());
        Assert.assertTrue((result instanceof User));
        final User user = ((User) (result));
        Assert.assertThat(user.getUsername(), Matchers.is(SAMLUserDetailsServiceImplUnitTests.USER_ID));
        Assert.assertThat(user.getAuthorities(), Matchers.hasItems(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")));
        Mockito.verify(this.loadAuthenticationTimer, Mockito.times(1)).record(Mockito.anyLong(), Mockito.eq(TimeUnit.NANOSECONDS));
    }

    /**
     * Make sure if user logs in and doesn't have admin group user only gets user role.
     */
    @Test
    public void canLoadUserWithoutAdminGroup() {
        final SAMLCredential credential = Mockito.mock(SAMLCredential.class);
        Mockito.when(credential.getAttributeAsString(Mockito.eq(SAMLUserDetailsServiceImplUnitTests.USER_ATTRIBUTE_NAME))).thenReturn(SAMLUserDetailsServiceImplUnitTests.USER_ID);
        Mockito.when(credential.getAttributeAsStringArray(Mockito.eq(SAMLUserDetailsServiceImplUnitTests.GROUP_ATTRIBUTE_NAME))).thenReturn(new String[]{ UUID.randomUUID().toString(), UUID.randomUUID().toString() });
        final Object result = this.service.loadUserBySAML(credential);
        Assert.assertThat(result, Matchers.notNullValue());
        Assert.assertTrue((result instanceof User));
        final User user = ((User) (result));
        Assert.assertThat(user.getUsername(), Matchers.is(SAMLUserDetailsServiceImplUnitTests.USER_ID));
        Assert.assertThat(user.getAuthorities(), Matchers.contains(new SimpleGrantedAuthority("ROLE_USER")));
        Assert.assertThat(user.getAuthorities().size(), Matchers.is(1));
        Mockito.verify(this.loadAuthenticationTimer, Mockito.times(1)).record(Mockito.anyLong(), Mockito.eq(TimeUnit.NANOSECONDS));
    }
}

