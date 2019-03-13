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
import org.jboss.as.test.integration.security.common.CoreUtils;
import org.jboss.as.test.integration.security.common.SSLTruststoreUtil;
import org.jboss.as.test.integration.security.common.SecurityTestConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.test.security.common.AbstractElytronSetupTask;
import org.wildfly.test.security.common.elytron.ConfigurableElement;
import org.wildfly.test.security.common.elytron.CredentialReference;
import org.wildfly.test.security.common.elytron.Path;
import org.wildfly.test.security.common.elytron.SimpleKeyManager;
import org.wildfly.test.security.common.elytron.SimpleKeyStore;
import org.wildfly.test.security.common.elytron.SimpleServerSslContext;
import org.wildfly.test.security.common.elytron.SimpleTrustManager;
import org.wildfly.test.security.common.elytron.UndertowSslContext;


/**
 * Smoke test for two way SSL connection with Undertow HTTPS listener backed by Elytron server-ssl-context with default
 * settings (client certificate is not required).
 *
 * In case the client certificate is not trusted or present, the request should be successful.
 *
 * @author Ondrej Kotek
 */
@RunWith(Arquillian.class)
@ServerSetup({ UndertowTwoWaySslTestCase.ElytronSslContextInUndertowSetupTask.class })
@RunAsClient
public class UndertowTwoWaySslTestCase {
    private static final String NAME = UndertowTwoWaySslTestCase.class.getSimpleName();

    private static final File WORK_DIR = new File((("target" + (File.separatorChar)) + (UndertowTwoWaySslTestCase.NAME)));

    private static final File SERVER_KEYSTORE_FILE = new File(UndertowTwoWaySslTestCase.WORK_DIR, SecurityTestConstants.SERVER_KEYSTORE);

    private static final File SERVER_TRUSTSTORE_FILE = new File(UndertowTwoWaySslTestCase.WORK_DIR, SecurityTestConstants.SERVER_TRUSTSTORE);

    private static final File CLIENT_KEYSTORE_FILE = new File(UndertowTwoWaySslTestCase.WORK_DIR, SecurityTestConstants.CLIENT_KEYSTORE);

    private static final File CLIENT_TRUSTSTORE_FILE = new File(UndertowTwoWaySslTestCase.WORK_DIR, SecurityTestConstants.CLIENT_TRUSTSTORE);

    private static final File UNTRUSTED_STORE_FILE = new File(UndertowTwoWaySslTestCase.WORK_DIR, SecurityTestConstants.UNTRUSTED_KEYSTORE);

    private static final String PASSWORD = SecurityTestConstants.KEYSTORE_PASSWORD;

    private static URL securedRootUrl;

    @Test
    public void testSendingTrustedClientCertificate() {
        HttpClient client = SSLTruststoreUtil.getHttpClientWithSSL(UndertowTwoWaySslTestCase.CLIENT_KEYSTORE_FILE, UndertowTwoWaySslTestCase.PASSWORD, UndertowTwoWaySslTestCase.CLIENT_TRUSTSTORE_FILE, UndertowTwoWaySslTestCase.PASSWORD);
        assertConnectionToServer(client, SC_OK);
        closeClient(client);
    }

    @Test
    public void testSendingNonTrustedClientCertificate() {
        HttpClient client = SSLTruststoreUtil.getHttpClientWithSSL(UndertowTwoWaySslTestCase.UNTRUSTED_STORE_FILE, UndertowTwoWaySslTestCase.PASSWORD, UndertowTwoWaySslTestCase.CLIENT_TRUSTSTORE_FILE, UndertowTwoWaySslTestCase.PASSWORD);
        assertConnectionToServer(client, SC_OK);
        closeClient(client);
    }

    @Test
    public void testSendingNoClientCertificate() {
        HttpClient client = SSLTruststoreUtil.getHttpClientWithSSL(UndertowTwoWaySslTestCase.CLIENT_TRUSTSTORE_FILE, UndertowTwoWaySslTestCase.PASSWORD);
        assertConnectionToServer(client, SC_OK);
        closeClient(client);
    }

    /**
     * Creates Elytron server-ssl-context and key/trust stores.
     */
    static class ElytronSslContextInUndertowSetupTask extends AbstractElytronSetupTask {
        @Override
        protected void setup(final ModelControllerClient modelControllerClient) throws Exception {
            UndertowTwoWaySslTestCase.ElytronSslContextInUndertowSetupTask.keyMaterialSetup(UndertowTwoWaySslTestCase.WORK_DIR);
            super.setup(modelControllerClient);
        }

        @Override
        protected ConfigurableElement[] getConfigurableElements() {
            return new ConfigurableElement[]{ SimpleKeyStore.builder().withName(((UndertowTwoWaySslTestCase.NAME) + (SecurityTestConstants.SERVER_KEYSTORE))).withPath(Path.builder().withPath(UndertowTwoWaySslTestCase.SERVER_KEYSTORE_FILE.getPath()).build()).withCredentialReference(CredentialReference.builder().withClearText(UndertowTwoWaySslTestCase.PASSWORD).build()).build(), SimpleKeyStore.builder().withName(((UndertowTwoWaySslTestCase.NAME) + (SecurityTestConstants.SERVER_TRUSTSTORE))).withPath(Path.builder().withPath(UndertowTwoWaySslTestCase.SERVER_TRUSTSTORE_FILE.getPath()).build()).withCredentialReference(CredentialReference.builder().withClearText(UndertowTwoWaySslTestCase.PASSWORD).build()).build(), SimpleKeyManager.builder().withName(UndertowTwoWaySslTestCase.NAME).withKeyStore(((UndertowTwoWaySslTestCase.NAME) + (SecurityTestConstants.SERVER_KEYSTORE))).withCredentialReference(CredentialReference.builder().withClearText(UndertowTwoWaySslTestCase.PASSWORD).build()).build(), SimpleTrustManager.builder().withName(UndertowTwoWaySslTestCase.NAME).withKeyStore(((UndertowTwoWaySslTestCase.NAME) + (SecurityTestConstants.SERVER_TRUSTSTORE))).build(), SimpleServerSslContext.builder().withName(UndertowTwoWaySslTestCase.NAME).withKeyManagers(UndertowTwoWaySslTestCase.NAME).withTrustManagers(UndertowTwoWaySslTestCase.NAME).build(), UndertowSslContext.builder().withName(UndertowTwoWaySslTestCase.NAME).build() };
        }

        @Override
        protected void tearDown(ModelControllerClient modelControllerClient) throws Exception {
            super.tearDown(modelControllerClient);
            FileUtils.deleteDirectory(UndertowTwoWaySslTestCase.WORK_DIR);
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

