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


import Request.Type.PUT;
import Resource.Type.ViewPrivilege;
import ResultStatus.STATUS;
import ViewPrivilegeResourceProvider.INSTANCE_NAME;
import ViewPrivilegeResourceProvider.PERMISSION_NAME;
import ViewPrivilegeResourceProvider.PRINCIPAL_NAME;
import ViewPrivilegeResourceProvider.PRINCIPAL_TYPE;
import ViewPrivilegeResourceProvider.VERSION;
import ViewPrivilegeResourceProvider.VIEW_NAME;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.api.services.NamedPropertySet;
import org.apache.ambari.server.api.services.Request;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.audit.event.AuditEvent;
import org.apache.ambari.server.audit.event.request.ViewPrivilegeChangeRequestAuditEvent;
import org.apache.ambari.server.audit.request.eventcreator.ViewPrivilegeEventCreator;
import org.junit.Test;


public class ViewPrivilegeEventCreatorTest extends AuditEventCreatorTestBase {
    @Test
    public void putTest() {
        ViewPrivilegeEventCreator creator = new ViewPrivilegeEventCreator();
        Map<String, Object> props = new HashMap<>();
        props.put(VIEW_NAME, "MyView");
        props.put(VERSION, "MyView");
        props.put(INSTANCE_NAME, "MyView");
        Map<String, Object> properties = new HashMap<>();
        properties.put(PRINCIPAL_TYPE, "USER");
        properties.put(PERMISSION_NAME, "Permission1");
        properties.put(PRINCIPAL_NAME, AuditEventCreatorTestBase.userName);
        Map<String, Object> properties2 = new HashMap<>();
        properties2.put(PRINCIPAL_TYPE, "USER");
        properties2.put(PERMISSION_NAME, "Permission2");
        properties2.put(PRINCIPAL_NAME, ((AuditEventCreatorTestBase.userName) + "2"));
        Map<String, Object> properties3 = new HashMap<>();
        properties3.put(PRINCIPAL_TYPE, "GROUP");
        properties3.put(PERMISSION_NAME, "Permission1");
        properties3.put(PRINCIPAL_NAME, "testgroup");
        NamedPropertySet nps = new NamedPropertySet("1", properties);
        NamedPropertySet nps2 = new NamedPropertySet("2", properties2);
        NamedPropertySet nps3 = new NamedPropertySet("3", properties3);
        Request request = AuditEventCreatorTestHelper.createRequest(PUT, ViewPrivilege, props, null);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        request.getBody().addPropertySet(nps);
        request.getBody().addPropertySet(nps2);
        request.getBody().addPropertySet(nps3);
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = (((((("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(View permission change), RequestType(PUT), url(http://example.com:8080/api/v1/test), ResultStatus(200 OK), Type(MyView), Version(MyView), Name(MyView), Permissions(\n") + "Permission1: \n") + "  Users: testuser\n") + "  Groups: testgroup\n") + "Permission2: \n") + "  Users: testuser2)";
        Assert.assertTrue("Class mismatch", (event instanceof ViewPrivilegeChangeRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }
}

