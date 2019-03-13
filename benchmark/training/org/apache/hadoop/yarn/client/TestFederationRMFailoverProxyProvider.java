/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.yarn.client;


import YarnConfiguration.DEFAULT_RM_SCHEDULER_ADDRESS;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_PORT;
import YarnConfiguration.RM_CLUSTER_ID;
import YarnConfiguration.RM_SCHEDULER_ADDRESS;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.retry.FailoverProxyProvider.ProxyInfo;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsResponse;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.federation.failover.FederationRMFailoverProxyProvider;
import org.apache.hadoop.yarn.server.federation.store.FederationStateStore;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for FederationRMFailoverProxyProvider.
 */
public class TestFederationRMFailoverProxyProvider {
    private Configuration conf;

    private FederationStateStore stateStore;

    private final String dummyCapability = "cap";

    private GetClusterMetricsResponse threadResponse;

    @Test(timeout = 60000)
    public void testFederationRMFailoverProxyProvider() throws Exception {
        testProxyProvider(true);
    }

    @Test(timeout = 60000)
    public void testFederationRMFailoverProxyProviderWithoutFlushFacadeCache() throws Exception {
        testProxyProvider(false);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testUGIForProxyCreation() throws IOException, InterruptedException {
        conf.set(RM_CLUSTER_ID, "cluster1");
        UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
        UserGroupInformation user1 = UserGroupInformation.createProxyUser("user1", currentUser);
        UserGroupInformation user2 = UserGroupInformation.createProxyUser("user2", currentUser);
        final TestFederationRMFailoverProxyProvider.TestableFederationRMFailoverProxyProvider provider = new TestFederationRMFailoverProxyProvider.TestableFederationRMFailoverProxyProvider();
        InetSocketAddress addr = conf.getSocketAddr(RM_SCHEDULER_ADDRESS, DEFAULT_RM_SCHEDULER_ADDRESS, DEFAULT_RM_SCHEDULER_PORT);
        final ClientRMProxy rmProxy = Mockito.mock(ClientRMProxy.class);
        Mockito.when(rmProxy.getRMAddress(ArgumentMatchers.any(YarnConfiguration.class), ArgumentMatchers.any(Class.class))).thenReturn(addr);
        user1.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() {
                provider.init(conf, rmProxy, ApplicationMasterProtocol.class);
                return null;
            }
        });
        final ProxyInfo currentProxy = provider.getProxy();
        Assert.assertEquals("user1", provider.getLastProxyUGI().getUserName());
        user2.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() {
                provider.performFailover(currentProxy.proxy);
                return null;
            }
        });
        Assert.assertEquals("user1", provider.getLastProxyUGI().getUserName());
        provider.close();
    }

    protected static class TestableFederationRMFailoverProxyProvider<T> extends FederationRMFailoverProxyProvider<T> {
        private UserGroupInformation lastProxyUGI = null;

        @Override
        protected T createRMProxy(InetSocketAddress rmAddress) throws IOException {
            lastProxyUGI = UserGroupInformation.getCurrentUser();
            return super.createRMProxy(rmAddress);
        }

        public UserGroupInformation getLastProxyUGI() {
            return lastProxyUGI;
        }
    }
}

