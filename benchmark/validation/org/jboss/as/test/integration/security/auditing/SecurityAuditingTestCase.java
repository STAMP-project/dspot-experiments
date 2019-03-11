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
package org.jboss.as.test.integration.security.auditing;


import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.ejb.security.AnnSBTest;
import org.jboss.as.test.integration.management.base.AbstractMgmtServerSetupTask;
import org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask;
import org.jboss.as.test.integration.security.common.config.SecurityDomain;
import org.jboss.as.test.integration.security.common.config.SecurityModule;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.as.test.shared.ServerSnapshot;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This class tests Security auditing functionality
 *
 * @author <a href="mailto:jlanik@redhat.com">Jan Lanik</a>.
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup({ SecurityAuditingTestCase.SecurityDomainsSetup.class, SecurityAuditingTestCase.SecurityAuditingTestCaseSetup.class })
public class SecurityAuditingTestCase extends AnnSBTest {
    private static final Logger log = Logger.getLogger(SecurityAuditingTestCase.testClass());

    private static File auditLog = new File(System.getProperty("jboss.home", null), (((("standalone" + (File.separator)) + "log") + (File.separator)) + "audit.log"));

    static class SecurityAuditingTestCaseSetup extends AbstractMgmtServerSetupTask {
        /**
         * The LOGGING
         */
        private static final String LOGGING = "logging";

        private AutoCloseable snapshot;

        @Override
        protected void doSetup(final ManagementClient managementClient) throws Exception {
            snapshot = ServerSnapshot.takeSnapshot(managementClient);
            final List<ModelNode> updates = new ArrayList<ModelNode>();
            ModelNode op = new ModelNode();
            op.get(OP).set(ADD);
            op.get(OP_ADDR).add(SUBSYSTEM, SecurityAuditingTestCase.SecurityAuditingTestCaseSetup.LOGGING);
            op.get(OP_ADDR).add("periodic-rotating-file-handler", "AUDIT");
            op.get("level").set("TRACE");
            op.get("append").set("true");
            op.get("suffix").set(".yyyy-MM-dd");
            ModelNode file = new ModelNode();
            file.get("relative-to").set("jboss.server.log.dir");
            file.get("path").set("audit.log");
            op.get("file").set(file);
            op.get("formatter").set("%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n");
            updates.add(op);
            op = new ModelNode();
            op.get(OP).set(ADD);
            op.get(OP_ADDR).add(SUBSYSTEM, SecurityAuditingTestCase.SecurityAuditingTestCaseSetup.LOGGING);
            op.get(OP_ADDR).add("logger", "org.jboss.security.audit");
            op.get("level").set("TRACE");
            ModelNode list = op.get("handlers");
            list.add("AUDIT");
            updates.add(op);
            if ((System.getProperty("elytron")) != null) {
                // /subsystem=elytron/security-domain=ApplicationDomain:write-attribute(name=security-event-listener, value=local-audit)
                op = new ModelNode();
                op.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
                op.get(OP_ADDR).add(SUBSYSTEM, "elytron");
                op.get(OP_ADDR).add("security-domain", "ApplicationDomain");
                op.get("name").set("security-event-listener");
                op.get("value").set("local-audit");
                updates.add(op);
            }
            executeOperations(updates);
            if ((System.getProperty("elytron")) != null) {
                ServerReload.executeReloadAndWaitForCompletion(managementClient.getControllerClient(), 50000);
            }
        }

        @Override
        public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
            snapshot.close();
        }
    }

    private static final String MODULE = "singleMethodsAnnOnlySFSB";

    /**
     * Basic test if auditing works in EJB module.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSingleMethodAnnotationsUser1Template() throws Exception {
        Assert.assertTrue((("Audit log file has not been created (" + (SecurityAuditingTestCase.auditLog.getAbsolutePath())) + ")"), SecurityAuditingTestCase.auditLog.exists());
        Assert.assertTrue((("Audit log file is closed for reading (" + (SecurityAuditingTestCase.auditLog.getAbsolutePath())) + ")"), SecurityAuditingTestCase.auditLog.canRead());
        BufferedReader reader = Files.newBufferedReader(SecurityAuditingTestCase.auditLog.toPath(), StandardCharsets.UTF_8);
        while ((reader.readLine()) != null) {
            // we need to get trough all old records (if any)
        } 
        testSingleMethodAnnotationsUser1Template(SecurityAuditingTestCase.MODULE, SecurityAuditingTestCase.log, SecurityAuditingTestCase.beanClass());
        checkAuditLog(reader, "(TRACE.+org.jboss.security.audit.+Success.+user1|SecurityAuthenticationSuccessfulEvent.*\"name\":\"user1\")");
    }

    /* A {@link ServerSetupTask} instance which creates security domains for this test case.

    @author Josef Cacek
     */
    static class SecurityDomainsSetup extends AbstractSecurityDomainsServerSetupTask {
        /* Returns SecurityDomains configuration for this testcase.

        @see org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask#getSecurityDomains()
         */
        @Override
        protected SecurityDomain[] getSecurityDomains() {
            final SecurityDomain sd = new SecurityDomain.Builder().name("form-auth").loginModules(new SecurityModule.Builder().name("UsersRoles").build()).build();
            return new SecurityDomain[]{ sd };
        }
    }
}

