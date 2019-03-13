/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.syslog;


import Syslog5424Attributes.APP_NAME;
import Syslog5424Attributes.MESSAGEID;
import Syslog5424Attributes.PROCID;
import Syslog5424Attributes.STRUCTURED_BASE;
import SyslogAttributes.BODY;
import SyslogAttributes.FACILITY;
import SyslogAttributes.HOSTNAME;
import SyslogAttributes.PRIORITY;
import SyslogAttributes.SEVERITY;
import SyslogAttributes.TIMESTAMP;
import SyslogAttributes.VERSION;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.syslog.keyproviders.SimpleKeyProvider;
import org.apache.nifi.syslog.parsers.StrictSyslog5424Parser;
import org.apache.nifi.syslog.utils.NifiStructuredDataPolicy;
import org.apache.nifi.syslog.utils.NilHandlingPolicy;
import org.junit.Assert;
import org.junit.Test;


public class TestSyslog5424RecordReader {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static final String expectedVersion = "1";

    private static final String expectedMessage = "Removing instance";

    private static final String expectedAppName = "d0602076-b14a-4c55-852a-981e7afeed38";

    private static final String expectedHostName = "loggregator";

    private static final String expectedPri = "14";

    private static final String expectedProcId = "DEA";

    private static final Timestamp expectedTimestamp = Timestamp.from(Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse("2014-06-20T09:14:07+00:00")));

    private static final String expectedMessageId = "MSG-01";

    private static final String expectedFacility = "1";

    private static final String expectedSeverity = "6";

    private static final String expectedIUT1 = "3";

    private static final String expectedIUT2 = "4";

    private static final String expectedEventSource1 = "Application";

    private static final String expectedEventSource2 = "Other Application";

    private static final String expectedEventID1 = "1011";

    private static final String expectedEventID2 = "2022";

    @Test
    @SuppressWarnings("unchecked")
    public void testParseSingleLine() throws IOException, MalformedRecordException {
        try (final InputStream fis = new FileInputStream(new File("src/test/resources/syslog/syslog5424/log_all.txt"))) {
            StrictSyslog5424Parser parser = new StrictSyslog5424Parser(TestSyslog5424RecordReader.CHARSET, NilHandlingPolicy.NULL, NifiStructuredDataPolicy.MAP_OF_MAPS, new SimpleKeyProvider());
            final Syslog5424RecordReader deserializer = new Syslog5424RecordReader(parser, fis, Syslog5424Reader.createRecordSchema());
            final Record record = deserializer.nextRecord();
            Assert.assertNotNull(record.getValues());
            Assert.assertEquals(TestSyslog5424RecordReader.expectedVersion, record.getAsString(VERSION.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedMessage, record.getAsString(BODY.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedAppName, record.getAsString(APP_NAME.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedHostName, record.getAsString(HOSTNAME.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedPri, record.getAsString(PRIORITY.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedSeverity, record.getAsString(SEVERITY.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedFacility, record.getAsString(FACILITY.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedProcId, record.getAsString(PROCID.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedTimestamp, ((Timestamp) (record.getValue(TIMESTAMP.key()))));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedMessageId, record.getAsString(MESSAGEID.key()));
            Assert.assertNotNull(record.getValue(STRUCTURED_BASE.key()));
            Map<String, Object> structured = ((Map<String, Object>) (record.getValue(STRUCTURED_BASE.key())));
            Assert.assertTrue(structured.containsKey("exampleSDID@32473"));
            Map<String, Object> example1 = ((Map<String, Object>) (structured.get("exampleSDID@32473")));
            Assert.assertTrue(example1.containsKey("iut"));
            Assert.assertTrue(example1.containsKey("eventSource"));
            Assert.assertTrue(example1.containsKey("eventID"));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedIUT1, example1.get("iut").toString());
            Assert.assertEquals(TestSyslog5424RecordReader.expectedEventSource1, example1.get("eventSource").toString());
            Assert.assertEquals(TestSyslog5424RecordReader.expectedEventID1, example1.get("eventID").toString());
            Assert.assertTrue(structured.containsKey("exampleSDID@32480"));
            Map<String, Object> example2 = ((Map<String, Object>) (structured.get("exampleSDID@32480")));
            Assert.assertTrue(example2.containsKey("iut"));
            Assert.assertTrue(example2.containsKey("eventSource"));
            Assert.assertTrue(example2.containsKey("eventID"));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedIUT2, example2.get("iut").toString());
            Assert.assertEquals(TestSyslog5424RecordReader.expectedEventSource2, example2.get("eventSource").toString());
            Assert.assertEquals(TestSyslog5424RecordReader.expectedEventID2, example2.get("eventID").toString());
            Assert.assertNull(deserializer.nextRecord());
            deserializer.close();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testParseSingleLineSomeNulls() throws IOException, MalformedRecordException {
        try (final InputStream fis = new FileInputStream(new File("src/test/resources/syslog/syslog5424/log.txt"))) {
            StrictSyslog5424Parser parser = new StrictSyslog5424Parser(TestSyslog5424RecordReader.CHARSET, NilHandlingPolicy.NULL, NifiStructuredDataPolicy.MAP_OF_MAPS, new SimpleKeyProvider());
            final Syslog5424RecordReader deserializer = new Syslog5424RecordReader(parser, fis, Syslog5424Reader.createRecordSchema());
            final Record record = deserializer.nextRecord();
            Assert.assertNotNull(record.getValues());
            Assert.assertEquals(TestSyslog5424RecordReader.expectedVersion, record.getAsString(VERSION.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedMessage, record.getAsString(BODY.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedAppName, record.getAsString(APP_NAME.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedHostName, record.getAsString(HOSTNAME.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedPri, record.getAsString(PRIORITY.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedSeverity, record.getAsString(SEVERITY.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedFacility, record.getAsString(FACILITY.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedProcId, record.getAsString(PROCID.key()));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedTimestamp, ((Timestamp) (record.getValue(TIMESTAMP.key()))));
            Assert.assertNull(record.getAsString(MESSAGEID.key()));
            Assert.assertNotNull(record.getValue(STRUCTURED_BASE.key()));
            Map<String, Object> structured = ((Map<String, Object>) (record.getValue(STRUCTURED_BASE.key())));
            Assert.assertTrue(structured.containsKey("exampleSDID@32473"));
            Map<String, Object> example1 = ((Map<String, Object>) (structured.get("exampleSDID@32473")));
            Assert.assertTrue(example1.containsKey("iut"));
            Assert.assertTrue(example1.containsKey("eventSource"));
            Assert.assertTrue(example1.containsKey("eventID"));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedIUT1, example1.get("iut").toString());
            Assert.assertEquals(TestSyslog5424RecordReader.expectedEventSource1, example1.get("eventSource").toString());
            Assert.assertEquals(TestSyslog5424RecordReader.expectedEventID1, example1.get("eventID").toString());
            Assert.assertTrue(structured.containsKey("exampleSDID@32480"));
            Map<String, Object> example2 = ((Map<String, Object>) (structured.get("exampleSDID@32480")));
            Assert.assertTrue(example2.containsKey("iut"));
            Assert.assertTrue(example2.containsKey("eventSource"));
            Assert.assertTrue(example2.containsKey("eventID"));
            Assert.assertEquals(TestSyslog5424RecordReader.expectedIUT2, example2.get("iut").toString());
            Assert.assertEquals(TestSyslog5424RecordReader.expectedEventSource2, example2.get("eventSource").toString());
            Assert.assertEquals(TestSyslog5424RecordReader.expectedEventID2, example2.get("eventID").toString());
            Assert.assertNull(deserializer.nextRecord());
            deserializer.close();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testParseMultipleLine() throws IOException, MalformedRecordException {
        try (final InputStream fis = new FileInputStream(new File("src/test/resources/syslog/syslog5424/log_mix.txt"))) {
            StrictSyslog5424Parser parser = new StrictSyslog5424Parser(TestSyslog5424RecordReader.CHARSET, NilHandlingPolicy.NULL, NifiStructuredDataPolicy.MAP_OF_MAPS, new SimpleKeyProvider());
            final Syslog5424RecordReader deserializer = new Syslog5424RecordReader(parser, fis, Syslog5424Reader.createRecordSchema());
            Record record = deserializer.nextRecord();
            int count = 0;
            while (record != null) {
                Assert.assertNotNull(record.getValues());
                count++;
                record = deserializer.nextRecord();
            } 
            Assert.assertEquals(count, 3);
            deserializer.close();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testParseMultipleLineWithError() throws IOException, MalformedRecordException {
        try (final InputStream fis = new FileInputStream(new File("src/test/resources/syslog/syslog5424/log_mix_in_error.txt"))) {
            StrictSyslog5424Parser parser = new StrictSyslog5424Parser(TestSyslog5424RecordReader.CHARSET, NilHandlingPolicy.NULL, NifiStructuredDataPolicy.MAP_OF_MAPS, new SimpleKeyProvider());
            final Syslog5424RecordReader deserializer = new Syslog5424RecordReader(parser, fis, Syslog5424Reader.createRecordSchema());
            Record record = deserializer.nextRecord();
            int count = 0;
            int exceptionCount = 0;
            while (record != null) {
                Assert.assertNotNull(record.getValues());
                try {
                    record = deserializer.nextRecord();
                    count++;
                } catch (Exception e) {
                    exceptionCount++;
                }
            } 
            Assert.assertEquals(count, 3);
            Assert.assertEquals(exceptionCount, 1);
            deserializer.close();
        }
    }
}

