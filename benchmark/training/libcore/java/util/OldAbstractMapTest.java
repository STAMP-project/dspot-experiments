/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package libcore.java.util;


import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


public class OldAbstractMapTest extends TestCase {
    public void test_Constructor() {
        OldAbstractMapTest.AMT amt = new OldAbstractMapTest.AMT();
        TestCase.assertNotNull(amt);
    }

    public void test_equalsLjava_lang_Object() {
        AbstractMap<String, String> amt1 = new OldAbstractMapTest.AMT();
        AbstractMap<String, String> amt2 = new OldAbstractMapTest.AMT();
        TestCase.assertTrue("assert 0", amt1.equals(amt2));
        TestCase.assertTrue("assert 1", amt1.equals(amt1));
        TestCase.assertTrue("assert 2", amt2.equals(amt1));
        amt1.put("1", "one");
        TestCase.assertFalse("assert 3", amt1.equals(amt2));
        amt1.put("2", "two");
        amt1.put("3", "three");
        amt2.put("1", "one");
        amt2.put("2", "two");
        amt2.put("3", "three");
        TestCase.assertTrue("assert 4", amt1.equals(amt2));
        TestCase.assertFalse("assert 5", amt1.equals(this));
    }

    public void test_hashCode() {
        OldAbstractMapTest.AMT amt1 = new OldAbstractMapTest.AMT();
        OldAbstractMapTest.AMT amt2 = new OldAbstractMapTest.AMT();
        amt1.put("1", "one");
        TestCase.assertNotSame(amt1.hashCode(), amt2.hashCode());
    }

    public void test_isEmpty() {
        OldAbstractMapTest.AMT amt = new OldAbstractMapTest.AMT();
        TestCase.assertTrue(amt.isEmpty());
        amt.put("1", "one");
        TestCase.assertFalse(amt.isEmpty());
    }

    public void test_put() {
        OldAbstractMapTest.AMT amt = new OldAbstractMapTest.AMT();
        TestCase.assertEquals(0, amt.size());
        amt.put("1", "one");
        TestCase.assertEquals(1, amt.size());
        amt.put("2", "two");
        TestCase.assertEquals(2, amt.size());
        amt.put("3", "three");
        TestCase.assertEquals(3, amt.size());
    }

    public void test_size() {
        OldAbstractMapTest.AMT amt = new OldAbstractMapTest.AMT();
        TestCase.assertEquals(0, amt.size());
        amt.put("1", "one");
        TestCase.assertEquals(1, amt.size());
        amt.put("2", "two");
        TestCase.assertEquals(2, amt.size());
        amt.put("3", "three");
        TestCase.assertEquals(3, amt.size());
    }

    public void test_toString() {
        OldAbstractMapTest.AMT amt = new OldAbstractMapTest.AMT();
        TestCase.assertEquals("{}", amt.toString());
        amt.put("1", "one");
        TestCase.assertEquals("{1=one}", amt.toString());
        amt.put("2", "two");
        TestCase.assertEquals("{1=one, 2=two}", amt.toString());
        amt.put("3", "three");
        TestCase.assertEquals("{1=one, 2=two, 3=three}", amt.toString());
    }

    static class AMT extends AbstractMap<String, String> {
        private final List<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>();

        @Override
        public String put(String key, String value) {
            String result = remove(key);
            entries.add(new AbstractMap.SimpleEntry<String, String>(key, value));
            return result;
        }

        @Override
        public Set<Map.Entry<String, String>> entrySet() {
            return new AbstractSet<Map.Entry<String, String>>() {
                @Override
                public Iterator<Map.Entry<String, String>> iterator() {
                    return entries.iterator();
                }

                @Override
                public int size() {
                    return entries.size();
                }
            };
        }
    }
}

