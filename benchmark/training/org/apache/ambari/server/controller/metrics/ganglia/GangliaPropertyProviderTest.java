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
package org.apache.ambari.server.controller.metrics.ganglia;


import org.apache.ambari.server.configuration.ComponentSSLConfiguration;
import org.apache.ambari.server.controller.metrics.MetricHostProvider;
import org.apache.ambari.server.controller.metrics.MetricsServiceProvider;
import org.apache.ambari.server.controller.metrics.MetricsServiceProvider.MetricsService;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.security.authorization.AuthorizationHelperInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Test the Ganglia property provider.
 */
@RunWith(Parameterized.class)
@PrepareForTest({ MetricHostProvider.class })
public class GangliaPropertyProviderTest {
    private static final String PROPERTY_ID = PropertyHelper.getPropertyId("metrics/process", "proc_total");

    private static final String PROPERTY_ID2 = PropertyHelper.getPropertyId("metrics/cpu", "cpu_wio");

    private static final String FLUME_CHANNEL_CAPACITY_PROPERTY = "metrics/flume/flume/CHANNEL/c1/ChannelCapacity";

    private static final String FLUME_CATEGORY = "metrics/flume";

    private static final String FLUME_CATEGORY2 = "metrics/flume/flume";

    private static final String FLUME_CATEGORY3 = "metrics/flume/flume/CHANNEL";

    private static final String FLUME_CATEGORY4 = "metrics/flume/flume/CHANNEL/c1";

    private static final String CLUSTER_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "cluster_name");

    private static final String HOST_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "host_name");

    private static final String COMPONENT_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("HostRoles", "component_name");

    private ComponentSSLConfiguration configuration;

    public GangliaPropertyProviderTest(ComponentSSLConfiguration configuration) {
        this.configuration = configuration;
    }

    @Test
    public void testGangliaPropertyProviderAsClusterAdministrator() throws Exception {
        // Setup user with Role 'ClusterAdministrator'.
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("ClusterAdmin", 2L));
        testPopulateResources();
        testPopulateResources_checkHostComponent();
        testPopulateResources_checkHost();
        testPopulateManyResources();
        testPopulateResources__LargeNumberOfHostResources();
        testPopulateResources_params();
        testPopulateResources_paramsMixed();
        testPopulateResources_paramsAll();
        testPopulateResources_params_category1();
        testPopulateResources_params_category2();
        testPopulateResources_params_category3();
        testPopulateResources_params_category4();
    }

    @Test
    public void testGangliaPropertyProviderAsAdministrator() throws Exception {
        // Setup user with Role 'Administrator'
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator("Admin"));
        testPopulateResources();
        testPopulateResources_checkHostComponent();
        testPopulateResources_checkHost();
        testPopulateManyResources();
        testPopulateResources__LargeNumberOfHostResources();
        testPopulateResources_params();
        testPopulateResources_paramsMixed();
        testPopulateResources_paramsAll();
        testPopulateResources_params_category1();
        testPopulateResources_params_category2();
        testPopulateResources_params_category3();
        testPopulateResources_params_category4();
    }

    @Test
    public void testGangliaPropertyProviderAsServiceAdministrator() throws Exception {
        // Setup user with 'ServiceAdministrator'
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createServiceAdministrator("ServiceAdmin", 2L));
        testPopulateResources();
        testPopulateResources_checkHostComponent();
        testPopulateResources_checkHost();
        testPopulateManyResources();
        testPopulateResources__LargeNumberOfHostResources();
        testPopulateResources_params();
        testPopulateResources_paramsMixed();
        testPopulateResources_paramsAll();
        testPopulateResources_params_category1();
        testPopulateResources_params_category2();
        testPopulateResources_params_category3();
        testPopulateResources_params_category4();
    }

    @Test(expected = AuthorizationException.class)
    public void testGangliaPropertyProviderAsViewUser() throws Exception {
        AuthorizationHelperInitializer.viewInstanceDAOReturningNull();
        // Setup user with 'ViewUser'
        // ViewUser doesn't have the 'CLUSTER_VIEW_METRICS', 'HOST_VIEW_METRICS' and 'SERVICE_VIEW_METRICS', thus
        // can't retrieve the Metrics.
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createViewUser("ViewUser", 2L));
        testPopulateResources();
        testPopulateResources_checkHostComponent();
        testPopulateResources_checkHost();
        testPopulateManyResources();
        testPopulateResources__LargeNumberOfHostResources();
        testPopulateResources_params();
        testPopulateResources_paramsMixed();
        testPopulateResources_paramsAll();
        testPopulateResources_params_category1();
        testPopulateResources_params_category2();
        testPopulateResources_params_category3();
        testPopulateResources_params_category4();
    }

    public static class TestGangliaServiceProvider implements MetricsServiceProvider {
        @Override
        public MetricsService getMetricsServiceType() {
            return MetricsService.GANGLIA;
        }
    }

    public static class TestGangliaHostProvider implements MetricHostProvider {
        private boolean isHostLive;

        private boolean isComponentLive;

        public TestGangliaHostProvider() {
            this(true, true);
        }

        public TestGangliaHostProvider(boolean isHostLive, boolean isComponentLive) {
            this.isHostLive = isHostLive;
            this.isComponentLive = isComponentLive;
        }

        @Override
        public String getCollectorHostName(String clusterName, MetricsService service) {
            return "domU-12-31-39-0E-34-E1.compute-1.internal";
        }

        @Override
        public String getHostName(String clusterName, String componentName) throws SystemException {
            return null;
        }

        @Override
        public String getCollectorPort(String clusterName, MetricsService service) throws SystemException {
            return null;
        }

        @Override
        public boolean isCollectorHostLive(String clusterName, MetricsService service) throws SystemException {
            return isHostLive;
        }

        @Override
        public boolean isCollectorComponentLive(String clusterName, MetricsService service) throws SystemException {
            return isComponentLive;
        }

        @Override
        public boolean isCollectorHostExternal(String clusterName) {
            return false;
        }
    }
}

