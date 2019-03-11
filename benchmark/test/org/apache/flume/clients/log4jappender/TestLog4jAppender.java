/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.clients.log4jappender;


import Log4jAvroHeaders.ADDRESS;
import Log4jAvroHeaders.LOGGER_NAME;
import Log4jAvroHeaders.LOG_LEVEL;
import Log4jAvroHeaders.MESSAGE_ENCODING;
import Log4jAvroHeaders.TIMESTAMP;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.Transaction;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.conf.Configurables;
import org.apache.flume.source.AvroSource;
import org.apache.flume.source.avro.AvroFlumeEvent;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestLog4jAppender {
    private AvroSource source;

    private Channel ch;

    private Properties props;

    @Test
    public void testLog4jAppender() throws IOException {
        configureSource();
        PropertyConfigurator.configure(props);
        Logger logger = LogManager.getLogger(TestLog4jAppender.class);
        for (int count = 0; count <= 1000; count++) {
            /* Log4j internally defines levels as multiples of 10000. So if we
            create levels directly using count, the level will be set as the
            default.
             */
            int level = ((count % 5) + 1) * 10000;
            String msg = "This is log message number" + (String.valueOf(count));
            logger.log(Level.toLevel(level), msg);
            Transaction transaction = ch.getTransaction();
            transaction.begin();
            Event event = ch.take();
            Assert.assertNotNull(event);
            Assert.assertEquals(new String(event.getBody(), "UTF8"), msg);
            Map<String, String> hdrs = event.getHeaders();
            Assert.assertNotNull(hdrs.get(TIMESTAMP.toString()));
            Assert.assertEquals(Level.toLevel(level), Level.toLevel(Integer.valueOf(hdrs.get(LOG_LEVEL.toString()))));
            Assert.assertNotNull(hdrs.get(ADDRESS.toString()));
            Assert.assertEquals(logger.getName(), hdrs.get(LOGGER_NAME.toString()));
            Assert.assertEquals("UTF8", hdrs.get(MESSAGE_ENCODING.toString()));
            transaction.commit();
            transaction.close();
        }
    }

    @Test
    public void testLogBatch() {
        testBatchedSending(5);
        Mockito.verify(source, Mockito.times(1)).appendBatch(Mockito.anyList());
        Mockito.verify(source, Mockito.times(0)).append(Mockito.any(AvroFlumeEvent.class));
    }

    @Test
    public void testLogSingleMessage() {
        configureSource();
        PropertyConfigurator.configure(props);
        Logger logger = LogManager.getLogger(getClass());
        logger.info("test");
        Transaction tx = ch.getTransaction();
        tx.begin();
        Event e = ch.take();
        Assert.assertNotNull(e);
        Assert.assertEquals("test", new String(e.getBody()));
        Mockito.verify(source, Mockito.times(0)).appendBatch(Mockito.anyList());
        Mockito.verify(source, Mockito.times(1)).append(Mockito.any(AvroFlumeEvent.class));
    }

    @Test
    public void testLogSingleMessageInCollection() {
        testBatchedSending(1);
        Mockito.verify(source, Mockito.times(0)).appendBatch(Mockito.anyList());
        Mockito.verify(source, Mockito.times(1)).append(Mockito.any(AvroFlumeEvent.class));
    }

    @Test
    public void testLogEmptyBatch() {
        testBatchedSending(0);
        Mockito.verify(source, Mockito.times(0)).appendBatch(Mockito.anyList());
        Mockito.verify(source, Mockito.times(0)).append(Mockito.any(AvroFlumeEvent.class));
    }

    @Test
    public void testLog4jAppenderFailureUnsafeMode() throws Throwable {
        configureSource();
        props.setProperty("log4j.appender.out2.UnsafeMode", String.valueOf(true));
        PropertyConfigurator.configure(props);
        Logger logger = LogManager.getLogger(TestLog4jAppender.class);
        source.stop();
        sendAndAssertFail(logger);
    }

    @Test(expected = EventDeliveryException.class)
    public void testLog4jAppenderFailureNotUnsafeMode() throws Throwable {
        configureSource();
        PropertyConfigurator.configure(props);
        Logger logger = LogManager.getLogger(TestLog4jAppender.class);
        source.stop();
        sendAndAssertFail(logger);
    }

    @Test
    public void testLayout() throws IOException {
        configureSource();
        props.put("log4j.appender.out2.layout", "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.out2.layout.ConversionPattern", "%-5p [%t]: %m%n");
        PropertyConfigurator.configure(props);
        Logger logger = LogManager.getLogger(TestLog4jAppender.class);
        Thread.currentThread().setName("Log4jAppenderTest");
        for (int count = 0; count <= 100; count++) {
            /* Log4j internally defines levels as multiples of 10000. So if we
            create levels directly using count, the level will be set as the
            default.
             */
            int level = ((count % 5) + 1) * 10000;
            String msg = "This is log message number" + (String.valueOf(count));
            logger.log(Level.toLevel(level), msg);
            Transaction transaction = ch.getTransaction();
            transaction.begin();
            Event event = ch.take();
            Assert.assertNotNull(event);
            StringBuilder builder = new StringBuilder();
            builder.append("[").append("Log4jAppenderTest").append("]: ").append(msg);
            // INFO seems to insert an extra space, so lets split the string.
            String eventBody = new String(event.getBody(), "UTF-8");
            String eventLevel = eventBody.split("\\s+")[0];
            Assert.assertEquals(Level.toLevel(level).toString(), eventLevel);
            Assert.assertEquals(new String(event.getBody(), "UTF8").trim().substring(eventLevel.length()).trim(), builder.toString());
            Map<String, String> hdrs = event.getHeaders();
            Assert.assertNotNull(hdrs.get(TIMESTAMP.toString()));
            Assert.assertEquals(Level.toLevel(level), Level.toLevel(Integer.parseInt(hdrs.get(LOG_LEVEL.toString()))));
            Assert.assertEquals(logger.getName(), hdrs.get(LOGGER_NAME.toString()));
            Assert.assertEquals("UTF8", hdrs.get(MESSAGE_ENCODING.toString()));
            transaction.commit();
            transaction.close();
        }
    }

    @Test(expected = EventDeliveryException.class)
    public void testSlowness() throws Throwable {
        ch = new TestLog4jAppender.SlowMemoryChannel(2000);
        Configurables.configure(ch, new Context());
        configureSource();
        props.put("log4j.appender.out2.Timeout", "1000");
        props.put("log4j.appender.out2.layout", "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.out2.layout.ConversionPattern", "%-5p [%t]: %m%n");
        PropertyConfigurator.configure(props);
        Logger logger = LogManager.getLogger(TestLog4jAppender.class);
        Thread.currentThread().setName("Log4jAppenderTest");
        int level = 10000;
        String msg = "This is log message number" + (String.valueOf(1));
        try {
            logger.log(Level.toLevel(level), msg);
        } catch (FlumeException ex) {
            throw ex.getCause();
        }
    }

    // Should not throw
    @Test
    public void testSlownessUnsafeMode() throws Throwable {
        props.setProperty("log4j.appender.out2.UnsafeMode", String.valueOf(true));
        testSlowness();
    }

    static class SlowMemoryChannel extends MemoryChannel {
        private final int slowTime;

        public SlowMemoryChannel(int slowTime) {
            this.slowTime = slowTime;
        }

        public void put(Event e) {
            try {
                TimeUnit.MILLISECONDS.sleep(slowTime);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            super.put(e);
        }
    }
}

