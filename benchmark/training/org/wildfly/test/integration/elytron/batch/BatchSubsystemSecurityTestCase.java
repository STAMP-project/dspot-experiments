/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
package org.wildfly.test.integration.elytron.batch;


import BatchStatus.ABANDONED;
import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import BatchStatus.STOPPED;
import SimplePermissionMapper.PermissionMapping;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.batch.operations.JobOperator;
import javax.batch.operations.JobSecurityException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.as.test.shared.SnapshotRestoreSetupTask;
import org.jboss.as.test.shared.TimeoutUtil;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extension.batch.jberet.deployment.BatchPermission;
import org.wildfly.security.auth.permission.LoginPermission;
import org.wildfly.security.auth.server.SecurityIdentity;
import org.wildfly.test.security.common.AbstractElytronSetupTask;
import org.wildfly.test.security.common.elytron.ConfigurableElement;
import org.wildfly.test.security.common.elytron.EJBApplicationSecurityDomainMapping;
import org.wildfly.test.security.common.elytron.PermissionRef;
import org.wildfly.test.security.common.elytron.PropertyFileBasedDomain;
import org.wildfly.test.security.common.elytron.SimplePermissionMapper;


/**
 * This is for testing the BatchPermission from batch-jberet subsystem.
 * It also checks that when running a Batch job as a particular user, the security identity can be retrieved
 * within the job's code.
 *
 * The security setup is like this:
 *      user1/password1 -> can do everything
 *      user2/password2 -> can stop jobs only
 *      user3/password3 -> can read jobs only
 *
 * @author Jan Martiska
 */
@ServerSetup({ BatchSubsystemSecurityTestCase.CreateBatchSecurityDomainSetupTask.class, BatchSubsystemSecurityTestCase.ActivateBatchSecurityDomainSetupTask.class })
@RunWith(Arquillian.class)
public class BatchSubsystemSecurityTestCase {
    static final String BATCH_SECURITY_DOMAIN_NAME = "BatchDomain";

    // this is the identityWithinJob using which the batch job was invoked
    // the job itself completes this future when it runs
    static volatile CompletableFuture<String> identityWithinJob;

    private JobOperator operator;

    /**
     * Try running a job as a user who has the permission to run jobs. It should succeed.
     * The job should also be able to retrieve the name of the user who ran it.
     */
    @Test
    public void testStart_Allowed() throws Exception {
        BatchSubsystemSecurityTestCase.identityWithinJob = new CompletableFuture<>();
        final SecurityIdentity user1 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user1", "password1");
        user1.runAs(((Callable<Long>) (() -> operator.start("assert-identity", new Properties()))));
        final String actualUsername = BatchSubsystemSecurityTestCase.identityWithinJob.get(TimeoutUtil.adjust(20), TimeUnit.SECONDS);
        Assert.assertEquals("user1", actualUsername);
    }

    /**
     * Try running a job as a user who doesn't have the permission to run jobs. It should not succeed.
     */
    @Test
    public void testStart_NotAllowed() throws Exception {
        final SecurityIdentity user2 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user2", "password2");
        try {
            user2.runAs(((Callable<Long>) (() -> operator.start("assert-identity", new Properties()))));
            Assert.fail("user2 shouldn't be allowed to start batch jobs");
        } catch (JobSecurityException e) {
            // OK
        }
    }

    /**
     * Test reading execution metadata by a user who has the permission to do it.
     * User1 runs a job and then user2 tries to read its metadata.
     */
    @Test
    public void testRead_Allowed() throws Exception {
        final Properties jobParams = new Properties();
        jobParams.put("prop1", "val1");
        final SecurityIdentity user1 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user1", "password1");
        final SecurityIdentity user3 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user3", "password3");
        final Long executionId = user1.runAs(((Callable<Long>) (() -> operator.start("assert-identity", jobParams))));
        final Properties retrievedParams = user3.runAs(((Callable<Properties>) (() -> operator.getJobExecution(executionId).getJobParameters())));
        Assert.assertEquals(jobParams, retrievedParams);
    }

