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
package io.undertow.servlet.test.security.digest;


import io.undertow.testutils.DefaultServer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case to test authentication using HTTP Digest.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(DefaultServer.class)
public class DigestAuthTestCase {
    private static final String REALM_NAME = "Servlet_Realm";

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    @Test
    public void testUserName() throws Exception {
        testCall("username", "user1");
    }

    @Test
    public void testAuthType() throws Exception {
        testCall("authType", "DIGEST");
    }
}

