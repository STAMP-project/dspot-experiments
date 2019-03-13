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
package org.apache.ambari.msi;


import Resource.Type;
import Resource.Type.Cluster;
import Resource.Type.Component;
import Resource.Type.Configuration;
import Resource.Type.Host;
import Resource.Type.HostComponent;
import Resource.Type.Request;
import Resource.Type.Service;
import Resource.Type.Task;
import org.apache.ambari.scom.TestClusterDefinitionProvider;
import org.apache.ambari.scom.TestHostInfoProvider;
import org.junit.Assert;
import org.junit.Test;


/**
 * AbstractResourceProvider tests.
 */
public class AbstractResourceProviderTest {
    private static Set<Resource.Type> types = new HashSet<Resource.Type>();

    static {
        AbstractResourceProviderTest.types.add(Cluster);
        AbstractResourceProviderTest.types.add(Service);
        AbstractResourceProviderTest.types.add(Component);
        AbstractResourceProviderTest.types.add(Host);
        AbstractResourceProviderTest.types.add(HostComponent);
        AbstractResourceProviderTest.types.add(Request);
        AbstractResourceProviderTest.types.add(Task);
        AbstractResourceProviderTest.types.add(Configuration);
    }

    @Test
    public void testGetResourceProvider() throws Exception {
        ClusterDefinition clusterDefinition = new ClusterDefinition(new TestStateProvider(), new TestClusterDefinitionProvider(), new TestHostInfoProvider());
        clusterDefinition.setServiceState("HDFS", "INSTALLED");
        for (Resource.Type type : AbstractResourceProviderTest.types) {
            ResourceProvider provider = AbstractResourceProvider.getResourceProvider(type, clusterDefinition);
            Set<Resource> resources = provider.getResources(PropertyHelper.getReadRequest(), null);
            for (Resource resource : resources) {
                Assert.assertEquals(type, resource.getType());
            }
        }
    }
}

