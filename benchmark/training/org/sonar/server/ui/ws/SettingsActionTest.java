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
package org.sonar.server.ui.ws;


import ProcessProperties.Property.SONAR_UPDATECENTER_ACTIVATE;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class SettingsActionTest {
    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    private MapSettings settings = new MapSettings();

    private WsActionTester ws;

    @Test
    public void empty() {
        init();
        logInAsSystemAdministrator();
        executeAndVerify("empty.json");
    }

    @Test
    public void returns_page_settings() {
        init(createPages());
        logInAsSystemAdministrator();
        executeAndVerify("with_pages.json");
    }

    @Test
    public void returns_update_center_settings() {
        init();
        settings.setProperty(SONAR_UPDATECENTER_ACTIVATE.getKey(), true);
        logInAsSystemAdministrator();
        executeAndVerify("with_update_center.json");
    }

    @Test
    public void request_succeeds_but_settings_are_not_returned_when_user_is_not_system_administrator() {
        init(createPages());
        settings.setProperty(SONAR_UPDATECENTER_ACTIVATE.getKey(), true);
        userSessionRule.logIn().setNonSystemAdministrator();
        executeAndVerify("empty.json");
    }
}

