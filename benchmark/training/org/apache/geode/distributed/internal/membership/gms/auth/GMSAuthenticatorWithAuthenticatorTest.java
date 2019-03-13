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
import org.apache.geode.test.junit.categories.SecurityTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.apache.geode.distributed.internal.membership.gms.auth.AbstractGMSAuthenticatorTestCase.SpyAuthInit.setAuthInitialize;
import static org.apache.geode.distributed.internal.membership.gms.auth.AbstractGMSAuthenticatorTestCase.SpyAuthenticator.getCreateCount;
import static org.apache.geode.distributed.internal.membership.gms.auth.AbstractGMSAuthenticatorTestCase.SpyAuthenticator.setAuthenticator;


/**
 * Unit tests GMSAuthenticator using old security.
 */
@Category({ SecurityTest.class })
public class GMSAuthenticatorWithAuthenticatorTest extends AbstractGMSAuthenticatorTestCase {
    @Test
    public void nullAuthenticatorShouldReturnNull() throws Exception {
        assertThat(this.securityProps).doesNotContainKey(ConfigurationProperties.SECURITY_PEER_AUTHENTICATOR);
        String result = this.authenticator.authenticate(this.member, this.securityProps, this.securityProps);
        // assertThat(result).isNull(); NOTE: old security used to return null
        assertThat(result).contains("Security check failed");
    }

    @Test
    public void emptyAuthenticatorShouldReturnNull() throws Exception {
        this.securityProps.setProperty(ConfigurationProperties.SECURITY_PEER_AUTHENTICATOR, "");
        String result = this.authenticator.authenticate(this.member, this.securityProps, this.securityProps);
        // assertThat(result).isNull(); NOTE: old security used to return null
        assertThat(result).contains("Security check failed");
    }

