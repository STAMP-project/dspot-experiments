package com.alibaba.json.bvt.issue_1000;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import junit.framework.TestCase;


/**
 * Created by wenshao on 20/03/2017.
 */
public class Issue1085 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1085.Model model = ((Issue1085.Model) (JSON.parseObject("{\"id\":123}", Issue1085.AbstractModel.class)));
        TestCase.assertEquals(123, model.id);
    }

    public abstract static class AbstractModel {
        public int id;

        @JSONCreator
        public static Issue1085.AbstractModel createInstance() {
            return new Issue1085.Model();
        }
    }

    public static class Model extends Issue1085.AbstractModel {}
}

