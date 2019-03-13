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
package io.undertow.servlet.test.security.ssl;


import io.undertow.testutils.DefaultServer;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case to test transport-guarantee enforcement.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(DefaultServer.class)
public class SSLMetaDataTestCase {
    @Test
    public void testSessionId() throws IOException {
        internalTest("/id");
    }

    @Test
    public void testCipherSuite() throws IOException {
        internalTest("/cipher-suite");
    }

    @Test
    public void testKeySize() throws IOException {
        internalTest("/key-size");
    }

    @Test
    public void testCert() throws IOException {
        internalTest("/cert");
    }
}

