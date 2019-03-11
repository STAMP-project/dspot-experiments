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
package org.apache.ambari.server.controller.metrics;


import Resource.Type.HostComponent;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.internal.PropertyInfo;
import org.apache.ambari.server.controller.jmx.JMXHostProvider;
import org.apache.ambari.server.controller.metrics.MetricsServiceProvider.MetricsService;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.services.MetricsRetrievalService;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * JMX property provider tests.
 */
public class JMXPropertyProviderTest {
    protected static final String CLUSTER_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "cluster_name");

    protected static final String HOST_COMPONENT_HOST_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "host_name");

    protected static final String HOST_COMPONENT_COMPONENT_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "component_name");

    protected static final String HOST_COMPONENT_STATE_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "state");

    public static final int NUMBER_OF_RESOURCES = 400;

    private static final int METRICS_SERVICE_TIMEOUT = 10;

    public static final Map<String, Map<String, PropertyInfo>> jmxPropertyIds = PropertyHelper.getJMXPropertyIds(HostComponent);

    public static final Map<String, Map<String, PropertyInfo>> jmxPropertyIdsWithHAState;

    static {
        jmxPropertyIdsWithHAState = new java.util.HashMap(JMXPropertyProviderTest.jmxPropertyIds);
        JMXPropertyProviderTest.jmxPropertyIdsWithHAState.get("NAMENODE").put("metrics/dfs/FSNamesystem/HAState", new PropertyInfo("Hadoop:service=NameNode,name=FSNamesystem.tag#HAState", false, true));
    }

    private static MetricPropertyProviderFactory metricPropertyProviderFactory;

    private static MetricsRetrievalService metricsRetrievalService;

    @Test
    public void testJMXPropertyProviderAsClusterAdministrator() throws Exception {
        // Setup user with Role 'ClusterAdministrator'.
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("ClusterAdmin", 2L));
        testPopulateResources();
        testPopulateResources_singleProperty();
        testPopulateResources_category();
        testPopulateResourcesWithUnknownPort();
        testPopulateResourcesUnhealthyResource();
        testPopulateResourcesMany();
        testPopulateResourcesTimeout();
        testPopulateResources_HAState_request();
    }

    @Test
    public void testJMXPropertyProviderAsAdministrator() throws Exception {
        // Setup user with Role 'Administrator'
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator("Admin"));
        testPopulateResources();
        testPopulateResources_singleProperty();
        testPopulateResources_category();
        testPopulateResourcesWithUnknownPort();
        testPopulateResourcesUnhealthyResource();
        testPopulateResourcesMany();
        testPopulateResourcesTimeout();
        testPopulateResources_HAState_request();
    }

    @Test
    public void testJMXPropertyProviderAsServiceAdministrator() throws Exception {
        // Setup user with 'ServiceAdministrator'
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createServiceAdministrator("ServiceAdmin", 2L));
        testPopulateResources();
        testPopulateResources_singleProperty();
        testPopulateResources_category();
        testPopulateResourcesWithUnknownPort();
        testPopulateResourcesUnhealthyResource();
        testPopulateResourcesMany();
        testPopulateResourcesTimeout();
        testPopulateResources_HAState_request();
    }

    @Test(expected = AuthorizationException.class)
    public void testJMXPropertyProviderAsViewUser() throws Exception {
        // Setup user with 'ViewUser'
        // ViewUser doesn't have the 'CLUSTER_VIEW_METRICS', 'HOST_VIEW_METRICS' and 'SERVICE_VIEW_METRICS', thus
        // can't retrieve the Metrics.
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createViewUser("ViewUser", 2L));
        testPopulateResources();
        testPopulateResources_singleProperty();
        testPopulateResources_category();
        testPopulateResourcesWithUnknownPort();
        testPopulateResourcesUnhealthyResource();
        testPopulateResourcesMany();
        testPopulateResourcesTimeout();
        testPopulateResources_HAState_request();
    }

    public static class TestJMXHostProvider implements JMXHostProvider {
        private final boolean unknownPort;

        public TestJMXHostProvider(boolean unknownPort) {
            this.unknownPort = unknownPort;
        }

        @Override
        public String getPublicHostName(final String clusterName, final String hostName) {
            return null;
        }

        @Override
        public Set<String> getHostNames(String clusterName, String componentName) {
            return null;
        }

        @Override
        public String getPort(String clusterName, String componentName, String hostName, boolean httpsEnabled) {
            return getPort(clusterName, componentName, hostName);
        }

        public String getPort(String clusterName, String componentName, String hostName) {
            if (unknownPort) {
                return null;
            }
            if (componentName.equals("NAMENODE")) {
                return "50070";
            } else
                if (componentName.equals("DATANODE")) {
                    return "50075";
                } else
                    if (componentName.equals("HBASE_MASTER")) {
                        if (clusterName.equals("c2")) {
                            return "60011";
                        } else {
                            // Caters the case where 'clusterName' is null or
                            // any other name (includes hardcoded name "c1").
                            return "60010";
                        }
                    } else
                        if (componentName.equals("JOURNALNODE")) {
                            return "8480";
                        } else
                            if (componentName.equals("STORM_REST_API")) {
                                return "8745";
                            } else {
                                return null;
                            }




        }

        @Override
        public String getJMXProtocol(String clusterName, String componentName) {
            return "http";
        }

        @Override
        public String getJMXRpcMetricTag(String clusterName, String componentName, String port) {
            return null;
        }
    }

    public static class TestMetricHostProvider implements MetricHostProvider {
        @Override
        public String getCollectorHostName(String clusterName, MetricsService service) throws SystemException {
            return null;
        }

        @Override
        public String getHostName(String clusterName, String componentName) {
            return null;
        }

        @Override
        public String getCollectorPort(String clusterName, MetricsService service) throws SystemException {
            return null;
        }

        @Override
        public boolean isCollectorHostLive(String clusterName, MetricsService service) throws SystemException {
            return false;
        }

        @Override
        public boolean isCollectorComponentLive(String clusterName, MetricsService service) throws SystemException {
            return false;
        }

        @Override
        public boolean isCollectorHostExternal(String clusterName) {
            return false;
        }
    }
}

