package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_list_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_list_public.VO vo = new WriteAsArray_list_public.VO();
        vo.setId(123);
        vo.setName("wenshao");
        vo.getValues().add("x");
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[123,\"wenshao\",[\"x\"]]", text);
        WriteAsArray_list_public.VO vo2 = JSON.parseObject(text, WriteAsArray_list_public.VO.class, SupportArrayToBean);
        Assert.assertEquals(vo.getId(), vo2.getId());
        Assert.assertEquals(vo.getName(), vo2.getName());
        Assert.assertEquals(vo.getValues().size(), vo2.getValues().size());
        Assert.assertEquals(vo.getValues().get(0), vo2.getValues().get(0));
    }

    public static class VO {
        private long id;

        private String name;

        private List<String> values = new ArrayList<String>();

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

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }
    }
}

