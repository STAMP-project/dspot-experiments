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
package org.springframework.batch.core.scope.context;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dave Syer
 */
public class ChunkContextTests {
    private ChunkContext context = new ChunkContext(new StepContext(createStepExecution("foo")));

    @Test
    public void testGetStepContext() {
        StepContext stepContext = context.getStepContext();
        Assert.assertNotNull(stepContext);
        Assert.assertEquals("bar", context.getStepContext().getJobParameters().get("foo"));
    }

    @Test
    public void testIsComplete() {
        Assert.assertFalse(context.isComplete());
        context.setComplete();
        Assert.assertTrue(context.isComplete());
    }

    @Test
    public void testToString() {
        String value = context.toString();
        Assert.assertTrue(("Wrong toString: " + value), value.contains("stepContext="));
        Assert.assertTrue(("Wrong toString: " + value), value.contains("complete=false"));
        Assert.assertTrue(("Wrong toString: " + value), value.contains("attributes=[]"));
    }
}

