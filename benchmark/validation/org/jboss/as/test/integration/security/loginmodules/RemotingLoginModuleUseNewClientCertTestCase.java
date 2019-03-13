/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.security.loginmodules;


import HelloBean.HELLO_WORLD;
import RealmKeystore.Builder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJBAccessException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.net.ssl.SSLContext;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask;
import org.jboss.as.test.integration.security.common.AbstractSecurityRealmsServerSetupTask;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.integration.security.common.config.SecurityDomain;
import org.jboss.as.test.integration.security.common.config.SecurityModule;
import org.jboss.as.test.integration.security.common.config.realm.Authentication;
import org.jboss.as.test.integration.security.common.config.realm.RealmKeystore;
import org.jboss.as.test.integration.security.common.config.realm.SecurityRealm;
import org.jboss.as.test.integration.security.common.config.realm.ServerIdentity;
import org.jboss.as.test.integration.security.common.ejb3.Hello;
import org.jboss.as.test.integration.security.common.ejb3.HelloBean;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picketbox.util.KeyStoreUtil;


/**
 * A testcase for {@link org.jboss.as.security.remoting.RemotingLoginModule} with useClientCert property on RemoteLoginModule. This test covers scenario when an EJB clients use
 * certificate for authentication.
 * </p>
 * It was derived from {@link RemotingLoginModuleTestCase}.
 *
 * @author Josef Cacek
 * @author Ivo Studensky
 */
@RunWith(Arquillian.class)
@ServerSetup({ RemotingLoginModuleUseNewClientCertTestCase.FilesSetup.class, RemotingLoginModuleUseNewClientCertTestCase.SecurityRealmsSetup.class, RemotingLoginModuleUseNewClientCertTestCase.RemotingSetup.class, RemotingLoginModuleUseNewClientCertTestCase.SecurityDomainsSetup.class })
@RunAsClient
public class RemotingLoginModuleUseNewClientCertTestCase {
    private static Logger LOGGER = Logger.getLogger(RemotingLoginModuleUseNewClientCertTestCase.class);

    private static final String TEST_NAME = "remoting-lm-uncc-test";

    /**
     * The LOOKUP_NAME
     */
    private static final String HELLOBEAN_LOOKUP_NAME = (((("/" + (RemotingLoginModuleUseNewClientCertTestCase.TEST_NAME)) + "/") + (HelloBean.class.getSimpleName())) + "!") + (Hello.class.getName());

    private static final String KEYSTORE_PASSWORD = "123456";

    private static final String SERVER_NAME = "server";

    private static final String CLIENT_AUTHORIZED_NAME = "client";

    private static final String CLIENT_NOT_AUTHORIZED_NAME = "clientNotAuthorized";

    private static final String CLIENT_NOT_TRUSTED_NAME = "clientNotTrusted";

    private static final String KEYSTORE_SUFFIX = ".keystore";

    private static final File WORK_DIR = new File(("workdir-" + (RemotingLoginModuleUseNewClientCertTestCase.TEST_NAME)));

    private static final File SERVER_KEYSTORE_FILE = new File(RemotingLoginModuleUseNewClientCertTestCase.WORK_DIR, "server.keystore");

    private static final File SERVER_TRUSTSTORE_FILE = new File(RemotingLoginModuleUseNewClientCertTestCase.WORK_DIR, "server.truststore");

    private static final File CLIENTS_TRUSTSTORE_FILE = new File(RemotingLoginModuleUseNewClientCertTestCase.WORK_DIR, "clients.truststore");

    private static final File ROLES_FILE = new File(RemotingLoginModuleUseNewClientCertTestCase.WORK_DIR, "roles.properties");

    private static final int REMOTING_PORT_TEST = 14447;

    private static final PathAddress ADDR_SOCKET_BINDING = PathAddress.pathAddress().append(SOCKET_BINDING_GROUP, "standard-sockets").append(SOCKET_BINDING, RemotingLoginModuleUseNewClientCertTestCase.TEST_NAME);

