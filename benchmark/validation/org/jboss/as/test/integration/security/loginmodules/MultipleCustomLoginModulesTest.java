/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.security.loginmodules;


import Constants.AUTHENTICATION;
import Constants.CLASSIC;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.security.Constants;
import org.jboss.as.test.categories.CommonCriteria;
import org.jboss.as.test.integration.security.common.AbstractSecurityDomainSetup;
import org.jboss.as.test.integration.security.loginmodules.common.CustomLoginModule1;
import org.jboss.as.test.integration.security.loginmodules.common.CustomLoginModule2;
import org.jboss.as.test.module.util.TestModule;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Tests whether the loading of two chained {@link javax.security.auth.spi.LoginModule}s from two separate JBoss modules works
 * properly. Created as a verification for <a href="https://issues.jboss.org/browse/SECURITY-930">SECURITY-930</a> and
 * <a href="https://issues.jboss.org/browse/WFLY-7412">WFLY-7412</a>.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(MultipleCustomLoginModulesTest.MultipleCustomLoginModulesSecurityDomainSetup.class)
@Category(CommonCriteria.class)
public class MultipleCustomLoginModulesTest {
    /**
     * Creates two JBoss modules that host {@link CustomLoginModule1} and {@link CustomLoginModule2} respectively and then creates a security
     * domain that uses them in a chain.
     */
    static class MultipleCustomLoginModulesSecurityDomainSetup extends AbstractSecurityDomainSetup {
        private static final Logger log = Logger.getLogger(MultipleCustomLoginModulesTest.MultipleCustomLoginModulesSecurityDomainSetup.class);

        private static final String SECURITY_DOMAIN_NAME = "custom-login-module";

        private static final Class<?>[] MODULE_CLASSES = new Class<?>[]{ CustomLoginModule1.class, CustomLoginModule2.class };

        private final TestModule[] modules;

        public MultipleCustomLoginModulesSecurityDomainSetup() {
            TestModule[] modules = new TestModule[MultipleCustomLoginModulesTest.MultipleCustomLoginModulesSecurityDomainSetup.MODULE_CLASSES.length];
            for (int i = 0; i < (MultipleCustomLoginModulesTest.MultipleCustomLoginModulesSecurityDomainSetup.MODULE_CLASSES.length); i++) {
                Class<?> loginModuleClass = MultipleCustomLoginModulesTest.MultipleCustomLoginModulesSecurityDomainSetup.MODULE_CLASSES[i];
                TestModule module = new TestModule(loginModuleClass.getName(), "org.picketbox", "javax.api", "org.jboss.logging");
                JavaArchive jar = module.addResource(((loginModuleClass.getSimpleName()) + ".jar"));
                jar.addClass(loginModuleClass);
                modules[i] = module;
            }
            this.modules = modules;
        }

        private void addLoginModuleSteps(ModelNode steps, PathAddress address, Class<?>... loginModuleClasses) {
            for (Class<?> loginModuleClass : loginModuleClasses) {
                ModelNode loginModule1 = Util.createAddOperation(address.append(Constants.LOGIN_MODULE, loginModuleClass.getSimpleName()));
                loginModule1.get(Constants.CODE).set(loginModuleClass.getName());
                loginModule1.get(Constants.MODULE).set(loginModuleClass.getName());
                loginModule1.get(Constants.FLAG).set("sufficient");
                loginModule1.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
                steps.add(loginModule1);
            }
        }

        @Override
        protected String getSecurityDomainName() {
            return MultipleCustomLoginModulesTest.MultipleCustomLoginModulesSecurityDomainSetup.SECURITY_DOMAIN_NAME;
        }

        @Override
        public void setup(final ManagementClient managementClient, final String containerId) throws IOException {
            for (TestModule testModule : modules) {
                testModule.create(true);
            }
            MultipleCustomLoginModulesTest.MultipleCustomLoginModulesSecurityDomainSetup.log.debug("start of the domain creation");
            final ModelNode compositeOp = new ModelNode();
            compositeOp.get(OP).set(COMPOSITE);
            compositeOp.get(OP_ADDR).setEmptyList();
            ModelNode steps = compositeOp.get(STEPS);
            PathAddress address = PathAddress.pathAddress().append(SUBSYSTEM, "security").append(Constants.SECURITY_DOMAIN, getSecurityDomainName());
            steps.add(Util.createAddOperation(address));
            address = address.append(AUTHENTICATION, CLASSIC);
            steps.add(Util.createAddOperation(address));
            addLoginModuleSteps(steps, address, MultipleCustomLoginModulesTest.MultipleCustomLoginModulesSecurityDomainSetup.MODULE_CLASSES);
            applyUpdates(managementClient.getControllerClient(), Arrays.asList(compositeOp));
            MultipleCustomLoginModulesTest.MultipleCustomLoginModulesSecurityDomainSetup.log.debug("end of the domain creation");
        }

        @Override
        public void tearDown(final ManagementClient managementClient, final String containerId) {
            super.tearDown(managementClient, containerId);
            for (TestModule testModule : modules) {
                testModule.remove();
            }
        }
    }

    @ArquillianResource
    private URL url;

    @Test
    public void testBaduser1() throws Exception {
        /* baduser1 can authenticate, because we use the correct password, but he does not have the "gooduser" role required by
        SecuredServlet and the server thus return 403 rather than 401
         */
        assertResponse(CustomLoginModule1.BADUSER1_USERNAME, CustomLoginModule1.BADUSER1_PASSWORD, 403);
    }

    @Test
    public void testBaduser2() throws Exception {
        /* baduser2 can authenticate, because we use the correct password, but he does not have the "gooduser" role required by
        SecuredServlet and the server thus return 403 rather than 401
         */
        assertResponse(CustomLoginModule2.BADUSER2_USERNAME, CustomLoginModule2.BADUSER2_PASSWORD, 403);
    }

    @Test
    public void testGooduser1() throws Exception {
        assertResponse(CustomLoginModule1.GOODUSER1_USERNAME, CustomLoginModule1.GOODUSER1_PASSWORD, 200);
    }

    @Test
    public void testGooduser1WithBadPassword() throws Exception {
        assertResponse(CustomLoginModule1.GOODUSER1_USERNAME, "bogus", 401);
    }

    @Test
    public void testGooduser1WithoutPassword() throws Exception {
        assertResponse(CustomLoginModule1.GOODUSER1_USERNAME, null, 401);
    }

    @Test
    public void testGooduser2() throws Exception {
        assertResponse(CustomLoginModule2.GOODUSER2_USERNAME, CustomLoginModule2.GOODUSER2_PASSWORD, 200);
    }

    @Test
    public void testGooduser2WithBadPassword() throws Exception {
        assertResponse(CustomLoginModule2.GOODUSER2_USERNAME, "whatever", 401);
    }

    @Test
    public void testGooduser2WithoutPassword() throws Exception {
        assertResponse(CustomLoginModule2.GOODUSER2_USERNAME, null, 401);
    }
}

