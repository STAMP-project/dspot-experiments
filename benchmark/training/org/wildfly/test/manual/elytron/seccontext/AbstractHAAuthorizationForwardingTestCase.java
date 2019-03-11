/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat Middleware LLC, and individual contributors
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
package org.wildfly.test.manual.elytron.seccontext;


import java.net.URL;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests authorization forwarding within a cluster.
 *
 * <h3>Given</h3>
 * See the superclass for common implementation details.
 * <pre>
 * Additional started and configured servers:
 * - seccontext-server1-backup (standalone-ha.xml - creates cluster with seccontext-server1) -
 *   * entry-servlet-form.war
 * </pre>
 *
 * @author Josef Cacek
 */
public abstract class AbstractHAAuthorizationForwardingTestCase extends AbstractSecurityContextPropagationTestBase {
    private static final AbstractSecurityContextPropagationTestBase.ServerHolder server1backup = new AbstractSecurityContextPropagationTestBase.ServerHolder(SeccontextUtil.SERVER1_BACKUP, TestSuiteEnvironment.getServerAddress(), 2000);

    /**
     * Verifies, the distributable web-app with FORM authentication supports session replication out of the box.
     *
     * <pre>
     * When: HTTP client calls WhoAmIServlet as "admin" (using FORM authn) on first cluster node and then
     *       it calls WhoAmIServlet (without authentication needed) on the second cluster node
     * Then: the call to WhoAmIServlet on second node (without authentication) passes and returns "admin"
     *       (i.e. SSO works with FORM authentication)
     * </pre>
     */
    @Test
    public void testServletSso() throws Exception {
        final URL whoamiUrl = new URL(((((AbstractSecurityContextPropagationTestBase.server1.getApplicationHttpUrl()) + "/") + (SeccontextUtil.WAR_ENTRY_SERVLET_FORM)) + (WhoAmIServlet.SERVLET_PATH)));
        final URL whoamiBackupUrl = new URL(((((AbstractHAAuthorizationForwardingTestCase.server1backup.getApplicationHttpUrl()) + "/") + (SeccontextUtil.WAR_ENTRY_SERVLET_FORM)) + (WhoAmIServlet.SERVLET_PATH)));
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(Utils.REDIRECT_STRATEGY).build()) {
            Assert.assertEquals("Unexpected result from WhoAmIServlet", "admin", doHttpRequestFormAuthn(httpClient, whoamiUrl, true, "admin", "admin", SC_OK));
            Assert.assertEquals("Unexpected result from WhoAmIServlet (backup-server)", "admin", doHttpRequest(httpClient, whoamiBackupUrl, SC_OK));
        }
    }

    /**
     * Verifies, the authorization forwarding works within cluster (FORM authn). This simulates failover on
     * distributed web application (e.g. when load balancer is used).
     *
     * <pre>
     * When: HTTP client calls WhoAmIServlet as "admin" (using FORM authn) on second cluster node and then
     *       it calls EntryServlet (without authentication needed) on the first cluster node;
     *       the EntryServlet uses Elytron API to forward authz name to call remote WhoAmIBean
     * Then: the calls pass and WhoAmIBean returns "admin" username
     * </pre>
     */
    @Test
    public void testServletSsoPropagation() throws Exception {
        final URL entryServletUrl = getEntryServletUrl(SeccontextUtil.WAR_ENTRY_SERVLET_FORM, "server", "server", ReAuthnType.FORWARDED_AUTHORIZATION);
        final URL whoamiUrl = new URL(((((AbstractHAAuthorizationForwardingTestCase.server1backup.getApplicationHttpUrl()) + "/") + (SeccontextUtil.WAR_ENTRY_SERVLET_FORM)) + (WhoAmIServlet.SERVLET_PATH)));
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(Utils.REDIRECT_STRATEGY).build()) {
            Assert.assertEquals("Unexpected result from WhoAmIServlet (backup-server)", "admin", doHttpRequestFormAuthn(httpClient, whoamiUrl, true, "admin", "admin", SC_OK));
            Assert.assertEquals("Unexpected result from EntryServlet", "admin", doHttpRequest(httpClient, entryServletUrl, SC_OK));
        }
    }
}

