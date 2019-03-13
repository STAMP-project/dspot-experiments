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
package org.springframework.batch.core.launch.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;


/**
 *
 *
 * @author Dave Syer
 * @author Michael Minella
 */
public class RunIdIncrementerTests {
    private RunIdIncrementer incrementer = new RunIdIncrementer();

    @Test
    public void testGetNext() {
        JobParameters next = incrementer.getNext(null);
        Assert.assertEquals(1, next.getLong("run.id").intValue());
        Assert.assertEquals(2, incrementer.getNext(next).getLong("run.id").intValue());
    }

    @Test
    public void testGetNextAppends() {
        JobParameters next = incrementer.getNext(new JobParametersBuilder().addString("foo", "bar").toJobParameters());
        Assert.assertEquals(1, next.getLong("run.id").intValue());
        Assert.assertEquals("bar", next.getString("foo"));
    }

    @Test
    public void testGetNextNamed() {
        incrementer.setKey("foo");
        JobParameters next = incrementer.getNext(null);
        Assert.assertEquals(1, next.getLong("foo").intValue());
    }
}

