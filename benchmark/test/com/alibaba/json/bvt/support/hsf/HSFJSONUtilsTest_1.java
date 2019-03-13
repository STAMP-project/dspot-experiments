package com.alibaba.json.bvt.support.hsf;


import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import com.alibaba.fastjson.support.hsf.MethodLocator;
import java.lang.reflect.Method;
import java.util.List;
import junit.framework.TestCase;


public class HSFJSONUtilsTest_1 extends TestCase {
    private Method method_f2;

    private Method method_f3;

    private Method method_f4;

    private Method method_f5;

    private MethodLocator methodLocator;

    public void test_invoke() throws Exception {
        String json = "{ \n" + (("    \"argsTypes\"  :  [ \"java.lang.String\", \"com.alibaba.json.bvt.support.hsf.HSFJSONUtilsTest_0$Model\"],\n" + "    \"argsObjs\"   :   [ \"abc\", {\"value\":\"xxx\"} ]\n") + "}");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals("abc", values[0]);
        TestCase.assertEquals("xxx", ((HSFJSONUtilsTest_1.Model) (values[1])).value);
    }

    public void test_invoke_type() throws Exception {
        String json = "{\"@type\":\"com.alibaba.fastjson.JSONObject\", \n" + (("    \"argsTypes\"  :  [ \"java.lang.String\", \"com.alibaba.json.bvt.support.hsf.HSFJSONUtilsTest_0$Model\"],\n" + "    \"argsObjs\"   :   [ \"abc\", {\"value\":\"xxx\"} ]\n") + "}");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals("abc", values[0]);
        TestCase.assertEquals("xxx", ((HSFJSONUtilsTest_1.Model) (values[1])).value);
    }

    public void test_invoke_reverse() throws Exception {
        String json = "{ \n" + (("    \"argsObjs\"   :   [ \"abc\", {\"value\":\"xxx\"} ],\n" + "    \"argsTypes\"  :  [ \"java.lang.String\", \"com.alibaba.json.bvt.support.hsf.HSFJSONUtilsTest_0$Model\"]\n") + "}");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals("abc", values[0]);
        TestCase.assertEquals("xxx", ((HSFJSONUtilsTest_1.Model) (values[1])).value);
    }

    public void test_invoke_reverse_list() throws Exception {
        String json = "{ \n" + (("    \"argsObjs\"   :   [ \"abc\", [{\"value\":\"xxx\"}] ],\n" + "    \"argsTypes\"  :  [ \"java.lang.String\", \"java.util.List\"]\n") + "}");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals("abc", values[0]);
        List list = ((List) (values[1]));
        TestCase.assertEquals("xxx", ((HSFJSONUtilsTest_1.Model) (list.get(0))).value);
    }

    public void test_invoke_reverse_array() throws Exception {
        String json = "{ \n" + (("    \"argsObjs\"   :   [ \"abc\", [{\"value\":\"xxx\"}] ],\n" + "    \"argsTypes\"  :  [ \"java.lang.String\", \"com.alibaba.json.bvt.support.hsf.HSFJSONUtilsTest_0$Model[]\"]\n") + "}");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals("abc", values[0]);
        HSFJSONUtilsTest_1.Model[] list = ((HSFJSONUtilsTest_1.Model[]) (values[1]));
        TestCase.assertEquals("xxx", ((HSFJSONUtilsTest_1.Model) (list[0])).value);
    }

    public void test_invoke_array() throws Exception {
        String json = "[ \n" + (("   [ \"java.lang.String\", \"com.alibaba.json.bvt.support.hsf.HSFJSONUtilsTest_0$Model\"],\n" + "    [ \"abc\", {\"value\":\"xxx\"} ]\n") + "]");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals("abc", values[0]);
        TestCase.assertEquals("xxx", ((HSFJSONUtilsTest_1.Model) (values[1])).value);
    }

    public void test_invoke_array_2() throws Exception {
        String json = "[ \n" + (("   [ \"java.lang.String\", \"java.util.List\"],\n" + "    [ \"abc\", [{\"value\":\"xxx\"}] ]\n") + "]");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals("abc", values[0]);
        List list = ((List) (values[1]));
        TestCase.assertEquals("xxx", ((HSFJSONUtilsTest_1.Model) (list.get(0))).value);
    }

    public void test_invoke_array_3() throws Exception {
        String json = "[ \n" + (("   [ \"java.lang.String\", \"com.alibaba.json.bvt.support.hsf.HSFJSONUtilsTest_0$Model[]\"],\n" + "    [ \"abc\", [{\"value\":\"xxx\"}] ]\n") + "]");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals("abc", values[0]);
        HSFJSONUtilsTest_1.Model[] list = ((HSFJSONUtilsTest_1.Model[]) (values[1]));
        TestCase.assertEquals("xxx", ((HSFJSONUtilsTest_1.Model) (list[0])).value);
    }

    public void test_invoke_int() throws Exception {
        String json = "[ \n" + (("   [ \"int\", \"long\"],\n" + "    [ 3,4 ]\n") + "]");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals(3, ((Integer) (values[0])).intValue());
        TestCase.assertEquals(4L, ((Long) (values[1])).longValue());
    }

    public void test_invoke_int_obj_reverse() throws Exception {
        String json = "{ \n" + (("    \"argsObjs\"   :   [ 3, 4],\n" + "    \"argsTypes\"  :  [ \"int\", \"long\"]\n") + "}");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals(3, ((Integer) (values[0])).intValue());
        TestCase.assertEquals(4L, ((Long) (values[1])).longValue());
    }

    public void test_invoke_int_obj() throws Exception {
        String json = "{ \n" + (("    \"argsTypes\"  :  [ \"int\", \"long\"],\n" + "    \"argsObjs\"   :   [ 3, 4 ]\n") + "}");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals(3, ((Integer) (values[0])).intValue());
        TestCase.assertEquals(4L, ((Long) (values[1])).longValue());
    }

    public void test_invoke_int_obj_2() throws Exception {
        String json = "{ \n" + ("    \"argsObjs\"   :   [ 3, 4 ]\n" + "}");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, new MethodLocator() {
            public Method findMethod(String[] types) {
                return method_f5;
            }
        });
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals(3, ((Integer) (values[0])).intValue());
        TestCase.assertEquals(4L, ((Long) (values[1])).longValue());
    }

    public void test_invoke_int_2() throws Exception {
        String json = "[ \n" + ("    null, [ 3,4 ]\n" + "]");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, new MethodLocator() {
            public Method findMethod(String[] types) {
                return method_f5;
            }
        });
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals(3, ((Integer) (values[0])).intValue());
        TestCase.assertEquals(4L, ((Long) (values[1])).longValue());
    }

    // 
    public static class Service {
        public void f2(String name, HSFJSONUtilsTest_1.Model model) {
        }

        public void f3(String name, List<HSFJSONUtilsTest_1.Model> models) {
        }

        public void f3(String name, HSFJSONUtilsTest_1.Model[] models) {
        }

        public void f3(int a, long b) {
        }
    }

    public static class Model {
        public String value;
    }

    public static class User {
        public String name;

        public int id;

        public int age;
    }
}

