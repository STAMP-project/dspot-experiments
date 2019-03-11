package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/8/29.
 */
public class Issue798 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue798.Model model = new Issue798.Model();
        model.value = " ?????????? ????????????? ????????????? ??????????? ";
        String json = JSON.toJSONString(model);
        Issue798.Model model2 = JSON.parseObject(json, Issue798.Model.class);
        TestCase.assertEquals(model.value, model2.value);
    }

    public static class Model {
        public String value;
    }
}

