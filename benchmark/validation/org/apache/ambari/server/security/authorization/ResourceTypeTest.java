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
package org.apache.ambari.server.security.authorization;


import ResourceType.AMBARI;
import ResourceType.CLUSTER;
import ResourceType.VIEW;
import org.junit.Assert;
import org.junit.Test;


public class ResourceTypeTest {
    @Test
    public void testGetId() throws Exception {
        Assert.assertEquals(1, AMBARI.getId());
        Assert.assertEquals(2, CLUSTER.getId());
        Assert.assertEquals(3, VIEW.getId());
    }

    @Test
    public void testTranslate() throws Exception {
        Assert.assertEquals(AMBARI, ResourceType.translate("ambari"));
        Assert.assertEquals(AMBARI, ResourceType.translate("Ambari"));
        Assert.assertEquals(AMBARI, ResourceType.translate("AMBARI"));
        Assert.assertEquals(AMBARI, ResourceType.translate(" AMBARI "));
        Assert.assertEquals(CLUSTER, ResourceType.translate("cluster"));
        Assert.assertEquals(CLUSTER, ResourceType.translate("Cluster"));
        Assert.assertEquals(CLUSTER, ResourceType.translate("CLUSTER"));
        Assert.assertEquals(CLUSTER, ResourceType.translate(" CLUSTER "));
        Assert.assertEquals(VIEW, ResourceType.translate("view"));
        Assert.assertEquals(VIEW, ResourceType.translate("View"));
        Assert.assertEquals(VIEW, ResourceType.translate("VIEW"));
        Assert.assertEquals(VIEW, ResourceType.translate(" VIEW "));
        Assert.assertEquals(VIEW, ResourceType.translate("CAPACITY-SCHEDULER{1.0.0}"));
        Assert.assertEquals(VIEW, ResourceType.translate("ADMIN_VIEW{2.1.2}"));
        Assert.assertEquals(VIEW, ResourceType.translate("FILES{1.0.0}"));
        Assert.assertEquals(VIEW, ResourceType.translate("PIG{1.0.0}"));
        Assert.assertEquals(VIEW, ResourceType.translate("CAPACITY-SCHEDULER{1.0.0}"));
        Assert.assertEquals(VIEW, ResourceType.translate("TEZ{0.7.0.2.3.2.0-377}"));
        Assert.assertEquals(VIEW, ResourceType.translate("SLIDER{2.0.0}"));
        Assert.assertEquals(VIEW, ResourceType.translate("HIVE{1.0.0}"));
    }
}

