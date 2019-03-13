package com.alibaba.json.bvt.support.hsf;


import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import com.alibaba.fastjson.support.hsf.MethodLocator;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashSet;
import junit.framework.TestCase;


public class HSFJSONUtilsTest_4 extends TestCase {
    public void test_for_hsf() throws Exception {
        final Method method = HSFJSONUtilsTest_4.class.getMethod("f", HashSet.class, HSFJSONUtilsTest_4.BigDecimalDO.class);
        String json = "{\"argsTypes\":[\"java.util.HashSet\",\"com.alibaba.json.bvt.support.hsf.HSFJSONUtilsTest_4$BigDecimalDO\"],\"argsObjs\":[[{\"bd\":10.12379}],{\"$ref\":\"$.argsObjs[0][0]\"}]}";
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, new MethodLocator() {
            public Method findMethod(String[] types) {
                return method;
            }
        });
        TestCase.assertEquals(2, values.length);
        HashSet<HSFJSONUtilsTest_4.BigDecimalDO> set = ((HashSet<HSFJSONUtilsTest_4.BigDecimalDO>) (values[0]));
        TestCase.assertEquals(1, set.size());
        TestCase.assertSame(set.iterator().next(), values[1]);
    }

    public static class BigDecimalDO implements Serializable {
        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 1081203063524239676L;

        private BigDecimal bd = null;

        public BigDecimal getBd() {
            return bd;
        }

        public void setBd(BigDecimal bd) {
            this.bd = bd;
        }
    }
}

