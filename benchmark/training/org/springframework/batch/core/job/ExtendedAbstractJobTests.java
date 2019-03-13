/**
 * Copyright 2006-2013 the original author or authors.
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
package org.springframework.batch.core.job;


import BatchStatus.FAILED;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.step.StepSupport;


/**
 *
 *
 * @author Dave Syer
 */
public class ExtendedAbstractJobTests {
    private AbstractJob job;

    private JobRepository jobRepository;

    /**
     * Test method for
     * {@link org.springframework.batch.core.job.AbstractJob#getName()}.
     */
    @Test
    public void testGetName() {
        job = new ExtendedAbstractJobTests.StubJob();
        Assert.assertNull(job.getName());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.job.AbstractJob#setBeanName(java.lang.String)}
     * .
     */
    @Test
    public void testSetBeanName() {
        job.setBeanName("foo");
        Assert.assertEquals("job", job.getName());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.job.AbstractJob#setBeanName(java.lang.String)}
     * .
     */
    @Test
    public void testSetBeanNameWithNullName() {
        job = new ExtendedAbstractJobTests.StubJob(null, null);
        Assert.assertEquals(null, job.getName());
        job.setBeanName("foo");
        Assert.assertEquals("foo", job.getName());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.job.AbstractJob#setRestartable(boolean)}
     * .
     */
    @Test
    public void testSetRestartable() {
        Assert.assertTrue(job.isRestartable());
        job.setRestartable(false);
        Assert.assertFalse(job.isRestartable());
    }

    @Test
    public void testToString() throws Exception {
        String value = job.toString();
        Assert.assertTrue(("Should contain name: " + value), ((value.indexOf("name=")) >= 0));
    }

    @Test
    public void testAfterPropertiesSet() throws Exception {
        job.setJobRepository(null);
        try {
            job.afterPropertiesSet();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("JobRepository"));
        }
    }

    @Test
    public void testValidatorWithNotNullParameters() throws Exception {
        JobExecution execution = jobRepository.createJobExecution("job", new JobParameters());
        job.execute(execution);
        // Should be free of side effects
    }

    @Test
    public void testSetValidator() throws Exception {
        job.setJobParametersValidator(new DefaultJobParametersValidator() {
            @Override
            public void validate(JobParameters parameters) throws JobParametersInvalidException {
                throw new JobParametersInvalidException("FOO");
            }
        });
        JobExecution execution = jobRepository.createJobExecution("job", new JobParameters());
        job.execute(execution);
        Assert.assertEquals(FAILED, execution.getStatus());
        Assert.assertEquals("FOO", execution.getFailureExceptions().get(0).getMessage());
        String description = execution.getExitStatus().getExitDescription();
        Assert.assertTrue(("Wrong description: " + description), description.contains("FOO"));
    }

    /**
     * Runs the step and persists job execution context.
     */
    @Test
    public void testHandleStep() throws Exception {
        class StubStep extends StepSupport {
            static final String value = "message for next steps";

            static final String key = "StubStep";

            {
                setName("StubStep");
            }

            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {
                stepExecution.getJobExecution().getExecutionContext().put(StubStep.key, StubStep.value);
            }
        }
        MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
        factory.afterPropertiesSet();
        JobRepository repository = factory.getObject();
        job.setJobRepository(repository);
        job.setRestartable(true);
        JobExecution execution = repository.createJobExecution("testHandleStepJob", new JobParameters());
        job.handleStep(new StubStep(), execution);
        Assert.assertEquals(StubStep.value, execution.getExecutionContext().get(StubStep.key));
        // simulate restart and check the job execution context's content survives
        execution.setEndTime(new Date());
        execution.setStatus(FAILED);
        repository.update(execution);
        JobExecution restarted = repository.createJobExecution("testHandleStepJob", new JobParameters());
        Assert.assertEquals(StubStep.value, restarted.getExecutionContext().get(StubStep.key));
    }

    /**
     *
     *
     * @author Dave Syer
     */
    private static class StubJob extends AbstractJob {
        /**
         *
         *
         * @param name
         * 		
         * @param jobRepository
         * 		
         */
        private StubJob(String name, JobRepository jobRepository) {
            super(name);
            try {
                setJobRepository(jobRepository);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        /**
         * No-name constructor
         */
        public StubJob() {
            super();
        }

        @Override
        protected void doExecute(JobExecution execution) throws JobExecutionException {
        }

        @Override
        public Step getStep(String stepName) {
            return null;
        }

        @Override
        public Collection<String> getStepNames() {
            return Collections.<String>emptySet();
        }
    }
}

