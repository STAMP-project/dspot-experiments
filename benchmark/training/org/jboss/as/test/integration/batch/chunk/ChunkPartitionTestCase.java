/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.batch.chunk;


import BatchStatus.COMPLETED;
import java.util.Properties;
import javax.batch.operations.JobOperator;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.batch.common.AbstractBatchTestCase;
import org.jboss.as.test.integration.batch.common.CountingItemWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
public class ChunkPartitionTestCase extends AbstractBatchTestCase {
    @ArquillianResource
    private ManagementClient managementClient;

    @Inject
    private CountingItemWriter countingItemWriter;

    @Test
    public void testSuspend() throws Exception {
        try {
            final Properties jobProperties = new Properties();
            jobProperties.setProperty("reader.end", "10");
            final JobOperator jobOperator = BatchRuntime.getJobOperator();
            // Start the first job
            long executionId = jobOperator.start("chunk-suspend", jobProperties);
            JobExecution jobExecution = jobOperator.getJobExecution(executionId);
            // Wait until the job is complete for a maximum of 5 seconds
            AbstractBatchTestCase.waitForTermination(jobExecution, 5);
            Assert.assertEquals(COMPLETED, jobExecution.getBatchStatus());
            // Check that count
            Assert.assertEquals(10, countingItemWriter.getWrittenItemSize());
            // Suspend the server
            managementClient.getControllerClient().execute(Operations.createOperation("suspend"));
            // Submit another job which should be queued, should be safe with an InMemoryJobRepository (the default)
            executionId = jobOperator.start("chunk-suspend", jobProperties);
            // The job should not exist yet as the server is suspended
            try {
                jobOperator.getJobExecution(executionId);
            } catch (NoSuchJobExecutionException expected) {
                Assert.fail(("Job should not exist as the server is suspended: " + executionId));
            }
            // Resume the server which should kick of queued jobs
            managementClient.getControllerClient().execute(Operations.createOperation("resume"));
            // Get the execution
            jobExecution = jobOperator.getJobExecution(executionId);
            // Wait until the job is complete for a maximum of 5 seconds
            AbstractBatchTestCase.waitForTermination(jobExecution, 5);
            Assert.assertEquals(COMPLETED, jobExecution.getBatchStatus());
            // Check that count
            Assert.assertEquals(20, countingItemWriter.getWrittenItemSize());
        } finally {
            managementClient.getControllerClient().execute(Operations.createOperation("resume"));
        }
    }
}

