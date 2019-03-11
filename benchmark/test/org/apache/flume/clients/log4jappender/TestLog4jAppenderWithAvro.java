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


import Log4jAvroHeaders.AVRO_SCHEMA_LITERAL;
import Log4jAvroHeaders.AVRO_SCHEMA_URL;
import Log4jAvroHeaders.MESSAGE_ENCODING;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import junit.framework.Assert;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.flume.Channel;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.apache.flume.source.AvroSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;


public class TestLog4jAppenderWithAvro {
    private AvroSource source;

    private Channel ch;

    private Properties props;

    private int port;

    @Test
    public void testAvroGeneric() throws IOException {
        loadProperties("flume-log4jtest-avro-generic.properties");
        props.put("log4j.appender.out2.Port", String.valueOf(port));
        PropertyConfigurator.configure(props);
        Logger logger = LogManager.getLogger(TestLog4jAppenderWithAvro.class);
        String msg = "This is log message number " + (String.valueOf(0));
        Schema schema = new Schema.Parser().parse(getClass().getClassLoader().getResource("myrecord.avsc").openStream());
        GenericRecordBuilder builder = new GenericRecordBuilder(schema);
        GenericRecord record = builder.set("message", msg).build();
        logger.info(record);
        Transaction transaction = ch.getTransaction();
        transaction.begin();
        Event event = ch.take();
        Assert.assertNotNull(event);
        GenericDatumReader<GenericRecord> reader = new GenericDatumReader(schema);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(event.getBody(), null);
        GenericRecord recordFromEvent = reader.read(null, decoder);
        Assert.assertEquals(msg, recordFromEvent.get("message").toString());
        Map<String, String> hdrs = event.getHeaders();
        Assert.assertNull(hdrs.get(MESSAGE_ENCODING.toString()));
        Assert.assertEquals("Schema URL should be set", "file:///tmp/myrecord.avsc", hdrs.get(AVRO_SCHEMA_URL.toString()));
        Assert.assertNull("Schema string should not be set", hdrs.get(AVRO_SCHEMA_LITERAL.toString()));
        transaction.commit();
        transaction.close();
    }

    @Test
    public void testAvroReflect() throws IOException {
        loadProperties("flume-log4jtest-avro-reflect.properties");
        props.put("log4j.appender.out2.Port", String.valueOf(port));
        PropertyConfigurator.configure(props);
        Logger logger = LogManager.getLogger(TestLog4jAppenderWithAvro.class);
        String msg = "This is log message number " + (String.valueOf(0));
        TestLog4jAppenderWithAvro.AppEvent appEvent = new TestLog4jAppenderWithAvro.AppEvent();
        appEvent.setMessage(msg);
        logger.info(appEvent);
        Transaction transaction = ch.getTransaction();
        transaction.begin();
        Event event = ch.take();
        Assert.assertNotNull(event);
        Schema schema = ReflectData.get().getSchema(appEvent.getClass());
        ReflectDatumReader<TestLog4jAppenderWithAvro.AppEvent> reader = new ReflectDatumReader(TestLog4jAppenderWithAvro.AppEvent.class);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(event.getBody(), null);
        TestLog4jAppenderWithAvro.AppEvent recordFromEvent = reader.read(null, decoder);
        Assert.assertEquals(msg, recordFromEvent.getMessage());
        Map<String, String> hdrs = event.getHeaders();
        Assert.assertNull(hdrs.get(MESSAGE_ENCODING.toString()));
        Assert.assertNull("Schema URL should not be set", hdrs.get(AVRO_SCHEMA_URL.toString()));
        Assert.assertEquals("Schema string should be set", schema.toString(), hdrs.get(AVRO_SCHEMA_LITERAL.toString()));
        transaction.commit();
        transaction.close();
    }

    @Test
    public void testDifferentEventTypesInBatchWithAvroReflect() throws IOException {
        loadProperties("flume-log4jtest-avro-reflect.properties");
        props.put("log4j.appender.out2.Port", String.valueOf(port));
        PropertyConfigurator.configure(props);
        Logger logger = LogManager.getLogger(getClass());
        List<Object> events = Arrays.asList("string", new TestLog4jAppenderWithAvro.AppEvent("appEvent"));
        logger.info(events);
        Transaction transaction = ch.getTransaction();
        transaction.begin();
        for (Object o : events) {
            Event e = ch.take();
            Assert.assertNotNull(e);
            ReflectDatumReader<?> reader = new ReflectDatumReader(o.getClass());
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(e.getBody(), null);
            Object readObject = reader.read(null, decoder);
            Assert.assertEquals(o, readObject);
            Map<String, String> hdrs = e.getHeaders();
            Assert.assertNull(hdrs.get(MESSAGE_ENCODING.toString()));
            Assert.assertNull("Schema URL should not be set", hdrs.get(AVRO_SCHEMA_URL.toString()));
            Assert.assertEquals("Schema string should be set", ReflectData.get().getSchema(readObject.getClass()).toString(), hdrs.get(AVRO_SCHEMA_LITERAL.toString()));
        }
        Assert.assertNull("There should be no more events in the channel", ch.take());
    }

    @Test
    public void testDifferentEventTypesInBatchWithAvroGeneric() throws IOException {
        loadProperties("flume-log4jtest-avro-generic.properties");
        props.put("log4j.appender.out2.Port", String.valueOf(port));
        PropertyConfigurator.configure(props);
        Logger logger = LogManager.getLogger(getClass());
        String msg = "Avro log message";
        Schema schema = new Schema.Parser().parse(getClass().getClassLoader().getResource("myrecord.avsc").openStream());
        GenericRecordBuilder builder = new GenericRecordBuilder(schema);
        GenericRecord record = builder.set("message", msg).build();
        List<Object> events = Arrays.asList("string", record);
        logger.info(events);
        Transaction transaction = ch.getTransaction();
        transaction.begin();
        Event event = ch.take();
        Assert.assertNotNull(event);
        Assert.assertEquals("string", new String(event.getBody()));
        event = ch.take();
        Assert.assertNotNull(event);
        GenericDatumReader<GenericRecord> reader = new GenericDatumReader(schema);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(event.getBody(), null);
        GenericRecord recordFromEvent = reader.read(null, decoder);
        Assert.assertEquals(msg, recordFromEvent.get("message").toString());
        Map<String, String> hdrs = event.getHeaders();
        Assert.assertNull(hdrs.get(MESSAGE_ENCODING.toString()));
        Assert.assertEquals("Schema URL should be set", "file:///tmp/myrecord.avsc", hdrs.get(AVRO_SCHEMA_URL.toString()));
        Assert.assertNull("Schema string should not be set", hdrs.get(AVRO_SCHEMA_LITERAL.toString()));
        transaction.commit();
        transaction.close();
    }

    public static class AppEvent {
        private String message;

        public AppEvent() {
        }

        public AppEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            TestLog4jAppenderWithAvro.AppEvent appEvent = ((TestLog4jAppenderWithAvro.AppEvent) (o));
            return (message) != null ? message.equals(appEvent.message) : (appEvent.message) == null;
        }

        @Override
        public int hashCode() {
            return (message) != null ? message.hashCode() : 0;
        }
    }
}

