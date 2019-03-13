/**
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.domain.mixed;


import DomainTestSupport.masterAddress;
import DomainTestSupport.slaveAddress;
import java.io.IOException;
import java.nio.file.Path;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.as.test.shared.TimeoutUtil;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Hugonnet (c) 2017 Red Hat, inc.
 */
public class MixedDeploymentOverlayTestCase {
    private static final int TIMEOUT = TimeoutUtil.adjust(20000);

    private static final String DEPLOYMENT_NAME = "deployment.war";

    private static final String MAIN_RUNTIME_NAME = "main-deployment.war";

    private static final String OTHER_RUNTIME_NAME = "other-deployment.war";

    private static final PathElement DEPLOYMENT_PATH = PathElement.pathElement(DEPLOYMENT, MixedDeploymentOverlayTestCase.DEPLOYMENT_NAME);

    private static final PathElement DEPLOYMENT_OVERLAY_PATH = PathElement.pathElement(DEPLOYMENT_OVERLAY, "test-overlay");

    private static final PathElement MAIN_SERVER_GROUP = PathElement.pathElement(SERVER_GROUP, "main-server-group");

    private static final PathElement OTHER_SERVER_GROUP = PathElement.pathElement(SERVER_GROUP, "other-server-group");

    private static MixedDomainTestSupport testSupport;

    private static DomainClient masterClient;

    private static DomainClient slaveClient;

    private Path overlayPath;