    /**
     * Test reading execution metadata by a user who doesn't have the permission to do it.
     * User1 runs a job and then user2 tries to read its metadata.
     */
    @Test
    public void testRead_NotAllowed() throws Exception {
        final SecurityIdentity user1 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user1", "password1");
        final SecurityIdentity user2 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user2", "password2");
        final Long executionId = user1.runAs(((Callable<Long>) (() -> operator.start("assert-identity", new Properties()))));
        try {
            user2.runAs(((Callable<Properties>) (() -> operator.getJobExecution(executionId).getJobParameters())));
            Assert.fail("user2 shouldn't be allowed to read batch job metadata");
        } catch (JobSecurityException e) {
            // OK
        }
    }

    /**
     * Test restarting failed jobs by a user who has the permission to do it.
     */
    @Test
    public void testRestart_Allowed() throws Exception {
        final SecurityIdentity user1 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user1", "password1");
        Properties params = new Properties();
        params.put("should.fail", "true");
        final Long executionId = user1.runAs(((Callable<Long>) (() -> operator.start("failing-batchlet", params))));
        waitForJobEnd(executionId, 10);
        Assert.assertEquals(FAILED, operator.getJobExecution(executionId).getBatchStatus());
        params.put("should.fail", "false");
        final Long executionIdAfterRestart = user1.runAs(((Callable<Long>) (() -> operator.restart(executionId, params))));
        waitForJobEnd(executionIdAfterRestart, 10);
        Assert.assertEquals(COMPLETED, operator.getJobExecution(executionIdAfterRestart).getBatchStatus());
    }

    /**
     * Test restarting failed jobs by a user who doesn't have the permission to do it.
     */
    @Test
    public void testRestart_NotAllowed() throws Exception {
        final SecurityIdentity user1 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user1", "password1");
        final SecurityIdentity user2 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user2", "password2");
        Properties params = new Properties();
        params.put("should.fail", "true");
        final Long executionId = user1.runAs(((Callable<Long>) (() -> operator.start("failing-batchlet", params))));
        waitForJobEnd(executionId, 10);
        Assert.assertEquals(FAILED, operator.getJobExecution(executionId).getBatchStatus());
        try {
            user2.runAs(((Callable<Long>) (() -> operator.restart(executionId, params))));
            Assert.fail("user2 shouldn't be allowed to restart batch jobs");
        } catch (JobSecurityException e) {
            // OK
        }
    }

    /**
     * Abandoning an execution by a user who has the permission to do it.
     */
    @Test
    public void testAbandon_Allowed() throws Exception {
        final SecurityIdentity user1 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user1", "password1");
        final Long id = user1.runAs(((Callable<Long>) (() -> operator.start("assert-identity", new Properties()))));
        waitForJobEnd(id, 10);
        user1.runAs(() -> operator.abandon(id));
        Assert.assertEquals(operator.getJobExecution(id).getBatchStatus(), ABANDONED);
    }

    /**
     * Abandoning an execution by a user who doesn't have the permission to do it.
     */
    @Test
    public void testAbandon_NotAllowed() throws Exception {
        final SecurityIdentity user1 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user1", "password1");
        final SecurityIdentity user2 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user2", "password2");
        final Long id = user1.runAs(((Callable<Long>) (() -> operator.start("assert-identity", new Properties()))));
        waitForJobEnd(id, 10);
        try {
            user2.runAs(() -> operator.abandon(id));
            Assert.fail("user2 should not be allowed to abandon job executions");
        } catch (JobSecurityException e) {
            // OK
        }
        Assert.assertEquals(operator.getJobExecution(id).getBatchStatus(), COMPLETED);
    }

