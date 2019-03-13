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


import Syslog5424Attributes.SYSLOG_APP_NAME;
import Syslog5424Attributes.SYSLOG_MESSAGEID;
import Syslog5424Attributes.SYSLOG_PROCID;
import SyslogAttributes.SYSLOG_BODY;
import SyslogAttributes.SYSLOG_FACILITY;
import SyslogAttributes.SYSLOG_HOSTNAME;
import SyslogAttributes.SYSLOG_PRIORITY;
import SyslogAttributes.SYSLOG_SEVERITY;
import SyslogAttributes.SYSLOG_TIMESTAMP;
import SyslogAttributes.SYSLOG_VERSION;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import org.apache.nifi.syslog.events.Syslog5424Event;
import org.apache.nifi.syslog.keyproviders.SyslogPrefixedKeyProvider;
import org.apache.nifi.syslog.parsers.StrictSyslog5424Parser;
import org.junit.Assert;
import org.junit.Test;


public abstract class BaseStrictSyslog5424ParserTest {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static final String NIL_VALUE = "-";

    private StrictSyslog5424Parser parser;

    @Test
    public void testRFC5424WithVersion() {
        final String pri = "34";
        final String version = "1";
        final String stamp = "2003-10-11T22:14:15.003Z";
        final String host = "mymachine.example.com";
        final String appName = "su";
        final String procId = "-";
        final String msgId = "ID17";
        final String structuredData = "-";
        final String body = "BOM'su root' failed for lonvick on /dev/pts/8";
        final String message = (((((((((((((((("<" + pri) + ">") + version) + " ") + stamp) + " ") + host) + " ") + appName) + " ") + procId) + " ") + msgId) + " ") + "-") + " ") + body;
        final byte[] bytes = message.getBytes(BaseStrictSyslog5424ParserTest.CHARSET);
        final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.clear();
        buffer.put(bytes);
        final Syslog5424Event event = parser.parseEvent(buffer);
        Assert.assertNotNull(event);
        Assert.assertTrue(event.isValid());
        Assert.assertFalse(event.getFieldMap().isEmpty());
        Map<String, Object> fieldMap = event.getFieldMap();
        Assert.assertEquals(pri, fieldMap.get(SYSLOG_PRIORITY.key()));
        Assert.assertEquals("2", fieldMap.get(SYSLOG_SEVERITY.key()));
        Assert.assertEquals("4", fieldMap.get(SYSLOG_FACILITY.key()));
        Assert.assertEquals(version, fieldMap.get(SYSLOG_VERSION.key()));
        Assert.assertEquals(stamp, fieldMap.get(SYSLOG_TIMESTAMP.key()));
        Assert.assertEquals(host, fieldMap.get(SYSLOG_HOSTNAME.key()));
        Assert.assertEquals(appName, fieldMap.get(SYSLOG_APP_NAME.key()));
        validateForPolicy(procId, fieldMap.get(SYSLOG_PROCID.key()));
        Assert.assertEquals(msgId, fieldMap.get(SYSLOG_MESSAGEID.key()));
        Pattern structuredPattern = new SyslogPrefixedKeyProvider().getStructuredElementIdParamNamePattern();
        fieldMap.forEach(( key, value) -> {
            if (value != null) {
                Assert.assertFalse(structuredPattern.matcher(key).matches());
            }
        });
        Assert.assertEquals(body, fieldMap.get(SYSLOG_BODY.key()));
        Assert.assertEquals(message, event.getFullMessage());
        Assert.assertNull(event.getSender());
    }

    @Test
    public void testRFC5424WithoutVersion() {
        final String pri = "34";
        final String version = "-";
        final String stamp = "2003-10-11T22:14:15.003Z";
        final String host = "mymachine.example.com";
        final String appName = "su";
        final String procId = "-";
        final String msgId = "ID17";
        final String structuredData = "-";
        final String body = "BOM'su root' failed for lonvick on /dev/pts/8";
        final String message = (((((((((((((((("<" + pri) + ">") + version) + " ") + stamp) + " ") + host) + " ") + appName) + " ") + procId) + " ") + msgId) + " ") + "-") + " ") + body;
        final byte[] bytes = message.getBytes(BaseStrictSyslog5424ParserTest.CHARSET);
        final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.clear();
        buffer.put(bytes);
        final Syslog5424Event event = parser.parseEvent(buffer);
        Assert.assertFalse(event.isValid());
    }

