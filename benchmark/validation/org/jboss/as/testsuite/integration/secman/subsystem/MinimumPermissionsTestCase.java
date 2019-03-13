/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.testsuite.integration.secman.subsystem;


import java.net.URL;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This class contains test for minimum-permissions attribute in security-manager subsystem. The permissions listed in
 * minimum-permissions should be granted to all deployed applications.
 *
 * @author Josef Cacek
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MinimumPermissionsTestCase extends ReloadableCliTestBase {
    private static final String DEPLOYMENT = "deployment";

    private static Logger LOGGER = Logger.getLogger(MinimumPermissionsTestCase.class);

    @ArquillianResource
    private URL webAppURL;

    /**
     * Tests that permissions are not granted if the minimum-permissions is empty.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEmptyMinPerm() throws Exception {
        assertPropertyNonReadable();
    }
}

