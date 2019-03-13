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
package org.jboss.as.test.integration.security.passwordmasking;


import java.io.IOException;
import java.net.URL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.h2.tools.Server;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.integration.security.common.VaultHandler;
import org.jboss.as.test.shared.SnapshotRestoreSetupTask;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="mailto:jlanik@redhat.com">Jan Lanik</a>.
 */
@RunWith(Arquillian.class)
@ServerSetup(PasswordMaskingTestCase.PasswordMaskingTestCaseSetup.class)
public class PasswordMaskingTestCase {
    private static Logger LOGGER = Logger.getLogger(PasswordMaskingTestCase.class);

    @ArquillianResource
    URL baseURL;

    static class PasswordMaskingTestCaseSetup extends SnapshotRestoreSetupTask {
        private Server server;

        private VaultHandler vaultHandler;

        @Override
        public void doSetup(ManagementClient managementClient, String containerId) throws Exception {
            VaultHandler.cleanFilesystem(PasswordMaskingTestCase.RESOURCE_LOCATION, true);
            ModelNode op;
            // setup DB
            server = Server.createTcpServer("-tcpAllowOthers").start();
            // create new vault
            vaultHandler = new VaultHandler(PasswordMaskingTestCase.RESOURCE_LOCATION);
            // create security attributes
            String attributeName = "password";
            String vaultPasswordString = vaultHandler.addSecuredAttribute(PasswordMaskingTestCase.VAULT_BLOCK, attributeName, PasswordMaskingTestCase.DS_CLEAR_TEXT_PASSWORD.toCharArray());
            PasswordMaskingTestCase.LOGGER.debug(("vaultPasswordString=" + vaultPasswordString));
            // create new vault setting in standalone
            op = new ModelNode();
            op.get(OP).set(ADD);
            op.get(OP_ADDR).add(CORE_SERVICE, VAULT);
            ModelNode vaultOption = op.get(VAULT_OPTIONS);
            vaultOption.get("KEYSTORE_URL").set(vaultHandler.getKeyStore());
            vaultOption.get("KEYSTORE_PASSWORD").set(vaultHandler.getMaskedKeyStorePassword());
            vaultOption.get("KEYSTORE_ALIAS").set(vaultHandler.getAlias());
            vaultOption.get("SALT").set(vaultHandler.getSalt());
            vaultOption.get("ITERATION_COUNT").set(vaultHandler.getIterationCountAsString());
            vaultOption.get("ENC_FILE_DIR").set(vaultHandler.getEncodedVaultFileDirectory());
            managementClient.getControllerClient().execute(build());
            PasswordMaskingTestCase.LOGGER.debug("Vault created in sever configuration");
            // create new datasource with right password
            ModelNode address = new ModelNode();
            address.add(SUBSYSTEM, "datasources");
            address.add("data-source", PasswordMaskingTestCase.VAULT_BLOCK);
            address.protect();
            op = new ModelNode();
            op.get(OP).set(ADD);
            op.get(OP_ADDR).set(address);
            op.get("jndi-name").set(("java:jboss/datasources/" + (PasswordMaskingTestCase.VAULT_BLOCK)));
            op.get("use-java-context").set("true");
            op.get("driver-name").set("h2");
            op.get("pool-name").set(PasswordMaskingTestCase.VAULT_BLOCK);
            op.get("connection-url").set((("jdbc:h2:tcp://" + (Utils.getSecondaryTestAddress(managementClient))) + "/mem:masked"));
            op.get("user-name").set("sa");
            op.get("password").set((("${" + vaultPasswordString) + "}"));
            op.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            managementClient.getControllerClient().execute(build());
            PasswordMaskingTestCase.LOGGER.debug(((PasswordMaskingTestCase.VAULT_BLOCK) + " datasource created"));
        }

        @Override
        protected void nonManagementCleanUp() throws Exception {
            // remove temporary files
            vaultHandler.cleanUp();
            // stop DB
            server.shutdown();
        }
    }

    static final String RESOURCE_LOCATION = (PasswordMaskingTestCase.class.getResource("/").getPath()) + "security/pwdmsk-vault/";

    static final String VAULT_BLOCK = "MaskedDS";

    static final String DS_CLEAR_TEXT_PASSWORD = "sa";

    /**
     * Tests if masked ds can be accessed from servlet
     */
    @RunAsClient
    @Test
    public void servletDatasourceInjectionTest() {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        HttpGet httpget = new HttpGet(baseURL.toString());
        String responseText;
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            responseText = EntityUtils.toString(entity);
        } catch (IOException ex) {
            throw new RuntimeException("No response from servlet!", ex);
        }
        Assert.assertTrue(("Masked datasource not injected correctly to the servlet! Servlet response text: " + responseText), responseText.contains("true"));
    }
}

