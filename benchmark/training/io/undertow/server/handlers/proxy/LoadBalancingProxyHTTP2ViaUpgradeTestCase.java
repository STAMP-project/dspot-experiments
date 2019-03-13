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
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server.handlers.proxy;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the load balancing proxy
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class LoadBalancingProxyHTTP2ViaUpgradeTestCase extends AbstractLoadBalancingProxyTestCase {
    @Test
    public void testHeadersAreLowercase() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/name"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            Header header = result.getFirstHeader("x-custom-header");
            Assert.assertEquals("x-custom-header", header.getName());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

