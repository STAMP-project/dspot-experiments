package com.alibaba.json.bvt.parser.deser.list;


import Feature.AllowUnQuotedFieldNames;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayListTypeFieldTest extends TestCase {
    public void test_0() throws Exception {
        ArrayListTypeFieldTest.Entity entity = JSON.parseObject("{,,,list:[,,,{value:3}]}", ArrayListTypeFieldTest.Entity.class);
        Assert.assertEquals(3, entity.getList().get(0).getValue());
    }

    public void test_1() throws Exception {
        ArrayListTypeFieldTest.Entity entity = JSON.parseObject("{list:[{value:3}]}", ArrayListTypeFieldTest.Entity.class, 0, AllowUnQuotedFieldNames);
        Assert.assertEquals(3, entity.getList().get(0).getValue());
    }

    public void test_null() throws Exception {
        ArrayListTypeFieldTest.Entity entity = JSON.parseObject("{list:null}", ArrayListTypeFieldTest.Entity.class, 0, AllowUnQuotedFieldNames);
        Assert.assertEquals(null, entity.getList());
    }

    public void test_null2() throws Exception {
        ArrayListTypeFieldTest.Entity entity = JSON.parseObject("{list:[null]}", ArrayListTypeFieldTest.Entity.class, 0, AllowUnQuotedFieldNames);
        Assert.assertEquals(null, entity.getList().get(0));
    }

    public void test_error_0() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{list:{{value:3}]}", ArrayListTypeFieldTest.Entity.class, 0, AllowUnQuotedFieldNames);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    private static class Entity {
        private ArrayList<ArrayListTypeFieldTest.VO> list;

        public ArrayList<ArrayListTypeFieldTest.VO> getList() {
            return list;
        }

        public void setList(ArrayList<ArrayListTypeFieldTest.VO> list) {
            this.list = list;
        }
    }

    public static class VO {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

