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


import AlertGroupResourceProvider.ALERT_GROUP_DEFINITIONS;
import AlertGroupResourceProvider.ALERT_GROUP_NAME;
import AlertGroupResourceProvider.ALERT_GROUP_TARGETS;
import Request.Type.DELETE;
import Request.Type.POST;
import Request.Type.PUT;
import Resource.Type;
import Resource.Type.AlertGroup;
import ResultStatus.STATUS;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.api.services.Request;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.audit.event.AuditEvent;
import org.apache.ambari.server.audit.event.request.AddAlertGroupRequestAuditEvent;
import org.apache.ambari.server.audit.event.request.ChangeAlertGroupRequestAuditEvent;
import org.apache.ambari.server.audit.event.request.DeleteAlertGroupRequestAuditEvent;
import org.apache.ambari.server.audit.request.eventcreator.AlertGroupEventCreator;
import org.apache.ambari.server.controller.spi.Resource;
import org.junit.Test;


public class AlertGroupEventCreatorTest extends AuditEventCreatorTestBase {
    @Test
    public void postTest() {
        AlertGroupEventCreator creator = new AlertGroupEventCreator();
        Map<String, Object> properties = new HashMap<>();
        properties.put(ALERT_GROUP_NAME, "GroupName");
        properties.put(ALERT_GROUP_DEFINITIONS, Arrays.asList("D", "E", "F", "S"));
        properties.put(ALERT_GROUP_TARGETS, Arrays.asList("T", "G", "T", "S"));
        Request request = AuditEventCreatorTestHelper.createRequest(POST, AlertGroup, properties, null);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(Alert group addition), RequestType(POST), url(http://example.com:8080/api/v1/test), ResultStatus(200 OK), Alert group name(GroupName), Definition IDs(D, E, F, S), Notification IDs(T, G, T, S)";
        Assert.assertTrue("Class mismatch", (event instanceof AddAlertGroupRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }

    @Test
    public void putTest() {
        AlertGroupEventCreator creator = new AlertGroupEventCreator();
        Map<String, Object> properties = new HashMap<>();
        properties.put(ALERT_GROUP_NAME, "GroupName");
        properties.put(ALERT_GROUP_DEFINITIONS, Arrays.asList("D", "E", "F", "S"));
        properties.put(ALERT_GROUP_TARGETS, Arrays.asList("T", "G", "T", "S"));
        Request request = AuditEventCreatorTestHelper.createRequest(PUT, AlertGroup, properties, null);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(Alert group change), RequestType(PUT), url(http://example.com:8080/api/v1/test), ResultStatus(200 OK), Alert group name(GroupName), Definition IDs(D, E, F, S), Notification IDs(T, G, T, S)";
        Assert.assertTrue("Class mismatch", (event instanceof ChangeAlertGroupRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }

    @Test
    public void deleteTest() {
        AlertGroupEventCreator creator = new AlertGroupEventCreator();
        Map<Resource.Type, String> resource = new HashMap<>();
        resource.put(AlertGroup, "999");
        Request request = AuditEventCreatorTestHelper.createRequest(DELETE, AlertGroup, null, resource);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(Alert group removal), RequestType(DELETE), url(http://example.com:8080/api/v1/test), ResultStatus(200 OK), Alert group ID(999)";
        Assert.assertTrue("Class mismatch", (event instanceof DeleteAlertGroupRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }
}

