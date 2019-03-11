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
package org.apache.ambari.server.serveraction.kerberos;


import UpdateConfigurationPolicy.ALL;
import UpdateConfigurationPolicy.IDENTITIES_ONLY;
import UpdateConfigurationPolicy.NEW_AND_IDENTITIES;
import UpdateConfigurationPolicy.NONE;
import com.google.inject.Injector;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import org.apache.ambari.server.agent.CommandReport;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.kerberos.KerberosComponentDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosDescriptorFactory;
import org.apache.ambari.server.state.kerberos.KerberosServiceDescriptor;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;


public class AbstractPrepareKerberosServerActionTest extends EasyMockSupport {
    private static final String KERBEROS_DESCRIPTOR_JSON = "" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("{" + "  \"identities\": [") + "    {") + "      \"keytab\": {") + "        \"file\": \"${keytab_dir}/spnego.service.keytab\",") + "        \"group\": {") + "          \"access\": \"r\",") + "          \"name\": \"${cluster-env/user_group}\"") + "        },") + "        \"owner\": {") + "          \"access\": \"r\",") + "          \"name\": \"root\"") + "        }") + "      },") + "      \"name\": \"spnego\",") + "      \"principal\": {") + "        \"configuration\": null,") + "        \"local_username\": null,") + "        \"type\": \"service\",") + "        \"value\": \"HTTP/_HOST@${realm}\"") + "      }") + "    }") + "  ],") + "  \"services\": [") + "    {") + "      \"components\": [") + "        {") + "          \"identities\": [") + "            {") + "              \"name\": \"service_master_spnego_identity\",") + "              \"reference\": \"/spnego\"") + "            }") + "          ],") + "          \"name\": \"SERVICE_MASTER\"") + "        }") + "      ],") + "      \"configurations\": [") + "        {") + "          \"service-site\": {") + "            \"property1\": \"property1_updated_value\",") + "            \"property2\": \"property2_updated_value\"") + "          }") + "        }") + "      ],") + "      \"identities\": [") + "        {") + "          \"name\": \"service_identity\",") + "          \"keytab\": {") + "            \"configuration\": \"service-site/keytab_file_path\",") + "            \"file\": \"${keytab_dir}/service.service.keytab\",") + "            \"group\": {") + "              \"access\": \"r\",") + "              \"name\": \"${cluster-env/user_group}\"") + "            },") + "            \"owner\": {") + "              \"access\": \"r\",") + "              \"name\": \"${service-env/service_user}\"") + "            }") + "          },") + "          \"principal\": {") + "            \"configuration\": \"service-site/principal_name\",") + "            \"local_username\": \"${service-env/service_user}\",") + "            \"type\": \"service\",") + "            \"value\": \"${service-env/service_user}/_HOST@${realm}\"") + "          }") + "        }") + "      ],") + "      \"name\": \"SERVICE\"") + "    }") + "  ],") + "  \"properties\": {") + "    \"additional_realms\": \"\",") + "    \"keytab_dir\": \"/etc/security/keytabs\",") + "    \"principal_suffix\": \"-${cluster_name|toLower()}\",") + "    \"realm\": \"${kerberos-env/realm}\"") + "  }") + "}");

    private class TestKerberosServerAction extends AbstractPrepareKerberosServerAction {
        @Override
        protected String getClusterName() {
            return "c1";
        }

        @Override
        public CommandReport execute(ConcurrentMap<String, Object> requestSharedDataContext) {
            return null;
        }
    }

    private Injector injector;

    private final AbstractPrepareKerberosServerAction testKerberosServerAction = new AbstractPrepareKerberosServerActionTest.TestKerberosServerAction();

    /**
     * Test checks that {@code KerberosHelper.applyStackAdvisorUpdates} would be called with
     * full list of the services and not only list of services with KerberosDescriptior.
     * In this test HDFS service will have KerberosDescriptor, while Zookeeper not.
     *
     * @throws Exception
     * 		
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testProcessServiceComponentHosts() throws Exception {
        final Cluster cluster = createNiceMock(Cluster.class);
        final KerberosIdentityDataFileWriter kerberosIdentityDataFileWriter = createNiceMock(KerberosIdentityDataFileWriter.class);
        final KerberosDescriptor kerberosDescriptor = createNiceMock(KerberosDescriptor.class);
        final ServiceComponentHost serviceComponentHostHDFS = createNiceMock(ServiceComponentHost.class);
        final ServiceComponentHost serviceComponentHostZK = createNiceMock(ServiceComponentHost.class);
        final KerberosServiceDescriptor serviceDescriptor = createNiceMock(KerberosServiceDescriptor.class);
        final KerberosComponentDescriptor componentDescriptor = createNiceMock(KerberosComponentDescriptor.class);
        final String hdfsService = "HDFS";
        final String zookeeperService = "ZOOKEEPER";
        final String hostName = "host1";
        final String hdfsComponent = "DATANODE";
        final String zkComponent = "ZK";
        Collection<String> identityFilter = new ArrayList<>();
        Map<String, Map<String, String>> kerberosConfigurations = new HashMap<>();
        Map<String, Set<String>> propertiesToIgnore = new HashMap<>();
        Map<String, Map<String, String>> configurations = new HashMap<>();
        List<ServiceComponentHost> serviceComponentHosts = new ArrayList<ServiceComponentHost>() {
            {
                add(serviceComponentHostHDFS);
                add(serviceComponentHostZK);
            }
        };
        Map<String, Service> clusterServices = new HashMap<String, Service>() {
            {
                put(hdfsService, null);
                put(zookeeperService, null);
            }
        };
        KerberosIdentityDataFileWriterFactory kerberosIdentityDataFileWriterFactory = injector.getInstance(KerberosIdentityDataFileWriterFactory.class);
        expect(kerberosIdentityDataFileWriterFactory.createKerberosIdentityDataFileWriter(anyObject(File.class))).andReturn(kerberosIdentityDataFileWriter);
        // it's important to pass a copy of clusterServices
        expect(cluster.getServices()).andReturn(new HashMap(clusterServices)).atLeastOnce();
        expect(serviceComponentHostHDFS.getHostName()).andReturn(hostName).atLeastOnce();
        expect(serviceComponentHostHDFS.getServiceName()).andReturn(hdfsService).atLeastOnce();
        expect(serviceComponentHostHDFS.getServiceComponentName()).andReturn(hdfsComponent).atLeastOnce();
        expect(serviceComponentHostHDFS.getHost()).andReturn(createNiceMock(Host.class)).atLeastOnce();
        expect(serviceComponentHostZK.getHostName()).andReturn(hostName).atLeastOnce();
        expect(serviceComponentHostZK.getServiceName()).andReturn(zookeeperService).atLeastOnce();
        expect(serviceComponentHostZK.getServiceComponentName()).andReturn(zkComponent).atLeastOnce();
        expect(serviceComponentHostZK.getHost()).andReturn(createNiceMock(Host.class)).atLeastOnce();
        expect(kerberosDescriptor.getService(hdfsService)).andReturn(serviceDescriptor).once();
        expect(serviceDescriptor.getComponent(hdfsComponent)).andReturn(componentDescriptor).once();
        expect(componentDescriptor.getConfigurations(anyBoolean())).andReturn(null);
        replayAll();
        injector.getInstance(AmbariMetaInfo.class).init();
        testKerberosServerAction.processServiceComponentHosts(cluster, kerberosDescriptor, serviceComponentHosts, identityFilter, "", configurations, kerberosConfigurations, false, propertiesToIgnore);
        verifyAll();
        // Ensure the host and hostname values were set in the configuration context
        Assert.assertEquals("host1", configurations.get("").get("host"));
        Assert.assertEquals("host1", configurations.get("").get("hostname"));
    }

    @Test
    public void testProcessConfigurationChanges() throws Exception {
        // Existing property map....
        Map<String, String> serviceSiteProperties = new HashMap<>();
        serviceSiteProperties.put("property1", "property1_value");
        serviceSiteProperties.put("principal_name", "principal_name_value");
        serviceSiteProperties.put("keytab_file_path", "keytab_file_path_value");
        Map<String, Map<String, String>> effectiveProperties = new HashMap<>();
        effectiveProperties.put("service-site", serviceSiteProperties);
        // Updated property map....
        Map<String, String> updatedServiceSiteProperties = new HashMap<>();
        updatedServiceSiteProperties.put("property1", "property1_updated_value");
        updatedServiceSiteProperties.put("property2", "property2_updated_value");
        updatedServiceSiteProperties.put("principal_name", "principal_name_updated_value");
        updatedServiceSiteProperties.put("keytab_file_path", "keytab_file_path_updated_value");
        Map<String, Map<String, String>> kerberosConfigurations = new HashMap<>();
        kerberosConfigurations.put("service-site", updatedServiceSiteProperties);
        KerberosDescriptor kerberosDescriptor = new KerberosDescriptorFactory().createInstance(AbstractPrepareKerberosServerActionTest.KERBEROS_DESCRIPTOR_JSON);
        ConfigHelper configHelper = injector.getInstance(ConfigHelper.class);
        expect(configHelper.getEffectiveConfigProperties(eq("c1"), eq(null))).andReturn(effectiveProperties).anyTimes();
        KerberosConfigDataFileWriterFactory factory = injector.getInstance(KerberosConfigDataFileWriterFactory.class);
        AbstractPrepareKerberosServerActionTest.ConfigWriterData dataCaptureAll = setupConfigWriter(factory);
        AbstractPrepareKerberosServerActionTest.ConfigWriterData dataCaptureIdentitiesOnly = setupConfigWriter(factory);
        AbstractPrepareKerberosServerActionTest.ConfigWriterData dataCaptureNewAndIdentities = setupConfigWriter(factory);
        AbstractPrepareKerberosServerActionTest.ConfigWriterData dataCaptureNone = setupConfigWriter(factory);
        replayAll();
        injector.getInstance(AmbariMetaInfo.class).init();
        Map<String, String> expectedProperties;
        // Update all configurations
        testKerberosServerAction.processConfigurationChanges("test_directory", kerberosConfigurations, Collections.emptyMap(), kerberosDescriptor, ALL);
        expectedProperties = new HashMap<>();
        expectedProperties.put("property1", "property1_updated_value");
        expectedProperties.put("property2", "property2_updated_value");
        expectedProperties.put("principal_name", "principal_name_updated_value");
        expectedProperties.put("keytab_file_path", "keytab_file_path_updated_value");
        verifyDataCapture(dataCaptureAll, Collections.singletonMap("service-site", expectedProperties));
        // Update only identity configurations
        testKerberosServerAction.processConfigurationChanges("test_directory", kerberosConfigurations, Collections.emptyMap(), kerberosDescriptor, IDENTITIES_ONLY);
        expectedProperties = new HashMap<>();
        expectedProperties.put("principal_name", "principal_name_updated_value");
        expectedProperties.put("keytab_file_path", "keytab_file_path_updated_value");
        verifyDataCapture(dataCaptureIdentitiesOnly, Collections.singletonMap("service-site", expectedProperties));
        // Update new and identity configurations
        testKerberosServerAction.processConfigurationChanges("test_directory", kerberosConfigurations, Collections.emptyMap(), kerberosDescriptor, NEW_AND_IDENTITIES);
        expectedProperties = new HashMap<>();
        expectedProperties.put("property2", "property2_updated_value");
        expectedProperties.put("principal_name", "principal_name_updated_value");
        expectedProperties.put("keytab_file_path", "keytab_file_path_updated_value");
        verifyDataCapture(dataCaptureNewAndIdentities, Collections.singletonMap("service-site", expectedProperties));
        // Update no configurations
        testKerberosServerAction.processConfigurationChanges("test_directory", kerberosConfigurations, Collections.emptyMap(), kerberosDescriptor, NONE);
        verifyDataCapture(dataCaptureNone, Collections.emptyMap());
        verifyAll();
    }

    private class ConfigWriterData {
        private final Capture<String> captureConfigType;

        private final Capture<String> capturePropertyName;

        private final Capture<String> capturePropertyValue;

        private ConfigWriterData(Capture<String> captureConfigType, Capture<String> capturePropertyName, Capture<String> capturePropertyValue) {
            this.captureConfigType = captureConfigType;
            this.capturePropertyName = capturePropertyName;
            this.capturePropertyValue = capturePropertyValue;
        }

        Capture<String> getCaptureConfigType() {
            return captureConfigType;
        }

        Capture<String> getCapturePropertyName() {
            return capturePropertyName;
        }

        Capture<String> getCapturePropertyValue() {
            return capturePropertyValue;
        }
    }
}

