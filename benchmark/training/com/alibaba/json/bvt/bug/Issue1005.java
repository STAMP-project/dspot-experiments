package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 18/01/2017.
 */
public class Issue1005 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1005.Model model = JSON.parseObject("{\"values\":[[1,2,3]]}", Issue1005.Model.class);
        TestCase.assertNotNull(model.values);
        TestCase.assertEquals(3, model.values[0].size());
        TestCase.assertEquals(Byte.class, model.values[0].get(0).getClass());
        TestCase.assertEquals(Byte.class, model.values[0].get(1).getClass());
        TestCase.assertEquals(Byte.class, model.values[0].get(2).getClass());
    }

    public void test_for_List() throws Exception {
        Issue1005.Model2 model = JSON.parseObject("{\"values\":[1,2,3]}", Issue1005.Model2.class);
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

