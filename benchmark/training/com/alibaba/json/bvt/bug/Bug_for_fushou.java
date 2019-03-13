package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class Bug_for_fushou extends TestCase {
    public void test_case1() {
        String text = "{\"modules\":{}}";
        Bug_for_fushou.L1<?> r1 = JSONObject.parseObject(text, new com.alibaba.fastjson.TypeReference<Bug_for_fushou.L1<Bug_for_fushou.L2>>() {});
        TestCase.assertEquals(true, ((r1.getModules()) instanceof Bug_for_fushou.L2));
        Bug_for_fushou.L1 r2 = JSONObject.parseObject(text, new com.alibaba.fastjson.TypeReference<Bug_for_fushou.L1>() {});
        TestCase.assertEquals(true, ((r2.getModules()) instanceof JSONObject));
        TestCase.assertEquals(false, ((r2.getModules()) instanceof Bug_for_fushou.L2));
    }

    public void test_case2() {
        String text = "{\"modules\":{}}";
        Bug_for_fushou.L1<?> r0 = JSONObject.parseObject(text, new com.alibaba.fastjson.TypeReference<Bug_for_fushou.L1>() {});
        TestCase.assertEquals(JSONObject.class, r0.getModules().getClass());
        Bug_for_fushou.L1<?> r1 = JSONObject.parseObject(text, new com.alibaba.fastjson.TypeReference<Bug_for_fushou.L1<Bug_for_fushou.L2>>() {});
        TestCase.assertEquals(Bug_for_fushou.L2.class, r1.getModules().getClass());
        Bug_for_fushou.L1 r2 = JSONObject.parseObject(text, new com.alibaba.fastjson.TypeReference<Bug_for_fushou.L1>() {});
        TestCase.assertEquals(JSONObject.class, r2.getModules().getClass());
        Bug_for_fushou.L1<?> r3 = JSONObject.parseObject(text, new com.alibaba.fastjson.TypeReference<Bug_for_fushou.L1<Bug_for_fushou.L3>>() {});
        TestCase.assertEquals(Bug_for_fushou.L3.class, r3.getModules().getClass());
    }

    public static class L1<T> {
        private T modules;

        public T getModules() {
            return modules;
        }

        public void setModules(T modules) {
            this.modules = modules;
        }
    }

    public static class L2 {
        public String name;

        public L2() {
        }
    }

    public static class L3 {
        public L3() {
        }
    }
}

