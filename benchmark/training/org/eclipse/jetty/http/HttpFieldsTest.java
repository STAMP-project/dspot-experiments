/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.http;


import HttpHeader.ACCEPT;
import HttpHeader.AGE;
import HttpHeader.CONNECTION;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.eclipse.jetty.util.BufferUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static HttpHeader.CONNECTION;
import static HttpHeader.CONTENT_ENCODING;
import static HttpHeader.TRANSFER_ENCODING;
import static HttpHeaderValue.CHUNKED;
import static HttpHeaderValue.GZIP;
import static HttpHeaderValue.KEEP_ALIVE;


public class HttpFieldsTest {
    @Test
    public void testPut() throws Exception {
        HttpFields header = new HttpFields();
        header.put("name0", "value:0");
        header.put("name1", "value1");
        Assertions.assertEquals(2, header.size());
        Assertions.assertEquals("value:0", header.get("name0"));
        Assertions.assertEquals("value1", header.get("name1"));
        Assertions.assertNull(header.get("name2"));
        int matches = 0;
        Enumeration<String> e = header.getFieldNames();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if ("name0".equals(o))
                matches++;

            if ("name1".equals(o))
                matches++;

        } 
        Assertions.assertEquals(2, matches);
        e = header.getValues("name0");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value:0");
        Assertions.assertEquals(false, e.hasMoreElements());
    }

    @Test
    public void testPutTo() throws Exception {
        HttpFields header = new HttpFields();
        header.put("name0", "value0");
        header.put("name1", "value:A");
        header.add("name1", "value:B");
        header.add("name2", "");
        ByteBuffer buffer = BufferUtil.allocate(1024);
        BufferUtil.flipToFill(buffer);
        HttpGenerator.putTo(header, buffer);
        BufferUtil.flipToFlush(buffer, 0);
        String result = BufferUtil.toString(buffer);
        MatcherAssert.assertThat(result, Matchers.containsString("name0: value0"));
        MatcherAssert.assertThat(result, Matchers.containsString("name1: value:A"));
        MatcherAssert.assertThat(result, Matchers.containsString("name1: value:B"));
    }

    @Test
    public void testGet() throws Exception {
        HttpFields header = new HttpFields();
        header.put("name0", "value0");
        header.put("name1", "value1");
        Assertions.assertEquals("value0", header.get("name0"));
        Assertions.assertEquals("value0", header.get("Name0"));
        Assertions.assertEquals("value1", header.get("name1"));
        Assertions.assertEquals("value1", header.get("Name1"));
        Assertions.assertEquals(null, header.get("Name2"));
        Assertions.assertEquals("value0", header.getField("name0").getValue());
        Assertions.assertEquals("value0", header.getField("Name0").getValue());
        Assertions.assertEquals("value1", header.getField("name1").getValue());
        Assertions.assertEquals("value1", header.getField("Name1").getValue());
        Assertions.assertEquals(null, header.getField("Name2"));
        Assertions.assertEquals("value0", header.getField(0).getValue());
        Assertions.assertEquals("value1", header.getField(1).getValue());
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            header.getField(2);
        });
    }

    @Test
    public void testGetKnown() throws Exception {
        HttpFields header = new HttpFields();
        header.put("Connection", "value0");
        header.put(ACCEPT, "value1");
        Assertions.assertEquals("value0", header.get(CONNECTION));
        Assertions.assertEquals("value1", header.get(ACCEPT));
        Assertions.assertEquals("value0", header.getField(CONNECTION).getValue());
        Assertions.assertEquals("value1", header.getField(ACCEPT).getValue());
        Assertions.assertEquals(null, header.getField(AGE));
        Assertions.assertEquals(null, header.get(AGE));
    }

    @Test
    public void testCRLF() throws Exception {
        HttpFields header = new HttpFields();
        header.put("name0", "value\r\n0");
        header.put("name\r\n1", "value1");
        header.put("name:2", "value:\r\n2");
        ByteBuffer buffer = BufferUtil.allocate(1024);
        BufferUtil.flipToFill(buffer);
        HttpGenerator.putTo(header, buffer);
        BufferUtil.flipToFlush(buffer, 0);
        String out = BufferUtil.toString(buffer);
        MatcherAssert.assertThat(out, Matchers.containsString("name0: value  0"));
        MatcherAssert.assertThat(out, Matchers.containsString("name??1: value1"));
        MatcherAssert.assertThat(out, Matchers.containsString("name?2: value:  2"));
    }

    @Test
    public void testCachedPut() throws Exception {
        HttpFields header = new HttpFields();
        header.put("Connection", "Keep-Alive");
        header.put("tRansfer-EncOding", "CHUNKED");
        header.put("CONTENT-ENCODING", "gZIP");
        ByteBuffer buffer = BufferUtil.allocate(1024);
        BufferUtil.flipToFill(buffer);
        HttpGenerator.putTo(header, buffer);
        BufferUtil.flipToFlush(buffer, 0);
        String out = BufferUtil.toString(buffer).toLowerCase(Locale.ENGLISH);
        MatcherAssert.assertThat(out, Matchers.containsString((((CONNECTION) + ": ") + (KEEP_ALIVE)).toLowerCase(Locale.ENGLISH)));
        MatcherAssert.assertThat(out, Matchers.containsString((((TRANSFER_ENCODING) + ": ") + (CHUNKED)).toLowerCase(Locale.ENGLISH)));
        MatcherAssert.assertThat(out, Matchers.containsString((((CONTENT_ENCODING) + ": ") + (GZIP)).toLowerCase(Locale.ENGLISH)));
    }

    @Test
    public void testRePut() throws Exception {
        HttpFields header = new HttpFields();
        header.put("name0", "value0");
        header.put("name1", "xxxxxx");
        header.put("name2", "value2");
        Assertions.assertEquals("value0", header.get("name0"));
        Assertions.assertEquals("xxxxxx", header.get("name1"));
        Assertions.assertEquals("value2", header.get("name2"));
        header.put("name1", "value1");
        Assertions.assertEquals("value0", header.get("name0"));
        Assertions.assertEquals("value1", header.get("name1"));
        Assertions.assertEquals("value2", header.get("name2"));
        Assertions.assertNull(header.get("name3"));
        int matches = 0;
        Enumeration<String> e = header.getFieldNames();
        while (e.hasMoreElements()) {
            String o = e.nextElement();
            if ("name0".equals(o))
                matches++;

            if ("name1".equals(o))
                matches++;

            if ("name2".equals(o))
                matches++;

        } 
        Assertions.assertEquals(3, matches);
        e = header.getValues("name1");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value1");
        Assertions.assertEquals(false, e.hasMoreElements());
    }

    @Test
    public void testRemovePut() throws Exception {
        HttpFields header = new HttpFields(1);
        header.put("name0", "value0");
        header.put("name1", "value1");
        header.put("name2", "value2");
        Assertions.assertEquals("value0", header.get("name0"));
        Assertions.assertEquals("value1", header.get("name1"));
        Assertions.assertEquals("value2", header.get("name2"));
        header.remove("name1");
        Assertions.assertEquals("value0", header.get("name0"));
        Assertions.assertNull(header.get("name1"));
        Assertions.assertEquals("value2", header.get("name2"));
        Assertions.assertNull(header.get("name3"));
        int matches = 0;
        Enumeration<String> e = header.getFieldNames();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if ("name0".equals(o))
                matches++;

            if ("name1".equals(o))
                matches++;

            if ("name2".equals(o))
                matches++;

        } 
        Assertions.assertEquals(2, matches);
        e = header.getValues("name1");
        Assertions.assertEquals(false, e.hasMoreElements());
    }

    @Test
    public void testAdd() throws Exception {
        HttpFields fields = new HttpFields();
        fields.add("name0", "value0");
        fields.add("name1", "valueA");
        fields.add("name2", "value2");
        Assertions.assertEquals("value0", fields.get("name0"));
        Assertions.assertEquals("valueA", fields.get("name1"));
        Assertions.assertEquals("value2", fields.get("name2"));
        fields.add("name1", "valueB");
        Assertions.assertEquals("value0", fields.get("name0"));
        Assertions.assertEquals("valueA", fields.get("name1"));
        Assertions.assertEquals("value2", fields.get("name2"));
        Assertions.assertNull(fields.get("name3"));
        int matches = 0;
        Enumeration<String> e = fields.getFieldNames();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if ("name0".equals(o))
                matches++;

            if ("name1".equals(o))
                matches++;

            if ("name2".equals(o))
                matches++;

        } 
        Assertions.assertEquals(3, matches);
        e = fields.getValues("name1");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "valueA");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "valueB");
        Assertions.assertEquals(false, e.hasMoreElements());
    }

    @Test
    public void testGetValues() throws Exception {
        HttpFields fields = new HttpFields();
        fields.put("name0", "value0A,value0B");
        fields.add("name0", "value0C,value0D");
        fields.put("name1", "value1A, \"value\t, 1B\" ");
        fields.add("name1", "\"value1C\",\tvalue1D");
        Enumeration<String> e = fields.getValues("name0");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0A,value0B");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0C,value0D");
        Assertions.assertEquals(false, e.hasMoreElements());
        e = fields.getValues("name0", ",");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0A");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0B");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0C");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0D");
        Assertions.assertEquals(false, e.hasMoreElements());
        e = fields.getValues("name1", ",");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value1A");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value\t, 1B");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value1C");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value1D");
        Assertions.assertEquals(false, e.hasMoreElements());
    }

    @Test
    public void testGetCSV() throws Exception {
        HttpFields fields = new HttpFields();
        fields.put("name0", "value0A,value0B");
        fields.add("name0", "value0C,value0D");
        fields.put("name1", "value1A, \"value\t, 1B\" ");
        fields.add("name1", "\"value1C\",\tvalue1D");
        Enumeration<String> e = fields.getValues("name0");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0A,value0B");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0C,value0D");
        Assertions.assertEquals(false, e.hasMoreElements());
        e = Collections.enumeration(fields.getCSV("name0", false));
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0A");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0B");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0C");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value0D");
        Assertions.assertEquals(false, e.hasMoreElements());
        e = Collections.enumeration(fields.getCSV("name1", false));
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value1A");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value\t, 1B");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value1C");
        Assertions.assertEquals(true, e.hasMoreElements());
        Assertions.assertEquals(e.nextElement(), "value1D");
        Assertions.assertEquals(false, e.hasMoreElements());
    }

    @Test
    public void testAddQuotedCSV() throws Exception {
        HttpFields fields = new HttpFields();
        fields.put("some", "value");
        fields.add("name", "\"zero\"");
        fields.add("name", "one, \"1 + 1\"");
        fields.put("other", "value");
        fields.add("name", "three");
        fields.add("name", "four, I V");
        List<String> list = fields.getCSV("name", false);
        Assertions.assertEquals(HttpFields.valueParameters(list.get(0), null), "zero");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(1), null), "one");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(2), null), "1 + 1");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(3), null), "three");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(4), null), "four");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(5), null), "I V");
        fields.addCSV("name", "six");
        list = fields.getCSV("name", false);
        Assertions.assertEquals(HttpFields.valueParameters(list.get(0), null), "zero");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(1), null), "one");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(2), null), "1 + 1");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(3), null), "three");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(4), null), "four");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(5), null), "I V");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(6), null), "six");
        fields.addCSV("name", "1 + 1", "7", "zero");
        list = fields.getCSV("name", false);
        Assertions.assertEquals(HttpFields.valueParameters(list.get(0), null), "zero");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(1), null), "one");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(2), null), "1 + 1");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(3), null), "three");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(4), null), "four");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(5), null), "I V");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(6), null), "six");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(7), null), "7");
    }

    @Test
    public void testGetQualityCSV() throws Exception {
        HttpFields fields = new HttpFields();
        fields.put("some", "value");
        fields.add("name", "zero;q=0.9,four;q=0.1");
        fields.put("other", "value");
        fields.add("name", "nothing;q=0");
        fields.add("name", "one;q=0.4");
        fields.add("name", "three;x=y;q=0.2;a=b,two;q=0.3");
        fields.add("name", "first;");
        List<String> list = fields.getQualityCSV("name");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(0), null), "first");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(1), null), "zero");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(2), null), "one");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(3), null), "two");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(4), null), "three");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(5), null), "four");
    }

    @Test
    public void testGetQualityCSVHeader() throws Exception {
        HttpFields fields = new HttpFields();
        fields.put("some", "value");
        fields.add("Accept", "zero;q=0.9,four;q=0.1");
        fields.put("other", "value");
        fields.add("Accept", "nothing;q=0");
        fields.add("Accept", "one;q=0.4");
        fields.add("Accept", "three;x=y;q=0.2;a=b,two;q=0.3");
        fields.add("Accept", "first;");
        List<String> list = fields.getQualityCSV(ACCEPT);
        Assertions.assertEquals(HttpFields.valueParameters(list.get(0), null), "first");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(1), null), "zero");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(2), null), "one");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(3), null), "two");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(4), null), "three");
        Assertions.assertEquals(HttpFields.valueParameters(list.get(5), null), "four");
    }

    @Test
    public void testDateFields() throws Exception {
        HttpFields fields = new HttpFields();
        fields.put("D0", "Wed, 31 Dec 1969 23:59:59 GMT");
        fields.put("D1", "Fri, 31 Dec 1999 23:59:59 GMT");
        fields.put("D2", "Friday, 31-Dec-99 23:59:59 GMT");
        fields.put("D3", "Fri Dec 31 23:59:59 1999");
        fields.put("D4", "Mon Jan 1 2000 00:00:01");
        fields.put("D5", "Tue Feb 29 2000 12:00:00");
        long d1 = fields.getDateField("D1");
        long d0 = fields.getDateField("D0");
        long d2 = fields.getDateField("D2");
        long d3 = fields.getDateField("D3");
        long d4 = fields.getDateField("D4");
        long d5 = fields.getDateField("D5");
        Assertions.assertTrue((d0 != (-1)));
        Assertions.assertTrue((d1 > 0));
        Assertions.assertTrue((d2 > 0));
        Assertions.assertEquals(d1, d2);
        Assertions.assertEquals(d2, d3);
        Assertions.assertEquals((d3 + 2000), d4);
        Assertions.assertEquals(951825600000L, d5);
        d1 = fields.getDateField("D1");
        d2 = fields.getDateField("D2");
        d3 = fields.getDateField("D3");
        d4 = fields.getDateField("D4");
        d5 = fields.getDateField("D5");
        Assertions.assertTrue((d1 > 0));
        Assertions.assertTrue((d2 > 0));
        Assertions.assertEquals(d1, d2);
        Assertions.assertEquals(d2, d3);
        Assertions.assertEquals((d3 + 2000), d4);
        Assertions.assertEquals(951825600000L, d5);
        fields.putDateField("D2", d1);
        Assertions.assertEquals("Fri, 31 Dec 1999 23:59:59 GMT", fields.get("D2"));
    }

    @Test
    public void testNegDateFields() throws Exception {
        HttpFields fields = new HttpFields();
        fields.putDateField("Dzero", 0);
        Assertions.assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", fields.get("Dzero"));
        fields.putDateField("Dminus", (-1));
        Assertions.assertEquals("Wed, 31 Dec 1969 23:59:59 GMT", fields.get("Dminus"));
        fields.putDateField("Dminus", (-1000));
        Assertions.assertEquals("Wed, 31 Dec 1969 23:59:59 GMT", fields.get("Dminus"));
        fields.putDateField("Dancient", Long.MIN_VALUE);
        Assertions.assertEquals("Sun, 02 Dec 55 16:47:04 GMT", fields.get("Dancient"));
    }

    @Test
    public void testLongFields() throws Exception {
        HttpFields header = new HttpFields();
        header.put("I1", "42");
        header.put("I2", " 43 99");
        header.put("I3", "-44");
        header.put("I4", " - 45abc");
        header.put("N1", " - ");
        header.put("N2", "xx");
        long i1 = header.getLongField("I1");
        try {
            header.getLongField("I2");
            Assertions.assertTrue(false);
        } catch (NumberFormatException e) {
            Assertions.assertTrue(true);
        }
        long i3 = header.getLongField("I3");
        try {
            header.getLongField("I4");
            Assertions.assertTrue(false);
        } catch (NumberFormatException e) {
            Assertions.assertTrue(true);
        }
        try {
            header.getLongField("N1");
            Assertions.assertTrue(false);
        } catch (NumberFormatException e) {
            Assertions.assertTrue(true);
        }
        try {
            header.getLongField("N2");
            Assertions.assertTrue(false);
        } catch (NumberFormatException e) {
            Assertions.assertTrue(true);
        }
        Assertions.assertEquals(42, i1);
        Assertions.assertEquals((-44), i3);
        header.putLongField("I5", 46);
        header.putLongField("I6", (-47));
        Assertions.assertEquals("46", header.get("I5"));
        Assertions.assertEquals("-47", header.get("I6"));
    }

    @Test
    public void testContains() throws Exception {
        HttpFields header = new HttpFields();
        header.add("n0", "");
        header.add("n1", ",");
        header.add("n2", ",,");
        header.add("N3", "abc");
        header.add("N4", "def");
        header.add("n5", "abc,def,hig");
        header.add("N6", "abc");
        header.add("n6", "def");
        header.add("N6", "hig");
        header.add("n7", "abc ,  def;q=0.9  ,  hig");
        header.add("n8", "abc ,  def;q=0  ,  hig");
        header.add(ACCEPT, "abc ,  def;q=0  ,  hig");
        for (int i = 0; i < 8; i++) {
            Assertions.assertTrue(header.containsKey(("n" + i)));
            Assertions.assertTrue(header.containsKey(("N" + i)));
            Assertions.assertFalse(header.contains(("n" + i), "xyz"), ("" + i));
            Assertions.assertEquals((i >= 4), header.contains(("n" + i), "def"), ("" + i));
        }
        Assertions.assertTrue(header.contains(new HttpField("N5", "def")));
        Assertions.assertTrue(header.contains(new HttpField("accept", "abc")));
        Assertions.assertTrue(header.contains(ACCEPT, "abc"));
        Assertions.assertFalse(header.contains(new HttpField("N5", "xyz")));
        Assertions.assertFalse(header.contains(new HttpField("N8", "def")));
        Assertions.assertFalse(header.contains(ACCEPT, "def"));
        Assertions.assertFalse(header.contains(AGE, "abc"));
        Assertions.assertFalse(header.containsKey("n11"));
    }

    @Test
    public void testIteration() throws Exception {
        HttpFields header = new HttpFields();
        Iterator<HttpField> i = header.iterator();
        MatcherAssert.assertThat(i.hasNext(), Matchers.is(false));
        header.put("name1", "valueA");
        header.put("name2", "valueB");
        header.add("name3", "valueC");
        i = header.iterator();
        MatcherAssert.assertThat(i.hasNext(), Matchers.is(true));
        MatcherAssert.assertThat(i.next().getName(), Matchers.is("name1"));
        MatcherAssert.assertThat(i.next().getName(), Matchers.is("name2"));
        i.remove();
        MatcherAssert.assertThat(i.next().getName(), Matchers.is("name3"));
        MatcherAssert.assertThat(i.hasNext(), Matchers.is(false));
        i = header.iterator();
        MatcherAssert.assertThat(i.hasNext(), Matchers.is(true));
        MatcherAssert.assertThat(i.next().getName(), Matchers.is("name1"));
        MatcherAssert.assertThat(i.next().getName(), Matchers.is("name3"));
        MatcherAssert.assertThat(i.hasNext(), Matchers.is(false));
        ListIterator<HttpField> l = header.listIterator();
        MatcherAssert.assertThat(l.hasNext(), Matchers.is(true));
        l.add(new HttpField("name0", "value"));
        MatcherAssert.assertThat(l.hasNext(), Matchers.is(true));
        MatcherAssert.assertThat(l.next().getName(), Matchers.is("name1"));
        l.set(new HttpField("NAME1", "value"));
        MatcherAssert.assertThat(l.hasNext(), Matchers.is(true));
        MatcherAssert.assertThat(l.hasPrevious(), Matchers.is(true));
        MatcherAssert.assertThat(l.previous().getName(), Matchers.is("NAME1"));
        MatcherAssert.assertThat(l.hasNext(), Matchers.is(true));
        MatcherAssert.assertThat(l.hasPrevious(), Matchers.is(true));
        MatcherAssert.assertThat(l.previous().getName(), Matchers.is("name0"));
        MatcherAssert.assertThat(l.hasNext(), Matchers.is(true));
        MatcherAssert.assertThat(l.hasPrevious(), Matchers.is(false));
        MatcherAssert.assertThat(l.next().getName(), Matchers.is("name0"));
        MatcherAssert.assertThat(l.hasNext(), Matchers.is(true));
        MatcherAssert.assertThat(l.hasPrevious(), Matchers.is(true));
        MatcherAssert.assertThat(l.next().getName(), Matchers.is("NAME1"));
        l.add(new HttpField("name2", "value"));
        MatcherAssert.assertThat(l.next().getName(), Matchers.is("name3"));
        MatcherAssert.assertThat(l.hasNext(), Matchers.is(false));
        MatcherAssert.assertThat(l.hasPrevious(), Matchers.is(true));
        l.add(new HttpField("name4", "value"));
        MatcherAssert.assertThat(l.hasNext(), Matchers.is(false));
        MatcherAssert.assertThat(l.hasPrevious(), Matchers.is(true));
        MatcherAssert.assertThat(l.previous().getName(), Matchers.is("name4"));
        i = header.iterator();
        MatcherAssert.assertThat(i.hasNext(), Matchers.is(true));
        MatcherAssert.assertThat(i.next().getName(), Matchers.is("name0"));
        MatcherAssert.assertThat(i.next().getName(), Matchers.is("NAME1"));
        MatcherAssert.assertThat(i.next().getName(), Matchers.is("name2"));
        MatcherAssert.assertThat(i.next().getName(), Matchers.is("name3"));
        MatcherAssert.assertThat(i.next().getName(), Matchers.is("name4"));
        MatcherAssert.assertThat(i.hasNext(), Matchers.is(false));
    }
}

