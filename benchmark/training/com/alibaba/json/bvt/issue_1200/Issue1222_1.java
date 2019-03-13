package com.alibaba.json.bvt.issue_1200;


import SerializerFeature.WriteEnumUsingToString;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import junit.framework.TestCase;


/**
 * Created by wenshao on 01/06/2017.
 */
public class Issue1222_1 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1222_1.Model model = new Issue1222_1.Model();
        model.type = Issue1222_1.Type.A;
        String text = JSON.toJSONString(model, WriteEnumUsingToString);
        TestCase.assertEquals("{\"type\":\"TypeA\"}", text);
    }

    private static class Model {
        public Issue1222_1.Type type;
    }

    private static enum Type implements JSONAware {

        A,
        B;
        public String toJSONString() {
            return ("\"Type" + (this.name())) + "\"";
        }
    }
}

