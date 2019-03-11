/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.manualmode.layered;


import ModelDescriptionConstants.NAME;
import ModelDescriptionConstants.OP;
import ModelDescriptionConstants.READ_ATTRIBUTE_OPERATION;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.as.test.integration.management.ManagementOperations;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Basic layered distribution integration test.
 *
 * @author Dominik Pospisil <dpospisi@redhat.com>
 */
@RunWith(Arquillian.class)
public class LayeredDistributionTestCase {
    private static final Logger log = Logger.getLogger(LayeredDistributionTestCase.class);

    private static final String CONTAINER = "jbossas-layered";

    private static final Path AS_PATH = Paths.get("target", LayeredDistributionTestCase.CONTAINER);

    private final Path layersDir = Paths.get(LayeredDistributionTestCase.AS_PATH.toString(), "modules", "system", "layers");

    private static final String TEST_LAYER = "test";

    private static final String PRODUCT_NAME = "Test-Product";

    private static final String PRODUCT_VERSION = "1.0.0-Test";

    private static final String DEPLOYMENT = "test-deployment";

    private static final String webURI = ("http://" + (TestSuiteEnvironment.getServerAddress())) + ":8080";

    @ArquillianResource
    private ContainerController controller;

    @ArquillianResource
    private Deployer deployer;

    @Test
    @InSequence(-1)
    public void before() throws Exception {
        buildLayer(LayeredDistributionTestCase.TEST_LAYER);
        buildProductModule(LayeredDistributionTestCase.TEST_LAYER);
        buildTestModule(LayeredDistributionTestCase.TEST_LAYER);
        LayeredDistributionTestCase.log.trace("===starting server===");
        controller.start(LayeredDistributionTestCase.CONTAINER);
        LayeredDistributionTestCase.log.trace("===appserver started===");
        // deployer.deploy(DEPLOYMENT);
        // log.trace("===deployment deployed===");
    }

    @Test
    @InSequence(1)
    public void after() throws Exception {
        try {
            // deployer.undeploy(DEPLOYMENT);
            // log.trace("===deployment undeployed===");
        } finally {
            controller.stop(LayeredDistributionTestCase.CONTAINER);
            LayeredDistributionTestCase.log.trace("===appserver stopped===");
        }
    }

    @Test
    public void testLayeredProductVersion() throws Throwable {
        final ModelControllerClient client = TestSuiteEnvironment.getModelControllerClient();
        ModelNode readAttrOp = new ModelNode();
        readAttrOp.get(OP).set(READ_ATTRIBUTE_OPERATION);
        readAttrOp.get(NAME).set("product-name");
        ModelNode result = ManagementOperations.executeOperation(client, readAttrOp);
        Assert.assertEquals(result.asString(), LayeredDistributionTestCase.PRODUCT_NAME);
        readAttrOp.get(NAME).set("product-version");
        result = ManagementOperations.executeOperation(client, readAttrOp);
        Assert.assertEquals(result.asString(), LayeredDistributionTestCase.PRODUCT_VERSION);
    }

    @Test
    public void testLayeredDeployment() throws Throwable {
        deployer.deploy(LayeredDistributionTestCase.DEPLOYMENT);
        // test that the deployment can access class from a layered module
        String response = HttpRequest.get(((LayeredDistributionTestCase.webURI) + "/test-deployment/LayeredTestServlet"), 10, TimeUnit.SECONDS);
        Assert.assertTrue(response.contains("LayeredTestServlet"));
        deployer.undeploy(LayeredDistributionTestCase.DEPLOYMENT);
    }
}

