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
package org.jboss.as.test.integration.ejb.security;


import MatchRule.ALL;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.categories.CommonCriteria;
import org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.integration.security.common.config.SecurityDomain;
import org.jboss.as.test.integration.security.common.config.SecurityModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.security.auth.client.AuthenticationConfiguration;
import org.wildfly.security.auth.client.AuthenticationContext;
import org.wildfly.security.sasl.SaslMechanismSelector;


/**
 * RunAsPrincipal test across legacy security domains.
 *
 * @author Josef Cacek
 */
@RunWith(Arquillian.class)
@ServerSetup({ RunAsPrincipalCustomDomainTestCase.PropertyFilesSetup.class, RunAsPrincipalCustomDomainTestCase.SecurityDomainsSetup.class })
@Category(CommonCriteria.class)
@RunAsClient
public class RunAsPrincipalCustomDomainTestCase {
    private static final String DEPLOYMENT = "runasprincipal-test";

    @Test
    public void test() throws Exception {
        Callable<String> callable = () -> {
            return lookupEntryBean().getCallerPrincipal();
        };
        String caller = AuthenticationContext.empty().with(ALL, AuthenticationConfiguration.empty().useName("guest").usePassword("guest").useRealm("ApplicationRealm").useHost(Utils.getDefaultHost(false)).usePort(8080).setSaslMechanismSelector(SaslMechanismSelector.fromString("DIGEST-MD5"))).runCallable(callable);
        Assert.assertEquals("Unexpected principal name returned", "principalFromEntryBean", caller);
    }

    /**
     * A {@link ServerSetupTask} instance which creates security domains for this test case.
     */
    static class SecurityDomainsSetup extends AbstractSecurityDomainsServerSetupTask {
        @Override
        protected SecurityDomain[] getSecurityDomains() {
            final Map<String, String> lmOptions = new HashMap<String, String>();
            lmOptions.put("usersProperties", RunAsPrincipalCustomDomainTestCase.PropertyFilesSetup.FILE_USERS.getAbsolutePath());
            lmOptions.put("rolesProperties", RunAsPrincipalCustomDomainTestCase.PropertyFilesSetup.FILE_ROLES.getAbsolutePath());
            final SecurityDomain sd = new SecurityDomain.Builder().name(RunAsPrincipalCustomDomainTestCase.DEPLOYMENT).loginModules(new SecurityModule.Builder().name("UsersRoles").flag("required").options(lmOptions).build()).build();
            return new SecurityDomain[]{ sd };
        }
    }

    /**
     * A {@link ServerSetupTask} instance which creates property files with users and roles.
     */
    static class PropertyFilesSetup implements ServerSetupTask {
        public static final File FILE_USERS = new File("test-users.properties");

        public static final File FILE_ROLES = new File("test-roles.properties");

        /**
         * Generates property files.
         */
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            FileUtils.writeStringToFile(RunAsPrincipalCustomDomainTestCase.PropertyFilesSetup.FILE_USERS, "target=target", "ISO-8859-1");
            FileUtils.writeStringToFile(RunAsPrincipalCustomDomainTestCase.PropertyFilesSetup.FILE_ROLES, "target=Target", "ISO-8859-1");
        }

        /**
         * Removes generated property files.
         */
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            RunAsPrincipalCustomDomainTestCase.PropertyFilesSetup.FILE_USERS.delete();
            RunAsPrincipalCustomDomainTestCase.PropertyFilesSetup.FILE_ROLES.delete();
        }
    }
}

