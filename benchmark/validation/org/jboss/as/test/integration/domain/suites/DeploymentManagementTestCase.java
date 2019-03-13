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


import DomainTestSupport.masterAddress;
import DomainTestSupport.slaveAddress;
import java.io.File;
import java.io.IOException;
import org.jboss.as.controller.client.Operation;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of various management operations involving deployment.
 *
 * @author Brian Stansberry (c) 2011 Red Hat Inc.
 */
public class DeploymentManagementTestCase {
    private static final String TEST = "test.war";

    private static final String TEST2 = "test2.war";

    private static final String REPLACEMENT = "test.war.v2";

    private static final ModelNode ROOT_ADDRESS = new ModelNode();

    private static final ModelNode ROOT_DEPLOYMENT_ADDRESS = new ModelNode();

    private static final ModelNode ROOT_REPLACEMENT_ADDRESS = new ModelNode();

    private static final ModelNode MAIN_SERVER_GROUP_ADDRESS = new ModelNode();

    private static final ModelNode MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS = new ModelNode();

    private static final ModelNode OTHER_SERVER_GROUP_ADDRESS = new ModelNode();

    private static final ModelNode OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS = new ModelNode();

    private static final ModelNode MAIN_RUNNING_SERVER_ADDRESS = new ModelNode();

    private static final ModelNode MAIN_RUNNING_SERVER_DEPLOYMENT_ADDRESS = new ModelNode();

    private static final ModelNode OTHER_RUNNING_SERVER_ADDRESS = new ModelNode();

    private static final ModelNode OTHER_RUNNING_SERVER_GROUP_ADDRESS = new ModelNode();

    static {
        DeploymentManagementTestCase.ROOT_ADDRESS.setEmptyList();
        DeploymentManagementTestCase.ROOT_ADDRESS.protect();
        DeploymentManagementTestCase.ROOT_DEPLOYMENT_ADDRESS.add(DEPLOYMENT, DeploymentManagementTestCase.TEST);
        DeploymentManagementTestCase.ROOT_DEPLOYMENT_ADDRESS.protect();
        DeploymentManagementTestCase.ROOT_REPLACEMENT_ADDRESS.add(DEPLOYMENT, DeploymentManagementTestCase.REPLACEMENT);
        DeploymentManagementTestCase.ROOT_REPLACEMENT_ADDRESS.protect();
        DeploymentManagementTestCase.MAIN_SERVER_GROUP_ADDRESS.add(SERVER_GROUP, "main-server-group");
        DeploymentManagementTestCase.MAIN_SERVER_GROUP_ADDRESS.protect();
        DeploymentManagementTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS.add(SERVER_GROUP, "main-server-group");
        DeploymentManagementTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS.add(DEPLOYMENT, DeploymentManagementTestCase.TEST);
        DeploymentManagementTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS.protect();
        DeploymentManagementTestCase.OTHER_SERVER_GROUP_ADDRESS.add(SERVER_GROUP, "other-server-group");
        DeploymentManagementTestCase.OTHER_SERVER_GROUP_ADDRESS.protect();
        DeploymentManagementTestCase.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS.add(SERVER_GROUP, "other-server-group");
        DeploymentManagementTestCase.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS.add(DEPLOYMENT, DeploymentManagementTestCase.TEST);
        DeploymentManagementTestCase.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS.protect();
        DeploymentManagementTestCase.MAIN_RUNNING_SERVER_ADDRESS.add(HOST, "master");
        DeploymentManagementTestCase.MAIN_RUNNING_SERVER_ADDRESS.add(SERVER, "main-one");
        DeploymentManagementTestCase.MAIN_RUNNING_SERVER_ADDRESS.protect();
        DeploymentManagementTestCase.MAIN_RUNNING_SERVER_DEPLOYMENT_ADDRESS.add(HOST, "master");
        DeploymentManagementTestCase.MAIN_RUNNING_SERVER_DEPLOYMENT_ADDRESS.add(SERVER, "main-one");
        DeploymentManagementTestCase.MAIN_RUNNING_SERVER_DEPLOYMENT_ADDRESS.add(DEPLOYMENT, DeploymentManagementTestCase.TEST);
        DeploymentManagementTestCase.MAIN_RUNNING_SERVER_DEPLOYMENT_ADDRESS.protect();
        DeploymentManagementTestCase.OTHER_RUNNING_SERVER_ADDRESS.add(HOST, "slave");
        DeploymentManagementTestCase.OTHER_RUNNING_SERVER_ADDRESS.add(SERVER, "other-two");
        DeploymentManagementTestCase.OTHER_RUNNING_SERVER_ADDRESS.protect();
        DeploymentManagementTestCase.OTHER_RUNNING_SERVER_GROUP_ADDRESS.add(HOST, "slave");
        DeploymentManagementTestCase.OTHER_RUNNING_SERVER_GROUP_ADDRESS.add(SERVER, "other-two");
        DeploymentManagementTestCase.OTHER_RUNNING_SERVER_GROUP_ADDRESS.add(DEPLOYMENT, DeploymentManagementTestCase.TEST);
        DeploymentManagementTestCase.OTHER_RUNNING_SERVER_GROUP_ADDRESS.protect();
    }

