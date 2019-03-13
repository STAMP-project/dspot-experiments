/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.webapp;


import LogWebService.AppInfo;
import LogWebService.ContainerInfo;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * test class for log web service.
 */
public class TestLogWebService {
    private HttpServletRequest request;

    private TestLogWebService.LogWebServiceTest logWebService;

    private static TimelineEntity entity;

    private ApplicationId appId;

    private ContainerId cId;

    private String user = "user1";

    private Map<String, TimelineEntity> entities;

    private String nodeHttpAddress = "localhost:0";

    @Test
    public void testGetApp() {
        LogWebService.AppInfo app = logWebService.getApp(request, appId.toString(), null);
        Assert.assertEquals("RUNNING", app.getAppState().toString());
        Assert.assertEquals(user, app.getUser());
    }

    @Test
    public void testGetContainer() {
        LogWebService.ContainerInfo container = logWebService.getContainer(request, appId.toString(), cId.toString(), null);
        Assert.assertEquals(nodeHttpAddress, container.getNodeHttpAddress());
    }

    class LogWebServiceTest extends LogWebService {
        @Override
        protected TimelineEntity getEntity(String path, MultivaluedMap<String, String> params) throws IOException {
            if (path.endsWith(cId.toString())) {
                return entities.get(cId.toString());
            } else
                if (path.endsWith(appId.toString())) {
                    return entities.get(appId.toString());
                } else {
                    throw new IOException();
                }

        }
    }
}

