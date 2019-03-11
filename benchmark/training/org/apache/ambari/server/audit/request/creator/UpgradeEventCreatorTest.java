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
package org.apache.ambari.server.audit.request.creator;


import Request.Type.POST;
import Resource.Type.Upgrade;
import ResultStatus.STATUS;
import UpgradeResourceProvider.UPGRADE_CLUSTER_NAME;
import UpgradeResourceProvider.UPGRADE_REPO_VERSION_ID;
import UpgradeResourceProvider.UPGRADE_TYPE;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.api.services.Request;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.audit.event.AuditEvent;
import org.apache.ambari.server.audit.event.request.AddUpgradeRequestAuditEvent;
import org.apache.ambari.server.audit.request.eventcreator.UpgradeEventCreator;
import org.junit.Test;


public class UpgradeEventCreatorTest extends AuditEventCreatorTestBase {
    @Test
    public void postTest() {
        UpgradeEventCreator creator = new UpgradeEventCreator();
        Map<String, Object> properties = new HashMap<>();
        properties.put(UPGRADE_REPO_VERSION_ID, "1234");
        properties.put(UPGRADE_TYPE, "ROLLING");
        properties.put(UPGRADE_CLUSTER_NAME, "mycluster");
        Request request = AuditEventCreatorTestHelper.createRequest(POST, Upgrade, properties, null);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(Upgrade addition), RequestType(POST), url(http://example.com:8080/api/v1/test), ResultStatus(200 OK), Repository version ID(1234), Upgrade type(ROLLING), Cluster name(mycluster)";
        Assert.assertTrue("Class mismatch", (event instanceof AddUpgradeRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }
}

