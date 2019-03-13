/**
 * Copyright 2011-2014 the original author or authors.
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
package org.springframework.batch.core.partition.support;


import BatchStatus.COMPLETED;
import BatchStatus.STARTING;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;


public class RemoteStepExecutionAggregatorTests {
    private RemoteStepExecutionAggregator aggregator = new RemoteStepExecutionAggregator();

    private JobExecution jobExecution;

    private StepExecution result;

    private StepExecution stepExecution1;

    private StepExecution stepExecution2;

    @Test
    public void testAggregateEmpty() {
        aggregator.aggregate(result, Collections.<StepExecution>emptySet());
    }

    @Test
    public void testAggregateNull() {
        aggregator.aggregate(result, null);
    }

    @Test
    public void testAggregateStatusSunnyDay() {
        stepExecution1.setStatus(COMPLETED);
        stepExecution2.setStatus(COMPLETED);
        aggregator.aggregate(result, Arrays.<StepExecution>asList(stepExecution1, stepExecution2));
        Assert.assertNotNull(result);
        Assert.assertEquals(STARTING, result.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void testAggregateStatusMissingExecution() {
        stepExecution2 = jobExecution.createStepExecution("foo:3");
        stepExecution1.setStatus(COMPLETED);
        stepExecution2.setStatus(COMPLETED);
        aggregator.aggregate(result, Arrays.<StepExecution>asList(stepExecution1, stepExecution2));
        Assert.assertNotNull(result);
        Assert.assertEquals(STARTING, result.getStatus());
    }
}

