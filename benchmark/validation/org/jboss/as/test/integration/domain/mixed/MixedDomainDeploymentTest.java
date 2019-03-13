/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.integration.domain.mixed;


import DomainTestSupport.slaveAddress;
import java.io.File;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public abstract class MixedDomainDeploymentTest {
    private static final String TEST = "test.war";

    private static final String REPLACEMENT = "test.war.v2";

    private static final PathAddress ROOT_DEPLOYMENT_ADDRESS = PathAddress.pathAddress(DEPLOYMENT, MixedDomainDeploymentTest.TEST);

    private static final PathAddress ROOT_REPLACEMENT_ADDRESS = PathAddress.pathAddress(DEPLOYMENT, MixedDomainDeploymentTest.REPLACEMENT);

    private static final PathAddress OTHER_SERVER_GROUP_ADDRESS = PathAddress.pathAddress(SERVER_GROUP, "other-server-group");

    private static final PathAddress OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS = MixedDomainDeploymentTest.OTHER_SERVER_GROUP_ADDRESS.append(DEPLOYMENT, MixedDomainDeploymentTest.TEST);

    private static final PathAddress MAIN_SERVER_GROUP_ADDRESS = PathAddress.pathAddress(SERVER_GROUP, "main-server-group");

    private static final PathAddress MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS = MixedDomainDeploymentTest.MAIN_SERVER_GROUP_ADDRESS.append(DEPLOYMENT, MixedDomainDeploymentTest.TEST);

    private WebArchive webArchive;

    private WebArchive webArchive2;

    private WebArchive jsfTestArchive;

    private MixedDomainTestSupport testSupport;

    private File tmpDir;

    @Test
    public void testDeploymentViaUrl() throws Exception {
        String url = new File(tmpDir, ("archives/" + (MixedDomainDeploymentTest.TEST))).toURI().toURL().toString();
        ModelNode content = new ModelNode();
        content.get("url").set(url);
        ModelNode composite = createDeploymentOperation(content, MixedDomainDeploymentTest.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS, MixedDomainDeploymentTest.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        executeOnMaster(composite);
        performHttpCall(slaveAddress, 8080);
    }

    @Test
    public void testDeploymentViaStream() throws Exception {
        ModelNode content = new ModelNode();
        content.get(INPUT_STREAM_INDEX).set(0);
        ModelNode composite = createDeploymentOperation(content, MixedDomainDeploymentTest.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS, MixedDomainDeploymentTest.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        OperationBuilder builder = new OperationBuilder(composite, true);
        builder.addInputStream(webArchive.as(ZipExporter.class).exportAsInputStream());
        executeOnMaster(builder.build());
        performHttpCall(slaveAddress, 8080);
    }

    @Test
    public void testUnmanagedArchiveDeployment() throws Exception {
        ModelNode content = new ModelNode();
        content.get("archive").set(true);
        content.get("path").set(new File(tmpDir, ("archives/" + (MixedDomainDeploymentTest.TEST))).getAbsolutePath());
        ModelNode composite = createDeploymentOperation(content, MixedDomainDeploymentTest.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS, MixedDomainDeploymentTest.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        executeOnMaster(composite);
        performHttpCall(slaveAddress, 8080);
    }

    @Test
    public void testUnmanagedExplodedDeployment() throws Exception {
        ModelNode content = new ModelNode();
        content.get("archive").set(false);
        content.get("path").set(new File(tmpDir, ("exploded/" + (MixedDomainDeploymentTest.TEST))).getAbsolutePath());
        ModelNode composite = createDeploymentOperation(content, MixedDomainDeploymentTest.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS, MixedDomainDeploymentTest.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        executeOnMaster(composite);
        performHttpCall(slaveAddress, 8080);
    }

    @Test
    public void testExplodedEmptyDeployment() throws Exception {
        ModelNode empty = new ModelNode();
        empty.get(EMPTY).set(true);
        ModelNode composite = createDeploymentOperation(empty, MixedDomainDeploymentTest.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS, MixedDomainDeploymentTest.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        if (supportManagedExplodedDeployment()) {
            executeOnMaster(composite);
        } else {
            ModelNode failure = executeForFailureOnMaster(composite);
            Assert.assertTrue(failure.toJSONString(true), failure.toJSONString(true).contains("WFLYCTL0421:"));
        }
    }

    @Test
    public void testExplodedDeployment() throws Exception {
        // TODO WFLY-9634
        Assume.assumeFalse(supportManagedExplodedDeployment());
        ModelNode composite = createEmptyOperation(COMPOSITE, PathAddress.EMPTY_ADDRESS);
        ModelNode steps = composite.get(STEPS);
        ModelNode op = createAddOperation(MixedDomainDeploymentTest.ROOT_DEPLOYMENT_ADDRESS);
        op.get(CONTENT).setEmptyList();
        ModelNode empty = new ModelNode();
        empty.get(EMPTY).set(true);
        op.get(CONTENT).add(empty);
        steps.add(op);
        op = createEmptyOperation(ADD_CONTENT, MixedDomainDeploymentTest.ROOT_DEPLOYMENT_ADDRESS);
        op.get(CONTENT).setEmptyList();
        ModelNode file = new ModelNode();
        file.get(TARGET_PATH).set("index.html");
        file.get(INPUT_STREAM_INDEX).set(0);
        op.get(CONTENT).add(file);
        file = new ModelNode();
        file.get(TARGET_PATH).set("index2.html");
        file.get(INPUT_STREAM_INDEX).set(1);
        op.get(CONTENT).add(file);
        steps.add(op);
        ModelNode sg = steps.add();
        sg.set(createAddOperation(MixedDomainDeploymentTest.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS));
        sg.get(ENABLED).set(true);
        sg = steps.add();
        sg.set(createAddOperation(MixedDomainDeploymentTest.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS));
        sg.get(ENABLED).set(true);
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        OperationBuilder builder = new OperationBuilder(composite);
        builder.addInputStream(tccl.getResourceAsStream("helloWorld/index.html"));
        builder.addInputStream(tccl.getResourceAsStream("helloWorld/index2.html"));
        if (supportManagedExplodedDeployment()) {
            executeOnMaster(builder.build());
            performHttpCall(slaveAddress, 8080);
        } else {
            ModelNode failure = executeForFailureOnMaster(builder.build());
            Assert.assertTrue(failure.toJSONString(true), failure.toJSONString(true).contains("WFLYCTL0421:"));
        }
    }

    @Test
    public void testUndeploy() throws Exception {
        // Establish the deployment
        testDeploymentViaStream();
        undeployTest();
    }

    @Test
    public void testUnmanagedArchiveUndeploy() throws Exception {
        // Establish the deployment
        testUnmanagedArchiveDeployment();
        undeployTest();
    }

    @Test
    public void testUnmanagedExplodedUndeploy() throws Exception {
        // Establish the deployment
        testUnmanagedExplodedDeployment();
        undeployTest();
    }

    @Test
    public void testRedeploy() throws Exception {
        // Establish the deployment
        testDeploymentViaStream();
        redeployTest();
    }

    @Test
    public void testUnmanagedArchiveRedeploy() throws Exception {
        // Establish the deployment
        testUnmanagedArchiveDeployment();
        redeployTest();
    }

    @Test
    public void testUnmanagedExplodedRedeploy() throws Exception {
        // Establish the deployment
        testUnmanagedExplodedDeployment();
        redeployTest();
    }

    @Test
    public void testReplace() throws Exception {
        // Establish the deployment
        testDeploymentViaStream();
        ModelNode content = new ModelNode();
        content.get(INPUT_STREAM_INDEX).set(0);
        ModelNode op = createDeploymentReplaceOperation(content, MixedDomainDeploymentTest.OTHER_SERVER_GROUP_ADDRESS, MixedDomainDeploymentTest.MAIN_SERVER_GROUP_ADDRESS);
        OperationBuilder builder = new OperationBuilder(op, true);
        builder.addInputStream(webArchive.as(ZipExporter.class).exportAsInputStream());
        executeOnMaster(builder.build());
        performHttpCall(slaveAddress, 8080);
    }

    @Test
    public void testJsfWorks() throws Exception {
        ModelNode content = new ModelNode();
        content.get(INPUT_STREAM_INDEX).set(0);
        // Just be lazy here and deploy the jsf-test.war with the same name as the other deployments we tried
        ModelNode composite = createDeploymentOperation(content, MixedDomainDeploymentTest.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS, MixedDomainDeploymentTest.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        OperationBuilder builder = new OperationBuilder(composite, true);
        builder.addInputStream(jsfTestArchive.as(ZipExporter.class).exportAsInputStream());
        executeOnMaster(builder.build());
        performHttpCall(slaveAddress, 8080, "test/home.jsf", "Bean Works");
    }
}

