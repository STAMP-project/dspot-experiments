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


import SecurityType.KERBEROS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.ambari.server.controller.internal.ProvisionAction;
import org.apache.ambari.server.security.encryption.CredentialStoreType;
import org.apache.ambari.server.topology.ConfigRecommendationStrategy;
import org.apache.ambari.server.topology.Configuration;
import org.apache.ambari.server.topology.SecurityConfiguration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link AddServiceRequest} serialization / deserialization / syntax validation
 */
public class AddServiceRequestTest {
    private static String REQUEST_ALL_FIELDS_SET;

    private static String REQUEST_MINIMAL_SERVICES_AND_COMPONENTS;

    private static String REQUEST_MINIMAL_SERVICES_ONLY;

    private static String REQUEST_MINIMAL_COMPONENTS_ONLY;

    private static String REQUEST_INVALID_NO_SERVICES_AND_COMPONENTS;

    private static String REQUEST_INVALID_INVALID_FIELD;

    private static String REQUEST_INVALID_INVALID_CONFIG;

    private static final Map<String, List<Map<String, String>>> KERBEROS_DESCRIPTOR1 = ImmutableMap.of("services", ImmutableList.of(ImmutableMap.of("name", "ZOOKEEPER")));

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeserialize_basic() throws Exception {
        AddServiceRequest request = mapper.readValue(AddServiceRequestTest.REQUEST_ALL_FIELDS_SET, AddServiceRequest.class);
        Assert.assertEquals(OperationType.ADD_SERVICE, request.getOperationType());
        Assert.assertEquals(ConfigRecommendationStrategy.ALWAYS_APPLY, request.getRecommendationStrategy());
        Assert.assertEquals(ProvisionAction.INSTALL_ONLY, request.getProvisionAction());
        Assert.assertEquals(ValidationType.PERMISSIVE, request.getValidationType());
        Assert.assertEquals("HDP", request.getStackName());
        Assert.assertEquals("3.0", request.getStackVersion());
        Configuration configuration = request.getConfiguration();
        Assert.assertEquals(ImmutableMap.of("storm-site", ImmutableMap.of("final", ImmutableMap.of("fs.defaultFS", "true"))), configuration.getAttributes());
        Assert.assertEquals(ImmutableMap.of("storm-site", ImmutableMap.of("ipc.client.connect.max.retries", "50")), configuration.getProperties());
        Assert.assertEquals(ImmutableSet.of(Component.of("NIMBUS", ProvisionAction.START_ONLY, "c7401.ambari.apache.org", "c7402.ambari.apache.org"), Component.of("BEACON_SERVER", "c7402.ambari.apache.org", "c7403.ambari.apache.org")), request.getComponents());
        Assert.assertEquals(ImmutableSet.of(Service.of("STORM", ProvisionAction.INSTALL_AND_START), Service.of("BEACON")), request.getServices());
        Assert.assertEquals(Optional.of(SecurityConfiguration.forTest(KERBEROS, "ref_to_kerb_desc", AddServiceRequestTest.KERBEROS_DESCRIPTOR1)), request.getSecurity());
        Assert.assertEquals(ImmutableMap.of("kdc.admin.credential", new org.apache.ambari.server.topology.Credential("kdc.admin.credential", "admin/admin@EXAMPLE.COM", "k", CredentialStoreType.TEMPORARY)), request.getCredentials());
    }

    @Test
    public void testDeserialize_defaultAndEmptyValues() throws Exception {
        AddServiceRequest request = mapper.readValue(AddServiceRequestTest.REQUEST_MINIMAL_SERVICES_AND_COMPONENTS, AddServiceRequest.class);
        // filled-out values
        Assert.assertEquals(ImmutableSet.of(Component.of("NIMBUS", "c7401.ambari.apache.org", "c7402.ambari.apache.org"), Component.of("BEACON_SERVER", "c7402.ambari.apache.org", "c7403.ambari.apache.org")), request.getComponents());
        Assert.assertEquals(ImmutableSet.of(Service.of("STORM"), Service.of("BEACON")), request.getServices());
        // default / empty values
        Assert.assertEquals(OperationType.ADD_SERVICE, request.getOperationType());
        Assert.assertEquals(ConfigRecommendationStrategy.defaultForAddService(), request.getRecommendationStrategy());
        Assert.assertEquals(ProvisionAction.INSTALL_AND_START, request.getProvisionAction());
        Assert.assertEquals(ValidationType.STRICT, request.getValidationType());
        Assert.assertNull(request.getStackName());
        Assert.assertNull(request.getStackVersion());
        Assert.assertEquals(Optional.empty(), request.getSecurity());
        Assert.assertEquals(ImmutableMap.of(), request.getCredentials());
        Configuration configuration = request.getConfiguration();
        Assert.assertTrue(configuration.getFullAttributes().isEmpty());
        Assert.assertTrue(configuration.getFullProperties().isEmpty());
    }

