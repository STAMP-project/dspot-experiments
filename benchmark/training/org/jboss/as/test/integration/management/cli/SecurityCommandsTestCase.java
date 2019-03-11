/**
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.management.cli;


import Util.JBOSS_SERVER_CONFIG_DIR;
import java.io.ByteArrayOutputStream;
import java.io.File;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.cli.Util;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author jdenise@redhat.com
 */
@RunWith(Arquillian.class)
public class SecurityCommandsTestCase {
    private static final String DEFAULT_SERVER = "default-server";

    private static final ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();

    private static CommandContext ctx;

    private static final String GENERATED_KEY_STORE_FILE_NAME = "gen-key-store.keystore";

    private static final String GENERATED_PEM_FILE_NAME = "gen-key-store.pem";

    private static final String GENERATED_CSR_FILE_NAME = "gen-key-store.csr";

    private static final String GENERATED_KEY_STORE_PASSWORD = "mysecret";

    private static final String GENERATED_KEY_STORE_ALIAS = "myalias";

    private static final String GENERATED_TRUST_STORE_FILE_NAME = "gen-trust-store.truststore";

    private static final String SERVER_KEY_STORE_FILE = "cli-security-test-server.keystore";

    private static final String CLIENT_KEY_STORE_FILE = "cli-security-test-client.keystore";

    private static final String CLIENT_CERTIFICATE_FILE = "cli-security-test-client-certificate.pem";

    private static final String KEY_STORE_PASSWORD = "secret";

    private static final String KEY_STORE_NAME = "ks1";

    private static final String KEY_MANAGER_NAME = "km1";

    private static final String TRUST_STORE_NAME = "ts1";

    private static final String TRUST_MANAGER_NAME = "tm1";

    private static final String SSL_CONTEXT_NAME = "sslCtx1";

    private static File serverKeyStore;

    private static File clientKeyStore;

    private static File clientCertificate;

    @ClassRule
    public static final TemporaryFolder temporaryUserHome = new TemporaryFolder();

