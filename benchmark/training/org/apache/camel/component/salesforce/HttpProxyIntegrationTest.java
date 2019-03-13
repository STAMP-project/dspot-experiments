/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.salesforce;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.eclipse.jetty.server.Server;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test HTTP proxy configuration for Salesforce component.
 */
@RunWith(Parameterized.class)
public class HttpProxyIntegrationTest extends AbstractSalesforceTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(HttpProxyIntegrationTest.class);

    private static final String HTTP_PROXY_HOST = "localhost";

    private static final String HTTP_PROXY_USER_NAME = "camel-user";

    private static final String HTTP_PROXY_PASSWORD = "camel-user-password";

    private static final String HTTP_PROXY_REALM = "proxy-realm";

    private static Server server;

    private static int httpProxyPort;

    private static final AtomicBoolean WENT_TORUGH_PROXY = new AtomicBoolean();

    @Parameterized.Parameter(0)
    public Consumer<SalesforceComponent> configurationMethod;

    @Test
    public void testGetVersions() throws Exception {
        doTestGetVersions("");
        doTestGetVersions("Xml");
        assertTrue("Should have gone through the test proxy", HttpProxyIntegrationTest.WENT_TORUGH_PROXY.get());
    }
}

