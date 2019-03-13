package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_zhaoyao extends TestCase {
    public void test_FieldMap() throws Exception {
        Bug_for_zhaoyao.FieldMap map = new Bug_for_zhaoyao.FieldMap();
        map.put("a", 1);
        map.put("b", 2);
        String text = JSON.toJSONString(map, WriteClassName);
        System.out.println(text);
        Bug_for_zhaoyao.FieldMap map2 = ((Bug_for_zhaoyao.FieldMap) (JSON.parse(text)));
        Assert.assertTrue(map.equals(map2));
    }

    public static class FieldMap extends HashMap<String, Object> {
        private static final long serialVersionUID = 1L;

        public Bug_for_zhaoyao.FieldMap field(String field, Object val) {
            this.put(field, val);
            return this;
        }
    }
}

