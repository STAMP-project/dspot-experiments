package com.alibaba.json.bvt;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;


public class JSONTypeTest_orders_arrayMapping extends TestCase {
    public void test_1() throws Exception {
        JSONTypeTest_orders_arrayMapping.Model vo = new JSONTypeTest_orders_arrayMapping.Model();
        vo.setId(1001);
        vo.setName("xx");
        vo.setAge(33);
        String json = JSON.toJSONString(vo, BeanToArray);
        TestCase.assertEquals("[1001,\"xx\",33]", json);
        JSON.parseObject(json, JSONTypeTest_orders_arrayMapping.Model.class, SupportArrayToBean);
        JSONTypeTest_orders_arrayMapping.Model[] array = new JSONTypeTest_orders_arrayMapping.Model[]{ vo };
        String json2 = JSON.toJSONString(array, BeanToArray);
        JSON.parseObject(json2, JSONTypeTest_orders_arrayMapping.Model[].class, SupportArrayToBean);
    }

    @JSONType(orders = { "id", "name", "age" })
    public static class Model {
        private int id;

        private String name;

        private int age;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}

