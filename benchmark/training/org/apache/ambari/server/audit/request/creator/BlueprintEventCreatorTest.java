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


import Request.Type.DELETE;
import Request.Type.POST;
import Resource.Type;
import Resource.Type.Blueprint;
import ResultStatus.STATUS;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.api.services.Request;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.audit.event.AuditEvent;
import org.apache.ambari.server.audit.event.request.AddBlueprintRequestAuditEvent;
import org.apache.ambari.server.audit.event.request.DeleteBlueprintRequestAuditEvent;
import org.apache.ambari.server.audit.request.eventcreator.BlueprintEventCreator;
import org.apache.ambari.server.controller.spi.Resource;
import org.junit.Test;


public class BlueprintEventCreatorTest extends AuditEventCreatorTestBase {
    @Test
    public void postTest() {
        BlueprintEventCreator creator = new BlueprintEventCreator();
        Map<Resource.Type, String> resource = new HashMap<>();
        resource.put(Blueprint, "myBluePrint");
        Request request = AuditEventCreatorTestHelper.createRequest(POST, Blueprint, null, resource);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(Upload blueprint), RequestType(POST), url(http://example.com:8080/api/v1/test), ResultStatus(200 OK), Blueprint name(myBluePrint)";
        Assert.assertTrue("Class mismatch", (event instanceof AddBlueprintRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }

    @Test
    public void deleteTest() {
        BlueprintEventCreator creator = new BlueprintEventCreator();
        Map<Resource.Type, String> resource = new HashMap<>();
        resource.put(Blueprint, "myBluePrint");
        Request request = AuditEventCreatorTestHelper.createRequest(DELETE, Blueprint, null, resource);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(Delete blueprint), RequestType(DELETE), url(http://example.com:8080/api/v1/test), ResultStatus(200 OK), Blueprint name(myBluePrint)";
        Assert.assertTrue("Class mismatch", (event instanceof DeleteBlueprintRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }
}

