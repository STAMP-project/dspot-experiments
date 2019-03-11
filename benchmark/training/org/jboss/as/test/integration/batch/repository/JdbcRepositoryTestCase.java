/**
 * Copyright 2018 Red Hat, Inc.
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
package org.jboss.as.test.integration.batch.repository;


import BatchStatus.COMPLETED;
import Operation.Factory;
import Operations.CompositeOperationBuilder;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import javax.annotation.Resource;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.sql.DataSource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.Operation;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.batch.common.AbstractBatchTestCase;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.as.test.shared.SnapshotRestoreSetupTask;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
@ServerSetup(JdbcRepositoryTestCase.AgroalJdbcJobRepositorySetUp.class)
public class JdbcRepositoryTestCase extends AbstractBatchTestCase {
    private static final String DEPLOYMENT_NAME = "jdbc-batch.war";

    private static final String REPOSITORY_NAME = "jdbc";

    @Resource(lookup = "java:jboss/datasources/batch")
    private DataSource dataSource;

    @Test
    public void testAgroalBackedRepository() throws Exception {
        final JobOperator jobOperator = BatchRuntime.getJobOperator();
        final Properties jobProperties = new Properties();
        jobProperties.setProperty("reader.end", "10");
        // Start the first job
        long executionId = jobOperator.start("test-chunk", jobProperties);
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        // Wait until the job is complete for a maximum of 5 seconds
        AbstractBatchTestCase.waitForTermination(jobExecution, 5);
        // Check the job as completed and the expected execution id should be 1
        Assert.assertEquals(COMPLETED, jobExecution.getBatchStatus());
        Assert.assertEquals(1L, jobExecution.getExecutionId());
        // Query the actual DB and ensure we're using the correct repository
        Assert.assertNotNull(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            final Statement stmt = connection.createStatement();
            final ResultSet rs = stmt.executeQuery("SELECT JOBEXECUTIONID, BATCHSTATUS FROM JOB_EXECUTION");
            Assert.assertTrue("Expected a single entry for the query", rs.next());
            Assert.assertEquals(1L, rs.getLong("JOBEXECUTIONID"));
            Assert.assertEquals(COMPLETED.toString(), rs.getString("BATCHSTATUS"));
            Assert.assertFalse("Expected a single entry for the query", rs.next());
        }
    }

    static class AgroalJdbcJobRepositorySetUp extends SnapshotRestoreSetupTask {
        @Override
        public void doSetup(final ManagementClient managementClient, final String containerId) throws Exception {
            final Operations.CompositeOperationBuilder operationBuilder = CompositeOperationBuilder.create();
            // First we need to add the Agroal extension
            final ModelNode extensionAddress = Operations.createAddress("extension", "org.wildfly.extension.datasources-agroal");
            ModelNode op = Operations.createAddOperation(extensionAddress);
            op.get("module").set("org.wildfly.extension.datasources-agroal");
            JdbcRepositoryTestCase.AgroalJdbcJobRepositorySetUp.execute(managementClient.getControllerClient(), Factory.create(op));
            // Next add the subsystem
            operationBuilder.addStep(Operations.createAddOperation(Operations.createAddress("subsystem", "datasources-agroal")));
            // Add the JDBC driver
            op = Operations.createAddOperation(Operations.createAddress("subsystem", "datasources-agroal", "driver", "agroal-h2"));
            op.get("module").set("com.h2database.h2");
            operationBuilder.addStep(op);
            // Add the datasource
            op = Operations.createAddOperation(Operations.createAddress("subsystem", "datasources-agroal", "datasource", "batch-db"));
            op.get("jndi-name").set("java:jboss/datasources/batch");
            final ModelNode connectionFactory = op.get("connection-factory");
            connectionFactory.get("driver").set("agroal-h2");
            connectionFactory.get("url").set("jdbc:h2:mem:batch-agroal-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            final ModelNode connectionPool = op.get("connection-pool");
            connectionPool.get("max-size").set(10);
            operationBuilder.addStep(op);
            // Add a new JDBC job repository with the new data-source
            op = Operations.createAddOperation(Operations.createAddress("subsystem", "batch-jberet", "jdbc-job-repository", JdbcRepositoryTestCase.REPOSITORY_NAME));
            op.get("data-source").set("batch-db");
            operationBuilder.addStep(op);
            operationBuilder.addStep(Operations.createWriteAttributeOperation(Operations.createAddress("subsystem", "batch-jberet"), "default-job-repository", JdbcRepositoryTestCase.REPOSITORY_NAME));
            JdbcRepositoryTestCase.AgroalJdbcJobRepositorySetUp.execute(managementClient.getControllerClient(), operationBuilder.build());
            ServerReload.reloadIfRequired(managementClient);
        }

        private static void execute(final ModelControllerClient client, final Operation op) throws IOException {
            final ModelNode result = client.execute(op);
            if (!(Operations.isSuccessfulOutcome(result))) {
                Assert.fail(Operations.getFailureDescription(result).toString());
            }
            Operations.readResult(result);
        }
    }
}

