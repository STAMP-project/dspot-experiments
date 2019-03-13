package org.jboss.as.test.manualmode.ejb.client.outbound.connection.security;


import java.io.File;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.integration.security.common.SecurityTestConstants;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case pertaining to remote outbound connection authentication between two server instances using remote-outbound-connection
 * management resource with Elytron authentication context.
 *
 * @author <a href="mailto:mjurc@redhat.com">Michal Jurc</a> (c) 2017 Red Hat, Inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ElytronRemoteOutboundConnectionTestCase {
    private static final Logger log = Logger.getLogger(ElytronRemoteOutboundConnectionTestCase.class);

    private static final String INBOUND_CONNECTION_MODULE_NAME = "inbound-module";

    private static final String OUTBOUND_CONNECTION_MODULE_NAME = "outbound-module";

    private static final String INBOUND_CONNECTION_SERVER = "inbound-server";

    private static final String OUTBOUND_CONNECTION_SERVER = "outbound-server";

    private static final String EJB_SERVER_DEPLOYMENT = "ejb-server-deployment";

    private static final String EJB_CLIENT_DEPLOYMENT = "ejb-client-deployment";

    private static final String RESOURCE_PREFIX = "ejb-remote-tests";

    private static final String PROPERTIES_REALM = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-properties-realm";

    private static final String SECURITY_DOMAIN = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-security-domain";

    private static final String AUTHENTICATION_FACTORY = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-sasl-authentication";

    private static final String APPLICATION_SECURITY_DOMAIN = ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX;

    private static final String INBOUND_SOCKET_BINDING = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-socket-binding";

    private static final String CONNECTOR = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-connector";

    private static final String OUTBOUND_SOCKET_BINDING = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-outbound-socket-binding";

    private static final String DEFAULT_AUTH_CONFIG = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-default-auth-config";

    private static final String DEFAULT_AUTH_CONTEXT = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-default-auth-context";

    private static final String OVERRIDING_AUTH_CONFIG = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-overriding-auth-config";

    private static final String OVERRIDING_AUTH_CONTEXT = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-overriding-auth-context";

    private static final String REMOTE_OUTBOUND_CONNECTION = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-remote-outbound-connection";

    private static final String SERVER_KEY_STORE = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-server-key-store";

    private static final String SERVER_KEY_MANAGER = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-server-key-manager";

    private static final String SERVER_TRUST_STORE = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-server-trust-store";

    private static final String SERVER_TRUST_MANAGER = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-server-trust-manager";

    private static final String SERVER_SSL_CONTEXT = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-server-ssl-context";

    private static final String DEFAULT_KEY_STORE = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-default-key-store";

    private static final String DEFAULT_KEY_MANAGER = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-default-key-manager";

    private static final String DEFAULT_TRUST_STORE = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-default-trust-store";

    private static final String DEFAULT_TRUST_MANAGER = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-default-trust-manager";

    private static final String DEFAULT_SERVER_SSL_CONTEXT = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-default-server-ssl-context";

    private static final String OVERRIDING_KEY_STORE = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-overriding-key-store";

    private static final String OVERRIDING_KEY_MANAGER = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-overriding-key-manager";

    private static final String OVERRIDING_TRUST_STORE = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-overriding-trust-store";

    private static final String OVERRIDING_TRUST_MANAGER = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-overriding-trust-manager";

    private static final String OVERRIDING_SERVER_SSL_CONTEXT = (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX) + "-overriding-server-ssl-context";

    private static final String DEFAULT_USERNAME = "ejbRemoteTests";

    private static final String DEFAULT_PASSWORD = "ejbRemoteTestsPassword";

    private static final String OVERRIDING_USERNAME = "ejbRemoteTestsOverriding";

    private static final String OVERRIDING_PASSWORD = "ejbRemoteTestsPasswordOverriding";

    private static final String KEY_STORE_KEYPASS = SecurityTestConstants.KEYSTORE_PASSWORD;

    private static final File WORKDIR = new File((((((new File("").getAbsoluteFile().getAbsolutePath()) + (File.separatorChar)) + "target") + (File.separatorChar)) + (ElytronRemoteOutboundConnectionTestCase.RESOURCE_PREFIX)));

    private static final String SERVER_KEY_STORE_PATH = new File(ElytronRemoteOutboundConnectionTestCase.WORKDIR.getAbsoluteFile(), SecurityTestConstants.SERVER_KEYSTORE).getAbsolutePath();

    private static final String SERVER_TRUST_STORE_PATH = new File(ElytronRemoteOutboundConnectionTestCase.WORKDIR.getAbsoluteFile(), SecurityTestConstants.SERVER_TRUSTSTORE).getAbsolutePath();

    private static final String CLIENT_KEY_STORE_PATH = new File(ElytronRemoteOutboundConnectionTestCase.WORKDIR.getAbsoluteFile(), SecurityTestConstants.CLIENT_KEYSTORE).getAbsolutePath();

    private static final String CLIENT_TRUST_STORE_PATH = new File(ElytronRemoteOutboundConnectionTestCase.WORKDIR.getAbsoluteFile(), SecurityTestConstants.CLIENT_TRUSTSTORE).getAbsolutePath();

    private static final String UNTRUSTED_KEY_STORE_PATH = new File(ElytronRemoteOutboundConnectionTestCase.WORKDIR.getAbsoluteFile(), SecurityTestConstants.UNTRUSTED_KEYSTORE).getAbsolutePath();

    private static final String USERS_PATH = new File(ElytronRemoteOutboundConnectionTestCase.class.getResource("users.properties").getFile()).getAbsolutePath();

    private static final String ROLES_PATH = new File(ElytronRemoteOutboundConnectionTestCase.class.getResource("roles.properties").getFile()).getAbsolutePath();

    private static final int BARE_REMOTING_PORT = 54447;

    private static final String BARE_REMOTING_PROTOCOL = "remote";

    private static final int SSL_REMOTING_PORT = 54448;

    private static final String SSL_REMOTING_PROTOCOL = "remote";

    private static final int HTTP_REMOTING_PORT = 8080;

    private static final String HTTP_REMOTING_PROTOCOL = "http-remoting";

    private static final int HTTPS_REMOTING_PORT = 8443;

    private static final String HTTPS_REMOTING_PROTOCOL = "https-remoting";

    @ArquillianResource
    private static ContainerController containerController;

    private static ModelControllerClient serverSideMCC;

    private static ModelControllerClient clientSideMCC;

    @ArquillianResource
    private Deployer deployer;

    /**
     * Test verifying that the authentication context host configuration overwrites host configuration in socket binding in remote
     * outbound connection referenced from deployment.
     *
     * The test uses remoting protocol.
     */
    @Test
    public void testAuthenticationHostConfigWithBareRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundBareRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddressNode1(), 54321));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.BARE_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.BARE_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ""));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that if no legacy security is used in remoting outbound connection referenced from deployment and no Elytron
     * authentication context is used, the connection will fall back to using Elytron default authentication context.
     *
     * The test uses remoting protocol.
     */
    @Test
    public void testElytronDefaultContextWithBareRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundBareRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.BARE_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.BARE_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ""));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that the authentication context defined in remote outbound connection referenced from deployment overrides the
     * Elytron default authentication context.
     *
     * The test uses remoting protocol.
     */
    @Test
    public void testOverridingElytronDefaultContextWithBareRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundBareRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.BARE_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.BARE_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.BARE_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_PASSWORD));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that authentication context defined in remote outbound connection referenced from deployment is sufficient and
     * no Elytron default authentication context is required.
     *
     * The test uses remoting protocol.
     */
    @Test
    public void testConnectionContextWithBareRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundBareRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.BARE_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.BARE_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_PASSWORD));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that the authentication context host configuration overwrites host configuration in socket binding in remote
     * outbound connection referenced from deployment.
     *
     * The test uses remoting protocol with two-side SSL authentication being enforced.
     */
    @Test
    public void testAuthenticationHostConfigWithSSLRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundSSLRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddressNode1(), 54321));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_TRUST_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddTrustManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddServerSSLContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ""));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that if no legacy security is used in remoting outbound connection referenced from deployment and no Elytron
     * authentication context is used, the connection will fall back to using Elytron default authentication context.
     *
     * The test uses remoting protocol with two-side SSL authentication being enforced.
     */
    @Test
    public void testElytronDefaultContextWithSSLRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundSSLRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_TRUST_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddTrustManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddServerSSLContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ""));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that the authentication context defined in remote outbound connection referenced from deployment overrides the
     * Elytron default authentication context.
     *
     * The test uses remoting protocol with two-side SSL authentication being enforced.
     */
    @Test
    public void testOverridingElytronDefaultContextWithSSLRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundSSLRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.UNTRUSTED_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE, ElytronRemoteOutboundConnectionTestCase.UNTRUSTED_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddTrustManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddServerSSLContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyManagerOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_TRUST_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddTrustManagerOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_STORE));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddServerSSLContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_SERVER_SSL_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_MANAGER));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_SERVER_SSL_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that authentication context defined in remote outbound connection referenced from deployment is sufficient and
     * no Elytron default authentication context is required.
     *
     * The test uses remoting protocol with two-side SSL authentication being enforced.
     */
    @Test
    public void testConnectionContextWithSSLRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundSSLRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyManagerOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_TRUST_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddTrustManagerOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_STORE));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddServerSSLContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_SERVER_SSL_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_MANAGER));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_SERVER_SSL_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that the authentication context host configuration overwrites host configuration in socket binding in remote
     * outbound connection referenced from deployment.
     *
     * The test uses http-remoting protocol.
     */
    @Test
    public void testAuthenticationHostConfigWithHttpRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundHttpRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddressNode1(), 54321));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.HTTP_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.HTTP_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ""));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that if no legacy security is used in remoting outbound connection referenced from deployment and no Elytron
     * authentication context is used, the connection will fall back to using Elytron default authentication context.
     *
     * The test uses http-remoting protocol.
     */
    @Test
    public void testElytronDefaultContextWithHttpRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundHttpRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.HTTP_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.HTTP_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ""));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that the authentication context defined in remote outbound connection referenced from deployment overrides the
     * Elytron default authentication context.
     *
     * The test uses http-remoting protocol.
     */
    @Test
    public void testOverridingElytronDefaultContextWithHttpRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundHttpRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.HTTP_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.HTTP_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.HTTP_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_PASSWORD));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that authentication context defined in remote outbound connection referenced from deployment is sufficient and
     * no Elytron default authentication context is required.
     *
     * The test uses http-remoting protocol.
     */
    @Test
    public void testConnectionContextWithHttpRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundHttpRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.HTTP_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.HTTP_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_PASSWORD));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that the authentication context host configuration overwrites host configuration in socket binding in remote
     * outbound connection referenced from deployment.
     *
     * The test uses https-remoting protocol with two-side SSL authentication being enforced.
     */
    @Test
    public void testAuthenticationHostConfigWithHttpsRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundHttpsRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddressNode1(), 54321));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_TRUST_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddTrustManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddServerSSLContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ""));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that if no legacy security is used in remoting outbound connection referenced from deployment and no Elytron
     * authentication context is used, the connection will fall back to using Elytron default authentication context.
     *
     * The test uses https-remoting protocol with two-side SSL authentication being enforced.
     */
    @Test
    public void testElytronDefaultContextWithHttpsRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundHttpsRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.SSL_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_TRUST_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddTrustManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddServerSSLContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ""));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that the authentication context defined in remote outbound connection referenced from deployment overrides the
     * Elytron default authentication context.
     *
     * The test uses https-remoting protocol with two-side SSL authentication being enforced.
     */
    @Test
    public void testOverridingElytronDefaultContextWithHttpsRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundHttpsRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.DEFAULT_USERNAME, ElytronRemoteOutboundConnectionTestCase.DEFAULT_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.UNTRUSTED_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE, ElytronRemoteOutboundConnectionTestCase.UNTRUSTED_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddTrustManagerOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_STORE));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddServerSSLContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.DEFAULT_TRUST_MANAGER));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.DEFAULT_SERVER_SSL_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyManagerOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_TRUST_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddTrustManagerOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_STORE));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddServerSSLContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_SERVER_SSL_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_MANAGER));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_SERVER_SSL_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getWriteElytronDefaultAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.DEFAULT_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, callIntermediateWhoAmI());
    }

    /**
     * Test verifying that authentication context defined in remote outbound connection referenced from deployment is sufficient and
     * no Elytron default authentication context is required.
     *
     * The test uses https-remoting protocol with two-side SSL authentication being enforced.
     */
    @Test
    public void testConnectionContextWithHttpsRemoting() {
        // ==================================
        // Server-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.configureServerSideForInboundHttpsRemoting(ElytronRemoteOutboundConnectionTestCase.serverSideMCC);
        // ==================================
        // Client-side server setup
        // ==================================
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddOutboundSocketBindingOp(ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationConfigurationOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PROTOCOL, ElytronRemoteOutboundConnectionTestCase.PROPERTIES_REALM, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_PASSWORD, TestSuiteEnvironment.getServerAddress(), ElytronRemoteOutboundConnectionTestCase.HTTPS_REMOTING_PORT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_KEY_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyManagerOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_STORE, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddKeyStoreOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_STORE, ElytronRemoteOutboundConnectionTestCase.CLIENT_TRUST_STORE_PATH, ElytronRemoteOutboundConnectionTestCase.KEY_STORE_KEYPASS));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddTrustManagerOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_STORE));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddServerSSLContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_SERVER_SSL_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_KEY_MANAGER, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_TRUST_MANAGER));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddAuthenticationContextOp(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONFIG, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_SERVER_SSL_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.applyUpdate(ElytronRemoteOutboundConnectionTestCase.clientSideMCC, ElytronRemoteOutboundConnectionTestCase.getAddConnectionOp(ElytronRemoteOutboundConnectionTestCase.REMOTE_OUTBOUND_CONNECTION, ElytronRemoteOutboundConnectionTestCase.OUTBOUND_SOCKET_BINDING, ElytronRemoteOutboundConnectionTestCase.OVERRIDING_AUTH_CONTEXT));
        ElytronRemoteOutboundConnectionTestCase.executeBlockingReloadClientServer(ElytronRemoteOutboundConnectionTestCase.clientSideMCC);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_SERVER_DEPLOYMENT);
        deployer.deploy(ElytronRemoteOutboundConnectionTestCase.EJB_CLIENT_DEPLOYMENT);
        Assert.assertEquals(ElytronRemoteOutboundConnectionTestCase.OVERRIDING_USERNAME, callIntermediateWhoAmI());
    }
}

