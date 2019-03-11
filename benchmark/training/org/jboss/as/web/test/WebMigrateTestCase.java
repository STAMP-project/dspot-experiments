/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.web.test;


import Constants.ACCESS_LOG;
import Constants.CONFIGURATION;
import Constants.CRAWLER_SESSION_MANAGEMENT;
import Constants.DEFAULT_HOST;
import Constants.DEFAULT_SESSION_TIMEOUT;
import Constants.DIRECTORY;
import Constants.DIRECTORY_LISTING;
import Constants.DOMAIN;
import Constants.ENABLED;
import Constants.FILTER;
import Constants.FILTER_REF;
import Constants.HANDLER;
import Constants.HOST;
import Constants.HTTPS_LISTENER;
import Constants.HTTP_LISTENER;
import Constants.HTTP_ONLY;
import Constants.JSP;
import Constants.MAX_POST_SIZE;
import Constants.MIME_MAPPING;
import Constants.PATTERN;
import Constants.PREDICATE;
import Constants.PREFIX;
import Constants.RECOMPILE_ON_FAIL;
import Constants.REDIRECT_SOCKET;
import Constants.RELATIVE_TO;
import Constants.RESOLVE_PEER_ADDRESS;
import Constants.ROTATE;
import Constants.SECURITY_REALM;
import Constants.SERVLET_CONTAINER;
import Constants.SESSION_TIMEOUT;
import Constants.SETTING;
import Constants.SINGLE_SIGN_ON;
import Constants.SOCKET_BINDING;
import Constants.SSL_SESSION_CACHE_SIZE;
import Constants.SUFFIX;
import Constants.USER_AGENTS;
import Constants.VALUE;
import Constants.VERIFY_CLIENT;
import Constants.WELCOME_FILE;
import CoreManagementResourceDefinition.PATH_ELEMENT;
import ExtensionRegistryType.SERVER;
import KeystoreAttributes.KEYSTORE_PATH;
import Resource.Factory;
import WebExtension.SUBSYSTEM_NAME;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationDefinition;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ProcessType;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.as.controller.RunningMode;
import org.jboss.as.controller.access.management.DelegatingConfigurableAuthorizer;
import org.jboss.as.controller.access.management.ManagementSecurityIdentitySupplier;
import org.jboss.as.controller.capability.registry.RuntimeCapabilityRegistry;
import org.jboss.as.controller.descriptions.common.ControllerResolver;
import org.jboss.as.controller.extension.ExtensionRegistry;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.domain.management.CoreManagementResourceDefinition;
import org.jboss.as.domain.management.audit.EnvironmentNameReader;
import org.jboss.as.network.SocketBinding;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.jboss.as.subsystem.test.AdditionalInitialization;
import org.jboss.as.subsystem.test.ControllerInitializer;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.as.web.WebExtension;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.wildfly.extension.io.IOExtension;
import org.wildfly.extension.undertow.UndertowExtension;


/**
 *
 *
 * @author Stuart Douglas
 */
public class WebMigrateTestCase extends AbstractSubsystemTest {
    public static final String UNDERTOW_SUBSYSTEM_NAME = "undertow";

    private static final char[] GENERATED_KEYSTORE_PASSWORD = "changeit".toCharArray();

    private static final String CLIENT_ALIAS = "client";

    private static final String CLIENT_2_ALIAS = "client2";

    private static final String TEST_ALIAS = "test";

    private static final String WORKING_DIRECTORY_LOCATION = "./target/test-classes";

    private static final File KEY_STORE_FILE = new File(WebMigrateTestCase.WORKING_DIRECTORY_LOCATION, "server.keystore");

    private static final File TRUST_STORE_FILE = new File(WebMigrateTestCase.WORKING_DIRECTORY_LOCATION, "jsse.keystore");

    private static final String TEST_CLIENT_DN = "CN=Test Client, OU=JBoss, O=Red Hat, L=Raleigh, ST=North Carolina, C=US";

    private static final String TEST_CLIENT_2_DN = "CN=Test Client 2, OU=JBoss, O=Red Hat, L=Raleigh, ST=North Carolina, C=US";

    private static final String AS_7_DN = "CN=AS7, OU=JBoss, O=Red Hat, L=Raleigh, ST=North Carolina, C=US";

    private static final String SHA_1_RSA = "SHA1withRSA";

    public WebMigrateTestCase() {
        super(SUBSYSTEM_NAME, new WebExtension());
    }

