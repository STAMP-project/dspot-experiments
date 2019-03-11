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
package org.sonar.server.platform.monitoring.cluster;


import ProtobufSystemInfo.Section;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.platform.Server;
import org.sonar.api.security.SecurityRealm;
import org.sonar.process.systeminfo.protobuf.ProtobufSystemInfo;
import org.sonar.server.authentication.IdentityProviderRepositoryRule;
import org.sonar.server.authentication.TestIdentityProvider;
import org.sonar.server.platform.monitoring.SystemInfoTesting;
import org.sonar.server.user.SecurityRealmFactory;


public class GlobalSystemSectionTest {
    @Rule
    public IdentityProviderRepositoryRule identityProviderRepository = new IdentityProviderRepositoryRule();

    private MapSettings settings = new MapSettings();

    private Server server = Mockito.mock(Server.class);

    private SecurityRealmFactory securityRealmFactory = Mockito.mock(SecurityRealmFactory.class);

    private GlobalSystemSection underTest = new GlobalSystemSection(settings.asConfig(), server, securityRealmFactory, identityProviderRepository);

    @Test
    public void name_is_not_empty() {
        assertThat(underTest.toProtobuf().getName()).isEqualTo("System");
    }

    @Test
    public void get_realm() {
        SecurityRealm realm = Mockito.mock(SecurityRealm.class);
        Mockito.when(realm.getName()).thenReturn("LDAP");
        Mockito.when(securityRealmFactory.getRealm()).thenReturn(realm);
        ProtobufSystemInfo.Section protobuf = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(protobuf, "External User Authentication", "LDAP");
    }

    @Test
    public void no_realm() {
        Mockito.when(securityRealmFactory.getRealm()).thenReturn(null);
        ProtobufSystemInfo.Section protobuf = underTest.toProtobuf();
        assertThat(attribute(protobuf, "External User Authentication")).isNull();
    }

    @Test
    public void get_enabled_identity_providers() {
        identityProviderRepository.addIdentityProvider(new TestIdentityProvider().setKey("github").setName("GitHub").setEnabled(true));
        identityProviderRepository.addIdentityProvider(new TestIdentityProvider().setKey("bitbucket").setName("Bitbucket").setEnabled(true));
        identityProviderRepository.addIdentityProvider(new TestIdentityProvider().setKey("disabled").setName("Disabled").setEnabled(false));
        ProtobufSystemInfo.Section protobuf = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(protobuf, "Accepted external identity providers", "Bitbucket, GitHub");
    }

    @Test
    public void get_enabled_identity_providers_allowing_users_to_signup() {
        identityProviderRepository.addIdentityProvider(new TestIdentityProvider().setKey("github").setName("GitHub").setEnabled(true).setAllowsUsersToSignUp(true));
        identityProviderRepository.addIdentityProvider(new TestIdentityProvider().setKey("bitbucket").setName("Bitbucket").setEnabled(true).setAllowsUsersToSignUp(false));
        identityProviderRepository.addIdentityProvider(new TestIdentityProvider().setKey("disabled").setName("Disabled").setEnabled(false).setAllowsUsersToSignUp(true));
        ProtobufSystemInfo.Section protobuf = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(protobuf, "External identity providers whose users are allowed to sign themselves up", "GitHub");
    }
}

