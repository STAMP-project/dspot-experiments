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
package org.apache.hadoop.hdfs.server.federation.router;


import java.util.Collection;
import java.util.List;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.apache.hadoop.hdfs.server.federation.StateStoreDFSCluster;
import org.apache.hadoop.hdfs.server.federation.resolver.FederationNamenodeContext;
import org.apache.hadoop.hdfs.server.federation.resolver.MembershipNamenodeResolver;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test namenodes monitor behavior in the Router.
 */
public class TestRouterNamenodeMonitoring {
    private static StateStoreDFSCluster cluster;

    private static MiniRouterDFSCluster.RouterContext routerContext;

    private static MembershipNamenodeResolver resolver;

    private String ns0;

    private String ns1;

    private long initializedTime;

    @Test
    public void testNamenodeMonitoring() throws Exception {
        // Set nn0 to active for all nameservices
        for (String ns : TestRouterNamenodeMonitoring.cluster.getNameservices()) {
            TestRouterNamenodeMonitoring.cluster.switchToActive(ns, "nn0");
            TestRouterNamenodeMonitoring.cluster.switchToStandby(ns, "nn1");
        }
        Collection<NamenodeHeartbeatService> heartbeatServices = TestRouterNamenodeMonitoring.routerContext.getRouter().getNamenodeHearbeatServices();
        // manually trigger the heartbeat
        for (NamenodeHeartbeatService service : heartbeatServices) {
            service.periodicInvoke();
        }
        TestRouterNamenodeMonitoring.resolver.loadCache(true);
        List<? extends FederationNamenodeContext> namespaceInfo0 = TestRouterNamenodeMonitoring.resolver.getNamenodesForNameserviceId(ns0);
        List<? extends FederationNamenodeContext> namespaceInfo1 = TestRouterNamenodeMonitoring.resolver.getNamenodesForNameserviceId(ns1);
        // The modified date won't be updated in ns0.nn0 since it isn't
        // monitored by the Router.
        Assert.assertEquals("nn0", getNamenodeId());
        Assert.assertTrue(((getDateModified()) < (initializedTime)));
        // other namnodes should be updated as expected
        Assert.assertEquals("nn1", getNamenodeId());
        Assert.assertTrue(((getDateModified()) > (initializedTime)));
        Assert.assertEquals("nn0", getNamenodeId());
        Assert.assertTrue(((getDateModified()) > (initializedTime)));
        Assert.assertEquals("nn1", getNamenodeId());
        Assert.assertTrue(((getDateModified()) > (initializedTime)));
    }
}

