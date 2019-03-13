package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_list_obj_first_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_list_obj_first_public.VO vo = new WriteAsArray_list_obj_first_public.VO();
        vo.setId(123);
        vo.setName("wenshao");
        vo.getFvalues().add(new WriteAsArray_list_obj_first_public.A());
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[[[0]],123,\"wenshao\"]", text);
        WriteAsArray_list_obj_first_public.VO vo2 = JSON.parseObject(text, WriteAsArray_list_obj_first_public.VO.class, SupportArrayToBean);
        Assert.assertEquals(vo.getId(), vo2.getId());
        Assert.assertEquals(vo.getName(), vo2.getName());
        Assert.assertEquals(vo.getFvalues().size(), vo2.getFvalues().size());
        Assert.assertEquals(vo.getFvalues().get(0).getClass(), vo2.getFvalues().get(0).getClass());
        Assert.assertEquals(vo.getFvalues().get(0).getValue(), vo2.getFvalues().get(0).getValue());
    }

    public static class VO {
        private long id;

        private String name;

        private List<WriteAsArray_list_obj_first_public.A> fvalues = new ArrayList<WriteAsArray_list_obj_first_public.A>();

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

        public List<WriteAsArray_list_obj_first_public.A> getFvalues() {
            return fvalues;
        }

        public void setFvalues(List<WriteAsArray_list_obj_first_public.A> fvalues) {
            this.fvalues = fvalues;
        }
    }

    public static class A {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

