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
package org.sonar.server.setting.ws;


import WebService.Action;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.config.Encryption;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.server.ws.WebService;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Settings.EncryptWsResponse;


public class EncryptActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private MapSettings settings = new MapSettings();

    private Encryption encryption = settings.getEncryption();

    private EncryptAction underTest = new EncryptAction(userSession, settings);

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void json_example() {
        logInAsSystemAdministrator();
        String result = ws.newRequest().setParam("value", "my value").execute().getInput();
        assertJson(result).isSimilarTo(ws.getDef().responseExampleAsString());
    }

    @Test
    public void encrypt() {
        logInAsSystemAdministrator();
        EncryptWsResponse result = call("my value!");
        assertThat(result.getEncryptedValue()).isEqualTo("{aes}NoofntibpMBdhkMfXQxYcA==");
    }

    @Test
    public void definition() {
        WebService.Action definition = ws.getDef();
        assertThat(definition.key()).isEqualTo("encrypt");
        assertThat(definition.isPost()).isFalse();
        assertThat(definition.isInternal()).isTrue();
        assertThat(definition.responseExampleAsString()).isNotEmpty();
        assertThat(definition.params()).hasSize(1);
    }

    @Test
    public void throw_ForbiddenException_if_not_system_administrator() {
        userSession.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        call("my value");
    }

    @Test
    public void fail_if_value_is_not_provided() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        call(null);
    }

    @Test
    public void fail_if_value_is_empty() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'value' parameter is missing");
        call("  ");
    }

    @Test
    public void fail_if_no_secret_key_available() {
        logInAsSystemAdministrator();
        encryption.setPathToSecretKey("unknown/path/to/secret/key");
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("No secret key available");
        call("my value");
    }
}

