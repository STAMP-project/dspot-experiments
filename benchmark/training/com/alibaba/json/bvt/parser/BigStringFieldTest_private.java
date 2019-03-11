package com.alibaba.json.bvt.parser;


import SerializerFeature.BrowserSecure;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class BigStringFieldTest_private extends TestCase {
    public void test_bigFieldString() throws Exception {
        BigStringFieldTest_private.Model model = new BigStringFieldTest_private.Model();
        model.f0 = random(1024);
        model.f1 = random(1024);
        model.f2 = random(1024);
        model.f3 = random(1024);
        model.f4 = random(1024);
        String text = JSON.toJSONString(model);
        BigStringFieldTest_private.Model model2 = JSON.parseObject(text, BigStringFieldTest_private.Model.class);
        TestCase.assertEquals(model2.f0, model.f0);
        TestCase.assertEquals(model2.f1, model.f1);
        TestCase.assertEquals(model2.f2, model.f2);
        TestCase.assertEquals(model2.f3, model.f3);
        TestCase.assertEquals(model2.f4, model.f4);
    }

    public void test_list() throws Exception {
        List<BigStringFieldTest_private.Model> list = new ArrayList<BigStringFieldTest_private.Model>();
        for (int i = 0; i < 1000; ++i) {
            BigStringFieldTest_private.Model model = new BigStringFieldTest_private.Model();
            model.f0 = random(64);
            model.f1 = random(64);
            model.f2 = random(64);
            model.f3 = random(64);
            model.f4 = random(64);
            list.add(model);
        }
        String text = JSON.toJSONString(list);
        List<BigStringFieldTest_private.Model> list2 = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<List<BigStringFieldTest_private.Model>>() {});
        TestCase.assertEquals(list.size(), list2.size());
        for (int i = 0; i < 1000; ++i) {
            TestCase.assertEquals(list.get(i).f0, list2.get(i).f0);
            TestCase.assertEquals(list.get(i).f1, list2.get(i).f1);
            TestCase.assertEquals(list.get(i).f2, list2.get(i).f2);
            TestCase.assertEquals(list.get(i).f3, list2.get(i).f3);
            TestCase.assertEquals(list.get(i).f4, list2.get(i).f4);
        }
    }

    public void test_list_browserSecure() throws Exception {
        List<BigStringFieldTest_private.Model> list = new ArrayList<BigStringFieldTest_private.Model>();
        for (int i = 0; i < 1000; ++i) {
            BigStringFieldTest_private.Model model = new BigStringFieldTest_private.Model();
            model.f0 = random(64);
            model.f1 = random(64);
            model.f2 = random(64);
            model.f3 = random(64);
            model.f4 = random(64);
            list.add(model);
        }
        String text = JSON.toJSONString(list, BrowserSecure);
        List<BigStringFieldTest_private.Model> list2 = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<List<BigStringFieldTest_private.Model>>() {});
        TestCase.assertEquals(list.size(), list2.size());
        for (int i = 0; i < 1000; ++i) {
            TestCase.assertEquals(list.get(i).f0, list2.get(i).f0);
            TestCase.assertEquals(list.get(i).f1, list2.get(i).f1);
            TestCase.assertEquals(list.get(i).f2, list2.get(i).f2);
            TestCase.assertEquals(list.get(i).f3, list2.get(i).f3);
            TestCase.assertEquals(list.get(i).f4, list2.get(i).f4);
        }
    }

    private static class Model {
        public String f0;

        public String f1;

        public String f2;

        public String f3;

        public String f4;
    }
}

