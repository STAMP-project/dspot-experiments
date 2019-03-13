/**
 * Copyright 2008-2012 the original author or authors.
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
package org.springframework.batch.core.repository.dao;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;


/**
 * Tests for {@link MapExecutionContextDao}.
 */
@RunWith(JUnit4.class)
public class MapExecutionContextDaoTests extends AbstractExecutionContextDaoTests {
    @Test
    public void testSaveBothJobAndStepContextWithSameId() throws Exception {
        MapExecutionContextDao tested = new MapExecutionContextDao();
        JobExecution jobExecution = new JobExecution(1L);
        StepExecution stepExecution = new StepExecution("stepName", jobExecution, 1L);
        Assert.assertTrue(((stepExecution.getId()) == (jobExecution.getId())));
        jobExecution.getExecutionContext().put("type", "job");
        stepExecution.getExecutionContext().put("type", "step");
        Assert.assertTrue((!(jobExecution.getExecutionContext().get("type").equals(stepExecution.getExecutionContext().get("type")))));
        Assert.assertEquals("job", jobExecution.getExecutionContext().get("type"));
        Assert.assertEquals("step", stepExecution.getExecutionContext().get("type"));
        tested.saveExecutionContext(jobExecution);
        tested.saveExecutionContext(stepExecution);
        ExecutionContext jobCtx = tested.getExecutionContext(jobExecution);
        ExecutionContext stepCtx = tested.getExecutionContext(stepExecution);
        Assert.assertEquals("job", jobCtx.get("type"));
        Assert.assertEquals("step", stepCtx.get("type"));
    }

    @Test
    public void testPersistentCopy() throws Exception {
        MapExecutionContextDao tested = new MapExecutionContextDao();
        JobExecution jobExecution = new JobExecution(((long) (1)));
        StepExecution stepExecution = new StepExecution("stepName", jobExecution, 123L);
        Assert.assertTrue(stepExecution.getExecutionContext().isEmpty());
        tested.updateExecutionContext(stepExecution);
        stepExecution.getExecutionContext().put("key", "value");
        ExecutionContext retrieved = tested.getExecutionContext(stepExecution);
        Assert.assertTrue(retrieved.isEmpty());
        tested.updateExecutionContext(jobExecution);
        jobExecution.getExecutionContext().put("key", "value");
        retrieved = tested.getExecutionContext(jobExecution);
        Assert.assertTrue(retrieved.isEmpty());
    }
}

