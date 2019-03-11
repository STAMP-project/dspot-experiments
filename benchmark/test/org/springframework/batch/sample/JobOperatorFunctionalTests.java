/**
 * Copyright 2008-2009 the original author or authors.
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


import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/simple-job-launcher-context.xml", "/jobs/infiniteLoopJob.xml" })
public class JobOperatorFunctionalTests {
    private static final Log LOG = LogFactory.getLog(JobOperatorFunctionalTests.class);

    @Autowired
    private JobOperator operator;

    @Autowired
    private Job job;

    @Autowired
    private JobRegistry jobRegistry;

    @Test
    public void testStartStopResumeJob() throws Exception {
        String params = new JobParametersBuilder().addLong("jobOperatorTestParam", 7L).toJobParameters().toString();
        long executionId = operator.start(job.getName(), params);
        Assert.assertEquals(params, operator.getParameters(executionId));
        stopAndCheckStatus(executionId);
        long resumedExecutionId = operator.restart(executionId);
        Assert.assertEquals(params, operator.getParameters(resumedExecutionId));
        stopAndCheckStatus(resumedExecutionId);
        List<Long> instances = operator.getJobInstances(job.getName(), 0, 1);
        Assert.assertEquals(1, instances.size());
        long instanceId = instances.get(0);
        List<Long> executions = operator.getExecutions(instanceId);
        Assert.assertEquals(2, executions.size());
        // latest execution is the first in the returned list
        Assert.assertEquals(resumedExecutionId, executions.get(0).longValue());
        Assert.assertEquals(executionId, executions.get(1).longValue());
    }

    @Test
    public void testMultipleSimultaneousInstances() throws Exception {
        String jobName = job.getName();
        Set<String> names = operator.getJobNames();
        Assert.assertEquals(1, names.size());
        Assert.assertTrue(names.contains(jobName));
        long exec1 = operator.startNextInstance(jobName);
        long exec2 = operator.startNextInstance(jobName);
        Assert.assertTrue((exec1 != exec2));
        Assert.assertTrue((!(operator.getParameters(exec1).equals(operator.getParameters(exec2)))));
        // Give the asynchronous task executor a chance to start executions
        Thread.sleep(1000);
        Set<Long> executions = operator.getRunningExecutions(jobName);
        Assert.assertTrue(executions.contains(exec1));
        Assert.assertTrue(executions.contains(exec2));
        int count = 0;
        boolean running = (operator.getSummary(exec1).contains("STARTED")) && (operator.getSummary(exec2).contains("STARTED"));
        while (((count++) < 10) && (!running)) {
            Thread.sleep(100L);
            running = (operator.getSummary(exec1).contains("STARTED")) && (operator.getSummary(exec2).contains("STARTED"));
        } 
        Assert.assertTrue(String.format("Jobs not started: [%s] and [%s]", operator.getSummary(exec1), operator.getSummary(exec1)), running);
        operator.stop(exec1);
        operator.stop(exec2);
    }
}

