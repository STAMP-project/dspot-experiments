/**
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.jca.security;


import ClientConstants.OPERATION_HEADERS;
import ModelDescriptionConstants.SUBSYSTEM;
import java.io.IOException;
import javax.annotation.Resource;
import javax.resource.cci.Connection;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.jca.rar.MultipleConnectionFactory1;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.test.security.common.AbstractElytronSetupTask;
import org.wildfly.test.security.common.elytron.ConfigurableElement;
import org.wildfly.test.security.common.elytron.CredentialReference;
import org.wildfly.test.security.common.elytron.MatchRules;
import org.wildfly.test.security.common.elytron.SimpleAuthConfig;
import org.wildfly.test.security.common.elytron.SimpleAuthContext;


/**
 * Test for RA with elytron security domain, RA is activated using resource-adapter subsystem
 *
 * @author Flavia Rainone
 */
@RunWith(Arquillian.class)
@ServerSetup({ WildFlyActivationRaWithElytronAuthContextTestCase.ElytronSetup.class, WildFlyActivationRaWithElytronAuthContextTestCase.RaSetup.class })
public class WildFlyActivationRaWithElytronAuthContextTestCase {
    private static final String AUTH_CONFIG = "MyAuthConfig";

    private static final String AUTH_CONTEXT = "MyAuthContext";

    private static final String CREDENTIAL = "sa";

    private static final String CONN_DEF_JNDI_NAME = "java:jboss/wf-ra-elytron-security";

    static class ElytronSetup extends AbstractElytronSetupTask {
        @Override
        protected ConfigurableElement[] getConfigurableElements() {
            final CredentialReference credRefPwd = CredentialReference.builder().withClearText(WildFlyActivationRaWithElytronAuthContextTestCase.CREDENTIAL).build();
            final ConfigurableElement authenticationConfiguration = SimpleAuthConfig.builder().withName(WildFlyActivationRaWithElytronAuthContextTestCase.AUTH_CONFIG).withAuthenticationName(WildFlyActivationRaWithElytronAuthContextTestCase.CREDENTIAL).withCredentialReference(credRefPwd).build();
            final MatchRules matchRules = MatchRules.builder().withAuthenticationConfiguration(WildFlyActivationRaWithElytronAuthContextTestCase.AUTH_CONFIG).build();
            final ConfigurableElement authenticationContext = SimpleAuthContext.builder().withName(WildFlyActivationRaWithElytronAuthContextTestCase.AUTH_CONTEXT).withMatchRules(matchRules).build();
            return new ConfigurableElement[]{ authenticationConfiguration, authenticationContext };
        }
    }

    static class RaSetup implements ServerSetupTask {
        private static final PathAddress RA_ADDRESS = PathAddress.pathAddress(SUBSYSTEM, "resource-adapters").append("resource-adapter", "wf-ra-elytron-security");

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            ModelControllerClient mcc = managementClient.getControllerClient();
            addResourceAdapter(mcc);
            addConnectionDefinition(mcc);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            removeResourceAdapterSilently(managementClient.getControllerClient());
        }

        private void addResourceAdapter(ModelControllerClient client) throws IOException {
            ModelNode addRaOperation = Operations.createAddOperation(WildFlyActivationRaWithElytronAuthContextTestCase.RaSetup.RA_ADDRESS.toModelNode());
            addRaOperation.get("archive").set("wf-ra-ely-security.rar");
            addRaOperation.get("transaction-support").set("NoTransaction");
            ModelNode response = execute(addRaOperation, client);
            Assert.assertEquals(response.toString(), SUCCESS, response.get(OUTCOME).asString());
        }

        private void addConnectionDefinition(ModelControllerClient client) throws IOException {
            PathAddress connectionDefinitionAddress = WildFlyActivationRaWithElytronAuthContextTestCase.RaSetup.RA_ADDRESS.append("connection-definitions", "Pool1");
            ModelNode addConnectionDefinitionOperation = Operations.createAddOperation(connectionDefinitionAddress.toModelNode());
            addConnectionDefinitionOperation.get("class-name").set("org.jboss.as.test.integration.jca.rar.MultipleManagedConnectionFactoryWithSubjectVerification");
            addConnectionDefinitionOperation.get("jndi-name").set(WildFlyActivationRaWithElytronAuthContextTestCase.CONN_DEF_JNDI_NAME);
            addConnectionDefinitionOperation.get("elytron-enabled").set("true");
            addConnectionDefinitionOperation.get("authentication-context").set(WildFlyActivationRaWithElytronAuthContextTestCase.AUTH_CONTEXT);
            ModelNode response = execute(addConnectionDefinitionOperation, client);
            Assert.assertEquals(response.toString(), SUCCESS, response.get(OUTCOME).asString());
        }

        private void removeResourceAdapterSilently(ModelControllerClient client) throws IOException {
            ModelNode removeRaOperation = Operations.createRemoveOperation(WildFlyActivationRaWithElytronAuthContextTestCase.RaSetup.RA_ADDRESS.toModelNode());
            removeRaOperation.get(OPERATION_HEADERS).get("allow-resource-service-restart").set("true");
            client.execute(removeRaOperation);
        }

        private ModelNode execute(ModelNode operation, ModelControllerClient client) throws IOException {
            return client.execute(operation);
        }
    }

    @Resource(mappedName = WildFlyActivationRaWithElytronAuthContextTestCase.CONN_DEF_JNDI_NAME)
    private MultipleConnectionFactory1 connectionFactory1;

    @ArquillianResource
    private ManagementClient client;

    @Test
    public void deploymentTest() throws Exception {
        Assert.assertNotNull("CF1 not found", connectionFactory1);
        Connection cci = connectionFactory1.getConnection();
        Assert.assertNotNull("Cannot obtain connection", cci);
        cci.close();
    }
}

