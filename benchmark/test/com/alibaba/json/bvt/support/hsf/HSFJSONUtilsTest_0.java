package com.alibaba.json.bvt.support.hsf;


import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import com.alibaba.fastjson.support.hsf.MethodLocator;
import java.lang.reflect.Method;
import java.util.List;
import junit.framework.TestCase;


public class HSFJSONUtilsTest_0 extends TestCase {
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
        TestCase.assertEquals("xxx", ((HSFJSONUtilsTest_0.Model) (values[1])).value);
    }

    public void test_invoke_1() throws Exception {
        String json = "{ \n" + ("    \"argsObjs\"   :   [ \"abc\", {\"value\":\"xxx\"} ]\n" + "}");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals("abc", values[0]);
        TestCase.assertEquals("xxx", ((HSFJSONUtilsTest_0.Model) (values[1])).value);
    }

    public void test_invoke_null() throws Exception {
        String json = "{ \n" + (("    \"argsTypes\"  :  [ \"java.lang.String\", \"com.alibaba.json.bvt.support.hsf.HSFJSONUtilsTest_0$Model\"],\n" + "    \"argsObjs\"   :   [ null, null ]\n") + "}");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals(null, values[0]);
        TestCase.assertEquals(null, values[1]);
    }

    public void test_invoke_list() throws Exception {
        String json = "{ \n" + ((((("    \"argsTypes\"  :  [ \"java.lang.String\", \"java.util.List\"],\n" + "    \"argsObjs\"   :   [ \"abc\", [") + "{") + "   \"value\":\"xxx\"") + "   }] ]\n") + "}");
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals("abc", values[0]);
        List list = ((List) (values[1]));
        TestCase.assertEquals("xxx", ((HSFJSONUtilsTest_0.Model) (list.get(0))).value);
    }

    public void test_invoke_list_f4() throws Exception {
        String json = "{\n" + (((((((((((((((("    \"argsTypes\": [\"java.util.List\"],\n" + "    \n") + "    \"argsObjs\": [\n") + "        [\n") + "    \t\t{\n") + "    \t\t\t\"name\": \"123\",\n") + "    \t\t\t\"id\": 123,\n") + "    \t\t\t\"age\": 123\n") + "    \t\t},\n") + "    \t\t{\n") + "    \t\t\t\"name\": \"123\",\n") + "    \t\t\t\"id\": 123,\n") + "    \t\t\t\"age\": 123\n") + "    \t\t}\n") + "\t\t]\n") + "    ]\n") + "}");
        // System.out.println(json);
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(1, values.length);
        List list = ((List) (values[0]));
        TestCase.assertEquals("123", ((HSFJSONUtilsTest_0.User) (list.get(0))).name);
        TestCase.assertEquals("123", ((HSFJSONUtilsTest_0.User) (list.get(1))).name);
    }

    public void test_invoke_list_f5() throws Exception {
        String json = " [\n" + (((((((((((((" \t[\"java.util.List\"],\n" + "    [\n") + "    \t\t[{\n") + "    \t\t\t\"name\": \"123\",\n") + "    \t\t\t\"id\": 123,\n") + "    \t\t\t\"age\": 123\n") + "    \t\t},\n") + "    \t\t{\n") + "    \t\t\t\"name\": \"123\",\n") + "    \t\t\t\"id\": 123,\n") + "    \t\t\t\"age\": 123\n") + "    \t\t}]\n") + "    ]\n") + "]");
        System.out.println(json);
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(1, values.length);
        List list = ((List) (values[0]));
        TestCase.assertEquals("123", ((HSFJSONUtilsTest_0.User) (list.get(0))).name);
        TestCase.assertEquals("123", ((HSFJSONUtilsTest_0.User) (list.get(1))).name);
    }

    public void test_invoke_array() throws Exception {
        String json = "{\n" + (((((((((((((((("    \"argsTypes\": [\"com.alibaba.json.bvt.support.hsf.HSFJSONUtilsTest_0.User[]\"],\n" + "    \n") + "    \"argsObjs\": [\n") + "        [\n") + "    \t\t{\n") + "    \t\t\t\"name\": \"123\",\n") + "    \t\t\t\"id\": 123,\n") + "    \t\t\t\"age\": 123\n") + "    \t\t},\n") + "    \t\t{\n") + "    \t\t\t\"name\": \"123\",\n") + "    \t\t\t\"id\": 123,\n") + "    \t\t\t\"age\": 123\n") + "    \t\t}\n") + "\t\t]\n") + "    ]\n") + "}");
        // System.out.println(json);
        Object[] values = HSFJSONUtils.parseInvocationArguments(json, methodLocator);
        TestCase.assertNotNull(values);
        TestCase.assertEquals(1, values.length);
        HSFJSONUtilsTest_0.User[] list = ((HSFJSONUtilsTest_0.User[]) (values[0]));
        TestCase.assertEquals("123", ((HSFJSONUtilsTest_0.User) (list[0])).name);
        TestCase.assertEquals("123", ((HSFJSONUtilsTest_0.User) (list[1])).name);
    }

    public static class Service {
        public void f1() {
        }

        public void f2(String name, HSFJSONUtilsTest_0.Model model) {
        }

        public void f3(String name, List<HSFJSONUtilsTest_0.Model> models) {
        }

        public void f4(List<HSFJSONUtilsTest_0.User> models) {
        }

        public void f4(HSFJSONUtilsTest_0.User[] models) {
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

