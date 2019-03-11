/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2017, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.test.integration.elytron.web;


import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.wildfly.test.integration.elytron.util.HttpUtil;
import org.wildfly.test.security.common.elytron.ElytronDomainSetup;


/**
 * Test case to test authentication to web applications, initially programatic authentication.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
abstract class WebAuthenticationTestCaseBase {
    @ArquillianResource
    protected URL url;

    @Test
    public void testProgramaticAuthentication() throws Exception {
        Map<String, String> headers = Collections.emptyMap();
        AtomicReference<String> sessionCookie = new AtomicReference<>();
        // Test Unauthenticated Call is 'null'
        String identity = HttpUtil.get(((url) + "login"), headers, 10, TimeUnit.SECONDS, sessionCookie);
        Assert.assertEquals("Expected Identity", "null", identity);
        // Test Call can login and identity established.
        identity = HttpUtil.get(((url) + "login?action=login&username=user1&password=password1"), headers, 10, TimeUnit.SECONDS, sessionCookie);
        Assert.assertEquals("Expected Identity", "user1", identity);
        // Test follow up call still has identity.
        identity = HttpUtil.get(((url) + "login"), headers, 10, TimeUnit.SECONDS, sessionCookie);
        Assert.assertEquals("Expected Identity", "user1", identity);
        // Logout and check back to 'null'.
        identity = HttpUtil.get(((url) + "login?action=logout"), headers, 10, TimeUnit.SECONDS, sessionCookie);
        Assert.assertEquals("Expected Identity", "null", identity);
        // Once more call to be sure we really are null.
        identity = HttpUtil.get(((url) + "login"), headers, 10, TimeUnit.SECONDS, sessionCookie);
        Assert.assertEquals("Expected Identity", "null", identity);
    }

    static class ElytronDomainSetupOverride extends ElytronDomainSetup {
        public ElytronDomainSetupOverride() {
            super(new File(WebAuthenticationTestCase.class.getResource("users.properties").getFile()).getAbsolutePath(), new File(WebAuthenticationTestCase.class.getResource("roles.properties").getFile()).getAbsolutePath());
        }
    }
}

