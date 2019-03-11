/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.container.interceptor.security.api;


import SecurityModule.Builder;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.ejb.container.interceptor.security.GuestDelegationLoginModule;
import org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask;
import org.jboss.as.test.integration.security.common.AbstractSecurityRealmsServerSetupTask;
import org.jboss.as.test.integration.security.common.config.SecurityDomain;
import org.jboss.as.test.integration.security.common.config.SecurityModule;
import org.jboss.as.test.integration.security.common.config.realm.SecurityRealm;
import org.jboss.as.test.integration.security.common.config.realm.ServerIdentity;
import org.jboss.security.ClientLoginModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;


/**
 * Testcase based on ejb-security-interceptors quickstart application. It tests security context propagation for EJBs.
 *
 * @author Josef Cacek
 * @author <a href="mailto:tadamski@redhat.com">Tomasz Adamski</a>
 */
@RunWith(Arquillian.class)
@ServerSetup({ SwitchIdentityTestCase.SecurityDomainsSetup.class// 
, SwitchIdentityTestCase.SecurityRealmsSetup.class })
@RunAsClient
public class SwitchIdentityTestCase {
    private static final String EJB_OUTBOUND_REALM = "ejb-outbound-realm";

    private static final String SECURITY_DOMAIN_NAME = "switch-identity-test";

    private final Map<String, String> passwordsToUse;

    public SwitchIdentityTestCase() {
        passwordsToUse = new HashMap<>();
        passwordsToUse.put("guest", "guest");
        passwordsToUse.put("user1", "password1");
        passwordsToUse.put("user2", "password2");
        passwordsToUse.put("remoteejbuser", "rem@teejbpasswd1");
    }

    @ArquillianResource
    private ManagementClient mgmtClient;

    /**
     * The login {@link Configuration} which always returns a single {@link AppConfigurationEntry} with a
     * {@link ClientLoginModule}.
     */
    private static final Configuration CLIENT_LOGIN_CONFIG = new Configuration() {
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            final Map<String, String> options = new HashMap<String, String>();
            options.put("multi-threaded", "true");
            options.put("restore-login-identity", "true");
            AppConfigurationEntry clmEntry = new AppConfigurationEntry(ClientLoginModule.class.getName(), REQUIRED, options);
            return new AppConfigurationEntry[]{ clmEntry };
        }
    };

    /**
     * Test identity propagation using SecurityContextAssociation API from the client.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSecurityContextAssociation() throws Exception {
        callUsingSecurityContextAssociation("guest", false, false);
        callUsingSecurityContextAssociation("user1", true, false);
        callUsingSecurityContextAssociation("user2", false, true);
    }

    /**
     * Test identity propagation using LoginContext API from the client.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientLoginModule() throws Exception {
        callUsingClientLoginModule("guest", false, false);
        callUsingClientLoginModule("user1", true, false);
        callUsingClientLoginModule("user2", false, true);
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
         * <pre>
         * &lt;security-domain name=&quot;switch-identity-test&quot; cache-type=&quot;default&quot;&gt;
         *     &lt;authentication&gt;
         *         &lt;login-module code=&quot;{@link GuestDelegationLoginModule}&quot; flag=&quot;optional&quot;&gt;
         *             &lt;module-option name=&quot;password-stacking&quot; value=&quot;useFirstPass&quot;/&gt;
         *         &lt;/login-module&gt;
         *         &lt;login-module code=&quot;Remoting&quot; flag=&quot;optional&quot;&gt;
         *             &lt;module-option name=&quot;password-stacking&quot; value=&quot;useFirstPass&quot;/&gt;
         *         &lt;/login-module&gt;
         *         &lt;login-module code=&quot;RealmDirect&quot; flag=&quot;required&quot;&gt;
         *             &lt;module-option name=&quot;password-stacking&quot; value=&quot;useFirstPass&quot;/&gt;
         *         &lt;/login-module&gt;
         *     &lt;/authentication&gt;
         * &lt;/security-domain&gt;
         * </pre>
         *
         * @see org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask#getSecurityDomains()
         */
        @Override
        protected SecurityDomain[] getSecurityDomains() {
            final SecurityModule.Builder loginModuleBuilder = new SecurityModule.Builder().flag("optional").putOption("password-stacking", "useFirstPass");
            final SecurityDomain sd = // 
            // 
            new SecurityDomain.Builder().name(SwitchIdentityTestCase.SECURITY_DOMAIN_NAME).loginModules(loginModuleBuilder.name(GuestDelegationLoginModule.class.getName()).build(), loginModuleBuilder.name("Remoting").build(), loginModuleBuilder.name("RealmDirect").build()).build();
            return new SecurityDomain[]{ sd };
        }
    }

    /**
     * A {@link ServerSetupTask} instance which creates security realms for this test case.
     *
     * @author Josef Cacek
     */
    static class SecurityRealmsSetup extends AbstractSecurityRealmsServerSetupTask {
        /**
         * Returns SecurityRealms configuration for this testcase.
         *
         * <pre>
         * &lt;security-realm name=&quot;ejb-outbound-realm&quot;&gt;
         *   &lt;server-identities&gt;
         *      &lt;secret value=&quot;xxx&quot;/&gt;
         *   &lt;/server-identities&gt;
         * &lt;/security-realm&gt;
         * </pre>
         */
        @Override
        protected SecurityRealm[] getSecurityRealms() {
            final ServerIdentity serverIdentity = new ServerIdentity.Builder().secretPlain(EJBUtil.CONNECTION_PASSWORD).build();
            final SecurityRealm realm = new SecurityRealm.Builder().name(SwitchIdentityTestCase.EJB_OUTBOUND_REALM).serverIdentity(serverIdentity).build();
            return new SecurityRealm[]{ realm };
        }
    }

    /**
     * An Enum, which holds expected method types in {@link Manage} interface.
     *
     * @author Josef Cacek
     */
    private enum ManageMethodEnum {

        ROLE1,
        ROLE2,
        ALLROLES;}
}

