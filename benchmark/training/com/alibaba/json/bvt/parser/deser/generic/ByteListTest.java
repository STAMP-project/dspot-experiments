package com.alibaba.json.bvt.parser.deser.generic;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 20/01/2017.
 */
public class ByteListTest extends TestCase {
    public void test_for_issue() throws Exception {
        ByteListTest.Model model = JSON.parseObject("{\"values\":[[1,2,3]]}", ByteListTest.Model.class);
        TestCase.assertNotNull(model.values);
        TestCase.assertEquals(3, model.values[0].size());
        TestCase.assertEquals(Byte.class, model.values[0].get(0).getClass());
        TestCase.assertEquals(Byte.class, model.values[0].get(1).getClass());
        TestCase.assertEquals(Byte.class, model.values[0].get(2).getClass());
    }

    public void test_for_List() throws Exception {
        ByteListTest.Model2 model = JSON.parseObject("{\"values\":[1,2,3]}", ByteListTest.Model2.class);
        TestCase.assertNotNull(model.values);
        TestCase.assertEquals(3, model.values.size());
        TestCase.assertEquals(Byte.class, model.values.get(0).getClass());
        TestCase.assertEquals(Byte.class, model.values.get(1).getClass());
        TestCase.assertEquals(Byte.class, model.values.get(2).getClass());
    }

    public static class Model {
        public List<Byte>[] values;
    }

    public static class Model2 {
        public List<Byte> values;
    }
}

