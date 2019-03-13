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
package org.jboss.as.test.smoke.mgmt.resourceadapter;


import ConnectionSecurityType.APPLICATION;
import ConnectionSecurityType.ELYTRON;
import ConnectionSecurityType.ELYTRON_AUTHENTICATION_CONTEXT;
import ConnectionSecurityType.SECURITY_DOMAIN;
import ConnectionSecurityType.SECURITY_DOMAIN_AND_APPLICATION;
import ConnectionSecurityType.USER_PASSWORD;
import java.util.Deque;
import java.util.LinkedList;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.dmr.ModelNode;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Resource adapter operation unit test.
 *
 * @author <a href="mailto:vrastsel@redhat.com">Vladimir Rastseluev</a>
 * @author Flavia Rainone
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResourceAdapterOperationsUnitTestCase extends ContainerResourceMgmtTestBase {
    private static final Deque<ModelNode> REMOVE_ADDRESSES = new LinkedList<>();

    @Test
    public void addComplexResourceAdapterWithAppSecurity() throws Exception {
        complexResourceAdapterAddTest(APPLICATION, null);
    }

    @Test
    public void addComplexResourceAdapterWithAppSecurity_UserPassRecovery() throws Exception {
        complexResourceAdapterAddTest(APPLICATION, USER_PASSWORD);
    }

    @Test
    public void addComplexResourceAdapterWithAppSecurity_SecurityDomainRecovery() throws Exception {
        complexResourceAdapterAddTest(APPLICATION, SECURITY_DOMAIN);
    }

    @Test
    public void addComplexResourceAdapterWithAppSecurity_ElytronRecovery() throws Exception {
        complexResourceAdapterAddTest(APPLICATION, ELYTRON);
    }

    @Test
    public void addComplexResourceAdapterWithAppSecurity_ElytronAuthCtxtRecovery() throws Exception {
        complexResourceAdapterAddTest(APPLICATION, ELYTRON_AUTHENTICATION_CONTEXT);
    }

    @Test
    public void addComplexResourceAdapterWithSecurityDomain() throws Exception {
        complexResourceAdapterAddTest(SECURITY_DOMAIN, SECURITY_DOMAIN);
    }

    @Test
    public void addComplexResourceAdapterWithSecurityDomain_NoRecoverySec() throws Exception {
        complexResourceAdapterAddTest(SECURITY_DOMAIN, null);
    }

    @Test
    public void addComplexResourceAdapterWithSecurityDomain_UserPassRecovery() throws Exception {
        complexResourceAdapterAddTest(SECURITY_DOMAIN, USER_PASSWORD);
    }

    @Test
    public void addComplexResourceAdapterWithSecurityDomain_ElytronRecovery() throws Exception {
        complexResourceAdapterAddTest(SECURITY_DOMAIN, ELYTRON);
    }

    @Test
    public void addComplexResourceAdapterWithSecurityDomain_ElytronAuthCtxtRecovery() throws Exception {
        complexResourceAdapterAddTest(SECURITY_DOMAIN, ELYTRON_AUTHENTICATION_CONTEXT);
    }

    @Test
    public void addComplexResourceAdapterWithSecurityDomainAndApp() throws Exception {
        complexResourceAdapterAddTest(SECURITY_DOMAIN_AND_APPLICATION, null);
    }

    @Test
    public void addComplexResourceAdapterWithSecurityDomainAndApp_UserPassRecovery() throws Exception {
        complexResourceAdapterAddTest(SECURITY_DOMAIN_AND_APPLICATION, USER_PASSWORD);
    }

    @Test
    public void addComplexResourceAdapterWithSecurityDomainAndApp_SecurityDomainRecovery() throws Exception {
        complexResourceAdapterAddTest(SECURITY_DOMAIN_AND_APPLICATION, SECURITY_DOMAIN);
    }

    @Test
    public void addComplexResourceAdapterWithSecurityDomainAndApp_ElytronRecovery() throws Exception {
        complexResourceAdapterAddTest(SECURITY_DOMAIN_AND_APPLICATION, ELYTRON);
    }

    @Test
    public void addComplexResourceAdapterWithSecurityDomainAndApp_ElytronAuthCtxtRecovery() throws Exception {
        complexResourceAdapterAddTest(SECURITY_DOMAIN_AND_APPLICATION, ELYTRON_AUTHENTICATION_CONTEXT);
    }

    @Test
    public void addComplexResourceAdapterWithElytron() throws Exception {
        complexResourceAdapterAddTest(ELYTRON, ELYTRON);
    }

    @Test
    public void addComplexResourceAdapterWithElytron_NoRecoverySec() throws Exception {
        complexResourceAdapterAddTest(ELYTRON, null);
    }

    @Test
    public void addComplexResourceAdapterWithElytron_UserPassRecoverySec() throws Exception {
        complexResourceAdapterAddTest(ELYTRON, USER_PASSWORD);
    }

    @Test
    public void addComplexResourceAdapterWithElytron_SecurityDomainRecoverySec() throws Exception {
        complexResourceAdapterAddTest(ELYTRON, SECURITY_DOMAIN);
    }

    @Test
    public void addComplexResourceAdapterWithElytron_ElytronAuthCtxtRecoverySec() throws Exception {
        complexResourceAdapterAddTest(ELYTRON, ELYTRON_AUTHENTICATION_CONTEXT);
    }

    @Test
    public void addComplexResourceAdapterWithElytronAuthCtxt() throws Exception {
        complexResourceAdapterAddTest(ELYTRON_AUTHENTICATION_CONTEXT, ELYTRON_AUTHENTICATION_CONTEXT);
    }

    @Test
    public void addComplexResourceAdapterWithElytronAuthCtxtN_oRecoverySec() throws Exception {
        complexResourceAdapterAddTest(ELYTRON_AUTHENTICATION_CONTEXT, null);
    }

    @Test
    public void addComplexResourceAdapterWithElytronAuthCtxt_UserPassRecoverySec() throws Exception {
        complexResourceAdapterAddTest(ELYTRON_AUTHENTICATION_CONTEXT, USER_PASSWORD);
    }

    @Test
    public void addComplexResourceAdapterWithElytronAuthCtxt_SecurityDomainRecoverySec() throws Exception {
        complexResourceAdapterAddTest(ELYTRON_AUTHENTICATION_CONTEXT, SECURITY_DOMAIN);
    }

    @Test
    public void addComplexResourceAdapterWithElytronAuthCtxt_ElytronRecoverySec() throws Exception {
        complexResourceAdapterAddTest(ELYTRON_AUTHENTICATION_CONTEXT, ELYTRON);
    }
}

