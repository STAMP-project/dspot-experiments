/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.distributed.internal.membership.gms.auth;


import java.util.Properties;
import org.apache.geode.distributed.ConfigurationProperties;
import org.apache.geode.security.GemFireSecurityException;
import org.apache.geode.test.junit.categories.SecurityTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.apache.geode.distributed.internal.membership.gms.auth.AbstractGMSAuthenticatorTestCase.SpyAuthInit.getCreateCount;
import static org.apache.geode.distributed.internal.membership.gms.auth.AbstractGMSAuthenticatorTestCase.SpyAuthInit.setAuthInitialize;


/**
 * Unit tests GMSAuthenticator using new integrated security.
 */
@Category({ SecurityTest.class })
public class GMSAuthenticatorWithSecurityManagerTest extends AbstractGMSAuthenticatorTestCase {
    @Test
    public void nullManagerShouldReturnNull() throws Exception {
        assertThat(this.securityProps).doesNotContainKey(ConfigurationProperties.SECURITY_MANAGER);
        String result = this.authenticator.authenticate(this.member, this.securityProps, this.securityProps);
        assertThat(result).isNull();
    }

    @Test
    public void emptyAuthenticatorShouldReturnNull() throws Exception {
        this.securityProps.setProperty(ConfigurationProperties.SECURITY_MANAGER, "");
        String result = this.authenticator.authenticate(this.member, this.securityProps, this.securityProps);
        assertThat(result).isNull();
    }

    @Test
    public void shouldGetSecurityPropsFromDistributionConfig() throws Exception {
        this.securityProps.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, "dummy1");
        this.securityProps.setProperty(ConfigurationProperties.SECURITY_MANAGER, "dummy2");
        Properties secProps = this.authenticator.getSecurityProps();
        assertThat(secProps.size()).isEqualTo(2);
        assertThat(secProps.getProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT)).isEqualTo("dummy1");
        assertThat(secProps.getProperty(ConfigurationProperties.SECURITY_MANAGER)).isEqualTo("dummy2");
    }

    @Test
    public void usesPeerAuthInitToGetCredentials() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, ((AbstractGMSAuthenticatorTestCase.SpyAuthInit.class.getName()) + ".create"));
        this.props.setProperty(ConfigurationProperties.SECURITY_MANAGER, "dummy");
        AbstractGMSAuthenticatorTestCase.SpyAuthInit auth = new AbstractGMSAuthenticatorTestCase.SpyAuthInit();
        assertThat(auth.isClosed()).isFalse();
        setAuthInitialize(auth);
        Properties credentials = this.authenticator.getCredentials(this.member, this.props);
        assertThat(credentials).isEqualTo(this.props);
        assertThat(auth.isClosed()).isTrue();
        assertThat(((getCreateCount()) == 1)).isTrue();
    }

    @Test
    public void getCredentialsShouldReturnNullIfNoPeerAuthInit() throws Exception {
        Properties credentials = this.authenticator.getCredentials(this.member, this.props);
        assertThat(credentials).isNull();
    }

    @Test
    public void getCredentialsShouldReturnNullIfEmptyPeerAuthInit() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, "");
        Properties credentials = this.authenticator.getCredentials(this.member, this.props);
        assertThat(credentials).isNull();
    }

    @Test
    public void getCredentialsShouldThrowIfPeerAuthInitDoesNotExist() throws Exception {
        String authInit = (getClass().getName()) + "$NotExistAuth.create";
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, authInit);
        assertThatThrownBy(() -> this.authenticator.getCredentials(this.member, this.props)).hasMessageContaining("Instance could not be obtained");
    }

    @Test
    public void getCredentialsShouldThrowIfPeerAuthInitCreateReturnsNull() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, ((AbstractGMSAuthenticatorTestCase.AuthInitCreateReturnsNull.class.getName()) + ".create"));
        assertThatThrownBy(() -> this.authenticator.getCredentials(this.member, this.props)).hasMessageContaining("Instance could not be obtained from");
    }

    @Test
    public void getCredentialsShouldThrowIfPeerAuthInitGetCredentialsAndInitThrow() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, ((AbstractGMSAuthenticatorTestCase.AuthInitGetCredentialsAndInitThrow.class.getName()) + ".create"));
        assertThatThrownBy(() -> this.authenticator.getCredentials(this.member, this.props)).hasMessage("expected init error");
    }

    @Test
    public void getCredentialsShouldThrowIfPeerAuthInitGetCredentialsThrows() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, ((AbstractGMSAuthenticatorTestCase.AuthInitGetCredentialsThrows.class.getName()) + ".create"));
        assertThatThrownBy(() -> this.authenticator.getCredentials(this.member, this.props)).hasMessage("expected get credential error");
    }

    @Test
    public void authenticateShouldReturnNullIfSuccessful() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_MANAGER, "dummy");
        String result = this.authenticator.authenticate(this.member, this.props, this.props);
        assertThat(result).isNull();
    }

    @Test
    public void authenticateShouldReturnNullIfNoSecurityManager() throws Exception {
        String result = this.authenticator.authenticate(this.member, this.props, this.props);
        assertThat(result).isNull();
    }

    @Test
    public void authenticateShouldReturnFailureMessageIfLoginThrows() throws Exception {
        Mockito.when(this.securityService.login(ArgumentMatchers.any(Properties.class))).thenThrow(new GemFireSecurityException("dummy"));
        this.props.setProperty(ConfigurationProperties.SECURITY_MANAGER, "dummy");
        String result = this.authenticator.authenticate(this.member, this.props, this.props);
        assertThat(result).startsWith("Security check failed. dummy");
    }

    @Test
    public void authenticateShouldReturnFailureMessageIfNullCredentials() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_MANAGER, "dummy");
        String result = this.authenticator.authenticate(this.member, null, this.props);
        assertThat(result).startsWith("Failed to find credentials from");
    }
}

