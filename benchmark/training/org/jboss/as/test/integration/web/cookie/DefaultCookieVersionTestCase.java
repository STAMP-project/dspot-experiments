/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.integration.web.cookie;


import java.net.URL;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.shared.SnapshotRestoreSetupTask;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case for default cookie version configuration.
 *
 * @author Stuart Douglas
 * @author Jan Stourac
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(SnapshotRestoreSetupTask.class)
public class DefaultCookieVersionTestCase {
    @ArquillianResource(SimpleCookieServlet.class)
    protected URL cookieURL;

    private static String DEF_SERVLET_ADDR = "/subsystem=undertow/servlet-container=default";

    @ContainerResource
    private static ManagementClient managementClient;

    @Test
    public void testDefaultCookieVersion0() throws Exception {
        commonDefaultCookieVersion(0);
    }

    @Test
    public void testDefaultCookieVersion1() throws Exception {
        commonDefaultCookieVersion(1);
    }

    @Test
    public void testSendCookieVersion0() throws Exception {
        commonSendCookieVersion(0);
    }

    @Test
    public void testSendCookieVersion1() throws Exception {
        commonSendCookieVersion(1);
    }
}

