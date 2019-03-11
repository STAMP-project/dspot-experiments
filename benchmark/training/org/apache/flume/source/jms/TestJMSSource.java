/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.source.jms;


import DefaultJMSMessageConverter.Builder;
import JMSSourceConfiguration.BATCH_SIZE;
import JMSSourceConfiguration.CONVERTER_TYPE;
import JMSSourceConfiguration.DESTINATION_NAME;
import JMSSourceConfiguration.DESTINATION_TYPE;
import JMSSourceConfiguration.INITIAL_CONTEXT_FACTORY;
import JMSSourceConfiguration.PASSWORD_FILE;
import JMSSourceConfiguration.PROVIDER_URL;
import JMSSourceConfiguration.USERNAME;
import Status.BACKOFF;
import Status.READY;
import java.io.File;
import java.util.List;
import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import junit.framework.Assert;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SourceCounter;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import static JMSSourceConfiguration.ERROR_THRESHOLD_DEFAULT;


public class TestJMSSource extends JMSMessageConsumerTestBase {
    private JMSSource source;

    private Context context;

    private InitialContext initialContext;

    private ChannelProcessor channelProcessor;

    private List<Event> events;

    private InitialContextFactory contextFactory;

    private File baseDir;

    private File passwordFile;

    @Test
    public void testStop() throws Exception {
        source.configure(context);
        source.start();
        source.stop();
        Mockito.verify(consumer).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigureWithoutInitialContextFactory() throws Exception {
        context.put(INITIAL_CONTEXT_FACTORY, "");
        source.configure(context);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigureWithoutProviderURL() throws Exception {
        context.put(PROVIDER_URL, "");
        source.configure(context);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigureWithoutDestinationName() throws Exception {
        context.put(JMSSourceConfiguration.DESTINATION_NAME, "");
        source.configure(context);
    }

    @Test(expected = FlumeException.class)
    public void testConfigureWithBadDestinationType() throws Exception {
        context.put(DESTINATION_TYPE, "DUMMY");
        source.configure(context);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigureWithEmptyDestinationType() throws Exception {
        context.put(DESTINATION_TYPE, "");
        source.configure(context);
    }

    @Test
    public void testStartConsumerCreateThrowsException() throws Exception {
        Mockito.doThrow(new RuntimeException("Expected")).when(source).createConsumer();
        source.configure(context);
        source.start();
        try {
            source.process();
            Assert.fail();
        } catch (FlumeException expected) {
        }
    }

    @Test(expected = FlumeException.class)
    public void testConfigureWithContextLookupThrowsException() throws Exception {
        Mockito.when(initialContext.lookup(ArgumentMatchers.anyString())).thenThrow(new NamingException());
        source.configure(context);
    }

    @Test(expected = FlumeException.class)
    public void testConfigureWithContextCreateThrowsException() throws Exception {
        Mockito.when(contextFactory.create(ArgumentMatchers.any(Properties.class))).thenThrow(new NamingException());
        source.configure(context);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigureWithInvalidBatchSize() throws Exception {
        context.put(BATCH_SIZE, "0");
        source.configure(context);
    }

    @Test(expected = FlumeException.class)
    public void testConfigureWithInvalidPasswordFile() throws Exception {
        context.put(PASSWORD_FILE, "/dev/does/not/exist/nor/will/ever/exist");
        source.configure(context);
    }

    @Test
    public void testConfigureWithUserNameButNoPasswordFile() throws Exception {
        context.put(JMSSourceConfiguration.USERNAME, "dummy");
        source.configure(context);
        source.start();
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(batchSize, events.size());
        assertBodyIsExpected(events);
    }

    @Test
    public void testConfigureWithUserNameAndPasswordFile() throws Exception {
        context.put(JMSSourceConfiguration.USERNAME, "dummy");
        context.put(PASSWORD_FILE, passwordFile.getAbsolutePath());
        source.configure(context);
        source.start();
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(batchSize, events.size());
        assertBodyIsExpected(events);
    }

    @Test(expected = FlumeException.class)
    public void testConfigureWithInvalidConverterClass() throws Exception {
        context.put(CONVERTER_TYPE, "not a valid classname");
        source.configure(context);
    }

    @Test
    public void testProcessNoStart() throws Exception {
        try {
            source.process();
            Assert.fail();
        } catch (EventDeliveryException expected) {
        }
    }

    @Test
    public void testNonDefaultConverter() throws Exception {
        // tests that a classname can be specified
        context.put(CONVERTER_TYPE, Builder.class.getName());
        source.configure(context);
        source.start();
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(batchSize, events.size());
        assertBodyIsExpected(events);
        Mockito.verify(consumer).commit();
    }

    public static class NonBuilderNonConfigurableConverter implements JMSMessageConverter {
        @Override
        public List<Event> convert(Message message) throws JMSException {
            throw new UnsupportedOperationException();
        }
    }

    public static class NonBuilderConfigurableConverter implements Configurable , JMSMessageConverter {
        @Override
        public List<Event> convert(Message message) throws JMSException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void configure(Context context) {
        }
    }

    @Test
    public void testNonBuilderConfigurableConverter() throws Exception {
        // tests that a non builder by configurable converter works
        context.put(CONVERTER_TYPE, TestJMSSource.NonBuilderConfigurableConverter.class.getName());
        source.configure(context);
        source.start();
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(batchSize, events.size());
        assertBodyIsExpected(events);
        Mockito.verify(consumer).commit();
    }

    @Test
    public void testNonBuilderNonConfigurableConverter() throws Exception {
        // tests that a non builder non configurable converter
        context.put(CONVERTER_TYPE, TestJMSSource.NonBuilderNonConfigurableConverter.class.getName());
        source.configure(context);
        source.start();
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(batchSize, events.size());
        assertBodyIsExpected(events);
        Mockito.verify(consumer).commit();
    }

    @Test
    public void testProcessFullBatch() throws Exception {
        source.configure(context);
        source.start();
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(batchSize, events.size());
        assertBodyIsExpected(events);
        Mockito.verify(consumer).commit();
    }

    @Test
    public void testProcessNoEvents() throws Exception {
        Mockito.when(messageConsumer.receive(ArgumentMatchers.anyLong())).thenReturn(null);
        source.configure(context);
        source.start();
        Assert.assertEquals(BACKOFF, source.process());
        Assert.assertEquals(0, events.size());
        Mockito.verify(consumer).commit();
    }

    @Test
    public void testProcessPartialBatch() throws Exception {
        Mockito.when(messageConsumer.receiveNoWait()).thenReturn(message, ((Message) (null)));
        source.configure(context);
        source.start();
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(2, events.size());
        assertBodyIsExpected(events);
        Mockito.verify(consumer).commit();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcessChannelProcessorThrowsChannelException() throws Exception {
        Mockito.doThrow(new ChannelException("dummy")).when(channelProcessor).processEventBatch(ArgumentMatchers.any(List.class));
        source.configure(context);
        source.start();
        Assert.assertEquals(BACKOFF, source.process());
        Mockito.verify(consumer).rollback();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcessChannelProcessorThrowsError() throws Exception {
        Mockito.doThrow(new Error()).when(channelProcessor).processEventBatch(ArgumentMatchers.any(List.class));
        source.configure(context);
        source.start();
        try {
            source.process();
            Assert.fail();
        } catch (Error ignores) {
        }
        Mockito.verify(consumer).rollback();
    }

    @Test
    public void testProcessReconnect() throws Exception {
        source.configure(context);
        source.start();
        Mockito.when(consumer.take()).thenThrow(new JMSException("dummy"));
        int attempts = ERROR_THRESHOLD_DEFAULT;
        for (int i = 0; i < attempts; i++) {
            Assert.assertEquals(BACKOFF, source.process());
        }
        Assert.assertEquals(BACKOFF, source.process());
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(source, "sourceCounter")));
        Assert.assertEquals(1, sc.getEventReadFail());
        Mockito.verify(consumer, Mockito.times((attempts + 1))).rollback();
        Mockito.verify(consumer, Mockito.times(1)).close();
    }

    @Test
    public void testErrorCounterEventReadFail() throws Exception {
        source.configure(context);
        source.start();
        Mockito.when(consumer.take()).thenThrow(new RuntimeException("dummy"));
        source.process();
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(source, "sourceCounter")));
        Assert.assertEquals(1, sc.getEventReadFail());
    }

    @Test
    public void testErrorCounterChannelWriteFail() throws Exception {
        source.configure(context);
        source.start();
        Mockito.when(source.getChannelProcessor()).thenThrow(new ChannelException("dummy"));
        source.process();
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(source, "sourceCounter")));
        Assert.assertEquals(1, sc.getChannelWriteFail());
    }
}

