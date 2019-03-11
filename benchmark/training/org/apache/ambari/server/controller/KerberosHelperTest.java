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
package org.apache.ambari.server.controller;


import CredentialStoreType.TEMPORARY;
import KerberosHelper.ALLOW_RETRY;
import KerberosHelper.CREATE_AMBARI_PRINCIPAL;
import KerberosHelper.DEFAULT_REALM;
import KerberosHelper.DIRECTIVE_FORCE_TOGGLE_KERBEROS;
import KerberosHelper.DIRECTIVE_MANAGE_KERBEROS_IDENTITIES;
import KerberosHelper.DIRECTIVE_REGENERATE_KEYTABS;
import KerberosHelper.INCLUDE_ALL_COMPONENTS_IN_AUTH_TO_LOCAL_RULES;
import KerberosHelper.KDC_ADMINISTRATOR_CREDENTIAL_ALIAS;
import KerberosHelper.KDC_TYPE;
import KerberosPrincipalType.SERVICE;
import RecommendationResponse.BindingHostGroup;
import RecommendationResponse.Blueprint;
import RecommendationResponse.BlueprintClusterBinding;
import RecommendationResponse.BlueprintConfigurations;
import RecommendationResponse.HostGroup;
import RecommendationResponse.Recommendation;
import SecurityType.KERBEROS;
import SecurityType.NONE;
import State.INSTALLED;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.actionmanager.Stage;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.api.services.stackadvisor.StackAdvisorHelper;
import org.apache.ambari.server.api.services.stackadvisor.StackAdvisorRequest;
import org.apache.ambari.server.api.services.stackadvisor.recommendations.RecommendationResponse;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.internal.RequestStageContainer;
import org.apache.ambari.server.controller.spi.ClusterController;
import org.apache.ambari.server.security.credential.PrincipalKeyCredential;
import org.apache.ambari.server.security.encryption.CredentialStoreService;
import org.apache.ambari.server.serveraction.kerberos.KerberosConfigDataFileWriterFactory;
import org.apache.ambari.server.serveraction.kerberos.KerberosInvalidConfigurationException;
import org.apache.ambari.server.serveraction.kerberos.KerberosMissingAdminCredentialsException;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.ComponentInfo;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.PropertyInfo;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.kerberos.KerberosComponentDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosConfigurationDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosDescriptorFactory;
import org.apache.ambari.server.state.kerberos.KerberosIdentityDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosKeytabDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosPrincipalDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosServiceDescriptor;
import org.apache.ambari.server.topology.TopologyManager;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


@SuppressWarnings("unchecked")
public class KerberosHelperTest extends EasyMockSupport {
    private static Injector injector;

    private final ClusterController clusterController = createStrictMock(ClusterController.class);

    private final KerberosDescriptorFactory kerberosDescriptorFactory = createStrictMock(KerberosDescriptorFactory.class);

    private final KerberosConfigDataFileWriterFactory kerberosConfigDataFileWriterFactory = createStrictMock(KerberosConfigDataFileWriterFactory.class);

    private final AmbariMetaInfo metaInfo = createMock(AmbariMetaInfo.class);

    private final TopologyManager topologyManager = createMock(TopologyManager.class);

    private final Configuration configuration = createMock(Configuration.class);

