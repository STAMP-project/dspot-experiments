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
package org.sonar.server.user;


import CoreProperties.CORE_AUTHENTICATOR_CLASS;
import CoreProperties.CORE_AUTHENTICATOR_IGNORE_STARTUP_FAILURE;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.security.LoginPasswordAuthenticator;
import org.sonar.api.security.SecurityRealm;
import org.sonar.api.utils.SonarException;


public class SecurityRealmFactoryTest {
    private MapSettings settings = new MapSettings();

    /**
     * Typical usage.
     */
    @Test
    public void should_select_realm_and_start() {
        SecurityRealm realm = Mockito.spy(new SecurityRealmFactoryTest.FakeRealm());
        settings.setProperty("sonar.security.realm", realm.getName());
        SecurityRealmFactory factory = new SecurityRealmFactory(settings.asConfig(), new SecurityRealm[]{ realm });
        factory.start();
        assertThat(factory.getRealm()).isSameAs(realm);
        assertThat(factory.hasExternalAuthentication()).isTrue();
        Mockito.verify(realm).init();
        factory.stop();
    }

    @Test
    public void do_not_fail_if_no_realms() {
        SecurityRealmFactory factory = new SecurityRealmFactory(settings.asConfig());
        factory.start();
        assertThat(factory.getRealm()).isNull();
        assertThat(factory.hasExternalAuthentication()).isFalse();
    }

    @Test
    public void realm_not_found() {
        settings.setProperty("sonar.security.realm", "Fake");
        try {
            new SecurityRealmFactory(settings.asConfig());
            Assert.fail();
        } catch (SonarException e) {
            assertThat(e.getMessage()).contains("Realm 'Fake' not found.");
        }
    }

    @Test
    public void should_provide_compatibility_for_authenticator() {
        settings.setProperty(CORE_AUTHENTICATOR_CLASS, SecurityRealmFactoryTest.FakeAuthenticator.class.getName());
        LoginPasswordAuthenticator authenticator = new SecurityRealmFactoryTest.FakeAuthenticator();
        SecurityRealmFactory factory = new SecurityRealmFactory(settings.asConfig(), new LoginPasswordAuthenticator[]{ authenticator });
        SecurityRealm realm = factory.getRealm();
        assertThat(realm).isInstanceOf(CompatibilityRealm.class);
    }

    @Test
    public void should_take_precedence_over_authenticator() {
        SecurityRealm realm = new SecurityRealmFactoryTest.FakeRealm();
        settings.setProperty("sonar.security.realm", realm.getName());
        LoginPasswordAuthenticator authenticator = new SecurityRealmFactoryTest.FakeAuthenticator();
        settings.setProperty(CORE_AUTHENTICATOR_CLASS, SecurityRealmFactoryTest.FakeAuthenticator.class.getName());
        SecurityRealmFactory factory = new SecurityRealmFactory(settings.asConfig(), new SecurityRealm[]{ realm }, new LoginPasswordAuthenticator[]{ authenticator });
        assertThat(factory.getRealm()).isSameAs(realm);
    }

    @Test
    public void authenticator_not_found() {
        settings.setProperty(CORE_AUTHENTICATOR_CLASS, "Fake");
        try {
            new SecurityRealmFactory(settings.asConfig());
            Assert.fail();
        } catch (SonarException e) {
            assertThat(e.getMessage()).contains("Authenticator 'Fake' not found.");
        }
    }

    @Test
    public void ignore_startup_failure() {
        SecurityRealm realm = Mockito.spy(new SecurityRealmFactoryTest.AlwaysFailsRealm());
        settings.setProperty("sonar.security.realm", realm.getName());
        settings.setProperty(CORE_AUTHENTICATOR_IGNORE_STARTUP_FAILURE, true);
        start();
        Mockito.verify(realm).init();
    }

    @Test
    public void should_fail() {
        SecurityRealm realm = Mockito.spy(new SecurityRealmFactoryTest.AlwaysFailsRealm());
        settings.setProperty("sonar.security.realm", realm.getName());
        try {
            start();
            Assert.fail();
        } catch (SonarException e) {
            assertThat(e.getCause()).isInstanceOf(IllegalStateException.class);
            assertThat(e.getMessage()).contains("Security realm fails to start");
        }
    }

    private static class AlwaysFailsRealm extends SecurityRealmFactoryTest.FakeRealm {
        @Override
        public void init() {
            throw new IllegalStateException();
        }
    }

    private static class FakeRealm extends SecurityRealm {
        @Override
        public LoginPasswordAuthenticator getLoginPasswordAuthenticator() {
            return null;
        }
    }

    private static class FakeAuthenticator implements LoginPasswordAuthenticator {
        public void init() {
        }

        public boolean authenticate(String login, String password) {
            return false;
        }
    }
}

