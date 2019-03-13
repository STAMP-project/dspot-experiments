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


import Health.Status.RED;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.sonar.process.cluster.health.NodeHealth;


public class AppNodeClusterCheckTest {
    private final Random random = new Random();

    private AppNodeClusterCheck underTest = new AppNodeClusterCheck();

    @Test
    public void status_RED_when_no_application_node() {
        Set<NodeHealth> nodeHealths = nodeHealths().collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(RED).andCauses("No application node");
    }

    @Test
    public void status_RED_when_single_RED_application_node() {
        Set<NodeHealth> nodeHealths = nodeHealths(RED).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(RED).andCauses("Status of all application nodes is RED", "There should be at least two application nodes");
    }

    @Test
    public void status_YELLOW_when_single_YELLOW_application_node() {
        Set<NodeHealth> nodeHealths = nodeHealths(YELLOW).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(Health.Status.YELLOW).andCauses("Status of all application nodes is YELLOW", "There should be at least two application nodes");
    }

    @Test
    public void status_YELLOW_when_single_GREEN_application_node() {
        Set<NodeHealth> nodeHealths = nodeHealths(GREEN).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(Health.Status.YELLOW).andCauses("There should be at least two application nodes");
    }

    @Test
    public void status_RED_when_two_RED_application_nodes() {
        Set<NodeHealth> nodeHealths = nodeHealths(RED, RED).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(RED).andCauses("Status of all application nodes is RED");
    }

    @Test
    public void status_YELLOW_when_two_YELLOW_application_nodes() {
        Set<NodeHealth> nodeHealths = nodeHealths(YELLOW, YELLOW).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(Health.Status.YELLOW).andCauses("Status of all application nodes is YELLOW");
    }

    @Test
    public void status_YELLOW_when_one_RED_node_and_one_YELLOW_application_node() {
        Set<NodeHealth> nodeHealths = nodeHealths(RED, YELLOW).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(Health.Status.YELLOW).andCauses("At least one application node is RED", "At least one application node is YELLOW");
    }

    @Test
    public void status_YELLOW_when_one_RED_node_and_one_GREEN_application_node() {
        Set<NodeHealth> nodeHealths = nodeHealths(RED, GREEN).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(Health.Status.YELLOW).andCauses("At least one application node is RED");
    }

    @Test
    public void status_YELLOW_when_one_YELLOW_node_and_one_GREEN_application_node() {
        Set<NodeHealth> nodeHealths = nodeHealths(YELLOW, GREEN).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(Health.Status.YELLOW).andCauses("At least one application node is YELLOW");
    }

    @Test
    public void status_GREEN_when_two_GREEN_application_node() {
        Set<NodeHealth> nodeHealths = nodeHealths(GREEN, GREEN).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(Health.Status.GREEN).andCauses();
    }

    @Test
    public void status_GREEN_when_two_GREEN_application_node_and_any_number_of_other_is_GREEN() {
        Set<NodeHealth> nodeHealths = // at least 1 extra GREEN
        // 0 to 10 GREEN
        // 2 GREEN
        Stream.of(Stream.of(appNodeHealth(GREEN)), randomNumberOfAppNodeHealthOfAnyStatus(GREEN), nodeHealths(GREEN, GREEN)).flatMap(( s) -> s).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(Health.Status.GREEN).andCauses();
    }

    @Test
    public void status_YELLOW_when_two_GREEN_application_node_and_any_number_of_other_is_YELLOW_or_GREEN() {
        Set<NodeHealth> nodeHealths = // at least 1 YELLOW
        // 0 to 10 YELLOW/GREEN
        // 2 GREEN
        Stream.of(Stream.of(appNodeHealth(YELLOW)), randomNumberOfAppNodeHealthOfAnyStatus(GREEN, YELLOW), nodeHealths(GREEN, GREEN)).flatMap(( s) -> s).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(Health.Status.YELLOW).andCauses("At least one application node is YELLOW");
    }

    @Test
    public void status_YELLOW_when_two_GREEN_application_node_and_any_number_of_other_is_RED_or_GREEN() {
        Set<NodeHealth> nodeHealths = // at least 1 RED
        // 0 to 10 RED/GREEN
        // 2 GREEN
        Stream.of(Stream.of(appNodeHealth(RED)), randomNumberOfAppNodeHealthOfAnyStatus(GREEN, RED), nodeHealths(GREEN, GREEN)).flatMap(( s) -> s).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(Health.Status.YELLOW).andCauses("At least one application node is RED");
    }

    @Test
    public void status_YELLOW_when_two_GREEN_application_node_and_any_number_of_other_is_either_RED_or_YELLOW() {
        Set<NodeHealth> nodeHealths = // at least 1 RED
        // at least 1 YELLOW
        // 0 to 10 RED/YELLOW/GREEN
        // 2 GREEN
        Stream.of(Stream.of(appNodeHealth(RED)), Stream.of(appNodeHealth(YELLOW)), randomNumberOfAppNodeHealthOfAnyStatus(RED, YELLOW, GREEN), nodeHealths(GREEN, GREEN)).flatMap(( s) -> s).collect(Collectors.toSet());
        Health check = underTest.check(nodeHealths);
        HealthAssert.assertThat(check).forInput(nodeHealths).hasStatus(Health.Status.YELLOW).andCauses("At least one application node is YELLOW", "At least one application node is RED");
    }
}

