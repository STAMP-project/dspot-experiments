package com.alibaba.json.bvt.bug;


import Feature.SupportNonPublicField;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 2016/11/17.
 */
public class Issue900 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue900.Model model = JSON.parseObject("{\"id\":123}", Issue900.Model.class, SupportNonPublicField);
        TestCase.assertEquals(123, model.id);
    }

    public static class Model {
        private int id;
    }
}

