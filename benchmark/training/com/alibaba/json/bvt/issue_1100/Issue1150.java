package com.alibaba.json.bvt.issue_1100;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 24/04/2017.
 */
public class Issue1150 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1150.Model model = JSON.parseObject("{\"values\":\"\"}", Issue1150.Model.class);
        TestCase.assertNull(model.values);
    }

    public void test_for_issue_array() throws Exception {
        Issue1150.Model2 model = JSON.parseObject("{\"values\":\"\"}", Issue1150.Model2.class);
        TestCase.assertNull(model.values);
    }

    public static class Model {
        public List values;
    }

    public static class Model2 {
        public Issue1150.Item[] values;
    }

    public static class Item {}
}