    @Test
    public void testDeserialize_onlyServices() throws Exception {
        AddServiceRequest request = mapper.readValue(AddServiceRequestTest.REQUEST_MINIMAL_SERVICES_ONLY, AddServiceRequest.class);
        // filled-out values
        Assert.assertEquals(ImmutableSet.of(Service.of("STORM"), Service.of("BEACON")), request.getServices());
        // default / empty values
        Assert.assertEquals(OperationType.ADD_SERVICE, request.getOperationType());
        Assert.assertEquals(ConfigRecommendationStrategy.defaultForAddService(), request.getRecommendationStrategy());
        Assert.assertEquals(ProvisionAction.INSTALL_AND_START, request.getProvisionAction());
        Assert.assertNull(request.getStackName());
        Assert.assertNull(request.getStackVersion());
        Configuration configuration = request.getConfiguration();
        Assert.assertTrue(configuration.getFullAttributes().isEmpty());
        Assert.assertTrue(configuration.getFullProperties().isEmpty());
        Assert.assertTrue(request.getComponents().isEmpty());
    }

    @Test
    public void testDeserialize_onlyComponents() throws Exception {
        AddServiceRequest request = mapper.readValue(AddServiceRequestTest.REQUEST_MINIMAL_COMPONENTS_ONLY, AddServiceRequest.class);
        // filled-out values
        Assert.assertEquals(ImmutableSet.of(Component.of("NIMBUS", "c7401.ambari.apache.org", "c7402.ambari.apache.org"), Component.of("BEACON_SERVER", "c7402.ambari.apache.org", "c7403.ambari.apache.org")), request.getComponents());
        // default / empty values
        Assert.assertEquals(OperationType.ADD_SERVICE, request.getOperationType());
        Assert.assertEquals(ConfigRecommendationStrategy.defaultForAddService(), request.getRecommendationStrategy());
        Assert.assertEquals(ProvisionAction.INSTALL_AND_START, request.getProvisionAction());
        Assert.assertNull(request.getStackName());
        Assert.assertNull(request.getStackVersion());
        Configuration configuration = request.getConfiguration();
        Assert.assertTrue(configuration.getFullAttributes().isEmpty());
        Assert.assertTrue(configuration.getFullProperties().isEmpty());
        Assert.assertTrue(request.getServices().isEmpty());
    }

    @Test
    public void testDeserialize_invalid_noServicesAndComponents() throws Exception {
        // empty service/component list should be accepted at the JSON level, will be rejected by the request handler
        mapper.readValue(AddServiceRequestTest.REQUEST_INVALID_NO_SERVICES_AND_COMPONENTS, AddServiceRequest.class);
    }

    @Test(expected = JsonProcessingException.class)
    public void testDeserialize_invalid_invalidField() throws Exception {
        mapper.readValue(AddServiceRequestTest.REQUEST_INVALID_INVALID_FIELD, AddServiceRequest.class);
    }

    @Test(expected = JsonProcessingException.class)
    public void testDeserialize_invalid_invalidConfiguration() throws Exception {
        mapper.readValue(AddServiceRequestTest.REQUEST_INVALID_INVALID_CONFIG, AddServiceRequest.class);
    }

    @Test
    public void testSerialize_basic() throws Exception {
        AddServiceRequest request = mapper.readValue(AddServiceRequestTest.REQUEST_ALL_FIELDS_SET, AddServiceRequest.class);
        AddServiceRequest serialized = serialize(request);
        Assert.assertEquals(request, serialized);
        Assert.assertEquals(ImmutableMap.of(), serialized.getCredentials());
    }

    @Test
    public void testSerialize_EmptyOmitted() throws Exception {
        AddServiceRequest request = mapper.readValue(AddServiceRequestTest.REQUEST_MINIMAL_SERVICES_ONLY, AddServiceRequest.class);
        AddServiceRequest serialized = serialize(request);
        Assert.assertEquals(request, serialized);
    }
}

