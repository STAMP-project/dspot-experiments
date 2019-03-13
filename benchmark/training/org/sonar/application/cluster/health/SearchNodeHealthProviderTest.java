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
package org.sonar.application.cluster.health;


import NodeHealth.Status.GREEN;
import NodeHealth.Status.RED;
import ProcessId.ELASTICSEARCH;
import SearchNodeHealthProvider.Clock;
import java.util.Properties;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.application.cluster.ClusterAppState;
import org.sonar.process.NetworkUtils;
import org.sonar.process.Props;
import org.sonar.process.cluster.health.NodeHealth;


public class SearchNodeHealthProviderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final Random random = new Random();

    private Clock clock = Mockito.mock(Clock.class);

    private NetworkUtils networkUtils = Mockito.mock(NetworkUtils.class);

    private ClusterAppState clusterAppState = Mockito.mock(ClusterAppState.class);

    @Test
    public void constructor_throws_IAE_if_property_node_name_is_not_set() {
        Props props = new Props(new Properties());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Missing property: sonar.cluster.node.name");
        new SearchNodeHealthProvider(props, clusterAppState, networkUtils);
    }

    @Test
    public void constructor_throws_NPE_if_NetworkUtils_getHostname_returns_null_and_property_is_not_set() {
        Properties properties = new Properties();
        properties.put(CLUSTER_NODE_NAME.getKey(), randomAlphanumeric(3));
        Props props = new Props(properties);
        expectedException.expect(NullPointerException.class);
        new SearchNodeHealthProvider(props, clusterAppState, networkUtils, clock);
    }

    @Test
    public void constructor_throws_IAE_if_property_node_port_is_not_set() {
        Properties properties = new Properties();
        properties.put(CLUSTER_NODE_NAME.getKey(), randomAlphanumeric(3));
        Mockito.when(networkUtils.getHostname()).thenReturn(randomAlphanumeric(34));
        Props props = new Props(properties);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Missing property: sonar.cluster.node.port");
        new SearchNodeHealthProvider(props, clusterAppState, networkUtils, clock);
    }

    @Test
    public void constructor_throws_FormatException_if_property_node_port_is_not_an_integer() {
        String port = randomAlphabetic(3);
        Properties properties = new Properties();
        properties.put(CLUSTER_NODE_NAME.getKey(), randomAlphanumeric(3));
        properties.put(CLUSTER_NODE_HZ_PORT.getKey(), port);
        Mockito.when(networkUtils.getHostname()).thenReturn(randomAlphanumeric(34));
        Props props = new Props(properties);
        expectedException.expect(NumberFormatException.class);
        expectedException.expectMessage((("For input string: \"" + port) + "\""));
        new SearchNodeHealthProvider(props, clusterAppState, networkUtils, clock);
    }

    @Test
    public void get_returns_name_and_port_from_properties_at_constructor_time() {
        String name = randomAlphanumeric(3);
        int port = 1 + (random.nextInt(4));
        Properties properties = new Properties();
        properties.setProperty(CLUSTER_NODE_NAME.getKey(), name);
        properties.setProperty(CLUSTER_NODE_HZ_PORT.getKey(), String.valueOf(port));
        Mockito.when(networkUtils.getHostname()).thenReturn(randomAlphanumeric(34));
        Mockito.when(clock.now()).thenReturn((1L + (random.nextInt(87))));
        SearchNodeHealthProvider underTest = new SearchNodeHealthProvider(new Props(properties), clusterAppState, networkUtils, clock);
        NodeHealth nodeHealth = underTest.get();
        assertThat(nodeHealth.getDetails().getName()).isEqualTo(name);
        assertThat(nodeHealth.getDetails().getPort()).isEqualTo(port);
        // change values in properties
        properties.setProperty(CLUSTER_NODE_NAME.getKey(), randomAlphanumeric(6));
        properties.setProperty(CLUSTER_NODE_HZ_PORT.getKey(), String.valueOf((1 + (random.nextInt(99)))));
        NodeHealth newNodeHealth = underTest.get();
        assertThat(newNodeHealth.getDetails().getName()).isEqualTo(name);
        assertThat(newNodeHealth.getDetails().getPort()).isEqualTo(port);
    }

    @Test
    public void get_returns_host_from_property_if_set_at_constructor_time() {
        String host = randomAlphanumeric(55);
        Properties properties = new Properties();
        properties.setProperty(CLUSTER_NODE_NAME.getKey(), randomAlphanumeric(3));
        properties.setProperty(CLUSTER_NODE_HZ_PORT.getKey(), String.valueOf((1 + (random.nextInt(4)))));
        properties.setProperty(CLUSTER_NODE_HOST.getKey(), host);
        Mockito.when(clock.now()).thenReturn((1L + (random.nextInt(87))));
        SearchNodeHealthProvider underTest = new SearchNodeHealthProvider(new Props(properties), clusterAppState, networkUtils, clock);
        NodeHealth nodeHealth = underTest.get();
        assertThat(nodeHealth.getDetails().getHost()).isEqualTo(host);
        // change now
        properties.setProperty(CLUSTER_NODE_HOST.getKey(), randomAlphanumeric(96));
        NodeHealth newNodeHealth = underTest.get();
        assertThat(newNodeHealth.getDetails().getHost()).isEqualTo(host);
    }

    @Test
    public void get_returns_host_from_NetworkUtils_getHostname_if_property_is_not_set_at_constructor_time() {
        getReturnsHostFromNetworkUtils(null);
    }

    @Test
    public void get_returns_host_from_NetworkUtils_getHostname_if_property_is_empty_at_constructor_time() {
        getReturnsHostFromNetworkUtils((random.nextBoolean() ? "" : "   "));
    }

    @Test
    public void get_returns_started_from_System2_now_at_constructor_time() {
        Properties properties = new Properties();
        long now = setRequiredPropertiesAndMocks(properties);
        SearchNodeHealthProvider underTest = new SearchNodeHealthProvider(new Props(properties), clusterAppState, networkUtils, clock);
        NodeHealth nodeHealth = underTest.get();
        assertThat(nodeHealth.getDetails().getStartedAt()).isEqualTo(now);
        // change now
        Mockito.when(clock.now()).thenReturn(now);
        NodeHealth newNodeHealth = underTest.get();
        assertThat(newNodeHealth.getDetails().getStartedAt()).isEqualTo(now);
    }

    @Test
    public void get_returns_status_GREEN_if_elasticsearch_process_is_operational_in_ClusterAppState() {
        Properties properties = new Properties();
        setRequiredPropertiesAndMocks(properties);
        Mockito.when(clusterAppState.isOperational(ELASTICSEARCH, true)).thenReturn(true);
        SearchNodeHealthProvider underTest = new SearchNodeHealthProvider(new Props(properties), clusterAppState, networkUtils, clock);
        NodeHealth nodeHealth = underTest.get();
        assertThat(nodeHealth.getStatus()).isEqualTo(GREEN);
    }

    @Test
    public void get_returns_status_RED_with_cause_if_elasticsearch_process_is_not_operational_in_ClusterAppState() {
        Properties properties = new Properties();
        setRequiredPropertiesAndMocks(properties);
        Mockito.when(clusterAppState.isOperational(ELASTICSEARCH, true)).thenReturn(false);
        SearchNodeHealthProvider underTest = new SearchNodeHealthProvider(new Props(properties), clusterAppState, networkUtils, clock);
        NodeHealth nodeHealth = underTest.get();
        assertThat(nodeHealth.getStatus()).isEqualTo(RED);
        assertThat(nodeHealth.getCauses()).containsOnly("Elasticsearch is not operational");
    }
}

