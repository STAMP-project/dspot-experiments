package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;


public class JSONTypeTest_orders_arrayMapping_2 extends TestCase {
    public void test_1() throws Exception {
        JSONTypeTest_orders_arrayMapping_2.Model vo = new JSONTypeTest_orders_arrayMapping_2.Model();
        vo.setId(1001);
        vo.setName("xx");
        vo.setAge(33);
        vo.setDvalue(0.1);
        String json = JSON.toJSONString(vo);
        TestCase.assertEquals("[1001,\"xx\",33,0.0,0.1]", json);
        JSON.parseObject(json, JSONTypeTest_orders_arrayMapping_2.Model.class);
        JSONTypeTest_orders_arrayMapping_2.Model[] array = new JSONTypeTest_orders_arrayMapping_2.Model[]{ vo };
        String json2 = JSON.toJSONString(array);
        JSON.parseObject(json2, JSONTypeTest_orders_arrayMapping_2.Model[].class);
        String json3 = "[\"1001\",\"xx\",33,\"0.0\",\"0.1\"]";
        JSON.parseObject(json3, JSONTypeTest_orders_arrayMapping_2.Model.class);
    }

    @JSONType(orders = { "id", "name", "age", "value" }, serialzeFeatures = SerializerFeature.BeanToArray, parseFeatures = Feature.SupportArrayToBean)
    public static class Model {
        private int id;

        private String name;

        private int age;

        private float value;

        private double dvalue;

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

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public double getDvalue() {
            return dvalue;
        }

        public void setDvalue(double dvalue) {
            this.dvalue = dvalue;
        }
    }
}

