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
package org.wildfly.test.integration.elytron.ssl;


import java.io.File;
import java.net.URL;
import org.apache.http.client.HttpClient;
import org.codehaus.plexus.util.FileUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.categories.CommonCriteria;
import org.jboss.as.test.integration.security.common.CoreUtils;
import org.jboss.as.test.integration.security.common.SSLTruststoreUtil;
import org.jboss.as.test.integration.security.common.SecurityTestConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.test.security.common.AbstractElytronSetupTask;
import org.wildfly.test.security.common.elytron.ClientCertUndertowDomainMapper;
import org.wildfly.test.security.common.elytron.ConcatenatingPrincipalDecoder;
import org.wildfly.test.security.common.elytron.ConfigurableElement;
import org.wildfly.test.security.common.elytron.ConstantPrincipalDecoder;
import org.wildfly.test.security.common.elytron.CredentialReference;
import org.wildfly.test.security.common.elytron.KeyStoreRealm;
import org.wildfly.test.security.common.elytron.Path;
import org.wildfly.test.security.common.elytron.PropertyFileAuthzBasedDomain;
import org.wildfly.test.security.common.elytron.SimpleKeyManager;
import org.wildfly.test.security.common.elytron.SimpleKeyStore;
import org.wildfly.test.security.common.elytron.SimpleServerSslContext;
import org.wildfly.test.security.common.elytron.SimpleTrustManager;
import org.wildfly.test.security.common.elytron.UndertowSslContext;
import org.wildfly.test.security.common.elytron.UserWithRoles;
import org.wildfly.test.security.common.elytron.X500AttributePrincipalDecoder;


/**
 * Smoke tests for certificate based authentication using Elytron server-ssl-context, security domain,
 * and key store realm.
 *
 * This test case is preparation and temporary replacement for
 * testsuite/integration/web/src/test/java/org/jboss/as/test/integration/web/security/cert/WebSecurityCERTTestCase.java
 * before making it work with Elytron.
 *
 * @author Ondrej Kotek
 */
@RunWith(Arquillian.class)
@ServerSetup({ UndertowSslSecurityDomainTestCase.ElytronSslContextInUndertowSetupTask.class })
@RunAsClient
@Category(CommonCriteria.class)
public class UndertowSslSecurityDomainTestCase {
    private static final String NAME = UndertowSslSecurityDomainTestCase.class.getSimpleName();

    private static final File WORK_DIR = new File((("target" + (File.separatorChar)) + (UndertowSslSecurityDomainTestCase.NAME)));

    private static final File SERVER_KEYSTORE_FILE = new File(UndertowSslSecurityDomainTestCase.WORK_DIR, SecurityTestConstants.SERVER_KEYSTORE);

    private static final File SERVER_TRUSTSTORE_FILE = new File(UndertowSslSecurityDomainTestCase.WORK_DIR, SecurityTestConstants.SERVER_TRUSTSTORE);

    private static final File CLIENT_KEYSTORE_FILE = new File(UndertowSslSecurityDomainTestCase.WORK_DIR, SecurityTestConstants.CLIENT_KEYSTORE);

    private static final File CLIENT_TRUSTSTORE_FILE = new File(UndertowSslSecurityDomainTestCase.WORK_DIR, SecurityTestConstants.CLIENT_TRUSTSTORE);

    private static final File UNTRUSTED_STORE_FILE = new File(UndertowSslSecurityDomainTestCase.WORK_DIR, SecurityTestConstants.UNTRUSTED_KEYSTORE);

    private static final String PASSWORD = SecurityTestConstants.KEYSTORE_PASSWORD;

    private static URL securedUrl;

    private static URL securedUrlRole1;

    private static URL securedUrlRole2;

    /**
     * Tests access to resource that does not require authentication.
     */
    @Test
    public void testUnprotectedAccess() {
        HttpClient client = SSLTruststoreUtil.getHttpClientWithSSL(UndertowSslSecurityDomainTestCase.CLIENT_KEYSTORE_FILE, UndertowSslSecurityDomainTestCase.PASSWORD, UndertowSslSecurityDomainTestCase.CLIENT_TRUSTSTORE_FILE, UndertowSslSecurityDomainTestCase.PASSWORD);
        assertUnprotectedAccess(client);
        closeClient(client);
    }

    /**
     * Tests access to resource that requires authentication and authorization.
     */
    @Test
    public void testProtectedAccess() {
        HttpClient client = SSLTruststoreUtil.getHttpClientWithSSL(UndertowSslSecurityDomainTestCase.CLIENT_KEYSTORE_FILE, UndertowSslSecurityDomainTestCase.PASSWORD, UndertowSslSecurityDomainTestCase.CLIENT_TRUSTSTORE_FILE, UndertowSslSecurityDomainTestCase.PASSWORD);
        assertProtectedAccess(client, SC_OK);
        closeClient(client);
    }

    /**
     * Tests access to resource that requires authentication and authorization. Principal has not required role.
     */
    @Test
    public void testForbidden() {
        HttpClient client = SSLTruststoreUtil.getHttpClientWithSSL(UndertowSslSecurityDomainTestCase.CLIENT_KEYSTORE_FILE, UndertowSslSecurityDomainTestCase.PASSWORD, UndertowSslSecurityDomainTestCase.CLIENT_TRUSTSTORE_FILE, UndertowSslSecurityDomainTestCase.PASSWORD);
        assertAccessForbidden(client);
        closeClient(client);
    }

