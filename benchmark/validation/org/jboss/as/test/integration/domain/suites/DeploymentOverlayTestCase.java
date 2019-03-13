/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.domain.suites;


import ModelDescriptionConstants.DEPLOYMENT_OVERLAY;
import java.io.File;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of various management operations involving deployment overlays
 */
public class DeploymentOverlayTestCase {
    public static final String TEST_OVERLAY = "test";

    public static final String TEST_WILDCARD = "test-server";

    private static final String TEST = "test.war";

    private static final String REPLACEMENT = "test.war.v2";

    private static final ModelNode ROOT_ADDRESS = new ModelNode();

    private static final ModelNode ROOT_DEPLOYMENT_ADDRESS = new ModelNode();

    private static final ModelNode ROOT_REPLACEMENT_ADDRESS = new ModelNode();

    private static final ModelNode MAIN_SERVER_GROUP_ADDRESS = new ModelNode();

    private static final ModelNode MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS = new ModelNode();

    private static final ModelNode MAIN_RUNNING_SERVER_ADDRESS = new ModelNode();

    private static final ModelNode MAIN_RUNNING_SERVER_DEPLOYMENT_ADDRESS = new ModelNode();

    static {
        DeploymentOverlayTestCase.ROOT_ADDRESS.setEmptyList();
        DeploymentOverlayTestCase.ROOT_ADDRESS.protect();
        DeploymentOverlayTestCase.ROOT_DEPLOYMENT_ADDRESS.add(DEPLOYMENT, DeploymentOverlayTestCase.TEST);
        DeploymentOverlayTestCase.ROOT_DEPLOYMENT_ADDRESS.protect();
        DeploymentOverlayTestCase.ROOT_REPLACEMENT_ADDRESS.add(DEPLOYMENT, DeploymentOverlayTestCase.REPLACEMENT);
        DeploymentOverlayTestCase.ROOT_REPLACEMENT_ADDRESS.protect();
        DeploymentOverlayTestCase.MAIN_SERVER_GROUP_ADDRESS.add(SERVER_GROUP, "main-server-group");
        DeploymentOverlayTestCase.MAIN_SERVER_GROUP_ADDRESS.protect();
        DeploymentOverlayTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS.add(SERVER_GROUP, "main-server-group");
        DeploymentOverlayTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS.add(DEPLOYMENT, DeploymentOverlayTestCase.TEST);
        DeploymentOverlayTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS.protect();
        DeploymentOverlayTestCase.MAIN_RUNNING_SERVER_ADDRESS.add(HOST, "master");
        DeploymentOverlayTestCase.MAIN_RUNNING_SERVER_ADDRESS.add(SERVER, "main-one");
        DeploymentOverlayTestCase.MAIN_RUNNING_SERVER_ADDRESS.protect();
        DeploymentOverlayTestCase.MAIN_RUNNING_SERVER_DEPLOYMENT_ADDRESS.add(HOST, "master");
        DeploymentOverlayTestCase.MAIN_RUNNING_SERVER_DEPLOYMENT_ADDRESS.add(SERVER, "main-one");
        DeploymentOverlayTestCase.MAIN_RUNNING_SERVER_DEPLOYMENT_ADDRESS.add(DEPLOYMENT, DeploymentOverlayTestCase.TEST);
        DeploymentOverlayTestCase.MAIN_RUNNING_SERVER_DEPLOYMENT_ADDRESS.protect();
    }

    private static DomainTestSupport testSupport;

    private static WebArchive webArchive;

    private static File tmpDir;

    /**
     * This test creates and links two deployment overlays, does a deployment, and then tests that the overlay has taken effect
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDeploymentOverlayInDomainMode() throws Exception {
        setupDeploymentOverride();
        ModelNode content = new ModelNode();
        content.get(INPUT_STREAM_INDEX).set(0);
        ModelNode composite = DeploymentOverlayTestCase.createDeploymentOperation(content, DeploymentOverlayTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        OperationBuilder builder = new OperationBuilder(composite, true);
        builder.addInputStream(DeploymentOverlayTestCase.webArchive.as(ZipExporter.class).exportAsInputStream());
        DeploymentOverlayTestCase.executeOnMaster(builder.build());
        DomainClient client = DeploymentOverlayTestCase.testSupport.getDomainMasterLifecycleUtil().createDomainClient();
        Assert.assertEquals("OVERRIDDEN", performHttpCall(client, "master", "main-one", "standard-sockets", "/test/servlet"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall(client, "slave", "main-three", "standard-sockets", "/test/servlet"));
        Assert.assertEquals("new file", performHttpCall(client, "master", "main-one", "standard-sockets", "/test/wildcard-new-file.txt"));
        Assert.assertEquals("new file", performHttpCall(client, "slave", "main-three", "standard-sockets", "/test/wildcard-new-file.txt"));
        // Remove the wildcard overlay
        ModelNode op = Operations.createRemoveOperation(PathAddress.pathAddress(ModelDescriptionConstants.SERVER_GROUP, "main-server-group").append(DEPLOYMENT_OVERLAY, DeploymentOverlayTestCase.TEST_WILDCARD).append(ModelDescriptionConstants.DEPLOYMENT, "*.war").toModelNode());
        op.get("redeploy-affected").set(true);
        DeploymentOverlayTestCase.executeOnMaster(op);
        Assert.assertEquals("OVERRIDDEN", performHttpCall(client, "master", "main-one", "standard-sockets", "/test/servlet"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall(client, "slave", "main-three", "standard-sockets", "/test/servlet"));
        Assert.assertEquals("<html><head><title>Error</title></head><body>Not Found</body></html>", performHttpCall(client, "master", "main-one", "standard-sockets", "/test/wildcard-new-file.txt"));
        Assert.assertEquals("<html><head><title>Error</title></head><body>Not Found</body></html>", performHttpCall(client, "slave", "main-three", "standard-sockets", "/test/wildcard-new-file.txt"));
        op = Operations.createRemoveOperation(PathAddress.pathAddress(ModelDescriptionConstants.SERVER_GROUP, "main-server-group").append(DEPLOYMENT_OVERLAY, DeploymentOverlayTestCase.TEST_OVERLAY).append(ModelDescriptionConstants.DEPLOYMENT, "test.war").toModelNode());
        op.get("redeploy-affected").set(true);
        DeploymentOverlayTestCase.executeOnMaster(op);
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall(client, "master", "main-one", "standard-sockets", "/test/servlet"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall(client, "slave", "main-three", "standard-sockets", "/test/servlet"));
        Assert.assertEquals("<html><head><title>Error</title></head><body>Not Found</body></html>", performHttpCall(client, "master", "main-one", "standard-sockets", "/test/wildcard-new-file.txt"));
        Assert.assertEquals("<html><head><title>Error</title></head><body>Not Found</body></html>", performHttpCall(client, "slave", "main-three", "standard-sockets", "/test/wildcard-new-file.txt"));
    }
}

