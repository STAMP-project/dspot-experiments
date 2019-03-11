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
package org.apache.ambari.server.audit.request;


import Request.Type.DELETE;
import Request.Type.GET;
import Request.Type.POST;
import Request.Type.PUT;
import ResultStatus.STATUS;
import ResultStatus.STATUS.ACCEPTED;
import ResultStatus.STATUS.OK;
import junit.framework.Assert;
import org.apache.ambari.server.api.resources.BlueprintResourceDefinition;
import org.apache.ambari.server.api.resources.HostComponentResourceDefinition;
import org.apache.ambari.server.api.services.Request;
import org.apache.ambari.server.api.services.RequestFactory;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.api.services.ResultImpl;
import org.apache.ambari.server.audit.AuditLogger;
import org.easymock.EasyMock;
import org.junit.Test;


public class RequestAuditLoggerTest {
    private static final String TEST_URI = "http://apache.org";

    private static RequestAuditLogger requestAuditLogger;

    private static AuditLogger mockAuditLogger;

    private RequestFactory requestFactory = new RequestFactory();

    @Test
    public void defaultEventCreatorPostTest() {
        testCreator(AllPostAndPutCreator.class, POST, new BlueprintResourceDefinition(), OK, null);
    }

    @Test
    public void customEventCreatorPutTest() {
        testCreator(PutHostComponentCreator.class, PUT, new HostComponentResourceDefinition(), OK, null);
    }

    @Test
    public void noCreatorForRequestTypeTest() {
        Request request = createRequest(new HostComponentResourceDefinition(), GET);
        Result result = new ResultImpl(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        try {
            createCapture();
            RequestAuditLoggerTest.requestAuditLogger.log(request, result);
            EasyMock.verify(RequestAuditLoggerTest.mockAuditLogger);
            Assert.fail("Exception is excepted to be thrown");
        } catch (AssertionError ae) {
            EasyMock.reset(RequestAuditLoggerTest.mockAuditLogger);
            EasyMock.replay(RequestAuditLoggerTest.mockAuditLogger);
        }
    }

    @Test
    public void noRequestTypeTest() {
        Request request = createRequest(new BlueprintResourceDefinition(), DELETE);
        Result result = new ResultImpl(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        try {
            createCapture();
            RequestAuditLoggerTest.requestAuditLogger.log(request, result);
            EasyMock.verify(RequestAuditLoggerTest.mockAuditLogger);
            Assert.fail("Exception is excepted to be thrown");
        } catch (AssertionError ae) {
            EasyMock.reset(RequestAuditLoggerTest.mockAuditLogger);
            EasyMock.replay(RequestAuditLoggerTest.mockAuditLogger);
        }
    }

    @Test
    public void noGetCreatorForResourceTypeTest__defaultGetCreatorUsed() {
        testCreator(AllGetCreator.class, GET, new HostComponentResourceDefinition(), ACCEPTED, null);
    }
}

