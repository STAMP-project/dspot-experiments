package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_Object_2_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_Object_2_public.A a = new WriteAsArray_Object_2_public.A();
        a.setId(123);
        a.setName("wenshao");
        WriteAsArray_Object_2_public.VO vo = new WriteAsArray_Object_2_public.VO();
        vo.setId(1001);
        vo.setValue(a);
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[1001,[123,\"wenshao\"]]", text);
        WriteAsArray_Object_2_public.VO vo2 = JSON.parseObject(text, WriteAsArray_Object_2_public.VO.class, SupportArrayToBean);
        Assert.assertEquals(vo.getValue().getId(), vo2.getValue().getId());
        Assert.assertEquals(vo.getValue().getName(), vo2.getValue().getName());
    }

    public static class VO {
        private int id;

        private WriteAsArray_Object_2_public.A value;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public WriteAsArray_Object_2_public.A getValue() {
            return value;
        }

        public void setValue(WriteAsArray_Object_2_public.A value) {
            this.value = value;
        }
    }

    public static class A {
        private int id;

        private String name;

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
    }
}

