/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.undertow.server.handlers.proxy.mod_cluster;


import StatusCodes.NOT_FOUND;
import StatusCodes.OK;
import StatusCodes.SERVICE_UNAVAILABLE;
import io.undertow.testutils.HttpClientUtils;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emanuel Muckenhuber
 */
public class BasicMCMPUnitTestCase extends AbstractModClusterTestBase {
    static NodeTestConfig server1;

    static NodeTestConfig server2;

    static {
        BasicMCMPUnitTestCase.server1 = NodeTestConfig.builder().setJvmRoute("s1").setType(AbstractModClusterTestBase.getType()).setHostname("localhost").setPort(((AbstractModClusterTestBase.port) + 1));
        BasicMCMPUnitTestCase.server2 = NodeTestConfig.builder().setJvmRoute("s2").setType(AbstractModClusterTestBase.getType()).setHostname("localhost").setPort(((AbstractModClusterTestBase.port) + 2));
    }

    @Test
    public void testBasic() throws IOException {
        registerNodes(false, BasicMCMPUnitTestCase.server1, BasicMCMPUnitTestCase.server2);
        AbstractModClusterTestBase.modClusterClient.updateLoad("s1", 100);
        AbstractModClusterTestBase.modClusterClient.updateLoad("s2", 1);
        AbstractModClusterTestBase.modClusterClient.enableApp("s1", "/name", "localhost", "localhost:7777");
        AbstractModClusterTestBase.modClusterClient.enableApp("s1", "/session", "localhost", "localhost:7777");
        AbstractModClusterTestBase.modClusterClient.enableApp("s2", "/name", "localhost", "localhost:7777");
        AbstractModClusterTestBase.modClusterClient.enableApp("s2", "/session", "localhost", "localhost:7777");
        // Ping
        AbstractModClusterTestBase.modClusterClient.updateLoad("s1", (-2));
        AbstractModClusterTestBase.modClusterClient.updateLoad("s2", (-2));
        for (int i = 0; i < 10; i++) {
            HttpGet get = AbstractModClusterTestBase.get("/name");
            HttpResponse result = AbstractModClusterTestBase.httpClient.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
        }
        for (int i = 0; i < 10; i++) {
            HttpGet get = AbstractModClusterTestBase.get("/session");
            HttpResponse result = AbstractModClusterTestBase.httpClient.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
        }
    }

    @Test
    public void testAppCommand() throws IOException {
        AbstractModClusterTestBase.checkGet("/name", NOT_FOUND);
        AbstractModClusterTestBase.checkGet("/session", NOT_FOUND);
        registerNodes(false, BasicMCMPUnitTestCase.server1, BasicMCMPUnitTestCase.server2);
        AbstractModClusterTestBase.checkGet("/name", NOT_FOUND);
        AbstractModClusterTestBase.checkGet("/session", NOT_FOUND);
        AbstractModClusterTestBase.modClusterClient.enableApp("s1", "/name", "localhost", "localhost:7777");
        AbstractModClusterTestBase.modClusterClient.enableApp("s1", "/session", "localhost", "localhost:7777");
        AbstractModClusterTestBase.modClusterClient.enableApp("s2", "/name", "localhost", "localhost:7777");
        AbstractModClusterTestBase.modClusterClient.enableApp("s2", "/session", "localhost", "localhost:7777");
        AbstractModClusterTestBase.checkGet("/name", SERVICE_UNAVAILABLE);
        AbstractModClusterTestBase.checkGet("/session", SERVICE_UNAVAILABLE);
        AbstractModClusterTestBase.modClusterClient.updateLoad("s1", 100);
        AbstractModClusterTestBase.modClusterClient.updateLoad("s2", 1);
        AbstractModClusterTestBase.checkGet("/name", OK);
        AbstractModClusterTestBase.checkGet("/session", OK);
    }

    @Test
    public void testErrorState() throws IOException {
        registerNodes(false, BasicMCMPUnitTestCase.server1);
        AbstractModClusterTestBase.modClusterClient.enableApp("s1", "/name", "localhost", "localhost:7777");
        AbstractModClusterTestBase.checkGet("/name", SERVICE_UNAVAILABLE);
        AbstractModClusterTestBase.modClusterClient.updateLoad("s1", 1);
        AbstractModClusterTestBase.checkGet("/name", OK);
        AbstractModClusterTestBase.modClusterClient.updateLoad("s1", (-1));
        AbstractModClusterTestBase.checkGet("/name", SERVICE_UNAVAILABLE);
        AbstractModClusterTestBase.modClusterClient.updateLoad("s1", (-2));
        AbstractModClusterTestBase.checkGet("/name", SERVICE_UNAVAILABLE);
    }

    @Test
    public void testPing() throws IOException {
        String response = AbstractModClusterTestBase.modClusterClient.ping(null, "localhost", ((AbstractModClusterTestBase.port) + 1));
        Assert.assertFalse(response.contains("NOTOK"));
        response = AbstractModClusterTestBase.modClusterClient.ping(BasicMCMPUnitTestCase.server1.getType(), "localhost", ((AbstractModClusterTestBase.port) + 1));
        Assert.assertFalse(response.contains("NOTOK"));
        response = AbstractModClusterTestBase.modClusterClient.ping(BasicMCMPUnitTestCase.server2.getType(), "localhost", ((AbstractModClusterTestBase.port) + 2));
        Assert.assertFalse(response.contains("NOTOK"));
        response = AbstractModClusterTestBase.modClusterClient.ping(null, "localhost", 0);
        Assert.assertTrue(response.contains("NOTOK"));
        response = AbstractModClusterTestBase.modClusterClient.ping("ajp", "localhost", 0);
        Assert.assertTrue(response.contains("NOTOK"));
        response = AbstractModClusterTestBase.modClusterClient.ping("http", "localhost", 0);
        Assert.assertTrue(response.contains("NOTOK"));
    }
}