    private final AmbariCustomCommandExecutionHelper customCommandExecutionHelperMock = createNiceMock(AmbariCustomCommandExecutionHelper.class);

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test(expected = AmbariException.class)
    public void testMissingClusterEnv() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        Cluster cluster = createNiceMock(Cluster.class);
        RequestStageContainer requestStageContainer = createNiceMock(RequestStageContainer.class);
        replayAll();
        kerberosHelper.toggleKerberos(cluster, KERBEROS, requestStageContainer, true);
        verifyAll();
    }

    @Test(expected = AmbariException.class)
    public void testMissingKrb5Conf() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        final Map<String, String> kerberosEnvProperties = createMock(Map.class);
        expect(kerberosEnvProperties.get("ldap_url")).andReturn("").once();
        expect(kerberosEnvProperties.get("container_dn")).andReturn("").once();
        final Config kerberosEnvConfig = createMock(Config.class);
        expect(kerberosEnvConfig.getProperties()).andReturn(kerberosEnvProperties).once();
        final Cluster cluster = createNiceMock(Cluster.class);
        expect(cluster.getDesiredConfigByType("kerberos-env")).andReturn(kerberosEnvConfig).once();
        replayAll();
        kerberosHelper.toggleKerberos(cluster, KERBEROS, null, true);
        verifyAll();
    }

    @Test(expected = AmbariException.class)
    public void testMissingKerberosEnvConf() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        final Map<String, String> kerberosEnvProperties = createMock(Map.class);
        expect(kerberosEnvProperties.get(DEFAULT_REALM)).andReturn("EXAMPLE.COM").once();
        expect(kerberosEnvProperties.get("kdc_hosts")).andReturn("10.0.100.1").once();
        final Map<String, String> krb5ConfProperties = createMock(Map.class);
        expect(krb5ConfProperties.get("kadmin_host")).andReturn("10.0.100.1").once();
        final Config krb5ConfConfig = createMock(Config.class);
        expect(krb5ConfConfig.getProperties()).andReturn(krb5ConfProperties).once();
        final Cluster cluster = createNiceMock(Cluster.class);
        expect(cluster.getDesiredConfigByType("krb5-conf")).andReturn(krb5ConfConfig).once();
        replayAll();
        kerberosHelper.toggleKerberos(cluster, KERBEROS, null, true);
        verifyAll();
    }

    @Test
    public void testEnableKerberos() throws Exception {
        testEnableKerberos(new PrincipalKeyCredential("principal", "password"), "mit-kdc", "true");
    }

    @Test
    public void testEnableKerberos_ManageIdentitiesFalseKdcNone() throws Exception {
        testEnableKerberos(new PrincipalKeyCredential("principal", "password"), "none", "false");
    }

    @Test(expected = AmbariException.class)
    public void testEnableKerberos_ManageIdentitiesTrueKdcNone() throws Exception {
        testEnableKerberos(new PrincipalKeyCredential("principal", "password"), "none", "true");
    }

    @Test(expected = KerberosInvalidConfigurationException.class)
    public void testEnableKerberos_ManageIdentitiesTrueKdcNull() throws Exception {
        testEnableKerberos(new PrincipalKeyCredential("principal", "password"), null, "true");
    }

    @Test(expected = KerberosMissingAdminCredentialsException.class)
    public void testEnableKerberosMissingCredentials() throws Exception {
        try {
            testEnableKerberos(null, "mit-kdc", "true");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().startsWith("Missing KDC administrator credentials"));
            throw e;
        }
    }

    @Test(expected = KerberosMissingAdminCredentialsException.class)
    public void testEnableKerberosInvalidCredentials() throws Exception {
        try {
            testEnableKerberos(new PrincipalKeyCredential("invalid_principal", "password"), "mit-kdc", "true");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().startsWith("Invalid KDC administrator credentials"));
            throw e;
        }
    }

    @Test
    public void testEnableKerberos_GetKerberosDescriptorFromCluster() throws Exception {
        testEnableKerberos(new PrincipalKeyCredential("principal", "password"), "mit-kdc", "true");
    }

    @Test
    public void testEnableKerberos_GetKerberosDescriptorFromStack() throws Exception {
        testEnableKerberos(new PrincipalKeyCredential("principal", "password"), "mit-kdc", "true");
    }

    @Test
    public void testEnsureIdentities() throws Exception {
        testEnsureIdentities(new PrincipalKeyCredential("principal", "password"), null);
    }

    @Test(expected = KerberosMissingAdminCredentialsException.class)
    public void testEnsureIdentitiesMissingCredentials() throws Exception {
        try {
            testEnsureIdentities(null, null);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().startsWith("Missing KDC administrator credentials"));
            throw e;
        }
    }

    @Test(expected = KerberosMissingAdminCredentialsException.class)
    public void testEnsureIdentitiesInvalidCredentials() throws Exception {
        try {
            testEnsureIdentities(new PrincipalKeyCredential("invalid_principal", "password"), null);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().startsWith("Invalid KDC administrator credentials"));
            throw e;
        }
    }

    @Test
    public void testEnsureIdentities_FilteredHosts() throws Exception {
        testEnsureIdentities(new PrincipalKeyCredential("principal", "password"), Collections.singleton("hostA"));
    }

    @Test
    public void testDeleteIdentities() throws Exception {
        testDeleteIdentities(new PrincipalKeyCredential("principal", "password"));
    }

    @Test(expected = KerberosMissingAdminCredentialsException.class)
    public void testDeleteIdentitiesMissingCredentials() throws Exception {
        try {
            testDeleteIdentities(null);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().startsWith("Missing KDC administrator credentials"));
            throw e;
        }
    }

    @Test(expected = KerberosMissingAdminCredentialsException.class)
    public void testDeleteIdentitiesInvalidCredentials() throws Exception {
        try {
            testDeleteIdentities(new PrincipalKeyCredential("invalid_principal", "password"));
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().startsWith("Invalid KDC administrator credentials"));
            throw e;
        }
    }

    @Test
    public void testExecuteCustomOperationsInvalidOperation() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        final Cluster cluster = createNiceMock(Cluster.class);
        try {
            kerberosHelper.executeCustomOperations(cluster, Collections.singletonMap("invalid_operation", "false"), null, true);
        } catch (Throwable t) {
            Assert.fail("Exception should not have been thrown");
        }
    }

    @Test(expected = AmbariException.class)
    public void testRegenerateKeytabsInvalidValue() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        final Cluster cluster = createNiceMock(Cluster.class);
        kerberosHelper.executeCustomOperations(cluster, Collections.singletonMap(DIRECTIVE_REGENERATE_KEYTABS, "false"), null, true);
        Assert.fail("AmbariException should have failed");
    }

    @Test
    public void testRegenerateKeytabsValidateRequestStageContainer() throws Exception {
        testRegenerateKeytabs(new PrincipalKeyCredential("principal", "password"), true, false);
    }

    @Test
    public void testRegenerateKeytabsValidateSkipInvalidHost() throws Exception {
        testRegenerateKeytabs(new PrincipalKeyCredential("principal", "password"), true, true);
    }

    @Test
    public void testRegenerateKeytabs() throws Exception {
        testRegenerateKeytabs(new PrincipalKeyCredential("principal", "password"), false, false);
    }

    /**
     * Tests that when regenerating keytabs for an upgrade, that the retry allowed
     * boolean is set on the tasks created.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRegenerateKeytabsWithRetryAllowed() throws Exception {
        Capture<ActionExecutionContext> captureContext = Capture.newInstance();
        customCommandExecutionHelperMock.addExecutionCommandsToStage(capture(captureContext), anyObject(Stage.class), anyObject(), eq(null));
        expectLastCall().atLeastOnce();
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put(DIRECTIVE_REGENERATE_KEYTABS, "true");
        requestMap.put(ALLOW_RETRY, "true");
        RequestStageContainer requestStageContainer = testRegenerateKeytabs(new PrincipalKeyCredential("principal", "password"), requestMap, false, false);
        Assert.assertNotNull(requestStageContainer);
        ActionExecutionContext capturedContext = captureContext.getValue();
        Assert.assertTrue(capturedContext.isRetryAllowed());
    }

    @Test
    public void testDisableKerberos() throws Exception {
        testDisableKerberos(new PrincipalKeyCredential("principal", "password"));
    }

    @Test
    public void testCreateTestIdentity_ManageIdentitiesDefault() throws Exception {
        testCreateTestIdentity(new PrincipalKeyCredential("principal", "password"), null);
    }

    @Test
    public void testCreateTestIdentity_ManageIdentitiesTrue() throws Exception {
        testCreateTestIdentity(new PrincipalKeyCredential("principal", "password"), Boolean.TRUE);
    }

    @Test
    public void testCreateTestIdentity_ManageIdentitiesFalse() throws Exception {
        testCreateTestIdentity(new PrincipalKeyCredential("principal", "password"), Boolean.FALSE);
    }

    @Test(expected = KerberosMissingAdminCredentialsException.class)
    public void testCreateTestIdentityNoCredentials_ManageIdentitiesDefault() throws Exception {
        testCreateTestIdentity(null, null);
    }

    @Test(expected = KerberosMissingAdminCredentialsException.class)
    public void testCreateTestIdentityNoCredentials_ManageIdentitiesTrue() throws Exception {
        testCreateTestIdentity(null, Boolean.TRUE);
    }

    @Test
    public void testCreateTestIdentityNoCredentials_ManageIdentitiesFalse() throws Exception {
        testCreateTestIdentity(null, Boolean.FALSE);
    }

    @Test
    public void testDeleteTestIdentity() throws Exception {
        testDeleteTestIdentity(new PrincipalKeyCredential("principal", "password"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetActiveIdentities_MissingCluster() throws Exception {
        testGetActiveIdentities(null, null, null, null, true, KERBEROS);
    }

    @Test
    public void testGetActiveIdentities_SecurityTypeKerberos_All() throws Exception {
        testGetActiveIdentities_All(KERBEROS);
    }

    @Test
    public void testGetActiveIdentities_SecurityTypeNone_All() throws Exception {
        testGetActiveIdentities_All(NONE);
    }

    @Test
    public void testGetActiveIdentities_SingleHost() throws Exception {
        Map<String, Collection<KerberosIdentityDescriptor>> identities = testGetActiveIdentities("c1", "host1", null, null, true, KERBEROS);
        Assert.assertNotNull(identities);
        Assert.assertEquals(1, identities.size());
        Collection<KerberosIdentityDescriptor> hostIdentities;
        hostIdentities = identities.get("host1");
        Assert.assertNotNull(hostIdentities);
        Assert.assertEquals(3, hostIdentities.size());
        validateIdentities(hostIdentities, new HashMap<String, Map<String, Object>>() {
            {
                put("identity1", new HashMap<String, Object>() {
                    {
                        put("principal_name", "service1/host1@EXAMPLE.COM");
                        put("principal_type", SERVICE);
                        put("principal_configuration", "service1-site/component1.kerberos.principal");
                        put("principal_local_username", "service1");
                        put("keytab_file", "${keytab_dir}/service1.keytab");
                        put("keytab_owner_name", "service1");
                        put("keytab_owner_access", "rw");
                        put("keytab_group_name", "hadoop");
                        put("keytab_group_access", "");
                        put("keytab_configuration", "service1-site/component1.keytab.file");
                        put("keytab_cachable", false);
                    }
                });
                put("identity2", new HashMap<String, Object>() {
                    {
                        put("principal_name", "component2/host1@EXAMPLE.COM");
                        put("principal_type", SERVICE);
                        put("principal_configuration", "service2-site/component2.kerberos.principal");
                        put("principal_local_username", "service2");
                        put("keytab_file", "${keytab_dir}/service2.keytab");
                        put("keytab_owner_name", "service2");
                        put("keytab_owner_access", "rw");
                        put("keytab_group_name", "hadoop");
                        put("keytab_group_access", "");
                        put("keytab_configuration", "service2-site/component2.keytab.file");
                        put("keytab_cachable", false);
                    }
                });
                put("identity3", new HashMap<String, Object>() {
                    {
                        put("principal_name", "service1/host1@EXAMPLE.COM");
                        put("principal_type", SERVICE);
                        put("principal_configuration", "service1-site/service1.kerberos.principal");
                        put("principal_local_username", "service1");
                        put("keytab_file", "${keytab_dir}/service1.service.keytab");
                        put("keytab_owner_name", "service1");
                        put("keytab_owner_access", "rw");
                        put("keytab_group_name", "hadoop");
                        put("keytab_group_access", "");
                        put("keytab_configuration", "service1-site/service1.keytab.file");
                        put("keytab_cachable", false);
                    }
                });
            }
        });
    }

    @Test
    public void addAmbariServerIdentity_CreateAmbariPrincipal() throws Exception {
        addAmbariServerIdentity(Collections.singletonMap(CREATE_AMBARI_PRINCIPAL, "true"));
    }

    @Test
    public void addAmbariServerIdentity_DoNotCreateAmbariPrincipal() throws Exception {
        addAmbariServerIdentity(Collections.singletonMap(CREATE_AMBARI_PRINCIPAL, "false"));
    }

    @Test
    public void addAmbariServerIdentity_MissingProperty() throws Exception {
        addAmbariServerIdentity(Collections.singletonMap("not_create_ambari_principal", "value"));
    }

    @Test
    public void addAmbariServerIdentity_MissingKerberosEnv() throws Exception {
        addAmbariServerIdentity(null);
    }

    @Test
    public void testGetActiveIdentities_SingleService() throws Exception {
        Map<String, Collection<KerberosIdentityDescriptor>> identities = testGetActiveIdentities("c1", null, "SERVICE1", null, true, KERBEROS);
        Assert.assertNotNull(identities);
        Assert.assertEquals(3, identities.size());
        Collection<KerberosIdentityDescriptor> hostIdentities;
        hostIdentities = identities.get("host1");
        Assert.assertNotNull(hostIdentities);
        Assert.assertEquals(2, hostIdentities.size());
        validateIdentities(hostIdentities, new HashMap<String, Map<String, Object>>() {
            {
                put("identity1", new HashMap<String, Object>() {
                    {
                        put("principal_name", "service1/host1@EXAMPLE.COM");
                        put("principal_type", SERVICE);
                        put("principal_configuration", "service1-site/component1.kerberos.principal");
                        put("principal_local_username", "service1");
                        put("keytab_file", "${keytab_dir}/service1.keytab");
                        put("keytab_owner_name", "service1");
                        put("keytab_owner_access", "rw");
                        put("keytab_group_name", "hadoop");
                        put("keytab_group_access", "");
                        put("keytab_configuration", "service1-site/component1.keytab.file");
                        put("keytab_cachable", false);
                    }
                });
                put("identity3", new HashMap<String, Object>() {
                    {
                        put("principal_name", "service1/host1@EXAMPLE.COM");
                        put("principal_type", SERVICE);
                        put("principal_configuration", "service1-site/service1.kerberos.principal");
                        put("principal_local_username", "service1");
                        put("keytab_file", "${keytab_dir}/service1.service.keytab");
                        put("keytab_owner_name", "service1");
                        put("keytab_owner_access", "rw");
                        put("keytab_group_name", "hadoop");
                        put("keytab_group_access", "");
                        put("keytab_configuration", "service1-site/service1.keytab.file");
                        put("keytab_cachable", false);
                    }
                });
            }
        });
        hostIdentities = identities.get("host2");
        Assert.assertNotNull(hostIdentities);
        Assert.assertEquals(2, hostIdentities.size());
        validateIdentities(hostIdentities, new HashMap<String, Map<String, Object>>() {
            {
                put("identity1", new HashMap<String, Object>() {
                    {
                        put("principal_name", "service1/host2@EXAMPLE.COM");
                        put("principal_type", SERVICE);
                        put("principal_configuration", "service1-site/component1.kerberos.principal");
                        put("principal_local_username", "service1");
                        put("keytab_file", "${keytab_dir}/service1.keytab");
                        put("keytab_owner_name", "service1");
                        put("keytab_owner_access", "rw");
                        put("keytab_group_name", "hadoop");
                        put("keytab_group_access", "");
                        put("keytab_configuration", "service1-site/component1.keytab.file");
                        put("keytab_cachable", false);
                    }
                });
                put("identity3", new HashMap<String, Object>() {
                    {
                        put("principal_name", "service1/host2@EXAMPLE.COM");
                        put("principal_type", SERVICE);
                        put("principal_configuration", "service1-site/service1.kerberos.principal");
                        put("principal_local_username", "service1");
                        put("keytab_file", "${keytab_dir}/service1.service.keytab");
                        put("keytab_owner_name", "service1");
                        put("keytab_owner_access", "rw");
                        put("keytab_group_name", "hadoop");
                        put("keytab_group_access", "");
                        put("keytab_configuration", "service1-site/service1.keytab.file");
                        put("keytab_cachable", false);
                    }
                });
            }
        });
    }

    @Test
    public void testGetActiveIdentities_SingleServiceSingleHost() throws Exception {
        Map<String, Collection<KerberosIdentityDescriptor>> identities = testGetActiveIdentities("c1", "host2", "SERVICE1", null, true, KERBEROS);
        Assert.assertNotNull(identities);
        Assert.assertEquals(1, identities.size());
        Collection<KerberosIdentityDescriptor> hostIdentities;
        hostIdentities = identities.get("host2");
        Assert.assertNotNull(hostIdentities);
        Assert.assertEquals(2, hostIdentities.size());
        validateIdentities(hostIdentities, new HashMap<String, Map<String, Object>>() {
            {
                put("identity1", new HashMap<String, Object>() {
                    {
                        put("principal_name", "service1/host2@EXAMPLE.COM");
                        put("principal_type", SERVICE);
                        put("principal_configuration", "service1-site/component1.kerberos.principal");
                        put("principal_local_username", "service1");
                        put("keytab_file", "${keytab_dir}/service1.keytab");
                        put("keytab_owner_name", "service1");
                        put("keytab_owner_access", "rw");
                        put("keytab_group_name", "hadoop");
                        put("keytab_group_access", "");
                        put("keytab_configuration", "service1-site/component1.keytab.file");
                        put("keytab_cachable", false);
                    }
                });
                put("identity3", new HashMap<String, Object>() {
                    {
                        put("principal_name", "service1/host2@EXAMPLE.COM");
                        put("principal_type", SERVICE);
                        put("principal_configuration", "service1-site/service1.kerberos.principal");
                        put("principal_local_username", "service1");
                        put("keytab_file", "${keytab_dir}/service1.service.keytab");
                        put("keytab_owner_name", "service1");
                        put("keytab_owner_access", "rw");
                        put("keytab_group_name", "hadoop");
                        put("keytab_group_access", "");
                        put("keytab_configuration", "service1-site/service1.keytab.file");
                        put("keytab_cachable", false);
                    }
                });
            }
        });
    }

    @Test
    public void testGetActiveIdentities_SingleComponent() throws Exception {
        Map<String, Collection<KerberosIdentityDescriptor>> identities = testGetActiveIdentities("c1", null, null, "COMPONENT2", true, KERBEROS);
        Assert.assertNotNull(identities);
        Assert.assertEquals(3, identities.size());
        Collection<KerberosIdentityDescriptor> hostIdentities;
        hostIdentities = identities.get("host1");
        Assert.assertNotNull(hostIdentities);
        Assert.assertEquals(1, hostIdentities.size());
        validateIdentities(hostIdentities, new HashMap<String, Map<String, Object>>() {
            {
                put("identity2", new HashMap<String, Object>() {
                    {
                        put("principal_name", "component2/host1@EXAMPLE.COM");
                        put("principal_type", SERVICE);
                        put("principal_configuration", "service2-site/component2.kerberos.principal");
                        put("principal_local_username", "service2");
                        put("keytab_file", "${keytab_dir}/service2.keytab");
                        put("keytab_owner_name", "service2");
                        put("keytab_owner_access", "rw");
                        put("keytab_group_name", "hadoop");
                        put("keytab_group_access", "");
                        put("keytab_configuration", "service2-site/component2.keytab.file");
                        put("keytab_cachable", false);
                    }
                });
            }
        });
        hostIdentities = identities.get("host2");
        Assert.assertNotNull(hostIdentities);
        Assert.assertEquals(1, hostIdentities.size());
        validateIdentities(hostIdentities, new HashMap<String, Map<String, Object>>() {
            {
                put("identity2", new HashMap<String, Object>() {
                    {
                        put("principal_name", "component2/host2@EXAMPLE.COM");
                        put("principal_type", SERVICE);
                        put("principal_configuration", "service2-site/component2.kerberos.principal");
                        put("principal_local_username", "service2");
                        put("keytab_file", "${keytab_dir}/service2.keytab");
                        put("keytab_owner_name", "service2");
                        put("keytab_owner_access", "rw");
                        put("keytab_group_name", "hadoop");
                        put("keytab_group_access", "");
                        put("keytab_configuration", "service2-site/component2.keytab.file");
                        put("keytab_cachable", false);
                    }
                });
            }
        });
    }

    @Test
    public void testIsClusterKerberosEnabled_false() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        Cluster cluster = createStrictMock(Cluster.class);
        expect(cluster.getSecurityType()).andReturn(NONE);
        replay(cluster);
        Assert.assertFalse(kerberosHelper.isClusterKerberosEnabled(cluster));
        verify(cluster);
    }

    @Test
    public void testIsClusterKerberosEnabled_true() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        Cluster cluster = createStrictMock(Cluster.class);
        expect(cluster.getSecurityType()).andReturn(KERBEROS);
        replay(cluster);
        Assert.assertTrue(kerberosHelper.isClusterKerberosEnabled(cluster));
        verify(cluster);
    }

    @Test
    public void testGetManageIdentitiesDirective_NotSet() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        Assert.assertEquals(null, kerberosHelper.getManageIdentitiesDirective(null));
        Assert.assertEquals(null, kerberosHelper.getManageIdentitiesDirective(Collections.emptyMap()));
        Assert.assertEquals(null, kerberosHelper.getManageIdentitiesDirective(new HashMap<String, String>() {
            {
                put(DIRECTIVE_MANAGE_KERBEROS_IDENTITIES, null);
                put("some_directive_0", "false");
                put("some_directive_1", null);
            }
        }));
        Assert.assertEquals(null, kerberosHelper.getManageIdentitiesDirective(new HashMap<String, String>() {
            {
                put("some_directive_0", "false");
                put("some_directive_1", null);
            }
        }));
    }

    @Test
    public void testGetManageIdentitiesDirective_True() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        Assert.assertEquals(Boolean.TRUE, kerberosHelper.getManageIdentitiesDirective(Collections.singletonMap(DIRECTIVE_MANAGE_KERBEROS_IDENTITIES, "true")));
        Assert.assertEquals(Boolean.TRUE, kerberosHelper.getManageIdentitiesDirective(Collections.singletonMap(DIRECTIVE_MANAGE_KERBEROS_IDENTITIES, "not_false")));
        Assert.assertEquals(Boolean.TRUE, kerberosHelper.getManageIdentitiesDirective(new HashMap<String, String>() {
            {
                put(DIRECTIVE_MANAGE_KERBEROS_IDENTITIES, "true");
                put("some_directive_0", "false");
                put("some_directive_1", null);
            }
        }));
    }

    @Test
    public void testGetManageIdentitiesDirective_False() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        Assert.assertEquals(Boolean.FALSE, kerberosHelper.getManageIdentitiesDirective(Collections.singletonMap(DIRECTIVE_MANAGE_KERBEROS_IDENTITIES, "false")));
        Assert.assertEquals(Boolean.FALSE, kerberosHelper.getManageIdentitiesDirective(new HashMap<String, String>() {
            {
                put(DIRECTIVE_MANAGE_KERBEROS_IDENTITIES, "false");
                put("some_directive_0", "false");
                put("some_directive_1", null);
            }
        }));
    }

    @Test
    public void testGetForceToggleKerberosDirective_NotSet() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        Assert.assertEquals(false, kerberosHelper.getForceToggleKerberosDirective(null));
        Assert.assertEquals(false, kerberosHelper.getForceToggleKerberosDirective(Collections.emptyMap()));
        Assert.assertEquals(false, kerberosHelper.getForceToggleKerberosDirective(new HashMap<String, String>() {
            {
                put(DIRECTIVE_FORCE_TOGGLE_KERBEROS, null);
                put("some_directive_0", "false");
                put("some_directive_1", null);
            }
        }));
        Assert.assertEquals(false, kerberosHelper.getForceToggleKerberosDirective(new HashMap<String, String>() {
            {
                put("some_directive_0", "false");
                put("some_directive_1", null);
            }
        }));
    }

    @Test
    public void testGetForceToggleKerberosDirective_True() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        Assert.assertEquals(true, kerberosHelper.getForceToggleKerberosDirective(Collections.singletonMap(DIRECTIVE_FORCE_TOGGLE_KERBEROS, "true")));
        Assert.assertEquals(false, kerberosHelper.getForceToggleKerberosDirective(Collections.singletonMap(DIRECTIVE_FORCE_TOGGLE_KERBEROS, "not_true")));
        Assert.assertEquals(true, kerberosHelper.getForceToggleKerberosDirective(new HashMap<String, String>() {
            {
                put(DIRECTIVE_FORCE_TOGGLE_KERBEROS, "true");
                put("some_directive_0", "false");
                put("some_directive_1", null);
            }
        }));
    }

    @Test
    public void testGetForceToggleKerberosDirective_False() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        Assert.assertEquals(false, kerberosHelper.getForceToggleKerberosDirective(Collections.singletonMap(DIRECTIVE_FORCE_TOGGLE_KERBEROS, "false")));
        Assert.assertEquals(false, kerberosHelper.getForceToggleKerberosDirective(new HashMap<String, String>() {
            {
                put(DIRECTIVE_FORCE_TOGGLE_KERBEROS, "false");
                put("some_directive_0", "false");
                put("some_directive_1", null);
            }
        }));
    }

    @Test
    public void testSetAuthToLocalRules() throws Exception {
        testSetAuthToLocalRules(false);
    }

    @Test
    public void testSetAuthToLocalRulesWithPreconfiguredServices() throws Exception {
        testSetAuthToLocalRules(true);
    }

    @Test
    public void testSettingAuthToLocalRulesForUninstalledServiceComponents() throws Exception {
        final KerberosPrincipalDescriptor principalDescriptor1 = createMock(KerberosPrincipalDescriptor.class);
        expect(principalDescriptor1.getValue()).andReturn("principal1/host1@EXAMPLE.COM").times(2);
        expect(principalDescriptor1.getLocalUsername()).andReturn("principal1_user").times(2);
        final KerberosPrincipalDescriptor principalDescriptor2 = createMock(KerberosPrincipalDescriptor.class);
        expect(principalDescriptor2.getValue()).andReturn("principal2/host2@EXAMPLE.COM").times(1);
        expect(principalDescriptor2.getLocalUsername()).andReturn("principal2_user").times(1);
        final KerberosIdentityDescriptor identityDescriptor1 = createMock(KerberosIdentityDescriptor.class);
        expect(identityDescriptor1.getPrincipalDescriptor()).andReturn(principalDescriptor1).times(2);
        expect(identityDescriptor1.shouldInclude(EasyMock.anyObject())).andReturn(true).anyTimes();
        final KerberosIdentityDescriptor identityDescriptor2 = createMock(KerberosIdentityDescriptor.class);
        expect(identityDescriptor2.getPrincipalDescriptor()).andReturn(principalDescriptor2).times(1);
        expect(identityDescriptor2.shouldInclude(EasyMock.anyObject())).andReturn(true).anyTimes();
        final KerberosComponentDescriptor componentDescriptor1 = createMockComponentDescriptor("COMPONENT1", Collections.singletonList(identityDescriptor1), null);// only this is installed

        final KerberosComponentDescriptor componentDescriptor2 = createMockComponentDescriptor("COMPONENT2", Collections.singletonList(identityDescriptor2), null);
        final KerberosServiceDescriptor serviceDescriptor1 = createMock(KerberosServiceDescriptor.class);
        expect(serviceDescriptor1.getName()).andReturn("SERVICE1").anyTimes();
        expect(serviceDescriptor1.getIdentities(eq(true), EasyMock.anyObject())).andReturn(Arrays.asList(identityDescriptor1)).times(1);
        final Map<String, KerberosComponentDescriptor> kerberosComponents = new HashMap<>();
        kerberosComponents.put("COMPONENT1", componentDescriptor1);
        kerberosComponents.put("COMPONENT2", componentDescriptor2);
        expect(serviceDescriptor1.getComponents()).andReturn(kerberosComponents).times(1);
        expect(serviceDescriptor1.getAuthToLocalProperties()).andReturn(new HashSet(Arrays.asList("default", "explicit_multiple_lines|new_lines", "explicit_multiple_lines_escaped|new_lines_escaped", "explicit_single_line|spaces", "service-site/default", "service-site/explicit_multiple_lines|new_lines", "service-site/explicit_multiple_lines_escaped|new_lines_escaped", "service-site/explicit_single_line|spaces"))).times(1);
        final Map<String, KerberosServiceDescriptor> serviceDescriptorMap = new HashMap<>();
        serviceDescriptorMap.put("SERVICE1", serviceDescriptor1);
        final Service service1 = createMockService("SERVICE1", new HashMap<>());
        final Map<String, Service> serviceMap = new HashMap<>();
        serviceMap.put("SERVICE1", service1);
        final Map<String, String> serviceSiteProperties = new HashMap<>();
        serviceSiteProperties.put("default", "RULE:[1:$1@$0](service_site@EXAMPLE.COM)s/.*/service_user/\nDEFAULT");
        serviceSiteProperties.put("explicit_multiple_lines", "RULE:[1:$1@$0](service_site@EXAMPLE.COM)s/.*/service_user/\nDEFAULT");
        serviceSiteProperties.put("explicit_multiple_lines_escaped", "RULE:[1:$1@$0](service_site@EXAMPLE.COM)s/.*/service_user/\\\nDEFAULT");
        serviceSiteProperties.put("explicit_single_line", "RULE:[1:$1@$0](service_site@EXAMPLE.COM)s/.*/service_user/ DEFAULT");
        final Map<String, Map<String, String>> existingConfigs = new HashMap<>();
        existingConfigs.put("kerberos-env", new HashMap<String, String>());
        existingConfigs.get("kerberos-env").put(INCLUDE_ALL_COMPONENTS_IN_AUTH_TO_LOCAL_RULES, "true");
        existingConfigs.put("service-site", serviceSiteProperties);
        final KerberosDescriptor kerberosDescriptor = createMock(KerberosDescriptor.class);
        expect(kerberosDescriptor.getProperty("additional_realms")).andReturn(null).times(1);
        expect(kerberosDescriptor.getIdentities(eq(true), EasyMock.anyObject())).andReturn(null).times(1);
        expect(kerberosDescriptor.getAuthToLocalProperties()).andReturn(null).times(1);
        expect(kerberosDescriptor.getServices()).andReturn(serviceDescriptorMap).times(1);
        final Cluster cluster = createMockCluster("c1", Collections.<Host>emptyList(), KERBEROS, null, null);
        final Map<String, Set<String>> installedServices = Collections.singletonMap("SERVICE1", Collections.singleton("COMPONENT1"));
        final Map<String, Map<String, String>> kerberosConfigurations = new HashMap<>();
        replayAll();
        // Needed by infrastructure
        KerberosHelperTest.injector.getInstance(AmbariMetaInfo.class).init();
        KerberosHelperTest.injector.getInstance(KerberosHelper.class).setAuthToLocalRules(cluster, kerberosDescriptor, "EXAMPLE.COM", installedServices, existingConfigs, kerberosConfigurations, false);
        verifyAll();
        Map<String, String> configs = kerberosConfigurations.get("");
        Assert.assertNotNull(configs);
        // asserts that the rules contain COMPONENT2 related rules too (with principal2) even if COMPONENT2 is not installed (see installedServices declaration above)
        Assert.assertEquals(("RULE:[1:$1@$0](.*@EXAMPLE.COM)s/@.*//\n" + (("RULE:[2:$1@$0](principal1@EXAMPLE.COM)s/.*/principal1_user/\n" + "RULE:[2:$1@$0](principal2@EXAMPLE.COM)s/.*/principal2_user/\n") + "DEFAULT")), configs.get("default"));
        Assert.assertEquals(("RULE:[1:$1@$0](.*@EXAMPLE.COM)s/@.*//\n" + (("RULE:[2:$1@$0](principal1@EXAMPLE.COM)s/.*/principal1_user/\n" + "RULE:[2:$1@$0](principal2@EXAMPLE.COM)s/.*/principal2_user/\n") + "DEFAULT")), configs.get("explicit_multiple_lines"));
        Assert.assertEquals(("RULE:[1:$1@$0](.*@EXAMPLE.COM)s/@.*//\\\n" + (("RULE:[2:$1@$0](principal1@EXAMPLE.COM)s/.*/principal1_user/\\\n" + "RULE:[2:$1@$0](principal2@EXAMPLE.COM)s/.*/principal2_user/\\\n") + "DEFAULT")), configs.get("explicit_multiple_lines_escaped"));
        Assert.assertEquals(("RULE:[1:$1@$0](.*@EXAMPLE.COM)s/@.*// " + (("RULE:[2:$1@$0](principal1@EXAMPLE.COM)s/.*/principal1_user/ " + "RULE:[2:$1@$0](principal2@EXAMPLE.COM)s/.*/principal2_user/ ") + "DEFAULT")), configs.get("explicit_single_line"));
        configs = kerberosConfigurations.get("service-site");
        Assert.assertNotNull(configs);
        Assert.assertEquals(("RULE:[1:$1@$0](service_site@EXAMPLE.COM)s/.*/service_user/\n" + ((("RULE:[1:$1@$0](.*@EXAMPLE.COM)s/@.*//\n" + "RULE:[2:$1@$0](principal1@EXAMPLE.COM)s/.*/principal1_user/\n") + "RULE:[2:$1@$0](principal2@EXAMPLE.COM)s/.*/principal2_user/\n") + "DEFAULT")), configs.get("default"));
        Assert.assertEquals(("RULE:[1:$1@$0](service_site@EXAMPLE.COM)s/.*/service_user/\n" + ((("RULE:[1:$1@$0](.*@EXAMPLE.COM)s/@.*//\n" + "RULE:[2:$1@$0](principal1@EXAMPLE.COM)s/.*/principal1_user/\n") + "RULE:[2:$1@$0](principal2@EXAMPLE.COM)s/.*/principal2_user/\n") + "DEFAULT")), configs.get("explicit_multiple_lines"));
        Assert.assertEquals(("RULE:[1:$1@$0](service_site@EXAMPLE.COM)s/.*/service_user/\\\n" + ((("RULE:[1:$1@$0](.*@EXAMPLE.COM)s/@.*//\\\n" + "RULE:[2:$1@$0](principal1@EXAMPLE.COM)s/.*/principal1_user/\\\n") + "RULE:[2:$1@$0](principal2@EXAMPLE.COM)s/.*/principal2_user/\\\n") + "DEFAULT")), configs.get("explicit_multiple_lines_escaped"));
        Assert.assertEquals(("RULE:[1:$1@$0](service_site@EXAMPLE.COM)s/.*/service_user/ " + ((("RULE:[1:$1@$0](.*@EXAMPLE.COM)s/@.*// " + "RULE:[2:$1@$0](principal1@EXAMPLE.COM)s/.*/principal1_user/ ") + "RULE:[2:$1@$0](principal2@EXAMPLE.COM)s/.*/principal2_user/ ") + "DEFAULT")), configs.get("explicit_single_line"));
    }

    @Test
    public void testMergeConfigurationsForPreconfiguring() throws Exception {
        Service existingService = createMockService("EXISTING_SERVICE", null);
        Set<String> serviceNames = new HashSet<>();
        serviceNames.add("EXISTING_SERVICE");
        serviceNames.add("PRECONFIGURE_SERVICE");
        Map<String, Set<String>> hostMap = new HashMap<>();
        Map<String, Service> services = new HashMap<>();
        Cluster cluster = createMockCluster("c1", Collections.<Host>emptyList(), KERBEROS, null, null);
        expect(cluster.getServices()).andReturn(services).times(2);
        expect(cluster.getServiceComponentHostMap(null, serviceNames)).andReturn(hostMap).once();
        KerberosDescriptor kerberosDescriptor = createKerberosDescriptor();
        ComponentInfo preconfigureComponentInfo = createMock(ComponentInfo.class);
        expect(preconfigureComponentInfo.getName()).andReturn("PRECONFIGURE_SERVICE_MASTER").once();
        List<PropertyInfo> preconfigureServiceProperties = Collections.singletonList(createMockPropertyInfo("preconfigure-service-env.xml", "service_user", "preconfigure_user"));
        ServiceInfo preconfigureServiceInfo = createMock(ServiceInfo.class);
        expect(preconfigureServiceInfo.getProperties()).andReturn(preconfigureServiceProperties).anyTimes();
        expect(preconfigureServiceInfo.getComponents()).andReturn(Collections.singletonList(preconfigureComponentInfo)).anyTimes();
        AmbariMetaInfo ambariMetaInfo = KerberosHelperTest.injector.getInstance(AmbariMetaInfo.class);
        expect(ambariMetaInfo.isValidService("HDP", "2.2", "PRECONFIGURE_SERVICE")).andReturn(true).anyTimes();
        expect(ambariMetaInfo.getService("HDP", "2.2", "PRECONFIGURE_SERVICE")).andReturn(preconfigureServiceInfo).anyTimes();
        Set<Map<String, String>> host1Components = new HashSet<>();
        host1Components.add(Collections.singletonMap("name", "EXISTING_SERVICE_MASTER"));
        host1Components.add(Collections.singletonMap("name", "PRECONFIGURE_SERVICE_MASTER"));
        Set<Map<String, String>> host2Components = new HashSet<>();
        host2Components.add(Collections.singletonMap("name", "EXISTING_SERVICE_MASTER"));
        RecommendationResponse.HostGroup hostGroup1 = createMock(HostGroup.class);
        expect(hostGroup1.getName()).andReturn("host1").once();
        expect(hostGroup1.getComponents()).andReturn(host1Components).once();
        RecommendationResponse.HostGroup hostGroup2 = createMock(HostGroup.class);
        expect(hostGroup2.getName()).andReturn("host2").once();
        expect(hostGroup2.getComponents()).andReturn(host2Components).once();
        Set<RecommendationResponse.HostGroup> hostGroups = new HashSet<>();
        hostGroups.add(hostGroup1);
        hostGroups.add(hostGroup2);
        RecommendationResponse.Blueprint blueprint = createMock(Blueprint.class);
        expect(blueprint.getHostGroups()).andReturn(hostGroups).once();
        RecommendationResponse.BindingHostGroup bindHostGroup1 = createMock(BindingHostGroup.class);
        expect(bindHostGroup1.getName()).andReturn("host1").once();
        expect(bindHostGroup1.getHosts()).andReturn(Collections.singleton(Collections.singletonMap("fqdn", "host1"))).once();
        RecommendationResponse.BindingHostGroup bindHostGroup2 = createMock(BindingHostGroup.class);
        expect(bindHostGroup2.getName()).andReturn("host2").once();
        expect(bindHostGroup2.getHosts()).andReturn(Collections.singleton(Collections.singletonMap("fqdn", "host2"))).once();
        Set<RecommendationResponse.BindingHostGroup> bindingHostGroups = new HashSet<>();
        bindingHostGroups.add(bindHostGroup1);
        bindingHostGroups.add(bindHostGroup2);
        RecommendationResponse.BlueprintClusterBinding binding = createMock(BlueprintClusterBinding.class);
        expect(binding.getHostGroups()).andReturn(bindingHostGroups).once();
        RecommendationResponse.Recommendation recommendation = createMock(Recommendation.class);
        expect(recommendation.getBlueprint()).andReturn(blueprint).once();
        expect(recommendation.getBlueprintClusterBinding()).andReturn(binding).once();
        RecommendationResponse response = createMock(RecommendationResponse.class);
        expect(response.getRecommendations()).andReturn(recommendation).once();
        StackAdvisorHelper stackAdvisorHelper = KerberosHelperTest.injector.getInstance(StackAdvisorHelper.class);
        expect(stackAdvisorHelper.recommend(anyObject(StackAdvisorRequest.class))).andReturn(response).once();
        replayAll();
        services.put(existingService.getName(), existingService);
        Map<String, Map<String, String>> existingConfigurations = new HashMap<>();
        existingConfigurations.put("core-site", new HashMap<>(Collections.singletonMap("core-property1", "original_value")));
        existingConfigurations.put("hadoop-env", new HashMap<>(Collections.singletonMap("proxyuser_group", "hadoop")));
        Map<String, Map<String, String>> replacements = new HashMap<>(existingConfigurations);
        // Needed by infrastructure
        KerberosHelperTest.injector.getInstance(AmbariMetaInfo.class).init();
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        Map<String, Map<String, String>> configurations = kerberosHelper.processPreconfiguredServiceConfigurations(existingConfigurations, replacements, cluster, kerberosDescriptor);
        verifyAll();
        Assert.assertNotNull(configurations);
        Assert.assertEquals(2, configurations.size());
        Assert.assertNotNull(configurations.get("core-site"));
        Assert.assertNotNull(configurations.get("hadoop-env"));
        Assert.assertEquals("hadoop", configurations.get("core-site").get("hadoop.proxyuser.preconfigure_user.groups"));
        Assert.assertEquals("host1", configurations.get("core-site").get("hadoop.proxyuser.preconfigure_user.hosts"));
    }

    @Test
    public void testGetServiceConfigurationUpdates() throws Exception {
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        final Host hostA = createMockHost("hostA");
        final Host hostB = createMockHost("hostB");
        final Host hostC = createMockHost("hostC");
        Collection<Host> hosts = Arrays.asList(hostA, hostB, hostC);
        final Map<String, String> kerberosEnvProperties = new HashMap<String, String>() {
            {
                put(KDC_TYPE, "mit-kdc");
                put(DEFAULT_REALM, "FOOBAR.COM");
                put("case_insensitive_username_rules", "false");
                put(CREATE_AMBARI_PRINCIPAL, "false");
            }
        };
        final Config kerberosEnvConfig = createMock(Config.class);
        expect(kerberosEnvConfig.getProperties()).andReturn(kerberosEnvProperties).atLeastOnce();
        final Map<String, String> krb5ConfProperties = createMock(Map.class);
        final Config krb5ConfConfig = createMock(Config.class);
        expect(krb5ConfConfig.getProperties()).andReturn(krb5ConfProperties).atLeastOnce();
        final KerberosPrincipalDescriptor principalDescriptor1 = createMockPrincipalDescriptor("service1/_HOST@${realm}", SERVICE, "service1user", "service1-site/service.kerberos.principal");
        final KerberosPrincipalDescriptor principalDescriptor1a = createMockPrincipalDescriptor("component1a/_HOST@${realm}", SERVICE, "service1user", "service1-site/component1a.kerberos.principal");
        final KerberosPrincipalDescriptor principalDescriptor1b = createMockPrincipalDescriptor("component1b/_HOST@${realm}", SERVICE, "service1user", "service1-site/component1b.kerberos.principal");
        final KerberosPrincipalDescriptor principalDescriptor2a = createMockPrincipalDescriptor("component2a/_HOST@${realm}", SERVICE, "service2user", "service2-site/component2a.kerberos.principal");
        final KerberosPrincipalDescriptor principalDescriptor2b = createMockPrincipalDescriptor("component2b/_HOST@${realm}", SERVICE, "service2user", "service2-site/component2b.kerberos.principal");
        final KerberosPrincipalDescriptor principalDescriptor3a = createMockPrincipalDescriptor("component3a/_HOST@${realm}", SERVICE, "service3user", "service3-site/component3a.kerberos.principal");
        final KerberosKeytabDescriptor keytabDescriptor1 = createMockKeytabDescriptor("keytab1", "service1-site/service.kerberos.keytab");
        final KerberosKeytabDescriptor keytabDescriptor1a = createMockKeytabDescriptor("keytab1a", "service1-site/component1a.kerberos.keytab");
        final KerberosKeytabDescriptor keytabDescriptor1b = createMockKeytabDescriptor("keytab1b", "service1-site/component1b.kerberos.keytab");
        final KerberosKeytabDescriptor keytabDescriptor2a = createMockKeytabDescriptor("keytab2a", "service2-site/component2a.kerberos.keytab");
        final KerberosKeytabDescriptor keytabDescriptor2b = createMockKeytabDescriptor("keytab2b", "service2-site/component2b.kerberos.keytab");
        final KerberosKeytabDescriptor keytabDescriptor3a = createMockKeytabDescriptor("keytab3a", "service3-site/component3a.kerberos.keytab");
        final KerberosIdentityDescriptor identityDescriptor1 = createMockIdentityDescriptor("identity1", principalDescriptor1, keytabDescriptor1);
        final KerberosIdentityDescriptor identityDescriptor1a = createMockIdentityDescriptor("identity1a", principalDescriptor1a, keytabDescriptor1a);
        final KerberosIdentityDescriptor identityDescriptor1b = createMockIdentityDescriptor("identity1b", principalDescriptor1b, keytabDescriptor1b);
        final KerberosIdentityDescriptor identityDescriptor2a = createMockIdentityDescriptor("identity2a", principalDescriptor2a, keytabDescriptor2a);
        final KerberosIdentityDescriptor identityDescriptor2b = createMockIdentityDescriptor("identity2b", principalDescriptor2b, keytabDescriptor2b);
        final KerberosIdentityDescriptor identityDescriptor3a = createMockIdentityDescriptor("identity3a", principalDescriptor3a, keytabDescriptor3a);
        final KerberosComponentDescriptor componentDescriptor1a = createMockComponentDescriptor("COMPONENT1A", new ArrayList<KerberosIdentityDescriptor>() {
            {
                add(identityDescriptor1a);
            }
        }, new HashMap<String, KerberosConfigurationDescriptor>() {
            {
                put("service1-site", createMockConfigurationDescriptor(Collections.singletonMap("component1a.property", "${replacement1}")));
            }
        });
        final KerberosComponentDescriptor componentDescriptor1b = createMockComponentDescriptor("COMPONENT1B", new ArrayList<KerberosIdentityDescriptor>() {
            {
                add(identityDescriptor1b);
            }
        }, new HashMap<String, KerberosConfigurationDescriptor>() {
            {
                put("service1-site", createMockConfigurationDescriptor(Collections.singletonMap("component1b.property", "${type1/replacement1}")));
            }
        });
        final KerberosComponentDescriptor componentDescriptor2a = createMockComponentDescriptor("COMPONENT2A", new ArrayList<KerberosIdentityDescriptor>() {
            {
                add(identityDescriptor2a);
            }
        }, new HashMap<String, KerberosConfigurationDescriptor>() {
            {
                put("service2-site", createMockConfigurationDescriptor(Collections.singletonMap("component2a.property", "${type1/replacement2}")));
            }
        });
        final KerberosComponentDescriptor componentDescriptor2b = createMockComponentDescriptor("COMPONENT2B", new ArrayList<KerberosIdentityDescriptor>() {
            {
                add(identityDescriptor2b);
            }
        }, new HashMap<String, KerberosConfigurationDescriptor>() {
            {
                put("service2-site", createMockConfigurationDescriptor(Collections.singletonMap("component2b.property", "${type2/replacement1}")));
            }
        });
        final KerberosComponentDescriptor componentDescriptor3a = createMockComponentDescriptor("COMPONENT3A", new ArrayList<KerberosIdentityDescriptor>() {
            {
                add(identityDescriptor3a);
            }
        }, new HashMap<String, KerberosConfigurationDescriptor>() {
            {
                put("service3-site", createMockConfigurationDescriptor(Collections.singletonMap("component3a.property", "${type3/replacement1}")));
                put("core-site", createMockConfigurationDescriptor(Collections.singletonMap("component3b.property", "${type3/replacement2}")));
            }
        });
        final KerberosServiceDescriptor serviceDescriptor1 = createMockServiceDescriptor("SERVICE1", new HashMap<String, KerberosComponentDescriptor>() {
            {
                put("COMPONENT1A", componentDescriptor1a);
                put("COMPONENT1B", componentDescriptor1b);
            }
        }, new ArrayList<KerberosIdentityDescriptor>() {
            {
                add(identityDescriptor1);
            }
        }, false);
        expect(serviceDescriptor1.getComponent("COMPONENT1A")).andReturn(componentDescriptor1a).times(2);
        expect(serviceDescriptor1.getComponent("COMPONENT1B")).andReturn(componentDescriptor1b).times(2);
        final KerberosServiceDescriptor serviceDescriptor2 = createMockServiceDescriptor("SERVICE2", new HashMap<String, KerberosComponentDescriptor>() {
            {
                put("COMPONENT2A", componentDescriptor2a);
                put("COMPONENT2B", componentDescriptor2b);
            }
        }, Collections.<KerberosIdentityDescriptor>emptyList(), false);
        expect(serviceDescriptor2.getComponent("COMPONENT2A")).andReturn(componentDescriptor2a).times(1);
        expect(serviceDescriptor2.getComponent("COMPONENT2B")).andReturn(componentDescriptor2b).times(1);
        final KerberosServiceDescriptor serviceDescriptor3 = createMockServiceDescriptor("SERVICE3", new HashMap<String, KerberosComponentDescriptor>() {
            {
                put("COMPONENT3A", componentDescriptor3a);
            }
        }, Collections.<KerberosIdentityDescriptor>emptyList(), false);
        expect(serviceDescriptor3.getComponent("COMPONENT3A")).andReturn(componentDescriptor3a).times(2);
        Map<String, KerberosServiceDescriptor> serviceDescriptorMap = new HashMap<>();
        serviceDescriptorMap.put("SERVICE1", serviceDescriptor1);
        serviceDescriptorMap.put("SERVICE2", serviceDescriptor2);
        serviceDescriptorMap.put("SERVICE3", serviceDescriptor3);
        final Map<String, String> kerberosDescriptorProperties = new HashMap<>();
        kerberosDescriptorProperties.put(DEFAULT_REALM, "${kerberos-env/realm}");
        final KerberosDescriptor kerberosDescriptor = createMock(KerberosDescriptor.class);
        expect(kerberosDescriptor.getProperties()).andReturn(kerberosDescriptorProperties).atLeastOnce();
        expect(kerberosDescriptor.getService("SERVICE1")).andReturn(serviceDescriptor1).atLeastOnce();
        expect(kerberosDescriptor.getService("SERVICE2")).andReturn(serviceDescriptor2).atLeastOnce();
        expect(kerberosDescriptor.getService("SERVICE3")).andReturn(serviceDescriptor3).atLeastOnce();
        expect(kerberosDescriptor.getServices()).andReturn(serviceDescriptorMap).atLeastOnce();
        expect(kerberosDescriptor.getProperty("additional_realms")).andReturn(null).atLeastOnce();
        expect(kerberosDescriptor.getIdentities(eq(true), EasyMock.anyObject())).andReturn(null).atLeastOnce();
        expect(kerberosDescriptor.getAuthToLocalProperties()).andReturn(Collections.singleton("core-site/auth.to.local")).atLeastOnce();
        setupKerberosDescriptor(kerberosDescriptor);
        RecommendationResponse.BlueprintConfigurations coreSiteRecommendation = createNiceMock(BlueprintConfigurations.class);
        expect(coreSiteRecommendation.getProperties()).andReturn(Collections.singletonMap("newPropertyRecommendation", "newPropertyRecommendation"));
        RecommendationResponse.BlueprintConfigurations newTypeRecommendation = createNiceMock(BlueprintConfigurations.class);
        expect(newTypeRecommendation.getProperties()).andReturn(Collections.singletonMap("newTypeRecommendation", "newTypeRecommendation"));
        RecommendationResponse.BlueprintConfigurations type1Recommendation = createNiceMock(BlueprintConfigurations.class);
        expect(type1Recommendation.getProperties()).andReturn(Collections.singletonMap("replacement1", "not replaced"));
        RecommendationResponse.BlueprintConfigurations service1SiteRecommendation = createNiceMock(BlueprintConfigurations.class);
        expect(service1SiteRecommendation.getProperties()).andReturn(Collections.singletonMap("component1b.property", "replaced value"));
        Map<String, RecommendationResponse.BlueprintConfigurations> configurations = new HashMap<>();
        configurations.put("core-site", coreSiteRecommendation);
        configurations.put("new-type", newTypeRecommendation);
        configurations.put("type1", type1Recommendation);
        configurations.put("service1-site", service1SiteRecommendation);
        RecommendationResponse.Blueprint blueprint = createMock(Blueprint.class);
        expect(blueprint.getConfigurations()).andReturn(configurations).once();
        RecommendationResponse.Recommendation recommendations = createMock(Recommendation.class);
        expect(recommendations.getBlueprint()).andReturn(blueprint).once();
        RecommendationResponse recommendationResponse = createMock(RecommendationResponse.class);
        expect(recommendationResponse.getRecommendations()).andReturn(recommendations).once();
        StackAdvisorHelper stackAdvisorHelper = KerberosHelperTest.injector.getInstance(StackAdvisorHelper.class);
        expect(stackAdvisorHelper.recommend(anyObject(StackAdvisorRequest.class))).andReturn(null).once();
        expect(stackAdvisorHelper.recommend(anyObject(StackAdvisorRequest.class))).andReturn(recommendationResponse).once();
        final Service service1 = createMockService("SERVICE1", new HashMap<String, ServiceComponent>() {
            {
                put("COMPONENT1A", createMockComponent("COMPONENT1A", true, new HashMap<String, ServiceComponentHost>() {
                    {
                        put("hostA", createMockServiceComponentHost(INSTALLED));
                    }
                }));
                put("COMPONENT1B", createMockComponent("COMPONENT1B", false, new HashMap<String, ServiceComponentHost>() {
                    {
                        put("hostB", createMockServiceComponentHost(INSTALLED));
                        put("hostC", createMockServiceComponentHost(INSTALLED));
                    }
                }));
            }
        });
        final Service service2 = createMockService("SERVICE2", new HashMap<String, ServiceComponent>() {
            {
                put("COMPONENT2A", createMockComponent("COMPONENT2A", true, new HashMap<String, ServiceComponentHost>() {
                    {
                        put("hostA", createMockServiceComponentHost(INSTALLED));
                    }
                }));
                put("COMPONENT2B", createMockComponent("COMPONENT2B", false, new HashMap<String, ServiceComponentHost>() {
                    {
                        put("hostB", createMockServiceComponentHost(INSTALLED));
                        put("hostC", createMockServiceComponentHost(INSTALLED));
                    }
                }));
            }
        });
        final Service service3 = createMockService("SERVICE3", new HashMap<String, ServiceComponent>() {
            {
                put("COMPONENT3A", createMockComponent("COMPONENT3A", true, new HashMap<String, ServiceComponentHost>() {
                    {
                        put("hostA", createMockServiceComponentHost(INSTALLED));
                    }
                }));
            }
        });
        Map<String, Service> services = new HashMap<>();
        services.put("SERVICE1", service1);
        services.put("SERVICE2", service2);
        services.put("SERVICE3", service3);
        Map<String, Set<String>> serviceComponentHostMap = new HashMap<>();
        serviceComponentHostMap.put("COMPONENT1A", new TreeSet<>(Arrays.asList("hostA")));
        serviceComponentHostMap.put("COMPONENT1B", new TreeSet<>(Arrays.asList("hostB", "hostC")));
        serviceComponentHostMap.put("COMPONENT2A", new TreeSet<>(Arrays.asList("hostA")));
        serviceComponentHostMap.put("COMPONENT2B", new TreeSet<>(Arrays.asList("hostB", "hostC")));
        serviceComponentHostMap.put("COMPONEN3A", new TreeSet<>(Arrays.asList("hostA")));
        final Cluster cluster = createMockCluster("c1", hosts, KERBEROS, krb5ConfConfig, kerberosEnvConfig);
        expect(cluster.getServices()).andReturn(services).anyTimes();
        expect(cluster.getServiceComponentHostMap(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(serviceComponentHostMap).anyTimes();
        final Map<String, Map<String, String>> existingConfigurations = new HashMap<String, Map<String, String>>() {
            {
                put("kerberos-env", kerberosEnvProperties);
                put("", new HashMap<String, String>() {
                    {
                        put("replacement1", "value1");
                    }
                });
                put("type1", new HashMap<String, String>() {
                    {
                        put("replacement1", "value2");
                        put("replacement2", "value3");
                    }
                });
                put("type2", new HashMap<String, String>() {
                    {
                        put("replacement1", "value4");
                        put("replacement2", "value5");
                    }
                });
                put("type3", new HashMap<String, String>() {
                    {
                        put("replacement1", "value6");
                        put("replacement2", "value7");
                    }
                });
            }
        };
        replayAll();
        // Needed by infrastructure
        KerberosHelperTest.injector.getInstance(AmbariMetaInfo.class).init();
        HashMap<String, Set<String>> installedServices1 = new HashMap<>();
        installedServices1.put("SERVICE1", new HashSet<>(Arrays.asList("COMPONENT1A", "COMPONENT1B")));
        installedServices1.put("SERVICE2", new HashSet<>(Arrays.asList("COMPONENT2A", "COMPONENT2B")));
        installedServices1.put("SERVICE3", Collections.singleton("COMPONENT3A"));
        Map<String, Map<String, String>> updates1 = kerberosHelper.getServiceConfigurationUpdates(cluster, existingConfigurations, installedServices1, null, null, true, true);
        HashMap<String, Set<String>> installedServices2 = new HashMap<>();
        installedServices2.put("SERVICE1", new HashSet<>(Arrays.asList("COMPONENT1A", "COMPONENT1B")));
        installedServices2.put("SERVICE3", Collections.singleton("COMPONENT3A"));
        Map<String, Collection<String>> serviceFilter2 = new HashMap<>();
        serviceFilter2.put("SERVICE1", new HashSet<>(Arrays.asList("COMPONENT1A", "COMPONENT1B")));
        serviceFilter2.put("SERVICE3", Collections.singleton("COMPONENT3A"));
        Map<String, Map<String, String>> updates2 = kerberosHelper.getServiceConfigurationUpdates(cluster, existingConfigurations, installedServices2, serviceFilter2, null, true, true);
        verifyAll();
        Map<String, Map<String, String>> expectedUpdates = new HashMap<String, Map<String, String>>() {
            {
                put("service1-site", new HashMap<String, String>() {
                    {
                        put("service.kerberos.principal", "service1/_HOST@FOOBAR.COM");
                        put("service.kerberos.keytab", "keytab1");
                        put("component1a.kerberos.principal", "component1a/_HOST@FOOBAR.COM");
                        put("component1a.kerberos.keytab", "keytab1a");
                        put("component1a.property", "value1");
                        put("component1b.kerberos.principal", "component1b/_HOST@FOOBAR.COM");
                        put("component1b.kerberos.keytab", "keytab1b");
                        put("component1b.property", "value2");
                    }
                });
                put("service2-site", new HashMap<String, String>() {
                    {
                        put("component2a.kerberos.principal", "component2a/_HOST@FOOBAR.COM");
                        put("component2a.kerberos.keytab", "keytab2a");
                        put("component2a.property", "value3");
                        put("component2b.kerberos.principal", "component2b/_HOST@FOOBAR.COM");
                        put("component2b.kerberos.keytab", "keytab2b");
                        put("component2b.property", "value4");
                    }
                });
                put("service3-site", new HashMap<String, String>() {
                    {
                        put("component3a.kerberos.principal", "component3a/_HOST@FOOBAR.COM");
                        put("component3a.kerberos.keytab", "keytab3a");
                        put("component3a.property", "value6");
                    }
                });
                put("core-site", new HashMap<String, String>() {
                    {
                        put("auth.to.local", ("RULE:[1:$1@$0](.*@FOOBAR.COM)s/@.*//\n" + (((((("RULE:[2:$1@$0](component1a@FOOBAR.COM)s/.*/service1user/\n" + "RULE:[2:$1@$0](component1b@FOOBAR.COM)s/.*/service1user/\n") + "RULE:[2:$1@$0](component2a@FOOBAR.COM)s/.*/service2user/\n") + "RULE:[2:$1@$0](component2b@FOOBAR.COM)s/.*/service2user/\n") + "RULE:[2:$1@$0](component3a@FOOBAR.COM)s/.*/service3user/\n") + "RULE:[2:$1@$0](service1@FOOBAR.COM)s/.*/service1user/\n") + "DEFAULT")));
                        put("component3b.property", "value7");
                    }
                });
            }
        };
        Assert.assertEquals(expectedUpdates, updates1);
        expectedUpdates.remove("service2-site");
        expectedUpdates.get("core-site").put("newPropertyRecommendation", "newPropertyRecommendation");
        expectedUpdates.get("core-site").put("auth.to.local", ("RULE:[1:$1@$0](.*@FOOBAR.COM)s/@.*//\n" + (((("RULE:[2:$1@$0](component1a@FOOBAR.COM)s/.*/service1user/\n" + "RULE:[2:$1@$0](component1b@FOOBAR.COM)s/.*/service1user/\n") + "RULE:[2:$1@$0](component3a@FOOBAR.COM)s/.*/service3user/\n") + "RULE:[2:$1@$0](service1@FOOBAR.COM)s/.*/service1user/\n") + "DEFAULT")));
        expectedUpdates.get("service1-site").put("component1b.property", "replaced value");
        expectedUpdates.put("new-type", new HashMap<String, String>() {
            {
                put("newTypeRecommendation", "newTypeRecommendation");
            }
        });
        Assert.assertEquals(expectedUpdates, updates2);
        // Make sure the existing configurations remained unchanged
        Map<String, Map<String, String>> expectedExistingConfigurations = new HashMap<String, Map<String, String>>() {
            {
                put("kerberos-env", new HashMap<String, String>() {
                    {
                        put(KDC_TYPE, "mit-kdc");
                        put(DEFAULT_REALM, "FOOBAR.COM");
                        put("case_insensitive_username_rules", "false");
                        put(CREATE_AMBARI_PRINCIPAL, "false");
                    }
                });
                put("", new HashMap<String, String>() {
                    {
                        put("replacement1", "value1");
                    }
                });
                put("type1", new HashMap<String, String>() {
                    {
                        put("replacement1", "value2");
                        put("replacement2", "value3");
                    }
                });
                put("type2", new HashMap<String, String>() {
                    {
                        put("replacement1", "value4");
                        put("replacement2", "value5");
                    }
                });
                put("type3", new HashMap<String, String>() {
                    {
                        put("replacement1", "value6");
                        put("replacement2", "value7");
                    }
                });
            }
        };
        Assert.assertEquals(expectedExistingConfigurations, existingConfigurations);
    }

    @Test
    public void testEnsureHeadlessIdentities() throws Exception {
        testEnsureHeadlessIdentities(false, false);
    }

    @Test
    public void testEnsureHeadlessAndAmbariIdentitiesAsUser() throws Exception {
        testEnsureHeadlessIdentities(true, false);
    }

    @Test
    public void testEnsureHeadlessAndAmbariIdentitiesAsService() throws Exception {
        testEnsureHeadlessIdentities(true, true);
    }

    /**
     * Test that a Kerberos Descriptor that contains a Service with 0 Components does not raise any exceptions.
     */
    @Test
    public void testServiceWithoutComponents() throws Exception {
        Map<String, String> propertiesKrb5Conf = new HashMap<>();
        Map<String, String> propertiesKerberosEnv = new HashMap<>();
        propertiesKerberosEnv.put(DEFAULT_REALM, "EXAMPLE.COM");
        propertiesKerberosEnv.put(KDC_TYPE, "mit-kdc");
        propertiesKerberosEnv.put(CREATE_AMBARI_PRINCIPAL, "false");
        Config configKrb5Conf = createMock(Config.class);
        expect(configKrb5Conf.getProperties()).andReturn(propertiesKrb5Conf).times(1);
        Config configKerberosEnv = createMock(Config.class);
        expect(configKerberosEnv.getProperties()).andReturn(propertiesKerberosEnv).times(1);
        // Create a Service (SERVICE1) with one Component (COMPONENT11)
        Host host1 = createMockHost("host1");
        Map<String, ServiceComponentHost> service1Component1HostMap = new HashMap<>();
        service1Component1HostMap.put("host1", createMockServiceComponentHost(INSTALLED));
        Map<String, ServiceComponent> service1ComponentMap = new HashMap<>();
        service1ComponentMap.put("COMPONENT11", createMockComponent("COMPONENT11", true, service1Component1HostMap));
        Service service1 = createMockService("SERVICE1", service1ComponentMap);
        Map<String, Service> servicesMap = new HashMap<>();
        servicesMap.put("SERVICE1", service1);
        Cluster cluster = createMockCluster("c1", Collections.singletonList(host1), KERBEROS, configKrb5Conf, configKerberosEnv);
        expect(cluster.getServices()).andReturn(servicesMap).anyTimes();
        Map<String, String> kerberosDescriptorProperties = new HashMap<>();
        kerberosDescriptorProperties.put("additional_realms", "");
        kerberosDescriptorProperties.put("keytab_dir", "/etc/security/keytabs");
        kerberosDescriptorProperties.put(DEFAULT_REALM, "${kerberos-env/realm}");
        // Notice that this map is empty, hence it has 0 Components in the kerberosDescriptor.
        HashMap<String, KerberosComponentDescriptor> service1ComponentDescriptorMap = new HashMap<>();
        List<KerberosIdentityDescriptor> service1Identities = new ArrayList<>();
        KerberosServiceDescriptor service1KerberosDescriptor = createMockServiceDescriptor("SERVICE1", service1ComponentDescriptorMap, service1Identities, false);
        KerberosDescriptor kerberosDescriptor = createMock(KerberosDescriptor.class);
        expect(kerberosDescriptor.getProperties()).andReturn(kerberosDescriptorProperties);
        expect(kerberosDescriptor.getService("SERVICE1")).andReturn(service1KerberosDescriptor).anyTimes();
        setupKerberosDescriptor(kerberosDescriptor);
        Map<String, Map<String, String>> existingConfigurations = new HashMap<>();
        existingConfigurations.put("kerberos-env", propertiesKerberosEnv);
        Set<String> services = new HashSet<String>() {
            {
                add("SERVICE1");
            }
        };
        Capture<? extends String> capturePrincipal = newCapture(CaptureType.ALL);
        Capture<? extends String> capturePrincipalForKeytab = newCapture(CaptureType.ALL);
        replayAll();
        AmbariMetaInfo ambariMetaInfo = KerberosHelperTest.injector.getInstance(AmbariMetaInfo.class);
        ambariMetaInfo.init();
        CredentialStoreService credentialStoreService = KerberosHelperTest.injector.getInstance(CredentialStoreService.class);
        credentialStoreService.setCredential(cluster.getClusterName(), KDC_ADMINISTRATOR_CREDENTIAL_ALIAS, new PrincipalKeyCredential("principal", "password"), TEMPORARY);
        KerberosHelper kerberosHelper = KerberosHelperTest.injector.getInstance(KerberosHelper.class);
        // Validate that it works with 0 Components.
        kerberosHelper.ensureHeadlessIdentities(cluster, existingConfigurations, services);
        verifyAll();
        List<? extends String> capturedPrincipals = capturePrincipal.getValues();
        Assert.assertEquals(0, capturedPrincipals.size());
        List<? extends String> capturedPrincipalsForKeytab = capturePrincipalForKeytab.getValues();
        Assert.assertEquals(0, capturedPrincipalsForKeytab.size());
    }

    @Test
    public void testFiltersParsing() {
        Map<String, String> requestProperties = new HashMap<String, String>() {
            {
                put(KerberosHelper.DIRECTIVE_HOSTS, "host1,host2,host3");
                put(KerberosHelper.DIRECTIVE_COMPONENTS, "SERVICE1:COMPONENT1;COMPONENT2,SERVICE2:COMPONENT1;COMPONENT2;COMPONENT3");
            }
        };
        Set<String> expectedHosts = new HashSet<>(Arrays.asList("host1", "host2", "host3"));
        Set<String> hosts = KerberosHelperImpl.parseHostFilter(requestProperties);
        Assert.assertEquals(expectedHosts, hosts);
        Map<String, Set<String>> expectedComponents = new HashMap<String, Set<String>>() {
            {
                put("SERVICE1", new HashSet<String>() {
                    {
                        add("COMPONENT1");
                        add("COMPONENT2");
                    }
                });
                put("SERVICE2", new HashSet<String>() {
                    {
                        add("COMPONENT1");
                        add("COMPONENT2");
                        add("COMPONENT3");
                    }
                });
            }
        };
        Map<String, Set<String>> components = KerberosHelperImpl.parseComponentFilter(requestProperties);
        Assert.assertEquals(expectedComponents, components);
    }
}

