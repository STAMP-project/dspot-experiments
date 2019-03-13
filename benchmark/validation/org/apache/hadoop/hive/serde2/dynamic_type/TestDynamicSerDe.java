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
package org.apache.hadoop.hive.serde2.dynamic_type;


import TCTLSeparatedProtocol.ReturnNullsKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.api.hive_metastoreConstants.META_TABLE_NAME;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.thrift.TBinarySortableProtocol;
import org.apache.hadoop.hive.serde2.thrift.TCTLSeparatedProtocol;
import org.apache.hadoop.io.BytesWritable;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import serdeConstants.COLLECTION_DELIM;
import serdeConstants.FIELD_DELIM;
import serdeConstants.LINE_DELIM;
import serdeConstants.MAPKEY_DELIM;
import serdeConstants.SERIALIZATION_DDL;
import serdeConstants.SERIALIZATION_FORMAT;
import serdeConstants.SERIALIZATION_LIB;


/**
 * TestDynamicSerDe.
 */
public class TestDynamicSerDe extends TestCase {
    public void testDynamicSerDe() throws Throwable {
        try {
            // Try to construct an object
            ArrayList<String> bye = new ArrayList<String>();
            bye.add("firstString");
            bye.add("secondString");
            HashMap<String, Integer> another = new HashMap<String, Integer>();
            another.put("firstKey", 1);
            another.put("secondKey", 2);
            ArrayList<Object> struct = new ArrayList<Object>();
            struct.add(Integer.valueOf(234));
            struct.add(bye);
            struct.add(another);
            struct.add(Integer.valueOf((-234)));
            struct.add(Double.valueOf(1.0));
            struct.add(Double.valueOf((-2.5)));
            // All protocols
            ArrayList<String> protocols = new ArrayList<String>();
            ArrayList<Boolean> isBinaries = new ArrayList<Boolean>();
            ArrayList<HashMap<String, String>> additionalParams = new ArrayList<HashMap<String, String>>();
            protocols.add(TBinarySortableProtocol.class.getName());
            isBinaries.add(true);
            additionalParams.add(TestDynamicSerDe.makeHashMap("serialization.sort.order", "++++++"));
            protocols.add(TBinarySortableProtocol.class.getName());
            isBinaries.add(true);
            additionalParams.add(TestDynamicSerDe.makeHashMap("serialization.sort.order", "------"));
            protocols.add(TBinaryProtocol.class.getName());
            isBinaries.add(true);
            additionalParams.add(null);
            protocols.add(TJSONProtocol.class.getName());
            isBinaries.add(false);
            additionalParams.add(null);
            // TSimpleJSONProtocol does not support deserialization.
            // protocols.add(org.apache.thrift.protocol.TSimpleJSONProtocol.class.getName());
            // isBinaries.add(false);
            // additionalParams.add(null);
            // TCTLSeparatedProtocol is not done yet.
            protocols.add(TCTLSeparatedProtocol.class.getName());
            isBinaries.add(false);
            additionalParams.add(null);
            System.out.println(("input struct = " + struct));
            for (int pp = 0; pp < (protocols.size()); pp++) {
                String protocol = protocols.get(pp);
                boolean isBinary = isBinaries.get(pp);
                System.out.println(("Testing protocol: " + protocol));
                Properties schema = new Properties();
                schema.setProperty(SERIALIZATION_FORMAT, protocol);
                schema.setProperty(META_TABLE_NAME, "test");
                schema.setProperty(SERIALIZATION_DDL, "struct test { i32 _hello, list<string> 2bye, map<string,i32> another, i32 nhello, double d, double nd}");
                schema.setProperty(SERIALIZATION_LIB, new DynamicSerDe().getClass().toString());
                HashMap<String, String> p = additionalParams.get(pp);
                if (p != null) {
                    for (Map.Entry<String, String> e : p.entrySet()) {
                        schema.setProperty(e.getKey(), e.getValue());
                    }
                }
                DynamicSerDe serde = new DynamicSerDe();
                serde.initialize(new Configuration(), schema);
                // Try getObjectInspector
                ObjectInspector oi = serde.getObjectInspector();
                System.out.println(("TypeName = " + (oi.getTypeName())));
                // Try to serialize
                BytesWritable bytes = ((BytesWritable) (serde.serialize(struct, oi)));
                System.out.println(("bytes =" + (hexString(bytes))));
                if (!isBinary) {
                    System.out.println(("bytes in text =" + (new String(bytes.get(), 0, bytes.getSize()))));
                }
                // Try to deserialize
                Object o = serde.deserialize(bytes);
                System.out.println(("o class = " + (o.getClass())));
                List<?> olist = ((List<?>) (o));
                System.out.println(("o size = " + (olist.size())));
                System.out.println(("o[0] class = " + (olist.get(0).getClass())));
                System.out.println(("o[1] class = " + (olist.get(1).getClass())));
                System.out.println(("o[2] class = " + (olist.get(2).getClass())));
                System.out.println(("o = " + o));
                TestCase.assertEquals(struct, o);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void testTBinarySortableProtocol() throws Throwable {
        try {
            System.out.println("Beginning Test testTBinarySortableProtocol:");
            int num = 100;
            Random r = new Random(1234);
            Object[] structs = new Object[num];
            String ddl;
            // Test double
            for (int i = 0; i < num; i++) {
                ArrayList<Object> struct = new ArrayList<Object>();
                if (i == 0) {
                    struct.add(null);
                } else {
                    struct.add(Double.valueOf((((r.nextDouble()) - 0.5) * 10)));
                }
                structs[i] = struct;
            }
            sort(structs);
            ddl = "struct test { double hello}";
            System.out.println(("Testing " + ddl));
            testTBinarySortableProtocol(structs, ddl, true);
            testTBinarySortableProtocol(structs, ddl, false);
            // Test integer
            for (int i = 0; i < num; i++) {
                ArrayList<Object> struct = new ArrayList<Object>();
                if (i == 0) {
                    struct.add(null);
                } else {
                    struct.add(((int) ((((r.nextDouble()) - 0.5) * 1.5) * (Integer.MAX_VALUE))));
                }
                structs[i] = struct;
            }
            sort(structs);
            // Null should be smaller than any other value, so put a null at the front
            // end
            // to test whether that is held.
            ((List) (structs[0])).set(0, null);
            ddl = "struct test { i32 hello}";
            System.out.println(("Testing " + ddl));
            testTBinarySortableProtocol(structs, ddl, true);
            testTBinarySortableProtocol(structs, ddl, false);
            // Test long
            for (int i = 0; i < num; i++) {
                ArrayList<Object> struct = new ArrayList<Object>();
                if (i == 0) {
                    struct.add(null);
                } else {
                    struct.add(((long) ((((r.nextDouble()) - 0.5) * 1.5) * (Long.MAX_VALUE))));
                }
                structs[i] = struct;
            }
            sort(structs);
            // Null should be smaller than any other value, so put a null at the front
            // end
            // to test whether that is held.
            ((List) (structs[0])).set(0, null);
            ddl = "struct test { i64 hello}";
            System.out.println(("Testing " + ddl));
            testTBinarySortableProtocol(structs, ddl, true);
            testTBinarySortableProtocol(structs, ddl, false);
            // Test string
            for (int i = 0; i < num; i++) {
                ArrayList<Object> struct = new ArrayList<Object>();
                if (i == 0) {
                    struct.add(null);
                } else {
                    struct.add(String.valueOf((((r.nextDouble()) - 0.5) * 1000)));
                }
                structs[i] = struct;
            }
            sort(structs);
            // Null should be smaller than any other value, so put a null at the front
            // end
            // to test whether that is held.
            ((List) (structs[0])).set(0, null);
            ddl = "struct test { string hello}";
            System.out.println(("Testing " + ddl));
            testTBinarySortableProtocol(structs, ddl, true);
            testTBinarySortableProtocol(structs, ddl, false);
            // Test string + double
            for (int i = 0; i < num; i++) {
                ArrayList<Object> struct = new ArrayList<Object>();
                if ((i % 9) == 0) {
                    struct.add(null);
                } else {
                    struct.add(("str" + (i / 5)));
                }
                if ((i % 7) == 0) {
                    struct.add(null);
                } else {
                    struct.add(Double.valueOf((((r.nextDouble()) - 0.5) * 10)));
                }
                structs[i] = struct;
            }
            sort(structs);
            // Null should be smaller than any other value, so put a null at the front
            // end
            // to test whether that is held.
            ((List) (structs[0])).set(0, null);
            ddl = "struct test { string hello, double another}";
            System.out.println(("Testing " + ddl));
            testTBinarySortableProtocol(structs, ddl, true);
            testTBinarySortableProtocol(structs, ddl, false);
            System.out.println("Test testTBinarySortableProtocol passed!");
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void testConfigurableTCTLSeparated() throws Throwable {
        try {
            // Try to construct an object
            ArrayList<String> bye = new ArrayList<String>();
            bye.add("firstString");
            bye.add("secondString");
            LinkedHashMap<String, Integer> another = new LinkedHashMap<String, Integer>();
            another.put("firstKey", 1);
            another.put("secondKey", 2);
            ArrayList<Object> struct = new ArrayList<Object>();
            struct.add(Integer.valueOf(234));
            struct.add(bye);
            struct.add(another);
            Properties schema = new Properties();
            schema.setProperty(SERIALIZATION_FORMAT, TCTLSeparatedProtocol.class.getName());
            schema.setProperty(META_TABLE_NAME, "test");
            schema.setProperty(SERIALIZATION_DDL, "struct test { i32 hello, list<string> bye, map<string,i32> another}");
            schema.setProperty(SERIALIZATION_LIB, new DynamicSerDe().getClass().toString());
            schema.setProperty(FIELD_DELIM, "9");
            schema.setProperty(COLLECTION_DELIM, "1");
            schema.setProperty(LINE_DELIM, "2");
            schema.setProperty(MAPKEY_DELIM, "4");
            DynamicSerDe serde = new DynamicSerDe();
            serde.initialize(new Configuration(), schema);
            TCTLSeparatedProtocol prot = ((TCTLSeparatedProtocol) (serde.oprot_));
            TestCase.assertTrue(prot.getPrimarySeparator().equals("\t"));
            ObjectInspector oi = serde.getObjectInspector();
            // Try to serialize
            BytesWritable bytes = ((BytesWritable) (serde.serialize(struct, oi)));
            hexString(bytes);
            String compare = "234" + ((((((((((("\t" + "firstString") + "\u0001") + "secondString") + "\t") + "firstKey") + "\u0004") + "1") + "\u0001") + "secondKey") + "\u0004") + "2");
            System.out.println((("bytes in text =" + (new String(bytes.get(), 0, bytes.getSize()))) + ">"));
            System.out.println((("compare to    =" + compare) + ">"));
            TestCase.assertTrue(compare.equals(new String(bytes.get(), 0, bytes.getSize())));
            // Try to deserialize
            Object o = serde.deserialize(bytes);
            System.out.println(("o class = " + (o.getClass())));
            List<?> olist = ((List<?>) (o));
            System.out.println(("o size = " + (olist.size())));
            System.out.println(("o[0] class = " + (olist.get(0).getClass())));
            System.out.println(("o[1] class = " + (olist.get(1).getClass())));
            System.out.println(("o[2] class = " + (olist.get(2).getClass())));
            System.out.println(("o = " + o));
            TestCase.assertEquals(o, struct);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Tests a single null list within a struct with return nulls on.
     */
    public void testNulls1() throws Throwable {
        try {
            // Try to construct an object
            ArrayList<String> bye = null;
            HashMap<String, Integer> another = new HashMap<String, Integer>();
            another.put("firstKey", 1);
            another.put("secondKey", 2);
            ArrayList<Object> struct = new ArrayList<Object>();
            struct.add(Integer.valueOf(234));
            struct.add(bye);
            struct.add(another);
            Properties schema = new Properties();
            schema.setProperty(SERIALIZATION_FORMAT, TCTLSeparatedProtocol.class.getName());
            schema.setProperty(META_TABLE_NAME, "test");
            schema.setProperty(SERIALIZATION_DDL, "struct test { i32 hello, list<string> bye, map<string,i32> another}");
            schema.setProperty(SERIALIZATION_LIB, new DynamicSerDe().getClass().toString());
            schema.setProperty(ReturnNullsKey, "true");
            DynamicSerDe serde = new DynamicSerDe();
            serde.initialize(new Configuration(), schema);
            ObjectInspector oi = serde.getObjectInspector();
            // Try to serialize
            BytesWritable bytes = ((BytesWritable) (serde.serialize(struct, oi)));
            hexString(bytes);
            // Try to deserialize
            Object o = serde.deserialize(bytes);
            TestCase.assertEquals(struct, o);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Tests all elements of a struct being null with return nulls on.
     */
    public void testNulls2() throws Throwable {
        try {
            // Try to construct an object
            ArrayList<String> bye = null;
            HashMap<String, Integer> another = null;
            ArrayList<Object> struct = new ArrayList<Object>();
            struct.add(null);
            struct.add(bye);
            struct.add(another);
            Properties schema = new Properties();
            schema.setProperty(SERIALIZATION_FORMAT, TCTLSeparatedProtocol.class.getName());
            schema.setProperty(META_TABLE_NAME, "test");
            schema.setProperty(SERIALIZATION_DDL, "struct test { i32 hello, list<string> bye, map<string,i32> another}");
            schema.setProperty(SERIALIZATION_LIB, new DynamicSerDe().getClass().toString());
            schema.setProperty(ReturnNullsKey, "true");
            DynamicSerDe serde = new DynamicSerDe();
            serde.initialize(new Configuration(), schema);
            ObjectInspector oi = serde.getObjectInspector();
            // Try to serialize
            BytesWritable bytes = ((BytesWritable) (serde.serialize(struct, oi)));
            hexString(bytes);
            // Try to deserialize
            Object o = serde.deserialize(bytes);
            List<?> olist = ((List<?>) (o));
            TestCase.assertTrue(((olist.size()) == 3));
            TestCase.assertEquals(null, olist.get(0));
            TestCase.assertEquals(null, olist.get(1));
            TestCase.assertEquals(null, olist.get(2));
            // assertEquals(o, struct); Cannot do this because types of null lists are
            // wrong.
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Tests map and list being empty with return nulls on.
     */
    public void testNulls3() throws Throwable {
        try {
            // Try to construct an object
            ArrayList<String> bye = new ArrayList<String>();
            HashMap<String, Integer> another = null;
            ArrayList<Object> struct = new ArrayList<Object>();
            struct.add(null);
            struct.add(bye);
            struct.add(another);
            Properties schema = new Properties();
            schema.setProperty(SERIALIZATION_FORMAT, TCTLSeparatedProtocol.class.getName());
            schema.setProperty(META_TABLE_NAME, "test");
            schema.setProperty(SERIALIZATION_DDL, "struct test { i32 hello, list<string> bye, map<string,i32> another}");
            schema.setProperty(SERIALIZATION_LIB, new DynamicSerDe().getClass().toString());
            schema.setProperty(ReturnNullsKey, "true");
            DynamicSerDe serde = new DynamicSerDe();
            serde.initialize(new Configuration(), schema);
            ObjectInspector oi = serde.getObjectInspector();
            // Try to serialize
            BytesWritable bytes = ((BytesWritable) (serde.serialize(struct, oi)));
            hexString(bytes);
            // Try to deserialize
            Object o = serde.deserialize(bytes);
            List<?> olist = ((List<?>) (o));
            TestCase.assertTrue(((olist.size()) == 3));
            TestCase.assertEquals(null, olist.get(0));
            TestCase.assertEquals(0, ((List<?>) (olist.get(1))).size());
            TestCase.assertEquals(null, olist.get(2));
            // assertEquals(o, struct); Cannot do this because types of null lists are
            // wrong.
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Tests map and list null/empty with return nulls *off*.
     */
    public void testNulls4() throws Throwable {
        try {
            // Try to construct an object
            ArrayList<String> bye = new ArrayList<String>();
            HashMap<String, Integer> another = null;
            ArrayList<Object> struct = new ArrayList<Object>();
            struct.add(null);
            struct.add(bye);
            struct.add(another);
            Properties schema = new Properties();
            schema.setProperty(SERIALIZATION_FORMAT, TCTLSeparatedProtocol.class.getName());
            schema.setProperty(META_TABLE_NAME, "test");
            schema.setProperty(SERIALIZATION_DDL, "struct test { i32 hello, list<string> bye, map<string,i32> another}");
            schema.setProperty(SERIALIZATION_LIB, new DynamicSerDe().getClass().toString());
            schema.setProperty(ReturnNullsKey, "false");
            DynamicSerDe serde = new DynamicSerDe();
            serde.initialize(new Configuration(), schema);
            ObjectInspector oi = serde.getObjectInspector();
            // Try to serialize
            BytesWritable bytes = ((BytesWritable) (serde.serialize(struct, oi)));
            hexString(bytes);
            // Try to deserialize
            Object o = serde.deserialize(bytes);
            List<?> olist = ((List<?>) (o));
            TestCase.assertTrue(((olist.size()) == 3));
            TestCase.assertEquals(new Integer(0), ((Integer) (olist.get(0))));
            List<?> num1 = ((List<?>) (olist.get(1)));
            TestCase.assertTrue(((num1.size()) == 0));
            Map<?, ?> num2 = ((Map<?, ?>) (olist.get(2)));
            TestCase.assertTrue(((num2.size()) == 0));
            // assertEquals(o, struct); Cannot do this because types of null lists are
            // wrong.
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Tests map and list null/empty with return nulls *off*.
     */
    public void testStructsinStructs() throws Throwable {
        try {
            Properties schema = new Properties();
            // schema.setProperty(serdeConstants.SERIALIZATION_FORMAT,
            // org.apache.thrift.protocol.TJSONProtocol.class.getName());
            schema.setProperty(SERIALIZATION_FORMAT, TBinaryProtocol.class.getName());
            schema.setProperty(META_TABLE_NAME, "test");
            schema.setProperty(SERIALIZATION_DDL, "struct inner { i32 field1, string field2 },struct  test {inner foo,  i32 hello, list<string> bye, map<string,i32> another}");
            schema.setProperty(SERIALIZATION_LIB, new DynamicSerDe().getClass().toString());
            // 
            // construct object of above type
            // 
            // construct the inner struct
            ArrayList<Object> innerStruct = new ArrayList<Object>();
            innerStruct.add(new Integer(22));
            innerStruct.add(new String("hello world"));
            // construct outer struct
            ArrayList<String> bye = new ArrayList<String>();
            bye.add("firstString");
            bye.add("secondString");
            HashMap<String, Integer> another = new HashMap<String, Integer>();
            another.put("firstKey", 1);
            another.put("secondKey", 2);
            ArrayList<Object> struct = new ArrayList<Object>();
            struct.add(innerStruct);
            struct.add(Integer.valueOf(234));
            struct.add(bye);
            struct.add(another);
            DynamicSerDe serde = new DynamicSerDe();
            serde.initialize(new Configuration(), schema);
            ObjectInspector oi = serde.getObjectInspector();
            // Try to serialize
            BytesWritable bytes = ((BytesWritable) (serde.serialize(struct, oi)));
            // Try to deserialize
            Object o = serde.deserialize(bytes);
            List<?> olist = ((List<?>) (o));
            TestCase.assertEquals(4, olist.size());
            TestCase.assertEquals(innerStruct, olist.get(0));
            TestCase.assertEquals(new Integer(234), olist.get(1));
            TestCase.assertEquals(bye, olist.get(2));
            TestCase.assertEquals(another, olist.get(3));
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void testSkip() throws Throwable {
        try {
            // Try to construct an object
            ArrayList<String> bye = new ArrayList<String>();
            bye.add("firstString");
            bye.add("secondString");
            LinkedHashMap<String, Integer> another = new LinkedHashMap<String, Integer>();
            another.put("firstKey", 1);
            another.put("secondKey", 2);
            ArrayList<Object> struct = new ArrayList<Object>();
            struct.add(Integer.valueOf(234));
            struct.add(bye);
            struct.add(another);
            Properties schema = new Properties();
            schema.setProperty(SERIALIZATION_FORMAT, TCTLSeparatedProtocol.class.getName());
            schema.setProperty(META_TABLE_NAME, "test");
            schema.setProperty(SERIALIZATION_DDL, "struct test { i32 hello, list<string> bye, map<string,i32> another}");
            schema.setProperty(SERIALIZATION_LIB, new DynamicSerDe().getClass().toString());
            schema.setProperty(FIELD_DELIM, "9");
            schema.setProperty(COLLECTION_DELIM, "1");
            schema.setProperty(LINE_DELIM, "2");
            schema.setProperty(MAPKEY_DELIM, "4");
            DynamicSerDe serde = new DynamicSerDe();
            serde.initialize(new Configuration(), schema);
            TCTLSeparatedProtocol prot = ((TCTLSeparatedProtocol) (serde.oprot_));
            TestCase.assertTrue(prot.getPrimarySeparator().equals("\t"));
            ObjectInspector oi = serde.getObjectInspector();
            // Try to serialize
            BytesWritable bytes = ((BytesWritable) (serde.serialize(struct, oi)));
            hexString(bytes);
            String compare = "234" + ((((((((((("\t" + "firstString") + "\u0001") + "secondString") + "\t") + "firstKey") + "\u0004") + "1") + "\u0001") + "secondKey") + "\u0004") + "2");
            System.out.println((("bytes in text =" + (new String(bytes.get(), 0, bytes.getSize()))) + ">"));
            System.out.println((("compare to    =" + compare) + ">"));
            TestCase.assertTrue(compare.equals(new String(bytes.get(), 0, bytes.getSize())));
            schema.setProperty(SERIALIZATION_DDL, "struct test { i32 hello, skip list<string> bye, map<string,i32> another}");
            serde.initialize(new Configuration(), schema);
            // Try to deserialize
            Object o = serde.deserialize(bytes);
            System.out.println(("o class = " + (o.getClass())));
            List<?> olist = ((List<?>) (o));
            System.out.println(("o size = " + (olist.size())));
            System.out.println(("o = " + o));
            TestCase.assertEquals(null, olist.get(1));
            // set the skipped field to null
            struct.set(1, null);
            TestCase.assertEquals(o, struct);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }
}

