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
package org.jboss.as.test.manualmode.security;


import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.AnnotationUtils;
import org.apache.directory.server.core.annotations.ContextEntry;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreateIndex;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.factory.DSAnnotationProcessor;
import org.apache.directory.server.core.kerberos.KeyDerivationInterceptor;
import org.apache.directory.server.factory.ServerAnnotationProcessor;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.ldap.handlers.ssl.LdapsInitializer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.server.protocol.shared.transport.Transport;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.ldap.InMemoryDirectoryServiceFactory;
import org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask;
import org.jboss.as.test.integration.security.common.AbstractSecurityRealmsServerSetupTask;
import org.jboss.as.test.integration.security.common.AbstractSystemPropertiesServerSetupTask;
import org.jboss.as.test.integration.security.common.ManagedCreateLdapServer;
import org.jboss.as.test.integration.security.common.ManagedCreateTransport;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.integration.security.common.config.SecurityDomain;
import org.jboss.as.test.integration.security.common.config.SecurityModule;
import org.jboss.as.test.integration.security.common.config.SecurityModule.Builder;
import org.jboss.as.test.integration.security.common.config.realm.Authentication;
import org.jboss.as.test.integration.security.common.config.realm.Authorization;
import org.jboss.as.test.integration.security.common.config.realm.LdapAuthentication;
import org.jboss.as.test.integration.security.common.config.realm.RealmKeystore;
import org.jboss.as.test.integration.security.common.config.realm.SecurityRealm;
import org.jboss.as.test.integration.security.common.config.realm.ServerIdentity;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A testcase which tests a SecurityRealm used as a SSL configuration source for LDAPs.<br/>
 * This test uses a simple re-implementation of ApacheDS {@link LdapsInitializer} class, which enables to set our own
 * TrustManager and ask for client authentication.<br/>
 * Test scenario:
 * <ol>
 * <li>start container</li>
 * <li>Start LDAP server with LDAPs protocol - use {@link TrustAndStoreTrustManager} as a TrustManager for incoming connections.
 * </li>
 * <li>configure security realms and LDAP outbound connection (one security realm with SSL configuration, ldap-connection using
 * the first security realm, second security realm with LDAP authentication using the new LDAP connection)</li>
 * <li>configure security domain, which uses RealmDirect login module pointing on the new security realm with LDAP authn</li>
 * <li>deploy protected web application, which uses security domain from the last step</li>
 * <li>test access to the web-app</li>
 * <li>test if the server certificate configured in the security realm was used for client authentication on LDAP server side
 * (use {@link TrustAndStoreTrustManager#isSubjectInClientCertChain(String)})</li>
 * <li>undo the changes</li>
 * </ol>
 *
 * @author Josef Cacek
 */
@RunWith(Arquillian.class)
@RunAsClient
public class OutboundLdapConnectionTestCase {
    private static final String KEYSTORE_PASSWORD = "123456";

    private static final String KEYSTORE_FILENAME_LDAPS = "ldaps.keystore";

    private static final String KEYSTORE_FILENAME_JBAS = "jbas.keystore";

    private static final String TRUSTSTORE_FILENAME_JBAS = "jbas.truststore";

    private static final File KEYSTORE_FILE_LDAPS = new File(OutboundLdapConnectionTestCase.KEYSTORE_FILENAME_LDAPS);

    private static final File KEYSTORE_FILE_JBAS = new File(OutboundLdapConnectionTestCase.KEYSTORE_FILENAME_JBAS);

    private static final File TRUSTSTORE_FILE_JBAS = new File(OutboundLdapConnectionTestCase.TRUSTSTORE_FILENAME_JBAS);

    private static final int LDAPS_PORT = 10636;

    private static final String CONTAINER = "default-jbossas";

    private static final String TEST_FILE = "test.txt";

    private static final String TEST_FILE_CONTENT = "OK";

    private static final String LDAPS_AUTHN_REALM = "ldaps-authn-realm";

    private static final String LDAPS_AUTHN_SD = "ldaps-authn-sd";

    private static final String SSL_CONF_REALM = "ssl-conf-realm";

    private static final String LDAPS_CONNECTION = "test-ldaps";

    private static final String LDAPS_AUTHN_REALM_NO_SSL = "ldaps-authn-realm-no-ssl";

    private static final String LDAPS_AUTHN_SD_NO_SSL = "ldaps-authn-sd-no-ssl";

    private static final String SECURITY_CREDENTIALS = "secret";

    private static final String SECURITY_PRINCIPAL = "uid=admin,ou=system";

    private final OutboundLdapConnectionTestCase.LDAPServerSetupTask ldapsSetup = new OutboundLdapConnectionTestCase.LDAPServerSetupTask();

    private final OutboundLdapConnectionTestCase.SecurityRealmsSetup realmsSetup = new OutboundLdapConnectionTestCase.SecurityRealmsSetup();

    private final OutboundLdapConnectionTestCase.SecurityDomainsSetup domainsSetup = new OutboundLdapConnectionTestCase.SecurityDomainsSetup();

    private final OutboundLdapConnectionTestCase.SystemPropertiesSetup systemPropertiesSetup = new OutboundLdapConnectionTestCase.SystemPropertiesSetup();

    private static boolean serverConfigured = false;

    @ArquillianResource
    private static ContainerController containerController;

    @ArquillianResource
    Deployer deployer;

    @Test
    @InSequence(-2)
    public void startContainer() throws Exception {
        OutboundLdapConnectionTestCase.containerController.start(OutboundLdapConnectionTestCase.CONTAINER);
    }

    @Test
    @InSequence(2)
    public void stopContainer() throws Exception {
        OutboundLdapConnectionTestCase.containerController.stop(OutboundLdapConnectionTestCase.CONTAINER);
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
         * @see org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask#getSecurityDomains()
         */
        @Override
        protected SecurityDomain[] getSecurityDomains() {
            final Builder realmDirectLMBuilder = new SecurityModule.Builder().name("RealmDirect");
            final SecurityModule mappingModule = new SecurityModule.Builder().name("SimpleRoles").putOption("jduke", "Admin").build();
            final SecurityDomain sd1 = new SecurityDomain.Builder().name(OutboundLdapConnectionTestCase.LDAPS_AUTHN_SD).loginModules(realmDirectLMBuilder.putOption("realm", OutboundLdapConnectionTestCase.LDAPS_AUTHN_REALM).build()).mappingModules(mappingModule).build();
            final SecurityDomain sd2 = new SecurityDomain.Builder().name(OutboundLdapConnectionTestCase.LDAPS_AUTHN_SD_NO_SSL).loginModules(realmDirectLMBuilder.putOption("realm", OutboundLdapConnectionTestCase.LDAPS_AUTHN_REALM_NO_SSL).build()).mappingModules(mappingModule).build();
            return new SecurityDomain[]{ sd1, sd2 };
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
            final RealmKeystore.Builder keyStoreBuilder = new RealmKeystore.Builder().keystorePassword(OutboundLdapConnectionTestCase.KEYSTORE_PASSWORD);
            final String ldapsUrl = (("ldaps://" + (Utils.getSecondaryTestAddress(managementClient))) + ":") + (OutboundLdapConnectionTestCase.LDAPS_PORT);
            final SecurityRealm sslConfRealm = new SecurityRealm.Builder().name(OutboundLdapConnectionTestCase.SSL_CONF_REALM).authentication(new Authentication.Builder().truststore(keyStoreBuilder.keystorePath(OutboundLdapConnectionTestCase.TRUSTSTORE_FILE_JBAS.getAbsolutePath()).build()).build()).serverIdentity(new ServerIdentity.Builder().ssl(keyStoreBuilder.keystorePath(OutboundLdapConnectionTestCase.KEYSTORE_FILE_JBAS.getAbsolutePath()).build()).build()).build();
            final SecurityRealm ldapsAuthRealm = new SecurityRealm.Builder().name(OutboundLdapConnectionTestCase.LDAPS_AUTHN_REALM).authentication(new Authentication.Builder().ldap(// ldap authentication
            // ldap-connection
            // shared attributes
            new LdapAuthentication.Builder().connection(OutboundLdapConnectionTestCase.LDAPS_CONNECTION).url(ldapsUrl).searchDn(OutboundLdapConnectionTestCase.SECURITY_PRINCIPAL).searchCredential(OutboundLdapConnectionTestCase.SECURITY_CREDENTIALS).securityRealm(OutboundLdapConnectionTestCase.SSL_CONF_REALM).baseDn("ou=People,dc=jboss,dc=org").recursive(Boolean.TRUE).usernameAttribute("uid").build()).build()).authorization(new Authorization.Builder().path("application-roles.properties").relativeTo("jboss.server.config.dir").build()).build();
            return new SecurityRealm[]{ sslConfRealm, ldapsAuthRealm };
        }
    }

    /**
     * This setup task disables hostname verification truststore file.
     */
    static class SystemPropertiesSetup extends AbstractSystemPropertiesServerSetupTask {
        /**
         *
         *
         * @see org.jboss.as.test.integration.security.common.AbstractSystemPropertiesServerSetupTask#getSystemProperties()
         */
        @Override
        protected SystemProperty[] getSystemProperties() {
            return new SystemProperty[]{ new DefaultSystemProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", "") };
        }
    }

    /**
     * A server setup task which configures and starts LDAP server.
     */
    // @formatter:off
    // @formatter:on
    @CreateDS(name = "JBossDS-OutboundLdapConnectionTestCase", factory = InMemoryDirectoryServiceFactory.class, partitions = { @CreatePartition(name = "jboss", suffix = "dc=jboss,dc=org", contextEntry = @ContextEntry(entryLdif = "dn: dc=jboss,dc=org\n" + (("dc: jboss\n" + "objectClass: top\n") + "objectClass: domain\n\n")), indexes = { @CreateIndex(attribute = "objectClass"), @CreateIndex(attribute = "dc"), @CreateIndex(attribute = "ou") }) }, additionalInterceptors = { KeyDerivationInterceptor.class })
    @CreateLdapServer(transports = { @CreateTransport(protocol = "LDAPS", port = OutboundLdapConnectionTestCase.LDAPS_PORT, address = "0.0.0.0") }, certificatePassword = OutboundLdapConnectionTestCase.KEYSTORE_PASSWORD)
    static class LDAPServerSetupTask {
        private DirectoryService directoryService;

        private LdapServer ldapServer;

        private final SslCertChainRecorder recorder = new SslCertChainRecorder();

        /**
         * Creates directory services, starts LDAP server and KDCServer.
         */
        public void startLdapServer() throws Exception {
            LdapsInitializer.setAndLockRecorder(recorder);
            directoryService = DSAnnotationProcessor.getDirectoryService();
            final SchemaManager schemaManager = directoryService.getSchemaManager();
            try (LdifReader ldifReader = new LdifReader(OutboundLdapConnectionTestCase.class.getResourceAsStream("OutboundLdapConnectionTestCase.ldif"))) {
                for (LdifEntry ldifEntry : ldifReader) {
                    directoryService.getAdminSession().add(new org.apache.directory.api.ldap.model.entry.DefaultEntry(schemaManager, ldifEntry.getEntry()));
                }
            }
            final ManagedCreateLdapServer createLdapServer = new ManagedCreateLdapServer(((CreateLdapServer) (AnnotationUtils.getInstance(CreateLdapServer.class))));
            createLdapServer.setKeyStore(OutboundLdapConnectionTestCase.KEYSTORE_FILE_LDAPS.getAbsolutePath());
            fixTransportAddress(createLdapServer, StringUtils.strip(TestSuiteEnvironment.getSecondaryTestAddress(false)));
            ldapServer = ServerAnnotationProcessor.instantiateLdapServer(createLdapServer, directoryService);
            /* set setWantClientAuth(true) manually as there is no way to do this via annotation */
            Transport[] transports = ldapServer.getTransports();
            Assert.assertTrue("The LDAP server configured via annotations should have just one transport", ((transports.length) == 1));
            final TcpTransport transport = ((TcpTransport) (transports[0]));
            transport.setWantClientAuth(true);
            TcpTransport newTransport = new InitializedTcpTransport(transport);
            ldapServer.setTransports(newTransport);
            Assert.assertEquals(ldapServer.getCertificatePassword(), OutboundLdapConnectionTestCase.KEYSTORE_PASSWORD);
            ldapServer.start();
        }

        /**
         * Fixes bind address in the CreateTransport annotation.
         *
         * @param createLdapServer
         * 		
         */
        private void fixTransportAddress(ManagedCreateLdapServer createLdapServer, String address) {
            final CreateTransport[] createTransports = createLdapServer.transports();
            for (int i = 0; i < (createTransports.length); i++) {
                final ManagedCreateTransport mgCreateTransport = new ManagedCreateTransport(createTransports[i]);
                mgCreateTransport.setAddress(address);
                createTransports[i] = mgCreateTransport;
            }
        }

        /**
         * Stops LDAP server and KDCServer and shuts down the directory service.
         */
        public void shutdownLdapServer() throws Exception {
            ldapServer.stop();
            directoryService.shutdown();
            FileUtils.deleteDirectory(directoryService.getInstanceLayout().getInstanceDirectory());
            LdapsInitializer.unsetAndUnlockRecorder();
        }
    }
}

