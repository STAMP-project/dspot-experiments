/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.topology.addservice;


import AddServiceInfo.Builder;
import ProvisionAction.INSTALL_AND_START;
import ProvisionAction.INSTALL_ONLY;
import ProvisionAction.START_ONLY;
import ProvisionStep.INSTALL;
import ProvisionStep.SKIP_INSTALL;
import ProvisionStep.START;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.ambari.server.controller.internal.RequestStageContainer;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Resource;
import org.junit.Assert;
import org.junit.Test;


public class ProvisionActionPredicateBuilderTest {
    private static final String CLUSTER_NAME = "TEST";

    private static final Map<String, Map<String, Set<String>>> NEW_SERVICES = ImmutableMap.of("AMBARI_METRICS", ImmutableMap.of("METRICS_COLLECTOR", ImmutableSet.of("c7401"), "METRICS_GRAFANA", ImmutableSet.of("c7403"), "METRICS_MONITOR", ImmutableSet.of("c7401", "c7402", "c7403", "c7404", "c7405")), "KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7402", "c7404")), "ZOOKEEPER", ImmutableMap.of("ZOOKEEPER_SERVER", ImmutableSet.of("c7401", "c7402", "c7403"), "ZOOKEEPER_CLIENT", ImmutableSet.of("c7404")));

    private static final RequestStageContainer STAGES = new RequestStageContainer(42L, null, null, null, null);

    private static final Builder ADD_SERVICE_INFO_BUILDER = new AddServiceInfo.Builder().setClusterName(ProvisionActionPredicateBuilderTest.CLUSTER_NAME).setStages(ProvisionActionPredicateBuilderTest.STAGES).setNewServices(ProvisionActionPredicateBuilderTest.NEW_SERVICES);

    @Test
    public void noCustomProvisionAction() {
        AddServiceRequest request = ProvisionActionPredicateBuilderTest.createRequest(null, null, null);
        AddServiceInfo info = ProvisionActionPredicateBuilderTest.ADD_SERVICE_INFO_BUILDER.setRequest(request).build();
        ProvisionActionPredicateBuilder builder = new ProvisionActionPredicateBuilder(info);
        Assert.assertTrue(builder.getPredicate(INSTALL).isPresent());
        Assert.assertFalse(builder.getPredicate(SKIP_INSTALL).isPresent());
        Assert.assertTrue(builder.getPredicate(START).isPresent());
        Predicate installPredicate = builder.getPredicate(INSTALL).get();
        Predicate startPredicate = builder.getPredicate(START).get();
        Set<Resource> allNewHostComponents = ProvisionActionPredicateBuilderTest.allHostComponents(ProvisionActionPredicateBuilderTest.NEW_SERVICES);
        ProvisionActionPredicateBuilderTest.assertMatchesAll(installPredicate, allNewHostComponents);
        ProvisionActionPredicateBuilderTest.assertMatchesAll(startPredicate, allNewHostComponents);
        ProvisionActionPredicateBuilderTest.assertNoMatchForExistingComponents(installPredicate, startPredicate);
    }

    @Test
    public void requestLevelStartOnly() {
        AddServiceRequest request = ProvisionActionPredicateBuilderTest.createRequest(START_ONLY, null, null);
        AddServiceInfo info = ProvisionActionPredicateBuilderTest.ADD_SERVICE_INFO_BUILDER.setRequest(request).build();
        ProvisionActionPredicateBuilder builder = new ProvisionActionPredicateBuilder(info);
        Assert.assertEquals(Optional.empty(), builder.getPredicate(INSTALL));
        Assert.assertTrue(builder.getPredicate(SKIP_INSTALL).isPresent());
        Assert.assertTrue(builder.getPredicate(START).isPresent());
        Predicate skipInstallPredicate = builder.getPredicate(SKIP_INSTALL).get();
        Predicate startPredicate = builder.getPredicate(START).get();
        Set<Resource> allNewHostComponents = ProvisionActionPredicateBuilderTest.allHostComponents(ProvisionActionPredicateBuilderTest.NEW_SERVICES);
        ProvisionActionPredicateBuilderTest.assertMatchesAll(skipInstallPredicate, allNewHostComponents);
        ProvisionActionPredicateBuilderTest.assertMatchesAll(startPredicate, allNewHostComponents);
        ProvisionActionPredicateBuilderTest.assertNoMatchForExistingComponents(skipInstallPredicate, startPredicate);
    }

    @Test
    public void customAtAllLevels() {
        AddServiceRequest request = ProvisionActionPredicateBuilderTest.createRequest(START_ONLY, ImmutableSet.of(Service.of("AMBARI_METRICS", INSTALL_AND_START), Service.of("KAFKA"), Service.of("ZOOKEEPER", INSTALL_ONLY)), // overrides request-level
        // KAFKA_BROKER on c7402 added by layout recommendation inherits request-level
        // overrides service-level
        // inherit from service
        // matches service-level
        // METRICS_MONITOR on c7403 added by layout recommendation, inherits service-level
        // overrides service-level
        ImmutableSet.of(Component.of("KAFKA_BROKER", INSTALL_AND_START, "c7404"), Component.of("METRICS_GRAFANA", START_ONLY, "c7403"), Component.of("METRICS_MONITOR", "c7401"), Component.of("METRICS_MONITOR", INSTALL_AND_START, "c7402"), Component.of("METRICS_MONITOR", INSTALL_ONLY, "c7404", "c7405")));
        AddServiceInfo info = ProvisionActionPredicateBuilderTest.ADD_SERVICE_INFO_BUILDER.setRequest(request).build();
        ProvisionActionPredicateBuilder builder = new ProvisionActionPredicateBuilder(info);
        Assert.assertTrue(builder.getPredicate(INSTALL).isPresent());
        Assert.assertTrue(builder.getPredicate(SKIP_INSTALL).isPresent());
        Assert.assertTrue(builder.getPredicate(START).isPresent());
        Predicate installPredicate = builder.getPredicate(INSTALL).get();
        Predicate skipInstallPredicate = builder.getPredicate(SKIP_INSTALL).get();
        Predicate startPredicate = builder.getPredicate(START).get();
        Map<String, Map<String, Set<String>>> installComponents = ImmutableMap.of("AMBARI_METRICS", ImmutableMap.of("METRICS_COLLECTOR", ImmutableSet.of("c7401"), "METRICS_MONITOR", ImmutableSet.of("c7401", "c7402", "c7404", "c7405")), "KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7404")), "ZOOKEEPER", ImmutableMap.of("ZOOKEEPER_SERVER", ImmutableSet.of("c7401", "c7402", "c7403"), "ZOOKEEPER_CLIENT", ImmutableSet.of("c7404")));
        Map<String, Map<String, Set<String>>> skipInstallComponents = ImmutableMap.of("AMBARI_METRICS", ImmutableMap.of("METRICS_GRAFANA", ImmutableSet.of("c7403")), "KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7402")));
        Map<String, Map<String, Set<String>>> startComponents = ImmutableMap.of("AMBARI_METRICS", ImmutableMap.of("METRICS_COLLECTOR", ImmutableSet.of("c7401"), "METRICS_GRAFANA", ImmutableSet.of("c7403"), "METRICS_MONITOR", ImmutableSet.of("c7401", "c7402", "c7403")), "KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7402", "c7404")));
        ProvisionActionPredicateBuilderTest.assertMatchesAll(installPredicate, ProvisionActionPredicateBuilderTest.allHostComponents(installComponents));
        ProvisionActionPredicateBuilderTest.assertMatchesAll(skipInstallPredicate, ProvisionActionPredicateBuilderTest.allHostComponents(skipInstallComponents));
        ProvisionActionPredicateBuilderTest.assertMatchesAll(startPredicate, ProvisionActionPredicateBuilderTest.allHostComponents(startComponents));
        ProvisionActionPredicateBuilderTest.assertNoMatchForExistingComponents(skipInstallPredicate, startPredicate);
    }
}

