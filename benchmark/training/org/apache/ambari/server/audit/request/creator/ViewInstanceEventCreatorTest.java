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
import Request.Type.PUT;
import Resource.Type;
import Resource.Type.View;
import Resource.Type.ViewInstance;
import Resource.Type.ViewVersion;
import ResultStatus.STATUS;
import ViewInstanceResourceProvider.DESCRIPTION;
import ViewInstanceResourceProvider.INSTANCE_NAME;
import ViewInstanceResourceProvider.LABEL;
import ViewInstanceResourceProvider.VERSION;
import ViewInstanceResourceProvider.VIEW_NAME;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.api.services.Request;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.audit.event.AuditEvent;
import org.apache.ambari.server.audit.event.request.AddViewInstanceRequestAuditEvent;
import org.apache.ambari.server.audit.event.request.ChangeViewInstanceRequestAuditEvent;
import org.apache.ambari.server.audit.event.request.DeleteViewInstanceRequestAuditEvent;
import org.apache.ambari.server.audit.request.eventcreator.ViewInstanceEventCreator;
import org.apache.ambari.server.controller.spi.Resource;
import org.junit.Test;


public class ViewInstanceEventCreatorTest extends AuditEventCreatorTestBase {
    @Test
    public void postTest() {
        ViewInstanceEventCreator creator = new ViewInstanceEventCreator();
        Map<String, Object> properties = new HashMap<>();
        properties.put(VIEW_NAME, "MyView");
        properties.put(VERSION, "1.9");
        properties.put(INSTANCE_NAME, "MyViewInstance");
        properties.put(LABEL, "MyViewLabel");
        properties.put(DESCRIPTION, "Test view");
        Request request = AuditEventCreatorTestHelper.createRequest(POST, ViewInstance, properties, null);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(View addition), RequestType(POST), url(http://example.com:8080/api/v1/test), ResultStatus(200 OK), Type(MyView), Version(1.9), Name(MyViewInstance), Display name(MyViewLabel), Description(Test view)";
        Assert.assertTrue("Class mismatch", (event instanceof AddViewInstanceRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }

    @Test
    public void putTest() {
        ViewInstanceEventCreator creator = new ViewInstanceEventCreator();
        Map<String, Object> properties = new HashMap<>();
        properties.put(VIEW_NAME, "MyView");
        properties.put(VERSION, "1.9");
        properties.put(INSTANCE_NAME, "MyViewInstance");
        properties.put(LABEL, "MyViewLabel");
        properties.put(DESCRIPTION, "Test view");
        Request request = AuditEventCreatorTestHelper.createRequest(PUT, ViewInstance, properties, null);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(View change), RequestType(PUT), url(http://example.com:8080/api/v1/test), ResultStatus(200 OK), Type(MyView), Version(1.9), Name(MyViewInstance), Display name(MyViewLabel), Description(Test view)";
        Assert.assertTrue("Class mismatch", (event instanceof ChangeViewInstanceRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }

    @Test
    public void deleteTest() {
        ViewInstanceEventCreator creator = new ViewInstanceEventCreator();
        Map<Resource.Type, String> resource = new HashMap<>();
        resource.put(View, "MyView");
        resource.put(ViewVersion, "1.2");
        resource.put(ViewInstance, "MyViewInstance");
        Request request = AuditEventCreatorTestHelper.createRequest(DELETE, ViewInstance, null, resource);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(View deletion), RequestType(DELETE), url(http://example.com:8080/api/v1/test), ResultStatus(200 OK), Type(MyView), Version(1.2), Name(MyViewInstance)";
        Assert.assertTrue("Class mismatch", (event instanceof DeleteViewInstanceRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }
}

