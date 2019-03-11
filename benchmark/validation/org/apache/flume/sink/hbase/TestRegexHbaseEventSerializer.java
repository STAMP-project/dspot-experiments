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
package org.apache.flume.sink.hbase;


import RegexHbaseEventSerializer.CHARSET_CONFIG;
import RegexHbaseEventSerializer.COLUMN_NAME_DEFAULT;
import RegexHbaseEventSerializer.COL_NAME_CONFIG;
import RegexHbaseEventSerializer.DEPOSIT_HEADERS_CONFIG;
import RegexHbaseEventSerializer.REGEX_CONFIG;
import RegexHbaseEventSerializer.nonce;
import com.google.common.collect.Maps;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static RegexHbaseEventSerializer.randomKey;


public class TestRegexHbaseEventSerializer {
    /**
     * Ensure that when no config is specified, the a catch-all regex is used
     *  with default column name.
     */
    @Test
    public void testDefaultBehavior() throws Exception {
        RegexHbaseEventSerializer s = new RegexHbaseEventSerializer();
        Context context = new Context();
        s.configure(context);
        String logMsg = "The sky is falling!";
        Event e = EventBuilder.withBody(Bytes.toBytes(logMsg));
        s.initialize(e, "CF".getBytes());
        List<Row> actions = s.getActions();
        Assert.assertTrue(((actions.size()) == 1));
        Assert.assertTrue(((actions.get(0)) instanceof Put));
        Put put = ((Put) (actions.get(0)));
        Assert.assertTrue(put.getFamilyMap().containsKey(s.cf));
        List<KeyValue> kvPairs = put.getFamilyMap().get(s.cf);
        Assert.assertTrue(((kvPairs.size()) == 1));
        Map<String, String> resultMap = Maps.newHashMap();
        for (KeyValue kv : kvPairs) {
            resultMap.put(new String(kv.getQualifier()), new String(kv.getValue()));
        }
        Assert.assertTrue(resultMap.containsKey(COLUMN_NAME_DEFAULT));
        Assert.assertEquals("The sky is falling!", resultMap.get(COLUMN_NAME_DEFAULT));
    }

    @Test
    public void testRowIndexKey() throws Exception {
        RegexHbaseEventSerializer s = new RegexHbaseEventSerializer();
        Context context = new Context();
        context.put(REGEX_CONFIG, ("^([^\t]+)\t([^\t]+)\t" + "([^\t]+)$"));
        context.put(COL_NAME_CONFIG, "col1,col2,ROW_KEY");
        context.put("rowKeyIndex", "2");
        s.configure(context);
        String body = "val1\tval2\trow1";
        Event e = EventBuilder.withBody(Bytes.toBytes(body));
        s.initialize(e, "CF".getBytes());
        List<Row> actions = s.getActions();
        Put put = ((Put) (actions.get(0)));
        List<KeyValue> kvPairs = put.getFamilyMap().get(s.cf);
        Assert.assertTrue(((kvPairs.size()) == 2));
        Map<String, String> resultMap = Maps.newHashMap();
        for (KeyValue kv : kvPairs) {
            resultMap.put(new String(kv.getQualifier()), new String(kv.getValue()));
        }
        Assert.assertEquals("val1", resultMap.get("col1"));
        Assert.assertEquals("val2", resultMap.get("col2"));
        Assert.assertEquals("row1", Bytes.toString(put.getRow()));
    }

