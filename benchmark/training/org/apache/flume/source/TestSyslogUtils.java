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
package org.apache.flume.source;


import SyslogUtils.EVENT_STATUS;
import SyslogUtils.SYSLOG_FACILITY;
import SyslogUtils.SYSLOG_SEVERITY;
import SyslogUtils.SyslogStatus.INCOMPLETE;
import SyslogUtils.SyslogStatus.INVALID;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import org.apache.flume.Event;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Assert;
import org.junit.Test;


public class TestSyslogUtils {
    @Test
    public void TestHeader0() throws ParseException {
        String stamp1 = "2012-04-13T11:11:11";
        String format1 = "yyyy-MM-dd'T'HH:mm:ssZ";
        String host1 = "ubuntu-11.cloudera.com";
        String data1 = "some msg";
        // timestamp with hh:mm format timezone with no version
        String msg1 = (((((("<10>" + stamp1) + "+08:00") + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, (stamp1 + "+0800"), format1, host1, data1);
    }

    @Test
    public void TestHeader1() throws ParseException {
        String stamp1 = "2012-04-13T11:11:11";
        String format1 = "yyyy-MM-dd'T'HH:mm:ss";
        String host1 = "ubuntu-11.cloudera.com";
        String data1 = "some msg";
        String msg1 = ((((("<10>1 " + stamp1) + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, stamp1, format1, host1, data1);
    }

    @Test
    public void TestHeader2() throws ParseException {
        String stamp1 = "2012-04-13T11:11:11";
        String format1 = "yyyy-MM-dd'T'HH:mm:ssZ";
        String host1 = "ubuntu-11.cloudera.com";
        String data1 = "some msg";
        // timestamp with 'Z' appended, translates to UTC
        String msg1 = (((((("<10>1 " + stamp1) + "Z") + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, (stamp1 + "+0000"), format1, host1, data1);
    }

    @Test
    public void TestHeader3() throws ParseException {
        String stamp1 = "2012-04-13T11:11:11";
        String format1 = "yyyy-MM-dd'T'HH:mm:ssZ";
        String host1 = "ubuntu-11.cloudera.com";
        String data1 = "some msg";
        // timestamp with hh:mm format timezone
        String msg1 = (((((("<10>1 " + stamp1) + "+08:00") + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, (stamp1 + "+0800"), format1, host1, data1);
    }

    @Test
    public void TestHeader4() throws ParseException {
        String host1 = "ubuntu-11.cloudera.com";
        String data1 = "some msg";
        // null format timestamp (-)
        String msg1 = (((("<10>1 " + ("-" + " ")) + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, null, null, host1, data1);
    }

    @Test
    public void TestHeader5() throws ParseException {
        String stamp1 = "2012-04-13T11:11:11";
        String format1 = "yyyy-MM-dd'T'HH:mm:ss";
        String host1 = "-";
        String data1 = "some msg";
        // null host
        String msg1 = ((((("<10>1 " + stamp1) + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, stamp1, format1, null, data1);
    }

    @Test
    public void TestHeader6() throws ParseException {
        String stamp1 = "2012-04-13T11:11:11";
        String format1 = "yyyy-MM-dd'T'HH:mm:ssZ";
        String host1 = "-";
        String data1 = "some msg";
        // null host
        String msg1 = (((((("<10>1 " + stamp1) + "Z") + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, (stamp1 + "+0000"), format1, null, data1);
    }

    @Test
    public void TestHeader7() throws ParseException {
        String stamp1 = "2012-04-13T11:11:11";
        String format1 = "yyyy-MM-dd'T'HH:mm:ssZ";
        String host1 = "-";
        String data1 = "some msg";
        // null host
        String msg1 = (((((("<10>1 " + stamp1) + "+08:00") + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, (stamp1 + "+0800"), format1, null, data1);
    }

    @Test
    public void TestHeader8() throws ParseException {
        String stamp1 = "2012-04-13T11:11:11.999";
        String format1 = "yyyy-MM-dd'T'HH:mm:ss.S";
        String host1 = "ubuntu-11.cloudera.com";
        String data1 = "some msg";
        String msg1 = ((((("<10>1 " + stamp1) + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, stamp1, format1, host1, data1);
    }

    @Test
    public void TestHeader9() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM  d hh:MM:ss", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String stamp1 = sdf.format(cal.getTime());
        String format1 = "yyyyMMM d HH:mm:ss";
        String host1 = "ubuntu-11.cloudera.com";
        String data1 = "some msg";
        // timestamp with 'Z' appended, translates to UTC
        String msg1 = ((((("<10>" + stamp1) + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, (year + stamp1), format1, host1, data1);
    }

    @Test
    public void TestHeader10() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM  d hh:MM:ss", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String stamp1 = sdf.format(cal.getTime());
        String format1 = "yyyyMMM d HH:mm:ss";
        String host1 = "ubuntu-11.cloudera.com";
        String data1 = "some msg";
        // timestamp with 'Z' appended, translates to UTC
        String msg1 = ((((("<10>" + stamp1) + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, (year + stamp1), format1, host1, data1);
    }

    @Test
    public void TestHeader11() throws ParseException {
        // SyslogUtils should truncate microsecond precision to only 3 digits.
        // This is to maintain consistency between the two syslog implementations.
        String inputStamp = "2014-10-03T17:20:01.123456-07:00";
        String outputStamp = "2014-10-03T17:20:01.123-07:00";
        String format1 = "yyyy-MM-dd'T'HH:mm:ss.S";
        String host1 = "ubuntu-11.cloudera.com";
        String data1 = "some msg";
        String msg1 = ((((("<10>" + inputStamp) + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, outputStamp, format1, host1, data1);
    }

    @Test
    public void TestRfc3164HeaderApacheLogWithNulls() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM  d hh:MM:ss", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String stamp1 = sdf.format(cal.getTime());
        String format1 = "yyyyMMM d HH:mm:ss";
        String host1 = "ubuntu-11.cloudera.com";
        String data1 = "- hyphen_null_breaks_5424_pattern [07/Jun/2012:14:46:44 -0600]";
        String msg1 = ((((("<10>" + stamp1) + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader(msg1, (year + stamp1), format1, host1, data1);
    }

    /* This test creates a series of dates that range from 10 months in the past to (5 days short
    of) one month in the future. This tests that the year addition code is clever enough to
    handle scenarios where the event received was generated in a different year to what flume
    considers to be "current" (e.g. where there has been some lag somewhere, especially when
    flicking over on New Year's eve, or when you are about to flick over and the flume's
    system clock is slightly slower than the Syslog source's clock).
     */
    @Test
    public void TestRfc3164Dates() throws ParseException {
        // We're going to run this test using a mocked clock, once for the next 13 months
        for (int monthOffset = 0; monthOffset <= 13; monthOffset++) {
            Clock mockClock = Clock.fixed(LocalDateTime.now().plusMonths(monthOffset).toInstant(ZoneOffset.UTC), Clock.systemDefaultZone().getZone());
            // We're then going to try input dates (without the year) for all 12 months, starting
            // 10 months ago, and finishing next month (all relative to our mocked clock)
            for (int i = -10; i <= 1; i++) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM  d hh:MM:ss", Locale.ENGLISH);
                Date date = new Date(mockClock.millis());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.MONTH, i);
                // Small tweak to avoid the 1 month in the future ticking over by a few seconds between now
                // and when the checkHeader actually runs
                if (i == 1) {
                    cal.add(Calendar.DAY_OF_MONTH, (-1));
                }
                String stamp1 = sdf.format(cal.getTime());
                String year = String.valueOf(cal.get(Calendar.YEAR));
                String format1 = "yyyyMMM d HH:mm:ss";
                String host1 = "ubuntu-11.cloudera.com";
                String data1 = "some msg";
                // timestamp with 'Z' appended, translates to UTC
                String msg1 = ((((("<10>" + stamp1) + " ") + host1) + " ") + data1) + "\n";
                TestSyslogUtils.checkHeader(msg1, (year + stamp1), format1, host1, data1, mockClock);
            }
        }
    }

    /**
     * Test bad event format 1: Priority is not numeric
     */
    @Test
    public void testExtractBadEvent1() {
        String badData1 = "<10F> bad bad data\n";
        SyslogUtils util = new SyslogUtils(false);
        ChannelBuffer buff = ChannelBuffers.buffer(100);
        buff.writeBytes(badData1.getBytes());
        Event e = util.extractEvent(buff);
        if (e == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers = e.getHeaders();
        Assert.assertEquals("0", headers.get(SYSLOG_FACILITY));
        Assert.assertEquals("0", headers.get(SYSLOG_SEVERITY));
        Assert.assertEquals(INVALID.getSyslogStatus(), headers.get(EVENT_STATUS));
        Assert.assertEquals(badData1.trim(), new String(e.getBody()).trim());
    }

    /**
     * Test bad event format 2: The first char is not <
     */
    @Test
    public void testExtractBadEvent2() {
        String badData1 = "hi guys! <10> bad bad data\n";
        SyslogUtils util = new SyslogUtils(false);
        ChannelBuffer buff = ChannelBuffers.buffer(100);
        buff.writeBytes(badData1.getBytes());
        Event e = util.extractEvent(buff);
        if (e == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers = e.getHeaders();
        Assert.assertEquals("0", headers.get(SYSLOG_FACILITY));
        Assert.assertEquals("0", headers.get(SYSLOG_SEVERITY));
        Assert.assertEquals(INVALID.getSyslogStatus(), headers.get(EVENT_STATUS));
        Assert.assertEquals(badData1.trim(), new String(e.getBody()).trim());
    }

    /**
     * Test bad event format 3: Empty priority - <>
     */
    @Test
    public void testExtractBadEvent3() {
        String badData1 = "<> bad bad data\n";
        SyslogUtils util = new SyslogUtils(false);
        ChannelBuffer buff = ChannelBuffers.buffer(100);
        buff.writeBytes(badData1.getBytes());
        Event e = util.extractEvent(buff);
        if (e == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers = e.getHeaders();
        Assert.assertEquals("0", headers.get(SYSLOG_FACILITY));
        Assert.assertEquals("0", headers.get(SYSLOG_SEVERITY));
        Assert.assertEquals(INVALID.getSyslogStatus(), headers.get(EVENT_STATUS));
        Assert.assertEquals(badData1.trim(), new String(e.getBody()).trim());
    }

    /**
     * Test bad event format 4: Priority too long
     */
    @Test
    public void testExtractBadEvent4() {
        String badData1 = "<123123123123123123123123123123> bad bad data\n";
        SyslogUtils util = new SyslogUtils(false);
        ChannelBuffer buff = ChannelBuffers.buffer(100);
        buff.writeBytes(badData1.getBytes());
        Event e = util.extractEvent(buff);
        if (e == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers = e.getHeaders();
        Assert.assertEquals("0", headers.get(SYSLOG_FACILITY));
        Assert.assertEquals("0", headers.get(SYSLOG_SEVERITY));
        Assert.assertEquals(INVALID.getSyslogStatus(), headers.get(EVENT_STATUS));
        Assert.assertEquals(badData1.trim(), new String(e.getBody()).trim());
    }

    /**
     * Good event
     */
    @Test
    public void testExtractGoodEvent() {
        String priority = "<10>";
        String goodData1 = "Good good good data\n";
        SyslogUtils util = new SyslogUtils(false);
        ChannelBuffer buff = ChannelBuffers.buffer(100);
        buff.writeBytes((priority + goodData1).getBytes());
        Event e = util.extractEvent(buff);
        if (e == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers = e.getHeaders();
        Assert.assertEquals("1", headers.get(SYSLOG_FACILITY));
        Assert.assertEquals("2", headers.get(SYSLOG_SEVERITY));
        Assert.assertEquals(null, headers.get(EVENT_STATUS));
        Assert.assertEquals((priority + (goodData1.trim())), new String(e.getBody()).trim());
    }

    /**
     * Bad event immediately followed by a good event
     */
    @Test
    public void testBadEventGoodEvent() {
        String badData1 = "hi guys! <10F> bad bad data\n";
        SyslogUtils util = new SyslogUtils(false);
        ChannelBuffer buff = ChannelBuffers.buffer(100);
        buff.writeBytes(badData1.getBytes());
        String priority = "<10>";
        String goodData1 = "Good good good data\n";
        buff.writeBytes((priority + goodData1).getBytes());
        Event e = util.extractEvent(buff);
        if (e == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers = e.getHeaders();
        Assert.assertEquals("0", headers.get(SYSLOG_FACILITY));
        Assert.assertEquals("0", headers.get(SYSLOG_SEVERITY));
        Assert.assertEquals(INVALID.getSyslogStatus(), headers.get(EVENT_STATUS));
        Assert.assertEquals(badData1.trim(), new String(e.getBody()).trim());
        Event e2 = util.extractEvent(buff);
        if (e2 == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers2 = e2.getHeaders();
        Assert.assertEquals("1", headers2.get(SYSLOG_FACILITY));
        Assert.assertEquals("2", headers2.get(SYSLOG_SEVERITY));
        Assert.assertEquals(null, headers2.get(EVENT_STATUS));
        Assert.assertEquals((priority + (goodData1.trim())), new String(e2.getBody()).trim());
    }

    @Test
    public void testGoodEventBadEvent() {
        String badData1 = "hi guys! <10F> bad bad data\n";
        String priority = "<10>";
        String goodData1 = "Good good good data\n";
        SyslogUtils util = new SyslogUtils(false);
        ChannelBuffer buff = ChannelBuffers.buffer(100);
        buff.writeBytes((priority + goodData1).getBytes());
        buff.writeBytes(badData1.getBytes());
        Event e2 = util.extractEvent(buff);
        if (e2 == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers2 = e2.getHeaders();
        Assert.assertEquals("1", headers2.get(SYSLOG_FACILITY));
        Assert.assertEquals("2", headers2.get(SYSLOG_SEVERITY));
        Assert.assertEquals(null, headers2.get(EVENT_STATUS));
        Assert.assertEquals((priority + (goodData1.trim())), new String(e2.getBody()).trim());
        Event e = util.extractEvent(buff);
        if (e == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers = e.getHeaders();
        Assert.assertEquals("0", headers.get(SYSLOG_FACILITY));
        Assert.assertEquals("0", headers.get(SYSLOG_SEVERITY));
        Assert.assertEquals(INVALID.getSyslogStatus(), headers.get(EVENT_STATUS));
        Assert.assertEquals(badData1.trim(), new String(e.getBody()).trim());
    }

    @Test
    public void testBadEventBadEvent() {
        String badData1 = "hi guys! <10F> bad bad data\n";
        SyslogUtils util = new SyslogUtils(false);
        ChannelBuffer buff = ChannelBuffers.buffer(100);
        buff.writeBytes(badData1.getBytes());
        String badData2 = "hi guys! <20> bad bad data\n";
        buff.writeBytes(badData2.getBytes());
        Event e = util.extractEvent(buff);
        if (e == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers = e.getHeaders();
        Assert.assertEquals("0", headers.get(SYSLOG_FACILITY));
        Assert.assertEquals("0", headers.get(SYSLOG_SEVERITY));
        Assert.assertEquals(INVALID.getSyslogStatus(), headers.get(EVENT_STATUS));
        Assert.assertEquals(badData1.trim(), new String(e.getBody()).trim());
        Event e2 = util.extractEvent(buff);
        if (e2 == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers2 = e2.getHeaders();
        Assert.assertEquals("0", headers2.get(SYSLOG_FACILITY));
        Assert.assertEquals("0", headers2.get(SYSLOG_SEVERITY));
        Assert.assertEquals(INVALID.getSyslogStatus(), headers2.get(EVENT_STATUS));
        Assert.assertEquals(badData2.trim(), new String(e2.getBody()).trim());
    }

    @Test
    public void testGoodEventGoodEvent() {
        String priority = "<10>";
        String goodData1 = "Good good good data\n";
        SyslogUtils util = new SyslogUtils(false);
        ChannelBuffer buff = ChannelBuffers.buffer(100);
        buff.writeBytes((priority + goodData1).getBytes());
        String priority2 = "<20>";
        String goodData2 = "Good really good data\n";
        buff.writeBytes((priority2 + goodData2).getBytes());
        Event e = util.extractEvent(buff);
        if (e == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers = e.getHeaders();
        Assert.assertEquals("1", headers.get(SYSLOG_FACILITY));
        Assert.assertEquals("2", headers.get(SYSLOG_SEVERITY));
        Assert.assertEquals(null, headers.get(EVENT_STATUS));
        Assert.assertEquals((priority + (goodData1.trim())), new String(e.getBody()).trim());
        Event e2 = util.extractEvent(buff);
        if (e2 == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers2 = e2.getHeaders();
        Assert.assertEquals("2", headers2.get(SYSLOG_FACILITY));
        Assert.assertEquals("4", headers2.get(SYSLOG_SEVERITY));
        Assert.assertEquals(null, headers.get(EVENT_STATUS));
        Assert.assertEquals((priority2 + (goodData2.trim())), new String(e2.getBody()).trim());
    }

    @Test
    public void testExtractBadEventLarge() {
        String badData1 = "<10> bad bad data bad bad\n";
        // The minimum size (which is 10) overrides the 5 specified here.
        SyslogUtils util = new SyslogUtils(5, null, false);
        ChannelBuffer buff = ChannelBuffers.buffer(100);
        buff.writeBytes(badData1.getBytes());
        Event e = util.extractEvent(buff);
        if (e == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers = e.getHeaders();
        Assert.assertEquals("1", headers.get(SYSLOG_FACILITY));
        Assert.assertEquals("2", headers.get(SYSLOG_SEVERITY));
        Assert.assertEquals(INCOMPLETE.getSyslogStatus(), headers.get(EVENT_STATUS));
        Assert.assertEquals("<10> bad b".trim(), new String(e.getBody()).trim());
        Event e2 = util.extractEvent(buff);
        if (e2 == null) {
            throw new NullPointerException("Event is null");
        }
        Map<String, String> headers2 = e2.getHeaders();
        Assert.assertEquals("0", headers2.get(SYSLOG_FACILITY));
        Assert.assertEquals("0", headers2.get(SYSLOG_SEVERITY));
        Assert.assertEquals(INVALID.getSyslogStatus(), headers2.get(EVENT_STATUS));
        Assert.assertEquals("ad data ba".trim(), new String(e2.getBody()).trim());
    }

    @Test
    public void testKeepFields() throws Exception {
        String stamp1 = "2012-04-13T11:11:11";
        String format1 = "yyyy-MM-dd'T'HH:mm:ssZ";
        String host1 = "ubuntu-11.cloudera.com";
        String data1 = "some msg";
        // timestamp with hh:mm format timezone
        String msg1 = (((((("<10>1 " + stamp1) + "+08:00") + " ") + host1) + " ") + data1) + "\n";
        TestSyslogUtils.checkHeader("none", msg1, (stamp1 + "+0800"), format1, host1, data1);
        TestSyslogUtils.checkHeader("false", msg1, (stamp1 + "+0800"), format1, host1, data1);
        String data2 = "ubuntu-11.cloudera.com some msg";
        TestSyslogUtils.checkHeader("hostname", msg1, (stamp1 + "+0800"), format1, host1, data2);
        String data3 = "2012-04-13T11:11:11+08:00 ubuntu-11.cloudera.com some msg";
        TestSyslogUtils.checkHeader("timestamp hostname", msg1, (stamp1 + "+0800"), format1, host1, data3);
        String data4 = "<10>2012-04-13T11:11:11+08:00 ubuntu-11.cloudera.com some msg";
        TestSyslogUtils.checkHeader("priority timestamp hostname", msg1, (stamp1 + "+0800"), format1, host1, data4);
        String data5 = "<10>1 2012-04-13T11:11:11+08:00 ubuntu-11.cloudera.com some msg";
        TestSyslogUtils.checkHeader("priority version timestamp hostname", msg1, (stamp1 + "+0800"), format1, host1, data5);
        TestSyslogUtils.checkHeader("all", msg1, (stamp1 + "+0800"), format1, host1, data5);
        TestSyslogUtils.checkHeader("true", msg1, (stamp1 + "+0800"), format1, host1, data5);
    }

    @Test
    public void testGetIPWhenSuccessful() {
        SocketAddress socketAddress = new InetSocketAddress("localhost", 2000);
        String ip = SyslogUtils.getIP(socketAddress);
        Assert.assertEquals("127.0.0.1", ip);
    }

    @Test
    public void testGetIPWhenInputIsNull() {
        SocketAddress socketAddress = null;
        String ip = SyslogUtils.getIP(socketAddress);
        Assert.assertEquals("", ip);
    }

    @Test
    public void testGetIPWhenInputIsNotInetSocketAddress() {
        SocketAddress socketAddress = new SocketAddress() {};
        String ip = SyslogUtils.getIP(socketAddress);
        Assert.assertEquals("", ip);
    }

    @Test
    public void testGetHostnameWhenSuccessful() {
        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 2000);
        String hostname = SyslogUtils.getHostname(socketAddress);
        Assert.assertEquals("localhost", hostname);
    }

    @Test
    public void testGetHostnameWhenInputIsNull() {
        SocketAddress socketAddress = null;
        String hostname = SyslogUtils.getHostname(socketAddress);
        Assert.assertEquals("", hostname);
    }

    @Test
    public void testGetHostnameWhenInputIsNotInetSocketAddress() {
        SocketAddress socketAddress = new SocketAddress() {};
        String hostname = SyslogUtils.getHostname(socketAddress);
        Assert.assertEquals("", hostname);
    }
}

