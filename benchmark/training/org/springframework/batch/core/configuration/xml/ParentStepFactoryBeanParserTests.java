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
package org.springframework.batch.core.configuration.xml;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.item.FaultTolerantChunkProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Dave Syer
 */
public class ParentStepFactoryBeanParserTests {
    @Test
    @SuppressWarnings("resource")
    public void testSimpleAttributes() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ParentStepFactoryBeanParserTests-context.xml");
        Object step = context.getBean("s1", Step.class);
        Assert.assertNotNull("Step not parsed", step);
        Object tasklet = ReflectionTestUtils.getField(step, "tasklet");
        Object chunkProcessor = ReflectionTestUtils.getField(tasklet, "chunkProcessor");
        Assert.assertTrue("Wrong processor type", (chunkProcessor instanceof FaultTolerantChunkProcessor<?, ?>));
    }

    @Test
    @SuppressWarnings("resource")
    public void testSkippableAttributes() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ParentSkippableStepFactoryBeanParserTests-context.xml");
        Object step = context.getBean("s1", Step.class);
        Assert.assertNotNull("Step not parsed", step);
        Object tasklet = ReflectionTestUtils.getField(step, "tasklet");
        Object chunkProcessor = ReflectionTestUtils.getField(tasklet, "chunkProcessor");
        Assert.assertTrue("Wrong processor type", (chunkProcessor instanceof FaultTolerantChunkProcessor<?, ?>));
    }

    @Test
    @SuppressWarnings("resource")
    public void testRetryableAttributes() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ParentRetryableStepFactoryBeanParserTests-context.xml");
        Object step = context.getBean("s1", Step.class);
        Assert.assertNotNull("Step not parsed", step);
        Object tasklet = ReflectionTestUtils.getField(step, "tasklet");
        Object chunkProcessor = ReflectionTestUtils.getField(tasklet, "chunkProcessor");
        Assert.assertTrue("Wrong processor type", (chunkProcessor instanceof FaultTolerantChunkProcessor<?, ?>));
    }

    // BATCH-1396
    @Test
    @SuppressWarnings("resource")
    public void testRetryableLateBindingAttributes() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ParentRetryableLateBindingStepFactoryBeanParserTests-context.xml");
        Object step = context.getBean("s1", Step.class);
        Assert.assertNotNull("Step not parsed", step);
        Object tasklet = ReflectionTestUtils.getField(step, "tasklet");
        Object chunkProcessor = ReflectionTestUtils.getField(tasklet, "chunkProcessor");
        Assert.assertTrue("Wrong processor type", (chunkProcessor instanceof FaultTolerantChunkProcessor<?, ?>));
    }

    // BATCH-1396
    @Test
    @SuppressWarnings("resource")
    public void testSkippableLateBindingAttributes() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ParentSkippableLateBindingStepFactoryBeanParserTests-context.xml");
        Object step = context.getBean("s1", Step.class);
        Assert.assertNotNull("Step not parsed", step);
        Object tasklet = ReflectionTestUtils.getField(step, "tasklet");
        Object chunkProcessor = ReflectionTestUtils.getField(tasklet, "chunkProcessor");
        Assert.assertTrue("Wrong processor type", (chunkProcessor instanceof FaultTolerantChunkProcessor<?, ?>));
    }
}

