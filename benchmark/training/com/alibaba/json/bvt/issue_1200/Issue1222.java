package com.alibaba.json.bvt.issue_1200;


import SerializerFeature.WriteEnumUsingToString;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import junit.framework.TestCase;


/**
 * Created by wenshao on 01/06/2017.
 */
public class Issue1222 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1222.Model model = new Issue1222.Model();
        model.type = Issue1222.Type.A;
        String text = JSON.toJSONString(model, WriteEnumUsingToString);
        TestCase.assertEquals("{\"type\":\"TypeA\"}", text);
    }

    public static class Model {
        public Issue1222.Type type;
    }

    public static enum Type implements JSONAware {

        A,
        B;
        public String toJSONString() {
            return ("\"Type" + (this.name())) + "\"";
        }
    }
}

