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
package org.sonar.server.platform.ws;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.core.platform.ComponentContainer;
import org.sonar.server.platform.WebServer;


@RunWith(DataProviderRunner.class)
public class ChangeLogLevelActionModuleTest {
    private WebServer webServer = Mockito.mock(WebServer.class);

    private MapSettings settings = new MapSettings();

    private ChangeLogLevelActionModule underTest = new ChangeLogLevelActionModule(webServer, settings.asConfig());

    @Test
    public void provide_returns_ChangeLogLevelStandaloneService_on_SonarCloud() {
        Mockito.when(webServer.isStandalone()).thenReturn(false);
        settings.setProperty("sonar.sonarcloud.enabled", true);
        ComponentContainer container = new ComponentContainer();
        underTest.configure(container);
        verifyInStandaloneSQ(container);
    }
}

