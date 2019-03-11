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
package io.undertow.server.security;


import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A simple test case to verify a redirect works.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(DefaultServer.class)
public class SimpleConfidentialRedirectTestCase {
    @Test
    public void simpleRedirectTestCase() throws IOException, GeneralSecurityException {
        TestHttpClient client = new TestHttpClient();
        client.setSSLContext(DefaultServer.getClientSSLContext());
        try {
            sendRequest(client, "/foo");
            sendRequest(client, "/foo+bar");
            sendRequest(client, "/foo+bar;aa");
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

