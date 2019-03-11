package org.keycloak.client.registration.cli.util;


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.client.registration.cli.common.AttributeKey;
import org.keycloak.client.registration.cli.common.AttributeOperation;


/**
 *
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class ReflectionUtilTest {
    @Test
    public void testSettingAttibutes() {
        ReflectionUtilTest.Data data = new ReflectionUtilTest.Data();
        LinkedList<AttributeOperation> attrs = new LinkedList<>();
        attrs.add(new AttributeOperation(SET, "longAttr", "42"));
        attrs.add(new AttributeOperation(SET, "strAttr", "not null"));
        attrs.add(new AttributeOperation(SET, "strList+", "two"));
        attrs.add(new AttributeOperation(SET, "strList+", "three"));
        attrs.add(new AttributeOperation(SET, "strList[0]+", "one"));
        attrs.add(new AttributeOperation(SET, "config", "{\"key1\": \"value1\"}"));
        attrs.add(new AttributeOperation(SET, "config.key2", "value2"));
        attrs.add(new AttributeOperation(SET, "nestedConfig", "{\"key1\": {\"sub key1\": \"sub value1\"}}"));
        attrs.add(new AttributeOperation(SET, "nestedConfig.key1.\"sub key2\"", "sub value2"));
        attrs.add(new AttributeOperation(SET, "nested.strList", "[1,2,3,4]"));
        attrs.add(new AttributeOperation(SET, "nested.dataList+", "{\"baseAttr\": \"item1\", \"strList\": [\"confidential\", \"public\"]}"));
        attrs.add(new AttributeOperation(SET, "nested.dataList+", "{\"baseAttr\": \"item2\", \"strList\": [\"external\"]}"));
        attrs.add(new AttributeOperation(SET, "nested.dataList[1].baseAttr", "changed item2"));
        attrs.add(new AttributeOperation(SET, "nested.nested.strList", "[\"first\",\"second\"]"));
        attrs.add(new AttributeOperation(DELETE, "nested.strList[1]"));
        attrs.add(new AttributeOperation(SET, "nested.nested.nested", "{\"baseAttr\": \"NEW VALUE\", \"strList\": [true, false]}"));
        attrs.add(new AttributeOperation(SET, "nested.strAttr", "NOT NULL"));
        attrs.add(new AttributeOperation(DELETE, "nested.strAttr"));
        ReflectionUtil.setAttributes(data, attrs);
        Assert.assertEquals("longAttr", Long.valueOf(42), data.getLongAttr());
        Assert.assertEquals("strAttr", "not null", data.getStrAttr());
        Assert.assertEquals("strList", Arrays.asList("one", "two", "three"), data.getStrList());
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("key1", "value1");
        expectedMap.put("key2", "value2");
        Assert.assertEquals("config", expectedMap, data.getConfig());
        expectedMap = new HashMap<>();
        expectedMap.put("sub key1", "sub value1");
        expectedMap.put("sub key2", "sub value2");
        Assert.assertNotNull("nestedConfig", data.getNestedConfig());
        Assert.assertEquals("nestedConfig has one element", 1, data.getNestedConfig().size());
        Assert.assertEquals("nestedConfig.key1", expectedMap, data.getNestedConfig().get("key1"));
        ReflectionUtilTest.Data nested = data.getNested();
        Assert.assertEquals("nested.strAttr", null, nested.getStrAttr());
        Assert.assertEquals("nested.strList", Arrays.asList("1", "3", "4"), nested.getStrList());
        Assert.assertEquals("nested.dataList[0].baseAttr", "item1", nested.getDataList().get(0).getBaseAttr());
        Assert.assertEquals("nested.dataList[0].strList", Arrays.asList("confidential", "public"), nested.getDataList().get(0).getStrList());
        Assert.assertEquals("nested.dataList[1].baseAttr", "changed item2", nested.getDataList().get(1).getBaseAttr());
        Assert.assertEquals("nested.dataList[1].strList", Arrays.asList("external"), nested.getDataList().get(1).getStrList());
        nested = nested.getNested();
        Assert.assertEquals("nested.nested.strList", Arrays.asList("first", "second"), nested.getStrList());
        nested = nested.getNested();
        Assert.assertEquals("nested.nested.nested.baseAttr", "NEW VALUE", nested.getBaseAttr());
        Assert.assertEquals("nested.nested.nested.strList", Arrays.asList("true", "false"), nested.getStrList());
    }

    @Test
    public void testKeyParsing() {
        assertAttributeKey(new AttributeKey("am.bam.pet"), "am", (-1), "bam", (-1), "pet", (-1));
        assertAttributeKey(new AttributeKey("a"), "a", (-1));
        assertAttributeKey(new AttributeKey("a.b"), "a", (-1), "b", (-1));
        assertAttributeKey(new AttributeKey("a.b[1]"), "a", (-1), "b", 1);
        assertAttributeKey(new AttributeKey("a[12].b"), "a", 12, "b", (-1));
        assertAttributeKey(new AttributeKey("a[10].b[20]"), "a", 10, "b", 20);
        assertAttributeKey(new AttributeKey("\"am\".\"bam\".\"pet\""), "am", (-1), "bam", (-1), "pet", (-1));
        assertAttributeKey(new AttributeKey("\"am\".bam.\"pet\""), "am", (-1), "bam", (-1), "pet", (-1));
        assertAttributeKey(new AttributeKey("\"am.bam\".\"pet\""), "am.bam", (-1), "pet", (-1));
        assertAttributeKey(new AttributeKey("\"am.bam[2]\".\"pet[6]\""), "am.bam", 2, "pet", 6);
        try {
            new AttributeKey("a.");
            Assert.fail("Should have failed");
        } catch (RuntimeException expected) {
        }
        try {
            new AttributeKey("a[]");
            Assert.fail("Should have failed");
        } catch (RuntimeException expected) {
        }
        try {
            new AttributeKey("a[lala]");
            Assert.fail("Should have failed");
        } catch (RuntimeException expected) {
        }
        try {
            new AttributeKey("a[\"lala\"]");
            Assert.fail("Should have failed");
        } catch (RuntimeException expected) {
        }
        try {
            new AttributeKey(".a");
            Assert.fail("Should have failed");
        } catch (RuntimeException expected) {
        }
        try {
            new AttributeKey("\"am\"..\"bam\".\"pet\"");
            Assert.fail("Should have failed");
        } catch (RuntimeException expected) {
        }
        try {
            new AttributeKey("\"am\"ups.\"bam\".\"pet\"");
            Assert.fail("Should have failed");
        } catch (RuntimeException expected) {
        }
        try {
            new AttributeKey("ups\"am\"ups.\"bam\".\"pet\"");
            Assert.fail("Should have failed");
        } catch (RuntimeException expected) {
        }
    }

    public static class BaseData {
        String baseAttr;

        public String getBaseAttr() {
            return baseAttr;
        }

        public void setBaseAttr(String baseAttr) {
            this.baseAttr = baseAttr;
        }
    }

    public static class Data extends ReflectionUtilTest.BaseData {
        String strAttr;

        Integer intAttr;

        Long longAttr;

        Boolean boolAttr;

        List<String> strList;

        List<Integer> intList;

        List<ReflectionUtilTest.Data> dataList;

        List<List<String>> deepList;

        ReflectionUtilTest.Data nested;

        Map<String, String> config;

        Map<String, Map<String, ReflectionUtilTest.Data>> nestedConfig;

        public String getStrAttr() {
            return strAttr;
        }

        public void setStrAttr(String strAttr) {
            this.strAttr = strAttr;
        }

        public Integer getIntAttr() {
            return intAttr;
        }

        public void setIntAttr(Integer intAttr) {
            this.intAttr = intAttr;
        }

        public Long getLongAttr() {
            return longAttr;
        }

        public void setLongAttr(Long longAttr) {
            this.longAttr = longAttr;
        }

        public Boolean getBoolAttr() {
            return boolAttr;
        }

        public void setBoolAttr(Boolean boolAttr) {
            this.boolAttr = boolAttr;
        }

        public List<String> getStrList() {
            return strList;
        }

        public void setStrList(List<String> strList) {
            this.strList = strList;
        }

        public List<Integer> getIntList() {
            return intList;
        }

        public void setIntList(List<Integer> intList) {
            this.intList = intList;
        }

        public List<ReflectionUtilTest.Data> getDataList() {
            return dataList;
        }

        public void setDataList(List<ReflectionUtilTest.Data> dataList) {
            this.dataList = dataList;
        }

        public ReflectionUtilTest.Data getNested() {
            return nested;
        }

        public void setNested(ReflectionUtilTest.Data nested) {
            this.nested = nested;
        }

        public List<List<String>> getDeepList() {
            return deepList;
        }

        public void setDeepList(List<List<String>> deepList) {
            this.deepList = deepList;
        }

        public void setConfig(Map<String, String> config) {
            this.config = config;
        }

        public Map<String, String> getConfig() {
            return config;
        }

        public void setNestedConfig(Map<String, Map<String, ReflectionUtilTest.Data>> nestedConfig) {
            this.nestedConfig = nestedConfig;
        }

        public Map<String, Map<String, ReflectionUtilTest.Data>> getNestedConfig() {
            return nestedConfig;
        }
    }
}