    @Test
    public void testMigrateOperation() throws Exception {
        String subsystemXml = readResource("subsystem-migrate-2.2.0.xml");
        WebMigrateTestCase.NewSubsystemAdditionalInitialization additionalInitialization = new WebMigrateTestCase.NewSubsystemAdditionalInitialization();
        KernelServices services = createKernelServicesBuilder(additionalInitialization).setSubsystemXml(subsystemXml).build();
        ModelNode model = services.readWholeModel();
        Assert.assertFalse(additionalInitialization.extensionAdded);
        Assert.assertTrue(model.get(SUBSYSTEM, SUBSYSTEM_NAME).isDefined());
        Assert.assertFalse(model.get(SUBSYSTEM, WebMigrateTestCase.UNDERTOW_SUBSYSTEM_NAME).isDefined());
        ModelNode migrateOp = new ModelNode();
        migrateOp.get(OP).set("migrate");
        migrateOp.get(OP_ADDR).add(SUBSYSTEM, SUBSYSTEM_NAME);
        checkOutcome(services.executeOperation(migrateOp));
        model = services.readWholeModel();
        Assert.assertTrue(additionalInitialization.extensionAdded);
        Assert.assertFalse(model.get(SUBSYSTEM, SUBSYSTEM_NAME).isDefined());
        Assert.assertTrue(model.get(SUBSYSTEM, WebMigrateTestCase.UNDERTOW_SUBSYSTEM_NAME).isDefined());
        // make sure we have an IO subsystem
        ModelNode ioSubsystem = model.get(SUBSYSTEM, "io");
        Assert.assertTrue(ioSubsystem.isDefined());
        Assert.assertTrue(ioSubsystem.get("worker", "default").isDefined());
        Assert.assertTrue(ioSubsystem.get("buffer-pool", "default").isDefined());
        ModelNode newSubsystem = model.get(SUBSYSTEM, WebMigrateTestCase.UNDERTOW_SUBSYSTEM_NAME);
        ModelNode newServer = newSubsystem.get("server", "default-server");
        Assert.assertNotNull(newServer);
        Assert.assertTrue(newServer.isDefined());
        Assert.assertEquals("default-host", newServer.get(DEFAULT_HOST).asString());
        // servlet container
        ModelNode servletContainer = newSubsystem.get(SERVLET_CONTAINER, "default");
        Assert.assertNotNull(servletContainer);
        Assert.assertTrue(servletContainer.isDefined());
        Assert.assertEquals("${prop.default-session-timeout:30}", servletContainer.get(DEFAULT_SESSION_TIMEOUT).asString());
        Assert.assertEquals("${prop.listings:true}", servletContainer.get(DIRECTORY_LISTING).asString());
        // jsp settings
        ModelNode jsp = servletContainer.get(SETTING, JSP);
        Assert.assertNotNull(jsp);
        Assert.assertEquals("${prop.recompile-on-fail:true}", jsp.get(RECOMPILE_ON_FAIL).asString());
        // welcome file
        ModelNode welcome = servletContainer.get(WELCOME_FILE, "toto");
        Assert.assertTrue(welcome.isDefined());
        // mime mapping
        ModelNode mimeMapping = servletContainer.get(MIME_MAPPING, "ogx");
        Assert.assertTrue(mimeMapping.isDefined());
        Assert.assertEquals("application/ogg", mimeMapping.get(VALUE).asString());
        // http connector
        ModelNode connector = newServer.get(HTTP_LISTENER, "http");
        Assert.assertTrue(connector.isDefined());
        Assert.assertEquals("http", connector.get(SOCKET_BINDING).asString());
        Assert.assertEquals("${prop.enabled:true}", connector.get(ENABLED).asString());
        Assert.assertEquals("${prop.enable-lookups:false}", connector.get(RESOLVE_PEER_ADDRESS).asString());
        Assert.assertEquals("${prop.max-post-size:2097153}", connector.get(MAX_POST_SIZE).asString());
        Assert.assertEquals("https", connector.get(REDIRECT_SOCKET).asString());
        // http-proxy connector
        ModelNode httpProxyConnector = newServer.get(HTTP_LISTENER, "http-proxy");
        Assert.assertTrue(connector.isDefined());
        Assert.assertEquals("http", httpProxyConnector.get(SOCKET_BINDING).asString());
        // https connector
        ModelNode httpsConnector = newServer.get(HTTPS_LISTENER, "https");
        String realmName = httpsConnector.get(SECURITY_REALM).asString();
        Assert.assertTrue(realmName, realmName.startsWith("jbossweb-migration-security-realm"));
        Assert.assertEquals("${prop.session-cache-size:512}", httpsConnector.get(SSL_SESSION_CACHE_SIZE).asString());
        Assert.assertEquals("REQUESTED", httpsConnector.get(VERIFY_CLIENT).asString());
        // realm name is dynamic
        ModelNode realm = model.get(CORE_SERVICE, MANAGEMENT).get(SECURITY_REALM, realmName);
        // trust store
        ModelNode trustStore = realm.get(AUTHENTICATION, TRUSTSTORE);
        Assert.assertEquals("${file-base}/jsse.keystore", trustStore.get(KEYSTORE_PATH.getName()).asString());
        // Valves
        ModelNode filters = newSubsystem.get(CONFIGURATION, FILTER);
        ModelNode dumpFilter = filters.get("expression-filter", "request-dumper");
        Assert.assertEquals("dump-request", dumpFilter.get("expression").asString());
        validateExpressionFilter(dumpFilter);
        ModelNode remoteAddrFilter = filters.get("expression-filter", "remote-addr");
        Assert.assertEquals("access-control(acl={'192.168.1.20 deny', '127.0.0.1 allow', '127.0.0.2 allow'} , attribute=%a)", remoteAddrFilter.get("expression").asString());
        validateExpressionFilter(remoteAddrFilter);
        ModelNode stuckFilter = filters.get("expression-filter", "stuck");
        Assert.assertEquals("stuck-thread-detector(threshhold='1000')", stuckFilter.get("expression").asString());
        validateExpressionFilter(stuckFilter);
        ModelNode proxyFilter = filters.get("expression-filter", "proxy");
        Assert.assertEquals("regex(pattern=\"proxy1|proxy2\", value=%{i,x-forwarded-for}, full-match=true) and regex(pattern=\"192\\.168\\.0\\.10|192\\.168\\.0\\.11\", value=%{i,x-forwarded-for}, full-match=true) -> proxy-peer-address", proxyFilter.get("expression").asString());
        validateExpressionFilter(proxyFilter);
        ModelNode crawler = servletContainer.get(SETTING, CRAWLER_SESSION_MANAGEMENT);
        Assert.assertTrue(crawler.isDefined());
        Assert.assertEquals(1, crawler.get(SESSION_TIMEOUT).asInt());
        Assert.assertEquals("Google", crawler.get(USER_AGENTS).asString());
        // virtual host
        ModelNode virtualHost = newServer.get(HOST, "default-host");
        // welcome content
        Assert.assertEquals("welcome-content", virtualHost.get("location", "/").get(HANDLER).asString());
        Assert.assertEquals("localhost", virtualHost.get("alias").asList().get(0).asString());
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "request-dumper"));
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "remote-addr"));
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "proxy"));
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "stuck"));
        Assert.assertFalse(virtualHost.hasDefined(FILTER_REF, "myvalve"));
        ModelNode accessLog = virtualHost.get(SETTING, ACCESS_LOG);
        Assert.assertEquals("prefix", accessLog.get(PREFIX).asString());
        Assert.assertEquals("true", accessLog.get(ROTATE).asString());
        Assert.assertEquals("extended", accessLog.get(PATTERN).asString());
        Assert.assertEquals("toto", accessLog.get(DIRECTORY).asString());
        Assert.assertEquals("jboss.server.base.dir", accessLog.get(RELATIVE_TO).asString());
        // sso
        ModelNode sso = virtualHost.get(SETTING, SINGLE_SIGN_ON);
        Assert.assertEquals("${prop.domain:myDomain}", sso.get(DOMAIN).asString());
        Assert.assertEquals("${prop.http-only:true}", sso.get(HTTP_ONLY).asString());
        // global access log valve
        virtualHost = newServer.get(HOST, "vs1");
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "request-dumper"));
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "remote-addr"));
        Assert.assertFalse(virtualHost.hasDefined(FILTER_REF, "myvalve"));
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "proxy"));
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "stuck"));
        accessLog = virtualHost.get(SETTING, ACCESS_LOG);
        Assert.assertEquals("myapp_access_log.", accessLog.get(PREFIX).asString());
        Assert.assertEquals(".log", accessLog.get(SUFFIX).asString());
        Assert.assertEquals("true", accessLog.get(ROTATE).asString());
        Assert.assertEquals("common", accessLog.get(PATTERN).asString());
        Assert.assertEquals("${jboss.server.log.dir}", accessLog.get(DIRECTORY).asString());
        Assert.assertEquals("exists(%{r,log-enabled})", accessLog.get(PREDICATE).asString());
        // proxy valve
        virtualHost = newServer.get(HOST, "vs1");
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "request-dumper"));
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "remote-addr"));
        Assert.assertFalse(virtualHost.hasDefined(FILTER_REF, "myvalve"));
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "proxy"));
        Assert.assertTrue(virtualHost.hasDefined(FILTER_REF, "stuck"));
        Assert.assertEquals("myapp_access_log.", accessLog.get(PREFIX).asString());
        Assert.assertEquals(".log", accessLog.get(SUFFIX).asString());
        Assert.assertEquals("true", accessLog.get(ROTATE).asString());
        Assert.assertEquals("common", accessLog.get(PATTERN).asString());
        Assert.assertEquals("${jboss.server.log.dir}", accessLog.get(DIRECTORY).asString());
        Assert.assertEquals("exists(%{r,log-enabled})", accessLog.get(PREDICATE).asString());
    }

    private static class NewSubsystemAdditionalInitialization extends AdditionalInitialization {
        UndertowExtension undertow = new UndertowExtension();

        IOExtension io = new IOExtension();

        boolean extensionAdded = false;

        @Override
        protected void initializeExtraSubystemsAndModel(ExtensionRegistry extensionRegistry, Resource rootResource, ManagementResourceRegistration rootRegistration, RuntimeCapabilityRegistry capabilityRegistry) {
            final OperationDefinition removeExtension = build();
            PathElement webExtension = PathElement.pathElement(EXTENSION, "org.jboss.as.web");
            rootRegistration.registerSubModel(new org.jboss.as.controller.SimpleResourceDefinition(webExtension, ControllerResolver.getResolver(EXTENSION))).registerOperationHandler(removeExtension, new ReloadRequiredRemoveStepHandler());
            rootResource.registerChild(webExtension, Factory.create());
            rootRegistration.registerSubModel(new org.jboss.as.controller.SimpleResourceDefinition(PathElement.pathElement(EXTENSION), ControllerResolver.getResolver(EXTENSION), new OperationStepHandler() {
                @Override
                public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
                    if (!(extensionAdded)) {
                        extensionAdded = true;
                        undertow.initialize(extensionRegistry.getExtensionContext("org.wildfly.extension.undertow", rootRegistration, SERVER));
                        io.initialize(extensionRegistry.getExtensionContext("org.wildfly.extension.io", rootRegistration, SERVER));
                    }
                }
            }, null));
            rootRegistration.registerSubModel(CoreManagementResourceDefinition.forStandaloneServer(new DelegatingConfigurableAuthorizer(), new ManagementSecurityIdentitySupplier(), null, null, new EnvironmentNameReader() {
                public boolean isServer() {
                    return true;
                }

                public String getServerName() {
                    return "Test";
                }

                public String getHostName() {
                    return null;
                }

                public String getProductName() {
                    return null;
                }
            }, null));
            rootResource.registerChild(PATH_ELEMENT, Factory.create());
            System.setProperty("file-base", new File(getClass().getClassLoader().getResource("server.keystore").getFile()).getParentFile().getAbsolutePath());
            Map<String, Class> capabilities = new HashMap<>();
            final String SOCKET_CAPABILITY = "org.wildfly.network.socket-binding";
            capabilities.put(buildDynamicCapabilityName(SOCKET_CAPABILITY, "http"), SocketBinding.class);
            capabilities.put(buildDynamicCapabilityName(SOCKET_CAPABILITY, "https"), SocketBinding.class);
            registerServiceCapabilities(capabilityRegistry, capabilities);
        }

        @Override
        protected RunningMode getRunningMode() {
            return RunningMode.ADMIN_ONLY;
        }

        @Override
        protected ProcessType getProcessType() {
            return ProcessType.SELF_CONTAINED;
        }

        @Override
        protected void setupController(ControllerInitializer controllerInitializer) {
            controllerInitializer.addPath("jboss.controller.temp.dir", System.getProperty("java.io.tmpdir"), null);
        }
    }
}

