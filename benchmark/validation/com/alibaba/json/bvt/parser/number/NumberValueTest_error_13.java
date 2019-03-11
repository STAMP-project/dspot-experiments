package com.alibaba.json.bvt.parser.number;


import SerializerFeature.WriteBigDecimalAsPlain;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;


public class NumberValueTest_error_13 extends TestCase {
    public void test_0() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v0\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertTrue(((error.getCause()) instanceof ArithmeticException));
    }

    public void test_1() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v1\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertTrue(((error.getCause()) instanceof ArithmeticException));
    }

    public void test_2() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v2\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertTrue(((error.getCause()) instanceof ArithmeticException));
    }

    public void test_3() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v3\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertTrue(((error.getCause()) instanceof ArithmeticException));
    }

    public void test_4() throws Exception {
        BigDecimal b = new BigDecimal("49e999999999");
        TestCase.assertEquals("4.9E+1000000000", JSON.toJSONString(b, WriteBigDecimalAsPlain));
        TestCase.assertEquals("{\"val\":4.9E+1000000000}", JSON.toJSONString(new NumberValueTest_error_13.M1(b), WriteBigDecimalAsPlain));
    }

    public void test_5() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v5\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertTrue(((error.getCause()) instanceof ArithmeticException));
    }

    public void test_6() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v6\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertTrue(((error.getCause()) instanceof ArithmeticException));
    }

    public void test_7() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v7\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertTrue(((error.getCause()) instanceof ArithmeticException));
    }

    public void test_8() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v8\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertEquals(NumberFormatException.class, error.getCause().getClass());
    }

    public void test_9() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v9\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertEquals(ArithmeticException.class, error.getCause().getClass());
    }

    public void test_10() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v10\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertEquals(ArithmeticException.class, error.getCause().getClass());
    }

    public void test_11() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v11\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertEquals(ArithmeticException.class, error.getCause().getClass());
    }

    public void test_11_new() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v11\":new Date(49e99999999)}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_12() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v12\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertEquals(ArithmeticException.class, error.getCause().getClass());
    }

    public void test_13() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v13\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertEquals(ArithmeticException.class, error.getCause().getClass());
    }

    public void test_14() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v14\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        TestCase.assertEquals(UnsupportedOperationException.class, error.getCause().getClass());
    }

    public void test_15() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v15\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_16() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v16\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_17() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v17\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_17_1() throws Exception {
        Exception error = null;
        try {
            JSONObject jsonObject = JSON.parseObject("{\"v17\":49e99999999}");
            jsonObject.getObject("v17", TimeUnit.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_18() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"v18\":49e99999999}", NumberValueTest_error_13.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_20() throws Exception {
        JSONObject jsonObject = JSON.parseObject("{\"v\":49e99999999}");
        Exception error = null;
        try {
            jsonObject.getIntValue("v");
        } catch (ArithmeticException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_21() throws Exception {
        JSONObject jsonObject = JSON.parseObject("{\"v\":49e99999999}");
        Exception error = null;
        try {
            jsonObject.getDate("v");
        } catch (ArithmeticException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_22() throws Exception {
        JSONObject jsonObject = JSON.parseObject("{\"v\":49e99999999}");
        Exception error = null;
        try {
            jsonObject.getObject("v", Date.class);
        } catch (ArithmeticException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_23() throws Exception {
        JSONObject jsonObject = JSON.parseObject("{\"v\":49e99999999}");
        Exception error = null;
        try {
            jsonObject.getObject("v", Timestamp.class);
        } catch (ArithmeticException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_24() throws Exception {
        JSONObject jsonObject = JSON.parseObject("{\"v\":49e99999999}");
        Exception error = null;
        try {
            jsonObject.getObject("v", LocalDateTime.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_25() throws Exception {
        JSONObject jsonObject = JSON.parseObject("{\"lineNumber\":49e99999999}");
        Exception error = null;
        try {
            jsonObject.toJavaObject(StackTraceElement.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_26() throws Exception {
        JSONObject jsonObject = JSON.parseObject("{\"v\":49e99999999}");
        Exception error = null;
        try {
            jsonObject.getObject("v", Time.class);
        } catch (ArithmeticException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_jsonpath() throws Exception {
        JSONObject jsonObject = JSON.parseObject("{\"v\":0}");
        Exception error = null;
        try {
            JSONPath.eval(jsonObject, "$.v in (49e99999999)");
        } catch (JSONPathException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_jsonpath_1() throws Exception {
        JSONArray jsonObject = JSON.parseArray("[{\"v\":49e99999999}]");
        JSONPath.eval(jsonObject, "[v=0]");
    }

    public void test_jsonpath_2() throws Exception {
        NumberValueTest_error_13.Model[] array = JSON.parseObject("[{\"v2\":0}]", NumberValueTest_error_13.Model[].class);
        JSONPath.eval(array, "[v='49e99999999']");
    }

    public void test_jsonpath_3() throws Exception {
        NumberValueTest_error_13.Model[] array = JSON.parseObject("[{\"v2\":0}]", NumberValueTest_error_13.Model[].class);
        Exception error = null;
        try {
            JSONPath.read("{\"a\":49e9999999}", "[a in (123,2,3)]");
        } catch (Exception ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_jsonpath_4() throws Exception {
        NumberValueTest_error_13.Model[] array = JSON.parseObject("[{\"v2\":0}]", NumberValueTest_error_13.Model[].class);
        Exception error = null;
        try {
            JSONPath.read("{\"a\":49e9999999}", "[a between 1 and 3]");
        } catch (Exception ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public void test_27() throws Exception {
        JSONObject object = JSON.parseObject(("{\n" + (((((("    \"connection_health\": {\"status\": \"good\", \"max_value\": 2.0, \"min_value\": 2.0, \"average_value\": 2.0}, \n" + "    \"qps_health\": {\"status\": \"good\", \"max_value\": 5.3, \"min_value\": 4.29, \"average_value\": 4.6},\n") + "    \"disksize_health\": {\"status\": \"good\", \"max_value\": 3089.0, \"min_value\": 3089.0, \"average_value\": 3089.0},\n") + "     \"cpu_health\": {\"status\": \"good\", \"max_value\": 0.0, \"min_value\": 0.0, \"average_value\": 0.0}, \n") + "     \"memory_health\": {\"status\": \"good\", \"max_value\": 17.1, \"min_value\": 17.1, \"average_value\": 17.1}, \n") + "    \"iops_health\": {\"status\": \"good\", \"max_value\": 0.09, \"min_value\": 0.07, \"average_value\": 0.08}\n") + "}")));
        for (String key : object.keySet()) {
            System.out.println(("key = " + key));
            System.out.println(("vaue = " + (object.getJSONObject(key).getIntValue("max_value"))));
        }
    }

    public static class Model {
        public byte v0;

        public short v1;

        public int v2;

        public long v3;

        public Byte v4;

        public Short v5;

        public Integer v6;

        public Long v7;

        public BigInteger v8;

        public Timestamp v9;

        public Date v10;

        public java.util.Date v11;

        public Calendar v12;

        public Timestamp v13;

        public LocalDateTime v14;

        public boolean v15;

        public Boolean v16;

        public TimeUnit v17;

        public Time v18;
    }

    public static class M1 {
        public BigDecimal val;

        public M1(BigDecimal val) {
            this.val = val;
        }
    }
}

