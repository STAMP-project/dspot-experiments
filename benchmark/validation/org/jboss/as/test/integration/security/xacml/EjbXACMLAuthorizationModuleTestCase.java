/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.security.xacml;


import HelloBean.HELLO_WORLD;
import SecurityDomain.DEFAULT_NAME;
import java.util.concurrent.Callable;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask;
import org.jboss.as.test.integration.security.common.config.SecurityDomain;
import org.jboss.as.test.integration.security.common.config.SecurityModule;
import org.jboss.as.test.integration.security.common.ejb3.Hello;
import org.jboss.as.test.shared.integration.ejb.security.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Arquillian JUnit testcase for testing XACML based authorization of EJBs.
 *
 * @author Josef Cacek
 */
@RunWith(Arquillian.class)
@ServerSetup({ EjbXACMLAuthorizationModuleTestCase.SecurityDomainsSetup.class })
public class EjbXACMLAuthorizationModuleTestCase {
    @EJB(mappedName = "java:global/test-custom-xacml/HelloBean")
    private Hello hello;

    /**
     * Tests secured EJB call for unauthenticated user.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNotAuthn() throws Exception {
        try {
            hello.sayHelloWorld();
            Assert.fail("Access to sayHelloWorld() should be denied if not authenticated.");
        } catch (EJBAccessException e) {
            // OK
        }
    }

    /**
     * Test secured EJB call for authenticated and authorized user.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAuthz() throws Exception {
        final Callable<Void> callable = () -> {
            Assert.assertEquals(HELLO_WORLD, hello.sayHelloWorld());
            return null;
        };
        Util.switchIdentitySCF("jduke", "theduke", callable);
    }

    /**
     * Test secured EJB call for authenticated but not authorized authorized user.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNotAuthz() throws Exception {
        final Callable<Void> callable = () -> {
            hello.sayHelloWorld();
            Assert.fail("Access to sayHelloWorld() should be denied for JohnDoe.");
            return null;
        };
        try {
            Util.switchIdentitySCF("JohnDoe", "jdoe", callable);
        } catch (EJBAccessException e) {
            // OK - expected
        }
    }

    /**
     * Tests unauthenticated call followed by the authentication and second call to the same instance.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAuthenticationCache() throws Exception {
        try {
            hello.sayHelloWorld();
            Assert.fail("Access to sayHello() should be denied if not authenticated.");
        } catch (EJBAccessException e) {
            // OK
        }
        final Callable<Void> callable = () -> {
            Assert.assertEquals(HELLO_WORLD, hello.sayHelloWorld());
            return null;
        };
        Util.switchIdentitySCF("jduke", "theduke", callable);
    }

    // Embedded classes ------------------------------------------------------
    /**
     * A {@link ServerSetupTask} instance which creates security domains for this test case.
     *
     * @author Josef Cacek
     */
    static class SecurityDomainsSetup extends AbstractSecurityDomainsServerSetupTask {
        /**
         * Returns SecurityDomains configuration for this testcase.
         *
         * @see org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask#getSecurityDomains()
         */
        @Override
        protected SecurityDomain[] getSecurityDomains() {
            final SecurityDomain sd = // 
            new SecurityDomain.Builder().name(DEFAULT_NAME).loginModules(new SecurityModule.Builder().name("UsersRoles").build()).authorizationModules(new SecurityModule.Builder().name(CustomXACMLAuthorizationModule.class.getName()).build()).build();
            return new SecurityDomain[]{ sd };
        }
    }
}

