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
package org.jboss.as.test.integration.messaging.mgmt;


import ClientConstants.FAILURE_DESCRIPTION;
import ClientConstants.OUTCOME;
import ClientConstants.SUCCESS;
import TransportConstants.HOST_PROP_NAME;
import TransportConstants.PORT_PROP_NAME;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.as.test.shared.SnapshotRestoreSetupTask;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by spyrkob on 18/05/2017.
 */
@RunWith(Arquillian.class)
@ServerSetup({ ConnectionFactoryClientMappingTestCase.SetupTask.class })
public class ConnectionFactoryClientMappingTestCase {
    private static final String CONNECTION_FACTORY_JNDI_NAME = "java:jboss/exported/jms/TestConnectionFactory";

    static class SetupTask extends SnapshotRestoreSetupTask {
        private static final Logger logger = Logger.getLogger(ConnectionFactoryClientMappingTestCase.SetupTask.class);

        @Override
        public void doSetup(ManagementClient managementClient, String s) throws Exception {
            JMSOperations ops = JMSOperationsProvider.getInstance(managementClient.getControllerClient());
            addSocketBinding(managementClient, "test-binding", clientMapping("test", "8000"));
            Map<String, String> parameters = new HashMap<>();
            parameters.put("multicast-prefix", "jms.topic");
            parameters.put("anycast-prefix", "jms.queue");
            ops.addHttpConnector("http-test-connector", "test-binding", "http-acceptor", parameters);
            ModelNode attr = new ModelNode();
            attr.get("connectors").add("http-test-connector");
            ops.addJmsConnectionFactory("TestConnectionFactory", ConnectionFactoryClientMappingTestCase.CONNECTION_FACTORY_JNDI_NAME, attr);
            ServerReload.executeReloadAndWaitForCompletion(managementClient.getControllerClient());
        }

        private ModelNode clientMapping(String destAddr, String destPort) {
            ModelNode clientMapping = new ModelNode();
            clientMapping.get("destination-address").set(destAddr);
            clientMapping.get("destination-port").set(destPort);
            return clientMapping;
        }

        private void addSocketBinding(ManagementClient managementClient, String bindingName, ModelNode clientMapping) throws Exception {
            ModelNode address = new ModelNode();
            address.add("socket-binding-group", "standard-sockets");
            address.add("socket-binding", bindingName);
            ModelNode socketBindingOp = new ModelNode();
            socketBindingOp.get(OP).set(ADD);
            socketBindingOp.get(OP_ADDR).set(address);
            ConnectionFactoryClientMappingTestCase.SetupTask.execute(managementClient, socketBindingOp);
            ModelNode clientMappingOp = new ModelNode();
            clientMappingOp.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            clientMappingOp.get(OP_ADDR).set(address);
            clientMappingOp.get(NAME).set("client-mappings");
            clientMappingOp.get(VALUE).add(clientMapping);
            ConnectionFactoryClientMappingTestCase.SetupTask.execute(managementClient, clientMappingOp);
        }

        static void execute(ManagementClient managementClient, final ModelNode operation) throws IOException {
            ModelNode result = managementClient.getControllerClient().execute(operation);
            if ((result.hasDefined(OUTCOME)) && (SUCCESS.equals(result.get(OUTCOME).asString()))) {
                ConnectionFactoryClientMappingTestCase.SetupTask.logger.trace(("Operation successful for update = " + (operation.toString())));
            } else
                if (result.hasDefined(FAILURE_DESCRIPTION)) {
                    final String failureDesc = result.get(FAILURE_DESCRIPTION).toString();
                    throw new RuntimeException(failureDesc);
                } else {
                    throw new RuntimeException(("Operation not successful; outcome = " + (result.get(OUTCOME))));
                }

        }
    }

    @Resource(lookup = "java:jboss/exported/jms/TestConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Test
    public void testClientMappingInConnectionFactory() throws Exception {
        Map<String, Object> params = getStaticConnectors()[0].getParams();
        Assert.assertEquals("test", params.get(HOST_PROP_NAME));
        Assert.assertEquals(8000, params.get(PORT_PROP_NAME));
    }
}

