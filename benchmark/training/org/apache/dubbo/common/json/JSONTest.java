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
package org.apache.dubbo.common.json;


import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@Deprecated
public class JSONTest {
    static byte[] DEFAULT_BYTES = new byte[]{ 3, 12, 14, 41, 12, 2, 3, 12, 4, 67, 23 };

    static int DEFAULT_$$ = 152;

    @Test
    public void testException() throws Exception {
        MyException e = new MyException("001", "AAAAAAAA");
        StringWriter writer = new StringWriter();
        JSON.json(e, writer);
        String json = writer.getBuffer().toString();
        System.out.println(json);
        // Assertions.assertEquals("{\"code\":\"001\",\"message\":\"AAAAAAAA\"}", json);
        StringReader reader = new StringReader(json);
        MyException result = JSON.parse(reader, MyException.class);
        Assertions.assertEquals("001", result.getCode());
        Assertions.assertEquals("AAAAAAAA", result.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMap() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("aaa", "bbb");
        StringWriter writer = new StringWriter();
        JSON.json(map, writer);
        String json = writer.getBuffer().toString();
        Assertions.assertEquals("{\"aaa\":\"bbb\"}", json);
        StringReader reader = new StringReader(json);
        Map<String, String> result = JSON.parse(reader, Map.class);
        Assertions.assertEquals("bbb", result.get("aaa"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapArray() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("aaa", "bbb");
        StringWriter writer = new StringWriter();
        JSON.json(new Object[]{ map }, writer);// args

        String json = writer.getBuffer().toString();
        Assertions.assertEquals("[{\"aaa\":\"bbb\"}]", json);
        StringReader reader = new StringReader(json);
        Object[] result = JSON.parse(reader, new Class<?>[]{ Map.class });
        Assertions.assertEquals(1, result.length);
        Assertions.assertEquals("bbb", ((Map<String, String>) (result[0])).get("aaa"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLinkedMap() throws Exception {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("aaa", "bbb");
        StringWriter writer = new StringWriter();
        JSON.json(map, writer);
        String json = writer.getBuffer().toString();
        Assertions.assertEquals("{\"aaa\":\"bbb\"}", json);
        StringReader reader = new StringReader(json);
        LinkedHashMap<String, String> result = JSON.parse(reader, LinkedHashMap.class);
        Assertions.assertEquals("bbb", result.get("aaa"));
    }

    @Test
    public void testObject2Json() throws Exception {
        JSONTest.Bean bean = new JSONTest.Bean();
        bean.array = new int[]{ 1, 3, 4 };
        bean.setName("ql");
        String json = JSON.json(bean);
        bean = JSON.parse(json, JSONTest.Bean.class);
        Assertions.assertEquals(bean.getName(), "ql");
        Assertions.assertEquals(bean.getDisplayName(), "??");
        Assertions.assertEquals(bean.bytes.length, JSONTest.DEFAULT_BYTES.length);
        Assertions.assertEquals(bean.$$, JSONTest.DEFAULT_$$);
        Assertions.assertEquals("{\"name\":\"ql\",\"array\":[1,3,4]}", JSON.json(bean, new String[]{ "name", "array" }));
    }

    @Test
    public void testParse2JSONObject() throws Exception {
        JSONObject jo = ((JSONObject) (JSON.parse("{name:'qianlei',array:[1,2,3,4,98.123],b1:TRUE,$1:NULL,$2:FALSE,__3:NULL}")));
        Assertions.assertEquals(jo.getString("name"), "qianlei");
        Assertions.assertEquals(jo.getArray("array").length(), 5);
        Assertions.assertEquals(jo.get("$2"), Boolean.FALSE);
        Assertions.assertEquals(jo.get("__3"), null);
        for (int i = 0; i < 10000; i++)
            JSON.parse("{\"name\":\"qianlei\",\"array\":[1,2,3,4,98.123],\"displayName\":\"\u94b1\u78ca\"}");

        long now = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++)
            JSON.parse("{\"name\":\"qianlei\",\"array\":[1,2,3,4,98.123],\"displayName\":\"\u94b1\u78ca\"}");

        System.out.println(("parse to JSONObject 10000 times in: " + ((System.currentTimeMillis()) - now)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testParse2Class() throws Exception {
        int[] o1 = new int[]{ 1, 2, 3, 4, 5 };
        int[] o2 = JSON.parse("[1.2,2,3,4,5]", int[].class);
        Assertions.assertEquals(o2.length, 5);
        for (int i = 0; i < 5; i++)
            Assertions.assertEquals(o1[i], o2[i]);

        List l1 = ((List) (JSON.parse("[1.2,2,3,4,5]", List.class)));
        Assertions.assertEquals(l1.size(), 5);
        for (int i = 0; i < 5; i++)
            Assertions.assertEquals(o1[i], ((Number) (l1.get(i))).intValue());

        JSONTest.Bean bean = JSON.parse("{name:'qianlei',array:[1,2,3,4,98.123],displayName:'??',$$:214726,$b:TRUE}", JSONTest.Bean.class);
        Assertions.assertEquals(bean.getName(), "qianlei");
        Assertions.assertEquals(bean.getDisplayName(), "??");
        Assertions.assertEquals(bean.array.length, 5);
        Assertions.assertEquals(bean.$$, 214726);
        Assertions.assertEquals(bean.$b, true);
        for (int i = 0; i < 10000; i++)
            JSON.parse("{name:'qianlei',array:[1,2,3,4,98.123],displayName:'??'}", JSONTest.Bean1.class);

        long now = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++)
            JSON.parse("{name:'qianlei',array:[1,2,3,4,98.123],displayName:'??'}", JSONTest.Bean1.class);

        System.out.println(("parse to Class 10000 times in: " + ((System.currentTimeMillis()) - now)));
    }

    @Test
    public void testParse2Arguments() throws Exception {
        Object[] test = JSON.parse("[1.2, 2, {name:'qianlei',array:[1,2,3,4,98.123]} ]", new Class<?>[]{ int.class, int.class, JSONTest.Bean.class });
        Assertions.assertEquals(test[1], 2);
        Assertions.assertEquals(test[2].getClass(), JSONTest.Bean.class);
        test = JSON.parse("[1.2, 2]", new Class<?>[]{ int.class, int.class });
        Assertions.assertEquals(test[0], 1);
    }

    @Test
    public void testLocale() throws Exception {
        Locale obj = Locale.US;
        String str = JSON.json(obj);
        Assertions.assertEquals("\"en_US\"", str);
        Assertions.assertEquals(obj, JSON.parse(str, Locale.class));
    }

    public static class Bean1 {
        public int[] array;

        private String name;

        private String displayName;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Bean {
        public int[] array;

        public boolean $b;

        public int $$ = JSONTest.DEFAULT_$$;

        public byte[] bytes = JSONTest.DEFAULT_BYTES;

        private String name;

        private String displayName = "??";

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

