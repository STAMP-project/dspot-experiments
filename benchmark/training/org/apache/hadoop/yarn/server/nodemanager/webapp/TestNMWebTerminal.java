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
package org.apache.hadoop.yarn.server.nodemanager.webapp;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import java.io.File;
import javax.ws.rs.core.MediaType;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.yarn.server.nodemanager.NodeHealthCheckerService;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for hosting web terminal servlet in node manager.
 */
public class TestNMWebTerminal {
    private static final File TESTROOTDIR = new File("target", TestNMWebServer.class.getSimpleName());

    private static final File TESTLOGDIR = new File("target", ((TestNMWebServer.class.getSimpleName()) + "LogDir"));

    private NodeHealthCheckerService healthChecker;

    private WebServer server;

    private int port;

    @Test
    public void testWebTerminal() {
        Client client = Client.create();
        Builder builder = client.resource((("http://127.0.0.1:" + (port)) + "/terminal/terminal.template")).accept("text/html");
        ClientResponse response = builder.get(ClientResponse.class);
        Assert.assertEquals((((MediaType.TEXT_HTML) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
    }
}

