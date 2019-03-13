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
package org.sonar.server.platform.monitoring;


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
import org.sonar.server.log.ServerLogging;
import org.sonar.server.platform.OfficialDistribution;
import org.sonar.server.user.SecurityRealmFactory;


public class StandaloneSystemSectionTest {
    private static final String SERVER_ID_PROPERTY = "Server ID";

    private static final String SERVER_ID_VALIDATED_PROPERTY = "Server ID validated";

    @Rule
    public IdentityProviderRepositoryRule identityProviderRepository = new IdentityProviderRepositoryRule();

    private MapSettings settings = new MapSettings();

    private Server server = Mockito.mock(Server.class);

    private ServerLogging serverLogging = Mockito.mock(ServerLogging.class);

    private SecurityRealmFactory securityRealmFactory = Mockito.mock(SecurityRealmFactory.class);

    private OfficialDistribution officialDistribution = Mockito.mock(OfficialDistribution.class);

    private StandaloneSystemSection underTest = new StandaloneSystemSection(settings.asConfig(), securityRealmFactory, identityProviderRepository, server, serverLogging, officialDistribution);

    @Test
    public void name_is_not_empty() {
        assertThat(underTest.name()).isNotEmpty();
    }

    @Test
    public void test_getServerId() {
        Mockito.when(server.getId()).thenReturn("ABC");
        assertThat(underTest.getServerId()).isEqualTo("ABC");
    }

    @Test
    public void official_distribution() {
        Mockito.when(officialDistribution.check()).thenReturn(true);
        ProtobufSystemInfo.Section protobuf = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(protobuf, "Official Distribution", true);
    }

    @Test
    public void not_an_official_distribution() {
        Mockito.when(officialDistribution.check()).thenReturn(false);
        ProtobufSystemInfo.Section protobuf = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(protobuf, "Official Distribution", false);
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

    @Test
    public void return_nb_of_processors() {
        ProtobufSystemInfo.Section protobuf = underTest.toProtobuf();
        assertThat(attribute(protobuf, "Processors").getLongValue()).isGreaterThan(0);
    }
}

