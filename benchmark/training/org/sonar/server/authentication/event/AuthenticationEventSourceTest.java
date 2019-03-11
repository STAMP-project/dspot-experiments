/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.authentication.event;


import Method.BASIC;
import Method.BASIC_TOKEN;
import Method.JWT;
import Method.OAUTH2;
import Method.SSO;
import Provider.EXTERNAL;
import Provider.LOCAL;
import Provider.REALM;
import java.io.Serializable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class AuthenticationEventSourceTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void local_fails_with_NPE_if_method_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("method can't be null");
        AuthenticationEvent.Source.local(null);
    }

    @Test
    public void local_creates_source_instance_with_specified_method_and_hardcoded_provider_and_provider_name() {
        AuthenticationEvent.Source underTest = AuthenticationEvent.Source.local(BASIC_TOKEN);
        assertThat(underTest.getMethod()).isEqualTo(BASIC_TOKEN);
        assertThat(underTest.getProvider()).isEqualTo(LOCAL);
        assertThat(underTest.getProviderName()).isEqualTo("local");
    }

    @Test
    public void oauth2_fails_with_NPE_if_provider_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("identityProvider can't be null");
        AuthenticationEvent.Source.oauth2(null);
    }

    @Test
    public void oauth2_fails_with_NPE_if_providerName_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("provider name can't be null");
        AuthenticationEvent.Source.oauth2(AuthenticationEventSourceTest.newOauth2IdentityProvider(null));
    }

    @Test
    public void oauth2_fails_with_IAE_if_providerName_is_empty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("provider name can't be empty");
        AuthenticationEvent.Source.oauth2(AuthenticationEventSourceTest.newOauth2IdentityProvider(""));
    }

    @Test
    public void oauth2_creates_source_instance_with_specified_provider_name_and_hardcoded_provider_and_method() {
        AuthenticationEvent.Source underTest = AuthenticationEvent.Source.oauth2(AuthenticationEventSourceTest.newOauth2IdentityProvider("some name"));
        assertThat(underTest.getMethod()).isEqualTo(OAUTH2);
        assertThat(underTest.getProvider()).isEqualTo(EXTERNAL);
        assertThat(underTest.getProviderName()).isEqualTo("some name");
    }

    @Test
    public void realm_fails_with_NPE_if_method_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("method can't be null");
        AuthenticationEvent.Source.realm(null, "name");
    }

    @Test
    public void realm_fails_with_NPE_if_providerName_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("provider name can't be null");
        AuthenticationEvent.Source.realm(BASIC, null);
    }

    @Test
    public void realm_fails_with_IAE_if_providerName_is_empty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("provider name can't be empty");
        AuthenticationEvent.Source.realm(BASIC, "");
    }

    @Test
    public void realm_creates_source_instance_with_specified_method_and_provider_name_and_hardcoded_provider() {
        AuthenticationEvent.Source underTest = AuthenticationEvent.Source.realm(BASIC, "some name");
        assertThat(underTest.getMethod()).isEqualTo(BASIC);
        assertThat(underTest.getProvider()).isEqualTo(REALM);
        assertThat(underTest.getProviderName()).isEqualTo("some name");
    }

    @Test
    public void sso_returns_source_instance_with_hardcoded_method_provider_and_providerName() {
        AuthenticationEvent.Source underTest = AuthenticationEvent.Source.sso();
        assertThat(underTest.getMethod()).isEqualTo(SSO);
        assertThat(underTest.getProvider()).isEqualTo(Provider.SSO);
        assertThat(underTest.getProviderName()).isEqualTo("sso");
        assertThat(underTest).isSameAs(AuthenticationEvent.Source.sso());
    }

    @Test
    public void jwt_returns_source_instance_with_hardcoded_method_provider_and_providerName() {
        AuthenticationEvent.Source underTest = AuthenticationEvent.Source.jwt();
        assertThat(underTest.getMethod()).isEqualTo(JWT);
        assertThat(underTest.getProvider()).isEqualTo(Provider.JWT);
        assertThat(underTest.getProviderName()).isEqualTo("jwt");
        assertThat(underTest).isSameAs(AuthenticationEvent.Source.jwt());
    }

    @Test
    public void external_fails_with_NPE_if_provider_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("identityProvider can't be null");
        AuthenticationEvent.Source.external(null);
    }

    @Test
    public void external_fails_with_NPE_if_providerName_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("provider name can't be null");
        AuthenticationEvent.Source.external(AuthenticationEventSourceTest.newBasicIdentityProvider(null));
    }

    @Test
    public void external_fails_with_IAE_if_providerName_is_empty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("provider name can't be empty");
        AuthenticationEvent.Source.external(AuthenticationEventSourceTest.newBasicIdentityProvider(""));
    }

    @Test
    public void external_creates_source_instance_with_specified_provider_name_and_hardcoded_provider_and_method() {
        AuthenticationEvent.Source underTest = AuthenticationEvent.Source.external(AuthenticationEventSourceTest.newBasicIdentityProvider("some name"));
        assertThat(underTest.getMethod()).isEqualTo(Method.EXTERNAL);
        assertThat(underTest.getProvider()).isEqualTo(EXTERNAL);
        assertThat(underTest.getProviderName()).isEqualTo("some name");
    }

    @Test
    public void source_is_serializable() {
        assertThat(Serializable.class.isAssignableFrom(AuthenticationEvent.Source.class)).isTrue();
    }

    @Test
    public void toString_displays_all_fields() {
        assertThat(AuthenticationEvent.Source.sso().toString()).isEqualTo("Source{method=SSO, provider=SSO, providerName='sso'}");
        assertThat(AuthenticationEvent.Source.oauth2(AuthenticationEventSourceTest.newOauth2IdentityProvider("bou")).toString()).isEqualTo("Source{method=OAUTH2, provider=EXTERNAL, providerName='bou'}");
    }

    @Test
    public void source_implements_equals_on_all_fields() {
        assertThat(AuthenticationEvent.Source.sso()).isEqualTo(AuthenticationEvent.Source.sso());
        assertThat(AuthenticationEvent.Source.sso()).isNotEqualTo(AuthenticationEvent.Source.jwt());
        assertThat(AuthenticationEvent.Source.jwt()).isEqualTo(AuthenticationEvent.Source.jwt());
        assertThat(AuthenticationEvent.Source.local(BASIC)).isEqualTo(AuthenticationEvent.Source.local(BASIC));
        assertThat(AuthenticationEvent.Source.local(BASIC)).isNotEqualTo(AuthenticationEvent.Source.local(BASIC_TOKEN));
        assertThat(AuthenticationEvent.Source.local(BASIC)).isNotEqualTo(AuthenticationEvent.Source.sso());
        assertThat(AuthenticationEvent.Source.local(BASIC)).isNotEqualTo(AuthenticationEvent.Source.jwt());
        assertThat(AuthenticationEvent.Source.local(BASIC)).isNotEqualTo(AuthenticationEvent.Source.oauth2(AuthenticationEventSourceTest.newOauth2IdentityProvider("voo")));
        assertThat(AuthenticationEvent.Source.oauth2(AuthenticationEventSourceTest.newOauth2IdentityProvider("foo"))).isEqualTo(AuthenticationEvent.Source.oauth2(AuthenticationEventSourceTest.newOauth2IdentityProvider("foo")));
        assertThat(AuthenticationEvent.Source.oauth2(AuthenticationEventSourceTest.newOauth2IdentityProvider("foo"))).isNotEqualTo(AuthenticationEvent.Source.oauth2(AuthenticationEventSourceTest.newOauth2IdentityProvider("bar")));
    }
}

