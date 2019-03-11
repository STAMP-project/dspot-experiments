package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_2 extends TestCase {
    public void test_0() throws Exception {
        JSONLexerTest_2.VO vo = ((JSONLexerTest_2.VO) (JSON.parseObject("{\"@type\":\"com.alibaba.json.bvt.parser.JSONLexerTest_2$VO\"}", JSONLexerTest_2.VO.class)));
        Assert.assertNotNull(vo);
    }

    public void test_1() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"@type\":\"com.alibaba.json.bvt.parser.JSONLexerTest_2$VO1\"}", JSONLexerTest_2.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_2() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"@type\":\"com.alibaba.json.bvt.parser.JSONLexerTest_2$A\"}", JSONLexerTest_2.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_a() throws Exception {
        JSONLexerTest_2.P a = JSON.parseObject("{\"vo\":{\"@type\":\"com.alibaba.json.bvt.parser.JSONLexerTest_2$VO\"}}", JSONLexerTest_2.P.class);
        Assert.assertNotNull(a);
    }

    public void test_list() throws Exception {
        List<JSONLexerTest_2.VO> list = JSON.parseObject("[{\"@type\":\"com.alibaba.json.bvt.parser.JSONLexerTest_2$VO\"}]", new com.alibaba.fastjson.TypeReference<List<JSONLexerTest_2.VO>>() {});
        Assert.assertNotNull(list);
        Assert.assertNotNull(list.get(0));
    }

    public void test_list_2() throws Exception {
        List<JSONLexerTest_2.VO> list = JSON.parseObject("[{\"@type\":\"com.alibaba.json.bvt.parser.JSONLexerTest_2$VO\"},{}]", new com.alibaba.fastjson.TypeReference<List<JSONLexerTest_2.VO>>() {});
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertNotNull(list.get(0));
        Assert.assertNotNull(list.get(1));
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[{\"@type\":\"com.alibaba.json.bvt.parser.JSONLexerTest_2$VO\"}[]", new com.alibaba.fastjson.TypeReference<List<JSONLexerTest_2.VO>>() {});
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class P {
        private JSONLexerTest_2.VO vo;

        public JSONLexerTest_2.VO getVo() {
            return vo;
        }

        public void setVo(JSONLexerTest_2.VO vo) {
            this.vo = vo;
        }
    }

    public static class VO {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class VO1 {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class A {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

