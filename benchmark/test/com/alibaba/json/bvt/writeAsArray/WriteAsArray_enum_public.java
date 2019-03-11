package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_enum_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_enum_public.VO vo = new WriteAsArray_enum_public.VO();
        vo.setId(WriteAsArray_enum_public.Type.AA);
        vo.setName("wenshao");
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[\"AA\",\"wenshao\"]", text);
        WriteAsArray_enum_public.VO vo2 = JSON.parseObject(text, WriteAsArray_enum_public.VO.class, SupportArrayToBean);
        Assert.assertEquals(vo.getId(), vo2.getId());
        Assert.assertEquals(vo.getName(), vo2.getName());
    }

    public static class VO {
        private WriteAsArray_enum_public.Type id;

        private String name;

        public WriteAsArray_enum_public.Type getId() {
            return id;
        }

        public void setId(WriteAsArray_enum_public.Type id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static enum Type {

        A,
        B,
        C,
        D,
        AA,
        BB,
        CC;}
}

