/**
 * Copyright 2008-2013 the original author or authors.
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
package org.springframework.batch.core.step.item;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.repeat.support.RepeatTemplate;


public class SimpleChunkProviderTests {
    private SimpleChunkProvider<String> provider;

    private StepContribution contribution = new StepContribution(new StepExecution("foo", new org.springframework.batch.core.JobExecution(new JobInstance(123L, "job"), new JobParameters())));

    @Test
    public void testProvide() throws Exception {
        provider = new SimpleChunkProvider(new org.springframework.batch.item.support.ListItemReader(Arrays.asList("foo", "bar")), new RepeatTemplate());
        Chunk<String> chunk = provider.provide(contribution);
        Assert.assertNotNull(chunk);
        Assert.assertEquals(2, chunk.getItems().size());
    }

    @Test
    public void testProvideWithOverflow() throws Exception {
        provider = new SimpleChunkProvider<String>(new org.springframework.batch.item.support.ListItemReader(Arrays.asList("foo", "bar")), new RepeatTemplate()) {
            @Override
            protected String read(StepContribution contribution, Chunk<String> chunk) throws Exception, SkipOverflowException {
                chunk.skip(new RuntimeException("Planned"));
                throw new SkipOverflowException("Overflow");
            }
        };
        Chunk<String> chunk = null;
        chunk = provider.provide(contribution);
        Assert.assertNotNull(chunk);
        Assert.assertEquals(0, chunk.getItems().size());
        Assert.assertEquals(1, chunk.getErrors().size());
    }
}

