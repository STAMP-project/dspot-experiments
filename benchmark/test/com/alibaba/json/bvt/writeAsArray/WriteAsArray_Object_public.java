package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_Object_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_Object_public.A a = new WriteAsArray_Object_public.A();
        a.setId(123);
        a.setName("wenshao");
        WriteAsArray_Object_public.VO vo = new WriteAsArray_Object_public.VO();
        vo.setA(a);
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[[123,\"wenshao\"]]", text);
        WriteAsArray_Object_public.VO vo2 = JSON.parseObject(text, WriteAsArray_Object_public.VO.class, SupportArrayToBean);
        Assert.assertEquals(vo.getA().getId(), vo2.getA().getId());
        Assert.assertEquals(vo.getA().getName(), vo2.getA().getName());
    }

    public static class VO {
        private WriteAsArray_Object_public.A a;

        public WriteAsArray_Object_public.A getA() {
            return a;
        }

        public void setA(WriteAsArray_Object_public.A a) {
            this.a = a;
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

