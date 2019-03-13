package com.alibaba.json.bvt.parser.bug;


import Feature.DisableFieldSmartMatch;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;


public class Bug_for_changhao extends TestCase {
    public void test_for_bug() throws Exception {
        String s = "{\"intValue\":1,\"stringValue\":\"abc\"}";
        ParserConfig parseConfig = new ParserConfig();
        parseConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        Bug_for_changhao.TestClass t = JSON.parseObject(s, Bug_for_changhao.TestClass.class, parseConfig, DisableFieldSmartMatch);
        System.out.println(JSON.toJSONString(t));
    }

    static class TestClass {
        String stringValue;

        int intValue;

        /**
         * Getter method for property <tt>stringValue</tt>.
         *
         * @return property value of stringValue
         */
        public String getStringValue() {
            return stringValue;
        }

        /**
         * Setter method for property <tt>stringValue</tt>.
         *
         * @param stringValue
         * 		value to be assigned to property stringValue
         */
        public Bug_for_changhao.TestClass setStringValue(String stringValue) {
            this.stringValue = stringValue;
            return this;
        }

        /**
         * Getter method for property <tt>intValue</tt>.
         *
         * @return property value of intValue
         */
        public int getIntValue() {
            return intValue;
        }

        /**
         * Setter method for property <tt>intValue</tt>.
         *
         * @param intValue
         * 		value to be assigned to property intValue
         */
        public Bug_for_changhao.TestClass setIntValue(int intValue) {
            this.intValue = intValue;
            return this;
        }
    }
}

