/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
import org.codehaus.plexus.util.FileUtils;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.management.util.CLIWrapper;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.shared.CliUtils;
import org.jboss.as.test.shared.ServerReload;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.test.security.common.elytron.FileAuditLog;


/**
 * Test case for 'file-audit-log' Elytron subsystem resource.
 *
 * @author Jan Tymel
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup({ AbstractAuditLogTestCase.SecurityDomainSetupTask.class, FileAuditLogTestCase.FileAuditLogSetupTask.class })
public class FileAuditLogTestCase extends AbstractAuditLogTestCase {
    private static final String NAME = FileAuditLogTestCase.class.getSimpleName();

    private static final String AUDIT_LOG_NAME = "test-audit.log";

    private static final File WORK_DIR = new File((("target" + (File.separatorChar)) + (FileAuditLogTestCase.NAME)));

    private static final File AUDIT_LOG_FILE = new File(FileAuditLogTestCase.WORK_DIR, FileAuditLogTestCase.AUDIT_LOG_NAME);

    /**
     * Tests whether successful authentication was logged.
     */
    @Test
    @OperateOnDeployment(AbstractAuditLogTestCase.SD_DEFAULT)
    public void testSuccessfulAuth() throws Exception {
        final URL servletUrl = new URL(((url.toExternalForm()) + "role1"));
        FileAuditLogTestCase.discardCurrentContents(FileAuditLogTestCase.AUDIT_LOG_FILE);
        Utils.makeCallWithBasicAuthn(servletUrl, AbstractAuditLogTestCase.USER, AbstractAuditLogTestCase.PASSWORD, SC_OK);
        Assert.assertTrue("Successful authentication was not logged", FileAuditLogTestCase.loggedSuccessfulAuth(FileAuditLogTestCase.AUDIT_LOG_FILE, AbstractAuditLogTestCase.USER));
    }

    /**
     * Tests whether failed authentication with wrong user was logged.
     */
    @Test
    @OperateOnDeployment(AbstractAuditLogTestCase.SD_DEFAULT)
    public void testFailedAuthWrongUser() throws Exception {
        final URL servletUrl = new URL(((url.toExternalForm()) + "role1"));
        FileAuditLogTestCase.discardCurrentContents(FileAuditLogTestCase.AUDIT_LOG_FILE);
        Utils.makeCallWithBasicAuthn(servletUrl, AbstractAuditLogTestCase.UNKNOWN_USER, AbstractAuditLogTestCase.PASSWORD, SC_UNAUTHORIZED);
        Assert.assertTrue("Failed authentication with wrong user was not logged", FileAuditLogTestCase.loggedFailedAuth(FileAuditLogTestCase.AUDIT_LOG_FILE, AbstractAuditLogTestCase.UNKNOWN_USER));
    }

    /**
     * Tests whether failed authentication with wrong password was logged.
     */
    @Test
    @OperateOnDeployment(AbstractAuditLogTestCase.SD_DEFAULT)
    public void testFailedAuthWrongPassword() throws Exception {
        final URL servletUrl = new URL(((url.toExternalForm()) + "role1"));
        FileAuditLogTestCase.discardCurrentContents(FileAuditLogTestCase.AUDIT_LOG_FILE);
        Utils.makeCallWithBasicAuthn(servletUrl, AbstractAuditLogTestCase.USER, AbstractAuditLogTestCase.WRONG_PASSWORD, SC_UNAUTHORIZED);
        Assert.assertTrue("Failed authentication with wrong password was not logged", FileAuditLogTestCase.loggedFailedAuth(FileAuditLogTestCase.AUDIT_LOG_FILE, AbstractAuditLogTestCase.USER));
    }

    /**
     * Tests whether failed authentication with empty password was logged.
     */
    @Test
    @OperateOnDeployment(AbstractAuditLogTestCase.SD_DEFAULT)
    public void testFailedAuthEmptyPassword() throws Exception {
        final URL servletUrl = new URL(((url.toExternalForm()) + "role1"));
        FileAuditLogTestCase.discardCurrentContents(FileAuditLogTestCase.AUDIT_LOG_FILE);
        Utils.makeCallWithBasicAuthn(servletUrl, AbstractAuditLogTestCase.USER, AbstractAuditLogTestCase.EMPTY_PASSWORD, SC_UNAUTHORIZED);
        Assert.assertTrue("Failed authentication with empty password was not logged", FileAuditLogTestCase.loggedFailedAuth(FileAuditLogTestCase.AUDIT_LOG_FILE, AbstractAuditLogTestCase.USER));
    }

    /**
     * Tests whether successful permission check was logged.
     */
    @Test
    @OperateOnDeployment(AbstractAuditLogTestCase.SD_DEFAULT)
    public void testSuccessfulPermissionCheck() throws Exception {
        final URL servletUrl = new URL(((url.toExternalForm()) + "role1"));
        FileAuditLogTestCase.discardCurrentContents(FileAuditLogTestCase.AUDIT_LOG_FILE);
        Utils.makeCallWithBasicAuthn(servletUrl, AbstractAuditLogTestCase.USER, AbstractAuditLogTestCase.PASSWORD, SC_OK);
        Assert.assertTrue("Successful permission check was not logged", FileAuditLogTestCase.loggedSuccessfulPermissionCheck(FileAuditLogTestCase.AUDIT_LOG_FILE, AbstractAuditLogTestCase.USER));
    }

    /**
     * Tests whether failed permission check was logged.
     */
    @Test
    @OperateOnDeployment(AbstractAuditLogTestCase.SD_WITHOUT_LOGIN_PERMISSION)
    public void testFailedPermissionCheck() throws Exception {
        final URL servletUrl = new URL(((url.toExternalForm()) + "role1"));
        FileAuditLogTestCase.discardCurrentContents(FileAuditLogTestCase.AUDIT_LOG_FILE);
        Utils.makeCallWithBasicAuthn(servletUrl, AbstractAuditLogTestCase.USER, AbstractAuditLogTestCase.PASSWORD, SC_UNAUTHORIZED);
        Assert.assertTrue("Failed permission check was not logged", FileAuditLogTestCase.loggedFailedPermissionCheck(FileAuditLogTestCase.AUDIT_LOG_FILE, AbstractAuditLogTestCase.USER));
    }

    /**
     * Creates Elytron 'file-audit-log' and sets it as ApplicationDomain's security listener.
     */
    static class FileAuditLogSetupTask implements ServerSetupTask {
        FileAuditLog auditLog;

        @Override
        public void setup(ManagementClient managementClient, String string) throws Exception {
            try (CLIWrapper cli = new CLIWrapper(true)) {
                FileAuditLogTestCase.createEmptyDirectory(FileAuditLogTestCase.WORK_DIR);
                auditLog = FileAuditLog.builder().withName(FileAuditLogTestCase.NAME).withPath(CliUtils.asAbsolutePath(FileAuditLogTestCase.AUDIT_LOG_FILE)).build();
                auditLog.create(cli);
                AbstractAuditLogTestCase.setEventListenerOfApplicationDomain(cli, FileAuditLogTestCase.NAME);
            }
            ServerReload.reloadIfRequired(managementClient.getControllerClient());
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            try (CLIWrapper cli = new CLIWrapper(true)) {
                AbstractAuditLogTestCase.setDefaultEventListenerOfApplicationDomain(cli);
                auditLog.remove(cli);
                FileUtils.deleteDirectory(FileAuditLogTestCase.WORK_DIR);
            }
            ServerReload.reloadIfRequired(managementClient.getControllerClient());
        }
    }
}

