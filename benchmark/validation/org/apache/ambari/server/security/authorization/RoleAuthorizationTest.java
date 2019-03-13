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


import RoleAuthorization.AMBARI_ADD_DELETE_CLUSTERS;
import RoleAuthorization.CLUSTER_VIEW_METRICS;
import RoleAuthorization.HOST_VIEW_METRICS;
import RoleAuthorization.SERVICE_VIEW_METRICS;
import RoleAuthorization.VIEW_USE;
import org.junit.Assert;
import org.junit.Test;


public class RoleAuthorizationTest {
    @Test
    public void testTranslate() throws Exception {
        Assert.assertEquals(VIEW_USE, RoleAuthorization.translate("VIEW.USE"));
        Assert.assertEquals(SERVICE_VIEW_METRICS, RoleAuthorization.translate("SERVICE.VIEW_METRICS"));
        Assert.assertEquals(HOST_VIEW_METRICS, RoleAuthorization.translate("HOST.VIEW_METRICS"));
        Assert.assertEquals(CLUSTER_VIEW_METRICS, RoleAuthorization.translate("CLUSTER.VIEW_METRICS"));
        Assert.assertEquals(AMBARI_ADD_DELETE_CLUSTERS, RoleAuthorization.translate("AMBARI.ADD_DELETE_CLUSTERS"));
    }
}

