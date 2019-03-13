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
package org.springframework.batch.core.resource;


import java.io.IOException;
import junit.framework.TestCase;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.repeat.RepeatContext;


/**
 * Unit tests for {@link StepExecutionSimpleCompletionPolicy}
 *
 * @author Dave Syer
 */
public class StepExecutionSimpleCompletionPolicyTests extends TestCase {
    /**
     * Object under test
     */
    private StepExecutionSimpleCompletionPolicy policy = new StepExecutionSimpleCompletionPolicy();

    private JobInstance jobInstance;

    private StepExecution stepExecution;

    public void testToString() throws Exception {
        String msg = policy.toString();
        TestCase.assertTrue("String does not contain chunk size", ((msg.indexOf("chunkSize=2")) >= 0));
    }

    public void testKeyName() throws IOException, Exception {
        RepeatContext context = policy.start(null);
        TestCase.assertFalse(policy.isComplete(context));
    }
}

