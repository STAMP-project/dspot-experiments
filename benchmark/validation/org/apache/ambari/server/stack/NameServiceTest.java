/**
 * * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package org.apache.ambari.server.stack;


import ConfigHelper.HTTPS_ONLY;
import java.util.List;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.ConfigHelper;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.hamcrest.Matchers;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class NameServiceTest extends EasyMockSupport {
    private ConfigHelper config = EasyMock.createNiceMock(ConfigHelper.class);

    private Cluster cluster = mock(Cluster.class);

    @Test
    public void testParseSingleNameService() {
        defineHdfsProperty("dfs.internal.nameservices", "ns1");
        defineHdfsProperty("dfs.ha.namenodes.ns1", "nn1");
        defineHdfsProperty("dfs.namenode.http-address.ns1.nn1", "c6401:1234");
        replay(config);
        List<NameService> nameServices = NameService.fromConfig(config, cluster);
        Assert.assertThat(nameServices, Matchers.hasSize(1));
        Assert.assertThat(nameServices.get(0).nameServiceId, Is.is("ns1"));
        Assert.assertThat(nameServices.get(0).getNameNodes(), hasOnlyItems(AllOf.allOf(hasHost("c6401"), hasPort(1234), hasPropertyName("dfs.namenode.http-address.ns1.nn1"))));
    }

    @Test
    public void testParseSingleNameServiceWhenHttpsEnabled() {
        defineHdfsProperty("dfs.internal.nameservices", "ns1");
        defineHdfsProperty("dfs.ha.namenodes.ns1", "nn1");
        defineHdfsProperty("dfs.namenode.https-address.ns1.nn1", "c6401:4567");
        defineHdfsProperty("dfs.http.policy", HTTPS_ONLY);
        replay(config);
        List<NameService> nameServices = NameService.fromConfig(config, cluster);
        Assert.assertThat(nameServices.get(0).getNameNodes(), hasOnlyItems(AllOf.allOf(hasPort(4567), hasPropertyName("dfs.namenode.https-address.ns1.nn1"))));
    }

    @Test
    public void testParseFederatedNameService() {
        defineHdfsProperty("dfs.internal.nameservices", "ns1,ns2");
        defineHdfsProperty("dfs.ha.namenodes.ns1", "nn1,nn2");
        defineHdfsProperty("dfs.ha.namenodes.ns2", "nn3,nn4");
        defineHdfsProperty("dfs.namenode.http-address.ns1.nn1", "c6401:1234");
        defineHdfsProperty("dfs.namenode.http-address.ns1.nn2", "c6402:1234");
        defineHdfsProperty("dfs.namenode.http-address.ns2.nn3", "c6403:1234");
        defineHdfsProperty("dfs.namenode.http-address.ns2.nn4", "c6404:1234");
        replay(config);
        Assert.assertThat(NameService.fromConfig(config, cluster), hasOnlyItems(hasNameNodes(hasOnlyItems(hasHost("c6401"), hasHost("c6402"))), hasNameNodes(hasOnlyItems(hasHost("c6403"), hasHost("c6404")))));
    }

    @Test
    public void tesEmptyWhenNameServiceIdIsMissingFromConfig() {
        defineHdfsProperty("dfs.internal.nameservices", null);
        replay(config);
        Assert.assertThat(NameService.fromConfig(config, cluster), Matchers.hasSize(0));
    }

    @Test
    public void tesEmptyNameNodesWhenNs1IsMissingFromConfig() {
        defineHdfsProperty("dfs.internal.nameservices", "ns1");
        defineHdfsProperty("dfs.ha.namenodes.ns1", null);
        replay(config);
        Assert.assertThat(NameService.fromConfig(config, cluster).get(0).getNameNodes(), Matchers.hasSize(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tesExceptionWhenNameNodeAddressIsMissingFromConfig() {
        defineHdfsProperty("dfs.internal.nameservices", "ns1");
        defineHdfsProperty("dfs.ha.namenodes.ns1", "nn1");
        defineHdfsProperty("dfs.namenode.http-address.ns1.nn1", null);
        replay(config);
        NameService.fromConfig(config, cluster).get(0).getNameNodes().get(0).getHost();
    }
}