    /**
     * Test a common case where regex is used to parse apache log format.
     */
    @Test
    public void testApacheRegex() throws Exception {
        RegexHbaseEventSerializer s = new RegexHbaseEventSerializer();
        Context context = new Context();
        context.put(REGEX_CONFIG, ("([^ ]*) ([^ ]*) ([^ ]*) (-|\\[[^\\]]*\\]) \"([^ ]+) ([^ ]+)" + (" ([^\"]+)\" (-|[0-9]*) (-|[0-9]*)(?: ([^ \"]*|\"[^\"]*\")" + " ([^ \"]*|\"[^\"]*\"))?")));
        context.put(COL_NAME_CONFIG, ("host,identity,user,time,method,request,protocol,status,size," + "referer,agent"));
        s.configure(context);
        String logMsg = "33.22.11.00 - - [20/May/2011:07:01:19 +0000] " + (("\"GET /wp-admin/css/install.css HTTP/1.0\" 200 813 " + "\"http://www.cloudera.com/wp-admin/install.php\" \"Mozilla/5.0 (comp") + "atible; Yahoo! Slurp; http://help.yahoo.com/help/us/ysearch/slurp)\"");
        Event e = EventBuilder.withBody(Bytes.toBytes(logMsg));
        s.initialize(e, "CF".getBytes());
        List<Row> actions = s.getActions();
        Assert.assertEquals(1, s.getActions().size());
        Assert.assertTrue(((actions.get(0)) instanceof Put));
        Put put = ((Put) (actions.get(0)));
        Assert.assertTrue(put.getFamilyMap().containsKey(s.cf));
        List<KeyValue> kvPairs = put.getFamilyMap().get(s.cf);
        Assert.assertTrue(((kvPairs.size()) == 11));
        Map<String, String> resultMap = Maps.newHashMap();
        for (KeyValue kv : kvPairs) {
            resultMap.put(new String(kv.getQualifier()), new String(kv.getValue()));
        }
        Assert.assertEquals("33.22.11.00", resultMap.get("host"));
        Assert.assertEquals("-", resultMap.get("identity"));
        Assert.assertEquals("-", resultMap.get("user"));
        Assert.assertEquals("[20/May/2011:07:01:19 +0000]", resultMap.get("time"));
        Assert.assertEquals("GET", resultMap.get("method"));
        Assert.assertEquals("/wp-admin/css/install.css", resultMap.get("request"));
        Assert.assertEquals("HTTP/1.0", resultMap.get("protocol"));
        Assert.assertEquals("200", resultMap.get("status"));
        Assert.assertEquals("813", resultMap.get("size"));
        Assert.assertEquals("\"http://www.cloudera.com/wp-admin/install.php\"", resultMap.get("referer"));
        Assert.assertEquals(("\"Mozilla/5.0 (compatible; Yahoo! Slurp; " + "http://help.yahoo.com/help/us/ysearch/slurp)\""), resultMap.get("agent"));
        List<Increment> increments = s.getIncrements();
        Assert.assertEquals(0, increments.size());
    }

    @Test
    public void testRowKeyGeneration() {
        Context context = new Context();
        RegexHbaseEventSerializer s1 = new RegexHbaseEventSerializer();
        s1.configure(context);
        RegexHbaseEventSerializer s2 = new RegexHbaseEventSerializer();
        s2.configure(context);
        // Reset shared nonce to zero
        nonce.set(0);
        String randomString = randomKey;
        Event e1 = EventBuilder.withBody(Bytes.toBytes("body"));
        Event e2 = EventBuilder.withBody(Bytes.toBytes("body"));
        Event e3 = EventBuilder.withBody(Bytes.toBytes("body"));
        Calendar cal = Mockito.mock(Calendar.class);
        Mockito.when(cal.getTimeInMillis()).thenReturn(1L);
        s1.initialize(e1, "CF".getBytes());
        String rk1 = new String(s1.getRowKey(cal));
        Assert.assertEquals((("1-" + randomString) + "-0"), rk1);
        Mockito.when(cal.getTimeInMillis()).thenReturn(10L);
        s1.initialize(e2, "CF".getBytes());
        String rk2 = new String(s1.getRowKey(cal));
        Assert.assertEquals((("10-" + randomString) + "-1"), rk2);
        Mockito.when(cal.getTimeInMillis()).thenReturn(100L);
        s2.initialize(e3, "CF".getBytes());
        String rk3 = new String(s2.getRowKey(cal));
        Assert.assertEquals((("100-" + randomString) + "-2"), rk3);
    }

    /**
     * Test depositing of the header information.
     */
    @Test
    public void testDepositHeaders() throws Exception {
        Charset charset = Charset.forName("KOI8-R");
        RegexHbaseEventSerializer s = new RegexHbaseEventSerializer();
        Context context = new Context();
        context.put(DEPOSIT_HEADERS_CONFIG, "true");
        context.put(CHARSET_CONFIG, charset.toString());
        s.configure(context);
        String body = "body";
        Map<String, String> headers = Maps.newHashMap();
        headers.put("header1", "value1");
        headers.put("?????????2", "????????2");
        Event e = EventBuilder.withBody(Bytes.toBytes(body), headers);
        s.initialize(e, "CF".getBytes());
        List<Row> actions = s.getActions();
        Assert.assertEquals(1, s.getActions().size());
        Assert.assertTrue(((actions.get(0)) instanceof Put));
        Put put = ((Put) (actions.get(0)));
        Assert.assertTrue(put.getFamilyMap().containsKey(s.cf));
        List<KeyValue> kvPairs = put.getFamilyMap().get(s.cf);
        Assert.assertTrue(((kvPairs.size()) == 3));
        Map<String, byte[]> resultMap = Maps.newHashMap();
        for (KeyValue kv : kvPairs) {
            resultMap.put(new String(kv.getQualifier(), charset), kv.getValue());
        }
        Assert.assertEquals(body, new String(resultMap.get(COLUMN_NAME_DEFAULT), charset));
        Assert.assertEquals("value1", new String(resultMap.get("header1"), charset));
        Assert.assertArrayEquals("????????2".getBytes(charset), resultMap.get("?????????2"));
        Assert.assertEquals("????????2".length(), resultMap.get("?????????2").length);
        List<Increment> increments = s.getIncrements();
        Assert.assertEquals(0, increments.size());
    }
}

