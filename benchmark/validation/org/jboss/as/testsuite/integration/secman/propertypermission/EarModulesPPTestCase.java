/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.jboss.as.testsuite.integration.secman.propertypermission;


import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case, which checks PropertyPermissions assigned to sub-deployment of deployed ear applications. The applications try to
 * do a protected action and it should either complete successfully if {@link java.util.PropertyPermission} is granted, or fail.
 *
 * @author Ondrej Lukas
 * @author Josef Cacek
 */
@RunWith(Arquillian.class)
@ServerSetup(SystemPropertiesSetup.class)
@RunAsClient
public class EarModulesPPTestCase extends AbstractPPTestsWithJSP {
    private static final String EJBAPP_BASE_NAME = "ejb-module";

    private static final String WEBAPP_BASE_NAME = "web-module";

    private static final String APP_NO_PERM = "read-props-noperm";

    private static final String APP_EMPTY_PERM = "read-props-emptyperm";

    private static Logger LOGGER = Logger.getLogger(EarModulesPPTestCase.class);

    @ArquillianResource
    private InitialContext iniCtx;

    /**
     * Check standard java property access for EJB in ear, where PropertyPermission for all properties is granted.
     *
     * @param webAppURL
     * 		
     * @throws Exception
     * 		
     */
    @Test
    @OperateOnDeployment(AbstractPropertyPermissionTests.APP_GRANT)
    public void testJavaHomePropertyEjbInJarGrant() throws Exception {
        checkJavaHomePropertyEjb(AbstractPropertyPermissionTests.APP_GRANT, false);
    }

    /**
     * Check standard java property access for EJB in ear, where not all PropertyPermissions are granted.
     *
     * @param webAppURL
     * 		
     * @throws Exception
     * 		
     */
    @Test
    @OperateOnDeployment(AbstractPropertyPermissionTests.APP_LIMITED)
    public void testJavaHomePropertyEjbInJarLimited() throws Exception {
        checkJavaHomePropertyEjb(AbstractPropertyPermissionTests.APP_LIMITED, false);
    }

    /**
     * Check standard java property access for EJB in ear, where no PropertyPermission is granted.
     *
     * @param webAppURL
     * 		
     * @throws Exception
     * 		
     */
    @Test
    @OperateOnDeployment(AbstractPropertyPermissionTests.APP_DENY)
    public void testJavaHomePropertyEjbInJarDeny() throws Exception {
        checkJavaHomePropertyEjb(AbstractPropertyPermissionTests.APP_DENY, true);
    }

    /**
     * Check standard java property access for EJB in ear, where PropertyPermission for all properties is granted.
     *
     * @param webAppURL
     * 		
     * @throws Exception
     * 		
     */
    @Test
    @OperateOnDeployment(AbstractPropertyPermissionTests.APP_GRANT)
    public void testASLevelPropertyEjbInJarGrant() throws Exception {
        checkTestPropertyEjb(AbstractPropertyPermissionTests.APP_GRANT, false);
    }

    /**
     * Check standard java property access for EJB in ear, where not all PropertyPermissions are granted.
     *
     * @param webAppURL
     * 		
     * @throws Exception
     * 		
     */
    @Test
    @OperateOnDeployment(AbstractPropertyPermissionTests.APP_LIMITED)
    public void testASLevelPropertyEjbInJarLimited() throws Exception {
        checkTestPropertyEjb(AbstractPropertyPermissionTests.APP_LIMITED, true);
    }

    /**
     * Check standard java property access for EJB in ear, where no PropertyPermission is granted.
     *
     * @param webAppURL
     * 		
     * @throws Exception
     * 		
     */
    @Test
    @OperateOnDeployment(AbstractPropertyPermissionTests.APP_DENY)
    public void testASLevelPropertyEjbInJarDeny() throws Exception {
        checkTestPropertyEjb(AbstractPropertyPermissionTests.APP_DENY, true);
    }

    /**
     * Check permission.xml overrides in ear deployments.
     */
    @Test
    @OperateOnDeployment(EarModulesPPTestCase.APP_NO_PERM)
    public void testASLevelPropertyEjbInJarNoPerm() throws Exception {
        checkTestPropertyEjb(EarModulesPPTestCase.APP_NO_PERM, true);
    }

    /**
     * Check permission.xml overrides in ear deployments.
     */
    @Test
    @OperateOnDeployment(EarModulesPPTestCase.APP_EMPTY_PERM)
    public void testASLevelPropertyEjbInJarEmptyPerm() throws Exception {
        checkTestPropertyEjb(EarModulesPPTestCase.APP_EMPTY_PERM, true);
    }
}

