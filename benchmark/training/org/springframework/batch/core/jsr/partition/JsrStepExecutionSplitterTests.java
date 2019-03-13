/**
 * Copyright 2013 the original author or authors.
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
package org.springframework.batch.core.jsr.partition;


import java.util.Iterator;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;


public class JsrStepExecutionSplitterTests {
    private JsrStepExecutionSplitter splitter;

    @Test
    public void test() throws Exception {
        Set<StepExecution> executions = splitter.split(new StepExecution("step1", new JobExecution(5L)), 3);
        Assert.assertEquals(3, executions.size());
        Iterator<StepExecution> stepExecutions = executions.iterator();
        int count = 0;
        while (stepExecutions.hasNext()) {
            StepExecution curExecution = stepExecutions.next();
            Assert.assertEquals(("step1:partition" + count), curExecution.getStepName());
            count++;
        } 
    }
}

