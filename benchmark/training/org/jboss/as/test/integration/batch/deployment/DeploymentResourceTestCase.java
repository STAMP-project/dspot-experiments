/**
 * Copyright 2016 Red Hat, Inc.
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
package org.jboss.as.test.integration.batch.deployment;


import ModelDescriptionConstants.INCLUDE_RUNTIME;
import java.util.Arrays;
import java.util.Collections;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.batch.common.AbstractBatchTestCase;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the start, stop and restart functionality for deployments.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DeploymentResourceTestCase extends AbstractBatchTestCase {
    private static final String DEPLOYMENT_NAME_1 = "test-batch-1.war";

    private static final String DEPLOYMENT_NAME_2 = "test-batch-2.war";

    // @OperateOnDeployment is required until WFARQ-13 is resolved
    @ArquillianResource
    @OperateOnDeployment(DeploymentResourceTestCase.DEPLOYMENT_NAME_1)
    private ManagementClient managementClient;

    @Test
    public void testRootResourceJobXmlListing() throws Exception {
        // First deployment should only have two available XML descriptors
        validateJobXmlNames(DeploymentResourceTestCase.DEPLOYMENT_NAME_1, "test-chunk.xml", "same-test-chunk.xml");
        // Second deployment should have 3 available descriptors and one missing descriptor as it's invalid
        validateJobXmlNames(DeploymentResourceTestCase.DEPLOYMENT_NAME_2, Arrays.asList("test-chunk.xml", "same-test-chunk.xml", "test-chunk-other.xml"), Collections.singleton("invalid.xml"));
    }

    @Test
    public void testDeploymentJobXmlListing() throws Exception {
        // First deployment should have two available XML descriptors on the single job
        ModelNode address = Operations.createAddress("deployment", DeploymentResourceTestCase.DEPLOYMENT_NAME_1, "subsystem", "batch-jberet", "job", "test-chunk");
        validateJobXmlNames(address, "test-chunk.xml", "same-test-chunk.xml");
        // Second deployment should have two available jobs. The first job should have two available XML descriptors the
        // second job should only have one descriptor.
        address = Operations.createAddress("deployment", DeploymentResourceTestCase.DEPLOYMENT_NAME_2, "subsystem", "batch-jberet", "job", "test-chunk");
        validateJobXmlNames(address, "test-chunk.xml", "same-test-chunk.xml");
        address = Operations.createAddress("deployment", DeploymentResourceTestCase.DEPLOYMENT_NAME_2, "subsystem", "batch-jberet", "job", "test-chunk-other");
        validateJobXmlNames(address, "test-chunk-other.xml");
    }

    @Test
    public void testEmptyResources() throws Exception {
        final ModelNode address = Operations.createAddress("deployment", DeploymentResourceTestCase.DEPLOYMENT_NAME_2, "subsystem", "batch-jberet");
        final ModelNode op = Operations.createReadResourceOperation(address, true);
        op.get(INCLUDE_RUNTIME).set(true);
        final ModelNode result = executeOperation(op);
        final ModelNode otherJob = result.get("job", "test-chunk-other");
        Assert.assertTrue("Expected the test-chunk-other job resource to exist", otherJob.isDefined());
        Assert.assertEquals(0, otherJob.get("instance-count").asInt());
        Assert.assertEquals(0, otherJob.get("running-executions").asInt());
        Assert.assertFalse(otherJob.get("executions").isDefined());
    }
}