    @Test
    public void testInvalidEnableSSL() throws Exception {
        SecurityCommandsTestCase.assertEmptyModel(null);
        {
            boolean failed = false;
            try {
                SecurityCommandsTestCase.ctx.handle("security enable-ssl-http-server");
            } catch (Exception ex) {
                failed = true;
                // XXX OK, expected
            }
            Assert.assertTrue(failed);
            SecurityCommandsTestCase.assertEmptyModel(null);
        }
        {
            boolean failed = false;
            try {
                SecurityCommandsTestCase.ctx.handle(((((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --no-reload") + " --server-name=foo"));
            } catch (Exception ex) {
                failed = true;
                // XXX OK, expected
            }
            Assert.assertTrue(failed);
            SecurityCommandsTestCase.assertEmptyModel(null);
        }
        // Call the command with an invalid key-store-path.
        {
            boolean failed = false;
            try {
                SecurityCommandsTestCase.ctx.handle(((("security enable-ssl-http-server --key-store-path=" + ("foo.bar" + " --key-store-password=")) + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --no-reload"));
            } catch (Exception ex) {
                failed = true;
                // XXX OK, expected
            }
            Assert.assertTrue(failed);
            SecurityCommandsTestCase.assertEmptyModel(null);
        }
        {
            // Call the command with no password.
            boolean failed = false;
            try {
                SecurityCommandsTestCase.ctx.handle((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --no-reload"));
            } catch (Exception ex) {
                failed = true;
                // XXX OK, expected
            }
            Assert.assertTrue(failed);
            SecurityCommandsTestCase.assertEmptyModel(null);
        }
        {
            // Call the command with an invalid key-store-name.
            boolean failed = false;
            try {
                SecurityCommandsTestCase.ctx.handle(("security enable-ssl-http-server --key-store-name=" + ("foo.bar" + " --no-reload")));
            } catch (Exception ex) {
                failed = true;
                // XXX OK, expected
            }
            Assert.assertTrue(failed);
            SecurityCommandsTestCase.assertEmptyModel(null);
        }
        {
            boolean failed = false;
            // call the command with both key-store-name and path
            SecurityCommandsTestCase.ctx.handle(((((("/subsystem=elytron/key-store=foo:add(path=foo.bar, type=JKS" + ", relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + ", credential-reference={clear-text=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + "})"));
            try {
                try {
                    SecurityCommandsTestCase.ctx.handle(((((((("security enable-ssl-http-server --key-store-name=foo" + " --key-store-path=") + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --no-reload"));
                } catch (Exception ex) {
                    failed = true;
                    // XXX OK, expected
                }
            } finally {
                SecurityCommandsTestCase.ctx.handle("/subsystem=elytron/key-store=foo:remove()");
            }
            Assert.assertTrue(failed);
            SecurityCommandsTestCase.assertEmptyModel(null);
        }
        {
            boolean failed = false;
            // call the command with both trust-store-name and certificate path
            SecurityCommandsTestCase.ctx.handle(((((("/subsystem=elytron/key-store=foo:add(path=foo.bar, type=JKS" + ", relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + ", credential-reference={clear-text=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + "})"));
            try {
                try {
                    SecurityCommandsTestCase.ctx.handle(((((((((((((((((((((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --trusted-certificate-path=") + (SecurityCommandsTestCase.clientCertificate.getAbsolutePath())) + " --trust-store-name=foo") + " --trust-store-file-name=") + (SecurityCommandsTestCase.GENERATED_TRUST_STORE_FILE_NAME)) + " --trust-store-file-password=") + (SecurityCommandsTestCase.GENERATED_KEY_STORE_PASSWORD)) + " --new-trust-store-name=") + (SecurityCommandsTestCase.TRUST_STORE_NAME)) + " --new-trust-manager-name=") + (SecurityCommandsTestCase.TRUST_MANAGER_NAME)) + " --new-key-store-name=") + (SecurityCommandsTestCase.KEY_STORE_NAME)) + " --new-key-manager-name=") + (SecurityCommandsTestCase.KEY_MANAGER_NAME)) + " --new-ssl-context-name=") + (SecurityCommandsTestCase.SSL_CONTEXT_NAME)) + " --no-reload"));
                } catch (Exception ex) {
                    failed = true;
                    // XXX OK, expected
                }
            } finally {
                SecurityCommandsTestCase.ctx.handle("/subsystem=elytron/key-store=foo:remove()");
            }
            Assert.assertTrue(failed);
            SecurityCommandsTestCase.assertEmptyModel(null);
        }
    }

    @Test
    public void testEnableSSLTwoWay() throws Exception {
        SecurityCommandsTestCase.assertEmptyModel(null);
        // first validation must fail
        boolean failed = false;
        try {
            SecurityCommandsTestCase.ctx.handle((((((((((((((((((((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --trusted-certificate-path=") + (SecurityCommandsTestCase.clientCertificate.getAbsolutePath())) + " --trust-store-file-name=") + (SecurityCommandsTestCase.GENERATED_TRUST_STORE_FILE_NAME)) + " --trust-store-file-password=") + (SecurityCommandsTestCase.GENERATED_KEY_STORE_PASSWORD)) + " --new-trust-store-name=") + (SecurityCommandsTestCase.TRUST_STORE_NAME)) + " --new-trust-manager-name=") + (SecurityCommandsTestCase.TRUST_MANAGER_NAME)) + " --new-key-store-name=") + (SecurityCommandsTestCase.KEY_STORE_NAME)) + " --new-key-manager-name=") + (SecurityCommandsTestCase.KEY_MANAGER_NAME)) + " --new-ssl-context-name=") + (SecurityCommandsTestCase.SSL_CONTEXT_NAME)) + " --no-reload"));
        } catch (Exception ex) {
            failed = true;
        }
        Assert.assertTrue(failed);
        // Call the command without validation and no-reload.
        SecurityCommandsTestCase.ctx.handle(((((((((((((((((((((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --trusted-certificate-path=") + (SecurityCommandsTestCase.clientCertificate.getAbsolutePath())) + " --trust-store-file-name=") + (SecurityCommandsTestCase.GENERATED_TRUST_STORE_FILE_NAME)) + " --trust-store-file-password=") + (SecurityCommandsTestCase.GENERATED_KEY_STORE_PASSWORD)) + " --new-trust-store-name=") + (SecurityCommandsTestCase.TRUST_STORE_NAME)) + " --new-trust-manager-name=") + (SecurityCommandsTestCase.TRUST_MANAGER_NAME)) + " --new-key-store-name=") + (SecurityCommandsTestCase.KEY_STORE_NAME)) + " --new-key-manager-name=") + (SecurityCommandsTestCase.KEY_MANAGER_NAME)) + " --new-ssl-context-name=") + (SecurityCommandsTestCase.SSL_CONTEXT_NAME)) + " --no-trusted-certificate-validation") + " --no-reload"));
        File genTrustStore = null;
        try {
            SecurityCommandsTestCase.assertTLSNumResources(2, 1, 1, 1);
            // Check that the trustStore has been generated.
            genTrustStore = new File((((((((TestSuiteEnvironment.getSystemProperty("jboss.home")) + (File.separator)) + "standalone") + (File.separator)) + "configuration") + (File.separator)) + (SecurityCommandsTestCase.GENERATED_TRUST_STORE_FILE_NAME)));
            Assert.assertTrue(genTrustStore.exists());
            // Check the model contains the provided values.
            SecurityCommandsTestCase.checkModel(null, SecurityCommandsTestCase.SERVER_KEY_STORE_FILE, JBOSS_SERVER_CONFIG_DIR, SecurityCommandsTestCase.KEY_STORE_PASSWORD, SecurityCommandsTestCase.GENERATED_TRUST_STORE_FILE_NAME, SecurityCommandsTestCase.GENERATED_KEY_STORE_PASSWORD, SecurityCommandsTestCase.KEY_STORE_NAME, SecurityCommandsTestCase.KEY_MANAGER_NAME, SecurityCommandsTestCase.TRUST_STORE_NAME, SecurityCommandsTestCase.TRUST_MANAGER_NAME, SecurityCommandsTestCase.SSL_CONTEXT_NAME);
        } finally {
            if (genTrustStore != null) {
                genTrustStore.delete();
            }
        }
        SecurityCommandsTestCase.ctx.handle("security disable-ssl-http-server --no-reload");
        // Re-use the trust-store generated in previous step.
        SecurityCommandsTestCase.ctx.handle((((((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --trust-store-name=") + (SecurityCommandsTestCase.TRUST_STORE_NAME)) + " --no-reload"));
        SecurityCommandsTestCase.assertTLSNumResources(2, 1, 1, 1);
        // Check that the model has not been updated.
        SecurityCommandsTestCase.checkModel(null, SecurityCommandsTestCase.SERVER_KEY_STORE_FILE, JBOSS_SERVER_CONFIG_DIR, SecurityCommandsTestCase.KEY_STORE_PASSWORD, SecurityCommandsTestCase.GENERATED_TRUST_STORE_FILE_NAME, SecurityCommandsTestCase.GENERATED_KEY_STORE_PASSWORD, SecurityCommandsTestCase.KEY_STORE_NAME, SecurityCommandsTestCase.KEY_MANAGER_NAME, SecurityCommandsTestCase.TRUST_STORE_NAME, SecurityCommandsTestCase.TRUST_MANAGER_NAME, SecurityCommandsTestCase.SSL_CONTEXT_NAME);
        SecurityCommandsTestCase.ctx.handle("security disable-ssl-http-server --no-reload");
    }

    @Test
    public void testEnableSSLDefaultServer() throws Exception {
        testEnableSSL(SecurityCommandsTestCase.DEFAULT_SERVER);
    }

    @Test
    public void testEnableSSL() throws Exception {
        testEnableSSL(null);
    }

    @Test
    public void testEnableSSLInteractiveConfirm() throws Exception {
        testEnableSSLInteractiveConfirm(null);
    }

    @Test
    public void testEnableSSLInteractiveConfirmDefaultServer() throws Exception {
        testEnableSSLInteractiveConfirm(SecurityCommandsTestCase.DEFAULT_SERVER);
    }

    @Test
    public void testEnableSSLInteractiveNoConfirm() throws Exception {
        SecurityCommandsTestCase.assertEmptyModel(null);
        CliProcessWrapper cli = new CliProcessWrapper().addJavaOption(("-Duser.home=" + (SecurityCommandsTestCase.temporaryUserHome.getRoot().toPath().toString()))).addCliArgument(((("--controller=remote+http://" + (TestSuiteEnvironment.getServerAddress())) + ":") + (TestSuiteEnvironment.getServerPort()))).addCliArgument("--connect");
        try {
            cli.executeInteractive();
            cli.clearOutput();
            Assert.assertTrue(cli.pushLineAndWaitForResults("security enable-ssl-http-server --interactive --no-reload", "Key-store file name"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "Password"));
            // Loop until DN has been provided.
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "What is your first and last name? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "What is the name of your organizational unit? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "What is the name of your organization? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "What is the name of your City or Locality? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "What is the name of your State or Province? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "What is the two-letter country code for this unit? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "Is CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown correct y/n [y]"));
            cli.clearOutput();
            Assert.assertTrue(cli.pushLineAndWaitForResults("n", "What is your first and last name? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("foo", "What is the name of your organizational unit? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("bar", "What is the name of your organization? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("foofoo", "What is the name of your City or Locality? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("barbar", "What is the name of your State or Province? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("toto", "What is the two-letter country code for this unit? [Unknown]"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("TO", "Is CN=foo, OU=bar, O=foofoo, L=barbar, ST=toto, C=TO correct y/n [y]"));
            // Loop until value is valid.
            Assert.assertTrue(cli.pushLineAndWaitForResults("y", "Validity"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("Hello", "Validity"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "Alias"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "Enable SSL Mutual Authentication"));
            cli.clearOutput();
            // Loop until value is y or n.
            Assert.assertTrue(cli.pushLineAndWaitForResults("TT", "Enable SSL Mutual Authentication"));
            cli.clearOutput();
            Assert.assertTrue(cli.pushLineAndWaitForResults("COCO", "Enable SSL Mutual Authentication"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("y", "Client certificate (path to pem file)"));
            cli.clearOutput();
            // Loop until certificate file exists
            Assert.assertTrue(cli.pushLineAndWaitForResults("foo.bar", "Client certificate (path to pem file)"));
            Assert.assertTrue(cli.pushLineAndWaitForResults(SecurityCommandsTestCase.clientCertificate.getAbsolutePath(), "Validate certificate"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "Trust-store file name"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "Password"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("", "Do you confirm"));
            cli.clearOutput();
            Assert.assertTrue(cli.pushLineAndWaitForResults("PP", "Do you confirm"));
            cli.clearOutput();
            Assert.assertTrue(cli.pushLineAndWaitForResults("COCO", "Do you confirm"));
            Assert.assertTrue(cli.pushLineAndWaitForResults("n", null));
            SecurityCommandsTestCase.assertEmptyModel(null);
        } catch (Throwable ex) {
            throw new Exception(cli.getOutput(), ex);
        } finally {
            cli.destroyProcess();
        }
    }

    @Test
    public void testKeyStoreDifferentPassword() throws Exception {
        SecurityCommandsTestCase.assertEmptyModel(null);
        // Create a credential store and alias
        SecurityCommandsTestCase.ctx.handle(("/subsystem=elytron/credential-store=cs:add(credential-reference={clear-text=cs-secret}," + "create,location=cs.store,relative-to=jboss.server.config.dir"));
        try {
            SecurityCommandsTestCase.ctx.handle("/subsystem=elytron/credential-store=cs:add-alias(alias=xxx,secret-value=secret");
            SecurityCommandsTestCase.ctx.handle(((("/subsystem=elytron/key-store=ks1:add(credential-reference={alias=xxx,store=cs},type=JKS,relative-to=" + (Util.JBOSS_SERVER_CONFIG_DIR)) + ",path=") + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)));
            SecurityCommandsTestCase.assertTLSNumResources(1, 0, 0, 0);
            // Don't reuse the ks because different credential-reference
            SecurityCommandsTestCase.ctx.handle((((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --no-reload"));
            SecurityCommandsTestCase.assertTLSNumResources(2, 1, 0, 1);
            SecurityCommandsTestCase.ctx.handle("security disable-ssl-http-server --no-reload");
        } finally {
            File credentialStores = new File((((((((TestSuiteEnvironment.getSystemProperty("jboss.home")) + (File.separator)) + "standalone") + (File.separator)) + "configuration") + (File.separator)) + "cs.store"));
            credentialStores.delete();
        }
    }

    @Test
    public void testKeyStoreDifferentAlias() throws Exception {
        SecurityCommandsTestCase.assertEmptyModel(null);
        // add a key-store with different alias.
        SecurityCommandsTestCase.ctx.handle(((((("/subsystem=elytron/key-store=ks3:add(credential-reference={clear-text=secret}," + "type=JKS,relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + ",path=") + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + ",alias-filter=foo"));
        SecurityCommandsTestCase.assertTLSNumResources(1, 0, 0, 0);
        // Don't reuse the key-store because different alias-filter
        SecurityCommandsTestCase.ctx.handle((((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --no-reload"));
        SecurityCommandsTestCase.assertTLSNumResources(2, 1, 0, 1);
        SecurityCommandsTestCase.ctx.handle("security disable-ssl-http-server --no-reload");
    }

    @Test
    public void testKeyManagerDifferentAlgorithm() throws Exception {
        SecurityCommandsTestCase.assertEmptyModel(null);
        // add a key-store that will be reused.
        SecurityCommandsTestCase.ctx.handle((((("/subsystem=elytron/key-store=ks1:add(credential-reference={clear-text=secret}," + "type=JKS,relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + ",path=") + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)));
        // A leymanager that will be not re-used.
        SecurityCommandsTestCase.ctx.handle("/subsystem=elytron/key-manager=km:add(algorithm=PKIX,credential-reference={clear-text=secret},key-store=ks1");
        SecurityCommandsTestCase.assertTLSNumResources(1, 1, 0, 0);
        // Reuse the key-store but create a new key-manager
        SecurityCommandsTestCase.ctx.handle((((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --no-reload"));
        SecurityCommandsTestCase.assertTLSNumResources(1, 2, 0, 1);
        SecurityCommandsTestCase.ctx.handle("security disable-ssl-http-server --no-reload");
    }

    @Test
    public void testKeyManagerDifferentAlias() throws Exception {
        SecurityCommandsTestCase.assertEmptyModel(null);
        // add a key-store that will be reused.
        SecurityCommandsTestCase.ctx.handle((((("/subsystem=elytron/key-store=ks1:add(credential-reference={clear-text=secret}," + "type=JKS,relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + ",path=") + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)));
        // A keymanager that will be not re-used.
        SecurityCommandsTestCase.ctx.handle("/subsystem=elytron/key-manager=km:add(alias-filter=foo,credential-reference={clear-text=secret},key-store=ks1");
        SecurityCommandsTestCase.assertTLSNumResources(1, 1, 0, 0);
        // Reuse the key-store but create a new key-manager
        SecurityCommandsTestCase.ctx.handle((((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --no-reload"));
        SecurityCommandsTestCase.assertTLSNumResources(1, 2, 0, 1);
        SecurityCommandsTestCase.ctx.handle("security disable-ssl-http-server --no-reload");
    }

    @Test
    public void testSSLContextDifferentNeedWant() throws Exception {
        SecurityCommandsTestCase.assertEmptyModel(null);
        // add a key-store that will be reused.
        SecurityCommandsTestCase.ctx.handle((((("/subsystem=elytron/key-store=ks1:add(credential-reference={clear-text=secret}," + "type=JKS,relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + ",path=") + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)));
        // A keymanager that will be reused.
        SecurityCommandsTestCase.ctx.handle("/subsystem=elytron/key-manager=km:add(credential-reference={clear-text=secret},key-store=ks1");
        // An SSLContext not reused.
        SecurityCommandsTestCase.ctx.handle("/subsystem=elytron/server-ssl-context=ctx:add(key-manager=km,need-client-auth=true,want-client-auth=true)");
        SecurityCommandsTestCase.assertTLSNumResources(1, 1, 0, 1);
        // Reuse the key-store but create a new key-manager
        SecurityCommandsTestCase.ctx.handle((((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --no-reload"));
        SecurityCommandsTestCase.assertTLSNumResources(1, 1, 0, 2);
        SecurityCommandsTestCase.ctx.handle("security disable-ssl-http-server --no-reload");
    }

    @Test
    public void testSSLContextDifferentTrustManager() throws Exception {
        SecurityCommandsTestCase.assertEmptyModel(null);
        // add a key-store that will be reused.
        SecurityCommandsTestCase.ctx.handle((((("/subsystem=elytron/key-store=ks1:add(credential-reference={clear-text=secret}," + "type=JKS,relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + ",path=") + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)));
        // A keymanager that will be reused.
        SecurityCommandsTestCase.ctx.handle("/subsystem=elytron/key-manager=km:add(credential-reference={clear-text=secret},key-store=ks1");
        // A trust manager.
        SecurityCommandsTestCase.ctx.handle("/subsystem=elytron/trust-manager=tm:add(key-store=ks1");
        // An SSLContext not reused.
        SecurityCommandsTestCase.ctx.handle("/subsystem=elytron/server-ssl-context=ctx:add(key-manager=km,trust-manager=tm)");
        SecurityCommandsTestCase.assertTLSNumResources(1, 1, 1, 1);
        // Reuse the key-store but create a new key-manager
        SecurityCommandsTestCase.ctx.handle((((((("security enable-ssl-http-server --key-store-path=" + (SecurityCommandsTestCase.SERVER_KEY_STORE_FILE)) + " --key-store-password=") + (SecurityCommandsTestCase.KEY_STORE_PASSWORD)) + " --key-store-path-relative-to=") + (Util.JBOSS_SERVER_CONFIG_DIR)) + " --no-reload"));
        SecurityCommandsTestCase.assertTLSNumResources(1, 1, 1, 2);
        SecurityCommandsTestCase.ctx.handle("security disable-ssl-http-server --no-reload");
    }
}

