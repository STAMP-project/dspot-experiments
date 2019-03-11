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
package org.apache.ambari.server.api.services.stackadvisor;


import ServiceInfo.ServiceAdvisorType.PYTHON;
import StackAdvisorCommandType.RECOMMEND_CONFIGURATIONS;
import StackAdvisorCommandType.RECOMMEND_CONFIGURATIONS_FOR_KERBEROS;
import StackAdvisorCommandType.RECOMMEND_CONFIGURATIONS_FOR_LDAP;
import StackAdvisorCommandType.RECOMMEND_CONFIGURATIONS_FOR_SSO;
import StackAdvisorRequestType.CONFIGURATIONS;
import StackAdvisorRequestType.KERBEROS_CONFIGURATIONS;
import StackAdvisorRequestType.LDAP_CONFIGURATIONS;
import StackAdvisorRequestType.SSO_CONFIGURATIONS;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.api.services.stackadvisor.StackAdvisorRequest.StackAdvisorRequestBuilder;
import org.apache.ambari.server.api.services.stackadvisor.StackAdvisorRequest.StackAdvisorRequestType;
import org.apache.ambari.server.api.services.stackadvisor.commands.ComponentLayoutRecommendationCommand;
import org.apache.ambari.server.api.services.stackadvisor.commands.ComponentLayoutValidationCommand;
import org.apache.ambari.server.api.services.stackadvisor.commands.ConfigurationDependenciesRecommendationCommand;
import org.apache.ambari.server.api.services.stackadvisor.commands.ConfigurationRecommendationCommand;
import org.apache.ambari.server.api.services.stackadvisor.commands.ConfigurationValidationCommand;
import org.apache.ambari.server.api.services.stackadvisor.commands.StackAdvisorCommand;
import org.apache.ambari.server.api.services.stackadvisor.recommendations.RecommendationResponse;
import org.apache.ambari.server.api.services.stackadvisor.validations.ValidationResponse;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.internal.AmbariServerConfigurationHandler;
import org.apache.ambari.server.state.ServiceInfo;
import org.codehaus.jackson.JsonNode;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * StackAdvisorHelper unit tests.
 */