    @Test
    public void shouldGetSecurityPropsFromDistributionConfig() throws Exception {
        this.securityProps.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, "dummy1");
        this.securityProps.setProperty(ConfigurationProperties.SECURITY_PEER_AUTHENTICATOR, "dummy2");
        Properties secProps = this.authenticator.getSecurityProps();
        assertThat(secProps.size()).isEqualTo(2);
        assertThat(secProps.getProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT)).isEqualTo("dummy1");
        assertThat(secProps.getProperty(ConfigurationProperties.SECURITY_PEER_AUTHENTICATOR)).isEqualTo("dummy2");
    }

    @Test
    public void usesPeerAuthInitToGetCredentials() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, ((AbstractGMSAuthenticatorTestCase.SpyAuthInit.class.getName()) + ".create"));
        AbstractGMSAuthenticatorTestCase.SpyAuthInit auth = new AbstractGMSAuthenticatorTestCase.SpyAuthInit();
        assertThat(auth.isClosed()).isFalse();
        setAuthInitialize(auth);
        Properties credentials = this.authenticator.getCredentials(this.member, this.props);
        assertThat(credentials).isEqualTo(this.props);
        assertThat(auth.isClosed()).isTrue();
        assertThat(AbstractGMSAuthenticatorTestCase.SpyAuthInit.getCreateCount()).isEqualTo(1);
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
        assertThatThrownBy(() -> this.authenticator.getCredentials(this.member, this.props)).hasMessageContaining("Instance could not be obtained from");
    }

    @Test
    public void getCredentialsShouldThrowIfPeerAuthInitCreateReturnsNull() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, ((AbstractGMSAuthenticatorTestCase.AuthInitCreateReturnsNull.class.getName()) + ".create"));
        assertThatThrownBy(() -> this.authenticator.getCredentials(this.member, this.props)).hasMessageContaining("Instance could not be obtained from");
    }

    @Test
    public void getCredentialsShouldThrowIfPeerAuthInitGetCredentialsAndInitThrow() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, ((AbstractGMSAuthenticatorTestCase.AuthInitGetCredentialsAndInitThrow.class.getName()) + ".create"));
        assertThatThrownBy(() -> this.authenticator.getCredentials(this.member, this.props)).hasMessageContaining("expected init error");
    }

    @Test
    public void getCredentialsShouldThrowIfPeerAuthInitGetCredentialsThrows() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTH_INIT, ((AbstractGMSAuthenticatorTestCase.AuthInitGetCredentialsThrows.class.getName()) + ".create"));
        assertThatThrownBy(() -> this.authenticator.getCredentials(this.member, this.props)).hasMessageContaining("expected get credential error");
    }

    @Test
    public void authenticateShouldReturnNullIfSuccessful() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTHENTICATOR, ((AbstractGMSAuthenticatorTestCase.SpyAuthenticator.class.getName()) + ".create"));
        AbstractGMSAuthenticatorTestCase.SpyAuthenticator auth = new AbstractGMSAuthenticatorTestCase.SpyAuthenticator();
        assertThat(auth.isClosed()).isFalse();
        setAuthenticator(auth);
        String result = this.authenticator.authenticate(this.member, this.props, this.props);
        assertThat(result).isNull();
        assertThat(auth.isClosed()).isTrue();
        assertThat(((getCreateCount()) == 1)).isTrue();
    }

    @Test
    public void authenticateShouldReturnNullIfPeerAuthenticatorIsNull() throws Exception {
        String result = this.authenticator.authenticate(this.member, this.props, this.props);
        // assertThat(result).isNull(); // NOTE: old security used to return null
        assertThat(result).contains("Security check failed. Instance could not be obtained from null");
    }

    @Test
    public void authenticateShouldReturnNullIfPeerAuthenticatorIsEmpty() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTHENTICATOR, "");
        String result = this.authenticator.authenticate(this.member, this.props, this.props);
        // assertThat(result).isNull(); // NOTE: old security used to return null
        assertThat(result).contains("Security check failed. Instance could not be obtained from");
    }

    @Test
    public void authenticateShouldReturnFailureMessageIfPeerAuthenticatorDoesNotExist() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTHENTICATOR, ((getClass().getName()) + "$NotExistAuth.create"));
        String result = this.authenticator.authenticate(this.member, this.props, this.props);
        assertThat(result).startsWith("Security check failed. Instance could not be obtained from");
    }

    @Test
    public void authenticateShouldReturnFailureMessageIfAuthenticateReturnsNull() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTHENTICATOR, ((AbstractGMSAuthenticatorTestCase.AuthenticatorReturnsNulls.class.getName()) + ".create"));
        String result = this.authenticator.authenticate(this.member, this.props, this.props);
        assertThat(result).startsWith("Security check failed. Instance could not be obtained");
    }

    @Test
    public void authenticateShouldReturnFailureMessageIfNullCredentials() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTHENTICATOR, ((AbstractGMSAuthenticatorTestCase.AuthenticatorReturnsNulls.class.getName()) + ".create"));
        String result = this.authenticator.authenticate(this.member, null, this.props);
        assertThat(result).startsWith("Failed to find credentials from");
    }

    @Test
    public void authenticateShouldReturnFailureMessageIfAuthenticateInitThrows() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTHENTICATOR, ((AbstractGMSAuthenticatorTestCase.AuthenticatorInitThrows.class.getName()) + ".create"));
        String result = this.authenticator.authenticate(this.member, this.props, this.props);
        assertThat(result).startsWith("Security check failed. expected init error");
    }

    @Test
    public void authenticateShouldReturnFailureMessageIfAuthenticateThrows() throws Exception {
        this.props.setProperty(ConfigurationProperties.SECURITY_PEER_AUTHENTICATOR, ((AbstractGMSAuthenticatorTestCase.AuthenticatorAuthenticateThrows.class.getName()) + ".create"));
        String result = this.authenticator.authenticate(this.member, this.props, this.props);
        assertThat(result).startsWith("Security check failed. expected authenticate error");
    }
}

