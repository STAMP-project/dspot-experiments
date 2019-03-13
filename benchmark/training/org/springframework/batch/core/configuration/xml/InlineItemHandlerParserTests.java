/**
 * Copyright 2009-2014 the original author or authors.
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


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Dan Garrette
 * @since 2.1
 */
public class InlineItemHandlerParserTests {
    private ConfigurableApplicationContext context;

    @Test
    public void testInlineHandlers() throws Exception {
        context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/InlineItemHandlerParserTests-context.xml");
        Object step = context.getBean("inlineHandlers");
        Object tasklet = ReflectionTestUtils.getField(step, "tasklet");
        Object chunkProvider = ReflectionTestUtils.getField(tasklet, "chunkProvider");
        Object reader = ReflectionTestUtils.getField(chunkProvider, "itemReader");
        Object chunkProcessor = ReflectionTestUtils.getField(tasklet, "chunkProcessor");
        Object processor = ReflectionTestUtils.getField(chunkProcessor, "itemProcessor");
        Object writer = ReflectionTestUtils.getField(chunkProcessor, "itemWriter");
        Assert.assertTrue((reader instanceof TestReader));
        Assert.assertTrue((processor instanceof TestProcessor));
        Assert.assertTrue((writer instanceof TestWriter));
    }

    @Test
    public void testInlineAdapters() throws Exception {
        context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/InlineItemHandlerParserTests-context.xml");
        Object step = context.getBean("inlineAdapters");
        Object tasklet = ReflectionTestUtils.getField(step, "tasklet");
        Object chunkProvider = ReflectionTestUtils.getField(tasklet, "chunkProvider");
        Object reader = ReflectionTestUtils.getField(chunkProvider, "itemReader");
        Object chunkProcessor = ReflectionTestUtils.getField(tasklet, "chunkProcessor");
        Object processor = ReflectionTestUtils.getField(chunkProcessor, "itemProcessor");
        Object writer = ReflectionTestUtils.getField(chunkProcessor, "itemWriter");
        Assert.assertTrue((reader instanceof ItemReaderAdapter<?>));
        Object readerObject = ReflectionTestUtils.getField(reader, "targetObject");
        Assert.assertTrue((readerObject instanceof DummyItemHandlerAdapter));
        Object readerMethod = ReflectionTestUtils.getField(reader, "targetMethod");
        Assert.assertEquals("dummyRead", readerMethod);
        Assert.assertTrue((processor instanceof ItemProcessorAdapter<?, ?>));
        Object processorObject = ReflectionTestUtils.getField(processor, "targetObject");
        Assert.assertTrue((processorObject instanceof DummyItemHandlerAdapter));
        Object processorMethod = ReflectionTestUtils.getField(processor, "targetMethod");
        Assert.assertEquals("dummyProcess", processorMethod);
        Assert.assertTrue((writer instanceof ItemWriterAdapter<?>));
        Object writerObject = ReflectionTestUtils.getField(writer, "targetObject");
        Assert.assertTrue((writerObject instanceof DummyItemHandlerAdapter));
        Object writerMethod = ReflectionTestUtils.getField(writer, "targetMethod");
        Assert.assertEquals("dummyWrite", writerMethod);
    }

    @Test
    public void testInlineHandlersWithStepScope() throws Exception {
        context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/InlineItemHandlerWithStepScopeParserTests-context.xml");
        StepSynchronizationManager.register(new org.springframework.batch.core.StepExecution("step", new JobExecution(123L)));
        @SuppressWarnings({ "rawtypes" })
        Map<String, ItemReader> readers = context.getBeansOfType(ItemReader.class);
        // Should be 2 each (proxy and target) for the two readers in the steps defined
        Assert.assertEquals(4, readers.size());
    }
}

