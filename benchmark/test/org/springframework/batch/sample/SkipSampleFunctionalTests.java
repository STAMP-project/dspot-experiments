/**
 * Copyright 2008-2014 the original author or authors.
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
package org.springframework.batch.sample;


import BatchStatus.COMPLETED;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Error is encountered during writing - transaction is rolled back and the
 * error item is skipped on second attempt to process the chunk.
 *
 * @author Robert Kasanicky
 * @author Dan Garrette
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/skipSample-job-launcher-context.xml" })
public class SkipSampleFunctionalTests {
    private JdbcOperations jdbcTemplate;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    @Qualifier("customerIncrementer")
    private DataFieldMaxValueIncrementer incrementer;

    /**
     * LAUNCH 1 <br>
     * <br>
     * step1
     * <ul>
     * <li>The step name is saved to the job execution context.
     * <li>Read five records from flat file and insert them into the TRADE
     * table.
     * <li>One record will be invalid, and it will be skipped. Four records will
     * be written to the database.
     * <li>The skip will result in an exit status that directs the job to run
     * the error logging step.
     * </ul>
     * errorPrint1
     * <ul>
     * <li>The error logging step will log one record using the step name from
     * the job execution context.
     * </ul>
     * step2
     * <ul>
     * <li>The step name is saved to the job execution context.
     * <li>Read four records from the TRADE table and processes them.
     * <li>One record will be invalid, and it will be skipped. Three records
     * will be stored in the writer's "items" property.
     * <li>The skip will result in an exit status that directs the job to run
     * the error logging step.
     * </ul>
     * errorPrint2
     * <ul>
     * <li>The error logging step will log one record using the step name from
     * the job execution context.
     * </ul>
     * <br>
     * <br>
     * LAUNCH 2 <br>
     * <br>
     * step1
     * <ul>
     * <li>The step name is saved to the job execution context.
     * <li>Read five records from flat file and insert them into the TRADE
     * table.
     * <li>No skips will occur.
     * <li>The exist status of SUCCESS will direct the job to step2.
     * </ul>
     * errorPrint1
     * <ul>
     * <li>This step does not occur. No error records are logged.
     * </ul>
     * step2
     * <ul>
     * <li>The step name is saved to the job execution context.
     * <li>Read five records from the TRADE table and processes them.
     * <li>No skips will occur.
     * <li>The exist status of SUCCESS will direct the job to end.
     * </ul>
     * errorPrint2
     * <ul>
     * <li>This step does not occur. No error records are logged.
     * </ul>
     */
    @Test
    public void testJobIncrementing() {
        // 
        // Launch 1
        // 
        long id1 = launchJobWithIncrementer();
        JobExecution execution1 = jobExplorer.getJobExecution(id1);
        Assert.assertEquals(COMPLETED, execution1.getStatus());
        validateLaunchWithSkips(execution1);
        // 
        // Clear the data
        // 
        setUp();
        // 
        // Launch 2
        // 
        long id2 = launchJobWithIncrementer();
        JobExecution execution2 = jobExplorer.getJobExecution(id2);
        Assert.assertEquals(COMPLETED, execution2.getStatus());
        validateLaunchWithoutSkips(execution2);
        // 
        // Make sure that the launches were separate executions and separate
        // instances
        // 
        Assert.assertTrue((id1 != id2));
        Assert.assertTrue((!(execution1.getJobId().equals(execution2.getJobId()))));
    }
}

