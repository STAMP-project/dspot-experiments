package com.alibaba.json.bvt.issue_1600;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;


public class Issue1660 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1660.Model model = new Issue1660.Model();
        model.values.add(new Date(1513755213202L));
        String json = JSON.toJSONString(model);
        TestCase.assertEquals("{\"values\":[\"2017-12-20\"]}", json);
    }

    public static class Model {
        @JSONField(format = "yyyy-MM-dd")
        private List<Date> values = new ArrayList<Date>();

        public List<Date> getValues() {
            return values;
        }
    }
}