public class StackAdvisorHelperTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testValidate_returnsCommandResult() throws IOException, StackAdvisorException {
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configuration.getRecommendationsArtifactsRolloverMax()).thenReturn(100);
        StackAdvisorRunner saRunner = Mockito.mock(StackAdvisorRunner.class);
        AmbariMetaInfo metaInfo = Mockito.mock(AmbariMetaInfo.class);
        ServiceInfo service = Mockito.mock(ServiceInfo.class);
        Mockito.when(metaInfo.getService(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(service);
        Mockito.when(service.getServiceAdvisorType()).thenReturn(PYTHON);
        StackAdvisorHelper helper = StackAdvisorHelperTest.stackAdvisorHelperSpy(configuration, saRunner, metaInfo);
        StackAdvisorCommand<ValidationResponse> command = Mockito.mock(StackAdvisorCommand.class);
        ValidationResponse expected = Mockito.mock(ValidationResponse.class);
        StackAdvisorRequestType requestType = StackAdvisorRequestType.HOST_GROUPS;
        StackAdvisorRequest request = StackAdvisorRequestBuilder.forStack("stackName", "stackVersion").ofType(requestType).build();
        Mockito.when(command.invoke(request, PYTHON)).thenReturn(expected);
        Mockito.doReturn(command).when(helper).createValidationCommand("ZOOKEEPER", request);
        ValidationResponse response = helper.validate(request);
        Assert.assertEquals(expected, response);
    }

    @Test(expected = StackAdvisorException.class)
    @SuppressWarnings("unchecked")
    public void testValidate_commandThrowsException_throwsException() throws IOException, StackAdvisorException {
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configuration.getRecommendationsArtifactsRolloverMax()).thenReturn(100);
        StackAdvisorRunner saRunner = Mockito.mock(StackAdvisorRunner.class);
        AmbariMetaInfo metaInfo = Mockito.mock(AmbariMetaInfo.class);
        ServiceInfo service = Mockito.mock(ServiceInfo.class);
        Mockito.when(metaInfo.getService(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(service);
        Mockito.when(service.getServiceAdvisorType()).thenReturn(PYTHON);
        StackAdvisorHelper helper = StackAdvisorHelperTest.stackAdvisorHelperSpy(configuration, saRunner, metaInfo);
        StackAdvisorCommand<ValidationResponse> command = Mockito.mock(StackAdvisorCommand.class);
        StackAdvisorRequestType requestType = StackAdvisorRequestType.HOST_GROUPS;
        StackAdvisorRequest request = StackAdvisorRequestBuilder.forStack("stackName", "stackVersion").ofType(requestType).build();
        Mockito.when(command.invoke(request, PYTHON)).thenThrow(new StackAdvisorException("message"));
        Mockito.doReturn(command).when(helper).createValidationCommand("ZOOKEEPER", request);
        helper.validate(request);
        Assert.fail();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRecommend_returnsCommandResult() throws IOException, StackAdvisorException {
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configuration.getRecommendationsArtifactsRolloverMax()).thenReturn(100);
        StackAdvisorRunner saRunner = Mockito.mock(StackAdvisorRunner.class);
        AmbariMetaInfo metaInfo = Mockito.mock(AmbariMetaInfo.class);
        ServiceInfo service = Mockito.mock(ServiceInfo.class);
        Mockito.when(metaInfo.getService(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(service);
        Mockito.when(service.getServiceAdvisorType()).thenReturn(PYTHON);
        StackAdvisorHelper helper = StackAdvisorHelperTest.stackAdvisorHelperSpy(configuration, saRunner, metaInfo);
        StackAdvisorCommand<RecommendationResponse> command = Mockito.mock(StackAdvisorCommand.class);
        RecommendationResponse expected = Mockito.mock(RecommendationResponse.class);
        StackAdvisorRequestType requestType = StackAdvisorRequestType.HOST_GROUPS;
        StackAdvisorRequest request = StackAdvisorRequestBuilder.forStack("stackName", "stackVersion").ofType(requestType).build();
        Mockito.when(command.invoke(request, PYTHON)).thenReturn(expected);
        Mockito.doReturn(command).when(helper).createRecommendationCommand("ZOOKEEPER", request);
        RecommendationResponse response = helper.recommend(request);
        Assert.assertEquals(expected, response);
    }

    @Test(expected = StackAdvisorException.class)
    @SuppressWarnings("unchecked")
    public void testRecommend_commandThrowsException_throwsException() throws IOException, StackAdvisorException {
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configuration.getRecommendationsArtifactsRolloverMax()).thenReturn(100);
        StackAdvisorRunner saRunner = Mockito.mock(StackAdvisorRunner.class);
        AmbariMetaInfo metaInfo = Mockito.mock(AmbariMetaInfo.class);
        ServiceInfo service = Mockito.mock(ServiceInfo.class);
        Mockito.when(metaInfo.getService(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(service);
        Mockito.when(service.getServiceAdvisorType()).thenReturn(PYTHON);
        StackAdvisorHelper helper = StackAdvisorHelperTest.stackAdvisorHelperSpy(configuration, saRunner, metaInfo);
        StackAdvisorCommand<RecommendationResponse> command = Mockito.mock(StackAdvisorCommand.class);
        StackAdvisorRequestType requestType = StackAdvisorRequestType.HOST_GROUPS;
        StackAdvisorRequest request = StackAdvisorRequestBuilder.forStack("stackName", "stackVersion").ofType(requestType).build();
        Mockito.when(command.invoke(request, PYTHON)).thenThrow(new StackAdvisorException("message"));
        Mockito.doReturn(command).when(helper).createRecommendationCommand("ZOOKEEPER", request);
        helper.recommend(request);
        Assert.fail("Expected StackAdvisorException to be thrown");
    }

    @Test
    public void testCreateRecommendationCommand_returnsComponentLayoutRecommendationCommand() throws IOException, StackAdvisorException {
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configuration.getRecommendationsArtifactsRolloverMax()).thenReturn(100);
        StackAdvisorRunner saRunner = Mockito.mock(StackAdvisorRunner.class);
        AmbariMetaInfo metaInfo = Mockito.mock(AmbariMetaInfo.class);
        ServiceInfo service = Mockito.mock(ServiceInfo.class);
        Mockito.when(metaInfo.getService(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(service);
        Mockito.when(service.getServiceAdvisorType()).thenReturn(PYTHON);
        StackAdvisorHelper helper = new StackAdvisorHelper(configuration, saRunner, metaInfo, null, null);
        StackAdvisorRequestType requestType = StackAdvisorRequestType.HOST_GROUPS;
        StackAdvisorRequest request = StackAdvisorRequestBuilder.forStack("stackName", "stackVersion").ofType(requestType).build();
        StackAdvisorCommand<RecommendationResponse> command = helper.createRecommendationCommand("ZOOKEEPER", request);
        Assert.assertEquals(ComponentLayoutRecommendationCommand.class, command.getClass());
    }

    @Test
    public void testCreateRecommendationCommand_returnsConfigurationRecommendationCommand() throws IOException, StackAdvisorException {
        testCreateConfigurationRecommendationCommand(CONFIGURATIONS, RECOMMEND_CONFIGURATIONS);
    }

    @Test
    public void testCreateRecommendationCommand_returnsSingleSignOnConfigurationRecommendationCommand() throws IOException, StackAdvisorException {
        testCreateConfigurationRecommendationCommand(SSO_CONFIGURATIONS, RECOMMEND_CONFIGURATIONS_FOR_SSO);
    }

    @Test
    public void testCreateRecommendationCommand_returnsLDAPConfigurationRecommendationCommand() throws IOException, StackAdvisorException {
        testCreateConfigurationRecommendationCommand(LDAP_CONFIGURATIONS, RECOMMEND_CONFIGURATIONS_FOR_LDAP);
    }

    @Test
    public void testCreateRecommendationCommand_returnsKerberosConfigurationRecommendationCommand() throws IOException, StackAdvisorException {
        testCreateConfigurationRecommendationCommand(KERBEROS_CONFIGURATIONS, RECOMMEND_CONFIGURATIONS_FOR_KERBEROS);
    }

    @Test
    public void testCreateValidationCommand_returnsComponentLayoutValidationCommand() throws IOException, StackAdvisorException {
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configuration.getRecommendationsArtifactsRolloverMax()).thenReturn(100);
        StackAdvisorRunner saRunner = Mockito.mock(StackAdvisorRunner.class);
        AmbariMetaInfo metaInfo = Mockito.mock(AmbariMetaInfo.class);
        ServiceInfo service = Mockito.mock(ServiceInfo.class);
        Mockito.when(metaInfo.getService(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(service);
        Mockito.when(service.getServiceAdvisorType()).thenReturn(PYTHON);
        StackAdvisorHelper helper = new StackAdvisorHelper(configuration, saRunner, metaInfo, null, null);
        StackAdvisorRequestType requestType = StackAdvisorRequestType.HOST_GROUPS;
        StackAdvisorRequest request = StackAdvisorRequestBuilder.forStack("stackName", "stackVersion").ofType(requestType).build();
        StackAdvisorCommand<ValidationResponse> command = helper.createValidationCommand("ZOOKEEPER", request);
        Assert.assertEquals(ComponentLayoutValidationCommand.class, command.getClass());
    }

    @Test
    public void testCreateValidationCommand_returnsConfigurationValidationCommand() throws IOException, StackAdvisorException {
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configuration.getRecommendationsArtifactsRolloverMax()).thenReturn(100);
        StackAdvisorRunner saRunner = Mockito.mock(StackAdvisorRunner.class);
        AmbariMetaInfo metaInfo = Mockito.mock(AmbariMetaInfo.class);
        ServiceInfo service = Mockito.mock(ServiceInfo.class);
        Mockito.when(metaInfo.getService(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(service);
        Mockito.when(service.getServiceAdvisorType()).thenReturn(PYTHON);
        StackAdvisorHelper helper = new StackAdvisorHelper(configuration, saRunner, metaInfo, null, null);
        StackAdvisorRequestType requestType = StackAdvisorRequestType.CONFIGURATIONS;
        StackAdvisorRequest request = StackAdvisorRequestBuilder.forStack("stackName", "stackVersion").ofType(requestType).build();
        StackAdvisorCommand<ValidationResponse> command = helper.createValidationCommand("ZOOKEEPER", request);
        Assert.assertEquals(ConfigurationValidationCommand.class, command.getClass());
    }

    @Test
    public void testCreateRecommendationDependencyCommand_returnsConfigurationDependencyRecommendationCommand() throws IOException, StackAdvisorException {
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configuration.getRecommendationsArtifactsRolloverMax()).thenReturn(100);
        StackAdvisorRunner saRunner = Mockito.mock(StackAdvisorRunner.class);
        AmbariMetaInfo metaInfo = Mockito.mock(AmbariMetaInfo.class);
        ServiceInfo service = Mockito.mock(ServiceInfo.class);
        Mockito.when(metaInfo.getService(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(service);
        Mockito.when(service.getServiceAdvisorType()).thenReturn(PYTHON);
        StackAdvisorHelper helper = new StackAdvisorHelper(configuration, saRunner, metaInfo, null, null);
        StackAdvisorRequestType requestType = StackAdvisorRequestType.CONFIGURATION_DEPENDENCIES;
        StackAdvisorRequest request = StackAdvisorRequestBuilder.forStack("stackName", "stackVersion").ofType(requestType).build();
        StackAdvisorCommand<RecommendationResponse> command = helper.createRecommendationCommand("ZOOKEEPER", request);
        Assert.assertEquals(ConfigurationDependenciesRecommendationCommand.class, command.getClass());
    }

    @Test
    public void testClearCacheAndHost() throws IOException, IllegalAccessException, NoSuchFieldException {
        Field hostInfoCacheField = StackAdvisorHelper.class.getDeclaredField("hostInfoCache");
        Field configsRecommendationResponseField = StackAdvisorHelper.class.getDeclaredField("configsRecommendationResponse");
        StackAdvisorHelper helper = testClearCachesSetup(hostInfoCacheField, configsRecommendationResponseField);
        helper.clearCaches("hostName1");
        Map<String, JsonNode> hostInfoCache = ((Map<String, JsonNode>) (hostInfoCacheField.get(helper)));
        Map<String, RecommendationResponse> configsRecommendationResponse = ((Map<String, RecommendationResponse>) (configsRecommendationResponseField.get(helper)));
        Assert.assertEquals(2, hostInfoCache.size());
        Assert.assertTrue(hostInfoCache.containsKey("hostName2"));
        Assert.assertTrue(hostInfoCache.containsKey("hostName3"));
        Assert.assertTrue(configsRecommendationResponse.isEmpty());
    }

    @Test
    public void testClearCacheAndHosts() throws IOException, IllegalAccessException, NoSuchFieldException {
        Field hostInfoCacheField = StackAdvisorHelper.class.getDeclaredField("hostInfoCache");
        Field configsRecommendationResponseField = StackAdvisorHelper.class.getDeclaredField("configsRecommendationResponse");
        StackAdvisorHelper helper = testClearCachesSetup(hostInfoCacheField, configsRecommendationResponseField);
        helper.clearCaches(new HashSet(Arrays.asList(new String[]{ "hostName1", "hostName2" })));
        Map<String, JsonNode> hostInfoCache = ((Map<String, JsonNode>) (hostInfoCacheField.get(helper)));
        Map<String, RecommendationResponse> configsRecommendationResponse = ((Map<String, RecommendationResponse>) (configsRecommendationResponseField.get(helper)));
        Assert.assertEquals(1, hostInfoCache.size());
        Assert.assertTrue(hostInfoCache.containsKey("hostName3"));
        Assert.assertTrue(configsRecommendationResponse.isEmpty());
    }

    @Test
    public void testCacheRecommendations() throws IOException, StackAdvisorException {
        Configuration configuration = createNiceMock(Configuration.class);
        StackAdvisorRunner stackAdvisorRunner = createNiceMock(StackAdvisorRunner.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        AmbariServerConfigurationHandler ambariServerConfigurationHandler = createNiceMock(AmbariServerConfigurationHandler.class);
        expect(configuration.getRecommendationsArtifactsRolloverMax()).andReturn(1);
        replay(configuration, stackAdvisorRunner, ambariMetaInfo, ambariServerConfigurationHandler);
        StackAdvisorHelper helper = partialMockBuilder(StackAdvisorHelper.class).withConstructor(Configuration.class, StackAdvisorRunner.class, AmbariMetaInfo.class, AmbariServerConfigurationHandler.class, Gson.class).withArgs(configuration, stackAdvisorRunner, ambariMetaInfo, ambariServerConfigurationHandler, new Gson()).addMockedMethod("createRecommendationCommand").createMock();
        verify(configuration, stackAdvisorRunner, ambariMetaInfo, ambariServerConfigurationHandler);
        reset(ambariMetaInfo);
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServiceAdvisorType(PYTHON);
        expect(ambariMetaInfo.getService(anyString(), anyString(), anyString())).andReturn(serviceInfo).atLeastOnce();
        ConfigurationRecommendationCommand command = createMock(ConfigurationRecommendationCommand.class);
        StackAdvisorRequest request = StackAdvisorRequestBuilder.forStack(null, null).ofType(CONFIGURATIONS).build();
        expect(helper.createRecommendationCommand(eq("ZOOKEEPER"), eq(request))).andReturn(command).times(2);
        // populate response with dummy info to check equivalence
        RecommendationResponse response = new RecommendationResponse();
        response.setServices(new HashSet<String>() {
            {
                add("service1");
                add("service2");
                add("service3");
            }
        });
        // invoke() should be fired for first recommend() execution only
        expect(command.invoke(eq(request), eq(PYTHON))).andReturn(response).once();
        replay(ambariMetaInfo, helper, command);
        RecommendationResponse calculatedResponse = helper.recommend(request);
        RecommendationResponse cachedResponse = helper.recommend(request);
        verify(ambariMetaInfo, helper, command);
        Assert.assertEquals(response.getServices(), calculatedResponse.getServices());
        Assert.assertEquals(response.getServices(), cachedResponse.getServices());
    }
}