    @Test
    public void testInstallAndOverlayDeploymentOnDC() throws IOException, MgmtOperationException {
        // Let's deploy it on main-server-group
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, deployOnServerGroup(MixedDeploymentOverlayTestCase.MAIN_SERVER_GROUP, MixedDeploymentOverlayTestCase.MAIN_RUNTIME_NAME));
        performHttpCall(slaveAddress, 8280, "main-deployment/index.html", "Hello World");
        if (isUndertowSupported()) {
            performHttpCall(masterAddress, 8080, "main-deployment/index.html", "Hello World");
        }
        try {
            performHttpCall(slaveAddress, 8380, "main-deployment/index.html", "Hello World");
            Assert.fail(((TEST) + " is available on slave server-two"));
        } catch (IOException good) {
            // good
        }
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, deployOnServerGroup(MixedDeploymentOverlayTestCase.OTHER_SERVER_GROUP, MixedDeploymentOverlayTestCase.OTHER_RUNTIME_NAME));
        try {
            performHttpCall(slaveAddress, 8280, "other-deployment/index.html", "Hello World");
            Assert.fail(((TEST) + " is available on master server-one"));
        } catch (IOException good) {
            // good
        }
        performHttpCall(slaveAddress, 8380, "other-deployment/index.html", "Hello World");
        if (isUndertowSupported()) {
            performHttpCall(masterAddress, 8180, "other-deployment/index.html", "Hello World");
        }
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation(ADD, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode()));
        // Add some content
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, addOverlayContent(overlayPath));
        // Add overlay on server-groups
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation(ADD, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.MAIN_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode()));
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation(ADD, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.MAIN_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH, PathElement.pathElement(DEPLOYMENT, MixedDeploymentOverlayTestCase.MAIN_RUNTIME_NAME)).toModelNode()));
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation(ADD, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.MAIN_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH, PathElement.pathElement(DEPLOYMENT, MixedDeploymentOverlayTestCase.OTHER_RUNTIME_NAME)).toModelNode()));
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation(ADD, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.OTHER_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode()));
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation(ADD, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.OTHER_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH, PathElement.pathElement(DEPLOYMENT, MixedDeploymentOverlayTestCase.OTHER_RUNTIME_NAME)).toModelNode()));
        // No deployment have been redeployed so overlay isn't really active
        if (isUndertowSupported()) {
            performHttpCall(masterAddress, 8080, "main-deployment/index.html", "Hello World");
            performHttpCall(masterAddress, 8180, "other-deployment/index.html", "Hello World");
        }
        performHttpCall(slaveAddress, 8280, "main-deployment/index.html", "Hello World");
        performHttpCall(slaveAddress, 8380, "other-deployment/index.html", "Hello World");
        ModelNode redeployNothingOperation = Operations.createOperation("redeploy-links", PathAddress.pathAddress(MixedDeploymentOverlayTestCase.MAIN_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode());
        redeployNothingOperation.get("deployments").setEmptyList();
        redeployNothingOperation.get("deployments").add(MixedDeploymentOverlayTestCase.OTHER_RUNTIME_NAME);// Doesn't exist

        redeployNothingOperation.get("deployments").add("inexisting.jar");
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, redeployNothingOperation);
        // Check that nothing happened
        // Only main-server-group deployments have been redeployed so overlay isn't active for other-server-group deployments
        if (isUndertowSupported()) {
            performHttpCall(masterAddress, 8080, "main-deployment/index.html", "Hello World");
            performHttpCall(masterAddress, 8180, "other-deployment/index.html", "Hello World");
        }
        performHttpCall(slaveAddress, 8280, "main-deployment/index.html", "Hello World");
        performHttpCall(slaveAddress, 8380, "other-deployment/index.html", "Hello World");
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation("redeploy-links", PathAddress.pathAddress(MixedDeploymentOverlayTestCase.MAIN_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode()));
        // Only main-server-group deployments have been redeployed so overlay isn't active for other-server-group deployments
        if (isUndertowSupported()) {
            performHttpCall(masterAddress, 8080, "main-deployment/index.html", "Bonjour le monde");
            performHttpCall(masterAddress, 8180, "other-deployment/index.html", "Hello World");
        }
        performHttpCall(slaveAddress, 8280, "main-deployment/index.html", "Bonjour le monde");
        performHttpCall(slaveAddress, 8380, "other-deployment/index.html", "Hello World");
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation(REMOVE, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.MAIN_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH, PathElement.pathElement(DEPLOYMENT, MixedDeploymentOverlayTestCase.MAIN_RUNTIME_NAME)).toModelNode()));
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation(REMOVE, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.MAIN_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH, PathElement.pathElement(DEPLOYMENT, MixedDeploymentOverlayTestCase.OTHER_RUNTIME_NAME)).toModelNode()));
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation("redeploy-links", PathAddress.pathAddress(MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode()));
        // Only other-server-group deployments have been redeployed because we have removed the overlay from main-server-group
        if (isUndertowSupported()) {
            performHttpCall(masterAddress, 8080, "main-deployment/index.html", "Bonjour le monde");
            performHttpCall(masterAddress, 8180, "other-deployment/index.html", "Bonjour le monde");
        }
        performHttpCall(slaveAddress, 8280, "main-deployment/index.html", "Bonjour le monde");
        performHttpCall(slaveAddress, 8380, "other-deployment/index.html", "Bonjour le monde");
        // Falling call to redeploy-links
        redeployNothingOperation = Operations.createOperation("redeploy-links", PathAddress.pathAddress(MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode());
        redeployNothingOperation.get("deployments").setEmptyList();
        redeployNothingOperation.get("deployments").add(MixedDeploymentOverlayTestCase.OTHER_RUNTIME_NAME);
        redeployNothingOperation.get("deployments").add("inexisting.jar");
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, redeployNothingOperation);
        // Check that nothing happened
        redeployNothingOperation = Operations.createOperation("redeploy-links", PathAddress.pathAddress(MixedDeploymentOverlayTestCase.OTHER_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode());
        redeployNothingOperation.get("deployments").setEmptyList();
        redeployNothingOperation.get("deployments").add(MixedDeploymentOverlayTestCase.OTHER_RUNTIME_NAME);
        executeAsyncForFailure(MixedDeploymentOverlayTestCase.slaveClient, redeployNothingOperation, getUnknowOperationErrorCode());
        // Removing overlay for other-server-group deployments with affected set to true so those will be redeployed but not the deployments of main-server-group
        ModelNode removeLinkOp = Operations.createOperation(REMOVE, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.OTHER_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH, PathElement.pathElement(DEPLOYMENT, MixedDeploymentOverlayTestCase.OTHER_RUNTIME_NAME)).toModelNode());
        removeLinkOp.get("redeploy-affected").set(true);
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, removeLinkOp);
        if (isUndertowSupported()) {
            performHttpCall(masterAddress, 8080, "main-deployment/index.html", "Bonjour le monde");
            performHttpCall(masterAddress, 8180, "other-deployment/index.html", "Hello World");
        }
        performHttpCall(slaveAddress, 8280, "main-deployment/index.html", "Bonjour le monde");
        performHttpCall(slaveAddress, 8380, "other-deployment/index.html", "Hello World");
        // Redeploying main-server-group deployment called main-deployment.war
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation(ADD, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.OTHER_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH, PathElement.pathElement(DEPLOYMENT, MixedDeploymentOverlayTestCase.OTHER_RUNTIME_NAME)).toModelNode()));
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, Operations.createOperation(ADD, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.MAIN_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH, PathElement.pathElement(DEPLOYMENT, MixedDeploymentOverlayTestCase.MAIN_RUNTIME_NAME)).toModelNode()));
        ModelNode redeployOp = Operations.createOperation("redeploy-links", PathAddress.pathAddress(MixedDeploymentOverlayTestCase.MAIN_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode());
        redeployOp.get("deployments").setEmptyList();
        redeployOp.get("deployments").add(MixedDeploymentOverlayTestCase.MAIN_RUNTIME_NAME);
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, redeployOp);
        if (isUndertowSupported()) {
            performHttpCall(masterAddress, 8080, "main-deployment/index.html", "Bonjour le monde");
            performHttpCall(masterAddress, 8180, "other-deployment/index.html", "Hello World");
        }
        performHttpCall(slaveAddress, 8280, "main-deployment/index.html", "Bonjour le monde");
        performHttpCall(slaveAddress, 8380, "other-deployment/index.html", "Hello World");
        // Redeploying main-server-group deployment called other-deployment.war
        redeployOp = Operations.createOperation("redeploy-links", PathAddress.pathAddress(MixedDeploymentOverlayTestCase.OTHER_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode());
        redeployOp.get("deployments").setEmptyList();
        redeployOp.get("deployments").add(MixedDeploymentOverlayTestCase.OTHER_RUNTIME_NAME);
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, redeployOp);
        if (isUndertowSupported()) {
            performHttpCall(masterAddress, 8080, "main-deployment/index.html", "Bonjour le monde");
            performHttpCall(masterAddress, 8180, "other-deployment/index.html", "Bonjour le monde");
        }
        performHttpCall(slaveAddress, 8280, "main-deployment/index.html", "Bonjour le monde");
        performHttpCall(slaveAddress, 8380, "other-deployment/index.html", "Bonjour le monde");
        // Remove all CLI style "deployment-overlay remove --name=overlay-test "
        ModelNode cliRemoveOverlay = Operations.createCompositeOperation();
        cliRemoveOverlay.get(STEPS).add(Operations.createOperation(REMOVE, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.MAIN_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode()));
        cliRemoveOverlay.get(STEPS).add(Operations.createOperation(REMOVE, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.OTHER_SERVER_GROUP, MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode()));
        cliRemoveOverlay.get(STEPS).add(Operations.createOperation(REMOVE, PathAddress.pathAddress(MixedDeploymentOverlayTestCase.DEPLOYMENT_OVERLAY_PATH).toModelNode()));
        executeAsyncForResult(MixedDeploymentOverlayTestCase.masterClient, cliRemoveOverlay);
    }
}

