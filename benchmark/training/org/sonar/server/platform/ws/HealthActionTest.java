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


import Health.Builder;
import Health.Status;
import Health.Status.RED;
import NodeDetails.Type.APPLICATION;
import NodeDetails.Type.SEARCH;
import NodeHealth.Status.GREEN;
import NodeHealth.Status.YELLOW;
import WebService.Action;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.server.ws.WebService;
import org.sonar.process.cluster.health.NodeHealth;
import org.sonar.server.health.Health;
import org.sonar.server.health.HealthChecker;
import org.sonar.server.platform.WebServer;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.user.SystemPasscode;
import org.sonar.server.ws.TestRequest;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;


public class HealthActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    private final Random random = new Random();

    private HealthChecker healthChecker = Mockito.mock(HealthChecker.class);

    private WebServer webServer = Mockito.mock(WebServer.class);

    private SystemPasscode systemPasscode = Mockito.mock(SystemPasscode.class);

    private WsActionTester underTest = new WsActionTester(new HealthAction(webServer, new HealthActionSupport(healthChecker), systemPasscode, userSessionRule));

    @Test
    public void verify_definition() {
        WebService.Action definition = underTest.getDef();
        assertThat(definition.key()).isEqualTo("health");
        assertThat(definition.isPost()).isFalse();
        assertThat(definition.description()).isNotEmpty();
        assertThat(definition.since()).isEqualTo("6.6");
        assertThat(definition.isInternal()).isFalse();
        assertThat(definition.responseExample()).isNotNull();
        assertThat(definition.params()).isEmpty();
    }

    @Test
    public void request_fails_with_ForbiddenException_when_anonymous() {
        TestRequest request = underTest.newRequest();
        expectForbiddenException();
        request.execute();
    }

    @Test
    public void request_fails_with_SystemPasscode_enabled_and_anonymous() {
        Mockito.when(systemPasscode.isValid(ArgumentMatchers.any())).thenReturn(false);
        TestRequest request = underTest.newRequest();
        expectForbiddenException();
        request.execute();
    }

    @Test
    public void request_fails_with_SystemPasscode_enabled_but_no_passcode_and_user_is_not_system_administrator() {
        Mockito.when(systemPasscode.isValid(ArgumentMatchers.any())).thenReturn(false);
        userSessionRule.logIn();
        Mockito.when(healthChecker.checkCluster()).thenReturn(randomStatusMinimalClusterHealth());
        TestRequest request = underTest.newRequest();
        expectForbiddenException();
        request.execute();
    }

    @Test
    public void request_succeeds_with_SystemPasscode_enabled_and_passcode() {
        Mockito.when(systemPasscode.isValid(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(healthChecker.checkCluster()).thenReturn(randomStatusMinimalClusterHealth());
        TestRequest request = underTest.newRequest();
        request.execute();
    }

    @Test
    public void request_succeeds_with_SystemPasscode_incorrect_and_user_is_system_administrator() {
        Mockito.when(systemPasscode.isValid(ArgumentMatchers.any())).thenReturn(false);
        userSessionRule.logIn().setSystemAdministrator();
        Mockito.when(healthChecker.checkCluster()).thenReturn(randomStatusMinimalClusterHealth());
        TestRequest request = underTest.newRequest();
        request.execute();
    }

    @Test
    public void verify_response_example() {
        authenticateWithRandomMethod();
        Mockito.when(webServer.isStandalone()).thenReturn(false);
        long time = parseDateTime("2015-08-13T23:34:59+0200").getTime();
        Mockito.when(healthChecker.checkCluster()).thenReturn(new org.sonar.server.health.ClusterHealth(Health.newHealthCheckBuilder().setStatus(RED).addCause("Application node app-1 is RED").build(), ImmutableSet.of(newNodeHealthBuilder().setStatus(NodeHealth.Status.RED).addCause("foo").setDetails(newNodeDetailsBuilder().setName("app-1").setType(APPLICATION).setHost("192.168.1.1").setPort(999).setStartedAt(time).build()).build(), newNodeHealthBuilder().setStatus(YELLOW).addCause("bar").setDetails(newNodeDetailsBuilder().setName("app-2").setType(APPLICATION).setHost("192.168.1.2").setPort(999).setStartedAt(time).build()).build(), newNodeHealthBuilder().setStatus(GREEN).setDetails(newNodeDetailsBuilder().setName("es-1").setType(SEARCH).setHost("192.168.1.3").setPort(999).setStartedAt(time).build()).build(), newNodeHealthBuilder().setStatus(GREEN).setDetails(newNodeDetailsBuilder().setName("es-2").setType(SEARCH).setHost("192.168.1.4").setPort(999).setStartedAt(time).build()).build(), newNodeHealthBuilder().setStatus(GREEN).setDetails(newNodeDetailsBuilder().setName("es-3").setType(SEARCH).setHost("192.168.1.5").setPort(999).setStartedAt(time).build()).build())));
        TestResponse response = underTest.newRequest().execute();
        assertJson(response.getInput()).isSimilarTo(underTest.getDef().responseExampleAsString());
    }

    @Test
    public void request_returns_status_and_causes_from_HealthChecker_checkNode_method_when_standalone() {
        authenticateWithRandomMethod();
        Health.Status randomStatus = Status.values()[new Random().nextInt(Status.values().length)];
        Health.Builder builder = Health.newHealthCheckBuilder().setStatus(randomStatus);
        IntStream.range(0, new Random().nextInt(5)).mapToObj(( i) -> RandomStringUtils.randomAlphanumeric(3)).forEach(builder::addCause);
        Health health = builder.build();
        Mockito.when(healthChecker.checkNode()).thenReturn(health);
        Mockito.when(webServer.isStandalone()).thenReturn(true);
        TestRequest request = underTest.newRequest();
        System.HealthResponse healthResponse = request.executeProtobuf(.class);
        assertThat(healthResponse.getHealth().name()).isEqualTo(randomStatus.name());
        assertThat(health.getCauses()).isEqualTo(health.getCauses());
    }

    @Test
    public void response_contains_status_and_causes_from_HealthChecker_checkCluster_when_standalone() {
        authenticateWithRandomMethod();
        Health.Status randomStatus = Status.values()[random.nextInt(Status.values().length)];
        String[] causes = IntStream.range(0, random.nextInt(33)).mapToObj(( i) -> randomAlphanumeric(4)).toArray(String[]::new);
        Health.Builder healthBuilder = Health.newHealthCheckBuilder().setStatus(randomStatus);
        Arrays.stream(causes).forEach(healthBuilder::addCause);
        Mockito.when(webServer.isStandalone()).thenReturn(false);
        Mockito.when(healthChecker.checkCluster()).thenReturn(new org.sonar.server.health.ClusterHealth(healthBuilder.build(), Collections.emptySet()));
        System.HealthResponse clusterHealthResponse = underTest.newRequest().executeProtobuf(.class);
        assertThat(clusterHealthResponse.getHealth().name()).isEqualTo(randomStatus.name());
        assertThat(clusterHealthResponse.getCausesList()).extracting(Cause::getMessage).containsOnly(causes);
    }

    @Test
    public void response_contains_information_of_nodes_when_clustered() {
        authenticateWithRandomMethod();
        NodeHealth nodeHealth = randomNodeHealth();
        Mockito.when(webServer.isStandalone()).thenReturn(false);
        Mockito.when(healthChecker.checkCluster()).thenReturn(new org.sonar.server.health.ClusterHealth(Health.GREEN, Collections.singleton(nodeHealth)));
        System.HealthResponse response = underTest.newRequest().executeProtobuf(.class);
        assertThat(response.getNodes().getNodesList()).hasSize(1);
        System.Node node = response.getNodes().getNodesList().iterator().next();
        assertThat(node.getHealth().name()).isEqualTo(nodeHealth.getStatus().name());
        assertThat(node.getCausesList()).extracting(Cause::getMessage).containsOnly(nodeHealth.getCauses().stream().toArray(String[]::new));
        assertThat(node.getName()).isEqualTo(nodeHealth.getDetails().getName());
        assertThat(node.getHost()).isEqualTo(nodeHealth.getDetails().getHost());
        assertThat(node.getPort()).isEqualTo(nodeHealth.getDetails().getPort());
        assertThat(node.getStartedAt()).isEqualTo(formatDateTime(nodeHealth.getDetails().getStartedAt()));
        assertThat(node.getType().name()).isEqualTo(nodeHealth.getDetails().getType().name());
    }

    @Test
    public void response_sort_nodes_by_type_name_host_then_port_when_clustered() {
        authenticateWithRandomMethod();
        // using created field as a unique identifier. pseudo random value to ensure sorting is not based on created field
        List<NodeHealth> nodeHealths = new java.util.ArrayList(Arrays.asList(randomNodeHealth(APPLICATION, "1_name", "1_host", 1, 99), randomNodeHealth(APPLICATION, "1_name", "2_host", 1, 85), randomNodeHealth(APPLICATION, "1_name", "2_host", 2, 12), randomNodeHealth(APPLICATION, "2_name", "1_host", 1, 6), randomNodeHealth(APPLICATION, "2_name", "1_host", 2, 30), randomNodeHealth(APPLICATION, "2_name", "2_host", 1, 75), randomNodeHealth(APPLICATION, "2_name", "2_host", 2, 258), randomNodeHealth(SEARCH, "1_name", "1_host", 1, 963), randomNodeHealth(SEARCH, "1_name", "1_host", 2, 1), randomNodeHealth(SEARCH, "1_name", "2_host", 1, 35), randomNodeHealth(SEARCH, "1_name", "2_host", 2, 45), randomNodeHealth(SEARCH, "2_name", "1_host", 1, 39), randomNodeHealth(SEARCH, "2_name", "1_host", 2, 28), randomNodeHealth(SEARCH, "2_name", "2_host", 1, 66), randomNodeHealth(SEARCH, "2_name", "2_host", 2, 77)));
        String[] expected = nodeHealths.stream().map(( s) -> formatDateTime(new Date(s.getDetails().getStartedAt()))).toArray(String[]::new);
        Collections.shuffle(nodeHealths);
        Mockito.when(webServer.isStandalone()).thenReturn(false);
        Mockito.when(healthChecker.checkCluster()).thenReturn(new org.sonar.server.health.ClusterHealth(Health.GREEN, new java.util.HashSet(nodeHealths)));
        System.HealthResponse response = underTest.newRequest().executeProtobuf(.class);
        assertThat(response.getNodes().getNodesList()).extracting(Node::getStartedAt).containsExactly(expected);
    }
}

