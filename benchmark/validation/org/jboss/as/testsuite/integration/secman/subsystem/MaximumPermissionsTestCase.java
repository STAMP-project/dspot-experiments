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


import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This class contains test for maximum-permissions attribute in security-manager subsystem. The deployment should failed if the
 * deployed application asks more permissions than is allowed by the maximum-permissions.
 *
 * @author Josef Cacek
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(RemoveDeploymentPermissionsServerSetupTask.class)
public class MaximumPermissionsTestCase extends ReloadableCliTestBase {
    private static final String DEPLOYMENT_PERM = "deployment-perm";

    private static final String DEPLOYMENT_JBOSS_PERM = "deployment-jboss-perm";

    private static final String DEPLOYMENT_NO_PERM = "deployment-no-perm";

    private static Logger LOGGER = Logger.getLogger(MaximumPermissionsTestCase.class);

    private static final String ADDRESS_WEB = ("http://" + (TestSuiteEnvironment.getServerAddress())) + ":8080/";

    private static final String INDEX_HTML = "OK";

    @ArquillianResource
    private Deployer deployer;

    /**
     * Tests if deployment fails
     * <ul>
     * <li>when maximum-permissions is not defined and {@code permissions.xml} requests some permissions;</li>
     * <li>when maximum-permissions is not defined and {@code jboss-permissions.xml} requests some permissions.</li>
     * </ul>
     */
    @Test
    public void testMaximumPermissionsEmpty() throws Exception {
        try {
            doCliOperation("/subsystem=security-manager/deployment-permissions=default:add(maximum-permissions=[])");
            reloadServer();
            assertNotDeployable(MaximumPermissionsTestCase.DEPLOYMENT_PERM);
            assertNotDeployable(MaximumPermissionsTestCase.DEPLOYMENT_JBOSS_PERM);
        } finally {
            doCliOperationWithoutChecks("/subsystem=security-manager/deployment-permissions=default:remove()");
            reloadServer();
        }
    }

    /**
     * Tests if deployment succeeds but doing protected action fails, when maximum-permissions is not defined and requested
     * permissions declaration is not part of deployment.
     */
    @Test
    public void testNoPermEmptySet() throws Exception {
        assertPropertyNonReadable(MaximumPermissionsTestCase.DEPLOYMENT_NO_PERM);
    }
}