    /**
     * Stopping an execution by a user who doesn't have the permission to do it.
     */
    @Test
    public void testStop_NotAllowed() throws Exception {
        final SecurityIdentity user1 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user1", "password1");
        final SecurityIdentity user3 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user3", "password3");
        final Long id = user1.runAs(((Callable<Long>) (() -> operator.start("long-running-batchlet", null))));
        TimeUnit.SECONDS.sleep(1);
        try {
            user3.runAs(() -> operator.stop(id));
            Assert.fail("user2 should not be allowed to stop job executions");
        } catch (JobSecurityException e) {
            // OK
        }
        Assert.assertNotEquals(STOPPED, operator.getJobExecution(id).getBatchStatus());
    }

    /**
     * Stopping an execution by a user who has the permission to do it.
     */
    @Test
    public void testStop_Allowed() throws Exception {
        final SecurityIdentity user1 = BatchSubsystemSecurityTestCase.getSecurityIdentity("user1", "password1");
        final Long id = user1.runAs(((Callable<Long>) (() -> operator.start("long-running-batchlet", null))));
        TimeUnit.SECONDS.sleep(1);
        user1.runAs(() -> operator.stop(id));
        waitForJobEnd(id, 10);
        Assert.assertEquals(STOPPED, operator.getJobExecution(id).getBatchStatus());
    }

    static class CreateBatchSecurityDomainSetupTask extends AbstractElytronSetupTask {
        @Override
        protected ConfigurableElement[] getConfigurableElements() {
            return new ConfigurableElement[]{ SimplePermissionMapper.builder().withName("batch-permission-mapper").permissionMappings(PermissionMapping.builder().withPrincipals("user1", "anonymous").withPermissions(PermissionRef.builder().targetName("*").className(BatchPermission.class.getName()).module("org.wildfly.extension.batch.jberet").build(), PermissionRef.builder().className(LoginPermission.class.getName()).build()).build(), PermissionMapping.builder().withPrincipals("user2").withPermissions(PermissionRef.builder().targetName("stop").className(BatchPermission.class.getName()).module("org.wildfly.extension.batch.jberet").build(), PermissionRef.builder().className(LoginPermission.class.getName()).build()).build(), PermissionMapping.builder().withPrincipals("user3").withPermissions(PermissionRef.builder().targetName("read").className(BatchPermission.class.getName()).module("org.wildfly.extension.batch.jberet").build(), PermissionRef.builder().className(LoginPermission.class.getName()).build()).build()).build(), PropertyFileBasedDomain.builder().withName(BatchSubsystemSecurityTestCase.BATCH_SECURITY_DOMAIN_NAME).permissionMapper("batch-permission-mapper").withUser("user1", "password1").withUser("user2", "password2").withUser("user3", "password3").build(), new EJBApplicationSecurityDomainMapping(BatchSubsystemSecurityTestCase.BATCH_SECURITY_DOMAIN_NAME, BatchSubsystemSecurityTestCase.BATCH_SECURITY_DOMAIN_NAME) };
        }
    }

    static class ActivateBatchSecurityDomainSetupTask extends SnapshotRestoreSetupTask {
        final ModelNode BATCH_SUBSYSTEM_ADDRESS = PathAddress.pathAddress("subsystem", "batch-jberet").toModelNode();

        @Override
        public void doSetup(ManagementClient managementClient, String s) throws Exception {
            final ModelNode setOp = new ModelNode();
            setOp.get(ClientConstants.OP).set(ClientConstants.WRITE_ATTRIBUTE_OPERATION);
            setOp.get(ClientConstants.ADDRESS).set(BATCH_SUBSYSTEM_ADDRESS);
            setOp.get("name").set("security-domain");
            setOp.get("value").set(BatchSubsystemSecurityTestCase.BATCH_SECURITY_DOMAIN_NAME);
            final ModelNode result = managementClient.getControllerClient().execute(setOp);
            Assert.assertTrue(result.get("outcome").asString().equals("success"));
            ServerReload.executeReloadAndWaitForCompletion(managementClient.getControllerClient());
        }
    }
}

