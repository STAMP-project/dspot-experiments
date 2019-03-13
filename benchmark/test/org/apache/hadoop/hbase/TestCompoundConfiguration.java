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
package org.apache.hadoop.hbase;


import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestCompoundConfiguration {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompoundConfiguration.class);

    private Configuration baseConf;

    private int baseConfSize;

    @Test
    public void testBasicFunctionality() throws ClassNotFoundException {
        CompoundConfiguration compoundConf = new CompoundConfiguration().add(baseConf);
        Assert.assertEquals("1", compoundConf.get("A"));
        Assert.assertEquals(2, compoundConf.getInt("B", 0));
        Assert.assertEquals(3, compoundConf.getInt("C", 0));
        Assert.assertEquals(0, compoundConf.getInt("D", 0));
        Assert.assertEquals(CompoundConfiguration.class, compoundConf.getClassByName(CompoundConfiguration.class.getName()));
        try {
            compoundConf.getClassByName("bad_class_name");
            Assert.fail("Trying to load bad_class_name should throw an exception");
        } catch (ClassNotFoundException e) {
            // win!
        }
    }

    @Test
    public void testPut() {
        CompoundConfiguration compoundConf = new CompoundConfiguration().add(baseConf);
        Assert.assertEquals("1", compoundConf.get("A"));
        Assert.assertEquals(2, compoundConf.getInt("B", 0));
        Assert.assertEquals(3, compoundConf.getInt("C", 0));
        Assert.assertEquals(0, compoundConf.getInt("D", 0));
        compoundConf.set("A", "1337");
        compoundConf.set("string", "stringvalue");
        Assert.assertEquals(1337, compoundConf.getInt("A", 0));
        Assert.assertEquals("stringvalue", compoundConf.get("string"));
        // we didn't modify the base conf
        Assert.assertEquals("1", baseConf.get("A"));
        Assert.assertNull(baseConf.get("string"));
        // adding to the base shows up in the compound
        baseConf.set("setInParent", "fromParent");
        Assert.assertEquals("fromParent", compoundConf.get("setInParent"));
    }

    @Test
    public void testWithConfig() {
        Configuration conf = new Configuration();
        conf.set("B", "2b");
        conf.set("C", "33");
        conf.set("D", "4");
        CompoundConfiguration compoundConf = new CompoundConfiguration().add(baseConf).add(conf);
        Assert.assertEquals("1", compoundConf.get("A"));
        Assert.assertEquals("2b", compoundConf.get("B"));
        Assert.assertEquals(33, compoundConf.getInt("C", 0));
        Assert.assertEquals("4", compoundConf.get("D"));
        Assert.assertEquals(4, compoundConf.getInt("D", 0));
        Assert.assertNull(compoundConf.get("E"));
        Assert.assertEquals(6, compoundConf.getInt("F", 6));
        int cnt = 0;
        for (Map.Entry<String, String> entry : compoundConf) {
            cnt++;
            if (entry.getKey().equals("B")) {
                Assert.assertEquals("2b", entry.getValue());
            } else
                if (entry.getKey().equals("G")) {
                    Assert.assertNull(entry.getValue());
                }

        }
        // verify that entries from ImmutableConfigMap's are merged in the iterator's view
        Assert.assertEquals(((baseConfSize) + 1), cnt);
    }

    @Test
    public void testWithIbwMap() {
        Map<Bytes, Bytes> map = new HashMap<>();
        map.put(strToIb("B"), strToIb("2b"));
        map.put(strToIb("C"), strToIb("33"));
        map.put(strToIb("D"), strToIb("4"));
        // unlike config, note that IBW Maps can accept null values
        map.put(strToIb("G"), null);
        CompoundConfiguration compoundConf = new CompoundConfiguration().add(baseConf).addBytesMap(map);
        Assert.assertEquals("1", compoundConf.get("A"));
        Assert.assertEquals("2b", compoundConf.get("B"));
        Assert.assertEquals(33, compoundConf.getInt("C", 0));
        Assert.assertEquals("4", compoundConf.get("D"));
        Assert.assertEquals(4, compoundConf.getInt("D", 0));
        Assert.assertNull(compoundConf.get("E"));
        Assert.assertEquals(6, compoundConf.getInt("F", 6));
        Assert.assertNull(compoundConf.get("G"));
        int cnt = 0;
        for (Map.Entry<String, String> entry : compoundConf) {
            cnt++;
            if (entry.getKey().equals("B")) {
                Assert.assertEquals("2b", entry.getValue());
            } else
                if (entry.getKey().equals("G")) {
                    Assert.assertNull(entry.getValue());
                }

        }
        // verify that entries from ImmutableConfigMap's are merged in the iterator's view
        Assert.assertEquals(((baseConfSize) + 2), cnt);
        // Verify that adding map after compound configuration is modified overrides properly
        CompoundConfiguration conf2 = new CompoundConfiguration();
        conf2.set("X", "modification");
        conf2.set("D", "not4");
        Assert.assertEquals("modification", conf2.get("X"));
        Assert.assertEquals("not4", conf2.get("D"));
        conf2.addBytesMap(map);
        Assert.assertEquals("4", conf2.get("D"));// map overrides

    }

    @Test
    public void testWithStringMap() {
        Map<String, String> map = new HashMap<>();
        map.put("B", "2b");
        map.put("C", "33");
        map.put("D", "4");
        // unlike config, note that IBW Maps can accept null values
        map.put("G", null);
        CompoundConfiguration compoundConf = new CompoundConfiguration().addStringMap(map);
        Assert.assertEquals("2b", compoundConf.get("B"));
        Assert.assertEquals(33, compoundConf.getInt("C", 0));
        Assert.assertEquals("4", compoundConf.get("D"));
        Assert.assertEquals(4, compoundConf.getInt("D", 0));
        Assert.assertNull(compoundConf.get("E"));
        Assert.assertEquals(6, compoundConf.getInt("F", 6));
        Assert.assertNull(compoundConf.get("G"));
        int cnt = 0;
        for (Map.Entry<String, String> entry : compoundConf) {
            cnt++;
            if (entry.getKey().equals("B")) {
                Assert.assertEquals("2b", entry.getValue());
            } else
                if (entry.getKey().equals("G")) {
                    Assert.assertNull(entry.getValue());
                }

        }
        // verify that entries from ImmutableConfigMap's are merged in the iterator's view
        Assert.assertEquals(4, cnt);
        // Verify that adding map after compound configuration is modified overrides properly
        CompoundConfiguration conf2 = new CompoundConfiguration();
        conf2.set("X", "modification");
        conf2.set("D", "not4");
        Assert.assertEquals("modification", conf2.get("X"));
        Assert.assertEquals("not4", conf2.get("D"));
        conf2.addStringMap(map);
        Assert.assertEquals("4", conf2.get("D"));// map overrides

    }

    @Test
    public void testLaterConfigsOverrideEarlier() {
        Map<String, String> map1 = new HashMap<>();
        map1.put("A", "2");
        map1.put("D", "5");
        Map<String, String> map2 = new HashMap<>();
        String newValueForA = "3";
        String newValueForB = "4";
        map2.put("A", newValueForA);
        map2.put("B", newValueForB);
        CompoundConfiguration compoundConf = new CompoundConfiguration().addStringMap(map1).add(baseConf);
        Assert.assertEquals("1", compoundConf.get("A"));
        Assert.assertEquals("5", compoundConf.get("D"));
        compoundConf.addStringMap(map2);
        Assert.assertEquals(newValueForA, compoundConf.get("A"));
        Assert.assertEquals(newValueForB, compoundConf.get("B"));
        Assert.assertEquals("5", compoundConf.get("D"));
        int cnt = 0;
        for (Map.Entry<String, String> entry : compoundConf) {
            cnt++;
            if (entry.getKey().equals("A")) {
                Assert.assertEquals(newValueForA, entry.getValue());
            } else
                if (entry.getKey().equals("B")) {
                    Assert.assertEquals(newValueForB, entry.getValue());
                }

        }
        // verify that entries from ImmutableConfigMap's are merged in the iterator's view
        Assert.assertEquals(((baseConfSize) + 1), cnt);
    }
}

