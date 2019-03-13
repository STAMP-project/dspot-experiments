/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
import org.jboss.as.test.integration.security.loginmodules.common.CustomTestLoginModule;
import org.jboss.as.test.integration.web.security.SecuredServlet;
import org.jboss.dmr.ModelNode;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Unit test for custom login modules in authentication.
 *
 * @author <a href="mailto:mmoyses@redhat.com">Marcus Moyses</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(CustomLoginModuleTestCase.CustomLoginModuleSecurityDomainSetup.class)
@Category(CommonCriteria.class)
public class CustomLoginModuleTestCase {
    @ArquillianResource(SecuredServlet.class)
    URL deploymentURL;

    static class CustomLoginModuleSecurityDomainSetup extends AbstractSecurityDomainSetup {
        @Override
        protected String getSecurityDomainName() {
            return "custom-login-module";
        }

        @Override
        public void setup(final ManagementClient managementClient, final String containerId) throws Exception {
            ModelNode op = new ModelNode();
            op.get(OP).set(COMPOSITE);
            op.get(OP_ADDR).setEmptyList();
            PathAddress address = PathAddress.pathAddress().append(SUBSYSTEM, "security").append(Constants.SECURITY_DOMAIN, getSecurityDomainName());
            op.get(STEPS).add(Util.createAddOperation(address));
            address = address.append(AUTHENTICATION, CLASSIC);
            op.get(STEPS).add(Util.createAddOperation(address));
            ModelNode loginModule = Util.createAddOperation(address.append(Constants.LOGIN_MODULE, CustomTestLoginModule.class.getName()));
            loginModule.get(Constants.CODE).set(CustomTestLoginModule.class.getName());
            loginModule.get(Constants.FLAG).set("required");
            op.get(STEPS).add(loginModule);
            applyUpdates(managementClient.getControllerClient(), Arrays.asList(op));
        }
    }

    @Test
    public void testSuccessfulAuth() throws Exception {
        makeCall("anil", "anil", 200);
    }

    @Test
    public void testUnsuccessfulAuth() throws Exception {
        makeCall("marcus", "marcus", 403);
    }
}

