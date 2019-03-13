/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.wildfly.test.integration.elytron.audit;


import java.io.File;
import java.net.URL;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.management.util.CLIWrapper;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.module.util.TestModule;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.security.auth.server.event.SecurityAuthenticationFailedEvent;
import org.wildfly.security.auth.server.event.SecurityAuthenticationSuccessfulEvent;


@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup({ AbstractAuditLogTestCase.SecurityDomainSetupTask.class, CustomSecurityEventListenerTestCase.CustomListenerSetupTask.class })
public class CustomSecurityEventListenerTestCase extends AbstractAuditLogTestCase {
    private static final String NAME = CustomSecurityEventListener.class.getSimpleName();

    private static final String AUDIT_LOG_NAME = (CustomSecurityEventListenerTestCase.NAME) + ".log";

    private static final File AUDIT_LOG_FILE = new File("target", CustomSecurityEventListenerTestCase.AUDIT_LOG_NAME);

    /**
     * Tests whether successful authentication was logged.
     */
    @Test
    @OperateOnDeployment(AbstractAuditLogTestCase.SD_DEFAULT)
    public void testSuccessfulAuth() throws Exception {
        final URL servletUrl = new URL(((url.toExternalForm()) + "role1"));
        CustomSecurityEventListenerTestCase.discardCurrentContents();
        Utils.makeCallWithBasicAuthn(servletUrl, AbstractAuditLogTestCase.USER, AbstractAuditLogTestCase.PASSWORD, SC_OK);
        Assert.assertEquals("Successful authentication was not logged", ("testingValue:" + (SecurityAuthenticationSuccessfulEvent.class.getName())), CustomSecurityEventListenerTestCase.getContent());
    }

    /**
     * Tests whether failed authentication with wrong user was logged.
     */
    @Test
    @OperateOnDeployment(AbstractAuditLogTestCase.SD_DEFAULT)
    public void testFailedAuthWrongUser() throws Exception {
        final URL servletUrl = new URL(((url.toExternalForm()) + "role1"));
        CustomSecurityEventListenerTestCase.discardCurrentContents();
        Utils.makeCallWithBasicAuthn(servletUrl, AbstractAuditLogTestCase.UNKNOWN_USER, AbstractAuditLogTestCase.PASSWORD, SC_UNAUTHORIZED);
        Assert.assertEquals("Failed authentication was not logged", ("testingValue:" + (SecurityAuthenticationFailedEvent.class.getName())), CustomSecurityEventListenerTestCase.getContent());
    }

    static class CustomListenerSetupTask implements ServerSetupTask {
        static final Class<?> listenerClass = CustomSecurityEventListener.class;

        private final TestModule module;

        CustomListenerSetupTask() {
            module = new TestModule(CustomSecurityEventListenerTestCase.CustomListenerSetupTask.listenerClass.getName(), "org.wildfly.security.elytron");
            JavaArchive auditJar = module.addResource(((CustomSecurityEventListenerTestCase.CustomListenerSetupTask.listenerClass.getSimpleName()) + ".jar"));
            auditJar.addClass(CustomSecurityEventListenerTestCase.CustomListenerSetupTask.listenerClass);
        }

        @Override
        public void setup(ManagementClient managementClient, String string) throws Exception {
            module.create(true);
            try (CLIWrapper cli = new CLIWrapper(true)) {
                cli.sendLine(((((((((("/subsystem=elytron/custom-security-event-listener=" + (CustomSecurityEventListenerTestCase.NAME)) + ":add(") + "module=\"") + (CustomSecurityEventListenerTestCase.CustomListenerSetupTask.listenerClass.getName())) + "\", ") + "class-name=\"") + (CustomSecurityEventListenerTestCase.CustomListenerSetupTask.listenerClass.getName())) + "\",") + "configuration={ testingAttribute = testingValue })"));
                AbstractAuditLogTestCase.setEventListenerOfApplicationDomain(cli, CustomSecurityEventListenerTestCase.NAME);
            }
            ServerReload.reloadIfRequired(managementClient.getControllerClient());
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            try (CLIWrapper cli = new CLIWrapper(true)) {
                AbstractAuditLogTestCase.setDefaultEventListenerOfApplicationDomain(cli);
                cli.sendLine((("/subsystem=elytron/custom-security-event-listener=" + (CustomSecurityEventListenerTestCase.NAME)) + ":remove"));
            }
            module.remove();
            ServerReload.reloadIfRequired(managementClient.getControllerClient());
        }
    }
}

