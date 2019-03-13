/**
 * Copyright 2014 the original author or authors.
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
package org.springframework.batch.integration.config.xml;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.step.item.ChunkProcessor;
import org.springframework.batch.core.step.item.SimpleChunkProcessor;
import org.springframework.batch.integration.chunk.ChunkHandler;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.ChunkProcessorChunkHandler;
import org.springframework.batch.integration.chunk.RemoteChunkHandlerFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.config.ServiceActivatorFactoryBean;
import org.springframework.integration.test.util.TestUtils;
import org.springframework.messaging.MessageChannel;


/**
 * <p>
 * Test cases for the {@link org.springframework.batch.integration.config.xml.RemoteChunkingSlaveParser}
 * and {@link org.springframework.batch.integration.config.xml.RemoteChunkingMasterParser}.
 * </p>
 *
 * @author Chris Schaefer
 * @since 3.1
 */
@SuppressWarnings("unchecked")
public class RemoteChunkingParserTests {
    @SuppressWarnings("rawtypes")
    @Test
    public void testRemoteChunkingSlaveParserWithProcessorDefined() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/org/springframework/batch/integration/config/xml/RemoteChunkingSlaveParserTests.xml");
        ChunkHandler chunkHandler = applicationContext.getBean(ChunkProcessorChunkHandler.class);
        ChunkProcessor chunkProcessor = ((SimpleChunkProcessor) (TestUtils.getPropertyValue(chunkHandler, "chunkProcessor")));
        Assert.assertNotNull("ChunkProcessor must not be null", chunkProcessor);
        ItemWriter<String> itemWriter = ((ItemWriter<String>) (TestUtils.getPropertyValue(chunkProcessor, "itemWriter")));
        Assert.assertNotNull("ChunkProcessor ItemWriter must not be null", itemWriter);
        Assert.assertTrue("Got wrong instance of ItemWriter", (itemWriter instanceof RemoteChunkingParserTests.Writer));
        ItemProcessor<String, String> itemProcessor = ((ItemProcessor<String, String>) (TestUtils.getPropertyValue(chunkProcessor, "itemProcessor")));
        Assert.assertNotNull("ChunkProcessor ItemWriter must not be null", itemProcessor);
        Assert.assertTrue("Got wrong instance of ItemProcessor", (itemProcessor instanceof RemoteChunkingParserTests.Processor));
        FactoryBean serviceActivatorFactoryBean = applicationContext.getBean(ServiceActivatorFactoryBean.class);
        Assert.assertNotNull("ServiceActivatorFactoryBean must not be null", serviceActivatorFactoryBean);
        Assert.assertNotNull("Output channel must not be null", TestUtils.getPropertyValue(serviceActivatorFactoryBean, "outputChannel"));
        MessageChannel inputChannel = applicationContext.getBean("requests", MessageChannel.class);
        Assert.assertNotNull("Input channel must not be null", inputChannel);
        String targetMethodName = ((String) (TestUtils.getPropertyValue(serviceActivatorFactoryBean, "targetMethodName")));
        Assert.assertNotNull("Target method name must not be null", targetMethodName);
        Assert.assertTrue(("Target method name must be handleChunk, got: " + targetMethodName), "handleChunk".equals(targetMethodName));
        ChunkHandler targetObject = ((ChunkHandler) (TestUtils.getPropertyValue(serviceActivatorFactoryBean, "targetObject")));
        Assert.assertNotNull("Target object must not be null", targetObject);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testRemoteChunkingSlaveParserWithProcessorNotDefined() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/org/springframework/batch/integration/config/xml/RemoteChunkingSlaveParserNoProcessorTests.xml");
        ChunkHandler chunkHandler = applicationContext.getBean(ChunkProcessorChunkHandler.class);
        ChunkProcessor chunkProcessor = ((SimpleChunkProcessor) (TestUtils.getPropertyValue(chunkHandler, "chunkProcessor")));
        Assert.assertNotNull("ChunkProcessor must not be null", chunkProcessor);
        ItemProcessor<String, String> itemProcessor = ((ItemProcessor<String, String>) (TestUtils.getPropertyValue(chunkProcessor, "itemProcessor")));
        Assert.assertNotNull("ChunkProcessor ItemWriter must not be null", itemProcessor);
        Assert.assertTrue("Got wrong instance of ItemProcessor", (itemProcessor instanceof PassThroughItemProcessor));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testRemoteChunkingMasterParser() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/org/springframework/batch/integration/config/xml/RemoteChunkingMasterParserTests.xml");
        ItemWriter itemWriter = applicationContext.getBean("itemWriter", ChunkMessageChannelItemWriter.class);
        Assert.assertNotNull("Messaging template must not be null", TestUtils.getPropertyValue(itemWriter, "messagingGateway"));
        Assert.assertNotNull("Reply channel must not be null", TestUtils.getPropertyValue(itemWriter, "replyChannel"));
        FactoryBean<ChunkHandler> remoteChunkingHandlerFactoryBean = applicationContext.getBean(RemoteChunkHandlerFactoryBean.class);
        Assert.assertNotNull("Chunk writer must not be null", TestUtils.getPropertyValue(remoteChunkingHandlerFactoryBean, "chunkWriter"));
        Assert.assertNotNull("Step must not be null", TestUtils.getPropertyValue(remoteChunkingHandlerFactoryBean, "step"));
    }

    @Test
    public void testRemoteChunkingMasterIdAttrAssert() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        applicationContext.setValidating(false);
        applicationContext.setConfigLocation("/org/springframework/batch/integration/config/xml/RemoteChunkingMasterParserMissingIdAttrTests.xml");
        try {
            applicationContext.refresh();
            Assert.fail();
        } catch (BeanDefinitionStoreException e) {
            Assert.assertTrue("Nested exception must be of type IllegalArgumentException", ((e.getCause()) instanceof IllegalArgumentException));
            IllegalArgumentException iae = ((IllegalArgumentException) (e.getCause()));
            Assert.assertTrue((("Expected: " + ("The id attribute must be specified" + " but got: ")) + (iae.getMessage())), "The id attribute must be specified".equals(iae.getMessage()));
        }
    }

    @Test
    public void testRemoteChunkingMasterMessageTemplateAttrAssert() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        applicationContext.setValidating(false);
        applicationContext.setConfigLocation("/org/springframework/batch/integration/config/xml/RemoteChunkingMasterParserMissingMessageTemplateAttrTests.xml");
        try {
            applicationContext.refresh();
            Assert.fail();
        } catch (BeanDefinitionStoreException e) {
            Assert.assertTrue("Nested exception must be of type IllegalArgumentException", ((e.getCause()) instanceof IllegalArgumentException));
            IllegalArgumentException iae = ((IllegalArgumentException) (e.getCause()));
            Assert.assertTrue((("Expected: " + ("The message-template attribute must be specified" + " but got: ")) + (iae.getMessage())), "The message-template attribute must be specified".equals(iae.getMessage()));
        }
    }

    @Test
    public void testRemoteChunkingMasterStepAttrAssert() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        applicationContext.setValidating(false);
        applicationContext.setConfigLocation("/org/springframework/batch/integration/config/xml/RemoteChunkingMasterParserMissingStepAttrTests.xml");
        try {
            applicationContext.refresh();
            Assert.fail();
        } catch (BeanDefinitionStoreException e) {
            Assert.assertTrue("Nested exception must be of type IllegalArgumentException", ((e.getCause()) instanceof IllegalArgumentException));
            IllegalArgumentException iae = ((IllegalArgumentException) (e.getCause()));
            Assert.assertTrue((("Expected: " + ("The step attribute must be specified" + " but got: ")) + (iae.getMessage())), "The step attribute must be specified".equals(iae.getMessage()));
        }
    }

    @Test
    public void testRemoteChunkingMasterReplyChannelAttrAssert() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        applicationContext.setValidating(false);
        applicationContext.setConfigLocation("/org/springframework/batch/integration/config/xml/RemoteChunkingMasterParserMissingReplyChannelAttrTests.xml");
        try {
            applicationContext.refresh();
            Assert.fail();
        } catch (BeanDefinitionStoreException e) {
            Assert.assertTrue("Nested exception must be of type IllegalArgumentException", ((e.getCause()) instanceof IllegalArgumentException));
            IllegalArgumentException iae = ((IllegalArgumentException) (e.getCause()));
            Assert.assertTrue((("Expected: " + ("The reply-channel attribute must be specified" + " but got: ")) + (iae.getMessage())), "The reply-channel attribute must be specified".equals(iae.getMessage()));
        }
    }

    @Test
    public void testRemoteChunkingSlaveIdAttrAssert() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        applicationContext.setValidating(false);
        applicationContext.setConfigLocation("/org/springframework/batch/integration/config/xml/RemoteChunkingSlaveParserMissingIdAttrTests.xml");
        try {
            applicationContext.refresh();
            Assert.fail();
        } catch (BeanDefinitionStoreException e) {
            Assert.assertTrue("Nested exception must be of type IllegalArgumentException", ((e.getCause()) instanceof IllegalArgumentException));
            IllegalArgumentException iae = ((IllegalArgumentException) (e.getCause()));
            Assert.assertTrue((("Expected: " + ("The id attribute must be specified" + " but got: ")) + (iae.getMessage())), "The id attribute must be specified".equals(iae.getMessage()));
        }
    }

    @Test
    public void testRemoteChunkingSlaveInputChannelAttrAssert() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        applicationContext.setValidating(false);
        applicationContext.setConfigLocation("/org/springframework/batch/integration/config/xml/RemoteChunkingSlaveParserMissingInputChannelAttrTests.xml");
        try {
            applicationContext.refresh();
            Assert.fail();
        } catch (BeanDefinitionStoreException e) {
            Assert.assertTrue("Nested exception must be of type IllegalArgumentException", ((e.getCause()) instanceof IllegalArgumentException));
            IllegalArgumentException iae = ((IllegalArgumentException) (e.getCause()));
            Assert.assertTrue((("Expected: " + ("The input-channel attribute must be specified" + " but got: ")) + (iae.getMessage())), "The input-channel attribute must be specified".equals(iae.getMessage()));
        }
    }

    @Test
    public void testRemoteChunkingSlaveItemWriterAttrAssert() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        applicationContext.setValidating(false);
        applicationContext.setConfigLocation("/org/springframework/batch/integration/config/xml/RemoteChunkingSlaveParserMissingItemWriterAttrTests.xml");
        try {
            applicationContext.refresh();
            Assert.fail();
        } catch (BeanDefinitionStoreException e) {
            Assert.assertTrue("Nested exception must be of type IllegalArgumentException", ((e.getCause()) instanceof IllegalArgumentException));
            IllegalArgumentException iae = ((IllegalArgumentException) (e.getCause()));
            Assert.assertTrue((("Expected: " + ("The item-writer attribute must be specified" + " but got: ")) + (iae.getMessage())), "The item-writer attribute must be specified".equals(iae.getMessage()));
        }
    }

    @Test
    public void testRemoteChunkingSlaveOutputChannelAttrAssert() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        applicationContext.setValidating(false);
        applicationContext.setConfigLocation("/org/springframework/batch/integration/config/xml/RemoteChunkingSlaveParserMissingOutputChannelAttrTests.xml");
        try {
            applicationContext.refresh();
            Assert.fail();
        } catch (BeanDefinitionStoreException e) {
            Assert.assertTrue("Nested exception must be of type IllegalArgumentException", ((e.getCause()) instanceof IllegalArgumentException));
            IllegalArgumentException iae = ((IllegalArgumentException) (e.getCause()));
            Assert.assertTrue((("Expected: " + ("The output-channel attribute must be specified" + " but got: ")) + (iae.getMessage())), "The output-channel attribute must be specified".equals(iae.getMessage()));
        }
    }

    private static class Writer implements ItemWriter<String> {
        @Override
        public void write(List<? extends String> items) throws Exception {
            // 
        }
    }

    private static class Processor implements ItemProcessor<String, String> {
        @Override
        public String process(String item) throws Exception {
            return item;
        }
    }
}

