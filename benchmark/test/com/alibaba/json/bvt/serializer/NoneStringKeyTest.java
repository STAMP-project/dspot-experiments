package com.alibaba.json.bvt.serializer;


import Feature.NonStringKeyAsString;
import SerializerFeature.BrowserCompatible;
import SerializerFeature.WriteNonStringKeyAsString;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class NoneStringKeyTest extends TestCase {
    public void test_0() throws Exception {
        Map map = new HashMap();
        map.put(1, 101);
        Assert.assertEquals("{1:101}", JSON.toJSONString(map));
    }

    public void test_1() throws Exception {
        Map map = new HashMap();
        map.put(1, 101);
        Assert.assertEquals("{\"1\":101}", JSON.toJSONString(map, BrowserCompatible));
    }

    public void test_2() throws Exception {
        Map map = new HashMap();
        map.put(1, 101);
        Assert.assertEquals("{\"1\":101}", JSON.toJSONString(map, WriteNonStringKeyAsString));
    }

    public void test_null_0() throws Exception {
        Map map = new HashMap();
        map.put(null, 101);
        Assert.assertEquals("{null:101}", JSON.toJSONString(map));
    }

    public void test_3() throws Exception {
        Map map = new HashMap();
        map.put(null, 101);
        Assert.assertEquals("{\"null\":101}", JSON.toJSONString(map, WriteNonStringKeyAsString));
    }

    public void test_4() throws Exception {
        NoneStringKeyTest.SubjectDTO dto = new NoneStringKeyTest.SubjectDTO();
        dto.getResults().put(3, new NoneStringKeyTest.Result());
        String json = JSON.toJSONString(dto);
        TestCase.assertEquals("{\"results\":{3:{}}}", json);
        NoneStringKeyTest.SubjectDTO dto2 = JSON.parseObject(json, NoneStringKeyTest.SubjectDTO.class, NonStringKeyAsString);
        System.out.println(JSON.toJSONString(dto2.getResults()));
    }

    public static class Result {}

    public static class SubjectDTO {
        private Map<Integer, NoneStringKeyTest.Result> results = new HashMap<Integer, NoneStringKeyTest.Result>();

        public Map<Integer, NoneStringKeyTest.Result> getResults() {
            return results;
        }
    }
}

