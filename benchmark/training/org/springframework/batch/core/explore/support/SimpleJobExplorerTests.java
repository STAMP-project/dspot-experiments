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
package org.springframework.batch.core.explore.support;


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;


/**
 * Test {@link SimpleJobExplorer}.
 *
 * @author Dave Syer
 * @author Will Schipp
 * @author Michael Minella
 */
public class SimpleJobExplorerTests {
    private SimpleJobExplorer jobExplorer;

    private JobExecutionDao jobExecutionDao;

    private JobInstanceDao jobInstanceDao;

    private StepExecutionDao stepExecutionDao;

    private JobInstance jobInstance = new JobInstance(111L, "job");

    private ExecutionContextDao ecDao;

    private JobExecution jobExecution = new JobExecution(jobInstance, 1234L, new JobParameters(), null);

    @Test
    public void testGetJobExecution() throws Exception {
        Mockito.when(jobExecutionDao.getJobExecution(123L)).thenReturn(jobExecution);
        Mockito.when(jobInstanceDao.getJobInstance(jobExecution)).thenReturn(jobInstance);
        stepExecutionDao.addStepExecutions(jobExecution);
        jobExplorer.getJobExecution(123L);
    }

    @Test
    public void testMissingGetJobExecution() throws Exception {
        Mockito.when(jobExecutionDao.getJobExecution(123L)).thenReturn(null);
        Assert.assertNull(jobExplorer.getJobExecution(123L));
    }

    @Test
    public void testGetStepExecution() throws Exception {
        Mockito.when(jobExecutionDao.getJobExecution(jobExecution.getId())).thenReturn(jobExecution);
        Mockito.when(jobInstanceDao.getJobInstance(jobExecution)).thenReturn(jobInstance);
        StepExecution stepExecution = jobExecution.createStepExecution("foo");
        Mockito.when(stepExecutionDao.getStepExecution(jobExecution, 123L)).thenReturn(stepExecution);
        Mockito.when(ecDao.getExecutionContext(stepExecution)).thenReturn(null);
        stepExecution = jobExplorer.getStepExecution(jobExecution.getId(), 123L);
        Assert.assertEquals(jobInstance, stepExecution.getJobExecution().getJobInstance());
        Mockito.verify(jobInstanceDao).getJobInstance(jobExecution);
    }

    @Test
    public void testGetStepExecutionMissing() throws Exception {
        Mockito.when(jobExecutionDao.getJobExecution(jobExecution.getId())).thenReturn(jobExecution);
        Mockito.when(stepExecutionDao.getStepExecution(jobExecution, 123L)).thenReturn(null);
        Assert.assertNull(jobExplorer.getStepExecution(jobExecution.getId(), 123L));
    }

    @Test
    public void testGetStepExecutionMissingJobExecution() throws Exception {
        Mockito.when(jobExecutionDao.getJobExecution(jobExecution.getId())).thenReturn(null);
        Assert.assertNull(jobExplorer.getStepExecution(jobExecution.getId(), 123L));
    }

    @Test
    public void testFindRunningJobExecutions() throws Exception {
        StepExecution stepExecution = jobExecution.createStepExecution("step");
        Mockito.when(jobExecutionDao.findRunningJobExecutions("job")).thenReturn(Collections.singleton(jobExecution));
        Mockito.when(jobInstanceDao.getJobInstance(jobExecution)).thenReturn(jobInstance);
        stepExecutionDao.addStepExecutions(jobExecution);
        Mockito.when(ecDao.getExecutionContext(jobExecution)).thenReturn(null);
        Mockito.when(ecDao.getExecutionContext(stepExecution)).thenReturn(null);
        jobExplorer.findRunningJobExecutions("job");
    }

    @Test
    public void testFindJobExecutions() throws Exception {
        StepExecution stepExecution = jobExecution.createStepExecution("step");
        Mockito.when(jobExecutionDao.findJobExecutions(jobInstance)).thenReturn(Collections.singletonList(jobExecution));
        Mockito.when(jobInstanceDao.getJobInstance(jobExecution)).thenReturn(jobInstance);
        stepExecutionDao.addStepExecutions(jobExecution);
        Mockito.when(ecDao.getExecutionContext(jobExecution)).thenReturn(null);
        Mockito.when(ecDao.getExecutionContext(stepExecution)).thenReturn(null);
        jobExplorer.getJobExecutions(jobInstance);
    }

    @Test
    public void testGetJobInstance() throws Exception {
        jobInstanceDao.getJobInstance(111L);
        jobExplorer.getJobInstance(111L);
    }

    @Test
    public void testGetLastJobInstances() throws Exception {
        jobInstanceDao.getJobInstances("foo", 0, 1);
        jobExplorer.getJobInstances("foo", 0, 1);
    }

    @Test
    public void testGetJobNames() throws Exception {
        jobInstanceDao.getJobNames();
        jobExplorer.getJobNames();
    }

    @Test
    public void testGetJobInstanceCount() throws Exception {
        Mockito.when(jobInstanceDao.getJobInstanceCount("myJob")).thenReturn(4);
        Assert.assertEquals(4, jobExplorer.getJobInstanceCount("myJob"));
    }

    @Test(expected = NoSuchJobException.class)
    public void testGetJobInstanceCountException() throws Exception {
        Mockito.when(jobInstanceDao.getJobInstanceCount("throwException")).thenThrow(new NoSuchJobException("expected"));
        jobExplorer.getJobInstanceCount("throwException");
    }
}

