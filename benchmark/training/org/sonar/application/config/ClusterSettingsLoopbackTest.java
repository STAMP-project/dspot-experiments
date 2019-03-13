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
package org.sonar.application.config;


import NetworkUtilsImpl.INSTANCE;
import java.net.InetAddress;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.process.NetworkUtils;


public class ClusterSettingsLoopbackTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private InetAddress loopback = InetAddress.getLoopbackAddress();

    private InetAddress nonLoopbackLocal;

    private NetworkUtils network = Mockito.spy(INSTANCE);

    @Test
    public void ClusterSettings_throws_MessageException_if_host_of_search_node_is_loopback() {
        verifySearchFailureIfLoopback(CLUSTER_NODE_HOST.getKey());
        verifySearchFailureIfLoopback(CLUSTER_SEARCH_HOSTS.getKey());
        verifySearchFailureIfLoopback(CLUSTER_HZ_HOSTS.getKey());
        verifySearchFailureIfLoopback(SEARCH_HOST.getKey());
    }

    @Test
    public void ClusterSettings_throws_MessageException_if_host_of_app_node_is_loopback() {
        verifyAppFailureIfLoopback(CLUSTER_NODE_HOST.getKey());
        verifyAppFailureIfLoopback(CLUSTER_SEARCH_HOSTS.getKey());
        verifyAppFailureIfLoopback(CLUSTER_HZ_HOSTS.getKey());
    }
}

