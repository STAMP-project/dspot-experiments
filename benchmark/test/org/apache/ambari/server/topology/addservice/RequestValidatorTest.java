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


import ConfigRecommendationStrategy.ALWAYS_APPLY;
import ProvisionAction.INSTALL_AND_START;
import ProvisionAction.INSTALL_ONLY;
import RequestValidator.State.INITIAL;
import SecurityConfiguration.NONE;
import SecurityType.KERBEROS;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.actionmanager.RequestFactory;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.internal.Stack;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.kerberos.KerberosDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosDescriptorFactory;
import org.apache.ambari.server.topology.Configuration;
import org.apache.ambari.server.topology.SecurityConfiguration;
import org.apache.ambari.server.topology.SecurityConfigurationFactory;
import org.apache.ambari.server.topology.StackFactory;
import org.apache.ambari.server.utils.Assertions;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;


public class RequestValidatorTest extends EasyMockSupport {
    private static final Map<String, ?> FAKE_DESCRIPTOR = ImmutableMap.of("kerberos", "descriptor");

    private static final String FAKE_DESCRIPTOR_REFERENCE = "ref";

    private final AddServiceRequest request = createNiceMock(AddServiceRequest.class);

    private final Cluster cluster = createMock(Cluster.class);

    private final AmbariManagementController controller = createNiceMock(AmbariManagementController.class);

    private final ConfigHelper configHelper = createMock(ConfigHelper.class);

    private final StackFactory stackFactory = createNiceMock(StackFactory.class);

    private final KerberosDescriptorFactory kerberosDescriptorFactory = createNiceMock(KerberosDescriptorFactory.class);

    private final SecurityConfigurationFactory securityConfigurationFactory = createStrictMock(SecurityConfigurationFactory.class);

    private final RequestValidator validator = new RequestValidator(request, cluster, controller, configHelper, stackFactory, kerberosDescriptorFactory, securityConfigurationFactory);

    @Test
    public void cannotConstructInvalidRequestInfo() {
        Assertions.assertThrows(IllegalStateException.class, () -> validator.createValidServiceInfo(null, null));
        Stack stack = simpleMockStack();
        Map<String, Map<String, Set<String>>> newServices = RequestValidatorTest.someNewServices();
        Configuration config = Configuration.newEmpty();
        validator.setState(INITIAL.with(stack));
        Assertions.assertThrows(IllegalStateException.class, () -> validator.createValidServiceInfo(null, null));
        validator.setState(validator.getState().with(config));
        Assertions.assertThrows(IllegalStateException.class, () -> validator.createValidServiceInfo(null, null));
        validator.setState(INITIAL.withNewServices(newServices));
        Assertions.assertThrows(IllegalStateException.class, () -> validator.createValidServiceInfo(null, null));
        validator.setState(validator.getState().with(stack));
        Assertions.assertThrows(IllegalStateException.class, () -> validator.createValidServiceInfo(null, null));
        validator.setState(INITIAL.with(config));
        Assertions.assertThrows(IllegalStateException.class, () -> validator.createValidServiceInfo(null, null));
        validator.setState(validator.getState().withNewServices(newServices));
        Assertions.assertThrows(IllegalStateException.class, () -> validator.createValidServiceInfo(null, null));
    }

    @Test
    public void canConstructValidRequestInfo() {
        validator.setState(INITIAL.withNewServices(RequestValidatorTest.someNewServices()).with(simpleMockStack()).with(Configuration.newEmpty()));
        ActionManager actionManager = createNiceMock(ActionManager.class);
        RequestFactory requestFactory = createNiceMock(RequestFactory.class);
        replayAll();
        AddServiceInfo addServiceInfo = validator.createValidServiceInfo(actionManager, requestFactory);
        Assert.assertNotNull(addServiceInfo);
        Assert.assertSame(request, addServiceInfo.getRequest());
        Assert.assertEquals(cluster.getClusterName(), addServiceInfo.clusterName());
        Assert.assertSame(validator.getState().getConfig(), addServiceInfo.getConfig());
        Assert.assertSame(validator.getState().getStack(), addServiceInfo.getStack());
        Assert.assertEquals(validator.getState().getNewServices(), addServiceInfo.newServices());
    }

