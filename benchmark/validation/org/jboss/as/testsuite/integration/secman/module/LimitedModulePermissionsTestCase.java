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
package org.jboss.as.testsuite.integration.secman.module;


import java.security.AccessControlException;
import java.security.Permission;
import java.util.PropertyPermission;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case, which checks if limited permissions set defined in module.xml is used for an installed module.
 *
 * @author Josef Cacek
 */
@RunWith(Arquillian.class)
@ServerSetup(LimitedModulePermissionsTestCase.CustomModuleSetup.class)
public class LimitedModulePermissionsTestCase {
    /**
     * Test which reads system property without Permission.
     */
    @Test
    public void testReadJavaHome() {
        try {
            CheckJSMUtils.getSystemProperty("java.home");
            Assert.fail("Access should be denied");
        } catch (AccessControlException e) {
            Permission expectedPerm = new PropertyPermission("java.home", "read");
            Assert.assertEquals("Permission type doesn't match", expectedPerm, e.getPermission());
        }
    }

    /**
     * Test which checks custom permission which should not be granted.
     */
    @Test
    public void testCustomPermWithoutGrant() {
        final String permissionName = "org.jboss.security.Permission";
        try {
            CheckJSMUtils.checkRuntimePermission(permissionName);
            Assert.fail("Access should be denied");
        } catch (AccessControlException e) {
            Permission expectedPerm = new RuntimePermission(permissionName);
            Assert.assertEquals("Permission type doesn't match", expectedPerm, e.getPermission());
        }
    }

    /**
     * Test which checks granted permission.
     */
    @Test
    public void testGrantedCustomPerm() {
        CheckJSMUtils.checkRuntimePermission("secman.test.Permission");
    }

    static class CustomModuleSetup extends AbstractCustomModuleServerSetup {
        @Override
        protected String getModuleSuffix() {
            return "limited";
        }
    }
}

