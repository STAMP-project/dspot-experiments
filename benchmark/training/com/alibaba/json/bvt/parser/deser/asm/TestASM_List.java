package com.alibaba.json.bvt.parser.deser.asm;


import SerializerFeature.UseSingleQuotes;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;


public class TestASM_List extends TestCase {
    public void test_decimal_3() throws Exception {
        TestASM_List.V0 v = new TestASM_List.V0();
        v.getList().add(new TestASM_List.V1());
        v.getList().add(new TestASM_List.V1());
        String text = JSON.toJSONString(v, UseSingleQuotes, WriteMapNullValue);
        System.out.println(text);
        // Assert.assertEquals("{'list':[{},{}]}", text);
    }

    public static class V0 {
        private List<TestASM_List.V1> list = new ArrayList<TestASM_List.V1>();

        public List<TestASM_List.V1> getList() {
            return list;
        }

        public void setList(List<TestASM_List.V1> list) {
            this.list = list;
        }
    }

    public static class V1 {
        private int id;

        private TimeUnit unit = TimeUnit.SECONDS;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        public void setUnit(TimeUnit unit) {
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