    @Test
    public void cannotConstructTwice() {
        ActionManager actionManager = createNiceMock(ActionManager.class);
        RequestFactory requestFactory = createNiceMock(RequestFactory.class);
        replayAll();
        validator.setState(INITIAL.withNewServices(RequestValidatorTest.someNewServices()).with(simpleMockStack()).with(Configuration.newEmpty()));
        validator.createValidServiceInfo(actionManager, requestFactory);
        Assertions.assertThrows(IllegalStateException.class, () -> validator.createValidServiceInfo(actionManager, requestFactory));
    }

    @Test
    public void reportsUnknownStackFromRequest() throws Exception {
        StackId requestStackId = new StackId("HDP", "123");
        expect(request.getStackId()).andReturn(Optional.of(requestStackId)).anyTimes();
        expect(stackFactory.createStack(requestStackId.getStackName(), requestStackId.getStackVersion(), controller)).andThrow(new AmbariException("Stack not found"));
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, validator::validateStack);
        Assert.assertTrue(e.getMessage().contains(requestStackId.toString()));
        Assert.assertNull(validator.getState().getStack());
    }

    @Test
    public void reportsUnknownStackFromCluster() throws Exception {
        StackId clusterStackId = new StackId("CLUSTER", "555");
        expect(request.getStackId()).andReturn(Optional.empty()).anyTimes();
        expect(cluster.getCurrentStackVersion()).andReturn(clusterStackId);
        expect(stackFactory.createStack(clusterStackId.getStackName(), clusterStackId.getStackVersion(), controller)).andThrow(new AmbariException("Stack not found"));
        replayAll();
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, validator::validateStack);
        Assert.assertTrue(e.getMessage().contains(clusterStackId.toString()));
        Assert.assertNull(validator.getState().getStack());
    }

    @Test
    public void useClusterStackIfAbsentInRequest() throws Exception {
        StackId clusterStackId = new StackId("CLUSTER", "123");
        Stack expectedStack = createNiceMock(Stack.class);
        expect(request.getStackId()).andReturn(Optional.empty()).anyTimes();
        expect(cluster.getCurrentStackVersion()).andReturn(clusterStackId);
        expect(stackFactory.createStack(clusterStackId.getStackName(), clusterStackId.getStackVersion(), controller)).andReturn(expectedStack);
        replayAll();
        validator.validateStack();
        Assert.assertSame(expectedStack, validator.getState().getStack());
    }

    @Test
    public void acceptsKnownServices() {
        String newService = "KAFKA";
        requestServices(false, newService);
        validator.setState(INITIAL.with(simpleMockStack()));
        replayAll();
        validator.validateServicesAndComponents();
        Map<String, Map<String, Set<String>>> expectedNewServices = ImmutableMap.of(newService, ImmutableMap.of());
        Assert.assertEquals(expectedNewServices, validator.getState().getNewServices());
    }

    @Test
    public void acceptsKnownComponents() {
        requestComponents("KAFKA_BROKER");
        validator.setState(INITIAL.with(simpleMockStack()));
        replayAll();
        validator.validateServicesAndComponents();
        Map<String, Map<String, Set<String>>> expectedNewServices = ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7401.ambari.apache.org")));
        Assert.assertEquals(expectedNewServices, validator.getState().getNewServices());
    }

    @Test
    public void handlesMultipleComponentInstances() {
        expect(request.getComponents()).andReturn(Stream.of("c7401", "c7402").map(( hostname) -> Component.of("KAFKA_BROKER", hostname)).collect(Collectors.toSet()));
        validator.setState(INITIAL.with(simpleMockStack()));
        replayAll();
        validator.validateServicesAndComponents();
        Map<String, Map<String, Set<String>>> expectedNewServices = ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7401", "c7402")));
        Assert.assertEquals(expectedNewServices, validator.getState().getNewServices());
    }

    @Test
    public void rejectsMultipleOccurrencesOfSameHostForSameComponent() {
        Set<String> duplicateHosts = ImmutableSet.of("c7402", "c7403");
        Set<String> uniqueHosts = ImmutableSet.of("c7401", "c7404");
        expect(request.getComponents()).andReturn(ImmutableSet.of(Component.of("KAFKA_BROKER", INSTALL_AND_START, "c7401", "c7402", "c7403"), Component.of("KAFKA_BROKER", INSTALL_ONLY, "c7402", "c7403", "c7404")));
        validator.setState(INITIAL.with(simpleMockStack()));
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, validator::validateServicesAndComponents);
        Assert.assertTrue(e.getMessage().contains("hosts appear multiple"));
        duplicateHosts.forEach(( host) -> Assert.assertTrue(e.getMessage().contains(host)));
        uniqueHosts.forEach(( host) -> Assert.assertFalse(e.getMessage().contains(host)));
        Assert.assertNull(validator.getState().getNewServices());
    }

    @Test
    public void rejectsUnknownService() {
        String serviceName = "UNKNOWN_SERVICE";
        requestServices(false, serviceName);
        validator.setState(INITIAL.with(simpleMockStack()));
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, validator::validateServicesAndComponents);
        Assert.assertTrue(e.getMessage().contains(serviceName));
        Assert.assertNull(validator.getState().getNewServices());
    }

    @Test
    public void rejectsUnknownComponent() {
        String componentName = "UNKNOWN_COMPONENT";
        requestComponents(componentName);
        validator.setState(INITIAL.with(simpleMockStack()));
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, validator::validateServicesAndComponents);
        Assert.assertTrue(e.getMessage().contains(componentName));
        Assert.assertNull(validator.getState().getNewServices());
    }

    @Test
    public void rejectsExistingServiceForService() {
        String serviceName = "KAFKA";
        requestServices(false, serviceName);
        clusterAlreadyHasServices(serviceName);
        validator.setState(INITIAL.with(simpleMockStack()));
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, validator::validateServicesAndComponents);
        Assert.assertTrue(e.getMessage().contains(serviceName));
        Assert.assertNull(validator.getState().getNewServices());
    }

    @Test
    public void rejectsExistingServiceForComponent() {
        String serviceName = "KAFKA";
        String componentName = "KAFKA_BROKER";
        clusterAlreadyHasServices(serviceName);
        requestComponents(componentName);
        validator.setState(INITIAL.with(simpleMockStack()));
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, validator::validateServicesAndComponents);
        Assert.assertTrue(e.getMessage().contains(serviceName));
        Assert.assertTrue(e.getMessage().contains(componentName));
        Assert.assertNull(validator.getState().getNewServices());
    }

    @Test
    public void rejectsEmptyServiceAndComponentList() {
        replayAll();
        Assertions.assertThrows(IllegalArgumentException.class, validator::validateServicesAndComponents);
        Assert.assertNull(validator.getState().getNewServices());
    }

    @Test
    public void acceptsKnownHosts() {
        Set<String> requestHosts = ImmutableSet.of("c7401.ambari.apache.org", "c7402.ambari.apache.org");
        Set<String> otherHosts = ImmutableSet.of("c7403.ambari.apache.org", "c7404.ambari.apache.org");
        Set<String> clusterHosts = Sets.union(requestHosts, otherHosts);
        expect(cluster.getHostNames()).andReturn(clusterHosts).anyTimes();
        validator.setState(INITIAL.withNewServices(ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", requestHosts))));
        replayAll();
        validator.validateHosts();
    }

    @Test
    public void rejectsUnknownHosts() {
        Set<String> clusterHosts = ImmutableSet.of("c7401.ambari.apache.org", "c7402.ambari.apache.org");
        Set<String> otherHosts = ImmutableSet.of("c7403.ambari.apache.org", "c7404.ambari.apache.org");
        Set<String> requestHosts = ImmutableSet.copyOf(Sets.union(clusterHosts, otherHosts));
        expect(cluster.getHostNames()).andReturn(clusterHosts).anyTimes();
        validator.setState(INITIAL.withNewServices(ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", requestHosts))));
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, validator::validateHosts);
        Assert.assertTrue(e.getMessage(), e.getMessage().contains("host"));
    }

    @Test
    public void acceptsAbsentSecurityWhenClusterHasKerberos() {
        secureCluster();
        replayAll();
        validator.validateSecurity();
    }

    @Test
    public void acceptsAbsentSecurityWhenClusterHasNone() {
        replayAll();
        validator.validateSecurity();
    }

    @Test
    public void acceptsMatchingKerberosSecurity() {
        secureCluster();
        requestSpecifiesSecurity();
        replayAll();
        validator.validateSecurity();
    }

    @Test
    public void acceptsMatchingNoneSecurity() {
        expect(request.getSecurity()).andReturn(Optional.of(NONE)).anyTimes();
        replayAll();
        validator.validateSecurity();
    }

    @Test
    public void rejectsNoneSecurityWhenClusterHasKerberos() {
        testBothValidationTypes(validator::validateSecurity, "KERBEROS", () -> {
            secureCluster();
            expect(request.getSecurity()).andReturn(Optional.of(SecurityConfiguration.NONE)).anyTimes();
        });
    }

    @Test
    public void rejectsKerberosSecurityWhenClusterHasNone() {
        testBothValidationTypes(validator::validateSecurity, "KERBEROS", this::requestSpecifiesSecurity);
    }

    @Test
    public void rejectsKerberosDescriptorForNoSecurity() {
        SecurityConfiguration requestSecurity = SecurityConfiguration.forTest(SecurityType.NONE, null, ImmutableMap.of("kerberos", "descriptor"));
        expect(request.getSecurity()).andReturn(Optional.of(requestSecurity)).anyTimes();
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, validator::validateSecurity);
        Assert.assertTrue(e.getMessage().contains("Kerberos descriptor"));
    }

    @Test
    public void rejectsKerberosDescriptorReferenceForNoSecurity() {
        SecurityConfiguration requestSecurity = SecurityConfiguration.forTest(SecurityType.NONE, "ref", null);
        expect(request.getSecurity()).andReturn(Optional.of(requestSecurity)).anyTimes();
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, validator::validateSecurity);
        Assert.assertTrue(e.getMessage().contains("Kerberos descriptor reference"));
    }

    @Test
    public void rejectsRequestWithBothKerberosDescriptorAndReference() {
        secureCluster();
        SecurityConfiguration invalidConfig = SecurityConfiguration.forTest(KERBEROS, "ref", ImmutableMap.of());
        expect(request.getSecurity()).andReturn(Optional.of(invalidConfig)).anyTimes();
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, validator::validateSecurity);
        Assert.assertTrue(e.getMessage().contains("Kerberos descriptor and reference"));
    }

    @Test
    public void loadsKerberosDescriptorByReference() {
        String newService = "KAFKA";
        secureCluster();
        requestServices(true, newService);
        KerberosDescriptor kerberosDescriptor = requestHasKerberosDescriptorFor(true, newService);
        replayAll();
        validator.validateSecurity();
        Assert.assertEquals(kerberosDescriptor, validator.getState().getKerberosDescriptor());
        verifyAll();
    }

    @Test
    public void reportsDanglingKerberosDescriptorReference() {
        String newService = "KAFKA";
        secureCluster();
        requestServices(true, newService);
        SecurityConfiguration requestSecurity = SecurityConfiguration.withReference(RequestValidatorTest.FAKE_DESCRIPTOR_REFERENCE);
        expect(request.getSecurity()).andReturn(Optional.of(requestSecurity)).anyTimes();
        expect(securityConfigurationFactory.loadSecurityConfigurationByReference(RequestValidatorTest.FAKE_DESCRIPTOR_REFERENCE)).andThrow(new IllegalArgumentException(("No security configuration found for the reference: " + (RequestValidatorTest.FAKE_DESCRIPTOR_REFERENCE))));
        replayAll();
        Assertions.assertThrows(IllegalArgumentException.class, validator::validateSecurity);
        verifyAll();
    }

    @Test
    public void acceptsDescriptorWithOnlyNewServices() {
        String newService = "KAFKA";
        secureCluster();
        requestServices(true, newService);
        KerberosDescriptor kerberosDescriptor = requestHasKerberosDescriptorFor(false, newService);
        replayAll();
        validator.validateSecurity();
        Assert.assertEquals(kerberosDescriptor, validator.getState().getKerberosDescriptor());
    }

    @Test
    public void acceptsDescriptorWithAdditionalServices() {
        String newService = "KAFKA";
        String otherService = "ZOOKEEPER";
        secureCluster();
        requestServices(true, newService);
        requestHasKerberosDescriptorFor(false, newService, otherService);
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, validator::validateSecurity);
        Assert.assertTrue(e.getMessage().contains("only for new services"));
    }

    @Test
    public void acceptsDescriptorWithoutServices() {
        secureCluster();
        requestServices(true, "KAFKA");
        KerberosDescriptor kerberosDescriptor = requestHasKerberosDescriptorFor(false);
        replayAll();
        validator.validateSecurity();
        Assert.assertEquals(kerberosDescriptor, validator.getState().getKerberosDescriptor());
    }

    @Test
    public void combinesRequestConfigWithClusterAndStack() throws AmbariException {
        Configuration requestConfig = Configuration.newEmpty();
        requestConfig.setProperty("kafka-broker", "zookeeper.connect", "zookeeper.connect:request");
        requestConfig.setProperty("kafka-env", "custom_property", "custom_property:request");
        expect(request.getConfiguration()).andReturn(requestConfig.copy()).anyTimes();
        expect(request.getRecommendationStrategy()).andReturn(ALWAYS_APPLY).anyTimes();
        Configuration clusterConfig = Configuration.newEmpty();
        clusterConfig.setProperty("zookeeper-env", "zk_user", "zk_user:cluster_level");
        expect(configHelper.calculateExistingConfigs(cluster)).andReturn(clusterConfig.asPair()).anyTimes();
        Stack stack = simpleMockStack();
        Configuration stackConfig = Configuration.newEmpty();
        stackConfig.setProperty("zookeeper-env", "zk_user", "zk_user:stack_default");
        stackConfig.setProperty("zookeeper-env", "zk_log_dir", "zk_log_dir:stack_default");
        stackConfig.setProperty("kafka-broker", "zookeeper.connect", "zookeeper.connect:stack_default");
        expect(stack.getDefaultConfig()).andReturn(stackConfig).anyTimes();
        replayAll();
        validator.setState(INITIAL.with(stack));
        validator.validateConfiguration();
        Configuration config = validator.getState().getConfig();
        RequestValidatorTest.verifyConfigOverrides(requestConfig, clusterConfig, stackConfig, config);
    }

    @Test
    public void rejectsKerberosEnvChange() {
        testBothValidationTypes(validator::validateConfiguration, () -> {
            Configuration requestConfig = Configuration.newEmpty();
            requestConfig.setProperty("kerberos-env", "some-property", "some-value");
            expect(request.getConfiguration()).andReturn(requestConfig.copy()).anyTimes();
            validator.setState(RequestValidator.State.INITIAL.with(simpleMockStack()));
        });
    }

    @Test
    public void rejectsKrb5ConfChange() {
        testBothValidationTypes(validator::validateConfiguration, () -> {
            Configuration requestConfig = Configuration.newEmpty();
            requestConfig.setProperty("krb5-conf", "some-property", "some-value");
            expect(request.getConfiguration()).andReturn(requestConfig.copy()).anyTimes();
            validator.setState(RequestValidator.State.INITIAL.with(simpleMockStack()));
        });
    }
}

