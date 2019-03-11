package com.alibaba.json.bvt.writeClassName;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import junit.framework.TestCase;
import org.junit.Assert;


public class MapTest extends TestCase {
    public void test_map() throws Exception {
        MapTest.VO vo = new MapTest.VO();
        vo.getValue().put("1", "AA");
        String text = JSON.toJSONString(vo, WriteClassName);
        System.out.println(text);
        MapTest.VO vo2 = ((MapTest.VO) (JSON.parse(text)));
        Assert.assertEquals(vo.getValue(), vo2.getValue());
    }

    public void test_map_2() throws Exception {
        MapTest.VO vo = new MapTest.VO();
        vo.setValue(new TreeMap<String, Object>());
        vo.getValue().put("1", "AA");
        String text = JSON.toJSONString(vo, WriteClassName);
        System.out.println(text);
        MapTest.VO vo2 = ((MapTest.VO) (JSON.parse(text)));
        Assert.assertEquals(vo.getValue(), vo2.getValue());
    }

    private static class VO {
        private Map<String, Object> value = new HashMap<String, Object>();

        public Map<String, Object> getValue() {
            return value;
        }

        public void setValue(Map<String, Object> value) {
            this.value = value;
        }
    }
}

