/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.controller.metrics;


import com.google.inject.Injector;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.internal.PropertyInfo;
import org.apache.ambari.server.controller.internal.StackDefinedPropertyProvider;
import org.apache.ambari.server.controller.jmx.TestStreamProvider;
import org.apache.ambari.server.controller.metrics.MetricsServiceProvider.MetricsService;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.services.MetricsRetrievalService;
import org.apache.ambari.server.state.stack.Metric;
import org.apache.ambari.server.state.stack.MetricDefinition;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Rest metrics property provider tests.
 */
public class RestMetricsPropertyProviderTest {
    public static final String WRAPPED_METRICS_KEY = "WRAPPED_METRICS_KEY";

    protected static final String HOST_COMPONENT_HOST_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "host_name");

    protected static final String HOST_COMPONENT_COMPONENT_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "component_name");

    protected static final String HOST_COMPONENT_STATE_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "state");

    protected static final Map<String, String> metricsProperties = new HashMap<>();

    protected static final Map<String, Metric> componentMetrics = new HashMap<>();

    private static final String CLUSTER_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "cluster_name");

    private static final String DEFAULT_STORM_UI_PORT = "8745";

    public static final int NUMBER_OF_RESOURCES = 400;

    private static final int METRICS_SERVICE_TIMEOUT = 10;

    private static Injector injector;

    private static Clusters clusters;

    private static Cluster c1;

    private static AmbariManagementController amc;

    private static MetricsRetrievalService metricsRetrievalService;

    {
        RestMetricsPropertyProviderTest.metricsProperties.put("default_port", RestMetricsPropertyProviderTest.DEFAULT_STORM_UI_PORT);
        RestMetricsPropertyProviderTest.metricsProperties.put("port_config_type", "storm-site");
        RestMetricsPropertyProviderTest.metricsProperties.put("port_property_name", "ui.port");
        RestMetricsPropertyProviderTest.metricsProperties.put("protocol", "http");
        RestMetricsPropertyProviderTest.componentMetrics.put("metrics/api/cluster/summary/tasks.total", new Metric("/api/cluster/summary##tasks.total", false, false, false, "unitless"));
        RestMetricsPropertyProviderTest.componentMetrics.put("metrics/api/cluster/summary/slots.total", new Metric("/api/cluster/summary##slots.total", false, false, false, "unitless"));
        RestMetricsPropertyProviderTest.componentMetrics.put("metrics/api/cluster/summary/slots.free", new Metric("/api/cluster/summary##slots.free", false, false, false, "unitless"));
        RestMetricsPropertyProviderTest.componentMetrics.put("metrics/api/cluster/summary/supervisors", new Metric("/api/cluster/summary##supervisors", false, false, false, "unitless"));
        RestMetricsPropertyProviderTest.componentMetrics.put("metrics/api/cluster/summary/executors.total", new Metric("/api/cluster/summary##executors.total", false, false, false, "unitless"));
        RestMetricsPropertyProviderTest.componentMetrics.put("metrics/api/cluster/summary/slots.used", new Metric("/api/cluster/summary##slots.used", false, false, false, "unitless"));
        RestMetricsPropertyProviderTest.componentMetrics.put("metrics/api/cluster/summary/topologies", new Metric("/api/cluster/summary##topologies", false, false, false, "unitless"));
        RestMetricsPropertyProviderTest.componentMetrics.put("metrics/api/cluster/summary/nimbus.uptime", new Metric("/api/cluster/summary##nimbus.uptime", false, false, false, "unitless"));
        RestMetricsPropertyProviderTest.componentMetrics.put("metrics/api/cluster/summary/wrong.metric", new Metric(null, false, false, false, "unitless"));
    }

    @Test
    public void testRestMetricsPropertyProviderAsClusterAdministrator() throws Exception {
        // Setup user with Role 'ClusterAdministrator'.
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("ClusterAdmin", 2L));
        testPopulateResources();
        testPopulateResources_singleProperty();
        testPopulateResources_category();
        testPopulateResourcesUnhealthyResource();
        testPopulateResourcesMany();
        testPopulateResourcesTimeout();
    }

    @Test
    public void testRestMetricsPropertyProviderAsAdministrator() throws Exception {
        // Setup user with Role 'Administrator'
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator("Admin"));
        testPopulateResources();
        testPopulateResources_singleProperty();
        testPopulateResources_category();
        testPopulateResourcesUnhealthyResource();
        testPopulateResourcesMany();
        testPopulateResourcesTimeout();
    }

    @Test
    public void testRestMetricsPropertyProviderAsServiceAdministrator() throws Exception {
        // Setup user with 'ServiceAdministrator'
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createServiceAdministrator("ServiceAdmin", 2L));
        testPopulateResources();
        testPopulateResources_singleProperty();
        testPopulateResources_category();
        testPopulateResourcesUnhealthyResource();
        testPopulateResourcesMany();
        testPopulateResourcesTimeout();
    }

    @Test(expected = AuthorizationException.class)
    public void testRestMetricsPropertyProviderAsViewUser() throws Exception {
        // Setup user with 'ViewUser'
        // ViewUser doesn't have the 'CLUSTER_VIEW_METRICS', 'HOST_VIEW_METRICS' and 'SERVICE_VIEW_METRICS', thus
        // can't retrieve the Metrics.
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createViewUser("ViewUser", 2L));
        testPopulateResources();
        testPopulateResources_singleProperty();
        testPopulateResources_category();
        testPopulateResourcesUnhealthyResource();
        testPopulateResourcesMany();
        testPopulateResourcesTimeout();
    }

    @Test
    public void testResolvePort() throws Exception {
        MetricDefinition metricDefinition = createNiceMock(MetricDefinition.class);
        expect(metricDefinition.getMetrics()).andReturn(RestMetricsPropertyProviderTest.componentMetrics);
        expect(metricDefinition.getType()).andReturn("org.apache.ambari.server.controller.metrics.RestMetricsPropertyProvider");
        expect(metricDefinition.getProperties()).andReturn(RestMetricsPropertyProviderTest.metricsProperties);
        replay(metricDefinition);
        Map<String, PropertyInfo> metrics = StackDefinedPropertyProvider.getPropertyInfo(metricDefinition);
        HashMap<String, Map<String, PropertyInfo>> componentMetrics = new HashMap<>();
        componentMetrics.put(RestMetricsPropertyProviderTest.WRAPPED_METRICS_KEY, metrics);
        TestStreamProvider streamProvider = new TestStreamProvider();
        RestMetricsPropertyProviderTest.TestMetricsHostProvider metricsHostProvider = new RestMetricsPropertyProviderTest.TestMetricsHostProvider();
        RestMetricsPropertyProvider restMetricsPropertyProvider = createRestMetricsPropertyProvider(metricDefinition, componentMetrics, streamProvider, metricsHostProvider);
        // a property with a port doesn't exist, should return a default
        Map<String, String> customMetricsProperties = new HashMap<>(RestMetricsPropertyProviderTest.metricsProperties);
        customMetricsProperties.put("port_property_name", "wrong_property");
        String resolvedPort = restMetricsPropertyProvider.resolvePort(RestMetricsPropertyProviderTest.c1, "domu-12-31-39-0e-34-e1.compute-1.internal", "STORM_REST_API", customMetricsProperties, "http");
        Assert.assertEquals(RestMetricsPropertyProviderTest.DEFAULT_STORM_UI_PORT, resolvedPort);
        // a port property exists (8745). Should return it, not a default_port (8746)
        customMetricsProperties = new HashMap<>(RestMetricsPropertyProviderTest.metricsProperties);
        // custom default
        customMetricsProperties.put("default_port", "8746");
        resolvedPort = restMetricsPropertyProvider.resolvePort(RestMetricsPropertyProviderTest.c1, "domu-12-31-39-0e-34-e1.compute-1.internal", "STORM_REST_API", customMetricsProperties, "http");
        Assert.assertEquals(RestMetricsPropertyProviderTest.DEFAULT_STORM_UI_PORT, resolvedPort);
    }

    public static class TestMetricsHostProvider implements MetricHostProvider {
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