    @Test
    public void testTrailingNewLine() {
        final String message = "<34>1 2003-10-11T22:14:15.003Z mymachine.example.com su - " + "ID47 - BOM\'su root\' failed for lonvick on /dev/pts/8\n";
        final byte[] bytes = message.getBytes(BaseStrictSyslog5424ParserTest.CHARSET);
        final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.clear();
        buffer.put(bytes);
        final Syslog5424Event event = parser.parseEvent(buffer);
        Assert.assertNotNull(event);
        Assert.assertTrue(event.isValid());
    }

    @Test
    public void testVariety() {
        final List<String> messages = new ArrayList<>();
        // supported examples from RFC 5424 including structured data with no message
        messages.add(("<34>1 2003-10-11T22:14:15.003Z mymachine.example.com su - " + "ID47 - BOM'su root' failed for lonvick on /dev/pts/8"));
        messages.add(("<165>1 2003-08-24T05:14:15.000003-07:00 192.0.2.1 myproc " + "8710 - - %% It's time to make the do-nuts."));
        messages.add(("<14>1 2014-06-20T09:14:07+00:00 loggregator" + ((" d0602076-b14a-4c55-852a-981e7afeed38 DEA MSG-01" + " [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"]") + " [exampleSDID@32480 iut=\"4\" eventSource=\"Other Application\" eventID=\"2022\"] Removing instance")));
        for (final String message : messages) {
            final byte[] bytes = message.getBytes(BaseStrictSyslog5424ParserTest.CHARSET);
            final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.clear();
            buffer.put(bytes);
            final Syslog5424Event event = parser.parseEvent(buffer);
            Assert.assertTrue(event.isValid());
        }
    }

    @Test
    public void testMessagePartNotRequired() {
        final List<String> messages = new ArrayList<>();
        messages.add(("<14>1 2014-06-20T09:14:07+00:00 loggregator" + (" d0602076-b14a-4c55-852a-981e7afeed38 DEA MSG-01" + " [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"]")));
        messages.add(("<14>1 2014-06-20T09:14:07+00:00 loggregator" + ((" d0602076-b14a-4c55-852a-981e7afeed38 DEA MSG-01" + " [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"]") + " [exampleSDID@32480 iut=\"4\" eventSource=\"Other Application\" eventID=\"2022\"]")));
        for (final String message : messages) {
            final byte[] bytes = message.getBytes(BaseStrictSyslog5424ParserTest.CHARSET);
            final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.clear();
            buffer.put(bytes);
            final Syslog5424Event event = parser.parseEvent(buffer);
            Assert.assertTrue(event.isValid());
            Assert.assertNull(event.getFieldMap().get(SYSLOG_BODY));
        }
    }

    @Test
    public void testInvalidPriority() {
        final String message = "10 Oct 13 14:14:43 localhost some body of the message";
        final byte[] bytes = message.getBytes(BaseStrictSyslog5424ParserTest.CHARSET);
        final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.clear();
        buffer.put(bytes);
        final Syslog5424Event event = parser.parseEvent(buffer);
        Assert.assertNotNull(event);
        Assert.assertFalse(event.isValid());
        Assert.assertEquals(message, event.getFullMessage());
    }

    @Test
    public void testParseWithSender() {
        final String sender = "127.0.0.1";
        final String message = "<14>1 2014-06-20T09:14:07+00:00 loggregator" + ((" d0602076-b14a-4c55-852a-981e7afeed38 DEA MSG-01" + " [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"]") + " [exampleSDID@32480 iut=\"4\" eventSource=\"Other Application\" eventID=\"2022\"] Removing instance");
        final byte[] bytes = message.getBytes(BaseStrictSyslog5424ParserTest.CHARSET);
        final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.clear();
        buffer.put(bytes);
        final Syslog5424Event event = parser.parseEvent(buffer, sender);
        Assert.assertNotNull(event);
        Assert.assertTrue(event.isValid());
        Assert.assertEquals(sender, event.getSender());
    }
}

