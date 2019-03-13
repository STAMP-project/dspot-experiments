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


import HostComponentResourceProvider.CLUSTER_NAME;
import HostComponentResourceProvider.COMPONENT_NAME;
import HostComponentResourceProvider.HOST_NAME;
import HostComponentResourceProvider.MAINTENANCE_STATE;
import HostComponentResourceProvider.STATE;
import Request.Type.DELETE;
import Request.Type.POST;
import Request.Type.PUT;
import RequestOperationLevel.OPERATION_CLUSTER_ID;
import RequestOperationLevel.OPERATION_HOST_NAME;
import RequestOperationLevel.OPERATION_LEVEL_ID;
import RequestOperationLevel.OPERATION_SERVICE_ID;
import Resource.Type;
import Resource.Type.HostComponent;
import ResultStatus.STATUS;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.api.services.Request;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.api.util.TreeNode;
import org.apache.ambari.server.audit.event.AuditEvent;
import org.apache.ambari.server.audit.event.request.StartOperationRequestAuditEvent;
import org.apache.ambari.server.audit.request.eventcreator.ComponentEventCreator;
import org.apache.ambari.server.controller.spi.Resource;
import org.junit.Test;


public class ComponentEventCreatorTest extends AuditEventCreatorTestBase {
    @Test
    public void deleteTest() {
        ComponentEventCreator creator = new ComponentEventCreator();
        Map<String, Object> properties = new HashMap<>();
        properties.put(HOST_NAME, "ambari1.example.com");
        Map<Resource.Type, String> resource = new HashMap<>();
        resource.put(HostComponent, "MyComponent");
        Request request = AuditEventCreatorTestHelper.createRequest(DELETE, HostComponent, properties, resource);
        TreeNode<Resource> resultTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, null, null);
        addRequestId(resultTree, 1L);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK), resultTree);
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(Delete component MyComponent), Host name(ambari1.example.com), RequestId(1), Status(Successfully queued)";
        Assert.assertTrue("Class mismatch", (event instanceof StartOperationRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }

    @Test
    public void putForAllHostsTest() {
        allHostsTest(PUT);
    }

    @Test
    public void postForAllHostsTest() {
        allHostsTest(POST);
    }

    @Test
    public void hostTest() {
        ComponentEventCreator creator = new ComponentEventCreator();
        Map<String, Object> properties = new HashMap<>();
        properties.put(HOST_NAME, "ambari1.example.com");
        properties.put(CLUSTER_NAME, "mycluster");
        properties.put(STATE, "STARTED");
        Request request = AuditEventCreatorTestHelper.createRequest(PUT, HostComponent, properties, null);
        request.getBody().addRequestInfoProperty(OPERATION_LEVEL_ID, "HOST");
        request.getBody().addRequestInfoProperty(OPERATION_HOST_NAME, "ambari1.example.com");
        request.getBody().addRequestInfoProperty(OPERATION_CLUSTER_ID, "mycluster");
        request.getBody().addRequestInfoProperty("query", "host_component.in(MYCOMPONENT,MYCOMPONENT2)");
        TreeNode<Resource> resultTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, null, null);
        addRequestId(resultTree, 1L);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK), resultTree);
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(STARTED: MYCOMPONENT,MYCOMPONENT2 on ambari1.example.com (mycluster)), Host name(ambari1.example.com), RequestId(1), Status(Successfully queued)";
        Assert.assertTrue("Class mismatch", (event instanceof StartOperationRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }

    @Test
    public void hostComponentTest() {
        ComponentEventCreator creator = new ComponentEventCreator();
        Map<String, Object> properties = new HashMap<>();
        properties.put(HOST_NAME, "ambari1.example.com");
        properties.put(CLUSTER_NAME, "mycluster");
        properties.put(STATE, "STARTED");
        properties.put(COMPONENT_NAME, "MYCOMPONENT");
        Request request = AuditEventCreatorTestHelper.createRequest(PUT, HostComponent, properties, null);
        request.getBody().addRequestInfoProperty(OPERATION_LEVEL_ID, "HOST_COMPONENT");
        request.getBody().addRequestInfoProperty(OPERATION_SERVICE_ID, "MYSERVICE");
        request.getBody().addRequestInfoProperty(OPERATION_HOST_NAME, "ambari1.example.com");
        request.getBody().addRequestInfoProperty(OPERATION_CLUSTER_ID, "mycluster");
        TreeNode<Resource> resultTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, null, null);
        addRequestId(resultTree, 1L);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK), resultTree);
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(STARTED: MYCOMPONENT/MYSERVICE on ambari1.example.com (mycluster)), Host name(ambari1.example.com), RequestId(1), Status(Successfully queued)";
        Assert.assertTrue("Class mismatch", (event instanceof StartOperationRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }

    @Test
    public void maintenanceModeTest() {
        ComponentEventCreator creator = new ComponentEventCreator();
        Map<String, Object> properties = new HashMap<>();
        properties.put(HOST_NAME, "ambari1.example.com");
        properties.put(MAINTENANCE_STATE, "ON");
        properties.put(COMPONENT_NAME, "MYCOMPONENT");
        Request request = AuditEventCreatorTestHelper.createRequest(PUT, HostComponent, properties, null);
        TreeNode<Resource> resultTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, null, null);
        addRequestId(resultTree, 1L);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK), resultTree);
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(Turn ON Maintenance Mode for MYCOMPONENT), Host name(ambari1.example.com), RequestId(1), Status(Successfully queued)";
        Assert.assertTrue("Class mismatch", (event instanceof StartOperationRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }

    @Test
    public void failureTest() {
        ComponentEventCreator creator = new ComponentEventCreator();
        Map<String, Object> properties = new HashMap<>();
        properties.put(HOST_NAME, "ambari1.example.com");
        properties.put(MAINTENANCE_STATE, "ON");
        properties.put(COMPONENT_NAME, "MYCOMPONENT");
        Request request = AuditEventCreatorTestHelper.createRequest(PUT, HostComponent, properties, null);
        TreeNode<Resource> resultTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, null, null);
        addRequestId(resultTree, 1L);
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.BAD_REQUEST, "Failed for testing"), resultTree);
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(Turn ON Maintenance Mode for MYCOMPONENT), Host name(ambari1.example.com), RequestId(1), Status(Failed to queue), Reason(Failed for testing)";
        Assert.assertTrue("Class mismatch", (event instanceof StartOperationRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }
}

