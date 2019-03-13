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
package org.sonar.server.user.ws;


import ProcessProperties.Property.SONARCLOUD_ENABLED;
import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.core.platform.ComponentContainer;


public class UsersWsModuleTest {
    private MapSettings settings = new MapSettings();

    @Test
    public void verify_count_of_added_components() {
        ComponentContainer container = new ComponentContainer();
        new UsersWsModule(new org.sonar.api.config.internal.ConfigurationBridge(settings)).configure(container);
        assertThat(container.size()).isEqualTo((2 + 15));
    }

    @Test
    public void verify_count_of_added_components_in_sonarcloud() {
        settings.setProperty(SONARCLOUD_ENABLED.getKey(), true);
        ComponentContainer container = new ComponentContainer();
        new UsersWsModule(new org.sonar.api.config.internal.ConfigurationBridge(settings)).configure(container);
        assertThat(container.size()).isEqualTo((2 + 16));
    }
}

