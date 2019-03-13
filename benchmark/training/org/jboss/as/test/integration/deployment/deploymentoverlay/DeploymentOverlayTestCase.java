package org.jboss.as.test.integration.deployment.deploymentoverlay;


import ModelDescriptionConstants.ADD;
import ModelDescriptionConstants.BYTES;
import ModelDescriptionConstants.CONTENT;
import ModelDescriptionConstants.DEPLOYMENT;
import ModelDescriptionConstants.DEPLOYMENT_OVERLAY;
import ModelDescriptionConstants.HASH;
import ModelDescriptionConstants.INPUT_STREAM_INDEX;
import ModelDescriptionConstants.OP;
import ModelDescriptionConstants.OP_ADDR;
import ModelDescriptionConstants.REMOVE;
import ModelDescriptionConstants.UPLOAD_DEPLOYMENT_BYTES;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.management.ManagementOperations;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.as.test.shared.FileUtils;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
@ServerSetup(DeploymentOverlayTestCase.DeploymentOverlayTestCaseServerSetup.class)
public class DeploymentOverlayTestCase {
    public static final String TEST_OVERLAY = "test";

    public static final String TEST_WILDCARD = "test-wildcard";

    static class DeploymentOverlayTestCaseServerSetup implements ServerSetupTask {
        @Override
        public void setup(final ManagementClient managementClient, final String containerId) throws Exception {
            ModelNode op = new ModelNode();
            op.get(OP_ADDR).set(DEPLOYMENT_OVERLAY, DeploymentOverlayTestCase.TEST_OVERLAY);
            op.get(OP).set(ADD);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
            // add an override that will not be linked via a wildcard
            op = new ModelNode();
            op.get(OP_ADDR).set(new ModelNode());
            op.get(OP).set(UPLOAD_DEPLOYMENT_BYTES);
            op.get(BYTES).set(FileUtils.readFile(DeploymentOverlayTestCase.class, "override.xml").getBytes(StandardCharsets.UTF_8));
            ModelNode result = ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
            // add the content
            op = new ModelNode();
            ModelNode addr = new ModelNode();
            addr.add(DEPLOYMENT_OVERLAY, DeploymentOverlayTestCase.TEST_OVERLAY);
            addr.add(CONTENT, "WEB-INF/web.xml");
            op.get(OP_ADDR).set(addr);
            op.get(OP).set(ADD);
            op.get(CONTENT).get(HASH).set(result);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
            // add the non-wildcard link
            op = new ModelNode();
            addr = new ModelNode();
            addr.add(DEPLOYMENT_OVERLAY, DeploymentOverlayTestCase.TEST_OVERLAY);
            addr.add(DEPLOYMENT, "test.war");
            op.get(OP_ADDR).set(addr);
            op.get(OP).set(ADD);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
            // add the deployment overlay that will be linked via wildcard
            op = new ModelNode();
            op.get(OP_ADDR).set(DEPLOYMENT_OVERLAY, DeploymentOverlayTestCase.TEST_WILDCARD);
            op.get(OP).set(ADD);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
            op = new ModelNode();
            addr = new ModelNode();
            addr.add(DEPLOYMENT_OVERLAY, DeploymentOverlayTestCase.TEST_WILDCARD);
            addr.add(CONTENT, "WEB-INF/web.xml");
            op.get(OP_ADDR).set(addr);
            op.get(OP).set(ADD);
            op.get(CONTENT).get(BYTES).set(FileUtils.readFile(DeploymentOverlayTestCase.class, "wildcard-override.xml").getBytes(StandardCharsets.UTF_8));
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
            op = new ModelNode();
            addr = new ModelNode();
            addr.add(DEPLOYMENT_OVERLAY, DeploymentOverlayTestCase.TEST_WILDCARD);
            addr.add(CONTENT, "WEB-INF/classes/wildcard-new-file");
            op.get(OP_ADDR).set(addr);
            op.get(OP).set(ADD);
            op.get(CONTENT).get(INPUT_STREAM_INDEX).set(0);
            OperationBuilder builder = new OperationBuilder(op, true);
            builder.addInputStream(DeploymentOverlayTestCase.class.getResourceAsStream("wildcard-new-file"));
            ManagementOperations.executeOperation(managementClient.getControllerClient(), builder.build());
            // add the wildcard link
            op = new ModelNode();
            addr = new ModelNode();
            addr.add(DEPLOYMENT_OVERLAY, DeploymentOverlayTestCase.TEST_WILDCARD);
            addr.add(DEPLOYMENT, "*.war");
            op.get(OP_ADDR).set(addr);
            op.get(OP).set(ADD);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
        }

        @Override
        public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
            removeContentItem(managementClient, DeploymentOverlayTestCase.TEST_OVERLAY, "WEB-INF/web.xml");
            removeDeploymentItem(managementClient, DeploymentOverlayTestCase.TEST_OVERLAY, "test.war");
            ModelNode op = new ModelNode();
            op.get(OP_ADDR).set(DEPLOYMENT_OVERLAY, DeploymentOverlayTestCase.TEST_OVERLAY);
            op.get(OP).set(REMOVE);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
            removeDeploymentItem(managementClient, DeploymentOverlayTestCase.TEST_WILDCARD, "*.war");
            removeContentItem(managementClient, DeploymentOverlayTestCase.TEST_WILDCARD, "WEB-INF/web.xml");
            removeContentItem(managementClient, DeploymentOverlayTestCase.TEST_WILDCARD, "WEB-INF/classes/wildcard-new-file");
            op = new ModelNode();
            op.get(OP_ADDR).set(DEPLOYMENT_OVERLAY, DeploymentOverlayTestCase.TEST_WILDCARD);
            op.get(OP).set(REMOVE);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
        }

        private void removeContentItem(final ManagementClient managementClient, final String overlayName, final String content) throws IOException, MgmtOperationException {
            final ModelNode addr = new ModelNode();
            addr.add(DEPLOYMENT_OVERLAY, overlayName);
            addr.add(CONTENT, content);
            final ModelNode op = Operations.createRemoveOperation(addr);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
        }

        private void removeDeploymentItem(final ManagementClient managementClient, final String overlayName, final String deploymentRuntimeName) throws IOException, MgmtOperationException {
            final ModelNode addr = new ModelNode();
            addr.add(DEPLOYMENT_OVERLAY, overlayName);
            addr.add(DEPLOYMENT, deploymentRuntimeName);
            final ModelNode op = Operations.createRemoveOperation(addr);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
        }
    }

    @ArquillianResource
    private InitialContext initialContext;

    @Test
    public void testContentOverridden() throws NamingException {
        Assert.assertEquals("OVERRIDDEN", initialContext.lookup("java:module/env/simpleString"));
    }

    @Test
    public void testAddingNewFile() {
        Assert.assertNotNull(getClass().getClassLoader().getResource("wildcard-new-file"));
    }
}