    private static final PathAddress ADDR_REMOTING_CONNECTOR = PathAddress.pathAddress().append(SUBSYSTEM, "remoting").append("connector", RemotingLoginModuleUseNewClientCertTestCase.TEST_NAME);

    @ArquillianResource
    private ManagementClient mgmtClient;

    /**
     * Tests that an authorized user has access to an EJB method.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAuthorizedClient() throws Exception {
        final Properties env = configureEjbClient(RemotingLoginModuleUseNewClientCertTestCase.CLIENT_AUTHORIZED_NAME);
        InitialContext ctx = new InitialContext(env);
        final Hello helloBean = ((Hello) (ctx.lookup(RemotingLoginModuleUseNewClientCertTestCase.HELLOBEAN_LOOKUP_NAME)));
        Assert.assertEquals(HELLO_WORLD, helloBean.sayHelloWorld());
        ctx.close();
    }

    /**
     * Tests if role check is done correctly for authenticated user.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNotAuthorizedClient() throws Exception {
        final Properties env = configureEjbClient(RemotingLoginModuleUseNewClientCertTestCase.CLIENT_NOT_AUTHORIZED_NAME);
        InitialContext ctx = new InitialContext(env);
        final Hello helloBean = ((Hello) (ctx.lookup(RemotingLoginModuleUseNewClientCertTestCase.HELLOBEAN_LOOKUP_NAME)));
        try {
            helloBean.sayHelloWorld();
            Assert.fail("The EJB call should fail for unauthorized client.");
        } catch (EJBAccessException e) {
            // OK
        }
        ctx.close();
    }

    /**
     * Tests if client access is denied for untrusted clients.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNotTrustedClient() throws Exception {
        final Properties env = configureEjbClient(RemotingLoginModuleUseNewClientCertTestCase.CLIENT_NOT_TRUSTED_NAME);
        InitialContext ctx = new InitialContext(env);
        try {
            ctx.lookup(RemotingLoginModuleUseNewClientCertTestCase.HELLOBEAN_LOOKUP_NAME);
            Assert.fail("The JNDI lookup should fail for untrusted client.");
        } catch (NamingException e) {
            // OK
        }
        ctx.close();
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
         * @see AbstractSecurityDomainsServerSetupTask#getSecurityDomains()
         */
        @Override
        protected SecurityDomain[] getSecurityDomains() {
            // <security-domain name="xxx" cache-type="default">
            // <authentication>
            // <login-module code="Remoting" flag="optional">
            // <module-option name="password-stacking" value="useFirstPass"/>
            // <module-option name="useNewClientCert" value="true"/>
            // </login-module>
            // <login-module code="CertRolesLoginModule" flag="required">
            // <module-option name="password-stacking" value="useFirstPass"/>
            // <module-option name="securityDomain" value="xxx"/>
            // <module-option name="rolesProperties" value="file:///${jboss.server.config.dir}/roles.properties"/>
            // </login-module>
            // </authentication>
            // </security-domain>
            final SecurityDomain sd = new SecurityDomain.Builder().name(RemotingLoginModuleUseNewClientCertTestCase.TEST_NAME).loginModules(new SecurityModule.Builder().name("Remoting").flag("optional").putOption("password-stacking", "useFirstPass").putOption("useNewClientCert", "true").build(), // it would be even better with our own CertificationLoginModule that would take roles from the client certificate
            new SecurityModule.Builder().name("CertificateRoles").flag("required").putOption("password-stacking", "useFirstPass").putOption("securityDomain", RemotingLoginModuleUseNewClientCertTestCase.TEST_NAME).putOption("rolesProperties", RemotingLoginModuleUseNewClientCertTestCase.ROLES_FILE.getAbsolutePath()).build()).build();
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
         */
        @Override
        protected SecurityRealm[] getSecurityRealms() {
            // <server-identities>
            // <ssl>
            // <keystore path="server.keystore" keystore-password="123456"/>
            // </ssl>
            // </server-identities>
            // <authentication>
            // <truststore path="server.truststore" keystore-password="123456"/>
            // </authentication>
            RealmKeystore.Builder keyStoreBuilder = new RealmKeystore.Builder().keystorePassword(RemotingLoginModuleUseNewClientCertTestCase.KEYSTORE_PASSWORD);
            final SecurityRealm realm = new SecurityRealm.Builder().name(RemotingLoginModuleUseNewClientCertTestCase.TEST_NAME).serverIdentity(new ServerIdentity.Builder().ssl(keyStoreBuilder.keystorePath(RemotingLoginModuleUseNewClientCertTestCase.SERVER_KEYSTORE_FILE.getAbsolutePath()).build()).build()).authentication(new Authentication.Builder().truststore(keyStoreBuilder.keystorePath(RemotingLoginModuleUseNewClientCertTestCase.SERVER_TRUSTSTORE_FILE.getAbsolutePath()).build()).build()).build();
            return new SecurityRealm[]{ realm };
        }
    }

