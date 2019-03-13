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
package org.apache.ambari.server.controller.internal;


import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.configuration.ComponentSSLConfiguration;
import org.apache.ambari.server.controller.metrics.JMXPropertyProviderTest;
import org.apache.ambari.server.controller.metrics.timeline.cache.TimelineMetricCacheEntryFactory;
import org.apache.ambari.server.controller.metrics.timeline.cache.TimelineMetricCacheProvider;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.PropertyProvider;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.services.MetricsRetrievalService;
import org.apache.ambari.server.state.stack.Metric;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Tests the stack defined property provider.
 */
public class StackDefinedPropertyProviderTest {
    private static final String HOST_COMPONENT_HOST_NAME_PROPERTY_ID = "HostRoles/host_name";

    private static final String HOST_COMPONENT_COMPONENT_NAME_PROPERTY_ID = "HostRoles/component_name";

    private static final String HOST_COMPONENT_STATE_PROPERTY_ID = "HostRoles/state";

    private static final String CLUSTER_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "cluster_name");

    private static final int METRICS_SERVICE_TIMEOUT = 10;

    private Clusters clusters = null;

    private Injector injector = null;

    private OrmTestHelper helper = null;

    private static TimelineMetricCacheEntryFactory cacheEntryFactory;

    private static TimelineMetricCacheProvider cacheProvider;

    private static MetricsRetrievalService metricsRetrievalService;

    public class TestModuleWithCacheProvider implements Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(TimelineMetricCacheProvider.class).toInstance(StackDefinedPropertyProviderTest.cacheProvider);
        }
    }

    @Test
    public void testStackDefinedPropertyProviderAsClusterAdministrator() throws Exception {
        // Setup user with Role 'ClusterAdministrator'.
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("ClusterAdmin", 2L));
        testPopulateHostComponentResources();
        testCustomProviders();
        testPopulateResources_HDP2();
        testPopulateResources_HDP2_params();
        testPopulateResources_HDP2_params_singleProperty();
        testPopulateResources_HDP2_params_category();
        testPopulateResources_HDP2_params_category2();
        testPopulateResources_jmx_JournalNode();
        testPopulateResources_jmx_Storm();
        testPopulateResources_NoRegionServer();
        testPopulateResources_HBaseMaster2();
        testPopulateResources_params_category5();
        testPopulateResources_ganglia_JournalNode();
        testPopulateResources_resourcemanager_clustermetrics();
        testPopulateResourcesWithAggregateFunctionMetrics();
    }

    @Test
    public void testStackDefinedPropertyProviderAsAdministrator() throws Exception {
        // Setup user with Role 'Administrator'
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator("Admin"));
        testPopulateHostComponentResources();
        testCustomProviders();
        testPopulateResources_HDP2();
        testPopulateResources_HDP2_params();
        testPopulateResources_HDP2_params_singleProperty();
        testPopulateResources_HDP2_params_category();
        testPopulateResources_HDP2_params_category2();
        testPopulateResources_jmx_JournalNode();
        testPopulateResources_jmx_Storm();
        testPopulateResources_NoRegionServer();
        testPopulateResources_HBaseMaster2();
        testPopulateResources_params_category5();
        testPopulateResources_ganglia_JournalNode();
        testPopulateResources_resourcemanager_clustermetrics();
        testPopulateResourcesWithAggregateFunctionMetrics();
    }

    @Test
    public void testStackDefinedPropertyProviderAsServiceAdministrator() throws Exception {
        // Setup user with 'ServiceAdministrator'
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createServiceAdministrator("ServiceAdmin", 2L));
        testPopulateHostComponentResources();
        testCustomProviders();
        testPopulateResources_HDP2();
        testPopulateResources_HDP2_params();
        testPopulateResources_HDP2_params_singleProperty();
        testPopulateResources_HDP2_params_category();
        testPopulateResources_HDP2_params_category2();
        testPopulateResources_jmx_JournalNode();
        testPopulateResources_jmx_Storm();
        testPopulateResources_NoRegionServer();
        testPopulateResources_HBaseMaster2();
        testPopulateResources_params_category5();
        testPopulateResources_ganglia_JournalNode();
        testPopulateResources_resourcemanager_clustermetrics();
        testPopulateResourcesWithAggregateFunctionMetrics();
    }

    @Test(expected = AuthorizationException.class)
    public void testStackDefinedPropertyProviderAsViewUser() throws Exception {
        // Setup user with 'ViewUser'
        // ViewUser doesn't have the 'CLUSTER_VIEW_METRICS', 'HOST_VIEW_METRICS' and 'SERVICE_VIEW_METRICS', thus
        // can't retrieve the Metrics.
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createViewUser("ViewUser", 2L));
        testPopulateHostComponentResources();
        testCustomProviders();
        testPopulateResources_HDP2();
        testPopulateResources_HDP2_params();
        testPopulateResources_HDP2_params_singleProperty();
        testPopulateResources_HDP2_params_category();
        testPopulateResources_HDP2_params_category2();
        testPopulateResources_jmx_JournalNode();
        testPopulateResources_jmx_Storm();
        testPopulateResources_NoRegionServer();
        testPopulateResources_HBaseMaster2();
        testPopulateResources_params_category5();
        testPopulateResources_ganglia_JournalNode();
        testPopulateResources_resourcemanager_clustermetrics();
        testPopulateResourcesWithAggregateFunctionMetrics();
    }

    private static class CombinedStreamProvider extends URLStreamProvider {
        public CombinedStreamProvider() {
            super(1000, 1000, ComponentSSLConfiguration.instance());
        }

        @Override
        public InputStream readFrom(String spec) throws IOException {
            if ((spec.indexOf("jmx")) > (-1)) {
                // jmx
                return ClassLoader.getSystemResourceAsStream("hdfs_namenode_jmx.json");
            } else {
                // ganglia
                return ClassLoader.getSystemResourceAsStream("temporal_ganglia_data.txt");
            }
        }

        @Override
        public InputStream readFrom(String spec, String requestMethod, String params) throws IOException {
            return readFrom(spec);
        }
    }

    private static class EmptyPropertyProvider implements PropertyProvider {
        @Override
        public Set<Resource> populateResources(Set<Resource> resources, Request request, Predicate predicate) throws SystemException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<String> checkPropertyIds(Set<String> propertyIds) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    /**
     * Test for empty constructor.  Public since instantiated via reflection.
     */
    public static class CustomMetricProvider1 implements PropertyProvider {
        @Override
        public Set<Resource> populateResources(Set<Resource> resources, Request request, Predicate predicate) throws SystemException {
            for (Resource r : resources) {
                r.setProperty("foo/type1/name", "value1");
            }
            return resources;
        }

        @Override
        public Set<String> checkPropertyIds(Set<String> propertyIds) {
            return Collections.emptySet();
        }
    }

    /**
     * Test map constructors.  Public since instantiated via reflection.
     */
    public static class CustomMetricProvider2 implements PropertyProvider {
        private Map<String, String> providerProperties = null;

        public CustomMetricProvider2(Map<String, String> properties, Map<String, Metric> metrics) {
            providerProperties = properties;
        }

        @Override
        public Set<Resource> populateResources(Set<Resource> resources, Request request, Predicate predicate) throws SystemException {
            for (Resource r : resources) {
                r.setProperty("foo/type2/name", providerProperties.get("Type2.Metric.Name"));
            }
            return resources;
        }

        @Override
        public Set<String> checkPropertyIds(Set<String> propertyIds) {
            return Collections.emptySet();
        }
    }

    /**
     * Test singleton accessor.  Public since instantiated via reflection.
     */
    public static class CustomMetricProvider3 implements PropertyProvider {
        private static StackDefinedPropertyProviderTest.CustomMetricProvider3 instance = null;

        private Map<String, String> providerProperties = new HashMap<>();

        public static StackDefinedPropertyProviderTest.CustomMetricProvider3 getInstance(Map<String, String> properties, Map<String, Metric> metrics) {
            if (null == (StackDefinedPropertyProviderTest.CustomMetricProvider3.instance)) {
                StackDefinedPropertyProviderTest.CustomMetricProvider3.instance = new StackDefinedPropertyProviderTest.CustomMetricProvider3();
                StackDefinedPropertyProviderTest.CustomMetricProvider3.instance.providerProperties.putAll(properties);
            }
            return StackDefinedPropertyProviderTest.CustomMetricProvider3.instance;
        }

        @Override
        public Set<Resource> populateResources(Set<Resource> resources, Request request, Predicate predicate) throws SystemException {
            for (Resource r : resources) {
                r.setProperty("foo/type3/name", providerProperties.get("Type3.Metric.Name"));
            }
            return resources;
        }

        @Override
        public Set<String> checkPropertyIds(Set<String> propertyIds) {
            return Collections.emptySet();
        }
    }

    public static class TestJMXProvider extends JMXPropertyProviderTest.TestJMXHostProvider {
        public TestJMXProvider(boolean unknownPort) {
            super(unknownPort);
        }

        @Override
        public String getJMXRpcMetricTag(String clusterName, String componentName, String port) {
            return "8020".equals(port) ? "client" : null;
        }
    }
}

