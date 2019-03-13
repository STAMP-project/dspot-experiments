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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.data;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.SerDeUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serdeConstants.LIST_COLUMNS;
import serdeConstants.LIST_COLUMN_TYPES;


public class TestJsonSerDe extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestJsonSerDe.class);

    public void testRW() throws Exception {
        Configuration conf = new Configuration();
        for (Pair<Properties, HCatRecord> e : getData()) {
            Properties tblProps = e.first;
            HCatRecord r = e.second;
            HCatRecordSerDe hrsd = new HCatRecordSerDe();
            SerDeUtils.initializeSerDe(hrsd, conf, tblProps, null);
            JsonSerDe jsde = new JsonSerDe();
            SerDeUtils.initializeSerDe(jsde, conf, tblProps, null);
            TestJsonSerDe.LOG.info("ORIG:{}", r);
            Writable s = hrsd.serialize(r, hrsd.getObjectInspector());
            TestJsonSerDe.LOG.info("ONE:{}", s);
            HCatRecord o1 = ((HCatRecord) (hrsd.deserialize(s)));
            StringBuilder msg = new StringBuilder();
            boolean isEqual = HCatDataCheckUtil.recordsEqual(r, o1);
            TestCase.assertTrue(msg.toString(), isEqual);
            Writable s2 = jsde.serialize(o1, hrsd.getObjectInspector());
            TestJsonSerDe.LOG.info("TWO:{}", s2);
            HCatRecord o2 = ((HCatRecord) (jsde.deserialize(s2)));
            TestJsonSerDe.LOG.info("deserialized TWO : {} ", o2);
            msg.setLength(0);
            isEqual = HCatDataCheckUtil.recordsEqual(r, o2, msg);
            TestCase.assertTrue(msg.toString(), isEqual);
        }
    }

    public void testRobustRead() throws Exception {
        /**
         * This test has been added to account for HCATALOG-436
         *  We write out columns with "internal column names" such
         *  as "_col0", but try to read with regular column names.
         */
        Configuration conf = new Configuration();
        for (Pair<Properties, HCatRecord> e : getData()) {
            Properties tblProps = e.first;
            HCatRecord r = e.second;
            Properties internalTblProps = new Properties();
            for (Map.Entry pe : tblProps.entrySet()) {
                if (!(pe.getKey().equals(LIST_COLUMNS))) {
                    internalTblProps.put(pe.getKey(), pe.getValue());
                } else {
                    internalTblProps.put(pe.getKey(), getInternalNames(((String) (pe.getValue()))));
                }
            }
            TestJsonSerDe.LOG.info("orig tbl props:{}", tblProps);
            TestJsonSerDe.LOG.info("modif tbl props:{}", internalTblProps);
            JsonSerDe wjsd = new JsonSerDe();
            SerDeUtils.initializeSerDe(wjsd, conf, internalTblProps, null);
            JsonSerDe rjsd = new JsonSerDe();
            SerDeUtils.initializeSerDe(rjsd, conf, tblProps, null);
            TestJsonSerDe.LOG.info("ORIG:{}", r);
            Writable s = wjsd.serialize(r, wjsd.getObjectInspector());
            TestJsonSerDe.LOG.info("ONE:{}", s);
            Object o1 = wjsd.deserialize(s);
            TestJsonSerDe.LOG.info("deserialized ONE : {} ", o1);
            Object o2 = rjsd.deserialize(s);
            TestJsonSerDe.LOG.info("deserialized TWO : {} ", o2);
            StringBuilder msg = new StringBuilder();
            boolean isEqual = HCatDataCheckUtil.recordsEqual(r, ((HCatRecord) (o2)), msg);
            TestCase.assertTrue(msg.toString(), isEqual);
        }
    }

    /**
     * This test tests that our json deserialization is not too strict, as per HIVE-6166
     *
     * i.e, if our schema is "s:struct<a:int,b:string>,k:int", and we pass in
     * data that looks like : {
     *                            "x" : "abc" ,
     *                            "t" : {
     *                                "a" : "1",
     *                                "b" : "2",
     *                                "c" : [
     *                                    { "x" : 2 , "y" : 3 } ,
     *                                    { "x" : 3 , "y" : 2 }
     *                                ]
     *                            } ,
     *                            "s" : {
     *                                "a" : 2 ,
     *                                "b" : "blah",
     *                                "c": "woo"
     *                            }
     *                        }
     *
     * Then it should still work, and ignore the "x" and "t" field and "c" subfield of "s", and it
     * should read k as null.
     */
    public void testLooseJsonReadability() throws Exception {
        Configuration conf = new Configuration();
        Properties props = new Properties();
        props.put(LIST_COLUMNS, "s,k");
        props.put(LIST_COLUMN_TYPES, "struct<a:int,b:string>,int");
        JsonSerDe rjsd = new JsonSerDe();
        SerDeUtils.initializeSerDe(rjsd, conf, props, null);
        Text jsonText = new Text(("{ \"x\" : \"abc\" , " + (" \"t\" : { \"a\":\"1\", \"b\":\"2\", \"c\":[ { \"x\":2 , \"y\":3 } , { \"x\":3 , \"y\":2 }] } ," + "\"s\" : { \"a\" : 2 , \"b\" : \"blah\", \"c\": \"woo\" } }")));
        List<Object> expected = new ArrayList<Object>();
        List<Object> inner = new ArrayList<Object>();
        inner.add(2);
        inner.add("blah");
        expected.add(inner);
        expected.add(null);
        HCatRecord expectedRecord = new DefaultHCatRecord(expected);
        HCatRecord r = ((HCatRecord) (rjsd.deserialize(jsonText)));
        System.err.println(("record : " + (r.toString())));
        TestCase.assertTrue(HCatDataCheckUtil.recordsEqual(r, expectedRecord));
    }

    public void testUpperCaseKey() throws Exception {
        Configuration conf = new Configuration();
        Properties props = new Properties();
        props.put(LIST_COLUMNS, "empid,name");
        props.put(LIST_COLUMN_TYPES, "int,string");
        JsonSerDe rjsd = new JsonSerDe();
        SerDeUtils.initializeSerDe(rjsd, conf, props, null);
        Text text1 = new Text("{ \"empId\" : 123, \"name\" : \"John\" } ");
        Text text2 = new Text("{ \"empId\" : 456, \"name\" : \"Jane\" } ");
        HCatRecord expected1 = new DefaultHCatRecord(Arrays.<Object>asList(123, "John"));
        HCatRecord expected2 = new DefaultHCatRecord(Arrays.<Object>asList(456, "Jane"));
        TestCase.assertTrue(HCatDataCheckUtil.recordsEqual(((HCatRecord) (rjsd.deserialize(text1))), expected1));
        TestCase.assertTrue(HCatDataCheckUtil.recordsEqual(((HCatRecord) (rjsd.deserialize(text2))), expected2));
    }

    public void testMapValues() throws Exception {
        Configuration conf = new Configuration();
        Properties props = new Properties();
        props.put(LIST_COLUMNS, "a,b");
        props.put(LIST_COLUMN_TYPES, "array<string>,map<string,int>");
        JsonSerDe rjsd = new JsonSerDe();
        SerDeUtils.initializeSerDe(rjsd, conf, props, null);
        Text text1 = new Text("{ \"a\":[\"aaa\"],\"b\":{\"bbb\":1}} ");
        Text text2 = new Text("{\"a\":[\"yyy\"],\"b\":{\"zzz\":123}}");
        Text text3 = new Text("{\"a\":[\"a\"],\"b\":{\"x\":11, \"y\": 22, \"z\": null}}");
        HCatRecord expected1 = new DefaultHCatRecord(Arrays.<Object>asList(Arrays.<String>asList("aaa"), TestJsonSerDe.createHashMapStringInteger("bbb", 1)));
        HCatRecord expected2 = new DefaultHCatRecord(Arrays.<Object>asList(Arrays.<String>asList("yyy"), TestJsonSerDe.createHashMapStringInteger("zzz", 123)));
        HCatRecord expected3 = new DefaultHCatRecord(Arrays.<Object>asList(Arrays.<String>asList("a"), TestJsonSerDe.createHashMapStringInteger("x", 11, "y", 22, "z", null)));
        TestCase.assertTrue(HCatDataCheckUtil.recordsEqual(((HCatRecord) (rjsd.deserialize(text1))), expected1));
        TestCase.assertTrue(HCatDataCheckUtil.recordsEqual(((HCatRecord) (rjsd.deserialize(text2))), expected2));
    }
}

