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
package org.sonar.server.health;


import java.util.Date;
import java.util.Random;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.platform.Server;
import org.sonar.api.utils.System2;
import org.sonar.core.platform.ComponentContainer;
import org.sonar.process.NetworkUtils;
import org.sonar.process.cluster.health.SharedHealthStateImpl;
import org.sonar.process.cluster.hz.HazelcastMember;


public class NodeHealthModuleTest {
    private Random random = new Random();

    private MapSettings mapSettings = new MapSettings();

    private NodeHealthModule underTest = new NodeHealthModule();

    @Test
    public void no_broken_dependencies() {
        ComponentContainer container = new ComponentContainer();
        Server server = Mockito.mock(Server.class);
        NetworkUtils networkUtils = Mockito.mock(NetworkUtils.class);
        // settings required by NodeHealthProvider
        mapSettings.setProperty("sonar.cluster.node.name", randomAlphanumeric(3));
        mapSettings.setProperty("sonar.cluster.node.port", String.valueOf((1 + (random.nextInt(10)))));
        Mockito.when(server.getStartedAt()).thenReturn(new Date());
        Mockito.when(networkUtils.getHostname()).thenReturn(randomAlphanumeric(12));
        // upper level dependencies
        container.add(Mockito.mock(System2.class), mapSettings.asConfig(), server, networkUtils, Mockito.mock(HazelcastMember.class));
        // HealthAction dependencies
        container.add(Mockito.mock(HealthChecker.class));
        underTest.configure(container);
        container.startComponents();
    }

    @Test
    public void provides_implementation_of_SharedHealthState() {
        ComponentContainer container = new ComponentContainer();
        underTest.configure(container);
        assertThat(classesAddedToContainer(container)).contains(SharedHealthStateImpl.class);
    }
}

