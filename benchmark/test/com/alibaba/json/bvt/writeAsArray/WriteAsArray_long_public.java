package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.parser.Feature;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_long_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_long_public.VO vo = new WriteAsArray_long_public.VO();
        vo.setId(123);
        vo.setName("wenshao");
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[123,\"wenshao\"]", text);
        WriteAsArray_long_public.VO vo2 = JSON.parseObject(text, WriteAsArray_long_public.VO.class, SupportArrayToBean);
        Assert.assertEquals(vo.getId(), vo2.getId());
        Assert.assertEquals(vo.getName(), vo2.getName());
    }

    public void test_1() throws Exception {
        String text = "[123 ,\"wenshao\"]";
        WriteAsArray_long_public.VO vo2 = JSON.parseObject(text, WriteAsArray_long_public.VO.class, SupportArrayToBean);
        Assert.assertEquals(123, vo2.getId());
        Assert.assertEquals("wenshao", vo2.getName());
    }

    public void test_2() throws Exception {
        String text = "[-123 ,\"wenshao\"]";
        WriteAsArray_long_public.VO vo2 = JSON.parseObject(text, WriteAsArray_long_public.VO.class, SupportArrayToBean);
        Assert.assertEquals((-123), vo2.getId());
        Assert.assertEquals("wenshao", vo2.getName());
    }

    public void test_1_stream() throws Exception {
        String text = "[123 ,\"wenshao\"]";
        JSONReader reader = new JSONReader(new StringReader(text), Feature.SupportArrayToBean);
        WriteAsArray_long_public.VO vo2 = reader.readObject(WriteAsArray_long_public.VO.class);
        Assert.assertEquals(123, vo2.getId());
        Assert.assertEquals("wenshao", vo2.getName());
    }

    public void test_2_stream() throws Exception {
        String text = "[-123 ,\"wenshao\"]";
        JSONReader reader = new JSONReader(new StringReader(text), Feature.SupportArrayToBean);
        WriteAsArray_long_public.VO vo2 = reader.readObject(WriteAsArray_long_public.VO.class);
        Assert.assertEquals((-123), vo2.getId());
        Assert.assertEquals("wenshao", vo2.getName());
    }

    public void test_error() throws Exception {
        String text = "[123.,\"wenshao\"]";
        Exception error = null;
        try {
            JSON.parseObject(text, WriteAsArray_long_public.VO.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_stream() throws Exception {
        String text = "[123.,\"wenshao\"]";
        Exception error = null;
        try {
            JSONReader reader = new JSONReader(new StringReader(text), Feature.SupportArrayToBean);
            reader.readObject(WriteAsArray_long_public.VO.class);
            reader.close();
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        String text = "[123:\"wenshao\"]";
        Exception error = null;
        try {
            JSON.parseObject(text, WriteAsArray_long_public.VO.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_stream_1() throws Exception {
        String text = "[123:\"wenshao\" ]";
        Exception error = null;
        try {
            JSONReader reader = new JSONReader(new StringReader(text), Feature.SupportArrayToBean);
            reader.readObject(WriteAsArray_long_public.VO.class);
            reader.close();
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() throws Exception {
        String text = "[-123:\"wenshao\"]";
        Exception error = null;
        try {
            JSON.parseObject(text, WriteAsArray_long_public.VO.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_stream_2() throws Exception {
        String text = "[-123:\"wenshao\" ]";
        Exception error = null;
        try {
            JSONReader reader = new JSONReader(new StringReader(text), Feature.SupportArrayToBean);
            reader.readObject(WriteAsArray_long_public.VO.class);
            reader.close();
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_overflow() throws Exception {
        String text = "[2147483649:\"wenshao\"]";
        Exception error = null;
        try {
            JSON.parseObject(text, WriteAsArray_long_public.VO.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_overflow_stream() throws Exception {
        String text = "[2147483649:\"wenshao\" ]";
        Exception error = null;
        try {
            JSONReader reader = new JSONReader(new StringReader(text), Feature.SupportArrayToBean);
            reader.readObject(WriteAsArray_long_public.VO.class);
            reader.close();
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_value_notmatch() throws Exception {
        String text = "[true,\"wenshao\"]";
        Exception error = null;
        try {
            JSON.parseObject(text, WriteAsArray_long_public.VO.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_value_notmatch_stream() throws Exception {
        String text = "[true,\"wenshao\"]";
        Exception error = null;
        try {
            JSONReader reader = new JSONReader(new StringReader(text), Feature.SupportArrayToBean);
            reader.readObject(WriteAsArray_long_public.VO.class);
            reader.close();
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_value_notmatch_2() throws Exception {
        String text = "[+,\"wenshao\"]";
        Exception error = null;
        try {
            JSON.parseObject(text, WriteAsArray_long_public.VO.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_value_notmatch_2_stream() throws Exception {
        String text = "[+,\"wenshao\"]";
        Exception error = null;
        try {
            JSONReader reader = new JSONReader(new StringReader(text), Feature.SupportArrayToBean);
            reader.readObject(WriteAsArray_long_public.VO.class);
            reader.close();
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        private long id;

        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

