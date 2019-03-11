package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_list_obj_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_list_obj_public.VO vo = new WriteAsArray_list_obj_public.VO();
        vo.setId(123);
        vo.setName("wenshao");
        vo.getValues().add(new WriteAsArray_list_obj_public.A());
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[123,\"wenshao\",[[0]]]", text);
        WriteAsArray_list_obj_public.VO vo2 = JSON.parseObject(text, WriteAsArray_list_obj_public.VO.class, SupportArrayToBean);
        Assert.assertEquals(vo.getId(), vo2.getId());
        Assert.assertEquals(vo.getName(), vo2.getName());
        Assert.assertEquals(vo.getValues().size(), vo2.getValues().size());
        Assert.assertEquals(vo.getValues().get(0).getClass(), vo2.getValues().get(0).getClass());
        Assert.assertEquals(vo.getValues().get(0).getValue(), vo2.getValues().get(0).getValue());
    }

    public static class VO {
        private long id;

        private String name;

        private List<WriteAsArray_list_obj_public.A> values = new ArrayList<WriteAsArray_list_obj_public.A>();

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

        public List<WriteAsArray_list_obj_public.A> getValues() {
            return values;
        }

        public void setValues(List<WriteAsArray_list_obj_public.A> values) {
            this.values = values;
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

