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


import EventType.AUTHENTICATED;
import EventType.FAILED_AUTHENTICATION;
import io.undertow.testutils.DefaultServer;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A test case to test when the only authentication mechanism is the BASIC mechanism.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(DefaultServer.class)
public class BasicAuthenticationTestCase extends AuthenticationTestBase {
    @Test
    public void testBasicSuccess() throws Exception {
        BasicAuthenticationTestCase._testBasicSuccess();
        AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
    }

    @Test
    public void testBadUserName() throws Exception {
        BasicAuthenticationTestCase._testBadUserName();
        AuthenticationTestBase.assertSingleNotificationType(FAILED_AUTHENTICATION);
    }

    @Test
    public void testBadPassword() throws Exception {
        BasicAuthenticationTestCase._testBadPassword();
        AuthenticationTestBase.assertSingleNotificationType(FAILED_AUTHENTICATION);
    }
}

