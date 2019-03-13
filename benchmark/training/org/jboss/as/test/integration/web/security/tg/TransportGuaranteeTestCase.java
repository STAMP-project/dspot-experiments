/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.web.security.tg;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.categories.CommonCriteria;
import org.jboss.as.test.integration.management.util.CLIWrapper;
import org.jboss.as.test.integration.security.common.AbstractSecurityRealmsServerSetupTask;
import org.jboss.as.test.integration.security.common.config.realm.RealmKeystore;
import org.jboss.as.test.integration.security.common.config.realm.SecurityRealm;
import org.jboss.as.test.integration.security.common.config.realm.ServerIdentity;
import org.jboss.as.test.integration.web.security.WebSecurityCommon;
import org.jboss.as.test.integration.web.security.WebTestsSecurityDomainSetup;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.test.security.common.elytron.CredentialReference;
import org.wildfly.test.security.common.elytron.Path;
import org.wildfly.test.security.common.elytron.SimpleKeyManager;
import org.wildfly.test.security.common.elytron.SimpleKeyStore;
import org.wildfly.test.security.common.elytron.SimpleServerSslContext;
import org.wildfly.test.security.common.other.SimpleSocketBinding;
import org.wildfly.test.undertow.common.elytron.SimpleHttpsListener;


/**
 * This test case check if transport-guarantee security constraint works properly.
 *
 * @author <a href="mailto:pskopek@redhat.com">Peter Skopek</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup({ WebTestsSecurityDomainSetup.class, TransportGuaranteeTestCase.ListenerSetup.class })
@Category(CommonCriteria.class)
public class TransportGuaranteeTestCase {
    private static final Logger log = Logger.getLogger(TransportGuaranteeTestCase.class);

    private static final String WAR = ".war";

    private static final String TG_ANN = "tg-annotated";

    private static final String TG_DD = "tg-dd";

    private static final String TG_MIXED = "tg-mixed";

    private static String httpsTestURL = null;

    private static String httpTestURL = null;

    @Test
    public void testTransportGuaranteedAnnotation() throws Exception {
        performRequestsAndCheck((("/" + (TransportGuaranteeTestCase.TG_ANN)) + (TransportGuaranteeAnnotatedServlet.servletContext)));
    }

    @Test
    public void testTransportGuaranteedDD() throws Exception {
        performRequestsAndCheck((("/" + (TransportGuaranteeTestCase.TG_DD)) + (TransportGuaranteeServlet.servletContext)));
    }

    @Test
    public void testTransportGuaranteedMixed() throws Exception {
        performRequestsAndCheck((("/" + (TransportGuaranteeTestCase.TG_MIXED)) + "/tg_mixed_override/srv"));
    }

    static class ListenerSetup extends AbstractSecurityRealmsServerSetupTask implements ServerSetupTask {
        private static final Logger log = Logger.getLogger(TransportGuaranteeTestCase.ListenerSetup.class);

        private static final String NAME = TransportGuaranteeTestCase.class.getSimpleName();

        private static final File WORK_DIR = new File("target", (((("wildfly" + (File.separator)) + "standalone") + (File.separator)) + "configuration"));

        private static final File SERVER_KEYSTORE_FILE = new File(TransportGuaranteeTestCase.ListenerSetup.WORK_DIR, "application.keystore");

        private static final String PASSWORD = "password";

        public static final int HTTPS_PORT = 8343;

        private CLIWrapper cli;

        private SimpleKeyStore simpleKeystore;

        private SimpleKeyManager simpleKeyManager;

        private SimpleServerSslContext simpleServerSslContext;

        private SimpleSocketBinding simpleSocketBinding;

        private SimpleHttpsListener simpleHttpsListener;

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            if (WebSecurityCommon.isElytron()) {
                cli = new CLIWrapper(true);
                setElytronBased(managementClient);
            } else {
                super.setup(managementClient, containerId);
                setLegacySecurityRealmBased(managementClient);
            }
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            if (WebSecurityCommon.isElytron()) {
                cli = new CLIWrapper(true);
                simpleHttpsListener.remove(cli);
                simpleSocketBinding.remove(cli);
                simpleServerSslContext.remove(cli);
                simpleKeyManager.remove(cli);
                simpleKeystore.remove(cli);
            } else {
                final List<ModelNode> updates = new ArrayList<ModelNode>();
                ModelNode op = new ModelNode();
                op.get(OP).set(REMOVE);
                op.get(OP_ADDR).add(SUBSYSTEM, "undertow");
                op.get(OP_ADDR).add("server", "default-server");
                op.get(OP_ADDR).add("https-listener", TransportGuaranteeTestCase.ListenerSetup.NAME);
                // Don't rollback when the AS detects the war needs the module
                op.get(OPERATION_HEADERS, ROLLBACK_ON_RUNTIME_FAILURE).set(false);
                op.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
                updates.add(op);
                op = new ModelNode();
                op.get(OP).set(REMOVE);
                op.get(OP_ADDR).add("socket-binding-group", "standard-sockets");
                op.get(OP_ADDR).add("socket-binding", TransportGuaranteeTestCase.ListenerSetup.NAME);
                op.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
                updates.add(op);
                try {
                    TransportGuaranteeTestCase.ListenerSetup.applyUpdates(managementClient.getControllerClient(), updates);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                super.tearDown(managementClient, containerId);
            }
        }

        protected void setElytronBased(ManagementClient managementClient) throws Exception {
            setHttpsListenerSslContextBased(managementClient, cli, TransportGuaranteeTestCase.ListenerSetup.NAME, TransportGuaranteeTestCase.ListenerSetup.NAME, TransportGuaranteeTestCase.ListenerSetup.HTTPS_PORT, TransportGuaranteeTestCase.ListenerSetup.NAME, false);
        }

        protected void setLegacySecurityRealmBased(final ManagementClient managementClient) throws Exception {
            setHttpsListenerSecurityRealmBased(TransportGuaranteeTestCase.ListenerSetup.NAME, TransportGuaranteeTestCase.ListenerSetup.NAME, TransportGuaranteeTestCase.ListenerSetup.HTTPS_PORT, TransportGuaranteeTestCase.ListenerSetup.NAME, "NOT_REQUESTED", managementClient);
        }

        private void setHttpsListenerSecurityRealmBased(String httpsListenerName, String sockBindName, int httpsPort, String secRealmName, String verifyClient, ManagementClient managementClient) {
            TransportGuaranteeTestCase.ListenerSetup.log.debug("start of the creation of the https-listener with legacy security-realm");
            final List<ModelNode> updates = new ArrayList<ModelNode>();
            // Add the HTTPS socket binding.
            ModelNode op = new ModelNode();
            op.get(OP).set(ADD);
            op.get(OP_ADDR).add("socket-binding-group", "standard-sockets");
            op.get(OP_ADDR).add("socket-binding", sockBindName);
            op.get("interface").set("public");
            op.get("port").set(httpsPort);
            op.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            updates.add(op);
            // Add the HTTPS connector.
            final ModelNode composite = Util.getEmptyOperation(COMPOSITE, new ModelNode());
            final ModelNode steps = composite.get(STEPS);
            op = new ModelNode();
            op.get(OP).set(ADD);
            op.get(OP_ADDR).add(SUBSYSTEM, "undertow");
            op.get(OP_ADDR).add("server", "default-server");
            op.get(OP_ADDR).add("https-listener", httpsListenerName);
            op.get("socket-binding").set(sockBindName);
            op.get("enabled").set(true);
            op.get("security-realm").set(secRealmName);
            op.get("verify-client").set(verifyClient);
            steps.add(op);
            composite.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            updates.add(composite);
            TransportGuaranteeTestCase.ListenerSetup.applyUpdates(managementClient.getControllerClient(), updates);
            TransportGuaranteeTestCase.ListenerSetup.log.debug("end of the security-realm https-listener creation");
        }

        @Override
        protected SecurityRealm[] getSecurityRealms() throws Exception {
            RealmKeystore keystore = new RealmKeystore.Builder().keystorePassword(TransportGuaranteeTestCase.ListenerSetup.PASSWORD).keystorePath(TransportGuaranteeTestCase.ListenerSetup.SERVER_KEYSTORE_FILE.getAbsolutePath()).build();
            return new SecurityRealm[]{ new SecurityRealm.Builder().name(TransportGuaranteeTestCase.ListenerSetup.NAME).serverIdentity(build()).build() };
        }

        private void setHttpsListenerSslContextBased(ManagementClient managementClient, CLIWrapper cli, String httpsListenerName, String sockBindName, int httpsPort, String sslContext, boolean verifyClient) throws Exception {
            TransportGuaranteeTestCase.ListenerSetup.log.debug("start of the creation of the https-listener with ssl-context");
            simpleKeystore = build();
            simpleKeystore.create(cli);
            simpleKeyManager = build();
            simpleKeyManager.create(cli);
            simpleServerSslContext = build();
            simpleServerSslContext.create(cli);
            simpleSocketBinding = build();
            simpleSocketBinding.create(managementClient.getControllerClient(), cli);
            simpleHttpsListener = build();
            simpleHttpsListener.create(cli);
            TransportGuaranteeTestCase.ListenerSetup.log.debug("end of the ssl-context https-listener creation");
        }

        protected static void applyUpdates(final ModelControllerClient client, final List<ModelNode> updates) {
            for (ModelNode update : updates) {
                try {
                    TransportGuaranteeTestCase.ListenerSetup.applyUpdate(client, update, false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        protected static void applyUpdate(final ModelControllerClient client, ModelNode update, boolean allowFailure) throws IOException {
            ModelNode result = client.execute(build());
            if ((result.hasDefined("outcome")) && (allowFailure || ("success".equals(result.get("outcome").asString())))) {
                if (result.hasDefined("result")) {
                    TransportGuaranteeTestCase.ListenerSetup.log.trace(result.get("result"));
                }
            } else
                if (result.hasDefined("failure-description")) {
                    throw new RuntimeException(result.get("failure-description").toString());
                } else {
                    throw new RuntimeException(("Operation not successful; outcome = " + (result.get("outcome"))));
                }

        }
    }
}