    /**
     * A {@link ServerSetupTask} instance which creates remoting mappings for this test case.
     */
    static class RemotingSetup implements ServerSetupTask {
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            final List<ModelNode> updates = new LinkedList<ModelNode>();
            RemotingLoginModuleUseNewClientCertTestCase.LOGGER.trace("Adding new socket binding and remoting connector");
            // /socket-binding-group=standard-sockets/socket-binding=remoting-xxx:add(port=14447)
            ModelNode socketBindingModelNode = Util.createAddOperation(RemotingLoginModuleUseNewClientCertTestCase.ADDR_SOCKET_BINDING);
            socketBindingModelNode.get(PORT).set(RemotingLoginModuleUseNewClientCertTestCase.REMOTING_PORT_TEST);
            socketBindingModelNode.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            updates.add(socketBindingModelNode);
            final ModelNode compositeOp = new ModelNode();
            compositeOp.get(OP).set(COMPOSITE);
            compositeOp.get(OP_ADDR).setEmptyList();
            ModelNode steps = compositeOp.get(STEPS);
            // /subsystem=remoting/connector=remoting-xx:add(security-realm=xx, socket-binding=yy)
            final ModelNode remotingConnectorModelNode = Util.createAddOperation(RemotingLoginModuleUseNewClientCertTestCase.ADDR_REMOTING_CONNECTOR);
            remotingConnectorModelNode.get("security-realm").set(RemotingLoginModuleUseNewClientCertTestCase.TEST_NAME);
            remotingConnectorModelNode.get("socket-binding").set(RemotingLoginModuleUseNewClientCertTestCase.TEST_NAME);
            remotingConnectorModelNode.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            steps.add(remotingConnectorModelNode);
            updates.add(compositeOp);
            Utils.applyUpdates(updates, managementClient.getControllerClient());
        }

        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            final List<ModelNode> updates = new ArrayList<ModelNode>();
            // /subsystem=remoting/connector=remoting-xx:remove()
            ModelNode op = Util.createRemoveOperation(RemotingLoginModuleUseNewClientCertTestCase.ADDR_REMOTING_CONNECTOR);
            op.get(OPERATION_HEADERS, ROLLBACK_ON_RUNTIME_FAILURE).set(false);
            op.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            updates.add(op);
            // /socket-binding-group=standard-sockets/socket-binding=remoting-xxx:remove()
            op = Util.createRemoveOperation(RemotingLoginModuleUseNewClientCertTestCase.ADDR_SOCKET_BINDING);
            op.get(OPERATION_HEADERS, ROLLBACK_ON_RUNTIME_FAILURE).set(false);
            op.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            updates.add(op);
            Utils.applyUpdates(updates, managementClient.getControllerClient());
        }
    }

    /**
     * A {@link ServerSetupTask} instance which creates keystores and property files for this test case. It also back-ups
     * original SSLContext and sets it back in {@link #tearDown(ManagementClient, String)} method.
     */
    static class FilesSetup implements ServerSetupTask {
        private SSLContext origSSLContext;

        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            RemotingLoginModuleUseNewClientCertTestCase.WORK_DIR.mkdir();
            FileUtils.write(RemotingLoginModuleUseNewClientCertTestCase.ROLES_FILE, ((("CN\\=" + (RemotingLoginModuleUseNewClientCertTestCase.CLIENT_AUTHORIZED_NAME)) + "=") + (HelloBean.ROLE_ALLOWED)));
            createKeystoreTruststore(RemotingLoginModuleUseNewClientCertTestCase.SERVER_NAME, RemotingLoginModuleUseNewClientCertTestCase.SERVER_KEYSTORE_FILE, RemotingLoginModuleUseNewClientCertTestCase.CLIENTS_TRUSTSTORE_FILE);
            createKeystoreTruststore(RemotingLoginModuleUseNewClientCertTestCase.CLIENT_AUTHORIZED_NAME, null, RemotingLoginModuleUseNewClientCertTestCase.SERVER_TRUSTSTORE_FILE);
            createKeystoreTruststore(RemotingLoginModuleUseNewClientCertTestCase.CLIENT_NOT_AUTHORIZED_NAME, null, RemotingLoginModuleUseNewClientCertTestCase.SERVER_TRUSTSTORE_FILE);
            createKeystoreTruststore(RemotingLoginModuleUseNewClientCertTestCase.CLIENT_NOT_TRUSTED_NAME, null, null);
            // backup SSLContext
            origSSLContext = SSLContext.getDefault();
        }

        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            SSLContext.setDefault(origSSLContext);
            FileUtils.deleteQuietly(RemotingLoginModuleUseNewClientCertTestCase.WORK_DIR);
        }

        private void createKeystoreTruststore(String name, File keystoreFile, File truststoreFile) throws IOException, IllegalStateException, GeneralSecurityException {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            final KeyPair keyPair = keyPairGenerator.generateKeyPair();
            final X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
            final long now = System.currentTimeMillis();
            v3CertGen.setNotBefore(new Date((now - ((((1000L * 60) * 60) * 24) * 30))));
            v3CertGen.setNotAfter(new Date((now + ((((1000L * 60) * 60) * 24) * 365))));
            final X509Principal dn = new X509Principal(("CN=" + name));
            v3CertGen.setIssuerDN(dn);
            v3CertGen.setSubjectDN(dn);
            v3CertGen.setPublicKey(keyPair.getPublic());
            v3CertGen.setSignatureAlgorithm("SHA256withRSA");
            final SecureRandom sr = new SecureRandom();
            v3CertGen.setSerialNumber(BigInteger.ONE);
            X509Certificate certificate = v3CertGen.generate(keyPair.getPrivate(), sr);
            // save keystore to a new file
            final KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(null, null);
            keystore.setKeyEntry(name, keyPair.getPrivate(), RemotingLoginModuleUseNewClientCertTestCase.KEYSTORE_PASSWORD.toCharArray(), new Certificate[]{ certificate });
            if (keystoreFile == null) {
                keystoreFile = RemotingLoginModuleUseNewClientCertTestCase.getClientKeystoreFile(name);
            }
            final OutputStream ksOut = new FileOutputStream(keystoreFile);
            keystore.store(ksOut, RemotingLoginModuleUseNewClientCertTestCase.KEYSTORE_PASSWORD.toCharArray());
            ksOut.close();
            // if requested, save truststore
            if (truststoreFile != null) {
                final KeyStore truststore;
                // if the truststore exists already, use it
                if (truststoreFile.exists()) {
                    truststore = KeyStoreUtil.getKeyStore(truststoreFile, RemotingLoginModuleUseNewClientCertTestCase.KEYSTORE_PASSWORD.toCharArray());
                } else {
                    truststore = KeyStore.getInstance("JKS");
                    truststore.load(null, null);
                }
                truststore.setCertificateEntry(name, certificate);
                final OutputStream tsOut = new FileOutputStream(truststoreFile);
                truststore.store(tsOut, RemotingLoginModuleUseNewClientCertTestCase.KEYSTORE_PASSWORD.toCharArray());
                tsOut.close();
            }
        }
    }
}