    private static DomainTestSupport testSupport;

    private static WebArchive webArchive;

    private static WebArchive webArchive2;

    private static File tmpDir;

    @Test
    public void testDeploymentViaUrl() throws Exception {
        String url = new File(DeploymentManagementTestCase.tmpDir, ("archives/" + (DeploymentManagementTestCase.TEST))).toURI().toURL().toString();
        ModelNode content = new ModelNode();
        content.get("url").set(url);
        ModelNode composite = DeploymentManagementTestCase.createDeploymentOperation(content, DeploymentManagementTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS, DeploymentManagementTestCase.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        DeploymentManagementTestCase.executeOnMaster(composite);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testDeploymentViaStream() throws Exception {
        ModelNode content = new ModelNode();
        content.get(INPUT_STREAM_INDEX).set(0);
        ModelNode composite = DeploymentManagementTestCase.createDeploymentOperation(content, DeploymentManagementTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS, DeploymentManagementTestCase.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        OperationBuilder builder = new OperationBuilder(composite, true);
        builder.addInputStream(DeploymentManagementTestCase.webArchive.as(ZipExporter.class).exportAsInputStream());
        DeploymentManagementTestCase.executeOnMaster(builder.build());
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testUploadURL() throws Exception {
        String url = new File(DeploymentManagementTestCase.tmpDir, ("archives/" + (DeploymentManagementTestCase.TEST))).toURI().toURL().toString();
        ModelNode op = DeploymentManagementTestCase.getEmptyOperation(UPLOAD_DEPLOYMENT_URL, DeploymentManagementTestCase.ROOT_ADDRESS);
        op.get("url").set(url);
        byte[] hash = DeploymentManagementTestCase.executeOnMaster(op).asBytes();
        testDeploymentViaHash(hash);
    }

    @Test
    public void testUploadStream() throws Exception {
        ModelNode op = DeploymentManagementTestCase.getEmptyOperation(UPLOAD_DEPLOYMENT_STREAM, DeploymentManagementTestCase.ROOT_ADDRESS);
        op.get(INPUT_STREAM_INDEX).set(0);
        OperationBuilder builder = new OperationBuilder(op, true);
        builder.addInputStream(DeploymentManagementTestCase.webArchive.as(ZipExporter.class).exportAsInputStream());
        byte[] hash = DeploymentManagementTestCase.executeOnMaster(builder.build()).asBytes();
        testDeploymentViaHash(hash);
    }

    @Test
    public void testDomainAddOnly() throws Exception {
        ModelNode op = DeploymentManagementTestCase.getEmptyOperation(UPLOAD_DEPLOYMENT_STREAM, DeploymentManagementTestCase.ROOT_ADDRESS);
        op.get(INPUT_STREAM_INDEX).set(0);
        OperationBuilder builder = new OperationBuilder(op, true);
        builder.addInputStream(DeploymentManagementTestCase.webArchive.as(ZipExporter.class).exportAsInputStream());
        byte[] hash = DeploymentManagementTestCase.executeOnMaster(builder.build()).asBytes();
        ModelNode content = new ModelNode();
        content.get("hash").set(hash);
        ModelNode composite = DeploymentManagementTestCase.createDeploymentOperation(content);
        DeploymentManagementTestCase.executeOnMaster(composite);
    }

    @Test
    public void testUnmanagedArchiveDeployment() throws Exception {
        ModelNode content = new ModelNode();
        content.get("archive").set(true);
        content.get("path").set(new File(DeploymentManagementTestCase.tmpDir, ("archives/" + (DeploymentManagementTestCase.TEST))).getAbsolutePath());
        ModelNode composite = DeploymentManagementTestCase.createDeploymentOperation(content, DeploymentManagementTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS, DeploymentManagementTestCase.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        DeploymentManagementTestCase.executeOnMaster(composite);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testUnmanagedExplodedDeployment() throws Exception {
        ModelNode content = new ModelNode();
        content.get("archive").set(false);
        content.get("path").set(new File(DeploymentManagementTestCase.tmpDir, ("exploded/" + (DeploymentManagementTestCase.TEST))).getAbsolutePath());
        ModelNode composite = DeploymentManagementTestCase.createDeploymentOperation(content, DeploymentManagementTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS, DeploymentManagementTestCase.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        DeploymentManagementTestCase.executeOnMaster(composite);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
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
        ModelNode op = DeploymentManagementTestCase.createDeploymentReplaceOperation(content, DeploymentManagementTestCase.MAIN_SERVER_GROUP_ADDRESS, DeploymentManagementTestCase.OTHER_SERVER_GROUP_ADDRESS);
        OperationBuilder builder = new OperationBuilder(op, true);
        builder.addInputStream(DeploymentManagementTestCase.webArchive.as(ZipExporter.class).exportAsInputStream());
        DeploymentManagementTestCase.executeOnMaster(builder.build());
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testUnmanagedArchiveReplace() throws Exception {
        // Establish the deployment
        testUnmanagedArchiveDeployment();
        ModelNode content = new ModelNode();
        content.get("archive").set(true);
        content.get("path").set(new File(DeploymentManagementTestCase.tmpDir, ("archives/" + (DeploymentManagementTestCase.TEST))).getAbsolutePath());
        ModelNode op = DeploymentManagementTestCase.createDeploymentReplaceOperation(content, DeploymentManagementTestCase.MAIN_SERVER_GROUP_ADDRESS, DeploymentManagementTestCase.OTHER_SERVER_GROUP_ADDRESS);
        DeploymentManagementTestCase.executeOnMaster(op);
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testUnmanagedExplodedReplace() throws Exception {
        // Establish the deployment
        testUnmanagedArchiveDeployment();
        ModelNode content = new ModelNode();
        content.get("archive").set(false);
        content.get("path").set(new File(DeploymentManagementTestCase.tmpDir, ("exploded/" + (DeploymentManagementTestCase.TEST))).getAbsolutePath());
        ModelNode op = DeploymentManagementTestCase.createDeploymentReplaceOperation(content, DeploymentManagementTestCase.MAIN_SERVER_GROUP_ADDRESS, DeploymentManagementTestCase.OTHER_SERVER_GROUP_ADDRESS);
        DeploymentManagementTestCase.executeOnMaster(op);
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testUnmanagedArchiveReplaceManaged() throws Exception {
        // Establish the deployment
        testDeploymentViaStream();
        ModelNode content = new ModelNode();
        content.get("archive").set(true);
        content.get("path").set(new File(DeploymentManagementTestCase.tmpDir, ("archives/" + (DeploymentManagementTestCase.TEST))).getAbsolutePath());
        ModelNode op = DeploymentManagementTestCase.createDeploymentReplaceOperation(content, DeploymentManagementTestCase.MAIN_SERVER_GROUP_ADDRESS, DeploymentManagementTestCase.OTHER_SERVER_GROUP_ADDRESS);
        DeploymentManagementTestCase.executeOnMaster(op);
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testUnmanagedExplodedReplaceManaged() throws Exception {
        // Establish the deployment
        testDeploymentViaStream();
        ModelNode content = new ModelNode();
        content.get("archive").set(false);
        content.get("path").set(new File(DeploymentManagementTestCase.tmpDir, ("exploded/" + (DeploymentManagementTestCase.TEST))).getAbsolutePath());
        ModelNode op = DeploymentManagementTestCase.createDeploymentReplaceOperation(content, DeploymentManagementTestCase.MAIN_SERVER_GROUP_ADDRESS, DeploymentManagementTestCase.OTHER_SERVER_GROUP_ADDRESS);
        DeploymentManagementTestCase.executeOnMaster(op);
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testManagedReplaceUnmanaged() throws Exception {
        // Establish the deployment
        testUnmanagedArchiveDeployment();
        ModelNode content = new ModelNode();
        content.get(INPUT_STREAM_INDEX).set(0);
        ModelNode op = DeploymentManagementTestCase.createDeploymentReplaceOperation(content, DeploymentManagementTestCase.MAIN_SERVER_GROUP_ADDRESS, DeploymentManagementTestCase.OTHER_SERVER_GROUP_ADDRESS);
        OperationBuilder builder = new OperationBuilder(op, true);
        builder.addInputStream(DeploymentManagementTestCase.webArchive.as(ZipExporter.class).exportAsInputStream());
        DeploymentManagementTestCase.executeOnMaster(builder.build());
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testFullReplaceViaStream() throws Exception {
        // Establish the deployment
        testDeploymentViaStream();
        ModelNode content = new ModelNode();
        content.get(INPUT_STREAM_INDEX).set(0);
        ModelNode op = DeploymentManagementTestCase.createDeploymentFullReplaceOperation(content);
        OperationBuilder builder = new OperationBuilder(op, true);
        builder.addInputStream(DeploymentManagementTestCase.webArchive.as(ZipExporter.class).exportAsInputStream());
        DeploymentManagementTestCase.executeOnMaster(builder.build());
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testFullReplaceViaUrl() throws Exception {
        // Establish the deployment
        testDeploymentViaStream();
        String url = new File(DeploymentManagementTestCase.tmpDir, ("archives/" + (DeploymentManagementTestCase.TEST))).toURI().toURL().toString();
        ModelNode content = new ModelNode();
        content.get("url").set(url);
        ModelNode op = DeploymentManagementTestCase.createDeploymentFullReplaceOperation(content);
        DeploymentManagementTestCase.executeOnMaster(op);
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testFullReplaceViaHash() throws Exception {
        // Establish the deployment
        testDeploymentViaStream();
        byte[] original = DeploymentManagementTestCase.getHash(DeploymentManagementTestCase.ROOT_DEPLOYMENT_ADDRESS);
        String url = new File(DeploymentManagementTestCase.tmpDir, ("archives/" + (DeploymentManagementTestCase.TEST))).toURI().toURL().toString();
        ModelNode op = DeploymentManagementTestCase.getEmptyOperation(UPLOAD_DEPLOYMENT_URL, DeploymentManagementTestCase.ROOT_ADDRESS);
        op.get("url").set(url);
        byte[] hash = DeploymentManagementTestCase.executeOnMaster(op).asBytes();
        ModelNode content = new ModelNode();
        content.get("hash").set(hash);
        op = DeploymentManagementTestCase.createDeploymentFullReplaceOperation(content);
        DeploymentManagementTestCase.executeOnMaster(op);
        // Check that the original content got removed!
        DeploymentManagementTestCase.testRemovedContent(DeploymentManagementTestCase.testSupport.getDomainMasterLifecycleUtil(), original);
        DeploymentManagementTestCase.testRemovedContent(DeploymentManagementTestCase.testSupport.getDomainSlaveLifecycleUtil(), original);
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testFullReplaceDifferentFile() throws Exception {
        // Establish the deployment
        testDeploymentViaStream();
        ModelNode content = new ModelNode();
        content.get(INPUT_STREAM_INDEX).set(0);
        ModelNode op = DeploymentManagementTestCase.createDeploymentFullReplaceOperation(content);
        OperationBuilder builder = new OperationBuilder(op, true);
        builder.addInputStream(DeploymentManagementTestCase.webArchive2.as(ZipExporter.class).exportAsInputStream());
        DeploymentManagementTestCase.executeOnMaster(builder.build());
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testUnmanagedArchiveFullReplace() throws Exception {
        // Establish the deployment
        testUnmanagedArchiveDeployment();
        ModelNode content = new ModelNode();
        content.get("archive").set(true);
        content.get("path").set(new File(DeploymentManagementTestCase.tmpDir, ("archives/" + (DeploymentManagementTestCase.TEST))).getAbsolutePath());
        ModelNode op = DeploymentManagementTestCase.createDeploymentFullReplaceOperation(content);
        DeploymentManagementTestCase.executeOnMaster(op);
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testUnmanagedExplodedFullReplace() throws Exception {
        // Establish the deployment
        testUnmanagedExplodedDeployment();
        ModelNode content = new ModelNode();
        content.get("archive").set(false);
        content.get("path").set(new File(DeploymentManagementTestCase.tmpDir, ("exploded/" + (DeploymentManagementTestCase.TEST))).getAbsolutePath());
        ModelNode op = DeploymentManagementTestCase.createDeploymentFullReplaceOperation(content);
        DeploymentManagementTestCase.executeOnMaster(op);
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testUnmanagedArchiveFullReplaceManaged() throws Exception {
        // Establish the deployment
        testDeploymentViaStream();
        ModelNode content = new ModelNode();
        content.get("archive").set(true);
        content.get("path").set(new File(DeploymentManagementTestCase.tmpDir, ("archives/" + (DeploymentManagementTestCase.TEST))).getAbsolutePath());
        ModelNode op = DeploymentManagementTestCase.createDeploymentFullReplaceOperation(content);
        DeploymentManagementTestCase.executeOnMaster(op);
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testUnmanagedExplodedFullReplaceManaged() throws Exception {
        // Establish the deployment
        testDeploymentViaStream();
        ModelNode content = new ModelNode();
        content.get("archive").set(false);
        content.get("path").set(new File(DeploymentManagementTestCase.tmpDir, ("exploded/" + (DeploymentManagementTestCase.TEST))).getAbsolutePath());
        ModelNode op = DeploymentManagementTestCase.createDeploymentFullReplaceOperation(content);
        DeploymentManagementTestCase.executeOnMaster(op);
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testManagedFullReplaceUnmanaged() throws Exception {
        // Establish the deployment
        testUnmanagedExplodedDeployment();
        ModelNode content = new ModelNode();
        content.get(INPUT_STREAM_INDEX).set(0);
        ModelNode op = DeploymentManagementTestCase.createDeploymentFullReplaceOperation(content);
        OperationBuilder builder = new OperationBuilder(op, true);
        builder.addInputStream(DeploymentManagementTestCase.webArchive.as(ZipExporter.class).exportAsInputStream());
        DeploymentManagementTestCase.executeOnMaster(builder.build());
        // Thread.sleep(1000);
        DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
    }

    @Test
    public void testServerGroupRuntimeName() throws Exception {
        ModelNode content = new ModelNode();
        content.get(INPUT_STREAM_INDEX).set(0);
        ModelNode composite = DeploymentManagementTestCase.createDeploymentOperation(content, DeploymentManagementTestCase.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        // Chnage the runtime name in the sg op
        composite.get("steps").get(1).get(RUNTIME_NAME).set("test1.war");
        OperationBuilder builder = new OperationBuilder(composite, true);
        builder.addInputStream(DeploymentManagementTestCase.webArchive.as(ZipExporter.class).exportAsInputStream());
        DeploymentManagementTestCase.executeOnMaster(builder.build());
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630, "test1");
    }

    @Test
    public void testDeployToSingleServerGroup() throws Exception {
        ModelNode content = new ModelNode();
        content.get(INPUT_STREAM_INDEX).set(0);
        ModelNode composite = DeploymentManagementTestCase.createDeploymentOperation(content, DeploymentManagementTestCase.OTHER_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        OperationBuilder builder = new OperationBuilder(composite, true);
        builder.addInputStream(DeploymentManagementTestCase.webArchive.as(ZipExporter.class).exportAsInputStream());
        DeploymentManagementTestCase.executeOnMaster(builder.build());
        DeploymentManagementTestCase.performHttpCall(slaveAddress, 8630);
        try {
            DeploymentManagementTestCase.performHttpCall(masterAddress, 8080);
            Assert.fail("Webapp deployed to unselected server group");
        } catch (IOException ioe) {
            // good
        }
    }

    @Test
    public void testDeploymentsWithSameHash() throws Exception {
        final ModelNode rootDeploymentAddress2 = new ModelNode();
        rootDeploymentAddress2.add(DEPLOYMENT, "test2");
        rootDeploymentAddress2.protect();
        final ModelNode otherServerGroupDeploymentAddress2 = new ModelNode();
        otherServerGroupDeploymentAddress2.add(SERVER_GROUP, "other-server-group");
        otherServerGroupDeploymentAddress2.add(DEPLOYMENT, "test2");
        otherServerGroupDeploymentAddress2.protect();
        class LocalMethods {
            Operation createDeploymentOperation(ModelNode rootDeploymentAddress, ModelNode serverGroupDeploymentAddress) {
                ModelNode composite = DeploymentManagementTestCase.getEmptyOperation(COMPOSITE, DeploymentManagementTestCase.ROOT_ADDRESS);
                ModelNode steps = composite.get(STEPS);
                ModelNode step = steps.add();
                step.set(DeploymentManagementTestCase.getEmptyOperation(ADD, rootDeploymentAddress));
                ModelNode content = new ModelNode();
                content.get(INPUT_STREAM_INDEX).set(0);
                step.get(CONTENT).add(content);
                step = steps.add();
                step.set(DeploymentManagementTestCase.getEmptyOperation(ADD, serverGroupDeploymentAddress));
                step.get(ENABLED).set(true);
                OperationBuilder builder = new OperationBuilder(composite, true);
                builder.addInputStream(DeploymentManagementTestCase.webArchive.as(ZipExporter.class).exportAsInputStream());
                return builder.build();
            }

            ModelNode createRemoveOperation(ModelNode rootDeploymentAddress, ModelNode serverGroupDeploymentAddress) {
                ModelNode composite = DeploymentManagementTestCase.getEmptyOperation(COMPOSITE, DeploymentManagementTestCase.ROOT_ADDRESS);
                ModelNode steps = composite.get(STEPS);
                ModelNode step = steps.add();
                step.set(DeploymentManagementTestCase.getEmptyOperation(REMOVE, serverGroupDeploymentAddress));
                step = steps.add();
                step.set(DeploymentManagementTestCase.getEmptyOperation(REMOVE, rootDeploymentAddress));
                return composite;
            }
        }
        LocalMethods localMethods = new LocalMethods();
        try {
            DeploymentManagementTestCase.executeOnMaster(localMethods.createDeploymentOperation(DeploymentManagementTestCase.ROOT_DEPLOYMENT_ADDRESS, DeploymentManagementTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS));
            try {
                DeploymentManagementTestCase.executeOnMaster(localMethods.createDeploymentOperation(rootDeploymentAddress2, otherServerGroupDeploymentAddress2));
            } finally {
                DeploymentManagementTestCase.executeOnMaster(localMethods.createRemoveOperation(rootDeploymentAddress2, otherServerGroupDeploymentAddress2));
            }
            ModelNode undeploySg = DeploymentManagementTestCase.getEmptyOperation(REMOVE, DeploymentManagementTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS);
            DeploymentManagementTestCase.executeOnMaster(undeploySg);
            ModelNode deploySg = DeploymentManagementTestCase.getEmptyOperation(ADD, DeploymentManagementTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS);
            deploySg.get(ENABLED).set(true);
            DeploymentManagementTestCase.executeOnMaster(deploySg);
        } finally {
            DeploymentManagementTestCase.executeOnMaster(localMethods.createRemoveOperation(DeploymentManagementTestCase.ROOT_DEPLOYMENT_ADDRESS, DeploymentManagementTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS));
        }
    }
}

