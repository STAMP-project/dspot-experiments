/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.audit.request;


import Request.Type.POST;
import ResultStatus.STATUS;
import java.util.HashMap;
import junit.framework.Assert;
import org.apache.ambari.server.api.resources.HostComponentResourceDefinition;
import org.apache.ambari.server.api.resources.ResourceInstance;
import org.apache.ambari.server.api.services.LocalUriInfo;
import org.apache.ambari.server.api.services.Request;
import org.apache.ambari.server.api.services.RequestBody;
import org.apache.ambari.server.api.services.RequestFactory;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.api.services.ResultImpl;
import org.apache.ambari.server.audit.request.eventcreator.DefaultEventCreator;
import org.junit.Test;


public class DefaultEventCreatorTest {
    private DefaultEventCreator defaultEventCreator;

    private RequestFactory requestFactory = new RequestFactory();

    @Test
    public void defaultEventCreatorTest__okWithMessage() {
        ResourceInstance resource = new org.apache.ambari.server.api.query.QueryImpl(new HashMap(), new HostComponentResourceDefinition(), null);
        Request request = requestFactory.createRequest(null, new RequestBody(), new LocalUriInfo("http://apache.org"), POST, resource);
        Result result = new ResultImpl(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK, "message"));
        String actual = defaultEventCreator.createAuditEvent(request, result).getAuditMessage();
        String expected = "User(testuser), RemoteIp(1.2.3.4), RequestType(POST), url(http://apache.org), ResultStatus(200 OK)";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void defaultEventCreatorTest__errorWithMessage() {
        ResourceInstance resource = new org.apache.ambari.server.api.query.QueryImpl(new HashMap(), new HostComponentResourceDefinition(), null);
        Request request = requestFactory.createRequest(null, new RequestBody(), new LocalUriInfo("http://apache.org"), POST, resource);
        Result result = new ResultImpl(new org.apache.ambari.server.api.services.ResultStatus(STATUS.BAD_REQUEST, "message"));
        String actual = defaultEventCreator.createAuditEvent(request, result).getAuditMessage();
        String expected = "User(testuser), RemoteIp(1.2.3.4), RequestType(POST), url(http://apache.org), ResultStatus(400 Bad Request), Reason(message)";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void defaultEventCreatorTest__okWithoutMessage() {
        ResourceInstance resource = new org.apache.ambari.server.api.query.QueryImpl(new HashMap(), new HostComponentResourceDefinition(), null);
        Request request = requestFactory.createRequest(null, new RequestBody(), new LocalUriInfo("http://apache.org"), POST, resource);
        Result result = new ResultImpl(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        String actual = defaultEventCreator.createAuditEvent(request, result).getAuditMessage();
        String expected = "User(testuser), RemoteIp(1.2.3.4), RequestType(POST), url(http://apache.org), ResultStatus(200 OK)";
        Assert.assertEquals(expected, actual);
    }
}