    /**
     * Tests access to resource that requires authentication and authorization. Client has not trusted certificate.
     */
    @Test
    public void testUntrustedCertificate() {
        HttpClient client = SSLTruststoreUtil.getHttpClientWithSSL(UndertowSslSecurityDomainTestCase.UNTRUSTED_STORE_FILE, UndertowSslSecurityDomainTestCase.PASSWORD, UndertowSslSecurityDomainTestCase.CLIENT_TRUSTSTORE_FILE, UndertowSslSecurityDomainTestCase.PASSWORD);
        assertProtectedAccess(client, SC_FORBIDDEN);
        closeClient(client);
    }

    /**
     * Creates Elytron server-ssl-context and key/trust stores.
     */
    static class ElytronSslContextInUndertowSetupTask extends AbstractElytronSetupTask {
        @Override
        protected void setup(final ModelControllerClient modelControllerClient) throws Exception {
            UndertowSslSecurityDomainTestCase.ElytronSslContextInUndertowSetupTask.keyMaterialSetup(UndertowSslSecurityDomainTestCase.WORK_DIR);
            super.setup(modelControllerClient);
        }

        @Override
        protected ConfigurableElement[] getConfigurableElements() {
            return new ConfigurableElement[]{ SimpleKeyStore.builder().withName(((UndertowSslSecurityDomainTestCase.NAME) + (SecurityTestConstants.SERVER_KEYSTORE))).withPath(Path.builder().withPath(UndertowSslSecurityDomainTestCase.SERVER_KEYSTORE_FILE.getPath()).build()).withCredentialReference(CredentialReference.builder().withClearText(UndertowSslSecurityDomainTestCase.PASSWORD).build()).build(), SimpleKeyStore.builder().withName(((UndertowSslSecurityDomainTestCase.NAME) + (SecurityTestConstants.SERVER_TRUSTSTORE))).withPath(Path.builder().withPath(UndertowSslSecurityDomainTestCase.SERVER_TRUSTSTORE_FILE.getPath()).build()).withCredentialReference(CredentialReference.builder().withClearText(UndertowSslSecurityDomainTestCase.PASSWORD).build()).build(), SimpleKeyManager.builder().withName(UndertowSslSecurityDomainTestCase.NAME).withKeyStore(((UndertowSslSecurityDomainTestCase.NAME) + (SecurityTestConstants.SERVER_KEYSTORE))).withCredentialReference(CredentialReference.builder().withClearText(UndertowSslSecurityDomainTestCase.PASSWORD).build()).build(), SimpleTrustManager.builder().withName(UndertowSslSecurityDomainTestCase.NAME).withKeyStore(((UndertowSslSecurityDomainTestCase.NAME) + (SecurityTestConstants.SERVER_TRUSTSTORE))).build(), KeyStoreRealm.builder().withName(UndertowSslSecurityDomainTestCase.NAME).withKeyStore(((UndertowSslSecurityDomainTestCase.NAME) + (SecurityTestConstants.SERVER_TRUSTSTORE))).build(), ConstantPrincipalDecoder.builder().withName(((UndertowSslSecurityDomainTestCase.NAME) + "constant")).withConstant("CN").build(), X500AttributePrincipalDecoder.builder().withName(((UndertowSslSecurityDomainTestCase.NAME) + "X500")).withOid("2.5.4.3").withMaximumSegments(1).build(), ConcatenatingPrincipalDecoder.builder().withName(UndertowSslSecurityDomainTestCase.NAME).withJoiner("=").withDecoders(((UndertowSslSecurityDomainTestCase.NAME) + "constant"), ((UndertowSslSecurityDomainTestCase.NAME) + "X500")).build(), PropertyFileAuthzBasedDomain.builder().withName(UndertowSslSecurityDomainTestCase.NAME).withAuthnRealm(UndertowSslSecurityDomainTestCase.NAME).withPrincipalDecoder(UndertowSslSecurityDomainTestCase.NAME).withUser(UserWithRoles.builder().withName("CN=client").withRoles("Role1").build()).build(), ClientCertUndertowDomainMapper.builder().withName(UndertowSslSecurityDomainTestCase.NAME).withSecurityDomain(UndertowSslSecurityDomainTestCase.NAME).build(), SimpleServerSslContext.builder().withName(UndertowSslSecurityDomainTestCase.NAME).withKeyManagers(UndertowSslSecurityDomainTestCase.NAME).withTrustManagers(UndertowSslSecurityDomainTestCase.NAME).withSecurityDomain(UndertowSslSecurityDomainTestCase.NAME).withAuthenticationOptional(true).build(), UndertowSslContext.builder().withName(UndertowSslSecurityDomainTestCase.NAME).build() };
        }

        @Override
        protected void tearDown(ModelControllerClient modelControllerClient) throws Exception {
            super.tearDown(modelControllerClient);
            FileUtils.deleteDirectory(UndertowSslSecurityDomainTestCase.WORK_DIR);
        }

        protected static void keyMaterialSetup(File workDir) throws Exception {
            FileUtils.deleteDirectory(workDir);
            workDir.mkdirs();
            Assert.assertTrue(workDir.exists());
            Assert.assertTrue(workDir.isDirectory());
            CoreUtils.createKeyMaterial(workDir);
        }
    }
}

