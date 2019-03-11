package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 13/02/2017.
 */
public class DupTest extends TestCase {
    public void test_dup() throws Exception {
        String json = "{\"id\":1001,\"_id\":1002}";
        DupTest.Model model = JSON.parseObject(json, DupTest.Model.class);
        TestCase.assertEquals(1001, model.id);
    }

    public static class Model {
        public int id;
    }
}

