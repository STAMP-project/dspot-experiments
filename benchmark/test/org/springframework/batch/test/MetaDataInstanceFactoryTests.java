/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.test;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.support.PropertiesConverter;


/**
 *
 *
 * @author Dave Syer
 */
public class MetaDataInstanceFactoryTests {
    private String jobName = "JOB";

    private Long instanceId = 321L;

    private String jobParametersString = "foo=bar";

    private JobParameters jobParameters = new DefaultJobParametersConverter().getJobParameters(PropertiesConverter.stringToProperties(jobParametersString));

    private Long executionId = 4321L;

    private String stepName = "step";

    private Long stepExecutionId = 11L;

    /**
     * Test method for
     * {@link MetaDataInstanceFactory#createJobInstance(String, Long)} .
     */
    @Test
    public void testCreateJobInstanceStringLong() {
        Assert.assertNotNull(MetaDataInstanceFactory.createJobInstance(jobName, instanceId));
    }

    /**
     * Test method for {@link MetaDataInstanceFactory#createJobInstance()} .
     */
    @Test
    public void testCreateJobInstance() {
        Assert.assertNotNull(MetaDataInstanceFactory.createJobInstance());
    }

    /**
     * Test method for {@link MetaDataInstanceFactory#createJobExecution()} .
     */
    @Test
    public void testCreateJobExecution() {
        Assert.assertNotNull(MetaDataInstanceFactory.createJobExecution());
    }

    /**
     * Test method for {@link MetaDataInstanceFactory#createJobExecution(Long)}
     * .
     */
    @Test
    public void testCreateJobExecutionLong() {
        Assert.assertNotNull(MetaDataInstanceFactory.createJobExecution(instanceId));
    }

    /**
     * Test method for
     * {@link MetaDataInstanceFactory#createJobExecution(String, Long, Long)} .
     */
    @Test
    public void testCreateJobExecutionStringLongLong() {
        Assert.assertNotNull(MetaDataInstanceFactory.createJobExecution(jobName, instanceId, executionId));
    }

    /**
     * Test method for
     * {@link MetaDataInstanceFactory#createJobExecution(String, Long, Long, String)}
     * .
     */
    @Test
    public void testCreateJobExecutionStringLongLongString() {
        Assert.assertNotNull(MetaDataInstanceFactory.createJobExecution(jobName, instanceId, executionId, jobParametersString));
    }

    /**
     * Test method for
     * {@link MetaDataInstanceFactory#createJobExecution(String, Long, Long, JobParameters)}
     * .
     */
    @Test
    public void testCreateJobExecutionStringLongLongJobParameters() {
        Assert.assertNotNull(MetaDataInstanceFactory.createJobExecution(jobName, instanceId, executionId, jobParameters));
    }

    /**
     * Test method for {@link MetaDataInstanceFactory#createStepExecution()} .
     */
    @Test
    public void testCreateStepExecution() {
        Assert.assertNotNull(MetaDataInstanceFactory.createStepExecution());
    }

    /**
     * Test method for
     * {@link MetaDataInstanceFactory#createStepExecution(String, Long)} .
     */
    @Test
    public void testCreateStepExecutionStringLong() {
        Assert.assertNotNull(MetaDataInstanceFactory.createStepExecution(stepName, stepExecutionId));
    }

    /**
     * Test method for
     * {@link MetaDataInstanceFactory#createStepExecution(JobExecution, String, Long)}
     * .
     */
    @Test
    public void testCreateStepExecutionJobExecutionStringLong() {
        Assert.assertNotNull(MetaDataInstanceFactory.createStepExecution(stepName, stepExecutionId));
    }

    /**
     * Test method for
     * {@link MetaDataInstanceFactory#createJobExecutionWithStepExecutions(Long, java.util.Collection)}
     * .
     */
    @Test
    public void testCreateJobExecutionWithStepExecutions() {
        Assert.assertNotNull(MetaDataInstanceFactory.createJobExecutionWithStepExecutions(executionId, Arrays.asList(stepName)));
    }
}

