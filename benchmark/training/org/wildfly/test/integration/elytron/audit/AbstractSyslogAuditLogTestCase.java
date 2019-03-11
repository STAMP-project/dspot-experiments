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


import java.net.URL;
import java.util.concurrent.BlockingQueue;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.syslogserver.BlockedSyslogServerEventHandler;
import org.junit.Assert;
import org.junit.Test;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;


/**
 * Abstract class for Elytron Audit Logging tests. Tests are placed here as well as a couple of syslog-specific helper methods.
 *
 * @author Jan Tymel
 */
public abstract class AbstractSyslogAuditLogTestCase extends AbstractAuditLogTestCase {
    /**
     * Tests whether failed permission check was logged.
     */
    @Test
    @OperateOnDeployment(AbstractAuditLogTestCase.SD_WITHOUT_LOGIN_PERMISSION)
    public void testFailedPermissionCheck() throws Exception {
        final URL servletUrl = new URL(((url.toExternalForm()) + "role1"));
        final BlockingQueue<SyslogServerEventIF> queue = BlockedSyslogServerEventHandler.getQueue();
        queue.clear();
        Utils.makeCallWithBasicAuthn(servletUrl, AbstractAuditLogTestCase.USER, AbstractAuditLogTestCase.PASSWORD, SC_UNAUTHORIZED);
        Assert.assertTrue("Failed permission check was not logged", AbstractSyslogAuditLogTestCase.loggedFailedPermissionCheck(queue, AbstractAuditLogTestCase.USER));
        Assert.assertTrue("Failed authentication was not logged", AbstractSyslogAuditLogTestCase.loggedFailedAuth(queue, AbstractAuditLogTestCase.USER));
        assertNoMoreMessages(queue);
    }
}

