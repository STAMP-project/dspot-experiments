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


import Health.Builder;
import Health.Status;
import NodeDetails.Type.APPLICATION;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.platform.Server;
import org.sonar.process.NetworkUtils;
import org.sonar.process.cluster.health.NodeHealth;


public class NodeHealthProviderImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final Random random = new Random();

    private MapSettings mapSettings = new MapSettings();

    private HealthChecker healthChecker = Mockito.mock(HealthChecker.class);

    private Server server = Mockito.mock(Server.class);

    private NetworkUtils networkUtils = Mockito.mock(NetworkUtils.class);

    @Test
    public void constructor_throws_ISE_if_node_name_property_is_not_set() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Property sonar.cluster.node.name is not defined");
        new NodeHealthProviderImpl(mapSettings.asConfig(), healthChecker, server, networkUtils);
    }

    @Test
    public void constructor_thows_NPE_if_NetworkUtils_getHostname_returns_null() {
        mapSettings.setProperty(CLUSTER_NODE_NAME.getKey(), randomAlphanumeric(3));
        expectedException.expect(NullPointerException.class);
        new NodeHealthProviderImpl(mapSettings.asConfig(), healthChecker, server, networkUtils);
    }

    @Test
    public void constructor_throws_ISE_if_node_port_property_is_not_set() {
        mapSettings.setProperty(CLUSTER_NODE_NAME.getKey(), randomAlphanumeric(3));
        Mockito.when(networkUtils.getHostname()).thenReturn(randomAlphanumeric(23));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Property sonar.cluster.node.port is not defined");
        new NodeHealthProviderImpl(mapSettings.asConfig(), healthChecker, server, networkUtils);
    }

    @Test
    public void constructor_throws_NPE_is_Server_getStartedAt_is_null() {
        setRequiredPropertiesForConstructor();
        expectedException.expect(NullPointerException.class);
        new NodeHealthProviderImpl(mapSettings.asConfig(), healthChecker, server, networkUtils);
    }

    @Test
    public void get_returns_HEALTH_status_and_causes_from_HealthChecker_checkNode() {
        setRequiredPropertiesForConstructor();
        setStartedAt();
        Mockito.when(networkUtils.getHostname()).thenReturn(randomAlphanumeric(4));
        Health.Status randomStatus = Status.values()[random.nextInt(Status.values().length)];
        String[] expected = IntStream.range(0, random.nextInt(4)).mapToObj(( s) -> randomAlphabetic(55)).toArray(String[]::new);
        Health.Builder healthBuilder = Health.newHealthCheckBuilder().setStatus(randomStatus);
        Arrays.stream(expected).forEach(healthBuilder::addCause);
        Mockito.when(healthChecker.checkNode()).thenReturn(healthBuilder.build());
        NodeHealthProviderImpl underTest = new NodeHealthProviderImpl(mapSettings.asConfig(), healthChecker, server, networkUtils);
        NodeHealth nodeHealth = underTest.get();
        assertThat(nodeHealth.getStatus().name()).isEqualTo(randomStatus.name());
        assertThat(nodeHealth.getCauses()).containsOnly(expected);
    }

    @Test
    public void get_returns_APPLICATION_type() {
        setRequiredPropertiesForConstructor();
        setStartedAt();
        Mockito.when(networkUtils.getHostname()).thenReturn(randomAlphanumeric(23));
        Mockito.when(healthChecker.checkNode()).thenReturn(Health.newHealthCheckBuilder().setStatus(Status.values()[random.nextInt(Status.values().length)]).build());
        NodeHealthProviderImpl underTest = new NodeHealthProviderImpl(mapSettings.asConfig(), healthChecker, server, networkUtils);
        NodeHealth nodeHealth = underTest.get();
        assertThat(nodeHealth.getDetails().getType()).isEqualTo(APPLICATION);
    }

    @Test
    public void get_returns_name_and_port_from_properties_at_constructor_time() {
        String name = randomAlphanumeric(3);
        int port = 1 + (random.nextInt(4));
        mapSettings.setProperty(CLUSTER_NODE_NAME.getKey(), name);
        mapSettings.setProperty(CLUSTER_NODE_HZ_PORT.getKey(), port);
        setStartedAt();
        Mockito.when(healthChecker.checkNode()).thenReturn(Health.newHealthCheckBuilder().setStatus(Status.values()[random.nextInt(Status.values().length)]).build());
        Mockito.when(networkUtils.getHostname()).thenReturn(randomAlphanumeric(3));
        NodeHealthProviderImpl underTest = new NodeHealthProviderImpl(mapSettings.asConfig(), healthChecker, server, networkUtils);
        NodeHealth nodeHealth = underTest.get();
        assertThat(nodeHealth.getDetails().getName()).isEqualTo(name);
        assertThat(nodeHealth.getDetails().getPort()).isEqualTo(port);
        // change values in properties
        setRequiredPropertiesForConstructor();
        NodeHealth newNodeHealth = underTest.get();
        assertThat(newNodeHealth.getDetails().getName()).isEqualTo(name);
        assertThat(newNodeHealth.getDetails().getPort()).isEqualTo(port);
    }

    @Test
    public void get_returns_host_from_property_if_set_at_constructor_time() {
        String host = randomAlphanumeric(4);
        mapSettings.setProperty(CLUSTER_NODE_NAME.getKey(), randomAlphanumeric(3));
        mapSettings.setProperty(CLUSTER_NODE_HZ_PORT.getKey(), (1 + (random.nextInt(4))));
        mapSettings.setProperty(CLUSTER_NODE_HOST.getKey(), host);
        setStartedAt();
        Mockito.when(healthChecker.checkNode()).thenReturn(Health.newHealthCheckBuilder().setStatus(Status.values()[random.nextInt(Status.values().length)]).build());
        NodeHealthProviderImpl underTest = new NodeHealthProviderImpl(mapSettings.asConfig(), healthChecker, server, networkUtils);
        NodeHealth nodeHealth = underTest.get();
        assertThat(nodeHealth.getDetails().getHost()).isEqualTo(host);
        // change values in properties
        mapSettings.setProperty(CLUSTER_NODE_HOST.getKey(), randomAlphanumeric(66));
        NodeHealth newNodeHealth = underTest.get();
        assertThat(newNodeHealth.getDetails().getHost()).isEqualTo(host);
    }

    @Test
    public void get_returns_hostname_from_NetworkUtils_if_property_is_not_set_at_constructor_time() {
        getReturnsHostnameFromNetworkUtils(null);
    }

    @Test
    public void get_returns_hostname_from_NetworkUtils_if_property_is_empty_at_constructor_time() {
        getReturnsHostnameFromNetworkUtils((random.nextBoolean() ? "" : "   "));
    }

    @Test
    public void get_returns_started_from_server_startedAt_at_constructor_time() {
        setRequiredPropertiesForConstructor();
        Mockito.when(networkUtils.getHostname()).thenReturn(randomAlphanumeric(4));
        Date date = new Date();
        Mockito.when(server.getStartedAt()).thenReturn(date);
        Mockito.when(healthChecker.checkNode()).thenReturn(Health.newHealthCheckBuilder().setStatus(Status.values()[random.nextInt(Status.values().length)]).build());
        NodeHealthProviderImpl underTest = new NodeHealthProviderImpl(mapSettings.asConfig(), healthChecker, server, networkUtils);
        NodeHealth nodeHealth = underTest.get();
        assertThat(nodeHealth.getDetails().getStartedAt()).isEqualTo(date.getTime());
        // change startedAt value
        setStartedAt();
        NodeHealth newNodeHealth = underTest.get();
        assertThat(newNodeHealth.getDetails().getStartedAt()).isEqualTo(date.getTime());
    }
}

