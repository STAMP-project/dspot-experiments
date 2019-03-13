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
package org.sonar.application.cluster;


import ProcessId.APP;
import ProcessId.COMPUTE_ENGINE;
import ProcessId.ELASTICSEARCH;
import ProcessId.WEB_SERVER;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.sonar.application.AppStateListener;
import org.sonar.application.config.TestAppSettings;
import org.sonar.application.es.EsConnector;
import org.sonar.process.MessageException;


public class ClusterAppStateImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TestRule safeguardTimeout = new DisableOnDebug(Timeout.seconds(60));

    @Test
    public void tryToLockWebLeader_returns_true_only_for_the_first_call() {
        try (ClusterAppStateImpl underTest = new ClusterAppStateImpl(new TestAppSettings(), ClusterAppStateImplTest.newHzMember(), Mockito.mock(EsConnector.class))) {
            assertThat(underTest.tryToLockWebLeader()).isEqualTo(true);
            assertThat(underTest.tryToLockWebLeader()).isEqualTo(false);
        }
    }

    @Test
    public void test_listeners() {
        AppStateListener listener = Mockito.mock(AppStateListener.class);
        try (ClusterAppStateImpl underTest = new ClusterAppStateImpl(new TestAppSettings(), ClusterAppStateImplTest.newHzMember(), Mockito.mock(EsConnector.class))) {
            underTest.addListener(listener);
            underTest.setOperational(ELASTICSEARCH);
            Mockito.verify(listener, Mockito.timeout(20000)).onAppStateOperational(ELASTICSEARCH);
            assertThat(underTest.isOperational(ELASTICSEARCH, true)).isEqualTo(true);
            assertThat(underTest.isOperational(APP, true)).isEqualTo(false);
            assertThat(underTest.isOperational(WEB_SERVER, true)).isEqualTo(false);
            assertThat(underTest.isOperational(COMPUTE_ENGINE, true)).isEqualTo(false);
        }
    }

    @Test
    public void registerSonarQubeVersion_publishes_version_on_first_call() {
        try (ClusterAppStateImpl underTest = new ClusterAppStateImpl(new TestAppSettings(), ClusterAppStateImplTest.newHzMember(), Mockito.mock(EsConnector.class))) {
            underTest.registerSonarQubeVersion("6.4.1.5");
            assertThat(underTest.getHazelcastMember().getAtomicReference(SONARQUBE_VERSION).get()).isEqualTo("6.4.1.5");
        }
    }

    @Test
    public void registerClusterName_publishes_clusterName_on_first_call() {
        try (ClusterAppStateImpl underTest = new ClusterAppStateImpl(new TestAppSettings(), ClusterAppStateImplTest.newHzMember(), Mockito.mock(EsConnector.class))) {
            underTest.registerClusterName("foo");
            assertThat(underTest.getHazelcastMember().getAtomicReference(CLUSTER_NAME).get()).isEqualTo("foo");
        }
    }

    @Test
    public void reset_always_throws_ISE() {
        try (ClusterAppStateImpl underTest = new ClusterAppStateImpl(new TestAppSettings(), ClusterAppStateImplTest.newHzMember(), Mockito.mock(EsConnector.class))) {
            expectedException.expect(IllegalStateException.class);
            expectedException.expectMessage("state reset is not supported in cluster mode");
            underTest.reset();
        }
    }

    @Test
    public void registerSonarQubeVersion_throws_ISE_if_initial_version_is_different() {
        // Now launch an instance that try to be part of the hzInstance cluster
        try (ClusterAppStateImpl underTest = new ClusterAppStateImpl(new TestAppSettings(), ClusterAppStateImplTest.newHzMember(), Mockito.mock(EsConnector.class))) {
            // Register first version
            underTest.getHazelcastMember().getAtomicReference(SONARQUBE_VERSION).set("6.6.0.1111");
            expectedException.expect(IllegalStateException.class);
            expectedException.expectMessage("The local version 6.7.0.9999 is not the same as the cluster 6.6.0.1111");
            // Registering a second different version must trigger an exception
            underTest.registerSonarQubeVersion("6.7.0.9999");
        }
    }

    @Test
    public void registerClusterName_throws_MessageException_if_clusterName_is_different() {
        try (ClusterAppStateImpl underTest = new ClusterAppStateImpl(new TestAppSettings(), ClusterAppStateImplTest.newHzMember(), Mockito.mock(EsConnector.class))) {
            // Register first version
            underTest.getHazelcastMember().getAtomicReference(CLUSTER_NAME).set("goodClusterName");
            expectedException.expect(MessageException.class);
            expectedException.expectMessage("This node has a cluster name [badClusterName], which does not match [goodClusterName] from the cluster");
            // Registering a second different cluster name must trigger an exception
            underTest.registerClusterName("badClusterName");
        }
    }
}

